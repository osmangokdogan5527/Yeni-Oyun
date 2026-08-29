/**
 * Uygulamanın giriş noktası ve ana navigasyon iskeleti.
 *
 * [MainActivity] tek bir Compose ekranı ([MainApp]) başlatır; sekmeler arası geçiş
 * [AppScreen] enum'u ile yönetilir (Dashboard, Devices, Market, Benchmark, Software,
 * PhoneBuilder, Research, Employees, News). Ekran bileşenleri modüler olarak ayrı
 * dosyalarda tutulmaktadır.
 */
package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.BenchmarkScreen
import com.example.ui.FinancialHubDialog
import com.example.ui.SaveLoadDialog
import com.example.ui.theme.*
import com.example.viewmodel.GameState
import com.example.viewmodel.GameViewModel

enum class AppScreen {
    CompanyHub, Devices, TechHub, Market, PhoneBuilder
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun GameTopBar(
    state: GameState,
    onAdvanceTime: () -> Unit,
    onOpenCompanyProfile: () -> Unit = {},
    onOpenSaveLoad: () -> Unit = {},
    onOpenAchievements: () -> Unit = {},
    onOpenFinance: () -> Unit = {}
) {
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

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sol: Şirket Logosu ve Tarih
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.clickable { onOpenCompanyProfile() }
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
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
                Column {
                    Text(
                        text = state.companyName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    val monthName = GameViewModel.getMonthName(state.month)
                    val periodLabel = if (state.period == 1) "(1.Yarı)" else "(2.Yarı)"
                    Text(
                        text = "$monthName ${state.year} $periodLabel",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Orta: Bütçe (Finans Menüsüne Gider)
            val isNegative = state.budget < 0
            val budgetDisplay = if (state.budget >= 0) "$${"%,d".format(state.budget).replace(',', '.')}" else "-$${"%,d".format(kotlin.math.abs(state.budget)).replace(',', '.')}"
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onOpenFinance() }
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text("Bütçe", fontSize = 9.sp, color = Slate500)
                androidx.compose.animation.AnimatedContent(
                    targetState = budgetDisplay,
                    label = "BudgetAnimation"
                ) { targetBudget ->
                    Text(
                        targetBudget,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (isNegative) Color(0xFFDC2626) else Green500
                    )
                }
            }

            // Sağ: Hızlı Aksiyonlar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                IconButton(onClick = onOpenAchievements, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = "Başarımlar", tint = Color(0xFFFACC15), modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onOpenSaveLoad, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Settings, contentDescription = "Ayarlar/Kayıt", tint = Slate500, modifier = Modifier.size(20.dp))
                }
                
                Button3D(
                    onClick = onAdvanceTime,
                    modifier = Modifier.height(34.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(17.dp)
                ) {
                    Text("İlerle ⏩", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MainApp(viewModel: GameViewModel = viewModel()) {
    var currentScreen by remember { mutableStateOf(AppScreen.CompanyHub) }
    var showEditCompanyDialog by remember { mutableStateOf(false) }
    var showSaveLoadDialog by remember { mutableStateOf(false) }
    var showAchievementsDialog by remember { mutableStateOf(false) }
    var showFinancialHubDialog by remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsState()
    val savedGames by viewModel.savedGamesState.collectAsState()

    // Finans & Bankacılık Merkezi Dialogu
    if (showFinancialHubDialog) {
        FinancialHubDialog(
            state = state,
            onDismiss = { showFinancialHubDialog = false },
            onTakeLoan = { loanType -> viewModel.takeOutLoan(loanType) },
            onPayOffEarly = { loanId -> viewModel.payOffLoanEarly(loanId) },
            onLiquidatePatents = { viewModel.liquidatePatents() },
            onSeekVentureCapital = { viewModel.seekVentureCapital() },
            onLiquidateStock = { modelId -> viewModel.emergencyLiquidateStock(modelId) }
        )
    }

    // Başarım Kilidi Açıldı Kutlama Kartı
    if (state.lastUnlockedAchievementIds.isNotEmpty()) {
        val unlockedAchievements = com.example.viewmodel.ALL_ACHIEVEMENTS.filter { it.id in state.lastUnlockedAchievementIds }
        AchievementUnlockedBanner(
            achievements = unlockedAchievements,
            onDismiss = { viewModel.clearLastUnlockedAchievements() }
        )
    }

    // Başarımlar Ekranı
    if (showAchievementsDialog) {
        AchievementsScreen(
            unlockedIds = state.unlockedAchievementIds,
            onDismiss = { showAchievementsDialog = false }
        )
    }

    // Save & Load Dialog
    if (showSaveLoadDialog) {
        SaveLoadDialog(
            currentState = state,
            savedGames = savedGames,
            onDismiss = { showSaveLoadDialog = false },
            onSaveSlot = { slotId, name -> viewModel.manualSaveGame(slotId, name) },
            onLoadSlot = { slotId -> viewModel.loadGame(slotId) },
            onDeleteSlot = { slotId -> viewModel.deleteSave(slotId) },
            onNewGame = { viewModel.startNewGame() }
        )
    }

    // Company Setup on First Launch or User Edit
    if (!state.isCompanySetupDone) {
        CompanySetupDialog(
            initialName = state.companyName,
            initialLogoId = state.companyLogoId,
            initialBrandColorHex = state.companyBrandColorHex,
            initialSlogan = state.companySlogan,
            isFirstLaunch = true,
            onSaveProfile = { name, logoId, logoStyle, brandColorHex, slogan ->
                viewModel.setCompanyProfile(name, logoId, logoStyle, brandColorHex, slogan)
            }
        )
    } else if (showEditCompanyDialog) {
        CompanySetupDialog(
            initialName = state.companyName,
            initialLogoId = state.companyLogoId,
            initialBrandColorHex = state.companyBrandColorHex,
            initialSlogan = state.companySlogan,
            isFirstLaunch = false,
            onDismiss = { showEditCompanyDialog = false },
            onSaveProfile = { name, logoId, logoStyle, brandColorHex, slogan ->
                viewModel.setCompanyProfile(name, logoId, logoStyle, brandColorHex, slogan)
                showEditCompanyDialog = false
            }
        )
    }

    // Tech Expo Grand Awards Ceremony Modal
    state.activeTechExpo?.let { expoEvent ->
        TechExpoCeremonyDialog(
            event = expoEvent,
            playerBrandColorHex = state.companyBrandColorHex,
            onDismiss = { viewModel.dismissTechExpo() }
        )
    }

    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }
        LaunchedEffect(state.noticeMessage) {
            state.noticeMessage?.let {
                snackbarHostState.showSnackbar(it)
                viewModel.clearNoticeMessage()
            }
        }
        Scaffold(
            snackbarHost = { androidx.compose.material3.SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (currentScreen != AppScreen.PhoneBuilder) {
                GameTopBar(
                    state = state,
                    onAdvanceTime = { viewModel.advanceTime() },
                    onOpenCompanyProfile = { showEditCompanyDialog = true },
                    onOpenSaveLoad = { showSaveLoadDialog = true },
                    onOpenAchievements = { showAchievementsDialog = true },
                    onOpenFinance = { showFinancialHubDialog = true }
                )
            }
        },
        bottomBar = {
            if (currentScreen != AppScreen.PhoneBuilder) {
                BottomNavigationBar(
                    currentScreen = currentScreen,
                    onNavigate = { currentScreen = it }
                )
            }
        }
    ) { innerPadding ->
        when (currentScreen) {
            AppScreen.CompanyHub -> {
                var selectedTab by remember { mutableIntStateOf(0) }
                Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                    ScrollableTabRow(
                        selectedTabIndex = selectedTab,
                        edgePadding = 0.dp
                    ) {
                        Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Özet") })
                        Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Personel") })
                        Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("M&A") })
                        Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("Haberler") })
                    }
                    when (selectedTab) {
                        0 -> GameDashboard(
                            viewModel = viewModel, 
                            onNewDevice = { currentScreen = AppScreen.PhoneBuilder },
                            onNavigateToMarket = { currentScreen = AppScreen.Market },
                            onNavigateToSoftware = { currentScreen = AppScreen.TechHub },
                            onEditCompanyProfile = { showEditCompanyDialog = true },
                            onOpenFinance = { showFinancialHubDialog = true }
                        )
                        1 -> EmployeesScreen(viewModel = viewModel)
                        2 -> AcquisitionScreen(viewModel = viewModel)
                        3 -> NewsScreen(viewModel = viewModel)
                    }
                }
            }
            AppScreen.Devices -> DevicesScreen(
                viewModel = viewModel, 
                modifier = Modifier.padding(innerPadding), 
                onNewDevice = { currentScreen = AppScreen.PhoneBuilder },
                onNavigateToBenchmark = { currentScreen = AppScreen.TechHub }
            )
            AppScreen.Market -> MarketScreen(
                state = state,
                onNavigateToBuilder = { currentScreen = AppScreen.PhoneBuilder }
            )
            AppScreen.TechHub -> {
                var selectedTab by remember { mutableIntStateOf(0) }
                Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                    TabRow(selectedTabIndex = selectedTab) {
                        Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Ar-Ge") })
                        Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Yazılım") })
                        Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("İşlemci") })
                        Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("Test Lab") })
                    }
                    when (selectedTab) {
                        0 -> ResearchScreen(viewModel = viewModel)
                        1 -> SoftwareScreen(viewModel = viewModel)
                        2 -> {
                            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
                                item {
                                    com.example.ui.ChipsetStudioView(
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
                        }
                        3 -> BenchmarkScreen(viewModel = viewModel)
                    }
                }
            }
            AppScreen.PhoneBuilder -> {
                val existingSeriesList = (state.activeModels.map { it.specs.seriesName } + state.manufacturedPhones.map { it.seriesName } + state.ownedLegacySeries.map { it.seriesName })
                    .filter { it.isNotBlank() }
                    .distinct()
                val previousSpecsList = (state.activeModels.map { it.specs } + state.manufacturedPhones).distinctBy { it.name }

                PhoneBuilderScreen(
                    unlockedTech = state.unlockedTech,
                    year = state.year,
                    existingSeries = existingSeriesList,
                    previousModels = previousSpecsList,
                    customOs = state.customOs,
                    customChipsets = state.customChipsets,
                    currentTrend = state.currentTrend,
                    companyName = state.companyName,
                    companyLogoStyle = state.companyLogoStyle,
                    companyBrandColorHex = state.companyBrandColorHex,
                    checkTrendMatch = { viewModel.checkTrendMatch(it, state.currentTrend) },
                    onBack = { currentScreen = AppScreen.CompanyHub },
                    factoryPeriodCapacity = state.currentFactoryTier.periodCapacity,
                    onManufacture = { specs ->
                        viewModel.manufacturePhone(specs)
                        currentScreen = AppScreen.CompanyHub
                    }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit
) {
    val navItems = listOf(
        Triple(AppScreen.CompanyHub, Icons.Default.Home, "Şirket"),
        Triple(AppScreen.Devices, Icons.Default.PhoneAndroid, "Cihazlar"),
        Triple(AppScreen.TechHub, Icons.Default.Science, "Teknoloji"),
        Triple(AppScreen.Market, Icons.AutoMirrored.Filled.TrendingUp, "Pazar")
    )

    val listState = rememberLazyListState()

    LaunchedEffect(currentScreen) {
        val selectedIndex = navItems.indexOfFirst { it.first == currentScreen }
        if (selectedIndex >= 0) {
            listState.animateScrollToItem((selectedIndex - 1).coerceAtLeast(0))
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shadowElevation = 6.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        LazyRow(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            itemsIndexed(navItems) { _, (screen, icon, title) ->
                val isSelected = currentScreen == screen
                val containerColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    label = "pill_bg_$title"
                )
                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    label = "pill_content_$title"
                )

                Surface(
                    onClick = { onNavigate(screen) },
                    shape = CircleShape,
                    color = containerColor,
                    contentColor = contentColor,
                    tonalElevation = if (isSelected) 4.dp else 0.dp,
                    shadowElevation = if (isSelected) 2.dp else 0.dp,
                    modifier = Modifier.height(42.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            modifier = Modifier.size(18.dp),
                            tint = contentColor
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = title,
                            fontSize = 12.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1,
                            softWrap = false,
                            color = contentColor
                        )
                    }
                }
            }
        }
    }
}
