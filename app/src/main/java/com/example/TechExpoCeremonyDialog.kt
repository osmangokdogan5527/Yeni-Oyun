package com.example

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.viewmodel.AwardCategory
import com.example.viewmodel.AwardNominee
import com.example.viewmodel.AwardResult
import com.example.viewmodel.GameState
import com.example.viewmodel.TechExpoEvent

@Composable
fun TechExpoCeremonyDialog(
    event: TechExpoEvent,
    playerBrandColorHex: Long,
    onDismiss: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }
    val totalAwards = event.awards.size

    Dialog(
        onDismissRequest = { /* Modal must be interacted with */ },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF090D16)),
            color = Color(0xFF090D16)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Banner
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF1E293B),
                    border = BorderStroke(1.5.dp, Color(0xFFF59E0B).copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🌐 ${event.expoName.uppercase()}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFF59E0B),
                            letterSpacing = 1.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "🏆 Yıllık Küresel Teknoloji Fuarı & Büyük Ödülleri",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "📍 ${event.city} • ${event.year} Yılı Sektörün En İyileri",
                            fontSize = 12.sp,
                            color = Slate400,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stepper Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until totalAwards) {
                        val isActive = i == currentStep
                        val isPassed = i < currentStep
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    when {
                                        isActive -> Color(0xFFF59E0B)
                                        isPassed -> Color(0xFF10B981)
                                        else -> Color(0xFF334155)
                                    }
                                )
                        )
                        if (i < totalAwards - 1) {
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Active Award Ceremony Screen
                if (currentStep < totalAwards) {
                    val currentAward = event.awards[currentStep]
                    AwardPresentationCard(
                        award = currentAward,
                        stepNumber = currentStep + 1,
                        totalSteps = totalAwards,
                        playerBrandColorHex = playerBrandColorHex,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Action Button
                Button(
                    onClick = {
                        if (currentStep < totalAwards - 1) {
                            currentStep++
                        } else {
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentStep < totalAwards - 1) Color(0xFFF59E0B) else Color(0xFF10B981)
                    )
                ) {
                    Text(
                        text = if (currentStep < totalAwards - 1) "Sıradaki Ödüle Geç ⏩ (${currentStep + 1}/$totalAwards)" else "Ödül Törenini Tamamla 🎖️",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun AwardPresentationCard(
    award: AwardResult,
    stepNumber: Int,
    totalSteps: Int,
    playerBrandColorHex: Long,
    modifier: Modifier = Modifier
) {
    val isPlayerWinner = award.winner.isPlayer

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131C2E)),
        border = BorderStroke(
            1.5.dp,
            if (isPlayerWinner) Color(0xFF10B981) else Color(0xFF334155)
        )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Category Header
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "KATEGORİ $stepNumber / $totalSteps",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF59E0B),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${award.category.iconEmoji} ${award.category.title}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = award.category.description,
                        fontSize = 12.sp,
                        color = Slate400,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Winner Stage
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = if (isPlayerWinner) Color(0xFF064E3B).copy(alpha = 0.6f) else Color(0xFF1E293B),
                    border = BorderStroke(
                        2.dp,
                        if (isPlayerWinner) Color(0xFF34D399) else Color(0xFFF59E0B)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🥇", fontSize = 24.sp)
                            Text(
                                text = "KAZANAN: ${award.winner.modelName.uppercase()}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isPlayerWinner) Color(0xFF34D399) else Color(0xFFF59E0B)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            BrandLogo(
                                companyName = award.winner.companyName,
                                isPlayer = award.winner.isPlayer,
                                playerBrandColorHex = playerBrandColorHex,
                                size = 42.dp
                            )
                            Column {
                                Text(
                                    text = award.winner.companyName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Değerlendirme Puanı: ${award.winner.score}/100 • $${award.winner.price}",
                                    fontSize = 12.sp,
                                    color = Slate400
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "\"${award.winner.highlightText}\"",
                            fontSize = 12.sp,
                            color = Slate200,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0F172A), RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        )

                        if (isPlayerWinner) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                color = Color(0xFF10B981).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("🎉", fontSize = 16.sp)
                                    Text(
                                        text = "+${award.category.reputationBonus} İtibar Puanı & $${"%,d".format(award.category.prizeMoney)} Para Ödülü Hesabınıza Eklendi!",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF34D399)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Other Nominees
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Diğer Finalist Adaylar:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate400,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    award.nominees.filter { it != award.winner }.forEach { nominee ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1A2333)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    BrandLogo(
                                        companyName = nominee.companyName,
                                        isPlayer = nominee.isPlayer,
                                        playerBrandColorHex = playerBrandColorHex,
                                        size = 28.dp
                                    )
                                    Column {
                                        Text(
                                            text = nominee.modelName,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (nominee.isPlayer) Color(0xFF60A5FA) else Color.White
                                        )
                                        Text(
                                            text = nominee.companyName,
                                            fontSize = 11.sp,
                                            color = Slate400
                                        )
                                    }
                                }

                                Text(
                                    text = "${nominee.score}/100",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF59E0B)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
