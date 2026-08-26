package com.example.api

import android.util.Log
import com.example.BuildConfig
import com.example.viewmodel.MarketTrend
import com.example.viewmodel.PhoneSpecs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.random.Random

object AiGameService {
    private const val TAG = "AiGameService"

    /**
     * Generates a realistic tech-critic review for a newly launched smartphone using Gemini AI.
     * Falls back to a rich procedural review if Gemini is unavailable or offline.
     */
    suspend fun generatePhoneReview(
        specs: PhoneSpecs,
        companyName: String,
        year: Int,
        reviewScore: Int,
        trend: MarketTrend?
    ): Pair<String, Boolean> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = buildString {
                    append("Sen ünlü bir teknoloji editörüsün (MKBHD, The Verge veya Technopat tarzı). ")
                    append("$year yılında $companyName şirketi yeni '${specs.name}' (${specs.tier.title} sınıfı) akıllı telefonunu piyasaya sürdü. ")
                    append("Özellikler: Ekran: ${specs.display}, İşlemci: ${specs.processor}, RAM: ${specs.ram}, Depolama: ${specs.storage}, Kamera: ${specs.camera}, Batarya: ${specs.batteryCapacity} (${specs.batteryType}), Gövde: ${specs.material} / ${specs.backFinish}, Çerçeve: ${specs.frameStyle}, İşletim Sistemi: ${specs.osName} (${specs.osFocus}), Fiyat: $${specs.price}. ")
                    if (trend != null) {
                        append("Aktif Pazar Trendi: '${trend.title}'. ")
                    }
                    append("Telefonun aldığı genel inceleme puanı: $reviewScore/100. ")
                    append("Lütfen Türkçe olarak 2-3 cümlelik akıcı, zekice ve eğlenceli bir basın incelemesi yaz. Yazının sonunda ünlü bir teknoloji editörü adı (örn. '— Marques Brownlee', '— Dieter Bohn (The Verge)', '— Hakkı Alkan (ShiftDelete)', '— Recep Baltaş (Technopat)' veya '— Linus Tech') ekle. Sadece inceleme metnini döndür.")
                }

                val request = GenerateContentRequest(
                    contents = listOf(
                        Content(
                            parts = listOf(Part(text = prompt)),
                            role = "user"
                        )
                    ),
                    generationConfig = GenerationConfig(
                        temperature = 0.8f,
                        topP = 0.95f,
                        topK = 40
                    )
                )

                val response = withTimeoutOrNull(8000L) {
                    RetrofitClient.service.generateContent(apiKey, request)
                }

                val aiText = response?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
                if (!aiText.isNullOrBlank()) {
                    return@withContext Pair(aiText, true)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Gemini API call failed, using procedural fallback: ${e.message}")
            }
        }

        // Procedural Fallback
        val reviewerNames = listOf(
            "Marques Brownlee (MKBHD)",
            "Dieter Bohn (The Verge)",
            "Hakkı Alkan (ShiftDelete)",
            "Recep Baltaş (Technopat)",
            "Ali Güngör (Technopat)",
            "Mrwhosetheboss (Arun Maini)",
            "Eren Caner",
            "Mesut Çevik"
        )
        val reviewer = reviewerNames[Random.nextInt(reviewerNames.size)]
        
        val tone = when {
            reviewScore >= 90 -> "Yılın tartışmasız en iddialı amiral gemisi! ${specs.material} gövde kalitesi, ${specs.display} ekran ve ${specs.camera} kamera performansı büyüleyici. $${specs.price} fiyatını sonuna kadar hak ediyor."
            reviewScore >= 75 -> "${specs.name}, sunduğu ${specs.processor} performansı ve ${specs.ram} RAM kapasitesiyle günlük kullanımda oldukça akıcı. ${specs.batteryCapacity} bataryası günü rahat çıkarıyor."
            reviewScore >= 55 -> "Segmentinde fena bir alternatif değil. ${specs.display} ekranı başarılı ancak $${specs.price} fiyat etiketine göre bazı rakiplerinin gerisinde kalabiliyor."
            else -> "${specs.name} maalesef beklentilerin altında kaldı. Donanım optimizasyonu ve malzeme hissiyatı geliştirilmeli."
        }

        Pair("\"$tone\" — $reviewer", false)
    }

    /**
     * Generates dynamic tech world breaking news using Gemini AI or fallback.
     */
    suspend fun generateDynamicNews(
        year: Int,
        month: Int,
        currentTrendTitle: String,
        topCompetitors: List<String>
    ): Pair<String, String>? = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val competitorsStr = topCompetitors.take(4).joinToString(", ")
                val prompt = "Sen bir teknoloji haber ajansısın. Yıl: $year, Ay: $month. Sektördeki aktif trend: '$currentTrendTitle'. Önde gelen markalar: $competitorsStr. Akıllı telefon dünyasında yaşanan, tüketicileri ve piyasayı heyecanlandıracak kısa, vurucu 1 adet Türkçe flaş haber üret. Format tam olarak şu şekilde olmalı:\nBAŞLIK: <Çarpıcı Başlık>\nMETİN: <1-2 cümlelik haber içeriği>"

                val request = GenerateContentRequest(
                    contents = listOf(
                        Content(
                            parts = listOf(Part(text = prompt)),
                            role = "user"
                        )
                    ),
                    generationConfig = GenerationConfig(
                        temperature = 0.85f,
                        topP = 0.95f
                    )
                )

                val response = withTimeoutOrNull(6000L) {
                    RetrofitClient.service.generateContent(apiKey, request)
                }

                val aiText = response?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
                if (!aiText.isNullOrBlank()) {
                    val lines = aiText.lines().map { it.trim() }.filter { it.isNotEmpty() }
                    val titleLine = lines.firstOrNull { it.startsWith("BAŞLIK:", ignoreCase = true) }?.substringAfter(":")?.trim()
                        ?: lines.firstOrNull()?.replace("BAŞLIK:", "", ignoreCase = true)?.trim()
                    val textLine = lines.firstOrNull { it.startsWith("METİN:", ignoreCase = true) }?.substringAfter(":")?.trim()
                        ?: lines.drop(1).joinToString(" ").replace("METİN:", "", ignoreCase = true).trim()

                    if (!titleLine.isNullOrBlank() && !textLine.isNullOrBlank()) {
                        return@withContext Pair("🤖 $titleLine", textLine)
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Dynamic news generation failed: ${e.message}")
            }
        }
        null
    }
}
