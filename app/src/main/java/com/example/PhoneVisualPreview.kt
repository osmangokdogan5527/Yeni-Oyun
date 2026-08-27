package com.example

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.ModelTier

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
                .height(430.dp),
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
                    topLeft = Offset(size.width * 0.08f, size.height * 0.72f),
                    size = androidx.compose.ui.geometry.Size(size.width * 0.84f, size.height * 0.28f)
                )
            }

            when (viewMode) {
                "Çift" -> {
                    // STÜDYO ÇİFT PERSPEKTİFİ: Arka planda gövde, önde büyük ve net ekran
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        // ARKADA: ARKA GÖVDE & KAMERA ADASI (hafifçe sağa kaymış, daha küçük katman)
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .offset(x = 58.dp, y = (-6).dp)
                                .size(width = 168.dp, height = 340.dp)
                                .shadow(
                                    elevation = 22.dp,
                                    shape = RoundedCornerShape(38.dp),
                                    spotColor = Color(colorHex).copy(alpha = 0.55f),
                                    ambientColor = Color.Black.copy(alpha = 0.75f)
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

                        // ÖNDE: BÜYÜK & NET ÖN EKRAN (baştan aşağı tüm özellikleri okunur şekilde gösterir)
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .offset(x = (-30).dp, y = 12.dp)
                                .size(width = 196.dp, height = 400.dp)
                                .shadow(
                                    elevation = 36.dp,
                                    shape = RoundedCornerShape(42.dp),
                                    spotColor = Color(0xFF38BDF8).copy(alpha = 0.55f),
                                    ambientColor = Color.Black.copy(alpha = 0.9f)
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
                }

                "Arka" -> {
                    // Tam Boyut Arka Kapak & Kamera Görünümü
                    Box(
                        modifier = Modifier
                            .size(width = 210.dp, height = 415.dp)
                            .shadow(
                                elevation = 32.dp,
                                shape = RoundedCornerShape(42.dp),
                                spotColor = Color(colorHex).copy(alpha = 0.7f),
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
                            .size(width = 210.dp, height = 415.dp)
                            .shadow(
                                elevation = 32.dp,
                                shape = RoundedCornerShape(42.dp),
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
                            .size(width = 260.dp, height = 380.dp)
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

        // --- BAŞTAN AŞAĞI ÖZELLİK ŞERİDİ: Telefonun her bölgesini spesifikasyona göre özetler ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F172A).copy(alpha = 0.85f), RoundedCornerShape(14.dp))
                .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SpecRow(icon = "📱", label = "Ekran", value = "$display • $notchStyle")
            SpecRow(icon = "🎛️", label = "Çerçeve", value = "$frameStyle • $material")
            SpecRow(icon = "🎨", label = "Arka Kapak", value = "$backFinish • $colorName", valueColor = Color(colorHex))
            SpecRow(icon = "📷", label = "Kamera", value = "$camera • $cameraBumpStyle")
            SpecRow(icon = "⚡", label = "Port & Şebeke", value = "$chargingPort • $cellularNetwork")
        }
    }
}

@Composable
private fun SpecRow(icon: String, label: String, value: String, valueColor: Color = Color(0xFFE2E8F0)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = icon, fontSize = 13.sp, modifier = Modifier.width(22.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64748B),
            modifier = Modifier.width(58.dp)
        )
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = valueColor,
            maxLines = 1
        )
    }
}
