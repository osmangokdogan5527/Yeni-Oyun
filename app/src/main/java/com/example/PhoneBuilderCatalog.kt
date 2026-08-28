package com.example

import androidx.compose.ui.graphics.Color

data class ColorOption(
    val name: String,
    val hexValue: Long,
    val color: Color
)

data class ComponentOption(
    val name: String,
    val cost: Int,
    val desc: String? = null,
    val availableFrom: Int = 2010,
    val requiredTech: String? = null
)

val ALL_COLORS = listOf(
    ColorOption("Gece Siyahı", 0xFF0F172A, Color(0xFF0F172A)),
    ColorOption("Titanyum Grisi", 0xFF475569, Color(0xFF475569)),
    ColorOption("Gece Mavisi", 0xFF1E3A8A, Color(0xFF1E3A8A)),
    ColorOption("Zümrüt Yeşili", 0xFF065F46, Color(0xFF065F46)),
    ColorOption("Lavanta Moru", 0xFF6D28D9, Color(0xFF6D28D9)),
    ColorOption("Roze Altın", 0xFFBE185D, Color(0xFFBE185D)),
    ColorOption("Buzul Beyazı", 0xFFF1F5F9, Color(0xFFF1F5F9)),
    ColorOption("Alev Turuncusu", 0xFFEA580C, Color(0xFFEA580C)),
    ColorOption("Siber Zümrüt", 0xFF0D9488, Color(0xFF0D9488)),
    ColorOption("Şampanya Sarısı", 0xFFD97706, Color(0xFFD97706))
)

val ALL_STYLES = listOf(
    ComponentOption("Modern", 10, "İnce, şık ve minimalist hatlar"),
    ComponentOption("Klasik", 5, "Geleneksel dengeli tasarım"),
    ComponentOption("Oyuncu", 15, "Agresif çizgiler ve RGB hissi", requiredTech = "Oyuncu Tasarım Çizgileri"),
    ComponentOption("Dayanıklı", 20, "Zırhlı köşeler ve sağlam gövde", requiredTech = "Zırhlı Dayanıklı Gövde")
)

val ALL_BACK_FINISHES = listOf(
    ComponentOption("Buzlu Mat Cam", 0, "Parmak izi tutmayan kadife doku"),
    ComponentOption("Parlak Ayna Cam", 5, "Işıltılı derin yansımalı ayna yüzey"),
    ComponentOption("Vegan Deri", 10, "Lüks dikişli yumuşak deri kaplama"),
    ComponentOption("Fırçalanmış Metal", 8, "Endüstriyel metalik çizgiler"),
    ComponentOption("Karbon Fiber", 15, "Yüksek dayanımlı sportif örgü doku")
)

val ALL_CAMERA_BUMPS = listOf(
    ComponentOption("Dikey Ada", 0, "Zarif ve kompakt dikey hap modülü"),
    ComponentOption("Dairesel Halo", 5, "Merkezi büyük daire halo adası"),
    ComponentOption("Yatay Vizör", 8, "Uçtan uca uzanan fütüristik vizör barı"),
    ComponentOption("Kare Ada", 5, "Köşeli modern amiral gemisi matrisi"),
    ComponentOption("Yüzen Çift Halka", 0, "Adasız bağımsız lens tasarımı")
)

val ALL_FRAME_STYLES = listOf(
    ComponentOption("Düz Metal Kenar", 0, "Modern köşeli düz kenarlar"),
    ComponentOption("Kavisli 2.5D", 5, "Ergonomik avuç içi kavisli kenar"),
    ComponentOption("Zırhlı Kesim", 10, "Darbe emici köşeli kesim"),
    ComponentOption("Ultra İnce Çerçeve", 12, "Hafif ve ultra ince çerçeve yapısı")
)

val ALL_NOTCH_STYLES = listOf(
    ComponentOption("Nokta Delik", 0, "Minimalist tekli ön kamera deliği"),
    ComponentOption("Dinamik Ada / Hap", 8, "İnteraktif hap şeklinde dinamik ada"),
    ComponentOption("Klasik Çentik", 0, "Geniş hoparlörlü klasik çentik"),
    ComponentOption("Görünmez Ekran Altı", 25, "Kusursuz tam ekran UDC teknolojisi", availableFrom = 2021, requiredTech = "Görünmez Ekran Altı Kamera Entegrasyonu")
)

