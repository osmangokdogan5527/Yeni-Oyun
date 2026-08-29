package com.example

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcquisitionScreen(viewModel: GameViewModel) {
    val companyProfileState by viewModel.companyProfileState.collectAsState()
    val financeState by viewModel.financeState.collectAsState()
    val state by viewModel.state.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Küresel Devler, 1: Şirketimi Sat / Hisse, 2: Startuplar & Portföy

    // Dialog States
    var selectedCompetitorTarget by remember { mutableStateOf<CompetitorCompany?>(null) }
    var selectedStartupTarget by remember { mutableStateOf<AcquisitionTarget?>(null) }
    var bidAmountStr by remember { mutableStateOf("") }
    var selectedStrategy by remember { mutableStateOf(PostAcquisitionStrategy.BECOME_MAIN_BRAND) }
    var showFullExitDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏢", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Şirket Satın Alma & Birleşme (M&A)", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                            Text("Holding Yönetimi, Şirket Satışı ve Küresel Devleri Satın Alma", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. CORPORATE SUMMARY KPI CARD
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.5.dp,
                            Brush.horizontalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                    Color(0xFF8B5CF6).copy(alpha = 0.6f)
                                )
                            ),
                            RoundedCornerShape(18.dp)
                        )
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
                                BrandLogo(
                                    companyId = "player_corp",
                                    companyName = state.companyName,
                                    isPlayer = true,
                                    playerLogoId = state.companyLogoId,
                                    playerBrandColorHex = state.companyBrandColorHex,
                                    size = 38.dp,
                                    shapeRadius = 10.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = state.companyName,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "Kurucu Payı: %${100 - state.equitySoldPercent} (Satılan: %${state.equitySoldPercent})",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "%${"%.1f".format(state.playerMarketSharePercent)} Pazar",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Şirket Piyasa Değeri", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    "$${formatShortCurrency(state.playerValuation)}",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF10B981)
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Kasadaki Nakit", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    "$${formatShortCurrency(financeState.budget)}",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("Alt Markalar", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    "${state.ownedSubBrands.size} Marka",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // 2. TAB SELECTOR
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Küresel Devler", fontSize = 12.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                        icon = { Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Şirketimi Sat", fontSize = 12.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                        icon = { Icon(Icons.Default.MonetizationOn, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Startuplar (${companyProfileState.acquisitionTargets.size})", fontSize = 12.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
                        icon = { Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }
            }

            // --- TAB 0: KÜRESEL DEVLERİ DEVRAL (RAKİPLERİ SATIN AL) ---
            if (selectedTab == 0) {
                item {
                    Text(
                        text = "Satın Alınabilecek Küresel Markalar",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                items(state.competitors.sortedByDescending { it.marketSharePercent }) { comp ->
                    CompetitorAcquisitionCard(
                        competitor = comp,
                        playerBudget = financeState.budget,
                        onBidClick = {
                            selectedCompetitorTarget = comp
                            bidAmountStr = comp.estimatedValuation.toString()
                            selectedStrategy = PostAcquisitionStrategy.BECOME_MAIN_BRAND
                        }
                    )
                }
            }

            // --- TAB 1: ŞİRKETİMİ SAT (HİSSELİ VEYA TAM DEVİR / EXIT) ---
            if (selectedTab == 1) {
                // 1. Şirket Değerleme Raporu
                item {
                    ValuationBreakdownCard(state = state)
                }

                // 2. Hisseli Satış (Equity Funding)
                item {
                    EquitySaleCard(
                        state = state,
                        onSellEquity = { percent -> viewModel.sellCompanyEquity(percent) },
                        onBuybackEquity = { percent -> viewModel.buybackCompanyEquity(percent) }
                    )
                }

                // 3. Tam Şirket Satışı (%100 Exit)
                item {
                    FullExitCard(
                        state = state,
                        onOpenExitDialog = { showFullExitDialog = true }
                    )
                }
            }

            // --- TAB 2: STARTUPLAR VE ALT MARKALAR ---
            if (selectedTab == 2) {
                if (state.ownedSubBrands.isNotEmpty()) {
                    item {
                        Text(
                            text = "Holdinge Bağlı Alt Markalarınız",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    items(state.ownedSubBrands) { subBrand ->
                        SubBrandCard(
                            subBrand = subBrand,
                            onRebrandToMain = {
                                viewModel.rebrandToAcquiredBrand(subBrand.id)
                            }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }

                item {
                    Text(
                        text = "Piyasadaki Teknoloji Girişimleri (Startuplar)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                if (companyProfileState.acquisitionTargets.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                        ) {
                            Text(
                                "Şu an satılık teknoloji girişimi bulunmuyor.",
                                color = Slate500,
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(companyProfileState.acquisitionTargets) { target ->
                        TargetCompanyCard(target = target, onSelect = {
                            selectedStartupTarget = target
                            bidAmountStr = target.valuation.toString()
                            selectedStrategy = PostAcquisitionStrategy.INDEPENDENT_BRAND
                        })
                    }
                }
            }
        }
    }

    // --- COMPREHENSIVE COMPETITOR ACQUISITION DIALOG ---
    if (selectedCompetitorTarget != null) {
        val comp = selectedCompetitorTarget!!
        AlertDialog(
            onDismissRequest = { selectedCompetitorTarget = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BrandLogo(companyId = comp.id, companyName = comp.name, size = 32.dp, shapeRadius = 8.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${comp.name} Satın Alma Teklifi", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Tahmini Değerleme:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("$${formatShortCurrency(comp.estimatedValuation)}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Pazar Payı / Satış:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("%${"%.1f".format(comp.marketSharePercent)} • ${"%,d".format(comp.monthlySales)}/ay", fontWeight = FontWeight.Medium, fontSize = 12.sp)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Kasadaki Bütçeniz:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("$${formatShortCurrency(financeState.budget)}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF10B981))
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = bidAmountStr,
                            onValueChange = { bidAmountStr = it.filter { char -> char.isDigit() } },
                            label = { Text("Teklif Tutarınız ($)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        Text("Satın Alma Sonrası Stratejiniz:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            PostAcquisitionStrategy.entries.forEach { strategy ->
                                val isSelected = selectedStrategy == strategy
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { selectedStrategy = strategy }
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(8.dp)
                                        ),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { selectedStrategy = strategy },
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(strategy.title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text(strategy.description, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button3D(
                    onClick = {
                        val bid = bidAmountStr.toLongOrNull() ?: 0L
                        viewModel.bidForCompetitorCompany(comp.id, bid, selectedStrategy)
                        selectedCompetitorTarget = null
                    },
                    enabled = (bidAmountStr.toLongOrNull() ?: 0L) > 0L
                ) {
                    Text("Teklifi Sun")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedCompetitorTarget = null }) {
                    Text("Vazgeç")
                }
            }
        )
    }

    // --- STARTUP ACQUISITION DIALOG ---
    if (selectedStartupTarget != null) {
        val target = selectedStartupTarget!!
        AlertDialog(
            onDismissRequest = { selectedStartupTarget = null },
            title = { Text("${target.name} İçin Teklif Ver") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Şirket Değerlemesi: $${formatShortCurrency(target.valuation)}", fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = bidAmountStr,
                        onValueChange = { bidAmountStr = it.filter { char -> char.isDigit() } },
                        label = { Text("Teklif (Bütçeniz: $${formatShortCurrency(financeState.budget)})") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Text("Satın Alma Sonrası Strateji:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(
                            PostAcquisitionStrategy.INDEPENDENT_BRAND,
                            PostAcquisitionStrategy.MERGE_TO_MAIN,
                            PostAcquisitionStrategy.LIQUIDATE_ASSETS
                        ).forEach { strategy ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedStrategy = strategy }
                                    .padding(vertical = 4.dp)
                            ) {
                                RadioButton(selected = selectedStrategy == strategy, onClick = { selectedStrategy = strategy })
                                Column {
                                    Text(strategy.title, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                    Text(strategy.description, fontSize = 10.5.sp, color = Slate500)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button3D(onClick = {
                    val bid = bidAmountStr.toLongOrNull() ?: 0L
                    viewModel.bidForCompany(target.id, bid, selectedStrategy)
                    selectedStartupTarget = null
                }) {
                    Text("Teklifi Gönder")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedStartupTarget = null }) {
                    Text("İptal")
                }
            }
        )
    }

    // --- FULL EXIT CONFIRMATION DIALOG ---
    if (showFullExitDialog) {
        AlertDialog(
            onDismissRequest = { showFullExitDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⚠️ ", fontSize = 22.sp)
                    Text("Şirketi Tamamen Sat (100% Exit)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "${state.companyName} şirketinizin tamamını uluslararası teknoloji konsorsiyumuna $${formatShortCurrency(state.playerValuation)} bedelle satmak üzeresiniz.",
                        fontSize = 13.sp
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Elde Edilecek Nakit:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text("+$${formatShortCurrency(state.playerValuation)}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                            Text("Tüm bu nakit kasaya aktarılır ve 'Küresel Devler' sekmesinden dilediğiniz şirketi (Örn: Apple, Samsung vb.) satın alıp doğrudan yeni CEO'su olabilirsiniz!", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f), modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            },
            confirmButton = {
                Button3D(
                    onClick = {
                        viewModel.sellCompanyEntirely()
                        showFullExitDialog = false
                        selectedTab = 0 // Navigate to Global giants
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Text("Onayla ve Sat ($${formatShortCurrency(state.playerValuation)})")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFullExitDialog = false }) {
                    Text("Vazgeç")
                }
            }
        )
    }
}

@Composable
fun CompetitorAcquisitionCard(
    competitor: CompetitorCompany,
    playerBudget: Long,
    onBidClick: () -> Unit
) {
    val canAfford = playerBudget >= competitor.estimatedValuation

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
                    BrandLogo(
                        companyId = competitor.id,
                        companyName = competitor.name,
                        size = 44.dp,
                        shapeRadius = 11.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(competitor.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(competitor.brandColorHex))
                        Text("\"${competitor.slogan}\"", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(competitor.brandColorHex).copy(alpha = 0.12f)
                    ) {
                        Text(
                            "%${"%.1f".format(competitor.marketSharePercent)} Pazar",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(competitor.brandColorHex),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "$${formatShortCurrency(competitor.estimatedValuation)}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Aylık Satış", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${"%,d".format(competitor.monthlySales)} adet", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }
                Column {
                    Text("Son Amiral Gemisi", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(competitor.currentTopModel, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Model Skoru", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${competitor.currentModelScore}/100 Puan", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Color(0xFF10B981))
                }
            }

            Button3D(
                onClick = onBidClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (canAfford) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Icon(Icons.Default.Handshake, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    if (canAfford) "Satın Alma Teklifi Ver ($${formatShortCurrency(competitor.estimatedValuation)})" else "Teklif Ver ($${formatShortCurrency(competitor.estimatedValuation)})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ValuationBreakdownCard(state: GameState) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Assessment, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Şirket Değerleme Dağılımı", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Varlıklar, patentler, fabrika kapasitesi ve pazar payı çarpanı", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF10B981).copy(alpha = 0.1f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("TOPLAM ŞİRKET DEĞERİ", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(
                        "$${formatShortCurrency(state.playerValuation)}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = Color(0xFF10B981)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                ValuationItemRow("Üretim & Fabrika Tesisleri", state.factoryValuation)
                ValuationItemRow("Ofis & Ar-Ge Tesisleri", state.officeValuation)
                ValuationItemRow("Mühendis & Uzman Personel", state.employeeValuation)
                ValuationItemRow("Teknoloji, OS & Çipler", state.techAndChipValuation)
                ValuationItemRow("Pazar Payı & Marka Değeri", state.marketShareValuation)
                if (state.ownedSubBrands.isNotEmpty()) {
                    ValuationItemRow("Alt Marka Portföyü", state.subBrandsValuation)
                }
            }
        }
    }
}

@Composable
fun ValuationItemRow(label: String, value: Long) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("$${formatShortCurrency(value)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun EquitySaleCard(
    state: GameState,
    onSellEquity: (Int) -> Unit,
    onBuybackEquity: (Int) -> Unit
) {
    val remainingControl = 100 - state.equitySoldPercent

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
                Icon(Icons.Default.PieChart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Hisseli Satış (Yatırımcı Sermayesi)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Şirket hissesi satarak kasaya anında yüksek nakit fon çekin", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Kurucu Hissesi (Kontrol)", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("%$remainingControl", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Yatırımcılara Satılan", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("%${state.equitySoldPercent} (Maks %49)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFFF59E0B))
                    }
                }
            }

            Text("Hisse Sat & Sermaye Çek:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(5, 10, 20).forEach { percent ->
                    val yield = (state.playerValuation * (percent / 100.0)).toLong()
                    val canSell = (state.equitySoldPercent + percent) <= 49

                    Button3D(
                        onClick = { onSellEquity(percent) },
                        enabled = canSell,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("+%$percent Sat", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("+$${formatShortCurrency(yield)}", fontSize = 9.sp)
                        }
                    }
                }
            }

            if (state.equitySoldPercent > 0) {
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                Text("Hisse Geri Alım (Kurucu Payını Yükselt):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(5, 10).filter { it <= state.equitySoldPercent }.forEach { percent ->
                        val cost = (state.playerValuation * (percent / 100.0) * 1.05).toLong()

                        Button3D(
                            onClick = { onBuybackEquity(percent) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64748B)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("%$percent Geri Al", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("-$${formatShortCurrency(cost)}", fontSize = 9.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FullExitCard(
    state: GameState,
    onOpenExitDialog: () -> Unit
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🚀", fontSize = 22.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Tam Şirket Satışı (%100 Exit)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                    Text("Şirketinizi satıp devasa nakit elde edin ve bir dünya devini satın alın!", fontSize = 11.sp, color = Color(0xFF94A3B8))
                }
            }

            Text(
                "${state.companyName} markasını $${formatShortCurrency(state.playerValuation)} karşılığında tamamen devrederek kasadaki dev servetinizle Samsung veya Apple gibi bir dünya devini satın alabilir ve doğrudan CEO'su olabilirsiniz!",
                fontSize = 11.5.sp,
                color = Color(0xFFCBD5E1),
                lineHeight = 16.sp
            )

            Button3D(
                onClick = onOpenExitDialog,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.MonetizationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Şirketi Tamamen Sat ($${formatShortCurrency(state.playerValuation)})", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SubBrandCard(
    subBrand: OwnedSubBrand,
    onRebrandToMain: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(subBrand.logoEmoji, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(subBrand.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Holding Alt Markası • %${"%.1f".format(subBrand.marketSharePercent)} Pazar Payı", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.15f)
                ) {
                    Text(
                        "+$${formatShortCurrency(subBrand.monthlyDividend)}/ay Temettü",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button3D(
                    onClick = onRebrandToMain,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("Bu Markayı Ana Şirketim Yap 👑", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TargetCompanyCard(target: AcquisitionTarget, onSelect: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(target.logoEmoji, fontSize = 24.sp)
                    Column {
                        Text(target.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(target.type.description, fontSize = 11.sp, color = Slate500)
                    }
                }
                Text("$${formatShortCurrency(target.valuation)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("Marka İtibarı", fontSize = 10.5.sp, color = Slate500)
                    Text("${target.brandReputation}/100", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }
                Column {
                    Text("Çalışan", fontSize = 10.5.sp, color = Slate500)
                    Text("${target.employees} Kişi", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }
                Column {
                    Text("Patent", fontSize = 10.5.sp, color = Slate500)
                    Text("${target.patents.size} Adet", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }
                Column {
                    Text("Seriler", fontSize = 10.5.sp, color = Slate500)
                    Text("${target.activeSeries.size} Seri", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }
            }

            if (target.activeSeries.isNotEmpty()) {
                Text("Aktif Telefon Serileri:", fontSize = 10.5.sp, color = Slate500, modifier = Modifier.padding(top = 2.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    target.activeSeries.forEach { series ->
                        Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(4.dp)) {
                            Text(series.seriesName, fontSize = 10.5.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))
            Button3D(onClick = onSelect, modifier = Modifier.fillMaxWidth()) {
                Text("İncele & Teklif Ver")
            }
        }
    }
}
