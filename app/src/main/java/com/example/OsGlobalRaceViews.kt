package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import com.example.ui.Button3D
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.*

@Composable
fun GlobalOsRaceHeaderCard(customOs: CustomOsState) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🌍", fontSize = 32.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Küresel İşletim Sistemi Rekabeti",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Apple iOS, Google Android, Huawei HarmonyOS ve diğer devlerin teknoloji seviyeleri ile karşılaştırmanız.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun PlayerOsComparisonCard(customOs: CustomOsState, state: GameState) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Color(customOs.themeColorHex), RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(customOs.themeColorHex), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (customOs.isCustomActive) "⭐" else "🤖", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${customOs.name} (Bizim OS)",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(customOs.themeColorHex).copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "v${customOs.version}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(customOs.themeColorHex),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "${customOs.type.title} • ${customOs.licenseType.badge}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(customOs.themeColorHex).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "%${"%.1f".format(customOs.popularityPercent)} Pazar Payı",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(customOs.themeColorHex),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            // Tech & Ecosystem Scores
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OsCompareMetric(label = "Teknoloji", score = customOs.overallTechScore, color = Color(0xFF3B82F6), modifier = Modifier.weight(1f))
                OsCompareMetric(label = "Ekosistem", score = customOs.ecosystemScore, color = Color(0xFF8B5CF6), modifier = Modifier.weight(1f))
                OsCompareMetric(label = "Sadakat", textValue = "%${customOs.customerLoyaltyPercent.toInt()}", color = Color(0xFF10B981), modifier = Modifier.weight(1f))
            }

            // Specs Row
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Mağaza: ${"%,d".format(customOs.totalStoreApps)} Uygulama", fontSize = 10.sp, fontWeight = FontWeight.Medium)
                    Text("Aylık Ciro: $${"%,d".format(customOs.lastMonthAppStoreIncome + customOs.lastMonthCloudRevenue + customOs.lastMonthLicenseIncome)}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                }
            }
        }
    }
}

