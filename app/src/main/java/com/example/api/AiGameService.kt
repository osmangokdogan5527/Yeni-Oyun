package com.example.api

import com.example.BuildConfig
import com.example.viewmodel.MarketTrend
import com.example.viewmodel.PhoneSpecs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Oyun içeriğini (dinamik haberler, telefon eleştirmen yorumları) Gemini API ile
 * üreten servis katmanı.
 *
 * ÖNEMLİ: [GameViewModel] bu sınıfı zaten çağırıyordu (generateDynamicNews /
 * generatePhoneReview), ama proje içinde tanımı yoktu — yani proje bu haliyle
 * DERLENEMİYORDU ("unresolved reference: AiGameService"). Bu dosya o eksikliği
 * gideriyor ve mevcut [GeminiApiService]/[RetrofitClient] altyapısını gerçekten
 * kullanıma sokuyor.
 *
 * Her iki fonksiyon da API anahtarı ayarlanmamışsa veya ağ çağrısı başarısız
 * olursa sessizce yerel bir yedek değere düşer — yani bu servis asla oyunu
 * çökertmez, kilitlemez veya oyuncuyu bekletmez.
 */
object AiGameService {

    private val json = Json { ignoreUnknownKeys = true }

    /** .env dosyasında GEMINI_API_KEY gerçekten ayarlanmışsa true döner. */
    private val isApiKeyConfigured: Boolean
        get() = BuildConfig.GEMINI_API_KEY.isNotBlank() &&
            BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY"

    @Serializable
    private data class NewsGenResult(val title: String, val text: String)

    /**
     * Yıl/ay ve o anki pazar trendine göre kısa, gerçekçi bir teknoloji haberi üretir.
     * Başarısız olursa (anahtar yok, ağ hatası, geçersiz cevap) null döner — çağıran
     * taraf bu ay için AI haberini atlar, oyunun akışında hiçbir kesinti olmaz.
     */
    suspend fun generateDynamicNews(
        year: Int,
        month: Int,
        trendTitle: String,
        competitorNames: List<String>
    ): Pair<String, String>? {
        if (!isApiKeyConfigured) return null

        return withContext(Dispatchers.IO) {
            try {
                val prompt = """
                    Sen bir teknoloji haber ajansısın. $year yılı $month. ayında geçen,
                    akıllı telefon sektörüyle ilgili KISA ve gerçekçi bir haber yaz.
                    Güncel pazar trendi: "$trendTitle".
                    Sektördeki bazı şirketler: ${competitorNames.take(4).joinToString(", ")}.
                    Sadece şu JSON formatında cevap ver, başka hiçbir açıklama ekleme:
                    {"title": "Haber başlığı (en fazla 10 kelime)", "text": "Haber metni (2-3 cümle, Türkçe)"}
                """.trimIndent()

                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt)), role = "user")),
                    generationConfig = GenerationConfig(
                        temperature = 0.9f,
                        responseMimeType = "application/json"
                    )
                )

                val response = RetrofitClient.service.generateContent(BuildConfig.GEMINI_API_KEY, request)
                val rawText = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: return@withContext null

                val parsed = json.decodeFromString<NewsGenResult>(rawText)
                if (parsed.title.isBlank() || parsed.text.isBlank()) null
                else parsed.title to parsed.text
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Yeni çıkan bir telefon modeli için kısa bir eleştirmen alıntısı üretir.
     * Her koşulda bir metin döndürür: Gemini başarısız olursa yerel, önceden
     * hazırlanmış bir yoruma (isAi = false) düşer, böylece lansman akışı asla
     * boş/kırık bir alıntıyla kalmaz.
     */
    suspend fun generatePhoneReview(
        specs: PhoneSpecs,
        companyName: String,
        year: Int,
        reviewScore: Int,
        trend: MarketTrend
    ): Pair<String, Boolean> {
        val fallback = localFallbackQuote(reviewScore)
        if (!isApiKeyConfigured) return fallback to false

        return withContext(Dispatchers.IO) {
            try {
                val prompt = """
                    Sen kıdemli bir teknoloji eleştirmenisin. $year yılında $companyName firmasının
                    çıkardığı "${specs.name}" adlı telefonu değerlendiriyorsun.
                    Özellikler: ${specs.processor}, ${specs.display}, ${specs.camera}, ${specs.batteryCapacity}, Fiyat: ${'$'}${specs.price}.
                    Puanı: $reviewScore/100. Güncel pazar trendi: "${trend.title}".
                    Tek cümlelik, esprili ama gerçekçi bir eleştirmen alıntısı yaz (Türkçe, tırnak
                    işareti KULLANMA, en fazla 25 kelime). Sadece alıntı metnini yaz, başka
                    hiçbir açıklama ekleme.
                """.trimIndent()

                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt)), role = "user")),
                    generationConfig = GenerationConfig(temperature = 1.0f)
                )

                val response = RetrofitClient.service.generateContent(BuildConfig.GEMINI_API_KEY, request)
                val quote = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()

                if (quote.isNullOrBlank()) fallback to false else quote to true
            } catch (e: Exception) {
                fallback to false
            }
        }
    }

    private fun localFallbackQuote(reviewScore: Int): String = when {
        reviewScore >= 85 -> "Bu yıl piyasaya çıkan en etkileyici cihazlardan biri, kesinlikle göz kamaştırıyor."
        reviewScore >= 65 -> "Sağlam bir seçenek; fiyat/performans dengesini gözetenler için makul bir tercih."
        reviewScore >= 40 -> "Beklentileri karşılıyor ama rakiplerinin gerisinde kalan yönleri de var."
        else -> "Maalesef bu modelde ciddi eksiklikler var, alıcıların dikkatli olması önerilir."
    }
}
