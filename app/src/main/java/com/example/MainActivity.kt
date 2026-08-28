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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
    Dashboard, Devices, Market, Benchmark, Software, PhoneBuilder, Research, Employees, News
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .clickable { onOpenCompanyProfile() }
                        .padding(vertical = 2.dp)
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = state.companyName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate900,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Şirket Profili Düzenle",
                                tint = Slate400,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        val monthName = GameViewModel.getMonthName(state.month)
                        val periodLabel = if (state.period == 1) "1-15 $monthName" else "16-30 $monthName"
                        Text(
                            text = "$periodLabel / ${state.year}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilledTonalIconButton(
                        onClick = onOpenFinance,
                        modifier = Modifier.size(36.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("🏦", fontSize = 16.sp)
                    }

                    BadgedBox(
                        badge = {
                            if (state.unlockedAchievementIds.isNotEmpty()) {
                                Badge(containerColor = Color(0xFFFACC15)) {
                                    Text("${state.unlockedAchievementIds.size}", fontSize = 9.sp, color = Color(0xFF0F172A))
                                }
                            }
                        }
                    ) {
                        FilledTonalIconButton(
                            onClick = onOpenAchievements,
                            modifier = Modifier.size(36.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                Icons.Default.EmojiEvents,
                                contentDescription = "Başarımlar",
                                tint = Color(0xFFFACC15),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    FilledTonalIconButton(
                        onClick = onOpenSaveLoad,
                        modifier = Modifier.size(36.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            Icons.Default.Save,
                            contentDescription = "Kayıt & Yükleme",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Button(
                        onClick = onAdvanceTime,
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text("İlerle (2 Hafta) ⏩", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            val isNegative = state.budget < 0
            val budgetDisplay = if (state.budget >= 0) "$${"%,d".format(state.budget).replace(',', '.')}" else "-$${"%,d".format(kotlin.math.abs(state.budget)).replace(',', '.')}"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isNegative) Color(0xFFFEE2E2) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                    .clickable { onOpenFinance() }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Bakiye", fontSize = 10.sp, color = if (isNegative) Color(0xFFDC2626) else MaterialTheme.colorScheme.onSurfaceVariant)
                        if (state.totalDebt > 0) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("(Borç: $${"%,d".format(state.totalDebt)})", fontSize = 9.sp, color = Color(0xFFE11D48), fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(
                        budgetDisplay,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (isNegative) Color(0xFFDC2626) else MaterialTheme.colorScheme.primary
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Aylık Gelir", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("+$${"%,d".format(state.monthlyIncome).replace(',', '.')}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Green500)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Aylık Gider", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("-$${"%,d".format(state.totalMonthlyExpenses).replace(',', '.')}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun MainApp(viewModel: GameViewModel = viewModel()) {
    var currentScreen by remember { mutableStateOf(AppScreen.Dashboard) }
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

    Scaffold(
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
            AppScreen.Dashboard -> GameDashboard(
                viewModel = viewModel, 
                modifier = Modifier.padding(innerPadding),
                onNewDevice = { currentScreen = AppScreen.PhoneBuilder },
                onNavigateToMarket = { currentScreen = AppScreen.Market },
                onNavigateToSoftware = { currentScreen = AppScreen.Software },
                onEditCompanyProfile = { showEditCompanyDialog = true },
                onOpenFinance = { showFinancialHubDialog = true }
            )
            AppScreen.Devices -> DevicesScreen(
                viewModel = viewModel, 
                modifier = Modifier.padding(innerPadding), 
                onNewDevice = { currentScreen = AppScreen.PhoneBuilder },
                onNavigateToBenchmark = { currentScreen = AppScreen.Benchmark }
            )
            AppScreen.Market -> MarketScreen(
                state = state,
                onNavigateToBuilder = { currentScreen = AppScreen.PhoneBuilder }
            )
            AppScreen.Benchmark -> BenchmarkScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
            AppScreen.Software -> SoftwareScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
            AppScreen.Research -> ResearchScreen(
                viewModel = viewModel, 
                modifier = Modifier.padding(innerPadding)
            )
            AppScreen.Employees -> EmployeesScreen(
                viewModel = viewModel, 
                modifier = Modifier.padding(innerPadding)
            )
            AppScreen.News -> NewsScreen(
                viewModel = viewModel, 
                modifier = Modifier.padding(innerPadding)
            )
            AppScreen.PhoneBuilder -> {
                val existingSeriesList = (state.activeModels.map { it.specs.seriesName } + state.manufacturedPhones.map { it.seriesName })
                    .filter { it.isNotBlank() }
                    .distinct()

                PhoneBuilderScreen(
                    unlockedTech = state.unlockedTech,
                    year = state.year,
                    existingSeries = existingSeriesList,
                    customOs = state.customOs,
                    customChipsets = state.customChipsets,
                    currentTrend = state.currentTrend,
                    companyName = state.companyName,
                    companyLogoStyle = state.companyLogoStyle,
                    companyBrandColorHex = state.companyBrandColorHex,
                    checkTrendMatch = { viewModel.checkTrendMatch(it, state.currentTrend) },
                    onBack = { currentScreen = AppScreen.Dashboard },
                    factoryPeriodCapacity = state.currentFactoryTier.periodCapacity,
                    onManufacture = { specs ->
                        viewModel.manufacturePhone(specs)
                        currentScreen = AppScreen.Dashboard
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
        Triple(AppScreen.Dashboard, Icons.Default.Home, "Ana Sayfa"),
        Triple(AppScreen.Devices, Icons.Default.PhoneAndroid, "Cihazlar"),
        Triple(AppScreen.Market, Icons.AutoMirrored.Filled.TrendingUp, "Pazar"),
        Triple(AppScreen.Benchmark, Icons.Default.Speed, "Test Lab"),
        Triple(AppScreen.Software, Icons.Default.Terminal, "Yazılım"),
        Triple(AppScreen.Research, Icons.Default.Science, "Ar-Ge"),
        Triple(AppScreen.Employees, Icons.Default.Group, "Personel"),
        Triple(AppScreen.News, Icons.Default.Campaign, "Haberler")
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
