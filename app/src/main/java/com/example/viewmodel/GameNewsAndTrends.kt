package com.example.viewmodel

import kotlin.random.Random

fun getHistoricalNewsForYearMonth(year: Int, month: Int): List<NewsArticle> {
    val list = mutableListOf<NewsArticle>()
    
    when {
        year == 2010 && month == 1 -> list.add(NewsArticle("hist_2010_1", "Akıllı Telefon Çağı Başlıyor", "Dokunmatik ekranlı akıllı cihazlar geleneksel tuşlu telefonların pazar payını hızla ele geçiriyor.", "Teknoloji", 2010, 1))
        year == 2010 && month == 6 -> list.add(NewsArticle("hist_2010_6", "Mobil Uygulama Ekosistemleri Genişliyor", "Kullanıcılar cihaz tercihlerinde uygulama desteğine ve ekran kalitesine önem vermeye başladı.", "Pazar", 2010, 6))
        year == 2011 && month == 1 -> list.add(NewsArticle("hist_2011_1", "Çift Çekirdekli İşlemci Çağı", "Mobil oyunlar ve HD videolar akıllı telefonlarda standart hale geliyor. Yüksek RAM kapasitesi aranıyor.", "Teknoloji", 2011, 1))
        year == 2011 && month == 7 -> list.add(NewsArticle("hist_2011_7", "Ön Kamera ve Sosyal Medya Patlaması", "Ön kameralı telefonlara olan talep rekor kırıyor. Özçekim (selfie) trendi pazarı yönlendiriyor.", "Sektör", 2011, 7))
        year == 2012 && month == 1 -> list.add(NewsArticle("hist_2012_1", "4G LTE Hızında Mobil Bağlantı", "Yüksek hızlı 4G şebekeleri yaygınlaşırken kullanıcılar görüntülü görüşme ve yayıncılığa yöneliyor.", "Teknoloji", 2012, 1))
        year == 2012 && month == 8 -> list.add(NewsArticle("hist_2012_8", "İnce Tasarım Yarışı", "8mm altı alüminyum ve cam gövdeli cihazlar premium segmente yön veriyor.", "Pazar", 2012, 8))
        year == 2013 && month == 1 -> list.add(NewsArticle("hist_2013_1", "Full HD (1080p) Ekran Devrimi", "Keskin görseller sunan 1080p paneller amiral gemisi telefonların vazgeçilmezi oldu.", "Teknoloji", 2013, 1))
        year == 2013 && month == 9 -> list.add(NewsArticle("hist_2013_9", "Biyometrik Parmak İzi Sensörleri", "Cihaz güvenliğinde parmak izi okuyucular yeni dönem başlatıyor.", "Sektör", 2013, 9))
        year == 2014 && month == 1 -> list.add(NewsArticle("hist_2014_1", "Büyük Ekran (Phablet) Popülaritesi", "5.5 inç ve üzeri geniş ekranlı cihazlar medya tüketiminde birinci tercih haline geldi.", "Pazar", 2014, 1))
        year == 2015 && month == 1 -> list.add(NewsArticle("hist_2015_1", "64-Bit İşlemciler ve 4GB RAM Standardı", "Performansta masaüstü bilgisayar seviyesine yaklaşan yeni mobil çipler tanıtıldı.", "Teknoloji", 2015, 1))
        year == 2016 && month == 1 -> list.add(NewsArticle("hist_2016_1", "Çift Kamera Dönemi ve Portre Modu", "Optik zoom ve arka plan bulanıklaştırma sunan çift kamera sistemleri amiral gemilerinde şart.", "Teknoloji", 2016, 1))
        year == 2017 && month == 1 -> list.add(NewsArticle("hist_2017_1", "Çerçevesiz Ekran ve Çentik Modası", "Ekran/gövde oranı %85 üzerine çıktı. 16:9 standart ekranlı cihaz satmak zorlaşıyor.", "Pazar", 2017, 1))
        year == 2018 && month == 1 -> list.add(NewsArticle("hist_2018_1", "Ekran İçi Parmak İzi & Üçlü Kameralar", "Gizli sensörler ve gelişmiş gece modu fotoğrafçılığı tüketicilerin gözdesi.", "Teknoloji", 2018, 1))
        year == 2019 && month == 1 -> list.add(NewsArticle("hist_2019_1", "5G Bağlantı ve Katlanabilir Ekranlar", "Yeni nesil katlanabilir cihazlar ve ultra hızlı 5G çipleri satışa sunuldu.", "Sektör", 2019, 1))
        year == 2020 && month == 1 -> list.add(NewsArticle("hist_2020_1", "120Hz Yüksek Yenileme Hızlı OLED Ekranlar", "Kullanıcılar takılmasız ve yağ gibi akan 120Hz ekran tecrübesi talep ediyor.", "Teknoloji", 2020, 1))
        year == 2021 && month == 1 -> list.add(NewsArticle("hist_2021_1", "5nm İşlemciler ve Güç Verimliliği", "Pil ömrünü uzatan ve ısınmayı engelleyen gelişmiş işlemciler pazara hakim.", "Teknoloji", 2021, 1))
        year == 2022 && month == 1 -> list.add(NewsArticle("hist_2022_1", "NPU Yapay Zeka Çipleri Mobil Cihazlarda", "Fotoğraf işleme, ses tanıma ve pil yönetimi tamamen yapay zekaya devredildi.", "Teknoloji", 2022, 1))
        year == 2023 && month == 1 -> list.add(NewsArticle("hist_2023_1", "Katlanabilir Ekran Satışları Katlandı", "Esnek ekran maliyetlerinin düşmesiyle katlanabilir modeller pazar payını artırdı.", "Pazar", 2023, 1))
        year == 2024 && month == 1 -> list.add(NewsArticle("hist_2024_1", "Cihaz Üstü Üretken Yapay Zeka (GenAI)", "Çevrimdışı çalışan yapay zeka asistanları cihaz tercihlerinde 1 numaralı faktör.", "Teknoloji", 2024, 1))
        year >= 2025 && month == 1 -> list.add(NewsArticle("hist_${year}_1", "Geleceğin Mobil Teknolojileri", "Katı hal bataryalar ve holografik ekran araştırmaları sektörde heyecan yaratıyor.", "Teknoloji", year, 1))
    }
    return list
}

