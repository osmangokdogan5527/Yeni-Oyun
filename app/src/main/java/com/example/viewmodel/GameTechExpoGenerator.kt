package com.example.viewmodel

import kotlin.random.Random

fun generateTechExpo(
    year: Int,
    playerModels: List<ActiveModel>,
    playerCompanyName: String,
    playerBrandColorHex: Long,
    competitors: List<CompetitorCompany>
): TechExpoEvent {
    val expoNames = listOf(
        "MWC (Mobile World Congress)" to "Barselona, İspanya",
        "CES (Consumer Electronics Show)" to "Las Vegas, ABD",
        "IFA Tech Global" to "Berlin, Almanya",
        "Computex World Expo" to "Taipei, Tayvan"
    )
    val selectedExpo = expoNames[(year + 3) % expoNames.size]

    // Pool of all eligible competitor phones for this year
    val competitorNominees = competitors.map { comp ->
        AwardNominee(
            modelName = comp.currentTopModel,
            companyName = comp.name,
            isPlayer = false,
            logoEmoji = comp.logoEmoji,
            brandColorHex = comp.brandColorHex,
            score = comp.currentModelScore,
            price = comp.currentModelPrice,
            highlightText = "${comp.name}, ${comp.currentTopModel} modeliyle ${comp.strategyType} alanında jüriden takdir topladı."
        )
    }

    // Player nominees from active models
    val playerNominees = playerModels.map { model ->
        AwardNominee(
            modelName = model.specs.name,
            companyName = playerCompanyName,
            isPlayer = true,
            logoEmoji = "📱",
            brandColorHex = playerBrandColorHex,
            score = model.reviewScore,
            price = model.specs.price,
            highlightText = "${playerCompanyName} imzalı ${model.specs.name}, yenilikçi tasarımı (${model.specs.material}) ve ${model.specs.osType} işletim sistemiyle fuarda büyük sükse yaptı."
        )
    }

    val allNominees = competitorNominees + playerNominees

    val awardResults = mutableListOf<AwardResult>()
    var playerWonCount = 0
    var totalPrizeWon = 0L
    var reputationGained = 0

    // 1. FLAGSHIP OF THE YEAR (Highest overall score)
    val flagshipNominees = allNominees.sortedByDescending { it.score }.take(4)
    val flagshipWinner = flagshipNominees.firstOrNull() ?: competitorNominees.first()
    if (flagshipWinner.isPlayer) {
        playerWonCount++
        totalPrizeWon += AwardCategory.FLAGSHIP_OF_THE_YEAR.prizeMoney
        reputationGained += AwardCategory.FLAGSHIP_OF_THE_YEAR.reputationBonus
    }
    awardResults.add(
        AwardResult(
            category = AwardCategory.FLAGSHIP_OF_THE_YEAR,
            winner = flagshipWinner,
            nominees = flagshipNominees,
            ceremonyReview = "${flagshipWinner.companyName} üretimi ${flagshipWinner.modelName}, olağanüstü ${flagshipWinner.score}/100 inceleme puanı ve tavizsiz donanımıyla ${year} Yılının En İyi Amiral Gemisi seçildi!"
        )
    )

    // 2. VALUE CHAMPION (Best score / price ratio)
    val valueNominees = allNominees.sortedByDescending { (it.score.toFloat() / it.price.coerceAtLeast(100).toFloat()) * 1000f }.take(4)
    val valueWinner = valueNominees.firstOrNull() ?: competitorNominees.first()
    if (valueWinner.isPlayer) {
        playerWonCount++
        totalPrizeWon += AwardCategory.VALUE_CHAMPION.prizeMoney
        reputationGained += AwardCategory.VALUE_CHAMPION.reputationBonus
    }
    awardResults.add(
        AwardResult(
            category = AwardCategory.VALUE_CHAMPION,
            winner = valueWinner,
            nominees = valueNominees,
            ceremonyReview = "${valueWinner.companyName} tarafından sunulan ${valueWinner.modelName}, $${valueWinner.price} fiyat etiketine karşılık sunduğu ${valueWinner.score} puanlık üstün deneyimle Fiyat/Performans Tacını kazandı!"
        )
    )

    // 3. INNOVATION AWARD (Favoring high tech score or special features)
    val innovationNominees = allNominees.shuffled().sortedByDescending {
        it.score + if (it.isPlayer) 5 else Random.nextInt(-3, 4)
    }.take(4)
    val innovationWinner = innovationNominees.firstOrNull() ?: competitorNominees.first()
    if (innovationWinner.isPlayer) {
        playerWonCount++
        totalPrizeWon += AwardCategory.INNOVATION_AWARD.prizeMoney
        reputationGained += AwardCategory.INNOVATION_AWARD.reputationBonus
    }
    awardResults.add(
        AwardResult(
            category = AwardCategory.INNOVATION_AWARD,
            winner = innovationWinner,
            nominees = innovationNominees,
            ceremonyReview = "${innovationWinner.companyName} ${innovationWinner.modelName}, endüstri standartlarını aşan cesur inovasyonları ve mühendislik başarısıyla Yılın İnovasyonu Ödülüne layık görüldü!"
        )
    )

    // 4. BEST DESIGN AWARD
    val designNominees = allNominees.shuffled().take(4).sortedByDescending { it.score }
    val designWinner = designNominees.firstOrNull() ?: competitorNominees.first()
    if (designWinner.isPlayer) {
        playerWonCount++
        totalPrizeWon += AwardCategory.BEST_DESIGN.prizeMoney
        reputationGained += AwardCategory.BEST_DESIGN.reputationBonus
    }
    awardResults.add(
        AwardResult(
            category = AwardCategory.BEST_DESIGN,
            winner = designWinner,
            nominees = designNominees,
            ceremonyReview = "${designWinner.companyName} tasarımı ${designWinner.modelName}, kusursuz malzeme kalitesi, ince çerçeveleri ve ergonomisiyle En İyi Endüstriyel Tasarım Ödülünü kazandı!"
        )
    )

    return TechExpoEvent(
        year = year,
        expoName = selectedExpo.first,
        city = selectedExpo.second,
        awards = awardResults,
        playerWonCount = playerWonCount,
        totalPrizeWon = totalPrizeWon,
        reputationGained = reputationGained
    )
}

