package com.example

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.CustomOsState
import com.example.viewmodel.LaunchCampaign
import com.example.viewmodel.ModelTier
import com.example.viewmodel.OsLicenseType
import com.example.viewmodel.OsType
import kotlin.math.roundToInt

@Composable
fun PhoneBuilderDesignTab(
    selectedColors: List<ColorOption>,
    previewColor: ColorOption,
    selectedBackFinish: String,
    selectedCameraBump: String,
    selectedFrameStyle: String,
    selectedNotchStyle: String,
    selectedStyle: String,
    selectedMaterial: String,
    unlockedTech: List<String>,
    onPreviewColorChange: (ColorOption) -> Unit,
    onColorsChange: (List<ColorOption>) -> Unit,
    onBackFinishChange: (String) -> Unit,
    onCameraBumpChange: (String) -> Unit,
    onFrameStyleChange: (String) -> Unit,
    onNotchStyleChange: (String) -> Unit,
    onStyleChange: (String) -> Unit,
    onMaterialChange: (String) -> Unit,
    onLockedClick: (String) -> Unit
) {
    // 1. Color Palette Selection
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Renk Seçenekleri & Palet (Çoklu Seçim)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text("${selectedColors.size} Renk Seçildi", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
        Text("Çoklu renk sunmak pazar çekiciliğini ve satışları artırır (Her ek renk: +$3)", fontSize = 11.sp, color = Slate500)

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(ALL_COLORS) { colorOpt ->
                val isSelected = selectedColors.any { it.hexValue == colorOpt.hexValue }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(colorOpt.color)
                        .border(
                            width = if (previewColor.hexValue == colorOpt.hexValue) 3.dp else if (isSelected) 2.dp else 1.dp,
                            color = if (previewColor.hexValue == colorOpt.hexValue) MaterialTheme.colorScheme.primary else if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f) else Slate300,
                            shape = CircleShape
                        )
                        .clickable {
                            onPreviewColorChange(colorOpt)
                            if (isSelected) {
                                if (selectedColors.size > 1) {
                                    onColorsChange(selectedColors.filter { it.hexValue != colorOpt.hexValue })
                                }
                            } else {
                                onColorsChange(selectedColors + colorOpt)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = if (colorOpt.color == Color(0xFFF1F5F9)) Color.Black else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }

    SelectionGroup(
        title = "Arka Kapak Yüzey Dokusu",
        options = ALL_BACK_FINISHES,
        selectedOption = selectedBackFinish,
        unlockedTech = unlockedTech,
        onOptionSelected = onBackFinishChange,
        onLockedClick = onLockedClick
    )

    SelectionGroup(
        title = "Kamera Modülü Tasarımı (Ada)",
        options = ALL_CAMERA_BUMPS,
        selectedOption = selectedCameraBump,
        unlockedTech = unlockedTech,
        onOptionSelected = onCameraBumpChange,
        onLockedClick = onLockedClick
    )

    SelectionGroup(
        title = "Kasa & Kenar Yapısı",
        options = ALL_FRAME_STYLES,
        selectedOption = selectedFrameStyle,
        unlockedTech = unlockedTech,
        onOptionSelected = onFrameStyleChange,
        onLockedClick = onLockedClick
    )

    SelectionGroup(
        title = "Ön Kamera & Çentik Tasarımı",
        options = ALL_NOTCH_STYLES,
        selectedOption = selectedNotchStyle,
        unlockedTech = unlockedTech,
        onOptionSelected = onNotchStyleChange,
        onLockedClick = onLockedClick
    )

    SelectionGroup(
        title = "Tasarım Teması",
        options = ALL_STYLES,
        selectedOption = selectedStyle,
        unlockedTech = unlockedTech,
        onOptionSelected = onStyleChange,
        onLockedClick = onLockedClick
    )

    SelectionGroup(
        title = "Kasa Malzemesi",
        options = ALL_MATERIALS,
        selectedOption = selectedMaterial,
        unlockedTech = unlockedTech,
        onOptionSelected = onMaterialChange,
        onLockedClick = onLockedClick
    )
}

@Composable
fun PhoneBuilderHardwareTab(
    currentProcessors: List<ComponentOption>,
    currentRamCapacities: List<ComponentOption>,
    currentRamTypes: List<ComponentOption>,
    currentStorages: List<ComponentOption>,
    currentSdCards: List<ComponentOption>,
    selectedProcessor: String,
    selectedRamCapacity: String,
    selectedRamType: String,
    selectedStorage: String,
    selectedSdCard: String,
    unlockedTech: List<String>,
    onProcessorChange: (String) -> Unit,
    onRamCapacityChange: (String) -> Unit,
    onRamTypeChange: (String) -> Unit,
    onStorageChange: (String) -> Unit,
    onSdCardChange: (String) -> Unit,
    onLockedClick: (String) -> Unit
) {
    SelectionGroup(
        title = "İşlemci (CPU & GPU)",
        options = currentProcessors,
        selectedOption = selectedProcessor,
        unlockedTech = unlockedTech,
        onOptionSelected = onProcessorChange,
        onLockedClick = onLockedClick
    )

    SelectionGroup(
        title = "RAM Kapasitesi",
        options = currentRamCapacities,
        selectedOption = selectedRamCapacity,
        unlockedTech = unlockedTech,
        onOptionSelected = onRamCapacityChange,
        onLockedClick = onLockedClick
    )

    SelectionGroup(
        title = "RAM Teknolojisi & Hızı",
        options = currentRamTypes,
        selectedOption = selectedRamType,
        unlockedTech = unlockedTech,
        onOptionSelected = onRamTypeChange,
        onLockedClick = onLockedClick
    )

    SelectionGroup(
        title = "Dahili Depolama Alanı",
        options = currentStorages,
        selectedOption = selectedStorage,
        unlockedTech = unlockedTech,
        onOptionSelected = onStorageChange,
        onLockedClick = onLockedClick
    )

    SelectionGroup(
        title = "MicroSD / Hafıza Kartı Yuvası",
        options = currentSdCards,
        selectedOption = selectedSdCard,
        unlockedTech = unlockedTech,
        onOptionSelected = onSdCardChange,
        onLockedClick = onLockedClick
    )
}

@Composable
fun PhoneBuilderScreenCameraTab(
    currentDisplays: List<ComponentOption>,
    currentGlasses: List<ComponentOption>,
    currentCameras: List<ComponentOption>,
    currentAudios: List<ComponentOption>,
    currentBatteryCapacities: List<ComponentOption>,
    currentBatteryTypes: List<ComponentOption>,
    currentCellularNetworks: List<ComponentOption>,
    currentChargingPorts: List<ComponentOption>,
    currentWirelessConnectivity: List<ComponentOption>,
    selectedDisplay: String,
    screenSizeInch: Float,
    thicknessMm: Float,
    selectedGlass: String,
    selectedCamera: String,
    selectedAudio: String,
    selectedBatteryCapacity: String,
    selectedBatteryType: String,
    selectedCellularNetwork: String,
    selectedChargingPort: String,
    selectedWirelessConnectivity: String,
    unlockedTech: List<String>,
    onDisplayChange: (String) -> Unit,
    onScreenSizeChange: (Float) -> Unit,
    onThicknessChange: (Float) -> Unit,
    onGlassChange: (String) -> Unit,
    onCameraChange: (String) -> Unit,
    onAudioChange: (String) -> Unit,
    onBatteryCapacityChange: (String) -> Unit,
    onBatteryTypeChange: (String) -> Unit,
    onCellularNetworkChange: (String) -> Unit,
    onChargingPortChange: (String) -> Unit,
    onWirelessConnectivityChange: (String) -> Unit,
    onLockedClick: (String) -> Unit
) {
    SelectionGroup(
        title = "Ekran Paneli & Çözünürlük",
        options = currentDisplays,
        selectedOption = selectedDisplay,
        unlockedTech = unlockedTech,
        onOptionSelected = onDisplayChange,
        onLockedClick = onLockedClick
    )

    // --- EKRAN BOYUTU & GÖVDE KALINLIĞI ---
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Slate200)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Fiziksel Boyut", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Ekran Boyutu", fontWeight = FontWeight.Medium, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${"%.1f".format(screenSizeInch)}\"", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                }
                Slider(
                    value = screenSizeInch,
                    onValueChange = onScreenSizeChange,
                    valueRange = 5.4f..6.9f,
                    steps = 14
                )
                val sizeHint = when {
                    screenSizeInch < 5.8f -> "Kompakt — tek elle kullanım, gençlerde popüler"
                    screenSizeInch < 6.4f -> "Standart — geniş kitleye hitap eden dengeli boyut"
                    screenSizeInch < 6.8f -> "Büyük — medya/oyun odaklı kullanıcılar için"
                    else -> "Phablet — maksimum ekran, taşınabilirlikten ödün verir"
                }
                Text(sizeHint, fontSize = 10.sp, color = Slate500)
            }

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Gövde Kalınlığı", fontWeight = FontWeight.Medium, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${"%.1f".format(thicknessMm)} mm", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                }
                Slider(
                    value = thicknessMm,
                    onValueChange = onThicknessChange,
                    valueRange = 6.5f..9.5f,
                    steps = 11
                )
                val thicknessHint = when {
                    thicknessMm < 7.2f -> "Ultra İnce — premium his, ama daha düşük batarya/dayanıklılık"
                    thicknessMm < 8.2f -> "Standart — çoğu amiral gemide kullanılan denge"
                    else -> "Kalın — daha büyük batarya kapasitesine ve dayanıklılığa izin verir"
                }
                Text(thicknessHint, fontSize = 10.sp, color = Slate500)
            }
        }
    }

    SelectionGroup(
        title = "Koruyucu Ön Cam",
        options = currentGlasses,
        selectedOption = selectedGlass,
        unlockedTech = unlockedTech,
        onOptionSelected = onGlassChange,
        onLockedClick = onLockedClick
    )

    SelectionGroup(
        title = "Kamera Sensörü & Lensler",
        options = currentCameras,
        selectedOption = selectedCamera,
        unlockedTech = unlockedTech,
        onOptionSelected = onCameraChange,
        onLockedClick = onLockedClick
    )

    SelectionGroup(
        title = "Ses & Hoparlör Sistemi",
        options = currentAudios,
        selectedOption = selectedAudio,
        unlockedTech = unlockedTech,
        onOptionSelected = onAudioChange,
        onLockedClick = onLockedClick
    )

    SelectionGroup(
        title = "Batarya Kapasitesi",
        options = currentBatteryCapacities,
        selectedOption = selectedBatteryCapacity,
        unlockedTech = unlockedTech,
        onOptionSelected = onBatteryCapacityChange,
        onLockedClick = onLockedClick
    )

    SelectionGroup(
        title = "Şarj Teknolojisi",
        options = currentBatteryTypes,
        selectedOption = selectedBatteryType,
        unlockedTech = unlockedTech,
        onOptionSelected = onBatteryTypeChange,
        onLockedClick = onLockedClick
    )

    SelectionGroup(
        title = "Mobil Şebeke & Hücresel Modem (3G / 4G / 5G / Uydu)",
        options = currentCellularNetworks,
        selectedOption = selectedCellularNetwork,
        unlockedTech = unlockedTech,
        onOptionSelected = onCellularNetworkChange,
        onLockedClick = onLockedClick
    )

    SelectionGroup(
        title = "Şarj & Kablolu Veri Portu (USB / Tip-C / Thunderbolt)",
        options = currentChargingPorts,
        selectedOption = selectedChargingPort,
        unlockedTech = unlockedTech,
        onOptionSelected = onChargingPortChange,
        onLockedClick = onLockedClick
    )

    SelectionGroup(
        title = "Kablosuz Ağ & Bluetooth Çipi (Wi-Fi 4/5/6/7 & BT)",
        options = currentWirelessConnectivity,
        selectedOption = selectedWirelessConnectivity,
        unlockedTech = unlockedTech,
        onOptionSelected = onWirelessConnectivityChange,
        onLockedClick = onLockedClick
    )
}

