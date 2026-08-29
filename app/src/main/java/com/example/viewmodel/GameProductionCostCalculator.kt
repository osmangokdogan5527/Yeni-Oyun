package com.example.viewmodel

import kotlin.math.roundToInt
import kotlin.random.Random

data class RecallOutcome(
    val model: ActiveModel,
    val compensationCost: Long,
    val reputationPenalty: Int
)

fun calculateProductionCost(
    specs: PhoneSpecs,
    customChipsets: List<CustomChipset>,
    unitCostDiscountPercent: Float
): Int {
    var cost = 30 // Base assembly, packaging, logistics & licensing overhead

    // Material & Frame & Finish
    cost += when(specs.material) { "Plastik" -> 5; "Alüminyum" -> 20; "Cam" -> 30; "Titanyum" -> 60; else -> 10 }
    cost += when(specs.backFinish) { "Buzlu Mat Cam" -> 0; "Parlak Ayna Cam" -> 5; "Vegan Deri" -> 10; "Fırçalanmış Metal" -> 8; "Karbon Fiber" -> 15; else -> 0 }
    cost += when(specs.cameraBumpStyle) { "Dikey Ada" -> 0; "Dairesel Halo" -> 5; "Yatay Vizör" -> 8; "Kare Ada" -> 5; "Yüzen Çift Halka" -> 0; else -> 0 }
    cost += when(specs.frameStyle) { "Düz Metal Kenar" -> 0; "Kavisli 2.5D" -> 5; "Zırhlı Kesim" -> 10; "Ultra İnce Çerçeve" -> 12; else -> 0 }
    cost += when(specs.notchStyle) { "Nokta Delik" -> 0; "Dinamik Ada / Hap" -> 8; "Klasik Çentik" -> 0; "Görünmez Ekran Altı" -> 25; else -> 0 }
    cost += when(specs.style) { "Modern" -> 10; "Klasik" -> 5; "Oyuncu" -> 15; "Dayanıklı" -> 20; else -> 5 }

    // Processor
    val customChip = customChipsets.find { specs.processor.contains(it.name) }
    cost += if (customChip != null) {
        customChip.unitCost
    } else {
        when {
            specs.processor.contains("Kuantum") -> 300
            specs.processor.contains("Gen 3") || specs.processor.contains("D9300") -> 240
            specs.processor.contains("Gen 1") || specs.processor.contains("D9000") -> 180
            specs.processor.contains("In-House") -> 150
            specs.processor.contains("865") || specs.processor.contains("D800") -> 140
            specs.processor.contains("845") || specs.processor.contains("G90") -> 110
            specs.processor.contains("820") || specs.processor.contains("Helio") -> 85
            specs.processor.contains("801") || specs.processor.contains("Atom") -> 60
            specs.processor.contains("S4") || specs.processor.contains("MT67") -> 40
            else -> 20
        }
    }

    // RAM Capacity
    cost += when {
        specs.ramCapacity.contains("32") -> 140
        specs.ramCapacity.contains("24") -> 110
        specs.ramCapacity.contains("16") -> 80
        specs.ramCapacity.contains("12") -> 60
        specs.ramCapacity.contains("8") -> 42
        specs.ramCapacity.contains("6") -> 30
        specs.ramCapacity.contains("4") -> 20
        specs.ramCapacity.contains("3") -> 15
        specs.ramCapacity.contains("2") -> 10
        specs.ramCapacity.contains("1") -> 6
        else -> 3 // 512 MB
    }

    // RAM Type
    cost += when {
        specs.ramType.contains("LPDDR6") -> 75
        specs.ramType.contains("LPDDR5X") -> 55
        specs.ramType.contains("LPDDR5") -> 40
        specs.ramType.contains("LPDDR4X") -> 28
        specs.ramType.contains("LPDDR4") -> 18
        specs.ramType.contains("LPDDR3") -> 10
        specs.ramType.contains("LPDDR2") -> 5
        else -> 2 // LPDDR1
    }

    // Internal Storage
    cost += when {
        specs.storage.contains("2 TB") -> 115
        specs.storage.contains("1 TB") -> 80
        specs.storage.contains("512 GB") -> 55
        specs.storage.contains("256 GB") -> 38
        specs.storage.contains("128 GB") -> 25
        specs.storage.contains("64 GB") -> 16
        specs.storage.contains("32 GB") -> 10
        specs.storage.contains("16 GB") -> 6
        else -> 3 // 8 GB
    }

    // External SD Card Slot
    cost += when {
        specs.sdCardSupport.contains("NM") || specs.sdCardSupport.contains("Express") -> 16
        specs.sdCardSupport.contains("2 TB") -> 11
        specs.sdCardSupport.contains("512 GB") -> 7
        specs.sdCardSupport.contains("128 GB") -> 4
        specs.sdCardSupport.contains("32 GB") -> 2
        else -> 0 // Yok
    }

    // Display
    cost += when {
        specs.display.contains("Holografik") -> 200
        specs.display.contains("240Hz") -> 140
        specs.display.contains("Katlanabilir") -> 120
        specs.display.contains("144Hz") -> 100
        specs.display.contains("120Hz") -> 80
        specs.display.contains("Çerçevesiz") -> 55
        specs.display.contains("Kavisli") -> 45
        specs.display.contains("QHD") -> 30
        specs.display.contains("FHD") -> 20
        else -> 10
    }

    // Display Resolution
    cost += when {
        specs.displayResolution.contains("4K+", ignoreCase = true) -> 30
        specs.displayResolution.contains("4K", ignoreCase = true) -> 22
        specs.displayResolution.contains("QHD+", ignoreCase = true) -> 14
        specs.displayResolution.contains("QHD", ignoreCase = true) -> 10
        specs.displayResolution.contains("FHD", ignoreCase = true) -> 6
        specs.displayResolution.contains("HD", ignoreCase = true) -> 3
        else -> 0
    }

    // Display Brightness
    val brightnessNits = specs.displayBrightness.filter { it.isDigit() }.toIntOrNull() ?: 350
    cost += when {
        brightnessNits >= 3000 -> 30
        brightnessNits >= 2000 -> 22
        brightnessNits >= 1600 -> 16
        brightnessNits >= 1300 -> 12
        brightnessNits >= 1000 -> 9
        brightnessNits >= 800 -> 6
        brightnessNits >= 600 -> 4
        brightnessNits >= 450 -> 2
        else -> 0
    }

    // Glass
    cost += when {
        specs.glass.contains("Sapphire") || specs.glass.contains("Armor+") -> 80
        specs.glass.contains("Gorilla Armor") -> 60
        specs.glass.contains("Ceramic") -> 50
        specs.glass.contains("Victus") -> 35
        specs.glass.contains("Glass 5") -> 25
        specs.glass.contains("Glass 4") -> 20
        specs.glass.contains("Glass 3") -> 15
        specs.glass.contains("Glass 2") -> 10
        else -> 5
    }

    // Camera
    cost += when {
        specs.camera.contains("Donanımsal ISP") -> 250
        specs.camera.contains("GenAI") -> 180
        specs.camera.contains("1 İnç") || specs.camera.contains("200MP") -> 130
        specs.camera.contains("Ekran Altı") -> 100
        specs.camera.contains("Periskop") -> 85
        specs.camera.contains("Üçlü") -> 50
        specs.camera.contains("Çift") -> 35
        specs.camera.contains("16-20") -> 25
        specs.camera.contains("8-13") -> 15
        else -> 10
    }

    // Connectivity (Cellular, Port, Wireless)
    cost += when {
        specs.cellularNetwork.contains("Uydu") -> 45
        specs.cellularNetwork.contains("5G mmWave") -> 30
        specs.cellularNetwork.contains("5G Sub-6") -> 22
        specs.cellularNetwork.contains("4G LTE Cat 6") -> 14
        specs.cellularNetwork.contains("4G LTE") -> 10
        specs.cellularNetwork.contains("3G HSPA+") -> 5
        specs.cellularNetwork.contains("3G") -> 3
        else -> 2
    }

    cost += when {
        specs.chargingPort.contains("Thunderbolt 4") -> 35
        specs.chargingPort.contains("USB-C 3.2") -> 22
        specs.chargingPort.contains("USB-C 3.1") -> 15
        specs.chargingPort.contains("USB-C 2.0") -> 10
        specs.chargingPort.contains("USB 3.0 Micro-B") -> 8
        specs.chargingPort.contains("Micro-USB") -> 3
        specs.chargingPort.contains("Mini-USB") -> 2
        else -> 2
    }

    cost += when {
        specs.wirelessConnectivity.contains("Wi-Fi 7") -> 30
        specs.wirelessConnectivity.contains("Wi-Fi 6E") -> 20
        specs.wirelessConnectivity.contains("Wi-Fi 6") -> 14
        specs.wirelessConnectivity.contains("Wi-Fi 5 (ac)") -> 9
        specs.wirelessConnectivity.contains("Wi-Fi 4 (n)") -> 5
        specs.wirelessConnectivity.contains("Wi-Fi 4 & BT 2.1") -> 3
        else -> 2
    }

    // Audio
    cost += when {
        specs.audio.contains("AI") || specs.audio.contains("Yapay Zeka") -> 45
        specs.audio.contains("Kayıpsız") -> 35
        specs.audio.contains("Uzamsal") -> 28
        specs.audio.contains("Asimetrik") -> 22
        specs.audio.contains("Dolby") -> 18
        specs.audio.contains("Jaksız") || specs.audio.contains("Tip-C") -> 14
        specs.audio.contains("Ön Stereo") -> 10
        specs.audio.contains("Beats") -> 6
        else -> 3
    }

    // Battery Capacity
    cost += when {
        specs.batteryCapacity.contains("7000") -> 45
        specs.batteryCapacity.contains("5500") -> 35
        specs.batteryCapacity.contains("5000") -> 30
        specs.batteryCapacity.contains("4500") -> 25
        specs.batteryCapacity.contains("4000") -> 20
        specs.batteryCapacity.contains("3600") || specs.batteryCapacity.contains("3500") -> 16
        specs.batteryCapacity.contains("3200") -> 12
        specs.batteryCapacity.contains("3100") -> 10
        specs.batteryCapacity.contains("2500") -> 8
        else -> 5
    }

    // Battery Type & Charging
    cost += when {
        specs.batteryType.contains("240W") -> 65
        specs.batteryType.contains("200W") || specs.batteryType.contains("Si-Ca") || specs.batteryType.contains("Silisyum") -> 50
        specs.batteryType.contains("100W") -> 42
        specs.batteryType.contains("120W") -> 35
        specs.batteryType.contains("65W") -> 25
        specs.batteryType.contains("25W") -> 18
        specs.batteryType.contains("20W") -> 14
        specs.batteryType.contains("15W") || specs.batteryType.contains("18W") -> 8
        specs.batteryType.contains("10W") -> 5
        else -> 2
    }

    // Multi-color setup cost: +$3 per extra color option beyond 1
    val extraColorCount = (specs.selectedColors.size - 1).coerceAtLeast(0)
    cost += extraColorCount * 3

    // OS License Fee
    cost += specs.osLicenseFee

    // Apply Assembly Worker & Factory Tier production discount
    val discountMultiplier = 1.0f - (unitCostDiscountPercent / 100.0f)
    return (cost * discountMultiplier).toInt().coerceAtLeast(5)
}