@Composable
fun RivalOsCompactRow(rivalOs: CompetitorOsInfo) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Text(rivalOs.iconEmoji, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = rivalOs.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${rivalOs.company} • ${rivalOs.licenseTypeBadge}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(rivalOs.brandColorHex).copy(alpha = 0.12f)
            ) {
                Text(
                    text = "%${rivalOs.marketSharePercent}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }
        }

        // Tech & Ecosystem Scores (Compact)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OsCompareCompactMetric(label = "Teknoloji", value = "${rivalOs.techScore}", color = Color(0xFF3B82F6), modifier = Modifier.weight(1f))
            OsCompareCompactMetric(label = "Ekosistem", value = "${rivalOs.ecosystemScore}", color = Color(0xFF8B5CF6), modifier = Modifier.weight(1f))
            OsCompareCompactMetric(label = "Kullanıcı", value = rivalOs.userBaseFormatted, color = Color(0xFF10B981), modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun OsCompareCompactMetric(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis)
            Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color, maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun OsCompareMetric(
    label: String,
    score: Int? = null,
    textValue: String? = null,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                fontSize = 9.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = textValue ?: "$score/100",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun CreateOsDialog(
    customOs: CustomOsState,
    currentBudget: Long,
    onDismiss: () -> Unit,
    onConfirm: (name: String, type: OsType, license: OsLicenseType, focus: OsFocus, color: Long, fee: Int) -> Unit
) {
    var osName by remember { mutableStateOf(if (customOs.name != "Stok Açık Kaynak Android") customOs.name else "NovaOS") }
    var selectedType by remember { mutableStateOf(if (customOs.isCustomActive) customOs.type else OsType.PROPRIETARY_KERNEL) }
    var selectedLicense by remember { mutableStateOf(customOs.licenseType) }
    var selectedFocus by remember { mutableStateOf(customOs.focus) }
    var selectedColor by remember { mutableLongStateOf(customOs.themeColorHex) }
    var perDeviceFee by remember { mutableIntStateOf(customOs.perDeviceLicenseFee) }

    val colorOptions = listOf(
        0xFF0284C7, 0xFF7C3AED, 0xFF10B981, 0xFFEF4444, 0xFFF59E0B, 0xFF0F172A, 0xFFEC4899
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📱 ", fontSize = 20.sp)
                Text("Özel Mobil İşletim Sistemi Tasarla", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = osName,
                        onValueChange = { osName = it },
                        label = { Text("İşletim Sistemi Adı") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    Text("İşletim Sistemi Çekirdek Türü:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(OsType.PROPRIETARY_KERNEL, OsType.CUSTOM_UI_SKIN).forEach { type ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedType = type }
                                    .border(
                                        width = if (selectedType == type) 2.dp else 1.dp,
                                        color = if (selectedType == type) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                color = if (selectedType == type) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(type.title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(type.description, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("Geliştirme Maliyeti: $${"%,d".format(type.devCost)}", fontSize = 10.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                item {
                    Text("Lisanslama Modeli:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OsLicenseType.entries.forEach { license ->
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedLicense = license }
                                    .border(
                                        width = if (selectedLicense == license) 2.dp else 1.dp,
                                        color = if (selectedLicense == license) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                color = if (selectedLicense == license) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                            ) {
                                Column(modifier = Modifier.padding(6.dp)) {
                                    Text(license.badge, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(if (license == OsLicenseType.OPEN_SOURCE) "Hızlı Büyüme" else "Lisans Geliri", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }

                item {
                    Text("Öne Çıkan Mimari Odak:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        OsFocus.entries.forEach { focus ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { selectedFocus = focus }
                                    .border(
                                        width = if (selectedFocus == focus) 2.dp else 1.dp,
                                        color = if (selectedFocus == focus) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(6.dp)
                                    ),
                                color = if (selectedFocus == focus) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                            ) {
                                Column(modifier = Modifier.padding(6.dp)) {
                                    Text("${focus.icon} ${focus.title}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(focus.bonusDescription, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }

                item {
                    Text("Marka & Tema Rengi:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        colorOptions.forEach { col ->
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(Color(col), CircleShape)
                                    .clickable { selectedColor = col }
                                    .border(
                                        width = if (selectedColor == col) 3.dp else 1.dp,
                                        color = if (selectedColor == col) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button3D(
                onClick = {
                    onConfirm(osName, selectedType, selectedLicense, selectedFocus, selectedColor, perDeviceFee)
                },
                enabled = osName.isNotBlank() && currentBudget >= selectedType.devCost
            ) {
                Text("Geliştirmeyi Başlat ($${"%,d".format(selectedType.devCost)})")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

@Composable
fun DeveloperFundDialog(
    currentBalance: Long,
    currentBudget: Long,
    onDismiss: () -> Unit,
    onInvest: (amount: Long) -> Unit
) {
    var selectedAmount by remember { mutableLongStateOf(2000000L) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("💡 ", fontSize = 20.sp)
                Text("Geliştirici Teşvik Fonu", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Bağımsız geliştiricilere hibe sağlayarak popüler uygulamaların (oyunlar, sosyal medya, üretkenlik) sizin işletim sisteminize uyarlanmasını sağlayın.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Mevcut Fon Havuzu", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$${"%,d".format(currentBalance)}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Text("Yatırım Yapılacak Tutar:", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(1000000L, 2000000L, 5000000L, 10000000L).forEach { amt ->
                        val isSelected = selectedAmount == amt
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedAmount = amt }
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                        ) {
                            Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$${amt / 1000000}M", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button3D(
                onClick = { onInvest(selectedAmount) },
                enabled = currentBudget >= selectedAmount
            ) {
                Text("Fona Aktar ($${"%,d".format(selectedAmount)})")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Kapat")
            }
        }
    )
}
