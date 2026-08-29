package com.example.util

import androidx.compose.ui.graphics.Color
import kotlin.math.roundToInt

enum class HardwareTier(
    val code: String,
    val title: String,
    val badgeColor: Color,
    val minScore: Int
) {
    TIER_S_PLUS("Tier S+", "Ultra Amiral Gemisi", Color(0xFF9333EA), 2200),
    TIER_S("Tier S", "Amiral Gemisi Zirve", Color(0xFF6366F1), 1500),
    TIER_A("Tier A", "Üst Düzey Güç", Color(0xFF2563EB), 950),
    TIER_B("Tier B", "Yüksek Performans", Color(0xFF0284C7), 550),
    TIER_C("Tier C", "Orta Segment", Color(0xFF059669), 320),
    TIER_D("Tier D", "Temel Hız", Color(0xFFD97706), 170),
    TIER_E("Tier E", "Giriş Seviye", Color(0xFF64748B), 0);

    companion object {
        fun fromProcessorScore(score: Int): HardwareTier = when {
            score >= 2200 -> TIER_S_PLUS
            score >= 1500 -> TIER_S
            score >= 950 -> TIER_A
            score >= 550 -> TIER_B
            score >= 320 -> TIER_C
            score >= 170 -> TIER_D
            else -> TIER_E
        }
    }
}

data class ComponentRating(
    val score: Int,
    val tier: HardwareTier,
    val label: String, // e.g. "⚡ 250 Puan (Tier D)"
    val shortBadge: String // e.g. "Tier D (250)"
)

object HardwareRatingHelper {

    /**
     * İşlemci isminden / donanımından performans puanı ve Tier seviyesi üretir.
     */
    fun getProcessorRating(processorName: String): ComponentRating {
        val clean = processorName.trim()
        
        // Eğer custom chip / özelleştirilmiş çip ise ("1800 Puan" formatında metin barındırabilir)
        val scoreRegex = "(\\d+)\\s*Puan".toRegex(RegexOption.IGNORE_CASE)
        val matchedScore = scoreRegex.find(clean)?.groupValues?.get(1)?.toIntOrNull()
        
        val score = when {
            matchedScore != null -> matchedScore
            clean.contains("Kuantum", ignoreCase = true) -> 3500
            clean.contains("Gen 3", ignoreCase = true) || clean.contains("D9300", ignoreCase = true) || clean.contains("A18", ignoreCase = true) || clean.contains("8 Elite", ignoreCase = true) -> 2500
            clean.contains("Gen 1", ignoreCase = true) || clean.contains("D9000", ignoreCase = true) || clean.contains("A16", ignoreCase = true) || clean.contains("A15", ignoreCase = true) || clean.contains("Tensor G4", ignoreCase = true) || clean.contains("Tensor G3", ignoreCase = true) || clean.contains("Kirin 9010", ignoreCase = true) -> 1800
            clean.contains("865", ignoreCase = true) || clean.contains("D800", ignoreCase = true) || clean.contains("A13", ignoreCase = true) || clean.contains("A14", ignoreCase = true) || clean.contains("Tensor G2", ignoreCase = true) || clean.contains("Tensor G1", ignoreCase = true) || clean.contains("Kirin 9000", ignoreCase = true) || clean.contains("888", ignoreCase = true) -> 1300
            clean.contains("845", ignoreCase = true) || clean.contains("G90", ignoreCase = true) || clean.contains("A11", ignoreCase = true) || clean.contains("A12", ignoreCase = true) || clean.contains("855", ignoreCase = true) || clean.contains("Exynos 9820", ignoreCase = true) || clean.contains("Kirin 980", ignoreCase = true) || clean.contains("778G", ignoreCase = true) || clean.contains("765G", ignoreCase = true) -> 900
            clean.contains("820", ignoreCase = true) || clean.contains("810", ignoreCase = true) || clean.contains("808", ignoreCase = true) || clean.contains("Helio", ignoreCase = true) || clean.contains("A9", ignoreCase = true) || clean.contains("A10", ignoreCase = true) || clean.contains("Exynos 7420", ignoreCase = true) || clean.contains("Kirin 950", ignoreCase = true) || clean.contains("835", ignoreCase = true) || clean.contains("Z3580", ignoreCase = true) -> 600
            clean.contains("801", ignoreCase = true) || clean.contains("805", ignoreCase = true) || clean.contains("800", ignoreCase = true) || clean.contains("Atom X5", ignoreCase = true) || clean.contains("A7", ignoreCase = true) || clean.contains("A8", ignoreCase = true) || clean.contains("Exynos 5433", ignoreCase = true) || clean.contains("600", ignoreCase = true) -> 400
            clean.contains("S4", ignoreCase = true) || clean.contains("MT67", ignoreCase = true) || clean.contains("A5", ignoreCase = true) || clean.contains("A6", ignoreCase = true) || clean.contains("Exynos 4210", ignoreCase = true) -> 250
            clean.contains("MT65", ignoreCase = true) || clean.contains("S2", ignoreCase = true) || clean.contains("A4", ignoreCase = true) -> 120
            clean.contains("In-House", ignoreCase = true) || clean.contains("Öz Tasarım", ignoreCase = true) || clean.contains("👑") -> 1600
            
            // Jenerik metinler
            clean.contains("3nm", ignoreCase = true) || clean.contains("4nm", ignoreCase = true) -> 2300
            clean.contains("5G Çipset", ignoreCase = true) || clean.contains("5G", ignoreCase = true) -> 1400
            clean.contains("Sekiz Çekirdek", ignoreCase = true) -> 650
            clean.contains("Dört Çekirdek", ignoreCase = true) -> 280
            clean.contains("Çift Çekirdek", ignoreCase = true) -> 160
            else -> 200
        }

        val tier = HardwareTier.fromProcessorScore(score)
        return ComponentRating(
            score = score,
            tier = tier,
            label = "⚡ $score Puan (${tier.code})",
            shortBadge = "${tier.code} • $score Pts"
        )
    }