val ALL_BRAND_LOGOS = listOf(
    ComponentOption("Minimal Elmas", 0, "Zarif geometrik elmas mühür"),
    ComponentOption("Nexus Yıldızı", 0, "Fütüristik 4 uçlu yıldız amblemi"),
    ComponentOption("Apex Üçgen", 0, "Modern prizmatik tepe üçgeni"),
    ComponentOption("Sonsuzluk Loop", 0, "Zarif sonsuzluk halkası"),
    ComponentOption("Neon Nova", 0, "Yüksek teknoloji orbital yörünge"),
    ComponentOption("Siber Kalkan", 0, "Sağlam siber zırh kalkanı"),
    ComponentOption("Stüdyo Monogram", 0, "Çift halkalı şirket monogramı"),
    ComponentOption("Logo Yok (Sade)", 0, "Gizli ve minimalist amblemsiz kapak")
)

val ALL_MATERIALS = listOf(
    ComponentOption("Plastik", 5),
    ComponentOption("Alüminyum", 20, requiredTech = "Alüminyum Kasa Entegrasyonu"),
    ComponentOption("Cam", 30, requiredTech = "Cam Arka Kapak Entegrasyonu"),
    ComponentOption("Titanyum", 60, requiredTech = "Titanyum Alaşım Kasa Entegrasyonu")
)

val BASE_PROCESSORS = listOf(
    ComponentOption("Qualcomm S2", 25, "Performans: 150 Puan • Tier E (Giriş)", 2010),
    ComponentOption("MediaTek MT65", 15, "Performans: 100 Puan • Tier E (Giriş)", 2010),
    ComponentOption("Qualcomm S4", 40, "Performans: 250 Puan • Tier D (Temel Hız)", 2012, requiredTech = "Qualcomm S4 Çip Entegrasyonu"),
    ComponentOption("MediaTek MT67", 25, "Performans: 180 Puan • Tier D (Temel Hız)", 2012, requiredTech = "MediaTek MT67 Çip Entegrasyonu"),
    ComponentOption("Qualcomm 801", 60, "Performans: 400 Puan • Tier C (Orta Segment)", 2014, requiredTech = "Qualcomm 801 Çip Entegrasyonu"),
    ComponentOption("Intel Atom X5", 45, "Performans: 350 Puan • Tier C (Orta Segment)", 2014, requiredTech = "Intel Atom X5 Çip Entegrasyonu"),
    ComponentOption("Qualcomm 820", 85, "Performans: 600 Puan • Tier B (Yüksek Hız)", 2016, requiredTech = "Qualcomm 820 Çip Entegrasyonu"),
    ComponentOption("MediaTek Helio", 60, "Performans: 500 Puan • Tier B (Yüksek Hız)", 2016, requiredTech = "MediaTek Helio Çip Entegrasyonu"),
    ComponentOption("Qualcomm 845", 110, "Performans: 900 Puan • Tier B+ (Amiral Gemisi)", 2018, requiredTech = "Qualcomm 845 Çip Entegrasyonu"),
    ComponentOption("MediaTek G90", 80, "Performans: 750 Puan • Tier B (Oyun Odaklı)", 2018, requiredTech = "MediaTek G90 Çip Entegrasyonu"),
    ComponentOption("Qualcomm 865", 140, "Performans: 1300 Puan • Tier A (Üst Düzey)", 2020, requiredTech = "Qualcomm 865 Çip Entegrasyonu"),
    ComponentOption("MediaTek D800", 100, "Performans: 1100 Puan • Tier A (Üst Düzey)", 2020, requiredTech = "MediaTek D800 Çip Entegrasyonu"),
    ComponentOption("Qualcomm 8 Gen 1", 180, "Performans: 1800 Puan • Tier S (Zirve Amiral)", 2022, requiredTech = "Qualcomm 8 Gen 1 Çip Entegrasyonu"),
    ComponentOption("MediaTek D9000", 140, "Performans: 1600 Puan • Tier S (Zirve Amiral)", 2022, requiredTech = "MediaTek D9000 Çip Entegrasyonu"),
    ComponentOption("Qualcomm 8 Gen 3", 240, "Performans: 2500 Puan • Tier S+ (Ultra Güç)", 2024, requiredTech = "Qualcomm 8 Gen 3 Çip Entegrasyonu"),
    ComponentOption("MediaTek D9300", 190, "Performans: 2300 Puan • Tier S+ (Ultra Güç)", 2024, requiredTech = "MediaTek D9300 Çip Entegrasyonu"),
    ComponentOption("In-House Ar-Ge", 150, "Özel Tasarım Çip • 1600 Puan (Tier S)", 2015, requiredTech = "Özel Yonga Seti Entegrasyonu"),
    ComponentOption("Kuantum İşlemci", 300, "Maksimum Güç • 3500 Puan • Tier S++", 2026, requiredTech = "Kuantum İşlemci Entegrasyonu")
)

