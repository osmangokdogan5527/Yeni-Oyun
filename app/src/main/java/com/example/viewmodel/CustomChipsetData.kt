package com.example.viewmodel

import kotlinx.serialization.Serializable

@Serializable
enum class ChipsetTier(
    val title: String,
    val badge: String,
    val description: String,
    val defaultCoreCount: Int,
    val defaultClockSpeed: Float,
    val targetMarket: String,
    val marketVolumeMultiplier: Float
) {
    ENTRY_LITE(
        "Giriş Seviye & Bütçe (Lite)",
        "🏷️",
        "Düşük maliyetli, yüksek enerji verimli temel çip. Ucuz telefonlar ve yüksek adetli pazar satışı için idealdir.",
        4,
        1.8f,
        "Giriş & Ekonomik Cihazlar",
        2.5f // High volume demand from budget brands
    ),
    MID_RANGE(
        "Orta Segment (Dengeli)",
        "⚖️",
        "Fiyat/performans dengeli çok yönlü işlemci. Günlük akıcılık ve stabil oyun performansı sunar.",
        8,
        2.4f,
        "Orta Segment & Popüler Modeller",
        1.6f
    ),
    FLAGSHIP_PRO(
        "Amiral Gemisi (Tepe Güç)",
        "👑",
        "Zirve CPU/GPU gücü, gelişmiş grafik ve yapay zeka birimi. En üst düzey modeller ve prestij için tasarlanır.",
        8,
        3.4f,
        "Premium Amiral Gemileri",
        1.0f
    )
}

@Serializable
enum class ProcessNode(
    val nodeName: String,
    val nm: Int,
    val minYear: Int,
    val baseCost: Int,
    val efficiencyBonus: Float,
    val requiredTech: String? = null
) {
    NM_45("45nm Standart", 45, 2010, 12, 0.75f),
    NM_28("28nm Yüksek Verim", 28, 2012, 18, 0.90f),
    NM_14("14nm FinFET", 14, 2015, 28, 1.10f),
    NM_10("10nm Yoğun Mimari", 10, 2017, 42, 1.25f),
    NM_7("7nm EUV Litografi", 7, 2019, 62, 1.45f),
    NM_5("5nm Ultra Yoğun", 5, 2021, 88, 1.70f),
    NM_4("4nm GenAI Optimize", 4, 2023, 115, 1.90f),
    NM_3("3nm Gate-All-Around", 3, 2024, 150, 2.15f),
    NM_2("2nm Kuantum Nano", 2, 2026, 195, 2.50f)
}

@Serializable
enum class GpuArchitecture(
    val title: String,
    val extraCost: Int,
    val perfBonus: Int,
    val description: String
) {
    BASIC_GPU("Temel 2D/3D GPU", 4, 80, "Günlük arayüz akıcılığı ve standart video oynatma."),
    PERFORMANCE_GPU("Gelişmiş Çok Çekirdekli Mobil GPU", 18, 380, "Akıcı 60/120 FPS mobil oyun ve grafik işleme gücü."),
    RAY_TRACING_EXTREME("Işın İzleme (Ray Tracing) Tepe GPU", 48, 950, "Konsol kalitesinde donanımsal ışıklandırma ve ekstrem grafik.")
}

@Serializable
enum class NpuArchitecture(
    val title: String,
    val tops: Int,
    val extraCost: Int,
    val perfBonus: Int,
    val description: String
) {
    NO_NPU("NPU Yok (Standart CPU)", 0, 0, 0, "Yapay zeka işlemleri standart CPU çekirdeklerinde yürütülür."),
    BASIC_NPU("10 TOPS Sinir Ağı Motoru", 10, 12, 220, "Yüz tanıma, sahne algılama ve kamera optimizasyonunu hızlandırır."),
    GEN_AI_NPU("45+ TOPS GenAI Nöral İşlemci", 45, 42, 800, "Cihaz içi LLM, anlık ses çevirisi ve yapay zeka render gücü.")
}

@Serializable
enum class PowerProfile(
    val title: String,
    val perfMultiplier: Float,
    val batteryScoreBonus: Int,
    val description: String
) {
    EFFICIENCY("Ultra Düşük Güç (Pil Dostu)", 0.88f, 15, "Isınmayı önler, minimum güç tüketimi ve uzun pil ömrü sağlar."),
    BALANCED("Dengeli Performans & Güç", 1.00f, 0, "Performans ile enerji tüketimi arasında ideal endüstri dengesi."),
    EXTREME_OC("Hız Aşırtmalı (Peak Boost)", 1.20f, -12, "Maksimum saat frekansı, sentetik test ve hız rekorları için tepe güç.")
}

@Serializable
data class CustomChipset(
    val id: String,
    val name: String,
    val tier: ChipsetTier = ChipsetTier.MID_RANGE,
    val processNode: ProcessNode = ProcessNode.NM_28,
    val coreCount: Int = 8, // 4, 6, 8, 10
    val clockSpeedGhz: Float = 2.4f, // 1.4 GHz .. 3.8 GHz
    val gpuArchitecture: GpuArchitecture = GpuArchitecture.PERFORMANCE_GPU,
    val npuArchitecture: NpuArchitecture = NpuArchitecture.NO_NPU,
    val powerProfile: PowerProfile = PowerProfile.BALANCED,
    val generation: Int = 1,
    val createdYear: Int = 2010,
    val createdMonth: Int = 1,
    
    // OEM Market Selling (Dış Pazara Satış)
    val isOemSaleActive: Boolean = false,
    val oemSalePrice: Int = 45, // External selling price per unit ($)
    val totalUnitsSoldToThirdParties: Long = 0L,
    val totalOemRevenueEarned: Long = 0L,
    val lastPeriodUnitsSold: Int = 0,
    val lastPeriodOemIncome: Long = 0L,
    val isArchived: Boolean = false
) {
    // Calculated Performance Score for Benchmarks & Phone Reviews
    val performanceScore: Int
        get() {
            val baseCoreScore = coreCount * (clockSpeedGhz * 68f)
            val nodeMultiplier = processNode.efficiencyBonus
            val gpuBonus = gpuArchitecture.perfBonus
            val npuBonus = npuArchitecture.perfBonus
            val profileMultiplier = powerProfile.perfMultiplier
            return (((baseCoreScore + gpuBonus + npuBonus) * nodeMultiplier * profileMultiplier)).toInt().coerceIn(120, 5500)
        }

    // Calculated Unit Production Cost ($ per chip inside phone bill of materials)
    val unitCost: Int
        get() {
            val baseNodeCost = processNode.baseCost
            val coreCost = (coreCount * 2.5f).toInt()
            val speedCost = ((clockSpeedGhz - 1.4f) * 9f).toInt().coerceAtLeast(0)
            val gpuCost = gpuArchitecture.extraCost
            val npuCost = npuArchitecture.extraCost
            return (baseNodeCost + coreCost + speedCost + gpuCost + npuCost).coerceIn(12, 350)
        }

    // Estimated R&D Tape-out / Fabrication Mask Cost ($)
    val tapeOutCost: Long
        get() {
            val base = when (tier) {
                ChipsetTier.ENTRY_LITE -> 350000L
                ChipsetTier.MID_RANGE -> 950000L
                ChipsetTier.FLAGSHIP_PRO -> 2800000L
            }
            return (base * (processNode.efficiencyBonus * 0.75f)).toLong()
        }

    val profitPerUnit: Int
        get() = (oemSalePrice - unitCost).coerceAtLeast(0)
}
