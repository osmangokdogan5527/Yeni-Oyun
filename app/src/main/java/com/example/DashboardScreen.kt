package com.example

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.GameViewModel
import com.example.viewmodel.MarketReport

@Composable
fun GameDashboard(
    modifier: Modifier = Modifier, 
    viewModel: GameViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNewDevice: () -> Unit = {},
    onNavigateToMarket: () -> Unit = {},
    onNavigateToSoftware: () -> Unit = {},
    onEditCompanyProfile: () -> Unit = {},
    onOpenFinance: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()

    val playerLogoDrawable = when (state.companyLogoId) {
        "ic_logo_diamond" -> R.drawable.ic_logo_diamond
        "ic_logo_star" -> R.drawable.ic_logo_star
        "ic_logo_apex" -> R.drawable.ic_logo_apex
        "ic_logo_infinity" -> R.drawable.ic_logo_infinity
        "ic_logo_shield" -> R.drawable.ic_logo_shield
        "ic_logo_nova" -> R.drawable.ic_logo_nova
        "ic_logo_monogram" -> R.drawable.ic_logo_monogram
        "ic_logo_bolt" -> R.drawable.ic_logo_bolt
        "ic_logo_rocket" -> R.drawable.ic_logo_rocket
        "ic_logo_crown" -> R.drawable.ic_logo_crown
        else -> R.drawable.ic_logo_diamond
    }
    val brandColor = Color(state.companyBrandColorHex)

    LaunchedEffect(state.reports.size) {
        if (state.reports.isNotEmpty()) {
            listState.scrollToItem(state.reports.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Finansal Kriz / Negatif Bakiye Uyarısı
        if (state.budget < 0) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 3.dp)
                    .clickable { onOpenFinance() },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                border = BorderStroke(1.dp, Color(0xFFF87171))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🚨", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "FİNANSAL KRİZ UYARISI!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626)
                        )
                        Text(
                            text = "Kasa -$${"%,d".format(kotlin.math.abs(state.budget))} ekside! İflas masasını önlemek için Finans & Kredi Merkezini açın.",
                            fontSize = 10.sp,
                            color = Color(0xFF991B1B)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFDC2626)
                    ) {
                        Text(
                            "Kurtar",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        // Aktif Tedarik Zinciri Olayı Uyarısı
        state.activeSupplyChainEvent?.let { event ->
            val isNegative = event.costMultiplierPercent > 100
            val bannerColor = if (isNegative) Color(0xFFEF4444) else Color(0xFF22C55E)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 3.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = bannerColor.copy(alpha = 0.12f)),
                border = BorderStroke(1.dp, bannerColor.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = event.icon, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = event.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = bannerColor
                        )
                        Text(
                            text = "Üretim maliyeti %${kotlin.math.abs(event.costMultiplierPercent - 100)} ${if (isNegative) "arttı" else "azaldı"} • ${event.remainingPeriods} periyot kaldı",
                            fontSize = 10.sp,
                            color = Slate700
                        )
                    }
                }
            }
        }

        // Company Identity Bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 3.dp)
                .clickable { onEditCompanyProfile() },
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(brandColor)
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = playerLogoDrawable),
                            contentDescription = state.companyName,
                            tint = Color.White,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = state.companyName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = brandColor.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = state.companyLogoStyle,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = brandColor,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "\"${state.companySlogan}\"",
                            fontSize = 10.sp,
                            color = Slate600,
                            maxLines = 1
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, Slate200)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            tint = Slate600,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Düzenle", fontSize = 10.sp, color = Slate600, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Bento Grid
        DashboardStatsGrid(
            budget = state.budget,
            reputation = state.reputation,
            reputationMomentum = state.reputationMomentum,
            modelCount = state.modelCount,
            techLevel = state.techLevel,
            activeLoansCount = state.activeLoans.size,
            totalDebt = state.totalDebt,
            onOpenFinance = onOpenFinance
        )

        // Market & Consumer Trend Snapshot Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clickable { onNavigateToMarket() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B2E))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(state.currentTrend.category.icon, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "🔥 TREND: ${state.currentTrend.title}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFB74D),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            val trendBonusPct = ((state.currentTrend.bonusMultiplier - 1.0f) * 100).toInt()
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF2E7D32)
                            ) {
                                Text(
                                    "+%$trendBonusPct",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                        Text(
                            "Pazar Payınız: %${"%.1f".format(state.playerMarketSharePercent)} • Rakipleri & Trendi Gör 🏆",
                            fontSize = 11.sp,
                            color = Color(0xFFE0E0E0),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Pazara Git",
                    tint = Color(0xFFFFB74D),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Narrative Area
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .background(Color.White, RoundedCornerShape(20.dp))
                .border(1.dp, Slate200, RoundedCornerShape(20.dp))
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.reports) { report ->
                    ReportItem(report)
                }
            }
        }
    }
}

