package com.example

import com.example.ui.ProIconButton
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import com.example.ui.Button3D
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.viewmodel.Achievement
import com.example.viewmodel.AchievementTier
import com.example.viewmodel.ALL_ACHIEVEMENTS

/**
 * Başarımlar (Achievements) tam ekran diyaloğu. Kilitli/açık tüm rozetleri
 * seviyelerine (Bronz/Gümüş/Altın/Platin) göre gruplayarak listeler.
 */
@Composable
fun AchievementsScreen(
    unlockedIds: List<String>,
    onDismiss: () -> Unit
) {
    val unlockedSet = unlockedIds.toSet()
    val grouped = ALL_ACHIEVEMENTS.groupBy { it.tier }
    val tierOrder = listOf(AchievementTier.PLATINUM, AchievementTier.GOLD, AchievementTier.SILVER, AchievementTier.BRONZE)

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0F172A)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFFACC15))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Başarımlar", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Text(
                            text = "${unlockedSet.size} / ${ALL_ACHIEVEMENTS.size} rozet açıldı",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                    ProIconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color(0xFF94A3B8))
                    }
                }

                // Progress bar
                val progress = if (ALL_ACHIEVEMENTS.isNotEmpty()) unlockedSet.size / ALL_ACHIEVEMENTS.size.toFloat() else 0f
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF38BDF8),
                    trackColor = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    tierOrder.forEach { tier ->
                        val achievements = grouped[tier].orEmpty()
                        if (achievements.isNotEmpty()) {
                            item {
                                Text(
                                    text = "${tier.displayName.uppercase()} RÜTBESİ",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(tier.colorHex),
                                    modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
                                )
                            }
                            items(achievements) { ach ->
                                AchievementRow(achievement = ach, isUnlocked = ach.id in unlockedSet)
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(12.dp)) }
                }
            }
        }
    }
}

@Composable
private fun AchievementRow(achievement: Achievement, isUnlocked: Boolean) {
    val tierColor = Color(achievement.tier.colorHex)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isUnlocked) Color(0xFF1E293B) else Color(0xFF1E293B).copy(alpha = 0.35f),
                RoundedCornerShape(14.dp)
            )
            .border(
                width = 1.dp,
                color = if (isUnlocked) tierColor.copy(alpha = 0.6f) else Color(0xFF334155),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (isUnlocked) tierColor.copy(alpha = 0.18f) else Color(0xFF0F172A)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isUnlocked) achievement.icon else "🔒",
                fontSize = 18.sp,
                modifier = Modifier.alpha(if (isUnlocked) 1f else 0.6f)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = achievement.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isUnlocked) Color.White else Color(0xFF64748B)
            )
            Text(
                text = achievement.description,
                fontSize = 11.sp,
                color = if (isUnlocked) Color(0xFF94A3B8) else Color(0xFF475569),
                maxLines = 2
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isUnlocked) tierColor.copy(alpha = 0.16f) else Color(0xFF0F172A)
        ) {
            Text(
                text = achievement.tier.displayName,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (isUnlocked) tierColor else Color(0xFF475569),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

/**
 * Yeni başarım açıldığında dashboard üstünde beliren küçük kutlama kartı.
 */
@Composable
fun AchievementUnlockedBanner(achievements: List<Achievement>, onDismiss: () -> Unit) {
    if (achievements.isEmpty()) return
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFFFACC15).copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🏅", fontSize = 40.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (achievements.size == 1) "Yeni Başarım!" else "${achievements.size} Yeni Başarım!",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(12.dp))
                achievements.forEach { ach ->
                    Text(
                        text = "${ach.icon} ${ach.title}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(ach.tier.colorHex),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button3D(onClick = onDismiss, shape = RoundedCornerShape(12.dp)) {
                    Text("Harika!")
                }
            }
        }
    }
}
