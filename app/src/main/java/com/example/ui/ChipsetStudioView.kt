package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.*
import com.example.ui.Button3D
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*
import com.example.viewmodel.*
import kotlin.random.Random

@Composable
fun ChipsetStudioView(
    state: GameState,
    onSaveChipset: (CustomChipset) -> Unit,
    onDeleteChipset: (String) -> Unit,
    onToggleOemSale: (String, Boolean, Int) -> Unit,
    onUnarchiveChipset: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showDesignerDialog by remember { mutableStateOf(false) }
    var editingChipset by remember { mutableStateOf<CustomChipset?>(null) }
    var selectedTierFilter by remember { mutableStateOf<ChipsetTier?>(null) }

    val totalOemUnits = state.customChipsets.sumOf { it.totalUnitsSoldToThirdParties }
    val totalOemRevenue = state.customChipsets.sumOf { it.totalOemRevenueEarned }
    val lastPeriodOemIncome = state.customChipsets.sumOf { it.lastPeriodOemIncome }
    val lastPeriodUnits = state.customChipsets.sumOf { it.lastPeriodUnitsSold }

    val filteredList = state.customChipsets.filter {
        selectedTierFilter == null || it.tier == selectedTierFilter
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- TOP HERO BANNER & STATS ---
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.5.dp,
                    Brush.horizontalGradient(listOf(Color(0xFF38BDF8), Color(0xFF6366F1), Color(0xFFA855F7))),
                    RoundedCornerShape(20.dp)
                )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .background(Color(0xFF38BDF8).copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Memory,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Öz İşlemci Tasarım Stüdyosu (In-House Silicon)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Kendi mobil işlemcinizi tasarlayın, telefonlarınızda kullanın veya dış pazara (OEM) satın!",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Tasarım Sayısı", fontSize = 10.sp, color = Color(0xFF94A3B8), maxLines = 1, softWrap = false, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                            Text("${state.customChipsets.size} Model", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8), maxLines = 1, softWrap = false)
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1.2f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Son 2 Hafta Dış Satış", fontSize = 9.sp, color = Color(0xFF94A3B8), maxLines = 1, softWrap = false, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                            Text(
                                if (lastPeriodOemIncome > 0) "+$${"%,d".format(lastPeriodOemIncome)}" else "$0",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981),
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1.2f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Toplam OEM Geliri", fontSize = 9.sp, color = Color(0xFF94A3B8), maxLines = 1, softWrap = false, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                            Text(
                                "$${"%,d".format(totalOemRevenue)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFBBF24),
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }

                // CTA Button: Tasarla
                Button3D(
                    onClick = {
                        editingChipset = null
                        showDesignerDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "YENİ ÖZEL İŞLEMCİ TASARLA (SILICON STUDIO)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
        }

        // --- FILTER CHIPS ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = selectedTierFilter == null,
                onClick = { selectedTierFilter = null },
                label = { Text("Tümü (${state.customChipsets.size})", fontSize = 11.sp) }
            )
            ChipsetTier.values().forEach { tier ->
                val count = state.customChipsets.count { it.tier == tier }
                FilterChip(
                    selected = selectedTierFilter == tier,
                    onClick = { selectedTierFilter = if (selectedTierFilter == tier) null else tier },
                    label = { Text("${tier.badge} ${tier.title.substringBefore(" (")} ($count)", fontSize = 11.sp) }
                )
            }
        }

        // --- EMPTY STATE OR CHIPSET LIST ---
        if (state.customChipsets.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.Memory,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(56.dp)
                    )
                    Text(
                        text = "Henüz Özel Bir İşlemci Tasarlamadınız",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "İster ucuz bütçeli Lite çiplerle fason üreticilere milyonlarca adet satın, ister amiral gemisi işlemcilerle sentetik test rekorları kırın!",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                    Button3D(
                        onClick = {
                            editingChipset = null
                            showDesignerDialog = true
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("İlk İşlemcini Tasarla ⚡", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                filteredList.forEach { chipset ->
                    ChipsetCard(
                        chipset = chipset,
                        onEdit = {
                            editingChipset = chipset
                            showDesignerDialog = true
                        },
                        onDelete = { onDeleteChipset(chipset.id) },
                        onUnarchive = { onUnarchiveChipset(chipset.id) },
                        onToggleOem = { isActive, price ->
                            onToggleOemSale(chipset.id, isActive, price)
                        }
                    )
                }
            }
        }
    }

    // --- DESIGNER DIALOG ---
    if (showDesignerDialog) {
        ChipsetDesignerDialog(
            existingChipset = editingChipset,
            currentYear = state.year,
            currentBudget = state.budget,
            unlockedTech = state.unlockedTech,
            onDismiss = { showDesignerDialog = false },
            onConfirm = { newChip ->
                onSaveChipset(newChip)
                showDesignerDialog = false
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipsetCard(
    chipset: CustomChipset,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onUnarchive: () -> Unit = {},
    onToggleOem: (Boolean, Int) -> Unit
) {
    var isOemExpanded by remember { mutableStateOf(chipset.isOemSaleActive) }
    var currentSalePrice by remember { mutableStateOf(chipset.oemSalePrice) }

    val tierBorderColor = if (chipset.isArchived) Color(0xFF64748B) else when (chipset.tier) {
        ChipsetTier.ENTRY_LITE -> Color(0xFF10B981)
        ChipsetTier.MID_RANGE -> Color(0xFF3B82F6)
        ChipsetTier.FLAGSHIP_PRO -> Color(0xFFA855F7)
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (chipset.isArchived) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, tierBorderColor.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(tierBorderColor.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (chipset.isArchived) "📦" else chipset.tier.badge, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = chipset.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (chipset.isArchived) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurface
                            )
                            if (chipset.isArchived) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = Color(0xFF475569),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        "Arşivlendi",
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            } else if (chipset.generation > 1) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        "Gen ${chipset.generation}",
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                        Text(
                            text = "${chipset.tier.title} • ${chipset.createdYear}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (chipset.isArchived) {
                        IconButton(onClick = onUnarchive, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Restore, contentDescription = "Arşivden Çıkar", modifier = Modifier.size(16.dp), tint = Color(0xFF10B981))
                        }
                    } else {
                        IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Düzenle", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Sil / Arşivle", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            // Specs Summary Chips
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SpecBadge("🔬 ${chipset.processNode.nodeName}")
                SpecBadge("⚡ ${chipset.coreCount} Çekirdek @ ${chipset.clockSpeedGhz}GHz")
                SpecBadge("🎮 ${chipset.gpuArchitecture.title.substringBefore(" (")}")
                if (chipset.npuArchitecture != NpuArchitecture.NO_NPU) {
                    SpecBadge("🧠 ${chipset.npuArchitecture.tops} TOPS NPU")
                }
                SpecBadge("🔋 ${chipset.powerProfile.title.substringBefore(" (")}")
            }

            // Key Metrics Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Sentetik Güç Puanı", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${"%,d".format(chipset.performanceScore)} Puan", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = tierBorderColor)
                }
                Box(modifier = Modifier.width(1.dp).height(24.dp).background(MaterialTheme.colorScheme.outlineVariant))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Telefon Birim Maliyeti", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("$${chipset.unitCost} / Adet", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }

            // OEM Sales Section
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (chipset.isOemSaleActive) Color(0xFF064E3B).copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Storefront,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (chipset.isOemSaleActive) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Dış Markalara Satış (OEM Foundry)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (chipset.isOemSaleActive) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = chipset.isOemSaleActive,
                            onCheckedChange = { active ->
                                onToggleOem(active, currentSalePrice)
                            },
                            modifier = Modifier.height(24.dp)
                        )
                    }

                    if (chipset.isOemSaleActive) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Birim Satış Fiyatı", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    "$$currentSalePrice (Birim Kâr: +$${chipset.profitPerUnit})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981)
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        if (currentSalePrice > chipset.unitCost + 2) {
                                            currentSalePrice -= 2
                                            onToggleOem(true, currentSalePrice)
                                        }
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(14.dp))
                                }
                                Text("$$currentSalePrice", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                IconButton(
                                    onClick = {
                                        currentSalePrice += 2
                                        onToggleOem(true, currentSalePrice)
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                }
                            }
                        }

                        // Stats
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Son 2 Hafta: ${"%,d".format(chipset.lastPeriodUnitsSold)} adet (+$$${"%,d".format(chipset.lastPeriodOemIncome)})",
                                fontSize = 11.sp,
                                color = Color(0xFF34D399)
                            )
                            Text(
                                "Toplam: ${"%,d".format(chipset.totalUnitsSoldToThirdParties)} adet",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SpecBadge(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