val ALL_RAM_CAPACITIES = listOf(
    ComponentOption("512 MB", 3, availableFrom = 2010),
    ComponentOption("1 GB", 6, availableFrom = 2010),
    ComponentOption("2 GB", 10, availableFrom = 2011, requiredTech = "2GB RAM Kapasitesi"),
    ComponentOption("3 GB", 15, availableFrom = 2012, requiredTech = "3GB RAM Kapasitesi"),
    ComponentOption("4 GB", 20, availableFrom = 2014, requiredTech = "4GB RAM Kapasitesi"),
    ComponentOption("6 GB", 30, availableFrom = 2016, requiredTech = "6GB RAM Kapasitesi"),
    ComponentOption("8 GB", 42, availableFrom = 2017, requiredTech = "8GB RAM Kapasitesi"),
    ComponentOption("12 GB", 60, availableFrom = 2019, requiredTech = "12GB RAM Kapasitesi"),
    ComponentOption("16 GB", 80, availableFrom = 2021, requiredTech = "16GB RAM Kapasitesi"),
    ComponentOption("24 GB", 110, availableFrom = 2023, requiredTech = "24GB RAM Kapasitesi"),
    ComponentOption("32 GB", 140, availableFrom = 2025, requiredTech = "32GB RAM Kapasitesi")
)

val ALL_RAM_TYPES = listOf(
    ComponentOption("LPDDR1", 2, "200 MHz Temel Hız", availableFrom = 2010),
    ComponentOption("LPDDR2", 5, "800 MHz Düşük Güç", availableFrom = 2011, requiredTech = "LPDDR2 Bellek Teknolojisi"),
    ComponentOption("LPDDR3", 10, "1866 MHz Yüksek Bant", availableFrom = 2013, requiredTech = "LPDDR3 Bellek Teknolojisi"),
    ComponentOption("LPDDR4", 18, "3200 MHz Çift Kanal", availableFrom = 2015, requiredTech = "LPDDR4 Bellek Teknolojisi"),
    ComponentOption("LPDDR4X", 28, "4266 MHz Ultra Verimli", availableFrom = 2017, requiredTech = "LPDDR4X Bellek Teknolojisi"),
    ComponentOption("LPDDR5", 40, "6400 Mbps Hızlı Yol", availableFrom = 2019, requiredTech = "LPDDR5 Bellek Teknolojisi"),
    ComponentOption("LPDDR5X", 55, "8533 Mbps Yapay Zeka", availableFrom = 2023, requiredTech = "LPDDR5X Bellek Teknolojisi"),
    ComponentOption("LPDDR6", 75, "12800 Mbps Yeni Nesil", availableFrom = 2025, requiredTech = "LPDDR6 Bellek Teknolojisi")
)

