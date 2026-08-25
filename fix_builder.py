with open('app/src/main/java/com/example/PhoneBuilderScreen.kt', 'r') as f:
    content = f.read()

# Replace list filtering & defaults
old_init = """    val styles = ALL_STYLES
    val materials = ALL_MATERIALS.filter { it.availableFrom <= year && (it.requiredTech == null || unlockedTech.contains(it.requiredTech)) }
    val processors = ALL_PROCESSORS.filter { it.availableFrom <= year && (it.requiredTech == null || unlockedTech.contains(it.requiredTech)) }
    val currentProcessors = processors.takeLast(6).ifEmpty { processors }
    
    val rams = ALL_RAMS.filter { it.availableFrom <= year && (it.requiredTech == null || unlockedTech.contains(it.requiredTech)) }
    val currentRams = rams.takeLast(4).ifEmpty { rams }

    val displays = ALL_DISPLAYS.filter { it.availableFrom <= year && (it.requiredTech == null || unlockedTech.contains(it.requiredTech)) }
    val currentDisplays = displays.takeLast(4).ifEmpty { displays }

    val glasses = ALL_GLASSES.filter { it.availableFrom <= year && (it.requiredTech == null || unlockedTech.contains(it.requiredTech)) }
    val currentGlasses = glasses.takeLast(4).ifEmpty { glasses }

    val cameras = ALL_CAMERAS.filter { it.availableFrom <= year && (it.requiredTech == null || unlockedTech.contains(it.requiredTech)) }
    val currentCameras = cameras.takeLast(4).ifEmpty { cameras }

    val connectivityOptions = ALL_CONNECTIVITY.filter { it.availableFrom <= year && (it.requiredTech == null || unlockedTech.contains(it.requiredTech)) }
    val currentConnectivity = connectivityOptions.takeLast(4).ifEmpty { connectivityOptions }

    val audios = ALL_AUDIO.filter { it.availableFrom <= year && (it.requiredTech == null || unlockedTech.contains(it.requiredTech)) }
    val currentAudios = audios.takeLast(4).ifEmpty { audios }

    val batteryCapacities = ALL_BATTERY_CAPACITIES.filter { it.availableFrom <= year && (it.requiredTech == null || unlockedTech.contains(it.requiredTech)) }
    val currentBatteryCapacities = batteryCapacities.takeLast(4).ifEmpty { batteryCapacities }

    val batteryTypes = ALL_BATTERY_TYPES.filter { it.availableFrom <= year && (it.requiredTech == null || unlockedTech.contains(it.requiredTech)) }
    val currentBatteryTypes = batteryTypes.takeLast(4).ifEmpty { batteryTypes }

    var phoneName by remember { mutableStateOf("X1 Alpha") }
    var selectedStyle by remember { mutableStateOf(styles[0].name) }
    var selectedMaterial by remember { mutableStateOf(materials[1].name) }
    var selectedProcessor by remember { mutableStateOf(currentProcessors.last().name) }
    var selectedRam by remember { mutableStateOf(currentRams.last().name) }
    var selectedDisplay by remember { mutableStateOf(currentDisplays.last().name) }
    var selectedGlass by remember { mutableStateOf(currentGlasses.last().name) }
    var selectedCamera by remember { mutableStateOf(currentCameras.last().name) }
    var selectedConnectivity by remember { mutableStateOf(currentConnectivity.last().name) }
    var selectedAudio by remember { mutableStateOf(currentAudios.last().name) }
    var selectedBatteryCapacity by remember { mutableStateOf(currentBatteryCapacities.last().name) }
    var selectedBatteryType by remember { mutableStateOf(currentBatteryTypes.last().name) }"""

