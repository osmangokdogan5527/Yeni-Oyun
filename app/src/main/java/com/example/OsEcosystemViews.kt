package com.example

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.CustomOsState
import com.example.viewmodel.GameState
import com.example.viewmodel.OsLicenseType
import com.example.viewmodel.StoreCommissionRate

@Composable
fun EcosystemRevenueOverviewCard(customOs: CustomOsState, state: GameState) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("💰", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Yazılım & Ekosistem Gelirleri", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Uygulama Mağazası, Bulut Abonelikleri ve OEM Lisansları", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            val totalMonthlyOsRevenue = customOs.lastMonthAppStoreIncome + customOs.lastMonthCloudRevenue + customOs.lastMonthLicenseIncome
            val totalAllTimeOsRevenue = customOs.totalAppStoreRevenueToDate + customOs.totalCloudRevenueToDate + customOs.totalLicenseRevenueToDate

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF10B981).copy(alpha = 0.1f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Son Dönem Ekosistem Geliri", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            "+$${"%,d".format(totalMonthlyOsRevenue)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF10B981)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Kümülatif Toplam Kazanç", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            "$${"%,d".format(totalAllTimeOsRevenue)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Breakdown Rows
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RevenueBreakdownTile(
                    title = "App Store Komisyonu",
                    amount = customOs.lastMonthAppStoreIncome,
                    icon = Icons.Default.ShoppingBag,
                    color = Color(0xFF3B82F6),
                    modifier = Modifier.weight(1f)
                )
                RevenueBreakdownTile(
                    title = "Bulut Abonelikleri",
                    amount = customOs.lastMonthCloudRevenue,
                    icon = Icons.Default.Cloud,
                    color = Color(0xFF8B5CF6),
                    modifier = Modifier.weight(1f)
                )
                RevenueBreakdownTile(
                    title = "OEM Cihaz Lisansı",
                    amount = customOs.lastMonthLicenseIncome,
                    icon = Icons.Default.Business,
                    color = Color(0xFFF59E0B),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun RevenueBreakdownTile(
    title: String,
    amount: Long,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(title, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text(
                "+$${"%,d".format(amount)}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun CustomerLoyaltyCard(customOs: CustomOsState) {
    val loyalty = customOs.customerLoyaltyPercent

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFEF4444))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Yazılım Müşteri Sadakati (Ecosystem Lock-in)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Yazılım kalitesi ve ekosistem kullanıcıların markada kalmasını sağlar", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFEF4444).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "%${"%.1f".format(loyalty)}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = Color(0xFFEF4444),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            LinearProgressIndicator(
                progress = { loyalty / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFFEF4444),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Eski Müşterilerin Tekrar Alma Oranı: %${(loyalty * 0.8f).toInt()}", fontSize = 11.sp, color = Color(0xFF10B981), fontWeight = FontWeight.SemiBold)
                Text(
                    text = when {
                        loyalty >= 85f -> "Kusursuz Apple Seviyesi 🍎"
                        loyalty >= 65f -> "Çok Güçlü Ekosistem 🌐"
                        loyalty >= 45f -> "Dengeli Marka Güveni 📱"
                        else -> "Geliştirilmeye Açık ⏳"
                    },
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun AppStoreManagementCard(
    customOs: CustomOsState,
    state: GameState,
    onCommissionSelect: (StoreCommissionRate) -> Unit,
    onOpenFundDialog: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Storefront, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Uygulama Mağazası & Geliştirici Politikası", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Toplam ${"%,d".format(customOs.totalStoreApps)} Uygulama Mağazada Yayında", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Button(
                    onClick = onOpenFundDialog,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("Geliştirici Fonu ($)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Text("Mağaza Komisyon Oranı Stratejisi:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                StoreCommissionRate.entries.forEach { rate ->
                    val isSelected = customOs.commissionRate == rate
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onCommissionSelect(rate) }
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(10.dp)
                            ),
                        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { onCommissionSelect(rate) },
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(rate.label, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        when (rate) {
                                            StoreCommissionRate.DEVELOPER_FRIENDLY -> "Geliştiricileri çeker, uygulama kataloğunu hızla büyütür."
                                            StoreCommissionRate.BALANCED -> "Sektör standardı, dengeli gelir ve büyüme."
                                            StoreCommissionRate.MAXIMUM_PROFIT -> "Yüksek komisyon kârı ancak geliştirici tepkisi."
                                        },
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GlobalDevConCard(
    customOs: CustomOsState,
    state: GameState,
    onHost: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎤", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Küresel Geliştirici Konferansı (DevCon)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Düzenlenen: ${customOs.devConCount} Etkinlik", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    }
                }

                Button(
                    onClick = onHost,
                    enabled = state.budget >= 5000000L,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("Konferans Düzenle ($5M)", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Text(
                "Dünya çapında binlerce yazılımcıyı bir araya getirerek yeni API ve yazılım geliştirme araçlarını tanıtın. +60.000 yeni uygulama, +10 Ekosistem puanı ve +6 Marka İtibarı kazandırır.",
                color = Color(0xFFCBD5E1),
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun LicenseModelCard(
    customOs: CustomOsState,
    onLicenseTypeChange: (OsLicenseType) -> Unit,
    onFeeChange: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Lisanslama Modeli & OEM Satış Stratejisi", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Üçüncü taraf üreticilerin bu OS'i kullanma şartları", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OsLicenseType.entries.forEach { license ->
                    val isSelected = customOs.licenseType == license
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onLicenseTypeChange(license) }
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(10.dp)
                            ),
                        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(license.badge, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(
                                if (license == OsLicenseType.OPEN_SOURCE) "Ücretsiz OEM Dağıtımı, Yüksek Pazar Yayılımı" else "Cihaz Başı Lisanslama Geliri, Prestij",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            if (customOs.licenseType == OsLicenseType.CLOSED_PROPRIETARY) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Cihaz Başı Lisans Ücreti:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("$${customOs.perDeviceLicenseFee} / Cihaz", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Slider(
                        value = customOs.perDeviceLicenseFee.toFloat(),
                        onValueChange = { onFeeChange(it.toInt()) },
                        valueRange = 0f..50f,
                        steps = 50
                    )
                }
            }
        }
    }
}
