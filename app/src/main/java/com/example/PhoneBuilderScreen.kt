/**
 * Telefon Tasarım (Phone Builder) ekranı.
 *
 * Kullanıcının yeni bir telefon modeli tasarlamasını sağlar: stil, malzeme, işlemci, RAM,
 * depolama, ekran, kamera, bağlantı, ses ve batarya gibi bileşenler yıl ve araştırılmış
 * teknolojiye ([unlockedTech]) göre filtrelenir. Tüm bileşen kataloğu [ComponentOption]
 * listeleri olarak ([ALL_PROCESSORS], [ALL_DISPLAYS] vb.) dosyanın başında tanımlıdır.
 *
 * Not: Her katalog listesinin ilk (en eski) elemanı `availableFrom = 2010` ve
 * `requiredTech = null` ile tanımlanmalıdır — aksi halde oyunun başlangıç yılında (2010)
 * ilgili liste boş kalıp `.first { ... }` çağrıları çökebilir.
 */
package com.example

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import com.example.ui.theme.*
import com.example.viewmodel.ModelTier
import com.example.viewmodel.PhoneSpecs

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneBuilderScreen(
    unlockedTech: List<String>,
    year: Int,
    existingSeries: List<String> = emptyList(),
    customOs: com.example.viewmodel.CustomOsState? = null,
    customChipsets: List<com.example.viewmodel.CustomChipset> = emptyList(),
    currentTrend: com.example.viewmodel.MarketTrend? = null,
    companyName: String = "Apex Mobile",
    companyLogoStyle: String = "Minimal Elmas",
    companyBrandColorHex: Long = 0xFF2563EB,
    checkTrendMatch: ((PhoneSpecs) -> Boolean)? = null,
    onBack: () -> Unit,
    onManufacture: (PhoneSpecs) -> Unit
) {
    var lockedNoticeTech by remember { mutableStateOf<String?>(null) }
    var selectedTabSection by remember { mutableIntStateOf(0) } // 0: Tasarım, 1: Yonga&RAM, 2: Ekran&Kamera, 3: Yazılım&OS, 4: Seri&Üretim
    var selectedOsChoice by remember { mutableIntStateOf(if (customOs != null && customOs.type != com.example.viewmodel.OsType.STOCK_ANDROID) 1 else 0) } // 0: Android AOSP, 1: Şirket OS, 2: Ticari GlobalOS

    // --- RENK SEÇENEKLERİ ---
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

    // --- GÖRSEL TASARIM & KASA ÖZELLEŞTİRMELERİ ---
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

    // --- DONANIM BİLEŞENLERİ ---
    val customChipOptions = customChipsets.filter { !it.isArchived }.map { chip ->
        ComponentOption(
            name = "👑 ${chip.name} (${chip.tier.title.substringBefore(" (")})",
            cost = chip.unitCost,
            desc = "Öz Tasarım • ${chip.processNode.nodeName} • ${chip.coreCount} Çekirdek • ${chip.performanceScore} Puan",
            availableFrom = chip.createdYear
        )
    }

    val ALL_PROCESSORS = customChipOptions + listOf(
        ComponentOption("Qualcomm S2", 25, "Performans: 150 (2010)", 2010),
        ComponentOption("MediaTek MT65", 15, "Performans: 100 (2010)", 2010),
        ComponentOption("Qualcomm S4", 40, "Performans: 250 (2012)", 2012, requiredTech = "Qualcomm S4 Çip Entegrasyonu"),
        ComponentOption("MediaTek MT67", 25, "Performans: 180 (2012)", 2012, requiredTech = "MediaTek MT67 Çip Entegrasyonu"),
        ComponentOption("Qualcomm 801", 60, "Performans: 400 (2014)", 2014, requiredTech = "Qualcomm 801 Çip Entegrasyonu"),
        ComponentOption("Intel Atom X5", 45, "Performans: 350 (2014)", 2014, requiredTech = "Intel Atom X5 Çip Entegrasyonu"),
        ComponentOption("Qualcomm 820", 85, "Performans: 600 (2016)", 2016, requiredTech = "Qualcomm 820 Çip Entegrasyonu"),
        ComponentOption("MediaTek Helio", 60, "Performans: 500 (2016)", 2016, requiredTech = "MediaTek Helio Çip Entegrasyonu"),
        ComponentOption("Qualcomm 845", 110, "Performans: 900 (2018)", 2018, requiredTech = "Qualcomm 845 Çip Entegrasyonu"),
        ComponentOption("MediaTek G90", 80, "Performans: 750 (2018)", 2018, requiredTech = "MediaTek G90 Çip Entegrasyonu"),
        ComponentOption("Qualcomm 865", 140, "Performans: 1300 (2020)", 2020, requiredTech = "Qualcomm 865 Çip Entegrasyonu"),
        ComponentOption("MediaTek D800", 100, "Performans: 1100 (2020)", 2020, requiredTech = "MediaTek D800 Çip Entegrasyonu"),
        ComponentOption("Qualcomm 8 Gen 1", 180, "Performans: 1800 (2022)", 2022, requiredTech = "Qualcomm 8 Gen 1 Çip Entegrasyonu"),
        ComponentOption("MediaTek D9000", 140, "Performans: 1600 (2022)", 2022, requiredTech = "MediaTek D9000 Çip Entegrasyonu"),
        ComponentOption("Qualcomm 8 Gen 3", 240, "Performans: 2500 (2024)", 2024, requiredTech = "Qualcomm 8 Gen 3 Çip Entegrasyonu"),
        ComponentOption("MediaTek D9300", 190, "Performans: 2300 (2024)", 2024, requiredTech = "MediaTek D9300 Çip Entegrasyonu"),
        ComponentOption("In-House Ar-Ge", 150, "Özel Üretim Çip", 2015, requiredTech = "Özel Yonga Seti Entegrasyonu"),
        ComponentOption("Kuantum İşlemci", 300, "Maksimum Güç", 2026, requiredTech = "Kuantum İşlemci Entegrasyonu")
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
        ComponentOption("Wi-Fi 4 & Bluetooth 4.0 LE", 5, "Düşük Enerji Tüketimi", availableFrom = 2011, requiredTech = "4G LTE Şebeke Entegrasyonu"),
        ComponentOption("Wi-Fi 5 (ac) Çift Bant", 9, "5 GHz Gigabit Wi-Fi & BT 5.0", availableFrom = 2014, requiredTech = "Wi-Fi 5 (ac) & Çift Bant Kablosuz"),
        ComponentOption("Wi-Fi 6 (ax) & BT 5.2", 14, "Düşük Gecikme & Yüksek Kapasite", availableFrom = 2019, requiredTech = "Wi-Fi 6 (ax) & Bluetooth 5.2"),
        ComponentOption("Wi-Fi 6E (6 GHz) & BT 5.3", 20, "Parazitsiz 6 GHz Geniş Bant", availableFrom = 2021, requiredTech = "Wi-Fi 6E & 6GHz Frekans Çipi"),
        ComponentOption("Wi-Fi 7 & BT 5.4 (MLO)", 30, "30 Gbps Çoklu Bağlantı İşlemi", availableFrom = 2024, requiredTech = "Wi-Fi 7 & Bluetooth 5.4 MLO")
    )

    val ALL_AUDIO = listOf(
        ComponentOption("Mono Hoparlör", 3, "3.5mm Jak Var", availableFrom = 2010),
        ComponentOption("Stereo Hoparlör", 8, "Dolby Atmos Jaklı", availableFrom = 2011, requiredTech = "Stereo Hoparlör & Dolby Atmos"),
        ComponentOption("Hi-Fi Quad DAC", 15, "32-bit/384kHz Jaklı", availableFrom = 2013, requiredTech = "Hi-Fi Quad DAC & 32-bit Ses"),
        ComponentOption("Simetrik Çift Hoparlör", 12, "3.5mm Jak Yok", availableFrom = 2016, requiredTech = "Simetrik Çift Hoparlör"),
        ComponentOption("Stüdyo Akustik & Mekansal", 22, "Spatial Audio", availableFrom = 2020, requiredTech = "Stüdyo Akustik & Mekansal Ses"),
        ComponentOption("Kayıpsız Kablosuz LDAC HD", 30, "Ultra HD Akustik", availableFrom = 2024, requiredTech = "Kayıpsız Kablosuz Ses (LDAC HD)")
    )

    val ALL_BATTERY_CAPACITIES = listOf(
        ComponentOption("1500 mAh", 5, availableFrom = 2010),
        ComponentOption("2500 mAh", 10, availableFrom = 2011, requiredTech = "2500 mAh Batarya Entegrasyonu"),
        ComponentOption("3500 mAh", 16, availableFrom = 2013, requiredTech = "3500 mAh Batarya Entegrasyonu"),
        ComponentOption("4500 mAh", 24, availableFrom = 2016, requiredTech = "4500 mAh Batarya Entegrasyonu"),
        ComponentOption("5500 mAh", 34, availableFrom = 2019, requiredTech = "5500 mAh Batarya Entegrasyonu"),
        ComponentOption("6500 mAh Silikon-Karbon", 50, availableFrom = 2023, requiredTech = "6500 mAh Silikon-Karbon Batarya"),
        ComponentOption("8000 mAh Katı Hal", 75, availableFrom = 2025, requiredTech = "8000 mAh Katı Hal Batarya")
    )

    val ALL_BATTERY_TYPES = listOf(
        ComponentOption("5W Standart Şarj", 2, "Li-Ion", availableFrom = 2010),
        ComponentOption("18W Hızlı Şarj", 6, "Quick Charge 2.0", availableFrom = 2012, requiredTech = "18W Hızlı Şarj Entegrasyonu"),
        ComponentOption("15W Kablosuz Şarj", 12, "Qi Şarj Desteği", availableFrom = 2014, requiredTech = "15W Kablosuz Şarj Entegrasyonu"),
        ComponentOption("65W Ultra Hızlı Şarj", 20, "25 Dk Tam Dolum", availableFrom = 2018, requiredTech = "65W Ultra Hızlı Şarj Entegrasyonu"),
        ComponentOption("120W GaN Şarj", 32, "15 Dk Tam Dolum", availableFrom = 2020, requiredTech = "120W GaN Hızlı Şarj Entegrasyonu"),
        ComponentOption("240W Hiper Şarj", 48, "9 Dk Tam Dolum", availableFrom = 2023, requiredTech = "240W Hiper Şarj Entegrasyonu")
    )

    // Filtreleme
    val currentProcessors = ALL_PROCESSORS.filter { it.availableFrom <= year }
    val currentRamCapacities = ALL_RAM_CAPACITIES.filter { it.availableFrom <= year }
    val currentRamTypes = ALL_RAM_TYPES.filter { it.availableFrom <= year }
    val currentStorages = ALL_STORAGES.filter { it.availableFrom <= year }
    val currentSdCards = ALL_SD_CARDS.filter { it.availableFrom <= year }
    val currentDisplays = ALL_DISPLAYS.filter { it.availableFrom <= year }
    val currentGlasses = ALL_GLASSES.filter { it.availableFrom <= year }
    val currentCameras = ALL_CAMERAS.filter { it.availableFrom <= year }
    val currentCellularNetworks = ALL_CELLULAR_NETWORKS.filter { it.availableFrom <= year }
    val currentChargingPorts = ALL_CHARGING_PORTS.filter { it.availableFrom <= year }
    val currentWirelessConnectivity = ALL_WIRELESS_CONNECTIVITY.filter { it.availableFrom <= year }
    val currentAudios = ALL_AUDIO.filter { it.availableFrom <= year }
    val currentBatteryCapacities = ALL_BATTERY_CAPACITIES.filter { it.availableFrom <= year }
    val currentBatteryTypes = ALL_BATTERY_TYPES.filter { it.availableFrom <= year }

    // --- SERİ, SEGMENT & MODEL YÖNETİMİ ---
    val isFlagshipUnlocked = unlockedTech.contains("Amiral Gemisi Ailesi Segmentasyonu") || year >= 2014
    var seriesMode by remember { mutableIntStateOf(if (existingSeries.isNotEmpty()) 0 else 1) } // 0: Mevcut Seri, 1: Yeni Seri
    var selectedExistingSeries by remember { mutableStateOf(existingSeries.firstOrNull() ?: "Aura") }
    var newSeriesName by remember { mutableStateOf("Nova") }
    var generationNumber by remember { mutableIntStateOf(1) }
    var selectedTier by remember { mutableStateOf(ModelTier.STANDARD) }

    val activeSeriesName = if (seriesMode == 0 && existingSeries.isNotEmpty()) selectedExistingSeries else newSeriesName
    
    // Otomatik Model İsim Önerisi
    val romanGen = when (generationNumber) {
        1 -> "I"
        2 -> "II"
        3 -> "III"
        4 -> "IV"
        5 -> "V"
        6 -> "VI"
        else -> "$generationNumber"
    }
    val defaultModelName = when (selectedTier) {
        ModelTier.STANDARD -> "$activeSeriesName $romanGen"
        ModelTier.PRO -> "$activeSeriesName $romanGen Pro"
        ModelTier.ULTRA -> "$activeSeriesName $romanGen Ultra"
        ModelTier.LITE -> "$activeSeriesName $romanGen Lite"
    }

    var phoneName by remember { mutableStateOf(defaultModelName) }

    // Seri veya Tier değiştikçe isim güncellemesi yapabilir
    LaunchedEffect(activeSeriesName, generationNumber, selectedTier) {
        phoneName = when (selectedTier) {
            ModelTier.STANDARD -> "$activeSeriesName $romanGen"
            ModelTier.PRO -> "$activeSeriesName $romanGen Pro"
            ModelTier.ULTRA -> "$activeSeriesName $romanGen Ultra"
            ModelTier.LITE -> "$activeSeriesName $romanGen Lite"
        }
    }

    // --- FORM STATE ---
    var selectedColors by remember { mutableStateOf(listOf(ALL_COLORS.first())) }
    var previewColor by remember { mutableStateOf(ALL_COLORS.first()) }
    var selectedBackFinish by remember { mutableStateOf(ALL_BACK_FINISHES.first().name) }
    var selectedCameraBump by remember { mutableStateOf(ALL_CAMERA_BUMPS.first().name) }
    var selectedFrameStyle by remember { mutableStateOf(ALL_FRAME_STYLES.first().name) }
    var selectedNotchStyle by remember { mutableStateOf(ALL_NOTCH_STYLES.first().name) }

    var selectedStyle by remember { mutableStateOf(ALL_STYLES.first().name) }
    var selectedMaterial by remember { mutableStateOf(ALL_MATERIALS.first { it.requiredTech == null || unlockedTech.contains(it.requiredTech) }.name) }
    var selectedProcessor by remember { mutableStateOf(currentProcessors.first { it.requiredTech == null || unlockedTech.contains(it.requiredTech) }.name) }
    var selectedRamCapacity by remember { mutableStateOf(currentRamCapacities.first { it.requiredTech == null || unlockedTech.contains(it.requiredTech) }.name) }
    var selectedRamType by remember { mutableStateOf(currentRamTypes.first { it.requiredTech == null || unlockedTech.contains(it.requiredTech) }.name) }
    var selectedStorage by remember { mutableStateOf(currentStorages.first { it.requiredTech == null || unlockedTech.contains(it.requiredTech) }.name) }
    var selectedSdCard by remember { mutableStateOf(currentSdCards.first { it.requiredTech == null || unlockedTech.contains(it.requiredTech) }.name) }
    var selectedDisplay by remember { mutableStateOf(currentDisplays.first { it.requiredTech == null || unlockedTech.contains(it.requiredTech) }.name) }
    var selectedGlass by remember { mutableStateOf(currentGlasses.first { it.requiredTech == null || unlockedTech.contains(it.requiredTech) }.name) }
    var selectedCamera by remember { mutableStateOf(currentCameras.first { it.requiredTech == null || unlockedTech.contains(it.requiredTech) }.name) }
    var selectedCellularNetwork by remember { mutableStateOf(currentCellularNetworks.first { it.requiredTech == null || unlockedTech.contains(it.requiredTech) }.name) }
    var selectedChargingPort by remember { mutableStateOf(currentChargingPorts.first { it.requiredTech == null || unlockedTech.contains(it.requiredTech) }.name) }
    var selectedWirelessConnectivity by remember { mutableStateOf(currentWirelessConnectivity.first { it.requiredTech == null || unlockedTech.contains(it.requiredTech) }.name) }
    var selectedAudio by remember { mutableStateOf(currentAudios.first { it.requiredTech == null || unlockedTech.contains(it.requiredTech) }.name) }
    var selectedBatteryCapacity by remember { mutableStateOf(currentBatteryCapacities.first { it.requiredTech == null || unlockedTech.contains(it.requiredTech) }.name) }
    var selectedBatteryType by remember { mutableStateOf(currentBatteryTypes.first { it.requiredTech == null || unlockedTech.contains(it.requiredTech) }.name) }
    
    var price by remember { mutableFloatStateOf(699f) }
    var quantity by remember { mutableFloatStateOf(100000f) }
    var qaBudget by remember { mutableFloatStateOf(100000f) }

    val extraColorCost = (selectedColors.size - 1).coerceAtLeast(0) * 3
    val osLicenseFee = when (selectedOsChoice) {
        2 -> 10 // Ticari lisanslı GlobalOS OEM lisans bedeli
        else -> 0
    }
    val rawUnitCost = 15 + // Temel montaj maliyeti
        (ALL_STYLES.find { it.name == selectedStyle }?.cost ?: 0) +
        (ALL_BACK_FINISHES.find { it.name == selectedBackFinish }?.cost ?: 0) +
        (ALL_CAMERA_BUMPS.find { it.name == selectedCameraBump }?.cost ?: 0) +
        (ALL_FRAME_STYLES.find { it.name == selectedFrameStyle }?.cost ?: 0) +
        (ALL_NOTCH_STYLES.find { it.name == selectedNotchStyle }?.cost ?: 0) +
        (ALL_MATERIALS.find { it.name == selectedMaterial }?.cost ?: 0) +
        (currentProcessors.find { it.name == selectedProcessor }?.cost ?: 0) +
        (currentRamCapacities.find { it.name == selectedRamCapacity }?.cost ?: 0) +
        (currentRamTypes.find { it.name == selectedRamType }?.cost ?: 0) +
        (currentStorages.find { it.name == selectedStorage }?.cost ?: 0) +
        (currentSdCards.find { it.name == selectedSdCard }?.cost ?: 0) +
        (currentDisplays.find { it.name == selectedDisplay }?.cost ?: 0) +
        (currentGlasses.find { it.name == selectedGlass }?.cost ?: 0) +
        (currentCameras.find { it.name == selectedCamera }?.cost ?: 0) +
        (currentCellularNetworks.find { it.name == selectedCellularNetwork }?.cost ?: 0) +
        (currentChargingPorts.find { it.name == selectedChargingPort }?.cost ?: 0) +
        (currentWirelessConnectivity.find { it.name == selectedWirelessConnectivity }?.cost ?: 0) +
        (currentAudios.find { it.name == selectedAudio }?.cost ?: 0) +
        (currentBatteryCapacities.find { it.name == selectedBatteryCapacity }?.cost ?: 0) +
        (currentBatteryTypes.find { it.name == selectedBatteryType }?.cost ?: 0) +
        extraColorCost +
        osLicenseFee

    val unitCost = rawUnitCost

    Scaffold(
        topBar = {
            val totalCost = (unitCost.toLong() * quantity.toLong()) + qaBudget.toLong()
            TopAppBar(
                title = { 
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Model Tasarım Stüdyosu", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = selectedTier.title,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text("Birim Maliyet: $$unitCost • Toplam: $${"%,d".format(totalCost).replace(',', '.')}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Button(
                    onClick = {
                        val currentOsType = when (selectedOsChoice) {
                            1 -> if (customOs != null && customOs.type != com.example.viewmodel.OsType.STOCK_ANDROID) customOs.type.title else "Özel Şirket Arayüzü"
                            2 -> "Ticari Lisanslı GlobalOS"
                            else -> "Saf Açık Kaynak"
                        }
                        val currentOsName = when (selectedOsChoice) {
                            1 -> if (customOs != null && customOs.type != com.example.viewmodel.OsType.STOCK_ANDROID) "${customOs.name} v${customOs.version}" else "NovaUI"
                            2 -> "GlobalOS Ticari OEM"
                            else -> "Android AOSP"
                        }
                        val currentOsFocus = when (selectedOsChoice) {
                            1 -> if (customOs != null && customOs.type != com.example.viewmodel.OsType.STOCK_ANDROID) customOs.focus.title else "Dengeli Standart"
                            2 -> "Küresel Servisler & Multimedya"
                            else -> "Temel Açık Kaynak"
                        }

                        onManufacture(
                            PhoneSpecs(
                                name = phoneName,
                                seriesName = activeSeriesName,
                                generation = generationNumber,
                                tier = selectedTier,
                                unitCost = unitCost,
                                style = selectedStyle,
                                material = selectedMaterial,
                                processor = selectedProcessor,
                                ramCapacity = selectedRamCapacity,
                                ramType = selectedRamType,
                                storage = selectedStorage,
                                sdCardSupport = selectedSdCard,
                                display = selectedDisplay,
                                glass = selectedGlass,
                                camera = selectedCamera,
                                connectivity = "$selectedCellularNetwork, $selectedChargingPort, $selectedWirelessConnectivity",
                                cellularNetwork = selectedCellularNetwork,
                                chargingPort = selectedChargingPort,
                                wirelessConnectivity = selectedWirelessConnectivity,
                                audio = selectedAudio,
                                batteryCapacity = selectedBatteryCapacity,
                                batteryType = selectedBatteryType,
                                price = price.toInt(),
                                quantity = quantity.toInt(),
                                qaBudget = qaBudget.toLong(),
                                osName = currentOsName,
                                osType = currentOsType,
                                osFocus = currentOsFocus,
                                osLicenseFee = osLicenseFee,
                                selectedColors = selectedColors.map { it.name },
                                colorHexes = selectedColors.map { it.hexValue },
                                colorName = previewColor.name,
                                colorHex = previewColor.hexValue,
                                frameStyle = selectedFrameStyle,
                                cameraBumpStyle = selectedCameraBump,
                                backFinish = selectedBackFinish,
                                notchStyle = selectedNotchStyle,
                                logoStyle = companyLogoStyle,
                                techScore = listOfNotNull(
                                    ALL_STYLES.find { it.name == selectedStyle },
                                    ALL_MATERIALS.find { it.name == selectedMaterial },
                                    currentProcessors.find { it.name == selectedProcessor },
                                    currentRamCapacities.find { it.name == selectedRamCapacity },
                                    currentRamTypes.find { it.name == selectedRamType },
                                    currentStorages.find { it.name == selectedStorage },
                                    currentSdCards.find { it.name == selectedSdCard },
                                    currentDisplays.find { it.name == selectedDisplay },
                                    currentGlasses.find { it.name == selectedGlass },
                                    currentCameras.find { it.name == selectedCamera },
                                    currentCellularNetworks.find { it.name == selectedCellularNetwork },
                                    currentChargingPorts.find { it.name == selectedChargingPort },
                                    currentWirelessConnectivity.find { it.name == selectedWirelessConnectivity },
                                    currentAudios.find { it.name == selectedAudio },
                                    currentBatteryCapacities.find { it.name == selectedBatteryCapacity },
                                    currentBatteryTypes.find { it.name == selectedBatteryType }
                                ).map { it.availableFrom }.let { years ->
                                    if (years.isNotEmpty()) years.sum() / years.size else 2010
                                }
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("ÜRETİME BAŞLA ($${"%,d".format((unitCost.toLong() * quantity.toLong()) + qaBudget.toLong()).replace(',', '.')})", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        val currentOsType = when (selectedOsChoice) {
            1 -> if (customOs != null && customOs.type != com.example.viewmodel.OsType.STOCK_ANDROID) customOs.type.title else "Özel Şirket Arayüzü"
            2 -> "Ticari Lisanslı GlobalOS"
            else -> "Saf Açık Kaynak"
        }
        val currentOsName = when (selectedOsChoice) {
            1 -> if (customOs != null && customOs.type != com.example.viewmodel.OsType.STOCK_ANDROID) "${customOs.name} v${customOs.version}" else "NovaUI"
            2 -> "GlobalOS Ticari OEM"
            else -> "Android AOSP"
        }
        val currentOsFocus = when (selectedOsChoice) {
            1 -> if (customOs != null && customOs.type != com.example.viewmodel.OsType.STOCK_ANDROID) customOs.focus.title else "Dengeli Standart"
            2 -> "Küresel Servisler & Multimedya"
            else -> "Temel Açık Kaynak"
        }

        val currentPreviewSpecs = PhoneSpecs(
            name = phoneName,
            seriesName = activeSeriesName,
            generation = generationNumber,
            tier = selectedTier,
            unitCost = unitCost,
            style = selectedStyle,
            material = selectedMaterial,
            processor = selectedProcessor,
            ramCapacity = selectedRamCapacity,
            ramType = selectedRamType,
            storage = selectedStorage,
            sdCardSupport = selectedSdCard,
            display = selectedDisplay,
            glass = selectedGlass,
            camera = selectedCamera,
            connectivity = "$selectedCellularNetwork, $selectedChargingPort, $selectedWirelessConnectivity",
            cellularNetwork = selectedCellularNetwork,
            chargingPort = selectedChargingPort,
            wirelessConnectivity = selectedWirelessConnectivity,
            audio = selectedAudio,
            batteryCapacity = selectedBatteryCapacity,
            batteryType = selectedBatteryType,
            price = price.toInt(),
            quantity = quantity.toInt(),
            qaBudget = qaBudget.toLong(),
            osName = currentOsName,
            osType = currentOsType,
            osFocus = currentOsFocus,
            osLicenseFee = osLicenseFee,
            selectedColors = selectedColors.map { it.name },
            colorHexes = selectedColors.map { it.hexValue },
            colorName = previewColor.name,
            colorHex = previewColor.hexValue,
            frameStyle = selectedFrameStyle,
            cameraBumpStyle = selectedCameraBump,
            backFinish = selectedBackFinish,
            notchStyle = selectedNotchStyle,
            logoStyle = companyLogoStyle
        )

        val isLiveTrendMatched = checkTrendMatch?.invoke(currentPreviewSpecs) ?: false

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Live Trend Match Banner
            if (currentTrend != null) {
                Surface(
                    color = if (isLiveTrendMatched) Color(0xFF1B5E20) else Color(0xFF311B92),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isLiveTrendMatched) "✨" else "🔥",
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            val trendBonusPct = ((currentTrend.bonusMultiplier - 1.0f) * 100).toInt()
                            Column {
                                Text(
                                    text = if (isLiveTrendMatched) "PAZAR TRENDİ YAKALANDI (+%$trendBonusPct SATIŞ)" else "PAZAR TRENDİ: ${currentTrend.title}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isLiveTrendMatched) Color(0xFFA5D6A7) else Color(0xFFFFD54F)
                                )
                                Text(
                                    text = if (isLiveTrendMatched) "Cihazınız ${currentTrend.category.title} talebini karşılıyor!" else "İpucu: ${currentTrend.category.tip}",
                                    fontSize = 10.sp,
                                    color = Color.White
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isLiveTrendMatched) Color(0xFF4CAF50) else Color(0xFFFF9800)
                        ) {
                            val trendBonusPct = ((currentTrend.bonusMultiplier - 1.0f) * 100).toInt()
                            Text(
                                text = if (isLiveTrendMatched) "UYUMLU" else "%$trendBonusPct BONUS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // --- ULTRA GERÇEKÇİ CANVASA SAHİP 3D/VEKTÖR ÖNİZLEME CONTAINER ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color(0xFF0B0F19), Color(0xFF1E293B))))
                    .padding(vertical = 12.dp, horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                PhoneVisualPreview(
                    style = selectedStyle,
                    material = selectedMaterial,
                    camera = selectedCamera,
                    display = selectedDisplay,
                    chargingPort = selectedChargingPort,
                    cellularNetwork = selectedCellularNetwork,
                    colorHex = previewColor.hexValue,
                    colorName = previewColor.name,
                    backFinish = selectedBackFinish,
                    cameraBumpStyle = selectedCameraBump,
                    frameStyle = selectedFrameStyle,
                    notchStyle = selectedNotchStyle,
                    logoStyle = companyLogoStyle,
                    tier = selectedTier,
                    seriesName = activeSeriesName,
                    phoneName = phoneName
                )
            }

            // --- 5 SEKME DÜZENİ: TÜM SEÇENEKLER DERLİ TOPLU VE MİNİMAL ---
            ScrollableTabRow(
                selectedTabIndex = selectedTabSection,
                containerColor = MaterialTheme.colorScheme.surface,
                edgePadding = 12.dp,
                divider = { HorizontalDivider(color = Slate200) }
            ) {
                val tabs = listOf(
                    Triple(0, "Tasarım", Icons.Default.Palette),
                    Triple(1, "Yonga & RAM", Icons.Default.Memory),
                    Triple(2, "Ekran & Kamera", Icons.Default.CameraAlt),
                    Triple(3, "Yazılım & OS", Icons.Default.Terminal),
                    Triple(4, "Seri & Üretim", Icons.Default.LocalOffer)
                )

                tabs.forEach { (index, title, icon) ->
                    val isSelected = selectedTabSection == index
                    Tab(
                        selected = isSelected,
                        onClick = { selectedTabSection = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp,
                                maxLines = 1,
                                softWrap = false
                            )
                        },
                        icon = {
                            Icon(
                                icon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else Slate500
                            )
                        }
                    )
                }
            }

            // Kilitli Teknoloji Uyarısı (Eğer tıklandıysa)
            lockedNoticeTech?.let { reqTech ->
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Column {
                                Text("Bu Seçenek Ar-Ge Kilidine Sahip", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onErrorContainer)
                                Text("Kilidi açmak için Ar-Ge ekranından '$reqTech' teknolojisini araştırın.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f))
                            }
                        }
                        IconButton(onClick = { lockedNoticeTech = null }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Kapat", tint = MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ==================== SEKME 0: TASARIM & KASA ====================
                if (selectedTabSection == 0) {
                    // 1. Color Palette Selection
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Renk Seçenekleri & Palet (Çoklu Seçim)", fontWeight = FontWeight.Bold, color = Slate800)
                            Text("${selectedColors.size} Renk Seçildi", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                        Text("Çoklu renk sunmak pazar çekiciliğini ve satışları artırır (Her ek renk: +$3)", fontSize = 11.sp, color = Slate500)

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(ALL_COLORS) { colorOpt ->
                                val isSelected = selectedColors.any { it.hexValue == colorOpt.hexValue }
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(colorOpt.color)
                                        .border(
                                            width = if (previewColor.hexValue == colorOpt.hexValue) 3.dp else if (isSelected) 2.dp else 1.dp,
                                            color = if (previewColor.hexValue == colorOpt.hexValue) MaterialTheme.colorScheme.primary else if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f) else Slate300,
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            previewColor = colorOpt
                                            if (isSelected) {
                                                if (selectedColors.size > 1) {
                                                    selectedColors = selectedColors.filter { it.hexValue != colorOpt.hexValue }
                                                }
                                            } else {
                                                selectedColors = selectedColors + colorOpt
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = if (colorOpt.color == Color(0xFFF1F5F9)) Color.Black else Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 2. Back Finish (Dokusu)
                    SelectionGroup(
                        title = "Arka Kapak Yüzey Dokusu",
                        options = ALL_BACK_FINISHES,
                        selectedOption = selectedBackFinish,
                        unlockedTech = unlockedTech,
                        onOptionSelected = { selectedBackFinish = it },
                        onLockedClick = { lockedNoticeTech = it }
                    )

                    // 4. Camera Island Bump Style
                    SelectionGroup(
                        title = "Kamera Modülü Tasarımı (Ada)",
                        options = ALL_CAMERA_BUMPS,
                        selectedOption = selectedCameraBump,
                        unlockedTech = unlockedTech,
                        onOptionSelected = { selectedCameraBump = it },
                        onLockedClick = { lockedNoticeTech = it }
                    )

                    // 5. Frame & Edge Style
                    SelectionGroup(
                        title = "Kasa & Kenar Yapısı",
                        options = ALL_FRAME_STYLES,
                        selectedOption = selectedFrameStyle,
                        unlockedTech = unlockedTech,
                        onOptionSelected = { selectedFrameStyle = it },
                        onLockedClick = { lockedNoticeTech = it }
                    )

                    // 6. Notch / Front Screen Style
                    SelectionGroup(
                        title = "Ön Kamera & Çentik Tasarımı",
                        options = ALL_NOTCH_STYLES,
                        selectedOption = selectedNotchStyle,
                        unlockedTech = unlockedTech,
                        onOptionSelected = { selectedNotchStyle = it },
                        onLockedClick = { lockedNoticeTech = it }
                    )

                    // 7. Design Theme Style
                    SelectionGroup(
                        title = "Tasarım Teması",
                        options = ALL_STYLES,
                        selectedOption = selectedStyle,
                        unlockedTech = unlockedTech,
                        onOptionSelected = { selectedStyle = it },
                        onLockedClick = { lockedNoticeTech = it }
                    )

                    // 8. Material
                    SelectionGroup(
                        title = "Kasa Malzemesi",
                        options = ALL_MATERIALS,
                        selectedOption = selectedMaterial,
                        unlockedTech = unlockedTech,
                        onOptionSelected = { selectedMaterial = it },
                        onLockedClick = { lockedNoticeTech = it }
                    )
                }

                // ==================== SEKME 1: YONGA & BELLEK ====================
                if (selectedTabSection == 1) {
                    // Processor
                    SelectionGroup(
                        title = "İşlemci / Yonga Seti",
                        options = currentProcessors,
                        selectedOption = selectedProcessor,
                        unlockedTech = unlockedTech,
                        onOptionSelected = { selectedProcessor = it },
                        onLockedClick = { lockedNoticeTech = it }
                    )

                    // RAM Capacity
                    SelectionGroup(
                        title = "RAM Kapasitesi",
                        options = currentRamCapacities,
                        selectedOption = selectedRamCapacity,
                        unlockedTech = unlockedTech,
                        onOptionSelected = { selectedRamCapacity = it },
                        onLockedClick = { lockedNoticeTech = it }
                    )

                    // RAM Type
                    SelectionGroup(
                        title = "RAM Teknolojisi & Hızı",
                        options = currentRamTypes,
                        selectedOption = selectedRamType,
                        unlockedTech = unlockedTech,
                        onOptionSelected = { selectedRamType = it },
                        onLockedClick = { lockedNoticeTech = it }
                    )

                    // Storage Capacity
                    SelectionGroup(
                        title = "Dahili Depolama Alanı",
                        options = currentStorages,
                        selectedOption = selectedStorage,
                        unlockedTech = unlockedTech,
                        onOptionSelected = { selectedStorage = it },
                        onLockedClick = { lockedNoticeTech = it }
                    )

                    // SD Card Slot
                    SelectionGroup(
                        title = "MicroSD / Hafıza Kartı Yuvası",
                        options = currentSdCards,
                        selectedOption = selectedSdCard,
                        unlockedTech = unlockedTech,
                        onOptionSelected = { selectedSdCard = it },
                        onLockedClick = { lockedNoticeTech = it }
                    )
                }

                // ==================== SEKME 2: EKRAN & KAMERA & DONANIM ====================
                if (selectedTabSection == 2) {
                    // Display Panel
                    SelectionGroup(
                        title = "Ekran Paneli & Çözünürlük",
                        options = currentDisplays,
                        selectedOption = selectedDisplay,
                        unlockedTech = unlockedTech,
                        onOptionSelected = { selectedDisplay = it },
                        onLockedClick = { lockedNoticeTech = it }
                    )

                    // Protective Glass
                    SelectionGroup(
                        title = "Koruyucu Ön Cam",
                        options = currentGlasses,
                        selectedOption = selectedGlass,
                        unlockedTech = unlockedTech,
                        onOptionSelected = { selectedGlass = it },
                        onLockedClick = { lockedNoticeTech = it }
                    )

                    // Camera Module
                    SelectionGroup(
                        title = "Kamera Sensörü & Lensler",
                        options = currentCameras,
                        selectedOption = selectedCamera,
                        unlockedTech = unlockedTech,
                        onOptionSelected = { selectedCamera = it },
                        onLockedClick = { lockedNoticeTech = it }
                    )

                    // Audio & Speakers
                    SelectionGroup(
                        title = "Ses & Hoparlör Sistemi",
                        options = currentAudios,
                        selectedOption = selectedAudio,
                        unlockedTech = unlockedTech,
                        onOptionSelected = { selectedAudio = it },
                        onLockedClick = { lockedNoticeTech = it }
                    )

                    // Battery Capacity
                    SelectionGroup(
                        title = "Batarya Kapasitesi",
                        options = currentBatteryCapacities,
                        selectedOption = selectedBatteryCapacity,
                        unlockedTech = unlockedTech,
                        onOptionSelected = { selectedBatteryCapacity = it },
                        onLockedClick = { lockedNoticeTech = it }
                    )

                    // Battery & Charging Tech
                    SelectionGroup(
                        title = "Şarj Teknolojisi",
                        options = currentBatteryTypes,
                        selectedOption = selectedBatteryType,
                        unlockedTech = unlockedTech,
                        onOptionSelected = { selectedBatteryType = it },
                        onLockedClick = { lockedNoticeTech = it }
                    )

                    // Mobile Cellular Network
                    SelectionGroup(
                        title = "Mobil Şebeke & Hücresel Modem (3G / 4G / 5G / Uydu)",
                        options = currentCellularNetworks,
                        selectedOption = selectedCellularNetwork,
                        unlockedTech = unlockedTech,
                        onOptionSelected = { selectedCellularNetwork = it },
                        onLockedClick = { lockedNoticeTech = it }
                    )

                    // Charging & Data Port
                    SelectionGroup(
                        title = "Şarj & Kablolu Veri Portu (USB / Tip-C / Thunderbolt)",
                        options = currentChargingPorts,
                        selectedOption = selectedChargingPort,
                        unlockedTech = unlockedTech,
                        onOptionSelected = { selectedChargingPort = it },
                        onLockedClick = { lockedNoticeTech = it }
                    )

                    // Wireless Connectivity (Wi-Fi & Bluetooth)
                    SelectionGroup(
                        title = "Kablosuz Ağ & Bluetooth Çipi (Wi-Fi 4/5/6/7 & BT)",
                        options = currentWirelessConnectivity,
                        selectedOption = selectedWirelessConnectivity,
                        unlockedTech = unlockedTech,
                        onOptionSelected = { selectedWirelessConnectivity = it },
                        onLockedClick = { lockedNoticeTech = it }
                    )
                }

                // ==================== SEKME 3: YAZILIM & OS ====================
                if (selectedTabSection == 3) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Text("🌐 İşletim Sistemi ve Yazılım Mimarisi", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
                            Text("Cihazınızda çalışacak işletim sistemi türünü seçin. Kendi yazılımınızı kullanmak uygulama mağazası ekosistem geliri ve optimizasyon puanı sağlar.", fontSize = 11.sp, color = Slate600)

                            // Seçenek 1: Şirket Özel İşletim Sistemi (eğer geliştirilmişse)
                            val hasCompanyOs = customOs != null && customOs.type != com.example.viewmodel.OsType.STOCK_ANDROID
                            val isCompanyOsSelected = selectedOsChoice == 1

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isCompanyOsSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                border = if (isCompanyOsSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { if (hasCompanyOs) selectedOsChoice = 1 }
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Text(
                                                text = if (hasCompanyOs) "🚀 Şirket Yazılımı: ${customOs?.name} v${customOs?.version}" else "🔒 Şirket Yazılımı (Geliştirilmedi)",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = if (hasCompanyOs) (if (isCompanyOsSelected) MaterialTheme.colorScheme.primary else Slate900) else Slate400
                                            )
                                            if (hasCompanyOs) {
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = if (customOs?.licenseType == com.example.viewmodel.OsLicenseType.OPEN_SOURCE) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFF6366F1).copy(alpha = 0.2f)
                                                ) {
                                                    Text(
                                                        text = customOs?.licenseType?.badge ?: "",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (customOs?.licenseType == com.example.viewmodel.OsLicenseType.OPEN_SOURCE) Color(0xFF10B981) else Color(0xFF6366F1),
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                        RadioButton(
                                            selected = isCompanyOsSelected,
                                            onClick = { if (hasCompanyOs) selectedOsChoice = 1 },
                                            enabled = hasCompanyOs
                                        )
                                    }

                                    if (hasCompanyOs) {
                                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Text("• Lisans Bedeli: $0 (Kendi ekosisteminiz, ekstra cihaz maliyeti yok)", fontSize = 11.sp, color = Green500, fontWeight = FontWeight.SemiBold)
                                            Text("• Optimizasyon Puanı: +${(customOs?.optimizationScore ?: 0) / 5} İnceleme Bonusu", fontSize = 11.sp, color = Slate700)
                                            Text("• Pazar Popülaritesi: %${"%.1f".format(customOs?.popularityPercent ?: 1.0f)} (${customOs?.focus?.title} Odaklı)", fontSize = 11.sp, color = Slate700)
                                            Text("• Ekosistem Kazancı: Satılan cihazlardan aylık App Store mağaza geliri!", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        Text("Yazılım & OS sekmesinden $100M Ar-Ge ile kendi işletim sisteminizi oluşturabilirsiniz.", fontSize = 11.sp, color = Slate500)
                                    }
                                }
                            }

                            // Seçenek 0: Saf Açık Kaynak Android (AOSP)
                            val isAospSelected = selectedOsChoice == 0
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isAospSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                border = if (isAospSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedOsChoice = 0 }
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text("🌐 Saf Açık Kaynak Android (AOSP)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (isAospSelected) MaterialTheme.colorScheme.primary else Slate900)
                                            Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF10B981).copy(alpha = 0.15f)) {
                                                Text("Ücretsiz", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981), modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                            }
                                        }
                                        RadioButton(selected = isAospSelected, onClick = { selectedOsChoice = 0 })
                                    }
                                    Text("• Lisans Bedeli: $0 • Standart temel optimizasyon", fontSize = 11.sp, color = Slate700)
                                    Text("• Ekosistem Mağaza Geliri: Yok (Gelir üçüncü taraf arama devine gider)", fontSize = 11.sp, color = Slate500)
                                }
                            }

                            // Seçenek 2: Ticari Lisanslı GlobalOS (Üçüncü Taraf OEM)
                            val isGlobalOsSelected = selectedOsChoice == 2
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isGlobalOsSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                border = if (isGlobalOsSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedOsChoice = 2 }
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text("🏢 Ticari Lisanslı GlobalOS", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (isGlobalOsSelected) MaterialTheme.colorScheme.primary else Slate900)
                                            Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFF59E0B).copy(alpha = 0.2f)) {
                                                Text("+$10 Lisans", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309), modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                            }
                                        }
                                        RadioButton(selected = isGlobalOsSelected, onClick = { selectedOsChoice = 2 })
                                    }
                                    Text("• Lisans Maliyeti: Cihaz başı $10 OEM lisans ücreti", fontSize = 11.sp, color = Color(0xFFB45309), fontWeight = FontWeight.SemiBold)
                                    Text("• Küresel hazır servisler ve popüler ön yüklü uygulamalar (+3 İnceleme Bonusu)", fontSize = 11.sp, color = Slate700)
                                }
                            }
                        }
                    }
                }

                // ==================== SEKME 4: SERİ, SEGMENT & ÜRETİM ====================
                if (selectedTabSection == 4) {
                    // Seri Yönetimi Kartı
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("📱 Seri ve Model Ailesi Yönetimi", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
                            Text("Aynı seriyi devam ettirmek marka sadakatini ve tekrar eden müşteri satışlarını artırır.", fontSize = 11.sp, color = Slate600)

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = seriesMode == 0 && existingSeries.isNotEmpty(),
                                    onClick = { if (existingSeries.isNotEmpty()) seriesMode = 0 },
                                    label = { Text("Mevcut Seriden Devam Et") },
                                    enabled = existingSeries.isNotEmpty()
                                )
                                FilterChip(
                                    selected = seriesMode == 1 || existingSeries.isEmpty(),
                                    onClick = { seriesMode = 1 },
                                    label = { Text("Yeni Seri Başlat") }
                                )
                            }

                            if (seriesMode == 0 && existingSeries.isNotEmpty()) {
                                Text("Mevcut Seriler:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate800)
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(existingSeries) { sName ->
                                        val isSel = selectedExistingSeries == sName
                                        ElevatedFilterChip(
                                            selected = isSel,
                                            onClick = { selectedExistingSeries = sName },
                                            label = { Text(sName, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) }
                                        )
                                    }
                                }
                            } else {
                                OutlinedTextField(
                                    value = newSeriesName,
                                    onValueChange = { newSeriesName = it },
                                    label = { Text("Yeni Seri Adı") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Nesil Numarası (Gen):", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate800)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    IconButton(onClick = { if (generationNumber > 1) generationNumber-- }) {
                                        Icon(Icons.Default.Remove, contentDescription = "-")
                                    }
                                    Text("Gen $generationNumber ($romanGen)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                                    IconButton(onClick = { generationNumber++ }) {
                                        Icon(Icons.Default.Add, contentDescription = "+")
                                    }
                                }
                            }
                        }
                    }

                    // Model Segmenti / Tier Seçici Kartı (Amiral Gemisi 3'lü Aile: Lite, Standart, Pro, Ultra)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🏆 Model Segmenti & Seri İsimlendirmesi", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900)
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "${selectedTier.badge} ${selectedTier.title}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Aynı seri altında Lite (Ekonomik), Standart (Dengeli), Pro ve Ultra / Pro Max (Zirve) modelleri oluşturabilirsiniz.",
                                fontSize = 11.sp,
                                color = Slate600
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                ModelTier.entries.forEach { tier ->
                                    val isTierSelected = selectedTier == tier

                                    OutlinedButton(
                                        onClick = {
                                            selectedTier = tier
                                            // Segment değiştiğinde tavsiye edilen fiyatı ayarla
                                            price = when (tier) {
                                                ModelTier.LITE -> 349f
                                                ModelTier.STANDARD -> 599f
                                                ModelTier.PRO -> 899f
                                                ModelTier.ULTRA -> 1199f
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (isTierSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(
                                            width = if (isTierSelected) 2.dp else 1.dp,
                                            color = if (isTierSelected) MaterialTheme.colorScheme.primary else Slate300
                                        ),
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = tier.title,
                                                fontWeight = if (isTierSelected) FontWeight.Bold else FontWeight.Medium,
                                                fontSize = 11.sp,
                                                color = if (isTierSelected) MaterialTheme.colorScheme.primary else Slate800
                                            )
                                        }
                                    }
                                }
                            }

                            Surface(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = "Üretim maliyeti ($$unitCost) yalnızca seçtiğiniz fiziksel donanımlara (İşlemci, RAM, Kamera vb.) göre hesaplanır. Segment ismi maliyeti yapay olarak değiştirmez.",
                                        fontSize = 10.5.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    // Telefon Adı Girişi
                    OutlinedTextField(
                        value = phoneName,
                        onValueChange = { phoneName = it },
                        label = { Text("Model Ticari Adı") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = {
                                phoneName = defaultModelName
                            }) {
                                Icon(Icons.Default.Refresh, contentDescription = "Otomatik İsim")
                            }
                        }
                    )

                    // Fiyat ve Üretim Miktarı
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            // Fiyat
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Satış Fiyatı (Perakende)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("$${price.toInt()}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                                }
                                Slider(
                                    value = price,
                                    onValueChange = { price = it },
                                    valueRange = 99f..2499f,
                                    steps = 47
                                )
                            }

                            // Üretim Adedi
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Üretim Adedi (Parti)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("${"%,d".format(quantity.toLong()).replace(',', '.')} Adet", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                                }
                                Slider(
                                    value = quantity,
                                    onValueChange = { quantity = it },
                                    valueRange = 10000f..1000000f,
                                    steps = 98
                                )
                            }

                            // QA Kalite Kontrol
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Kalite Kontrol (QA) Bütçesi", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("$${"%,d".format(qaBudget.toLong()).replace(',', '.')}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                                }
                                Slider(
                                    value = qaBudget,
                                    onValueChange = { qaBudget = it },
                                    valueRange = 0f..1000000f,
                                    steps = 20
                                )

                                // Geri Çağırma (Recall) Risk Önizlemesi: QA bütçesi düştükçe
                                // üretim sonrası geri çağrılma riski artar. Kesin risk üretim
                                // anında tasarım/teknoloji puanına göre biraz daha değişebilir.
                                val qaPerUnitPreview = if (quantity > 0) qaBudget / quantity else 0f
                                val recallRiskPreview = (55f - (qaPerUnitPreview * 3f).coerceAtMost(40f))
                                    .roundToInt().coerceIn(2, 55)
                                val riskColor = when {
                                    recallRiskPreview <= 15 -> Green500
                                    recallRiskPreview <= 30 -> Color(0xFFF59E0B)
                                    else -> Color(0xFFEF4444)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Tahmini Geri Çağırma Riski (ilk 3 ay)",
                                        fontSize = 11.sp,
                                        color = Slate600
                                    )
                                    Text(
                                        "%$recallRiskPreview",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = riskColor
                                    )
                                }
                            }

                            // Finansal Özet Kartı
                            val margin = price - unitCost
                            val marginPercent = if (price > 0) ((margin / price) * 100).toInt() else 0
                            Surface(
                                color = if (margin > 0) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Birim Kâr Marjı", fontSize = 11.sp, color = Slate700)
                                        Text("+$${margin.toInt()} (%$marginPercent)", fontWeight = FontWeight.Bold, color = if (margin > 0) Green500 else Color.Red, fontSize = 13.sp)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Parti Toplam Maliyeti", fontSize = 11.sp, color = Slate700)
                                        Text("$${"%,d".format((unitCost.toLong() * quantity.toLong()) + qaBudget.toLong()).replace(',', '.')}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// 📱 ULTRA GERÇEKÇİ TELEFON VEKTÖR & 3D CANVASA SAHİP ÖNİZLEME BİLEŞENİ
// =========================================================================
@Composable
fun PhoneVisualPreview(
    style: String,
    material: String,
    camera: String,
    display: String,
    chargingPort: String = "USB-C 3.1 & DisplayPort Çıkışı",
    cellularNetwork: String = "5G Sub-6 Şebeke",
    colorHex: Long,
    colorName: String,
    backFinish: String,
    cameraBumpStyle: String,
    frameStyle: String,
    notchStyle: String,
    logoStyle: String,
    tier: ModelTier = ModelTier.STANDARD,
    seriesName: String = "",
    phoneName: String = ""
) {
    // 4 Mod: "Çift", "Arka", "Ön", "Port"
    var viewMode by remember { mutableStateOf("Çift") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // --- 4'LÜ ETKİLEŞİMLİ STÜDYO GÖRÜNÜM SEÇİCİ ---
        Row(
            modifier = Modifier
                .background(Color(0xFF0F172A), RoundedCornerShape(22.dp))
                .border(1.dp, Color(0xFF334155), RoundedCornerShape(22.dp))
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            val modes = listOf(
                Pair("Çift", "✨ Stüdyo Çift"),
                Pair("Arka", "📱 Arka Kapak"),
                Pair("Ön", "📺 Ön Ekran"),
                Pair("Port", "⚡ Kenar & Port")
            )
            modes.forEach { (modeKey, label) ->
                val isSel = viewMode == modeKey
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { viewMode = modeKey }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = label,
                        color = if (isSel) Color.White else Color(0xFF94A3B8),
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- 3D STÜDYO SAHNESİ & GÖLGELER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(355.dp),
            contentAlignment = Alignment.Center
        ) {
            // Stüdyo Zemin Işık Halkası & Spot
            Canvas(modifier = Modifier.fillMaxSize()) {
                val baseColor = Color(colorHex)
                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            baseColor.copy(alpha = 0.35f),
                            Color(0xFF1E293B).copy(alpha = 0.2f),
                            Color.Transparent
                        ),
                        center = Offset(size.width / 2, size.height * 0.88f),
                        radius = size.width * 0.48f
                    ),
                    topLeft = Offset(size.width * 0.08f, size.height * 0.76f),
                    size = Size(size.width * 0.84f, size.height * 0.22f)
                )
            }

            when (viewMode) {
                "Çift" -> {
                    // Stüdyo Çift Görünüm (Ön Ekran Hafif Açılı Arkada + Arka Kapak Önde)
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Sol / Arkadaki Ön Ekran
                        Box(
                            modifier = Modifier
                                .offset(x = (-38).dp, y = (-8).dp)
                                .size(width = 150.dp, height = 300.dp)
                                .shadow(
                                    elevation = 20.dp,
                                    shape = RoundedCornerShape(30.dp),
                                    spotColor = Color.Black.copy(alpha = 0.7f)
                                )
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawFrontDisplay(
                                    size = size,
                                    frameStyle = frameStyle,
                                    material = material,
                                    notchStyle = notchStyle,
                                    display = display,
                                    colorHex = colorHex
                                )
                            }
                        }

                        // Sağ / Öndeki Arka Gövde
                        Box(
                            modifier = Modifier
                                .offset(x = 38.dp, y = 8.dp)
                                .size(width = 155.dp, height = 310.dp)
                                .shadow(
                                    elevation = 28.dp,
                                    shape = RoundedCornerShape(32.dp),
                                    spotColor = Color(colorHex).copy(alpha = 0.55f),
                                    ambientColor = Color.Black.copy(alpha = 0.8f)
                                )
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawBackChassis(
                                    size = size,
                                    frameStyle = frameStyle,
                                    material = material,
                                    backFinish = backFinish,
                                    cameraBumpStyle = cameraBumpStyle,
                                    camera = camera,
                                    colorHex = colorHex,
                                    logoStyle = logoStyle
                                )
                            }
                        }
                    }
                }

                "Arka" -> {
                    // Tam Ekran Arka Gövde
                    Box(
                        modifier = Modifier
                            .size(width = 175.dp, height = 345.dp)
                            .shadow(
                                elevation = 32.dp,
                                shape = RoundedCornerShape(36.dp),
                                spotColor = Color(colorHex).copy(alpha = 0.65f),
                                ambientColor = Color.Black.copy(alpha = 0.8f)
                            )
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawBackChassis(
                                size = size,
                                frameStyle = frameStyle,
                                material = material,
                                backFinish = backFinish,
                                cameraBumpStyle = cameraBumpStyle,
                                camera = camera,
                                colorHex = colorHex,
                                logoStyle = logoStyle
                            )
                        }
                    }
                }

                "Ön" -> {
                    // Tam Ekran Ön Ekran (OLED)
                    Box(
                        modifier = Modifier
                            .size(width = 175.dp, height = 345.dp)
                            .shadow(
                                elevation = 32.dp,
                                shape = RoundedCornerShape(36.dp),
                                spotColor = Color(colorHex).copy(alpha = 0.65f),
                                ambientColor = Color.Black.copy(alpha = 0.8f)
                            )
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawFrontDisplay(
                                size = size,
                                frameStyle = frameStyle,
                                material = material,
                                notchStyle = notchStyle,
                                display = display,
                                colorHex = colorHex
                            )
                        }
                    }
                }

                "Port" -> {
                    // Yan Profil & Alt Port Detayı
                    Box(
                        modifier = Modifier
                            .size(width = 240.dp, height = 330.dp)
                            .shadow(
                                elevation = 24.dp,
                                shape = RoundedCornerShape(20.dp),
                                spotColor = Color(colorHex).copy(alpha = 0.45f)
                            )
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawSideAndPortChassis(
                                size = size,
                                material = material,
                                chargingPort = chargingPort,
                                cellularNetwork = cellularNetwork,
                                colorHex = colorHex,
                                frameStyle = frameStyle
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- CİHAZ ÖZELLİKLERİ PREMIUM ETİKET ŞERİDİ ---
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1E293B).copy(alpha = 0.9f),
            border = BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "✨ $material Çerçeve",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF38BDF8)
                )
                Text("•", color = Color(0xFF64748B), fontSize = 11.sp)
                Text(
                    text = backFinish,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFE2E8F0)
                )
                Text("•", color = Color(0xFF64748B), fontSize = 11.sp)
                Text(
                    text = colorName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(colorHex)
                )
            }
        }
    }
}

