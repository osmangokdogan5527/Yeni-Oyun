package com.example

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
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

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color(0xFFFEF2F2), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(crisis.crisisType.iconEmoji, fontSize = 22.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "KRİZ YÖNETİM MASASI",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626)
                        )
                        Text(
                            text = crisis.modelName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                // Crisis Summary Card
                Surface(
                    color = Color(0xFFFFF1F2),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFFECDD3)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Arıza: ${crisis.crisisType.title}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF9F1239)
                            )
                            val sevBadge = when (crisis.severityLevel) {
                                3 -> "🚨 Kritik Kriz"
                                2 -> "⚠️ Ciddi Boyut"
                                else -> "🟡 Orta Düzey"
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
                            fontSize = 12.sp,
                            color = Color(0xFF4C0519),
                            lineHeight = 16.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Olası Kaynak: ${crisis.crisisType.typicalCulprit}",
                                fontSize = 10.5.sp,
                                color = Color(0xFF881337),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Etkilenen: ${"%,d".format(crisis.affectedUnitsCount)} adet",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF881337)
                            )
                        }
                    }
                }

                Text(
                    text = "Kriz Çözüm Stratejisini Seçin:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Strategies List
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
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

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Daha Sonra")
                    }

                    Button(
                        onClick = {
                            selectedStrategy?.let { onApplyStrategy(it) }
                        },
                        enabled = selectedStrategy != null,
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedStrategy == CrisisResolutionStrategy.FULL_RECALL_REFUND) Color(0xFFDC2626) else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Kararı Uygula", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
