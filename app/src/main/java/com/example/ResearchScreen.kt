/**
 * Araştırma (Research) ekranı: teknoloji ağacı, aktif Ar-Ge kuyruğu ve
 * kilidi açılan teknolojilerin ([unlockedTech]) görüntülenmesini sağlar.
 */
package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import com.example.ui.Button3D
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.GameViewModel

data class TechNode(
    val id: String, 
    val name: String, 
    val description: String, 
    val cost: Long, 
    val yearAvailable: Int,
    val category: String = "Genel"
)

val allTechNodes = listOf(
    // EKRAN & CAM
    TechNode("FHD IPS Panel Entegrasyonu", "FHD IPS Panel Entegrasyonu", "Yüksek çözünürlüklü keskin IPS ekran panelleri entegrasyonu.", 300000, 2011, "Ekran & Cam"),
    TechNode("2K QHD Panel Entegrasyonu", "2K QHD Panel Entegrasyonu", "Ultra netlik sunan 2K QHD ekran entegrasyonu.", 700000, 2013, "Ekran & Cam"),
    TechNode("Kavisli AMOLED Panel Entegrasyonu", "Kavisli AMOLED Panel Entegrasyonu", "Derin siyahlar ve kavisli şık tasarım sunan AMOLED paneller.", 1200000, 2014, "Ekran & Cam"),
    TechNode("Çerçevesiz 18:9 OLED Panel Entegrasyonu", "Çerçevesiz 18:9 OLED Panel Entegrasyonu", "İnce çerçeveli 18:9 oranında OLED paneller.", 2000000, 2017, "Ekran & Cam"),
    TechNode("120Hz LTPO OLED Panel Entegrasyonu", "120Hz LTPO OLED Panel Entegrasyonu", "Akıcı 120Hz adaptif yenileme hızlı ekran entegrasyonu.", 3500000, 2021, "Ekran & Cam"),
    TechNode("Katlanabilir OLED Panel Entegrasyonu", "Katlanabilir OLED Panel Entegrasyonu", "Gelecek nesil esnek katlanabilir OLED paneller.", 5000000, 2019, "Ekran & Cam"),
    TechNode("144Hz LTPO 3.0 Panel Entegrasyonu", "144Hz LTPO 3.0 Panel Entegrasyonu", "Ultra akıcı 144Hz oyuncu ve amiral gemisi paneller.", 7000000, 2023, "Ekran & Cam"),
    TechNode("240Hz Tandem OLED Panel Entegrasyonu", "240Hz Tandem OLED Panel Entegrasyonu", "Çift katmanlı yüksek parlaklıkta Tandem OLED paneller.", 10000000, 2025, "Ekran & Cam"),
    TechNode("Holografik 3D Panel Entegrasyonu", "Holografik 3D Panel Entegrasyonu", "Gözlüksüz 3D görüntü oluşturan deneysel ekran.", 15000000, 2024, "Ekran & Cam"),

    TechNode("Gorilla Glass 2 Entegrasyonu", "Gorilla Glass 2 Entegrasyonu", "İnce ve çizilmelere dayanıklı 2. nesil koruma camı.", 200000, 2011, "Ekran & Cam"),
    TechNode("Gorilla Glass 3 Entegrasyonu", "Gorilla Glass 3 Entegrasyonu", "Derin çiziklere karşı dirençli 3. nesil koruma camı.", 400000, 2013, "Ekran & Cam"),
    TechNode("Gorilla Glass 4 Entegrasyonu", "Gorilla Glass 4 Entegrasyonu", "Sert yüzeylere düşmelere dayanıklı 4. nesil cam.", 800000, 2015, "Ekran & Cam"),
    TechNode("Gorilla Glass 5 Entegrasyonu", "Gorilla Glass 5 Entegrasyonu", "1.2 metreden düşmelere dayanıklı 5. nesil koruyucu cam.", 1500000, 2017, "Ekran & Cam"),
    TechNode("Gorilla Glass Victus Entegrasyonu", "Gorilla Glass Victus Entegrasyonu", "Çizilme ve düşme direncini birleştiren Victus camı.", 2500000, 2019, "Ekran & Cam"),
    TechNode("Ceramic Shield Zırh Entegrasyonu", "Ceramic Shield Zırh Entegrasyonu", "Kırılmaya karşı 4 kat dayanıklı seramik kristal zırh.", 3500000, 2021, "Ekran & Cam"),
    TechNode("Gorilla Armor Zırh Entegrasyonu", "Gorilla Armor Zırh Entegrasyonu", "Yansıma önleyici ve darbelere ultra dayanıklı zırh cam.", 5000000, 2023, "Ekran & Cam"),
    TechNode("Safir Cam & Armor+ Entegrasyonu", "Safir Cam & Armor+ Entegrasyonu", "Çizilmez safir kristal ve Armor+ kaplama.", 7500000, 2025, "Ekran & Cam"),

    // RAM & BELLEK KAPASİTESİ
    TechNode("2GB RAM Kapasitesi", "2GB RAM Kapasitesi", "2GB RAM bellek kapasitesi entegrasyonu.", 200000, 2011, "RAM & Bellek"),
    TechNode("3GB RAM Kapasitesi", "3GB RAM Kapasitesi", "3GB RAM bellek kapasitesi entegrasyonu.", 350000, 2012, "RAM & Bellek"),
    TechNode("4GB RAM Kapasitesi", "4GB RAM Kapasitesi", "4GB RAM bellek kapasitesi entegrasyonu.", 600000, 2014, "RAM & Bellek"),
    TechNode("6GB RAM Kapasitesi", "6GB RAM Kapasitesi", "6GB RAM bellek kapasitesi entegrasyonu.", 1000000, 2016, "RAM & Bellek"),
    TechNode("8GB RAM Kapasitesi", "8GB RAM Kapasitesi", "8GB RAM bellek kapasitesi entegrasyonu.", 1600000, 2017, "RAM & Bellek"),
    TechNode("12GB RAM Kapasitesi", "12GB RAM Kapasitesi", "12GB RAM amiral gemisi bellek kapasitesi.", 2500000, 2019, "RAM & Bellek"),
    TechNode("16GB RAM Kapasitesi", "16GB RAM Kapasitesi", "16GB RAM ultra performans bellek modülü.", 3800000, 2021, "RAM & Bellek"),
    TechNode("24GB RAM Kapasitesi", "24GB RAM Kapasitesi", "24GB RAM yapay zeka ve oyun belleği.", 5500000, 2023, "RAM & Bellek"),
    TechNode("32GB RAM Kapasitesi", "32GB RAM Kapasitesi", "32GB RAM ekstrem iş istasyonu belleği.", 8000000, 2025, "RAM & Bellek"),

    // RAM MİMARİSİ VE TEKNOLOJİSİ
    TechNode("LPDDR2 Bellek Teknolojisi", "LPDDR2 Bellek Teknolojisi", "LPDDR2 düşük güç tüketimli mobil RAM standardı.", 180000, 2011, "RAM & Bellek"),
    TechNode("LPDDR3 Bellek Teknolojisi", "LPDDR3 Bellek Teknolojisi", "LPDDR3 yüksek bant genişlikli mobil RAM mimarisi.", 380000, 2013, "RAM & Bellek"),
    TechNode("LPDDR4 Bellek Teknolojisi", "LPDDR4 Bellek Teknolojisi", "LPDDR4 çift kanallı yüksek hızlı bellek veri yolu.", 800000, 2015, "RAM & Bellek"),
    TechNode("LPDDR4X Bellek Teknolojisi", "LPDDR4X Bellek Teknolojisi", "LPDDR4X ultra verimli ve düşük voltajlı RAM mimarisi.", 1400000, 2017, "RAM & Bellek"),
    TechNode("LPDDR5 Bellek Teknolojisi", "LPDDR5 Bellek Teknolojisi", "6400 Mbps hızında LPDDR5 yeni nesil mobil bellek.", 2400000, 2019, "RAM & Bellek"),
    TechNode("LPDDR5X Bellek Teknolojisi", "LPDDR5X Bellek Teknolojisi", "8533 Mbps hızında LPDDR5X yapay zeka destekli RAM.", 4200000, 2023, "RAM & Bellek"),
    TechNode("LPDDR6 Bellek Teknolojisi", "LPDDR6 Bellek Teknolojisi", "12800 Mbps hızında LPDDR6 kuantum destekli mobil bellek.", 7000000, 2025, "RAM & Bellek"),

    // DEPOLAMA & SD KART
    TechNode("32GB Depolama Entegrasyonu", "32GB Depolama Entegrasyonu", "32GB dahili eMMC depolama entegrasyonu.", 150000, 2011, "Depolama & SD Kart"),
    TechNode("64GB Depolama Entegrasyonu", "64GB Depolama Entegrasyonu", "64GB eMMC 5.0 dahili depolama entegrasyonu.", 300000, 2012, "Depolama & SD Kart"),
    TechNode("128GB UFS Depolama Entegrasyonu", "128GB UFS Depolama Entegrasyonu", "128GB yüksek hızlı UFS 2.0 depolama yongası.", 700000, 2015, "Depolama & SD Kart"),
    TechNode("256GB UFS Depolama Entegrasyonu", "256GB UFS Depolama Entegrasyonu", "256GB UFS 2.1 depolama ve yüksek okuma hızı.", 1300000, 2017, "Depolama & SD Kart"),
    TechNode("512GB UFS Depolama Entegrasyonu", "512GB UFS Depolama Entegrasyonu", "512GB UFS 3.0 dev depolama alanı.", 2200000, 2019, "Depolama & SD Kart"),
    TechNode("1TB UFS 4.0 Depolama Entegrasyonu", "1TB UFS 4.0 Depolama Entegrasyonu", "1TB UFS 4.0 ultra hızlı veri aktarımı ve depolama.", 3800000, 2022, "Depolama & SD Kart"),
    TechNode("2TB Ultra Depolama Entegrasyonu", "2TB Ultra Depolama Entegrasyonu", "2TB UFS 4.1 devasa yapay zeka yerel depolama alanı.", 6000000, 2024, "Depolama & SD Kart"),

    TechNode("128GB MicroSDHC Desteği", "128GB MicroSDHC Desteği", "128GB'a kadar harici hafıza kartı yuvası desteği.", 200000, 2012, "Depolama & SD Kart"),
    TechNode("512GB MicroSDXC Desteği", "512GB MicroSDXC Desteği", "512GB'a kadar UHS-I hızlı microSDXC desteği.", 500000, 2015, "Depolama & SD Kart"),
    TechNode("2TB Ultra MicroSD Desteği", "2TB Ultra MicroSD Desteği", "2TB'a kadar UHS-II yüksek hızlı microSDXC desteği.", 1200000, 2019, "Depolama & SD Kart"),
    TechNode("NM & MicroSD Express Desteği", "NM & MicroSD Express Desteği", "985 MB/s PCIe Express ve NM nano hafıza kartı desteği.", 2500000, 2022, "Depolama & SD Kart"),

    // İŞLEMCİ & ÇİP
    TechNode("Qualcomm S4 Çip Entegrasyonu", "Qualcomm S4 Çip Entegrasyonu", "Çift çekirdekli Qualcomm S4 işlemci entegrasyonu.", 300000, 2012, "İşlemci & Çip"),
    TechNode("MediaTek MT67 Çip Entegrasyonu", "MediaTek MT67 Çip Entegrasyonu", "Fiyat/performans odaklı MediaTek MT67 çip desteği.", 200000, 2012, "İşlemci & Çip"),
    TechNode("Qualcomm 801 Çip Entegrasyonu", "Qualcomm 801 Çip Entegrasyonu", "Dört çekirdekli amiral gemisi Snapdragon 801 çip.", 800000, 2014, "İşlemci & Çip"),
    TechNode("Intel Atom X5 Çip Entegrasyonu", "Intel Atom X5 Çip Entegrasyonu", "x86 mimarili Intel Atom mobil işlemci entegrasyonu.", 600000, 2014, "İşlemci & Çip"),
    TechNode("Qualcomm 820 Çip Entegrasyonu", "Qualcomm 820 Çip Entegrasyonu", "64-bit Kyro çekirdekli Snapdragon 820 işlemci.", 1500000, 2016, "İşlemci & Çip"),
    TechNode("MediaTek Helio Çip Entegrasyonu", "MediaTek Helio Çip Entegrasyonu", "10 çekirdekli MediaTek Helio performans çipi.", 1100000, 2016, "İşlemci & Çip"),
    TechNode("Qualcomm 845 Çip Entegrasyonu", "Qualcomm 845 Çip Entegrasyonu", "10nm Snapdragon 845 amiral gemisi işlemci.", 2500000, 2018, "İşlemci & Çip"),
    TechNode("MediaTek G90 Çip Entegrasyonu", "MediaTek G90 Çip Entegrasyonu", "Oyuncu odaklı MediaTek Helio G90 çip desteği.", 1800000, 2018, "İşlemci & Çip"),
    TechNode("Qualcomm 865 Çip Entegrasyonu", "Qualcomm 865 Çip Entegrasyonu", "Snapdragon 865 5G destekli güç yongası.", 4000000, 2020, "İşlemci & Çip"),
    TechNode("MediaTek D800 Çip Entegrasyonu", "MediaTek D800 Çip Entegrasyonu", "Dimensity 800 serisi dengeli 5G işlemci.", 3000000, 2020, "İşlemci & Çip"),
    TechNode("Qualcomm 8 Gen 1 Çip Entegrasyonu", "Qualcomm 8 Gen 1 Çip Entegrasyonu", "4nm mimarili Snapdragon 8 Gen 1 ultra işlemci.", 6000000, 2022, "İşlemci & Çip"),
    TechNode("MediaTek D9000 Çip Entegrasyonu", "MediaTek D9000 Çip Entegrasyonu", "Dimensity 9000 amiral gemisi mobil yonga.", 5000000, 2022, "İşlemci & Çip"),
    TechNode("Qualcomm 8 Gen 3 Çip Entegrasyonu", "Qualcomm 8 Gen 3 Çip Entegrasyonu", "Yapay zeka hızlandırıcılı Snapdragon 8 Gen 3.", 9000000, 2024, "İşlemci & Çip"),
    TechNode("MediaTek D9300 Çip Entegrasyonu", "MediaTek D9300 Çip Entegrasyonu", "Tüm büyük çekirdekli Dimensity 9300 çip.", 8000000, 2024, "İşlemci & Çip"),
    TechNode("Özel Yonga Seti Entegrasyonu", "Özel Yonga Seti Entegrasyonu", "Şirketinizin kendi tasarladığı in-house mobil yonga.", 12000000, 2015, "İşlemci & Çip"),
    TechNode("Kuantum İşlemci Entegrasyonu", "Kuantum İşlemci Entegrasyonu", "Sonsuz işlem gücüne sahip kuantum mobil çip.", 25000000, 2026, "İşlemci & Çip"),

    // BATARYA & ŞARJ
    TechNode("3100 mAh Batarya Entegrasyonu", "3100 mAh Batarya Entegrasyonu", "3100 mAh kapasiteli batarya modülü entegrasyonu.", 200000, 2011, "Batarya & Şarj"),
    TechNode("3200 mAh Batarya Entegrasyonu", "3200 mAh Batarya Entegrasyonu", "3200 mAh kapasiteli uzun ömürlü batarya hücresi.", 300000, 2013, "Batarya & Şarj"),
    TechNode("3600 mAh Batarya Entegrasyonu", "3600 mAh Batarya Entegrasyonu", "3600 mAh yoğun batarya hücresi entegrasyonu.", 500000, 2015, "Batarya & Şarj"),
    TechNode("4000 mAh Batarya Entegrasyonu", "4000 mAh Batarya Entegrasyonu", "4000 mAh tam gün kullanım sunan batarya.", 900000, 2017, "Batarya & Şarj"),
    TechNode("4500 mAh Batarya Entegrasyonu", "4500 mAh Batarya Entegrasyonu", "4500 mAh dev batarya hücresi entegrasyonu.", 1500000, 2019, "Batarya & Şarj"),
    TechNode("5000 mAh Batarya Entegrasyonu", "5000 mAh Batarya Entegrasyonu", "5000 mAh amiral gemisi batarya modülü.", 2200000, 2021, "Batarya & Şarj"),
    TechNode("5500 mAh Batarya Entegrasyonu", "5500 mAh Batarya Entegrasyonu", "5500 mAh yüksek yoğunluklu batarya hücresi.", 3200000, 2023, "Batarya & Şarj"),
    TechNode("7000 mAh Dev Batarya Entegrasyonu", "7000 mAh Dev Batarya Entegrasyonu", "7000 mAh 3 gün ömürlü dev batarya hücresi.", 5000000, 2025, "Batarya & Şarj"),

    TechNode("10W Hızlı Şarj Entegrasyonu", "10W Hızlı Şarj Entegrasyonu", "10W temel hızlı şarj devresi entegrasyonu.", 200000, 2011, "Batarya & Şarj"),
    TechNode("15W QuickCharge Entegrasyonu", "15W QuickCharge Entegrasyonu", "15W Qualcomm QuickCharge protokol desteği.", 400000, 2013, "Batarya & Şarj"),
    TechNode("20W Li-Po & 5W Kablosuz Şarj Entegrasyonu", "20W Li-Po & 5W Kablosuz Şarj Entegrasyonu", "Li-Po batarya ve Qi kablosuz şarj desteği.", 800000, 2015, "Batarya & Şarj"),
    TechNode("25W Li-Po & 10W Kablosuz Şarj Entegrasyonu", "25W Li-Po & 10W Kablosuz Şarj Entegrasyonu", "25W kablolu ve 10W kablosuz hızlı şarj.", 1400000, 2017, "Batarya & Şarj"),
    TechNode("65W Çift Hücre & Ters Şarj Entegrasyonu", "65W Çift Hücre & Ters Şarj Entegrasyonu", "30 dakikada şarj ve kablosuz kulaklık şarjı.", 2500000, 2019, "Batarya & Şarj"),
    TechNode("120W Çift Hücre & 50W Kablosuz Şarj Entegrasyonu", "120W Çift Hücre & 50W Kablosuz Şarj Entegrasyonu", "15 dakikada %100 dolduran 120W ultra şarj.", 4000000, 2021, "Batarya & Şarj"),
    TechNode("200W+ Silisyum-Karbon Şarj Entegrasyonu", "200W+ Silisyum-Karbon Şarj Entegrasyonu", "Silisyum-karbon anotlu ultra hızlı şarj.", 6000000, 2023, "Batarya & Şarj"),
    TechNode("100W Katı Hal Batarya Entegrasyonu", "100W Katı Hal Batarya Entegrasyonu", "Alev almaz güvenli katı hal batarya hücresi.", 7500000, 2022, "Batarya & Şarj"),
    TechNode("240W Katı Hal & Qi2 Şarj Entegrasyonu", "240W Katı Hal & Qi2 Şarj Entegrasyonu", "Magnetik Qi2 ve 240W rekor kıran şarj hızı.", 10000000, 2025, "Batarya & Şarj"),

    // KAMERA & SES
    TechNode("13MP HD Kamera Entegrasyonu", "13MP HD Kamera Entegrasyonu", "13MP çözünürlüklü 1080p video kayıtlı kamera.", 250000, 2011, "Kamera & Ses"),
    TechNode("20MP OIS & 4K Kamera Entegrasyonu", "20MP OIS & 4K Kamera Entegrasyonu", "Optik imaj sabitlemeli 4K video çeken kamera.", 600000, 2013, "Kamera & Ses"),
    TechNode("Çift Kamera Sistemi Entegrasyonu", "Çift Kamera Sistemi Entegrasyonu", "Portre modu sunan çift lensli kamera modülü.", 1200000, 2015, "Kamera & Ses"),
    TechNode("Üçlü Kamera Sistemi Entegrasyonu", "Üçlü Kamera Sistemi Entegrasyonu", "Geniş, ultra geniş ve telefoto üçlü sensör.", 2200000, 2017, "Kamera & Ses"),
    TechNode("108MP Periskop Kamera Entegrasyonu", "108MP Periskop Kamera Entegrasyonu", "108MP 100x zoom periskop telefoto kamera.", 3800000, 2019, "Kamera & Ses"),
    TechNode("Görünmez Ekran Altı Kamera Entegrasyonu", "Görünmez Ekran Altı Kamera Entegrasyonu", "Ekranda delik bırakmayan ekran altı kamera.", 5000000, 2021, "Kamera & Ses"),
    TechNode("200MP 1-İnç Sensör Entegrasyonu", "200MP 1-İnç Sensör Entegrasyonu", "DevASA 1-inç dev kamera sensörü entegrasyonu.", 6500000, 2021, "Kamera & Ses"),
    TechNode("3D Mekansal Video & GenAI Kamera Entegrasyonu", "3D Mekansal Video & GenAI Kamera Entegrasyonu", "3D mekansal video kaydı ve yapay zeka render.", 8500000, 2023, "Kamera & Ses"),
    TechNode("Donanımsal GenAI ISP Kamera Entegrasyonu", "Donanımsal GenAI ISP Kamera Entegrasyonu", "Gece çekimlerini anında netleştiren GenAI NPU.", 11000000, 2025, "Kamera & Ses"),

    TechNode("Gelişmiş Beats Mono Ses Entegrasyonu", "Gelişmiş Beats Mono Ses Entegrasyonu", "Derin bas veren Beats Audio ses çipi entegrasyonu.", 200000, 2011, "Kamera & Ses"),
    TechNode("Ön Stereo Hoparlör Entegrasyonu", "Ön Stereo Hoparlör Entegrasyonu", "Cihazın ön yüzünde çift stereo hoparlör.", 450000, 2013, "Kamera & Ses"),
    TechNode("Tip-C Çift Stereo Entegrasyonu", "Tip-C Çift Stereo Entegrasyonu", "Jak girişi olmayan gelişmiş Tip-C stereo ses.", 900000, 2015, "Kamera & Ses"),
    TechNode("Dolby Atmos Ses Entegrasyonu", "Dolby Atmos Ses Entegrasyonu", "Sinematik Dolby Atmos çevresel ses desteği.", 1600000, 2017, "Kamera & Ses"),
    TechNode("Asimetrik Güçlü Stereo Entegrasyonu", "Asimetrik Güçlü Stereo Entegrasyonu", "Yüksek desibel veren asimetrik amfi ve stereo.", 2500000, 2019, "Kamera & Ses"),
    TechNode("Kafa Takipli Uzamsal Ses Entegrasyonu", "Kafa Takipli Uzamsal Ses Entegrasyonu", "Baş hareketlerini takip eden 3D uzamsal ses.", 3800000, 2021, "Kamera & Ses"),
    TechNode("24-bit Kayıpsız Bluetooth Ses Entegrasyonu", "24-bit Kayıpsız Bluetooth Ses Entegrasyonu", "24-bit/192kHz kayıpsız kablosuz ses aktarımı.", 5500000, 2023, "Kamera & Ses"),
    TechNode("Yapay Zeka Ses Kalibrasyon Entegrasyonu", "Yapay Zeka Ses Kalibrasyon Entegrasyonu", "Ortam akustiğine göre kendini ayarlayan AI ses.", 7500000, 2025, "Kamera & Ses"),

    // BAĞLANTI & PORT & KASA
    TechNode("4G LTE Şebeke Entegrasyonu", "4G LTE Şebeke Entegrasyonu", "4G LTE yüksek hızlı mobil hücresel internet modu.", 300000, 2011, "Bağlantı & Kasa"),
    TechNode("USB 3.0 & Yüksek Hızlı Port", "USB 3.0 & Yüksek Hızlı Port", "5Gbps hızlı kablolu veri aktarımı ve şarj bağlantı noktası.", 500000, 2013, "Bağlantı & Kasa"),
    TechNode("USB-C Simetrik Port Mimarisi", "USB-C Simetrik Port Mimarisi", "Çift yönlü takılabilir modern USB-C şarj ve veri arayüzü.", 900000, 2015, "Bağlantı & Kasa"),
    TechNode("Wi-Fi 5 (ac) & Çift Bant Kablosuz", "Wi-Fi 5 (ac) & Çift Bant Kablosuz", "5GHz frekans bandında gigabit kablosuz yerel ağ bağlantısı.", 800000, 2014, "Bağlantı & Kasa"),
    TechNode("4G LTE Gelişmiş (Cat 6) Modem", "4G LTE Gelişmiş (Cat 6) Modem", "300 Mbps taşıyıcı birleştirme destekli hücresel modem.", 1200000, 2016, "Bağlantı & Kasa"),
    TechNode("USB-C 3.1 & DisplayPort Çıkışı", "USB-C 3.1 & DisplayPort Çıkışı", "10Gbps USB-C 3.1 portu ve harici monitör video çıkışı.", 1600000, 2017, "Bağlantı & Kasa"),
    TechNode("5G Sub-6 Mobil Şebeke", "5G Sub-6 Mobil Şebeke", "Yeni nesil 5G geniş kapsama alanı hücresel modem entegrasyonu.", 2800000, 2019, "Bağlantı & Kasa"),
    TechNode("Wi-Fi 6 (ax) & Bluetooth 5.2", "Wi-Fi 6 (ax) & Bluetooth 5.2", "Düşük gecikmeli Wi-Fi 6 ve kararlı Bluetooth 5.2 kablosuz ağ.", 2200000, 2019, "Bağlantı & Kasa"),
    TechNode("5G mmWave Ultra Hızlı Şebeke", "5G mmWave Ultra Hızlı Şebeke", "Ultra yüksek bant genişlikli milimetrik dalga 5G anten dizisi.", 4200000, 2020, "Bağlantı & Kasa"),
    TechNode("Wi-Fi 6E & 6GHz Frekans Çipi", "Wi-Fi 6E & 6GHz Frekans Çipi", "6GHz bandını kullanan parazitsiz ultra akıcı Wi-Fi 6E.", 3800000, 2021, "Bağlantı & Kasa"),
    TechNode("Thunderbolt 4 / USB4 Portu", "Thunderbolt 4 / USB4 Portu", "40Gbps profesyonel aktarım ve 8K görüntü aktarabilen evrensel port.", 5500000, 2023, "Bağlantı & Kasa"),
    TechNode("Wi-Fi 7 & Bluetooth 5.4 MLO", "Wi-Fi 7 & Bluetooth 5.4 MLO", "Çoklu bağlantı işlemli (MLO) 30Gbps teorik Wi-Fi 7 kablosuz çip.", 6200000, 2024, "Bağlantı & Kasa"),
    TechNode("Doğrudan Uydu Şebekesi & SOS", "Doğrudan Uydu Şebekesi & SOS", "Şebekesiz acil durumlarda alçak yörünge uydularına iki yönlü doğrudan bağlantı.", 8500000, 2025, "Bağlantı & Kasa"),

    TechNode("Alüminyum Kasa Entegrasyonu", "Alüminyum Kasa Entegrasyonu", "Hafif ve şık alüminyum gövde imalatı.", 400000, 2012, "Bağlantı & Kasa"),
    TechNode("Cam Arka Kapak Entegrasyonu", "Cam Arka Kapak Entegrasyonu", "Kablosuz şarja uygun premium cam arka kapak.", 800000, 2014, "Bağlantı & Kasa"),
    TechNode("Titanyum Alaşım Kasa Entegrasyonu", "Titanyum Alaşım Kasa Entegrasyonu", "Havacılık sınıfı hafif ve çizilmez titanyum.", 4500000, 2020, "Bağlantı & Kasa"),
    TechNode("Oyuncu Tasarım Çizgileri", "Oyuncu Tasarım Çizgileri", "RGB ışıklandırmalı ve tetik butonlu oyuncu gövdesi.", 1200000, 2016, "Bağlantı & Kasa"),
    TechNode("Zırhlı Dayanıklı Gövde", "Zırhlı Dayanıklı Gövde", "IP68 su geçirmez ve kauçuk korumalı gövde.", 1500000, 2015, "Bağlantı & Kasa"),
    TechNode("Amiral Gemisi Ailesi Segmentasyonu", "Amiral Gemisi Ailesi Segmentasyonu", "Aynı anda Standart, Pro ve Ultra amiral gemisi serileri üretebilme kabiliyeti.", 2000000, 2013, "Bağlantı & Kasa"),
    TechNode("Kuantum Seri Üretim Mimarisi", "Kuantum Seri Üretim Mimarisi", "Ultra optimize parça entegrasyonu ve sıfır hata toleransı.", 6000000, 2022, "Bağlantı & Kasa"),

    // YAZILIM & EKOSİSTEM (ÖZEL OS & YAZILIM TEKNOLOJİLERİ)
    TechNode("Özel Mobil İşletim Sistemi Mimarisi", "Özel Mobil İşletim Sistemi Mimarisi", "Şirketinize ait bağımsız mobil işletim sistemi, özel arayüz ve küresel uygulama mağazası ekosistemi geliştirme lisansı.", 100000000L, 2010, "Yazılım & Ekosistem"),
    TechNode("Açık Kaynak Topluluk & OEM Dağıtım Protokolü", "Açık Kaynak Topluluk & OEM Dağıtım Protokolü", "Diğer telefon üreticilerinin ve bağımsız geliştiricilerin sisteminizi ücretsiz kullanmasını sağlayarak pazar payını katlar.", 15000000L, 2012, "Yazılım & Ekosistem"),
    TechNode("Kapalı Kaynak Güvenlik Çekirdeği & DRM", "Kapalı Kaynak Güvenlik Çekirdeği & DRM", "Apple iOS benzeri tamamen tescilli kapalı ekosistem. Cihazlarınıza özel prestij ve yüksek mağaza komisyonu sağlar.", 25000000L, 2014, "Yazılım & Ekosistem"),
    TechNode("Yapay Zeka & Nöral Arayüz Motoru", "Yapay Zeka & Nöral Arayüz Motoru", "İşletim sistemine doğrudan entegre yerel yapay zeka asistanı ve akıllı hesaplamalı kamera motoru.", 35000000L, 2020, "Yazılım & Ekosistem")
)