new_init = """    val styles = ALL_STYLES.ifEmpty { listOf(ComponentOption("Modern", 10)) }
    val materials = ALL_MATERIALS.filter { it.availableFrom <= year && (it.requiredTech == null || unlockedTech.contains(it.requiredTech)) }.ifEmpty { ALL_MATERIALS.take(1) }
    val processors = ALL_PROCESSORS.filter { it.availableFrom <= year && (it.requiredTech == null || unlockedTech.contains(it.requiredTech)) }
    val currentProcessors = processors.takeLast(6).ifEmpty { ALL_PROCESSORS.take(1) }
    
    val rams = ALL_RAMS.filter { it.availableFrom <= year && (it.requiredTech == null || unlockedTech.contains(it.requiredTech)) }
    val currentRams = rams.takeLast(4).ifEmpty { ALL_RAMS.take(1) }

    val displays = ALL_DISPLAYS.filter { it.availableFrom <= year && (it.requiredTech == null || unlockedTech.contains(it.requiredTech)) }
    val currentDisplays = displays.takeLast(4).ifEmpty { ALL_DISPLAYS.take(1) }

    val glasses = ALL_GLASSES.filter { it.availableFrom <= year && (it.requiredTech == null || unlockedTech.contains(it.requiredTech)) }
    val currentGlasses = glasses.takeLast(4).ifEmpty { ALL_GLASSES.take(1) }

    val cameras = ALL_CAMERAS.filter { it.availableFrom <= year && (it.requiredTech == null || unlockedTech.contains(it.requiredTech)) }
    val currentCameras = cameras.takeLast(4).ifEmpty { ALL_CAMERAS.take(1) }

    val connectivityOptions = ALL_CONNECTIVITY.filter { it.availableFrom <= year && (it.requiredTech == null || unlockedTech.contains(it.requiredTech)) }
    val currentConnectivity = connectivityOptions.takeLast(4).ifEmpty { ALL_CONNECTIVITY.take(1) }

    val audios = ALL_AUDIO.filter { it.availableFrom <= year && (it.requiredTech == null || unlockedTech.contains(it.requiredTech)) }
    val currentAudios = audios.takeLast(4).ifEmpty { ALL_AUDIO.take(1) }

    val batteryCapacities = ALL_BATTERY_CAPACITIES.filter { it.availableFrom <= year && (it.requiredTech == null || unlockedTech.contains(it.requiredTech)) }
    val currentBatteryCapacities = batteryCapacities.takeLast(4).ifEmpty { ALL_BATTERY_CAPACITIES.take(1) }

    val batteryTypes = ALL_BATTERY_TYPES.filter { it.availableFrom <= year && (it.requiredTech == null || unlockedTech.contains(it.requiredTech)) }
    val currentBatteryTypes = batteryTypes.takeLast(4).ifEmpty { ALL_BATTERY_TYPES.take(1) }

    var phoneName by remember { mutableStateOf("X1 Alpha") }
    var selectedStyle by remember { mutableStateOf(styles.firstOrNull()?.name ?: "Modern") }
    var selectedMaterial by remember { mutableStateOf(materials.getOrNull(1)?.name ?: materials.firstOrNull()?.name ?: "Plastik") }
    var selectedProcessor by remember { mutableStateOf(currentProcessors.lastOrNull()?.name ?: ALL_PROCESSORS.first().name) }
    var selectedRam by remember { mutableStateOf(currentRams.lastOrNull()?.name ?: ALL_RAMS.first().name) }
    var selectedDisplay by remember { mutableStateOf(currentDisplays.lastOrNull()?.name ?: ALL_DISPLAYS.first().name) }
    var selectedGlass by remember { mutableStateOf(currentGlasses.lastOrNull()?.name ?: ALL_GLASSES.first().name) }
    var selectedCamera by remember { mutableStateOf(currentCameras.lastOrNull()?.name ?: ALL_CAMERAS.first().name) }
    var selectedConnectivity by remember { mutableStateOf(currentConnectivity.lastOrNull()?.name ?: ALL_CONNECTIVITY.first().name) }
    var selectedAudio by remember { mutableStateOf(currentAudios.lastOrNull()?.name ?: ALL_AUDIO.first().name) }
    var selectedBatteryCapacity by remember { mutableStateOf(currentBatteryCapacities.lastOrNull()?.name ?: ALL_BATTERY_CAPACITIES.first().name) }
    var selectedBatteryType by remember { mutableStateOf(currentBatteryTypes.lastOrNull()?.name ?: ALL_BATTERY_TYPES.first().name) }"""

content = content.replace(old_init, new_init)

# Remove steps from Sliders
content = content.replace(",\n                        steps = 190", "")
content = content.replace(",\n                        steps = 499", "")
content = content.replace(",\n                        steps = 200", "")

with open('app/src/main/java/com/example/PhoneBuilderScreen.kt', 'w') as f:
    f.write(content)