fun calculateRecallRisk(qaPerUnit: Float, reviewScore: Int): Int {
    val qaProtection = (qaPerUnit * 3f).coerceAtMost(40f)
    val reviewProtection = ((reviewScore - 40).coerceAtLeast(0) * 0.6f).coerceAtMost(35f)
    val baseRisk = 55f
    return (baseRisk - qaProtection - reviewProtection).roundToInt().coerceIn(2, 55)
}

fun detectHardwareCrisisType(specs: PhoneSpecs): HardwareCrisisType {
    val batteryLower = specs.batteryType.lowercase()
    val matLower = specs.material.lowercase()
    val procLower = specs.processor.lowercase()

    return when {
        batteryLower.contains("120w") || batteryLower.contains("65w") || specs.batteryCapacity.contains("5000") || specs.batteryCapacity.contains("7000") -> HardwareCrisisType.BATTERY_OVERHEATING
        matLower.contains("alüminyum") || specs.thicknessMm <= 7.2f || specs.frameStyle.contains("Ultra İnce") -> HardwareCrisisType.CHASSIS_BENDGATE
        procLower.contains("888") || procLower.contains("810") || procLower.contains("gen") || procLower.contains("kuantum") -> HardwareCrisisType.SOC_THROTTLING
        specs.camera.contains("200mp") || specs.camera.contains("periskop") || specs.camera.contains("üçlü") -> HardwareCrisisType.CAMERA_FOCUS_BLUR
        else -> HardwareCrisisType.DISPLAY_GREEN_LINE
    }
}

