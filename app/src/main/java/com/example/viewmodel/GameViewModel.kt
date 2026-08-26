/**
 * Oyunun ana durum yönetimi ve iş mantığı katmanı.
 *
 * Bu dosya şunları içerir:
 * - [GameState] ve alt veri sınıfları (şirket, telefon modelleri, rakipler, Ar-Ge, yazılım vb.)
 * - [GameViewModel]: aylık simülasyon adımını (satış, pazar, haberler, rakip AI) yürüten,
 *   kayıt/yükleme (Room) işlemlerini yöneten ana ViewModel.
 *
 * Not: Dosya oldukça büyük (3000+ satır) — yeni özellik eklerken ilgili bölümü bulmak için
 * "MARK:"tarzı yorum başlıkları veya IDE'nin yapı (structure) görünümünü kullanmanız önerilir.
 * Uzun vadede bu dosyayı sorumluluk alanına göre (SaveRepository, MarketSimulation,
 * CompetitorAI gibi) ayrı dosyalara bölmek okunabilirliği artırır.
 */
package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.AiGameService
import com.example.data.AppDatabase
import com.example.data.GameSaveEntity
import com.example.data.GameSaveRepository
import com.example.model.BenchmarkScore
import com.example.util.BenchmarkCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.random.Random

@Serializable
enum class EmployeeType {
    ENGINEER,       // Mühendis ($8,000/ay): Teknolojik yıpranmayı düşürür ve taban kaliteyi artırır.
    QA_INSPECTOR,   // QA Uzmanı ($5,000/ay): Cihaz eleştirmen ve test puanına doğrudan +2 puan ekler.
    ASSEMBLY_WORKER // Üretim İşçisi ($3,000/ay): Birim cihaz üretim maliyetini düşürür (%25'e kadar).
}

@Serializable
enum class CampaignType(
    val title: String,
    val cost: Long,
    val durationMonths: Int,
    val boostPercent: Int,
    val description: String
) {
    SOCIAL_MEDIA("Sosyal Medya Reklamı", 50000L, 3, 25, "3 Ay boyunca +%25 satış talebi artışı"),
    INFLUENCER("Influencer İşbirliği", 150000L, 3, 50, "3 Ay boyunca +%50 satış talebi artışı"),
    TV_COMMERCIAL("TV Reklam Kampanyası", 400000L, 6, 80, "6 Ay boyunca +%80 satış talebi artışı")
}

@Serializable
data class ActiveCampaign(
    val type: CampaignType,
    val remainingMonths: Int
)

@Serializable
enum class OsType(
    val title: String,
    val subtitle: String,
    val devCost: Long,
    val monthlyMaintenance: Long,
    val reviewBonus: Int,
    val storeRevenuePerUser: Float, // Dolar / aktif kullanıcı / ay
    val description: String
) {
    STOCK_ANDROID(
        "Saf Açık Kaynak OS (Stok)",
        "Hazır Açık Kaynak Çekirdek",
        0L,
        0L,
        0,
        0.0f,
        "Sıfır geliştirme ve bakım maliyeti. Özel marka kimliği veya mağaza komisyon geliri sağlamaz."
    ),
    CUSTOM_UI_SKIN(
        "Özel Şirket Arayüzü (UI Skin)",
        "Özelleştirilmiş Kullanıcı Deneyimi",
        750000L,
        40000L,
        10,
        1.20f,
        "Kendi tema motoru, özel widget'lar ve entegre uygulama mağazası. İnceleme puanına +10 ekler ve aktif kullanıcılardan aylık mağaza komisyon geliri sağlar."
    ),
    PROPRIETARY_KERNEL(
        "Bağımsız Çekirdek İşletim Sistemi (Proprietary OS)",
        "Sıfırdan Özel Çekirdek & Ekosistem",
        3500000L,
        120000L,
        20,
        3.00f,
        "Donanımla %100 kusursuz entegrasyon, ultra akıcı optimizasyon, +20 inceleme puanı ve cihaz başına rekor mağaza/servis geliri!"
    )
}

@Serializable
enum class OsFocus(
    val title: String,
    val icon: String,
    val bonusDescription: String,
    val strategicAdvantage: String
) {
    SECURITY(
        "Güvenlik & Gizlilik Odaklı",
        "🛡️",
        "Uçtan uca donanımsal şifreleme ve kurumsal veri kalkanı.",
        "İş & Kurumsal segmentinde +%25 Satış Talebi ve İtibar Bonusu"
    ),
    AI_SMART(
        "Yapay Zeka & Akıllı Asistan",
        "🧠",
        "Cihaz üstü yerel LLM asistanı ve akıllı hesaplamalı kamera motoru.",
        "Kamera puanına +15 ve Trend uyumunda dev avantaj"
    ),
    GAMING_TURBO(
        "Oyun & Turbo Performans",
        "🎮",
        "Düşük gecikmeli GPU optimizasyonu ve gelişmiş termal yönetim.",
        "Oyuncu telefonlarında +%30 Satış ve Hız Bonusu"
    ),
    LIGHTWEIGHT(
        "Hafiflik & Pil Optimizasyonu",
        "⚡",
        "Sıfır bloatware, akıcı 120Hz animasyonlar ve enerji tasarrufu.",
        "Batarya puanına +15 ve tüm segmentlerde geniş memnuniyet"
    ),
    AESTHETIC(
        "Estetik & Zengin Özelleştirme",
        "🎨",
        "Dinamik temalar, saydam cam efektleri ve widget ekosistemi.",
        "Tasarım puanına +12 ve genç kitlede popülerlik"
    )
}

@Serializable
enum class UpdateGuarantee(
    val years: Int,
    val title: String,
    val monthlyCost: Long,
    val reputationBonus: Int
) {
    ONE_YEAR(1, "1 Yıl Temel Destek", 0L, 0),
    THREE_YEARS(3, "3 Yıl Düzenli Güncelleme", 30000L, 5),
    FIVE_YEARS(5, "5 Yıl Uzun Ömür Garantisi", 80000L, 12),
    SEVEN_YEARS(7, "7 Yıl Lider Amiral Gemisi Desteği", 150000L, 20)
}

@Serializable
enum class OsLicenseType(
    val title: String, 
    val badge: String, 
    val shortLabel: String,
    val description: String,
    val adoptionSpeedMultiplier: Float,
    val storeRevenueMultiplier: Float,
    val priceTolerationBonusPercent: Int
) {
    OPEN_SOURCE(
        "Açık Kaynak (Open-Source / OEM Serbest)", 
        "🌐 Açık Kaynak",
        "Açık Kaynak",
        "Topluluk ve diğer tüm telefon üreticileri (OEM) işletim sisteminizi serbestçe kullanabilir. Pazar popülaritesi ve aktif cihaz sayısı hızla yayılır; devasa küresel App Store ekosistemi ve servis geliri üretir.",
        2.4f, 
        1.2f,
        0
    ),
    CLOSED_PROPRIETARY(
        "Kapalı Kaynak (Tescilli / Lisanslı Mülkiyet)", 
        "🔒 Kapalı / Lisanslı",
        "Kapalı Kaynak",
        "Apple iOS ve Windows Mobile benzeri kapalı ekosistem. Şirketinizin amiral gemisi cihazlarına özel prestij, yüksek satış fiyatı toleransı ve diğer firmalara cihaz başı lisans satış geliri sağlar.",
        0.75f, 
        2.2f,
        15
    )
}

@Serializable
enum class StoreCommissionRate(val percent: Int, val label: String, val marketLoyaltyBonus: Float) {
    DEVELOPER_FRIENDLY(15, "%15 Geliştirici Dostu (Yüksek Büyüme)", 1.25f),
    BALANCED(20, "%20 Dengeli Pazar Standardı", 1.0f),
    MAXIMUM_PROFIT(30, "%30 Maksimum Kâr Marjı", 0.85f)
}

@Serializable
enum class OsModuleType(
    val id: String,
    val title: String,
    val icon: String,
    val summary: String,
    val impactText: String,
    val baseCost: Long,
    val maxLevel: Int = 5
) {
    KERNEL_ENGINE(
        "kernel",
        "Çekirdek & 144Hz Akıcılık",
        "⚡",
        "Düşük gecikmeli grafik işleme ve mikroçekirdek bellek mimarisi.",
        "+15 İnceleme Puanı & Kusursuz Cihaz Akıcılığı",
        6000000L
    ),
    AI_NEURAL(
        "ai",
        "Yerel Nöral Yapay Zeka",
        "🧠",
        "Cihaz üstü LLM yapay zeka asistanı ve hesaplamalı kamera motoru.",
        "+20 İnovasyon Bonusu & Trend Eşleşmesinde +%25 Satış",
        10000000L
    ),
    SECURITY_VAULT(
        "security",
        "Donanımsal Güvenlik Kasası",
        "🛡️",
        "Kriptografik veri yalıtımı, sandbox ve sıfır gün kalkanı.",
        "İş & Kurumsal Pazarda +%35 Talep Patlaması",
        5000000L
    ),
    CLOUD_SYNC(
        "cloud",
        "Bulut & Çoklu Cihaz Ekosistemi",
        "☁️",
        "Masaüstü, tablet ve saatler arası anlık kesintisiz senkronizasyon.",
        "%95 Müşteri Sadakati & Aylık Düzenli Bulut Geliri",
        8000000L
    ),
    APP_STORE_SDK(
        "store",
        "App Store & Geliştirici SDK",
        "🏪",
        "Gelişmiş API setleri, grafik kütüphaneleri ve küresel mağaza altyapısı.",
        "Uygulama Kataloğunu Katlar & Komisyon Gelirlerini %50 Artırır",
        7000000L
    )
}

@Serializable
data class CompetitorOsInfo(
    val id: String,
    val name: String,
    val company: String,
    val iconEmoji: String,
    val licenseTypeBadge: String,
    val marketSharePercent: Float,
    val techScore: Int,
    val ecosystemScore: Int,
    val userBaseFormatted: String,
    val appCountFormatted: String,
    val monthlyEcosystemRevenue: String,
    val coreStrength: String,
    val mainFlaw: String,
    val brandColorHex: Long
)

@Serializable
enum class ModelTier(
    val title: String, 
    val badge: String, 
    val priceMultiplier: Float, 
    val costMultiplier: Float,
    val reviewBonus: Int, 
    val description: String
) {
    STANDARD("Standart", "📱", 1.0f, 1.0f, 0, "Dengeli ana akım model. Fiyat/performans kitlesine hitap eder."),
    PRO("Pro", "💎", 1.35f, 1.0f, 0, "Gelişmiş amiral gemisi. Yüksek marj ve teknoloji meraklıları için üstün donanım."),
    ULTRA("Ultra / Pro Max", "👑", 1.75f, 1.0f, 0, "En üst seviye amiral gemisi. Zirve prestij, en yüksek fiyat tavanı ve talep çekiciliği."),
    LITE("Lite / SE", "🏷️", 0.75f, 1.0f, 0, "Bütçe dostu giriş seviyesi. Düşük fiyat, yüksek satış hacmi.")
}

@Serializable
data class CustomOsState(
    val name: String = "Stok Açık Kaynak Android",
    val version: String = "1.0",
    val type: OsType = OsType.STOCK_ANDROID,
    val licenseType: OsLicenseType = OsLicenseType.OPEN_SOURCE,
    val focus: OsFocus = OsFocus.LIGHTWEIGHT,
    val updateGuarantee: UpdateGuarantee = UpdateGuarantee.ONE_YEAR,
    val commissionRate: StoreCommissionRate = StoreCommissionRate.BALANCED,
    val themeColorHex: Long = 0xFF0284C7,
    val majorVersionCount: Int = 1,
    val minorVersionCount: Int = 0,
    val lastUpdateMonth: Int = 1,
    val lastUpdateYear: Int = 2010,
    val assignedDevs: Int = 0,
    val popularityPercent: Float = 0.0f,
    val ecosystemScore: Int = 10,
    val optimizationScore: Int = 20,
    val thirdPartyAdoptersCount: Int = 0,
    val thirdPartyActiveDevices: Long = 0L,
    val perDeviceLicenseFee: Int = 0,
    val totalAppStoreRevenueToDate: Long = 0L,
    val totalLicenseRevenueToDate: Long = 0L,
    val lastMonthAppStoreIncome: Long = 0L,
    val lastMonthLicenseIncome: Long = 0L,
    val devXp: Int = 0,
    // New OS Architecture & Strategy Levels
    val kernelLevel: Int = 1,
    val aiLevel: Int = 1,
    val securityLevel: Int = 1,
    val cloudLevel: Int = 1,
    val appStoreLevel: Int = 1,
    val devFundBalance: Long = 0L,
    val totalStoreApps: Long = 45000L,
    val customerLoyaltyPercent: Float = 35.0f,
    val devConCount: Int = 0,
    val lastMonthCloudRevenue: Long = 0L,
    val totalCloudRevenueToDate: Long = 0L
) {
    val isCustomActive: Boolean get() = type != OsType.STOCK_ANDROID
    val overallTechScore: Int get() = (optimizationScore * 0.4f + (kernelLevel + aiLevel + securityLevel + cloudLevel + appStoreLevel) * 4f).toInt().coerceIn(10, 100)
}

@Serializable
data class PhoneSpecs(
    val name: String,
    val seriesName: String = "",
    val generation: Int = 1,
    val tier: ModelTier = ModelTier.STANDARD,
    val style: String,
    val material: String,
    val processor: String,
    val ramCapacity: String = "1 GB",
    val ramType: String = "LPDDR1",
    val storage: String = "16 GB",
    val sdCardSupport: String = "MicroSD (32 GB)",
    val display: String,
    val camera: String,
    val batteryCapacity: String,
    val batteryType: String,
    val connectivity: String,
    val cellularNetwork: String = "3G HSPA+",
    val chargingPort: String = "Micro-USB",
    val wirelessConnectivity: String = "Wi-Fi 4 & BT 2.1",
    val audio: String,
    val glass: String,
    val price: Int,
    val quantity: Int,
    val qaBudget: Long = 0,
    val techScore: Int = 2010,
    val matchesTrend: Boolean = false,
    val selectedColors: List<String> = listOf("Gece Siyahı"),
    val colorHexes: List<Long> = listOf(0xFF0F172A),
    val colorName: String = "Gece Siyahı",
    val colorHex: Long = 0xFF0F172A,
    val frameStyle: String = "Düz Metal Kenar",
    val cameraBumpStyle: String = "Dikey Ada",
    val backFinish: String = "Buzlu Mat Cam",
    val notchStyle: String = "Nokta Delik",
    val logoStyle: String = "Minimal Elmas",
    val osName: String = "Android AOSP (Stok Açık Kaynak)",
    val osType: String = "Açık Kaynak",
    val osFocus: String = "Standart",
    val osLicenseFee: Int = 0,
    val unitCost: Int = 50
) {
    val ram: String get() = "$ramCapacity $ramType"
}