// =========================================================================
// 🎨 ÇİZİM YARDIMCILARI: ARKA KAPAK, ÖN EKRAN VE YAN PORT KANVASI
// =========================================================================

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBackChassis(
    size: Size,
    frameStyle: String,
    material: String,
    backFinish: String,
    cameraBumpStyle: String,
    camera: String,
    colorHex: Long,
    logoStyle: String
) {
    val phoneCorner = when (frameStyle) {
        "Düz Metal Kenar" -> 28.dp.toPx()
        "Zırhlı Kesim" -> 16.dp.toPx()
        "Ultra İnce Çerçeve" -> 34.dp.toPx()
        else -> 30.dp.toPx() // Kavisli 2.5D
    }
    val baseColor = Color(colorHex)
    val isLightColor = colorHex == 0xFFF1F5F9L || colorHex == 0xFFFFD700L || colorHex == 0xFFFFFFFFL

    // 1. ANODİZE DIŞ CNC ÇERÇEVE
    val frameBrush = when (material) {
        "Plastik" -> Brush.linearGradient(
            listOf(Color(0xFF64748B), Color(0xFF334155), Color(0xFF1E293B)),
            start = Offset(0f, 0f), end = Offset(size.width, size.height)
        )
        "Cam" -> Brush.linearGradient(
            listOf(Color(0xFF475569), Color(0xFF1E293B), Color(0xFF0F172A)),
            start = Offset(0f, 0f), end = Offset(size.width, size.height)
        )
        "Titanyum" -> Brush.linearGradient(
            listOf(Color(0xFFF1F5F9), Color(0xFF94A3B8), Color(0xFF475569), Color(0xFF1E293B), Color(0xFFCBD5E1)),
            start = Offset(0f, 0f), end = Offset(size.width, size.height)
        )
        else -> Brush.linearGradient( // Alüminyum & standard
            listOf(Color(0xFFFFFFFF), Color(0xFFCBD5E1), Color(0xFF94A3B8), Color(0xFF64748B)),
            start = Offset(0f, 0f), end = Offset(size.width, size.height)
        )
    }

    // Dış Metal Kasa
    drawRoundRect(
        brush = frameBrush,
        size = size,
        cornerRadius = CornerRadius(phoneCorner, phoneCorner)
    )

    // Çerçeve Kenar Chamfer (Pah Kırımı)
    drawRoundRect(
        brush = Brush.linearGradient(
            listOf(Color.White.copy(alpha = 0.55f), Color.Transparent, Color.Black.copy(alpha = 0.6f)),
            start = Offset(0f, 0f), end = Offset(size.width, size.height)
        ),
        size = size,
        cornerRadius = CornerRadius(phoneCorner, phoneCorner),
        style = Stroke(width = 1.5.dp.toPx())
    )

    // Yan Düğmeler
    drawRoundRect(
        brush = frameBrush,
        topLeft = Offset(-1.5.dp.toPx(), 54.dp.toPx()),
        size = Size(3.5.dp.toPx(), 40.dp.toPx()),
        cornerRadius = CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx())
    )
    drawRoundRect(
        brush = frameBrush,
        topLeft = Offset(size.width - 2.dp.toPx(), 65.dp.toPx()),
        size = Size(3.5.dp.toPx(), 28.dp.toPx()),
        cornerRadius = CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx())
    )

    // 2. ARKA KAPAK YÜZEYİ & DOKU
    val backMargin = 1.8.dp.toPx()
    val backRect = Size(size.width - (backMargin * 2), size.height - (backMargin * 2))
    val backCorner = phoneCorner - backMargin

    val backBrush = when (backFinish) {
        "Parlak Ayna Cam" -> Brush.linearGradient(
            listOf(
                baseColor.copy(alpha = 0.8f),
                baseColor,
                Color.White.copy(alpha = 0.45f),
                baseColor.copy(alpha = 0.9f),
                baseColor,
                Color.Black.copy(alpha = 0.35f)
            ),
            start = Offset(0f, 0f), end = Offset(size.width, size.height)
        )
        "Karbon Fiber" -> Brush.radialGradient(
            listOf(Color(0xFF1E293B), Color(0xFF0F172A), baseColor.copy(alpha = 0.8f)),
            center = Offset(size.width / 2, size.height / 2), radius = size.height * 0.7f
        )
        "Vegan Deri" -> Brush.verticalGradient(
            listOf(baseColor, baseColor.copy(alpha = 0.95f), baseColor.copy(alpha = 0.85f))
        )
        "Fırçalanmış Metal" -> Brush.horizontalGradient(
            listOf(baseColor.copy(alpha = 0.9f), baseColor, baseColor.copy(alpha = 0.95f), baseColor.copy(alpha = 0.85f))
        )
        else -> Brush.radialGradient( // Buzlu Mat Cam
            listOf(baseColor.copy(alpha = 0.98f), baseColor, baseColor.copy(alpha = 0.85f)),
            center = Offset(size.width * 0.38f, size.height * 0.28f), radius = size.height * 0.75f
        )
    }

    drawRoundRect(
        brush = backBrush,
        topLeft = Offset(backMargin, backMargin),
        size = backRect,
        cornerRadius = CornerRadius(backCorner, backCorner)
    )

    // Doku Efektleri
    when (backFinish) {
        "Vegan Deri" -> {
            drawRoundRect(
                color = if (isLightColor) Color(0xFF334155).copy(alpha = 0.4f) else Color.White.copy(alpha = 0.3f),
                topLeft = Offset(backMargin + 4.5.dp.toPx(), backMargin + 4.5.dp.toPx()),
                size = Size(backRect.width - 9.dp.toPx(), backRect.height - 9.dp.toPx()),
                cornerRadius = CornerRadius(backCorner - 4.dp.toPx(), backCorner - 4.dp.toPx()),
                style = Stroke(width = 1.dp.toPx())
            )
            drawLine(
                color = if (isLightColor) Color(0xFF0F172A).copy(alpha = 0.2f) else Color.White.copy(alpha = 0.15f),
                start = Offset(size.width / 2, backMargin + 6.dp.toPx()),
                end = Offset(size.width / 2, size.height - backMargin - 6.dp.toPx()),
                strokeWidth = 1.2.dp.toPx()
            )
        }
        "Karbon Fiber" -> {
            for (i in -8..20) {
                val offset = i * 16.dp.toPx()
                drawLine(
                    color = Color.White.copy(alpha = 0.08f),
                    start = Offset(backMargin, offset),
                    end = Offset(offset + size.width, backMargin + size.height),
                    strokeWidth = 1.2.dp.toPx()
                )
            }
        }
        "Parlak Ayna Cam" -> {
            val mirrorGleam = Path().apply {
                moveTo(size.width * 0.75f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width * 0.25f, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(mirrorGleam, brush = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.25f), Color.Transparent)))
        }
        else -> {
            // Buzlu Cam İpeksi Diffuse Parıltı
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color.White.copy(alpha = 0.24f), Color.White.copy(alpha = 0.06f), Color.Transparent),
                    center = Offset(size.width * 0.72f, size.height * 0.32f), radius = 80.dp.toPx()
                ),
                radius = 80.dp.toPx(),
                center = Offset(size.width * 0.72f, size.height * 0.32f)
            )
        }
    }

    // 3. FOTOGERÇEKÇİ KAMERA MODÜLÜ (CAMERA ISLAND & LENSES)
    val camPlateBrush = Brush.linearGradient(
        listOf(Color(0xFF1E293B), Color(0xFF0F172A), Color(0xFF020617)),
        start = Offset(0f, 0f), end = Offset(size.width, size.height)
    )
    val metalRingBrush = Brush.linearGradient(
        listOf(Color(0xFFFFFFFF), Color(0xFF94A3B8), Color(0xFF475569), Color(0xFFCBD5E1)),
        start = Offset(0f, 0f), end = Offset(20f, 20f)
    )

    fun drawOpticalLens(center: Offset, outerRadius: Float, isPeriscope: Boolean = false) {
        if (isPeriscope) {
            // Dikdörtgen Periskop Prizması
            val pW = outerRadius * 1.8f
            val pH = outerRadius * 1.4f
            drawRoundRect(
                brush = metalRingBrush,
                topLeft = Offset(center.x - (pW / 2), center.y - (pH / 2)),
                size = Size(pW, pH),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
            drawRoundRect(
                color = Color(0xFF090D16),
                topLeft = Offset(center.x - (pW / 2) + 1.5.dp.toPx(), center.y - (pH / 2) + 1.5.dp.toPx()),
                size = Size(pW - 3.dp.toPx(), pH - 3.dp.toPx()),
                cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
            )
            drawCircle(
                brush = Brush.radialGradient(listOf(Color(0xFF0284C7), Color(0xFF0F172A)), center = center, radius = outerRadius * 0.6f),
                radius = outerRadius * 0.5f,
                center = center
            )
            return
        }

        // Çok Katmanlı Dairesel Lens Barrel & AR Kaplama
        drawCircle(brush = metalRingBrush, radius = outerRadius, center = center)
        drawCircle(color = Color(0xFF090D16), radius = outerRadius - 1.8.dp.toPx(), center = center)
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0xFF0284C7), Color(0xFF1E1B4B), Color(0xFF030712)),
                center = center, radius = outerRadius - 3.dp.toPx()
            ),
            radius = outerRadius - 3.dp.toPx(),
            center = center
        )
        drawCircle(color = Color(0xFF000000), radius = outerRadius * 0.4f, center = center)
        // Safir Kristal Yansıma Flaresi
        drawCircle(
            color = Color.White.copy(alpha = 0.9f),
            radius = 1.4.dp.toPx(),
            center = Offset(center.x - (outerRadius * 0.32f), center.y - (outerRadius * 0.32f))
        )
        drawCircle(
            color = Color(0xFF38BDF8).copy(alpha = 0.7f),
            radius = 0.8.dp.toPx(),
            center = Offset(center.x + (outerRadius * 0.22f), center.y + (outerRadius * 0.22f))
        )
    }

    fun drawFlashAndSensors(centerFlash: Offset, centerLaser: Offset?) {
        // True Tone Dual LED Flash
        drawCircle(brush = metalRingBrush, radius = 4.dp.toPx(), center = centerFlash)
        drawCircle(
            brush = Brush.radialGradient(listOf(Color(0xFFFEF08A), Color(0xFFF59E0B)), center = centerFlash, radius = 3.5.dp.toPx()),
            radius = 3.2.dp.toPx(),
            center = centerFlash
        )
        if (centerLaser != null) {
            drawCircle(color = Color(0xFF0F172A), radius = 2.8.dp.toPx(), center = centerLaser)
            drawCircle(color = Color(0xFFEF4444).copy(alpha = 0.85f), radius = 1.1.dp.toPx(), center = centerLaser)
        }
    }

    val isPeriscopeCam = camera.contains("Periskop") || camera.contains("200MP") || camera.contains("1 İnç")

    when (cameraBumpStyle) {
        "Dairesel Halo" -> {
            val haloRadius = 38.dp.toPx()
            val haloCenter = Offset(size.width / 2, 70.dp.toPx())

            drawCircle(color = Color.Black.copy(alpha = 0.45f), radius = haloRadius + 3.dp.toPx(), center = Offset(haloCenter.x, haloCenter.y + 2.dp.toPx()))
            drawCircle(brush = metalRingBrush, radius = haloRadius + 1.8.dp.toPx(), center = haloCenter)
            drawCircle(brush = camPlateBrush, radius = haloRadius, center = haloCenter)
            drawCircle(color = Color.White.copy(alpha = 0.15f), radius = haloRadius - 2.dp.toPx(), center = haloCenter, style = Stroke(width = 1.dp.toPx()))

            val lensDist = 15.dp.toPx()
            val lensR = 7.dp.toPx()
            drawOpticalLens(Offset(haloCenter.x, haloCenter.y - lensDist), lensR)
            drawOpticalLens(Offset(haloCenter.x + lensDist, haloCenter.y), lensR)
            drawOpticalLens(Offset(haloCenter.x, haloCenter.y + lensDist), lensR, isPeriscope = isPeriscopeCam)
            drawOpticalLens(Offset(haloCenter.x - lensDist, haloCenter.y), lensR)
            drawFlashAndSensors(haloCenter, null)
        }
        "Yatay Vizör" -> {
            val visorTop = 40.dp.toPx()
            val visorHeight = 42.dp.toPx()
            val visorRect = Size(size.width - (backMargin * 2), visorHeight)

            drawRoundRect(
                color = Color.Black.copy(alpha = 0.45f),
                topLeft = Offset(backMargin, visorTop + 2.dp.toPx()),
                size = visorRect,
                cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
            )
            drawRoundRect(
                brush = camPlateBrush,
                topLeft = Offset(backMargin, visorTop),
                size = visorRect,
                cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
            )
            drawLine(
                brush = metalRingBrush,
                start = Offset(backMargin, visorTop),
                end = Offset(size.width - backMargin, visorTop),
                strokeWidth = 1.5.dp.toPx()
            )
            drawLine(
                brush = metalRingBrush,
                start = Offset(backMargin, visorTop + visorHeight),
                end = Offset(size.width - backMargin, visorTop + visorHeight),
                strokeWidth = 1.5.dp.toPx()
            )

            val lensR = 7.dp.toPx()
            drawOpticalLens(Offset(32.dp.toPx(), visorTop + 21.dp.toPx()), lensR)
            drawOpticalLens(Offset(60.dp.toPx(), visorTop + 21.dp.toPx()), lensR)
            if (isPeriscopeCam) {
                drawOpticalLens(Offset(88.dp.toPx(), visorTop + 21.dp.toPx()), lensR, isPeriscope = true)
            }
            drawFlashAndSensors(Offset(size.width - 30.dp.toPx(), visorTop + 21.dp.toPx()), Offset(size.width - 46.dp.toPx(), visorTop + 21.dp.toPx()))
        }
        "Kare Ada" -> {
            val bumpW = 62.dp.toPx()
            val bumpH = 62.dp.toPx()
            val bumpLeft = 14.dp.toPx()
            val bumpTop = 16.dp.toPx()

            drawRoundRect(
                color = Color.Black.copy(alpha = 0.45f),
                topLeft = Offset(bumpLeft, bumpTop + 2.5.dp.toPx()),
                size = Size(bumpW, bumpH),
                cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx())
            )
            drawRoundRect(
                brush = camPlateBrush,
                topLeft = Offset(bumpLeft, bumpTop),
                size = Size(bumpW, bumpH),
                cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx())
            )
            drawRoundRect(
                brush = metalRingBrush,
                topLeft = Offset(bumpLeft, bumpTop),
                size = Size(bumpW, bumpH),
                cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx()),
                style = Stroke(width = 1.5.dp.toPx())
            )

            val lensR = 7.5.dp.toPx()
            drawOpticalLens(Offset(bumpLeft + 18.dp.toPx(), bumpTop + 18.dp.toPx()), lensR)
            drawOpticalLens(Offset(bumpLeft + 18.dp.toPx(), bumpTop + 44.dp.toPx()), lensR)
            drawOpticalLens(Offset(bumpLeft + 44.dp.toPx(), bumpTop + 31.dp.toPx()), lensR, isPeriscope = isPeriscopeCam)
            drawFlashAndSensors(Offset(bumpLeft + 44.dp.toPx(), bumpTop + 13.dp.toPx()), Offset(bumpLeft + 44.dp.toPx(), bumpTop + 49.dp.toPx()))
        }
        "Yüzen Çift Halka" -> {
            val topY = 26.dp.toPx()
            val lensX = 30.dp.toPx()
            val ringR = 14.dp.toPx()

            drawOpticalLens(Offset(lensX, topY), ringR)
            drawOpticalLens(Offset(lensX, topY + 34.dp.toPx()), ringR, isPeriscope = isPeriscopeCam)
            drawFlashAndSensors(Offset(lensX + 22.dp.toPx(), topY + 17.dp.toPx()), Offset(lensX + 22.dp.toPx(), topY + 34.dp.toPx()))
        }
        else -> {
            // Dikey Ada (Triple Studio Pro)
            val islandW = 40.dp.toPx()
            val islandH = 88.dp.toPx()
            val islandLeft = 14.dp.toPx()
            val islandTop = 16.dp.toPx()

            drawRoundRect(
                color = Color.Black.copy(alpha = 0.45f),
                topLeft = Offset(islandLeft, islandTop + 2.5.dp.toPx()),
                size = Size(islandW, islandH),
                cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx())
            )
            drawRoundRect(
                brush = camPlateBrush,
                topLeft = Offset(islandLeft, islandTop),
                size = Size(islandW, islandH),
                cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx())
            )
            drawRoundRect(
                brush = metalRingBrush,
                topLeft = Offset(islandLeft, islandTop),
                size = Size(islandW, islandH),
                cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx()),
                style = Stroke(width = 1.5.dp.toPx())
            )

            val lensR = 7.dp.toPx()
            drawOpticalLens(Offset(islandLeft + 20.dp.toPx(), islandTop + 18.dp.toPx()), lensR)
            drawOpticalLens(Offset(islandLeft + 20.dp.toPx(), islandTop + 39.dp.toPx()), lensR)
            drawOpticalLens(Offset(islandLeft + 20.dp.toPx(), islandTop + 60.dp.toPx()), lensR, isPeriscope = isPeriscopeCam)
            drawFlashAndSensors(Offset(islandLeft + 20.dp.toPx(), islandTop + 76.dp.toPx()), null)
        }
    }

    // 4. ŞİRKET LOGO EMBLEMİ
    val logoCenterX = size.width / 2
    val logoCenterY = size.height * 0.62f
    val emblemFill = if (isLightColor) Color(0xFF0F172A).copy(alpha = 0.85f) else Color.White.copy(alpha = 0.9f)
    val accentCyan = if (isLightColor) Color(0xFF0284C7) else Color(0xFF38BDF8)

    when (logoStyle) {
        "Minimal Elmas" -> {
            val dPath = Path().apply {
                moveTo(logoCenterX, logoCenterY - 12.dp.toPx())
                lineTo(logoCenterX + 11.dp.toPx(), logoCenterY)
                lineTo(logoCenterX, logoCenterY + 12.dp.toPx())
                lineTo(logoCenterX - 11.dp.toPx(), logoCenterY)
                close()
            }
            drawPath(dPath, color = emblemFill, style = Stroke(width = 2.dp.toPx()))
            val dInner = Path().apply {
                moveTo(logoCenterX, logoCenterY - 6.dp.toPx())
                lineTo(logoCenterX + 5.dp.toPx(), logoCenterY)
                lineTo(logoCenterX, logoCenterY + 6.dp.toPx())
                lineTo(logoCenterX - 5.dp.toPx(), logoCenterY)
                close()
            }
            drawPath(dInner, color = accentCyan)
        }
        "Nexus Yıldızı" -> {
            val starPath = Path().apply {
                moveTo(logoCenterX, logoCenterY - 12.dp.toPx())
                quadraticTo(logoCenterX + 2.dp.toPx(), logoCenterY - 2.dp.toPx(), logoCenterX + 12.dp.toPx(), logoCenterY)
                quadraticTo(logoCenterX + 2.dp.toPx(), logoCenterY + 2.dp.toPx(), logoCenterX, logoCenterY + 12.dp.toPx())
                quadraticTo(logoCenterX - 2.dp.toPx(), logoCenterY + 2.dp.toPx(), logoCenterX - 12.dp.toPx(), logoCenterY)
                quadraticTo(logoCenterX - 2.dp.toPx(), logoCenterY - 2.dp.toPx(), logoCenterX, logoCenterY - 12.dp.toPx())
                close()
            }
            drawPath(starPath, color = emblemFill)
            drawCircle(color = accentCyan, radius = 3.dp.toPx(), center = Offset(logoCenterX, logoCenterY))
        }
        else -> {
            drawCircle(color = emblemFill, radius = 7.dp.toPx(), center = Offset(logoCenterX - 5.dp.toPx(), logoCenterY), style = Stroke(width = 2.dp.toPx()))
            drawCircle(color = accentCyan, radius = 7.dp.toPx(), center = Offset(logoCenterX + 5.dp.toPx(), logoCenterY), style = Stroke(width = 2.dp.toPx()))
            drawCircle(color = Color.White, radius = 1.8.dp.toPx(), center = Offset(logoCenterX, logoCenterY))
        }
    }

    // Lazer Gravür Yazı Çizgileri
    drawLine(
        color = emblemFill.copy(alpha = 0.35f),
        start = Offset(logoCenterX - 20.dp.toPx(), size.height - 24.dp.toPx()),
        end = Offset(logoCenterX + 20.dp.toPx(), size.height - 24.dp.toPx()),
        strokeWidth = 1.5.dp.toPx()
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFrontDisplay(
    size: Size,
    frameStyle: String,
    material: String,
    notchStyle: String,
    display: String,
    colorHex: Long
) {
    val phoneCorner = when (frameStyle) {
        "Düz Metal Kenar" -> 28.dp.toPx()
        "Zırhlı Kesim" -> 16.dp.toPx()
        "Ultra İnce Çerçeve" -> 34.dp.toPx()
        else -> 30.dp.toPx()
    }
    
    // Ekran paneli teknolojisine göre temel çerçeve kalınlığı (Inç/Ekran boyutu hissiyatı)
    val baseBezel = when (frameStyle) {
        "Ultra İnce Çerçeve" -> 1.5.dp.toPx()
        "Zırhlı Kesim" -> 4.5.dp.toPx()
        else -> 2.5.dp.toPx()
    }
    
    val displayModifier = when {
        display.contains("TFT LCD") -> 5.5.dp.toPx() // En kalın çerçeve (Eski nesil)
        display.contains("FHD IPS") || display.contains("QHD IPS") -> 3.5.dp.toPx() // IPS ekran çerçevesi
        display.contains("Edge AMOLED") -> 1.0.dp.toPx() // Yanlar kavisli, çok ince
        display.contains("Katlanabilir") -> 2.5.dp.toPx() // Katlanabilir dudak payı
        display.contains("Tandem OLED") || display.contains("LTPO 3.0") -> 0.5.dp.toPx() // Ultra ince çerçevesiz
        display.contains("Holografik") -> (-1.0).dp.toPx() // Çerçeve yok
        else -> 1.8.dp.toPx() // Standart OLED
    }
    
    val bezelSize = (baseBezel + displayModifier).coerceAtLeast(0.5.dp.toPx())
    
    // Eski LCD/IPS panellerde bulunan "Alt Çene" (Chin) kalınlığı
    val bottomChin = if (display.contains("LCD") || display.contains("IPS")) 5.dp.toPx() else 0f

    // Dış Metal Kasa Çerçevesi (Glossy edge)
    val frameBrush = Brush.linearGradient(
        listOf(Color(0xFFCBD5E1), Color(0xFF64748B), Color(0xFF1E293B), Color(0xFF0F172A)),
        start = Offset(0f, 0f), end = Offset(size.width, size.height)
    )
    drawRoundRect(
        brush = frameBrush,
        size = size,
        cornerRadius = CornerRadius(phoneCorner, phoneCorner)
    )

    // Inner bevel shadow for realistic depth
    drawRoundRect(
        color = Color.Black.copy(alpha = 0.5f),
        topLeft = Offset(1.5.dp.toPx(), 1.5.dp.toPx()),
        size = Size(size.width - 3.dp.toPx(), size.height - 3.dp.toPx()),
        cornerRadius = CornerRadius(phoneCorner - 1.dp.toPx(), phoneCorner - 1.dp.toPx())
    )

    val screenRect = Size(size.width - (bezelSize * 2), size.height - (bezelSize * 2) - bottomChin)
    val innerCorner = (phoneCorner - bezelSize).coerceAtLeast(4.dp.toPx())

    // Siyah Ekran Çerçeve Maskesi (Camın altındaki gerçek siyah bölge)
    drawRoundRect(
        color = Color(0xFF000000),
        topLeft = Offset(bezelSize * 0.5f, bezelSize * 0.5f),
        size = Size(size.width - bezelSize, size.height - bezelSize),
        cornerRadius = CornerRadius(innerCorner, innerCorner)
    )

    // EKRAN PANELİ (Aurora / Nebula Duvar Kağıdı)
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF030712),
                Color(0xFF0F172A),
                Color(0xFF1E1B4B),
                Color(0xFF0E7490),
                Color(0xFF020617)
            )
        ),
        topLeft = Offset(bezelSize, bezelSize),
        size = screenRect,
        cornerRadius = CornerRadius(innerCorner, innerCorner)
    )

    // Dinamik Ekran Yansımaları ve Işık Halkaları (Premium Görünüm)
    drawCircle(
        brush = Brush.radialGradient(
            listOf(Color(0xFF38BDF8).copy(alpha = 0.65f), Color(0xFF818CF8).copy(alpha = 0.25f), Color.Transparent),
            center = Offset(size.width * 0.3f, size.height * 0.35f), radius = 80.dp.toPx()
        ),
        radius = 80.dp.toPx(),
        center = Offset(size.width * 0.3f, size.height * 0.35f)
    )
    drawCircle(
        brush = Brush.radialGradient(
            listOf(Color(0xFFF43F5E).copy(alpha = 0.5f), Color(0xFF9333EA).copy(alpha = 0.2f), Color.Transparent),
            center = Offset(size.width * 0.75f, size.height * 0.65f), radius = 75.dp.toPx()
        ),
        radius = 75.dp.toPx(),
        center = Offset(size.width * 0.75f, size.height * 0.65f)
    )

    // Status Bar (Saat, Pil, Sinyal)
    drawRoundRect(
        color = Color.White.copy(alpha = 0.95f),
        topLeft = Offset(bezelSize + 12.dp.toPx(), bezelSize + 8.dp.toPx()),
        size = Size(18.dp.toPx(), 4.dp.toPx()),
        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
    )
    drawRoundRect(
        color = Color.White.copy(alpha = 0.85f),
        topLeft = Offset(size.width - bezelSize - 20.dp.toPx(), bezelSize + 7.5.dp.toPx()),
        size = Size(12.dp.toPx(), 6.dp.toPx()),
        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
        style = Stroke(width = 1.dp.toPx())
    )
    drawRoundRect(
        color = Color(0xFF22C55E),
        topLeft = Offset(size.width - bezelSize - 18.dp.toPx(), bezelSize + 8.8.dp.toPx()),
        size = Size(7.dp.toPx(), 3.2.dp.toPx()),
        cornerRadius = CornerRadius(1.dp.toPx(), 1.dp.toPx())
    )

    // Çentik / Dinamik Ada
    when (notchStyle) {
        "Dinamik Ada / Hap" -> {
            val islandW = 44.dp.toPx()
            val islandH = 14.dp.toPx()
            val islandLeft = (size.width / 2) - (islandW / 2)
            val islandTop = bezelSize + 4.dp.toPx()

            drawRoundRect(
                color = Color.Black,
                topLeft = Offset(islandLeft, islandTop),
                size = Size(islandW, islandH),
                cornerRadius = CornerRadius(7.dp.toPx(), 7.dp.toPx())
            )
            drawRoundRect(
                color = Color(0xFF334155).copy(alpha = 0.4f),
                topLeft = Offset(islandLeft, islandTop),
                size = Size(islandW, islandH),
                cornerRadius = CornerRadius(7.dp.toPx(), 7.dp.toPx()),
                style = Stroke(width = 0.8.dp.toPx())
            )
            drawCircle(
                brush = Brush.radialGradient(listOf(Color(0xFF0284C7), Color(0xFF0B192C)), center = Offset((size.width / 2) + 12.dp.toPx(), islandTop + 7.dp.toPx()), radius = 3.dp.toPx()),
                radius = 3.dp.toPx(),
                center = Offset((size.width / 2) + 12.dp.toPx(), islandTop + 7.dp.toPx())
            )
        }
        "Klasik Çentik" -> {
            val notchW = 56.dp.toPx()
            val notchH = 14.dp.toPx()
            val notchLeft = (size.width / 2) - (notchW / 2)
            drawRoundRect(
                color = Color.Black,
                topLeft = Offset(notchLeft, bezelSize - 1.dp.toPx()),
                size = Size(notchW, notchH),
                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )
        }
        "Görünmez Ekran Altı" -> {
            drawCircle(
                brush = Brush.radialGradient(listOf(Color(0xFF38BDF8).copy(alpha = 0.15f), Color.Transparent), center = Offset(size.width / 2, bezelSize + 10.dp.toPx()), radius = 4.dp.toPx()),
                radius = 4.dp.toPx(),
                center = Offset(size.width / 2, bezelSize + 10.dp.toPx())
            )
        }
        else -> {
            // Punch-hole (Nokta Delik)
            val holeCenter = Offset(size.width / 2, bezelSize + 10.dp.toPx())
            drawCircle(color = Color.Black, radius = 4.2.dp.toPx(), center = holeCenter)
            drawCircle(
                brush = Brush.radialGradient(listOf(Color(0xFF38BDF8), Color(0xFF0284C7), Color(0xFF030712)), center = holeCenter, radius = 2.8.dp.toPx()),
                radius = 2.5.dp.toPx(),
                center = holeCenter
            )
        }
    }

    // Kilit Ekranı Saat & Widget Paneli
    val widgetTop = 50.dp.toPx()
    drawRoundRect(
        color = Color.White.copy(alpha = 0.18f),
        topLeft = Offset((size.width / 2) - 34.dp.toPx(), widgetTop),
        size = Size(68.dp.toPx(), 26.dp.toPx()),
        cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
    )
    drawRoundRect(
        color = Color.White.copy(alpha = 0.95f),
        topLeft = Offset((size.width / 2) - 22.dp.toPx(), widgetTop + 6.dp.toPx()),
        size = Size(44.dp.toPx(), 7.dp.toPx()),
        cornerRadius = CornerRadius(2.5.dp.toPx(), 2.5.dp.toPx())
    )

    // Alt Dock Çubuğu & İkonlar
    val dockY = size.height - bezelSize - bottomChin - 46.dp.toPx()
    drawRoundRect(
        color = Color.White.copy(alpha = 0.2f),
        topLeft = Offset(bezelSize + 10.dp.toPx(), dockY - 4.dp.toPx()),
        size = Size(screenRect.width - 20.dp.toPx(), 36.dp.toPx()),
        cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx())
    )
    val dockColors = listOf(Color(0xFF22C55E), Color(0xFF3B82F6), Color(0xFFEC4899), Color(0xFFF59E0B))
    val iconSpacing = (screenRect.width - 20.dp.toPx() - (dockColors.size * 22.dp.toPx())) / (dockColors.size + 1)
    
    dockColors.forEachIndexed { idx, dCol ->
        val dX = (bezelSize + 10.dp.toPx()) + iconSpacing + (idx * (22.dp.toPx() + iconSpacing))
        drawRoundRect(
            brush = Brush.linearGradient(listOf(dCol, dCol.copy(alpha = 0.8f))),
            topLeft = Offset(dX, dockY + 3.dp.toPx()),
            size = Size(22.dp.toPx(), 22.dp.toPx()),
            cornerRadius = CornerRadius(7.dp.toPx(), 7.dp.toPx())
        )
    }

    // Home Bar
    drawRoundRect(
        color = Color.White.copy(alpha = 0.9f),
        topLeft = Offset((size.width / 2) - 24.dp.toPx(), size.height - bezelSize - bottomChin - 6.dp.toPx()),
        size = Size(48.dp.toPx(), 3.dp.toPx()),
        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
    )

    // Ultra Gerçekçi Cam Yansıması
    val glassHighlight = Path().apply {
        moveTo(bezelSize + 8.dp.toPx(), bezelSize)
        lineTo(bezelSize + 60.dp.toPx(), bezelSize)
        lineTo(bezelSize, bezelSize + 120.dp.toPx())
        lineTo(bezelSize, bezelSize + 40.dp.toPx())
        close()
    }
    drawPath(
        glassHighlight,
        brush = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.22f), Color.Transparent))
    )
    
    // Kenar Yansımaları (Edge glare)
    drawRoundRect(
        brush = Brush.horizontalGradient(
            listOf(Color.White.copy(alpha = 0.3f), Color.Transparent, Color.Transparent, Color.White.copy(alpha = 0.1f))
        ),
        topLeft = Offset(bezelSize, bezelSize),
        size = screenRect,
        cornerRadius = CornerRadius(innerCorner, innerCorner),
        style = Stroke(width = 1.2.dp.toPx())
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSideAndPortChassis(
    size: Size,
    material: String,
    chargingPort: String,
    cellularNetwork: String,
    colorHex: Long,
    frameStyle: String
) {
    val baseColor = Color(colorHex)
    val metalBrush = when (material) {
        "Titanyum" -> Brush.linearGradient(
            listOf(Color(0xFFF1F5F9), Color(0xFF94A3B8), Color(0xFF475569), Color(0xFFCBD5E1))
        )
        "Plastik" -> Brush.linearGradient(listOf(Color(0xFF64748B), Color(0xFF334155)))
        else -> Brush.linearGradient(listOf(Color(0xFFFFFFFF), Color(0xFFCBD5E1), Color(0xFF64748B)))
    }

    // 1. ÜST PANEL: YAN PROFİL (DÜĞMELER & ANTEN ÇİZGİLERİ)
    val sideProfileY = 35.dp.toPx()
    val sideHeight = 24.dp.toPx()
    val sideWidth = size.width - 24.dp.toPx()
    val sideLeft = 12.dp.toPx()

    // Yan Kasa Gövdesi
    drawRoundRect(
        brush = metalBrush,
        topLeft = Offset(sideLeft, sideProfileY),
        size = Size(sideWidth, sideHeight),
        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
    )
    drawRoundRect(
        color = Color.White.copy(alpha = 0.45f),
        topLeft = Offset(sideLeft, sideProfileY),
        size = Size(sideWidth, sideHeight),
        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
        style = Stroke(width = 1.dp.toPx())
    )

    // CNC Anten Bantları
    drawLine(color = Color(0xFF0F172A), start = Offset(sideLeft + 25.dp.toPx(), sideProfileY), end = Offset(sideLeft + 25.dp.toPx(), sideProfileY + sideHeight), strokeWidth = 2.dp.toPx())
    drawLine(color = Color(0xFF0F172A), start = Offset(sideLeft + sideWidth - 25.dp.toPx(), sideProfileY), end = Offset(sideLeft + sideWidth - 25.dp.toPx(), sideProfileY + sideHeight), strokeWidth = 2.dp.toPx())

    // Ses Açma / Kısma Tuşları (Yanda)
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(sideLeft + 45.dp.toPx(), sideProfileY - 2.5.dp.toPx()),
        size = Size(28.dp.toPx(), 3.dp.toPx()),
        cornerRadius = CornerRadius(1.dp.toPx(), 1.dp.toPx())
    )
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(sideLeft + 80.dp.toPx(), sideProfileY - 2.5.dp.toPx()),
        size = Size(28.dp.toPx(), 3.dp.toPx()),
        cornerRadius = CornerRadius(1.dp.toPx(), 1.dp.toPx())
    )

    // SIM / eSIM Yuvası & İğne Deliği
    drawRoundRect(
        color = Color(0xFF0F172A).copy(alpha = 0.4f),
        topLeft = Offset(sideLeft + 130.dp.toPx(), sideProfileY + 8.dp.toPx()),
        size = Size(32.dp.toPx(), 8.dp.toPx()),
        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
        style = Stroke(width = 0.8.dp.toPx())
    )
    drawCircle(color = Color(0xFF0F172A), radius = 1.2.dp.toPx(), center = Offset(sideLeft + 124.dp.toPx(), sideProfileY + 12.dp.toPx()))

    // 2. ALT PANEL: ŞARJ PORTU & HOPARLÖR IZGARALARI (BOTTOM EDGE HERO)
    val bottomEdgeY = 160.dp.toPx()
    val bottomEdgeHeight = 52.dp.toPx()

    drawRoundRect(
        brush = metalBrush,
        topLeft = Offset(sideLeft, bottomEdgeY),
        size = Size(sideWidth, bottomEdgeHeight),
        cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
    )
    drawRoundRect(
        color = Color.White.copy(alpha = 0.5f),
        topLeft = Offset(sideLeft, bottomEdgeY),
        size = Size(sideWidth, bottomEdgeHeight),
        cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
        style = Stroke(width = 1.2.dp.toPx())
    )

    val portCenter = Offset(size.width / 2, bottomEdgeY + (bottomEdgeHeight / 2))

    // ŞARJ PORTU ÇİZİMİ
    when {
        chargingPort.contains("Thunderbolt") -> {
            // Thunderbolt / USB-C 40Gbps Simetrik Port + ⚡ Şimşek Simgesi
            drawRoundRect(
                color = Color(0xFF090D16),
                topLeft = Offset(portCenter.x - 22.dp.toPx(), portCenter.y - 7.dp.toPx()),
                size = Size(44.dp.toPx(), 14.dp.toPx()),
                cornerRadius = CornerRadius(7.dp.toPx(), 7.dp.toPx())
            )
            drawRoundRect(
                brush = metalBrush,
                topLeft = Offset(portCenter.x - 22.dp.toPx(), portCenter.y - 7.dp.toPx()),
                size = Size(44.dp.toPx(), 14.dp.toPx()),
                cornerRadius = CornerRadius(7.dp.toPx(), 7.dp.toPx()),
                style = Stroke(width = 1.dp.toPx())
            )
            // Orta Kontak Dili (Internal Tongue)
            drawRoundRect(
                color = Color(0xFF38BDF8),
                topLeft = Offset(portCenter.x - 14.dp.toPx(), portCenter.y - 1.8.dp.toPx()),
                size = Size(28.dp.toPx(), 3.6.dp.toPx()),
                cornerRadius = CornerRadius(1.8.dp.toPx(), 1.8.dp.toPx())
            )
        }
        chargingPort.contains("USB-C") -> {
            // Standart / 3.1 / 3.2 USB-C Portu
            drawRoundRect(
                color = Color(0xFF090D16),
                topLeft = Offset(portCenter.x - 20.dp.toPx(), portCenter.y - 6.5.dp.toPx()),
                size = Size(40.dp.toPx(), 13.dp.toPx()),
                cornerRadius = CornerRadius(6.5.dp.toPx(), 6.5.dp.toPx())
            )
            drawRoundRect(
                brush = metalBrush,
                topLeft = Offset(portCenter.x - 20.dp.toPx(), portCenter.y - 6.5.dp.toPx()),
                size = Size(40.dp.toPx(), 13.dp.toPx()),
                cornerRadius = CornerRadius(6.5.dp.toPx(), 6.5.dp.toPx()),
                style = Stroke(width = 1.dp.toPx())
            )
            drawRoundRect(
                color = Color(0xFFE2E8F0),
                topLeft = Offset(portCenter.x - 12.dp.toPx(), portCenter.y - 1.5.dp.toPx()),
                size = Size(24.dp.toPx(), 3.dp.toPx()),
                cornerRadius = CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx())
            )
        }
        chargingPort.contains("USB 3.0") -> {
            // USB 3.0 Micro-B Çift Yuvalı Port
            drawRoundRect(
                color = Color(0xFF090D16),
                topLeft = Offset(portCenter.x - 22.dp.toPx(), portCenter.y - 6.dp.toPx()),
                size = Size(44.dp.toPx(), 12.dp.toPx()),
                cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
            )
            drawRoundRect(
                color = Color(0xFF0284C7),
                topLeft = Offset(portCenter.x - 18.dp.toPx(), portCenter.y - 3.dp.toPx()),
                size = Size(20.dp.toPx(), 6.dp.toPx()),
                cornerRadius = CornerRadius(1.dp.toPx(), 1.dp.toPx())
            )
            drawRoundRect(
                color = Color(0xFF0284C7),
                topLeft = Offset(portCenter.x + 4.dp.toPx(), portCenter.y - 3.dp.toPx()),
                size = Size(14.dp.toPx(), 6.dp.toPx()),
                cornerRadius = CornerRadius(1.dp.toPx(), 1.dp.toPx())
            )
        }
        else -> {
            // Micro-USB 2.0 Trapez Port
            val mPath = Path().apply {
                moveTo(portCenter.x - 16.dp.toPx(), portCenter.y - 5.dp.toPx())
                lineTo(portCenter.x + 16.dp.toPx(), portCenter.y - 5.dp.toPx())
                lineTo(portCenter.x + 11.dp.toPx(), portCenter.y + 5.dp.toPx())
                lineTo(portCenter.x - 11.dp.toPx(), portCenter.y + 5.dp.toPx())
                close()
            }
            drawPath(mPath, color = Color(0xFF090D16))
            drawPath(mPath, brush = metalBrush, style = Stroke(width = 1.dp.toPx()))
        }
    }

    // Hoparlör & Mikrofon CNC Delikleri (Sağ ve Sol)
    val speakerRadius = 2.2.dp.toPx()
    // Sol Mikrofon Delikleri (3 adet)
    for (i in 0..2) {
        val sX = portCenter.x - 36.dp.toPx() - (i * 8.dp.toPx())
        drawCircle(color = Color(0xFF090D16), radius = speakerRadius, center = Offset(sX, portCenter.y))
    }
    // Sağ Hoparlör Izgarası (5 adet)
    for (i in 0..4) {
        val sX = portCenter.x + 36.dp.toPx() + (i * 8.dp.toPx())
        drawCircle(color = Color(0xFF090D16), radius = speakerRadius, center = Offset(sX, portCenter.y))
    }
}

