/**
 * Yazılım (Software) ekranı: işletim sistemi geliştirme, mühendis atama ve
 * yazılım modüllerinin (kamera işleme, güvenlik, batarya optimizasyonu vb.) seviye
 * yükseltmesiyle ilgili arayüzü içerir. İş mantığı [com.example.viewmodel.GameViewModel]
 * içindeki yazılım/OS ile ilgili fonksiyonlarda yürütülür.
 */
package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ChipsetStudioView
import com.example.ui.theme.*
import com.example.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoftwareScreen(viewModel: GameViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsState()
    val customOs = state.customOs

    var selectedTab by remember { mutableIntStateOf(0) }
    var showCreateOsDialog by remember { mutableStateOf(false) }
    var showReleaseUpdateDialog by remember { mutableStateOf(false) }
    var showFundDialog by remember { mutableStateOf(false) }

    val activeUsers = state.activeUserBase
    val isOsResearchUnlocked = state.unlockedTech.contains("Özel Mobil İşletim Sistemi Mimarisi")
    val isBeingResearched = state.activeResearch?.techId == "Özel Mobil İşletim Sistemi Mimarisi"
    val isQueued = state.researchQueue.any { it.techId == "Özel Mobil İşletim Sistemi Mimarisi" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(customOs.themeColorHex).copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Terminal,
                                contentDescription = null,
                                tint = Color(customOs.themeColorHex),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Yazılım & Ekosistem",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Mobil İşletim Sistemi, Teknoloji Seviyeleri & Rekabet",
                                fontSize = 11.sp,
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
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- TAB SELECTOR ---
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "İşletim Sistemi",
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        icon = { Icon(Icons.Default.PhoneAndroid, contentDescription = null, modifier = Modifier.size(17.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "Öz Yonga & OEM (${state.customChipsets.size})",
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        icon = { Icon(Icons.Default.Memory, contentDescription = null, modifier = Modifier.size(17.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text(
                                "Ekosistem",
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        icon = { Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, modifier = Modifier.size(17.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = {
                            Text(
                                "Küresel Rekabet",
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        icon = { Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(17.dp)) }
                    )
                }
            }

            // --- TAB 0: İŞLETİM SİSTEMİMİZ ---
            if (selectedTab == 0) {
                // 1. AR-GE KİLİT KARTI (Eğer kilitliyse)
                if (!isOsResearchUnlocked) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    2.dp,
                                    Brush.horizontalGradient(listOf(Color(0xFF6366F1), Color(0xFFA855F7))),
                                    RoundedCornerShape(20.dp)
                                )
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .background(Color(0xFF818CF8).copy(alpha = 0.2f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = Color(0xFFA5B4FC),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Özel İşletim Sistemi Mimarisi Kilitli",
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            text = "Gereken Ar-Ge Bütçesi: $100,000,000",
                                            color = Color(0xFFFBBF24),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                Text(
                                    text = "Kendi bağımsız mobil işletim sisteminizi geliştirmek; çekirdek mimarisini sıfırdan tasarlamayı, global uygulama mağazası kurmayı ve üreticilere lisanslamayı kapsayan kapsamlı bir Ar-Ge yatırımı gerektirir.",
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )

                                Surface(
                                    color = Color.Black.copy(alpha = 0.35f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                "Mevcut Durum",
                                                color = Color(0xFF94A3B8),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                if (isBeingResearched) "Şu Anda Ar-Ge Yapılıyor ⏳" else if (isQueued) "Ar-Ge Sırasında Bekliyor 📋" else "Stok Android AOSP Kullanılıyor 📱",
                                                color = if (isBeingResearched) Color(0xFF38BDF8) else Color.White,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        if (!isBeingResearched && !isQueued) {
                                            Button(
                                                onClick = {
                                                    viewModel.startResearch("Özel Mobil İşletim Sistemi Mimarisi", "Özel Mobil İşletim Sistemi Mimarisi", 100000000L)
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                                                shape = RoundedCornerShape(10.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                                            ) {
                                                Text("Ar-Ge'yi Başlat", fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. HERO OS STATUS CARD
                item {
                    OsHeroStatusCard(
                        customOs = customOs,
                        isUnlocked = isOsResearchUnlocked,
                        onOpenCreateDialog = { showCreateOsDialog = true },
                        onOpenReleaseDialog = { showReleaseUpdateDialog = true }
                    )
                }

                // 3. YAZILIM TEKNOLOJİ MODÜLLERİ (Grouped in one card)
                item {
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
                                Icon(Icons.Default.Memory, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Yazılım Teknolojisi & Çekirdek Modülleri", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("İşletim sistemi yeteneklerini geliştirerek sadakati ve pazar payını artırın", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            
                            OsModuleType.entries.forEachIndexed { index, module ->
                                val currentLevel = when (module) {
                                    OsModuleType.KERNEL_ENGINE -> customOs.kernelLevel
                                    OsModuleType.AI_NEURAL -> customOs.aiLevel
                                    OsModuleType.SECURITY_VAULT -> customOs.securityLevel
                                    OsModuleType.CLOUD_SYNC -> customOs.cloudLevel
                                    OsModuleType.APP_STORE_SDK -> customOs.appStoreLevel
                                }
                                val cost = module.baseCost * currentLevel
                                
                                OsModuleCompactRow(
                                    module = module,
                                    level = currentLevel,
                                    upgradeCost = cost,
                                    canAfford = state.budget >= cost,
                                    isMaxLevel = currentLevel >= module.maxLevel,
                                    isUnlocked = isOsResearchUnlocked,
                                    onUpgrade = { viewModel.upgradeOsModule(module) }
                                )
                                
                                if (index < OsModuleType.entries.size - 1) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 4.dp))
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            DevTeamAllocationSection(
                                customOs = customOs,
                                totalEngineers = state.engineers,
                                onDevsChange = { viewModel.setAssignedDevs(it) }
                            )
                            
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            
                            UpdateGuaranteeSelectorSection(
                                currentGuarantee = customOs.updateGuarantee,
                                onSelect = { viewModel.setUpdateGuarantee(it) }
                            )
                        }
                    }
                }
            }

            // --- TAB 1: ÖZ YONGA & OEM SATIŞ (SILICON FOUNDRY) ---
            if (selectedTab == 1) {
                item {
                    ChipsetStudioView(
                        state = state,
                        onSaveChipset = { viewModel.saveCustomChipset(it) },
                        onDeleteChipset = { viewModel.deleteCustomChipset(it) },
                        onUnarchiveChipset = { viewModel.unarchiveCustomChipset(it) },
                        onToggleOemSale = { id, active, price ->
                            viewModel.toggleChipsetOemSale(id, active, price)
                        }
                    )
                }
            }

            // --- TAB 2: EKOSİSTEM & FİNANSMAN ---
            if (selectedTab == 2) {
                // 1. REVENUE METRICS OVERVIEW
                item {
                    EcosystemRevenueOverviewCard(customOs = customOs, state = state)
                }

                // 2. CUSTOMER RETENTION & LOYALTY METER
                item {
                    CustomerLoyaltyCard(customOs = customOs)
                }

                // 3. APP STORE MANAGEMENT & COMMISSION
                item {
                    AppStoreManagementCard(
                        customOs = customOs,
                        state = state,
                        onCommissionSelect = { viewModel.setCommissionRate(it) },
                        onOpenFundDialog = { showFundDialog = true }
                    )
                }

                // 4. GLOBAL DEVELOPER CONFERENCE (DEVCON / WWDC)
                item {
                    GlobalDevConCard(
                        customOs = customOs,
                        state = state,
                        onHost = { viewModel.hostDevConference() }
                    )
                }

                // 5. LİSANS POLİTİKASI & OEM DAĞITIMI
                item {
                    LicenseModelCard(
                        customOs = customOs,
                        onLicenseTypeChange = { viewModel.setOsLicenseType(it) },
                        onFeeChange = { viewModel.setPerDeviceLicenseFee(it) }
                    )
                }
            }

            // --- TAB 3: KÜRESEL OS REKABETİ & TEKNOLOJİ MATRİSİ ---
            if (selectedTab == 3) {
                item {
                    GlobalOsRaceHeaderCard(customOs = customOs)
                }

                item {
                    Text(
                        text = "Küresel Mobil İşletim Sistemi Ekosistemleri",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // First show Player's OS
                item {
                    PlayerOsComparisonCard(customOs = customOs, state = state)
                }

                // Show Competitor Operating Systems (Grouped)
                item {
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
                                Icon(Icons.Default.Public, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Rakiplerin Ekosistemleri", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Küresel pazardaki diğer oyuncuların teknoloji ve güç durumu.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            
                            viewModel.rivalOperatingSystems.forEachIndexed { index, rivalOs ->
                                RivalOsCompactRow(rivalOs = rivalOs)
                                if (index < viewModel.rivalOperatingSystems.size - 1) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 4.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- DIALOGS ---
    if (showCreateOsDialog) {
        CreateOsDialog(
            customOs = customOs,
            currentBudget = state.budget,
            onDismiss = { showCreateOsDialog = false },
            onConfirm = { name, type, license, focus, color, fee ->
                viewModel.createOrUpgradeOs(name, type, license, focus, color, fee)
                showCreateOsDialog = false
            }
        )
    }

    if (showReleaseUpdateDialog) {
        AlertDialog(
            onDismissRequest = { showReleaseUpdateDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🚀 ", fontSize = 20.sp)
                    Text("Büyük Sistem Güncellemesi Yayınla", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Tüm aktif cihazlara ve üçüncü taraf üreticilere ${customOs.name} v${customOs.majorVersionCount + 1}.0 OTA sistem güncellemesi dağıtılacak.",
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("• Ar-Ge & Dağıtım Maliyeti: $150,000", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("• Marka İtibarı Kazancı: +5 İtibar", fontSize = 12.sp, color = Color(0xFF10B981))
                            Text("• Müşteri Memnuniyeti & Güvenlik Tazeleyici", fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.releaseMajorOsUpdate()
                        showReleaseUpdateDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Güncellemeyi Yayınla ($150,000)")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReleaseUpdateDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }

    if (showFundDialog) {
        DeveloperFundDialog(
            currentBalance = customOs.devFundBalance,
            currentBudget = state.budget,
            onDismiss = { showFundDialog = false },
            onInvest = { amount ->
                viewModel.investInDeveloperFund(amount)
                showFundDialog = false
            }
        )
    }
}

// ==========================================
// COMPONENT WIDGETS
// ==========================================

@Composable
fun OsHeroStatusCard(
    customOs: CustomOsState,
    isUnlocked: Boolean,
    onOpenCreateDialog: () -> Unit,
    onOpenReleaseDialog: () -> Unit
) {
    val themeColor = Color(customOs.themeColorHex)

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            themeColor.copy(alpha = 0.15f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Top Row: Title + Version + Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(themeColor, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (customOs.isCustomActive) "📱" else "🤖",
                                fontSize = 20.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f, fill = false)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = customOs.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = themeColor.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "v${customOs.version}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = themeColor,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            }
                            Text(
                                text = "${customOs.type.title} • ${customOs.licenseType.badge}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (customOs.isCustomActive) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFF64748B).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (customOs.isCustomActive) "Aktif" else "Stok Android",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (customOs.isCustomActive) Color(0xFF10B981) else Color(0xFF64748B),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }

                // Stats Grid: Tech Score, Optimization, Ecosystem, Popularity
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OsMetricTile(
                        label = "Teknoloji",
                        value = "${customOs.overallTechScore}/100",
                        subtext = "Gelişmişlik",
                        icon = Icons.Default.Memory,
                        color = Color(0xFF3B82F6),
                        modifier = Modifier.weight(1f)
                    )
                    OsMetricTile(
                        label = "Ekosistem",
                        value = "${customOs.ecosystemScore}/100",
                        subtext = "Bağlılık",
                        icon = Icons.Default.Hub,
                        color = Color(0xFF8B5CF6),
                        modifier = Modifier.weight(1f)
                    )
                    OsMetricTile(
                        label = "Pazar Payı",
                        value = "%${"%.1f".format(customOs.popularityPercent)}",
                        subtext = "Kullanıcı",
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        color = Color(0xFF10B981),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onOpenCreateDialog,
                        enabled = isUnlocked,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (customOs.isCustomActive) "İşletim Sistemini Düzenle" else "Özel OS Geliştir",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (customOs.isCustomActive) {
                        OutlinedButton(
                            onClick = onOpenReleaseDialog,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("OTA Güncellemesi ($150k)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OsMetricTile(
    label: String,
    value: String,
    subtext: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                softWrap = false
            )
            Text(
                text = subtext,
                fontSize = 9.sp,
                color = color,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun OsModuleCompactRow(
    module: OsModuleType,
    level: Int,
    upgradeCost: Long,
    canAfford: Boolean,
    isMaxLevel: Boolean,
    isUnlocked: Boolean,
    onUpgrade: () -> Unit
) {
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
                Text(module.icon, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = module.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = module.summary,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))
            
            if (!isMaxLevel) {
                Button(
                    onClick = onUpgrade,
                    enabled = isUnlocked && canAfford,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = "Lvl ${level+1} ($${"%,d".format(upgradeCost / 1000000)}M)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "MAX LVL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Progress bar and impact text
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            LinearProgressIndicator(
                progress = { level / module.maxLevel.toFloat() },
                modifier = Modifier
                    .weight(0.4f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = if (level >= module.maxLevel) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(modifier = Modifier.width(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(0.6f)) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = module.impactText,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun DevTeamAllocationSection(
    customOs: CustomOsState,
    totalEngineers: Int,
    onDevsChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Engineering, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Yazılım Ar-Ge Mühendisi Atama", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Yazılım ekibi optimizasyon ve otomatik yama üretir", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "${customOs.assignedDevs} / $totalEngineers",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Slider(
            value = customOs.assignedDevs.toFloat(),
            onValueChange = { onDevsChange(it.toInt()) },
            valueRange = 0f..totalEngineers.coerceAtLeast(1).toFloat(),
            steps = if (totalEngineers > 1) totalEngineers - 1 else 0,
            modifier = Modifier.height(24.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Dönem Başı Yazılım Puanı: +${(customOs.assignedDevs * 15) + (totalEngineers * 2)} XP", fontSize = 11.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
            Text("Toplam Yazılım Deneyimi: ${customOs.devXp} XP", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun UpdateGuaranteeSelectorSection(
    currentGuarantee: UpdateGuarantee,
    onSelect: (UpdateGuarantee) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.SecurityUpdateGood, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("Android & OS Güncelleme Taahhüdü", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Uzun vadeli güncelleme taahhüdü müşteri sadakatini artırır", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            UpdateGuarantee.entries.forEach { guarantee ->
                val isSelected = guarantee == currentGuarantee
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onSelect(guarantee) }
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp)
                        ),
                    color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(guarantee.title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text("+${guarantee.reputationBonus} İtibar", fontSize = 9.sp, color = Color(0xFF10B981), fontWeight = FontWeight.SemiBold)
                        Text("$${"%,d".format(guarantee.monthlyCost)}/ay", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 1: EKOSİSTEM & FİNANSMAN WIDGETS
// ==========================================

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

// ==========================================
// TAB 2: KÜRESEL REKABET & TEKNOLOJİ MATRİSİ WIDGETS
// ==========================================

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
                OsCompareMetric(label = "Teknoloji Seviyesi", score = customOs.overallTechScore, color = Color(0xFF3B82F6), modifier = Modifier.weight(1f))
                OsCompareMetric(label = "Ekosistem Gücü", score = customOs.ecosystemScore, color = Color(0xFF8B5CF6), modifier = Modifier.weight(1f))
                OsCompareMetric(label = "Kullanıcı Sadakati", score = customOs.customerLoyaltyPercent.toInt(), color = Color(0xFF10B981), modifier = Modifier.weight(1f))
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
            Text(label, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
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
            modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = textValue ?: "$score/100",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

// ==========================================
// CREATE OS DIALOG & DEVELOPER FUND DIALOG
// ==========================================

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
            Button(
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
            Button(
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
