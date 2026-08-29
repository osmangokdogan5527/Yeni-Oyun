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
enum class LaunchCampaign(
    val title: String,
    val cost: Long,
    val initialHype: Int,
    val minRecommendedScore: Int,
    val description: String,
    val riskWarning: String
) {
    ORGANIC(
        "Sıfır Reklam (Organik Lansman)",
        0L,
        12,
        0,
        "Sıfır pazarlama bütçesi. Tanıtım eksikliği nedeniyle ilk satışlar çok yavaş olur, ancak fiyasko veya iade riski yoktur.",
        "Düşük Talep Riski: Harika telefon sıfır reklamla zor satılır!"
    ),
    SOCIAL_MEDIA(
        "Sosyal Medya & Basın Bülteni",
        60000L,
        35,
        55,
        "Temel dijital reklamlar ve teknoloji basını duyuruları. Giriş ve orta segment için dengeli ve güvenli.",
        "Düşük Risk: Dengeli beklenti"
    ),
    VIRAL_INFLUENCER(
        "Viral Influencer & YouTube Lansmanı",
        200000L,
        65,
        70,
        "Teknoloji yayıncıları ve YouTube kanallarıyla büyük sponsorluklar. Güçlü erken satış patlaması.",
        "Orta Risk: Puan 70 altındaysa eleştiriler ve iadeler başlar!"
    ),
    GLOBAL_BILLBOARD(
        "Küresel Reklam & Şehir Panoları",
        600000L,
        90,
        80,
        "TV spotları ve metropol dev billboardları. Üst segment için devasa talep ve mağaza önü kuyrukları!",
        "Yüksek Risk: Puan 80 altındaysa hayal kırıklığı ve iade dalgası!"
    ),
    MEGA_CELEBRITY(
        "Süperstar & Küresel Sahne Şovu",
        1500000L,
        125,
        90,
        "Dünya yıldızlarıyla görkemli lansman etkinliği. Zirve Hype ve tarihi satış rekoru potansiyeli!",
        "Kritik Risk: En ufak donanım hatasında felaket ve itibar çöküşü!"
    )
}

