/**
 * Yazılım (Software) ekranı: işletim sistemi geliştirme, mühendis atama ve
 * yazılım modüllerinin (kamera işleme, güvenlik, batarya optimizasyonu vb.) seviye
 * yükseltmesiyle ilgili arayüzü içerir. İş mantığı [com.example.viewmodel.GameViewModel]
 * içindeki yazılım/OS ile ilgili fonksiyonlarda yürütülür.
 */
package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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

                // 2b. POPÜLERLİK TRENDİ & YETENEK PROFİLİ (sadece aktif özel OS varken anlamlı)
                if (customOs.isCustomActive) {
                    item { OsPopularityTrendCard(customOs = customOs) }
                    item { OsCapabilityRadarCard(customOs = customOs) }
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