@Composable
fun PhoneBuilderSoftwareTab(
    customOs: CustomOsState?,
    selectedOsChoice: Int,
    onOsChoiceChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("📲 Bu Cihaz İçin OS Seçimi", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
            Text("Bu telefonda hangi işletim sisteminin çalışacağını seçin. Kendi platformunuzu geliştirmek/yükseltmek için Yazılım ekranına gidin — burada sadece bu cihaza hangisini yükleyeceğinizi seçiyorsunuz.", fontSize = 11.sp, color = Slate600)

            val hasCompanyOs = customOs != null && customOs.type != OsType.STOCK_ANDROID
            val isCompanyOsSelected = selectedOsChoice == 1

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isCompanyOsSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                border = if (isCompanyOsSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else BorderStroke(1.dp, Slate200),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { if (hasCompanyOs) onOsChoiceChange(1) }
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = if (hasCompanyOs) "🚀 Şirket Yazılımı: ${customOs?.name} v${customOs?.version}" else "🔒 Şirket Yazılımı (Geliştirilmedi)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (hasCompanyOs) (if (isCompanyOsSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface) else Slate400
                            )
                            if (hasCompanyOs) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (customOs?.licenseType == OsLicenseType.OPEN_SOURCE) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFF6366F1).copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = customOs?.licenseType?.badge ?: "",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (customOs?.licenseType == OsLicenseType.OPEN_SOURCE) Color(0xFF10B981) else Color(0xFF6366F1),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        RadioButton(
                            selected = isCompanyOsSelected,
                            onClick = { if (hasCompanyOs) onOsChoiceChange(1) },
                            enabled = hasCompanyOs
                        )
                    }

                    if (hasCompanyOs) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text("• Lisans Bedeli: $0 (Kendi ekosisteminiz, ekstra cihaz maliyeti yok)", fontSize = 11.sp, color = Green500, fontWeight = FontWeight.SemiBold)
                            Text("• Optimizasyon Puanı: +${(customOs?.optimizationScore ?: 0) / 5} İnceleme Bonusu", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("• Pazar Popülaritesi: %${"%.1f".format(customOs?.popularityPercent ?: 1.0f)} (${customOs?.focus?.title} Odaklı)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("• Ekosistem Kazancı: Satılan cihazlardan aylık App Store mağaza geliri!", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text("Yazılım & OS sekmesinden $100M Ar-Ge ile kendi işletim sisteminizi oluşturabilirsiniz.", fontSize = 11.sp, color = Slate500)
                    }
                }
            }

            val isAospSelected = selectedOsChoice == 0
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isAospSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                border = if (isAospSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else BorderStroke(1.dp, Slate200),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOsChoiceChange(0) }
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("🌐 Saf Açık Kaynak Android (AOSP)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (isAospSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                            Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF10B981).copy(alpha = 0.15f)) {
                                Text("Ücretsiz", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981), modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }
                        RadioButton(selected = isAospSelected, onClick = { onOsChoiceChange(0) })
                    }
                    Text("• Lisans Bedeli: $0 • Standart temel optimizasyon", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("• Ekosistem Mağaza Geliri: Yok (Gelir üçüncü taraf arama devine gider)", fontSize = 11.sp, color = Slate500)
                }
            }

            val isGlobalOsSelected = selectedOsChoice == 2
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isGlobalOsSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                border = if (isGlobalOsSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else BorderStroke(1.dp, Slate200),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOsChoiceChange(2) }
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("🏢 Ticari Lisanslı GlobalOS", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (isGlobalOsSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                            Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFF59E0B).copy(alpha = 0.2f)) {
                                Text("+$10 Lisans", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309), modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }
                        RadioButton(selected = isGlobalOsSelected, onClick = { onOsChoiceChange(2) })
                    }
                    Text("• Lisans Maliyeti: Cihaz başı $10 OEM lisans ücreti", fontSize = 11.sp, color = Color(0xFFB45309), fontWeight = FontWeight.SemiBold)
                    Text("• Küresel hazır servisler ve popüler ön yüklü uygulamalar (+3 İnceleme Bonusu)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun PhoneBuilderProductionTab(
    existingSeries: List<String>,
    seriesMode: Int,
    selectedExistingSeries: String,
    newSeriesName: String,
    generationNumber: Int,
    romanGen: String,
    selectedTier: ModelTier,
    phoneName: String,
    defaultModelName: String,
    price: Float,
    quantity: Float,
    qaBudget: Float,
    unitCost: Int,
    onSeriesModeChange: (Int) -> Unit,
    onSelectedExistingSeriesChange: (String) -> Unit,
    onNewSeriesNameChange: (String) -> Unit,
    onGenerationNumberChange: (Int) -> Unit,
    onTierChange: (ModelTier) -> Unit,
    onPhoneNameChange: (String) -> Unit,
    onPriceChange: (Float) -> Unit,
    onQuantityChange: (Float) -> Unit,
    onQaBudgetChange: (Float) -> Unit,
    factoryPeriodCapacity: Int = 0,
    selectedLaunchCampaign: LaunchCampaign = LaunchCampaign.ORGANIC,
    onLaunchCampaignChange: (LaunchCampaign) -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("📱 Seri ve Model Ailesi Yönetimi", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
            Text("Aynı seriyi devam ettirmek marka sadakatini ve tekrar eden müşteri satışlarını artırır.", fontSize = 11.sp, color = Slate600)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (seriesMode == 0 && existingSeries.isNotEmpty()) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, if (seriesMode == 0 && existingSeries.isNotEmpty()) MaterialTheme.colorScheme.primary else Slate300),
                    modifier = Modifier
                        .weight(1f)
                        .clickable(enabled = existingSeries.isNotEmpty()) { onSeriesModeChange(0) }
                ) {
                    Text(
                        text = "Mevcut Seriden Devam Et",
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontWeight = if (seriesMode == 0 && existingSeries.isNotEmpty()) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 11.5.sp,
                        color = if (seriesMode == 0 && existingSeries.isNotEmpty()) MaterialTheme.colorScheme.primary else if (existingSeries.isEmpty()) Slate400 else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                        maxLines = 1,
                        softWrap = false
                    )
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (seriesMode == 1 || existingSeries.isEmpty()) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, if (seriesMode == 1 || existingSeries.isEmpty()) MaterialTheme.colorScheme.primary else Slate300),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSeriesModeChange(1) }
                ) {
                    Text(
                        text = "Yeni Seri Başlat",
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontWeight = if (seriesMode == 1 || existingSeries.isEmpty()) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 11.5.sp,
                        color = if (seriesMode == 1 || existingSeries.isEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            if (seriesMode == 0 && existingSeries.isNotEmpty()) {
                Text("Mevcut Seriler:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(existingSeries) { sName ->
                        val isSel = selectedExistingSeries == sName
                        ElevatedFilterChip(
                            selected = isSel,
                            onClick = { onSelectedExistingSeriesChange(sName) },
                            label = { Text(sName, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }
            } else {
                OutlinedTextField(
                    value = newSeriesName,
                    onValueChange = onNewSeriesNameChange,
                    label = { Text("Yeni Seri Adı") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Nesil Numarası (Gen):", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(onClick = { if (generationNumber > 1) onGenerationNumberChange(generationNumber - 1) }) {
                        Icon(Icons.Default.Remove, contentDescription = "-")
                    }
                    Text("Gen $generationNumber ($romanGen)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                    IconButton(onClick = { onGenerationNumberChange(generationNumber + 1) }) {
                        Icon(Icons.Default.Add, contentDescription = "+")
                    }
                }
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🏆 Model Segmenti",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${selectedTier.badge} ${selectedTier.title}",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
            Text(
                text = "Aynı seri altında Lite (Ekonomik), Standart (Dengeli), Pro ve Ultra / Pro Max (Zirve) modelleri oluşturabilirsiniz.",
                fontSize = 11.sp,
                color = Slate600
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val chunkedTiers = ModelTier.entries.chunked(2)
                chunkedTiers.forEach { rowTiers ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowTiers.forEach { tier ->
                            val isTierSelected = selectedTier == tier
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isTierSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                border = BorderStroke(
                                    width = if (isTierSelected) 2.dp else 1.dp,
                                    color = if (isTierSelected) MaterialTheme.colorScheme.primary else Slate300
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        onTierChange(tier)
                                        val suggestedPrice = when (tier) {
                                            ModelTier.LITE -> 349f
                                            ModelTier.STANDARD -> 599f
                                            ModelTier.PRO -> 899f
                                            ModelTier.ULTRA -> 1199f
                                        }
                                        onPriceChange(suggestedPrice)
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(tier.badge, fontSize = 18.sp)
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = tier.title,
                                            fontWeight = if (isTierSelected) FontWeight.Bold else FontWeight.SemiBold,
                                            fontSize = 12.sp,
                                            color = if (isTierSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                        Text(
                                            text = when (tier) {
                                                ModelTier.LITE -> "Ekonomik Giriş"
                                                ModelTier.STANDARD -> "Dengeli Ana Akım"
                                                ModelTier.PRO -> "Amiral Gemisi"
                                                ModelTier.ULTRA -> "Zirve Prestij"
                                            },
                                            fontSize = 10.sp,
                                            color = if (isTierSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f) else Slate500,
                                            maxLines = 1
                                        )
                                    }
                                    if (isTierSelected) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "Üretim maliyeti ($$unitCost) yalnızca seçtiğiniz fiziksel donanımlara (İşlemci, RAM, Kamera vb.) göre hesaplanır. Segment ismi maliyeti yapay olarak değiştirmez.",
                        fontSize = 10.5.sp,
                        color = MaterialTheme.colorScheme.primary,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }

    OutlinedTextField(
        value = phoneName,
        onValueChange = onPhoneNameChange,
        label = { Text("Model Ticari Adı") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            IconButton(onClick = {
                onPhoneNameChange(defaultModelName)
            }) {
                Icon(Icons.Default.Refresh, contentDescription = "Otomatik İsim")
            }
        }
    )

    // --- CANLI SEGMENT, FİYAT ESNEKLİĞİ & KÂR MARJI ANALİZ PANELİ ---
    val margin = price - unitCost
    val marginPercent = if (price > 0) ((margin / price) * 100).toInt() else 0
    val (fairMin, fairMax) = when {
        unitCost >= 500 -> 1099 to 1799
        unitCost >= 320 -> 799 to 1199
        unitCost >= 180 -> 499 to 799
        unitCost >= 90 -> 299 to 499
        else -> 129 to 299
    }
    val autoSegment = when {
        unitCost >= 500 -> "👑 Ultra Amiral Gemisi"
        unitCost >= 320 -> "⚡ Amiral Gemisi & Premium"
        unitCost >= 180 -> "💎 Orta-Üst Segment"
        unitCost >= 90 -> "⚖️ Orta Segment (F/P)"
        else -> "🌱 Giriş Seviyesi (Ekonomik)"
    }
    val (elasticityLabel, elasticityColor, demandMultiplierText, elasticityDesc) = when {
        price < fairMin * 0.88f -> Quadruple("🔥 Agresif Fiyat Kırıcı", Color(0xFF10B981), "+%45 Talep Artışı", "Fiyat donanıma göre inanılmaz cazip! Tüketici akını ve pazar payı patlaması.")
        price <= fairMax -> Quadruple("✨ Mükemmel F/P & Dengeli", Color(0xFF2563EB), "%100 Standart Talep", "Fiyat segment standartlarına tam oturuyor. Dengeli satış ve kâr marjı.")
        price <= fairMax * 1.25f -> Quadruple("⚠️ Yüksek Fiyatlandırma", Color(0xFFF59E0B), "-%25 Talep Azalışı", "Fiyat biraz tuzlu. Sadece marka itibarınız güçlüyse hedeflenen satış yakalanır.")
        else -> Quadruple("🚨 Aşırı Pahalı (Stok Riski)", Color(0xFFEF4444), "-%55 Sert Talep Düşüşü", "Tüketici bu fiyata tepki gösterir! Satışlar çok yavaşlar ve depo stokları şişebilir.")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Slate200)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("📊 Canlı Segment & Fiyat/Talep Analizi", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = autoSegment,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            // Fiyat Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Satış Fiyatı (Perakende)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Segment Tavsiyesi: $$fairMin - $$fairMax", fontSize = 10.5.sp, color = Slate500)
                    }
                    Text("$${price.toInt()}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
                }
                Slider(
                    value = price,
                    onValueChange = onPriceChange,
                    valueRange = 99f..2499f,
                    steps = 47
                )
            }

            // Talep Esnekliği & Kâr Marjı Bilgi Kutusu
            Surface(
                color = elasticityColor.copy(alpha = 0.08f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, elasticityColor.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = elasticityLabel,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp,
                            color = elasticityColor
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = elasticityColor.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = demandMultiplierText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = elasticityColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = elasticityDesc,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp
                    )
                }
            }

            // Üretim Adedi Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Üretim Adedi (Parti)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("${"%,d".format(quantity.toLong()).replace(',', '.')} Adet", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                }
                Slider(
                    value = quantity,
                    onValueChange = onQuantityChange,
                    valueRange = 10000f..1000000f,
                    steps = 98
                )
                if (factoryPeriodCapacity > 0) {
                    val periodsNeeded = kotlin.math.ceil(quantity / factoryPeriodCapacity.toFloat()).toInt().coerceAtLeast(1)
                    val instantPart = quantity.toInt().coerceAtMost(factoryPeriodCapacity)
                    val note = if (quantity.toInt() <= factoryPeriodCapacity) {
                        "🏭 Fabrikanız bu partiyi tek periyotta üretebilir."
                    } else {
                        "🏭 Fabrika kapasitesi periyotta ${"%,d".format(factoryPeriodCapacity)} adet. İlk periyotta ${"%,d".format(instantPart)} adet üretilir, tamamı yaklaşık $periodsNeeded periyotta (${(periodsNeeded / 2f).let { "%.1f".format(it) }} ay) tamamlanır."
                    }
                    Text(
                        text = note,
                        fontSize = 10.sp,
                        color = if (quantity.toInt() <= factoryPeriodCapacity) Green500 else Color(0xFFB45309),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Kalite Kontrol Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Kalite Kontrol (QA) Bütçesi", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("$${"%,d".format(qaBudget.toLong()).replace(',', '.')}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                }
                Slider(
                    value = qaBudget,
                    onValueChange = onQaBudgetChange,
                    valueRange = 0f..1000000f,
                    steps = 20
                )

                val qaPerUnitPreview = if (quantity > 0) qaBudget / quantity else 0f
                val recallRiskPreview = (55f - (qaPerUnitPreview * 3f).coerceAtMost(40f))
                    .roundToInt().coerceIn(2, 55)
                val riskColor = when {
                    recallRiskPreview <= 15 -> Green500
                    recallRiskPreview <= 30 -> Color(0xFFF59E0B)
                    else -> Color(0xFFEF4444)
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Tahmini Geri Çağırma Riski (ilk 3 ay)",
                        fontSize = 11.sp,
                        color = Slate600
                    )
                    Text(
                        "%$recallRiskPreview",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = riskColor
                    )
                }
            }

            // Lansman Pazarlama ve Hype Kampanyası
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📢 Lansman Pazarlama & Hype", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(
                        "Hype: ${selectedLaunchCampaign.initialHype}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp
                    )
                }

                Text(
                    "Harika bir telefon sıfır reklamla satılmaz; zayıf bir telefona devasa reklam bütçesi dökerseniz Hype patlar ancak ikinci hafta ısınma/donma iadeleriyle ve itibar kaybıyla yüzleşirsiniz!",
                    fontSize = 10.5.sp,
                    color = Slate600,
                    lineHeight = 14.sp
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    LaunchCampaign.entries.forEach { campaign ->
                        val isSelected = selectedLaunchCampaign == campaign
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onLaunchCampaignChange(campaign) },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface,
                            border = if (isSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text(campaign.title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text(
                                            "+${campaign.initialHype} Hype",
                                            fontSize = 10.sp,
                                            color = if (campaign.initialHype >= 90) Color(0xFFEF4444) else if (campaign.initialHype >= 50) Color(0xFFF59E0B) else Green500,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Text(campaign.description, fontSize = 10.sp, color = Slate600)
                                    Text(campaign.riskWarning, fontSize = 9.5.sp, color = if (campaign == LaunchCampaign.ORGANIC) Color(0xFFB45309) else Color(0xFFDC2626))
                                }
                                Text(
                                    if (campaign.cost == 0L) "Ücretsiz" else "$${"%,d".format(campaign.cost).replace(',', '.')}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (campaign.cost == 0L) Green500 else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            // Kâr Marjı ve Maliyet Özeti
            val totalBatchCost = (unitCost.toLong() * quantity.toLong()) + qaBudget.toLong() + selectedLaunchCampaign.cost
            Surface(
                color = if (margin > 0) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Birim Kâr & Marj", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = if (margin >= 0) "+$${margin.toInt()} (%$marginPercent Marj)" else "-$${kotlin.math.abs(margin).toInt()} (Zararına Satış!)",
                            fontWeight = FontWeight.Bold,
                            color = if (margin > 0) Green500 else Color.Red,
                            fontSize = 13.5.sp
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Parti + Lansman Maliyeti", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$${"%,d".format(totalBatchCost).replace(',', '.')}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
