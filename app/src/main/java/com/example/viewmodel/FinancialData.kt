package com.example.viewmodel

import kotlinx.serialization.Serializable

@Serializable
enum class LoanType(
    val title: String,
    val icon: String,
    val principal: Long,
    val interestPercent: Int,
    val durationPeriods: Int, // 2 haftalık dönem sayısı (12 dönem = 6 Ay, 24 dönem = 1 Yıl, 48 dönem = 2 Yıl)
    val requiredReputation: Int,
    val description: String
) {
    STARTER_SME(
        title = "KOBİ Girişim & Nakit Avans Kredisi",
        icon = "🌱",
        principal = 600000L,
        interestPercent = 5,
        durationPeriods = 12, // 6 Ay
        requiredReputation = 0,
        description = "Küçük ölçekli başlangıç kredisi. Düşük dönemlik taksitlerle nakit akışınızı anında rahatlatır."
    ),
    GROWTH_EXPANSION(
        title = "Seri Üretim & Fabrika Yatırım Kredisi",
        icon = "🏭",
        principal = 2500000L,
        interestPercent = 8,
        durationPeriods = 24, // 1 Yıl
        requiredReputation = 15,
        description = "Büyük ölçekli üretim siparişleri, yeni fabrika kademeleri ve pazarlama kampanyaları için kurumsal finansman."
    ),
    MEGA_SYNDICATION(
        title = "Küresel Teknoloji Sendikasyon Kredisi",
        icon = "🏦",
        principal = 7500000L,
        interestPercent = 12,
        durationPeriods = 48, // 2 Yıl
        requiredReputation = 40,
        description = "Küresel amiral gemisi lansmanları, dev Ar-Ge projeleri ve çipset yatırımları için en üst düzey sendikasyon fonu."
    ),
    EMERGENCY_BAILOUT(
        title = "Hükümet Acil Kurtarma & Teşvik Paketi",
        icon = "🚨",
        principal = 1500000L,
        interestPercent = 3,
        durationPeriods = 36, // 1.5 Yıl
        requiredReputation = 0,
        description = "Yalnızca bütçe eksiye düştüğünde veya iflas riski belirdiğinde açılan düşük faizli acil can suyu paketi."
    )
}

@Serializable
data class BankLoan(
    val id: String,
    val type: LoanType,
    val principalAmount: Long,
    val totalRepayment: Long,
    val remainingBalance: Long,
    val totalPeriods: Int,
    val remainingPeriods: Int,
    val periodPayment: Long,
    val interestPercent: Int
) {
    val progressPercent: Float
        get() = if (totalPeriods <= 0) 1f else ((totalPeriods - remainingPeriods).toFloat() / totalPeriods.toFloat()).coerceIn(0f, 1f)

    val earlyPayoffDiscountAmount: Long
        get() = (remainingBalance * 0.05).toLong()

    val earlyPayoffCost: Long
        get() = (remainingBalance - earlyPayoffDiscountAmount).coerceAtLeast(0L)
}
