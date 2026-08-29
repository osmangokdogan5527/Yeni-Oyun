package com.example

import com.example.ui.ProCard
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.CustomOsState
import kotlin.math.cos
import kotlin.math.sin

/**
 * Popülerlik (%) zaman çizelgesi grafiği. [CustomOsState.popularityHistory] her periyotta
 * biriken en fazla 30 kayıtlık geçmişi çizgi grafik olarak gösterir.
 */
@Composable
fun OsPopularityTrendCard(customOs: CustomOsState) {
    val history = customOs.popularityHistory
    val themeColor = Color(customOs.themeColorHex)

    ProCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = themeColor)
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Popülerlik Trendi", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Son ${history.size} periyotluk pazar popülerliği değişimi", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Surface(shape = RoundedCornerShape(8.dp), color = themeColor.copy(alpha = 0.15f)) {
                    Text(
                        text = "%${"%.1f".format(customOs.popularityPercent)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = themeColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (history.size < 2) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Trend grafiği için birkaç periyot daha ilerlemeniz gerekiyor.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                val maxVal = (history.max().coerceAtLeast(5f))
                val minVal = 0f

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    val chartWidth = size.width
                    val chartHeight = size.height - 8.dp.toPx()
                    val stepX = if (history.size > 1) chartWidth / (history.size - 1) else chartWidth

                    fun yFor(value: Float): Float {
                        val ratio = (value - minVal) / (maxVal - minVal).coerceAtLeast(0.01f)
                        return chartHeight - (ratio * chartHeight)
                    }

                    // Yatay referans çizgileri (grid)
                    val gridLines = 4
                    repeat(gridLines + 1) { i ->
                        val y = chartHeight * i / gridLines
                        drawLine(
                            color = Color(0xFFE2E8F0),
                            start = Offset(0f, y),
                            end = Offset(chartWidth, y),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                        )
                    }

                    // Dolgu alanı (gradient fill altında)
                    val fillPath = Path().apply {
                        moveTo(0f, chartHeight)
                        history.forEachIndexed { index, value ->
                            lineTo(index * stepX, yFor(value))
                        }
                        lineTo((history.size - 1) * stepX, chartHeight)
                        close()
                    }
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            listOf(themeColor.copy(alpha = 0.35f), Color.Transparent)
                        )
                    )

                    // Çizgi
                    val linePath = Path().apply {
                        history.forEachIndexed { index, value ->
                            val x = index * stepX
                            val y = yFor(value)
                            if (index == 0) moveTo(x, y) else lineTo(x, y)
                        }
                    }
                    drawPath(path = linePath, color = themeColor, style = Stroke(width = 2.5.dp.toPx()))

                    // Son nokta vurgusu
                    val lastX = (history.size - 1) * stepX
                    val lastY = yFor(history.last())
                    drawCircle(color = themeColor, radius = 4.dp.toPx(), center = Offset(lastX, lastY))
                    drawCircle(color = Color.White, radius = 1.8.dp.toPx(), center = Offset(lastX, lastY))
                }
            }
        }
    }
}

/**
 * OS yetenek profili radar (örümcek) grafiği: Çekirdek, Yapay Zeka, Güvenlik,
 * Bulut ve Uygulama Mağazası seviyelerini 5 eksenli bir pentagon üzerinde gösterir.
 */
@Composable
fun OsCapabilityRadarCard(customOs: CustomOsState, maxLevel: Int = 5) {
    val themeColor = Color(customOs.themeColorHex)
    val axisLabels = listOf("Çekirdek", "Yapay Zeka", "Güvenlik", "Bulut", "Mağaza")
    val values = listOf(
        customOs.kernelLevel,
        customOs.aiLevel,
        customOs.securityLevel,
        customOs.cloudLevel,
        customOs.appStoreLevel
    )

    ProCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Radar, contentDescription = null, tint = themeColor)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Yetenek Profili", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Modül seviyelerinin dengesi (Maks. Seviye $maxLevel)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = (size.minDimension / 2) * 0.68f
                    val axisCount = axisLabels.size
                    val angleStep = (2 * Math.PI / axisCount)
                    val startAngle = -Math.PI / 2 // Yukarıdan başla

                    fun pointFor(axisIndex: Int, ratio: Float): Offset {
                        val angle = startAngle + axisIndex * angleStep
                        val r = radius * ratio
                        return Offset(
                            x = (center.x + r * cos(angle)).toFloat(),
                            y = (center.y + r * sin(angle)).toFloat()
                        )
                    }

                    // Arka plan seviyeleri (pentagon halkalar)
                    val ringSteps = 4
                    for (ring in 1..ringSteps) {
                        val ringRatio = ring / ringSteps.toFloat()
                        val ringPath = Path().apply {
                            for (i in 0 until axisCount) {
                                val p = pointFor(i, ringRatio)
                                if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                            }
                            close()
                        }
                        drawPath(
                            path = ringPath,
                            color = Color(0xFFCBD5E1).copy(alpha = 0.5f),
                            style = Stroke(width = 1.dp.toPx())
                        )
                    }

                    // Eksen çizgileri + etiketler
                    for (i in 0 until axisCount) {
                        val outer = pointFor(i, 1f)
                        drawLine(
                            color = Color(0xFFCBD5E1),
                            start = center,
                            end = outer,
                            strokeWidth = 1.dp.toPx()
                        )

                        val labelPoint = pointFor(i, 1.22f)
                        drawContext.canvas.nativeCanvas.apply {
                            val paint = android.graphics.Paint().apply {
                                color = android.graphics.Color.parseColor("#475569")
                                textSize = 10.5f * density
                                textAlign = android.graphics.Paint.Align.CENTER
                                isAntiAlias = true
                                isFakeBoldText = true
                            }
                            drawText(axisLabels[i], labelPoint.x, labelPoint.y, paint)
                        }
                    }

                    // Oyuncu OS değer poligonu
                    val valuePath = Path().apply {
                        for (i in 0 until axisCount) {
                            val ratio = (values[i].toFloat() / maxLevel).coerceIn(0f, 1f)
                            val p = pointFor(i, ratio)
                            if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                        }
                        close()
                    }
                    drawPath(path = valuePath, color = themeColor.copy(alpha = 0.25f))
                    drawPath(path = valuePath, color = themeColor, style = Stroke(width = 2.5.dp.toPx()))

                    for (i in 0 until axisCount) {
                        val ratio = (values[i].toFloat() / maxLevel).coerceIn(0f, 1f)
                        val p = pointFor(i, ratio)
                        drawCircle(color = themeColor, radius = 3.5.dp.toPx(), center = p)
                        drawCircle(color = Color.White, radius = 1.4.dp.toPx(), center = p)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Seviye özet şeridi
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                axisLabels.forEachIndexed { index, label ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text(label, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, softWrap = false, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                        Text("${values[index]}/$maxLevel", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = themeColor, maxLines = 1, softWrap = false)
                    }
                }
            }
        }
    }
}
