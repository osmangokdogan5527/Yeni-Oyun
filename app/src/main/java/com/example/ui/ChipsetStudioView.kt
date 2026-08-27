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
                                text = "Öz Tasarım Yonga Stüdyosu (In-House Silicon)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Kendi mimarinizi tasarlayın, telefonlarınızda kullanın veya dış pazara (OEM) satın!",
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
                            Text("Tasarım Sayısı", fontSize = 10.sp, color = Color(0xFF94A3B8))
                            Text("${state.customChipsets.size} Model", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1.2f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Son 2 Hafta Dış Satış", fontSize = 10.sp, color = Color(0xFF94A3B8))
                            Text(
                                if (lastPeriodOemIncome > 0) "+$${"%,d".format(lastPeriodOemIncome)}" else "$0",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1.2f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Toplam OEM Geliri", fontSize = 10.sp, color = Color(0xFF94A3B8))
                            Text(
                                "$${"%,d".format(totalOemRevenue)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFBBF24)
                            )
                        }
                    }
                }

                // CTA Button: Tasarla
                Button(
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
                    Button(
                        onClick = {
                            editingChipset = null
                            showDesignerDialog = true
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("İlk Yonganı Tasarla ⚡", fontWeight = FontWeight.Bold)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChipsetDesignerDialog(
    existingChipset: CustomChipset?,
    currentYear: Int,
    currentBudget: Long,
    unlockedTech: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (CustomChipset) -> Unit
) {
    var name by remember {
        mutableStateOf(
            existingChipset?.name ?: generateRandomChipName()
        )
    }
    var selectedTier by remember { mutableStateOf(existingChipset?.tier ?: ChipsetTier.MID_RANGE) }
    var selectedNode by remember {
        mutableStateOf(
            existingChipset?.processNode ?: getDefaultNodeForYear(currentYear)
        )
    }
    var coreCount by remember { mutableStateOf(existingChipset?.coreCount ?: selectedTier.defaultCoreCount) }
    var clockSpeedGhz by remember { mutableStateOf(existingChipset?.clockSpeedGhz ?: selectedTier.defaultClockSpeed) }
    var gpuArchitecture by remember { mutableStateOf(existingChipset?.gpuArchitecture ?: GpuArchitecture.PERFORMANCE_GPU) }
    var npuArchitecture by remember { mutableStateOf(existingChipset?.npuArchitecture ?: NpuArchitecture.NO_NPU) }
    var powerProfile by remember { mutableStateOf(existingChipset?.powerProfile ?: PowerProfile.BALANCED) }
    var isOemSaleActive by remember { mutableStateOf(existingChipset?.isOemSaleActive ?: true) }
    var oemSalePrice by remember { mutableStateOf(existingChipset?.oemSalePrice ?: 38) }

    // Live preview object
    val previewChip = CustomChipset(
        id = existingChipset?.id ?: "chip_${System.currentTimeMillis()}_${Random.nextInt(1000, 9999)}",
        name = name.ifBlank { "Apex Chip" },
        tier = selectedTier,
        processNode = selectedNode,
        coreCount = coreCount,
        clockSpeedGhz = clockSpeedGhz,
        gpuArchitecture = gpuArchitecture,
        npuArchitecture = npuArchitecture,
        powerProfile = powerProfile,
        generation = if (existingChipset != null) existingChipset.generation + 1 else 1,
        createdYear = existingChipset?.createdYear ?: currentYear,
        createdMonth = existingChipset?.createdMonth ?: 1,
        isOemSaleActive = isOemSaleActive,
        oemSalePrice = oemSalePrice.coerceAtLeast(previewChipUnitCostFallback(selectedNode, coreCount, clockSpeedGhz, gpuArchitecture, npuArchitecture)),
        isArchived = existingChipset?.isArchived ?: false
    )

    val isEditing = existingChipset != null
    val tapeOutCost = if (isEditing) 0L else previewChip.tapeOutCost
    val canAfford = isEditing || currentBudget >= previewChip.tapeOutCost

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Memory,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                if (isEditing) "Yongayı Revize Et (Ücretsiz Revizyon)" else "Öz Tasarım Yonga Stüdyosu",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                if (isEditing) "Mevcut yongayı güncelleyin. Yeniden tape-out maske bedeli alınmaz." else "Özellikleri artırıp azaltarak bütçenize veya tepe güce göre çip üretin",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Config Form
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Chip Name & Randomizer
                    item {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("İşlemci / Yonga İsmi") },
                            trailingIcon = {
                                IconButton(onClick = { name = generateRandomChipName() }) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Rastgele İsim")
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // 2. Chipset Tier (Segment)
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("1. Hedef Segment & Pazar Tipi", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ChipsetTier.values().forEach { tier ->
                                    val isSelected = selectedTier == tier
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                selectedTier = tier
                                                coreCount = tier.defaultCoreCount
                                                clockSpeedGhz = tier.defaultClockSpeed
                                                oemSalePrice = when (tier) {
                                                    ChipsetTier.ENTRY_LITE -> 28
                                                    ChipsetTier.MID_RANGE -> 48
                                                    ChipsetTier.FLAGSHIP_PRO -> 85
                                                }
                                            },
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        border = if (isSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(10.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(tier.badge, fontSize = 20.sp)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                tier.title.substringBefore(" ("),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 3. Process Node (Nanometre Litografi)
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("2. Üretim Mimarisi (Litografi / nm)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("${selectedNode.nm}nm (${(selectedNode.efficiencyBonus * 100).toInt()}% Verim)", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(ProcessNode.values()) { node ->
                                    val isAvailable = currentYear >= node.minYear || (node.requiredTech != null && unlockedTech.contains(node.requiredTech))
                                    val isSelected = selectedNode == node
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { if (isAvailable) selectedNode = node },
                                        enabled = isAvailable,
                                        label = {
                                            Column(modifier = Modifier.padding(vertical = 2.dp)) {
                                                Text(node.nodeName, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                                if (!isAvailable) {
                                                    Text("${node.minYear}+", fontSize = 9.sp, color = MaterialTheme.colorScheme.error)
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // 4. CPU Cores & Clock Speed (Özellikleri Artırıp Azaltma)
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("3. CPU Çekirdek & Saat Frekansı Ayarı", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                                // Cores Stepper
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("CPU Çekirdek Sayısı", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(
                                            when (coreCount) {
                                                4 -> "4 Çekirdek (Quad-Core Lite)"
                                                6 -> "6 Çekirdek (Hexa-Core)"
                                                8 -> "8 Çekirdek (Octa-Core Standart)"
                                                else -> "10 Çekirdek (Deca-Core Canavar)"
                                            },
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = {
                                                coreCount = when (coreCount) {
                                                    10 -> 8
                                                    8 -> 6
                                                    6 -> 4
                                                    else -> 4
                                                }
                                            },
                                            enabled = coreCount > 4
                                        ) {
                                            Icon(Icons.Default.Remove, contentDescription = "Azalt")
                                        }
                                        Text("$coreCount", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                        IconButton(
                                            onClick = {
                                                coreCount = when (coreCount) {
                                                    4 -> 6
                                                    6 -> 8
                                                    8 -> 10
                                                    else -> 10
                                                }
                                            },
                                            enabled = coreCount < 10
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = "Artır")
                                        }
                                    }
                                }

                                Divider()

                                // Clock Speed Slider / Stepper
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Maksimum Saat Hızı (Frekans)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(
                                            "${"%.1f".format(clockSpeedGhz)} GHz",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = { if (clockSpeedGhz > 1.4f) clockSpeedGhz = (clockSpeedGhz - 0.2f).coerceAtLeast(1.4f) }
                                        ) {
                                            Icon(Icons.Default.Remove, contentDescription = "Azalt")
                                        }
                                        IconButton(
                                            onClick = { if (clockSpeedGhz < 3.8f) clockSpeedGhz = (clockSpeedGhz + 0.2f).coerceAtMost(3.8f) }
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = "Artır")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 5. GPU & NPU & Power
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("4. Grafik, Yapay Zeka & Güç Profili", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                            // GPU Selector
                            Text("GPU Grafik Birimi", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                GpuArchitecture.values().forEach { gpu ->
                                    val isSelected = gpuArchitecture == gpu
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                            .clickable { gpuArchitecture = gpu }
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(gpu.title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text(gpu.description, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Text("+$${gpu.extraCost}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // NPU Selector
                            Text("NPU Yapay Zeka Hızlandırıcı", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                NpuArchitecture.values().forEach { npu ->
                                    val isSelected = npuArchitecture == npu
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                            .clickable { npuArchitecture = npu }
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(npu.title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text(npu.description, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Text(if (npu.extraCost > 0) "+$${npu.extraCost}" else "Dahil", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Power Profile
                            Text("Güç & Isı / TDP Profili", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                PowerProfile.values().forEach { profile ->
                                    val isSelected = powerProfile == profile
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { powerProfile = profile },
                                        label = { Text(profile.title.substringBefore(" ("), fontSize = 11.sp) }
                                    )
                                }
                            }
                        }
                    }

                    // 6. OEM Selling Option
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B).copy(alpha = 0.25f)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Dış Markalara / Pazara Satış (OEM)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("Samsung, Xiaomi, Oppo ve fason üreticilere çip satarak düzenli gelir elde edin.", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Switch(checked = isOemSaleActive, onCheckedChange = { isOemSaleActive = it })
                                }

                                if (isOemSaleActive) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Birim Satış Fiyatı", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            val profit = (oemSalePrice - previewChip.unitCost).coerceAtLeast(0)
                                            Text(
                                                "$$oemSalePrice (Birim Kâr: +$$profit)",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF10B981)
                                            )
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(
                                                onClick = { if (oemSalePrice > previewChip.unitCost + 2) oemSalePrice -= 2 }
                                            ) {
                                                Icon(Icons.Default.Remove, contentDescription = null)
                                            }
                                            Text("$$oemSalePrice", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                            IconButton(
                                                onClick = { oemSalePrice += 2 }
                                            ) {
                                                Icon(Icons.Default.Add, contentDescription = null)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 7. Live Preview Calculated Card
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Canlı Tasarım & Performans Özeti", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Sentetik Güç Puanı", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                        Text("${"%,d".format(previewChip.performanceScore)} Puan", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                                    }
                                    Column {
                                        Text("Telefon Üretim Maliyeti", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                        Text("$${previewChip.unitCost} / Adet", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                                    }
                                    Column {
                                        Text("Ar-Ge & Maske Maliyeti", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                        Text("$${"%,d".format(tapeOutCost)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (canAfford) Color(0xFFFBBF24) else Color(0xFFEF4444))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("İptal")
                    }

                    Button(
                        onClick = { onConfirm(previewChip) },
                        modifier = Modifier.weight(2f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = canAfford && name.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                    ) {
                        Text(
                            if (isEditing) "GÜNCELLEMEYİ ONAYLA ($0) ✏️" else if (canAfford) "YONGAYI ONAYLA ($${"%,d".format(tapeOutCost)}) ⚡" else "Yetersiz Bütçe ($${"%,d".format(tapeOutCost)})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

private fun previewChipUnitCostFallback(
    node: ProcessNode,
    cores: Int,
    speed: Float,
    gpu: GpuArchitecture,
    npu: NpuArchitecture
): Int {
    val baseNodeCost = node.baseCost
    val coreCost = (cores * 2.5f).toInt()
    val speedCost = ((speed - 1.4f) * 9f).toInt().coerceAtLeast(0)
    return (baseNodeCost + coreCost + speedCost + gpu.extraCost + npu.extraCost).coerceIn(12, 350)
}

private fun getDefaultNodeForYear(year: Int): ProcessNode {
    return when {
        year >= 2026 -> ProcessNode.NM_2
        year >= 2024 -> ProcessNode.NM_3
        year >= 2023 -> ProcessNode.NM_4
        year >= 2021 -> ProcessNode.NM_5
        year >= 2019 -> ProcessNode.NM_7
        year >= 2017 -> ProcessNode.NM_10
        year >= 2015 -> ProcessNode.NM_14
        year >= 2012 -> ProcessNode.NM_28
        else -> ProcessNode.NM_45
    }
}

private fun generateRandomChipName(): String {
    val prefixes = listOf("Apex", "Nova", "Titan", "Pulse", "Vortex", "Helio", "Kirin", "Aegis", "Quantum", "Nexus", "Cortex", "Hyper")
    val suffixes = listOf("Silicon", "Core", "Bionic", "GenX", "Ultra", "Lite", "Prime", "Neural", "Fusion", "Max", "Extreme")
    val num = Random.nextInt(1, 10)
    return "${prefixes.random()} ${suffixes.random()} $num"
}
