package com.example.viewmodel

import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
data class SupplyChainEvent(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val costMultiplierPercent: Int, // 100 = normal, 130 = %30 daha pahalı, 85 = %15 daha ucuz
    val remainingPeriods: Int,
    val totalPeriods: Int
)

private data class SupplyChainTemplate(
    val title: String,
    val description: String,
    val icon: String,
    val costMultiplierPercent: Int,
    val durationPeriods: IntRange,
    val minYear: Int = 2010
)

private val SUPPLY_CHAIN_TEMPLATES = listOf(
    SupplyChainTemplate(
        title = "Küresel Çip Krizi",
        description = "Yarı iletken tedarikinde küresel darboğaz yaşanıyor. Cihaz üretim maliyetleri geçici olarak artıyor.",
        icon = "⚠️",
        costMultiplierPercent = 128,
        durationPeriods = 4..8
    ),
    SupplyChainTemplate(
        title = "Nadir Toprak Element Fiyat Şoku",
        description = "Ekran ve batarya üretiminde kullanılan hammaddelerin fiyatı ani şekilde yükseldi.",
        icon = "⛏️",
        costMultiplierPercent = 118,
        durationPeriods = 3..6
    ),
    SupplyChainTemplate(
        title = "Liman Grevi & Lojistik Aksaması",
        description = "Büyük konteyner limanlarındaki grevler bileşen tedarikini yavaşlattı, üretim maliyetleri arttı.",
        icon = "🚢",
        costMultiplierPercent = 112,
        durationPeriods = 2..5
    ),
    SupplyChainTemplate(
        title = "Fabrika Otomasyon Atılımı",
        description = "Tedarikçi ağınızdaki fabrikalar yeni otomasyon hattına geçti; birim üretim maliyetleri düştü.",
        icon = "🤖",
        costMultiplierPercent = 90,
        durationPeriods = 3..6,
        minYear = 2014
    ),
    SupplyChainTemplate(
        title = "Bileşen Fazlası Fırsatı",
        description = "Sektörde geçici arz fazlası oluştu, tedarikçiler indirimli toplu satış yapıyor.",
        icon = "📉",
        costMultiplierPercent = 88,
        durationPeriods = 2..4
    ),
    SupplyChainTemplate(
        title = "Panik Alım Dalgası",
        description = "Rakip şirketlerin panik halinde bileşen stoklaması piyasada geçici kıtlık yarattı.",
        icon = "📦",
        costMultiplierPercent = 115,
        durationPeriods = 2..4
    )
)

/**
 * Her 2 haftalık periyotta çağrılır. Aktif olay yoksa küçük bir ihtimalle (%5) yeni bir
 * tedarik zinciri olayı tetikler. Aktif olay varsa süresini bir azaltır, süresi dolunca null döner.
 * Dönüş: (yeni/güncel olay, olay bu periyotta YENİ başladıysa true, olay bu periyotta BİTTİYSE true)
 */
fun tickSupplyChainEvent(current: SupplyChainEvent?, year: Int): Triple<SupplyChainEvent?, Boolean, SupplyChainEvent?> {
    if (current != null) {
        val remaining = current.remainingPeriods - 1
        return if (remaining <= 0) {
            Triple(null, false, current) // bitti, bitmiş olayı da döndür (haber için)
        } else {
            Triple(current.copy(remainingPeriods = remaining), false, null)
        }
    }

    // Olay yok: küçük ihtimalle yeni bir tedarik zinciri olayı başlat
    if (Random.nextInt(100) >= 5) return Triple(null, false, null)

    val eligibleTemplates = SUPPLY_CHAIN_TEMPLATES.filter { year >= it.minYear }
    if (eligibleTemplates.isEmpty()) return Triple(null, false, null)

    val template = eligibleTemplates.random()
    val duration = template.durationPeriods.random()
    val newEvent = SupplyChainEvent(
        id = "sce_${year}_${Random.nextInt(1000, 9999)}",
        title = template.title,
        description = template.description,
        icon = template.icon,
        costMultiplierPercent = template.costMultiplierPercent,
        remainingPeriods = duration,
        totalPeriods = duration
    )
    return Triple(newEvent, true, null)
}
