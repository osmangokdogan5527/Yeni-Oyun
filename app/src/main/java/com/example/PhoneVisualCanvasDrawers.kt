package com.example

import androidx.compose.material3.MaterialTheme

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.dp

internal fun DrawScope.drawBackChassis(
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
            listOf(
                Color(0xFF181B20),
                Color(0xFF0A0C0F),
                baseColor.copy(alpha = 0.16f)
            ),
            center = Offset(size.width * 0.4f, size.height * 0.3f), radius = size.height * 0.9f
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
            // Gerçekçi 2x2 dokuma (twill weave) karbon fiber deseni: küçük kareler halinde
            // dönüşümlü açık/koyu "iplik" hücreleri + epoksi cam kaplama parlaklığı + kenar vinyeti.
            val clipShape = Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = Rect(
                            left = backMargin,
                            top = backMargin,
                            right = backMargin + backRect.width,
                            bottom = backMargin + backRect.height
                        ),
                        cornerRadius = CornerRadius(backCorner, backCorner)
                    )
                )
            }
            clipPath(clipShape) {
                val cell = 6.dp.toPx()
                var row = 0
                var y = backMargin
                while (y < backMargin + backRect.height) {
                    var col = 0
                    var x = backMargin
                    while (x < backMargin + backRect.width) {
                        val phase = (row + col) % 4
                        when (phase) {
                            0 -> drawRect(color = Color.White.copy(alpha = 0.09f), topLeft = Offset(x, y), size = Size(cell, cell))
                            1 -> drawRect(color = Color.White.copy(alpha = 0.04f), topLeft = Offset(x, y), size = Size(cell, cell))
                            2 -> drawRect(color = Color(0xFF1E293B).copy(alpha = 0.30f), topLeft = Offset(x, y), size = Size(cell, cell))
                            else -> drawRect(color = Color(0xFF1E293B).copy(alpha = 0.14f), topLeft = Offset(x, y), size = Size(cell, cell))
                        }
                        // İplik demeti (tow) çapraz parıltısı — dokumaya derinlik katar
                        drawLine(
                            color = Color.White.copy(alpha = if (phase < 2) 0.10f else 0.04f),
                            start = Offset(x, y + cell),
                            end = Offset(x + cell, y),
                            strokeWidth = 0.6.dp.toPx()
                        )
                        x += cell
                        col++
                    }
                    y += cell
                    row++
                }

                // Epoksi Cam Kaplama Parlaklığı (Glossy Clear-Coat)
                val glossPath = Path().apply {
                    moveTo(backMargin, backMargin + backRect.height * 0.12f)
                    lineTo(backMargin + backRect.width * 0.6f, backMargin)
                    lineTo(backMargin + backRect.width * 0.8f, backMargin)
                    lineTo(backMargin + backRect.width * 0.18f, backMargin + backRect.height * 0.42f)
                    close()
                }
                drawPath(
                    path = glossPath,
                    brush = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.20f), Color.Transparent))
                )

                // Kenar Vinyeti (Derinlik Hissi)
                drawRect(
                    brush = Brush.radialGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.38f)),
                        center = Offset(backMargin + backRect.width / 2, backMargin + backRect.height / 2),
                        radius = backRect.width * 0.85f
                    ),
                    topLeft = Offset(backMargin, backMargin),
                    size = backRect
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

            drawCircle(color = Color(0xFF1E293B).copy(alpha = 0.45f), radius = haloRadius + 3.dp.toPx(), center = Offset(haloCenter.x, haloCenter.y + 2.dp.toPx()))
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
                color = Color(0xFF1E293B).copy(alpha = 0.45f),
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
                color = Color(0xFF1E293B).copy(alpha = 0.45f),
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
                color = Color(0xFF1E293B).copy(alpha = 0.45f),
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

internal fun DrawScope.drawFrontDisplay(
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
        color = Color(0xFF1E293B).copy(alpha = 0.5f),
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
                color = Color(0xFF1E293B),
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
                color = Color(0xFF1E293B),
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
            drawCircle(color = Color(0xFF1E293B), radius = 4.2.dp.toPx(), center = holeCenter)
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

internal fun DrawScope.drawSideAndPortChassis(
    size: Size,
    material: String,
    chargingPort: String,
    cellularNetwork: String,
    colorHex: Long,
    frameStyle: String
) {
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
