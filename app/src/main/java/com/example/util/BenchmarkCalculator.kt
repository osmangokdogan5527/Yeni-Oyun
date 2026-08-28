package com.example.util

import com.example.model.BenchmarkScore
import com.example.viewmodel.PhoneSpecs
import kotlin.math.roundToInt

/**
 * [PhoneSpecs] içindeki bileşen isimlerini (metin) okuyarak 0-100 aralığında
 * kategori bazlı benchmark puanları üretir. Gerçek bir donanım ölçümü değil,
 * bileşen isimlerindeki anahtar kelimelere ve sayısal değerlere (mAh, yenileme
 * hızı vb.) dayalı bir tahmindir — "Test Lab" ekranında karşılaştırma amaçlıdır.
 */
object BenchmarkCalculator {

    fun calculateScore(specs: PhoneSpecs, osOptimization: Int): BenchmarkScore {
        val performance = performanceScore(specs)
        val display = displayScore(specs)
        val camera = cameraScore(specs)
        val battery = batteryScore(specs)
        val software = osOptimization.coerceIn(0, 100)

        val overall = (
            performance * 0.30f +
                display * 0.20f +
                camera * 0.20f +
                battery * 0.15f +
                software * 0.15f
            ).roundToInt().coerceIn(0, 100)

        return BenchmarkScore(
            performanceScore = performance,
            displayScore = display,
            cameraScore = camera,
            batteryScore = battery,
            softwareScore = software,
            overallScore = overall
        )
    }

    private fun performanceScore(specs: PhoneSpecs): Int {
        // Yıl bazlı taban puan: 2010 -> ~15, 2026+ -> ~90
        val yearBase = ((specs.techScore - 2010).coerceAtLeast(0) * 4.7f).roundToInt().coerceIn(15, 90)

        val processorBonus = when {
            specs.processor.contains("👑") || specs.processor.contains("Amiral") -> 12
            specs.processor.contains("Kuantum") -> 10
            specs.processor.contains("Gen 3") || specs.processor.contains("D9300") -> 8
            specs.processor.contains("Gen 1") || specs.processor.contains("D9000") -> 6
            specs.processor.contains("In-House") || specs.processor.contains("⚖️") -> 6
            specs.processor.contains("🏷️") || specs.processor.contains("Lite") -> 2
            else -> 0
        }

        val ramBonus = when {
            specs.ramCapacity.contains("32") || specs.ramCapacity.contains("24") -> 8
            specs.ramCapacity.contains("16") || specs.ramCapacity.contains("12") -> 5
            specs.ramCapacity.contains("8") -> 3
            else -> 0
        }

        return (yearBase + processorBonus + ramBonus).coerceIn(0, 100)
    }

    private fun displayScore(specs: PhoneSpecs): Int {
        val base = when {
            specs.display.contains("Holografik") -> 98
            specs.display.contains("240Hz") -> 92
            specs.display.contains("Katlanabilir") -> 88
            specs.display.contains("144Hz") -> 82
            specs.display.contains("120Hz") -> 72
            specs.display.contains("Çerçevesiz") -> 60
            specs.display.contains("Kavisli") -> 55
            specs.display.contains("QHD") -> 48
            specs.display.contains("FHD") -> 38
            else -> 22
        }

        val glassBonus = when {
            specs.glass.contains("Sapphire") || specs.glass.contains("Armor+") -> 6
            specs.glass.contains("Gorilla Armor") || specs.glass.contains("Ceramic") -> 4
            specs.glass.contains("Victus") -> 2
            else -> 0
        }

        return (base + glassBonus).coerceIn(0, 100)
    }

    private fun cameraScore(specs: PhoneSpecs): Int {
        val base = when {
            specs.camera.contains("Donanımsal ISP") -> 97
            specs.camera.contains("GenAI") -> 90
            specs.camera.contains("Periskop") -> 84
            specs.camera.contains("200MP") -> 80
            specs.camera.contains("Üçlü") -> 68
            specs.camera.contains("Çift") -> 52
            else -> 28 // Tek kamera
        }
        return base.coerceIn(0, 100)
    }

    private fun batteryScore(specs: PhoneSpecs): Int {
        // "4500 mAh" gibi metinlerden sayısal değeri çıkarıyoruz.
        val mah = Regex("""\d+""").find(specs.batteryCapacity)?.value?.toIntOrNull() ?: 1500
        // 1500 mAh -> ~10, 8000 mAh -> ~85 aralığına ölçekleniyor.
        val capacityScore = (((mah - 1500).coerceAtLeast(0) / 6500f) * 75f + 10f).roundToInt()

        val chargeBonus = when {
            specs.batteryType.contains("240W") -> 15
            specs.batteryType.contains("200W") -> 14
            specs.batteryType.contains("120W") -> 12
            specs.batteryType.contains("100W") -> 10
            specs.batteryType.contains("65W") -> 8
            specs.batteryType.contains("25W") -> 6
            specs.batteryType.contains("20W") -> 5
            specs.batteryType.contains("15W") || specs.batteryType.contains("18W") -> 4
            specs.batteryType.contains("10W") -> 2
            else -> 0
        }

        return (capacityScore + chargeBonus).coerceIn(0, 100)
    }
}
