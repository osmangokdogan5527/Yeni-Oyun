package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BenchmarkScore
import com.example.model.LeaderboardEntry
import com.example.util.BenchmarkCalculator
import com.example.viewmodel.ActiveModel
import com.example.viewmodel.GameState
import com.example.viewmodel.GameViewModel

enum class BenchmarkTab(val title: String, val icon: String) {
    ANTUTU("AnTuTu v10", "⚡"),
    DXOMARK("DXOMARK Kamera", "📸"),
    GEEKBENCH("Geekbench 6", "🧠"),
    THERMAL_BATTERY("Termal & Batarya", "🌡️"),
    LEADERBOARD("Küresel Sıralama", "🏆")
}

@Composable
fun BenchmarkScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel
) {
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableStateOf(BenchmarkTab.ANTUTU) }

    // Combine player's active and completed models
    val playerModels = state.activeModels.map { model ->
        val score = model.benchmarkScore ?: BenchmarkCalculator.calculateScore(model.specs, state.customOs.overallTechScore)
        LeaderboardEntry(
            id = model.id,
            modelName = model.specs.name,
            brandName = state.companyName,
            brandColorHex = state.companyBrandColorHex,
            isPlayer = true,
            releaseYear = model.launchYear,
            price = model.specs.price,
            tierTitle = model.specs.tier.title,
            antutuScore = score.totalAntutuScore,
            dxomarkScore = score.totalDxomarkScore,
            geekbenchSingle = score.geekbenchSingle,
            geekbenchMulti = score.geekbenchMulti,
            batteryLifeHours = score.screenOnTimeHours,
            peakTempCelsius = score.peakTempCelsius,
            socName = model.specs.processor,
            cameraSummary = model.specs.camera,
            verdict = if (score.totalAntutuScore > 500000) "Sektörün en güçlü canavarı!" else "Dengeli ve optimize amiral gemisi."
        )
    }

    // Generate competitor benchmark entries
    val competitorEntries = remember(state.competitors, state.year, state.competitorReleases) {
        state.competitors.map { comp ->
            val recentRelease = state.competitorReleases.firstOrNull { it.companyName == comp.name }
            BenchmarkCalculator.createCompetitorBenchmark(comp, state.year, recentRelease)
        }
    }

    // All entries
    val allEntries = (playerModels + competitorEntries)

    // Selected model for in-depth benchmark review
    var selectedDetailModel by remember { mutableStateOf<ActiveModel?>(state.activeModels.firstOrNull()) }

    // Sync selected model when list updates
    LaunchedEffect(state.activeModels) {
        if (selectedDetailModel == null && state.activeModels.isNotEmpty()) {
            selectedDetailModel = state.activeModels.first()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Benchmark Header Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 3.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            Color(0xFFFF5722),
                                            Color(0xFFFF9800)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Speed,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Benchmark & Test Laboratuvarı",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "AnTuTu • DXOMARK • Geekbench • Termal",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Lab badge
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "v10.4 PRO",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BenchmarkTab.values().forEach { tab ->
                        val isSelected = selectedTab == tab
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedTab = tab },
                            label = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(tab.icon, fontSize = 13.sp)
                                    Text(
                                        tab.title,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.sp
                                    )
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }
        }

        // Tab Content
        if (state.activeModels.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "🧪", fontSize = 48.sp)
                        Text(
                            text = "Test Edilecek Cihaz Bulunamadı",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Benchmark laboratuvarında sentetik puanları, kamera kalitesini ve termal değerleri görmek için önce 'Cihazlar' sekmesinden bir telefon tasarlayıp üretin.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            when (selectedTab) {
                BenchmarkTab.ANTUTU -> AnTuTuView(
                    activeModels = state.activeModels,
                    selectedModel = selectedDetailModel ?: state.activeModels.first(),
                    onSelectModel = { selectedDetailModel = it },
                    allEntries = allEntries,
                    customOsScore = state.customOs.overallTechScore
                )
                BenchmarkTab.DXOMARK -> DxomarkView(
                    activeModels = state.activeModels,
                    selectedModel = selectedDetailModel ?: state.activeModels.first(),
                    onSelectModel = { selectedDetailModel = it },
                    allEntries = allEntries
                )
                BenchmarkTab.GEEKBENCH -> GeekbenchView(
                    activeModels = state.activeModels,
                    selectedModel = selectedDetailModel ?: state.activeModels.first(),
                    onSelectModel = { selectedDetailModel = it },
                    allEntries = allEntries
                )
                BenchmarkTab.THERMAL_BATTERY -> ThermalBatteryView(
                    activeModels = state.activeModels,
                    selectedModel = selectedDetailModel ?: state.activeModels.first(),
                    onSelectModel = { selectedDetailModel = it }
                )
                BenchmarkTab.LEADERBOARD -> LeaderboardView(
                    allEntries = allEntries,
                    currentYear = state.year
                )
            }
        }
    }
}

