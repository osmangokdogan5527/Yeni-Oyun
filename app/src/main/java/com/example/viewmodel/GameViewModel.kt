package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.AiGameService
import com.example.data.AppDatabase
import com.example.data.GameSaveEntity
import com.example.data.GameSaveRepository
import com.example.util.BenchmarkCalculator
import com.example.util.HardwareRatingHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.random.Random

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val saveRepository = GameSaveRepository(database.gameSaveDao())

    val savedGamesState: StateFlow<List<GameSaveEntity>> = saveRepository.allSaves.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }

    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    // Modüler StateFlow'lar: Sadece kendi alanı değiştiğinde tetiklenir (Gereksiz Recomposition'ı önler)
    val financeState: StateFlow<CompanyFinanceState> = _state
        .map { it.toFinanceState() }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = _state.value.toFinanceState()
        )

    val marketState: StateFlow<MarketEcosystemState> = _state
        .map { it.toMarketState() }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = _state.value.toMarketState()
        )

    val productionState: StateFlow<ProductionOpsState> = _state
        .map { it.toProductionState() }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = _state.value.toProductionState()
        )

    val techSoftwareState: StateFlow<TechSoftwareState> = _state
        .map { it.toTechSoftwareState() }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = _state.value.toTechSoftwareState()
        )

    val companyProfileState: StateFlow<CompanyProfileState> = _state
        .map { it.toCompanyProfileState() }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = _state.value.toCompanyProfileState()
        )

    init {
        val initialNews = getHistoricalNewsForYearMonth(2010, 1)
        _state.update { it.copy(
            reports = listOf(
                MarketReport(
                    title = "Şirket Kuruldu",
                    text = "Akıllı telefon pazarına hoş geldiniz! İlk telefon modelinizi tasarlamak için Cihazlar menüsüne gidin.",
                    profit = 0,
                    unitsSold = 0,
                    reviewScore = 0
                )
            ),
            newsList = initialNews
        )}

        // Attempt to auto-load slot 0 if exists
        viewModelScope.launch(Dispatchers.IO) {
            val autoSave = saveRepository.getSave(0)
            if (autoSave != null) {
                try {
                    val loaded = json.decodeFromString<GameState>(autoSave.gameStateJson)
                    _state.value = sanitizeLoadedGameState(loaded)
                } catch (e: Exception) {
                    // Fall back to default
                }
            }
        }
    }

    private fun sanitizeLoadedGameState(loaded: GameState): GameState {
        val sanitizedModels = loaded.activeModels.map { model ->
            val resolvedWithFullRecall = loaded.activeHardwareCrises.any {
                it.modelId == model.id && it.isResolved && it.resolutionChoice == CrisisResolutionStrategy.FULL_RECALL_REFUND.title
            }
            if (!resolvedWithFullRecall && model.isRecalled) {
                val restoredStock = if (model.remainingStock == 0 && model.totalSold < model.totalStock) {
                    model.totalStock - model.totalSold
                } else model.remainingStock
                model.copy(
                    isRecalled = false,
                    remainingStock = restoredStock.coerceAtLeast(0)
                )
            } else if (!model.isRecalled && model.remainingStock == 0 && model.totalSold < model.totalStock) {
                // If model is not recalled but remainingStock was wiped by a crisis before
                val restoredStock = model.totalStock - model.totalSold
                model.copy(remainingStock = restoredStock.coerceAtLeast(0))
            } else {
                model
            }
        }

        val sanitizedTrend = if (loaded.currentTrend.tip.isBlank()) {
            when {
                loaded.year <= 2013 && loaded.currentTrend.category == TrendCategory.PREMIUM_BUILD ->
                    loaded.currentTrend.copy(tip = "Alüminyum gövde veya ince tasarım çizgileri kullanın.")
                loaded.year in 2014..2019 && loaded.currentTrend.category == TrendCategory.PREMIUM_BUILD ->
                    loaded.currentTrend.copy(tip = "Cam arka kapak veya Alüminyum kasa kullanın.")
                loaded.year in 2014..2019 && loaded.currentTrend.category == TrendCategory.LONG_BATTERY ->
                    loaded.currentTrend.copy(tip = "3600mAh - 4500mAh batarya veya hızlı şarj seçin.")
                else -> loaded.currentTrend
            }
        } else loaded.currentTrend

        val sanitizedCustomOs = if (loaded.customOs.activeDevelopment != null) {
            val dev = loaded.customOs.activeDevelopment!!
            val assigned = loaded.customOs.assignedDevs.coerceIn(0, loaded.engineers)
            val realisticTotal = calculateOsDevelopmentPeriods(dev.type, assigned, loaded.engineers)
            if (dev.totalMonths > 24 || dev.remainingMonths > 24) {
                val currentProgress = (1f - (dev.remainingMonths.toFloat() / dev.totalMonths.coerceAtLeast(1).toFloat())).coerceIn(0f, 0.95f)
                val newRem = ((1f - currentProgress) * realisticTotal).toInt().coerceIn(1, realisticTotal)
                loaded.customOs.copy(
                    activeDevelopment = dev.copy(
                        totalMonths = realisticTotal,
                        remainingMonths = newRem
                    )
                )
            } else loaded.customOs
        } else loaded.customOs

        return loaded.copy(activeModels = sanitizedModels, currentTrend = sanitizedTrend, customOs = sanitizedCustomOs)
    }

    fun calculateOsDevelopmentPeriods(type: OsType, assignedDevs: Int, totalEngineers: Int): Int {
        val basePeriods = if (type == OsType.PROPRIETARY_KERNEL) 14 else 8
        val effectiveDevs = assignedDevs.coerceAtLeast(1) + (totalEngineers / 8)
        val speedMultiplier = kotlin.math.sqrt(effectiveDevs.toDouble() / 3.0).coerceIn(0.6, 3.5)
        return (basePeriods / speedMultiplier).toInt().coerceIn(2, 20)
    }

    fun autoSaveGame() {
        val current = _state.value
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val jsonString = json.encodeToString(current)
                saveRepository.saveGame(
                    GameSaveEntity(
                        slotId = 0,
                        slotName = "Otomatik Kayıt",
                        companyName = current.companyName,
                        year = current.year,
                        month = current.month,
                        budget = current.budget,
                        reputation = current.reputation,
                        modelCount = current.activeModels.size,
                        lastSavedTimestamp = System.currentTimeMillis(),
                        gameStateJson = jsonString
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun manualSaveGame(slotId: Int, slotName: String = "Kayıt Slotu $slotId") {
        val current = _state.value
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val jsonString = json.encodeToString(current)
                saveRepository.saveGame(
                    GameSaveEntity(
                        slotId = slotId,
                        slotName = slotName,
                        companyName = current.companyName,
                        year = current.year,
                        month = current.month,
                        budget = current.budget,
                        reputation = current.reputation,
                        modelCount = current.activeModels.size,
                        lastSavedTimestamp = System.currentTimeMillis(),
                        gameStateJson = jsonString
                    )
                )
                _state.update { it.copy(noticeMessage = "Oyun başarıyla kaydedildi ($slotName).") }
            } catch (e: Exception) {
                _state.update { it.copy(noticeMessage = "Kayıt sırasında hata oluştu: ${e.message}") }
            }
        }
    }

    fun loadGame(slotId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val entity = saveRepository.getSave(slotId)
                if (entity != null) {
                    val loaded = json.decodeFromString<GameState>(entity.gameStateJson)
                    _state.value = sanitizeLoadedGameState(loaded)
                    _state.update { it.copy(noticeMessage = "${entity.slotName} başarıyla yüklendi!") }
                } else {
                    _state.update { it.copy(noticeMessage = "Kayıt dosyası bulunamadı.") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(noticeMessage = "Kayıt yüklenirken hata oluştu: ${e.message}") }
            }
        }
    }

    fun deleteSave(slotId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            saveRepository.deleteSave(slotId)
        }
    }

    fun startNewGame(freshCompanyName: String = "Apex Mobile") {
        val initialNews = getHistoricalNewsForYearMonth(2010, 1)
        val freshState = GameState(
            companyName = freshCompanyName,
            isCompanySetupDone = false,
            reports = listOf(
                MarketReport(
                    title = "Yeni Şirket Kuruldu",
                    text = "Yeni bir yolculuk başlıyor! İlk akıllı telefonunuzu üretmek için şirketinizi kurun.",
                    profit = 0,
                    unitsSold = 0,
                    reviewScore = 0
                )
            ),
            newsList = initialNews
        )
        _state.value = freshState
        autoSaveGame()
    }

    fun checkTrendMatch(specs: PhoneSpecs, trend: MarketTrend? = _state.value.currentTrend): Boolean {
        return com.example.viewmodel.checkTrendMatch(specs, trend)
    }

    companion object {
        fun getMonthName(month: Int): String = when (month) {
            1 -> "Ocak"
            2 -> "Şubat"
            3 -> "Mart"
            4 -> "Nisan"
            5 -> "Mayıs"
            6 -> "Haziran"
            7 -> "Temmuz"
            8 -> "Ağustos"
            9 -> "Eylül"
            10 -> "Ekim"
            11 -> "Kasım"
            12 -> "Aralık"
            else -> "$month. Ay"
        }
    }

    fun clearLastUnlockedAchievements() {
        _state.update { it.copy(lastUnlockedAchievementIds = emptyList()) }
    }

    fun advanceTime() {
        val currentState = _state.value
        val isSecondHalf = currentState.period == 2
        val newPeriod = if (isSecondHalf) 1 else 2
        val newMonth = if (isSecondHalf) (if (currentState.month == 12) 1 else currentState.month + 1) else currentState.month
        val newYear = if (isSecondHalf && currentState.month == 12) currentState.year + 1 else currentState.year
        val periodName = if (newPeriod == 1) "1. Yarı (1-15 Gün)" else "2. Yarı (16-30 Gün)"

        var totalMonthlyRevenue = 0L
        var totalMonthlyUnitsSold = 0
        var qualityWeightedReviewSum = 0.0
        var totalRecallCost = 0L
        var totalRecallReputationPenalty = 0
        var directReputationPenaltyFromRefunds = 0
        val newNewsList = currentState.newsList.toMutableList()
        val finishedModelsNews = mutableListOf<NewsArticle>()
        val newCrisesList = mutableListOf<HardwareCrisis>()

        // Update Market Trends (Progresses every full month / 2 periods)
        var updatedTrend = currentState.currentTrend
        if (isSecondHalf) {
            if (updatedTrend.remainingMonths <= 1) {
                val newGeneratedTrend = generateNewTrend(newYear, updatedTrend.category)
                updatedTrend = newGeneratedTrend
                val trendBonusPct = newGeneratedTrend.bonusPercent
                newNewsList.add(
                    NewsArticle(
                        id = "trend_news_${newYear}_${newMonth}_${Random.nextInt(100, 999)}",
                        title = "🔥 YENİ TÜKETİCİ TRENDİ: ${newGeneratedTrend.title}",
                        text = "${newGeneratedTrend.description} Bu trende uygun cihazlar pazarda +%$trendBonusPct daha fazla talep görecek! (${newGeneratedTrend.category.tip})",
                        category = "Pazar",
                        year = newYear,
                        month = newMonth
                    )
                )
            } else {
                updatedTrend = updatedTrend.copy(remainingMonths = updatedTrend.remainingMonths - 1)
            }
        }

        // Process bi-weekly sales (2-week cycle, 24 periods per year) for each active model
        // Fabrika, bu periyotta paylaşılan sınırlı bir üretim kapasitesine sahiptir; kapasite,
        // üretimi devam eden (backlog'u olan) modellere piyasaya çıkış sırasına göre (FIFO) dağıtılır.
        var remainingFactoryCapacityThisPeriod = currentState.currentFactoryTier.periodCapacity
        val updatedActiveModels = currentState.activeModels.map { model ->
            // 0. ÜRETİM ADIMI: Fabrika kapasitesi izin verdiğince backlog'u kapat
            var workingModel = model
            if (model.hasPendingProduction && remainingFactoryCapacityThisPeriod > 0) {
                val backlog = model.totalStock - model.producedStock
                val producedThisPeriod = backlog.coerceAtMost(remainingFactoryCapacityThisPeriod)
                if (producedThisPeriod > 0) {
                    workingModel = model.copy(
                        producedStock = model.producedStock + producedThisPeriod,
                        remainingStock = model.remainingStock + producedThisPeriod
                    )
                    remainingFactoryCapacityThisPeriod -= producedThisPeriod
                }
            }

            if (!workingModel.isCompleted && workingModel.remainingStock > 0) {
                val model = workingModel
                val newPeriods = model.periodsOnMarket + 1
                val newMonths = newPeriods / 2
                
                // Ana satış temposunu 48 aya yayma: ilk 12-24 ay normal lansman dönemi,
                // 25-48. aylar yalnızca elde kalan stok için uzun-kuyruk dönemidir.
                val primarySalesMonths = model.maxMonthsOnMarket.coerceAtMost(24)
                val basePeriodBatch = model.totalStock / (primarySalesMonths * 2).toFloat()
                
                // Quality, reputation and active marketing campaign demand multiplier
                val qualityFactor = (model.reviewScore / 55.0f).coerceIn(0.4f, 2.0f)
                
                // 1. ZORLUK & İTİBAR DENGESİ: Şirket olgunluğu ve marka itibarına göre satış hacmi kademeli açılır.
                // İlk modellerde ve düşük itibarda sınırlı satış ve mütevazı kâr, itibar oluştukça (ve 3-5 başarılı model sonrasında) büyük kârlar açılır.
                val repFactor = when {
                    currentState.reputation < 15 -> 0.20f + (currentState.reputation / 15.0f) * 0.20f // 0.20x - 0.40x (Bilinmeyen yeni marka dönemi)
                    currentState.reputation < 35 -> 0.40f + ((currentState.reputation - 15) / 20.0f) * 0.30f // 0.40x - 0.70x (İlk tanınma evresi)
                    currentState.reputation < 60 -> 0.70f + ((currentState.reputation - 35) / 25.0f) * 0.35f // 0.70x - 1.05x (Büyüyen şirket)
                    currentState.reputation < 85 -> 1.05f + ((currentState.reputation - 60) / 25.0f) * 0.45f // 1.05x - 1.50x (Güçlü pazar aktörü)
                    else -> 1.50f + ((currentState.reputation - 85) / 15.0f) * 0.50f // 1.50x - 2.00x (Dev marka / amiral gemisi lideri)
                }

                // Şirket Olgunluk & Tecrübe Çarpanı (3-5 modelden sonra pazar güveni ve kanal dağıtımı oturur)
                val marketMaturityFactor = when {
                    currentState.modelCount <= 1 -> 0.65f // İlk telefon: Mütevazı pilot satış
                    currentState.modelCount <= 3 -> 0.82f // 2-3. telefon: Kanal genişlemesi
                    currentState.modelCount <= 5 -> 0.95f // 4-5. model: Yerleşik dağıtım ağı
                    else -> 1.0f + ((currentState.modelCount - 5).coerceAtMost(10) * 0.02f) // Düzenli üretici bonusu
                }

                // 2. GELİŞMİŞ FİYAT/TALEP ESNEKLİĞİ (ADVANCED PRICE ELASTICITY & DISCOUNT IMPACT)
                val currentPrice = model.effectivePrice
                val estimatedUnitCost = model.specs.unitCost.coerceAtLeast(30)
                val markupRatio = currentPrice.toFloat() / estimatedUnitCost.toFloat()
                val (fairMin, fairMax) = when {
                    estimatedUnitCost >= 500 -> 1099 to 1799
                    estimatedUnitCost >= 320 -> 799 to 1199
                    estimatedUnitCost >= 180 -> 499 to 799
                    estimatedUnitCost >= 90 -> 299 to 499
                    else -> 129 to 299
                }
                val basePriceElasticity = when {
                    currentPrice < fairMin * 0.88f -> 1.45f // F/P Patlaması (+%45)
                    currentPrice <= fairMax -> 1.05f // Standart Dengeli Talep
                    currentPrice <= fairMax * 1.25f -> {
                        if (currentState.reputation >= 60) 0.90f else 0.75f // Tuzlu fiyat
                    }
                    else -> {
                        if (currentState.reputation >= 85) 0.65f else 0.45f // Aşırı pahalı, talep çöküşü (-%55)
                    }
                } * when {
                    markupRatio > 2.5f && currentState.reputation < 30 -> 0.40f // Bilinmeyen markanın fahiş fiyat koyması durumunda sert talep kesintisi
                    markupRatio > 2.2f && currentState.reputation < 50 -> 0.65f
                    markupRatio <= 1.25f -> 1.15f
                    else -> 1.0f
                }
                // İndirim talebi artırır fakat tek başına satışları patlatmaz.
                // %15 -> ~1.18x, %30 -> ~1.36x, %50 -> ~1.60x
                val discountBoost = if (model.discountPercent > 0) 1.0f + (model.discountPercent * 0.012f) else 1.0f
                val priceElasticityFactor = basePriceElasticity * discountBoost

                // 3. SERİ DEVAMI & NESİL SADAKATİ
                val seriesLoyaltyFactor = if (model.specs.generation > 1) {
                    1.0f + ((model.specs.generation - 1).coerceAtMost(6) * 0.06f)
                } else 1.0f

                // 4. MODEL SINIFI (TIER) ETKİSİ
                val tierDemandFactor = when (model.specs.tier) {
                    ModelTier.LITE -> 1.30f
                    ModelTier.STANDARD -> 1.0f
                    ModelTier.PRO -> if (currentState.reputation >= 30) 0.95f else 0.60f
                    ModelTier.ULTRA -> if (currentState.reputation >= 50) 0.85f else 0.40f
                }

                val campaignFactor = if (model.activeCampaign != null && model.activeCampaign.remainingMonths > 0) {
                    1.0f + (model.activeCampaign.type.boostPercent / 100.0f)
                } else 1.0f

                // Trend Bonus
                val isTrendActive = model.matchesTrend || checkTrendMatch(model.specs, updatedTrend)
                val trendFactor = if (isTrendActive) (updatedTrend?.bonusMultiplier ?: 1.15f) else 1.0f

                // Design & Color Variety Sales Appeal Multiplier
                val extraColors = (model.specs.selectedColors.size - 1).coerceAtLeast(0)
                val colorFactor = 1.0f + (extraColors * 0.04f)
                val designFactor = when {
                    model.specs.material == "Titanyum" || model.specs.backFinish == "Vegan Deri" -> 1.10f
                    model.specs.material == "Cam" || model.specs.backFinish == "Karbon Fiber" -> 1.06f
                    model.specs.material == "Alüminyum" || model.specs.backFinish == "Buzlu Mat Cam" -> 1.03f
                    else -> 1.0f
                }
                // OS & Software Synergy Demand Multiplier
                var baseOsSynergy = when {
                    model.specs.osFocus.contains("Oyun") && model.specs.style == "Oyuncu" -> 1.30f
                    model.specs.osFocus.contains("Güvenlik") && model.specs.style == "Klasik" -> 1.25f
                    model.specs.osFocus.contains("Yapay Zeka") && model.matchesTrend -> 1.20f
                    model.specs.osFocus.contains("Estetik") -> 1.12f
                    else -> 1.0f
                }
                
                // Ecosystem Penalty / Windows Phone Effect
                if (model.specs.osType.contains("Bağımsız") || model.specs.osType.contains("Özel")) {
                    val appCount = currentState.customOs.totalStoreApps
                    val requiredAppsForFullSales = 50000L
                    if (appCount < requiredAppsForFullSales) {
                        val penalty = (appCount.toFloat() / requiredAppsForFullSales.toFloat()).coerceIn(0.1f, 1.0f)
                        baseOsSynergy *= penalty
                        
                        if (penalty < 0.5f && Random.nextInt(100) < 5) {
                            newNewsList.add(
                                NewsArticle(
                                    id = "no_apps_${model.id}_${newYear}_${newMonth}",
                                    title = "⚠️ MÜŞTERİLER ŞİKAYETÇİ: Mağazada Uygulama Yok!",
                                    text = "${model.specs.name} donanım olarak harika olsa da, uygulama mağazasındaki eksiklikler nedeniyle (sadece ${"%,d".format(appCount)} uygulama) satışlar durma noktasına geldi! Geliştirici fonuna acil yatırım yapın.",
                                    category = "Pazar",
                                    year = newYear,
                                    month = newMonth
                                )
                            )
                        }
                    } else if (appCount > 200000L) {
                        baseOsSynergy *= 1.2f // Huge ecosystem advantage (iOS effect)
                    }
                }
                val osSynergyFactor = baseOsSynergy

                // ÜRÜN YAŞAM DÖNGÜSÜ: Güçlü modeller 4 yıla kadar satılabilir.
                // İlk yıl ana satış dönemi, 2. yıl belirgin düşüş, 3-4. yıl ise "uzun kuyruk" satışıdır.
                // Eski model tamamen ölmez; uygun fiyatla giriş/bütçe modeli olarak yaşamaya devam eder.
                val lifecycleSalesCurve = when (newMonths) {
                    0, 1 -> 0.50f
                    2 -> 0.80f
                    3 -> 1.25f
                    4 -> 1.45f
                    5, 6 -> 1.20f
                    in 7..9 -> 0.90f
                    in 10..12 -> 0.72f
                    in 13..18 -> 0.55f
                    in 19..24 -> 0.40f
                    in 25..30 -> 0.28f
                    in 31..36 -> 0.21f
                    in 37..42 -> 0.15f
                    in 43..48 -> 0.10f
                    else -> 0.05f
                }

                // Aynı serinin yeni nesli çıktıysa eski model talebi düşer; fakat tamamen yok olmaz.
                // İndirim, eski neslin "uygun fiyatlı alternatif" olarak talebinin bir kısmını geri kazanır.
                val hasNewerGenerationInSeries = currentState.activeModels.any { other ->
                    other.id != model.id &&
                        !other.isCompleted &&
                        other.specs.seriesName.isNotBlank() &&
                        other.specs.seriesName == model.specs.seriesName &&
                        other.specs.generation > model.specs.generation
                }
                val newerGenerationFactor = if (hasNewerGenerationInSeries) {
                    when {
                        model.discountPercent >= 40 -> 0.88f
                        model.discountPercent >= 30 -> 0.82f
                        model.discountPercent >= 20 -> 0.76f
                        model.discountPercent >= 10 -> 0.70f
                        else -> 0.62f
                    }
                } else 1.0f

                // 2. yıldan sonra indirim, uzun-kuyruk talebini kontrollü şekilde canlandırır.
                // Bu bonus fiyat elastikiyetinden ayrıdır ve sadece yaşlı modellerde devreye girer.
                val longTailDiscountRecovery = if (newMonths >= 13 && model.discountPercent > 0) {
                    (1.0f + model.discountPercent * 0.008f).coerceAtMost(1.40f)
                } else 1.0f

                // --- HYPE & MÜŞTERİ MEMNUNİYETİ DENGESİ (HYPE & SATISFACTION MECHANIC) ---
                // 1. Hype Evrimi: Doğal soğuma + pazarlama desteği + kulaktan kulağa (Word-of-Mouth)
                val decayedHype = (model.hypeScore - 2).coerceAtLeast(8)
                val campaignHypeBoost = if (model.activeCampaign != null) (model.activeCampaign.type.hypeBoost / 3).coerceAtLeast(3) else 0

                val wordOfMouthEffect = when {
                    model.customerSatisfactionScore >= 88 -> if (model.reviewScore >= 85) 4 else 2
                    model.customerSatisfactionScore >= 75 -> 1
                    model.customerSatisfactionScore <= 35 -> -6
                    model.customerSatisfactionScore <= 50 -> -2
                    else -> 0
                }
                val currentHype = (decayedHype + campaignHypeBoost + wordOfMouthEffect).coerceIn(5, 140)

                // 2. Müşteri Memnuniyeti Hesaplaması (Beklenti vs Gerçek Performans)
                val expectedQuality = (currentHype * 0.65f + (model.effectivePrice.toFloat() / estimatedUnitCost.coerceAtLeast(30) * 1.5f).coerceIn(10f, 40f))
                val actualQuality = model.reviewScore.toFloat() + (model.specs.qaBudget.toFloat() / model.totalStock.coerceAtLeast(1) * 0.25f).coerceAtMost(10f) + if (model.benchmarkScore != null && model.benchmarkScore.overallScore >= 120000) 4f else 0f

                val updatedSatisfaction = when {
                    currentHype >= 55 && model.reviewScore < 60 -> {
                        // Büyük Hype vs Zayıf Donanım Fiyaskosu (Kullanıcı aldatılmış hisseder, 'telefon ısınıyor' tepkileri!)
                        val gap = currentHype - model.reviewScore
                        (40 - (gap * 0.8f) - (if (model.specs.qaBudget == 0L) 10 else 0)).toInt().coerceIn(5, 38)
                    }
                    model.reviewScore >= 85 && currentHype <= 35 -> {
                        // Sıfır/az reklamla çıkan gizli cevher (Beklentinin katbekat üstünde memnuniyet)
                        (90 + (model.reviewScore - 85)).coerceIn(85, 100)
                    }
                    else -> {
                        (75f + (actualQuality - expectedQuality) * 0.9f).toInt().coerceIn(10, 100)
                    }
                }

                // 3. Hype & Memnuniyet Talep Çarpanları
                val hypeDemandMultiplier = when {
                    currentHype < 15 -> 0.45f // Sıfır reklam -> Harika telefon bile ilk haftalarda zor satılır
                    currentHype < 30 -> 0.75f
                    currentHype < 55 -> 1.05f
                    currentHype < 80 -> 1.50f // Yüksek Hype -> İlk haftalar satış patlaması!
                    else -> 2.05f // Zirve Hype -> Kuyruklar ve izdiham!
                }

                val satisfactionDemandMultiplier = when {
                    updatedSatisfaction >= 85 -> 1.25f // Mükemmel tavsiyeler, satışlar uzun süre diri kalır
                    updatedSatisfaction >= 70 -> 1.05f
                    updatedSatisfaction >= 50 -> 0.85f
                    updatedSatisfaction >= 30 -> 0.50f // Isınma ve donma şikayetleri satışları keser
                    else -> 0.20f // Talep bıçak gibi kesilir
                }

                val wordOfMouthMultiplier = if (updatedSatisfaction >= 85) 1.15f else if (updatedSatisfaction <= 35) 0.65f else 1.0f

                val demandFactor = qualityFactor * repFactor * marketMaturityFactor * priceElasticityFactor * seriesLoyaltyFactor * tierDemandFactor * campaignFactor * trendFactor * colorFactor * designFactor * osSynergyFactor * lifecycleSalesCurve * newerGenerationFactor * longTailDiscountRecovery * hypeDemandMultiplier * satisfactionDemandMultiplier * wordOfMouthMultiplier

                // --- SADELEŞTİRİLMİŞ 3 GRUPLU ÖZET (sadece arayüzde gösterim için; matematik yukarıdaki gibi kalıyor) ---
                // "Ürün Kalitesi": telefonun kendi niteliği — inceleme puanı, tasarım/malzeme, OS uyumu
                val productQualityGroup = qualityFactor * designFactor * osSynergyFactor
                // "Pazar Talebi": dış etkenler — trend, renk çeşitliliği, aktif kampanya, fiyat cazibesi
                val marketDemandGroup = trendFactor * colorFactor * campaignFactor * priceElasticityFactor * hypeDemandMultiplier
                // "Marka Gücü": şirketin birikimi — itibar, pazar olgunluğu, seri sadakati, memnuniyet
                val brandStrengthGroup = repFactor * marketMaturityFactor * seriesLoyaltyFactor * satisfactionDemandMultiplier
                
                // 4. SATIŞ VE İADE (REFUND WAVE) HESAPLAMASI
                val calculatedUnits = (basePeriodBatch * demandFactor).toInt().coerceAtLeast(0)
                val grossUnitsSoldThisPeriod = calculatedUnits.coerceAtMost(model.remainingStock)

                val refundRatePercent = when {
                    updatedSatisfaction >= 88 -> 0.005f // Binde 5
                    updatedSatisfaction >= 70 -> 0.015f // %1.5
                    updatedSatisfaction >= 50 -> 0.045f // %4.5
                    updatedSatisfaction >= 35 -> 0.16f  // %16 - Hayal kırıklığı ve iade dalgası
                    updatedSatisfaction >= 20 -> 0.32f  // %32 - İade kuyrukları
                    else -> 0.50f                      // %50 - Yıkıcı hezimet!
                }

                val refundUnits = (grossUnitsSoldThisPeriod * refundRatePercent).toInt()
                val netUnitsSoldThisPeriod = (grossUnitsSoldThisPeriod - refundUnits).coerceAtLeast(0)
                val grossRevenue = grossUnitsSoldThisPeriod.toLong() * model.effectivePrice
                val periodRefundCost = refundUnits.toLong() * model.effectivePrice
                val netRevenueThisPeriod = grossRevenue - periodRefundCost
                
                totalMonthlyRevenue += netRevenueThisPeriod
                totalMonthlyUnitsSold += netUnitsSoldThisPeriod
                qualityWeightedReviewSum += netUnitsSoldThisPeriod.toDouble() * model.reviewScore

                // İade Dalgası Prestij Darbesi ve Haber Tetikleyicisi
                if (updatedSatisfaction <= 35 && refundUnits >= 80) {
                    val repPenalty = if (updatedSatisfaction <= 20) 2 else 1
                    directReputationPenaltyFromRefunds += repPenalty
                    finishedModelsNews.add(
                        NewsArticle(
                            id = "news_refund_${model.id}_${newYear}_${newMonth}_${newPeriod}",
                            title = "🚨 ŞİKAYET VE İADE DALGASI: ${model.specs.name}",
                            text = "${model.specs.name} modelinde yoğun pazarlama sonrası beklentiyi bulamayan kullanıcılardan 'Isınma ve Düşük Performans' tepkileri yağıyor! Bu dönem ${"%,d".format(refundUnits)} adet cihaz iade edildi ($${"%,d".format(periodRefundCost)} geri ödendi). Marka prestijiniz -$repPenalty darbe aldı!",
                            category = "Pazar",
                            year = newYear,
                            month = newMonth
                        )
                    )
                } else if (updatedSatisfaction >= 90 && netUnitsSoldThisPeriod >= 2500 && newPeriods == 2) {
                    finishedModelsNews.add(
                        NewsArticle(
                            id = "news_happy_${model.id}_${newYear}_${newMonth}",
                            title = "🌟 MÜŞTERİ MEMNUNİYETİ REKORU: ${model.specs.name}",
                            text = "${model.specs.name} modeli %${updatedSatisfaction} kullanıcı memnuniyet oranı ve sıfıra yakın iadeyle pazarda efsaneleşti! Kulaktan kulağa övgüler satışları patlatıyor.",
                            category = "Pazar",
                            year = newYear,
                            month = newMonth
                        )
                    )
                }

                var isExtendedNewsSent = model.isExtendedNewsSent
                if (newMonths == 24 && model.maxMonthsOnMarket == 48 && !model.isExtendedNewsSent) {
                    isExtendedNewsSent = true
                    finishedModelsNews.add(
                        NewsArticle(
                            id = "news_ext_${model.id}",
                            title = "UZUN ÖMÜRLÜ MODEL: ${model.specs.name} 3. Yılına Girdi!",
                            text = "${model.specs.name} modeli güçlü müşteri memnuniyeti (${model.reviewScore}/100) sayesinde uzun-kuyruk satış dönemine geçti. Doğru fiyat ve indirimlerle 48 aya kadar pazarda kalabilir.",
                            category = "Pazar",
                            year = newYear,
                            month = newMonth
                        )
                    )
                }

                val updatedCampaign = model.activeCampaign?.let { camp ->
                    if (isSecondHalf) {
                        if (camp.remainingMonths > 1) camp.copy(remainingMonths = camp.remainingMonths - 1) else null
                    } else camp
                }

                val updatedModel = model.copy(
                    remainingStock = (model.remainingStock - netUnitsSoldThisPeriod).coerceAtLeast(0),
                    totalSold = model.totalSold + netUnitsSoldThisPeriod,
                    totalRevenue = model.totalRevenue + netRevenueThisPeriod,
                    periodsOnMarket = newPeriods,
                    monthsOnMarket = newMonths,
                    hypeScore = currentHype,
                    customerSatisfactionScore = updatedSatisfaction,
                    totalRefundsCount = model.totalRefundsCount + refundUnits,
                    lastPeriodRefunds = refundUnits,
                    lastPeriodRefundCost = periodRefundCost,
                    wordOfMouthBoost = wordOfMouthMultiplier,
                    isExtendedNewsSent = isExtendedNewsSent,
                    activeCampaign = updatedCampaign,
                    matchesTrend = isTrendActive,
                    lastProductQualityScore = productQualityGroup,
                    lastMarketDemandScore = marketDemandGroup,
                    lastBrandStrengthScore = brandStrengthGroup
                )

                // Check if model completed its sales cycle or sold out
                if (updatedModel.isCompleted && !model.isCompleted) {
                    finishedModelsNews.add(
                        NewsArticle(
                            id = "news_finish_${updatedModel.id}",
                            title = "SATIŞ TAMAMLANDI: ${updatedModel.specs.name}",
                            text = "${updatedModel.specs.name} modelinin ${updatedModel.maxMonthsOnMarket} aylık pazar ömrü tamamlandı. Toplam ${"%,d".format(updatedModel.totalSold)} / ${"%,d".format(updatedModel.totalStock)} adet satıldı ve $${"%,d".format(updatedModel.totalRevenue)} ciro sağlandı.",
                            category = "Şirket",
                            year = newYear,
                            month = newMonth
                        )
                    )
                }

                // Geri Çağırma (Recall) & Kronik Donanım Krizi Kontrolü
                val (modelAfterRecallCheck, recallOutcome) = checkForRecall(updatedModel, newYear, newMonth)
                if (recallOutcome != null) {
                    val crisisType = detectHardwareCrisisType(modelAfterRecallCheck.specs)
                    val crisisId = "crisis_${modelAfterRecallCheck.id}_${newYear}_${newMonth}"
                    val newCrisis = HardwareCrisis(
                        id = crisisId,
                        modelId = modelAfterRecallCheck.id,
                        modelName = modelAfterRecallCheck.specs.name,
                        crisisType = crisisType,
                        severityLevel = if (modelAfterRecallCheck.recallRiskPercent > 35) 3 else if (modelAfterRecallCheck.recallRiskPercent > 20) 2 else 1,
                        yearTriggered = newYear,
                        monthTriggered = newMonth,
                        periodTriggered = newPeriod,
                        affectedUnitsCount = modelAfterRecallCheck.totalSold,
                        isResolved = false
                    )

                    // State'e kriz ekleme listesi için biriktir
                    newCrisesList.add(newCrisis)

                    finishedModelsNews.add(
                        NewsArticle(
                            id = "news_crisis_${modelAfterRecallCheck.id}",
                            title = "🚨 KRİZ PATLAK VERDİ: ${modelAfterRecallCheck.specs.name} (${crisisType.title})",
                            text = "${modelAfterRecallCheck.specs.name} modelinde '${crisisType.title}' skandalı patlak verdi! ${crisisType.description} Kullanıcılar sosyal medyada tepkili. Cihazlar ekranından 'Acil Kriz Yönetimi' masasını toplayıp karar almalısınız!",
                            category = "Şirket",
                            year = newYear,
                            month = newMonth
                        )
                    )
                }

                modelAfterRecallCheck
            } else {
                workingModel
            }
        }

        // --- CUSTOM OS BI-WEEKLY EVOLUTION & ADOPTION LOGIC ---
        var updatedCustomOs = currentState.customOs
        var appStoreRevenueThisPeriod = 0L
        var licenseRevenueThisPeriod = 0L
        val osReports = mutableListOf<MarketReport>()
        val osNewsList = mutableListOf<NewsArticle>()

        // 0. Active OS Development Countdown & Completion
        if (updatedCustomOs.activeDevelopment != null) {
            val activeDev = updatedCustomOs.activeDevelopment!!
            val assigned = updatedCustomOs.assignedDevs.coerceIn(0, currentState.engineers)
            val progressSteps = when {
                assigned >= 35 -> 3
                assigned >= 12 -> 2
                else -> 1
            }
            val remaining = activeDev.remainingMonths - progressSteps

            if (remaining <= 0) {
                // OS Development Completed!
                val isMajor = activeDev.isMajorUpdate
                val newMajorVer = if (isMajor) updatedCustomOs.majorVersionCount + 1 else 1
                val newVersionStr = activeDev.targetVersion

                val baseOpt = when (activeDev.focus) {
                    OsFocus.GAMING_TURBO -> 45
                    OsFocus.LIGHTWEIGHT -> 50
                    OsFocus.AI_SMART -> 42
                    OsFocus.SECURITY -> 40
                    OsFocus.AESTHETIC -> 38
                }

                updatedCustomOs = updatedCustomOs.copy(
                    activeDevelopment = null,
                    name = activeDev.name,
                    version = newVersionStr,
                    type = activeDev.type,
                    licenseType = activeDev.licenseType,
                    focus = activeDev.focus,
                    themeColorHex = activeDev.themeColorHex,
                    perDeviceLicenseFee = activeDev.perDeviceLicenseFee,
                    majorVersionCount = newMajorVer,
                    minorVersionCount = 0,
                    stability = 100,
                    optimizationScore = (baseOpt + (assigned * 2)).coerceIn(30, 95),
                    popularityPercent = if (isMajor) (updatedCustomOs.popularityPercent + 6.0f).coerceAtMost(85f) else 5.0f,
                    ecosystemScore = if (isMajor) (updatedCustomOs.ecosystemScore + 8).coerceAtMost(100) else 15
                )

                osNewsList.add(
                    NewsArticle(
                        id = "news_os_launch_${newYear}_${newMonth}_${newPeriod}_${Random.nextInt(100, 999)}",
                        title = "🚀 YENİ İŞLETİM SİSTEMİ: ${activeDev.name} v$newVersionStr Yayında!",
                        text = "${currentState.companyName} şirketi, geliştirmesini tamamladığı '${activeDev.name} v$newVersionStr' (${activeDev.type.title}) mobil işletim sistemini resmen duyurdu ve ekosistemine kazandırdı!",
                        category = "Yazılım",
                        year = newYear,
                        month = newMonth
                    )
                )

                osReports.add(
                    MarketReport(
                        title = "İşletim Sistemi Ar-Ge Tamamlandı: ${activeDev.name}",
                        text = "${activeDev.name} v$newVersionStr projesinin Ar-Ge ve optimizasyon süreci başarıyla bitti. Artık Cihazlar sekmesinden yeni telefon modellerinizde '${activeDev.name}' seçebilir ve mağaza ekosisteminizi büyütebilirsiniz.",
                        profit = 0,
                        unitsSold = 0,
                        reviewScore = 0
                    )
                )
            } else {
                updatedCustomOs = updatedCustomOs.copy(
                    activeDevelopment = activeDev.copy(remainingMonths = remaining)
                )
            }
        }

        if (updatedCustomOs.isCustomActive) {
            val devCount = updatedCustomOs.assignedDevs.coerceAtMost(currentState.engineers)
            
            // 1. Developers produce XP and increase optimization score (bi-weekly)
            val periodXpGain = ((devCount * 25) + (currentState.engineers * 4)) / 2
            val newDevXp = updatedCustomOs.devXp + periodXpGain
            
            val newOptScore = (20 + (devCount * 4) + (updatedCustomOs.majorVersionCount * 8) + (newDevXp / 120)).coerceIn(15, 100)
            
            // 2. Active player phones running this OS
            val activePlayerUserBase = updatedActiveModels
                .filter { it.specs.osName == updatedCustomOs.name }
                .sumOf { it.totalSold }

            // 3. Third-Party / OEM Adoption growth
            var adopters = updatedCustomOs.thirdPartyAdoptersCount
            val licenseType = updatedCustomOs.licenseType
            
            if (licenseType == OsLicenseType.OPEN_SOURCE) {
                if (updatedCustomOs.popularityPercent >= 4.0f && adopters < 2) adopters = 2
                if (updatedCustomOs.popularityPercent >= 10.0f && adopters < 4) adopters = 4
                if (updatedCustomOs.popularityPercent >= 20.0f && adopters < 7) adopters = 7
                if (updatedCustomOs.popularityPercent >= 35.0f && adopters < 12) adopters = 12
                if (updatedCustomOs.popularityPercent >= 50.0f && adopters < 18) adopters = 18
            } else {
                if (updatedCustomOs.perDeviceLicenseFee <= 25 && updatedCustomOs.popularityPercent >= 8.0f && adopters < 1) adopters = 1
                if (updatedCustomOs.perDeviceLicenseFee <= 20 && updatedCustomOs.popularityPercent >= 18.0f && adopters < 2) adopters = 2
                if (updatedCustomOs.perDeviceLicenseFee <= 15 && updatedCustomOs.popularityPercent >= 30.0f && adopters < 4) adopters = 4
                if (updatedCustomOs.perDeviceLicenseFee <= 10 && updatedCustomOs.popularityPercent >= 45.0f && adopters < 7) adopters = 7
            }

            // Bi-weekly device production by third party adopters
            val thirdPartyPeriodProduction = if (adopters > 0) {
                val perPartnerVolume = ((10000 + (newYear - 2010) * 3500) * (updatedCustomOs.popularityPercent / 10f).coerceAtLeast(0.4f)) / 2f
                (adopters * perPartnerVolume * licenseType.adoptionSpeedMultiplier).toLong()
            } else 0L

            val newThirdPartyActiveDevices = updatedCustomOs.thirdPartyActiveDevices + thirdPartyPeriodProduction

            // 4. Popularity increment (Significantly slowed down as requested)
            val devGrowthFactor = (devCount * 0.05f)
            val versionFactor = (updatedCustomOs.majorVersionCount * 0.10f)
            val playerSalesFactor = (totalMonthlyUnitsSold / 40000f) // Slower scaling from sales
            val adoptionBonus = (adopters * 0.10f * licenseType.adoptionSpeedMultiplier)
            // Reduced multiplier from 0.18f to 0.035f for realistic, slow growth
            val popularityDelta = (devGrowthFactor + versionFactor + playerSalesFactor + adoptionBonus) * 0.035f
            
            val newPopularity = (updatedCustomOs.popularityPercent + popularityDelta).coerceIn(1.0f, 85.0f)
            val newEcosystemScore = (15 + (newPopularity * 0.5f).toInt() + (devCount * 1) + (updatedCustomOs.majorVersionCount * 3)).coerceIn(10, 100)

            // 5. App Store Revenue & License Revenue Calculation (Bi-weekly)
            val totalEcosystemDevices = activePlayerUserBase + newThirdPartyActiveDevices
            if (totalEcosystemDevices > 0) {
                val baseUserRate = updatedCustomOs.type.storeRevenuePerUser / 2f
                val commissionMultiplier = (updatedCustomOs.commissionRate.percent / 20.0f) * updatedCustomOs.commissionRate.marketLoyaltyBonus
                val popMultiplier = (newPopularity / 40.0f).coerceIn(0.10f, 1.5f)
                val storeModuleBonus = 1.0f + (updatedCustomOs.appStoreLevel - 1) * 0.15f
                
                appStoreRevenueThisPeriod = (totalEcosystemDevices * baseUserRate * commissionMultiplier * popMultiplier * licenseType.storeRevenueMultiplier * storeModuleBonus / 5.0f).toLong()
            }

            // 6. Cloud & Ecosystem Subscription Revenue
            val cloudRevenueThisPeriod = if (updatedCustomOs.cloudLevel > 1 && totalEcosystemDevices > 0) {
                val payingUsersRatio = 0.04f + (updatedCustomOs.cloudLevel * 0.02f)
                val cloudUsers = (totalEcosystemDevices * payingUsersRatio).toLong()
                (cloudUsers * 0.50f).toLong() // $0.50 per 2-weeks ($1/mo)
            } else 0L

            if (licenseType == OsLicenseType.CLOSED_PROPRIETARY && updatedCustomOs.perDeviceLicenseFee > 0 && thirdPartyPeriodProduction > 0) {
                licenseRevenueThisPeriod = thirdPartyPeriodProduction * updatedCustomOs.perDeviceLicenseFee
            }

            // 7. Store Apps Catalog Growth (Made significantly harder)
            val appsFromDevFund = (updatedCustomOs.devFundBalance / 250000L).coerceAtLeast(0L)
            val newAppsAdded = ((150L * updatedCustomOs.appStoreLevel) + (devCount * 50L) + (updatedCustomOs.ecosystemScore * 20L) + appsFromDevFund) / 2L
            val newTotalApps = updatedCustomOs.totalStoreApps + newAppsAdded

            // 8. Dynamic Customer Loyalty calculation
            val calculatedLoyalty = (35.0f + 
                (updatedCustomOs.ecosystemScore * 0.35f) + 
                (updatedCustomOs.cloudLevel * 4.5f) + 
                (updatedCustomOs.securityLevel * 3.5f) + 
                (updatedCustomOs.updateGuarantee.reputationBonus * 1.2f)
            ).coerceIn(25.0f, 96.0f)

            // Automatic minor patch update if XP thresholds reached
            var minorVer = updatedCustomOs.minorVersionCount
            var majorVer = updatedCustomOs.majorVersionCount
            var newVerStr = updatedCustomOs.version
            if (devCount >= 2 && newDevXp >= (minorVer + 1) * 250 && minorVer < 9) {
                minorVer++
                newVerStr = "$majorVer.$minorVer"
            }

            updatedCustomOs = updatedCustomOs.copy(
                version = newVerStr,
                minorVersionCount = minorVer,
                devXp = newDevXp,
                optimizationScore = newOptScore,
                popularityPercent = newPopularity,
                ecosystemScore = newEcosystemScore,
                thirdPartyAdoptersCount = adopters,
                thirdPartyActiveDevices = newThirdPartyActiveDevices,
                totalStoreApps = newTotalApps,
                customerLoyaltyPercent = calculatedLoyalty,
                totalAppStoreRevenueToDate = updatedCustomOs.totalAppStoreRevenueToDate + appStoreRevenueThisPeriod,
                totalLicenseRevenueToDate = updatedCustomOs.totalLicenseRevenueToDate + licenseRevenueThisPeriod,
                totalCloudRevenueToDate = updatedCustomOs.totalCloudRevenueToDate + cloudRevenueThisPeriod,
                lastMonthAppStoreIncome = appStoreRevenueThisPeriod * 2,
                lastMonthLicenseIncome = licenseRevenueThisPeriod * 2,
                lastMonthCloudRevenue = cloudRevenueThisPeriod * 2,
                popularityHistory = (updatedCustomOs.popularityHistory + newPopularity).takeLast(30)
            )
        }

        // --- CUSTOM CHIPSET / IN-HOUSE SILICON OEM SALES LOGIC ---
        var periodChipsetIncome = 0L
        var periodChipsetsSold = 0
        val updatedChipsets = currentState.customChipsets.map { chip ->
            if (chip.isOemSaleActive) {
                val profitPerUnit = (chip.oemSalePrice - chip.unitCost).coerceAtLeast(0)
                val perfPerDollar = (chip.performanceScore.toFloat() / chip.oemSalePrice.coerceAtLeast(10).toFloat())
                val marketReputationFactor = (currentState.reputation / 50f).coerceIn(0.5f, 2.0f)
                val tierDemand = chip.tier.marketVolumeMultiplier
                val baseOrders = (7500 * tierDemand * marketReputationFactor * (perfPerDollar / 14f)).toInt().coerceIn(1500, 95000)
                val periodIncome = baseOrders.toLong() * profitPerUnit.toLong()

                periodChipsetIncome += periodIncome
                periodChipsetsSold += baseOrders

                chip.copy(
                    totalUnitsSoldToThirdParties = chip.totalUnitsSoldToThirdParties + baseOrders,
                    totalOemRevenueEarned = chip.totalOemRevenueEarned + periodIncome,
                    lastPeriodUnitsSold = baseOrders,
                    lastPeriodOemIncome = periodIncome
                )
            } else {
                chip.copy(lastPeriodUnitsSold = 0, lastPeriodOemIncome = 0L)
            }
        }

        // Patent lisansları dönemsel gelir üretir. Patent süresi her 2 haftalık dönemde azalır.
        val patentLicenseRevenueThisPeriod = currentState.patents
            .filter { it.strategy == PatentStrategy.LICENSED && it.remainingPeriods > 0 }
            .sumOf { (it.monthlyLicenseIncome / 2).coerceAtLeast(0L) }

        val existingPatentIds = currentState.patents.map { it.techId }.toSet()
        val migratedLegacyPatents = currentState.unlockedTech
            .filterNot { it in existingPatentIds }
            .map { techId -> PatentAsset(
                techId = techId,
                techName = techId,
                baseValue = 300000L,
                strategy = PatentStrategy.PROTECTED,
                remainingPeriods = 120
            ) }

        var updatedPatents = (currentState.patents + migratedLegacyPatents).map { patent ->
            if (patent.strategy == PatentStrategy.PROTECTED || patent.strategy == PatentStrategy.LICENSED) {
                val remaining = (patent.remainingPeriods - 1).coerceAtLeast(0)
                if (remaining == 0) patent.copy(strategy = PatentStrategy.EXPIRED, remainingPeriods = 0, monthlyLicenseIncome = 0L)
                else patent.copy(remainingPeriods = remaining)
            } else patent
        }

        // Holding bünyesindeki alt markalardan gelen dönemsel temettü/kâr payı
        val subBrandDividendsThisPeriod = currentState.ownedSubBrands.sumOf { (it.monthlyDividend / 2).coerceAtLeast(0L) }

        val totalCombinedRevenue = totalMonthlyRevenue + appStoreRevenueThisPeriod + licenseRevenueThisPeriod + (updatedCustomOs.lastMonthCloudRevenue / 2) + periodChipsetIncome + subBrandDividendsThisPeriod + patentLicenseRevenueThisPeriod
        val periodExpenses = (currentState.totalMonthlyExpenses / 2).coerceAtLeast(0)
        val netIncome = totalCombinedRevenue - periodExpenses

        // Competitors AI Simulation & Releases (Every 6 periods = 3 months, or random chance)
        var updatedCompetitorReleases = currentState.competitorReleases.toMutableList()
        var updatedCompetitors = currentState.competitors.toMutableList()

        val shouldCompetitorLaunch = (newMonth % 3 == 0 && newPeriod == 1) || (Random.nextInt(100) < 15)
        if (shouldCompetitorLaunch && updatedCompetitors.isNotEmpty()) {
            val randomCompIndex = Random.nextInt(updatedCompetitors.size)
            val comp = updatedCompetitors[randomCompIndex]
            val (modelName, price, score) = getCompetitorModelForYear(comp.name, newYear)

            if (modelName != comp.currentTopModel) {
                val releaseHeadline = when (comp.name) {
                    "Samsung" -> "🌌 SAMSUNG GÖVDE GÖSTERİSİ: ${modelName} Satışta!"
                    "Apple" -> "🍎 APPLE LANSMANI: Yeni ${modelName} Tanıtıldı!"
                    "Xiaomi" -> "🟠 XIAOMI F/P CANAVARI: ${modelName} Duyuruldu!"
                    "Oppo" -> "🟢 OPPO KAMERA AMİRALİ: ${modelName} Piyasada!"
                    "Vivo" -> "🔷 VIVO ZEISS TEKNOLOJİSİ: ${modelName} Sahneye Çıktı!"
                    "Huawei" -> "🌸 HUAWEI XMAGE GÜCÜ: ${modelName} Resmen Duyuruldu!"
                    "Google" -> "🌐 GOOGLE PIXEL: Saf Android & Gemini Yapay Zekalı ${modelName} Geldi!"
                    "Motorola" -> "🦇 MOTOROLA EFSANESİ: ${modelName} Tanıtıldı!"
                    "OnePlus" -> "➕ ONEPLUS HIZ VE PERFORMANS: ${modelName} Satışta!"
                    "Realme" -> "⚡ REALME CESUR HAMLE: ${modelName} Duyuruldu!"
                    "Honor" -> "💫 HONOR ŞIK TASARIM: ${modelName} Vitrinlerde!"
                    "Sony" -> "📷 SONY PRO SENSÖR: ${modelName} İçerik Üreticileri İçin Çıktı!"
                    "Asus" -> "🎮 ASUS ROG OYUN CANAVARI: ${modelName} Duyuruldu!"
                    "Nokia" -> "🏛️ NOKIA DAYANIKLILIK & MİRAS: ${modelName} Geldi!"
                    "Tecno" -> "🌍 TECNO YÜKSELEN YILDIZ: ${modelName} Tanıtıldı!"
                    "Infinix" -> "🚀 INFINIX OYUNCU & HIZLI ŞARJ: ${modelName} Piyasada!"
                    "Nothing" -> "💡 NOTHING GLYPH ŞEFFAF TASARIM: ${modelName} Satışta!"
                    "ZTE" -> "🔮 ZTE REDMAGIC & ÇENTİKSİZ: ${modelName} Tanıtıldı!"
                    "TCL" -> "📺 TCL NXTPAPER MAT EKRAN: ${modelName} Duyuruldu!"
                    "Fairphone" -> "🌱 FAIRPHONE SÜRDÜRÜLEBİLİR & MODÜLER: ${modelName} Satışa Çıktı!"
                    else -> "📱 SEKTÖR YENİLİĞİ: ${comp.name} ${modelName} Modelini Duyurdu!"
                }

                val releaseNewsText = when (comp.name) {
                    "Samsung" -> "Samsung, sektör lideri ekran paneli ve güçlü donanımıyla ${modelName} modelini $${price} fiyatla piyasaya sundu. Pazar puanı: ${score}/100."
                    "Apple" -> "Apple şirketi yeni amiral gemisi ${modelName} modelini $${price} fiyat etiketiyle tanıttı. Tasarım ve ekosistem kalitesiyle eleştirmenlerden ${score}/100 aldı."
                    "Xiaomi" -> "Xiaomi agresif fiyat politikasıyla $${price} fiyata ${modelName} modelini satışa çıkardı. Cihaz ${score}/100 puanla yüksek talep topluyor."
                    "Oppo" -> "Oppo, gelişmiş kamera sensörleri ve SuperVOOC şarj desteğine sahip ${modelName} cihazını $${price} fiyata duyurdu (${score}/100)."
                    "Vivo" -> "Vivo, Zeiss optikleri ve ekran içi biyometrik inovasyonlarıyla öne çıkan ${modelName} modelini $${price} fiyata sundu (${score}/100)."
                    "Huawei" -> "Huawei, XMAGE görüntüleme teknolojisi ve güçlü tasarımıyla dikkat çeken ${modelName} cihazını $${price} fiyata satışa çıkardı (${score}/100)."
                    "Google" -> "Google, yapay zeka fotoğrafçılığı ve saf Android deneyimi sunan ${modelName} modelini $${price} fiyatla kullanıcılara sundu (${score}/100)."
                    "Motorola" -> "Motorola, ikonik tasarımı ve katlanabilir teknolojiyi harmanlayan ${modelName} modelini $${price} fiyat etiketiyle duyurdu (${score}/100)."
                    "OnePlus" -> "OnePlus, akıcı ekran tazeleme ve yüksek performansıyla 'amiral gemisi katili' genlerini taşıyan ${modelName} modelini $${price} fiyata çıkardı (${score}/100)."
                    "Realme" -> "Realme, genç kullanıcılara hitap eden iddialı ve rekor şarj hızlı ${modelName} modelini $${price} fiyata satışa sundu (${score}/100)."
                    "Honor" -> "Honor, ultra ince gövde mühendisliği ve göz dostu ekranıyla ${modelName} modelini $${price} fiyata tanıttı (${score}/100)."
                    "Sony" -> "Sony, profesyonel Exmor-T kamera sensörleri ve 4K ekran teknolojili ${modelName} cihazını $${price} fiyata pazara sundu (${score}/100)."
                    "Asus" -> "Asus ROG, aktif soğutma fanı ve yüksek ekran tazeleme hızına sahip ${modelName} oyuncu canavarını $${price} fiyata duyurdu (${score}/100)."
                    "Nokia" -> "Nokia (HMD), kolay tamir edilebilirlik ve efsanevi dayanıklılık sunan ${modelName} modelini $${price} fiyata satışa çıkardı (${score}/100)."
                    "Tecno" -> "Tecno, gelişmekte olan pazarlarda büyük ilgi gören ve şık tasarımıyla dikkat çeken ${modelName} modelini $${price} fiyata sundu (${score}/100)."
                    "Infinix" -> "Infinix, bütçe dostu mecha oyun tasarımı ve süper hızlı şarjlı ${modelName} cihazını $${price} fiyata tanıttı (${score}/100)."
                    "Nothing" -> "Nothing, şeffaf Glyph LED ışıklandırması ve minimalist işletim sistemli ${modelName} modelini $${price} fiyata satışa çıkardı (${score}/100)."
                    "ZTE" -> "ZTE Nubia, gerçek çentiksiz ekran altı kameraya ve oyun donanımına sahip ${modelName} modelini $${price} fiyata duyurdu (${score}/100)."
                    "TCL" -> "TCL, göz yormayan mat NXTPAPER ekran paneli teknolojili ${modelName} cihazını $${price} fiyata tüketicilere sundu (${score}/100)."
                    "Fairphone" -> "Fairphone, %100 modüler tornavidayla tamir edilebilir ve adil ticaret materyalli ${modelName} modelini $${price} fiyata piyasaya sürdü (${score}/100)."
                    else -> "${comp.name}, yeni akıllı telefon modeli ${modelName} cihazını $${price} fiyat ve ${score}/100 değerlendirme notuyla piyasaya sundu."
                }

                val hardware = getCompetitorHardwareSpecs(comp.name, newYear)

                // VS Duel with Player's Best Active Model
                val playerBestModel = currentState.activeModels.filter { !it.isCompleted }.maxByOrNull { it.reviewScore }
                val duelVerdict = if (playerBestModel != null) {
                    val playerHwIndex = HardwareRatingHelper.calculateHardwarePowerIndex(
                        processor = playerBestModel.specs.processor,
                        ram = playerBestModel.specs.ramCapacity,
                        camera = playerBestModel.specs.camera,
                        battery = playerBestModel.specs.batteryCapacity,
                        display = playerBestModel.specs.display
                    )
                    val compHwIndex = HardwareRatingHelper.calculateHardwarePowerIndex(
                        processor = hardware.processor,
                        ram = hardware.ram,
                        camera = hardware.camera,
                        battery = hardware.battery,
                        display = hardware.display
                    )
                    val scoreDiff = playerBestModel.reviewScore - score
                    val hwDiff = playerHwIndex - compHwIndex

                    when {
                        scoreDiff >= 4 && hwDiff >= 40 -> "🏆 Şirketiniz (${playerBestModel.specs.name}) Hem Üstün Donanımı Hem de Yüksek Puanıyla Ezdi!"
                        scoreDiff >= 4 && hwDiff <= -40 -> "🎯 Şirketiniz (${playerBestModel.specs.name}), Donanımda Geride Olsa da Yazılım ve Tasarımıyla Kazandı!"
                        scoreDiff >= 4 -> "🏆 Şirketiniz (${playerBestModel.specs.name}) Yüksek Eleştirmen Puanıyla Öne Çıktı!"
                        scoreDiff <= -4 && hwDiff >= 40 -> "💡 Şirketiniz Güçlü Donanıma Sahip, Ancak Rakip Fiyat/Optimizasyonla Puanı Kaptı."
                        scoreDiff <= -4 && hwDiff <= -40 -> "⚡ Rakip (${modelName}) Üstün Donanımı ve Yüksek Puanıyla Ezdi!"
                        scoreDiff <= -4 -> "⚡ Rakip (${modelName}) Eleştirmen Puanlarında Öne Geçti!"
                        hwDiff >= 60 -> "💪 Donanım Canavarı Cihazınız (${playerBestModel.specs.name}) Saf Güçte Önde!"
                        hwDiff <= -60 -> "⚠️ Rakip (${modelName}) Donanım Gücünde Fark Yarattı!"
                        else -> "⚖️ Kafa Kafaya Mücadele! İki Cihaz da Çok Dengeli."
                    }
                } else null

                newNewsList.add(
                    NewsArticle(
                        id = "comp_launch_${newYear}_${newMonth}_${newPeriod}_${Random.nextInt(100, 999)}",
                        title = releaseHeadline,
                        text = releaseNewsText,
                        category = "Sektör",
                        year = newYear,
                        month = newMonth
                    )
                )

                updatedCompetitorReleases.add(
                    0,
                    CompetitorReleaseHistory(
                        id = "rel_${comp.id}_${newYear}_${newMonth}_${newPeriod}",
                        companyName = comp.name,
                        logoEmoji = comp.logoEmoji,
                        modelName = modelName,
                        price = price,
                        score = score,
                        year = newYear,
                        month = newMonth,
                        headline = releaseHeadline,
                        processor = hardware.processor,
                        ram = hardware.ram,
                        camera = hardware.camera,
                        battery = hardware.battery,
                        display = hardware.display,
                        vsPlayerModelName = playerBestModel?.specs?.name,
                        vsPlayerModelScore = playerBestModel?.reviewScore,
                        vsPlayerModelPrice = playerBestModel?.specs?.price,
                        duelVerdict = duelVerdict
                    )
                )

                updatedCompetitors[randomCompIndex] = comp.copy(
                    currentTopModel = modelName,
                    currentModelPrice = price,
                    currentModelScore = score
                )
            }
        }

        // Global Market Sizing (Bi-weekly volume)
        val baseMarketSize = ((800000 + (newYear - 2010) * 100000 + (newMonth * 15000)) / 2).coerceAtLeast(300000)
        val totalMarketVolume = baseMarketSize

        // Calculate player market share
        val playerSharePercent = ((totalMonthlyUnitsSold.toFloat() / totalMarketVolume.toFloat()) * 100f).coerceIn(0f, 95f)

        // Remaining market share distributed to competitors (Total player + competitors = 100%)
        val remainingShare = (100f - playerSharePercent).coerceAtLeast(0f)
        val compWeights = mapOf(
            "Samsung" to 0.215f,
            "Apple" to 0.195f,
            "Xiaomi" to 0.130f,
            "Oppo" to 0.085f,
            "Vivo" to 0.075f,
            "Huawei" to 0.055f,
            "Google" to 0.035f,
            "Motorola" to 0.032f,
            "OnePlus" to 0.028f,
            "Realme" to 0.026f,
            "Honor" to 0.024f,
            "Sony" to 0.018f,
            "Asus" to 0.015f,
            "Nokia" to 0.015f,
            "Tecno" to 0.018f,
            "Infinix" to 0.015f,
            "Nothing" to 0.010f,
            "ZTE" to 0.012f,
            "TCL" to 0.012f,
            "Fairphone" to 0.005f
        )

        val totalWeight = updatedCompetitors.sumOf { comp ->
            val raw = compWeights[comp.name] ?: (1.0f / updatedCompetitors.size.coerceAtLeast(1))
            raw.coerceAtLeast(0f).toDouble()
        }.toFloat()

        updatedCompetitors = updatedCompetitors.map { comp ->
            val rawWeight = (compWeights[comp.name] ?: (1.0f / updatedCompetitors.size.coerceAtLeast(1))).coerceAtLeast(0f)
            val normalizedWeight = if (totalWeight > 0f) rawWeight / totalWeight else (1.0f / updatedCompetitors.size.coerceAtLeast(1))
            val compShare = remainingShare * normalizedWeight
            val compSales = (totalMarketVolume * (compShare / 100f)).toInt().coerceAtLeast(0)
            comp.copy(
                marketSharePercent = compShare,
                monthlySales = compSales * 2
            )
        }.toMutableList()

        // Process Active Research progress (Steps every 2-week period)
        var currentActiveResearch = currentState.activeResearch
        var updatedUnlockedTech = currentState.unlockedTech
        val researchNewsList = mutableListOf<NewsArticle>()
        val researchReports = mutableListOf<MarketReport>()

        if (currentActiveResearch != null) {
            val remaining = currentActiveResearch.remainingMonths - 1
            if (remaining <= 0) {
                // Research completed!
                updatedUnlockedTech = updatedUnlockedTech + currentActiveResearch.techId
                val completedTechName = currentActiveResearch.techName

                // Her tamamlanan Ar-Ge otomatik olarak korunan bir patent varlığına dönüşür.
                // Eski kayıtlarda patent listesi boş olabileceği için yalnızca yeni tamamlanan teknoloji eklenir.
                if (updatedPatents.none { it.techId == currentActiveResearch.techId }) {
                    val patentValue = (currentActiveResearch.cost * 65L / 100L).coerceAtLeast(150000L)
                    updatedPatents = updatedPatents + PatentAsset(
                        techId = currentActiveResearch.techId,
                        techName = completedTechName,
                        baseValue = patentValue,
                        strategy = PatentStrategy.PROTECTED,
                        remainingPeriods = 120
                    )
                }
                
                researchReports.add(
                    MarketReport(
                        title = "Ar-Ge Tamamlandı: $completedTechName",
                        text = "Ar-Ge mühendislerimizin çalışmaları sonucunda $completedTechName teknolojisi başarıyla tamamlandı! Artık yeni cihaz modellerinizde kullanabilirsiniz.",
                        profit = 0,
                        unitsSold = 0,
                        reviewScore = 0
                    )
                )

                researchNewsList.add(
                    NewsArticle(
                        id = "news_tech_finish_${newYear}_${newMonth}_${newPeriod}_${Random.nextInt(100, 999)}",
                        title = "AR-GE BAŞARISI: $completedTechName Tamamlandı",
                        text = "Şirketinizin Ar-Ge ekibi $completedTechName entegrasyonunu tamamladı ve yeni modellerde kullanıma sundu.",
                        category = "Teknoloji",
                        year = newYear,
                        month = newMonth
                    )
                )

                currentActiveResearch = null
            } else {
                currentActiveResearch = currentActiveResearch.copy(remainingMonths = remaining)
            }
        }

        // Check and trigger next queued research automatically if available
        var updatedResearchQueue = currentState.researchQueue.toMutableList()
        var autoStartResearchCost = 0L

        if (currentActiveResearch == null && updatedResearchQueue.isNotEmpty()) {
            val nextCandidate = updatedResearchQueue.first()
            val availableBudget = currentState.budget + netIncome
            if (availableBudget >= nextCandidate.cost) {
                val duration = calculateResearchDuration(currentState.engineers, nextCandidate.cost)
                currentActiveResearch = nextCandidate.copy(
                    totalMonths = duration,
                    remainingMonths = duration
                )
                autoStartResearchCost = nextCandidate.cost
                updatedResearchQueue.removeAt(0)

                researchReports.add(
                    MarketReport(
                        title = "Sıradaki Ar-Ge Başlatıldı: ${nextCandidate.techName}",
                        text = "Kuyruktaki ${nextCandidate.techName} araştırması otomatik olarak başlatıldı ($${"%,d".format(nextCandidate.cost)} harcandı). Tahmini süre: $duration Dönem (2 Hafta x $duration).",
                        profit = -nextCandidate.cost,
                        unitsSold = 0,
                        reviewScore = 0
                    )
                )
                researchNewsList.add(
                    NewsArticle(
                        id = "news_tech_auto_${newYear}_${newMonth}_${newPeriod}_${Random.nextInt(100, 999)}",
                        title = "OTOMATİK AR-GE BAŞLADI: ${nextCandidate.techName}",
                        text = "Ar-Ge ekibi sıradaki '${nextCandidate.techName}' projesi üzerinde çalışmaya başladı.",
                        category = "Teknoloji",
                        year = newYear,
                        month = newMonth
                    )
                )
            }
        }

        // Fetch historical sector news for this year/month (only once per month on period 1)
        if (newPeriod == 1) {
            val periodicNews = getHistoricalNewsForYearMonth(newYear, newMonth)
            newNewsList.addAll(periodicNews)
        }
        newNewsList.addAll(finishedModelsNews)
        newNewsList.addAll(researchNewsList)
        newNewsList.addAll(osNewsList)

        val activeCount = updatedActiveModels.count { !it.isCompleted }

        val storeNote = if (appStoreRevenueThisPeriod > 0) " 🌐 ${updatedCustomOs.name} Mağaza Geliri: $${"%,d".format(appStoreRevenueThisPeriod)}." else ""
        val licenseNote = if (licenseRevenueThisPeriod > 0) " 🔒 OEM Lisans Satış Geliri: $${"%,d".format(licenseRevenueThisPeriod)}." else ""
        val chipsetNote = if (periodChipsetIncome > 0) " ⚡ OEM Yonga Satış Kârı: +$${"%,d".format(periodChipsetIncome)} (${"%,d".format(periodChipsetsSold)} adet)." else ""

        val report = MarketReport(
            title = "2 Haftalık Finansal Rapor (${getMonthName(newMonth)} $newYear - $periodName)",
            text = "$newYear yılı ${getMonthName(newMonth)} ayı $periodName tamamlandı. Satıştaki $activeCount modelden bu 2 haftalık dönemde ${"%,d".format(totalMonthlyUnitsSold)} adet cihaz satıldı (Pazar Payı: %${"%.1f".format(playerSharePercent)}). Cihaz Satış Geliri: $${"%,d".format(totalMonthlyRevenue)}.$storeNote$licenseNote$chipsetNote Dönemlik Giderler: $${"%,d".format(periodExpenses)}. Net Gelir: $${"%,d".format(netIncome)}.",
            profit = netIncome,
            unitsSold = totalMonthlyUnitsSold,
            reviewScore = 0
        )

        // Tech Expo Grand Awards Ceremony held at the end of December (Period 2)
        val isYearEndExpo = (newMonth == 12 && newPeriod == 2)
        var triggeredExpoEvent: TechExpoEvent? = null
        var expoPrizeTotal = 0L
        var expoRepGain = 0

        if (isYearEndExpo) {
            val expo = generateTechExpo(
                year = newYear,
                playerModels = updatedActiveModels,
                playerCompanyName = currentState.companyName,
                playerBrandColorHex = currentState.companyBrandColorHex,
                competitors = updatedCompetitors
            )
            triggeredExpoEvent = expo
            expoPrizeTotal = expo.totalPrizeWon
            expoRepGain = expo.reputationGained

            newNewsList.add(
                0,
                NewsArticle(
                    id = "expo_news_${newYear}_12",
                    title = "🌐 ${expo.expoName.uppercase()} ÖDÜLLERİ AÇIKLANDI!",
                    text = "${expo.city} şehrinde düzenlenen küresel teknoloji fuarında ${currentState.companyName} şirketi ${expo.playerWonCount} prestijli ödül kazandı! ($${"%,d".format(expoPrizeTotal)} ödül havuzu & +${expoRepGain} İtibar)",
                    category = "Sektör",
                    year = newYear,
                    month = 12
                )
            )
        }

        // --- KADEMELİ İTİBAR SÜRÜKLENMESİ (yumuşak, öngörülebilir itibar değişimi) ---
        // Bu periyotta fiilen satılan cihazların ağırlıklı ortalama inceleme puanına göre itibarı
        // çok küçük adımlarla hedefe doğru çeker. Kesirli birikim (momentum) sayesinde tek periyotta
        // asla ani bir sıçrama olmaz; etki ancak birkaç periyot boyunca tutarlı kalite/kalitesizlikle birikir.
        var updatedReputationMomentum = currentState.reputationMomentum
        var passiveReputationDelta = 0
        if (totalMonthlyUnitsSold > 0) {
            val avgSoldQuality = qualityWeightedReviewSum / totalMonthlyUnitsSold.toDouble()
            // 65 puan "nötr" kabul edilir: bunun altı itibarı yavaşça aşındırır, üstü yavaşça güçlendirir.
            val qualityGap = (avgSoldQuality - 65.0) / 35.0 // yaklaşık -1.3..+1.0 aralığı
            val driftThisPeriod = (qualityGap * 0.35).coerceIn(-0.5, 0.5)
            updatedReputationMomentum += driftThisPeriod.toFloat()
        }
        // Birikim tam sayı puana ulaştıysa gerçek itibar değişimine dönüştür, kalanı sakla
        if (updatedReputationMomentum >= 1f) {
            passiveReputationDelta = kotlin.math.floor(updatedReputationMomentum).toInt()
            updatedReputationMomentum -= passiveReputationDelta
        } else if (updatedReputationMomentum <= -1f) {
            passiveReputationDelta = kotlin.math.ceil(updatedReputationMomentum).toInt()
            updatedReputationMomentum -= passiveReputationDelta
        }
        passiveReputationDelta -= directReputationPenaltyFromRefunds

        // --- TEDARİK ZİNCİRİ OLAYI (SUPPLY CHAIN EVENT) — ana state.update'ten ÖNCE hesaplanmalı ---
        val (nextSupplyEvent, supplyEventJustStarted, endedSupplyEvent) = tickSupplyChainEvent(currentState.activeSupplyChainEvent, newYear)
        if (supplyEventJustStarted && nextSupplyEvent != null) {
            val direction = if (nextSupplyEvent.costMultiplierPercent > 100) "artırıyor" else "azaltıyor"
            val deltaPercent = kotlin.math.abs(nextSupplyEvent.costMultiplierPercent - 100)
            newNewsList.add(
                0,
                NewsArticle(
                    id = "supply_start_${nextSupplyEvent.id}",
                    title = "${nextSupplyEvent.icon} TEDARİK ZİNCİRİ: ${nextSupplyEvent.title}",
                    text = "${nextSupplyEvent.description} Bu durum yaklaşık ${nextSupplyEvent.totalPeriods} periyot boyunca üretim maliyetlerini %$deltaPercent $direction.",
                    category = "Sektör",
                    year = newYear,
                    month = newMonth
                )
            )
        }
        if (endedSupplyEvent != null) {
            newNewsList.add(
                NewsArticle(
                    id = "supply_end_${endedSupplyEvent.id}",
                    title = "✅ ATLATILDI: ${endedSupplyEvent.title} Sona Erdi",
                    text = "${endedSupplyEvent.title} etkisini yitirdi, üretim maliyetleri normale döndü.",
                    category = "Sektör",
                    year = newYear,
                    month = newMonth
                )
            )
        }

        // --- BANK LOANS & DEBT PROCESSING ---
        val processedLoans = mutableListOf<BankLoan>()
        val closedLoansNews = mutableListOf<NewsArticle>()
        var creditScoreGain = 0

        for (loan in currentState.activeLoans) {
            val payment = loan.periodPayment
            val newRemainingBalance = (loan.remainingBalance - payment).coerceAtLeast(0L)
            val newRemainingPeriods = loan.remainingPeriods - 1

            if (newRemainingPeriods <= 0 || newRemainingBalance <= 0) {
                // Loan fully paid off!
                creditScoreGain += 20
                closedLoansNews.add(
                    NewsArticle(
                        id = "loan_closed_${loan.id}_${newYear}_${newMonth}_${newPeriod}",
                        title = "✅ KREDİ BORCU KAPANDI: ${loan.type.title}",
                        text = "${loan.type.title} kapsamındaki $${"%,d".format(loan.principalAmount)} tutarındaki banka kredisi borcu başarıyla sıfırlandı. Şirket kredi notunuz yükseldi (+20 Puan)!",
                        category = "Şirket",
                        year = newYear,
                        month = newMonth
                    )
                )
            } else {
                processedLoans.add(
                    loan.copy(
                        remainingBalance = newRemainingBalance,
                        remainingPeriods = newRemainingPeriods
                    )
                )
            }
        }
        newNewsList.addAll(closedLoansNews)

        val updatedCreditScore = (currentState.creditScore + creditScoreGain).coerceIn(300, 900)
        val updatedPatentCooldown = (currentState.patentLiquidationCooldown - 1).coerceAtLeast(0)
        val finalCalculatedBudget = currentState.budget + netIncome - autoStartResearchCost + expoPrizeTotal - totalRecallCost

        // Depo Şişmesi (Inventory Buildup) & Stok Tükenme Uyarıları
        for (m in updatedActiveModels) {
            if (!m.isCompleted && m.remainingStock > 25000 && m.monthsOnMarket >= 6 && m.discountPercent == 0) {
                newNewsList.add(
                    0,
                    NewsArticle(
                        id = "stock_buildup_${m.id}_${newYear}_${newMonth}_${newPeriod}",
                        title = "📦 LOJİSTİK UYARISI: ${m.specs.name} Depolarda Şişti!",
                        text = "${m.specs.name} modelinin deposunda ${"%,d".format(m.remainingStock)} adet satılmamış stok birikti. Depolama maliyetlerini azaltmak ve sermayeyi döndürmek için İndirim Kampanyası uygulayabilir veya kalan stokları geri dönüştürebilirsiniz.",
                        category = "Lojistik",
                        year = newYear,
                        month = newMonth
                    )
                )
            } else if (!m.isCompleted && m.remainingStock in 1..2000 && m.monthsOnMarket <= 8) {
                newNewsList.add(
                    0,
                    NewsArticle(
                        id = "stock_low_${m.id}_${newYear}_${newMonth}_${newPeriod}",
                        title = "⚡ STOK TÜKENİYOR: ${m.specs.name} Rafları Boşalıyor!",
                        text = "${m.specs.name} modelinden piyasada yalnızca ${"%,d".format(m.remainingStock)} adet kaldı! Satış ivmesini kaybetmemek için Cihazlar sekmesinden tekrar üretim siparişi verebilirsiniz.",
                        category = "Lojistik",
                        year = newYear,
                        month = newMonth
                    )
                )
            }
        }

        if (finalCalculatedBudget < 0) {
            newNewsList.add(
                0,
                NewsArticle(
                    id = "bankruptcy_warn_${newYear}_${newMonth}_${newPeriod}",
                    title = "🚨 FİNANSAL KRİZ: Şirket Kasası Negatife Düştü!",
                    text = "Bütçeniz -$${"%,d".format(kotlin.math.abs(finalCalculatedBudget))} seviyesine geriledi! İflas masasına sürüklenmemek için Finans Merkezinden Banka Kredisi çekin, patent devredin veya acil melek yatırımcı fonu sağlayın.",
                    category = "Şirket",
                    year = newYear,
                    month = newMonth
                )
            )
        }

        val updatedAcquisitionTargets = updateAcquisitions(currentState)

        _state.update {
            it.copy(
                period = newPeriod,
                month = newMonth,
                year = newYear,
                budget = finalCalculatedBudget,
                reputation = (it.reputation + expoRepGain - totalRecallReputationPenalty.coerceAtMost(20) + passiveReputationDelta).coerceIn(0, 100),
                reputationMomentum = updatedReputationMomentum,
                monthlyIncome = totalCombinedRevenue * 2, // Equivalent monthly rate
                customOs = updatedCustomOs,
                customChipsets = updatedChipsets,
                totalChipsetOemRevenue = it.totalChipsetOemRevenue + periodChipsetIncome,
                lastPeriodChipsetOemRevenue = periodChipsetIncome,
                lastPeriodChipsetsSold = periodChipsetsSold,
                activeModels = updatedActiveModels,
                unlockedTech = updatedUnlockedTech,
                activeResearch = currentActiveResearch,
                researchQueue = updatedResearchQueue,
                acquisitionTargets = updatedAcquisitionTargets,
                reports = it.reports + researchReports + osReports + report,
                newsList = newNewsList,
                currentTrend = updatedTrend,
                competitors = updatedCompetitors,
                competitorReleases = updatedCompetitorReleases,
                playerMarketSharePercent = playerSharePercent,
                totalMarketMonthlyVolume = totalMarketVolume * 2,
                activeTechExpo = triggeredExpoEvent,
                pastTechExpos = if (triggeredExpoEvent != null) listOf(triggeredExpoEvent) + it.pastTechExpos else it.pastTechExpos,
                activeSupplyChainEvent = nextSupplyEvent,
                activeLoans = processedLoans,
                creditScore = updatedCreditScore,
                patentLiquidationCooldown = updatedPatentCooldown,
                patents = updatedPatents,
                totalPatentLicenseRevenue = it.totalPatentLicenseRevenue + patentLicenseRevenueThisPeriod,
                lastPeriodPatentLicenseRevenue = patentLicenseRevenueThisPeriod,
                activeHardwareCrises = it.activeHardwareCrises + newCrisesList,
                techLevel = if (updatedUnlockedTech.size >= 15) "Yapay Zeka" else if (updatedUnlockedTech.size >= 5) "İleri Düzey" else "Giriş"
            )
        }

        // --- BAŞARIMLAR (ACHIEVEMENTS) KONTROLÜ ---
        val stateAfterPeriod = _state.value
        val newlyUnlocked = evaluateNewlyUnlockedAchievements(stateAfterPeriod)
        if (newlyUnlocked.isNotEmpty()) {
            val achievementBonusRep = newlyUnlocked.sumOf {
                when (it.tier) {
                    AchievementTier.BRONZE -> 1
                    AchievementTier.SILVER -> 2
                    AchievementTier.GOLD -> 4
                    AchievementTier.PLATINUM -> 8
                }
            }.coerceAtMost(6) // Aynı periyotta birden çok başarım birden açılırsa itibarın aniden sıçramasını önler
            val achievementNews = newlyUnlocked.map { ach ->
                NewsArticle(
                    id = "achv_${ach.id}_${newYear}_${newMonth}",
                    title = "🏅 BAŞARIM AÇILDI: ${ach.title}",
                    text = "${ach.icon} ${ach.description} (${ach.tier.displayName} Rozet)",
                    category = "Şirket",
                    year = newYear,
                    month = newMonth
                )
            }
            _state.update {
                it.copy(
                    unlockedAchievementIds = it.unlockedAchievementIds + newlyUnlocked.map { a -> a.id },
                    lastUnlockedAchievementIds = newlyUnlocked.map { a -> a.id },
                    reputation = (it.reputation + achievementBonusRep).coerceIn(0, 100),
                    newsList = it.newsList + achievementNews
                )
            }
        } else {
            _state.update { it.copy(lastUnlockedAchievementIds = emptyList()) }
        }

        autoSaveGame()

        // Periodically fetch dynamic AI news (every 6 periods = 3 months)
        if (newMonth % 3 == 0 && newPeriod == 1) {
            viewModelScope.launch {
                val trendTitle = updatedTrend?.title ?: "Yeni Nesil Akıllı Telefonlar"
                val compNames = updatedCompetitors.map { it.name }
                val aiNews = AiGameService.generateDynamicNews(newYear, newMonth, trendTitle, compNames)
                if (aiNews != null) {
                    val (title, text) = aiNews
                    val dynamicArticle = NewsArticle(
                        id = "ai_news_${newYear}_${newMonth}_${Random.nextInt(100, 999)}",
                        title = title,
                        text = text,
                        category = "Teknoloji",
                        year = newYear,
                        month = newMonth,
                        isAiGenerated = true
                    )
                    _state.update { current ->
                        current.copy(newsList = listOf(dynamicArticle) + current.newsList)
                    }
                    autoSaveGame()
                }
            }
        }
    }

    fun hireEmployee(type: EmployeeType, count: Int = 1) {
        val currentState = _state.value
        val newTotalEmployees = currentState.totalEmployees + count

        if (newTotalEmployees > currentState.maxEmployees) {
            _state.update { it.copy(noticeMessage = "Ofis kapasitesi yetersiz! (${currentState.totalEmployees}/${currentState.maxEmployees}) Ofisinizi yükseltin.") }
            return
        }

        if (type == EmployeeType.ASSEMBLY_WORKER) {
            val newWorkerCount = currentState.assemblyWorkers + count
            if (newWorkerCount > currentState.currentFactoryTier.maxWorkers) {
                _state.update { it.copy(noticeMessage = "Fabrika işçi kapasitesi yetersiz! (${currentState.assemblyWorkers}/${currentState.currentFactoryTier.maxWorkers}) Daha fazla montaj işçisi için fabrikanızı yükseltin.") }
                return
            }
        }

        val salaryCost = when (type) {
            EmployeeType.ENGINEER -> 8000L * count
            EmployeeType.QA_INSPECTOR -> 5000L * count
            EmployeeType.ASSEMBLY_WORKER -> 3000L * count
        }

        if (currentState.budget < salaryCost) {
            _state.update { it.copy(noticeMessage = "İşe alım ve ilk maaş teminatı için yetersiz bütçe! ($${"%,d".format(salaryCost)} gerekli)") }
            return
        }

        val typeName = when(type) {
            EmployeeType.ENGINEER -> "Ar-Ge Mühendisi"
            EmployeeType.QA_INSPECTOR -> "QA Test Uzmanı"
            EmployeeType.ASSEMBLY_WORKER -> "Üretim Hattı İşçisi"
        }

        _state.update { current ->
            when (type) {
                EmployeeType.ENGINEER -> current.copy(
                    engineers = current.engineers + count,
                    noticeMessage = "$count adet $typeName işe alındı."
                )
                EmployeeType.QA_INSPECTOR -> current.copy(
                    qaInspectors = current.qaInspectors + count,
                    noticeMessage = "$count adet $typeName işe alındı."
                )
                EmployeeType.ASSEMBLY_WORKER -> current.copy(
                    assemblyWorkers = current.assemblyWorkers + count,
                    noticeMessage = "$count adet $typeName işe alındı."
                )
            }
        }
        autoSaveGame()
    }

    fun fireEmployee(type: EmployeeType, count: Int = 1) {
        val currentState = _state.value
        val currentCount = when (type) {
            EmployeeType.ENGINEER -> currentState.engineers
            EmployeeType.QA_INSPECTOR -> currentState.qaInspectors
            EmployeeType.ASSEMBLY_WORKER -> currentState.assemblyWorkers
        }

        if (currentCount < count) {
            return
        }

        val typeName = when(type) {
            EmployeeType.ENGINEER -> "Ar-Ge Mühendisi"
            EmployeeType.QA_INSPECTOR -> "QA Test Uzmanı"
            EmployeeType.ASSEMBLY_WORKER -> "Üretim Hattı İşçisi"
        }

        _state.update { current ->
            when (type) {
                EmployeeType.ENGINEER -> {
                    val newEng = current.engineers - count
                    val updatedCustomOs = if (current.customOs.assignedDevs > newEng) {
                        current.customOs.copy(assignedDevs = newEng)
                    } else current.customOs
                    current.copy(
                        engineers = newEng,
                        customOs = updatedCustomOs,
                        noticeMessage = "$count adet $typeName ile yollar ayrıldı."
                    )
                }
                EmployeeType.QA_INSPECTOR -> current.copy(
                    qaInspectors = current.qaInspectors - count,
                    noticeMessage = "$count adet $typeName ile yollar ayrıldı."
                )
                EmployeeType.ASSEMBLY_WORKER -> current.copy(
                    assemblyWorkers = current.assemblyWorkers - count,
                    noticeMessage = "$count adet $typeName ile yollar ayrıldı."
                )
            }
        }
        autoSaveGame()
    }

    fun upgradeOffice() {
        val currentState = _state.value
        val nextTier = OFFICE_TIERS.firstOrNull { it.level == currentState.officeLevel + 1 } ?: return

        if (currentState.budget < nextTier.upgradeCost) {
            _state.update { it.copy(noticeMessage = "Ofis yükseltmesi için yetersiz bütçe! Gereken: $${"%,d".format(nextTier.upgradeCost)}") }
            return
        }

        val report = MarketReport(
            title = "Ofis Yükseltildi: ${nextTier.name}",
            text = "Şirket merkezimiz yeni binaya taşındı ($${"%,d".format(nextTier.upgradeCost)}). Yeni çalışan kapasitesi: ${nextTier.maxEmployees} kişi.",
            profit = -nextTier.upgradeCost,
            unitsSold = 0,
            reviewScore = 0
        )

        val news = NewsArticle(
            id = "office_up_${currentState.year}_${currentState.month}_${Random.nextInt(100, 999)}",
            title = "ŞİRKET MERKEZİ GENİŞLİYOR: ${nextTier.name}",
            text = "${currentState.companyName}, büyüme hedefleri doğrultusunda ${nextTier.name} seviyesine taşındı. Kapasite ${nextTier.maxEmployees} çalışana çıkarıldı.",
            category = "Şirket",
            year = currentState.year,
            month = currentState.month
        )

        _state.update {
            it.copy(
                budget = it.budget - nextTier.upgradeCost,
                officeLevel = nextTier.level,
                reputation = (it.reputation + 2).coerceIn(0, 100),
                reports = it.reports + report,
                newsList = it.newsList + news,
                noticeMessage = "Tebrikler! ${nextTier.name} genel merkezine taşındınız."
            )
        }
        autoSaveGame()
    }

    fun upgradeFactory() {
        val currentState = _state.value
        val nextTier = FACTORY_TIERS.firstOrNull { it.level == currentState.factoryLevel + 1 } ?: return

        if (currentState.budget < nextTier.upgradeCost) {
            _state.update { it.copy(noticeMessage = "Fabrika yükseltmesi için yetersiz bütçe! Gereken: $${"%,d".format(nextTier.upgradeCost)}") }
            return
        }

        val report = MarketReport(
            title = "Fabrika Yükseltildi: ${nextTier.name}",
            text = "Üretim tesislerimiz ${nextTier.name} seviyesine modernize edildi ($${"%,d".format(nextTier.upgradeCost)}). Üretim maliyet indirimi: %${nextTier.discountPercent}.",
            profit = -nextTier.upgradeCost,
            unitsSold = 0,
            reviewScore = 0
        )

        val news = NewsArticle(
            id = "factory_up_${currentState.year}_${currentState.month}_${Random.nextInt(100, 999)}",
            title = "ÜRETİM TESİSİ YENİLENDİ: ${nextTier.name}",
            text = "${currentState.companyName}, üretim hatlarını ${nextTier.name} standardına yükseltti. Cihaz başına üretim maliyeti %${nextTier.discountPercent} düşecek.",
            category = "Şirket",
            year = currentState.year,
            month = currentState.month
        )

        _state.update {
            it.copy(
                budget = it.budget - nextTier.upgradeCost,
                factoryLevel = nextTier.level,
                reputation = (it.reputation + 3).coerceIn(0, 100),
                reports = it.reports + report,
                newsList = it.newsList + news,
                noticeMessage = "Tebrikler! Üretim tesisiniz ${nextTier.name} seviyesine yükseltildi."
            )
        }
        autoSaveGame()
    }

    fun clearNoticeMessage() {
        _state.update { it.copy(noticeMessage = null) }
    }

    fun setCompanyProfile(name: String, logoId: String, logoStyle: String, brandColorHex: Long, slogan: String) {
        _state.update {
            it.copy(
                companyName = name.ifBlank { "Apex Mobile" },
                companyLogoId = logoId,
                companyLogoStyle = logoStyle,
                companyBrandColorHex = brandColorHex,
                companySlogan = slogan.ifBlank { "Geleceğin Akıllı Telefonları" },
                isCompanySetupDone = true
            )
        }
        autoSaveGame()
    }

    fun manufacturePhone(specs: PhoneSpecs) {
        val currentState = _state.value
        val productionCost = calculateProductionCost(specs)
        val launchMarketingCost = (specs.launchCampaign.cost * currentState.scaleMultiplier).toLong()
        val totalCost = (productionCost.toLong() * specs.quantity) + specs.qaBudget + launchMarketingCost
        
        if (currentState.budget < totalCost) {
            return
        }

        // Technological Obsolescence Calculation
        val currentYear = currentState.year
        var techPenalty = 0

        if (currentYear >= 2012 && specs.processor.contains("S4")) techPenalty += 10
        if (currentYear >= 2014 && specs.processor.contains("801")) techPenalty += 10
        if (currentYear >= 2016 && specs.processor.contains("820")) techPenalty += 10
        if (currentYear >= 2018 && specs.processor.contains("845")) techPenalty += 10
        if (currentYear >= 2020 && specs.processor.contains("865")) techPenalty += 10

        if (currentYear >= 2013 && specs.displayResolution.contains("WVGA", ignoreCase = true)) techPenalty += 10
        if (currentYear >= 2016 && specs.displayResolution.contains("HD", ignoreCase = true) && !specs.displayResolution.contains("FHD", ignoreCase = true) && !specs.displayResolution.contains("QHD", ignoreCase = true)) techPenalty += 8
        if (currentYear >= 2020 && (specs.displayBrightness.filter { it.isDigit() }.toIntOrNull() ?: 350) < 600) techPenalty += 5
        if (currentYear >= 2023 && (specs.displayBrightness.filter { it.isDigit() }.toIntOrNull() ?: 350) < 800) techPenalty += 5
        if (currentYear >= 2019 && !specs.display.contains("Çerçevesiz") && !specs.display.contains("120Hz") && !specs.display.contains("Katlanabilir") && !specs.display.contains("144Hz") && !specs.display.contains("240Hz")) techPenalty += 12

        if (currentYear >= 2014 && specs.camera.contains("5 MP")) techPenalty += 15
        if (currentYear >= 2016 && specs.camera.contains("8-13")) techPenalty += 10
        if (currentYear >= 2019 && !specs.camera.contains("Üçlü") && !specs.camera.contains("Periskop") && !specs.camera.contains("108MP") && !specs.camera.contains("200MP") && !specs.camera.contains("GenAI") && !specs.camera.contains("Donanımsal ISP")) techPenalty += 8

        // Mitigate penalty with engineers
        val finalTechPenalty = (techPenalty - currentState.engineerTechBonus).coerceAtLeast(0)

        // Quality and Review Score calculation
        val baseQuality = (100 - finalTechPenalty).coerceIn(40, 95)
        val qaPerUnit = if (specs.quantity > 0) specs.qaBudget.toFloat() / specs.quantity else 0f
        val qaFactor = (qaPerUnit * 0.35f).toInt().coerceIn(0, 15)
        val zeroQaPenalty = if (specs.qaBudget == 0L) -8 else 0

        // Design & Aesthetics Review Score Bonus
        val designBonus = when {
            specs.material == "Titanyum" || specs.backFinish == "Vegan Deri" -> 5
            specs.material == "Cam" || specs.backFinish == "Karbon Fiber" -> 3
            specs.material == "Alüminyum" || specs.backFinish == "Buzlu Mat Cam" -> 2
            else -> 0
        } + when {
            specs.frameStyle == "Ultra İnce Çerçeve" || specs.frameStyle == "Zırhlı Kesim" -> 3
            specs.notchStyle == "Dinamik Ada / Hap" || specs.notchStyle == "Görünmez Ekran Altı" -> 3
            else -> 0
        }

        // OS & Software Review Score Bonus
        val osBonus = when {
            specs.osType.contains("Bağımsız") -> 10
            specs.osType.contains("Özel") -> 6
            else -> 0
        } + when {
            specs.osFocus.contains("Yapay Zeka") -> 4
            specs.osFocus.contains("Güvenlik") -> 3
            specs.osFocus.contains("Oyun") && specs.style == "Oyuncu" -> 5
            specs.osFocus.contains("Hafiflik") -> 3
            specs.osFocus.contains("Estetik") -> 3
            else -> 0
        }

        var legacyBonus = 0
        val legacySeriesList = currentState.ownedLegacySeries.toMutableList()
        val legacyIndex = legacySeriesList.indexOfFirst { it.seriesName == specs.seriesName }
        var legacyReputationText = ""
        
        if (legacyIndex != -1) {
            val legacy = legacySeriesList[legacyIndex]
            legacyBonus = ((legacy.seriesReputation - 50) / 10).coerceIn(-5, 8)
            legacyReputationText = " (Miras Kalan Seri: ${if (legacyBonus > 0) "+$legacyBonus" else "$legacyBonus"} Etki)"
        }

        val reviewScore = (baseQuality + qaFactor + zeroQaPenalty + currentState.qaScoreBonus + designBonus + osBonus + specs.tier.reviewBonus + legacyBonus + Random.nextInt(-3, 4)).coerceIn(10, 100)

        if (legacyIndex != -1) {
            val legacy = legacySeriesList[legacyIndex]
            val newRep = (legacy.seriesReputation + if (reviewScore > 80) 5 else if (reviewScore < 60) -8 else 1).coerceIn(0, 100)
            val newAvg = ((legacy.averageReviewScore * legacy.totalModelsReleased) + reviewScore) / (legacy.totalModelsReleased + 1)
            
            legacySeriesList[legacyIndex] = legacy.copy(
                totalModelsReleased = legacy.totalModelsReleased + 1,
                averageReviewScore = newAvg,
                seriesReputation = newRep
            )
        }

        // Geri Çağırma (Recall) Riski
        val recallRiskPercent = calculateRecallRisk(qaPerUnit = qaPerUnit, reviewScore = reviewScore)

        val techComment = when {
            finalTechPenalty == 0 -> "Teknolojisi, tasarımı ve yazılımı çağın ötesinde!"
            finalTechPenalty < 10 -> "Donanımı ve yazılımı günümüz standartlarına uygun."
            finalTechPenalty < 25 -> "Biraz geride kalmış bir teknoloji."
            else -> "Teknolojisi maalesef çok eski."
        }

        val isTrendMatched = checkTrendMatch(specs, currentState.currentTrend)
        val trendBonusPct = currentState.currentTrend.bonusPercent
        val trendBonusNote = if (isTrendMatched) " 🔥 Aktif Pazar Trendi (${currentState.currentTrend.title}) yakalandı! +%$trendBonusPct Satış Bonusu devrede." else ""
        val colorNote = if (specs.selectedColors.size > 1) " 🎨 ${specs.selectedColors.size} Lansman Rengi sunuldu." else ""
        val osNote = if (specs.osType != "Saf Açık Kaynak") " 🌐 ${specs.osName} (${specs.osFocus}) yüklü." else ""
        val tierNote = if (specs.tier != ModelTier.STANDARD) " ${specs.tier.badge} ${specs.tier.title} Segmenti." else ""
        val genNote = if (specs.generation > 1) " 🔄 ${specs.generation}. Nesil Seri Devamı." else ""

        val finalSpecs = specs.copy(matchesTrend = isTrendMatched, unitCost = productionCost)

        val computedBenchmark = BenchmarkCalculator.calculateScore(
            specs = finalSpecs,
            osOptimization = currentState.customOs.overallTechScore
        )

        val factoryPeriodCapacity = currentState.currentFactoryTier.periodCapacity
        val initialProducedBatch = specs.quantity.coerceAtMost(factoryPeriodCapacity)

        val baseHype = specs.launchCampaign.initialHype
        val repHypeBonus = when {
            currentState.reputation >= 80 -> 18
            currentState.reputation >= 60 -> 10
            currentState.reputation >= 40 -> 5
            else -> 0
        }
        val trendHype = if (isTrendMatched) 10 else 0
        val initialHype = (baseHype + repHypeBonus + trendHype).coerceIn(5, 140)

        val estimatedUnitCost = productionCost.coerceAtLeast(30)
        val expectedQuality = (initialHype * 0.65f + (specs.price / estimatedUnitCost.toFloat() * 1.5f).coerceIn(10f, 40f))
        val actualQuality = reviewScore.toFloat() + (specs.qaBudget.toFloat() / specs.quantity.coerceAtLeast(1) * 0.25f).coerceAtMost(10f)

        val initialSatisfaction = when {
            initialHype >= 55 && reviewScore < 60 -> {
                val gap = initialHype - reviewScore
                (40 - (gap * 0.8f) - (if (specs.qaBudget == 0L) 10 else 0)).toInt().coerceIn(5, 40)
            }
            reviewScore >= 85 && initialHype <= 35 -> (90 + (reviewScore - 85)).coerceIn(85, 100)
            else -> (75f + (actualQuality - expectedQuality) * 0.9f).toInt().coerceIn(10, 100)
        }

        val newActiveModel = ActiveModel(
            id = "${specs.name}_${currentState.year}_${currentState.month}_${Random.nextInt(1000, 9999)}",
            specs = finalSpecs,
            totalStock = specs.quantity,
            remainingStock = initialProducedBatch,
            producedStock = initialProducedBatch,
            totalSold = 0,
            totalRevenue = 0,
            monthsOnMarket = 0,
            reviewScore = reviewScore,
            launchYear = currentState.year,
            launchMonth = currentState.month,
            matchesTrend = isTrendMatched,
            benchmarkScore = computedBenchmark,
            recallRiskPercent = recallRiskPercent,
            hypeScore = initialHype,
            customerSatisfactionScore = initialSatisfaction
        )

        val productionNote = if (initialProducedBatch < specs.quantity) {
            " 🏭 Fabrika kapasitesi nedeniyle ilk seride ${"%,d".format(initialProducedBatch)} adet üretildi; kalan ${"%,d".format(specs.quantity - initialProducedBatch)} adet önümüzdeki periyotlarda kademeli olarak üretilecek."
        } else ""

        val launchNews = NewsArticle(
            id = "news_launch_${newActiveModel.id}",
            title = "${currentState.companyName.uppercase()} LANSMANI: ${specs.name} Piyasada!",
            text = "${currentState.companyName}, ${specs.name} modelini üretti! ${"%,d".format(specs.quantity)} adetlik stok hedeflendi. Başarılı modeller fiyat indirimi ve uzun-kuyruk talebiyle 48 aya kadar piyasada kalabilir.$productionNote $techComment$colorNote$osNote$trendBonusNote$tierNote$genNote Eleştirmen puanı: $reviewScore/100.$legacyReputationText",
            category = "Şirket",
            year = currentState.year,
            month = currentState.month
        )

        val report = MarketReport(
            title = "${specs.name} Üretimi Başladı",
            text = "${specs.name} modelinin ${"%,d".format(specs.quantity)} adetlik stok üretimi tamamlandı ($${"%,d".format(totalCost)} harcandı). Eleştirmenler $reviewScore/100 verdi.$colorNote$osNote$trendBonusNote$tierNote$genNote$legacyReputationText",
            profit = -totalCost,
            unitsSold = 0,
            reviewScore = reviewScore
        )

        _state.update { 
            it.copy(
                budget = it.budget - totalCost,
                reputation = (it.reputation + ((reviewScore - 70) / 10).coerceIn(0, 3)).coerceIn(0, 100),
                modelCount = it.modelCount + 1,
                manufacturedPhones = it.manufacturedPhones + specs,
                activeModels = it.activeModels + newActiveModel,
                ownedLegacySeries = legacySeriesList,
                reports = it.reports + report,
                newsList = it.newsList + launchNews
            )
        }

        autoSaveGame()

        // Generate dynamic AI Critic Review asynchronously
        viewModelScope.launch {
            val (aiQuote, isAi) = AiGameService.generatePhoneReview(
                specs = finalSpecs,
                companyName = currentState.companyName,
                year = currentState.year,
                reviewScore = reviewScore,
                trend = currentState.currentTrend
            )

            _state.update { current ->
                val updatedReports = current.reports.map { rep ->
                    if (rep.title == "${specs.name} Üretimi Başladı") {
                        rep.copy(aiReviewQuote = aiQuote, isAiGenerated = isAi)
                    } else rep
                }
                val updatedNews = current.newsList.map { news ->
                    if (news.id == "news_launch_${newActiveModel.id}") {
                        news.copy(reviewerQuote = aiQuote, isAiGenerated = isAi)
                    } else news
                }
                current.copy(reports = updatedReports, newsList = updatedNews)
            }
            autoSaveGame()
        }
    }

    fun calculateProductionCost(specs: PhoneSpecs): Int {
        val baseCost = com.example.viewmodel.calculateProductionCost(
            specs = specs,
            customChipsets = _state.value.customChipsets,
            unitCostDiscountPercent = _state.value.unitCostDiscountPercent
        )
        val supplyMultiplier = (_state.value.activeSupplyChainEvent?.costMultiplierPercent ?: 100) / 100f
        return (baseCost * supplyMultiplier).toInt().coerceAtLeast(1)
    }

    fun remanufactureModel(modelId: String, additionalQuantity: Int) {
        val currentState = _state.value
        val model = currentState.activeModels.find { it.id == modelId } ?: return
        if (model.isRecalled) return // Geri çağrılmış bir ürün yeniden üretilemez

        val unitCost = calculateProductionCost(model.specs)
        val totalCost = unitCost.toLong() * additionalQuantity

        if (currentState.budget < totalCost) {
            return
        }

        val updatedModels = currentState.activeModels.map {
            if (it.id == modelId) {
                // If model completed its time or was near completion, extend market presence
                val newMonthsOnMarket = if (it.monthsOnMarket >= it.maxMonthsOnMarket) {
                    (it.maxMonthsOnMarket - 6).coerceAtLeast(0)
                } else {
                    it.monthsOnMarket
                }

                it.copy(
                    totalStock = it.totalStock + additionalQuantity,
                    monthsOnMarket = newMonthsOnMarket
                    // remainingStock ve producedStock kasıtlı olarak değişmiyor: ek sipariş
                    // fabrika kapasitesine göre önümüzdeki periyotlarda kademeli üretilecek.
                )
            } else {
                it
            }
        }

        val restockNews = NewsArticle(
            id = "news_restock_${modelId}_${currentState.year}_${currentState.month}_${Random.nextInt(100, 999)}",
            title = "TEKRAR ÜRETİM SİPARİŞİ: ${model.specs.name}",
            text = "Yoğun talep üzerine ${model.specs.name} modeli için ${"%,d".format(additionalQuantity)} adet yeni üretim siparişi verildi. Fabrika kapasitesine göre önümüzdeki periyotlarda kademeli olarak üretilip mağazalara dağıtılacak.",
            category = "Şirket",
            year = currentState.year,
            month = currentState.month
        )

        val report = MarketReport(
            title = "${model.specs.name} Stok Yenilendi",
            text = "${model.specs.name} modeli için $${"%,d".format(totalCost)} harcanarak ${"%,d".format(additionalQuantity)} adet ilave stok üretildi.",
            profit = -totalCost,
            unitsSold = 0,
            reviewScore = model.reviewScore
        )

        _state.update {
            it.copy(
                budget = it.budget - totalCost,
                activeModels = updatedModels,
                newsList = it.newsList + restockNews,
                reports = it.reports + report
            )
        }
    }

    fun recycleRemainingStock(modelId: String) {
        val currentState = _state.value
        val model = currentState.activeModels.find { it.id == modelId } ?: return
        if (model.remainingStock <= 0) return

        val unitCost = calculateProductionCost(model.specs)
        // Geri dönüşüm değeri: birim maliyetin %50'si (yarı fiyatı)
        val recyclePerUnit = (unitCost * 0.50f).toLong()
        val totalRefund = recyclePerUnit * model.remainingStock.toLong()
        val recycledQty = model.remainingStock

        val updatedModels = currentState.activeModels.map {
            if (it.id == modelId) {
                it.copy(
                    remainingStock = 0,
                    monthsOnMarket = it.maxMonthsOnMarket // Mark as completed
                )
            } else {
                it
            }
        }

        val recycleNews = NewsArticle(
            id = "news_recycle_${modelId}_${currentState.year}_${currentState.month}_${Random.nextInt(100, 999)}",
            title = "♻️ GERİ DÖNÜŞÜM & TASFİYE: ${model.specs.name}",
            text = "${model.specs.name} modelinin elde kalan ${"%,d".format(recycledQty)} adet stoku parça geri kazanımına gönderildi. Üretim maliyetinin yarısı (%50) karşılığında $${"%,d".format(totalRefund)} bütçeye iade edildi.",
            category = "Şirket",
            year = currentState.year,
            month = currentState.month
        )

        val report = MarketReport(
            title = "${model.specs.name} Stokları Geri Dönüştürüldü",
            text = "Elde kalan ${"%,d".format(recycledQty)} adet ${model.specs.name} cihazı birim başı $${recyclePerUnit} (%50 maliyet) üzerinden geri dönüştürüldü ve şirkete $${"%,d".format(totalRefund)} nakit girişi sağlandı.",
            profit = totalRefund,
            unitsSold = 0,
            reviewScore = model.reviewScore
        )

        _state.update {
            it.copy(
                budget = it.budget + totalRefund,
                activeModels = updatedModels,
                newsList = it.newsList + recycleNews,
                reports = it.reports + report,
                noticeMessage = "♻️ ${"%,d".format(recycledQty)} adet cihaz geri dönüştürüldü! $${"%,d".format(totalRefund)} bütçeye aktarıldı."
            )
        }
    }

    fun applyModelDiscount(modelId: String, discountPercent: Int) {
        val currentState = _state.value
        val model = currentState.activeModels.find { it.id == modelId } ?: return
        val clampedDiscount = discountPercent.coerceIn(0, 70)

        val updatedModels = currentState.activeModels.map {
            if (it.id == modelId) {
                it.copy(discountPercent = clampedDiscount)
            } else it
        }

        val actionText = if (clampedDiscount > 0) {
            "🏷️ '${model.specs.name}' için %$clampedDiscount İndirim Kampanyası başlatıldı! Yeni Satış Fiyatı: $${model.specs.price * (100 - clampedDiscount) / 100}"
        } else {
            "İndirim kaldırıldı. '${model.specs.name}' standart fiyattan ($${model.specs.price}) satılmaya devam ediyor."
        }

        val news = if (clampedDiscount > 0) {
            NewsArticle(
                id = "news_discount_${modelId}_${currentState.year}_${currentState.month}_${Random.nextInt(100, 999)}",
                title = "🔥 İNDİRİM FIRSATI: ${model.specs.name} %$clampedDiscount İndirimde!",
                text = "${currentState.companyName}, popüler modeli ${model.specs.name} için sınırlı süreliğine %$clampedDiscount indirim kampanyası başlattı. Yeni fiyat: $${model.specs.price * (100 - clampedDiscount) / 100}!",
                category = "Pazar",
                year = currentState.year,
                month = currentState.month
            )
        } else null

        _state.update {
            it.copy(
                activeModels = updatedModels,
                newsList = if (news != null) it.newsList + news else it.newsList,
                noticeMessage = actionText
            )
        }
        autoSaveGame()
    }

    fun calculateResearchDuration(engineers: Int, cost: Long = 0L): Int {
        val baseMonths = when {
            cost >= 80000000L -> 16 // 100M Mega OS Research
            cost >= 20000000L -> 10
            cost >= 5000000L -> 7
            else -> 5
        }
        val engineerBonus = when {
            engineers <= 0 -> 0
            engineers in 1..2 -> 1
            engineers in 3..5 -> 3
            engineers in 6..10 -> 5
            engineers in 11..20 -> 8
            else -> 11
        }
        return (baseMonths - engineerBonus).coerceAtLeast(1)
    }

    fun startResearch(techId: String, techName: String, cost: Long) {
        val currentState = _state.value
        if (currentState.budget >= cost && currentState.activeResearch == null && !currentState.unlockedTech.contains(techId)) {
            val duration = calculateResearchDuration(currentState.engineers, cost)
            val newResearch = ActiveResearch(
                techId = techId,
                techName = techName,
                totalMonths = duration,
                remainingMonths = duration,
                cost = cost
            )

            val report = MarketReport(
                title = "Ar-Ge Araştırması Başlatıldı: $techName",
                text = "$techName teknolojisinin araştırması başlatıldı ($${"%,d".format(cost)} harcandı). Mevcut ${currentState.engineers} Ar-Ge mühendisi ile projenin $duration dönem (~${"%.1f".format(duration / 2.0)} ay) sürmesi öngörülüyor.",
                profit = -cost,
                unitsSold = 0,
                reviewScore = 0
            )

            val techNews = NewsArticle(
                id = "news_tech_start_${currentState.year}_${currentState.month}_${Random.nextInt(100, 999)}",
                title = "YENİ AR-GE PROJESİ: $techName",
                text = "Ar-Ge ekibimiz $techName üzerinde çalışmaya başladı. Tahmini tamamlama süresi: $duration dönem (~${"%.1f".format(duration / 2.0)} ay).",
                category = "Teknoloji",
                year = currentState.year,
                month = currentState.month
            )

            _state.update { it.copy(
                budget = it.budget - cost,
                activeResearch = newResearch,
                reports = it.reports + report,
                newsList = it.newsList + techNews
            )}
        } else if (currentState.activeResearch != null && !currentState.unlockedTech.contains(techId) && currentState.researchQueue.none { it.techId == techId }) {
            // Add to queue
            queueResearch(techId, techName, cost)
        } else if (currentState.budget < cost && currentState.activeResearch == null) {
            _state.update { it.copy(noticeMessage = "Yetersiz bütçe! '$techName' için $${"%,d".format(cost)} gerekiyor.") }
        }
    }

    fun queueResearch(techId: String, techName: String, cost: Long) {
        val currentState = _state.value
        if (currentState.unlockedTech.contains(techId) || currentState.activeResearch?.techId == techId || currentState.researchQueue.any { it.techId == techId }) {
            return
        }

        if (currentState.activeResearch == null) {
            startResearch(techId, techName, cost)
            return
        }

        val duration = calculateResearchDuration(currentState.engineers, cost)
        val queueItem = ActiveResearch(
            techId = techId,
            techName = techName,
            totalMonths = duration,
            remainingMonths = duration,
            cost = cost
        )

        _state.update {
            it.copy(
                researchQueue = it.researchQueue + queueItem,
                noticeMessage = "📋 '$techName' araştırma sırasına eklendi (${it.researchQueue.size + 1}. sırada)."
            )
        }
    }

    fun cancelQueuedResearch(techId: String) {
        _state.update {
            it.copy(
                researchQueue = it.researchQueue.filter { item -> item.techId != techId },
                noticeMessage = "Araştırma sırasından çıkarıldı."
            )
        }
    }

    fun unlockTech(techName: String, cost: Long) {
        queueResearch(techName, techName, cost)
    }

    fun launchCampaign(modelId: String, campaignType: CampaignType) {
        val currentState = _state.value
        val dynamicCost = (campaignType.cost * currentState.scaleMultiplier).toLong()
        
        if (currentState.budget < dynamicCost) return

        val model = currentState.activeModels.find { it.id == modelId } ?: return

        val updatedModels = currentState.activeModels.map { m ->
            if (m.id == modelId) {
                m.copy(
                    activeCampaign = ActiveCampaign(campaignType, campaignType.durationMonths),
                    hypeScore = (m.hypeScore + campaignType.hypeBoost).coerceAtMost(140)
                )
            } else m
        }

        val news = NewsArticle(
            id = "campaign_${modelId}_${currentState.year}_${currentState.month}_${Random.nextInt(100, 999)}",
            title = "PAZARLAMA KAMPANYASI: ${model.specs.name}",
            text = "${model.specs.name} için ${campaignType.title} başlatıldı! (${campaignType.durationMonths} ay boyunca +%${campaignType.boostPercent} satış artışı)",
            category = "Pazar",
            year = currentState.year,
            month = currentState.month
        )

        val report = MarketReport(
            title = "${campaignType.title} Başlatıldı",
            text = "${model.specs.name} cihazı için $${"%,d".format(dynamicCost)} bütçe ayrılarak ${campaignType.title} başlatıldı.",
            profit = -dynamicCost,
            unitsSold = 0,
            reviewScore = model.reviewScore
        )

        _state.update {
            it.copy(
                budget = it.budget - dynamicCost,
                activeModels = updatedModels,
                newsList = it.newsList + news,
                reports = it.reports + report
            )
        }
    }


    fun createOrUpgradeOs(
        name: String, 
        type: OsType, 
        licenseType: OsLicenseType,
        focus: OsFocus, 
        themeColorHex: Long,
        perDeviceLicenseFee: Int = 0
    ) {
        val currentState = _state.value
        if (!currentState.unlockedTech.contains("Özel Mobil İşletim Sistemi Mimarisi")) {
            _state.update { 
                it.copy(noticeMessage = "⚠️ Kendi işletim sisteminizi geliştirmek için Ar-Ge bölümünden 'Özel Mobil İşletim Sistemi Mimarisi' ($100,000,000) projesini araştırmalısınız!") 
            }
            return
        }

        if (currentState.customOs.activeDevelopment != null) {
            _state.update { it.copy(noticeMessage = "Halihazırda devam eden bir OS geliştirme projeniz var.") }
            return
        }

        val devCost = type.devCost
        if (currentState.budget < devCost) {
            _state.update { it.copy(noticeMessage = "İşletim sistemi geliştirme için yetersiz bütçe! Gereken: $${"%,d".format(devCost)}") }
            return
        }

        val isMajorUpdate = (type == currentState.customOs.type && currentState.customOs.name == name)
        val initialVersion = if (isMajorUpdate) "${currentState.customOs.majorVersionCount + 1}.0" else "1.0"

        val effectiveDevs = if (currentState.customOs.assignedDevs > 0) currentState.customOs.assignedDevs else currentState.engineers.coerceIn(1, 20)
        val totalPeriods = calculateOsDevelopmentPeriods(type, effectiveDevs, currentState.engineers)

        val qaInvestment = (currentState.qaInspectors * 5000L).coerceAtMost(devCost)

        val activeDev = ActiveOsDevelopment(
            name = name,
            targetVersion = initialVersion,
            type = type,
            licenseType = licenseType,
            focus = focus,
            themeColorHex = themeColorHex,
            perDeviceLicenseFee = perDeviceLicenseFee,
            totalMonths = totalPeriods,
            remainingMonths = totalPeriods,
            cost = devCost,
            isMajorUpdate = isMajorUpdate,
            qaInvestment = qaInvestment
        )

        val report = MarketReport(
            title = "OS Geliştirmesi Başladı: $name v$initialVersion",
            text = "$name v$initialVersion ($type) için Ar-Ge başladı. Bütçe: $${"%,d".format(devCost)}. Tahmini Süre: $totalPeriods dönem (Mühendis atayarak süreyi kısaltabilirsiniz).",
            profit = -devCost,
            unitsSold = 0,
            reviewScore = 0
        )

        _state.update {
            it.copy(
                budget = it.budget - devCost,
                customOs = it.customOs.copy(
                    activeDevelopment = activeDev,
                    assignedDevs = if (it.customOs.assignedDevs == 0) effectiveDevs else it.customOs.assignedDevs
                ),
                reports = it.reports + report,
                noticeMessage = "OS Geliştirme Projesi başlatıldı! ($totalPeriods Dönem)"
            )
        }
    }

    fun setAssignedDevs(count: Int) {
        val maxEng = _state.value.engineers
        val validCount = count.coerceIn(0, maxEng)
        _state.update {
            val currentDev = it.customOs.activeDevelopment
            val updatedDev = if (currentDev != null) {
                val newTotal = calculateOsDevelopmentPeriods(currentDev.type, validCount, maxEng)
                val currentProg = (1f - (currentDev.remainingMonths.toFloat() / currentDev.totalMonths.coerceAtLeast(1).toFloat())).coerceIn(0f, 0.95f)
                val newRemaining = ((1f - currentProg) * newTotal).toInt().coerceIn(1, newTotal)
                currentDev.copy(totalMonths = newTotal, remainingMonths = newRemaining)
            } else null

            val speedNote = if (updatedDev != null) " (Kalan Süre: ${updatedDev.remainingMonths} Dönem)" else ""
            it.copy(
                customOs = it.customOs.copy(
                    assignedDevs = validCount,
                    activeDevelopment = updatedDev ?: it.customOs.activeDevelopment
                ),
                noticeMessage = "Yazılım ekibine $validCount geliştirici atandı$speedNote."
            )
        }
        autoSaveGame()
    }
    fun releaseOsHotfix() {
        val currentState = _state.value
        val customOs = currentState.customOs

        if (!customOs.isCustomActive || customOs.stability >= 95) {
            _state.update { it.copy(noticeMessage = "Yazılım zaten yeterince stabil, hotfix gerekmiyor.") }
            return
        }

        val hotfixCost = 500000L
        if (currentState.budget < hotfixCost) {
            _state.update { it.copy(noticeMessage = "Hotfix yayınlamak için $500,000 bütçe gerekiyor.") }
            return
        }

        val stabilityGain = (10 + currentState.qaInspectors / 10).coerceIn(10, 30)

        _state.update {
            it.copy(
                budget = it.budget - hotfixCost,
                customOs = customOs.copy(stability = (customOs.stability + stabilityGain).coerceAtMost(100)),
                noticeMessage = "🛠️ Hotfix yayınlandı! Stabilite +$stabilityGain arttı."
            )
        }
        autoSaveGame()
    }



    fun setOsLicenseType(licenseType: OsLicenseType) {
        _state.update {
            it.copy(
                customOs = it.customOs.copy(licenseType = licenseType),
                noticeMessage = "İşletim sistemi lisans modeli '${licenseType.title}' olarak değiştirildi."
            )
        }
    }

    fun setPerDeviceLicenseFee(fee: Int) {
        _state.update {
            it.copy(
                customOs = it.customOs.copy(perDeviceLicenseFee = fee.coerceIn(0, 50)),
                noticeMessage = "Cihaz başı OEM lisans ücreti $$fee olarak belirlendi."
            )
        }
    }

    fun releaseMajorOsUpdate(selectedDeviceIds: List<String> = emptyList()) {
        val currentState = _state.value
        val baseUpdateCost = 150000L
        val perDeviceCost = 25000L
        val totalUpdateCost = baseUpdateCost + (selectedDeviceIds.size * perDeviceCost)
        
        if (currentState.budget < totalUpdateCost) {
            _state.update { it.copy(noticeMessage = "Güncelleme için yetersiz bütçe! Gereken: $${"%,d".format(totalUpdateCost)}") }
            return
        }

        val nextMajor = currentState.customOs.majorVersionCount + 1
        val newVersion = "$nextMajor.0"

        val updatedModels = currentState.activeModels.map { model ->
            if (selectedDeviceIds.contains(model.id)) {
                model.copy(
                    reviewScore = (model.reviewScore + 5).coerceAtMost(100),
                    lastProductQualityScore = model.lastProductQualityScore + 0.15f
                )
            } else model
        }

        val deviceNamesText = if (selectedDeviceIds.isNotEmpty()) {
            val names = currentState.activeModels.filter { selectedDeviceIds.contains(it.id) }.map { it.specs.name }.joinToString(", ")
            " Güncelleme şu cihazlara sunuldu: $names."
        } else {
            " Sadece yeni üretilecek cihazlar için yayınlandı."
        }

        val news = NewsArticle(
            id = "os_update_${currentState.year}_${currentState.month}_${Random.nextInt(100, 999)}",
            title = "🚀 GÜNCELLEME: ${currentState.customOs.name} v$newVersion",
            text = "Kullanıcıların merakla beklediği ${currentState.customOs.name} v$newVersion büyük sistem güncellemesi yayınlandı.$deviceNamesText",
            category = "Teknoloji",
            year = currentState.year,
            month = currentState.month
        )

        val report = MarketReport(
            title = "${currentState.customOs.name} v$newVersion Güncellemesi",
            text = "${currentState.customOs.name} v$newVersion yayınlandı ($${"%,d".format(totalUpdateCost)} harcandı). ${selectedDeviceIds.size} modele OTA ile dağıtıldı. İtibar ve kalite arttı.",
            profit = -totalUpdateCost,
            unitsSold = 0,
            reviewScore = 90
        )

        val updatedOs = currentState.customOs.copy(
            version = newVersion,
            majorVersionCount = nextMajor,
            minorVersionCount = 0,
            lastUpdateMonth = currentState.month,
            lastUpdateYear = currentState.year
        )

        val reputationBoost = 2 + selectedDeviceIds.size

        _state.update {
            it.copy(
                budget = it.budget - totalUpdateCost,
                reputation = (it.reputation + reputationBoost).coerceIn(0, 100),
                customOs = updatedOs,
                activeModels = updatedModels,
                newsList = it.newsList + news,
                reports = it.reports + report,
                noticeMessage = "${currentState.customOs.name} v$newVersion başarıyla yayınlandı!"
            )
        }
    }

    fun setUpdateGuarantee(guarantee: UpdateGuarantee) {
        val currentState = _state.value
        val oldGuarantee = currentState.customOs.updateGuarantee
        val repDiff = guarantee.reputationBonus - oldGuarantee.reputationBonus

        val updatedOs = currentState.customOs.copy(updateGuarantee = guarantee)
        _state.update {
            it.copy(
                customOs = updatedOs,
                reputation = (it.reputation + repDiff).coerceIn(0, 100),
                noticeMessage = "Yazılım güncelleme taahhüdü '${guarantee.title}' olarak güncellendi."
            )
        }
    }

    fun setCommissionRate(rate: StoreCommissionRate) {
        val updatedOs = _state.value.customOs.copy(commissionRate = rate)
        _state.update {
            it.copy(
                customOs = updatedOs,
                noticeMessage = "App Store komisyon politikası '${rate.label}' olarak ayarlandı."
            )
        }
    }

    fun upgradeOsModule(module: OsModuleType) {
        val currentState = _state.value
        val customOs = currentState.customOs
        val currentLevel = when (module) {
            OsModuleType.KERNEL_ENGINE -> customOs.kernelLevel
            OsModuleType.AI_NEURAL -> customOs.aiLevel
            OsModuleType.SECURITY_VAULT -> customOs.securityLevel
            OsModuleType.CLOUD_SYNC -> customOs.cloudLevel
            OsModuleType.APP_STORE_SDK -> customOs.appStoreLevel
        }

        if (currentLevel >= module.maxLevel) {
            _state.update { it.copy(noticeMessage = "${module.title} zaten maksimum Seviye ${module.maxLevel} seviyesinde!") }
            return
        }

        val upgradeCost = module.baseCost * (currentLevel)
        if (currentState.budget < upgradeCost) {
            _state.update { it.copy(noticeMessage = "${module.title} yükseltmesi için yetersiz bütçe! Gereken: $${"%,d".format(upgradeCost)}") }
            return
        }

        val nextLevel = currentLevel + 1
        val updatedOs = when (module) {
            OsModuleType.KERNEL_ENGINE -> customOs.copy(
                kernelLevel = nextLevel,
                optimizationScore = (customOs.optimizationScore + 12).coerceAtMost(100)
            )
            OsModuleType.AI_NEURAL -> customOs.copy(
                aiLevel = nextLevel,
                ecosystemScore = (customOs.ecosystemScore + 10).coerceAtMost(100),
                popularityPercent = (customOs.popularityPercent + 2.5f).coerceAtMost(90f)
            )
            OsModuleType.SECURITY_VAULT -> customOs.copy(
                securityLevel = nextLevel,
                customerLoyaltyPercent = (customOs.customerLoyaltyPercent + 8f).coerceAtMost(98f)
            )
            OsModuleType.CLOUD_SYNC -> customOs.copy(
                cloudLevel = nextLevel,
                customerLoyaltyPercent = (customOs.customerLoyaltyPercent + 10f).coerceAtMost(98f),
                ecosystemScore = (customOs.ecosystemScore + 8).coerceAtMost(100)
            )
            OsModuleType.APP_STORE_SDK -> customOs.copy(
                appStoreLevel = nextLevel,
                totalStoreApps = customOs.totalStoreApps + 50000L,
                ecosystemScore = (customOs.ecosystemScore + 12).coerceAtMost(100)
            )
        }

        val news = NewsArticle(
            id = "os_mod_${module.id}_${currentState.year}_${currentState.month}_${Random.nextInt(100, 999)}",
            title = "🔬 YAZILIM AR-GE GELİŞMESİ: ${module.title} Seviye $nextLevel!",
            text = "${customOs.name} işletim sistemi için ${module.title} yatırımı tamamlandı ($${"%,d".format(upgradeCost)}). Yeni özellik: ${module.impactText}.",
            category = "Teknoloji",
            year = currentState.year,
            month = currentState.month
        )

        val report = MarketReport(
            title = "${module.title} Yükseltildi (Seviye $nextLevel)",
            text = "${customOs.name} için ${module.title} Seviye $nextLevel'a çıkarıldı ($${"%,d".format(upgradeCost)} harcandı). ${module.impactText}",
            profit = -upgradeCost,
            unitsSold = 0,
            reviewScore = 92
        )

        _state.update {
            it.copy(
                budget = it.budget - upgradeCost,
                reputation = (it.reputation + 4).coerceIn(0, 100),
                customOs = updatedOs,
                newsList = it.newsList + news,
                reports = it.reports + report,
                noticeMessage = "${module.title} başarıyla Seviye $nextLevel yapıldı! (+4 İtibar)"
            )
        }
    }

    fun hostDevConference() {
        val currentState = _state.value
        val cost = 25000000L
        if (currentState.budget < cost) {
            _state.update { it.copy(noticeMessage = "Geliştirici Konferansı (DevCon) düzenlemek için yetersiz bütçe! Gereken: $${"%,d".format(cost)}") }
            return
        }

        val customOs = currentState.customOs
        val newDevConCount = customOs.devConCount + 1
        val newApps = 60000L
        val updatedOs = customOs.copy(
            devConCount = newDevConCount,
            totalStoreApps = customOs.totalStoreApps + newApps,
            ecosystemScore = (customOs.ecosystemScore + 10).coerceAtMost(100),
            popularityPercent = (customOs.popularityPercent + 3.0f).coerceAtMost(90f),
            customerLoyaltyPercent = (customOs.customerLoyaltyPercent + 5f).coerceAtMost(98f)
        )

        val news = NewsArticle(
            id = "devcon_${currentState.year}_${currentState.month}_${Random.nextInt(100, 999)}",
            title = "🌐 KÜRESEL GELİŞTİRİCİ KONFERANSI: ${customOs.name} DevCon $newDevConCount!",
            text = "Dünyanın dört bir yanından binlerce yazılım geliştiricisi şirketimizin ${customOs.name} DevCon etkinliğine katıldı. Yeni API'lar, geliştirici araçları ve ${"%,d".format(newApps)} yeni uygulama mağazaya katılıyor!",
            category = "Şirket",
            year = currentState.year,
            month = currentState.month
        )

        val report = MarketReport(
            title = "${customOs.name} DevCon Konferansı",
            text = "Küresel geliştirici konferansı düzenlendi ($${"%,d".format(cost)} harcandı). +${"%,d".format(newApps)} yeni uygulama çekildi, ekosistem puanı +10 arttı.",
            profit = -cost,
            unitsSold = 0,
            reviewScore = 98
        )

        _state.update {
            it.copy(
                budget = it.budget - cost,
                reputation = (it.reputation + 6).coerceIn(0, 100),
                customOs = updatedOs,
                newsList = it.newsList + news,
                reports = it.reports + report,
                noticeMessage = "🎉 Küresel Geliştirici Konferansı (DevCon) başarıyla tamamlandı! (+6 İtibar, +10 Ekosistem)"
            )
        }
    }


    fun saveCustomChipset(chipset: CustomChipset) {
        val currentState = _state.value
        val existingIndex = currentState.customChipsets.indexOfFirst { it.id == chipset.id }
        val isNew = existingIndex < 0
        val cost = if (isNew) chipset.tapeOutCost else 0L

        if (isNew && currentState.budget < cost) {
            _state.update { it.copy(noticeMessage = "❌ Yetersiz bütçe! Yonga tasarımı için $${"%,d".format(cost)} gerekiyor.") }
            return
        }

        val updatedList = if (isNew) {
            currentState.customChipsets + chipset
        } else {
            currentState.customChipsets.map { if (it.id == chipset.id) chipset else it }
        }

        if (isNew) {
            val repBonus = when (chipset.tier) {
                ChipsetTier.ENTRY_LITE -> 2
                ChipsetTier.MID_RANGE -> 4
                ChipsetTier.FLAGSHIP_PRO -> 7
            }

            val news = NewsArticle(
                id = "chip_news_${System.currentTimeMillis()}",
                title = "⚡ ÖZ YONGA SETİ TAMAMLANDI: ${chipset.name} (${chipset.tier.title})",
                text = "${currentState.companyName} Ar-Ge laboratuvarı ${chipset.name} özel mobil yongasını başarıyla tasarladı! ${chipset.processNode.nodeName}, ${chipset.coreCount} Çekirdek ve ${chipset.performanceScore} sentetik güç puanıyla üretim bandına hazır.",
                category = "Şirket",
                year = currentState.year,
                month = currentState.month
            )

            val report = MarketReport(
                title = "Öz Yonga Tasarımı: ${chipset.name}",
                text = "${chipset.name} yongası için $${"%,d".format(cost)} Ar-Ge maske yatırımı yapıldı. Birim üretim maliyeti: $${chipset.unitCost}. Güç Puanı: ${chipset.performanceScore}.",
                profit = -cost,
                unitsSold = 0,
                reviewScore = 95
            )

            _state.update {
                it.copy(
                    budget = it.budget - cost,
                    reputation = (it.reputation + repBonus).coerceIn(0, 100),
                    customChipsets = updatedList,
                    newsList = it.newsList + news,
                    reports = it.reports + report,
                    noticeMessage = "🎉 ${chipset.name} özel yongası başarıyla üretildi! (+${repBonus} İtibar)"
                )
            }
        } else {
            // Düzenleme / Revizyon: Tape-out maliyeti tekrar alınmaz, ID ve telefon referansları korunur
            _state.update {
                it.copy(
                    customChipsets = updatedList,
                    noticeMessage = "✏️ ${chipset.name} yongası başarıyla güncellendi (Revizyon Maliyeti: $0)."
                )
            }
        }
        autoSaveGame()
    }

    fun deleteCustomChipset(chipsetId: String) {
        _state.update { state ->
            val chip = state.customChipsets.find { c -> c.id == chipsetId } ?: return@update state
            val isUsedInAnyPhone = state.activeModels.any { it.specs.processor.contains(chip.name) } ||
                    state.manufacturedPhones.any { it.processor.contains(chip.name) }

            if (isUsedInAnyPhone) {
                // Telefonlar tarafından kullanıldığı için fiziksel silinmez, arşivlenir
                val updated = state.customChipsets.map {
                    if (it.id == chipsetId) it.copy(isArchived = true, isOemSaleActive = false) else it
                }
                state.copy(
                    customChipsets = updated,
                    noticeMessage = "📦 ${chip.name} yongası cihaz modellerinde kullanıldığı için arşivlendi. Eski telefonların maliyet ve donanım verileri korundu."
                )
            } else {
                // Hiçbir telefonda kullanılmadıysa fiziksel silinebilir
                state.copy(
                    customChipsets = state.customChipsets.filterNot { c -> c.id == chipsetId },
                    noticeMessage = "🗑️ ${chip.name} yongası başarıyla silindi."
                )
            }
        }
        autoSaveGame()
    }

    fun unarchiveCustomChipset(chipsetId: String) {
        _state.update { state ->
            val chip = state.customChipsets.find { c -> c.id == chipsetId }
            val updated = state.customChipsets.map {
                if (it.id == chipsetId) it.copy(isArchived = false) else it
            }
            state.copy(
                customChipsets = updated,
                noticeMessage = chip?.let { "♻️ ${it.name} yongası tekrar aktif edildi." }
            )
        }
        autoSaveGame()
    }

    fun toggleChipsetOemSale(chipsetId: String, isOemSaleActive: Boolean, oemSalePrice: Int) {
        _state.update { state ->
            val updated = state.customChipsets.map { chip ->
                if (chip.id == chipsetId) {
                    chip.copy(isOemSaleActive = isOemSaleActive, oemSalePrice = oemSalePrice)
                } else chip
            }
            state.copy(
                customChipsets = updated,
                noticeMessage = if (isOemSaleActive) "🌐 OEM Çip Satışı Aktif Edildi ($$oemSalePrice / adet)." else "🔒 OEM Çip Satışı Durduruldu."
            )
        }
        autoSaveGame()
    }

    fun takeOutLoan(loanType: LoanType): Boolean {
        val current = _state.value
        if (loanType == LoanType.EMERGENCY_BAILOUT && current.budget > 500000L) {
            _state.update { it.copy(noticeMessage = "Devlet Acil Kurtarma Kredisi yalnızca nakit sıkışıklığında veya iflas riski belirdiğinde (< $500.000 bakiye) kullanılabilir.") }
            return false
        }
        if (current.reputation < loanType.requiredReputation) {
            _state.update { it.copy(noticeMessage = "Bu kredi için en az ${loanType.requiredReputation} şirket itibarı gerekiyor!") }
            return false
        }
        if (current.activeLoans.size >= 4) {
            _state.update { it.copy(noticeMessage = "Aynı anda en fazla 4 aktif banka kredisi taşıyabilirsiniz. Lütfen mevcut kredilerinizden bazılarını kapatın.") }
            return false
        }

        val scoreAdjustment = when {
            current.creditScore >= 820 -> -2
            current.creditScore >= 780 -> -1
            current.creditScore < 600 -> +3
            current.creditScore < 680 -> +1
            else -> 0
        }
        val effectiveInterest = (loanType.interestPercent + scoreAdjustment).coerceAtLeast(2)
        val principal = loanType.principal
        val totalRepayment = principal + (principal * effectiveInterest / 100)
        val periodPayment = totalRepayment / loanType.durationPeriods

        val newLoan = BankLoan(
            id = "loan_${System.currentTimeMillis()}_${Random.nextInt(100, 999)}",
            type = loanType,
            principalAmount = principal,
            totalRepayment = totalRepayment,
            remainingBalance = totalRepayment,
            totalPeriods = loanType.durationPeriods,
            remainingPeriods = loanType.durationPeriods,
            periodPayment = periodPayment,
            interestPercent = effectiveInterest
        )

        val news = NewsArticle(
            id = "loan_taken_${current.year}_${current.month}_${current.period}_${Random.nextInt(100, 999)}",
            title = "🏦 BANKA FİNANSMANI: ${loanType.title} Onaylandı",
            text = "Şirket hesabına $${"%,d".format(principal)} tutarında nakit aktarıldı. ${loanType.durationPeriods} dönem boyunca dönemlik $${"%,d".format(periodPayment)} taksit tahsil edilecektir (Toplam Faiz: %$effectiveInterest).",
            category = "Şirket",
            year = current.year,
            month = current.month
        )

        _state.update { state ->
            state.copy(
                budget = state.budget + principal,
                activeLoans = state.activeLoans + newLoan,
                newsList = listOf(news) + state.newsList,
                noticeMessage = "${loanType.title} onaylandı! Hesabınıza $${"%,d".format(principal)} eklendi."
            )
        }
        autoSaveGame()
        return true
    }

    fun payOffLoanEarly(loanId: String): Boolean {
        val current = _state.value
        val loan = current.activeLoans.firstOrNull { it.id == loanId } ?: return false
        val cost = loan.earlyPayoffCost
        if (current.budget < cost) {
            _state.update { it.copy(noticeMessage = "Krediyi erken kapatmak için yeterli bakiye yok! Gereken: $${"%,d".format(cost)}") }
            return false
        }

        val news = NewsArticle(
            id = "loan_early_${current.year}_${current.month}_${Random.nextInt(100, 999)}",
            title = "⚡ ERKEN ÖDEME: ${loan.type.title} Borcu Kapatıldı!",
            text = "${loan.type.title} borcu $${"%,d".format(cost)} peşin ödenerek erken kapatıldı ($${"%,d".format(loan.earlyPayoffDiscountAmount)} faiz indirimi kazanıldı). Kredi notunuz +25 puan arttı!",
            category = "Şirket",
            year = current.year,
            month = current.month
        )

        _state.update { state ->
            state.copy(
                budget = state.budget - cost,
                activeLoans = state.activeLoans.filterNot { it.id == loanId },
                creditScore = (state.creditScore + 25).coerceIn(300, 900),
                newsList = listOf(news) + state.newsList,
                noticeMessage = "${loan.type.title} erken kapatıldı ($${"%,d".format(loan.earlyPayoffDiscountAmount)} faiz indirimi)!"
            )
        }
        autoSaveGame()
        return true
    }

    fun licenseProtectedPatents(): Boolean {
        val current = _state.value
        val eligible = current.patents.filter { it.strategy == PatentStrategy.PROTECTED && it.remainingPeriods > 0 }
        if (eligible.isEmpty()) {
            _state.update { it.copy(noticeMessage = "Lisanslanabilecek korunan patent bulunmuyor.") }
            return false
        }

        val ids = eligible.map { it.techId }.toSet()
        val updated = current.patents.map { patent ->
            if (patent.techId in ids) {
                val monthly = (patent.baseValue * 4L / 1000L).coerceIn(1500L, 250000L)
                patent.copy(strategy = PatentStrategy.LICENSED, monthlyLicenseIncome = monthly)
            } else patent
        }
        val monthlyTotal = updated.filter { it.strategy == PatentStrategy.LICENSED }.sumOf { it.monthlyLicenseIncome }

        _state.update { it.copy(
            patents = updated,
            noticeMessage = "📜 ${eligible.size} patent lisanslandı. Tahmini aylık patent geliri: $${"%,d".format(monthlyTotal)}"
        ) }
        autoSaveGame()
        return true
    }

    /**
     * Korunan patentleri tek seferlik nakde çevirir. Satılan patent tekrar gelir üretmez.
     * Eski UI bağlantısını ve kayıt uyumluluğunu korumak için fonksiyon adı değiştirilmedi.
     */
    fun liquidatePatents(): Boolean {
        val current = _state.value
        val eligible = current.patents.filter { it.strategy == PatentStrategy.PROTECTED && it.remainingPeriods > 0 }
        if (eligible.isEmpty()) {
            _state.update { it.copy(noticeMessage = "Satılabilecek korunan patent bulunmuyor. Araştırmaları tamamladıktan sonra patentler otomatik oluşur.") }
            return false
        }

        // Acil nakit güçlüdür ama lisanslamadan daha düşük uzun vadeli değer verir.
        val cashYield = eligible.sumOf { (it.baseValue * 50L / 100L).coerceAtLeast(75000L) }
        val ids = eligible.map { it.techId }.toSet()
        val updated = current.patents.map { patent ->
            if (patent.techId in ids) patent.copy(strategy = PatentStrategy.SOLD, monthlyLicenseIncome = 0L) else patent
        }

        val news = NewsArticle(
            id = "patent_sale_${current.year}_${current.month}_${Random.nextInt(100, 999)}",
            title = "📜 PATENT PORTFÖYÜ SATIŞI",
            text = "${eligible.size} korunan patentin mülkiyet hakkı devredildi. Şirket $${"%,d".format(cashYield)} nakit aldı; bu patentler artık lisans geliri üretmeyecek.",
            category = "Şirket",
            year = current.year,
            month = current.month
        )

        _state.update { state ->
            state.copy(
                budget = state.budget + cashYield,
                patents = updated,
                newsList = listOf(news) + state.newsList,
                noticeMessage = "${eligible.size} patent satıldı: +$${"%,d".format(cashYield)}. Bu karar geri alınamaz."
            )
        }
        autoSaveGame()
        return true
    }

    fun seekVentureCapital(): Boolean {
        val current = _state.value
        if (current.equitySoldPercent >= 25) {
            _state.update { it.copy(noticeMessage = "Maksimum hisse devir sınırına (%25) ulaştınız! Şirket kontrolünü korumak için daha fazla hisse devredilemez.") }
            return false
        }

        val equitySlice = 5
        val cashYield = 1800000L + (current.reputation * 20000L)

        val news = NewsArticle(
            id = "vc_fund_${current.year}_${current.month}_${Random.nextInt(100, 999)}",
            title = "💼 RİSK SERMAYESİ: %$equitySlice Hisse Karşılığı Fon Girişi",
            text = "Teknoloji yatırım fonu, şirketin %$equitySlice hissesi karşılığında $${"%,d".format(cashYield)} tutarında sermaye yatırımı gerçekleştirdi.",
            category = "Şirket",
            year = current.year,
            month = current.month
        )

        _state.update { state ->
            state.copy(
                budget = state.budget + cashYield,
                equitySoldPercent = state.equitySoldPercent + equitySlice,
                newsList = listOf(news) + state.newsList,
                noticeMessage = "%$equitySlice hisse karşılığında $${"%,d".format(cashYield)} yatırım fonu sağlandı!"
            )
        }
        autoSaveGame()
        return true
    }

    fun emergencyLiquidateStock(modelId: String): Boolean {
        val current = _state.value
        val model = current.activeModels.firstOrNull { it.id == modelId } ?: return false
        if (model.remainingStock <= 0) {
            _state.update { it.copy(noticeMessage = "Bu model için depoda tasfiye edilecek stok kalmadı!") }
            return false
        }

        val unitWholesalePrice = (model.specs.price * 0.5f).toInt().coerceAtLeast(1)
        val totalGained = model.remainingStock.toLong() * unitWholesalePrice.toLong()
        val liquidatedQty = model.remainingStock
        val repPenalty = 3 // Spot piyasa ve toptan tasfiyeden kaynaklanan marka prestiji ve itibar kaybı

        val updatedModels = current.activeModels.map {
            if (it.id == modelId) {
                it.copy(
                    remainingStock = 0,
                    totalSold = it.totalSold + liquidatedQty,
                    totalRevenue = it.totalRevenue + totalGained
                )
            } else it
        }

        val news = NewsArticle(
            id = "stock_liq_${current.year}_${current.month}_${Random.nextInt(100, 999)}",
            title = "📦 ACİL SPOT STOK TASFİYESİ: ${model.specs.name}",
            text = "${model.specs.name} modelinin depodaki ${"%,d".format(liquidatedQty)} adet stoğu spot toptancılara %50 indirimle tasfiye edildi ($${"%,d".format(totalGained)} acil nakit sağlandı). Spot pazara düşen ürünler marka değerini zedelediğinden şirket itibarı -$repPenalty puan düştü.",
            category = "Şirket",
            year = current.year,
            month = current.month
        )

        _state.update { state ->
            state.copy(
                budget = state.budget + totalGained,
                reputation = (state.reputation - repPenalty).coerceIn(0, 100),
                activeModels = updatedModels,
                newsList = listOf(news) + state.newsList,
                noticeMessage = "📦 ${model.specs.name} spot toptan tasfiye edildi (+$${"%,d".format(totalGained)}, -$repPenalty İtibar)!"
            )
        }
        autoSaveGame()
        return true
    }

    fun resolveHardwareCrisis(crisisId: String, strategy: CrisisResolutionStrategy): Boolean {
        val current = _state.value
        val crisis = current.activeHardwareCrises.firstOrNull { it.id == crisisId } ?: return false
        val model = current.activeModels.firstOrNull { it.id == crisis.modelId }

        val cost = when (strategy) {
            CrisisResolutionStrategy.SOFTWARE_PATCH_LIMIT -> 50000L
            CrisisResolutionStrategy.FREE_SERVICE_REPAIR -> (crisis.affectedUnitsCount.toLong() * 25L).coerceAtLeast(100000L)
            CrisisResolutionStrategy.FULL_RECALL_REFUND -> {
                val unitRefund = model?.specs?.price?.toLong() ?: 300L
                (crisis.affectedUnitsCount.toLong() * unitRefund) + ((model?.remainingStock ?: 0).toLong() * 50L)
            }
        }

        if (current.budget < cost) {
            _state.update { it.copy(noticeMessage = "Bu kriz yönetimi stratejisini uygulamak için bütçe yetersiz! Gereken: $${"%,d".format(cost)}") }
            return false
        }

        val repDelta = when (strategy) {
            CrisisResolutionStrategy.SOFTWARE_PATCH_LIMIT -> -4
            CrisisResolutionStrategy.FREE_SERVICE_REPAIR -> +3
            CrisisResolutionStrategy.FULL_RECALL_REFUND -> +12
        }

        val updatedCrises = current.activeHardwareCrises.map {
            if (it.id == crisisId) {
                it.copy(
                    isResolved = true,
                    resolvedYear = current.year,
                    resolvedMonth = current.month,
                    resolutionChoice = strategy.title
                )
            } else it
        }

        val updatedModels = current.activeModels.map {
            if (it.id == crisis.modelId) {
                when (strategy) {
                    CrisisResolutionStrategy.FULL_RECALL_REFUND -> it.copy(
                        remainingStock = 0,
                        isRecalled = true,
                        recalledYear = current.year,
                        recalledMonth = current.month
                    )
                    CrisisResolutionStrategy.SOFTWARE_PATCH_LIMIT -> {
                        val restoredStock = if (it.remainingStock == 0 && it.totalSold < it.totalStock) {
                            it.totalStock - it.totalSold
                        } else it.remainingStock
                        it.copy(
                            isRecalled = false,
                            remainingStock = restoredStock,
                            reviewScore = (it.reviewScore - 6).coerceAtLeast(10)
                        )
                    }
                    CrisisResolutionStrategy.FREE_SERVICE_REPAIR -> {
                        val restoredStock = if (it.remainingStock == 0 && it.totalSold < it.totalStock) {
                            it.totalStock - it.totalSold
                        } else it.remainingStock
                        it.copy(
                            isRecalled = false,
                            remainingStock = restoredStock,
                            reviewScore = (it.reviewScore + 2).coerceAtMost(100)
                        )
                    }
                }
            } else it
        }

        val news = NewsArticle(
            id = "crisis_res_${crisisId}_${current.year}_${current.month}_${Random.nextInt(100, 999)}",
            title = "🛡️ KRİZ ÇÖZÜLDÜ: ${model?.specs?.name ?: "Bilinmeyen Model"} - ${strategy.title}",
            text = "${current.companyName}, ${model?.specs?.name ?: "Bilinmeyen Model"} modelindeki ${crisis.crisisType.name} sorununa yönelik '${strategy.title}' stratejisini uyguladı ($${"%,d".format(cost)} harcandı). ${""}!",
            category = "Şirket",
            year = current.year,
            month = current.month
        )

        _state.update { state ->
            state.copy(
                budget = state.budget - cost,
                reputation = (state.reputation + repDelta).coerceIn(0, 100),
                activeHardwareCrises = updatedCrises,
                activeModels = updatedModels,
                newsList = listOf(news) + state.newsList,
                noticeMessage = "${model?.specs?.name ?: "Bilinmeyen Model"} krizi yönetildi: ${strategy.title} ($${"%,d".format(cost)})"
            )
        }
        autoSaveGame()
        return true
    }
    private fun updateAcquisitions(currentState: GameState): List<AcquisitionTarget> {
        var targets = currentState.acquisitionTargets.toMutableList()
        
        // Remove old targets
        targets = targets.map { it.copy(remainingMonthsAvailable = it.remainingMonthsAvailable - 1) }.filter { it.remainingMonthsAvailable > 0 }.toMutableList()

        // Randomly generate new targets if empty or rare chance
        if (targets.isEmpty() || kotlin.random.Random.nextFloat() < 0.05f) {
            val names = listOf("Pulse", "Nexa", "Vanguard", "CyberCore", "Zeni", "Titan", "Aura", "Nebula", "Spectra", "Lumina")
            val name = names.random()
            
            val type = CompanyType.values().random()
            val baseVal = kotlin.random.Random.nextLong(10_000_000L, 200_000_000L)
            val valMult = when(type) {
                CompanyType.STRUGGLING -> 0.5f
                CompanyType.NORMAL -> 1.0f
                CompanyType.SUCCESSFUL -> 2.0f
                CompanyType.TECH_STARTUP -> 0.3f
            }
            
            val finalValuation = (baseVal * valMult).toLong()
            val debt = if (type == CompanyType.STRUGGLING) (finalValuation * 0.8f).toLong() else (finalValuation * 0.1f).toLong()
            val rep = kotlin.random.Random.nextInt(10, 80)
            val emps = kotlin.random.Random.nextInt(50, 2000)
            
            val numSeries = if (type == CompanyType.TECH_STARTUP) 0 else kotlin.random.Random.nextInt(1, 4)
            val generatedSeries = mutableListOf<PhoneSeriesLegacy>()
            for (i in 0 until numSeries) {
                generatedSeries.add(
                    PhoneSeriesLegacy(
                        seriesName = "${name.take(3)} Series $i",
                        originCompanyId = name,
                        launchYear = currentState.year - kotlin.random.Random.nextInt(1, 5),
                        totalModelsReleased = kotlin.random.Random.nextInt(1, 6),
                        averageReviewScore = kotlin.random.Random.nextInt(40, 95),
                        seriesReputation = kotlin.random.Random.nextInt(30, 90),
                        totalSales = kotlin.random.Random.nextLong(1_000_000, 20_000_000)
                    )
                )
            }
            
            targets.add(
                AcquisitionTarget(
                    id = "MNA_${currentState.year}_${kotlin.random.Random.nextInt(1000, 9999)}",
                    name = "$name Mobile",
                    logoEmoji = listOf("📱", "💻", "🌐", "⚡", "🔮").random(),
                    type = type,
                    cash = (finalValuation * 0.2f).toLong(),
                    debt = debt,
                    brandReputation = rep,
                    employees = emps,
                    patents = emptyList(), // Can add random tech nodes
                    activeSeries = generatedSeries,
                    valuation = finalValuation,
                    minimumAcceptableMultiplier = type.baseMultiplier * kotlin.random.Random.nextFloat().coerceAtLeast(0.8f),
                    remainingMonthsAvailable = kotlin.random.Random.nextInt(6, 18)
                )
            )
        }
        
        return targets
    }

    fun bidForCompany(targetId: String, bidAmount: Long, strategy: PostAcquisitionStrategy) {
        val currentState = _state.value
        val target = currentState.acquisitionTargets.find { it.id == targetId } ?: return
        
        if (currentState.budget < bidAmount) {
            _state.update { it.copy(noticeMessage = "Bu teklif için yeterli bütçeniz yok.") }
            return
        }
        
        val minAcceptable = (target.valuation * target.minimumAcceptableMultiplier).toLong()
        
        if (bidAmount >= minAcceptable) {
            val newSubBrands = currentState.ownedSubBrands.toMutableList()
            
            var newEngineers = currentState.engineers
            var newQa = currentState.qaInspectors
            var newWorkers = currentState.assemblyWorkers
            var newCompanyName = currentState.companyName
            var newLogoId = currentState.companyLogoId
            var newColorHex = currentState.companyBrandColorHex
            var newSlogan = currentState.companySlogan
            
            val totalCost = bidAmount + target.debt
            if (currentState.budget < totalCost) {
                _state.update { it.copy(noticeMessage = "Borçlarla birlikte toplam satın alma maliyetini karşılayamıyorsunuz.") }
                return
            }
            
            when (strategy) {
                PostAcquisitionStrategy.BECOME_MAIN_BRAND -> {
                    newCompanyName = target.name
                    newLogoId = "ic_logo_crown"
                    newSlogan = "${target.name} Ekosistemi"
                    newEngineers += (target.employees * 0.4f).toInt()
                    newWorkers += (target.employees * 0.6f).toInt()
                }
                PostAcquisitionStrategy.INDEPENDENT_BRAND -> {
                    newSubBrands.add(
                        OwnedSubBrand(
                            id = target.id,
                            name = target.name,
                            logoEmoji = target.logoEmoji,
                            brandReputation = target.brandReputation,
                            cash = target.cash,
                            marketSharePercent = 1.0f,
                            monthlySales = target.employees * 30,
                            monthlyDividend = (target.valuation * 0.008f).toLong().coerceAtLeast(50_000L),
                            strategyType = "Bağımsız Girişim Portföyü",
                            logoId = "ic_logo_crown"
                        )
                    )
                }
                PostAcquisitionStrategy.MERGE_TO_MAIN -> {
                    newEngineers += (target.employees * 0.3f).toInt()
                    newWorkers += (target.employees * 0.7f).toInt()
                }
                PostAcquisitionStrategy.LIQUIDATE_ASSETS -> {
                    newEngineers += (target.employees * 0.15f).toInt()
                }
            }
            
            val report = MarketReport(
                title = "Şirket Satın Alındı: ${target.name}",
                text = "${target.name} şirketi $${formatShortCurrency(bidAmount)} bedelle satın alındı. Strateji: ${strategy.title}.",
                profit = -totalCost,
                unitsSold = 0,
                reviewScore = 0
            )

            val news = NewsArticle(
                id = "acq_target_${target.id}_${currentState.year}_${currentState.month}",
                title = "💼 M&A HABERİ: ${target.name} Satın Alındı!",
                text = "${currentState.companyName}, ${target.name} girişimini $${formatShortCurrency(bidAmount)} bedelle bünyesine kattı.",
                category = "Şirket",
                year = currentState.year,
                month = currentState.month
            )
            
            _state.update {
                it.copy(
                    budget = it.budget - bidAmount - target.debt + if(strategy == PostAcquisitionStrategy.LIQUIDATE_ASSETS) target.cash else 0L,
                    acquisitionTargets = it.acquisitionTargets.filter { t -> t.id != targetId },
                    ownedSubBrands = newSubBrands,
                    companyName = newCompanyName,
                    companyLogoId = newLogoId,
                    companyBrandColorHex = newColorHex,
                    companySlogan = newSlogan,
                    engineers = newEngineers,
                    qaInspectors = newQa,
                    assemblyWorkers = newWorkers,
                    reports = it.reports + report,
                    newsList = listOf(news) + it.newsList,
                    noticeMessage = "Satın alma BAŞARILI! ${target.name} artık sizin."
                )
            }
            autoSaveGame()
        } else {
            _state.update { it.copy(noticeMessage = "Teklifiniz reddedildi! Şirket yönetim kurulu $${formatShortCurrency(minAcceptable)} üzerinde teklif bekliyor.") }
        }
    }

    /**
     * Küresel bir rakip şirkete (Samsung, Apple, Xiaomi, Google, Huawei, Oppo vb.) satın alma teklifi verir.
     */
    fun bidForCompetitorCompany(competitorId: String, bidAmount: Long, strategy: PostAcquisitionStrategy) {
        val currentState = _state.value
        val competitor = currentState.competitors.find { it.id == competitorId } ?: return

        if (currentState.budget < bidAmount) {
            _state.update { it.copy(noticeMessage = "Bu teklif için yeterli bütçeniz yok! Gereken: $${formatShortCurrency(bidAmount)}") }
            return
        }

        // Kabul barajı (Prestijli markalar için değerlemenin %100 - %115'i)
        val minAcceptableMultiplier = when {
            competitor.name.contains("Apple", ignoreCase = true) || competitor.id == "comp_apple" -> 1.15f
            competitor.name.contains("Samsung", ignoreCase = true) || competitor.id == "comp_samsung" -> 1.10f
            competitor.name.contains("Xiaomi", ignoreCase = true) -> 1.05f
            else -> 0.95f
        }
        val minAcceptable = (competitor.estimatedValuation * minAcceptableMultiplier).toLong()

        if (bidAmount >= minAcceptable) {
            // SATIN ALMA KABUL EDİLDİ!
            val newSubBrands = currentState.ownedSubBrands.toMutableList()
            var newCompanyName = currentState.companyName
            var newLogoId = currentState.companyLogoId
            var newBrandColorHex = currentState.companyBrandColorHex
            var newSlogan = currentState.companySlogan
            var newPlayerMarketShare = currentState.playerMarketSharePercent
            var newEngineers = currentState.engineers
            var newQa = currentState.qaInspectors
            var newWorkers = currentState.assemblyWorkers
            var newReputation = currentState.reputation

            when (strategy) {
                PostAcquisitionStrategy.BECOME_MAIN_BRAND -> {
                    // OYUNCU DOĞRUDAN SATIN ALINAN DEVİN SAHİBİ VE CEO'SU OLUR!
                    newCompanyName = competitor.name
                    newLogoId = competitor.id
                    newBrandColorHex = competitor.brandColorHex
                    newSlogan = competitor.slogan
                    newPlayerMarketShare = (currentState.playerMarketSharePercent + competitor.marketSharePercent).coerceAtMost(85f)
                    newEngineers += (competitor.monthlySales / 800).coerceIn(15, 120)
                    newQa += (competitor.monthlySales / 1600).coerceIn(8, 60)
                    newWorkers += (competitor.monthlySales / 200).coerceIn(50, 500)
                    newReputation = (currentState.reputation + 25).coerceAtMost(100)
                }
                PostAcquisitionStrategy.INDEPENDENT_BRAND -> {
                    // Holding çatısı altında bağımsız alt marka
                    val monthlyDividend = (competitor.monthlySales.toLong() * competitor.currentModelPrice * 0.08).toLong().coerceAtLeast(500_000L)
                    newSubBrands.add(
                        OwnedSubBrand(
                            id = competitor.id,
                            name = competitor.name,
                            logoEmoji = competitor.logoEmoji,
                            brandReputation = 90,
                            cash = (competitor.estimatedValuation * 0.05).toLong(),
                            brandColorHex = competitor.brandColorHex,
                            marketSharePercent = competitor.marketSharePercent,
                            monthlySales = competitor.monthlySales,
                            monthlyDividend = monthlyDividend,
                            strategyType = competitor.strategyType,
                            logoId = competitor.id
                        )
                    )
                    newReputation = (currentState.reputation + 15).coerceAtMost(100)
                }
                PostAcquisitionStrategy.MERGE_TO_MAIN -> {
                    // Varlıkları ana markaya birleştir
                    newPlayerMarketShare = (currentState.playerMarketSharePercent + competitor.marketSharePercent * 0.85f).coerceAtMost(85f)
                    newEngineers += (competitor.monthlySales / 1000).coerceIn(10, 80)
                    newWorkers += (competitor.monthlySales / 250).coerceIn(40, 400)
                    newReputation = (currentState.reputation + 10).coerceAtMost(100)
                }
                PostAcquisitionStrategy.LIQUIDATE_ASSETS -> {
                    // Tasfiye et ve nakit çek
                    newEngineers += (competitor.monthlySales / 1500).coerceIn(5, 40)
                    newPlayerMarketShare = (currentState.playerMarketSharePercent + competitor.marketSharePercent * 0.4f).coerceAtMost(85f)
                }
            }

            // Rakibi aktif rakipler listesinden çıkar (Artık oyuncuya ait)
            val updatedCompetitors = currentState.competitors.filter { it.id != competitorId }

            val headline = "👑 TARİHİ SATIN ALMA: ${currentState.companyName}, ${competitor.name.uppercase()}'ı Satın Aldı!"
            val articleText = "Küresel teknoloji dünyasında deprem etkisi! ${currentState.companyName}, $${formatShortCurrency(bidAmount)} rekor bedelle ${competitor.name} şirketini satın alarak bünyesine kattı. Seçilen Strateji: '${strategy.title}'."

            val news = NewsArticle(
                id = "acq_comp_${competitor.id}_${currentState.year}_${currentState.month}_${currentState.period}",
                title = headline,
                text = articleText,
                category = "Sektör",
                year = currentState.year,
                month = currentState.month
            )

            val report = MarketReport(
                title = "Dev Satın Alma: ${competitor.name}",
                text = "${competitor.name} şirketi $${formatShortCurrency(bidAmount)} bedelle başarıyla satın alındı. ($articleText)",
                profit = -bidAmount,
                unitsSold = 0,
                reviewScore = 0
            )

            _state.update {
                it.copy(
                    budget = it.budget - bidAmount,
                    competitors = updatedCompetitors,
                    ownedSubBrands = newSubBrands,
                    companyName = newCompanyName,
                    companyLogoId = newLogoId,
                    companyBrandColorHex = newBrandColorHex,
                    companySlogan = newSlogan,
                    playerMarketSharePercent = newPlayerMarketShare,
                    engineers = newEngineers,
                    qaInspectors = newQa,
                    assemblyWorkers = newWorkers,
                    reputation = newReputation,
                    reports = it.reports + report,
                    newsList = listOf(news) + it.newsList,
                    noticeMessage = "🎉 TEBRİKLER! ${competitor.name} şirketi $${formatShortCurrency(bidAmount)} bedelle satın alındı!"
                )
            }
            autoSaveGame()
        } else {
            _state.update {
                it.copy(
                    noticeMessage = "Teklifiniz ${competitor.name} Yönetim Kurulu tarafından REDDEDİLDİ! Minimum kabul eşiği: $${formatShortCurrency(minAcceptable)}."
                )
            }
        }
    }

    /**
     * Kendi şirketinin belirli bir hissesini (%5, %10, %20, %49) yatırımcılara satar ve kasaya nakit sağlar.
     */
    fun sellCompanyEquity(percentToSell: Int): Boolean {
        val currentState = _state.value
        val currentSold = currentState.equitySoldPercent
        val maxSellable = 49 - currentSold

        if (percentToSell <= 0 || percentToSell > maxSellable) {
            _state.update { it.copy(noticeMessage = "Maksimum %49 hisse satabilirsiniz (Şirket çoğunluk kontrolünü korumak için). Kalan satılabilir pay: %$maxSellable.") }
            return false
        }

        val valuation = currentState.playerValuation
        val cashYield = (valuation * (percentToSell / 100.0)).toLong()

        val news = NewsArticle(
            id = "equity_sale_${currentState.year}_${currentState.month}_${currentState.period}_$percentToSell",
            title = "📈 YATIRIM TURU: ${currentState.companyName} %$percentToSell Hisse Sattı",
            text = "Yatırım konsorsiyumu, ${currentState.companyName} şirketinin %$percentToSell hissesine karşılık kasaya +$${formatShortCurrency(cashYield)} nakit sermaye yatırımı yaptı. Şirket değerlemesi: $${formatShortCurrency(valuation)}.",
            category = "Şirket",
            year = currentState.year,
            month = currentState.month
        )

        val report = MarketReport(
            title = "Hisse Satışı Geliri (+%$percentToSell)",
            text = "%$percentToSell şirket hissesi satılarak kasaya $${formatShortCurrency(cashYield)} sermaye girişi sağlandı.",
            profit = cashYield,
            unitsSold = 0,
            reviewScore = 0
        )

        _state.update {
            it.copy(
                budget = it.budget + cashYield,
                equitySoldPercent = it.equitySoldPercent + percentToSell,
                reports = it.reports + report,
                newsList = listOf(news) + it.newsList,
                noticeMessage = "Hisse satışı tamamlandı! Kasaya +$${formatShortCurrency(cashYield)} eklendi."
            )
        }
        autoSaveGame()
        return true
    }

    /**
     * Daha önce yatırımcılara satılmış hisseleri şirket bütçesiyle geri satın alır (Share Buyback).
     */
    fun buybackCompanyEquity(percentToBuyback: Int): Boolean {
        val currentState = _state.value
        if (percentToBuyback <= 0 || percentToBuyback > currentState.equitySoldPercent) {
            _state.update { it.copy(noticeMessage = "Geri satın alınabilecek hisse payı: %${currentState.equitySoldPercent}.") }
            return false
        }

        val valuation = currentState.playerValuation
        val cost = (valuation * (percentToBuyback / 100.0) * 1.05).toLong() // %5 prim ile geri alış

        if (currentState.budget < cost) {
            _state.update { it.copy(noticeMessage = "Hisseleri geri almak için yetersiz bütçe! Gereken: $${formatShortCurrency(cost)}") }
            return false
        }

        val news = NewsArticle(
            id = "equity_buyback_${currentState.year}_${currentState.month}_${currentState.period}_$percentToBuyback",
            title = "💎 HİSSE GERİ ALIMI: ${currentState.companyName} %$percentToBuyback Hisselerini Geri Aldı",
            text = "${currentState.companyName}, yatırımcılardaki %$percentToBuyback payını $${formatShortCurrency(cost)} karşılığında geri satın alarak kurucu sahiplik oranını artırdı.",
            category = "Şirket",
            year = currentState.year,
            month = currentState.month
        )

        _state.update {
            it.copy(
                budget = it.budget - cost,
                equitySoldPercent = (it.equitySoldPercent - percentToBuyback).coerceAtLeast(0),
                newsList = listOf(news) + it.newsList,
                noticeMessage = "%$percentToBuyback hisse $${formatShortCurrency(cost)} bedelle başarıyla geri alındı!"
            )
        }
        autoSaveGame()
        return true
    }

    /**
     * Kendi şirketini tamamen (%100) global bir holdinge / yatırım fonuna satıp (Full Exit)
     * tam piyasa değerini kasaya nakit çeker.
     */
    fun sellCompanyEntirely(): Boolean {
        val currentState = _state.value
        val fullValuation = currentState.playerValuation
        val oldCompanyName = currentState.companyName

        val news = NewsArticle(
            id = "full_exit_${currentState.year}_${currentState.month}_${currentState.period}",
            title = "🔥 TARİHİ DEVİR: $oldCompanyName $${formatShortCurrency(fullValuation)} Bedelle Satıldı!",
            text = "Teknoloji dünyasının başarılı ismi, $oldCompanyName şirketini $${formatShortCurrency(fullValuation)} değerleme üzerinden uluslararası yatırım fonuna devretti. Kasadaki dev nakit birikimiyle yeni bir küresel devi devralmaya hazırlanıyor!",
            category = "Şirket",
            year = currentState.year,
            month = currentState.month
        )

        val report = MarketReport(
            title = "Şirket Tam Devri (Exit Geliri)",
            text = "$oldCompanyName şirketinin tamamı satılarak $${formatShortCurrency(fullValuation)} nakit sermaye elde edildi.",
            profit = fullValuation,
            unitsSold = 0,
            reviewScore = 0
        )

        _state.update {
            it.copy(
                budget = it.budget + fullValuation,
                equitySoldPercent = 0,
                companyName = "Apex Capital",
                companyLogoId = "ic_logo_crown",
                companySlogan = "Küresel Teknoloji Yatırımları & Holding",
                reports = it.reports + report,
                newsList = listOf(news) + it.newsList,
                noticeMessage = "🎉 Şirketiniz $${formatShortCurrency(fullValuation)} bedelle satıldı! Kasadaki dev servetle artık Apple, Samsung veya dilediğiniz devi satın alabilirsiniz."
            )
        }
        autoSaveGame()
        return true
    }

    /**
     * Satın alınan alt markalardan birini aktif birincil marka yapar.
     */
    fun rebrandToAcquiredBrand(subBrandId: String): Boolean {
        val currentState = _state.value
        val subBrand = currentState.ownedSubBrands.find { it.id == subBrandId } ?: return false

        _state.update {
            it.copy(
                companyName = subBrand.name,
                companyLogoId = subBrand.logoId ?: "ic_logo_crown",
                companyBrandColorHex = subBrand.brandColorHex,
                companySlogan = "${subBrand.name} - Küresel Güç",
                noticeMessage = "Birincil marka '${subBrand.name}' olarak güncellendi!"
            )
        }
        autoSaveGame()
        return true
    }

    fun dismissTechExpo() {
        _state.update { it.copy(activeTechExpo = null) }
    }

    fun investInDeveloperFund(amount: Long) {
        val currentState = _state.value
        if (currentState.budget < amount) {
            _state.update { it.copy(noticeMessage = "Geliştirici Teşvik Fonu için yetersiz bütçe! Gereken: $${"%,d".format(amount)}") }
            return
        }

        val customOs = currentState.customOs
        val updatedOs = customOs.copy(
            devFundBalance = customOs.devFundBalance + amount,
            ecosystemScore = (customOs.ecosystemScore + (amount / 2000000L).toInt().coerceIn(1, 10)).coerceAtMost(100)
        )

        _state.update {
            it.copy(
                budget = it.budget - amount,
                customOs = updatedOs,
                noticeMessage = "Geliştirici Fonuna $${"%,d".format(amount)} aktarıldı! (Zamanla organik uygulama artışı sağlayacak)"
            )
        }
        autoSaveGame()
    }
}
