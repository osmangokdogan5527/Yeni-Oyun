package com.example.util

import com.example.model.BenchmarkScore
import com.example.model.LeaderboardEntry
import com.example.viewmodel.CompetitorCompany
import com.example.viewmodel.CompetitorReleaseHistory
import com.example.viewmodel.PhoneSpecs
import kotlin.math.roundToInt
import kotlin.random.Random

object BenchmarkCalculator {

    /**
     * Calculates realistic, year-scaled AnTuTu, DXOMARK, Geekbench, and Thermal scores
     * based on PhoneSpecs and custom OS optimization.
     */
    fun calculateScore(specs: PhoneSpecs, osOptimization: Int = 50): BenchmarkScore {
        val year = specs.techScore.coerceIn(2010, 2030)
        val yearFactor = (year - 2010).coerceAtLeast(0) // 0 to 20

        // Base CPU Score scaling with year and processor tier
        val cpuBase = when {
            specs.processor.contains("Snapdragon 8 Gen", ignoreCase = true) || specs.processor.contains("A17", ignoreCase = true) || specs.processor.contains("Dimensity 9300", ignoreCase = true) -> 580000
            specs.processor.contains("Snapdragon 8+", ignoreCase = true) || specs.processor.contains("A16", ignoreCase = true) || specs.processor.contains("Tensor G3", ignoreCase = true) -> 450000
            specs.processor.contains("Snapdragon 888", ignoreCase = true) || specs.processor.contains("A15", ignoreCase = true) || specs.processor.contains("Exynos 2200", ignoreCase = true) -> 320000
            specs.processor.contains("Snapdragon 865", ignoreCase = true) || specs.processor.contains("A13", ignoreCase = true) -> 220000
            specs.processor.contains("Snapdragon 845", ignoreCase = true) || specs.processor.contains("A11", ignoreCase = true) -> 140000
            specs.processor.contains("Snapdragon 820", ignoreCase = true) || specs.processor.contains("A9", ignoreCase = true) -> 85000
            specs.processor.contains("Snapdragon 800", ignoreCase = true) || specs.processor.contains("A7", ignoreCase = true) -> 35000
            specs.processor.contains("Snapdragon S4", ignoreCase = true) || specs.processor.contains("A6", ignoreCase = true) -> 18000
            specs.processor.contains("Snapdragon S3", ignoreCase = true) || specs.processor.contains("A5", ignoreCase = true) -> 8500
            specs.processor.contains("Snapdragon S1", ignoreCase = true) || specs.processor.contains("Hummingbird", ignoreCase = true) -> 3200
            else -> (4000 + yearFactor * 25000)
        }

        // RAM & Storage impact on Mem & UX
        val ramMultiplier = when {
            specs.ramCapacity.contains("24 GB") || specs.ramCapacity.contains("16 GB") -> 1.5f
            specs.ramCapacity.contains("12 GB") || specs.ramCapacity.contains("8 GB") -> 1.3f
            specs.ramCapacity.contains("6 GB") || specs.ramCapacity.contains("4 GB") -> 1.1f
            specs.ramCapacity.contains("3 GB") || specs.ramCapacity.contains("2 GB") -> 0.85f
            else -> 0.6f
        }

        val ramSpeedBonus = when {
            specs.ramType.contains("LPDDR5X") || specs.ramType.contains("LPDDR5T") -> 1.35f
            specs.ramType.contains("LPDDR5") -> 1.2f
            specs.ramType.contains("LPDDR4X") -> 1.0f
            specs.ramType.contains("LPDDR3") -> 0.8f
            else -> 0.6f
        }

        val memScore = (cpuBase * 0.28f * ramMultiplier * ramSpeedBonus).toInt()
        val gpuScore = (cpuBase * 0.42f * if (specs.style.contains("Oyuncu", ignoreCase = true)) 1.25f else 1.0f).toInt()
        val uxScore = (cpuBase * 0.30f * (0.8f + (osOptimization / 100f) * 0.4f)).toInt()
        val totalAntutu = cpuBase + gpuScore + memScore + uxScore

        // DXOMARK Breakdown
        val cameraBase = when {
            specs.camera.contains("200 MP", ignoreCase = true) || specs.camera.contains("Periskop", ignoreCase = true) || specs.camera.contains("1-inç", ignoreCase = true) -> 148
            specs.camera.contains("108 MP", ignoreCase = true) || specs.camera.contains("Dörtlü", ignoreCase = true) || specs.camera.contains("OIS", ignoreCase = true) -> 132
            specs.camera.contains("50 MP", ignoreCase = true) || specs.camera.contains("Üçlü", ignoreCase = true) -> 118
            specs.camera.contains("48 MP", ignoreCase = true) || specs.camera.contains("Çift", ignoreCase = true) -> 98
            specs.camera.contains("12 MP", ignoreCase = true) || specs.camera.contains("13 MP", ignoreCase = true) -> 78
            specs.camera.contains("8 MP", ignoreCase = true) || specs.camera.contains("5 MP", ignoreCase = true) -> 58
            else -> (40 + (yearFactor * 5)).coerceAtMost(160)
        }

        val photoScore = (cameraBase * 1.05f).toInt()
        val videoScore = (cameraBase * 0.95f).toInt()
        val zoomScore = (cameraBase * if (specs.camera.contains("Periskop", ignoreCase = true) || specs.camera.contains("Telefoto", ignoreCase = true)) 1.2f else 0.75f).toInt()
        val nightScore = (cameraBase * if (specs.camera.contains("OIS", ignoreCase = true) || specs.camera.contains("1-inç", ignoreCase = true)) 1.15f else 0.85f).toInt()
        val totalDxomark = ((photoScore * 0.4f) + (videoScore * 0.3f) + (zoomScore * 0.15f) + (nightScore * 0.15f)).roundToInt()

        // Geekbench
        val geekbenchSingle = ((cpuBase / 220f) * (0.9f + (osOptimization / 200f))).roundToInt().coerceAtLeast(120)
        val geekbenchMulti = (geekbenchSingle * when {
            specs.processor.contains("Sekiz Çekirdek", ignoreCase = true) || specs.processor.contains("Octa", ignoreCase = true) || year >= 2015 -> 3.6f
            specs.processor.contains("Dört Çekirdek", ignoreCase = true) || specs.processor.contains("Quad", ignoreCase = true) || year >= 2012 -> 2.6f
            else -> 1.8f
        }).roundToInt()

        // Battery & Thermal
        val batteryMah = specs.batteryCapacity.filter { it.isDigit() }.toIntOrNull() ?: 3000
        val screenOnTime = (batteryMah / 450f) * (if (specs.display.contains("LTPO", ignoreCase = true) || specs.display.contains("AMOLED", ignoreCase = true)) 1.15f else 0.95f)
        
        val thermalThrottle = when {
            specs.material.contains("Titanyum", ignoreCase = true) -> 12
            specs.material.contains("Alüminyum", ignoreCase = true) -> 8
            specs.style.contains("Oyuncu", ignoreCase = true) -> 5
            else -> 15
        }
        val peakTemp = 36.5f + (cpuBase / 100000f).coerceAtMost(8f) - (if (specs.style.contains("Oyuncu")) 2.5f else 0f)

        return BenchmarkScore(
            cpuScore = cpuBase,
            gpuScore = gpuScore,
            memScore = memScore,
            uxScore = uxScore,
            totalAntutuScore = totalAntutu,
            photoScore = photoScore,
            videoScore = videoScore,
            zoomScore = zoomScore,
            nightScore = nightScore,
            totalDxomarkScore = totalDxomark,
            geekbenchSingle = geekbenchSingle,
            geekbenchMulti = geekbenchMulti,
            screenOnTimeHours = String.format("%.1f", screenOnTime).replace(',', '.').toFloatOrNull() ?: 6.5f,
            peakTempCelsius = String.format("%.1f", peakTemp).replace(',', '.').toFloatOrNull() ?: 39.2f,
            thermalThrottlingPercent = thermalThrottle,
            durabilityScore = if (specs.glass.contains("Victus", ignoreCase = true) || specs.glass.contains("Ceramic", ignoreCase = true)) 95 else if (specs.glass.contains("Gorilla", ignoreCase = true)) 82 else 65
        )
    }

