package com.example

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import com.example.ui.Button3D
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.util.ComponentRating
import com.example.util.HardwareRatingHelper
import com.example.util.HardwareTier
import com.example.viewmodel.CompetitorCompany
import com.example.viewmodel.CompetitorReleaseHistory
import com.example.viewmodel.GameState
import com.example.viewmodel.TechExpoEvent

@Composable
fun CompetitorProfileCard(competitor: CompetitorCompany) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BrandLogo(
                    companyId = competitor.id,
                    companyName = competitor.name,
                    size = 46.dp,
                    shapeRadius = 12.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        competitor.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(competitor.brandColorHex)
                    )
                    Text(
                        "\"${competitor.slogan}\"",
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(competitor.brandColorHex).copy(alpha = 0.12f)
                    ) {
                        Text(
                            "%${"%.1f".format(competitor.marketSharePercent)} Pazar",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(competitor.brandColorHex),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Değer: $${com.example.viewmodel.formatShortCurrency(competitor.estimatedValuation)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(12.dp))

            // Strateji
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.TrendingUp, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "Strateji: ${competitor.strategyType}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Güçlü Yön
            Row(verticalAlignment = Alignment.Top) {
                Text("✅ ", fontSize = 12.sp)
                Text(
                    competitor.strengthText,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Zayıf Yön
            Row(verticalAlignment = Alignment.Top) {
                Text("⚠️ ", fontSize = 12.sp)
                Text(
                    competitor.weaknessText,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Son Amiral Gemisi", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(competitor.currentTopModel, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Text(
                        "$${competitor.currentModelPrice} • ${competitor.currentModelScore}/100 Puan",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CompetitorReleaseRow(
    release: CompetitorReleaseHistory,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = if (release.duelVerdict != null) BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BrandLogo(
                    companyName = release.companyName,
                    size = 40.dp,
                    shapeRadius = 10.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        release.headline,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "${release.companyName} • $${release.price} • Puan: ${release.score}/100",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        "${release.month}/${release.year}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            if (release.duelVerdict != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
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
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Default.CompareArrows,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = release.duelVerdict,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Text(
                            text = "VS Kıyasla ⚔️",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VsDuelComparisonDialog(
    release: CompetitorReleaseHistory,
    state: GameState,
    onDismiss: () -> Unit
) {
    // Find player's best model or model specified in duel
    val playerModel = state.activeModels.find { it.specs.name == release.vsPlayerModelName }
        ?: state.activeModels.filter { !it.isCompleted }.maxByOrNull { it.reviewScore }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("⚔️", fontSize = 20.sp)
                    Text("Pazar Düellosu: VS Kıyaslama", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Kapat")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Verdict Banner
                if (release.duelVerdict != null) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF1E293B),
                        border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("📰", fontSize = 16.sp)
                            Text(
                                text = "Basın Değerlendirmesi: ${release.duelVerdict}",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8)
                            )
                        }
                    }
                }

                // Two Column VS Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val playerProc = playerModel?.specs?.processor ?: "—"
                    val playerRam = playerModel?.specs?.ram ?: "—"
                    val playerCam = playerModel?.specs?.camera ?: "—"
                    val playerBat = "${playerModel?.specs?.batteryCapacity ?: "—"} ${playerModel?.specs?.batteryType ?: ""}"
                    val playerDisp = playerModel?.specs?.display ?: "—"

                    val playerProcRating = HardwareRatingHelper.getProcessorRating(playerProc)
                    val compProcRating = HardwareRatingHelper.getProcessorRating(release.processor)

                    val playerRamRating = HardwareRatingHelper.getRamRating(playerRam)
                    val compRamRating = HardwareRatingHelper.getRamRating(release.ram)

                    val playerCamRating = HardwareRatingHelper.getCameraRating(playerCam)
                    val compCamRating = HardwareRatingHelper.getCameraRating(release.camera)

                    val playerBatRating = HardwareRatingHelper.getBatteryRating(playerBat)
                    val compBatRating = HardwareRatingHelper.getBatteryRating(release.battery)

                    val playerDispRating = HardwareRatingHelper.getDisplayRating(playerDisp)
                    val compDispRating = HardwareRatingHelper.getDisplayRating(release.display)

                    val playerHwIndex = HardwareRatingHelper.calculateHardwarePowerIndex(
                        playerProc, playerRam, playerCam, playerBat, playerDisp
                    )
                    val compHwIndex = HardwareRatingHelper.calculateHardwarePowerIndex(
                        release.processor, release.ram, release.camera, release.battery, release.display
                    )

                    // Player Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            BrandLogo(
                                companyName = state.companyName,
                                isPlayer = true,
                                playerLogoId = state.companyLogoId,
                                playerBrandColorHex = state.companyBrandColorHex,
                                size = 32.dp,
                                shapeRadius = 8.dp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                state.companyName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                playerModel?.specs?.name ?: "Modeliniz Yok",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "$${playerModel?.specs?.price ?: 0}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.primary
                                ) {
                                    Text(
                                        "⭐ ${playerModel?.reviewScore ?: 0}/100",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF1E293B)
                            ) {
                                Text(
                                    "⚡ $playerHwIndex Donanım",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF38BDF8),
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // VS Divider
                    Box(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEF4444)
                        ) {
                            Text(
                                "VS",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Competitor Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            BrandLogo(
                                companyName = release.companyName,
                                size = 32.dp,
                                shapeRadius = 8.dp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                release.companyName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                release.modelName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "$${release.price}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ) {
                                    Text(
                                        "⭐ ${release.score}/100",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF1E293B)
                            ) {
                                Text(
                                    "⚡ $compHwIndex Donanım",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFBBF24),
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                // Specs Breakdown Comparison Table
                val playerProc = playerModel?.specs?.processor ?: "—"
                val playerRam = playerModel?.specs?.ram ?: "—"
                val playerCam = playerModel?.specs?.camera ?: "—"
                val playerBat = "${playerModel?.specs?.batteryCapacity ?: "—"} ${playerModel?.specs?.batteryType ?: ""}"
                val playerDisp = playerModel?.specs?.display ?: "—"

                val playerProcRating = HardwareRatingHelper.getProcessorRating(playerProc)
                val compProcRating = HardwareRatingHelper.getProcessorRating(release.processor)

                val playerRamRating = HardwareRatingHelper.getRamRating(playerRam)
                val compRamRating = HardwareRatingHelper.getRamRating(release.ram)

                val playerCamRating = HardwareRatingHelper.getCameraRating(playerCam)
                val compCamRating = HardwareRatingHelper.getCameraRating(release.camera)

                val playerBatRating = HardwareRatingHelper.getBatteryRating(playerBat)
                val compBatRating = HardwareRatingHelper.getBatteryRating(release.battery)

                val playerDispRating = HardwareRatingHelper.getDisplayRating(playerDisp)
                val compDispRating = HardwareRatingHelper.getDisplayRating(release.display)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DuelSpecRow(
                            title = "İşlemci / Çip (CPU & GPU)",
                            playerVal = playerProc,
                            compVal = release.processor,
                            playerRating = playerProcRating,
                            compRating = compProcRating
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        DuelSpecRow(
                            title = "Bellek (RAM)",
                            playerVal = playerRam,
                            compVal = release.ram,
                            playerRating = playerRamRating,
                            compRating = compRamRating
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        DuelSpecRow(
                            title = "Kamera Sistemi",
                            playerVal = playerCam,
                            compVal = release.camera,
                            playerRating = playerCamRating,
                            compRating = compCamRating
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        DuelSpecRow(
                            title = "Batarya & Pil",
                            playerVal = playerBat,
                            compVal = release.battery,
                            playerRating = playerBatRating,
                            compRating = compBatRating
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        DuelSpecRow(
                            title = "Ekran Teknolojisi",
                            playerVal = playerDisp,
                            compVal = release.display,
                            playerRating = playerDispRating,
                            compRating = compDispRating
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button3D(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Anladım", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun DuelSpecRow(
    title: String,
    playerVal: String,
    compVal: String,
    playerRating: ComponentRating,
    compRating: ComponentRating
) {
    val compResult = HardwareRatingHelper.compareScores(playerRating.score, compRating.score)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            // Advantage Badge
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = when (compResult) {
                    1 -> Color(0xFF10B981).copy(alpha = 0.15f)
                    -1 -> Color(0xFFEF4444).copy(alpha = 0.15f)
                    else -> Color(0xFF64748B).copy(alpha = 0.15f)
                }
            ) {
                Text(
                    text = when (compResult) {
                        1 -> "🟢 Siz Üstünsünüz"
                        -1 -> "🔴 Rakip Üstün"
                        else -> "🟡 Dengeli"
                    },
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (compResult) {
                        1 -> Color(0xFF059669)
                        -1 -> Color(0xFFDC2626)
                        else -> Color(0xFF475569)
                    },
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(3.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Player Column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = playerVal,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = playerRating.tier.badgeColor.copy(alpha = 0.15f),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = "⚡ ${playerRating.score} Puan • ${playerRating.tier.code}",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = playerRating.tier.badgeColor,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Competitor Column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = compVal,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = compRating.tier.badgeColor.copy(alpha = 0.15f),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = "⚡ ${compRating.score} Puan • ${compRating.tier.code}",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = compRating.tier.badgeColor,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun TechExpoMarketTabHeader(state: GameState) {
    val totalPlayerAwards = state.pastTechExpos.sumOf { it.playerWonCount }
    val monthsUntilNextExpo = 12 - state.month

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🏆", fontSize = 24.sp)
                    Column {
                        Text(
                            text = "Yıllık Teknoloji Fuarı & Ödülleri",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Global Tech Expo & MWC",
                            fontSize = 11.sp,
                            color = Slate400
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF0F172A)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("🎖️", fontSize = 12.sp)
                        Text(
                            text = "$totalPlayerAwards Ödül",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF59E0B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF0F172A).copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (monthsUntilNextExpo == 0) "🔥 Fuar Bu Ay Düzenleniyor!" else "⏳ Sıradaki Fuar için Kalan Süre:",
                        fontSize = 12.sp,
                        color = if (monthsUntilNextExpo == 0) Color(0xFFF59E0B) else Slate300,
                        fontWeight = if (monthsUntilNextExpo == 0) FontWeight.Bold else FontWeight.Normal
                    )
                    Text(
                        text = if (monthsUntilNextExpo == 0) "12. Ay (Aralık)" else "$monthsUntilNextExpo Ay Kaldı",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF60A5FA)
                    )
                }
            }
        }
    }
}

@Composable
fun PastTechExpoCard(
    expo: TechExpoEvent,
    playerBrandColorHex: Long
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "🌐 ${expo.expoName}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${expo.city} • ${expo.year}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (expo.playerWonCount > 0) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "🎉 ${expo.playerWonCount} Ödül Kazandınız!",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(10.dp))

            expo.awards.forEach { award ->
                val isPlayer = award.winner.isPlayer
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = if (isPlayer) Color(0xFF10B981).copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = if (isPlayer) BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f)) else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(award.category.iconEmoji, fontSize = 16.sp)
                            Column {
                                Text(
                                    text = award.category.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${award.winner.companyName} ${award.winner.modelName}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPlayer) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Text(
                            text = "${award.winner.score}/100",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
