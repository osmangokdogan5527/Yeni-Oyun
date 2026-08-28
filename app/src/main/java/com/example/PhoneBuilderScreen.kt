/**
 * Telefon Tasarım (Phone Builder) ekranı.
 *
 * Kullanıcının yeni bir telefon modeli tasarlamasını sağlar: stil, malzeme, işlemci, RAM,
 * depolama, ekran, kamera, bağlantı, ses ve batarya gibi bileşenler yıl ve araştırılmış
 * teknolojiye ([unlockedTech]) göre filtrelenir.
 */
package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import com.example.ui.theme.*
import com.example.viewmodel.ModelTier
import com.example.viewmodel.PhoneSpecs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneBuilderScreen(
    unlockedTech: List<String>,
    year: Int,
    existingSeries: List<String> = emptyList(),
    customOs: com.example.viewmodel.CustomOsState? = null,
    customChipsets: List<com.example.viewmodel.CustomChipset> = emptyList(),
    currentTrend: com.example.viewmodel.MarketTrend? = null,
    companyName: String = "Apex Mobile",
    companyLogoStyle: String = "Minimal Elmas",
    companyBrandColorHex: Long = 0xFF2563EB,
    checkTrendMatch: ((PhoneSpecs) -> Boolean)? = null,
    onBack: () -> Unit,
    onManufacture: (PhoneSpecs) -> Unit,
    factoryPeriodCapacity: Int = 0
) {
    var lockedNoticeTech by remember { mutableStateOf<String?>(null) }
    var selectedTabSection by remember { mutableIntStateOf(0) } // 0: Tasarım, 1: Yonga&RAM, 2: Ekran&Kamera, 3: Yazılım&OS, 4: Seri&Üretim
    var selectedOsChoice by remember { mutableIntStateOf(if (customOs != null && customOs.type != com.example.viewmodel.OsType.STOCK_ANDROID) 1 else 0) } // 0: Android AOSP, 1: Şirket OS, 2: Ticari GlobalOS

    // --- DONANIM BİLEŞENLERİ & ÖZEL ÇİPLER ---
    val customChipOptions = customChipsets.filter { !it.isArchived }.map { chip ->
        ComponentOption(
            name = "👑 ${chip.name} (${chip.tier.title.substringBefore(" (")})",
            cost = chip.unitCost,
            desc = "Öz Tasarım • ${chip.processNode.nodeName} • ${chip.coreCount} Çekirdek • ${chip.performanceScore} Puan",
            availableFrom = chip.createdYear
        )
    }

    val allProcessors = customChipOptions + BASE_PROCESSORS

    // Filtreleme
    val currentProcessors = allProcessors.filter { it.availableFrom <= year }
    val currentRamCapacities = ALL_RAM_CAPACITIES.filter { it.availableFrom <= year }
    val currentRamTypes = ALL_RAM_TYPES.filter { it.availableFrom <= year }
    val currentStorages = ALL_STORAGES.filter { it.availableFrom <= year }
    val currentSdCards = ALL_SD_CARDS.filter { it.availableFrom <= year }
    val currentDisplays = ALL_DISPLAYS.filter { it.availableFrom <= year }
    val currentGlasses = ALL_GLASSES.filter { it.availableFrom <= year }
    val currentCameras = ALL_CAMERAS.filter { it.availableFrom <= year }
    val currentCellularNetworks = ALL_CELLULAR_NETWORKS.filter { it.availableFrom <= year }
    val currentChargingPorts = ALL_CHARGING_PORTS.filter { it.availableFrom <= year }
    val currentWirelessConnectivity = ALL_WIRELESS_CONNECTIVITY.filter { it.availableFrom <= year }
    val currentAudios = ALL_AUDIO.filter { it.availableFrom <= year }
    val currentBatteryCapacities = ALL_BATTERY_CAPACITIES.filter { it.availableFrom <= year }
    val currentBatteryTypes = ALL_BATTERY_TYPES.filter { it.availableFrom <= year }

    // --- SERİ, SEGMENT & MODEL YÖNETİMİ ---
    val isFlagshipUnlocked = unlockedTech.contains("Amiral Gemisi Ailesi Segmentasyonu") || year >= 2014
    var seriesMode by remember { mutableIntStateOf(if (existingSeries.isNotEmpty()) 0 else 1) } // 0: Mevcut Seri, 1: Yeni Seri
    var selectedExistingSeries by remember { mutableStateOf(existingSeries.firstOrNull() ?: "Aura") }
    var newSeriesName by remember { mutableStateOf("Nova") }
    var generationNumber by remember { mutableIntStateOf(1) }
    var selectedTier by remember { mutableStateOf(ModelTier.STANDARD) }

    val activeSeriesName = if (seriesMode == 0 && existingSeries.isNotEmpty()) selectedExistingSeries else newSeriesName
    
    // Otomatik Model İsim Önerisi
    val romanGen = when (generationNumber) {
        1 -> "I"
        2 -> "II"
        3 -> "III"
        4 -> "IV"
        5 -> "V"
        6 -> "VI"
        else -> "$generationNumber"
    }
    val defaultModelName = when (selectedTier) {
        ModelTier.STANDARD -> "$activeSeriesName $romanGen"
        ModelTier.PRO -> "$activeSeriesName $romanGen Pro"
        ModelTier.ULTRA -> "$activeSeriesName $romanGen Ultra"
        ModelTier.LITE -> "$activeSeriesName $romanGen Lite"
    }

    var phoneName by remember { mutableStateOf(defaultModelName) }

    // Seri veya Tier değiştikçe isim güncellemesi yapabilir
    LaunchedEffect(activeSeriesName, generationNumber, selectedTier) {
        phoneName = when (selectedTier) {
            ModelTier.STANDARD -> "$activeSeriesName $romanGen"
            ModelTier.PRO -> "$activeSeriesName $romanGen Pro"
            ModelTier.ULTRA -> "$activeSeriesName $romanGen Ultra"
            ModelTier.LITE -> "$activeSeriesName $romanGen Lite"
        }
    }

    // --- FORM STATE ---
    var selectedColors by remember { mutableStateOf(listOf(ALL_COLORS.first())) }
    var previewColor by remember { mutableStateOf(ALL_COLORS.first()) }
    var selectedBackFinish by remember { mutableStateOf(ALL_BACK_FINISHES.first().name) }
    var selectedCameraBump by remember { mutableStateOf(ALL_CAMERA_BUMPS.first().name) }
    var selectedFrameStyle by remember { mutableStateOf(ALL_FRAME_STYLES.first().name) }
    var selectedNotchStyle by remember { mutableStateOf(ALL_NOTCH_STYLES.first().name) }

    var selectedStyle by remember { mutableStateOf(ALL_STYLES.first().name) }
    var selectedMaterial by remember { mutableStateOf(ALL_MATERIALS.first { it.requiredTech == null || unlockedTech.contains(it.requiredTech) }.name) }
    var selectedProcessor by remember { mutableStateOf(currentProcessors.first { it.requiredTech == null || unlockedTech.contains(it.requiredTech) }.name) }
    var selectedRamCapacity by remember { mutableStateOf(currentRamCapacities.first { it.requiredTech == null || unlockedTech.contains(it.requiredTech) }.name) }
    var selectedRamType by remember { mutableStateOf(currentRamTypes.first { it.requiredTech == null || unlockedTech.contains(it.requiredTech) }.name) }
    var selectedStorage by remember { mutableStateOf(currentStorages.first { it.requiredTech == null || unlockedTech.contains(it.requiredTech) }.name) }
    var selectedSdCard by remember { mutableStateOf(currentSdCards.first { it.requiredTech == null || unlockedTech.contains(it.requiredTech) }.name) }
    var selectedDisplay by remember { mutableStateOf(currentDisplays.first { it.requiredTech == null || unlockedTech.contains(it.requiredTech) }.name) }
    var screenSizeInch by remember { mutableFloatStateOf(6.1f) }
    var thicknessMm by remember { mutableFloatStateOf(8.0f) }
    var selectedGlass by remember { mutableStateOf(currentGlasses.first { it.requiredTech == null || unlockedTech.contains(it.requiredTech) }.name) }
    var selectedCamera by remember { mutableStateOf(currentCameras.first { it.requiredTech == null || unlockedTech.contains(it.requiredTech) }.name) }
    var selectedCellularNetwork by remember { mutableStateOf(currentCellularNetworks.first { it.requiredTech == null || unlockedTech.contains(it.requiredTech) }.name) }
    var selectedChargingPort by remember { mutableStateOf(currentChargingPorts.first { it.requiredTech == null || unlockedTech.contains(it.requiredTech) }.name) }
    var selectedWirelessConnectivity by remember { mutableStateOf(currentWirelessConnectivity.first { it.requiredTech == null || unlockedTech.contains(it.requiredTech) }.name) }
    var selectedAudio by remember { mutableStateOf(currentAudios.first { it.requiredTech == null || unlockedTech.contains(it.requiredTech) }.name) }
    var selectedBatteryCapacity by remember { mutableStateOf(currentBatteryCapacities.first { it.requiredTech == null || unlockedTech.contains(it.requiredTech) }.name) }
    var selectedBatteryType by remember { mutableStateOf(currentBatteryTypes.first { it.requiredTech == null || unlockedTech.contains(it.requiredTech) }.name) }
    
    var price by remember { mutableFloatStateOf(699f) }
    var quantity by remember { mutableFloatStateOf(100000f) }
    var qaBudget by remember { mutableFloatStateOf(100000f) }

    val extraColorCost = (selectedColors.size - 1).coerceAtLeast(0) * 3
    val osLicenseFee = when (selectedOsChoice) {
        2 -> 10 // Ticari lisanslı GlobalOS OEM lisans bedeli
        else -> 0
    }
    val rawUnitCost = 15 + // Temel montaj maliyeti
        (ALL_STYLES.find { it.name == selectedStyle }?.cost ?: 0) +
        (ALL_BACK_FINISHES.find { it.name == selectedBackFinish }?.cost ?: 0) +
        (ALL_CAMERA_BUMPS.find { it.name == selectedCameraBump }?.cost ?: 0) +
        (ALL_FRAME_STYLES.find { it.name == selectedFrameStyle }?.cost ?: 0) +
        (ALL_NOTCH_STYLES.find { it.name == selectedNotchStyle }?.cost ?: 0) +
        (ALL_MATERIALS.find { it.name == selectedMaterial }?.cost ?: 0) +
        (currentProcessors.find { it.name == selectedProcessor }?.cost ?: 0) +
        (currentRamCapacities.find { it.name == selectedRamCapacity }?.cost ?: 0) +
        (currentRamTypes.find { it.name == selectedRamType }?.cost ?: 0) +
        (currentStorages.find { it.name == selectedStorage }?.cost ?: 0) +
        (currentSdCards.find { it.name == selectedSdCard }?.cost ?: 0) +
        (currentDisplays.find { it.name == selectedDisplay }?.cost ?: 0) +
        (currentGlasses.find { it.name == selectedGlass }?.cost ?: 0) +
        (currentCameras.find { it.name == selectedCamera }?.cost ?: 0) +
        (currentCellularNetworks.find { it.name == selectedCellularNetwork }?.cost ?: 0) +
        (currentChargingPorts.find { it.name == selectedChargingPort }?.cost ?: 0) +
        (currentWirelessConnectivity.find { it.name == selectedWirelessConnectivity }?.cost ?: 0) +
        (currentAudios.find { it.name == selectedAudio }?.cost ?: 0) +
        (currentBatteryCapacities.find { it.name == selectedBatteryCapacity }?.cost ?: 0) +
        (currentBatteryTypes.find { it.name == selectedBatteryType }?.cost ?: 0) +
        extraColorCost +
        osLicenseFee

    val unitCost = rawUnitCost

    Scaffold(
        topBar = {
            val totalCost = (unitCost.toLong() * quantity.toLong()) + qaBudget.toLong()
            TopAppBar(
                title = { 
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Model Tasarım Stüdyosu", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = selectedTier.title,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text("Birim Maliyet: $$unitCost • Toplam: $${"%,d".format(totalCost).replace(',', '.')}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                        val sectionNames = listOf("Tasarım", "Yonga & RAM", "Ekran & Kamera", "OS Seçimi", "Seri & Üretim")
                        Text(
                            "Cihazlar  ›  Yeni Model  ›  ${sectionNames.getOrElse(selectedTabSection) { "Tasarım" }}",
                            fontSize = 10.sp,
                            color = Slate400,
                            maxLines = 1
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Button(
                    onClick = {
                        val currentOsType = when (selectedOsChoice) {
                            1 -> if (customOs != null && customOs.type != com.example.viewmodel.OsType.STOCK_ANDROID) customOs.type.title else "Özel Şirket Arayüzü"
                            2 -> "Ticari Lisanslı GlobalOS"
                            else -> "Saf Açık Kaynak"
                        }
                        val currentOsName = when (selectedOsChoice) {
                            1 -> if (customOs != null && customOs.type != com.example.viewmodel.OsType.STOCK_ANDROID) "${customOs.name} v${customOs.version}" else "NovaUI"
                            2 -> "GlobalOS Ticari OEM"
                            else -> "Android AOSP"
                        }
                        val currentOsFocus = when (selectedOsChoice) {
                            1 -> if (customOs != null && customOs.type != com.example.viewmodel.OsType.STOCK_ANDROID) customOs.focus.title else "Dengeli Standart"
                            2 -> "Küresel Servisler & Multimedya"
                            else -> "Temel Açık Kaynak"
                        }

                        onManufacture(
                            PhoneSpecs(
                                name = phoneName,
                                seriesName = activeSeriesName,
                                generation = generationNumber,
                                tier = selectedTier,
                                unitCost = unitCost,
                                style = selectedStyle,
                                material = selectedMaterial,
                                processor = selectedProcessor,
                                ramCapacity = selectedRamCapacity,
                                ramType = selectedRamType,
                                storage = selectedStorage,
                                sdCardSupport = selectedSdCard,
                                display = selectedDisplay,
                                screenSizeInch = screenSizeInch,
                                thicknessMm = thicknessMm,
                                glass = selectedGlass,
                                camera = selectedCamera,
                                connectivity = "$selectedCellularNetwork, $selectedChargingPort, $selectedWirelessConnectivity",
                                cellularNetwork = selectedCellularNetwork,
                                chargingPort = selectedChargingPort,
                                wirelessConnectivity = selectedWirelessConnectivity,
                                audio = selectedAudio,
                                batteryCapacity = selectedBatteryCapacity,
                                batteryType = selectedBatteryType,
                                price = price.toInt(),
                                quantity = quantity.toInt(),
                                qaBudget = qaBudget.toLong(),
                                osName = currentOsName,
                                osType = currentOsType,
                                osFocus = currentOsFocus,
                                osLicenseFee = osLicenseFee,
                                selectedColors = selectedColors.map { it.name },
                                colorHexes = selectedColors.map { it.hexValue },
                                colorName = previewColor.name,
                                colorHex = previewColor.hexValue,
                                frameStyle = selectedFrameStyle,
                                cameraBumpStyle = selectedCameraBump,
                                backFinish = selectedBackFinish,
                                notchStyle = selectedNotchStyle,
                                logoStyle = companyLogoStyle,
                                techScore = listOfNotNull(
                                    ALL_STYLES.find { it.name == selectedStyle },
                                    ALL_MATERIALS.find { it.name == selectedMaterial },
                                    currentProcessors.find { it.name == selectedProcessor },
                                    currentRamCapacities.find { it.name == selectedRamCapacity },
                                    currentRamTypes.find { it.name == selectedRamType },
                                    currentStorages.find { it.name == selectedStorage },
                                    currentSdCards.find { it.name == selectedSdCard },
                                    currentDisplays.find { it.name == selectedDisplay },
                                    currentGlasses.find { it.name == selectedGlass },
                                    currentCameras.find { it.name == selectedCamera },
                                    currentCellularNetworks.find { it.name == selectedCellularNetwork },
                                    currentChargingPorts.find { it.name == selectedChargingPort },
                                    currentWirelessConnectivity.find { it.name == selectedWirelessConnectivity },
                                    currentAudios.find { it.name == selectedAudio },
                                    currentBatteryCapacities.find { it.name == selectedBatteryCapacity },
                                    currentBatteryTypes.find { it.name == selectedBatteryType }
                                ).map { it.availableFrom }.let { years ->
                                    if (years.isNotEmpty()) years.sum() / years.size else 2010
                                }
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("ÜRETİME BAŞLA ($${"%,d".format((unitCost.toLong() * quantity.toLong()) + qaBudget.toLong()).replace(',', '.')})", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        val currentOsType = when (selectedOsChoice) {
            1 -> if (customOs != null && customOs.type != com.example.viewmodel.OsType.STOCK_ANDROID) customOs.type.title else "Özel Şirket Arayüzü"
            2 -> "Ticari Lisanslı GlobalOS"
            else -> "Saf Açık Kaynak"
        }
        val currentOsName = when (selectedOsChoice) {
            1 -> if (customOs != null && customOs.type != com.example.viewmodel.OsType.STOCK_ANDROID) "${customOs.name} v${customOs.version}" else "NovaUI"
            2 -> "GlobalOS Ticari OEM"
            else -> "Android AOSP"
        }
        val currentOsFocus = when (selectedOsChoice) {
            1 -> if (customOs != null && customOs.type != com.example.viewmodel.OsType.STOCK_ANDROID) customOs.focus.title else "Dengeli Standart"
            2 -> "Küresel Servisler & Multimedya"
            else -> "Temel Açık Kaynak"
        }

        val currentPreviewSpecs = PhoneSpecs(
            name = phoneName,
            seriesName = activeSeriesName,
            generation = generationNumber,
            tier = selectedTier,
            unitCost = unitCost,
            style = selectedStyle,
            material = selectedMaterial,
            processor = selectedProcessor,
            ramCapacity = selectedRamCapacity,
            ramType = selectedRamType,
            storage = selectedStorage,
            sdCardSupport = selectedSdCard,
            display = selectedDisplay,
            screenSizeInch = screenSizeInch,
            thicknessMm = thicknessMm,
            glass = selectedGlass,
            camera = selectedCamera,
            connectivity = "$selectedCellularNetwork, $selectedChargingPort, $selectedWirelessConnectivity",
            cellularNetwork = selectedCellularNetwork,
            chargingPort = selectedChargingPort,
            wirelessConnectivity = selectedWirelessConnectivity,
            audio = selectedAudio,
            batteryCapacity = selectedBatteryCapacity,
            batteryType = selectedBatteryType,
            price = price.toInt(),
            quantity = quantity.toInt(),
            qaBudget = qaBudget.toLong(),
            osName = currentOsName,
            osType = currentOsType,
            osFocus = currentOsFocus,
            osLicenseFee = osLicenseFee,
            selectedColors = selectedColors.map { it.name },
            colorHexes = selectedColors.map { it.hexValue },
            colorName = previewColor.name,
            colorHex = previewColor.hexValue,
            frameStyle = selectedFrameStyle,
            cameraBumpStyle = selectedCameraBump,
            backFinish = selectedBackFinish,
            notchStyle = selectedNotchStyle,
            logoStyle = companyLogoStyle
        )

        val isLiveTrendMatched = checkTrendMatch?.invoke(currentPreviewSpecs) ?: false

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Live Trend Match Banner
            if (currentTrend != null) {
                Surface(
                    color = if (isLiveTrendMatched) Color(0xFF1B5E20) else Color(0xFF311B92),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isLiveTrendMatched) "✨" else "🔥",
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            val trendBonusPct = ((currentTrend.bonusMultiplier - 1.0f) * 100).toInt()
                            Column {
                                Text(
                                    text = if (isLiveTrendMatched) "PAZAR TRENDİ YAKALANDI (+%$trendBonusPct SATIŞ)" else "PAZAR TRENDİ: ${currentTrend.title}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isLiveTrendMatched) Color(0xFFA5D6A7) else Color(0xFFFFD54F)
                                )
                                Text(
                                    text = if (isLiveTrendMatched) "Cihazınız ${currentTrend.category.title} talebini karşılıyor!" else "İpucu: ${currentTrend.category.tip}",
                                    fontSize = 10.sp,
                                    color = Color.White
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isLiveTrendMatched) Color(0xFF4CAF50) else Color(0xFFFF9800)
                        ) {
                            val trendBonusPct = ((currentTrend.bonusMultiplier - 1.0f) * 100).toInt()
                            Text(
                                text = if (isLiveTrendMatched) "UYUMLU" else "%$trendBonusPct BONUS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // --- ULTRA GERÇEKÇİ CANVASA SAHİP 3D/VEKTÖR ÖNİZLEME CONTAINER ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color(0xFF0B0F19), Color(0xFF1E293B))))
                    .padding(vertical = 12.dp, horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                PhoneVisualPreview(
                    style = selectedStyle,
                    material = selectedMaterial,
                    camera = selectedCamera,
                    display = selectedDisplay,
                    screenSizeInch = screenSizeInch,
                    thicknessMm = thicknessMm,
                    chargingPort = selectedChargingPort,
                    cellularNetwork = selectedCellularNetwork,
                    colorHex = previewColor.hexValue,
                    colorName = previewColor.name,
                    backFinish = selectedBackFinish,
                    cameraBumpStyle = selectedCameraBump,
                    frameStyle = selectedFrameStyle,
                    notchStyle = selectedNotchStyle,
                    logoStyle = companyLogoStyle,
                    tier = selectedTier,
                    seriesName = activeSeriesName,
                    phoneName = phoneName
                )
            }

            // --- 5 SEKME DÜZENİ: TÜM SEÇENEKLER DERLİ TOPLU VE MİNİMAL ---
            ScrollableTabRow(
                selectedTabIndex = selectedTabSection,
                containerColor = MaterialTheme.colorScheme.surface,
                edgePadding = 12.dp,
                divider = { HorizontalDivider(color = Slate200) }
            ) {
                val tabs = listOf(
                    Triple(0, "Tasarım", Icons.Default.Palette),
                    Triple(1, "Yonga & RAM", Icons.Default.Memory),
                    Triple(2, "Ekran & Kamera", Icons.Default.CameraAlt),
                    Triple(3, "OS Seçimi", Icons.Default.Terminal),
                    Triple(4, "Seri & Üretim", Icons.Default.LocalOffer)
                )

                tabs.forEach { (index, title, icon) ->
                    val isSelected = selectedTabSection == index
                    Tab(
                        selected = isSelected,
                        onClick = { selectedTabSection = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp,
                                maxLines = 1,
                                softWrap = false
                            )
                        },
                        icon = {
                            Icon(
                                icon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else Slate500
                            )
                        }
                    )
                }
            }

            // Kilitli Teknoloji Uyarısı (Eğer tıklandıysa)
            lockedNoticeTech?.let { reqTech ->
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Column {
                                Text("Bu Seçenek Ar-Ge Kilidine Sahip", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onErrorContainer)
                                Text("Kilidi açmak için Ar-Ge ekranından '$reqTech' teknolojisini araştırın.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f))
                            }
                        }
                        IconButton(onClick = { lockedNoticeTech = null }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Kapat", tint = MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ==================== SEKME 0: TASARIM & KASA ====================
                if (selectedTabSection == 0) {
                    PhoneBuilderDesignTab(
                        selectedColors = selectedColors,
                        previewColor = previewColor,
                        selectedBackFinish = selectedBackFinish,
                        selectedCameraBump = selectedCameraBump,
                        selectedFrameStyle = selectedFrameStyle,
                        selectedNotchStyle = selectedNotchStyle,
                        selectedStyle = selectedStyle,
                        selectedMaterial = selectedMaterial,
                        unlockedTech = unlockedTech,
                        onPreviewColorChange = { previewColor = it },
                        onColorsChange = { selectedColors = it },
                        onBackFinishChange = { selectedBackFinish = it },
                        onCameraBumpChange = { selectedCameraBump = it },
                        onFrameStyleChange = { selectedFrameStyle = it },
                        onNotchStyleChange = { selectedNotchStyle = it },
                        onStyleChange = { selectedStyle = it },
                        onMaterialChange = { selectedMaterial = it },
                        onLockedClick = { lockedNoticeTech = it }
                    )
                }

                // ==================== SEKME 1: YONGA & BELLEK ====================
                if (selectedTabSection == 1) {
                    PhoneBuilderHardwareTab(
                        currentProcessors = currentProcessors,
                        currentRamCapacities = currentRamCapacities,
                        currentRamTypes = currentRamTypes,
                        currentStorages = currentStorages,
                        currentSdCards = currentSdCards,
                        selectedProcessor = selectedProcessor,
                        selectedRamCapacity = selectedRamCapacity,
                        selectedRamType = selectedRamType,
                        selectedStorage = selectedStorage,
                        selectedSdCard = selectedSdCard,
                        unlockedTech = unlockedTech,
                        onProcessorChange = { selectedProcessor = it },
                        onRamCapacityChange = { selectedRamCapacity = it },
                        onRamTypeChange = { selectedRamType = it },
                        onStorageChange = { selectedStorage = it },
                        onSdCardChange = { selectedSdCard = it },
                        onLockedClick = { lockedNoticeTech = it }
                    )
                }

                // ==================== SEKME 2: EKRAN & KAMERA & DONANIM ====================
                if (selectedTabSection == 2) {
                    PhoneBuilderScreenCameraTab(
                        currentDisplays = currentDisplays,
                        currentGlasses = currentGlasses,
                        currentCameras = currentCameras,
                        currentAudios = currentAudios,
                        currentBatteryCapacities = currentBatteryCapacities,
                        currentBatteryTypes = currentBatteryTypes,
                        currentCellularNetworks = currentCellularNetworks,
                        currentChargingPorts = currentChargingPorts,
                        currentWirelessConnectivity = currentWirelessConnectivity,
                        selectedDisplay = selectedDisplay,
                        screenSizeInch = screenSizeInch,
                        thicknessMm = thicknessMm,
                        selectedGlass = selectedGlass,
                        selectedCamera = selectedCamera,
                        selectedAudio = selectedAudio,
                        selectedBatteryCapacity = selectedBatteryCapacity,
                        selectedBatteryType = selectedBatteryType,
                        selectedCellularNetwork = selectedCellularNetwork,
                        selectedChargingPort = selectedChargingPort,
                        selectedWirelessConnectivity = selectedWirelessConnectivity,
                        unlockedTech = unlockedTech,
                        onDisplayChange = { selectedDisplay = it },
                        onScreenSizeChange = { screenSizeInch = it },
                        onThicknessChange = { thicknessMm = it },
                        onGlassChange = { selectedGlass = it },
                        onCameraChange = { selectedCamera = it },
                        onAudioChange = { selectedAudio = it },
                        onBatteryCapacityChange = { selectedBatteryCapacity = it },
                        onBatteryTypeChange = { selectedBatteryType = it },
                        onCellularNetworkChange = { selectedCellularNetwork = it },
                        onChargingPortChange = { selectedChargingPort = it },
                        onWirelessConnectivityChange = { selectedWirelessConnectivity = it },
                        onLockedClick = { lockedNoticeTech = it }
                    )
                }

                // ==================== SEKME 3: YAZILIM & OS ====================
                if (selectedTabSection == 3) {
                    PhoneBuilderSoftwareTab(
                        customOs = customOs,
                        selectedOsChoice = selectedOsChoice,
                        onOsChoiceChange = { selectedOsChoice = it }
                    )
                }

                // ==================== SEKME 4: SERİ, SEGMENT & ÜRETİM ====================
                if (selectedTabSection == 4) {
                    PhoneBuilderProductionTab(
                        existingSeries = existingSeries,
                        seriesMode = seriesMode,
                        selectedExistingSeries = selectedExistingSeries,
                        newSeriesName = newSeriesName,
                        generationNumber = generationNumber,
                        romanGen = romanGen,
                        selectedTier = selectedTier,
                        phoneName = phoneName,
                        defaultModelName = defaultModelName,
                        price = price,
                        quantity = quantity,
                        qaBudget = qaBudget,
                        unitCost = unitCost,
                        onSeriesModeChange = { seriesMode = it },
                        onSelectedExistingSeriesChange = { selectedExistingSeries = it },
                        onNewSeriesNameChange = { newSeriesName = it },
                        onGenerationNumberChange = { generationNumber = it },
                        onTierChange = { selectedTier = it },
                        onPhoneNameChange = { phoneName = it },
                        onPriceChange = { price = it },
                        onQuantityChange = { quantity = it },
                        onQaBudgetChange = { qaBudget = it },
                        factoryPeriodCapacity = factoryPeriodCapacity
                    )
                }
            }
        }
    }
}