    /**
     * RAM donanım gücü puanı
     */
    fun getRamRating(ramCapacity: String, ramType: String = ""): ComponentRating {
        val cleanCap = ramCapacity.trim()
        val baseScore = when {
            cleanCap.contains("32 GB") || cleanCap.contains("32GB") -> 160
            cleanCap.contains("24 GB") || cleanCap.contains("24GB") -> 140
            cleanCap.contains("16 GB") || cleanCap.contains("16GB") -> 120
            cleanCap.contains("12 GB") || cleanCap.contains("12GB") -> 100
            cleanCap.contains("8 GB") || cleanCap.contains("8GB") -> 80
            cleanCap.contains("6 GB") || cleanCap.contains("6GB") -> 65
            cleanCap.contains("4 GB") || cleanCap.contains("4GB") -> 50
            cleanCap.contains("3 GB") || cleanCap.contains("3GB") -> 40
            cleanCap.contains("2 GB") || cleanCap.contains("2GB") -> 28
            cleanCap.contains("1 GB") || cleanCap.contains("1GB") -> 18
            else -> 10
        }

        val typeBonus = when {
            ramType.contains("LPDDR6") -> 30
            ramType.contains("LPDDR5X") -> 22
            ramType.contains("LPDDR5") -> 16
            ramType.contains("LPDDR4X") -> 10
            ramType.contains("LPDDR4") -> 6
            ramType.contains("LPDDR3") -> 3
            else -> 0
        }

        val total = baseScore + typeBonus
        val tier = when {
            total >= 130 -> HardwareTier.TIER_S_PLUS
            total >= 95 -> HardwareTier.TIER_S
            total >= 70 -> HardwareTier.TIER_A
            total >= 50 -> HardwareTier.TIER_B
            total >= 35 -> HardwareTier.TIER_C
            total >= 20 -> HardwareTier.TIER_D
            else -> HardwareTier.TIER_E
        }

        return ComponentRating(
            score = total,
            tier = tier,
            label = "$total Puan (${tier.code})",
            shortBadge = "${tier.code} • $total Pts"
        )
    }

    /**
     * Batarya donanım puanı
     */
    fun getBatteryRating(batteryCapacity: String, batteryType: String = ""): ComponentRating {
        val clean = "$batteryCapacity $batteryType"
        val mahRegex = "(\\d{3,5})\\s*mAh".toRegex(RegexOption.IGNORE_CASE)
        val mah = mahRegex.find(clean)?.groupValues?.get(1)?.toIntOrNull() ?: 2000

        val baseScore = (mah / 50.0).roundToInt().coerceIn(20, 150)
        val chargingBonus = when {
            clean.contains("240W") || clean.contains("180W") || clean.contains("Katı Hal") -> 40
            clean.contains("120W") || clean.contains("100W") || clean.contains("90W") || clean.contains("80W") -> 25
            clean.contains("65W") || clean.contains("40W") || clean.contains("Dash") || clean.contains("Warp") -> 15
            clean.contains("30W") || clean.contains("20W") || clean.contains("Hızlı Şarj") -> 8
            else -> 0
        }

        val total = baseScore + chargingBonus
        val tier = when {
            total >= 140 -> HardwareTier.TIER_S_PLUS
            total >= 105 -> HardwareTier.TIER_S
            total >= 80 -> HardwareTier.TIER_A
            total >= 60 -> HardwareTier.TIER_B
            total >= 42 -> HardwareTier.TIER_C
            total >= 28 -> HardwareTier.TIER_D
            else -> HardwareTier.TIER_E
        }

        return ComponentRating(
            score = total,
            tier = tier,
            label = "🔋 $total Puan (${tier.code})",
            shortBadge = "${tier.code} • $total Pts"
        )
    }

