package com.example.viewmodel

import kotlinx.serialization.Serializable

/**
 * Şirketin bütçe, kredi, borç ve maliyet operasyonlarını yöneten modüler Finans Durumu.
 * Yalnızca finansal değişikliklerde ilgili bileşenlerin tetiklenmesini sağlar.
 */
@Serializable
data class CompanyFinanceState(
    val budget: Long = 1200000L,
    val monthlyIncome: Long = 0L,
    val totalMonthlyExpenses: Long = 0L,
    val totalSalaries: Long = 0L,
    val engineerSalary: Long = 0L,
    val qaSalary: Long = 0L,
    val workerSalary: Long = 0L,
    val officeExpense: Long = 0L,
    val factoryMaintenance: Long = 0L,
    val osMaintenanceExpense: Long = 0L,
    val totalDebt: Long = 0L,
    val totalLoanPeriodPayments: Long = 0L,
    val activeLoans: List<BankLoan> = emptyList(),
    val creditScore: Int = 750,
    val equitySoldPercent: Int = 0,
    val patentLiquidationCooldown: Int = 0,
    val totalChipsetOemRevenue: Long = 0L,
    val lastPeriodChipsetOemRevenue: Long = 0L,
    val scaleMultiplier: Double = 1.0
)

/**
 * Pazar payı, rakipler, trendler, lansman düelloları ve haberleri yöneten modüler Pazar Durumu.
 */
@Serializable
data class MarketEcosystemState(
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
    val reports: List<MarketReport> = emptyList(),
    val newsList: List<NewsArticle> = emptyList(),
    val activeTechExpo: TechExpoEvent? = null,
    val pastTechExpos: List<TechExpoEvent> = emptyList(),
    val unlockedAchievementIds: List<String> = emptyList()
)

/**
 * Fabrika, ofis çalışanları, aktif cihaz modelleri ve üretim operasyonlarını yöneten modüler Üretim Durumu.
 */
@Serializable
data class ProductionOpsState(
    val manufacturedPhones: List<PhoneSpecs> = emptyList(),
    val activeModels: List<ActiveModel> = emptyList(),
    val officeLevel: Int = 1,
    val factoryLevel: Int = 0,
    val currentOfficeTier: OfficeTier = OFFICE_TIERS.first(),
    val currentFactoryTier: FactoryTier = FACTORY_TIERS.first(),
    val engineers: Int = 3,
    val qaInspectors: Int = 2,
    val assemblyWorkers: Int = 15,
    val totalEmployees: Int = 20,
    val maxEmployees: Int = 25,
    val workerDiscountPercent: Float = 0f,
    val unitCostDiscountPercent: Float = 0f,
    val qaScoreBonus: Int = 0,
    val engineerTechBonus: Int = 0,
    val activeSupplyChainEvent: SupplyChainEvent? = null,
    val activeHardwareCrises: List<HardwareCrisis> = emptyList()
) {
    fun marginalEngineerBonus(): Int = ((engineers + 1).let { n -> (kotlin.math.sqrt(n.toDouble()) * 3.5).toInt().coerceAtMost(30) }) - engineerTechBonus
    fun marginalQaBonus(): Int = ((qaInspectors + 1).let { n -> (kotlin.math.sqrt(n.toDouble()) * 2.6).toInt().coerceAtMost(20) }) - qaScoreBonus
    fun marginalWorkerDiscount(): Float = (((assemblyWorkers + 1).let { n -> (kotlin.math.sqrt(n.toDouble()) * 0.97).toFloat().coerceAtMost(22f) }) - workerDiscountPercent)
}

/**
 * İşletim sistemi mimarisi, özel çip tasarım stüdyosu ve teknoloji araştırmalarını yöneten modüler Teknoloji Durumu.
 */
@Serializable
data class TechSoftwareState(
    val techLevel: String = "Giriş",
    val unlockedTech: List<String> = emptyList(),
    val activeResearch: ActiveResearch? = null,
    val researchQueue: List<ActiveResearch> = emptyList(),
    val customOs: CustomOsState = CustomOsState(),
    val customChipsets: List<CustomChipset> = emptyList(),
    val totalChipsetOemRevenue: Long = 0L,
    val lastPeriodChipsetOemRevenue: Long = 0L,
    val lastPeriodChipsetsSold: Int = 0
)

/**
 * Şirket kimliği, zaman akışı, satın alma hedefleri ve kurumsal profil durumunu yöneten modüler Şirket Durumu.
 */