    /**
     * Generates a realistic competitor flagship entry for the leaderboard.
     */
    fun createCompetitorBenchmark(
        comp: CompetitorCompany,
        year: Int,
        recentRelease: CompetitorReleaseHistory?
    ): LeaderboardEntry {
        val modelName = recentRelease?.modelName ?: "${comp.name} Flagship $year"
        val price = recentRelease?.price ?: comp.currentModelPrice

        val yearFactor = (year - 2010).coerceAtLeast(0)
        val baseAntutu = (15000 + yearFactor * 45000) * (comp.currentModelScore / 80f)
        val antutu = (baseAntutu * (0.95f + (Random.nextFloat() * 0.15f))).toInt()

        val baseDxomark = (55 + yearFactor * 5) * (if (comp.strengthText.contains("Kamera", ignoreCase = true)) 1.12f else 0.98f)
        val dxomark = baseDxomark.roundToInt().coerceIn(40, 165)

        val geekSingle = (antutu / 550).coerceAtLeast(150)
        val geekMulti = (geekSingle * 3.4f).roundToInt()

        val socName = when {
            comp.name.contains("Apple", ignoreCase = true) || comp.name.contains("Armut", ignoreCase = true) -> "A${(year - 2007).coerceAtLeast(4)} Bionic Pro"
            comp.name.contains("Samsung", ignoreCase = true) || comp.name.contains("Samsong", ignoreCase = true) -> "Exynos ${year}00 Ultra"
            comp.name.contains("Google", ignoreCase = true) || comp.name.contains("Gugıl", ignoreCase = true) -> "Tensor G${(year - 2020).coerceAtLeast(1)}"
            comp.name.contains("Xiaomi", ignoreCase = true) || comp.name.contains("Xiaomeme", ignoreCase = true) -> "Snapdragon 8 Gen ${(year - 2021).coerceAtLeast(1)}"
            else -> "Snapdragon Elite $year"
        }

        val verdict = when {
            antutu > 800000 -> "Hız canavarı, saf performans."
            dxomark > 130 -> "Stüdyo kalitesinde profesyonel kamera."
            price < 500 -> "Fiyat/Performans şampiyonu."
            else -> "Dengeli amiral gemisi deneyimi."
        }

        return LeaderboardEntry(
            id = "comp_bench_${comp.id}_$year",
            modelName = modelName,
            brandName = comp.name,
            brandColorHex = comp.brandColorHex,
            isPlayer = false,
            releaseYear = year,
            price = price,
            tierTitle = comp.strategyType,
            antutuScore = antutu,
            dxomarkScore = dxomark,
            geekbenchSingle = geekSingle,
            geekbenchMulti = geekMulti,
            batteryLifeHours = 6.0f + (comp.currentModelScore / 25f),
            peakTempCelsius = 38.0f + (Random.nextFloat() * 4.5f),
            socName = socName,
            cameraSummary = if (dxomark > 115) "50MP Çoklu Sensör + OIS" else "12MP Çift Kamera",
            verdict = verdict
        )
    }
}