    /**
     * Kamera donanım puanı
     */
    fun getCameraRating(cameraName: String): ComponentRating {
        val clean = cameraName.trim()
        val score = when {
            clean.contains("GenAI") || clean.contains("3D Mekansal") -> 160
            clean.contains("200 MP") || clean.contains("200MP") || clean.contains("1-İnç") || clean.contains("Leica") || clean.contains("Hasselblad") || clean.contains("Periskop") || clean.contains("Fusion") -> 130
            clean.contains("108 MP") || clean.contains("108MP") || clean.contains("Üçlü") || clean.contains("Donanımsal ISP") -> 100
            clean.contains("48-64") || clean.contains("50 MP") || clean.contains("64 MP") || clean.contains("48 MP") || clean.contains("41MP") -> 80
            clean.contains("Çift") || clean.contains("16-20") || clean.contains("16 MP") || clean.contains("20 MP") -> 55
            clean.contains("8-13") || clean.contains("12 MP") || clean.contains("13 MP") || clean.contains("8 MP") -> 38
            clean.contains("5 MP") || clean.contains("5MP") -> 20
            else -> 25
        }

        val tier = when {
            score >= 140 -> HardwareTier.TIER_S_PLUS
            score >= 110 -> HardwareTier.TIER_S
            score >= 85 -> HardwareTier.TIER_A
            score >= 60 -> HardwareTier.TIER_B
            score >= 40 -> HardwareTier.TIER_C
            score >= 25 -> HardwareTier.TIER_D
            else -> HardwareTier.TIER_E
        }

        return ComponentRating(
            score = score,
            tier = tier,
            label = "📸 $score Puan (${tier.code})",
            shortBadge = "${tier.code} • $score Pts"
        )
    }

    /**
     * Ekran donanım puanı
     */
    fun getDisplayRating(displayName: String): ComponentRating {
        val clean = displayName.trim()
        val score = when {
            clean.contains("Holografik") || clean.contains("Tandem OLED") || clean.contains("240Hz") -> 150
            clean.contains("165Hz") || clean.contains("144Hz") || clean.contains("Katlanabilir") -> 125
            clean.contains("120Hz") || clean.contains("LTPO") || clean.contains("ProMotion") || clean.contains("Dynamic Island") -> 100
            clean.contains("Çerçevesiz") || clean.contains("Kavisli") || clean.contains("90Hz") || clean.contains("Fluid AMOLED") -> 75
            clean.contains("QHD") || clean.contains("2K") || clean.contains("Quad HD") || clean.contains("Retina") -> 58
            clean.contains("FHD") || clean.contains("1080p") || clean.contains("Full HD") || clean.contains("AMOLED") -> 42
            clean.contains("HD") || clean.contains("720p") -> 28
            else -> 18
        }

        val tier = when {
            score >= 135 -> HardwareTier.TIER_S_PLUS
            score >= 100 -> HardwareTier.TIER_S
            score >= 75 -> HardwareTier.TIER_A
            score >= 55 -> HardwareTier.TIER_B
            score >= 38 -> HardwareTier.TIER_C
            score >= 24 -> HardwareTier.TIER_D
            else -> HardwareTier.TIER_E
        }

        return ComponentRating(
            score = score,
            tier = tier,
            label = "🖥️ $score Puan (${tier.code})",
            shortBadge = "${tier.code} • $score Pts"
        )
    }

    /**
     * Toplam Donanım Güç İndeksi (0 - 1000+ Normalize Donanım Skoru)
     */
    fun calculateHardwarePowerIndex(
        processor: String,
        ram: String,
        camera: String,
        battery: String,
        display: String
    ): Int {
        val pScore = getProcessorRating(processor).score * 0.35f
        val rScore = getRamRating(ram).score * 1.5f
        val cScore = getCameraRating(camera).score * 1.5f
        val bScore = getBatteryRating(battery).score * 1.3f
        val dScore = getDisplayRating(display).score * 1.4f

        return (pScore + rScore + cScore + bScore + dScore).roundToInt().coerceAtLeast(50)
    }

    /**
     * İki donanım puanı arasındaki farka göre kıyaslama rozeti üretir:
     * 1: Oyuncu Üstün, -1: Rakip Üstün, 0: Eşit
     */
    fun compareScores(playerScore: Int, compScore: Int): Int {
        val diff = playerScore - compScore
        val threshold = (compScore * 0.08f).roundToInt().coerceAtLeast(3)
        return when {
            diff >= threshold -> 1
            diff <= -threshold -> -1
            else -> 0
        }
    }
}