@Serializable
enum class CampaignType(
    val title: String,
    val cost: Long,
    val durationMonths: Int,
    val boostPercent: Int,
    val hypeBoost: Int,
    val description: String,
    val riskWarning: String
) {
    SOCIAL_MEDIA(
        "Sosyal Medya Reklamı",
        50000L,
        3,
        25,
        20,
        "3 Ay boyunca +%25 satış talebi ve +20 Hype artışı",
        "Güvenli: Dengeli beklenti"
    ),
    INFLUENCER(
        "Influencer İşbirliği",
        150000L,
        3,
        50,
        40,
        "3 Ay boyunca +%50 satış talebi ve +40 Hype artışı",
        "Orta Risk: Kalite yetersizse tepki çeker"
    ),
    TV_COMMERCIAL(
        "TV & Küresel Reklam Kampanyası",
        400000L,
        6,
        80,
        65,
        "6 Ay boyunca +%80 satış talebi ve +65 Hype artışı",
        "Yüksek Risk: Beklenti-gerçeklik uçurumu iadelere yol açabilir!"
    )
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
data class ActiveOsDevelopment(
    val name: String,
    val targetVersion: String,
    val type: OsType,
    val licenseType: OsLicenseType,
    val focus: OsFocus,
    val themeColorHex: Long,
    val perDeviceLicenseFee: Int,
    val totalMonths: Int,
    val remainingMonths: Int,
    val cost: Long,
    val isMajorUpdate: Boolean,
    val qaInvestment: Long
)

@Serializable
data class CustomOsState(
    val stability: Int = 100,
    val bugsEncountered: Int = 0,
    val activeDevelopment: ActiveOsDevelopment? = null,
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
    val launchCampaign: LaunchCampaign = LaunchCampaign.ORGANIC,
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
    val producedStock: Int = totalStock, // Şimdiye kadar fabrikada fiilen üretilmiş toplam adet (eski kayıtlarda geriye dönük uyum için totalStock'a eşit başlar)
    val discountPercent: Int = 0, // İndirim Kampanyası (%0, %15, %30, %50)
    // Sadeleştirilmiş satış özeti (arayüzde gösterim için): 1.0 = nötr, >1.0 olumlu, <1.0 olumsuz etki
    val lastProductQualityScore: Float = 1f, // Kalite + tasarım/malzeme + OS uyumu
    val lastMarketDemandScore: Float = 1f, // Trend + renk çeşitliliği + kampanya + fiyat cazibesi
    val lastBrandStrengthScore: Float = 1f, // İtibar + pazar olgunluğu + seri sadakati
    // Hype & Müşteri Memnuniyeti Dengesi
    val hypeScore: Int = 20, // 0 - 140+ (Pazarlama beklentisi)
    val customerSatisfactionScore: Int = 80, // %0 - %100 (Gerçek memnuniyet)
    val totalRefundsCount: Int = 0, // Toplam iade adedi
    val lastPeriodRefunds: Int = 0, // Son 2 haftalık dönemdeki iade
    val lastPeriodRefundCost: Long = 0L, // Son 2 haftada iade edilen tutar
    val wordOfMouthBoost: Float = 1.0f // Ağızdan ağıza pazarlama çarpanı
) {
    val effectivePrice: Int
        get() = if (discountPercent > 0) (specs.price * (100 - discountPercent) / 100).coerceAtLeast(1) else specs.price

    val maxMonthsOnMarket: Int
        get() = if (reviewScore >= 60) 24 else 12

    val maxPeriodsOnMarket: Int
        get() = maxMonthsOnMarket * 2

    /** Üretimi hâlâ devam eden (fabrika kapasitesi bekleyen) sipariş kalıntısı var mı? */
    val hasPendingProduction: Boolean
        get() = producedStock < totalStock

    val isCompleted: Boolean
        get() = (remainingStock <= 0 && !hasPendingProduction) || periodsOnMarket >= maxPeriodsOnMarket || (periodsOnMarket == 0 && monthsOnMarket >= maxMonthsOnMarket)

    val satisfactionStatus: String
        get() = when {
            customerSatisfactionScore >= 88 -> "Beklentileri Aştı 🌟"
            customerSatisfactionScore >= 70 -> "Memnun & Sadık 👍"
            customerSatisfactionScore >= 50 -> "Dengeli / Karışık 😐"
            customerSatisfactionScore >= 30 -> "Hayal Kırıklığı 📉"
            else -> "Büyük Fiyasko & İade 🚨"
        }

    val hypeStatus: String
        get() = when {
            hypeScore >= 90 -> "Zirve Hype 🔥"
            hypeScore >= 60 -> "Yüksek Beklenti 🚀"
            hypeScore >= 30 -> "Dengeli Tanıtım 📢"
            else -> "Düşük / Sessiz 💤"
        }

    /** 1.0 nötr referans alınarak bir skor grubunu basit bir Türkçe etikete çevirir (arayüzde gösterim için). */
    private fun scoreLabel(score: Float): String = when {
        score >= 1.35f -> "Mükemmel"
        score >= 1.10f -> "İyi"
        score >= 0.90f -> "Orta"
        score >= 0.65f -> "Zayıf"
        else -> "Kötü"
    }

    val productQualityLabel: String get() = scoreLabel(lastProductQualityScore)
    val marketDemandLabel: String get() = scoreLabel(lastMarketDemandScore)
    val brandStrengthLabel: String get() = scoreLabel(lastBrandStrengthScore)
}

@Serializable
enum class TrendCategory(val title: String, val icon: String, val tip: String) {
    HIGH_REFRESH_DISPLAY("Yüksek Yenileme Hızlı Ekran", "⚡", "120Hz/144Hz/240Hz ekran veya Oyuncu tarzı kullanın."),
    CAMERA_PRO("Gelişmiş Kamera & Özçekim", "📸", "HD, Çift, Üçlü veya Periskop kamera seçin."),
    LONG_BATTERY("Yüksek Kapasiteli Batarya", "🔋", "Yüksek mAh kapasiteli batarya veya hızlı şarj seçin."),
    BUDGET_VALUE("Ekonomik Fiyat / Performans", "🏷️", "Cihaz satış fiyatını $400 veya altına ayarlayın."),
    PREMIUM_BUILD("Premium Kasa & Arka Kapak", "💎", "Alüminyum veya Cam kasa malzemesi kullanın."),
    AI_PROCESSOR("Yapay Zeka & Güçlü İşlemci", "🧠", "Yüksek performanslı işlemci veya 12GB+ RAM seçin."),
    FAST_CONNECTIVITY("Hızlı Mobil Şebeke & Bağlantı", "📶", "4G LTE, 5G veya hızlı Wi-Fi bağlantısı seçin.")
}

@Serializable
data class MarketTrend(
    val id: String,
    val title: String,
    val description: String,
    val category: TrendCategory,
    val bonusMultiplier: Float = 1.5f,
    val remainingMonths: Int = 4,
    val totalDurationMonths: Int = 4,
    val tip: String = ""
) {
    val bonusPercent: Int
        get() = kotlin.math.round((bonusMultiplier - 1.0f) * 100).toInt()

    val effectiveTip: String
        get() = if (tip.isNotBlank()) tip else category.tip
}

// --- M&A (Mergers and Acquisitions) Classes ---

@Serializable
enum class CompanyType(val description: String, val baseMultiplier: Float) {
    STRUGGLING("Zor Durumda", 0.9f),
    NORMAL("Normal", 1.5f),
    SUCCESSFUL("Başarılı", 2.5f),
    TECH_STARTUP("Teknoloji Startup'ı", 1.2f)
}

@Serializable
enum class PostAcquisitionStrategy(val title: String, val description: String) {
    BECOME_MAIN_BRAND(
        "Yeni Sahibi & CEO'su Ol (Ana Şirketim Yap)",
        "Satın alınan şirketin (Örn: Apple, Samsung) doğrudan yeni sahibi olun! Şirket adı, logosu, pazar payı ve modelleri ana markanız haline gelir."
    ),
    INDEPENDENT_BRAND(
        "Holdinge Bağlı Alt Marka Yap",
        "Şirket holdinginiz çatısı altında bağımsız çalışmaya devam eder ve her ay kasaya düzenli kâr payı (temettü) öder."
    ),
    MERGE_TO_MAIN(
        "Mevcut Şirkete Kat & Varlıkları Birleştir",
        "Şirketin pazar payı, mühendisleri ve üretim hatları doğrudan mevcut şirketinize transfer edilir, marka pazardan çekilir."
    ),
    LIQUIDATE_ASSETS(
        "Varlıkları Tasfiye Et & Nakde Çevir",
        "Şirketin nakit kasası, patentleri ve çalışanları devralınır, marka kapatılır."
    )
}

@Serializable
data class PhoneSeriesLegacy(
    val seriesName: String,
    val originCompanyId: String = "PLAYER",
    val launchYear: Int,
    val totalModelsReleased: Int = 1,
    val averageReviewScore: Int = 75,
    val seriesReputation: Int = 70, // 0-100
    val totalSales: Long = 0L,
    val isActive: Boolean = true
)

@Serializable
data class OwnedSubBrand(
    val id: String,
    val name: String,
    val logoEmoji: String = "📱",
    val brandReputation: Int = 80,
    val cash: Long = 0L,
    val autoManage: Boolean = true,
    val brandColorHex: Long = 0xFF2563EB,
    val marketSharePercent: Float = 0f,
    val monthlySales: Int = 0,
    val monthlyDividend: Long = 0L,
    val strategyType: String = "Bağımsız İştirak Markası",
    val logoId: String? = null
)

@Serializable
data class AcquisitionTarget(
    val id: String,
    val name: String,
    val logoEmoji: String,
    val type: CompanyType,
    val cash: Long,
    val debt: Long,
    val brandReputation: Int,
    val employees: Int,
    val patents: List<String>,
    val activeSeries: List<PhoneSeriesLegacy>,
    val valuation: Long,
    val minimumAcceptableMultiplier: Float,
    val remainingMonthsAvailable: Int = 12
)

// ----------------------------------------------

@Serializable
data class CompetitorCompany(
    val id: String,
    val name: String, // "Apple", "Samsung", "Xiaomi", "Google", "Oppo" etc.
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
    val weaknessText: String,
    val isAcquiredByPlayer: Boolean = false
) {
    val estimatedValuation: Long
        get() {
            val annualSalesRevenue = monthlySales.toLong() * currentModelPrice.coerceAtLeast(120) * 12L
            val shareBase = (marketSharePercent * 320_000_000L).toLong()
            val prestigeMultiplier = when {
                name.contains("Apple", ignoreCase = true) || id == "comp_apple" -> 2.6f
                name.contains("Samsung", ignoreCase = true) || id == "comp_samsung" -> 2.2f
                name.contains("Xiaomi", ignoreCase = true) || id == "comp_xiaomi" -> 1.6f
                name.contains("Huawei", ignoreCase = true) || id == "comp_huawei" -> 1.5f
                name.contains("Google", ignoreCase = true) || id == "comp_google" -> 1.5f
                name.contains("Oppo", ignoreCase = true) || name.contains("Vivo", ignoreCase = true) -> 1.35f
                name.contains("Sony", ignoreCase = true) || name.contains("OnePlus", ignoreCase = true) -> 1.25f
                name.contains("Motorola", ignoreCase = true) || name.contains("Realme", ignoreCase = true) || name.contains("Honor", ignoreCase = true) -> 1.15f
                else -> 1.0f
            }
            val calculated = ((annualSalesRevenue * 1.6f + shareBase) * prestigeMultiplier).toLong()
            return calculated.coerceAtLeast(40_000_000L)
        }
}

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
    val headline: String,
    val processor: String = "Gelişmiş Mobil Çip",
    val ram: String = "8 GB",
    val camera: String = "50 MP Pro Sensör",
    val battery: String = "4500 mAh",
    val display: String = "120Hz OLED",
    val vsPlayerModelName: String? = null,
    val vsPlayerModelScore: Int? = null,
    val vsPlayerModelPrice: Int? = null,
    val duelVerdict: String? = null // e.g. "Oyuncu Üstün Çıktı", "Kafa Kafaya", "Rakip Öne Geçti"
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
enum class HardwareCrisisType(
    val title: String,
    val iconEmoji: String,
    val description: String,
    val typicalCulprit: String
) {
    BATTERY_OVERHEATING(
        "Batarya Aşırı Isınma & Şişme",
        "🔥",
        "Kullanıcılar cihazın şarjda veya oyun sırasında aşırı ısındığını, bazı bataryaların genleştiğini bildiriyor.",
        "Batarya Kalitesi / Hızlı Şarj Entegrasyonu"
    ),
    CHASSIS_BENDGATE(
        "Kasa Yapısal Bükülme (Bendgate)",
        "📐",
        "Cihazın cepte taşınırken veya hafif baskıda şasiden kalıcı olarak büküldüğü videolar sosyal medyada viral oldu.",
        "Kasa Malzemesi & İnce Gövde Dayanımı"
    ),
    DISPLAY_GREEN_LINE(
        "Ekran Yeşil Çizgi & Titreme Kusuru",
        "🟢",
        "Yazılım ve panel sürücüsü uyumsuzluğu nedeniyle ekranda dikey kalıcı yeşil çizgiler ve titremeler beliriyor.",
        "Ekran Paneli Kalite Kontrolü & QA Eksikliği"
    ),
    CAMERA_FOCUS_BLUR(
        "Kamera OIS & Odaklama Arızası",
        "📷",
        "Fotoğraf çekerken mekanik lens motorunun titrediği ve sürekli bulanık odaklama yaptığı tespit edildi.",
        "Kamera Modülü & Optik Kalibrasyon"
    ),
    SOC_THROTTLING(
        "Aşırı Isınan İşlemci & Ani Kasma",
        "⚡",
        "İşlemci termal sınırları aştığı için cihaz birkaç dakika içinde frekans düşürüyor ve arayüz donuyor.",
        "Termal Soğutma Bloğu & Çip Güç Optimizasyonu"
    )
}

@Serializable
data class HardwareCrisis(
    val id: String,
    val modelId: String,
    val modelName: String,
    val crisisType: HardwareCrisisType,
    val severityLevel: Int = 1, // 1: Hafif/Orta, 2: Ciddi, 3: Kritik Kriz
    val yearTriggered: Int,
    val monthTriggered: Int,
    val periodTriggered: Int,
    val affectedUnitsCount: Int,
    val isResolved: Boolean = false,
    val resolvedYear: Int? = null,
    val resolvedMonth: Int? = null,
    val resolutionChoice: String? = null // "Yazılım Yaması", "Ücretsiz Servis Tamiri", "Tam Geri Çağırma (Recall)"
)

@Serializable
enum class CrisisResolutionStrategy(
    val title: String,
    val iconEmoji: String,
    val costType: String,
    val repImpactText: String,
    val salesImpactText: String,
    val description: String
) {
    SOFTWARE_PATCH_LIMIT(
        "Acil Yazılım Güncellemesi & Açıklama",
        "💻",
        "Çok Düşük Maliyet ($50.000)",
        "-4 İtibar Kaybı",
        "Satış Talebi %15 Azalır",
        "Donanım performansını/şarj hızını yazılımla kısıtlayarak sorunu maskeleyin ve resmi özür yayımlayın."
    ),
    FREE_SERVICE_REPAIR(
        "Yetkili Servislerde Ücretsiz Parça Onarımı",
        "🔧",
        "Orta Maliyet (Cihaz Başı $25)",
        "+2 İtibar (Müşteri Desteği)",
        "Satışlar Hızla Normale Döner",
        "Sorun yaşayan tüm müşterilere servislerde ücretsiz modül ve batarya/panel değişimi sağlayın."
    ),
    FULL_RECALL_REFUND(
        "Tam Geri Çağırma (Full Recall) & Para İadesi",
        "🚨",
        "Yüksek Maliyet (Birim Satış Bedeli + %15)",
        "+12 Tüketici Güveni & Prestij",
        "Model Satıştan Çekilir",
        "Cihazı tüm dünyada satıştan çekin, satılan cihazların ücretini eksiksiz iade edin. Cesur kriz liderliği takdir toplar!"
    )
}

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
    val budget: Long = 1200000,
    val monthlyIncome: Long = 0,
    val rdSpending: Long = 0,
    val reputation: Int = 5,
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
    val acquisitionTargets: List<AcquisitionTarget> = emptyList(),
    val ownedSubBrands: List<OwnedSubBrand> = emptyList(),
    val ownedLegacySeries: List<PhoneSeriesLegacy> = emptyList(),
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
    val reputationMomentum: Float = 0f, // Kademeli itibar sürüklenmesinin kesirli birikimi (ani zıplama yerine yumuşak geçiş için)
    val activeLoans: List<BankLoan> = emptyList(),
    val creditScore: Int = 750, // 300 - 900 Kredi Notu (Düzenli geri ödemelerle yükselir, kredi faizlerini düşürür)
    val equitySoldPercent: Int = 0, // Yatırımcılara satılan toplam hisse payı (%25'e kadar)
    val patentLiquidationCooldown: Int = 0, // Patent satışı bekleme süresi (Dönem cinsinden)
    val activeHardwareCrises: List<HardwareCrisis> = emptyList() // Aktif veya geçmiş donanım krizleri ve geri çağırmalar
) {
    val currentOfficeTier: OfficeTier
        get() = OFFICE_TIERS.firstOrNull { it.level == officeLevel } ?: OFFICE_TIERS.first()

    val currentFactoryTier: FactoryTier
        get() = FACTORY_TIERS.firstOrNull { it.level == factoryLevel } ?: FACTORY_TIERS.first()

    val totalEmployees: Int
        get() = engineers + qaInspectors + assemblyWorkers

    val maxEmployees: Int
        get() = currentOfficeTier.maxEmployees

    /**
     * Şirket büyüdükçe artan masraf çarpanı (Wealth Tax / Scale Overhead).
     * Bütçe 10 milyonun altındayken 1.0x, üstündeyken logaritmik olarak artarak
     * ofis, fabrika ve maaş giderlerini katlar. (Zorluğu korumak için).
     */
    val scaleMultiplier: Double
        get() {
            val wealthMillions = (budget / 1_000_000.0).coerceAtLeast(0.0)
            return if (wealthMillions <= 10.0) {
                1.0
            } else {
                // Her 10 katı büyümede masraflar ~%120 daha fazla artar
                1.0 + Math.log10(wealthMillions / 10.0) * 1.2
            }
        }

    val engineerSalary: Long get() = (engineers * 8000L * scaleMultiplier).toLong()
    val qaSalary: Long get() = (qaInspectors * 5000L * scaleMultiplier).toLong()
    val workerSalary: Long get() = (assemblyWorkers * 3000L * scaleMultiplier).toLong()
    val totalSalaries: Long get() = engineerSalary + qaSalary + workerSalary

    val officeExpense: Long get() = (currentOfficeTier.monthlyRent * scaleMultiplier).toLong()
    val factoryMaintenance: Long get() = (currentFactoryTier.monthlyMaintenance * scaleMultiplier).toLong()
    val osMaintenanceExpense: Long 
        get() = (((if (customOs.type != OsType.STOCK_ANDROID) customOs.type.monthlyMaintenance else 0L) + customOs.updateGuarantee.monthlyCost) * scaleMultiplier).toLong()

    val totalLoanPeriodPayments: Long get() = activeLoans.sumOf { it.periodPayment }
    val totalDebt: Long get() = activeLoans.sumOf { it.remainingBalance }

    val totalMonthlyExpenses: Long get() = totalSalaries + officeExpense + factoryMaintenance + osMaintenanceExpense + (totalLoanPeriodPayments * 2)

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

    // --- DEĞERLEME & M&A FİNANS MOTORU ---
    val factoryValuation: Long
        get() = when (factoryLevel) {
            0 -> 10_000_000L
            1 -> 35_000_000L
            2 -> 120_000_000L
            3 -> 400_000_000L
            4 -> 1_200_000_000L
            else -> 3_500_000_000L
        }

    val officeValuation: Long
        get() = when (officeLevel) {
            0 -> 3_000_000L
            1 -> 12_000_000L
            2 -> 35_000_000L
            3 -> 120_000_000L
            else -> 400_000_000L
        }

    val employeeValuation: Long
        get() = (engineers * 80_000L) + (qaInspectors * 50_000L) + (assemblyWorkers * 25_000L)

    val techAndChipValuation: Long
        get() {
            val techVal = unlockedTech.size * 12_000_000L
            val chipVal = customChipsets.size * 40_000_000L
            val osVal = if (customOs.type != OsType.STOCK_ANDROID) {
                (customOs.thirdPartyActiveDevices * 75L + customOs.ecosystemScore * 2_000_000L).coerceAtLeast(20_000_000L)
            } else 0L
            return techVal + chipVal + osVal
        }

    val marketShareValuation: Long
        get() {
            val shareVal = (playerMarketSharePercent * 400_000_000L).toLong()
            val runRateVal = (monthlyIncome.coerceAtLeast(0L) * 6L)
            return shareVal + runRateVal
        }

    val subBrandsValuation: Long
        get() = ownedSubBrands.sumOf { (it.monthlyDividend * 24L).coerceAtLeast(30_000_000L) }

    val reputationMultiplier: Float
        get() = (1.0f + (reputation / 75f)).coerceIn(0.8f, 3.0f)

    /**
     * Şirketin toplam kurumsal piyasa değeri (Enterprise Valuation).
     * Kasa + Tesisler + İK + Ar-Ge/Patentler + Pazar Payı & Gelir + Alt Markalar x İtibar Çarpanı.
     */
    val playerValuation: Long
        get() {
            val baseCash = budget.coerceAtLeast(0L)
            val sumAssets = baseCash + factoryValuation + officeValuation + employeeValuation + techAndChipValuation + marketShareValuation + subBrandsValuation
            val total = (sumAssets * reputationMultiplier).toLong()
            return total.coerceAtLeast(25_000_000L)
        }
}

/**
 * Para birimini kompakt ve okunabilir biçimde formatlar ($1.25B, $450M, $12K vb.)
 */
fun formatShortCurrency(amount: Long): String {
    val absAmount = kotlin.math.abs(amount)
    val sign = if (amount < 0) "-" else ""
    return when {
        absAmount >= 1_000_000_000_000L -> "$sign$${"%.2f".format(absAmount / 1_000_000_000_000.0)}T"
        absAmount >= 1_000_000_000L -> "$sign$${"%.2f".format(absAmount / 1_000_000_000.0)}B"
        absAmount >= 1_000_000L -> "$sign$${"%.1f".format(absAmount / 1_000_000.0)}M"
        absAmount >= 1_000L -> "$sign$${"%,d".format(absAmount / 1000)}K"
        else -> "$sign$$absAmount"
    }
}