fun checkForRecall(model: ActiveModel, year: Int, month: Int): Pair<ActiveModel, RecallOutcome?> {
    if (model.isRecalled || model.recallRiskPercent <= 0 || model.monthsOnMarket > 3) {
        return model to null
    }

    var monthlyHazardPercent = model.recallRiskPercent / 3f
    // Yüksek Hype ile kalitesiz cihaz satıldığında kullanıcı baskısı ve aşırı ısınma/donma krizleri hızla patlak verir
    if (model.hypeScore >= 60 && model.customerSatisfactionScore <= 35) {
        monthlyHazardPercent *= 1.8f
    }

    if (Random.nextFloat() * 100f >= monthlyHazardPercent) {
        return model to null
    }

    val compensationCost = (model.totalSold.toLong() * model.specs.unitCost) +
        (model.remainingStock.toLong() * (model.specs.unitCost / 2))
    val reputationPenalty = (((70 - model.reviewScore).coerceAtLeast(15)) / 3).coerceIn(3, 15)

    // Note: Do not wipe stock or permanently set isRecalled = true here.
    // The player will choose their crisis resolution strategy (Full Recall, Free Service, or Software Patch) in the Crisis Dialog.
    // We set recallRiskPercent = 0 to avoid repeated triggering of the detection loop.
    val updatedModel = model.copy(
        recallRiskPercent = 0
    )

    return updatedModel to RecallOutcome(
        model = updatedModel,
        compensationCost = compensationCost,
        reputationPenalty = reputationPenalty
    )
}

