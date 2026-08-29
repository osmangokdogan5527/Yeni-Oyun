package com.example

import com.example.ui.ProOutlinedButton
import com.example.ui.ProCard
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.*
import com.example.ui.Button3D
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
    val productionState by viewModel.productionState.collectAsState()
    val financeState by viewModel.financeState.collectAsState()
    val totalStaff = productionState.totalEmployees

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
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "$totalStaff / ${productionState.maxEmployees} Personel",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        // Compact, Highly Legible Summary Card
        ProCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Row 1: Sabit Kesinti & Dağılım
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Aylık Sabit Kesinti",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "-$${"%,d".format(financeState.totalMonthlyExpenses)}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Text(
                        text = "Maaş $${"%,d".format(financeState.totalSalaries)} • Kira $${"%,d".format(financeState.officeExpense)} • Bakım $${"%,d".format(financeState.factoryMaintenance)}",
                        fontSize = 9.5.sp,
                        color = Slate500,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                        textAlign = androidx.compose.ui.text.style.TextAlign.End
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.12f))

                // Row 2: 3 Balanced Metric Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CompactMetric(
                        icon = Icons.Outlined.Savings,
                        value = "%${"%.1f".format(productionState.unitCostDiscountPercent)}",
                        label = "Tasarruf"
                    )
                    CompactMetric(
                        icon = Icons.Outlined.VerifiedUser,
                        value = "+${productionState.qaScoreBonus}",
                        label = "QA Puanı"
                    )
                    CompactMetric(
                        icon = Icons.Outlined.Bolt,
                        value = "${viewModel.calculateResearchDuration(productionState.engineers)}",
                        label = "Dönem/Ar-Ge"
                    )
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
                val currentOffice = productionState.currentOfficeTier
                val nextOffice = OFFICE_TIERS.firstOrNull { it.level == productionState.officeLevel + 1 }

                ProCard(
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
                                    Text(currentOffice.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }

                            Surface(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Kapasite: ${productionState.totalEmployees} / ${currentOffice.maxEmployees}",
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
                            val canAfford = financeState.budget >= nextOffice.upgradeCost
                            Button3D(
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
                val currentFactory = productionState.currentFactoryTier
                val nextFactory = FACTORY_TIERS.firstOrNull { it.level == productionState.factoryLevel + 1 }

                ProCard(
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
                                    Text(currentFactory.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }

                            Surface(
                                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "İşçi: ${productionState.assemblyWorkers} / ${currentFactory.maxWorkers}",
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
                            val canAfford = financeState.budget >= nextFactory.upgradeCost
                            Button3D(
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
                    currentCount = productionState.engineers,
                    impactText = "Eskime Cezası Azaltma: -${productionState.engineerTechBonus} Puan (Tavan: 30)",
                    marginalText = "Sıradaki mühendis: +${productionState.marginalEngineerBonus()} puan katkı (azalan verim) • Ar-Ge hızı: ~${viewModel.calculateResearchDuration(productionState.engineers)} Dönem/proje",
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
                    currentCount = productionState.qaInspectors,
                    impactText = "Puan Bonusu: +${productionState.qaScoreBonus} Puan (Tavan: 20)",
                    marginalText = "Sıradaki uzman: +${productionState.marginalQaBonus()} puan katkı (azalan verim)",
                    onHire = { viewModel.hireEmployee(EmployeeType.QA_INSPECTOR, it) },
                    onFire = { viewModel.fireEmployee(EmployeeType.QA_INSPECTOR, it) }
                )
            }

            item {
                EmployeeCategoryCard(
                    title = "Montaj & Üretim İşçileri",
                    icon = Icons.Default.Build,
                    roleDesc = "Fabrikada montaj verimliliğini yükseltir (Maksımum ${productionState.currentFactoryTier.maxWorkers} işçi).",
                    salaryText = "$3,000 / Ay",
                    currentCount = productionState.assemblyWorkers,
                    impactText = "İşçi İndirimi: %${"%.1f".format(productionState.workerDiscountPercent)} (Tavan: %22)",
                    marginalText = "Sıradaki işçi: +%${"%.2f".format(productionState.marginalWorkerDiscount())} indirim katkısı",
                    onHire = { viewModel.hireEmployee(EmployeeType.ASSEMBLY_WORKER, it) },
                    onFire = { viewModel.fireEmployee(EmployeeType.ASSEMBLY_WORKER, it) }
                )
            }
        }
    }
}

@Composable
fun CompactMetric(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Slate500, modifier = Modifier.size(15.dp))
        Spacer(modifier = Modifier.width(5.dp))
        Column {
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, softWrap = false)
            Text(label, fontSize = 9.sp, color = Slate500, maxLines = 1, softWrap = false)
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
    var expanded by remember { mutableStateOf(false) }
    ProCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(9.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(18.dp))
                    }
                    Column {
                        Text(title, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text(impactText, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "$currentCount",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Daralt" else "Genişlet",
                        tint = Slate400,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(roleDesc, fontSize = 11.sp, color = Slate600, lineHeight = 15.sp)
                Text(salaryText, fontSize = 10.5.sp, color = Slate500, modifier = Modifier.padding(top = 2.dp))
                if (marginalText != null) {
                    Text(
                        text = marginalText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Slate500,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ProOutlinedButton(
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
                    Button3D(
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

                    Button3D(
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
