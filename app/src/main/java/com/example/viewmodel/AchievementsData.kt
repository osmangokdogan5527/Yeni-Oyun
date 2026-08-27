package com.example.viewmodel

import kotlinx.serialization.Serializable

@Serializable
enum class AchievementTier(val displayName: String, val colorHex: Long) {
    BRONZE("Bronz", 0xFFCD7F32),
    SILVER("Gümüş", 0xFF94A3B8),
    GOLD("Altın", 0xFFFACC15),
    PLATINUM("Platin", 0xFF38BDF8)
}

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val tier: AchievementTier,
    val condition: (GameState) -> Boolean
)

/**
 * Toplam ömür boyu satılan cihaz adedi (tamamlanmış modeller dahil, activeModels listesinden
 * hiçbir model asla silinmediği için burada tüm zamanların toplamı hesaplanır).
 */
val GameState.lifetimeUnitsSold: Int
    get() = activeModels.sumOf { it.totalSold }

val GameState.lifetimeRevenue: Long
    get() = activeModels.sumOf { it.totalRevenue }

val GameState.lifetimeRecalls: Int
    get() = activeModels.count { it.isRecalled }

val GameState.bestReviewScore: Int
    get() = activeModels.maxOfOrNull { it.reviewScore } ?: 0

val ALL_ACHIEVEMENTS: List<Achievement> = listOf(
    // --- İLK ADIMLAR (Bronz) ---
    Achievement(
        id = "first_launch",
        title = "İlk Adım",
        description = "Piyasaya ilk telefon modelini sür.",
        icon = "📱",
        tier = AchievementTier.BRONZE,
        condition = { it.activeModels.isNotEmpty() }
    ),
    Achievement(
        id = "first_thousand_sold",
        title = "İlk Bin",
        description = "Ömür boyu toplamda 1.000 adet cihaz sat.",
        icon = "📦",
        tier = AchievementTier.BRONZE,
        condition = { it.lifetimeUnitsSold >= 1_000 }
    ),
    Achievement(
        id = "first_employee",
        title = "Ekip Kuruluyor",
        description = "İlk kez ekibine bir çalışan kat.",
        icon = "👥",
        tier = AchievementTier.BRONZE,
        condition = { it.totalEmployees > (3 + 2 + 15) || it.engineers > 3 || it.qaInspectors > 2 || it.assemblyWorkers > 15 }
    ),
    Achievement(
        id = "first_research",
        title = "Ar-Ge Yolculuğu",
        description = "İlk teknolojini araştır ve kilidini aç.",
        icon = "🔬",
        tier = AchievementTier.BRONZE,
        condition = { it.unlockedTech.isNotEmpty() }
    ),

    // --- BÜYÜME (Gümüş) ---
    Achievement(
        id = "hundred_k_units",
        title = "Kitlesel Üretim",
        description = "Ömür boyu toplamda 100.000 adet cihaz sat.",
        icon = "🏭",
        tier = AchievementTier.SILVER,
        condition = { it.lifetimeUnitsSold >= 100_000 }
    ),
    Achievement(
        id = "million_budget",
        title = "İlk Milyon",
        description = "Şirket bütçen $1.000.000'u aşsın.",
        icon = "💰",
        tier = AchievementTier.SILVER,
        condition = { it.budget >= 1_000_000L }
    ),
    Achievement(
        id = "reputation_50",
        title = "Güvenilir Marka",
        description = "İtibar puanını 50'ye çıkar.",
        icon = "⭐",
        tier = AchievementTier.SILVER,
        condition = { it.reputation >= 50 }
    ),
    Achievement(
        id = "critically_acclaimed",
        title = "Eleştirmen Favorisi",
        description = "Bir modelin inceleme puanı 90+ olsun.",
        icon = "🏆",
        tier = AchievementTier.SILVER,
        condition = { it.bestReviewScore >= 90 }
    ),
    Achievement(
        id = "office_upgrade",
        title = "Büyüyen Merkez",
        description = "Genel merkezini en az bir kez yükselt.",
        icon = "🏢",
        tier = AchievementTier.SILVER,
        condition = { it.officeLevel >= 2 }
    ),
    Achievement(
        id = "factory_upgrade",
        title = "Üretim Gücü",
        description = "Fabrikanı en az bir kez yükselt.",
        icon = "⚙️",
        tier = AchievementTier.SILVER,
        condition = { it.factoryLevel >= 1 }
    ),
    Achievement(
        id = "custom_os_launched",
        title = "Kendi Ekosistemin",
        description = "Kendi işletim sistemini piyasaya sür.",
        icon = "💿",
        tier = AchievementTier.SILVER,
        condition = { it.customOs.isCustomActive }
    ),
    Achievement(
        id = "chipset_designed",
        title = "Silikon Mimarı",
        description = "Kendi özel çipsetini tasarla.",
        icon = "🧩",
        tier = AchievementTier.SILVER,
        condition = { it.customChipsets.isNotEmpty() }
    ),
    Achievement(
        id = "market_leader_5",
        title = "Pazarda Söz Sahibi",
        description = "Pazar payını %5'in üzerine çıkar.",
        icon = "📈",
        tier = AchievementTier.SILVER,
        condition = { it.playerMarketSharePercent >= 5f }
    ),
    Achievement(
        id = "survived_recall",
        title = "Krizden Dönüş",
        description = "Bir geri çağırma skandalı atlat ve şirketi ayakta tut (bütçe pozitif kalsın).",
        icon = "🛡️",
        tier = AchievementTier.SILVER,
        condition = { it.lifetimeRecalls >= 1 && it.budget > 0 }
    ),

    // --- ZİRVE (Altın) ---
    Achievement(
        id = "million_units",
        title = "Milyonlarca Kullanıcı",
        description = "Ömür boyu toplamda 1.000.000 adet cihaz sat.",
        icon = "🌍",
        tier = AchievementTier.GOLD,
        condition = { it.lifetimeUnitsSold >= 1_000_000 }
    ),
    Achievement(
        id = "ten_million_budget",
        title = "Kurumsal Dev",
        description = "Şirket bütçen $10.000.000'u aşsın.",
        icon = "🏦",
        tier = AchievementTier.GOLD,
        condition = { it.budget >= 10_000_000L }
    ),
    Achievement(
        id = "reputation_90",
        title = "Sektör İkonu",
        description = "İtibar puanını 90'a çıkar.",
        icon = "👑",
        tier = AchievementTier.GOLD,
        condition = { it.reputation >= 90 }
    ),
    Achievement(
        id = "market_leader_25",
        title = "Pazar Lideri",
        description = "Pazar payını %25'in üzerine çıkar.",
        icon = "🥇",
        tier = AchievementTier.GOLD,
        condition = { it.playerMarketSharePercent >= 25f }
    ),
    Achievement(
        id = "flagship_hq",
        title = "Amiral Gemisi Merkez",
        description = "Genel merkezini en üst seviyeye taşı.",
        icon = "🏙️",
        tier = AchievementTier.GOLD,
        condition = { state -> OFFICE_TIERS.maxByOrNull { it.level }?.level?.let { state.officeLevel >= it } ?: false }
    ),
    Achievement(
        id = "expo_champion",
        title = "Fuar Şampiyonu",
        description = "Yıl Sonu Teknoloji Fuarı'nda ödül kazan.",
        icon = "🎖️",
        tier = AchievementTier.GOLD,
        condition = { it.pastTechExpos.any { expo -> expo.playerWonCount > 0 } }
    ),
    Achievement(
        id = "ten_years_running",
        title = "On Yıllık Miras",
        description = "Şirketini 2020 yılına kadar ayakta tut.",
        icon = "📅",
        tier = AchievementTier.GOLD,
        condition = { it.year >= 2020 }
    ),

    // --- EFSANE (Platin) ---
    Achievement(
        id = "hundred_million_budget",
        title = "İmparatorluk",
        description = "Şirket bütçen $100.000.000'u aşsın.",
        icon = "💎",
        tier = AchievementTier.PLATINUM,
        condition = { it.budget >= 100_000_000L }
    ),
    Achievement(
        id = "ten_million_units",
        title = "Küresel Fenomen",
        description = "Ömür boyu toplamda 10.000.000 adet cihaz sat.",
        icon = "🚀",
        tier = AchievementTier.PLATINUM,
        condition = { it.lifetimeUnitsSold >= 10_000_000 }
    ),
    Achievement(
        id = "market_leader_50",
        title = "Tekelin Eşiğinde",
        description = "Pazar payını %50'nin üzerine çıkar.",
        icon = "🌐",
        tier = AchievementTier.PLATINUM,
        condition = { it.playerMarketSharePercent >= 50f }
    ),
    Achievement(
        id = "perfect_score",
        title = "Kusursuzluk",
        description = "Bir modelin inceleme puanını tam 100 yap.",
        icon = "✨",
        tier = AchievementTier.PLATINUM,
        condition = { it.bestReviewScore >= 100 }
    ),
    Achievement(
        id = "twenty_years_running",
        title = "Efsanevi Şirket",
        description = "Şirketini 2030 yılına kadar ayakta tut.",
        icon = "🏛️",
        tier = AchievementTier.PLATINUM,
        condition = { it.year >= 2030 }
    )
)

/** Henüz kilitli olan başarımlardan, verilen state ile artık koşulu sağlananları döndürür. */
fun evaluateNewlyUnlockedAchievements(state: GameState): List<Achievement> {
    val alreadyUnlocked = state.unlockedAchievementIds.toSet()
    return ALL_ACHIEVEMENTS.filter { it.id !in alreadyUnlocked && it.condition(state) }
}