fun checkTrendMatch(specs: PhoneSpecs, trend: MarketTrend?): Boolean {
    if (trend == null) return false
    return when (trend.category) {
        TrendCategory.HIGH_REFRESH_DISPLAY -> {
            specs.display.contains("120Hz") || specs.display.contains("144Hz") || specs.display.contains("240Hz") || specs.style == "Oyuncu"
        }
        TrendCategory.CAMERA_PRO -> {
            specs.camera.contains("Çift") || specs.camera.contains("Üçlü") || specs.camera.contains("Periskop") || specs.camera.contains("200MP") || specs.camera.contains("GenAI") || specs.camera.contains("16-20") || specs.camera.contains("13 MP")
        }
        TrendCategory.LONG_BATTERY -> {
            val mah = Regex("""\d+""").find(specs.batteryCapacity)?.value?.toIntOrNull() ?: 1500
            mah >= 3100 || specs.batteryType.contains("Katı Hal") || specs.batteryType.contains("Si-Ca") || specs.batteryType.contains("Silisyum")
        }
        TrendCategory.BUDGET_VALUE -> {
            specs.price <= 400
        }
        TrendCategory.PREMIUM_BUILD -> {
            specs.material == "Titanyum" || specs.material == "Cam" || specs.material == "Alüminyum" || 
            specs.backFinish == "Vegan Deri" || specs.backFinish == "Karbon Fiber" || specs.backFinish == "Buzlu Mat Cam" ||
            specs.frameStyle == "Ultra İnce Çerçeve" || specs.style == "Modern"
        }
        TrendCategory.AI_PROCESSOR -> {
            specs.processor.contains("Gen") || specs.processor.contains("In-House") || specs.processor.contains("Kuantum") || 
            specs.processor.contains("865") || specs.processor.contains("888") || specs.processor.contains("845") || 
            specs.ramCapacity.contains("12") || specs.ramCapacity.contains("16") || specs.ramCapacity.contains("24") || specs.ramCapacity.contains("32") ||
            specs.ramType.contains("LPDDR5") || specs.ramType.contains("LPDDR6")
        }
        TrendCategory.FAST_CONNECTIVITY -> {
            specs.connectivity.contains("5G") || specs.connectivity.contains("Wi-Fi 6") || specs.connectivity.contains("Wi-Fi 7") || specs.connectivity.contains("Thunderbolt") || specs.connectivity.contains("Uydu") || specs.connectivity.contains("4G")
        }
    }
}

