package com.example.viewmodel

import kotlinx.serialization.Serializable

@Serializable
enum class AwardCategory(
    val title: String,
    val iconEmoji: String,
    val description: String,
    val reputationBonus: Int,
    val prizeMoney: Long
) {
    FLAGSHIP_OF_THE_YEAR(
        "🏆 Yılın Amiral Gemisi",
        "👑",
        "Yıl boyunca piyasaya sürülen en yüksek inceleme ve teknoloji puanına sahip akıllı telefon.",
        8,
        1500000L
    ),
    VALUE_CHAMPION(
        "💰 Fiyat/Performans Kralı",
        "🎯",
        "En yüksek fiyat/performans dengesini sunup tüketici memnuniyetini maksimize eden cihaz.",
        5,
        1000000L
    ),
    INNOVATION_AWARD(
        "💡 Yılın İnovasyonu Ödülü",
        "⚡",
        "Katlanabilir ekran, şeffaf gövde veya yapay zeka gibi sektörde çığır açan donanım yeniliği.",
        6,
        2000000L
    ),
    BEST_DESIGN(
        "🎨 En İyi Endüstriyel Tasarım",
        "✨",
        "Malzeme kalitesi, premium çerçeve ve kusursuz ergonomik estetik başarısı.",
        4,
        800000L
    )
}

@Serializable
data class AwardNominee(
    val modelName: String,
    val companyName: String,
    val isPlayer: Boolean,
    val logoEmoji: String,
    val brandColorHex: Long,
    val score: Int,
    val price: Int,
    val highlightText: String
)

@Serializable
data class AwardResult(
    val category: AwardCategory,
    val winner: AwardNominee,
    val nominees: List<AwardNominee>,
    val ceremonyReview: String
)

@Serializable
data class TechExpoEvent(
    val year: Int,
    val expoName: String, // Örn: "MWC Barcelona 2015" veya "Global Tech Expo 2018"
    val city: String,
    val awards: List<AwardResult>,
    val playerWonCount: Int,
    val totalPrizeWon: Long,
    val reputationGained: Int
)