val ALL_STORAGES = listOf(
    ComponentOption("8 GB", 3, "eMMC Standart", availableFrom = 2010),
    ComponentOption("16 GB", 6, "eMMC Standart", availableFrom = 2010),
    ComponentOption("32 GB", 10, "eMMC Hızlı", availableFrom = 2011, requiredTech = "32GB Depolama Entegrasyonu"),
    ComponentOption("64 GB", 16, "eMMC 5.0", availableFrom = 2012, requiredTech = "64GB Depolama Entegrasyonu"),
    ComponentOption("128 GB", 25, "UFS 2.0 Hızlı", availableFrom = 2015, requiredTech = "128GB UFS Depolama Entegrasyonu"),
    ComponentOption("256 GB", 38, "UFS 2.1 Geniş", availableFrom = 2017, requiredTech = "256GB UFS Depolama Entegrasyonu"),
    ComponentOption("512 GB", 55, "UFS 3.0 Ultra", availableFrom = 2019, requiredTech = "512GB UFS Depolama Entegrasyonu"),
    ComponentOption("1 TB", 80, "UFS 4.0 Pro", availableFrom = 2022, requiredTech = "1TB UFS 4.0 Depolama Entegrasyonu"),
    ComponentOption("2 TB", 115, "UFS 4.1 GenAI Dev", availableFrom = 2024, requiredTech = "2TB Ultra Depolama Entegrasyonu")
)

val ALL_SD_CARDS = listOf(
    ComponentOption("SD Kart Yuvası Yok", 0, "Kapalı Gövde", availableFrom = 2010),
    ComponentOption("MicroSD (32 GB)", 2, "Standart Yuva", availableFrom = 2010),
    ComponentOption("MicroSDHC (128 GB)", 4, "UHS-I Hızlı Yuva", availableFrom = 2012, requiredTech = "128GB MicroSDHC Desteği"),
    ComponentOption("MicroSDXC (512 GB)", 7, "UHS-I Class 10", availableFrom = 2015, requiredTech = "512GB MicroSDXC Desteği"),
    ComponentOption("Ultra MicroSD (2 TB)", 11, "UHS-II V90 Hız", availableFrom = 2019, requiredTech = "2TB Ultra MicroSD Desteği"),
    ComponentOption("NM & Express (2 TB)", 16, "PCIe 985 MB/s Ultra", availableFrom = 2022, requiredTech = "NM & MicroSD Express Desteği")
)

val ALL_DISPLAYS = listOf(
    ComponentOption("TFT LCD 60Hz", 10, availableFrom = 2010),
    ComponentOption("FHD IPS 60Hz", 20, availableFrom = 2011, requiredTech = "FHD IPS Panel Entegrasyonu"),
    ComponentOption("QHD IPS 60Hz", 30, availableFrom = 2013, requiredTech = "2K QHD Panel Entegrasyonu"),
    ComponentOption("Kavisli Edge AMOLED", 45, availableFrom = 2015, requiredTech = "Kavisli AMOLED Panel Entegrasyonu"),
    ComponentOption("Çerçevesiz 18:9 OLED", 55, availableFrom = 2017, requiredTech = "Çerçevesiz 18:9 OLED Panel Entegrasyonu"),
    ComponentOption("120Hz LTPO OLED", 80, availableFrom = 2021, requiredTech = "120Hz LTPO OLED Panel Entegrasyonu"),
    ComponentOption("120Hz Katlanabilir OLED", 120, availableFrom = 2019, requiredTech = "Katlanabilir OLED Panel Entegrasyonu"),
    ComponentOption("144Hz LTPO 3.0", 100, availableFrom = 2023, requiredTech = "144Hz LTPO 3.0 Panel Entegrasyonu"),
    ComponentOption("240Hz Tandem OLED", 140, availableFrom = 2025, requiredTech = "240Hz Tandem OLED Panel Entegrasyonu"),
    ComponentOption("Holografik Ekran", 200, availableFrom = 2024, requiredTech = "Holografik 3D Panel Entegrasyonu")
)

