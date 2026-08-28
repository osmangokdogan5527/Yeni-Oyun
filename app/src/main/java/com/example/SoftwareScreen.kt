/**
 * Yazılım (Software) ekranı: işletim sistemi geliştirme, mühendis atama ve
 * yazılım modüllerinin (kamera işleme, güvenlik, batarya optimizasyonu vb.) seviye
 * yükseltmesiyle ilgili arayüzü içerir. İş mantığı [com.example.viewmodel.GameViewModel]
 * içindeki yazılım/OS ile ilgili fonksiyonlarda yürütülür.
 */
package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
                            val softwareSectionNames = listOf("İşletim Sistemi", "Cihaz Güncellemeleri", "Ekosistem", "Küresel Rekabet")
                            Text(
                                text = "Ana Ekran  ›  Yazılım  ›  ${softwareSectionNames.getOrElse(selectedTab) { "İşletim Sistemi" }}",
                                fontSize = 9.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
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
                                "Cihaz Güncellemeleri",
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        icon = { Icon(Icons.Default.SystemUpdate, contentDescription = null, modifier = Modifier.size(17.dp)) }
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
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
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
                                            Button3D(
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
    if (customOs.activeDevelopment != null) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🚀 İşletim Sistemi Geliştirme Sürecinde", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Proje: ${customOs.activeDevelopment.name} v${customOs.activeDevelopment.targetVersion}", fontSize = 14.sp)
                    Text("Tür: ${customOs.activeDevelopment.type.title}", fontSize = 14.sp)
                    Text("Kalan Süre: ${customOs.activeDevelopment.remainingMonths} Dönem", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { 
                            1f - (customOs.activeDevelopment.remainingMonths.toFloat() / customOs.activeDevelopment.totalMonths.toFloat())
                        },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
                item {
                    OsHeroStatusCard(
                        customOs = customOs,
                        isUnlocked = isOsResearchUnlocked,
                        onOpenCreateDialog = { showCreateOsDialog = true },
                        onOpenReleaseDialog = { showReleaseUpdateDialog = true },
                        onReleaseHotfix = { viewModel.releaseOsHotfix() }
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

            // --- TAB 1: CİHAZ GÜNCELLEMELERİ ---
            if (selectedTab == 1) {
                if (!customOs.isCustomActive) {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.SystemUpdate, contentDescription = null, modifier = Modifier.size(48.dp), tint = Slate400)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Özel İşletim Sistemi Gerekli", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Cihazlara OTA güncellemesi göndermek için önce kendi işletim sisteminizi geliştirmelisiniz.", fontSize = 12.sp, color = Slate500, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
                            }
                        }
                    }
                } else {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 2.dp
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.SystemUpdate, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Text("İşletim Sistemi Sürüm Yönetimi", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                                Text(
                                    "Büyük sürüm güncellemeleri, piyasadaki eski cihazlarınızın ömrünü uzatır ve müşteri memnuniyetini artırır. Hangi modellerin v${customOs.majorVersionCount + 1}.0 güncellemesini alacağını seçerek yayınlayabilirsiniz.",
                                    fontSize = 12.sp,
                                    color = Slate600
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button3D(
                                    onClick = { showReleaseUpdateDialog = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Text("Büyük Güncelleme Yayınla (v${customOs.majorVersionCount + 1}.0)")
                                }
                            }
                        }
                    }
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
                            
                            
                            
                            
                            val dummyRivals = emptyList<com.example.viewmodel.CompetitorOsInfo>()
                            dummyRivals.forEachIndexed { index, rivalOs ->



                                RivalOsCompactRow(rivalOs = rivalOs)
                                if (index < dummyRivals.size - 1) {
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
        var selectedDevices by remember { mutableStateOf(setOf<String>()) }
        val eligibleDevices = state.activeModels.filter { it.specs.osName.contains(customOs.name) }
        val baseCost = 150000L
        val totalCost = baseCost + (selectedDevices.size * 25000L)

        AlertDialog(
            onDismissRequest = { showReleaseUpdateDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🚀 ", fontSize = 20.sp)
                    Text("Büyük Sistem Güncellemesi", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "${customOs.name} v${customOs.majorVersionCount + 1}.0 güncellemesini yayınlayın. Hangi aktif cihazların bu güncellemeyi (OTA) alacağını seçebilirsiniz:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (eligibleDevices.isEmpty()) {
                        Text("Piyasada bu işletim sistemini kullanan aktif cihazınız bulunmuyor. Yeni üretimler v${customOs.majorVersionCount + 1}.0 ile çıkacaktır.", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 160.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(8.dp)
                        ) {
                            items(eligibleDevices.size) { index ->
                                val device = eligibleDevices[index]
                                val isSelected = selectedDevices.contains(device.id)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { 
                                            selectedDevices = if (isSelected) selectedDevices - device.id else selectedDevices + device.id
                                        }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    androidx.compose.material3.Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = { checked ->
                                            selectedDevices = if (checked) selectedDevices + device.id else selectedDevices - device.id
                                        }
                                    )
                                    Column {
                                        Text(device.specs.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("${device.monthsOnMarket} aydır piyasada", fontSize = 11.sp, color = Slate500)
                                    }
                                }
                            }
                        }
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Temel Dağıtım Gideri:", fontSize = 12.sp)
                                Text("$$baseCost", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Cihaz Başı OTA:", fontSize = 12.sp)
                                Text("$25,000 x ${selectedDevices.size}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            androidx.compose.material3.HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Toplam:", fontWeight = FontWeight.Bold)
                                Text("$${"%,d".format(totalCost)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button3D(
                    onClick = {
                        viewModel.releaseMajorOsUpdate(selectedDevices.toList())
                        showReleaseUpdateDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Yayınla")
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
