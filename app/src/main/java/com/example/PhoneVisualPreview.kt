package com.example

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AspectRatio
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.CropSquare
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
    displayResolution: String = "480 x 800 (WVGA)",
    displayBrightness: String = "350 nit",
    screenSizeInch: Float = 6.1f,
    thicknessMm: Float = 8.0f,
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

    val brightnessNits = displayBrightness.filter { it.isDigit() }.toIntOrNull() ?: 350

    // Seçilen fiziksel boyuta göre görsel ölçek: 5.4"-6.9" aralığı ~0.90x-1.08x arasına haritalanır
    val sizeScale = 0.90f + ((screenSizeInch - 5.4f) / (6.9f - 5.4f)).coerceIn(0f, 1f) * 0.18f
    // Kalınlığa göre gölge derinliği ve kenar vurgusu: ince telefonlar zarif/az gölgeli, kalın telefonlar "ağır" görünür
    val thicknessRatio = ((thicknessMm - 6.5f) / (9.5f - 6.5f)).coerceIn(0f, 1f)
    val depthElevation = 22.dp + (thicknessRatio * 20).dp
    val phoneCornerDp = when (frameStyle) {
        "Düz Metal Kenar" -> 28.dp
        "Zırhlı Kesim" -> 16.dp
        "Ultra İnce Çerçeve" -> 34.dp
        else -> 30.dp
    }
    val phoneShape = RoundedCornerShape(phoneCornerDp)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // --- 4'LÜ ETKİLEŞİMLİ STÜDYO GÖRÜNÜM SEÇİCİ (sade: ikon + kısa metin) ---
        Row(
            modifier = Modifier
                .background(Color(0xFF0F172A), RoundedCornerShape(22.dp))
                .border(1.dp, Color(0xFF334155), RoundedCornerShape(22.dp))
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            val modes = listOf(
                Triple("Çift", Icons.Outlined.AutoAwesome, "Stüdyo"),
                Triple("Arka", Icons.Outlined.Smartphone, "Arka"),
                Triple("Ön", Icons.Outlined.Tv, "Ön Ekran"),
                Triple("Port", Icons.Outlined.Bolt, "Port")
            )
            modes.forEach { (modeKey, icon, label) ->
                val isSel = viewMode == modeKey
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { viewMode = modeKey }
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isSel) Color.White else Color(0xFF94A3B8),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = label,
                        color = if (isSel) Color.White else Color(0xFF94A3B8),
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 11.sp,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- 3D STÜDYO SAHNESİ & GÖLGELER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp),
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
                                .size(width = 168.dp * sizeScale, height = 340.dp * sizeScale)
                                .shadow(
                                    elevation = depthElevation,
                                    shape = phoneShape,
                                    spotColor = Color(colorHex).copy(alpha = 0.55f),
                                    ambientColor = Color.Black.copy(alpha = 0.75f)
                                )
                                .clip(phoneShape)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawBackChassis(
                                    size = size,
                                    frameStyle = frameStyle,
                                    material = material,
                                    backFinish = backFinish,
                                    cameraBumpStyle = cameraBumpStyle,
                                    camera = camera,
                                    style = style,
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
                                .size(width = 196.dp * sizeScale, height = 400.dp * sizeScale)
                                .shadow(
                                    elevation = depthElevation + 12.dp,
                                    shape = phoneShape,
                                    spotColor = Color(0xFF38BDF8).copy(alpha = 0.55f),
                                    ambientColor = Color.Black.copy(alpha = 0.9f)
                                )
                                .clip(phoneShape)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawFrontDisplay(
                                    size = size,
                                    frameStyle = frameStyle,
                                    material = material,
                                    notchStyle = notchStyle,
                                    display = display,
                                    colorHex = colorHex,
                                    brightnessNits = brightnessNits
                                )
                            }
                        }
                    }
                }

                "Arka" -> {
                    // Tam Boyut Arka Kapak & Kamera Görünümü
                    Box(
                        modifier = Modifier
                            .size(width = 210.dp * sizeScale, height = 415.dp * sizeScale)
                            .shadow(
                                elevation = depthElevation + 8.dp,
                                shape = phoneShape,
                                spotColor = Color(colorHex).copy(alpha = 0.7f),
                                ambientColor = Color.Black.copy(alpha = 0.8f)
                            )
                            .clip(phoneShape)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawBackChassis(
                                size = size,
                                frameStyle = frameStyle,
                                material = material,
                                backFinish = backFinish,
                                cameraBumpStyle = cameraBumpStyle,
                                camera = camera,
                                style = style,
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
                            .size(width = 210.dp * sizeScale, height = 415.dp * sizeScale)
                            .shadow(
                                elevation = depthElevation + 8.dp,
                                shape = phoneShape,
                                spotColor = Color(colorHex).copy(alpha = 0.65f),
                                ambientColor = Color.Black.copy(alpha = 0.8f)
                            )
                            .clip(phoneShape)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawFrontDisplay(
                                size = size,
                                frameStyle = frameStyle,
                                material = material,
                                notchStyle = notchStyle,
                                display = display,
                                colorHex = colorHex,
                                brightnessNits = brightnessNits
                            )
                        }
                    }
                }

                "Port" -> {
                    // Yan Profil & Alt Port Detayı
                    Box(
                        modifier = Modifier
                            .size(width = 260.dp * sizeScale, height = 380.dp)
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
                                frameStyle = frameStyle,
                                thicknessMm = thicknessMm
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- SADE ÖZELLİK ŞERİDİ: gerçek Material ikonları, nötr tek renk, geniş satır aralığı ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F172A).copy(alpha = 0.85f), RoundedCornerShape(14.dp))
                .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SpecRow(icon = Icons.Outlined.Smartphone, label = "Ekran", value = "$display • $notchStyle")
            SpecRow(icon = Icons.Outlined.Tv, label = "Çözünürlük", value = displayResolution)
            SpecRow(icon = Icons.Outlined.AutoAwesome, label = "Parlaklık", value = displayBrightness)
            SpecRow(icon = Icons.Outlined.AspectRatio, label = "Boyut", value = "${"%.1f".format(screenSizeInch)}\" • ${"%.1f".format(thicknessMm)}mm kalınlık")
            SpecRow(icon = Icons.Outlined.CropSquare, label = "Çerçeve", value = "$frameStyle • $material")
            SpecRow(icon = Icons.Outlined.Palette, label = "Arka Kapak", value = "$backFinish • $colorName", valueColor = Color(colorHex))
            SpecRow(icon = Icons.Outlined.CameraAlt, label = "Kamera", value = "$camera • $cameraBumpStyle")
            SpecRow(icon = Icons.Outlined.Bolt, label = "Port & Şebeke", value = "$chargingPort • $cellularNetwork")
        }
    }
}

@Composable
private fun SpecRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, valueColor: Color = Color(0xFFE2E8F0)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF64748B),
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(9.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64748B),
            modifier = Modifier.width(64.dp),
            maxLines = 1
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