val ALL_GLASSES = listOf(
    ComponentOption("Gorilla Glass 1", 5, availableFrom = 2010),
    ComponentOption("Gorilla Glass 2", 10, availableFrom = 2011, requiredTech = "Gorilla Glass 2 Entegrasyonu"),
    ComponentOption("Gorilla Glass 3", 15, availableFrom = 2013, requiredTech = "Gorilla Glass 3 Entegrasyonu"),
    ComponentOption("Gorilla Glass 4", 20, availableFrom = 2015, requiredTech = "Gorilla Glass 4 Entegrasyonu"),
    ComponentOption("Gorilla Glass 5", 25, availableFrom = 2017, requiredTech = "Gorilla Glass 5 Entegrasyonu"),
    ComponentOption("Gorilla Glass Victus", 35, availableFrom = 2019, requiredTech = "Gorilla Glass Victus Entegrasyonu"),
    ComponentOption("Ceramic Shield", 50, availableFrom = 2021, requiredTech = "Ceramic Shield Zırh Entegrasyonu"),
    ComponentOption("Gorilla Armor", 60, availableFrom = 2023, requiredTech = "Gorilla Armor Zırh Entegrasyonu"),
    ComponentOption("Gorilla Armor+ / Sapphire", 80, availableFrom = 2025, requiredTech = "Safir Cam & Armor+ Entegrasyonu")
)

val ALL_CAMERAS = listOf(
    ComponentOption("5-8 MP Tek", 10, "720p Video", availableFrom = 2010),
    ComponentOption("8-13 MP Tek", 15, "1080p Video", availableFrom = 2011, requiredTech = "13MP HD Kamera Entegrasyonu"),
    ComponentOption("16-20 MP OIS", 25, "4K Video", availableFrom = 2013, requiredTech = "20MP OIS & 4K Kamera Entegrasyonu"),
    ComponentOption("12+12MP Çift Kamera", 35, "f/1.7 Geniş", availableFrom = 2015, requiredTech = "Çift Kamera Sistemi Entegrasyonu"),
    ComponentOption("Üçlü Kamera", 50, "Değişken Diyafram", availableFrom = 2017, requiredTech = "Üçlü Kamera Sistemi Entegrasyonu"),
    ComponentOption("48-108 MP Periskop", 85, "8K Video", availableFrom = 2019, requiredTech = "108MP Periskop Kamera Entegrasyonu"),
    ComponentOption("1 İnç 200MP", 130, "Sensör-Shift OIS", availableFrom = 2021, requiredTech = "200MP 1-İnç Sensör Entegrasyonu"),
    ComponentOption("GenAI Mekansal Video", 180, "Mekansal Video", availableFrom = 2023, requiredTech = "3D Mekansal Video & GenAI Kamera Entegrasyonu"),
    ComponentOption("Donanımsal ISP GenAI", 250, "Kayıpsız Zoom", availableFrom = 2025, requiredTech = "Donanımsal GenAI ISP Kamera Entegrasyonu"),
    ComponentOption("Ekran Altı UDC", 100, availableFrom = 2021, requiredTech = "Görünmez Ekran Altı Kamera Entegrasyonu")
)

val ALL_CELLULAR_NETWORKS = listOf(
    ComponentOption("2G / 3G HSPA+", 5, "21 Mbps Mobil Veri", availableFrom = 2010),
    ComponentOption("4G LTE Şebeke", 10, "150 Mbps Hızlı İnternet", availableFrom = 2011, requiredTech = "4G LTE Şebeke Entegrasyonu"),
    ComponentOption("4G LTE Cat 6 (Gelişmiş)", 14, "300 Mbps Taşıyıcı Birleştirme", availableFrom = 2016, requiredTech = "4G LTE Gelişmiş (Cat 6) Modem"),
    ComponentOption("5G Sub-6 Şebeke", 22, "Geniş Kapsama 1Gbps", availableFrom = 2019, requiredTech = "5G Sub-6 Mobil Şebeke"),
    ComponentOption("5G mmWave Ultra Hız", 30, "Milimetrik Dalga 5Gbps", availableFrom = 2020, requiredTech = "5G mmWave Ultra Hızlı Şebeke"),
    ComponentOption("Doğrudan Uydu Şebekesi & SOS", 45, "Çift Yönlü Yörünge İletişimi", availableFrom = 2025, requiredTech = "Doğrudan Uydu Şebekesi & SOS")
)

