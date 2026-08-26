/**
 * Uygulamanın giriş noktası ve ana navigasyon iskeleti.
 *
 * [MainActivity] tek bir Compose ekranı ([MainApp]) başlatır; sekmeler arası geçiş
 * [AppScreen] enum'u ile yönetilir (Dashboard, Devices, Market, Benchmark, Software,
 * PhoneBuilder, Research, Employees, News). Dashboard, cihaz listesi ve üst bar gibi
 * paylaşılan bileşenler burada; Market/News/PhoneBuilder/Research/Software ekranlarının
 * kendi mantığı ise ayrı dosyalarda (örn. MarketScreen.kt) tanımlıdır.
 */
package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.filled.Save
import com.example.ui.BenchmarkScreen
import com.example.ui.SaveLoadDialog
import com.example.ui.theme.Green500
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.viewmodel.ActiveModel
import com.example.viewmodel.CampaignType
import com.example.viewmodel.EmployeeType
import com.example.viewmodel.OFFICE_TIERS
import com.example.viewmodel.FACTORY_TIERS
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
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
    onOpenSaveLoad: () -> Unit = {}
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
                        Text(
                            text = "${state.month}. Ay / ${state.year}",
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
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text("İleri (1 Ay) ⏩", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Bakiye", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("$${"%,d".format(state.budget).replace(',', '.')}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
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
    val state by viewModel.state.collectAsState()
    val savedGames by viewModel.savedGamesState.collectAsState()

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
                    onOpenSaveLoad = { showSaveLoadDialog = true }
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
                onEditCompanyProfile = { showEditCompanyDialog = true }
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
                    currentTrend = state.currentTrend,
                    companyName = state.companyName,
                    companyLogoStyle = state.companyLogoStyle,
                    companyBrandColorHex = state.companyBrandColorHex,
                    checkTrendMatch = { viewModel.checkTrendMatch(it, state.currentTrend) },
                    onBack = { currentScreen = AppScreen.Dashboard },
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
fun DevicesScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel,
    onNewDevice: () -> Unit,
    onNavigateToBenchmark: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    var selectedModelForRestock by remember { mutableStateOf<ActiveModel?>(null) }
    var selectedModelForMarketing by remember { mutableStateOf<ActiveModel?>(null) }
    var selectedModelForRecycle by remember { mutableStateOf<ActiveModel?>(null) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Prominent Call to Action Button
        Button(
            onClick = onNewDevice,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("YENİ MODEL TASARLA & ÜRET", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        
        if (state.activeModels.isEmpty() && state.manufacturedPhones.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(Icons.Default.PhoneAndroid, contentDescription = null, modifier = Modifier.size(64.dp), tint = Slate400)
                    Text("Henüz hiç telefon üretmediniz.", color = Slate600, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                    Button(
                        onClick = onNewDevice,
                        modifier = Modifier.height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("İlk Modelini Tasarla", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.activeModels.reversed()) { model ->
                    val phone = model.specs
                    val progressFraction = (model.totalSold.toFloat() / model.totalStock.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
                    val isSelling = !model.isCompleted

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .background(
                                                Color(phone.colorHex),
                                                RoundedCornerShape(12.dp)
                                            )
                                            .border(
                                                1.dp,
                                                if (phone.colorHex == 0xFFF1F5F9L) Color(0xFF94A3B8) else Color.White.copy(alpha = 0.2f),
                                                RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.PhoneAndroid,
                                            contentDescription = null,
                                            tint = if (phone.colorHex == 0xFFF1F5F9L) Color.Black else Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Column {
                                        Text(phone.name, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Slate900)
                                        val hardwareSummary = if (phone.sdCardSupport.contains("Yok", ignoreCase = true)) {
                                            "${phone.ramCapacity} (${phone.ramType}) • ${phone.storage}"
                                        } else {
                                            "${phone.ramCapacity} (${phone.ramType}) • ${phone.storage} + SD"
                                        }
                                        Text("${phone.colorName} • ${phone.logoStyle} • $hardwareSummary", fontSize = 11.sp, color = Slate600)
                                    }
                                }

                                Surface(
                                    color = when {
                                        model.isRecalled -> MaterialTheme.colorScheme.error
                                        isSelling -> MaterialTheme.colorScheme.primary
                                        else -> Slate400
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = when {
                                            model.isRecalled -> "⚠️ GERİ ÇAĞRILDI"
                                            isSelling -> "${model.monthsOnMarket}/${model.maxMonthsOnMarket} Ay"
                                            else -> "Satış Bitti"
                                        },
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Popularity & Demand Banner
                            val demandLabel = when {
                                model.reviewScore >= 75 -> "🔥 Yoğun Pazar Talebi (24 Ay Satış İzni)"
                                model.reviewScore >= 60 -> "⭐ Yüksek Talep (24 Ay Satış İzni)"
                                else -> "📉 Standart Talep (12 Ay Satış Süresi)"
                            }

                            val demandBgColor = when {
                                model.reviewScore >= 75 -> Color(0xFFFFF3E0)
                                model.reviewScore >= 60 -> Color(0xFFE8F5E9)
                                else -> Slate200
                            }

                            val demandTextColor = when {
                                model.reviewScore >= 75 -> Color(0xFFE65100)
                                model.reviewScore >= 60 -> Color(0xFF2E7D32)
                                else -> Slate800
                            }

                            Surface(
                                color = demandBgColor,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = demandLabel,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = demandTextColor
                                    )
                                    Text(
                                        text = "Puan: ${model.reviewScore}/100",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = demandTextColor
                                    )
                                }
                            }

                            // Trend Match Banner (if phone caught the market trend)
                            if (model.matchesTrend) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = Color(0xFF1B5E20),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text("🔥", fontSize = 14.sp)
                                            Text(
                                                text = "Pazar Trendi Yakalandı!",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFA5D6A7)
                                            )
                                        }
                                        val trendBonusPct = ((state.currentTrend.bonusMultiplier - 1.0f) * 100).toInt()
                                        Text(
                                            text = "+%$trendBonusPct Satış Bonusu Devrede",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            // Active Marketing Campaign Banner
                            model.activeCampaign?.let { camp ->
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.tertiaryContainer,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Campaign,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                                            )
                                            Text(
                                                text = "${camp.type.title} (${camp.remainingMonths} Ay Kaldı)",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onTertiaryContainer
                                            )
                                        }
                                        Text(
                                            text = "+%${camp.type.boostPercent} Satış",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Green500
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(12.dp))

                            // Sales Progress Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${model.maxMonthsOnMarket} Aylık Satış İlerlemesi",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate600
                                )
                                Text(
                                    text = "${"%,d".format(model.totalSold)} / ${"%,d".format(model.totalStock)} (%${(progressFraction * 100).toInt()})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            LinearProgressIndicator(
                                progress = { progressFraction },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = Slate200,
                                strokeCap = StrokeCap.Round
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Fiyat", fontSize = 10.sp, color = Slate500)
                                    Text("$${phone.price}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate900)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Kalan Stok", fontSize = 10.sp, color = Slate500)
                                    Text("${"%,d".format(model.remainingStock)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate900)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Elde Edilen Ciro", fontSize = 10.sp, color = Slate500)
                                    Text("$${"%,d".format(model.totalRevenue)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Green500)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Action Buttons: Benchmark, Marketing & Restock
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onNavigateToBenchmark,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFFFF5722)
                                    ),
                                    border = BorderStroke(1.dp, Color(0xFFFFCCBC))
                                ) {
                                    Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("TEST ET", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { selectedModelForMarketing = model },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.secondary
                                    )
                                ) {
                                    Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("PAZARLAMA", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { selectedModelForRestock = model },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("STOK", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (model.remainingStock > 0) {
                                Spacer(modifier = Modifier.height(6.dp))
                                val unitCost = viewModel.calculateProductionCost(model.specs)
                                val refundEstimate = (unitCost * 0.50f * model.remainingStock).toLong()
                                OutlinedButton(
                                    onClick = { selectedModelForRecycle = model },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(36.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFFD97706)
                                    ),
                                    border = BorderStroke(1.dp, Color(0xFFFDE68A))
                                ) {
                                    Text("♻️ KALAN STOKU GERİ DÖNÜŞTÜR (+$${"%,d".format(refundEstimate)})", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    selectedModelForRestock?.let { targetModel ->
        val unitCost = viewModel.calculateProductionCost(targetModel.specs)
        RestockDialog(
            model = targetModel,
            unitCost = unitCost,
            currentBudget = state.budget,
            onDismiss = { selectedModelForRestock = null },
            onConfirmRestock = { qty ->
                viewModel.remanufactureModel(targetModel.id, qty)
                selectedModelForRestock = null
            }
        )
    }

    selectedModelForMarketing?.let { targetModel ->
        MarketingDialog(
            model = targetModel,
            currentBudget = state.budget,
            onDismiss = { selectedModelForMarketing = null },
            onConfirmCampaign = { type ->
                viewModel.launchCampaign(targetModel.id, type)
                selectedModelForMarketing = null
            }
        )
    }

    selectedModelForRecycle?.let { targetModel ->
        val unitCost = viewModel.calculateProductionCost(targetModel.specs)
        RecycleStockDialog(
            model = targetModel,
            unitCost = unitCost,
            onDismiss = { selectedModelForRecycle = null },
            onConfirmRecycle = {
                viewModel.recycleRemainingStock(targetModel.id)
                selectedModelForRecycle = null
            }
        )
    }
}

@Composable
fun RecycleStockDialog(
    model: ActiveModel,
    unitCost: Int,
    onDismiss: () -> Unit,
    onConfirmRecycle: () -> Unit
) {
    val recyclePerUnit = (unitCost * 0.50f).toLong()
    val totalRefund = recyclePerUnit * model.remainingStock.toLong()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("♻️ Kalan Stok Geri Dönüşümü", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "${model.specs.name} modelinin elde kalan tüm stokları parçalarına ayrılarak geri dönüştürülecektir. Birim üretim maliyetinin yarı fiyatı (%50) şirket kasasına nakit olarak aktarılır.",
                    fontSize = 13.sp,
                    color = Slate600
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Elde Kalan Stok:", fontSize = 12.sp, color = Slate600)
                            Text("${"%,d".format(model.remainingStock)} adet", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate900)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Birim Üretim Maliyeti:", fontSize = 12.sp, color = Slate600)
                            Text("$${unitCost}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate900)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Geri İade Değeri (Adet Başı):", fontSize = 12.sp, color = Slate600)
                            Text("$${recyclePerUnit} (%50)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Slate200)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Kasaya Aktarılacak Tutar:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate900)
                            Text("+$${"%,d".format(totalRefund)}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Green500)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmRecycle,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))
            ) {
                Text("♻️ Geri Dönüştür (+$${"%,d".format(totalRefund)})", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Vazgeç", color = Slate600)
            }
        }
    )
}

@Composable
fun MarketingDialog(
    model: ActiveModel,
    currentBudget: Long,
    onDismiss: () -> Unit,
    onConfirmCampaign: (CampaignType) -> Unit
) {
    var selectedType by remember { mutableStateOf(CampaignType.SOCIAL_MEDIA) }
    val canAfford = currentBudget >= selectedType.cost

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("${model.specs.name} - Pazarlama Kampanyası", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Cihazınızın pazardaki satış hızını ve talebini yükseltmek için bir pazarlama stratejisi seçin.",
                    fontSize = 12.sp,
                    color = Slate600
                )

                CampaignType.entries.forEach { campaign ->
                    val isSelected = selectedType == campaign
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedType = campaign },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = campaign.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else Slate900
                                )
                                Text(
                                    text = "$${"%,d".format(campaign.cost)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${campaign.durationMonths} Ay • +%${campaign.boostPercent} Satış Artışı",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Green500
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = campaign.description,
                                fontSize = 11.sp,
                                color = Slate600
                            )
                        }
                    }
                }

                if (!canAfford) {
                    Text(
                        "Yetersiz Bütçe! (Mevcut: $${"%,d".format(currentBudget)})",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmCampaign(selectedType) },
                enabled = canAfford,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Kampanyayı Başlat", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal", color = Slate600)
            }
        }
    )
}

@Composable
fun RestockDialog(
    model: ActiveModel,
    unitCost: Int,
    currentBudget: Long,
    onDismiss: () -> Unit,
    onConfirmRestock: (Int) -> Unit
) {
    var selectedQty by remember { mutableIntStateOf(10000) }
    val totalCost = unitCost.toLong() * selectedQty
    val canAfford = currentBudget >= totalCost

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("${model.specs.name} - Tekrar Üretim", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Bu model için ilave stok üreterek satış kanalını besleyebilirsiniz. Popüler ürünler 24 aya kadar pazarda satılmaya devam eder.",
                    fontSize = 13.sp,
                    color = Slate600
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Birim Üretim Maliyeti:", fontSize = 12.sp, color = Slate600)
                    Text("$${unitCost} / adet", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate900)
                }

                Text("Üretilecek Adet:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate900)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(5000, 10000, 25000, 50000).forEach { qty ->
                        FilterChip(
                            selected = selectedQty == qty,
                            onClick = { selectedQty = qty },
                            label = { Text("${qty / 1000}k", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                HorizontalDivider(color = Slate200)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Toplam Maliyet:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate900)
                    Text(
                        "$${"%,d".format(totalCost)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (canAfford) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }

                if (!canAfford) {
                    Text(
                        "Yetersiz Bütçe! (Mevcut: $${"%,d".format(currentBudget)})",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmRestock(selectedQty) },
                enabled = canAfford,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Üret ve Stokla", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal", color = Slate600)
            }
        }
    )
}

@Composable
fun GameDashboard(
    modifier: Modifier = Modifier, 
    viewModel: GameViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNewDevice: () -> Unit = {},
    onNavigateToMarket: () -> Unit = {},
    onNavigateToSoftware: () -> Unit = {},
    onEditCompanyProfile: () -> Unit = {}
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
            modelCount = state.modelCount,
            techLevel = state.techLevel
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
                .background(androidx.compose.ui.graphics.Color.White, RoundedCornerShape(20.dp))
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
    modelCount: Int,
    techLevel: String
) {
    // Format budget
    val formattedBudget = "$%,d".format(budget).replace(',', '.')

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
                modifier = Modifier.weight(1f)
            ) {
                Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(Slate200, CircleShape)) {
                    val widthFraction = (budget.toFloat() / 20000000f).coerceIn(0f, 1f)
                    Box(modifier = Modifier.fillMaxWidth(widthFraction).height(4.dp).background(Green500, CircleShape))
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
                Text(reputationText, fontSize = 9.sp, color = Slate400, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, fontWeight = FontWeight.Medium)
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
                Text(modelText, fontSize = 9.sp, color = Slate400)
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
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun ReportItem(report: com.example.viewmodel.MarketReport) {
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
                            Text("$${"%,d".format(Math.abs(report.profit))}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (report.profit >= 0) androidx.compose.ui.graphics.Color(0xFF4CAF50) else MaterialTheme.colorScheme.error)
                        }
                    }
                }
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

@Composable
fun EmployeesScreen(modifier: Modifier = Modifier, viewModel: GameViewModel) {
    val state by viewModel.state.collectAsState()
    val totalStaff = state.totalEmployees

    if (state.noticeMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearNoticeMessage() },
            title = { Text("Kapasite / Bütçe Uyarısı", fontWeight = FontWeight.Bold) },
            text = { Text(state.noticeMessage ?: "", fontSize = 14.sp) },
            confirmButton = {
                Button(onClick = { viewModel.clearNoticeMessage() }) {
                    Text("Anladım")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Compact Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    Icons.Default.Group,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    "Personel & Fabrika Yönetimi",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
            }

            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "$totalStaff / ${state.maxEmployees} Personel",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        // Compact, Highly Legible Summary Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                // Row 1: Sabit Kesinti & Dağılım
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            "Aylık Sabit Kesinti:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate700
                        )
                        Text(
                            "-$${"%,d".format(state.totalMonthlyExpenses)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Text(
                        text = "Maaş: $${"%,d".format(state.totalSalaries)} • Kira: $${"%,d".format(state.officeExpense)} • Bakım: $${"%,d".format(state.factoryMaintenance)}",
                        fontSize = 9.5.sp,
                        color = Slate600,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.12f))

                // Row 2: 3 Balanced Metric Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("🏷️ ", fontSize = 10.sp)
                            Text(
                                "%${"%.1f".format(state.unitCostDiscountPercent)} Tasarruf",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Green500
                            )
                        }
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("⭐ ", fontSize = 10.sp)
                            Text(
                                "+${state.qaScoreBonus} QA Puanı",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("⚡ ", fontSize = 10.sp)
                            Text(
                                "${viewModel.calculateResearchDuration(state.engineers)} Ay Ar-Ge",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                        }
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Office Tier Upgrade Card
            item {
                val currentOffice = state.currentOfficeTier
                val nextOffice = OFFICE_TIERS.firstOrNull { it.level == state.officeLevel + 1 }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Business, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                                Column {
                                    Text("ŞİRKET GENEL MERKEZİ", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text(currentOffice.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Slate900)
                                }
                            }

                            Surface(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Kapasite: ${state.totalEmployees} / ${currentOffice.maxEmployees}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Aylık Ofis Kirası: $${"%,d".format(currentOffice.monthlyRent)} / Ay. Daha fazla personel istihdam etmek için ofis alanını büyütün.",
                            fontSize = 11.sp,
                            color = Slate600
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (nextOffice != null) {
                            val canAfford = state.budget >= nextOffice.upgradeCost
                            Button(
                                onClick = { viewModel.upgradeOffice() },
                                modifier = Modifier.fillMaxWidth().height(34.dp),
                                enabled = canAfford,
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.ArrowUpward, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${nextOffice.name} Seviyesine Yükselt ($${"%,d".format(nextOffice.upgradeCost)})",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Surface(
                                color = Green500.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "🏆 MAKSİMUM OFİS SEVİYESİ (500 Kapasite)",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Green500,
                                    modifier = Modifier.padding(6.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Factory Tier Upgrade Card
            item {
                val currentFactory = state.currentFactoryTier
                val nextFactory = FACTORY_TIERS.firstOrNull { it.level == state.factoryLevel + 1 }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.PrecisionManufacturing, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(22.dp))
                                Column {
                                    Text("ÜRETİM FABRİKASI TESİSİ", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                                    Text(currentFactory.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Slate900)
                                }
                            }

                            Surface(
                                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "İşçi: ${state.assemblyWorkers} / ${currentFactory.maxWorkers}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Sabit Bakım: $${"%,d".format(currentFactory.monthlyMaintenance)} / Ay. Tesis birim maliyette %${currentFactory.discountPercent.toInt()} tasarruf sağlar.",
                            fontSize = 11.sp,
                            color = Slate600
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (nextFactory != null) {
                            val canAfford = state.budget >= nextFactory.upgradeCost
                            Button(
                                onClick = { viewModel.upgradeFactory() },
                                modifier = Modifier.fillMaxWidth().height(34.dp),
                                enabled = canAfford,
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                            ) {
                                Icon(Icons.Default.ArrowUpward, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${nextFactory.name} Yatırımı Yap ($${"%,d".format(nextFactory.upgradeCost)})",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Surface(
                                color = Green500.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "🏆 EN GELİŞMİŞ ROBOTİK FABRİKA SEVİYESİ",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Green500,
                                    modifier = Modifier.padding(6.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Employees Cards
            item {
                EmployeeCategoryCard(
                    title = "Donanım & Yazılım Mühendisleri",
                    icon = Icons.Default.Science,
                    roleDesc = "Ar-Ge araştırmalarının süresini kısaltır (8+ mühendis ile 1 ayda biter), eskime cezasını düşürür.",
                    salaryText = "$8,000 / Ay",
                    currentCount = state.engineers,
                    impactText = "Araştırma Süresi: ${viewModel.calculateResearchDuration(state.engineers)} Ay",
                    onHire = { viewModel.hireEmployee(EmployeeType.ENGINEER, it) },
                    onFire = { viewModel.fireEmployee(EmployeeType.ENGINEER, it) }
                )
            }

            item {
                EmployeeCategoryCard(
                    title = "Kalite Kontrol (QA) Uzmanları",
                    icon = Icons.Default.CheckCircle,
                    roleDesc = "Üretilen telefonlardaki hataları ayıklar ve inceleme puanına doğrudan bonus ekler.",
                    salaryText = "$5,000 / Ay",
                    currentCount = state.qaInspectors,
                    impactText = "Puan Bonusu: +${state.qaScoreBonus} Puan",
                    onHire = { viewModel.hireEmployee(EmployeeType.QA_INSPECTOR, it) },
                    onFire = { viewModel.fireEmployee(EmployeeType.QA_INSPECTOR, it) }
                )
            }

            item {
                EmployeeCategoryCard(
                    title = "Montaj & Üretim İşçileri",
                    icon = Icons.Default.Build,
                    roleDesc = "Fabrikada montaj verimliliğini yükseltir (Maksımum ${state.currentFactoryTier.maxWorkers} işçi).",
                    salaryText = "$3,000 / Ay",
                    currentCount = state.assemblyWorkers,
                    impactText = "İşçi İndirimi: %${"%.1f".format(state.workerDiscountPercent)}",
                    onHire = { viewModel.hireEmployee(EmployeeType.ASSEMBLY_WORKER, it) },
                    onFire = { viewModel.fireEmployee(EmployeeType.ASSEMBLY_WORKER, it) }
                )
            }
        }
    }
}

@Composable
fun EmployeeCategoryCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    roleDesc: String,
    salaryText: String,
    currentCount: Int,
    impactText: String,
    onHire: (Int) -> Unit,
    onFire: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(18.dp))
                    }
                    Column {
                        Text(title, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = Slate900)
                        Text(salaryText, fontSize = 10.5.sp, color = Slate600)
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "$currentCount Kişi",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(roleDesc, fontSize = 11.sp, color = Slate600, lineHeight = 15.sp)

            Spacer(modifier = Modifier.height(4.dp))
            Text(impactText, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = { onFire(1) },
                        enabled = currentCount > 0,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Çıkar (-1)", fontSize = 10.5.sp)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = { onHire(1) },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("İşe Al (+1)", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onHire(5) },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("+5", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

