package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import com.example.ui.theme.*
import com.example.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialHubDialog(
    state: GameState,
    onDismiss: () -> Unit,
    onTakeLoan: (LoanType) -> Unit,
    onPayOffEarly: (String) -> Unit,
    onLiquidatePatents: () -> Unit,
    onSeekVentureCapital: () -> Unit,
    onLiquidateStock: (String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val brandColor = Color(state.companyBrandColorHex)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏦", fontSize = 22.sp)
                        }
                        Column {
                            Text(
                                text = "Finans & Bankacılık Merkezi",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Kredi paketleri, acil likidite & nakit akış yönetimi",
                                fontSize = 11.sp,
                                color = Slate500
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Financial Health Summary Card
                val isNegativeBudget = state.budget < 0
                val budgetColor = if (isNegativeBudget) MaterialTheme.colorScheme.error else Green500
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isNegativeBudget) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isNegativeBudget) MaterialTheme.colorScheme.error.copy(alpha = 0.5f) else Slate200
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Şirket Kasası (Bakiye)", fontSize = 10.sp, color = Slate600)
                            Text(
                                text = if (state.budget >= 0) "$${"%,d".format(state.budget).replace(',', '.')}" else "-$${"%,d".format(kotlin.math.abs(state.budget)).replace(',', '.')}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = budgetColor
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Kredi Notu (Findeks)", fontSize = 10.sp, color = Slate600)
                            val creditRatingText = when {
                                state.creditScore >= 820 -> "Mükemmel (A+)"
                                state.creditScore >= 740 -> "Çok İyi (A)"
                                state.creditScore >= 650 -> "Orta (B)"
                                else -> "Riskli (C)"
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${state.creditScore}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (state.creditScore >= 700) Color(0xFF0284C7) else Color(0xFFD97706)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (state.creditScore >= 700) Color(0xFFE0F2FE) else Color(0xFFFEF3C7)
                                ) {
                                    Text(
                                        text = creditRatingText,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (state.creditScore >= 700) Color(0xFF0369A1) else Color(0xFFB45309),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Toplam Borç", fontSize = 10.sp, color = Slate600)
                            Text(
                                text = "$${"%,d".format(state.totalDebt).replace(',', '.')}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (state.totalDebt > 0) Color(0xFFE11D48) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tab Selector
                SecondaryTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "Banka Kredileri (${state.activeLoans.size})",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "Acil Likidite & Kurtarma",
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                                )
                                if (isNegativeBudget) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(Color(0xFFEF4444), CircleShape)
                                    )
                                }
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text(
                                "Finansal Bilanço",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }

                HorizontalDivider(color = Slate200, modifier = Modifier.padding(vertical = 6.dp))

                // Content Views
                when (selectedTab) {
                    0 -> BankLoansTabContent(
                        state = state,
                        onTakeLoan = onTakeLoan,
                        onPayOffEarly = onPayOffEarly
                    )
                    1 -> EmergencyRecoveryTabContent(
                        state = state,
                        onLiquidatePatents = onLiquidatePatents,
                        onSeekVentureCapital = onSeekVentureCapital,
                        onLiquidateStock = onLiquidateStock
                    )
                    2 -> FinancialStatementTabContent(state = state)
                }
            }
        }
    }
}