@Composable
fun ResearchScreen(modifier: Modifier = Modifier, viewModel: GameViewModel) {
    val state by viewModel.state.collectAsState()
    var selectedCategory by remember { mutableStateOf("Tümü") }
    var selectedMainTab by remember { mutableStateOf(0) } // 0: Araştırılacaklar, 1: Araştırma Sırası, 2: Tamamlananlar

    val categories = listOf("Tümü", "Yazılım & Ekosistem", "RAM & Bellek", "Depolama & SD Kart", "İşlemci & Çip", "Ekran & Cam", "Batarya & Şarj", "Kamera & Ses", "Bağlantı & Kasa")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Compact Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    Icons.Default.Science,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    "Ar-Ge Araştırma Merkezi",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "${state.unlockedTech.size} Tamamlandı • ${state.researchQueue.size} Sırada",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Sleek & Compact Active Research Banner
        state.activeResearch?.let { activeRes ->
            val progressFraction = ((activeRes.totalMonths - activeRes.remainingMonths).toFloat() / activeRes.totalMonths.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("⚡", fontSize = 12.sp)
                            Column {
                                Text(
                                    "DEVAM EDEN AR-GE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                                )
                                Text(
                                    activeRes.techName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Kalan: ${activeRes.remainingMonths} Dönem (~${"%.1f".format(activeRes.remainingMonths / 2.0)} Ay)",
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.White.copy(alpha = 0.5f),
                        strokeCap = StrokeCap.Round
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "%${(progressFraction * 100).toInt()} Tamamlandı",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Slate600
                        )
                        Text(
                            text = "${state.engineers} Mühendis Çalışıyor",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Primary Tab Bar: Araştırılacaklar vs Sıradakiler vs Tamamlananlar
        ScrollableTabRow(
            selectedTabIndex = selectedMainTab,
            containerColor = MaterialTheme.colorScheme.background,
            modifier = Modifier.height(40.dp),
            edgePadding = 0.dp,
            divider = {}
        ) {
            Tab(
                selected = selectedMainTab == 0,
                onClick = { selectedMainTab = 0 },
                text = {
                    val count = allTechNodes.count { !state.unlockedTech.contains(it.id) && state.year >= it.yearAvailable }
                    Text("Araştırılacaklar ($count)", fontWeight = if (selectedMainTab == 0) FontWeight.Bold else FontWeight.Normal, fontSize = 11.5.sp, maxLines = 1, softWrap = false)
                }
            )
            Tab(
                selected = selectedMainTab == 1,
                onClick = { selectedMainTab = 1 },
                text = {
                    Text("📋 Sırada (${state.researchQueue.size})", fontWeight = if (selectedMainTab == 1) FontWeight.Bold else FontWeight.Normal, fontSize = 11.5.sp, maxLines = 1, softWrap = false)
                }
            )
            Tab(
                selected = selectedMainTab == 2,
                onClick = { selectedMainTab = 2 },
                text = {
                    Text("Tamamlanan (${state.unlockedTech.size})", fontWeight = if (selectedMainTab == 2) FontWeight.Bold else FontWeight.Normal, fontSize = 11.5.sp, maxLines = 1, softWrap = false)
                }
            )
        }

        if (selectedMainTab == 1) {
            // RESEARCH QUEUE VIEW
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        "📋 Otomatik Araştırma Sırası",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Mevcut araştırma bittiğinde sıradaki proje otomatik olarak başlar ve mühendisler çalışmaya devam eder.",
                        fontSize = 11.sp,
                        color = Slate600
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                if (state.researchQueue.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Araştırma sırası şu an boş.", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Araştırılacaklar sekmesinden istediğiniz teknolojilere 'Sıraya Ekle' diyerek otomatik zincir kurabilirsiniz.",
                                    fontSize = 12.sp,
                                    color = Slate500,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                } else {
                    items(state.researchQueue) { queueItem ->
                        val index = state.researchQueue.indexOf(queueItem) + 1
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "#$index",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(queueItem.techName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                                    Text("Maliyet: $${"%,d".format(queueItem.cost).replace(',', '.')} • Tahmini: ${queueItem.totalMonths} Dönem (~${"%.1f".format(queueItem.totalMonths / 2.0)} Ay)", fontSize = 11.sp, color = Slate600)
                                }

                                OutlinedButton(
                                    onClick = { viewModel.cancelQueuedResearch(queueItem.techId) },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Sıradan Çıkar", fontSize = 11.sp, color = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Category Filter Chips
            ScrollableTabRow(
                selectedTabIndex = categories.indexOf(selectedCategory).coerceAtLeast(0),
                edgePadding = 12.dp,
                containerColor = MaterialTheme.colorScheme.background,
                modifier = Modifier.height(36.dp),
                divider = {}
            ) {
                categories.forEach { cat ->
                    Tab(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        text = { Text(cat, fontSize = 11.5.sp, fontWeight = if (selectedCategory == cat) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val categoryFiltered = if (selectedCategory == "Tümü") allTechNodes else allTechNodes.filter { it.category == selectedCategory }
                
                val visibleTechNodes = if (selectedMainTab == 0) {
                    // Unresearched tech only
                    categoryFiltered.filter { !state.unlockedTech.contains(it.id) && state.year >= it.yearAvailable }
                } else {
                    // Completed/Researched tech only
                    categoryFiltered.filter { state.unlockedTech.contains(it.id) }
                }
                
                if (visibleTechNodes.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val message = if (selectedMainTab == 0) {
                                "🎉 Harika! Bu kategoride henüz kilitli araştırma kalmadı. Tamamlanan araştırmaları 'Tamamlanan' sekmesinden görebilirsiniz."
                            } else {
                                "Henüz bu kategoride tamamlanmış bir araştırma yok."
                            }
                            Text(
                                message,
                                fontSize = 12.sp,
                                color = Slate600
                            )
                        }
                    }
                }

                items(visibleTechNodes) { tech ->
                    val dynamicCost = (tech.cost * state.scaleMultiplier).toLong()
                    val isUnlocked = state.unlockedTech.contains(tech.id)
                    val isBeingResearched = state.activeResearch?.techId == tech.id
                    val queueIndex = state.researchQueue.indexOfFirst { it.techId == tech.id }
                    val isInQueue = queueIndex != -1
                    val canAfford = state.budget >= dynamicCost
                    val isAvailable = state.year >= tech.yearAvailable
                    val estimatedDuration = viewModel.calculateResearchDuration(state.engineers, dynamicCost)
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                when {
                                    isUnlocked -> MaterialTheme.colorScheme.secondaryContainer
                                    isInQueue -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                    else -> Color.White
                                }, 
                                RoundedCornerShape(12.dp)
                            )
                            .border(1.dp, if (isUnlocked) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else if (isInQueue) MaterialTheme.colorScheme.primary else Slate200, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        when {
                                            isUnlocked -> MaterialTheme.colorScheme.primary
                                            isBeingResearched -> MaterialTheme.colorScheme.tertiary
                                            isInQueue -> MaterialTheme.colorScheme.primary
                                            else -> Slate200
                                        }, 
                                        RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isInQueue) {
                                    Text("#${queueIndex + 1}", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                                } else {
                                    Icon(
                                        if (isUnlocked) Icons.Default.LockOpen else if (isBeingResearched) Icons.Default.Science else Icons.Default.Lock, 
                                        contentDescription = null, 
                                        tint = if (isUnlocked || isBeingResearched) Color.White else Slate400,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    tech.name, 
                                    fontWeight = FontWeight.Bold, 
                                    fontSize = 13.sp, 
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    tech.description, 
                                    fontSize = 10.5.sp, 
                                    color = Slate600, 
                                    lineHeight = 14.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                if (isUnlocked) {
                                    Text("✓ Açıldı & Yapılandırmaya Eklendi", fontWeight = FontWeight.Bold, color = Green500, fontSize = 10.sp)
                                } else if (isBeingResearched) {
                                    Text("⚡ Araştırma Sürüyor (${state.activeResearch?.remainingMonths} Dönem Kaldı)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary, fontSize = 10.sp)
                                } else if (isInQueue) {
                                    Text("📋 Sırada (${queueIndex + 1}. Sırada)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 10.sp)
                                } else if (!isAvailable) {
                                    Text("Gereken Yıl: ${tech.yearAvailable}", fontWeight = FontWeight.Bold, color = Slate500, fontSize = 10.sp)
                                } else {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Maliyet: $${"%,d".format(dynamicCost).replace(',', '.')}", fontWeight = FontWeight.Bold, color = if (canAfford) MaterialTheme.colorScheme.primary else Color.Red, fontSize = 10.5.sp)
                                        Text("• Süre: $estimatedDuration Dönem (~${"%.1f".format(estimatedDuration / 2.0)} Ay)", fontWeight = FontWeight.Medium, color = Slate600, fontSize = 10.sp)
                                    }
                                }
                            }
                            
                            if (!isUnlocked) {
                                if (isInQueue) {
                                    OutlinedButton(
                                        onClick = { viewModel.cancelQueuedResearch(tech.id) },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(32.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                    ) {
                                        Text("Çıkar", fontSize = 10.5.sp, color = Color.Red)
                                    }
                                } else {
                                    val buttonEnabled = isAvailable && (state.activeResearch == null && canAfford || state.activeResearch != null)
                                    Button3D(
                                        onClick = { 
                                            if (state.activeResearch == null) {
                                                viewModel.startResearch(tech.id, tech.name, dynamicCost)
                                            } else {
                                                viewModel.queueResearch(tech.id, tech.name, dynamicCost)
                                            }
                                        },
                                        enabled = buttonEnabled,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = when {
                                                isBeingResearched -> MaterialTheme.colorScheme.tertiary
                                                state.activeResearch != null -> MaterialTheme.colorScheme.secondary
                                                else -> MaterialTheme.colorScheme.primary
                                            }
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(32.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                                    ) {
                                        val buttonText = when {
                                            isBeingResearched -> "Sürüyor"
                                            state.activeResearch != null -> "+ Sırada"
                                            !isAvailable -> "Kilitli"
                                            else -> "Araştır"
                                        }
                                        Text(buttonText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