@Serializable
data class ActiveModel(
    val id: String,
    val specs: PhoneSpecs,
    val totalStock: Int,
    val remainingStock: Int,
    val totalSold: Int = 0,
    val totalRevenue: Long = 0,
    val monthsOnMarket: Int = 0,
    val reviewScore: Int,
    val launchYear: Int,
    val launchMonth: Int,
    val isExtendedNewsSent: Boolean = false,
    val activeCampaign: ActiveCampaign? = null,
    val matchesTrend: Boolean = false,
    val benchmarkScore: BenchmarkScore? = null
) {
    val maxMonthsOnMarket: Int
        get() = if (reviewScore >= 60) 24 else 12

    val isCompleted: Boolean
        get() = remainingStock <= 0 || monthsOnMarket >= maxMonthsOnMarket
}

@Serializable
enum class TrendCategory(val title: String, val icon: String, val tip: String) {
    HIGH_REFRESH_DISPLAY("Yüksek Yenileme Hızlı Ekran", "⚡", "120Hz/144Hz/240Hz ekran veya Oyuncu tarzı kullanın."),
    CAMERA_PRO("Gelişmiş Kamera & Özçekim", "📸", "Çift, Üçlü veya Periskop/200MP kamera seçin."),
    LONG_BATTERY("Yüksek Kapasiteli Batarya", "🔋", "4500mAh+ kapasite veya Katı Hal batarya seçin."),
    BUDGET_VALUE("Ekonomik Fiyat / Performans", "🏷️", "Cihaz satış fiyatını $400 veya altına ayarlayın."),
    PREMIUM_BUILD("Titanyum & Cam Premium Kasa", "💎", "Titanyum veya Cam kasa malzemesi kullanın."),
    AI_PROCESSOR("Yapay Zeka & Güçlü İşlemci", "🧠", "Qualcomm Gen / In-House / Kuantum çip veya 12GB+ RAM seçin."),
    FAST_CONNECTIVITY("5G & Hızlı Bağlantı", "📶", "5G, Wi-Fi 6E/7 veya Uydu bağlantısı seçin.")
}

@Serializable
data class MarketTrend(
    val id: String,
    val title: String,
    val description: String,
    val category: TrendCategory,
    val bonusMultiplier: Float = 1.5f,
    val remainingMonths: Int = 4,
    val totalDurationMonths: Int = 4
)

@Serializable
data class CompetitorCompany(
    val id: String,
    val name: String, // "Armut", "Samsong", "Xiaomeme", "Gugıl"
    val logoEmoji: String,
    val slogan: String,
    val brandColorHex: Long,
    val marketSharePercent: Float,
    val monthlySales: Int,
    val currentTopModel: String,
    val currentModelPrice: Int,
    val currentModelScore: Int,
    val strategyType: String,
    val strengthText: String,
    val weaknessText: String
)

@Serializable
data class CompetitorReleaseHistory(
    val id: String,
    val companyName: String,
    val logoEmoji: String,
    val modelName: String,
    val price: Int,
    val score: Int,
    val year: Int,
    val month: Int,
    val headline: String
)

@Serializable
data class NewsArticle(
    val id: String,
    val title: String,
    val text: String,
    val category: String, // "Sektör", "Teknoloji", "Pazar", "Şirket"
    val year: Int,
    val month: Int,
    val isAiGenerated: Boolean = false,
    val reviewerQuote: String? = null
)

@Serializable
data class MarketReport(
    val title: String,
    val text: String,
    val profit: Long,
    val unitsSold: Int,
    val reviewScore: Int,
    val aiReviewQuote: String? = null,
    val isAiGenerated: Boolean = false
)

@Serializable
data class OfficeTier(
    val level: Int,
    val name: String,
    val maxEmployees: Int,
    val monthlyRent: Long,
    val upgradeCost: Long
)

val OFFICE_TIERS = listOf(
    OfficeTier(1, "Başlangıç Ofisi", 25, 25000L, 0L),
    OfficeTier(2, "İş Merkezi Ofisi", 50, 65000L, 500000L),
    OfficeTier(3, "Teknoloji Plazası", 100, 180000L, 2500000L),
    OfficeTier(4, "Akıllı Gökdelen Kampüsü", 500, 600000L, 12000000L)
)

@Serializable
data class FactoryTier(
    val level: Int,
    val name: String,
    val maxWorkers: Int,
    val discountPercent: Float,
    val monthlyMaintenance: Long,
    val upgradeCost: Long
)

val FACTORY_TIERS = listOf(
    FactoryTier(0, "Atölye (Fason Üretim)", 15, 0f, 10000L, 0L),
    FactoryTier(1, "Küçük Ölçekli Fabrika", 40, 10f, 50000L, 1000000L),
    FactoryTier(2, "Otomatik Seri Üretim Fabrikası", 100, 20f, 180000L, 5000000L),
    FactoryTier(3, "Mega Akıllı Robotik Fabrika", 300, 35f, 500000L, 20000000L)
)

@Serializable
data class ActiveResearch(
    val techId: String,
    val techName: String,
    val totalMonths: Int,
    val remainingMonths: Int,
    val cost: Long
)