@Composable
fun BankLoansTabContent(
    state: GameState,
    onTakeLoan: (LoanType) -> Unit,
    onPayOffEarly: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Active Loans Section
        if (state.activeLoans.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AKTİF KREDİ BORÇLARI (${state.activeLoans.size}/4)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Dönemlik Taksit: -$${"%,d".format(state.totalLoanPeriodPayments)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            items(state.activeLoans, key = { it.id }) { loan ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    border = BorderStroke(1.dp, Slate200)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(loan.type.icon, fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = loan.type.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Anapara: $${"%,d".format(loan.principalAmount)} • %${loan.interestPercent} Faiz",
                                        fontSize = 10.sp,
                                        color = Slate500
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Kalan: $${"%,d".format(loan.remainingBalance)}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE11D48)
                                )
                                Text(
                                    text = "${loan.remainingPeriods}/${loan.totalPeriods} Dönem Kaldı",
                                    fontSize = 10.sp,
                                    color = Slate500
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Repayment progress
                        LinearProgressIndicator(
                            progress = { loan.progressPercent },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Green500,
                            trackColor = Slate200
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Dönem Başına: -$${"%,d".format(loan.periodPayment)} (2 Hafta)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Button3D(
                                onClick = { onPayOffEarly(loan.id) },
                                enabled = state.budget >= loan.earlyPayoffCost,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF0284C7)
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Kapat ($${"%,d".format(loan.earlyPayoffCost)})",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "KREDİ TEKLİFLERİ & FİNANSMAN PAKETLERİ",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp
            )
        }

        items(LoanType.entries.filter { it != LoanType.EMERGENCY_BAILOUT || state.budget < 500000L }) { offer ->
            val isEligible = state.reputation >= offer.requiredReputation && state.activeLoans.size < 4
            val isBailout = offer == LoanType.EMERGENCY_BAILOUT
            val containerColor = if (isBailout) Color(0xFFFFF1F2) else MaterialTheme.colorScheme.surface
            val borderColor = if (isBailout) Color(0xFFFECDD3) else Slate200

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = containerColor),
                border = BorderStroke(1.dp, borderColor)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(modifier = Modifier.weight(1f)) {
                            Text(offer.icon, fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = offer.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isBailout) Color(0xFFBE123C) else MaterialTheme.colorScheme.onSurface
                                    )
                                    if (isBailout) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFFBE123C)
                                        ) {
                                            Text(
                                                "Acil Kurtarma",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = offer.description,
                                    fontSize = 10.sp,
                                    color = Slate600,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Loan specs row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Nakit Girişi", fontSize = 9.sp, color = Slate500)
                            Text(
                                "+$${"%,d".format(offer.principal)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Green500
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Faiz & Vade", fontSize = 9.sp, color = Slate500)
                            Text(
                                "%${offer.interestPercent} • ${offer.durationPeriods / 2} Ay",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            val totalPay = offer.principal + (offer.principal * offer.interestPercent / 100)
                            val perPeriod = totalPay / offer.durationPeriods
                            Text("Dönemlik Taksit", fontSize = 9.sp, color = Slate500)
                            Text(
                                "-$${"%,d".format(perPeriod)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE11D48)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (offer.requiredReputation > 0 && state.reputation < offer.requiredReputation) {
                            Text(
                                text = "Gereken Şirket İtibarı: ${offer.requiredReputation} (Mevcut: ${state.reputation})",
                                fontSize = 10.sp,
                                color = Color(0xFFD97706),
                                fontWeight = FontWeight.Medium
                            )
                        } else {
                            Text(
                                text = "Onay: Anında Hesaba Geçer",
                                fontSize = 10.sp,
                                color = Green500,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Button3D(
                            onClick = { onTakeLoan(offer) },
                            enabled = isEligible,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isBailout) Color(0xFFE11D48) else MaterialTheme.colorScheme.primary
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                            modifier = Modifier.height(34.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Kredi Çek", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmergencyRecoveryTabContent(
    state: GameState,
    onLiquidatePatents: () -> Unit,
    onSeekVentureCapital: () -> Unit,
    onLiquidateStock: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                border = BorderStroke(1.dp, Color(0xFFBFDBFE))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💡", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Bütçeniz negatife düştüğünde veya acil nakit gerektiğinde borçlanmadan sermaye elde etmek için bu kurtarma yöntemlerini kullanabilirsiniz.",
                        fontSize = 11.sp,
                        color = Color(0xFF1E40AF),
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // 1. Venture Capital (Equity Injection)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Slate200)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("💼", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Risk Sermayesi & Melek Yatırımcı",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "%5 Şirket hissesi karşılığı sermaye girişi",
                                    fontSize = 10.sp,
                                    color = Slate500
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "Satılan: %${state.equitySoldPercent} / %25",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val cashYield = 1800000L + (state.reputation * 20000L)
                    Text(
                        text = "Yatırımcı konsorsiyumu şirketinizin %5 hissesine karşılık kasaya anında +$${"%,d".format(cashYield)} nakit sermaye yatırımı yapacaktır. Geri ödeme veya taksit yoktur.",
                        fontSize = 11.sp,
                        color = Slate600,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Getiri: +$${"%,d".format(cashYield)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Green500
                        )

                        Button3D(
                            onClick = onSeekVentureCapital,
                            enabled = state.equitySoldPercent < 25,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Yatırım Al (%5 Hisse)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 2. Patent License Liquidation
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Slate200)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📜", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Ar-Ge Patent Lisansı Devri",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Patent kullanım hakkını üreticilere devret",
                                    fontSize = 10.sp,
                                    color = Slate500
                                )
                            }
                        }

                        if (state.patentLiquidationCooldown > 0) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFFEF3C7)
                            ) {
                                Text(
                                    text = "Bekleme: ${state.patentLiquidationCooldown} Dönem",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB45309),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val patentCash = 850000L + (state.unlockedTech.size * 50000L)
                    Text(
                        text = "Geliştirdiğiniz Ar-Ge teknolojilerinin kullanım lisansı sektör ortaklarına devredilir. Teknolojileriniz kaybolmaz ancak şirket itibarı 4 puan düşer.",
                        fontSize = 11.sp,
                        color = Slate600,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Getiri: +$${"%,d".format(patentCash)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Green500
                            )
                            Text(
                                text = "Maliyet: -4 İtibar Puanı",
                                fontSize = 10.sp,
                                color = Color(0xFFE11D48)
                            )
                        }

                        Button3D(
                            onClick = onLiquidatePatents,
                            enabled = state.unlockedTech.isNotEmpty() && state.patentLiquidationCooldown <= 0,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Patent Devret", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 3. Emergency Inventory Clearance
        val modelsWithStock = state.activeModels.filter { it.remainingStock > 0 }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Slate200)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📦", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Acil Depo Stok Tasfiyesi (Toptan Satış)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Elde kalan cihazları %50 indirimle tek seferde spot piyasaya devret",
                                fontSize = 10.sp,
                                color = Slate500
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Spot toptancılara yapılan acil tasfiyeler yüksek nakit sağlar ancak marka algısını zedelediği için her model tasfiyesinde şirket itibarı 3 puan düşer.",
                        fontSize = 11.sp,
                        color = Slate600,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (modelsWithStock.isEmpty()) {
                        Text(
                            text = "Şu anda depoda satılmamış aktif model stoğu bulunmuyor.",
                            fontSize = 11.sp,
                            color = Slate400,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    } else {
                        modelsWithStock.forEach { model ->
                            val unitWholesale = (model.specs.price * 0.5f).toInt()
                            val totalYield = model.remainingStock.toLong() * unitWholesale

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        model.specs.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        "${"%,d".format(model.remainingStock)} adet stok • Birim: $$unitWholesale (Normal: $${model.specs.price})",
                                        fontSize = 10.sp,
                                        color = Slate600
                                    )
                                    Text(
                                        "Maliyet: -3 İtibar Puanı (Spot Pazar İmajı)",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFE11D48)
                                    )
                                }

                                Button3D(
                                    onClick = { onLiquidateStock(model.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        "+$${"%,d".format(totalYield)}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
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
fun FinancialStatementTabContent(state: GameState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Income Statement Breakdown
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Slate200)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "📈 AYLIK DÜZENLİ GELİR KALEMLERİ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Green500
                        )
                        Text(
                            "+$${"%,d".format(state.monthlyIncome)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Green500
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Slate200)
                    Spacer(modifier = Modifier.height(6.dp))

                    val appStoreMonthly = state.customOs.lastMonthAppStoreIncome
                    val oemLicenseMonthly = state.customOs.lastMonthLicenseIncome
                    val chipsetOemMonthly = state.lastPeriodChipsetOemRevenue * 2
                    val phoneSalesEstimated = (state.monthlyIncome - appStoreMonthly - oemLicenseMonthly - chipsetOemMonthly).coerceAtLeast(0L)

                    FinancialRow("Cihaz Satış Gelirleri (Telefonlar)", "+$${"%,d".format(phoneSalesEstimated)}", Green500)
                    if (state.customOs.isCustomActive) {
                        FinancialRow("App Store & Servis Komisyonları", "+$${"%,d".format(appStoreMonthly)}", Green500)
                        if (oemLicenseMonthly > 0) {
                            FinancialRow("OEM İşletim Sistemi Lisans Gelirleri", "+$${"%,d".format(oemLicenseMonthly)}", Green500)
                        }
                    }
                    if (chipsetOemMonthly > 0) {
                        FinancialRow("OEM Öz Yonga (Çipset) Satış Geliri", "+$${"%,d".format(chipsetOemMonthly)}", Green500)
                    }
                }
            }
        }

        // Expenses Statement Breakdown
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Slate200)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "📉 AYLIK SABİT GİDER KALEMLERİ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            "-$${"%,d".format(state.totalMonthlyExpenses)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Slate200)
                    Spacer(modifier = Modifier.height(6.dp))

                    FinancialRow("Mühendis Maaşları (${state.engineers} kişi)", "-$${"%,d".format(state.engineerSalary)}", MaterialTheme.colorScheme.error)
                    FinancialRow("QA Test Uzmanı Maaşları (${state.qaInspectors} kişi)", "-$${"%,d".format(state.qaSalary)}", MaterialTheme.colorScheme.error)
                    FinancialRow("Fabrika Montaj İşçisi Maaşları (${state.assemblyWorkers} kişi)", "-$${"%,d".format(state.workerSalary)}", MaterialTheme.colorScheme.error)
                    FinancialRow("Ofis Kirası (${state.currentOfficeTier.name})", "-$${"%,d".format(state.officeExpense)}", MaterialTheme.colorScheme.error)
                    FinancialRow("Fabrika Tesis Bakımı (${state.currentFactoryTier.name})", "-$${"%,d".format(state.factoryMaintenance)}", MaterialTheme.colorScheme.error)
                    if (state.osMaintenanceExpense > 0) {
                        FinancialRow("Yazılım & Güncelleme Altyapı Bakımı", "-$${"%,d".format(state.osMaintenanceExpense)}", MaterialTheme.colorScheme.error)
                    }
                    if (state.totalLoanPeriodPayments > 0) {
                        FinancialRow("Banka Kredi Geri Ödeme Taksitleri (Aylık)", "-$${"%,d".format(state.totalLoanPeriodPayments * 2)}", Color(0xFFE11D48))
                    }
                }
            }
        }

        // Net Cashflow Card
        item {
            val netMonthly = state.monthlyIncome - state.totalMonthlyExpenses
            val isNetPositive = netMonthly >= 0
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isNetPositive) Color(0xFFF0FDF4) else Color(0xFFFFF1F2)
                ),
                border = BorderStroke(1.dp, if (isNetPositive) Color(0xFFBBF7D0) else Color(0xFFFECDD3))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "NET AYLIK NAKİT AKIŞI",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isNetPositive) "Şirket nakit fazlası üretiyor" else "Sabit giderler geliri aşıyor!",
                            fontSize = 10.sp,
                            color = Slate600
                        )
                    }

                    Text(
                        text = if (isNetPositive) "+$${"%,d".format(netMonthly)}" else "-$${"%,d".format(kotlin.math.abs(netMonthly))}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isNetPositive) Green500 else Color(0xFFE11D48)
                    )
                }
            }
        }
    }
}

@Composable
fun FinancialRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 11.sp, color = Slate600)
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}
