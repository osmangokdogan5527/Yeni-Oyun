package com.example

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.CompetitorCompany
import com.example.viewmodel.GameState
import com.example.viewmodel.MarketTrend

@Composable
fun BrandLogo(
    companyId: String? = null,
    companyName: String? = null,
    isPlayer: Boolean = false,
    playerLogoId: String? = null,
    playerBrandColorHex: Long? = null,
    size: Dp = 38.dp,
    shapeRadius: Dp = 10.dp
) {
    val name = (companyName ?: "").lowercase()
    val id = (companyId ?: "").lowercase()

    val isPlayerBrand = isPlayer || id == "player_corp" || name.contains("şirket") || name.contains("sirket")
    val isApple = id == "comp_apple" || name.contains("apple") || name.contains("iphone")
    val isSamsung = id == "comp_samsung" || name.contains("samsung") || name.contains("galaxy")
    val isXiaomi = id == "comp_xiaomi" || name.contains("xiaomi") || name.contains("redmi") || name.contains("poco")
    val isOppo = id == "comp_oppo" || name.contains("oppo")
    val isVivo = id == "comp_vivo" || name.contains("vivo")
    val isHuawei = id == "comp_huawei" || name.contains("huawei") || name.contains("harmony") || name.contains("pura") || name.contains("mate")
    val isGoogle = id == "comp_google" || name.contains("google") || name.contains("pixel") || name.contains("nexus")
    val isMotorola = id == "comp_motorola" || name.contains("motorola") || name.contains("moto") || name.contains("razr")
    val isOnePlus = id == "comp_oneplus" || name.contains("oneplus")
    val isRealme = id == "comp_realme" || name.contains("realme")
    val isHonor = id == "comp_honor" || name.contains("honor")
    val isSony = id == "comp_sony" || name.contains("sony") || name.contains("xperia")
    val isAsus = id == "comp_asus" || name.contains("asus") || name.contains("rog") || name.contains("zenfone")
    val isNokia = id == "comp_nokia" || name.contains("nokia") || name.contains("hmd")
    val isTecno = id == "comp_tecno" || name.contains("tecno") || name.contains("phantom") || name.contains("camon")
    val isInfinix = id == "comp_infinix" || name.contains("infinix")
    val isNothing = id == "comp_nothing" || name.contains("nothing")
    val isZte = id == "comp_zte" || name.contains("zte") || name.contains("nubia") || name.contains("redmagic")
    val isTcl = id == "comp_tcl" || name.contains("tcl")
    val isFairphone = id == "comp_fairphone" || name.contains("fairphone")

    val playerDrawable = when (playerLogoId) {
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
        else -> R.drawable.ic_brand_player
    }
    val playerColor = if (playerBrandColorHex != null) Color(playerBrandColorHex) else Color(0xFF2563EB)

    val (drawableRes, bgColor, tintColor) = when {
        isPlayerBrand -> Triple(playerDrawable, playerColor, Color.White)
        isApple -> Triple(R.drawable.ic_brand_apple, Color(0xFF0F172A), Color.White)
        isSamsung -> Triple(R.drawable.ic_brand_samsung, Color(0xFF0D47A1), Color.Unspecified)
        isXiaomi -> Triple(R.drawable.ic_brand_xiaomi, Color(0xFFFF6900), Color.Unspecified)
        isOppo -> Triple(R.drawable.ic_brand_oppo, Color(0xFF008A4B), Color.White)
        isVivo -> Triple(R.drawable.ic_brand_vivo, Color(0xFF0057FF), Color.White)
        isHuawei -> Triple(R.drawable.ic_brand_huawei, Color(0xFFFFFFFF), Color.Unspecified)
        isGoogle -> Triple(R.drawable.ic_brand_google, Color(0xFFFFFFFF), Color.Unspecified)
        isMotorola -> Triple(R.drawable.ic_brand_motorola, Color(0xFF001489), Color.Unspecified)
        isOnePlus -> Triple(R.drawable.ic_brand_oneplus, Color(0xFFEB0028), Color.Unspecified)
        isRealme -> Triple(R.drawable.ic_brand_realme, Color(0xFFFFC915), Color.Unspecified)
        isHonor -> Triple(R.drawable.ic_brand_honor, Color(0xFF0F172A), Color.Unspecified)
        isSony -> Triple(R.drawable.ic_brand_sony, Color(0xFF000000), Color.Unspecified)
        isAsus -> Triple(R.drawable.ic_brand_asus, Color(0xFF111827), Color.Unspecified)
        isNokia -> Triple(R.drawable.ic_brand_nokia, Color(0xFF124191), Color.Unspecified)
        isTecno -> Triple(R.drawable.ic_brand_tecno, Color(0xFF0072CE), Color.Unspecified)
        isInfinix -> Triple(R.drawable.ic_brand_infinix, Color(0xFF1E824C), Color.Unspecified)
        isNothing -> Triple(R.drawable.ic_brand_nothing, Color(0xFF18181B), Color.Unspecified)
        isZte -> Triple(R.drawable.ic_brand_zte, Color(0xFF008CD6), Color.Unspecified)
        isTcl -> Triple(R.drawable.ic_brand_tcl, Color(0xFFED1C24), Color.Unspecified)
        isFairphone -> Triple(R.drawable.ic_brand_fairphone, Color(0xFF0084A8), Color.Unspecified)
        else -> Triple(R.drawable.ic_brand_player, Color(0xFF334155), Color.Unspecified)
    }

    val internalPadding = when {
        isApple -> (size.value * 0.18f).dp
        isSamsung -> (size.value * 0.08f).dp
        isXiaomi -> 0.dp
        isGoogle || isHuawei -> (size.value * 0.14f).dp
        isOppo || isVivo -> (size.value * 0.12f).dp
        else -> (size.value * 0.10f).dp
    }

    val needsBorder = isGoogle || isHuawei

    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(shapeRadius))
            .background(bgColor)
            .then(
                if (needsBorder) Modifier.border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(shapeRadius))
                else Modifier
            )
            .padding(internalPadding),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = drawableRes),
            contentDescription = companyName ?: "Marka Logosu",
            tint = tintColor,
            modifier = Modifier.fillMaxSize()
        )
    }
}