fun generateNewTrend(year: Int, currentCategory: TrendCategory? = null): MarketTrend {
    val pool = when {
        year <= 2013 -> listOf(
            MarketTrend(
                id = "tr_cam_${Random.nextInt(100, 999)}",
                title = "Özçekim & HD Kamera Çılgınlığı",
                description = "Sosyal medya patlamasıyla tüketiciler yüksek çözünürlüklü ön/arka kamera istiyor.",
                category = TrendCategory.CAMERA_PRO,
                bonusMultiplier = 1.20f,
                remainingMonths = 4,
                totalDurationMonths = 4,
                tip = "13MP HD veya 20MP OIS kamera seçin."
            ),
            MarketTrend(
                id = "tr_prem_${Random.nextInt(100, 999)}",
                title = "İnce & Alüminyum Gövde Modası",
                description = "Plastikten sıkılan kullanıcılar metal şıklığını ve ince tasarımı arıyor.",
                category = TrendCategory.PREMIUM_BUILD,
                bonusMultiplier = 1.15f,
                remainingMonths = 4,
                totalDurationMonths = 4,
                tip = "Alüminyum gövde veya ince tasarım çizgileri kullanın."
            ),
            MarketTrend(
                id = "tr_budg_${Random.nextInt(100, 999)}",
                title = "Ekonomik Akıllı Telefon Akını",
                description = "Gelişmekte olan pazarlarda $400 altı bütçe dostu cihazlar kapışılıyor.",
                category = TrendCategory.BUDGET_VALUE,
                bonusMultiplier = 1.10f,
                remainingMonths = 4,
                totalDurationMonths = 4,
                tip = "Cihaz satış fiyatını $400 veya altına ayarlayın."
            ),
            MarketTrend(
                id = "tr_bat_${Random.nextInt(100, 999)}",
                title = "Tüm Gün Yeten Batarya Arayışı",
                description = "Büyük ekranlarla artan enerji ihtiyacı için 3000mAh+ piller aranıyor.",
                category = TrendCategory.LONG_BATTERY,
                bonusMultiplier = 1.15f,
                remainingMonths = 4,
                totalDurationMonths = 4,
                tip = "3100mAh veya 3200mAh kapasiteli batarya seçin."
            ),
            MarketTrend(
                id = "tr_conn_${Random.nextInt(100, 999)}",
                title = "4G LTE Hızlı Bağlantı Talebi",
                description = "Hızlı internet isteyen mobil kullanıcılar 4G destekli telefonları tercih ediyor.",
                category = TrendCategory.FAST_CONNECTIVITY,
                bonusMultiplier = 1.20f,
                remainingMonths = 4,
                totalDurationMonths = 4,
                tip = "4G LTE şebeke desteği kullanın."
            )
        )
        year in 2014..2019 -> listOf(
            MarketTrend(
                id = "tr_disp_${Random.nextInt(100, 999)}",
                title = "Canlı AMOLED & Oyuncu Tasarımı",
                description = "Mobil oyunlar popülerleştikçe canlı ekranlar ve agresif gövde hatları revaçta.",
                category = TrendCategory.HIGH_REFRESH_DISPLAY,
                bonusMultiplier = 1.15f,
                remainingMonths = 4,
                totalDurationMonths = 4,
                tip = "AMOLED / 2K ekran veya Oyuncu tarzı kullanın."
            ),
            MarketTrend(
                id = "tr_cam2_${Random.nextInt(100, 999)}",
                title = "Çoklu Kamera & Portre Modu Modası",
                description = "Arka planı bulanıklaştıran çift ve üçlü kameralar tüketicilerin 1 numaralı tercihi.",
                category = TrendCategory.CAMERA_PRO,
                bonusMultiplier = 1.20f,
                remainingMonths = 4,
                totalDurationMonths = 4,
                tip = "Çift veya Üçlü kamera sistemi kullanın."
            ),
            MarketTrend(
                id = "tr_prem2_${Random.nextInt(100, 999)}",
                title = "Cam & Çerçevesiz Tasarım Yarışı",
                description = "Cam arka kapaklar ve lüks metal çerçeveler vitrinleri süslüyor.",
                category = TrendCategory.PREMIUM_BUILD,
                bonusMultiplier = 1.15f,
                remainingMonths = 4,
                totalDurationMonths = 4,
                tip = "Cam arka kapak veya Alüminyum kasa kullanın."
            ),
            MarketTrend(
                id = "tr_bat2_${Random.nextInt(100, 999)}",
                title = "Mega Kapasiteli Batarya & Hızlı Şarj",
                description = "Kullanıcılar 2 gün şarj istemeyen 3600mAh - 4500mAh pillere ve hızlı şarja yöneliyor.",
                category = TrendCategory.LONG_BATTERY,
                bonusMultiplier = 1.15f,
                remainingMonths = 4,
                totalDurationMonths = 4,
                tip = "3600mAh - 4500mAh batarya veya 20W+ hızlı şarj seçin."
            ),
            MarketTrend(
                id = "tr_conn2_${Random.nextInt(100, 999)}",
                title = "USB-C & Hızlı Veri Bağlantısı",
                description = "Simetrik USB-C portu ve çift bant Wi-Fi 5 standart hale geliyor.",
                category = TrendCategory.FAST_CONNECTIVITY,
                bonusMultiplier = 1.15f,
                remainingMonths = 4,
                totalDurationMonths = 4,
                tip = "USB-C simetrik port veya Wi-Fi 5 bağlantısı kullanın."
            ),
            MarketTrend(
                id = "tr_budg2_${Random.nextInt(100, 999)}",
                title = "Fiyat / Performans Patlaması",
                description = "Orta segmentte amiral gemisi hissi veren ucuz modeller kapışılıyor.",
                category = TrendCategory.BUDGET_VALUE,
                bonusMultiplier = 1.10f,
                remainingMonths = 4,
                totalDurationMonths = 4,
                tip = "Cihaz satış fiyatını $400 veya altına ayarlayın."
            )
        )
        year in 2020..2023 -> listOf(
            MarketTrend(
                id = "tr_disp2_${Random.nextInt(100, 999)}",
                title = "120Hz Ultra Akıcı OLED Ekranlar",
                description = "Takılmasız 120Hz LTPO ekranlar ve akıcı paneller tüm segmentlerde talep ediliyor.",
                category = TrendCategory.HIGH_REFRESH_DISPLAY,
                bonusMultiplier = 1.20f,
                remainingMonths = 4,
                totalDurationMonths = 4,
                tip = "120Hz LTPO AMOLED panel veya Oyuncu stili kullanın."
            ),
            MarketTrend(
                id = "tr_cam3_${Random.nextInt(100, 999)}",
                title = "108MP - 200MP Periskop Fotoğrafçılık",
                description = "Profesyonel seviye 100x periskop zoom ve 8K kayıt yeteneği aranıyor.",
                category = TrendCategory.CAMERA_PRO,
                bonusMultiplier = 1.20f,
                remainingMonths = 4,
                totalDurationMonths = 4,
                tip = "108MP Periskop veya 200MP kamera seçin."
            ),
            MarketTrend(
                id = "tr_prem3_${Random.nextInt(100, 999)}",
                title = "Titanyum Alaşım & Zırhlı Gövde Trendi",
                description = "Uzay endüstrisi sınıfı titanyum gövde modelleri prestij sembolü oldu.",
                category = TrendCategory.PREMIUM_BUILD,
                bonusMultiplier = 1.15f,
                remainingMonths = 4,
                totalDurationMonths = 4,
                tip = "Titanyum kasa veya zırhlı gövde kullanın."
            ),
            MarketTrend(
                id = "tr_conn3_${Random.nextInt(100, 999)}",
                title = "5G Ultra Hızlı Mobil Şebeke Çılgınlığı",
                description = "Kesintisiz küresel 5G Sub-6 ve mmWave bağlantısı kullanıcıların gözdesi.",
                category = TrendCategory.FAST_CONNECTIVITY,
                bonusMultiplier = 1.20f,
                remainingMonths = 4,
                totalDurationMonths = 4,
                tip = "5G Sub-6 veya 5G mmWave şebeke seçin."
            ),
            MarketTrend(
                id = "tr_ai_${Random.nextInt(100, 999)}",
                title = "NPU Yapay Zeka & Güçlü İşlemci Çipleri",
                description = "NPU birimli işlemciler ve 12GB+ RAM satın alma tercihlerini belirliyor.",
                category = TrendCategory.AI_PROCESSOR,
                bonusMultiplier = 1.25f,
                remainingMonths = 4,
                totalDurationMonths = 4,
                tip = "NPU işlemci, Qualcomm Gen / In-House çip veya 12GB+ RAM seçin."
            ),
            MarketTrend(
                id = "tr_bat3_${Random.nextInt(100, 999)}",
                title = "5000mAh+ Dev Batarya & 120W Şarj",
                description = "Dakikalar içinde dolan 120W çift hücre ve 5000mAh bataryalar revaçta.",
                category = TrendCategory.LONG_BATTERY,
                bonusMultiplier = 1.20f,
                remainingMonths = 4,
                totalDurationMonths = 4,
                tip = "5000mAh+ batarya veya 65W+ hızlı şarj seçin."
            )
        )
        else -> listOf(
            MarketTrend(
                id = "tr_ai2_${Random.nextInt(100, 999)}",
                title = "Cihaz Üstü Üretken Yapay Zeka (GenAI)",
                description = "Yerel çalışan üretken yapay zeka modelleri ve devasa RAM kapasitesi aranıyor.",
                category = TrendCategory.AI_PROCESSOR,
                bonusMultiplier = 1.25f,
                remainingMonths = 4,
                totalDurationMonths = 4,
                tip = "GenAI hızlandırıcılı çip, Kuantum işlemci veya 16GB+ RAM seçin."
            ),
            MarketTrend(
                id = "tr_bat4_${Random.nextInt(100, 999)}",
                title = "Katı Hal Batarya & 240W Şarj Devrimi",
                description = "Alev almayan katı hal bataryalar ve 7000mAh dev kapasiteler pazarı sallıyor.",
                category = TrendCategory.LONG_BATTERY,
                bonusMultiplier = 1.25f,
                remainingMonths = 4,
                totalDurationMonths = 4,
                tip = "Katı Hal batarya veya 7000mAh batarya seçin."
            ),
            MarketTrend(
                id = "tr_conn4_${Random.nextInt(100, 999)}",
                title = "Doğrudan Uydu SOS & Wi-Fi 7 İletişimi",
                description = "Şebekesiz uydu iletişimi ve 30Gbps teorik hızlı Wi-Fi 7 standart oluyor.",
                category = TrendCategory.FAST_CONNECTIVITY,
                bonusMultiplier = 1.20f,
                remainingMonths = 4,
                totalDurationMonths = 4,
                tip = "Doğrudan Uydu Şebekesi veya Wi-Fi 7 kullanın."
            ),
            MarketTrend(
                id = "tr_cam4_${Random.nextInt(100, 999)}",
                title = "GenAI ISP & 3D Mekansal Video Kameraları",
                description = "Yapay zeka render destekli kameralar ve 3D mekansal video kayıtları revaçta.",
                category = TrendCategory.CAMERA_PRO,
                bonusMultiplier = 1.20f,
                remainingMonths = 4,
                totalDurationMonths = 4,
                tip = "GenAI ISP veya 3D Mekansal Video kamera seçin."
            ),
            MarketTrend(
                id = "tr_disp3_${Random.nextInt(100, 999)}",
                title = "240Hz & Tandem OLED Ekran Teknolojisi",
                description = "Göz alıcı parlaklıktaki Tandem OLED ve holografik paneller kapışılıyor.",
                category = TrendCategory.HIGH_REFRESH_DISPLAY,
                bonusMultiplier = 1.20f,
                remainingMonths = 4,
                totalDurationMonths = 4,
                tip = "Tandem OLED, Holografik 3D veya 240Hz ekran seçin."
            ),
            MarketTrend(
                id = "tr_prem4_${Random.nextInt(100, 999)}",
                title = "Havacılık Sınıfı Titanyum & Zırh+ Gövde",
                description = "Armor+ zırh kaplama ve hafif titanyum gövdeler tepe segmenti domine ediyor.",
                category = TrendCategory.PREMIUM_BUILD,
                bonusMultiplier = 1.15f,
                remainingMonths = 4,
                totalDurationMonths = 4,
                tip = "Titanyum kasa ve Gorilla Armor koruma seçin."
            )
        )
    }

    val candidates = pool.filter { it.category != currentCategory }
    return candidates.randomOrNull() ?: pool.random()
}

