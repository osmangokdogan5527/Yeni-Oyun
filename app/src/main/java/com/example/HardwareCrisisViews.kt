package com.example

import com.example.ui.ProOutlinedButton
import com.example.ui.ProCard
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import com.example.ui.Button3D
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*
import com.example.viewmodel.ActiveModel
import com.example.viewmodel.CrisisResolutionStrategy
import com.example.viewmodel.HardwareCrisis

@Composable
fun HardwareCrisisManagementDialog(
    crisis: HardwareCrisis,
    model: ActiveModel?,
    currentBudget: Long,
    onDismiss: () -> Unit,
    onApplyStrategy: (CrisisResolutionStrategy) -> Unit
) {
    var selectedStrategy by remember { mutableStateOf<CrisisResolutionStrategy?>(null) }
    val scrollState = rememberScrollState()

    val selectedStrategyCost = selectedStrategy?.let { strategy ->
        when (strategy) {
            CrisisResolutionStrategy.SOFTWARE_PATCH_LIMIT -> 50000L
            CrisisResolutionStrategy.FREE_SERVICE_REPAIR -> (crisis.affectedUnitsCount.toLong() * 25L).coerceAtLeast(100000L)
            CrisisResolutionStrategy.FULL_RECALL_REFUND -> {
                val unitRefund = model?.specs?.price?.toLong() ?: 300L
                (crisis.affectedUnitsCount.toLong() * unitRefund) + ((model?.remainingStock ?: 0).toLong() * 50L)
            }
        }
    } ?: 0L
    val canAffordSelected = selectedStrategy == null || currentBudget >= selectedStrategyCost

    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        ProCard(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Fixed Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFFEF2F2), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(crisis.crisisType.iconEmoji, fontSize = 22.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "KRİZ YÖNETİM MASASI",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626)
                        )
                        Text(
                            text = crisis.modelName,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Crisis Summary Card
                    Surface(
                        color = Color(0xFFFFF1F2),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFFECDD3)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Arıza: ${crisis.crisisType.title}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF9F1239)
                                )
                                val sevBadge = when (crisis.severityLevel) {
                                    3 -> "🚨 Kritik"
                                    2 -> "⚠️ Ciddi"
                                    else -> "🟡 Orta"
                                }
                                Text(
                                    text = sevBadge,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = Color(0xFFBE123C)
                                )
                            }

                            Text(
                                text = crisis.crisisType.description,
                                fontSize = 11.5.sp,
                                color = Color(0xFF4C0519),
                                lineHeight = 15.sp
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Kaynak: ${crisis.crisisType.typicalCulprit}",
                                    fontSize = 10.sp,
                                    color = Color(0xFF881337),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Etkilenen: ${"%,d".format(crisis.affectedUnitsCount)} adet",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF881337)
                                )
                            }
                        }
                    }

                    Text(
                        text = "Kriz Çözüm Stratejisini Seçin:",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Strategies List
                    CrisisResolutionStrategy.values().forEach { strategy ->
                        val isSelected = selectedStrategy == strategy
                        val calculatedCost = when (strategy) {
                            CrisisResolutionStrategy.SOFTWARE_PATCH_LIMIT -> 50000L
                            CrisisResolutionStrategy.FREE_SERVICE_REPAIR -> (crisis.affectedUnitsCount.toLong() * 25L).coerceAtLeast(100000L)
                            CrisisResolutionStrategy.FULL_RECALL_REFUND -> {
                                val unitRefund = model?.specs?.price?.toLong() ?: 300L
                                (crisis.affectedUnitsCount.toLong() * unitRefund) + ((model?.remainingStock ?: 0).toLong() * 50L)
                            }
                        }
                        val canAfford = currentBudget >= calculatedCost

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedStrategy = strategy },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = BorderStroke(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(strategy.iconEmoji, fontSize = 16.sp)
                                        Text(
                                            text = strategy.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Text(
                                        text = "$${"%,d".format(calculatedCost)}",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        color = if (canAfford) MaterialTheme.colorScheme.primary else Color(0xFFDC2626)
                                    )
                                }

                                Text(
                                    text = strategy.description,
                                    fontSize = 11.sp,
                                    color = Slate600,
                                    lineHeight = 14.sp
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "İtibar: ${strategy.repImpactText}",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (strategy.repImpactText.contains("-")) Color(0xFFDC2626) else Green500
                                    )
                                    Text(
                                        text = strategy.salesImpactText,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Slate500
                                    )
                                }
                            }
                        }
                    }
                }

                // Insufficient Budget warning if applicable
                if (selectedStrategy != null && !canAffordSelected) {
                    Surface(
                        color = Color(0xFFFEF2F2),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "⚠️ Yetersiz bütçe! (Mevcut: $${"%,d".format(currentBudget)}, Gereken: $${"%,d".format(selectedStrategyCost)}). Daha düşük maliyetli bir strateji seçin.",
                            fontSize = 10.5.sp,
                            color = Color(0xFFDC2626),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Fixed Action Buttons at the Bottom
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProOutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Daha Sonra", fontSize = 12.sp)
                    }

                    Button3D(
                        onClick = {
                            selectedStrategy?.let { onApplyStrategy(it) }
                        },
                        enabled = selectedStrategy != null && canAffordSelected,
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedStrategy == CrisisResolutionStrategy.FULL_RECALL_REFUND) Color(0xFFDC2626) else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Kararı Uygula", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
