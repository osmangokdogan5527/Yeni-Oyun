package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import com.example.ui.Button3D
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.viewmodel.*
import kotlin.random.Random

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
                                if (isEditing) "İşlemciyi Revize Et (Ücretsiz Revizyon)" else "Öz İşlemci Tasarım Stüdyosu",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                if (isEditing) "Mevcut işlemciyi güncelleyin. Yeniden tape-out maske bedeli alınmaz." else "Özellikleri artırıp azaltarak bütçenize veya tepe güce göre işlemci üretin",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    ProIconButton(onClick = onDismiss) {
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
                        ProOutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("İşlemci / Yonga İsmi") },
                            trailingIcon = {
                                ProIconButton(onClick = { name = generateRandomChipName() }) {
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
                                    ProCard(
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
                        ProCard(
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
                                        ProIconButton(
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
                                        ProIconButton(
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
                                        ProIconButton(
                                            onClick = { if (clockSpeedGhz > 1.4f) clockSpeedGhz = (clockSpeedGhz - 0.2f).coerceAtLeast(1.4f) }
                                        ) {
                                            Icon(Icons.Default.Remove, contentDescription = "Azalt")
                                        }
                                        ProIconButton(
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
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(PowerProfile.values()) { profile ->
                                    val isSelected = powerProfile == profile
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { powerProfile = profile },
                                        label = {
                                            Text(
                                                profile.title.substringBefore(" ("),
                                                fontSize = 11.sp,
                                                softWrap = false
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // 6. OEM Selling Option
                    item {
                        ProCard(
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
                                            ProIconButton(
                                                onClick = { if (oemSalePrice > previewChip.unitCost + 2) oemSalePrice -= 2 }
                                            ) {
                                                Icon(Icons.Default.Remove, contentDescription = null)
                                            }
                                            Text("$$oemSalePrice", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                            ProIconButton(
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
                        ProCard(
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
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Sentetik Güç Puanı", fontSize = 10.sp, color = Color(0xFF94A3B8), softWrap = false, overflow = TextOverflow.Ellipsis)
                                        Text("${"%,d".format(previewChip.performanceScore)} Puan", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8), softWrap = false)
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Üretim Maliyeti", fontSize = 10.sp, color = Color(0xFF94A3B8), softWrap = false, overflow = TextOverflow.Ellipsis)
                                        Text("$${previewChip.unitCost} / Adet", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981), softWrap = false)
                                    }
                                    Column(modifier = Modifier.weight(1.1f)) {
                                        Text("Ar-Ge & Maske Bedeli", fontSize = 10.sp, color = Color(0xFF94A3B8), softWrap = false, overflow = TextOverflow.Ellipsis)
                                        Text("$${"%,d".format(tapeOutCost)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (canAfford) Color(0xFFFBBF24) else Color(0xFFEF4444), softWrap = false)
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
                    ProOutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("İptal")
                    }

                    Button3D(
                        onClick = { onConfirm(previewChip) },
                        modifier = Modifier.weight(2f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = canAfford && name.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                    ) {
                        Text(
                            if (isEditing) "İŞLEMCİYİ GÜNCELLE ($0) ✏️" else if (canAfford) "İŞLEMCİYİ ONAYLA ($${"%,d".format(tapeOutCost)}) ⚡" else "Yetersiz Bütçe ($${"%,d".format(tapeOutCost)})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
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