fun getCompetitorModelForYear(companyName: String, year: Int): Triple<String, Int, Int> {
    val cleanName = companyName.lowercase()
    return when {
        cleanName.contains("samsung") -> when {
            year <= 2010 -> Triple("Galaxy S", 599, 85)
            year == 2011 -> Triple("Galaxy S2 AMOLED", 649, 87)
            year in 2012..2013 -> Triple("Galaxy S4 Note", 699, 89)
            year in 2014..2015 -> Triple("Galaxy S6 Edge", 749, 91)
            year in 2016..2017 -> Triple("Galaxy S8 Infinity", 799, 93)
            year in 2018..2019 -> Triple("Galaxy S10 Plus", 899, 94)
            year in 2020..2021 -> Triple("Galaxy S21 Ultra 5G", 1199, 95)
            year in 2022..2023 -> Triple("Galaxy Z Fold 5", 1499, 96)
            else -> Triple("Galaxy S25 Ultra AI", 1299, 97)
        }
        cleanName.contains("apple") -> when {
            year <= 2010 -> Triple("iPhone 4 Retina", 699, 89)
            year == 2011 -> Triple("iPhone 4S Siri", 699, 90)
            year == 2012 -> Triple("iPhone 5 Lightning", 749, 91)
            year == 2013 -> Triple("iPhone 5S Touch ID", 799, 92)
            year in 2014..2015 -> Triple("iPhone 6 Plus", 849, 93)
            year in 2016..2017 -> Triple("iPhone X Bionic", 999, 95)
            year in 2018..2019 -> Triple("iPhone 11 Pro Max", 1099, 95)
            year in 2020..2021 -> Triple("iPhone 13 Pro Ceramic", 1199, 96)
            year in 2022..2023 -> Triple("iPhone 15 Pro Titanium", 1299, 97)
            else -> Triple("iPhone 16 Pro AI Max", 1399, 98)
        }
        cleanName.contains("xiaomi") -> when {
            year <= 2010 -> Triple("Mi 1 MIUI", 249, 79)
            year in 2011..2013 -> Triple("Mi 3 Speed", 279, 82)
            year in 2014..2016 -> Triple("Redmi Note 3 Metal", 219, 84)
            year in 2017..2018 -> Triple("Mi 8 Pro Şeffaf", 399, 86)
            year in 2019..2020 -> Triple("Redmi K20 Pro Pop-up", 349, 88)
            year in 2021..2022 -> Triple("Xiaomi 12 Pro 120W", 599, 90)
            year in 2023..2024 -> Triple("Redmi Turbo 3", 399, 92)
            else -> Triple("Xiaomi 15 Ultra Titanium", 799, 95)
        }
        cleanName.contains("oppo") -> when {
            year <= 2012 -> Triple("Finder Ultra Slim", 399, 80)
            year in 2013..2015 -> Triple("N3 Dönen Kamera", 499, 83)
            year in 2016..2018 -> Triple("Find X Stealth 3D", 699, 87)
            year in 2019..2021 -> Triple("Find X3 Pro MicroLens", 799, 90)
            year in 2022..2023 -> Triple("Find N2 Flip SuperVOOC", 999, 92)
            else -> Triple("Find X8 Pro Hasselblad AI", 1099, 95)
        }
        cleanName.contains("vivo") -> when {
            year <= 2013 -> Triple("Xplay Hi-Fi", 449, 80)
            year in 2014..2016 -> Triple("X5Max 4.75mm Slim", 499, 82)
            year in 2017..2019 -> Triple("NEX Ekran İçi Parmak İzi", 649, 86)
            year in 2020..2022 -> Triple("X70 Pro+ Zeiss Gimbal", 799, 91)
            else -> Triple("X200 Pro Zeiss Telephoto AI", 999, 95)
        }
        cleanName.contains("huawei") -> when {
            year <= 2013 -> Triple("Ascend P6 Metal", 399, 81)
            year in 2014..2016 -> Triple("P9 Leica Çift Kamera", 549, 86)
            year in 2017..2019 -> Triple("Mate 20 Pro Matrix", 799, 92)
            year in 2020..2022 -> Triple("Mate 40 Pro 5nm Kirin", 899, 94)
            else -> Triple("Pura 70 Ultra XMAGE Geri Çekilebilir", 1199, 96)
        }
        cleanName.contains("google") -> when {
            year <= 2012 -> Triple("Nexus 4 Pure", 399, 83)
            year in 2013..2015 -> Triple("Nexus 6P Metal", 549, 86)
            year in 2016..2018 -> Triple("Pixel 3 XL HDR+", 799, 90)
            year in 2019..2021 -> Triple("Pixel 6 Pro Tensor", 899, 92)
            year in 2022..2024 -> Triple("Pixel 8 Pro Gemini AI", 999, 94)
            else -> Triple("Pixel 9 Pro Fold AI", 1399, 96)
        }
        cleanName.contains("motorola") -> when {
            year <= 2013 -> Triple("Droid Razr Kevlar", 399, 80)
            year in 2014..2016 -> Triple("Moto X Ahşap Kapak", 449, 83)
            year in 2017..2019 -> Triple("Moto Z Modüler Mod", 499, 85)
            year in 2020..2022 -> Triple("Edge 30 Ultra 200MP", 699, 89)
            else -> Triple("Razr 50 Ultra Katlanabilir Dış Ekran", 899, 93)
        }
        cleanName.contains("oneplus") -> when {
            year <= 2014 -> Triple("OnePlus One Flagship Killer", 299, 86)
            year in 2015..2017 -> Triple("OnePlus 3 Dash Charge", 399, 87)
            year in 2018..2020 -> Triple("OnePlus 7 Pro 90Hz Çerçevesiz", 669, 91)
            year in 2021..2023 -> Triple("OnePlus 11 Hasselblad 100W", 699, 93)
            else -> Triple("OnePlus 13 Snapdragon 8 Gen Elite", 799, 95)
        }
        cleanName.contains("realme") -> when {
            year <= 2018 -> Triple("Realme 1 Diamond Design", 199, 78)
            year in 2019..2021 -> Triple("Realme X Master Edition", 269, 82)
            year in 2022..2023 -> Triple("GT Neo 5 240W Rekor Şarj", 399, 88)
            else -> Triple("GT 6 AI Master Pro", 499, 92)
        }
        cleanName.contains("honor") -> when {
            year <= 2015 -> Triple("Honor 6 Kirin", 299, 79)
            year in 2016..2018 -> Triple("Honor 8 Aurora Cam", 399, 84)
            year in 2019..2021 -> Triple("Honor View 20 Delikli Ekran", 499, 87)
            year in 2022..2023 -> Triple("Magic V2 Dünyanın En İnce Katlanabilir", 1299, 94)
            else -> Triple("Magic 7 Pro AI Göz Dostu PWM", 899, 95)
        }
        cleanName.contains("sony") -> when {
            year <= 2012 -> Triple("Xperia Arc İnce Kavis", 499, 82)
            year in 2013..2015 -> Triple("Xperia Z Suya Dayanıklı Cam", 599, 86)
            year in 2016..2018 -> Triple("Xperia XZ Premium 4K HDR", 799, 89)
            year in 2019..2022 -> Triple("Xperia 1 III 120Hz 4K CinemaWide", 1199, 92)
            else -> Triple("Xperia 1 VI Exmor-T Pro Sensör", 1299, 94)
        }
        cleanName.contains("asus") -> when {
            year <= 2014 -> Triple("PadFone Hibrit", 499, 79)
            year in 2015..2017 -> Triple("ZenFone 2 4GB RAM Öncüsü", 299, 82)
            year in 2018..2020 -> Triple("ROG Phone 2 120Hz AMOLED 6000mAh", 799, 90)
            year in 2021..2023 -> Triple("ROG Phone 7 Ultimate Aktif Fan", 1199, 94)
            else -> Triple("ROG Phone 8 Pro 165Hz Matrix LED", 1099, 96)
        }
        cleanName.contains("nokia") -> when {
            year <= 2011 -> Triple("N8 Symbian 12MP Carl Zeiss", 429, 80)
            year in 2012..2014 -> Triple("Lumia 1020 41MP PureView", 599, 87)
            year in 2015..2018 -> Triple("Nokia 8 Sirocco Zırhlı", 649, 85)
            year in 2019..2022 -> Triple("Nokia XR20 Askeri Standart", 499, 83)
            else -> Triple("HMD Skyline QuickFix Kolay Tamir", 399, 86)
        }
        cleanName.contains("tecno") -> when {
            year <= 2015 -> Triple("Tecno Boom J7 Müzik", 129, 72)
            year in 2016..2019 -> Triple("Phantom 8 Çift Kamera", 189, 76)
            year in 2020..2022 -> Triple("Camon 19 Pro RGBW Sensör", 249, 81)
            else -> Triple("Phantom V Fold 2 Katlanabilir", 799, 89)
        }
        cleanName.contains("infinix") -> when {
            year <= 2016 -> Triple("Infinix Hot 3 Bütçe", 119, 71)
            year in 2017..2020 -> Triple("Note 7 Büyük Ekran", 169, 75)
            year in 2021..2023 -> Triple("Zero Ultra 180W Thunder Charge", 399, 84)
            else -> Triple("GT 20 Pro Mecha RGB Gaming", 299, 88)
        }
        cleanName.contains("nothing") -> when {
            year <= 2021 -> Triple("Nothing Concept Glyph 1", 399, 83)
            year == 2022 -> Triple("Nothing Phone (1) Glyph Şeffaf", 449, 87)
            year in 2023..2024 -> Triple("Nothing Phone (2) Glyph Matrix", 599, 90)
            else -> Triple("Nothing Phone (3) Transparent AI", 699, 93)
        }
        cleanName.contains("zte") -> when {
            year <= 2014 -> Triple("Blade V Akıllı Tasarım", 199, 75)
            year in 2015..2018 -> Triple("Axon 7 Hi-Fi Çift Hoparlör", 399, 82)
            year in 2019..2022 -> Triple("Axon 20 Dünyanın İlk Ekran Altı Kamerası", 449, 86)
            else -> Triple("RedMagic 9 Pro Çentiksiz RGB Fanlı Canavar", 749, 93)
        }
        cleanName.contains("tcl") -> when {
            year <= 2015 -> Triple("Idol 3 Çift Yönlü Kullanım", 199, 74)
            year in 2016..2019 -> Triple("TCL Plex NXTVISION", 249, 78)
            year in 2020..2023 -> Triple("TCL 40 NXTPAPER Göz Koruma Mat Ekran", 299, 83)
            else -> Triple("TCL 50 Pro NXTPAPER 3.0 Kağıt Hissi", 349, 87)
        }
        else -> when { // Fairphone & Diğerleri
            year <= 2014 -> Triple("Fairphone 1 Modüler Başlangıç", 399, 76)
            year in 2015..2019 -> Triple("Fairphone 2 Kendin Tamir Et", 499, 79)
            year in 2020..2022 -> Triple("Fairphone 4 5G Adil Ticaret", 579, 83)
            else -> Triple("Fairphone 5 %100 Modüler 8 Yıl Destek", 699, 87)
        }
    }
}