val DEFAULT_RIVAL_OPERATING_SYSTEMS: List<CompetitorOsInfo> = listOf(
    CompetitorOsInfo(
        id = "os_ios",
        name = "iOS (Apple OS)",
        company = "Apple Inc.",
        iconEmoji = "🍎",
        licenseTypeBadge = "🔒 Kapalı / Tescilli",
        marketSharePercent = 23.5f,
        techScore = 96,
        ecosystemScore = 99,
        userBaseFormatted = "1.25 Milyar",
        appCountFormatted = "2.2M+ Uygulama",
        monthlyEcosystemRevenue = "$1,850,000,000",
        coreStrength = "Kusursuz donanım-yazılım optimizasyonu, Metal grafik motoru, rekor kâr marjı ve %98 kullanıcı bağlılığı.",
        mainFlaw = "Katı kapalı duvar bahçesi ve yan yükleme kısıtlamaları.",
        brandColorHex = 0xFF0F172A
    ),
    CompetitorOsInfo(
        id = "os_android",
        name = "Android (GMS/AOSP)",
        company = "Google LLC",
        iconEmoji = "🌐",
        licenseTypeBadge = "🌐 Açık Kaynak",
        marketSharePercent = 63.5f,
        techScore = 94,
        ecosystemScore = 98,
        userBaseFormatted = "3.20 Milyar",
        appCountFormatted = "3.8M+ Uygulama",
        monthlyEcosystemRevenue = "$2,400,000,000",
        coreStrength = "Dünya çapında binlerce OEM üretici desteği, Google Play Store ve derin Gemini AI entegrasyonu.",
        mainFlaw = "Cihaz parçalanması ve eski telefonlara güvenlik güncellemesi gecikmeleri.",
        brandColorHex = 0xFF10B981
    ),
    CompetitorOsInfo(
        id = "os_harmony",
        name = "HarmonyOS (Next)",
        company = "Huawei Technologies",
        iconEmoji = "🌸",
        licenseTypeBadge = "⚡ Mikroçekirdek",
        marketSharePercent = 8.2f,
        techScore = 90,
        ecosystemScore = 86,
        userBaseFormatted = "550 Milyon",
        appCountFormatted = "950K+ Uygulama",
        monthlyEcosystemRevenue = "$380,000,000",
        coreStrength = "Süper Cihaz dağıtık mikroçekirdek mimarisi, Kirin çip senkronizasyonu ve Asya pazarında dev sadakat.",
        mainFlaw = "Batı pazarlarında GMS kısıtları ve küresel uygulama adaptasyon süreci.",
        brandColorHex = 0xFFCF0A2C
    ),
    CompetitorOsInfo(
        id = "os_oneui",
        name = "One UI (Galaxy OS)",
        company = "Samsung Electronics",
        iconEmoji = "🌌",
        licenseTypeBadge = "📱 Özel Android Arayüzü",
        marketSharePercent = 18.8f,
        techScore = 92,
        ecosystemScore = 93,
        userBaseFormatted = "1.15 Milyar",
        appCountFormatted = "2.9M+ Uygulama",
        monthlyEcosystemRevenue = "$880,000,000",
        coreStrength = "DeX masaüstü modu, Galaxy AI üretkenlik araçları ve katlanabilir ekran optimizasyonu.",
        mainFlaw = "Geniş sistem boyutu ve yoğun arka plan bellek kullanımı.",
        brandColorHex = 0xFF1428A0
    ),
    CompetitorOsInfo(
        id = "os_hyperos",
        name = "HyperOS",
        company = "Xiaomi Corp",
        iconEmoji = "🟠",
        licenseTypeBadge = "⚡ Hibrit Kernel",
        marketSharePercent = 9.4f,
        techScore = 88,
        ecosystemScore = 87,
        userBaseFormatted = "620 Milyon",
        appCountFormatted = "2.1M+ Uygulama",
        monthlyEcosystemRevenue = "$440,000,000",
        coreStrength = "Vela IoT mimarisi, elektrikli otomobil & akıllı ev cihazları arası ultra hızlı bağlantı.",
        mainFlaw = "Sistem içi reklamlar ve agresif arka plan pil yönetimi kısıtları.",
        brandColorHex = 0xFFFF6900
    ),
    CompetitorOsInfo(
        id = "os_windows",
        name = "Windows 10 Mobile",
        company = "Microsoft",
        iconEmoji = "🪟",
        licenseTypeBadge = "🔒 Kapalı Kaynak",
        marketSharePercent = 0.6f,
        techScore = 75,
        ecosystemScore = 40,
        userBaseFormatted = "15 Milyon",
        appCountFormatted = "280K Uygulama",
        monthlyEcosystemRevenue = "$15,000,000",
        coreStrength = "Canlı kutucuklar (Live Tiles), Continuum masaüstü modu ve yerel Microsoft Office desteği.",
        mainFlaw = "Geliştiricilerin platformu terk etmesi ve kritik popüler uygulamaların eksikliği.",
        brandColorHex = 0xFF00A4EF
    )
)
