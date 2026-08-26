package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BenchmarkScore
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate900
import com.example.viewmodel.ActiveModel
import com.example.viewmodel.GameViewModel

/**
 * "Test Lab" sekmesi: piyasadaki aktif modellerin donanım/yazılım benchmark
 * puanlarını (performans, ekran, kamera, batarya, yazılım) karşılaştırmalı
 * olarak gösterir.
 */
@Composable
fun BenchmarkScreen(viewModel: GameViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsState()
    val modelsWithBenchmark = state.activeModels.filter { it.benchmarkScore != null }

    if (modelsWithBenchmark.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize().padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Science, contentDescription = null, tint = Slate400, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Henüz test edilecek bir cihaz yok.",
                    color = Slate600,
                    fontSize = 14.sp
                )
                Text(
                    text = "Bir telefon ürettiğinde burada karşılaştırmalı testler görünecek.",
                    color = Slate400,
                    fontSize = 12.sp
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Test Lab",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )
            Text(
                text = "Piyasadaki cihazlarının bağımsız laboratuvar sonuçları.",
                fontSize = 12.sp,
                color = Slate600
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(modelsWithBenchmark, key = { it.id }) { model ->
            BenchmarkCard(model)
        }
    }
}

@Composable
private fun BenchmarkCard(model: ActiveModel) {
    val score = model.benchmarkScore ?: return

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(model.specs.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Slate900)
                    Text(
                        text = "${model.specs.processor} • ${model.specs.ramCapacity}",
                        fontSize = 11.sp,
                        color = Slate600
                    )
                }
                OverallScoreBadge(score.overallScore)
            }

            Spacer(modifier = Modifier.height(14.dp))

            BenchmarkBar("Performans", score.performanceScore)
            BenchmarkBar("Ekran", score.displayScore)
            BenchmarkBar("Kamera", score.cameraScore)
            BenchmarkBar("Batarya", score.batteryScore)
            BenchmarkBar("Yazılım", score.softwareScore)
        }
    }
}

@Composable
private fun OverallScoreBadge(overallScore: Int) {
    val color = scoreColor(overallScore)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = "$overallScore",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun BenchmarkBar(label: String, value: Int) {
    val color = scoreColor(value)
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 11.sp, color = Slate600)
            Text("$value", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { value / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
    }
}

private fun scoreColor(value: Int): Color = when {
    value >= 80 -> Color(0xFF10B981)
    value >= 55 -> Color(0xFFF59E0B)
    else -> Color(0xFFEF4444)
}