data class MarketParticipant(
    val id: String,
    val name: String,
    val logoEmoji: String,
    val isPlayer: Boolean,
    val marketShare: Float,
    val monthlySales: Int,
    val currentModel: String,
    val modelPrice: Int,
    val modelScore: Int,
    val strategy: String,
    val brandColorHex: Long,
    val logoId: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketScreen(
    state: GameState,
    onNavigateToBuilder: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Pazar Payı, 1: Rakip Analizi, 2: Lansman Akışı

    // Build sorted participant list (Player + Competitors)
    val playerParticipant = MarketParticipant(
        id = "player_corp",
        name = state.companyName,
        logoEmoji = "📱",
        isPlayer = true,
        marketShare = state.playerMarketSharePercent,
        monthlySales = state.activeModels.filter { !it.isCompleted }.sumOf { model ->
            // Current monthly run-rate estimate
            val baseBatch = model.totalStock / model.maxMonthsOnMarket.toFloat()
            val qFactor = (model.reviewScore / 55.0f).coerceIn(0.4f, 2.0f)
            val rFactor = 1.0f + (state.reputation / 200.0f)
            val tFactor = if (model.matchesTrend) (state.currentTrend.bonusMultiplier) else 1.0f
            (baseBatch * qFactor * rFactor * tFactor).toInt().coerceIn(0, model.remainingStock)
        },
        currentModel = state.activeModels.firstOrNull { !it.isCompleted }?.specs?.name ?: "Satışta Model Yok",
        modelPrice = state.activeModels.firstOrNull { !it.isCompleted }?.specs?.price ?: 0,
        modelScore = state.activeModels.firstOrNull { !it.isCompleted }?.reviewScore ?: 0,
        strategy = if (state.activeModels.any { it.matchesTrend }) "Trend Avcısı (+%${((state.currentTrend.bonusMultiplier - 1f) * 100).toInt()})" else "Dengeli Üretim",
        brandColorHex = state.companyBrandColorHex,
        logoId = state.companyLogoId
    )

    val competitorParticipants = state.competitors.map { comp ->
        MarketParticipant(
            id = comp.id,
            name = comp.name,
            logoEmoji = comp.logoEmoji,
            isPlayer = false,
            marketShare = comp.marketSharePercent,
            monthlySales = comp.monthlySales,
            currentModel = comp.currentTopModel,
            modelPrice = comp.currentModelPrice,
            modelScore = comp.currentModelScore,
            strategy = comp.strategyType,
            brandColorHex = comp.brandColorHex
        )
    }

    val allParticipants = (listOf(playerParticipant) + competitorParticipants)
        .sortedByDescending { it.marketShare }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🏆 ", fontSize = 20.sp)
                        Column {
                            Text("Piyasa Dinamikleri & Rakipler", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(
                                "Küresel Akıllı Telefon Pazarı (${state.year})",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. ACTIVE CONSUMER TREND CARD
            item {
                ActiveTrendCard(
                    trend = state.currentTrend,
                    onNavigateToBuilder = onNavigateToBuilder
                )
            }

            // 2. MARKET OVERVIEW KPI
            item {
                MarketOverviewKpiCard(
                    playerShare = state.playerMarketSharePercent,
                    totalVolume = state.totalMarketMonthlyVolume,
                    leader = allParticipants.firstOrNull()
                )
            }

            // 3. TABS SELECTOR
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Pazar Payı", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                        icon = { Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Rakip Analizi", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                        icon = { Icon(Icons.Default.Business, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Lansmanlar (${state.competitorReleases.size})", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
                        icon = { Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }
            }

            // 4. TAB CONTENTS
            when (selectedTab) {
                0 -> {
                    // Pazar Payı Tablosu
                    items(allParticipants) { participant ->
                        val rankIndex = allParticipants.indexOf(participant) + 1
                        MarketParticipantRowCard(
                            participant = participant,
                            rank = rankIndex
                        )
                    }
                }
                1 -> {
                    // Rakip Şirket Profilleri
                    items(state.competitors) { comp ->
                        CompetitorProfileCard(competitor = comp)
                    }
                }
                2 -> {
                    // Lansman Geçmişi
                    if (state.competitorReleases.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "Henüz rakip şirket lansmanı kaydedilmedi.",
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        "Zaman ilerledikçe Apple, Samsung ve Xiaomi yeni telefonlarını duyuracak!",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    } else {
                        items(state.competitorReleases) { release ->
                            CompetitorReleaseRow(release = release)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveTrendCard(
    trend: MarketTrend,
    onNavigateToBuilder: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.5.dp,
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFFFF9800),
                        Color(0xFFE91E63),
                        Color(0xFF9C27B0)
                    )
                ),
                RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1B2E)
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF5722)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(trend.category.icon, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "🔥 AKTİF TÜKETİCİ TRENDİ",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFFFB74D),
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            trend.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF2E7D32)
                ) {
                    val bonusPct = ((trend.bonusMultiplier - 1.0f) * 100).toInt()
                    Text(
                        text = "+%$bonusPct SATIŞ",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = trend.description,
                fontSize = 13.sp,
                color = Color(0xFFE0E0E0),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // How to catch tip
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF2A2742)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = Color(0xFFFFD54F),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "İpucu: ${trend.category.tip}",
                        fontSize = 12.sp,
                        color = Color(0xFFFFECB3),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⏳ Kalan Süre: ${trend.remainingMonths} Ay",
                    fontSize = 12.sp,
                    color = Color(0xFFB0BEC5),
                    fontWeight = FontWeight.SemiBold
                )

                Button(
                    onClick = onNavigateToBuilder,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9800)
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Trende Uygun Üret", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MarketOverviewKpiCard(
    playerShare: Float,
    totalVolume: Int,
    leader: MarketParticipant?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Pazar Payınız",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "%${"%.1f".format(playerShare)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Divider(
                modifier = Modifier
                    .height(40.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Aylık Pazar Hacmi",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "%,d".format(totalVolume).replace(',', '.'),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Divider(
                modifier = Modifier
                    .height(40.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Pazar Lideri",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (leader != null) {
                        BrandLogo(
                            companyId = leader.id,
                            companyName = leader.name,
                            isPlayer = leader.isPlayer,
                            size = 20.dp,
                            shapeRadius = 5.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        leader?.name ?: "Bilinmiyor",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )
                }
            }
        }
    }
}

@Composable
fun MarketParticipantRowCard(
    participant: MarketParticipant,
    rank: Int
) {
    val rankBadge = when (rank) {
        1 -> "🥇 1."
        2 -> "🥈 2."
        3 -> "🥉 3."
        else -> "#$rank"
    }

    val animatedProgress by animateFloatAsState(
        targetValue = (participant.marketShare / 100f).coerceIn(0.01f, 1f),
        label = "shareProgress"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (participant.isPlayer) {
                    Modifier.border(
                        2.dp,
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(16.dp)
                    )
                } else Modifier
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (participant.isPlayer) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (participant.isPlayer) 3.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left content with Rank, Logo, Name & Strategy
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = rankBadge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = when (rank) {
                            1 -> Color(0xFFFFB300)
                            2 -> Color(0xFF78909C)
                            3 -> Color(0xFF8D6E63)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BrandLogo(
                        companyId = participant.id,
                        companyName = participant.name,
                        isPlayer = participant.isPlayer,
                        playerLogoId = participant.logoId,
                        playerBrandColorHex = participant.brandColorHex,
                        size = 34.dp,
                        shapeRadius = 9.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = participant.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (participant.isPlayer) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.primary
                                ) {
                                    Text(
                                        "SİZ",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = participant.strategy,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Right percentage & monthly sales (Guaranteed min space and no squishing)
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Text(
                        text = "%${"%.1f".format(participant.marketShare)}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = Color(participant.brandColorHex),
                        maxLines = 1,
                        softWrap = false
                    )
                    Text(
                        text = "${"%,d".format(participant.monthlySales)} adet/ay",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Market share progress bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(participant.brandColorHex),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Top model info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.PhoneAndroid,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = participant.currentModel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                if (participant.modelPrice > 0) {
                    Text(
                        text = "$${participant.modelPrice} • ${participant.modelScore}/100 Puan",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
    }
}

@Composable
fun CompetitorProfileCard(competitor: CompetitorCompany) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BrandLogo(
                    companyId = competitor.id,
                    companyName = competitor.name,
                    size = 46.dp,
                    shapeRadius = 12.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        competitor.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(competitor.brandColorHex)
                    )
                    Text(
                        "\"${competitor.slogan}\"",
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(competitor.brandColorHex).copy(alpha = 0.12f)
                ) {
                    Text(
                        "%${"%.1f".format(competitor.marketSharePercent)} Pazar",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(competitor.brandColorHex),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(12.dp))

            // Strateji
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.TrendingUp, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "Strateji: ${competitor.strategyType}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Güçlü Yön
            Row(verticalAlignment = Alignment.Top) {
                Text("✅ ", fontSize = 12.sp)
                Text(
                    competitor.strengthText,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Zayıf Yön
            Row(verticalAlignment = Alignment.Top) {
                Text("⚠️ ", fontSize = 12.sp)
                Text(
                    competitor.weaknessText,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Son Amiral Gemisi", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(competitor.currentTopModel, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Text(
                        "$${competitor.currentModelPrice} • ${competitor.currentModelScore}/100 Puan",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CompetitorReleaseRow(release: com.example.viewmodel.CompetitorReleaseHistory) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BrandLogo(
                companyName = release.companyName,
                size = 38.dp,
                shapeRadius = 10.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    release.headline,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    "${release.companyName} • $${release.price} • Puan: ${release.score}/100",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    "${release.month}/${release.year}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
