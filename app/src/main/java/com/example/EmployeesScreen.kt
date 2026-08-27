package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.EmployeeType
import com.example.viewmodel.FACTORY_TIERS
import com.example.viewmodel.GameViewModel
import com.example.viewmodel.OFFICE_TIERS

@Composable
fun EmployeesScreen(modifier: Modifier = Modifier, viewModel: GameViewModel) {
    val state by viewModel.state.collectAsState()
    val totalStaff = state.totalEmployees

    if (state.noticeMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearNoticeMessage() },
            title = { Text("Kapasite / Bütçe Uyarısı", fontWeight = FontWeight.Bold) },
            text = { Text(state.noticeMessage ?: "", fontSize = 14.sp) },
            confirmButton = {
                Button(onClick = { viewModel.clearNoticeMessage() }) {
                    Text("Anladım")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Compact Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    Icons.Default.Group,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    "Personel & Fabrika Yönetimi",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
            }

            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "$totalStaff / ${state.maxEmployees} Personel",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        // Compact, Highly Legible Summary Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                // Row 1: Sabit Kesinti & Dağılım
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            "Aylık Sabit Kesinti:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate700
                        )
                        Text(
                            "-$${"%,d".format(state.totalMonthlyExpenses)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Text(
                        text = "Maaş: $${"%,d".format(state.totalSalaries)} • Kira: $${"%,d".format(state.officeExpense)} • Bakım: $${"%,d".format(state.factoryMaintenance)}",
                        fontSize = 9.5.sp,
                        color = Slate600,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.12f))

                // Row 2: 3 Balanced Metric Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("🏷️ ", fontSize = 10.sp)
                            Text(
                                "%${"%.1f".format(state.unitCostDiscountPercent)} Tasarruf",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Green500,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("⭐ ", fontSize = 10.sp)
                            Text(
                                "+${state.qaScoreBonus} QA Puanı",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("⚡ ", fontSize = 10.sp)
                            Text(
                                "${viewModel.calculateResearchDuration(state.engineers)} Ay Ar-Ge",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate900,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Office Tier Upgrade Card
            item {
                val currentOffice = state.currentOfficeTier
                val nextOffice = OFFICE_TIERS.firstOrNull { it.level == state.officeLevel + 1 }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Business, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                                Column {
                                    Text("ŞİRKET GENEL MERKEZİ", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text(currentOffice.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Slate900)
                                }
                            }

                            Surface(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Kapasite: ${state.totalEmployees} / ${currentOffice.maxEmployees}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Aylık Ofis Kirası: $${"%,d".format(currentOffice.monthlyRent)} / Ay. Daha fazla personel istihdam etmek için ofis alanını büyütün.",
                            fontSize = 11.sp,
                            color = Slate600
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (nextOffice != null) {
                            val canAfford = state.budget >= nextOffice.upgradeCost
                            Button(
                                onClick = { viewModel.upgradeOffice() },
                                modifier = Modifier.fillMaxWidth().height(34.dp),
                                enabled = canAfford,
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.ArrowUpward, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${nextOffice.name} Seviyesine Yükselt ($${"%,d".format(nextOffice.upgradeCost)})",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Surface(
                                color = Green500.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "🏆 MAKSİMUM OFİS SEVİYESİ (500 Kapasite)",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Green500,
                                    modifier = Modifier.padding(6.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Factory Tier Upgrade Card
            item {
                val currentFactory = state.currentFactoryTier
                val nextFactory = FACTORY_TIERS.firstOrNull { it.level == state.factoryLevel + 1 }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.PrecisionManufacturing, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(22.dp))
                                Column {
                                    Text("ÜRETİM FABRİKASI TESİSİ", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                                    Text(currentFactory.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Slate900)
                                }
                            }

                            Surface(
                                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "İşçi: ${state.assemblyWorkers} / ${currentFactory.maxWorkers}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Sabit Bakım: $${"%,d".format(currentFactory.monthlyMaintenance)} / Ay. Tesis birim maliyette %${currentFactory.discountPercent.toInt()} tasarruf sağlar.",
                            fontSize = 11.sp,
                            color = Slate600
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (nextFactory != null) {
                            val canAfford = state.budget >= nextFactory.upgradeCost
                            Button(
                                onClick = { viewModel.upgradeFactory() },
                                modifier = Modifier.fillMaxWidth().height(34.dp),
                                enabled = canAfford,
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                            ) {
                                Icon(Icons.Default.ArrowUpward, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${nextFactory.name} Yatırımı Yap ($${"%,d".format(nextFactory.upgradeCost)})",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Surface(
                                color = Green500.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "🏆 EN GELİŞMİŞ ROBOTİK FABRİKA SEVİYESİ",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Green500,
                                    modifier = Modifier.padding(6.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Employees Cards
            item {
                EmployeeCategoryCard(
                    title = "Donanım & Yazılım Mühendisleri",
                    icon = Icons.Default.Science,
                    roleDesc = "Ar-Ge araştırmalarının süresini kısaltır (8+ mühendis ile 1 ayda biter), eskime cezasını düşürür.",
                    salaryText = "$8,000 / Ay",
                    currentCount = state.engineers,
                    impactText = "Eskime Cezası Azaltma: -${state.engineerTechBonus} Puan (Tavan: 30)",
                    marginalText = "Sıradaki mühendis: +${state.marginalEngineerBonus()} puan katkı (azalan verim) • Ar-Ge hızı: ~${viewModel.calculateResearchDuration(state.engineers)} Dönem/proje",
                    onHire = { viewModel.hireEmployee(EmployeeType.ENGINEER, it) },
                    onFire = { viewModel.fireEmployee(EmployeeType.ENGINEER, it) }
                )
            }

            item {
                EmployeeCategoryCard(
                    title = "Kalite Kontrol (QA) Uzmanları",
                    icon = Icons.Default.CheckCircle,
                    roleDesc = "Üretilen telefonlardaki hataları ayıklar ve inceleme puanına doğrudan bonus ekler.",
                    salaryText = "$5,000 / Ay",
                    currentCount = state.qaInspectors,
                    impactText = "Puan Bonusu: +${state.qaScoreBonus} Puan (Tavan: 20)",
                    marginalText = "Sıradaki uzman: +${state.marginalQaBonus()} puan katkı (azalan verim)",
                    onHire = { viewModel.hireEmployee(EmployeeType.QA_INSPECTOR, it) },
                    onFire = { viewModel.fireEmployee(EmployeeType.QA_INSPECTOR, it) }
                )
            }

            item {
                EmployeeCategoryCard(
                    title = "Montaj & Üretim İşçileri",
                    icon = Icons.Default.Build,
                    roleDesc = "Fabrikada montaj verimliliğini yükseltir (Maksımum ${state.currentFactoryTier.maxWorkers} işçi).",
                    salaryText = "$3,000 / Ay",
                    currentCount = state.assemblyWorkers,
                    impactText = "İşçi İndirimi: %${"%.1f".format(state.workerDiscountPercent)} (Tavan: %22)",
                    marginalText = "Sıradaki işçi: +%${"%.2f".format(state.marginalWorkerDiscount())} indirim katkısı",
                    onHire = { viewModel.hireEmployee(EmployeeType.ASSEMBLY_WORKER, it) },
                    onFire = { viewModel.fireEmployee(EmployeeType.ASSEMBLY_WORKER, it) }
                )
            }
        }
    }
}

@Composable
fun EmployeeCategoryCard(
    title: String,
    icon: ImageVector,
    roleDesc: String,
    salaryText: String,
    currentCount: Int,
    impactText: String,
    marginalText: String? = null,
    onHire: (Int) -> Unit,
    onFire: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(18.dp))
                    }
                    Column {
                        Text(title, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = Slate900)
                        Text(salaryText, fontSize = 10.5.sp, color = Slate600)
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "$currentCount Kişi",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(roleDesc, fontSize = 11.sp, color = Slate600, lineHeight = 15.sp)

            Spacer(modifier = Modifier.height(4.dp))
            Text(impactText, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            if (marginalText != null) {
                Text(
                    text = marginalText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Slate500
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = { onFire(1) },
                        enabled = currentCount > 0,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Çıkar (-1)", fontSize = 10.5.sp)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = { onHire(1) },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("İşe Al (+1)", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onHire(5) },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("+5", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