@Serializable
data class CompanyProfileState(
    val companyName: String = "Apex Mobile",
    val companyLogoId: String = "ic_logo_diamond",
    val companyLogoStyle: String = "Minimal Elmas",
    val companyBrandColorHex: Long = 0xFF2563EB,
    val companySlogan: String = "Geleceğin Akıllı Telefonları",
    val isCompanySetupDone: Boolean = false,
    val reputation: Int = 5,
    val reputationMomentum: Float = 0f,
    val year: Int = 2010,
    val month: Int = 1,
    val period: Int = 1,
    val noticeMessage: String? = null,
    val acquisitionTargets: List<AcquisitionTarget> = emptyList(),
    val ownedSubBrands: List<OwnedSubBrand> = emptyList(),
    val ownedLegacySeries: List<PhoneSeriesLegacy> = emptyList()
)

// Extension mapper fonksiyonları
fun GameState.toFinanceState(): CompanyFinanceState = CompanyFinanceState(
    budget = budget,
    monthlyIncome = monthlyIncome,
    totalMonthlyExpenses = totalMonthlyExpenses,
    totalSalaries = totalSalaries,
    engineerSalary = engineerSalary,
    qaSalary = qaSalary,
    workerSalary = workerSalary,
    officeExpense = officeExpense,
    factoryMaintenance = factoryMaintenance,
    osMaintenanceExpense = osMaintenanceExpense,
    totalDebt = totalDebt,
    totalLoanPeriodPayments = totalLoanPeriodPayments,
    activeLoans = activeLoans,
    creditScore = creditScore,
    equitySoldPercent = equitySoldPercent,
    patentLiquidationCooldown = patentLiquidationCooldown,
    totalChipsetOemRevenue = totalChipsetOemRevenue,
    lastPeriodChipsetOemRevenue = lastPeriodChipsetOemRevenue,
    scaleMultiplier = scaleMultiplier
)

fun GameState.toMarketState(): MarketEcosystemState = MarketEcosystemState(
    currentTrend = currentTrend,
    competitors = competitors,
    competitorReleases = competitorReleases,
    playerMarketSharePercent = playerMarketSharePercent,
    totalMarketMonthlyVolume = totalMarketMonthlyVolume,
    reports = reports,
    newsList = newsList,
    activeTechExpo = activeTechExpo,
    pastTechExpos = pastTechExpos,
    unlockedAchievementIds = unlockedAchievementIds
)

fun GameState.toProductionState(): ProductionOpsState = ProductionOpsState(
    manufacturedPhones = manufacturedPhones,
    activeModels = activeModels,
    officeLevel = officeLevel,
    factoryLevel = factoryLevel,
    currentOfficeTier = currentOfficeTier,
    currentFactoryTier = currentFactoryTier,
    engineers = engineers,
    qaInspectors = qaInspectors,
    assemblyWorkers = assemblyWorkers,
    totalEmployees = totalEmployees,
    maxEmployees = maxEmployees,
    workerDiscountPercent = workerDiscountPercent,
    unitCostDiscountPercent = unitCostDiscountPercent,
    qaScoreBonus = qaScoreBonus,
    engineerTechBonus = engineerTechBonus,
    activeSupplyChainEvent = activeSupplyChainEvent,
    activeHardwareCrises = activeHardwareCrises
)

fun GameState.toTechSoftwareState(): TechSoftwareState = TechSoftwareState(
    techLevel = techLevel,
    unlockedTech = unlockedTech,
    activeResearch = activeResearch,
    researchQueue = researchQueue,
    customOs = customOs,
    customChipsets = customChipsets,
    totalChipsetOemRevenue = totalChipsetOemRevenue,
    lastPeriodChipsetOemRevenue = lastPeriodChipsetOemRevenue,
    lastPeriodChipsetsSold = lastPeriodChipsetsSold
)

fun GameState.toCompanyProfileState(): CompanyProfileState = CompanyProfileState(
    companyName = companyName,
    companyLogoId = companyLogoId,
    companyLogoStyle = companyLogoStyle,
    companyBrandColorHex = companyBrandColorHex,
    companySlogan = companySlogan,
    isCompanySetupDone = isCompanySetupDone,
    reputation = reputation,
    reputationMomentum = reputationMomentum,
    year = year,
    month = month,
    period = period,
    noticeMessage = noticeMessage,
    acquisitionTargets = acquisitionTargets,
    ownedSubBrands = ownedSubBrands,
    ownedLegacySeries = ownedLegacySeries
)