val ALL_CHARGING_PORTS = listOf(
    ComponentOption("Micro-USB 2.0 Portu", 3, "Standart 480 Mbps", availableFrom = 2010),
    ComponentOption("USB 3.0 Micro-B Portu", 8, "5 Gbps Hızlı Veri", availableFrom = 2013, requiredTech = "USB 3.0 & Yüksek Hızlı Port"),
    ComponentOption("USB-C 2.0 Simetrik Port", 10, "Çift Yönlü Takılabilir", availableFrom = 2015, requiredTech = "USB-C Simetrik Port Mimarisi"),
    ComponentOption("USB-C 3.1 & DP Video Çıkışı", 15, "10 Gbps Monitör Çıkışı", availableFrom = 2017, requiredTech = "USB-C 3.1 & DisplayPort Çıkışı"),
    ComponentOption("USB-C 3.2 Gen 2x2", 22, "20 Gbps Ultra Aktarım", availableFrom = 2021, requiredTech = "USB-C 3.1 & DisplayPort Çıkışı"),
    ComponentOption("Thunderbolt 4 / USB4 Portu", 35, "40 Gbps 8K Harici Ekran", availableFrom = 2023, requiredTech = "Thunderbolt 4 / USB4 Portu")
)

val ALL_WIRELESS_CONNECTIVITY = listOf(
    ComponentOption("Wi-Fi 4 (n) & Bluetooth 2.1", 3, "2.4 GHz Temel Bağlantı", availableFrom = 2010),
    ComponentOption("Wi-Fi 4 & Bluetooth 4.0 LE", 5, "Düşük Enerji Tüketimi", availableFrom = 2011),
    ComponentOption("Wi-Fi 5 (ac) Çift Bant", 9, "5 GHz Gigabit Wi-Fi & BT 5.0", availableFrom = 2014, requiredTech = "Wi-Fi 5 (ac) & Çift Bant Kablosuz"),
    ComponentOption("Wi-Fi 6 (ax) & BT 5.2", 14, "Düşük Gecikme & Yüksek Kapasite", availableFrom = 2019, requiredTech = "Wi-Fi 6 (ax) & Bluetooth 5.2"),
    ComponentOption("Wi-Fi 6E (6 GHz) & BT 5.3", 20, "Parazitsiz 6 GHz Geniş Bant", availableFrom = 2021, requiredTech = "Wi-Fi 6E & 6GHz Frekans Çipi"),
    ComponentOption("Wi-Fi 7 & BT 5.4 (MLO)", 30, "30 Gbps Çoklu Bağlantı İşlemi", availableFrom = 2024, requiredTech = "Wi-Fi 7 & Bluetooth 5.4 MLO")
)

val ALL_AUDIO = listOf(
    ComponentOption("Mono Hoparlör", 3, "3.5mm Jak Var", availableFrom = 2010),
    ComponentOption("Gelişmiş Beats Mono Ses", 6, "Beats Audio Çipi & 3.5mm Jak", availableFrom = 2011, requiredTech = "Gelişmiş Beats Mono Ses Entegrasyonu"),
    ComponentOption("Ön Stereo Hoparlör", 10, "Çift Ön Stereo & 3.5mm Jak", availableFrom = 2013, requiredTech = "Ön Stereo Hoparlör Entegrasyonu"),
    ComponentOption("Tip-C Çift Stereo", 14, "Jaksız Simetrik Ses Çıkışı", availableFrom = 2015, requiredTech = "Tip-C Çift Stereo Entegrasyonu"),
    ComponentOption("Dolby Atmos Ses", 18, "Sinematik Çevresel 3D Akustik", availableFrom = 2017, requiredTech = "Dolby Atmos Ses Entegrasyonu"),
    ComponentOption("Asimetrik Güçlü Stereo", 22, "Yüksek Desibel & Bas Amfisi", availableFrom = 2019, requiredTech = "Asimetrik Güçlü Stereo Entegrasyonu"),
    ComponentOption("Kafa Takipli Uzamsal Ses", 28, "3D Spatial Audio", availableFrom = 2021, requiredTech = "Kafa Takipli Uzamsal Ses Entegrasyonu"),
    ComponentOption("24-bit Kayıpsız Bluetooth Ses", 35, "LDAC / AptX Lossless", availableFrom = 2023, requiredTech = "24-bit Kayıpsız Bluetooth Ses Entegrasyonu"),
    ComponentOption("Yapay Zeka Ses Kalibrasyonu", 45, "AI NPU Akustik Optimizasyonu", availableFrom = 2025, requiredTech = "Yapay Zeka Ses Kalibrasyon Entegrasyonu")
)