data class MarketSegmentAnalysis(
    val detectedSegment: String,
    val segmentBadge: String,
    val recommendedPriceRange: IntRange,
    val estimatedReviewScore: Int,
    val priceCompetitiveness: String, // "Mükemmel Fiyat", "Dengeli", "Pahalı", "Aşırı Fiyat"
    val demandMultiplierPercent: Int, // e.g. 130 means +30% demand, 60 means -40% demand
    val profitPerUnit: Int,
    val profitMarginPercent: Int,
    val priceElasticityNote: String
)

fun analyzeModelMarketPosition(
    unitCost: Int,
    price: Float,
    tier: ModelTier,
    specs: PhoneSpecs,
    reputation: Int
): MarketSegmentAnalysis {
    val profit = (price.toInt() - unitCost)
    val marginPercent = if (price > 0) ((profit.toFloat() / price) * 100f).roundToInt() else 0

    // Detect hardware tier based on raw hardware cost
    val (hardwareSegment, badge, fairPriceRange) = when {
        unitCost >= 500 -> Triple("Ultra Amiral Gemisi", "👑", 1099..1799)
        unitCost >= 320 -> Triple("Amiral Gemisi & Premium", "⚡", 799..1199)
        unitCost >= 180 -> Triple("Orta-Üst Segment", "💎", 499..799)
        unitCost >= 90 -> Triple("Orta Segment (F/P)", "⚖️", 299..499)
        else -> Triple("Giriş Seviyesi (Ekonomik)", "🌱", 129..299)
    }

    // Estimate review score base
    val hwScore = when {
        unitCost >= 500 -> 92
        unitCost >= 350 -> 84
        unitCost >= 220 -> 76
        unitCost >= 130 -> 68
        else -> 58
    }

    // Price elasticity & price vs expected range
    val midFair = (fairPriceRange.first + fairPriceRange.last) / 2
    val priceRatio = price / midFair.toFloat()

    val (competitiveness, demandMult, elasticityNote) = when {
        price <= fairPriceRange.first * 0.85f -> Triple("Agresif Fiyat Kırıcı (Yüksek Talep)", 145, "🔥 Fiyat donanıma göre çok uygun! Tüketiciler akın edecek, yüksek talep.")
        price <= fairPriceRange.first -> Triple("Mükemmel F/P Oranı", 125, "✨ Fiyat/performans dengesi harika. Pazar payı kazanmak için ideal.")
        price <= fairPriceRange.last -> Triple("Dengeli Piyasa Fiyatı", 100, "✅ Segment standartlarına tam uygun kâr ve talep dengesi.")
        price <= fairPriceRange.last * 1.25f -> Triple("Yüksek Fiyatlandırma", 75, "⚠️ Fiyat segmente göre biraz tuzlu. Sadece marka sadakati olanlar tercih edebilir.")
        else -> Triple("Aşırı Pahalı (Stok Riski)", 45, "🚨 Tüketici tepkisi! Donanımına göre aşırı pahalı, talep sert düşecek ve stok riski oluşabilir.")
    }

    // Score adjustment for pricing
    val priceScoreModifier = when {
        priceRatio < 0.8f -> +6
        priceRatio < 1.05f -> +2
        priceRatio < 1.25f -> -4
        else -> -10
    }

    val estimatedScore = (hwScore + priceScoreModifier + (reputation / 20)).coerceIn(35, 99)

    return MarketSegmentAnalysis(
        detectedSegment = hardwareSegment,
        segmentBadge = badge,
        recommendedPriceRange = fairPriceRange,
        estimatedReviewScore = estimatedScore,
        priceCompetitiveness = competitiveness,
        demandMultiplierPercent = demandMult,
        profitPerUnit = profit,
        profitMarginPercent = marginPercent,
        priceElasticityNote = elasticityNote
    )
}

