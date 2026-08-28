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
            specs.batteryCapacity.contains("4500") || specs.batteryCapacity.contains("5000") || specs.batteryCapacity.contains("5500") || specs.batteryCapacity.contains("7000") || specs.batteryCapacity.contains("3600") || specs.batteryCapacity.contains("4000") || specs.batteryType.contains("Katı Hal")
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
            MarketTrend("tr_cam_${Random.nextInt(100, 999)}", "Özçekim & HD Kamera Çılgınlığı", "Sosyal medya patlamasıyla tüketiciler yüksek çözünürlüklü ön/arka kamera istiyor.", TrendCategory.CAMERA_PRO, 1.20f, 4, 4),
            MarketTrend("tr_prem_${Random.nextInt(100, 999)}", "İnce & Alüminyum Gövde Modası", "Plastikten sıkılan kullanıcılar metal ve cam şıklığını arıyor.", TrendCategory.PREMIUM_BUILD, 1.15f, 4, 4),
            MarketTrend("tr_budg_${Random.nextInt(100, 999)}", "Ekonomik Akıllı Telefon Akını", "Gelişmekte olan pazarlarda $400 altı bütçe dostu cihazlar kapışılıyor.", TrendCategory.BUDGET_VALUE, 1.10f, 4, 4),
            MarketTrend("tr_bat_${Random.nextInt(100, 999)}", "Tüm Gün Yeten Batarya Arayışı", "Büyük ekranlarla artan enerji ihtiyacı için yüksek mAh piller aranıyor.", TrendCategory.LONG_BATTERY, 1.15f, 4, 4),
            MarketTrend("tr_conn_${Random.nextInt(100, 999)}", "4G LTE Hızlı Bağlantı Talebi", "Hızlı internet isteyen mobil kullanıcılar 4G destekli telefonları tercih ediyor.", TrendCategory.FAST_CONNECTIVITY, 1.20f, 4, 4)
        )
        year in 2014..2019 -> listOf(
            MarketTrend("tr_disp_${Random.nextInt(100, 999)}", "Yüksek Yenileme Hızı & Oyuncu Ekranları", "Mobil oyunlar popülerleştikçe akıcı ekranlar ve oyuncu tasarımı revaçta.", TrendCategory.HIGH_REFRESH_DISPLAY, 1.15f, 4, 4),
            MarketTrend("tr_cam2_${Random.nextInt(100, 999)}", "Çoklu Kamera & Portre Modu Modası", "Arka planı bulanıklaştıran çift ve üçlü kameralar tüketicilerin 1 numaralı tercihi.", TrendCategory.CAMERA_PRO, 1.20f, 4, 4),
            MarketTrend("tr_prem2_${Random.nextInt(100, 999)}", "Cam & Çerçevesiz Tasarım Yarışı", "Cam arka kapaklar ve lüks metal çerçeveler vitrinleri süslüyor.", TrendCategory.PREMIUM_BUILD, 1.15f, 4, 4),
            MarketTrend("tr_bat2_${Random.nextInt(100, 999)}", "Mega Kapasiteli Batarya Çılgınlığı", "Kullanıcılar 2 gün şarj istemeyen 4500mAh+ devasa pillere yöneliyor.", TrendCategory.LONG_BATTERY, 1.15f, 4, 4),
            MarketTrend("tr_budg2_${Random.nextInt(100, 999)}", "Fiyat / Performans Patlaması", "Orta segmentte amiral gemisi hissi veren ucuz modeller kapışılıyor.", TrendCategory.BUDGET_VALUE, 1.10f, 4, 4)
        )
        else -> listOf(
            MarketTrend("tr_ai_${Random.nextInt(100, 999)}", "Cihaz Üstü Yapay Zeka (AI) Çipleri", "NPU birimli işlemciler ve yüksek RAM kapasitesi satın alma tercihlerini belirliyor.", TrendCategory.AI_PROCESSOR, 1.25f, 4, 4),
            MarketTrend("tr_disp2_${Random.nextInt(100, 999)}", "120Hz-240Hz Ultra Akıcı OLED Ekranlar", "Takılmasız LTPO ekranlar ve oyuncu dizaynları tüm segmentlerde talep ediliyor.", TrendCategory.HIGH_REFRESH_DISPLAY, 1.20f, 4, 4),
            MarketTrend("tr_cam3_${Random.nextInt(100, 999)}", "1-İnç Sensörler & Periskop Zoom", "Profesyonel seviye fotoğrafçılık ve 8K kayıt yeteneği aranıyor.", TrendCategory.CAMERA_PRO, 1.20f, 4, 4),
            MarketTrend("tr_prem3_${Random.nextInt(100, 999)}", "Titanyum Alaşım & Zırhlı Gövde Trendi", "Uzay endüstrisi sınıfı titanyum gövde modelleri prestij sembolü oldu.", TrendCategory.PREMIUM_BUILD, 1.15f, 4, 4),
            MarketTrend("tr_conn2_${Random.nextInt(100, 999)}", "5G & Uydu İletişimi Çılgınlığı", "Kesintisiz küresel bağlantı ve acil durum uydu iletişimi gözde.", TrendCategory.FAST_CONNECTIVITY, 1.20f, 4, 4),
            MarketTrend("tr_bat3_${Random.nextInt(100, 999)}", "Katı Hal Batarya & 5000mAh+ Güç", "Isınmayan ve günlerce dayanan yeni nesil bataryalar pazarı sallıyor.", TrendCategory.LONG_BATTERY, 1.20f, 4, 4)
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
        else -> when {
            year <= 2013 -> CompetitorModelHardware("Dört Çekirdekli Mobil İşlemci", "2 GB", "8-13 MP Kamera", "2500 mAh", "4.7\"-5.0\" HD Ekran")
            year in 2014..2018 -> CompetitorModelHardware("Sekiz Çekirdekli Performans Çipi", "4-6 GB", "16 MP Çift Kamera", "3500 mAh", "5.5\"-6.0\" Full HD+")
            year in 2019..2022 -> CompetitorModelHardware("Amiral Gemisi 5G Çipset", "8-12 GB", "50-64 MP OIS Kamera", "4500 mAh (65W)", "6.5\" 120Hz AMOLED")
            else -> CompetitorModelHardware("Yeni Nesil 3nm/4nm AI Çip", "12-16 GB", "50-200 MP Gelişmiş Lens", "5000+ mAh Hızlı Şarj", "6.7\" 120Hz-165Hz OLED")
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