fun getCompetitorHardwareSpecs(companyName: String, year: Int): CompetitorModelHardware {
    val cleanName = companyName.lowercase()
    return when {
        cleanName.contains("apple") -> when {
            year <= 2011 -> CompetitorModelHardware("Apple A4/A5 Bionic", "512 MB", "5 MP HDR Kamera", "1420 mAh", "3.5\" Retina IPS")
            year in 2012..2015 -> CompetitorModelHardware("Apple A6/A8 Çip", "1-2 GB", "8 MP iSight Kamera", "1810 mAh", "4.0\"-5.5\" Retina HD")
            year in 2016..2019 -> CompetitorModelHardware("Apple A11/A13 Bionic", "3-4 GB", "12 MP Çift OIS Kamera", "3110 mAh", "5.8\" Super Retina OLED")
            year in 2020..2022 -> CompetitorModelHardware("Apple A15 Bionic 5G", "6 GB", "12 MP Üçlü Pro Kamera", "4352 mAh", "6.7\" ProMotion 120Hz")
            else -> CompetitorModelHardware("Apple A18 Pro 3nm AI", "8 GB LPDDR5X", "48 MP Fusion 5x Periskop", "4685 mAh", "6.9\" Dynamic Island 120Hz")
        }
        cleanName.contains("samsung") -> when {
            year <= 2011 -> CompetitorModelHardware("Exynos 4210 Dual", "1 GB", "8 MP 1080p Kamera", "1650 mAh", "4.3\" Super AMOLED Plus")
            year in 2012..2015 -> CompetitorModelHardware("Exynos 7420 Octa", "3 GB", "16 MP OIS Kamera", "2550 mAh", "5.1\" Quad HD Kavisli AMOLED")
            year in 2016..2019 -> CompetitorModelHardware("Exynos 9820 / Snapdragon 855", "8 GB", "12 MP Değişken Diyafram", "4100 mAh", "6.4\" Dinamik AMOLED Infinity-O")
            year in 2020..2022 -> CompetitorModelHardware("Snapdragon 8 Gen 1", "12 GB", "108 MP 100x Uzay Zoom", "5000 mAh", "6.8\" Edge QHD+ 120Hz")
            else -> CompetitorModelHardware("Snapdragon 8 Elite Galaxy", "16 GB LPDDR5X", "200 MP ISOCELL 100x Zoom", "5500 mAh", "6.8\" Düz Titanyum 120Hz AMOLED")
        }
        cleanName.contains("oneplus") -> when {
            year <= 2013 -> CompetitorModelHardware("Snapdragon 600 Quad", "2 GB", "13 MP Kamera", "2600 mAh", "5.0\" Full HD IPS")
            year in 2014..2015 -> CompetitorModelHardware("Snapdragon 801 Flagship", "3 GB", "13 MP Sony IMX214", "3100 mAh", "5.5\" FHD IPS")
            year in 2016..2017 -> CompetitorModelHardware("Snapdragon 820/835", "6 GB", "16 MP Dash Charge", "3400 mAh", "5.5\" Optic AMOLED")
            year in 2018..2019 -> CompetitorModelHardware("Snapdragon 845/855", "8 GB", "48 MP OIS Warp Charge", "3700 mAh", "6.41\" Fluid AMOLED 90Hz")
            year in 2020..2022 -> CompetitorModelHardware("Snapdragon 8 Gen 1 Pro", "12 GB", "50 MP Hasselblad Kamera", "5000 mAh (80W)", "6.7\" Fluid AMOLED 120Hz")
            else -> CompetitorModelHardware("Snapdragon 8 Gen 3 / 8 Elite", "16 GB LPDDR5X", "50 MP Hasselblad Periskop", "5400 mAh (100W)", "6.82\" 2K 120Hz LTPO")
        }
        cleanName.contains("xiaomi") -> when {
            year <= 2013 -> CompetitorModelHardware("Snapdragon S4 / 800", "2 GB", "13 MP Sony Sensör", "3050 mAh", "5.0\" IPS 1080p")
            year in 2014..2018 -> CompetitorModelHardware("Snapdragon 845 Flagship", "6 GB", "12 MP Çift AI Kamera", "3400 mAh", "6.21\" AMOLED Çentikli")
            year in 2019..2022 -> CompetitorModelHardware("Snapdragon 8 Gen 1 Pro", "12 GB", "50 MP 1/1.28\" Sensör", "4600 mAh (120W)", "6.73\" 2K 120Hz AMOLED")
            else -> CompetitorModelHardware("Snapdragon 8 Elite Ultra", "16 GB", "50 MP 1-İnç Leica Sensör", "5500 mAh (90W)", "6.73\" 120Hz 3000nit OLED")
        }
        cleanName.contains("google") -> when {
            year <= 2015 -> CompetitorModelHardware("Snapdragon 810 Octa", "3 GB", "12.3 MP 1.55µm Piksel", "3450 mAh", "5.7\" WQHD AMOLED")
            year in 2016..2020 -> CompetitorModelHardware("Snapdragon 845/765G", "6 GB", "12.2 MP Dual Pixel HDR+", "4080 mAh", "6.0\" OLED 90Hz")
            year in 2021..2023 -> CompetitorModelHardware("Google Tensor G2 AI", "12 GB", "50 MP GN1 + 48 MP 5x Zoom", "5000 mAh", "6.7\" LTPO 120Hz")
            else -> CompetitorModelHardware("Google Tensor G4 Gemini AI", "16 GB", "50 MP AI Pro + Gemini Studio", "5060 mAh", "6.8\" Super Actua LTPO")
        }
        cleanName.contains("huawei") -> when {
            year <= 2015 -> CompetitorModelHardware("Kirin 950 Octa-core", "3-4 GB", "12 MP Leica Çift Kamera", "3000 mAh", "5.2\" Full HD IPS")
            year in 2016..2019 -> CompetitorModelHardware("Kirin 980 7nm Dual NPU", "8 GB", "40 MP RYYB Sensör + 5x Zoom", "4200 mAh (40W)", "6.39\" Kavisli OLED")
            year in 2020..2022 -> CompetitorModelHardware("Kirin 9000 5nm 5G", "8-12 GB", "50 MP Ultra Vision XMAGE", "4400 mAh (66W)", "6.76\" 90Hz Horizon OLED")
            else -> CompetitorModelHardware("Kirin 9010 XMAGE Engine", "16 GB", "50 MP Geri Çekilebilir 1-İnç", "5200 mAh (100W)", "6.8\" 120Hz Dört Kavisli LTPO")
        }
        cleanName.contains("sony") -> when {
            year <= 2013 -> CompetitorModelHardware("Snapdragon S4 Pro", "2 GB", "13 MP Exmor RS", "2330 mAh", "5.0\" Full HD")
            year in 2014..2017 -> CompetitorModelHardware("Snapdragon 801/820", "3-4 GB", "20.7 MP 4K Video", "3200 mAh", "5.2\" Triluminos FHD")
            year in 2018..2021 -> CompetitorModelHardware("Snapdragon 855/888", "8-12 GB", "12 MP Üçlü ZEISS T*", "4000 mAh", "6.5\" 4K HDR OLED 120Hz")
            else -> CompetitorModelHardware("Snapdragon 8 Gen 3 ZEISS", "16 GB", "48 MP Exmor-T Pro Sensör", "5000 mAh", "6.5\" 120Hz LTPO OLED")
        }
        cleanName.contains("asus") -> when {
            year <= 2015 -> CompetitorModelHardware("Intel Atom Z3580 Quad", "4 GB", "13 MP PixelMaster", "3000 mAh", "5.5\" IPS FHD")
            year in 2016..2019 -> CompetitorModelHardware("Snapdragon 845/855+ ROG", "8-12 GB", "48 MP Sony Sensör", "6000 mAh", "6.59\" 120Hz AMOLED")
            year in 2020..2022 -> CompetitorModelHardware("Snapdragon 8 Gen 1 ROG", "16 GB", "50 MP Sony IMX766", "6000 mAh (65W)", "6.78\" 165Hz AMOLED")
            else -> CompetitorModelHardware("Snapdragon 8 Gen 3 Ultimate", "24 GB", "50 MP Gimbal OIS", "5500 mAh (65W)", "6.78\" 165Hz LTPO OLED")
        }
        cleanName.contains("oppo") || cleanName.contains("realme") || cleanName.contains("vivo") -> when {
            year <= 2013 -> CompetitorModelHardware("Snapdragon 600 / MT6589", "2 GB", "13 MP Dönen Kamera", "2500 mAh", "5.0\" IPS 1080p")
            year in 2014..2017 -> CompetitorModelHardware("Snapdragon 801 / VOOC", "3-4 GB", "16 MP Schneider-Kreuznach", "3000 mAh (VOOC)", "5.5\" AMOLED")
            year in 2018..2021 -> CompetitorModelHardware("Snapdragon 855 / 865 5G", "8-12 GB", "48-64 MP Çift/Üçlü OIS", "4200 mAh (65W)", "6.5\" 90Hz-120Hz OLED")
            else -> CompetitorModelHardware("MediaTek D9300 / 8 Gen 3", "16 GB", "50 MP 1-İnç Hasselblad/ZEISS", "5400 mAh (100W)", "6.78\" 120Hz 1.5K LTPO")
        }
        cleanName.contains("nothing") -> when {
            year <= 2022 -> CompetitorModelHardware("Snapdragon 778G+ 5G", "8 GB", "50 MP Çift Sony IMX766", "4500 mAh (33W)", "6.55\" 120Hz OLED")
            year in 2023..2024 -> CompetitorModelHardware("Snapdragon 8+ Gen 1", "12 GB", "50 MP Sony IMX890 OIS", "4700 mAh (45W)", "6.7\" 120Hz LTPO OLED")
            else -> CompetitorModelHardware("Snapdragon 8 Gen 3 AI", "16 GB", "50 MP Üçlü Pro Glyph", "5000 mAh (65W)", "6.7\" 144Hz LTPO OLED")
        }
        cleanName.contains("nokia") -> when {
            year <= 2012 -> CompetitorModelHardware("Snapdragon S4 Dual", "1 GB", "41 MP PureView Carl Zeiss", "2000 mAh", "4.5\" PureMotion HD+")
            year in 2013..2017 -> CompetitorModelHardware("Snapdragon 800 / 835", "3-4 GB", "20 MP PureView OIS", "3000 mAh", "5.0\"-5.3\" QHD IPS")
            else -> CompetitorModelHardware("Snapdragon 778G / 865", "6-8 GB", "64 MP ZEISS Dörtlü", "4500 mAh", "6.67\" FHD+ 120Hz")
        }
        else -> when {
            year <= 2013 -> CompetitorModelHardware("Snapdragon S4 / MT6589", "2 GB", "8-13 MP Kamera", "2500 mAh", "4.7\"-5.0\" HD Ekran")
            year in 2014..2018 -> CompetitorModelHardware("Snapdragon 801 / Helio X10", "3-4 GB", "16 MP Çift Kamera", "3200 mAh", "5.5\" Full HD")
            year in 2019..2022 -> CompetitorModelHardware("Snapdragon 865 / D800 5G", "8-12 GB", "50-64 MP OIS Kamera", "4500 mAh (65W)", "6.5\" 120Hz AMOLED")
            else -> CompetitorModelHardware("Snapdragon 8 Gen 3 / D9300", "12-16 GB", "50-200 MP Gelişmiş Lens", "5000+ mAh Hızlı Şarj", "6.7\" 120Hz OLED")
        }
    }
}

data class CompetitorModelHardware(
    val processor: String,
    val ram: String,
    val camera: String,
    val battery: String,
    val display: String
)

