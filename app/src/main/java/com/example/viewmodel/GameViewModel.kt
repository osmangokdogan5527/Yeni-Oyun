package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.AiGameService
import com.example.data.AppDatabase
import com.example.data.GameSaveEntity
import com.example.data.GameSaveRepository
import com.example.util.BenchmarkCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
                    _state.value = loaded
                } catch (e: Exception) {
                    // Fall back to default
                }
            }
        }
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
                    _state.value = loaded
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
        val newNewsList = currentState.newsList.toMutableList()
        val finishedModelsNews = mutableListOf<NewsArticle>()

        // Update Market Trends (Progresses every full month / 2 periods)
        var updatedTrend = currentState.currentTrend
        if (isSecondHalf) {
            if (updatedTrend.remainingMonths <= 1) {
                val newGeneratedTrend = generateNewTrend(newYear, updatedTrend.category)
                updatedTrend = newGeneratedTrend
                newNewsList.add(
                    NewsArticle(
                        id = "trend_news_${newYear}_${newMonth}_${Random.nextInt(100, 999)}",
                        title = "🔥 YENİ TÜKETİCİ TRENDİ: ${newGeneratedTrend.title}",
                        text = "${newGeneratedTrend.description} Bu trende uygun cihazlar pazarda +%50 daha fazla talep görecek! (${newGeneratedTrend.category.tip})",
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
                
                // Base bi-weekly batch (half of monthly)
                val basePeriodBatch = model.totalStock / (model.maxMonthsOnMarket * 2).toFloat()
                
                // Quality, reputation and active marketing campaign demand multiplier
                val qualityFactor = (model.reviewScore / 55.0f).coerceIn(0.4f, 2.0f)
                
                // 1. ZORLUK & İTİBAR DENGESİ: Düşük itibarda satışlar daha zor ve gerçekçi
                val repFactor = when {
                    currentState.reputation < 20 -> 0.35f + (currentState.reputation / 20.0f) * 0.30f // 0.35x - 0.65x
                    currentState.reputation < 50 -> 0.65f + ((currentState.reputation - 20) / 30.0f) * 0.40f // 0.65x - 1.05x
                    currentState.reputation < 80 -> 1.05f + ((currentState.reputation - 50) / 30.0f) * 0.45f // 1.05x - 1.50x
                    else -> 1.50f + ((currentState.reputation - 80) / 20.0f) * 0.50f // 1.50x - 2.00x
                }

                // 2. FİYAT DUYARLILIĞI (PRICE ELASTICITY)
                val estimatedUnitCost = model.specs.unitCost.coerceAtLeast(40)
                val markupRatio = model.specs.price.toFloat() / estimatedUnitCost.toFloat()
                val priceElasticityFactor = when {
                    markupRatio > 2.6f && currentState.reputation < 35 -> 0.40f
                    markupRatio > 2.0f && currentState.reputation < 50 -> 0.65f
                    markupRatio > 1.7f && currentState.reputation < 25 -> 0.75f
                    markupRatio <= 1.3f -> 1.25f
                    else -> 1.0f
                }

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
                val osSynergyFactor = when {
                    model.specs.osFocus.contains("Oyun") && model.specs.style == "Oyuncu" -> 1.30f
                    model.specs.osFocus.contains("Güvenlik") && model.specs.style == "Klasik" -> 1.25f
                    model.specs.osFocus.contains("Yapay Zeka") && model.matchesTrend -> 1.20f
                    model.specs.osFocus.contains("Estetik") -> 1.12f
                    model.specs.osType.contains("Bağımsız") -> 1.15f
                    model.specs.osType.contains("Özel") -> 1.08f
                    else -> 1.0f
                }

                // Lifecycle Sales Curve Factor (over periods)
                val lifecycleSalesCurve = when (newMonths) {
                    0, 1 -> 0.45f
                    2 -> 0.80f
                    3 -> 2.20f
                    4 -> 2.40f
                    5 -> 2.00f
                    6 -> 1.50f
                    7 -> 0.70f
                    8 -> 0.40f
                    9 -> 0.25f
                    10 -> 0.15f
                    11 -> 0.10f
                    12 -> 0.05f
                    else -> 0.04f
                }

                val demandFactor = qualityFactor * repFactor * priceElasticityFactor * seriesLoyaltyFactor * tierDemandFactor * campaignFactor * trendFactor * colorFactor * designFactor * osSynergyFactor * lifecycleSalesCurve
                
                // 2-week units sold (0 if demand is zero or below)
                val calculatedUnits = (basePeriodBatch * demandFactor).toInt().coerceAtLeast(0)
                val unitsSoldThisPeriod = calculatedUnits.coerceAtMost(model.remainingStock)
                val revenueThisPeriod = unitsSoldThisPeriod.toLong() * model.specs.price
                
                totalMonthlyRevenue += revenueThisPeriod
                totalMonthlyUnitsSold += unitsSoldThisPeriod
                qualityWeightedReviewSum += unitsSoldThisPeriod.toDouble() * model.reviewScore

                var isExtendedNewsSent = model.isExtendedNewsSent
                if (newMonths == 12 && model.maxMonthsOnMarket == 24 && !model.isExtendedNewsSent) {
                    isExtendedNewsSent = true
                    finishedModelsNews.add(
                        NewsArticle(
                            id = "news_ext_${model.id}",
                            title = "YOĞUN TALEP: ${model.specs.name} 2. Yılına Girdi!",
                            text = "${model.specs.name} modeli yüksek müşteri memnuniyeti (${model.reviewScore}/100) ve yoğun pazar talebi sayesinde 24 aya kadar satışta kalmaya devam ediyor!",
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
                    remainingStock = model.remainingStock - unitsSoldThisPeriod,
                    totalSold = model.totalSold + unitsSoldThisPeriod,
                    totalRevenue = model.totalRevenue + revenueThisPeriod,
                    periodsOnMarket = newPeriods,
                    monthsOnMarket = newMonths,
                    isExtendedNewsSent = isExtendedNewsSent,
                    activeCampaign = updatedCampaign,
                    matchesTrend = isTrendActive
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

                // Geri Çağırma (Recall) Kontrolü
                val (modelAfterRecallCheck, recallOutcome) = checkForRecall(updatedModel, newYear, newMonth)
                if (recallOutcome != null) {
                    totalRecallCost += recallOutcome.compensationCost
                    totalRecallReputationPenalty += recallOutcome.reputationPenalty
                    finishedModelsNews.add(
                        NewsArticle(
                            id = "news_recall_${modelAfterRecallCheck.id}",
                            title = "⚠️ GERİ ÇAĞIRMA: ${modelAfterRecallCheck.specs.name} Piyasadan Çekildi!",
                            text = "${modelAfterRecallCheck.specs.name} modelinde tespit edilen üretim kusurları nedeniyle cihaz geri çağrıldı. " +
                                "Müşteri tazminatları ve kalan stoğun imhası $${"%,d".format(recallOutcome.compensationCost)} maliyet oluşturdu, " +
                                "itibarınız ${recallOutcome.reputationPenalty} puan düştü.",
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

            // 4. Popularity increment
            val devGrowthFactor = (devCount * 0.15f)
            val versionFactor = (updatedCustomOs.majorVersionCount * 0.20f)
            val playerSalesFactor = (totalMonthlyUnitsSold / 15000f)
            val adoptionBonus = (adopters * 0.30f * licenseType.adoptionSpeedMultiplier)
            val popularityDelta = (devGrowthFactor + versionFactor + playerSalesFactor + adoptionBonus + 0.15f) * 0.18f
            
            val newPopularity = (updatedCustomOs.popularityPercent + popularityDelta).coerceIn(1.0f, 85.0f)
            val newEcosystemScore = (15 + (newPopularity * 0.8f).toInt() + (devCount * 3) + (updatedCustomOs.majorVersionCount * 5)).coerceIn(10, 100)

            // 5. App Store Revenue & License Revenue Calculation (Bi-weekly)
            val totalEcosystemDevices = activePlayerUserBase + newThirdPartyActiveDevices
            if (totalEcosystemDevices > 0) {
                val baseUserRate = updatedCustomOs.type.storeRevenuePerUser / 2f
                val commissionMultiplier = (updatedCustomOs.commissionRate.percent / 20.0f) * updatedCustomOs.commissionRate.marketLoyaltyBonus
                val popMultiplier = (newPopularity / 20.0f).coerceIn(0.25f, 2.5f)
                val storeModuleBonus = 1.0f + (updatedCustomOs.appStoreLevel - 1) * 0.25f
                
                appStoreRevenueThisPeriod = (totalEcosystemDevices * baseUserRate * commissionMultiplier * popMultiplier * licenseType.storeRevenueMultiplier * storeModuleBonus / 3.5f).toLong()
            }

            // 6. Cloud & Ecosystem Subscription Revenue
            val cloudRevenueThisPeriod = if (updatedCustomOs.cloudLevel > 1 && totalEcosystemDevices > 0) {
                val payingUsersRatio = 0.08f + (updatedCustomOs.cloudLevel * 0.04f)
                val cloudUsers = (totalEcosystemDevices * payingUsersRatio).toLong()
                (cloudUsers * 0.75f).toLong() // $0.75 per 2-weeks ($1.5/mo)
            } else 0L

            if (licenseType == OsLicenseType.CLOSED_PROPRIETARY && updatedCustomOs.perDeviceLicenseFee > 0 && thirdPartyPeriodProduction > 0) {
                licenseRevenueThisPeriod = thirdPartyPeriodProduction * updatedCustomOs.perDeviceLicenseFee
            }

            // 7. Store Apps Catalog Growth
            val appsFromDevFund = (updatedCustomOs.devFundBalance / 80000L).coerceAtLeast(0L)
            val newAppsAdded = ((1200L * updatedCustomOs.appStoreLevel) + (devCount * 600L) + (updatedCustomOs.ecosystemScore * 450L) + appsFromDevFund) / 2L
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

        val totalCombinedRevenue = totalMonthlyRevenue + appStoreRevenueThisPeriod + licenseRevenueThisPeriod + (updatedCustomOs.lastMonthCloudRevenue / 2) + periodChipsetIncome
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
                        headline = releaseHeadline
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

        _state.update {
            it.copy(
                period = newPeriod,
                month = newMonth,
                year = newYear,
                budget = it.budget + netIncome - autoStartResearchCost + expoPrizeTotal - totalRecallCost,
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
                reports = it.reports + researchReports + report,
                newsList = newNewsList,
                currentTrend = updatedTrend,
                competitors = updatedCompetitors,
                competitorReleases = updatedCompetitorReleases,
                playerMarketSharePercent = playerSharePercent,
                totalMarketMonthlyVolume = totalMarketVolume * 2,
                activeTechExpo = triggeredExpoEvent,
                pastTechExpos = if (triggeredExpoEvent != null) listOf(triggeredExpoEvent) + it.pastTechExpos else it.pastTechExpos,
                activeSupplyChainEvent = nextSupplyEvent,
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
        val totalCost = (productionCost.toLong() * specs.quantity) + specs.qaBudget
        
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

        if (currentYear >= 2013 && specs.display.contains("720p")) techPenalty += 10
        if (currentYear >= 2016 && specs.display.contains("1080p")) techPenalty += 8
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

        val reviewScore = (baseQuality + qaFactor + zeroQaPenalty + currentState.qaScoreBonus + designBonus + osBonus + specs.tier.reviewBonus + Random.nextInt(-3, 4)).coerceIn(10, 100)

        // Geri Çağırma (Recall) Riski
        val recallRiskPercent = calculateRecallRisk(qaPerUnit = qaPerUnit, reviewScore = reviewScore)

        val techComment = when {
            finalTechPenalty == 0 -> "Teknolojisi, tasarımı ve yazılımı çağın ötesinde!"
            finalTechPenalty < 10 -> "Donanımı ve yazılımı günümüz standartlarına uygun."
            finalTechPenalty < 25 -> "Biraz geride kalmış bir teknoloji."
            else -> "Teknolojisi maalesef çok eski."
        }

        val isTrendMatched = checkTrendMatch(specs, currentState.currentTrend)
        val trendBonusNote = if (isTrendMatched) " 🔥 Aktif Pazar Trendi (${currentState.currentTrend.title}) yakalandı! +%50 Satış Bonusu devrede." else ""
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
            recallRiskPercent = recallRiskPercent
        )

        val productionNote = if (initialProducedBatch < specs.quantity) {
            " 🏭 Fabrika kapasitesi nedeniyle ilk seride ${"%,d".format(initialProducedBatch)} adet üretildi; kalan ${"%,d".format(specs.quantity - initialProducedBatch)} adet önümüzdeki periyotlarda kademeli olarak üretilecek."
        } else ""

        val launchNews = NewsArticle(
            id = "news_launch_${newActiveModel.id}",
            title = "${currentState.companyName.uppercase()} LANSMANI: ${specs.name} Piyasada!",
            text = "${currentState.companyName}, ${specs.name} modelini üretti! ${"%,d".format(specs.quantity)} adetlik stok hedeflendi ve 12-24 ay boyunca satılacak.$productionNote $techComment$colorNote$osNote$trendBonusNote$tierNote$genNote Eleştirmen puanı: $reviewScore/100.",
            category = "Şirket",
            year = currentState.year,
            month = currentState.month
        )

        val report = MarketReport(
            title = "${specs.name} Üretimi Başladı",
            text = "${specs.name} modelinin ${"%,d".format(specs.quantity)} adetlik stok üretimi tamamlandı ($${"%,d".format(totalCost)} harcandı). Eleştirmenler $reviewScore/100 verdi.$colorNote$osNote$trendBonusNote$tierNote$genNote",
            profit = -totalCost,
            unitsSold = 0,
            reviewScore = reviewScore
        )

        _state.update { 
            it.copy(
                budget = it.budget - totalCost,
                reputation = (it.reputation + ((reviewScore - 65) / 12).coerceAtLeast(0)).coerceIn(0, 100),
                modelCount = it.modelCount + 1,
                manufacturedPhones = it.manufacturedPhones + specs,
                activeModels = it.activeModels + newActiveModel,
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
                text = "$techName teknolojisinin araştırması başlatıldı ($${"%,d".format(cost)} harcandı). Mevcut ${currentState.engineers} Ar-Ge mühendisi ile projenin $duration ay sürmesi öngörülüyor.",
                profit = -cost,
                unitsSold = 0,
                reviewScore = 0
            )

            val techNews = NewsArticle(
                id = "news_tech_start_${currentState.year}_${currentState.month}_${Random.nextInt(100, 999)}",
                title = "YENİ AR-GE PROJESİ: $techName",
                text = "Ar-Ge ekibimiz $techName üzerinde çalışmaya başladı. Tahmini tamamlama süresi: $duration Ay.",
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
        if (currentState.budget < campaignType.cost) return

        val model = currentState.activeModels.find { it.id == modelId } ?: return

        val updatedModels = currentState.activeModels.map { m ->
            if (m.id == modelId) {
                m.copy(activeCampaign = ActiveCampaign(campaignType, campaignType.durationMonths))
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
            text = "${model.specs.name} cihazı için $${"%,d".format(campaignType.cost)} bütçe ayrılarak ${campaignType.title} başlatıldı.",
            profit = -campaignType.cost,
            unitsSold = 0,
            reviewScore = model.reviewScore
        )

        _state.update {
            it.copy(
                budget = it.budget - campaignType.cost,
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

        val devCost = type.devCost
        if (currentState.budget < devCost) {
            _state.update { it.copy(noticeMessage = "İşletim sistemi geliştirme için yetersiz bütçe! Gereken: $${"%,d".format(devCost)}") }
            return
        }

        val initialVersion = if (type == currentState.customOs.type && currentState.customOs.name == name) {
            "${currentState.customOs.majorVersionCount + 1}.0"
        } else {
            "1.0"
        }

        val repBonus = when (type) {
            OsType.PROPRIETARY_KERNEL -> 18
            OsType.CUSTOM_UI_SKIN -> 10
            OsType.STOCK_ANDROID -> 0
        }

        val news = NewsArticle(
            id = "os_launch_${currentState.year}_${currentState.month}_${Random.nextInt(100, 999)}",
            title = "🌐 BÜYÜK YAZILIM HAMLESİ: ${name} v$initialVersion (${licenseType.badge})!",
            text = "Şirketimiz ${type.title} ve ${licenseType.title} mimarisine sahip ${name} yazılımını başarıyla geliştirdi! Odak: ${focus.title}. ${focus.bonusDescription} Bu hamle şirketinizin bağımsızlık ve ekosistem gücünü zirveye taşıyor.",
            category = "Şirket",
            year = currentState.year,
            month = currentState.month
        )

        val report = MarketReport(
            title = "Yeni İşletim Sistemi Geliştirildi: $name",
            text = "$name v$initialVersion ($type - ${licenseType.shortLabel}) yazılımı tamamlandı ($${"%,d".format(devCost)} harcandı). Marka İtibarı +$repBonus arttı. Odak: ${focus.title}.",
            profit = -devCost,
            unitsSold = 0,
            reviewScore = 95
        )

        val initialDevs = (currentState.engineers / 2).coerceAtLeast(1).coerceAtMost(currentState.engineers)

        val updatedOs = currentState.customOs.copy(
            name = name,
            version = initialVersion,
            type = type,
            licenseType = licenseType,
            focus = focus,
            themeColorHex = themeColorHex,
            perDeviceLicenseFee = perDeviceLicenseFee,
            assignedDevs = if (currentState.customOs.assignedDevs > 0) currentState.customOs.assignedDevs else initialDevs,
            popularityPercent = if (currentState.customOs.popularityPercent > 0) currentState.customOs.popularityPercent else (if (licenseType == OsLicenseType.OPEN_SOURCE) 3.5f else 2.0f),
            optimizationScore = if (currentState.customOs.optimizationScore > 20) currentState.customOs.optimizationScore else 35,
            ecosystemScore = if (currentState.customOs.ecosystemScore > 10) currentState.customOs.ecosystemScore else 20,
            thirdPartyAdoptersCount = if (currentState.customOs.thirdPartyAdoptersCount > 0) currentState.customOs.thirdPartyAdoptersCount else (if (licenseType == OsLicenseType.OPEN_SOURCE) 1 else 0),
            majorVersionCount = if (initialVersion == "1.0") 1 else currentState.customOs.majorVersionCount + 1,
            minorVersionCount = 0,
            lastUpdateMonth = currentState.month,
            lastUpdateYear = currentState.year
        )

        _state.update {
            it.copy(
                budget = it.budget - devCost,
                reputation = (it.reputation + repBonus).coerceIn(0, 100),
                customOs = updatedOs,
                newsList = it.newsList + news,
                reports = it.reports + report,
                noticeMessage = "$name v$initialVersion (${licenseType.badge}) başarıyla faaliyete geçti!"
            )
        }
    }

    fun setAssignedDevs(count: Int) {
        val maxEng = _state.value.engineers
        val validCount = count.coerceIn(0, maxEng)
        _state.update {
            it.copy(
                customOs = it.customOs.copy(assignedDevs = validCount),
                noticeMessage = "Yazılım Ar-Ge ekibine $validCount mühendis görevlendirildi."
            )
        }
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

    fun releaseMajorOsUpdate() {
        val currentState = _state.value
        val updateCost = 150000L
        if (currentState.budget < updateCost) {
            _state.update { it.copy(noticeMessage = "Büyük sürüm güncellemesi için yetersiz bütçe! Gereken: $${"%,d".format(updateCost)}") }
            return
        }

        val nextMajor = currentState.customOs.majorVersionCount + 1
        val newVersion = "$nextMajor.0"

        val news = NewsArticle(
            id = "os_update_${currentState.year}_${currentState.month}_${Random.nextInt(100, 999)}",
            title = "🚀 GÜNCELLEME: ${currentState.customOs.name} v$newVersion Yayınlandı!",
            text = "Kullanıcıların merakla beklediği ${currentState.customOs.name} v$newVersion büyük sistem güncellemesi OTA yoluyla tüm aktif cihazlara dağıtıldı. Performans, pil ve yapay zeka optimizasyonları kullanıcılardan tam not aldı.",
            category = "Teknoloji",
            year = currentState.year,
            month = currentState.month
        )

        val report = MarketReport(
            title = "${currentState.customOs.name} v$newVersion Güncellemesi",
            text = "${currentState.customOs.name} v$newVersion güncellemesi tüm dünyaya sunuldu ($${"%,d".format(updateCost)} Ar-Ge harcandı). Marka İtibarı +5 arttı ve kullanıcı memnuniyeti tazelendi.",
            profit = -updateCost,
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

        _state.update {
            it.copy(
                budget = it.budget - updateCost,
                reputation = (it.reputation + 5).coerceIn(0, 100),
                customOs = updatedOs,
                newsList = it.newsList + news,
                reports = it.reports + report,
                noticeMessage = "${currentState.customOs.name} v$newVersion güncellemesi başarıyla yayınlandı! (+5 İtibar)"
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
        val cost = 5000000L
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

    fun investInDeveloperFund(amount: Long) {
        val currentState = _state.value
        if (currentState.budget < amount) {
            _state.update { it.copy(noticeMessage = "Geliştirici Teşvik Fonu için yetersiz bütçe! Gereken: $${"%,d".format(amount)}") }
            return
        }

        val customOs = currentState.customOs
        val addedApps = (amount / 25000L).coerceAtLeast(1000L)
        val updatedOs = customOs.copy(
            devFundBalance = customOs.devFundBalance + amount,
            totalStoreApps = customOs.totalStoreApps + addedApps,
            ecosystemScore = (customOs.ecosystemScore + (amount / 2000000L).toInt().coerceIn(1, 10)).coerceAtMost(100)
        )

        _state.update {
            it.copy(
                budget = it.budget - amount,
                customOs = updatedOs,
                noticeMessage = "Geliştirici Fonuna $${"%,d".format(amount)} aktarıldı! (+$addedApps yeni uygulama mağazaya katıldı)"
            )
        }
    }

    fun dismissTechExpo() {
        _state.update { it.copy(activeTechExpo = null) }
    }

    val rivalOperatingSystems: List<CompetitorOsInfo> = DEFAULT_RIVAL_OPERATING_SYSTEMS

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
}