@Serializable
data class GameState(
    val reports: List<MarketReport> = emptyList(),
    val budget: Long = 4500000,
    val monthlyIncome: Long = 0,
    val rdSpending: Long = 0,
    val reputation: Int = 0,
    val year: Int = 2010,
    val month: Int = 1,
    val modelCount: Int = 0,
    val techLevel: String = "Giriş",
    val manufacturedPhones: List<PhoneSpecs> = emptyList(),
    val activeModels: List<ActiveModel> = emptyList(),
    val unlockedTech: List<String> = emptyList(),
    val activeResearch: ActiveResearch? = null,
    val researchQueue: List<ActiveResearch> = emptyList(),
    val newsList: List<NewsArticle> = emptyList(),
    val officeLevel: Int = 1,
    val factoryLevel: Int = 0,
    val engineers: Int = 3,
    val qaInspectors: Int = 2,
    val assemblyWorkers: Int = 15,
    val noticeMessage: String? = null,
    val companyName: String = "Apex Mobile",
    val companyLogoId: String = "ic_logo_diamond",
    val companyLogoStyle: String = "Minimal Elmas",
    val companyBrandColorHex: Long = 0xFF2563EB,
    val companySlogan: String = "Geleceğin Akıllı Telefonları",
    val isCompanySetupDone: Boolean = false,
    val currentTrend: MarketTrend = MarketTrend(
        id = "trend_2010_init",
        title = "Dokunmatik & Metal Şıklığı Trendi",
        description = "Tüketiciler tuşlu telefonları terk edip şık dokunmatik ekranlara ve kaliteli gövdelere yöneliyor.",
        category = TrendCategory.PREMIUM_BUILD,
        bonusMultiplier = 1.15f,
        remainingMonths = 4,
        totalDurationMonths = 4
    ),
    val competitors: List<CompetitorCompany> = listOf(
        CompetitorCompany(
            id = "comp_samsung",
            name = "Samsung",
            logoEmoji = "🌌",
            slogan = "Geleceği Bugünden Şekillendir",
            brandColorHex = 0xFF1428A0,
            marketSharePercent = 21.5f,
            monthlySales = 172000,
            currentTopModel = "Galaxy S",
            currentModelPrice = 599,
            currentModelScore = 85,
            strategyType = "Global Lider & Geniş Portföy",
            strengthText = "Sektör lideri AMOLED ekran teknolojisi, katlanabilir ekranlar ve devasa üretim gücü",
            weaknessText = "Geniş model yelpazesi nedeniyle hızlı değer kaybı"
        ),
        CompetitorCompany(
            id = "comp_apple",
            name = "Apple",
            logoEmoji = "🍎",
            slogan = "Farklı Düşün (Think Different)",
            brandColorHex = 0xFF0F172A,
            marketSharePercent = 19.5f,
            monthlySales = 156000,
            currentTopModel = "iPhone 4",
            currentModelPrice = 699,
            currentModelScore = 89,
            strategyType = "Ultra Premium & Kapalı iOS Ekosistemi",
            strengthText = "Kusursuz donanım-yazılım uyumu, Bionic çipler ve rekor kâr marjı",
            weaknessText = "Yüksek fiyatlandırma ve kapalı ekosistem kısıtlamaları"
        ),
        CompetitorCompany(
            id = "comp_xiaomi",
            name = "Xiaomi",
            logoEmoji = "🟠",
            slogan = "Herkes İçin İnovasyon",
            brandColorHex = 0xFFFF6900,
            marketSharePercent = 13.0f,
            monthlySales = 104000,
            currentTopModel = "Mi 1",
            currentModelPrice = 249,
            currentModelScore = 79,
            strategyType = "Fiyat / Performans & Ekosistem Devi",
            strengthText = "Agresif fiyatlandırma, zengin donanım, HyperOS ekosistemi ve 120W+ hızlı şarj",
            weaknessText = "Giriş segmentinde daha düşük birim kâr marjı"
        ),
        CompetitorCompany(
            id = "comp_oppo",
            name = "Oppo",
            logoEmoji = "🟢",
            slogan = "İlham Veren Teknoloji (Inspiration Ahead)",
            brandColorHex = 0xFF008A4B,
            marketSharePercent = 8.5f,
            monthlySales = 68000,
            currentTopModel = "Find X",
            currentModelPrice = 549,
            currentModelScore = 83,
            strategyType = "Gelişmiş Kamera & SuperVOOC Şarj",
            strengthText = "MariSilicon görüntüleme işlemcisi, Hasselblad ortaklığı ve ultra şık tasarım",
            weaknessText = "Batı pazarlarında dalgalı patent süreçleri"
        ),
        CompetitorCompany(
            id = "comp_vivo",
            name = "Vivo",
            logoEmoji = "🔷",
            slogan = "Kusursuz Çekim, Sınırsız İnovasyon",
            brandColorHex = 0xFF0057FF,
            marketSharePercent = 7.5f,
            monthlySales = 60000,
            currentTopModel = "Xplay",
            currentModelPrice = 499,
            currentModelScore = 82,
            strategyType = "Zeiss Optik Kamera & Sektör İlkleri",
            strengthText = "Ekran içi parmak izinde öncülük, Zeiss T* kaplama lensler ve V serisi ISP çipleri",
            weaknessText = "Küresel yazılım arayüzü dağıtımında bölgesel farklılıklar"
        ),
        CompetitorCompany(
            id = "comp_huawei",
            name = "Huawei",
            logoEmoji = "🌸",
            slogan = "Geleceği Yeniden İnşa Et",
            brandColorHex = 0xFFCF0A2C,
            marketSharePercent = 5.5f,
            monthlySales = 44000,
            currentTopModel = "Ascend P1",
            currentModelPrice = 449,
            currentModelScore = 84,
            strategyType = "HarmonyOS Ekosistemi & Bağımsız Güç",
            strengthText = "Kendi Kirin çipleri, XMAGE mobil fotoğrafçılık liderliği ve inanılmaz AR-GE direnci",
            weaknessText = "Bazı Batı pazarlarında lisans ve 5G tedarik ambargoları"
        ),
        CompetitorCompany(
            id = "comp_google",
            name = "Google",
            logoEmoji = "🌐",
            slogan = "Saf Android & Gemini AI Gücü",
            brandColorHex = 0xFF4285F4,
            marketSharePercent = 3.5f,
            monthlySales = 28000,
            currentTopModel = "Nexus One",
            currentModelPrice = 529,
            currentModelScore = 83,
            strategyType = "Yapay Zeka & Saf Yazılım Deneyimi",
            strengthText = "Gemini AI entegrasyonu, anında Android güncellemeleri ve hesaplamalı fotoğrafçılık",
            weaknessText = "Sınırlı sayıda ülkede resmi satış ve donanım çeşitliliği"
        ),
        CompetitorCompany(
            id = "comp_motorola",
            name = "Motorola",
            logoEmoji = "🦇",
            slogan = "Merhaba Moto (Hello Moto)",
            brandColorHex = 0xFF001489,
            marketSharePercent = 3.2f,
            monthlySales = 25600,
            currentTopModel = "Droid Razr",
            currentModelPrice = 399,
            currentModelScore = 80,
            strategyType = "Razr Katlanabilir & Temiz Android Deneyimi",
            strengthText = "Razr serisi ikonik katlanabilir dış ekranlar, güçlü marka mirası ve uygun fiyatlı modeller",
            weaknessText = "Yazılım güncelleme sıklığı ve uzun vadeli destek"
        ),
        CompetitorCompany(
            id = "comp_oneplus",
            name = "OnePlus",
            logoEmoji = "➕",
            slogan = "Asla Yetinme (Never Settle)",
            brandColorHex = 0xFFEB0028,
            marketSharePercent = 2.8f,
            monthlySales = 22400,
            currentTopModel = "OnePlus One",
            currentModelPrice = 299,
            currentModelScore = 86,
            strategyType = "Akıcı Ekran, Hızlı Şarj & Amiral Gemisi Gücü",
            strengthText = "OxygenOS akıcılığı, Hasselblad renk kalibrasyonu ve 100W+ SUPERVOOC şarj",
            weaknessText = "Fiyatların zamanla premium seviyeye yaklaşmasıyla eski F/P algısının değişmesi"
        ),
        CompetitorCompany(
            id = "comp_realme",
            name = "Realme",
            logoEmoji = "⚡",
            slogan = "Cesaret Et (Dare to Leap)",
            brandColorHex = 0xFFFFC915,
            marketSharePercent = 2.6f,
            monthlySales = 20800,
            currentTopModel = "Realme 1",
            currentModelPrice = 199,
            currentModelScore = 77,
            strategyType = "Genç Nesil & Trend Yaratan Tasarımlar",
            strengthText = "Çok hızlı üretim döngüsü, 240W rekor şarj hızları ve şık cesur tasarımlar",
            weaknessText = "Pazarda çok sayıda benzer model bulunması"
        ),
        CompetitorCompany(
            id = "comp_honor",
            name = "Honor",
            logoEmoji = "💫",
            slogan = "Sihri Keşfet (Go Beyond)",
            brandColorHex = 0xFF00A3E0,
            marketSharePercent = 2.4f,
            monthlySales = 19200,
            currentTopModel = "Honor 6",
            currentModelPrice = 349,
            currentModelScore = 80,
            strategyType = "Ultra İnce Katlanabilir & MagicOS",
            strengthText = "Dünyanın en ince katlanabilir (Magic V) gövdeleri, göz koruma PWM ekran teknolojisi",
            weaknessText = "Eski Huawei mirasını bağımsız kimliğe tam dönüştürme süreci"
        ),
        CompetitorCompany(
            id = "comp_sony",
            name = "Sony",
            logoEmoji = "📷",
            slogan = "Duyguları Harekete Geçir (Make.Believe)",
            brandColorHex = 0xFF1F2937,
            marketSharePercent = 1.8f,
            monthlySales = 14400,
            currentTopModel = "Xperia Arc",
            currentModelPrice = 599,
            currentModelScore = 82,
            strategyType = "Kamera Sensör Teknolojisi & Pro İçerik",
            strengthText = "Tüm sektöre Exmor-T kamera sensörü sağlama, 4K OLED ekranlar, 3.5mm jak ve MicroSD sadakati",
            weaknessText = "Yüksek fiyatlandırma ve sınırlı pazarlama bütçesi"
        ),
        CompetitorCompany(
            id = "comp_asus",
            name = "Asus",
            logoEmoji = "🎮",
            slogan = "Oyuncuların Cumhuriyeti (ROG)",
            brandColorHex = 0xFFFF0033,
            marketSharePercent = 1.5f,
            monthlySales = 12000,
            currentTopModel = "PadFone",
            currentModelPrice = 649,
            currentModelScore = 81,
            strategyType = "ROG Ultra Oyuncu Donanımı & Aktif Soğutma",
            strengthText = "Hava tetikleyicileri (AirTrigger), aktif soğutucu fanlar, 165Hz+ ekranlar ve 6000mAh batarya",
            weaknessText = "Oyun odaklı kalın gövde ve niş tüketici kitlesi"
        ),
        CompetitorCompany(
            id = "comp_nokia",
            name = "Nokia",
            logoEmoji = "🏛️",
            slogan = "İnsanları Birbirine Bağlar (Connecting People)",
            brandColorHex = 0xFF124191,
            marketSharePercent = 1.5f,
            monthlySales = 12000,
            currentTopModel = "N8 Symbian",
            currentModelPrice = 429,
            currentModelScore = 78,
            strategyType = "Miras, Dayanıklılık & Kolay Tamir",
            strengthText = "HMD QuickFix evde tamir edilebilirlik, saf Android ve efsanevi gövde sağlamlığı",
            weaknessText = "Üst segment amiral gemisi yarışından çekilmiş olması"
        ),
        CompetitorCompany(
            id = "comp_tecno",
            name = "Tecno",
            logoEmoji = "🌍",
            slogan = "Geleceğe Adım At (Stop At Nothing)",
            brandColorHex = 0xFF0072CE,
            marketSharePercent = 1.8f,
            monthlySales = 14400,
            currentTopModel = "Phantom 6 Plus",
            currentModelPrice = 149,
            currentModelScore = 73,
            strategyType = "Gelişmekte Olan Pazarlar & Phantom Serisi",
            strengthText = "Yerel pazar ten rengi kalibrasyon algoritmaları, agresif uygun fiyat ve Phantom katlanabilir modeller",
            weaknessText = "Gelişmiş Batı pazarlarında düşük marka bilinirliği"
        ),
        CompetitorCompany(
            id = "comp_infinix",
            name = "Infinix",
            logoEmoji = "🚀",
            slogan = "Gelecek Şimdi (The Future is Now)",
            brandColorHex = 0xFF1E824C,
            marketSharePercent = 1.5f,
            monthlySales = 12000,
            currentTopModel = "Infinix Zero",
            currentModelPrice = 169,
            currentModelScore = 74,
            strategyType = "Bütçe Dostu Oyuncu & 260W Hızlı Şarj",
            strengthText = "Gençlere yönelik GT serisi mecha oyun tasarımları, dev bataryalar ve uygun fiyat",
            weaknessText = "İnceleme puanlarında kamera tutarlılığı"
        ),
        CompetitorCompany(
            id = "comp_nothing",
            name = "Nothing",
            logoEmoji = "💡",
            slogan = "Teknolojiyi Yeniden Heyecanlı Kıl",
            brandColorHex = 0xFF18181B,
            marketSharePercent = 1.0f,
            monthlySales = 8000,
            currentTopModel = "Concept 1",
            currentModelPrice = 449,
            currentModelScore = 85,
            strategyType = "Şeffaf Glyph Işıklandırma & Minimalist OS",
            strengthText = "Benzersiz şeffaf arka kapak LED ışıkları, sade nokta-matris tasarımı ve yüksek tasarım cazibesi",
            weaknessText = "Yeni bir marka olarak sınırlı servis ağı"
        ),
        CompetitorCompany(
            id = "comp_zte",
            name = "ZTE",
            logoEmoji = "🔮",
            slogan = "Yarın Bugünden Başlar",
            brandColorHex = 0xFF008CD6,
            marketSharePercent = 1.2f,
            monthlySales = 9600,
            currentTopModel = "Blade V",
            currentModelPrice = 229,
            currentModelScore = 76,
            strategyType = "Görünmez Ekran Altı Kamera & RedMagic Canavarları",
            strengthText = "Gerçek tam ekran (çentiksiz ekran altı kamera), dahili RGB fanlı RedMagic oyun serisi",
            weaknessText = "Yazılım arayüzünün küresel yerelleştirme derinliği"
        ),
        CompetitorCompany(
            id = "comp_tcl",
            name = "TCL",
            logoEmoji = "📺",
            slogan = "Görsel Büyüklüğe İlham Ver",
            brandColorHex = 0xFFED1C24,
            marketSharePercent = 1.2f,
            monthlySales = 9600,
            currentTopModel = "TCL Idol",
            currentModelPrice = 199,
            currentModelScore = 75,
            strategyType = "NXTPAPER Kağıt Hissiyatlı Ekranlar & Uygun Fiyat",
            strengthText = "Kendi CSOT panel üretimi, göz yormayan mat NXTPAPER ekran teknolojisi",
            weaknessText = "İşlemci ve kamera performansında orta segmentte kalması"
        ),
        CompetitorCompany(
            id = "comp_fairphone",
            name = "Fairphone",
            logoEmoji = "🌱",
            slogan = "Daha Adil Bir Akıllı Telefon",
            brandColorHex = 0xFF0084A8,
            marketSharePercent = 0.5f,
            monthlySales = 4000,
            currentTopModel = "Fairphone 1 Modüler",
            currentModelPrice = 499,
            currentModelScore = 80,
            strategyType = "%100 Modüler & 8 Yıl Yazılım Desteği",
            strengthText = "Tornavidayla 2 dakikada değişen parçalar, geri dönüştürülmüş adil materyaller ve 8 yıl güncelleme",
            weaknessText = "Modüler yapı nedeniyle daha kalın gövde ve mütevazı donanım özellikleri"
        )
    ),
    val competitorReleases: List<CompetitorReleaseHistory> = emptyList(),
    val playerMarketSharePercent: Float = 0f,
    val totalMarketMonthlyVolume: Int = 800000,
    val customOs: CustomOsState = CustomOsState(),
    val activeTechExpo: TechExpoEvent? = null,
    val pastTechExpos: List<TechExpoEvent> = emptyList()
) {
    val currentOfficeTier: OfficeTier
        get() = OFFICE_TIERS.firstOrNull { it.level == officeLevel } ?: OFFICE_TIERS.first()

    val currentFactoryTier: FactoryTier
        get() = FACTORY_TIERS.firstOrNull { it.level == factoryLevel } ?: FACTORY_TIERS.first()

    val totalEmployees: Int
        get() = engineers + qaInspectors + assemblyWorkers

    val maxEmployees: Int
        get() = currentOfficeTier.maxEmployees

    val engineerSalary: Long get() = engineers * 8000L
    val qaSalary: Long get() = qaInspectors * 5000L
    val workerSalary: Long get() = assemblyWorkers * 3000L
    val totalSalaries: Long get() = engineerSalary + qaSalary + workerSalary

    val officeExpense: Long get() = currentOfficeTier.monthlyRent
    val factoryMaintenance: Long get() = currentFactoryTier.monthlyMaintenance
    val osMaintenanceExpense: Long 
        get() = (if (customOs.type != OsType.STOCK_ANDROID) customOs.type.monthlyMaintenance else 0L) + customOs.updateGuarantee.monthlyCost

    val totalMonthlyExpenses: Long get() = totalSalaries + officeExpense + factoryMaintenance + osMaintenanceExpense

    val activeUserBase: Int 
        get() = activeModels.sumOf { it.totalSold }

    val workerDiscountPercent: Float
        get() = (assemblyWorkers * 0.25f).coerceAtMost(15f)

    val unitCostDiscountPercent: Float
        get() = (workerDiscountPercent + currentFactoryTier.discountPercent).coerceAtMost(50f)

    val qaScoreBonus: Int
        get() = qaInspectors * 2

    val engineerTechBonus: Int
        get() = engineers * 2
}

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

    private fun getHistoricalNewsForYearMonth(year: Int, month: Int): List<NewsArticle> {
        val list = mutableListOf<NewsArticle>()
        
        when {
            year == 2010 && month == 1 -> list.add(NewsArticle("hist_2010_1", "Akıllı Telefon Çağı Başlıyor", "Dokunmatik ekranlı akıllı cihazlar geleneksel tuşlu telefonların pazar payını hızla ele geçiriyor.", "Teknoloji", 2010, 1))
            year == 2010 && month == 6 -> list.add(NewsArticle("hist_2010_6", "Mobil Uygulama Ekosistemleri Genişliyor", "Kullanıcılar cihaz tercihlerinde uygulama desteğine ve ekran kalitesine önem vermeye başladı.", "Pazar", 2010, 6))
            year == 2011 && month == 1 -> list.add(NewsArticle("hist_2011_1", "Çift Çekirdekli İşlemci Çağı", "Mobil oyunlar ve HD videolar akıllı telefonlarda standart hale geliyor. Yüksek RAM kapasitesi aranıyor.", "Teknoloji", 2011, 1))
            year == 2011 && month == 7 -> list.add(NewsArticle("hist_2011_7", "Ön Kamera ve Sosyal Medya Patlaması", "Ön kameralı telefonlara olan talep rekor kırıyor. Özçekim (selfie) trendi pazarı yönlendiriyor.", "Sektör", 2011, 7))
            year == 2012 && month == 1 -> list.add(NewsArticle("hist_2012_1", "4G LTE Hızında Mobil Bağlantı", "Yüksek hızlı 4G şebekeleri yaygınlaşırken kullanıcılar görüntülü görüşme ve yayıncılığa yöneliyor.", "Teknoloji", 2012, 1))
            year == 2012 && month == 8 -> list.add(NewsArticle("hist_2012_8", "İnce Tasarım Yarışı", "8mm altı alüminyum ve cam gövdeli cihazlar premium segmente yön veriyor.", "Pazar", 2012, 8))
            year == 2013 && month == 1 -> list.add(NewsArticle("hist_2013_1", "Full HD (1080p) Ekran Devrimi", "Keskin görseller sunan 1080p paneller amiral gemisi telefonların vazgeçilmezi oldu.", "Teknoloji", 2013, 1))
            year == 2013 && month == 9 -> list.add(NewsArticle("hist_2013_9", "Biyometrik Parmak İzi Sensörleri", "Cihaz güvenliğinde parmak izi okuyucular yeni dönem başlatıyor.", "Sektör", 2013, 9))
            year == 2014 && month == 1 -> list.add(NewsArticle("hist_2014_1", "Büyük Ekran (Phablet) Popülaritesi", "5.5 inç ve üzeri geniş ekranlı cihazlar medya tüketiminde birinci tercih haline geldi.", "Pazar", 2014, 1))
            year == 2015 && month == 1 -> list.add(NewsArticle("hist_2015_1", "64-Bit İşlemciler ve 4GB RAM Standardı", "Performansta masaüstü bilgisayar seviyesine yaklaşan yeni mobil çipler tanıtıldı.", "Teknoloji", 2015, 1))
            year == 2016 && month == 1 -> list.add(NewsArticle("hist_2016_1", "Çift Kamera Dönemi ve Portre Modu", "Optik zoom ve arka plan bulanıklaştırma sunan çift kamera sistemleri amiral gemilerinde şart.", "Teknoloji", 2016, 1))
            year == 2017 && month == 1 -> list.add(NewsArticle("hist_2017_1", "Çerçevesiz Ekran ve Çentik Modası", "Ekran/gövde oranı %85 üzerine çıktı. 16:9 standart ekranlı cihaz satmak zorlaşıyor.", "Pazar", 2017, 1))
            year == 2018 && month == 1 -> list.add(NewsArticle("hist_2018_1", "Ekran İçi Parmak İzi & Üçlü Kameralar", "Gizli sensörler ve gelişmiş gece modu fotoğrafçılığı tüketicilerin gözdesi.", "Teknoloji", 2018, 1))
            year == 2019 && month == 1 -> list.add(NewsArticle("hist_2019_1", "5G Bağlantı ve Katlanabilir Ekranlar", "Yeni nesil katlanabilir cihazlar ve ultra hızlı 5G çipleri satışa sunuldu.", "Sektör", 2019, 1))
            year == 2020 && month == 1 -> list.add(NewsArticle("hist_2020_1", "120Hz Yüksek Yenileme Hızlı OLED Ekranlar", "Kullanıcılar takılmasız ve yağ gibi akan 120Hz ekran tecrübesi talep ediyor.", "Teknoloji", 2020, 1))
            year == 2021 && month == 1 -> list.add(NewsArticle("hist_2021_1", "5nm İşlemciler ve Güç Verimliliği", "Pil ömrünü uzatan ve ısınmayı engelleyen gelişmiş işlemciler pazara hakim.", "Teknoloji", 2021, 1))
            year == 2022 && month == 1 -> list.add(NewsArticle("hist_2022_1", "NPU Yapay Zeka Çipleri Mobil Cihazlarda", "Fotoğraf işleme, ses tanıma ve pil yönetimi tamamen yapay zekaya devredildi.", "Teknoloji", 2022, 1))
            year == 2023 && month == 1 -> list.add(NewsArticle("hist_2023_1", "Katlanabilir Ekran Satışları Katlandı", "Esnek ekran maliyetlerinin düşmesiyle katlanabilir modeller pazar payını artırdı.", "Pazar", 2023, 1))
            year == 2024 && month == 1 -> list.add(NewsArticle("hist_2024_1", "Cihaz Üstü Üretken Yapay Zeka (GenAI)", "Çevrimdışı çalışan yapay zeka asistanları cihaz tercihlerinde 1 numaralı faktör.", "Teknoloji", 2024, 1))
            year >= 2025 && month == 1 -> list.add(NewsArticle("hist_${year}_1", "Geleceğin Mobil Teknolojileri", "Katı hal bataryalar ve holografik ekran araştırmaları sektörde heyecan yaratıyor.", "Teknoloji", year, 1))
        }
        return list
    }

    fun checkTrendMatch(specs: PhoneSpecs, trend: MarketTrend? = _state.value.currentTrend): Boolean {
        if (trend == null) return false
        return when (trend.category) {
            TrendCategory.HIGH_REFRESH_DISPLAY -> {
                specs.display.contains("120Hz") || specs.display.contains("144Hz") || specs.display.contains("240Hz") || specs.style == "Oyuncu"
            }
            TrendCategory.CAMERA_PRO -> {
                specs.camera.contains("Çift") || specs.camera.contains("Üçlü") || specs.camera.contains("Periskop") || specs.camera.contains("200MP") || specs.camera.contains("GenAI") || specs.camera.contains("16-20") || specs.camera.contains("13 MP")
            }
            TrendCategory.LONG_BATTERY -> {
                specs.batteryCapacity.contains("4500") || specs.batteryCapacity.contains("5000") || specs.batteryCapacity.contains("5500") || specs.batteryCapacity.contains("7000") || specs.batteryCapacity.contains("3600") || specs.batteryCapacity.contains("4000") || specs.batteryType.contains("Katı Hal")
            }
            TrendCategory.BUDGET_VALUE -> {
                specs.price <= 400
            }
            TrendCategory.PREMIUM_BUILD -> {
                specs.material == "Titanyum" || specs.material == "Cam" || specs.material == "Alüminyum" || 
                specs.backFinish == "Vegan Deri" || specs.backFinish == "Karbon Fiber" || specs.backFinish == "Buzlu Mat Cam" ||
                specs.frameStyle == "Ultra İnce Çerçeve" || specs.style == "Modern"
            }
            TrendCategory.AI_PROCESSOR -> {
                specs.processor.contains("Gen") || specs.processor.contains("In-House") || specs.processor.contains("Kuantum") || 
                specs.processor.contains("865") || specs.processor.contains("888") || specs.processor.contains("845") || 
                specs.ramCapacity.contains("12") || specs.ramCapacity.contains("16") || specs.ramCapacity.contains("24") || specs.ramCapacity.contains("32") ||
                specs.ramType.contains("LPDDR5") || specs.ramType.contains("LPDDR6")
            }
            TrendCategory.FAST_CONNECTIVITY -> {
                specs.connectivity.contains("5G") || specs.connectivity.contains("Wi-Fi 6") || specs.connectivity.contains("Wi-Fi 7") || specs.connectivity.contains("Thunderbolt") || specs.connectivity.contains("Uydu") || specs.connectivity.contains("4G")
            }
        }
    }

    private fun generateNewTrend(year: Int, currentCategory: TrendCategory? = null): MarketTrend {
        val pool = when {
            year <= 2013 -> listOf(
                MarketTrend("tr_cam_${Random.nextInt(100, 999)}", "Özçekim & HD Kamera Çılgınlığı", "Sosyal medya patlamasıyla tüketiciler yüksek çözünürlüklü ön/arka kamera istiyor.", TrendCategory.CAMERA_PRO, 1.20f, 4, 4),
                MarketTrend("tr_prem_${Random.nextInt(100, 999)}", "İnce & Alüminyum Gövde Modası", "Plastikten sıkılan kullanıcılar metal ve cam şıklığını arıyor.", TrendCategory.PREMIUM_BUILD, 1.15f, 4, 4),
                MarketTrend("tr_budg_${Random.nextInt(100, 999)}", "Ekonomik Akıllı Telefon Akını", "Gelişmekte olan pazarlarda $400 altı bütçe dostu cihazlar kapışılıyor.", TrendCategory.BUDGET_VALUE, 1.10f, 4, 4),
                MarketTrend("tr_bat_${Random.nextInt(100, 999)}", "Tüm Gün Yeten Batarya Arayışı", "Büyük ekranlarla artan enerji ihtiyacı için yüksek mAh piller aranıyor.", TrendCategory.LONG_BATTERY, 1.15f, 4, 4),
                MarketTrend("tr_conn_${Random.nextInt(100, 999)}", "4G LTE Hızlı Bağlantı Talebi", "Hızlı internet isteyen mobil kullanıcılar 4G destekli telefonları tercih ediyor.", TrendCategory.FAST_CONNECTIVITY, 1.20f, 4, 4)
            )
            year in 2014..2019 -> listOf(
                MarketTrend("tr_disp_${Random.nextInt(100, 999)}", "Yüksek Yenileme Hızı & Oyuncu Ekranları", "Mobil oyunlar popülerleştikçe akıcı ekranlar ve oyuncu tasarımı revaçta.", TrendCategory.HIGH_REFRESH_DISPLAY, 1.15f, 4, 4),
                MarketTrend("tr_cam2_${Random.nextInt(100, 999)}", "Çoklu Kamera & Portre Modu Modası", "Arka planı bulanıklaştıran çift ve üçlü kameralar tüketicilerin 1 numaralı tercihi.", TrendCategory.CAMERA_PRO, 1.20f, 4, 4),
                MarketTrend("tr_prem2_${Random.nextInt(100, 999)}", "Cam & Çerçevesiz Tasarım Yarışı", "Cam arka kapaklar ve lüks metal çerçeveler vitrinleri süslüyor.", TrendCategory.PREMIUM_BUILD, 1.15f, 4, 4),
                MarketTrend("tr_bat2_${Random.nextInt(100, 999)}", "Mega Kapasiteli Batarya Çılgınlığı", "Kullanıcılar 2 gün şarj istemeyen 4500mAh+ devasa pillere yöneliyor.", TrendCategory.LONG_BATTERY, 1.15f, 4, 4),
                MarketTrend("tr_budg2_${Random.nextInt(100, 999)}", "Fiyat / Performans Patlaması", "Orta segmentte amiral gemisi hissi veren ucuz modeller kapışılıyor.", TrendCategory.BUDGET_VALUE, 1.10f, 4, 4)
            )
            else -> listOf(
                MarketTrend("tr_ai_${Random.nextInt(100, 999)}", "Cihaz Üstü Yapay Zeka (AI) Çipleri", "NPU birimli işlemciler ve yüksek RAM kapasitesi satın alma tercihlerini belirliyor.", TrendCategory.AI_PROCESSOR, 1.25f, 4, 4),
                MarketTrend("tr_disp2_${Random.nextInt(100, 999)}", "120Hz-240Hz Ultra Akıcı OLED Ekranlar", "Takılmasız LTPO ekranlar ve oyuncu dizaynları tüm segmentlerde talep ediliyor.", TrendCategory.HIGH_REFRESH_DISPLAY, 1.20f, 4, 4),
                MarketTrend("tr_cam3_${Random.nextInt(100, 999)}", "1-İnç Sensörler & Periskop Zoom", "Profesyonel seviye fotoğrafçılık ve 8K kayıt yeteneği aranıyor.", TrendCategory.CAMERA_PRO, 1.20f, 4, 4),
                MarketTrend("tr_prem3_${Random.nextInt(100, 999)}", "Titanyum Alaşım & Zırhlı Gövde Trendi", "Uzay endüstrisi sınıfı titanyum gövde modelleri prestij sembolü oldu.", TrendCategory.PREMIUM_BUILD, 1.15f, 4, 4),
                MarketTrend("tr_conn2_${Random.nextInt(100, 999)}", "5G & Uydu İletişimi Çılgınlığı", "Kesintisiz küresel bağlantı ve acil durum uydu iletişimi gözde.", TrendCategory.FAST_CONNECTIVITY, 1.20f, 4, 4),
                MarketTrend("tr_bat3_${Random.nextInt(100, 999)}", "Katı Hal Batarya & 5000mAh+ Güç", "Isınmayan ve günlerce dayanan yeni nesil bataryalar pazarı sallıyor.", TrendCategory.LONG_BATTERY, 1.20f, 4, 4)
            )
        }

        val candidates = pool.filter { it.category != currentCategory }
        return candidates.randomOrNull() ?: pool.random()
    }

    private fun getCompetitorModelForYear(companyName: String, year: Int): Triple<String, Int, Int> {
        val cleanName = companyName.lowercase()
        return when {
            cleanName.contains("samsung") -> when {
                year <= 2010 -> Triple("Galaxy S", 599, 85)
                year == 2011 -> Triple("Galaxy S2 AMOLED", 649, 87)
                year in 2012..2013 -> Triple("Galaxy S4 Note", 699, 89)
                year in 2014..2015 -> Triple("Galaxy S6 Edge", 749, 91)
                year in 2016..2017 -> Triple("Galaxy S8 Infinity", 799, 93)
                year in 2018..2019 -> Triple("Galaxy S10 Plus", 899, 94)
                year in 2020..2021 -> Triple("Galaxy S21 Ultra 5G", 1199, 95)
                year in 2022..2023 -> Triple("Galaxy Z Fold 5", 1499, 96)
                else -> Triple("Galaxy S25 Ultra AI", 1299, 97)
            }
            cleanName.contains("apple") -> when {
                year <= 2010 -> Triple("iPhone 4 Retina", 699, 89)
                year == 2011 -> Triple("iPhone 4S Siri", 699, 90)
                year == 2012 -> Triple("iPhone 5 Lightning", 749, 91)
                year == 2013 -> Triple("iPhone 5S Touch ID", 799, 92)
                year in 2014..2015 -> Triple("iPhone 6 Plus", 849, 93)
                year in 2016..2017 -> Triple("iPhone X Bionic", 999, 95)
                year in 2018..2019 -> Triple("iPhone 11 Pro Max", 1099, 95)
                year in 2020..2021 -> Triple("iPhone 13 Pro Ceramic", 1199, 96)
                year in 2022..2023 -> Triple("iPhone 15 Pro Titanium", 1299, 97)
                else -> Triple("iPhone 16 Pro AI Max", 1399, 98)
            }
            cleanName.contains("xiaomi") -> when {
                year <= 2010 -> Triple("Mi 1 MIUI", 249, 79)
                year in 2011..2013 -> Triple("Mi 3 Speed", 279, 82)
                year in 2014..2016 -> Triple("Redmi Note 3 Metal", 219, 84)
                year in 2017..2018 -> Triple("Mi 8 Pro Şeffaf", 399, 86)
                year in 2019..2020 -> Triple("Redmi K20 Pro Pop-up", 349, 88)
                year in 2021..2022 -> Triple("Xiaomi 12 Pro 120W", 599, 90)
                year in 2023..2024 -> Triple("Redmi Turbo 3", 399, 92)
                else -> Triple("Xiaomi 15 Ultra Titanium", 799, 95)
            }
            cleanName.contains("oppo") -> when {
                year <= 2012 -> Triple("Finder Ultra Slim", 399, 80)
                year in 2013..2015 -> Triple("N3 Dönen Kamera", 499, 83)
                year in 2016..2018 -> Triple("Find X Stealth 3D", 699, 87)
                year in 2019..2021 -> Triple("Find X3 Pro MicroLens", 799, 90)
                year in 2022..2023 -> Triple("Find N2 Flip SuperVOOC", 999, 92)
                else -> Triple("Find X8 Pro Hasselblad AI", 1099, 95)
            }
            cleanName.contains("vivo") -> when {
                year <= 2013 -> Triple("Xplay Hi-Fi", 449, 80)
                year in 2014..2016 -> Triple("X5Max 4.75mm Slim", 499, 82)
                year in 2017..2019 -> Triple("NEX Ekran İçi Parmak İzi", 649, 86)
                year in 2020..2022 -> Triple("X70 Pro+ Zeiss Gimbal", 799, 91)
                else -> Triple("X200 Pro Zeiss Telephoto AI", 999, 95)
            }
            cleanName.contains("huawei") -> when {
                year <= 2013 -> Triple("Ascend P6 Metal", 399, 81)
                year in 2014..2016 -> Triple("P9 Leica Çift Kamera", 549, 86)
                year in 2017..2019 -> Triple("Mate 20 Pro Matrix", 799, 92)
                year in 2020..2022 -> Triple("Mate 40 Pro 5nm Kirin", 899, 94)
                else -> Triple("Pura 70 Ultra XMAGE Geri Çekilebilir", 1199, 96)
            }
            cleanName.contains("google") -> when {
                year <= 2012 -> Triple("Nexus 4 Pure", 399, 83)
                year in 2013..2015 -> Triple("Nexus 6P Metal", 549, 86)
                year in 2016..2018 -> Triple("Pixel 3 XL HDR+", 799, 90)
                year in 2019..2021 -> Triple("Pixel 6 Pro Tensor", 899, 92)
                year in 2022..2024 -> Triple("Pixel 8 Pro Gemini AI", 999, 94)
                else -> Triple("Pixel 9 Pro Fold AI", 1399, 96)
            }
            cleanName.contains("motorola") -> when {
                year <= 2013 -> Triple("Droid Razr Kevlar", 399, 80)
                year in 2014..2016 -> Triple("Moto X Ahşap Kapak", 449, 83)
                year in 2017..2019 -> Triple("Moto Z Modüler Mod", 499, 85)
                year in 2020..2022 -> Triple("Edge 30 Ultra 200MP", 699, 89)
                else -> Triple("Razr 50 Ultra Katlanabilir Dış Ekran", 899, 93)
            }
            cleanName.contains("oneplus") -> when {
                year <= 2014 -> Triple("OnePlus One Flagship Killer", 299, 86)
                year in 2015..2017 -> Triple("OnePlus 3 Dash Charge", 399, 87)
                year in 2018..2020 -> Triple("OnePlus 7 Pro 90Hz Çerçevesiz", 669, 91)
                year in 2021..2023 -> Triple("OnePlus 11 Hasselblad 100W", 699, 93)
                else -> Triple("OnePlus 13 Snapdragon 8 Gen Elite", 799, 95)
            }
            cleanName.contains("realme") -> when {
                year <= 2018 -> Triple("Realme 1 Diamond Design", 199, 78)
                year in 2019..2021 -> Triple("Realme X Master Edition", 269, 82)
                year in 2022..2023 -> Triple("GT Neo 5 240W Rekor Şarj", 399, 88)
                else -> Triple("GT 6 AI Master Pro", 499, 92)
            }
            cleanName.contains("honor") -> when {
                year <= 2015 -> Triple("Honor 6 Kirin", 299, 79)
                year in 2016..2018 -> Triple("Honor 8 Aurora Cam", 399, 84)
                year in 2019..2021 -> Triple("Honor View 20 Delikli Ekran", 499, 87)
                year in 2022..2023 -> Triple("Magic V2 Dünyanın En İnce Katlanabilir", 1299, 94)
                else -> Triple("Magic 7 Pro AI Göz Dostu PWM", 899, 95)
            }
            cleanName.contains("sony") -> when {
                year <= 2012 -> Triple("Xperia Arc İnce Kavis", 499, 82)
                year in 2013..2015 -> Triple("Xperia Z Suya Dayanıklı Cam", 599, 86)
                year in 2016..2018 -> Triple("Xperia XZ Premium 4K HDR", 799, 89)
                year in 2019..2022 -> Triple("Xperia 1 III 120Hz 4K CinemaWide", 1199, 92)
                else -> Triple("Xperia 1 VI Exmor-T Pro Sensör", 1299, 94)
            }
            cleanName.contains("asus") -> when {
                year <= 2014 -> Triple("PadFone Hibrit", 499, 79)
                year in 2015..2017 -> Triple("ZenFone 2 4GB RAM Öncüsü", 299, 82)
                year in 2018..2020 -> Triple("ROG Phone 2 120Hz AMOLED 6000mAh", 799, 90)
                year in 2021..2023 -> Triple("ROG Phone 7 Ultimate Aktif Fan", 1199, 94)
                else -> Triple("ROG Phone 8 Pro 165Hz Matrix LED", 1099, 96)
            }
            cleanName.contains("nokia") -> when {
                year <= 2011 -> Triple("N8 Symbian 12MP Carl Zeiss", 429, 80)
                year in 2012..2014 -> Triple("Lumia 1020 41MP PureView", 599, 87)
                year in 2015..2018 -> Triple("Nokia 8 Sirocco Zırhlı", 649, 85)
                year in 2019..2022 -> Triple("Nokia XR20 Askeri Standart", 499, 83)
                else -> Triple("HMD Skyline QuickFix Kolay Tamir", 399, 86)
            }
            cleanName.contains("tecno") -> when {
                year <= 2015 -> Triple("Tecno Boom J7 Müzik", 129, 72)
                year in 2016..2019 -> Triple("Phantom 8 Çift Kamera", 189, 76)
                year in 2020..2022 -> Triple("Camon 19 Pro RGBW Sensör", 249, 81)
                else -> Triple("Phantom V Fold 2 Katlanabilir", 799, 89)
            }
            cleanName.contains("infinix") -> when {
                year <= 2016 -> Triple("Infinix Hot 3 Bütçe", 119, 71)
                year in 2017..2020 -> Triple("Note 7 Büyük Ekran", 169, 75)
                year in 2021..2023 -> Triple("Zero Ultra 180W Thunder Charge", 399, 84)
                else -> Triple("GT 20 Pro Mecha RGB Gaming", 299, 88)
            }
            cleanName.contains("nothing") -> when {
                year <= 2021 -> Triple("Nothing Concept Glyph 1", 399, 83)
                year == 2022 -> Triple("Nothing Phone (1) Glyph Şeffaf", 449, 87)
                year in 2023..2024 -> Triple("Nothing Phone (2) Glyph Matrix", 599, 90)
                else -> Triple("Nothing Phone (3) Transparent AI", 699, 93)
            }
            cleanName.contains("zte") -> when {
                year <= 2014 -> Triple("Blade V Akıllı Tasarım", 199, 75)
                year in 2015..2018 -> Triple("Axon 7 Hi-Fi Çift Hoparlör", 399, 82)
                year in 2019..2022 -> Triple("Axon 20 Dünyanın İlk Ekran Altı Kamerası", 449, 86)
                else -> Triple("RedMagic 9 Pro Çentiksiz RGB Fanlı Canavar", 749, 93)
            }
            cleanName.contains("tcl") -> when {
                year <= 2015 -> Triple("Idol 3 Çift Yönlü Kullanım", 199, 74)
                year in 2016..2019 -> Triple("TCL Plex NXTVISION", 249, 78)
                year in 2020..2023 -> Triple("TCL 40 NXTPAPER Göz Koruma Mat Ekran", 299, 83)
                else -> Triple("TCL 50 Pro NXTPAPER 3.0 Kağıt Hissi", 349, 87)
            }
            else -> when { // Fairphone
                year <= 2014 -> Triple("Fairphone 1 Modüler Başlangıç", 399, 76)
                year in 2015..2019 -> Triple("Fairphone 2 Kendin Tamir Et", 499, 79)
                year in 2020..2022 -> Triple("Fairphone 4 5G Adil Ticaret", 579, 83)
                else -> Triple("Fairphone 5 %100 Modüler 8 Yıl Destek", 699, 87)
            }
        }
    }

    fun advanceTime() {
        val currentState = _state.value
        val newMonth = if (currentState.month == 12) 1 else currentState.month + 1
        val newYear = if (currentState.month == 12) currentState.year + 1 else currentState.year

        var totalMonthlyRevenue = 0L
        var totalMonthlyUnitsSold = 0
        val newNewsList = currentState.newsList.toMutableList()
        val finishedModelsNews = mutableListOf<NewsArticle>()

        // Update Market Trends
        var updatedTrend = currentState.currentTrend
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

        // Process monthly sales over 12 or 24-month cycle for each active model
        val updatedActiveModels = currentState.activeModels.map { model ->
            if (model.monthsOnMarket < model.maxMonthsOnMarket && model.remainingStock > 0) {
                val newMonths = model.monthsOnMarket + 1
                
                // Base monthly sales target based on max months (12 or 24)
                val baseMonthlyBatch = model.totalStock / model.maxMonthsOnMarket.toFloat()
                
                // Quality, reputation and active marketing campaign demand multiplier
                val qualityFactor = (model.reviewScore / 55.0f).coerceIn(0.4f, 2.0f)
                
                // 1. ZORLUK & İTİBAR DENGESİ: Düşük itibarda satışlar daha zor ve gerçekçi
                val repFactor = when {
                    currentState.reputation < 20 -> 0.35f + (currentState.reputation / 20.0f) * 0.30f // 0.35x - 0.65x (Erken aşamada marka bilinirliği düşük)
                    currentState.reputation < 50 -> 0.65f + ((currentState.reputation - 20) / 30.0f) * 0.40f // 0.65x - 1.05x (Büyüyen marka)
                    currentState.reputation < 80 -> 1.05f + ((currentState.reputation - 50) / 30.0f) * 0.45f // 1.05x - 1.50x (Pazar devi)
                    else -> 1.50f + ((currentState.reputation - 80) / 20.0f) * 0.50f // 1.50x - 2.00x (Küresel mega lider)
                }

                // 2. FİYAT DUYARLILIĞI (PRICE ELASTICITY): Düşük itibarda fahiş kâr marjına talep cezası
                val estimatedUnitCost = model.specs.unitCost.coerceAtLeast(40)
                val markupRatio = model.specs.price.toFloat() / estimatedUnitCost.toFloat()
                val priceElasticityFactor = when {
                    markupRatio > 2.6f && currentState.reputation < 35 -> 0.40f // Fahiş fiyat & düşük itibar = %60 talep cezası!
                    markupRatio > 2.0f && currentState.reputation < 50 -> 0.65f // %35 talep cezası
                    markupRatio > 1.7f && currentState.reputation < 25 -> 0.75f
                    markupRatio <= 1.3f -> 1.25f // Uygun fiyat / F-P patlaması = +%25 satış hacmi
                    else -> 1.0f
                }

                // 3. SERİ DEVAMI & NESİL SADAKATİ: Model serisi devam ettikçe kemik kitle oluşur
                val seriesLoyaltyFactor = if (model.specs.generation > 1) {
                    1.0f + ((model.specs.generation - 1).coerceAtMost(6) * 0.06f) // Her nesilde +%6 sadık kitle talebi
                } else 1.0f

                // 4. MODEL SINIFI (TIER) ETKİSİ: Standart, Pro, Ultra, Lite dinamikleri
                val tierDemandFactor = when (model.specs.tier) {
                    ModelTier.LITE -> 1.30f // Hızlı sürüm ve yüksek hacim
                    ModelTier.STANDARD -> 1.0f
                    ModelTier.PRO -> if (currentState.reputation >= 30) 0.95f else 0.60f // Pro amiral gemisi için itibar şart
                    ModelTier.ULTRA -> if (currentState.reputation >= 50) 0.85f else 0.40f // Ultra tepe model için güçlü marka şart
                }

                val campaignFactor = if (model.activeCampaign != null && model.activeCampaign.remainingMonths > 0) {
                    1.0f + (model.activeCampaign.type.boostPercent / 100.0f)
                } else 1.0f

                // Trend Bonus (Dynamic realistic boost +10% to +25% depending on era trend)
                val isTrendActive = model.matchesTrend || checkTrendMatch(model.specs, updatedTrend)
                val trendFactor = if (isTrendActive) (updatedTrend?.bonusMultiplier ?: 1.15f) else 1.0f

                // Design & Color Variety Sales Appeal Multiplier
                val extraColors = (model.specs.selectedColors.size - 1).coerceAtLeast(0)
                val colorFactor = 1.0f + (extraColors * 0.04f) // +4% boost per extra color
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

                // Lifecycle Sales Curve Factor:
                // Months 1-2: Slow warm-up / early adopters (0.35x -> 0.65x)
                // Months 3-6: Massive peak / viral boom / mainstream frenzy (2.2x -> 1.8x)
                // Months 7+: Sharp drop-off / market saturation (0.25x -> 0.08x)
                val lifecycleSalesCurve = when (newMonths) {
                    1 -> 0.35f
                    2 -> 0.65f
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
                    else -> 0.04f // 2nd year if extended
                }

                val demandFactor = qualityFactor * repFactor * priceElasticityFactor * seriesLoyaltyFactor * tierDemandFactor * campaignFactor * trendFactor * colorFactor * designFactor * osSynergyFactor * lifecycleSalesCurve
                
                // Monthly units sold
                val calculatedUnits = (baseMonthlyBatch * demandFactor).toInt().coerceAtLeast(1)
                val unitsSoldThisMonth = calculatedUnits.coerceAtMost(model.remainingStock)
                
                val revenueThisMonth = unitsSoldThisMonth.toLong() * model.specs.price
                
                totalMonthlyRevenue += revenueThisMonth
                totalMonthlyUnitsSold += unitsSoldThisMonth

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
                    if (camp.remainingMonths > 1) camp.copy(remainingMonths = camp.remainingMonths - 1) else null
                }

                val updatedModel = model.copy(
                    remainingStock = model.remainingStock - unitsSoldThisMonth,
                    totalSold = model.totalSold + unitsSoldThisMonth,
                    totalRevenue = model.totalRevenue + revenueThisMonth,
                    monthsOnMarket = newMonths,
                    isExtendedNewsSent = isExtendedNewsSent,
                    activeCampaign = updatedCampaign,
                    matchesTrend = isTrendActive
                )

                // Check if model completed its sales cycle or sold out this month
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

                updatedModel
            } else {
                model
            }
        }

        val activeInstalledBase = updatedActiveModels.sumOf { it.totalSold }

        // --- CUSTOM OS MONTHLY EVOLUTION & ADOPTION LOGIC ---
        var updatedCustomOs = currentState.customOs
        var appStoreRevenueThisMonth = 0L
        var licenseRevenueThisMonth = 0L

        if (updatedCustomOs.isCustomActive) {
            val devCount = updatedCustomOs.assignedDevs.coerceAtMost(currentState.engineers)
            
            // 1. Developers produce monthly XP and increase optimization score
            val monthlyXpGain = (devCount * 25) + (currentState.engineers * 4)
            val newDevXp = updatedCustomOs.devXp + monthlyXpGain
            
            val newOptScore = (20 + (devCount * 4) + (updatedCustomOs.majorVersionCount * 8) + (newDevXp / 120)).coerceIn(15, 100)
            
            // 2. Active player phones running this OS
            val activePlayerUserBase = updatedActiveModels
                .filter { it.specs.osName == updatedCustomOs.name }
                .sumOf { it.totalSold }

            // 3. Third-Party / OEM Adoption growth
            var adopters = updatedCustomOs.thirdPartyAdoptersCount
            val licenseType = updatedCustomOs.licenseType
            
            if (licenseType == OsLicenseType.OPEN_SOURCE) {
                // Open Source expands rapidly as popularity and ecosystem grows
                if (updatedCustomOs.popularityPercent >= 4.0f && adopters < 2) adopters = 2
                if (updatedCustomOs.popularityPercent >= 10.0f && adopters < 4) adopters = 4
                if (updatedCustomOs.popularityPercent >= 20.0f && adopters < 7) adopters = 7
                if (updatedCustomOs.popularityPercent >= 35.0f && adopters < 12) adopters = 12
                if (updatedCustomOs.popularityPercent >= 50.0f && adopters < 18) adopters = 18
            } else {
                // Closed Proprietary has selective partners if license fee is reasonable
                if (updatedCustomOs.perDeviceLicenseFee <= 25 && updatedCustomOs.popularityPercent >= 8.0f && adopters < 1) adopters = 1
                if (updatedCustomOs.perDeviceLicenseFee <= 20 && updatedCustomOs.popularityPercent >= 18.0f && adopters < 2) adopters = 2
                if (updatedCustomOs.perDeviceLicenseFee <= 15 && updatedCustomOs.popularityPercent >= 30.0f && adopters < 4) adopters = 4
                if (updatedCustomOs.perDeviceLicenseFee <= 10 && updatedCustomOs.popularityPercent >= 45.0f && adopters < 7) adopters = 7
            }

            // Monthly device production by third party adopters
            val thirdPartyMonthlyProduction = if (adopters > 0) {
                val perPartnerVolume = (10000 + (newYear - 2010) * 3500) * (updatedCustomOs.popularityPercent / 10f).coerceAtLeast(0.4f)
                (adopters * perPartnerVolume * licenseType.adoptionSpeedMultiplier).toLong()
            } else 0L

            val newThirdPartyActiveDevices = updatedCustomOs.thirdPartyActiveDevices + thirdPartyMonthlyProduction

            // 4. Popularity increment (Gradual, realistic adoption curve - starts low, grows over time)
            val devGrowthFactor = (devCount * 0.15f)
            val versionFactor = (updatedCustomOs.majorVersionCount * 0.20f)
            val playerSalesFactor = (totalMonthlyUnitsSold / 30000f)
            val adoptionBonus = (adopters * 0.30f * licenseType.adoptionSpeedMultiplier)
            val popularityDelta = (devGrowthFactor + versionFactor + playerSalesFactor + adoptionBonus + 0.15f) * 0.35f
            
            val newPopularity = (updatedCustomOs.popularityPercent + popularityDelta).coerceIn(1.0f, 85.0f)
            val newEcosystemScore = (15 + (newPopularity * 0.8f).toInt() + (devCount * 3) + (updatedCustomOs.majorVersionCount * 5)).coerceIn(10, 100)

            // 5. App Store Revenue & License Revenue Calculation
            val totalEcosystemDevices = activePlayerUserBase + newThirdPartyActiveDevices
            if (totalEcosystemDevices > 0) {
                val baseUserRate = updatedCustomOs.type.storeRevenuePerUser
                val commissionMultiplier = (updatedCustomOs.commissionRate.percent / 20.0f) * updatedCustomOs.commissionRate.marketLoyaltyBonus
                val popMultiplier = (newPopularity / 20.0f).coerceIn(0.25f, 2.5f)
                val storeModuleBonus = 1.0f + (updatedCustomOs.appStoreLevel - 1) * 0.25f
                
                appStoreRevenueThisMonth = (totalEcosystemDevices * baseUserRate * commissionMultiplier * popMultiplier * licenseType.storeRevenueMultiplier * storeModuleBonus / 3.5f).toLong()
            }

            // 6. Cloud & Ecosystem Subscription Revenue
            val cloudRevenueThisMonth = if (updatedCustomOs.cloudLevel > 1 && totalEcosystemDevices > 0) {
                val payingUsersRatio = 0.08f + (updatedCustomOs.cloudLevel * 0.04f)
                val cloudUsers = (totalEcosystemDevices * payingUsersRatio).toLong()
                (cloudUsers * 1.5f).toLong() // $1.5 per active cloud subscriber
            } else 0L

            if (licenseType == OsLicenseType.CLOSED_PROPRIETARY && updatedCustomOs.perDeviceLicenseFee > 0 && thirdPartyMonthlyProduction > 0) {
                licenseRevenueThisMonth = thirdPartyMonthlyProduction * updatedCustomOs.perDeviceLicenseFee
            }

            // 7. Store Apps Catalog Growth
            val appsFromDevFund = (updatedCustomOs.devFundBalance / 80000L).coerceAtLeast(0L)
            val newAppsAdded = (1200L * updatedCustomOs.appStoreLevel) + (devCount * 600L) + (updatedCustomOs.ecosystemScore * 450L) + appsFromDevFund
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
                totalAppStoreRevenueToDate = updatedCustomOs.totalAppStoreRevenueToDate + appStoreRevenueThisMonth,
                totalLicenseRevenueToDate = updatedCustomOs.totalLicenseRevenueToDate + licenseRevenueThisMonth,
                totalCloudRevenueToDate = updatedCustomOs.totalCloudRevenueToDate + cloudRevenueThisMonth,
                lastMonthAppStoreIncome = appStoreRevenueThisMonth,
                lastMonthLicenseIncome = licenseRevenueThisMonth,
                lastMonthCloudRevenue = cloudRevenueThisMonth
            )
        }

        val totalCombinedRevenue = totalMonthlyRevenue + appStoreRevenueThisMonth + licenseRevenueThisMonth + (updatedCustomOs.lastMonthCloudRevenue)
        val totalExpenses = currentState.totalMonthlyExpenses
        val netIncome = totalCombinedRevenue - totalExpenses

        // Competitors AI Simulation & Releases
        var updatedCompetitorReleases = currentState.competitorReleases.toMutableList()
        var updatedCompetitors = currentState.competitors.toMutableList()

        // Every 3 months (or randomly), a competitor launches a new phone
        val shouldCompetitorLaunch = (newMonth % 3 == 0) || (Random.nextInt(100) < 30)
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
                        id = "comp_launch_${newYear}_${newMonth}_${Random.nextInt(100, 999)}",
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
                        id = "rel_${comp.id}_${newYear}_${newMonth}",
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

        // Global Market Sizing (grows over years from 800k in 2010 to 2.5M in 2026+)
        val baseMarketSize = (800000 + (newYear - 2010) * 100000 + (newMonth * 15000)).coerceAtLeast(600000)
        val totalMarketVolume = baseMarketSize

        // Calculate player market share
        val playerSharePercent = ((totalMonthlyUnitsSold.toFloat() / totalMarketVolume.toFloat()) * 100f).coerceIn(0f, 95f)

        // Remaining market share distributed to 20 competitors based on their relative base weights
        val remainingShare = (100f - playerSharePercent).coerceAtLeast(5f)
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

        updatedCompetitors = updatedCompetitors.map { comp ->
            val weight = compWeights[comp.name] ?: (1.0f / updatedCompetitors.size)
            val compShare = remainingShare * weight
            val compSales = (totalMarketVolume * (compShare / 100f)).toInt()
            comp.copy(
                marketSharePercent = compShare,
                monthlySales = compSales
            )
        }.toMutableList()

        // Process Active Research progress
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
                        id = "news_tech_finish_${newYear}_${newMonth}_${Random.nextInt(100, 999)}",
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
                val duration = calculateResearchDuration(currentState.engineers)
                currentActiveResearch = nextCandidate.copy(
                    totalMonths = duration,
                    remainingMonths = duration
                )
                autoStartResearchCost = nextCandidate.cost
                updatedResearchQueue.removeAt(0)

                researchReports.add(
                    MarketReport(
                        title = "Sıradaki Ar-Ge Başlatıldı: ${nextCandidate.techName}",
                        text = "Kuyruktaki ${nextCandidate.techName} araştırması otomatik olarak başlatıldı ($${"%,d".format(nextCandidate.cost)} harcandı). Tahmini süre: $duration Ay.",
                        profit = -nextCandidate.cost,
                        unitsSold = 0,
                        reviewScore = 0
                    )
                )
                researchNewsList.add(
                    NewsArticle(
                        id = "news_tech_auto_${newYear}_${newMonth}_${Random.nextInt(100, 999)}",
                        title = "OTOMATİK AR-GE BAŞLADI: ${nextCandidate.techName}",
                        text = "Ar-Ge ekibi sıradaki '${nextCandidate.techName}' projesi üzerinde çalışmaya başladı.",
                        category = "Teknoloji",
                        year = newYear,
                        month = newMonth
                    )
                )
            }
        }

        // Fetch historical sector news for this year/month
        val periodicNews = getHistoricalNewsForYearMonth(newYear, newMonth)
        newNewsList.addAll(periodicNews)
        newNewsList.addAll(finishedModelsNews)
        newNewsList.addAll(researchNewsList)

        val activeCount = updatedActiveModels.count { !it.isCompleted }

        val storeNote = if (appStoreRevenueThisMonth > 0) " 🌐 ${updatedCustomOs.name} Mağaza Geliri: $${"%,d".format(appStoreRevenueThisMonth)}." else ""
        val licenseNote = if (licenseRevenueThisMonth > 0) " 🔒 OEM Lisans Satış Geliri: $${"%,d".format(licenseRevenueThisMonth)}." else ""

        val report = MarketReport(
            title = "Aylık Finansal Rapor ($newMonth/$newYear)",
            text = "$newMonth. Ay, $newYear yılı tamamlandı. Satıştaki $activeCount modelden bu ay ${"%,d".format(totalMonthlyUnitsSold)} adet cihaz satıldı (Pazar Payı: %${"%.1f".format(playerSharePercent)}). Cihaz Satış Geliri: $${"%,d".format(totalMonthlyRevenue)}.$storeNote$licenseNote Aylık Giderler: $${"%,d".format(totalExpenses)}. Net Gelir: $${"%,d".format(netIncome)}.",
            profit = netIncome,
            unitsSold = totalMonthlyUnitsSold,
            reviewScore = 0
        )

        val isYearEndExpo = (newMonth == 12)
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

        _state.update {
            it.copy(
                month = newMonth,
                year = newYear,
                budget = it.budget + netIncome - autoStartResearchCost + expoPrizeTotal,
                reputation = (it.reputation + expoRepGain).coerceIn(0, 100),
                monthlyIncome = totalCombinedRevenue,
                customOs = updatedCustomOs,
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
                totalMarketMonthlyVolume = totalMarketVolume,
                activeTechExpo = triggeredExpoEvent,
                pastTechExpos = if (triggeredExpoEvent != null) listOf(triggeredExpoEvent) + it.pastTechExpos else it.pastTechExpos,
                techLevel = if (updatedUnlockedTech.size >= 15) "Yapay Zeka" else if (updatedUnlockedTech.size >= 5) "İleri Düzey" else "Giriş"
            )
        }

        autoSaveGame()

        // Periodically fetch dynamic AI news (every 3 months)
        if (newMonth % 3 == 0) {
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
            _state.update {
                it.copy(
                    noticeMessage = "Ofis Kapasitesi Yetersiz! Mevcut ofisiniz en fazla ${currentState.maxEmployees} çalışan barındırabilir. Personel alabilmek için Şirket Ofisinizi büyütün."
                )
            }
            return
        }

        if (type == EmployeeType.ASSEMBLY_WORKER) {
            val newWorkers = currentState.assemblyWorkers + count
            if (newWorkers > currentState.currentFactoryTier.maxWorkers) {
                _state.update {
                    it.copy(
                        noticeMessage = "Fabrika İşçi Kapasitesi Yetersiz! Mevcut tesisiniz en fazla ${currentState.currentFactoryTier.maxWorkers} işçi barındırabilir. Daha fazla montaj işçisi için Fabrikanızı geliştirin."
                    )
                }
                return
            }
        }

        _state.update { state ->
            when (type) {
                EmployeeType.ENGINEER -> state.copy(engineers = state.engineers + count)
                EmployeeType.QA_INSPECTOR -> state.copy(qaInspectors = state.qaInspectors + count)
                EmployeeType.ASSEMBLY_WORKER -> state.copy(assemblyWorkers = state.assemblyWorkers + count)
            }
        }
    }

    fun fireEmployee(type: EmployeeType, count: Int = 1) {
        _state.update { state ->
            when (type) {
                EmployeeType.ENGINEER -> {
                    val newEng = (state.engineers - count).coerceAtLeast(0)
                    state.copy(
                        engineers = newEng,
                        customOs = state.customOs.copy(assignedDevs = state.customOs.assignedDevs.coerceAtMost(newEng))
                    )
                }
                EmployeeType.QA_INSPECTOR -> state.copy(qaInspectors = (state.qaInspectors - count).coerceAtLeast(0))
                EmployeeType.ASSEMBLY_WORKER -> state.copy(assemblyWorkers = (state.assemblyWorkers - count).coerceAtLeast(0))
            }
        }
    }

    fun upgradeOffice() {
        val currentState = _state.value
        val nextTier = OFFICE_TIERS.firstOrNull { it.level == currentState.officeLevel + 1 } ?: return

        if (currentState.budget >= nextTier.upgradeCost) {
            val news = NewsArticle(
                id = "news_office_${currentState.year}_${currentState.month}_${Random.nextInt(100, 999)}",
                title = "ŞİRKET BÜYÜYOR: ${nextTier.name}",
                text = "Şirketiniz Genel Merkezini ${nextTier.name} seviyesine taşıdı! Yeni çalışan kapasitesi: ${nextTier.maxEmployees} Personel.",
                category = "Şirket",
                year = currentState.year,
                month = currentState.month
            )

            val report = MarketReport(
                title = "Şirket Ofisi Büyütüldü: ${nextTier.name}",
                text = "Genel Merkez ${nextTier.name} tesisine taşındı ($${"%,d".format(nextTier.upgradeCost)} harcandı). Yeni Çalışan Kapasitesi: ${nextTier.maxEmployees}, Aylık Kira: $${"%,d".format(nextTier.monthlyRent)}.",
                profit = -nextTier.upgradeCost,
                unitsSold = 0,
                reviewScore = 0
            )

            _state.update {
                it.copy(
                    budget = it.budget - nextTier.upgradeCost,
                    officeLevel = nextTier.level,
                    newsList = it.newsList + news,
                    reports = it.reports + report
                )
            }
        } else {
            _state.update {
                it.copy(noticeMessage = "Ofis yükseltmesi için yetersiz bütçe! Gereken: $${"%,d".format(nextTier.upgradeCost)}")
            }
        }
    }

    fun upgradeFactory() {
        val currentState = _state.value
        val nextTier = FACTORY_TIERS.firstOrNull { it.level == currentState.factoryLevel + 1 } ?: return

        if (currentState.budget >= nextTier.upgradeCost) {
            val news = NewsArticle(
                id = "news_factory_${currentState.year}_${currentState.month}_${Random.nextInt(100, 999)}",
                title = "YENİ ÜRETİM TESİSİ: ${nextTier.name}",
                text = "Şirketiniz ${nextTier.name} yatırımını tamamladı! Yeni üretim indirimi: %${nextTier.discountPercent.toInt()}, Maksimum İşçi Kapasitesi: ${nextTier.maxWorkers}.",
                category = "Şirket",
                year = currentState.year,
                month = currentState.month
            )

            val report = MarketReport(
                title = "Fabrika Yatırımı: ${nextTier.name}",
                text = "${nextTier.name} faaliyete geçti ($${"%,d".format(nextTier.upgradeCost)} harcandı). Birim Üretim İndirimi: %${nextTier.discountPercent.toInt()}, Aylık Sabit Tesis Bakımı: $${"%,d".format(nextTier.monthlyMaintenance)}.",
                profit = -nextTier.upgradeCost,
                unitsSold = 0,
                reviewScore = 0
            )

            _state.update {
                it.copy(
                    budget = it.budget - nextTier.upgradeCost,
                    factoryLevel = nextTier.level,
                    newsList = it.newsList + news,
                    reports = it.reports + report
                )
            }
        } else {
            _state.update {
                it.copy(noticeMessage = "Fabrika yatırımı için yetersiz bütçe! Gereken: $${"%,d".format(nextTier.upgradeCost)}")
            }
        }
    }

    fun clearNoticeMessage() {
        _state.update { it.copy(noticeMessage = null) }
    }

    fun setCompanyProfile(
        name: String,
        logoId: String,
        logoStyle: String,
        brandColorHex: Long,
        slogan: String
    ) {
        _state.update { current ->
            val safeName = name.trim().ifEmpty { "Apex Mobile" }
            val safeSlogan = slogan.trim().ifEmpty { "Geleceğin Akıllı Telefonları" }
            current.copy(
                companyName = safeName,
                companyLogoId = logoId,
                companyLogoStyle = logoStyle,
                companyBrandColorHex = brandColorHex,
                companySlogan = safeSlogan,
                isCompanySetupDone = true,
                reports = listOf(
                    MarketReport(
                        title = "$safeName Kuruldu",
                        text = "$safeName akıllı telefon pazarına adım attı! \"$safeSlogan\" vizyonuyla ilk telefonunuzu tasarlamak için Cihazlar bölümüne gidin.",
                        profit = 0,
                        unitsSold = 0,
                        reviewScore = 0
                    )
                )
            )
        }
        autoSaveGame()
    }

    fun manufacturePhone(specs: PhoneSpecs) {
        val currentState = _state.value
        val productionCost = calculateProductionCost(specs)
        val totalCost = (productionCost.toLong() * specs.quantity) + specs.qaBudget
        
        if (currentState.budget < totalCost) {
            // Not enough budget
            return
        }

        // Calculate product quality based on QA budget, component balance, engineers, QA inspectors, and design aesthetics
        val qaPerUnit = specs.qaBudget.toFloat() / (specs.quantity.coerceAtLeast(1)).toFloat()
        val qaFactor = (qaPerUnit * 1.2f).coerceAtMost(35f).toInt()
        val zeroQaPenalty = if (specs.qaBudget == 0L && specs.quantity > 5000) -6 else if (specs.qaBudget == 0L) -3 else 0
        val techPenalty = ((currentState.year - specs.techScore) * 9 - currentState.engineerTechBonus).coerceAtLeast(0)
        val baseQuality = (64 - techPenalty + (currentState.engineers * 2)).coerceIn(10, 85)
        
        // Design Score Bonus (Premium materials, modern notch, frame, back finish & color options)
        val designBonus = when (specs.material) {
            "Titanyum" -> 6
            "Cam" -> 4
            "Alüminyum" -> 2
            else -> 0
        } + when (specs.backFinish) {
            "Vegan Deri" -> 4
            "Karbon Fiber" -> 3
            "Buzlu Mat Cam" -> 2
            "Parlak Ayna Cam" -> 1
            else -> 0
        } + when (specs.notchStyle) {
            "Görünmez Ekran Altı" -> 6
            "Dinamik Ada / Hap" -> 3
            "Nokta Delik" -> 2
            else -> 0
        } + when (specs.frameStyle) {
            "Ultra İnce Çerçeve" -> 3
            "Zırhlı Kesim", "Kavisli 2.5D" -> 2
            else -> 1
        } + when {
            specs.selectedColors.size >= 4 -> 4
            specs.selectedColors.size >= 2 -> 2
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

        val techComment = when {
            techPenalty == 0 -> "Teknolojisi, tasarımı ve yazılımı çağın ötesinde!"
            techPenalty < 10 -> "Donanımı ve yazılımı günümüz standartlarına uygun."
            techPenalty < 25 -> "Biraz geride kalmış bir teknoloji."
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

        val newActiveModel = ActiveModel(
            id = "${specs.name}_${currentState.year}_${currentState.month}_${Random.nextInt(1000, 9999)}",
            specs = finalSpecs,
            totalStock = specs.quantity,
            remainingStock = specs.quantity,
            totalSold = 0,
            totalRevenue = 0,
            monthsOnMarket = 0,
            reviewScore = reviewScore,
            launchYear = currentState.year,
            launchMonth = currentState.month,
            matchesTrend = isTrendMatched,
            benchmarkScore = computedBenchmark
        )

        val launchNews = NewsArticle(
            id = "news_launch_${newActiveModel.id}",
            title = "${currentState.companyName.uppercase()} LANSMANI: ${specs.name} Piyasada!",
            text = "${currentState.companyName}, ${specs.name} modelini üretti! ${"%,d".format(specs.quantity)} adetlik stok mağazalara dağıtıldı ve 12-24 ay boyunca satılacak. $techComment$colorNote$osNote$trendBonusNote$tierNote$genNote Eleştirmen puanı: $reviewScore/100.",
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
                reputation = (it.reputation + (reviewScore - 50) / 10).coerceIn(0, 100),
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
        var cost = 15 // Base assembly cost

        // Material & Frame & Finish
        cost += when(specs.material) { "Plastik" -> 5; "Alüminyum" -> 20; "Cam" -> 30; "Titanyum" -> 60; else -> 10 }
        cost += when(specs.backFinish) { "Buzlu Mat Cam" -> 0; "Parlak Ayna Cam" -> 5; "Vegan Deri" -> 10; "Fırçalanmış Metal" -> 8; "Karbon Fiber" -> 15; else -> 0 }
        cost += when(specs.cameraBumpStyle) { "Dikey Ada" -> 0; "Dairesel Halo" -> 5; "Yatay Vizör" -> 8; "Kare Ada" -> 5; "Yüzen Çift Halka" -> 0; else -> 0 }
        cost += when(specs.frameStyle) { "Düz Metal Kenar" -> 0; "Kavisli 2.5D" -> 5; "Zırhlı Kesim" -> 10; "Ultra İnce Çerçeve" -> 12; else -> 0 }
        cost += when(specs.notchStyle) { "Nokta Delik" -> 0; "Dinamik Ada / Hap" -> 8; "Klasik Çentik" -> 0; "Görünmez Ekran Altı" -> 25; else -> 0 }
        cost += when(specs.style) { "Modern" -> 10; "Klasik" -> 5; "Oyuncu" -> 15; "Dayanıklı" -> 20; else -> 5 }

        // Processor
        cost += when {
            specs.processor.contains("Kuantum") -> 300
            specs.processor.contains("Gen 3") || specs.processor.contains("D9300") -> 240
            specs.processor.contains("Gen 1") || specs.processor.contains("D9000") -> 180
            specs.processor.contains("In-House") -> 150
            specs.processor.contains("865") || specs.processor.contains("D800") -> 140
            specs.processor.contains("845") || specs.processor.contains("G90") -> 110
            specs.processor.contains("820") || specs.processor.contains("Helio") -> 85
            specs.processor.contains("801") || specs.processor.contains("Atom") -> 60
            specs.processor.contains("S4") || specs.processor.contains("MT67") -> 40
            else -> 20
        }

        // RAM Capacity
        cost += when {
            specs.ramCapacity.contains("32") -> 140
            specs.ramCapacity.contains("24") -> 110
            specs.ramCapacity.contains("16") -> 80
            specs.ramCapacity.contains("12") -> 60
            specs.ramCapacity.contains("8") -> 42
            specs.ramCapacity.contains("6") -> 30
            specs.ramCapacity.contains("4") -> 20
            specs.ramCapacity.contains("3") -> 15
            specs.ramCapacity.contains("2") -> 10
            specs.ramCapacity.contains("1") -> 6
            else -> 3 // 512 MB
        }

        // RAM Type
        cost += when {
            specs.ramType.contains("LPDDR6") -> 75
            specs.ramType.contains("LPDDR5X") -> 55
            specs.ramType.contains("LPDDR5") -> 40
            specs.ramType.contains("LPDDR4X") -> 28
            specs.ramType.contains("LPDDR4") -> 18
            specs.ramType.contains("LPDDR3") -> 10
            specs.ramType.contains("LPDDR2") -> 5
            else -> 2 // LPDDR1
        }

        // Internal Storage
        cost += when {
            specs.storage.contains("2 TB") -> 115
            specs.storage.contains("1 TB") -> 80
            specs.storage.contains("512 GB") -> 55
            specs.storage.contains("256 GB") -> 38
            specs.storage.contains("128 GB") -> 25
            specs.storage.contains("64 GB") -> 16
            specs.storage.contains("32 GB") -> 10
            specs.storage.contains("16 GB") -> 6
            else -> 3 // 8 GB
        }

        // External SD Card Slot
        cost += when {
            specs.sdCardSupport.contains("NM") || specs.sdCardSupport.contains("Express") -> 16
            specs.sdCardSupport.contains("2 TB") -> 11
            specs.sdCardSupport.contains("512 GB") -> 7
            specs.sdCardSupport.contains("128 GB") -> 4
            specs.sdCardSupport.contains("32 GB") -> 2
            else -> 0 // Yok
        }

        // Display
        cost += when {
            specs.display.contains("Holografik") -> 200
            specs.display.contains("240Hz") -> 140
            specs.display.contains("Katlanabilir") -> 120
            specs.display.contains("144Hz") -> 100
            specs.display.contains("120Hz") -> 80
            specs.display.contains("Çerçevesiz") -> 55
            specs.display.contains("Kavisli") -> 45
            specs.display.contains("QHD") -> 30
            specs.display.contains("FHD") -> 20
            else -> 10
        }

        // Glass
        cost += when {
            specs.glass.contains("Sapphire") || specs.glass.contains("Armor+") -> 80
            specs.glass.contains("Gorilla Armor") -> 60
            specs.glass.contains("Ceramic") -> 50
            specs.glass.contains("Victus") -> 35
            specs.glass.contains("Glass 5") -> 25
            specs.glass.contains("Glass 4") -> 20
            specs.glass.contains("Glass 3") -> 15
            specs.glass.contains("Glass 2") -> 10
            else -> 5
        }

        // Camera
        cost += when {
            specs.camera.contains("Donanımsal ISP") -> 250
            specs.camera.contains("GenAI") -> 180
            specs.camera.contains("1 İnç") || specs.camera.contains("200MP") -> 130
            specs.camera.contains("Ekran Altı") -> 100
            specs.camera.contains("Periskop") -> 85
            specs.camera.contains("Üçlü") -> 50
            specs.camera.contains("Çift") -> 35
            specs.camera.contains("16-20") -> 25
            specs.camera.contains("8-13") -> 15
            else -> 10
        }

        // Connectivity (Cellular, Port, Wireless)
        cost += when {
            specs.cellularNetwork.contains("Uydu") -> 45
            specs.cellularNetwork.contains("5G mmWave") -> 30
            specs.cellularNetwork.contains("5G Sub-6") -> 22
            specs.cellularNetwork.contains("4G LTE Cat 6") -> 14
            specs.cellularNetwork.contains("4G LTE") -> 10
            specs.cellularNetwork.contains("3G HSPA+") -> 5
            specs.cellularNetwork.contains("3G") -> 3
            else -> 2
        }

        cost += when {
            specs.chargingPort.contains("Thunderbolt 4") -> 35
            specs.chargingPort.contains("USB-C 3.2") -> 22
            specs.chargingPort.contains("USB-C 3.1") -> 15
            specs.chargingPort.contains("USB-C 2.0") -> 10
            specs.chargingPort.contains("USB 3.0 Micro-B") -> 8
            specs.chargingPort.contains("Micro-USB") -> 3
            specs.chargingPort.contains("Mini-USB") -> 2
            else -> 2
        }

        cost += when {
            specs.wirelessConnectivity.contains("Wi-Fi 7") -> 30
            specs.wirelessConnectivity.contains("Wi-Fi 6E") -> 20
            specs.wirelessConnectivity.contains("Wi-Fi 6") -> 14
            specs.wirelessConnectivity.contains("Wi-Fi 5 (ac)") -> 9
            specs.wirelessConnectivity.contains("Wi-Fi 4 (n)") -> 5
            specs.wirelessConnectivity.contains("Wi-Fi 4 & BT 2.1") -> 3
            else -> 2
        }

        // Audio
        cost += when {
            specs.audio.contains("AI") -> 100
            specs.audio.contains("Kayıpsız") -> 80
            specs.audio.contains("Uzamsal") -> 60
            specs.audio.contains("Asimetrik") -> 45
            specs.audio.contains("Dolby") -> 35
            specs.audio.contains("Jaksız") -> 25
            specs.audio.contains("Ön Stereo") -> 20
            specs.audio.contains("Beats") -> 10
            else -> 5
        }

        // Battery Capacity
        cost += when {
            specs.batteryCapacity.contains("7000") -> 45
            specs.batteryCapacity.contains("5500") -> 35
            specs.batteryCapacity.contains("5000") -> 30
            specs.batteryCapacity.contains("4500") -> 25
            specs.batteryCapacity.contains("4000") -> 20
            specs.batteryCapacity.contains("3600") -> 16
            specs.batteryCapacity.contains("3200") -> 12
            specs.batteryCapacity.contains("3100") -> 10
            else -> 2
        }

        // Battery Type & Charging
        cost += when {
            specs.batteryType.contains("Katı Hal 240W") -> 150
            specs.batteryType.contains("Katı Hal 100W") -> 100
            specs.batteryType.contains("Si-Ca") -> 85
            specs.batteryType.contains("120W") -> 60
            specs.batteryType.contains("65W") -> 40
            specs.batteryType.contains("25W") -> 25
            specs.batteryType.contains("20W") -> 20
            specs.batteryType.contains("15W") -> 15
            specs.batteryType.contains("10W") -> 8
            else -> 5
        }

        // Multi-color setup cost: +$3 per extra color option beyond 1
        val extraColorCount = (specs.selectedColors.size - 1).coerceAtLeast(0)
        cost += extraColorCount * 3

        // OS License Fee
        cost += specs.osLicenseFee

        // Apply Assembly Worker & Factory Tier production discount
        val discountMultiplier = 1.0f - (_state.value.unitCostDiscountPercent / 100.0f)
        return (cost * discountMultiplier).toInt().coerceAtLeast(5)
    }

    fun remanufactureModel(modelId: String, additionalQuantity: Int) {
        val currentState = _state.value
        val model = currentState.activeModels.find { it.id == modelId } ?: return

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
                    remainingStock = it.remainingStock + additionalQuantity,
                    monthsOnMarket = newMonthsOnMarket
                )
            } else {
                it
            }
        }

        val restockNews = NewsArticle(
            id = "news_restock_${modelId}_${currentState.year}_${currentState.month}_${Random.nextInt(100, 999)}",
            title = "TEKRAR ÜRETİM: ${model.specs.name}",
            text = "Yoğun talep üzerine ${model.specs.name} modeli için ${"%,d".format(additionalQuantity)} adet yeni stok üretilerek mağazalara dağıtıldı.",
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
        val newApps = 120000L
        val updatedOs = customOs.copy(
            devConCount = newDevConCount,
            totalStoreApps = customOs.totalStoreApps + newApps,
            ecosystemScore = (customOs.ecosystemScore + 12).coerceAtMost(100),
            popularityPercent = (customOs.popularityPercent + 4.5f).coerceAtMost(90f),
            customerLoyaltyPercent = (customOs.customerLoyaltyPercent + 6f).coerceAtMost(98f)
        )

        val news = NewsArticle(
            id = "devcon_${currentState.year}_${currentState.month}_${Random.nextInt(100, 999)}",
            title = "🌐 KÜRESEL GELİŞTİRİCİ KONFERANSI: ${customOs.name} DevCon $newDevConCount!",
            text = "Dünyanın dört bir yanından on binlerce yazılım geliştiricisi şirketimizin ${customOs.name} DevCon etkinliğine katıldı. Yeni API'lar, yapay zeka araçları ve $newApps yeni uygulama mağazaya katılıyor!",
            category = "Şirket",
            year = currentState.year,
            month = currentState.month
        )

        val report = MarketReport(
            title = "${customOs.name} DevCon Konferansı",
            text = "Küresel geliştirici konferansı düzenlendi ($${"%,d".format(cost)} harcandı). +$newApps yeni uygulama çekildi, ekosistem puanı +12 arttı.",
            profit = -cost,
            unitsSold = 0,
            reviewScore = 98
        )

        _state.update {
            it.copy(
                budget = it.budget - cost,
                reputation = (it.reputation + 8).coerceIn(0, 100),
                customOs = updatedOs,
                newsList = it.newsList + news,
                reports = it.reports + report,
                noticeMessage = "🎉 Küresel Geliştirici Konferansı (DevCon) büyük başarıyla tamamlandı! (+8 İtibar, +12 Ekosistem)"
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

    private fun generateTechExpo(
        year: Int,
        playerModels: List<ActiveModel>,
        playerCompanyName: String,
        playerBrandColorHex: Long,
        competitors: List<CompetitorCompany>
    ): TechExpoEvent {
        val expoNames = listOf(
            "MWC (Mobile World Congress)" to "Barselona, İspanya",
            "CES (Consumer Electronics Show)" to "Las Vegas, ABD",
            "IFA Tech Global" to "Berlin, Almanya",
            "Computex World Expo" to "Taipei, Tayvan"
        )
        val selectedExpo = expoNames[(year + 3) % expoNames.size]

        // Pool of all eligible competitor phones for this year
        val competitorNominees = competitors.map { comp ->
            AwardNominee(
                modelName = comp.currentTopModel,
                companyName = comp.name,
                isPlayer = false,
                logoEmoji = comp.logoEmoji,
                brandColorHex = comp.brandColorHex,
                score = comp.currentModelScore,
                price = comp.currentModelPrice,
                highlightText = "${comp.name}, ${comp.currentTopModel} modeliyle ${comp.strategyType} alanında jüriden takdir topladı."
            )
        }

        // Player nominees from active models
        val playerNominees = playerModels.map { model ->
            AwardNominee(
                modelName = model.specs.name,
                companyName = playerCompanyName,
                isPlayer = true,
                logoEmoji = "📱",
                brandColorHex = playerBrandColorHex,
                score = model.reviewScore,
                price = model.specs.price,
                highlightText = "${playerCompanyName} imzalı ${model.specs.name}, yenilikçi tasarımı (${model.specs.material}) ve ${model.specs.osType} işletim sistemiyle fuarda büyük sükse yaptı."
            )
        }

        val allNominees = competitorNominees + playerNominees

        val awardResults = mutableListOf<AwardResult>()
        var playerWonCount = 0
        var totalPrizeWon = 0L
        var reputationGained = 0

        // 1. FLAGSHIP OF THE YEAR (Highest overall score)
        val flagshipNominees = allNominees.sortedByDescending { it.score }.take(4)
        val flagshipWinner = flagshipNominees.firstOrNull() ?: competitorNominees.first()
        if (flagshipWinner.isPlayer) {
            playerWonCount++
            totalPrizeWon += AwardCategory.FLAGSHIP_OF_THE_YEAR.prizeMoney
            reputationGained += AwardCategory.FLAGSHIP_OF_THE_YEAR.reputationBonus
        }
        awardResults.add(
            AwardResult(
                category = AwardCategory.FLAGSHIP_OF_THE_YEAR,
                winner = flagshipWinner,
                nominees = flagshipNominees,
                ceremonyReview = "${flagshipWinner.companyName} üretimi ${flagshipWinner.modelName}, olağanüstü ${flagshipWinner.score}/100 inceleme puanı ve tavizsiz donanımıyla ${year} Yılının En İyi Amiral Gemisi seçildi!"
            )
        )

        // 2. VALUE CHAMPION (Best score / price ratio)
        val valueNominees = allNominees.sortedByDescending { (it.score.toFloat() / it.price.coerceAtLeast(100).toFloat()) * 1000f }.take(4)
        val valueWinner = valueNominees.firstOrNull() ?: competitorNominees.first()
        if (valueWinner.isPlayer) {
            playerWonCount++
            totalPrizeWon += AwardCategory.VALUE_CHAMPION.prizeMoney
            reputationGained += AwardCategory.VALUE_CHAMPION.reputationBonus
        }
        awardResults.add(
            AwardResult(
                category = AwardCategory.VALUE_CHAMPION,
                winner = valueWinner,
                nominees = valueNominees,
                ceremonyReview = "${valueWinner.companyName} tarafından sunulan ${valueWinner.modelName}, $${valueWinner.price} fiyat etiketine karşılık sunduğu ${valueWinner.score} puanlık üstün deneyimle Fiyat/Performans Tacını kazandı!"
            )
        )

        // 3. INNOVATION AWARD (Favoring high tech score or special features)
        val innovationNominees = allNominees.shuffled().sortedByDescending {
            it.score + if (it.isPlayer) 5 else Random.nextInt(-3, 4)
        }.take(4)
        val innovationWinner = innovationNominees.firstOrNull() ?: competitorNominees.first()
        if (innovationWinner.isPlayer) {
            playerWonCount++
            totalPrizeWon += AwardCategory.INNOVATION_AWARD.prizeMoney
            reputationGained += AwardCategory.INNOVATION_AWARD.reputationBonus
        }
        awardResults.add(
            AwardResult(
                category = AwardCategory.INNOVATION_AWARD,
                winner = innovationWinner,
                nominees = innovationNominees,
                ceremonyReview = "${innovationWinner.companyName} ${innovationWinner.modelName}, endüstri standartlarını aşan cesur inovasyonları ve mühendislik başarısıyla Yılın İnovasyonu Ödülüne layık görüldü!"
            )
        )

        // 4. BEST DESIGN AWARD
        val designNominees = allNominees.shuffled().take(4).sortedByDescending { it.score }
        val designWinner = designNominees.firstOrNull() ?: competitorNominees.first()
        if (designWinner.isPlayer) {
            playerWonCount++
            totalPrizeWon += AwardCategory.BEST_DESIGN.prizeMoney
            reputationGained += AwardCategory.BEST_DESIGN.reputationBonus
        }
        awardResults.add(
            AwardResult(
                category = AwardCategory.BEST_DESIGN,
                winner = designWinner,
                nominees = designNominees,
                ceremonyReview = "${designWinner.companyName} tasarımı ${designWinner.modelName}, kusursuz malzeme kalitesi, ince çerçeveleri ve ergonomisiyle En İyi Endüstriyel Tasarım Ödülünü kazandı!"
            )
        )

        return TechExpoEvent(
            year = year,
            expoName = selectedExpo.first,
            city = selectedExpo.second,
            awards = awardResults,
            playerWonCount = playerWonCount,
            totalPrizeWon = totalPrizeWon,
            reputationGained = reputationGained
        )
    }

    val rivalOperatingSystems: List<CompetitorOsInfo> = listOf(
        CompetitorOsInfo(
            id = "os_ios",
            name = "iOS (Apple OS)",
            company = "Apple Inc.",
            iconEmoji = "🍎",
            licenseTypeBadge = "🔒 Kapalı / Tescilli",
            marketSharePercent = 23.5f,
            techScore = 96,
            ecosystemScore = 99,
            userBaseFormatted = "1.25 Milyar",
            appCountFormatted = "2.2M+ Uygulama",
            monthlyEcosystemRevenue = "$1,850,000,000",
            coreStrength = "Kusursuz donanım-yazılım optimizasyonu, Metal grafik motoru, rekor kâr marjı ve %98 kullanıcı bağlılığı.",
            mainFlaw = "Katı kapalı duvar bahçesi ve yan yükleme kısıtlamaları.",
            brandColorHex = 0xFF0F172A
        ),
        CompetitorOsInfo(
            id = "os_android",
            name = "Android (GMS/AOSP)",
            company = "Google LLC",
            iconEmoji = "🌐",
            licenseTypeBadge = "🌐 Açık Kaynak",
            marketSharePercent = 63.5f,
            techScore = 94,
            ecosystemScore = 98,
            userBaseFormatted = "3.20 Milyar",
            appCountFormatted = "3.8M+ Uygulama",
            monthlyEcosystemRevenue = "$2,400,000,000",
            coreStrength = "Dünya çapında binlerce OEM üretici desteği, Google Play Store ve derin Gemini AI entegrasyonu.",
            mainFlaw = "Cihaz parçalanması ve eski telefonlara güvenlik güncellemesi gecikmeleri.",
            brandColorHex = 0xFF10B981
        ),
        CompetitorOsInfo(
            id = "os_harmony",
            name = "HarmonyOS (Next)",
            company = "Huawei Technologies",
            iconEmoji = "🌸",
            licenseTypeBadge = "⚡ Mikroçekirdek",
            marketSharePercent = 8.2f,
            techScore = 90,
            ecosystemScore = 86,
            userBaseFormatted = "550 Milyon",
            appCountFormatted = "950K+ Uygulama",
            monthlyEcosystemRevenue = "$380,000,000",
            coreStrength = "Süper Cihaz dağıtık mikroçekirdek mimarisi, Kirin çip senkronizasyonu ve Asya pazarında dev sadakat.",
            mainFlaw = "Batı pazarlarında GMS kısıtları ve küresel uygulama adaptasyon süreci.",
            brandColorHex = 0xFFCF0A2C
        ),
        CompetitorOsInfo(
            id = "os_oneui",
            name = "One UI (Galaxy OS)",
            company = "Samsung Electronics",
            iconEmoji = "🌌",
            licenseTypeBadge = "📱 Özel Android Arayüzü",
            marketSharePercent = 18.8f,
            techScore = 92,
            ecosystemScore = 93,
            userBaseFormatted = "1.15 Milyar",
            appCountFormatted = "2.9M+ Uygulama",
            monthlyEcosystemRevenue = "$880,000,000",
            coreStrength = "DeX masaüstü modu, Galaxy AI üretkenlik araçları ve katlanabilir ekran optimizasyonu.",
            mainFlaw = "Geniş sistem boyutu ve yoğun arka plan bellek kullanımı.",
            brandColorHex = 0xFF1428A0
        ),
        CompetitorOsInfo(
            id = "os_hyperos",
            name = "HyperOS",
            company = "Xiaomi Corp",
            iconEmoji = "🟠",
            licenseTypeBadge = "⚡ Hibrit Kernel",
            marketSharePercent = 9.4f,
            techScore = 88,
            ecosystemScore = 87,
            userBaseFormatted = "620 Milyon",
            appCountFormatted = "2.1M+ Uygulama",
            monthlyEcosystemRevenue = "$440,000,000",
            coreStrength = "Vela IoT mimarisi, elektrikli otomobil & akıllı ev cihazları arası ultra hızlı bağlantı.",
            mainFlaw = "Sistem içi reklamlar ve agresif arka plan pil yönetimi kısıtları.",
            brandColorHex = 0xFFFF6900
        ),
        CompetitorOsInfo(
            id = "os_windows",
            name = "Windows 10 Mobile",
            company = "Microsoft",
            iconEmoji = "🪟",
            licenseTypeBadge = "🔒 Kapalı Kaynak",
            marketSharePercent = 0.6f,
            techScore = 75,
            ecosystemScore = 40,
            userBaseFormatted = "15 Milyon",
            appCountFormatted = "280K Uygulama",
            monthlyEcosystemRevenue = "$15,000,000",
            coreStrength = "Canlı kutucuklar (Live Tiles), Continuum masaüstü modu ve yerel Microsoft Office desteği.",
            mainFlaw = "Geliştiricilerin platformu terk etmesi ve kritik popüler uygulamaların eksikliği.",
            brandColorHex = 0xFF00A4EF
        )
    )
}