@Composable
private fun DeviceSelectorChips(
    activeModels: List<ActiveModel>,
    selectedModel: ActiveModel,
    onSelectModel: (ActiveModel) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "📱 Test Edilen Cihazı Seçin:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            activeModels.forEach { model ->
                val isSelected = model.id == selectedModel.id
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.clickable { onSelectModel(model) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(model.specs.colorHex))
                        )
                        Text(
                            text = model.specs.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 1. ANTUTU VIEW
// -------------------------------------------------------------
@Composable
private fun AnTuTuView(
    activeModels: List<ActiveModel>,
    selectedModel: ActiveModel,
    onSelectModel: (ActiveModel) -> Unit,
    allEntries: List<LeaderboardEntry>,
    customOsScore: Int
) {
    val score = selectedModel.benchmarkScore ?: BenchmarkCalculator.calculateScore(selectedModel.specs, customOsScore)
    val maxScore = allEntries.maxOfOrNull { it.antutuScore }?.coerceAtLeast(score.totalAntutuScore) ?: 1000000

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            DeviceSelectorChips(activeModels, selectedModel, onSelectModel)
        }

        // Main AnTuTu Dial / Total Score Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AnTuTu Benchmark v10",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF5722)
                        )
                        Surface(
                            color = Color(0xFFFF5722).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "GENEL SKOR",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF5722)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Big Score Display with animated gradient look
                    Text(
                        text = "%,d".format(score.totalAntutuScore),
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "${selectedModel.specs.name} • ${selectedModel.specs.processor}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(18.dp))

                    // Breakdown bars
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ScoreBar(
                            title = "CPU (İşlemci Gücü)",
                            score = score.cpuScore,
                            total = score.totalAntutuScore,
                            barColor = Color(0xFF3B82F6),
                            subtext = selectedModel.specs.processor
                        )
                        ScoreBar(
                            title = "GPU (Grafik & Oyun)",
                            score = score.gpuScore,
                            total = score.totalAntutuScore,
                            barColor = Color(0xFF10B981),
                            subtext = if (selectedModel.specs.style == "Oyuncu") "🎮 Gaming Boost Aktif" else "Standart GPU"
                        )
                        ScoreBar(
                            title = "MEM (RAM & Depolama)",
                            score = score.memScore,
                            total = score.totalAntutuScore,
                            barColor = Color(0xFFF59E0B),
                            subtext = "${selectedModel.specs.ram} • ${selectedModel.specs.storage}"
                        )
                        ScoreBar(
                            title = "UX (Kullanıcı Deneyimi & Arayüz)",
                            score = score.uxScore,
                            total = score.totalAntutuScore,
                            barColor = Color(0xFF8B5CF6),
                            subtext = "${selectedModel.specs.osName} (Opt: %$customOsScore)"
                        )
                    }
                }
            }
        }

        // Competitor Comparison Bar Chart
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "📊 Pazar Kıyaslaması (AnTuTu Top 5)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    val topEntries = allEntries.sortedByDescending { it.antutuScore }.take(5)
                    topEntries.forEachIndexed { index, entry ->
                        val isCurrent = entry.modelName == selectedModel.specs.name
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "#${index + 1} ${entry.brandName} ${entry.modelName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "%,d".format(entry.antutuScore),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { (entry.antutuScore.toFloat() / maxScore.toFloat()).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = if (isCurrent) MaterialTheme.colorScheme.primary else Color(entry.brandColorHex),
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. DXOMARK VIEW
// -------------------------------------------------------------
@Composable
private fun DxomarkView(
    activeModels: List<ActiveModel>,
    selectedModel: ActiveModel,
    onSelectModel: (ActiveModel) -> Unit,
    allEntries: List<LeaderboardEntry>
) {
    val score = selectedModel.benchmarkScore ?: BenchmarkCalculator.calculateScore(selectedModel.specs)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            DeviceSelectorChips(activeModels, selectedModel, onSelectModel)
        }

        // DXOMARK Hero Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DXOMARK Camera Suite",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0284C7)
                        )
                        Surface(
                            color = Color(0xFF0284C7).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "GLOBAL RANK #1",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0284C7)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "${score.totalDxomarkScore}",
                        fontSize = 52.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0284C7)
                    )

                    Text(
                        text = "DXOMARK Kamera Skoru",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = selectedModel.specs.camera,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(16.dp))

                    // 4 Subscores grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        DxomarkSubscore(title = "Fotoğraf", score = score.photoScore, icon = "📷")
                        DxomarkSubscore(title = "Video", score = score.videoScore, icon = "🎥")
                        DxomarkSubscore(title = "Zoom", score = score.zoomScore, icon = "🔍")
                        DxomarkSubscore(title = "Gece", score = score.nightScore, icon = "🌙")
                    }
                }
            }
        }

        // Camera verdict & test details
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "🔬 Laboratuvar İnceleme Notu",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = when {
                            score.totalDxomarkScore >= 140 -> "Olağanüstü dinamik aralık, keskin portre ayrımı ve periskop yakınlaştırmada sıfır gürültü. Sektör standardını belirliyor."
                            score.totalDxomarkScore >= 115 -> "Canlı renk doğruluğu, hızlı otomatik odaklama ve düşük ışıkta başarılı pozlama sunuyor."
                            score.totalDxomarkScore >= 85 -> "Gündüz çekimlerinde tatminkar detaylar sunarken, gece modunda hafif grenlenme gözlemlendi."
                            else -> "Temel sosyal medya paylaşımları için yeterli, profesyonel modda geliştirilmeye açık."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun DxomarkSubscore(title: String, score: Int, icon: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = icon, fontSize = 22.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$score",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = title,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// -------------------------------------------------------------
// 3. GEEKBENCH VIEW
// -------------------------------------------------------------
@Composable
private fun GeekbenchView(
    activeModels: List<ActiveModel>,
    selectedModel: ActiveModel,
    onSelectModel: (ActiveModel) -> Unit,
    allEntries: List<LeaderboardEntry>
) {
    val score = selectedModel.benchmarkScore ?: BenchmarkCalculator.calculateScore(selectedModel.specs)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            DeviceSelectorChips(activeModels, selectedModel, onSelectModel)
        }

        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Geekbench 6 CPU Test",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6366F1)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "%,d".format(score.geekbenchSingle),
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF6366F1)
                            )
                            Text(
                                text = "Single-Core Skor",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        VerticalDivider(modifier = Modifier.height(50.dp), color = MaterialTheme.colorScheme.outlineVariant)

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "%,d".format(score.geekbenchMulti),
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Multi-Core Skor",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "İşlemci: ${selectedModel.specs.processor}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. THERMAL & BATTERY VIEW
// -------------------------------------------------------------
@Composable
private fun ThermalBatteryView(
    activeModels: List<ActiveModel>,
    selectedModel: ActiveModel,
    onSelectModel: (ActiveModel) -> Unit
) {
    val score = selectedModel.benchmarkScore ?: BenchmarkCalculator.calculateScore(selectedModel.specs)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            DeviceSelectorChips(activeModels, selectedModel, onSelectModel)
        }

        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "🌡️ Sıcaklık & Termal Kararlılık Testi",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Tepe Sıcaklık", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "${score.peakTempCelsius}°C",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (score.peakTempCelsius > 42f) MaterialTheme.colorScheme.error else Color(0xFF10B981)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Termal Throttling (Kısılma)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "%${score.thermalThrottlingPercent}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (score.thermalThrottlingPercent > 10) Color(0xFFF59E0B) else Color(0xFF10B981)
                            )
                        }
                    }

                    LinearProgressIndicator(
                        progress = { (score.peakTempCelsius / 50f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = if (score.peakTempCelsius > 42f) MaterialTheme.colorScheme.error else Color(0xFF10B981),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Text(
                        text = "Kasa Materyali: ${selectedModel.specs.material} • Soğutma: ${if (selectedModel.specs.style == "Oyuncu") "Vapor Chamber + Bakır Boru" else "Grafit Tabaka"}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "🔋 Batarya & Ekran Açık Kalma Süresi (SoT)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Ekran Süresi (SoT)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "${score.screenOnTimeHours} Saat",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Kapasite", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = selectedModel.specs.batteryCapacity,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Text(
                        text = "Ekran Teknolojisi: ${selectedModel.specs.display} • Dayanıklılık Skoru: ${score.durabilityScore}/100",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 5. GLOBAL LEADERBOARD VIEW
// -------------------------------------------------------------
@Composable
private fun LeaderboardView(
    allEntries: List<LeaderboardEntry>,
    currentYear: Int
) {
    val sorted = remember(allEntries) {
        allEntries.sortedByDescending { it.antutuScore }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "🏆 $currentYear Küresel Amiral Gemisi Sıralaması",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tüm piyasadaki modellerin AnTuTu ve DXOMARK sentetik güç tablosu.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(sorted.mapIndexed { idx, item -> item.copy(rank = idx + 1) }) { entry ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (entry.isPlayer) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                ),
                border = if (entry.isPlayer) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Rank Badge
                        Surface(
                            shape = CircleShape,
                            color = when (entry.rank) {
                                1 -> Color(0xFFFFD700)
                                2 -> Color(0xFFC0C0C0)
                                3 -> Color(0xFFCD7F32)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${entry.rank}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (entry.rank <= 3) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = entry.brandName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = entry.modelName,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (entry.isPlayer) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "SEN",
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "${entry.socName} • $${entry.price}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "%,d".format(entry.antutuScore),
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "📸 ${entry.dxomarkScore} Puan",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0284C7)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreBar(
    title: String,
    score: Int,
    total: Int,
    barColor: Color,
    subtext: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "%,d".format(score), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = barColor)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { (score.toFloat() / total.toFloat() * 2.5f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = barColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Text(
            text = subtext,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