@Composable
fun DashboardAppBar(year: Int, month: Int, onAdvanceTime: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🏢", fontSize = 20.sp)
            }
            Column {
                Text(
                    text = "Smartphone Tycoon",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Slate900,
                    lineHeight = 20.sp
                )
                Text(
                    text = "YÖNETİM PANELİ",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Slate500,
                    letterSpacing = 0.5.sp
                )
            }
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "$month / $year",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            if (onAdvanceTime != null) {
                Button(
                    onClick = onAdvanceTime,
                    modifier = Modifier.height(36.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("İleri (1 Ay)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DashboardStatsGrid(
    budget: Long,
    reputation: Int,
    reputationMomentum: Float = 0f,
    modelCount: Int,
    techLevel: String,
    activeLoansCount: Int = 0,
    totalDebt: Long = 0L,
    onOpenFinance: () -> Unit = {}
) {
    // Format budget
    val formattedBudget = if (budget >= 0) "$%,d".format(budget).replace(',', '.') else "-$%,d".format(kotlin.math.abs(budget)).replace(',', '.')

    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "NAKİT BÜTÇE",
                value = formattedBudget,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onOpenFinance() }
            ) {
                if (totalDebt > 0) {
                    Text(
                        text = "🏦 Borç: $${"%,d".format(totalDebt)} (Finans)",
                        fontSize = 9.sp,
                        color = Color(0xFFE11D48),
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false
                    )
                } else {
                    Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(Slate200, CircleShape)) {
                        val widthFraction = (budget.toFloat() / 20000000f).coerceIn(0f, 1f)
                        Box(modifier = Modifier.fillMaxWidth(widthFraction).height(4.dp).background(Green500, CircleShape))
                    }
                }
            }
            StatCard(
                title = "İTİBAR PUANI",
                value = "%$reputation",
                modifier = Modifier.weight(1f)
            ) {
                val reputationText = when {
                    reputation == 0 -> "Yeni kurulan şirket"
                    reputation < 20 -> "Bilinmiyor"
                    reputation < 50 -> "Gelişmekte"
                    reputation < 80 -> "Tanınan Marka"
                    else -> "Sektör Lideri"
                }
                val momentum = reputationMomentum
                val trendArrow = when {
                    momentum > 0.15f -> "↗ Yükseliyor"
                    momentum < -0.15f -> "↘ Düşüyor"
                    else -> "→ Sabit"
                }
                val trendColor = when {
                    momentum > 0.15f -> Green500
                    momentum < -0.15f -> MaterialTheme.colorScheme.error
                    else -> Slate400
                }
                Text(reputationText, fontSize = 9.sp, color = Slate400, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, fontWeight = FontWeight.Medium, maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis)
                Text(trendArrow, fontSize = 9.sp, color = trendColor, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "MODEL SAYISI",
                value = modelCount.toString(),
                modifier = Modifier.weight(1f)
            ) {
                val modelText = if (modelCount == 0) "Ar-Ge bekleniyor" else "Aktif satışta"
                Text(modelText, fontSize = 9.sp, color = Slate400, maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis)
            }
            StatCard(
                title = "TEKNOLOJİ SEVİYESİ",
                value = techLevel,
                modifier = Modifier.weight(1f)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.padding(top = 4.dp)) {
                    Box(modifier = Modifier.size(8.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
                    Box(modifier = Modifier.size(8.dp).background(if (techLevel != "Giriş") MaterialTheme.colorScheme.primary else Slate200, CircleShape))
                    Box(modifier = Modifier.size(8.dp).background(if (techLevel == "İleri" || techLevel == "Yapay Zeka") MaterialTheme.colorScheme.primary else Slate200, CircleShape))
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Slate500,
                letterSpacing = (-0.5).sp,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun ReportItem(report: MarketReport) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    )
                    Text(
                        text = report.title.uppercase(),
                        color = Slate600,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "YÖNETİM RAPORU",
                    color = Slate400,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Text(
                text = report.text,
                color = Slate800,
                fontSize = 14.sp,
                lineHeight = 22.sp
            )

            if (!report.aiReviewQuote.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "🤖 Yapay Zeka Eleştirmen İncelemesi",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = report.aiReviewQuote,
                            fontSize = 12.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            if (report.unitsSold > 0 || report.profit != 0L || report.reviewScore > 0) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (report.reviewScore > 0) {
                        Column {
                            Text("İnceleme", fontSize = 10.sp, color = Slate500)
                            Text("${report.reviewScore}/100", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (report.unitsSold > 0) {
                        Column {
                            Text("Satışlar", fontSize = 10.sp, color = Slate500)
                            Text("${"%,d".format(report.unitsSold)} adet", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (report.profit != 0L) {
                        Column {
                            Text(if (report.profit >= 0) "Kâr" else "Zarar", fontSize = 10.sp, color = Slate500)
                            Text("$${"%,d".format(Math.abs(report.profit))}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (report.profit >= 0) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}