@Composable
fun SelectionGroup(
    title: String,
    options: List<ComponentOption>,
    selectedOption: String,
    unlockedTech: List<String>,
    onOptionSelected: (String) -> Unit,
    onLockedClick: (String) -> Unit = {}
) {
    val selectedItem = options.find { it.name == selectedOption }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = Slate800,
                fontSize = 13.sp
            )
            if (selectedItem != null && selectedItem.cost > 0) {
                Text(
                    text = "+$${selectedItem.cost}",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 11.sp
                )
            }
        }

        // Subtitle info for the currently selected item
        if (selectedItem?.desc != null) {
            Text(
                text = "✓ ${selectedItem.name}: ${selectedItem.desc}",
                fontSize = 11.sp,
                color = Slate500,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }

        // Compact 2-column paired layout for neatness and minimal height
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val chunkedOptions = options.chunked(2)
            chunkedOptions.forEach { rowPair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowPair.forEach { option ->
                        val isUnlocked = option.requiredTech == null || unlockedTech.contains(option.requiredTech)
                        val isSelected = option.name == selectedOption

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = when {
                                !isUnlocked -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                isSelected -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.surface
                            },
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = when {
                                    isSelected -> MaterialTheme.colorScheme.primary
                                    !isUnlocked -> Slate200.copy(alpha = 0.6f)
                                    else -> Slate200
                                }
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clickable {
                                    if (isUnlocked) {
                                        onOptionSelected(option.name)
                                    } else {
                                        onLockedClick(option.requiredTech ?: "Ar-Ge")
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                    modifier = Modifier.weight(1f, fill = false)
                                ) {
                                    if (!isUnlocked) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Kilitli",
                                            tint = Slate400,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                    Text(
                                        text = option.name,
                                        color = when {
                                            !isUnlocked -> Slate400
                                            isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                                            else -> Slate800
                                        },
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 11.5.sp,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                }

                                if (option.cost > 0 && isUnlocked) {
                                    Text(
                                        text = "+$${option.cost}",
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Slate500,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 10.sp
                                    )
                                } else if (!isUnlocked) {
                                    Text(
                                        text = "Ar-Ge",
                                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    }

                    // If odd number of items, insert spacer for alignment
                    if (rowPair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