val ALL_BATTERY_CAPACITIES = listOf(
    ComponentOption("1500 mAh", 5, "Temel Kapasite", availableFrom = 2010),
    ComponentOption("3100 mAh", 10, "Genişletilmiş Hücre", availableFrom = 2011, requiredTech = "3100 mAh Batarya Entegrasyonu"),
    ComponentOption("3200 mAh", 12, "Uzun Ömürlü Hücre", availableFrom = 2013, requiredTech = "3200 mAh Batarya Entegrasyonu"),
    ComponentOption("3600 mAh", 16, "Yoğun Batarya Hücresi", availableFrom = 2015, requiredTech = "3600 mAh Batarya Entegrasyonu"),
    ComponentOption("4000 mAh", 20, "Tam Gün Kullanım", availableFrom = 2017, requiredTech = "4000 mAh Batarya Entegrasyonu"),
    ComponentOption("4500 mAh", 25, "Dev Batarya Modülü", availableFrom = 2019, requiredTech = "4500 mAh Batarya Entegrasyonu"),
    ComponentOption("5000 mAh", 30, "Amiral Gemisi Batarya", availableFrom = 2021, requiredTech = "5000 mAh Batarya Entegrasyonu"),
    ComponentOption("5500 mAh", 35, "Yüksek Yoğunluklu Hücre", availableFrom = 2023, requiredTech = "5500 mAh Batarya Entegrasyonu"),
    ComponentOption("7000 mAh Dev Batarya", 45, "3 Günlük Dev Güç", availableFrom = 2025, requiredTech = "7000 mAh Dev Batarya Entegrasyonu")
)

val ALL_BATTERY_TYPES = listOf(
    ComponentOption("5W Standart Şarj", 2, "Li-Ion Standart", availableFrom = 2010),
    ComponentOption("10W Hızlı Şarj", 5, "Temel Hızlı Şarj", availableFrom = 2011, requiredTech = "10W Hızlı Şarj Entegrasyonu"),
    ComponentOption("15W QuickCharge", 8, "Qualcomm QC Protokolü", availableFrom = 2013, requiredTech = "15W QuickCharge Entegrasyonu"),
    ComponentOption("20W Li-Po & 5W Kablosuz", 14, "Li-Po & Qi Şarj", availableFrom = 2015, requiredTech = "20W Li-Po & 5W Kablosuz Şarj Entegrasyonu"),
    ComponentOption("25W Li-Po & 10W Kablosuz", 18, "Hızlı Kablosuz & Kablolu", availableFrom = 2017, requiredTech = "25W Li-Po & 10W Kablosuz Şarj Entegrasyonu"),
    ComponentOption("65W Çift Hücre & Ters Şarj", 25, "25 Dk Şarj & Ters Şarj", availableFrom = 2019, requiredTech = "65W Çift Hücre & Ters Şarj Entegrasyonu"),
    ComponentOption("120W Çift Hücre & 50W Kablosuz", 35, "15 Dk Ultra Şarj", availableFrom = 2021, requiredTech = "120W Çift Hücre & 50W Kablosuz Şarj Entegrasyonu"),
    ComponentOption("100W Katı Hal Batarya", 42, "Güvenli Katı Hal Hücresi", availableFrom = 2022, requiredTech = "100W Katı Hal Batarya Entegrasyonu"),
    ComponentOption("200W+ Silisyum-Karbon (Si-Ca)", 50, "Si-Ca Anotlu Ultra Hız", availableFrom = 2023, requiredTech = "200W+ Silisyum-Karbon Şarj Entegrasyonu"),
    ComponentOption("240W Katı Hal & Qi2 Şarj", 65, "9 Dk Şarj & Manyetik Qi2", availableFrom = 2025, requiredTech = "240W Katı Hal & Qi2 Şarj Entegrasyonu")
)
