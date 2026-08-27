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
    var cost = 15 // Base assembly cost

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
        specs.audio.contains("AI") -> 100
        specs.audio.contains("Kayıpsız") -> 80
        specs.audio.contains("Uzamsal") -> 60
        specs.audio.contains("Asimetrik") -> 45
        specs.audio.contains("Dolby") -> 35
        specs.audio.contains("Jaksız") -> 25
        specs.audio.contains("Ön Stereo") -> 20
        specs.audio.contains("Beats") -> 10
        else -> 5
    }

    // Battery Capacity
    cost += when {
        specs.batteryCapacity.contains("7000") -> 45
        specs.batteryCapacity.contains("5500") -> 35
        specs.batteryCapacity.contains("5000") -> 30
        specs.batteryCapacity.contains("4500") -> 25
        specs.batteryCapacity.contains("4000") -> 20
        specs.batteryCapacity.contains("3600") -> 16
        specs.batteryCapacity.contains("3200") -> 12
        specs.batteryCapacity.contains("3100") -> 10
        else -> 2
    }

    // Battery Type & Charging
    cost += when {
        specs.batteryType.contains("Katı Hal 240W") -> 150
        specs.batteryType.contains("Katı Hal 100W") -> 100
        specs.batteryType.contains("Si-Ca") -> 85
        specs.batteryType.contains("120W") -> 60
        specs.batteryType.contains("65W") -> 40
        specs.batteryType.contains("25W") -> 25
        specs.batteryType.contains("20W") -> 20
        specs.batteryType.contains("15W") -> 15
        specs.batteryType.contains("10W") -> 8
        else -> 5
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

fun checkForRecall(model: ActiveModel, year: Int, month: Int): Pair<ActiveModel, RecallOutcome?> {
    if (model.isRecalled || model.recallRiskPercent <= 0 || model.monthsOnMarket > 3) {
        return model to null
    }

    val monthlyHazardPercent = model.recallRiskPercent / 3f
    if (Random.nextFloat() * 100f >= monthlyHazardPercent) {
        return model to null
    }

    val compensationCost = (model.totalSold.toLong() * model.specs.unitCost) +
        (model.remainingStock.toLong() * (model.specs.unitCost / 2))
    val reputationPenalty = (((70 - model.reviewScore).coerceAtLeast(15)) / 3).coerceIn(3, 15)

    val recalledModel = model.copy(
        remainingStock = 0,
        isRecalled = true,
        recalledYear = year,
        recalledMonth = month
    )

    return recalledModel to RecallOutcome(
        model = recalledModel,
        compensationCost = compensationCost,
        reputationPenalty = reputationPenalty
    )
}
