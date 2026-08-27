package com.example.viewmodel

import com.example.model.BenchmarkScore
import kotlinx.serialization.Serializable

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
    val totalCloudRevenueToDate: Long = 0L,
    val popularityHistory: List<Float> = emptyList()
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
    val screenSizeInch: Float = 6.1f,
    val thicknessMm: Float = 8.0f,
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
    val totalStock: Int, // Hedeflenen/sipariş edilen toplam üretim miktarı
    val remainingStock: Int, // Şu an satışa hazır, üretilmiş ve satılmamış envanter
    val totalSold: Int = 0,
    val totalRevenue: Long = 0,
    val periodsOnMarket: Int = 0, // 2 periyot = 1 ay
    val monthsOnMarket: Int = 0,
    val reviewScore: Int,
    val launchYear: Int,
    val launchMonth: Int,
    val isExtendedNewsSent: Boolean = false,
    val activeCampaign: ActiveCampaign? = null,
    val matchesTrend: Boolean = false,
    val benchmarkScore: BenchmarkScore? = null,
    val recallRiskPercent: Int = 0,
    val isRecalled: Boolean = false,
    val recalledYear: Int? = null,
    val recalledMonth: Int? = null,
    val producedStock: Int = totalStock // Şimdiye kadar fabrikada fiilen üretilmiş toplam adet (eski kayıtlarda geriye dönük uyum için totalStock'a eşit başlar)
) {
    val maxMonthsOnMarket: Int
        get() = if (reviewScore >= 60) 24 else 12

    val maxPeriodsOnMarket: Int
        get() = maxMonthsOnMarket * 2

    /** Üretimi hâlâ devam eden (fabrika kapasitesi bekleyen) sipariş kalıntısı var mı? */
    val hasPendingProduction: Boolean
        get() = producedStock < totalStock

    val isCompleted: Boolean
        get() = (remainingStock <= 0 && !hasPendingProduction) || periodsOnMarket >= maxPeriodsOnMarket || (periodsOnMarket == 0 && monthsOnMarket >= maxMonthsOnMarket)
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
    val upgradeCost: Long,
    val periodCapacity: Int // Bu tesiste 2 haftalık periyotta üretilebilecek maksimum toplam cihaz adedi
)

val FACTORY_TIERS = listOf(
    FactoryTier(0, "Atölye (Fason Üretim)", 15, 0f, 10000L, 0L, periodCapacity = 4000),
    FactoryTier(1, "Küçük Ölçekli Fabrika", 40, 10f, 50000L, 1000000L, periodCapacity = 18000),
    FactoryTier(2, "Otomatik Seri Üretim Fabrikası", 100, 20f, 180000L, 5000000L, periodCapacity = 70000),
    FactoryTier(3, "Mega Akıllı Robotik Fabrika", 300, 35f, 500000L, 20000000L, periodCapacity = 260000)
)

@Serializable
data class ActiveResearch(
    val techId: String,
    val techName: String,
    val totalMonths: Int,
    val remainingMonths: Int,
    val cost: Long
)

val DEFAULT_COMPETITORS = listOf(
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
    val period: Int = 1, // 1: Ayın 1. Yarısı (1-15 Gün / 2 Hafta), 2: Ayın 2. Yarısı (16-30 Gün / 2 Hafta)
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
    val competitors: List<CompetitorCompany> = DEFAULT_COMPETITORS,
    val competitorReleases: List<CompetitorReleaseHistory> = emptyList(),
    val playerMarketSharePercent: Float = 0f,
    val totalMarketMonthlyVolume: Int = 800000,
    val customOs: CustomOsState = CustomOsState(),
    val customChipsets: List<CustomChipset> = emptyList(),
    val totalChipsetOemRevenue: Long = 0L,
    val lastPeriodChipsetOemRevenue: Long = 0L,
    val lastPeriodChipsetsSold: Int = 0,
    val activeTechExpo: TechExpoEvent? = null,
    val pastTechExpos: List<TechExpoEvent> = emptyList(),
    val unlockedAchievementIds: List<String> = emptyList(),
    val lastUnlockedAchievementIds: List<String> = emptyList(),
    val activeSupplyChainEvent: SupplyChainEvent? = null,
    val reputationMomentum: Float = 0f // Kademeli itibar sürüklenmesinin kesirli birikimi (ani zıplama yerine yumuşak geçiş için)
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

    /**
     * Montaj işçisi birim üretim maliyeti indirimi. Azalan verim eğrisi kullanır:
     * ilk işçiler orantısal olarak daha değerli, kalabalık ekiplerde katkı payı
     * giderek küçülür (yönetim/koordinasyon yükü gerçekçiliği). %22 tavanı vardır.
     */
    val workerDiscountPercent: Float
        get() = if (assemblyWorkers <= 0) 0f else
            (kotlin.math.sqrt(assemblyWorkers.toDouble()) * 0.97).toFloat().coerceAtMost(22f)

    val unitCostDiscountPercent: Float
        get() = (workerDiscountPercent + currentFactoryTier.discountPercent).coerceAtMost(55f)

    /**
     * QA ekibinin sabit kalite puanı katkısı. Azalan verim eğrisiyle hesaplanır ve
     * cihaz başına harcanan QA bütçesinin (manufacturePhone) yerini almaz, onu tamamlar —
     * böylece sadece kalabalık QA ekibi kurmak, dönemsel test bütçesi ayırmanın
     * yerini tamamen alamaz.
     */
    val qaScoreBonus: Int
        get() = if (qaInspectors <= 0) 0 else
            (kotlin.math.sqrt(qaInspectors.toDouble()) * 2.6).toInt().coerceAtMost(20)

    /**
     * Mühendislerin teknolojik yıpranmayı (tech penalty) azaltma gücü. Azalan verim
     * eğrisiyle hesaplanır; devasa mühendis ordusu teknoloji güncelliği sorununu
     * tamamen bertaraf edemez, sadece büyük ölçüde hafifletir (30 puan tavanı).
     */
    val engineerTechBonus: Int
        get() = if (engineers <= 0) 0 else
            (kotlin.math.sqrt(engineers.toDouble()) * 3.5).toInt().coerceAtMost(30)

    /** Bir sonraki işe alınacak çalışanın marjinal (ek) katkısını gösterir — UI'da "sıradaki kişi ne katar" bilgisi için. */
    fun marginalEngineerBonus(): Int = ((engineers + 1).let { n -> (kotlin.math.sqrt(n.toDouble()) * 3.5).toInt().coerceAtMost(30) }) - engineerTechBonus
    fun marginalQaBonus(): Int = ((qaInspectors + 1).let { n -> (kotlin.math.sqrt(n.toDouble()) * 2.6).toInt().coerceAtMost(20) }) - qaScoreBonus
    fun marginalWorkerDiscount(): Float = (((assemblyWorkers + 1).let { n -> (kotlin.math.sqrt(n.toDouble()) * 0.97).toFloat().coerceAtMost(22f) }) - workerDiscountPercent)
}
