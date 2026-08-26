package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.viewmodel.AwardResult
import com.example.viewmodel.TechExpoEvent

/**
 * Yıl sonunda (her Aralık) açılan büyük ödül töreni modalı. [GameViewModel]'in
 * ürettiği [TechExpoEvent]'i, kategori kategori ödül sonuçlarıyla gösterir.
 */
@Composable
fun TechExpoCeremonyDialog(
    event: TechExpoEvent,
    playerBrandColorHex: Long,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("🏆", fontSize = 34.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = event.expoName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "${event.city} • ${event.year}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (event.playerWonCount > 0) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.15f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "🎉 ${event.playerWonCount} Ödül Kazandınız!",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981),
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Ödül Havuzu: $${"%,d".format(event.totalPrizeWon)} • İtibar: +${event.reputationGained}",
                                fontSize = 12.sp,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                LazyColumn(
                    modifier = Modifier.heightIn(max = 360.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(event.awards) { award ->
                        AwardResultRow(award)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Devam Et")
                }
            }
        }
    }
}

@Composable
private fun AwardResultRow(award: AwardResult) {
    val isPlayer = award.winner.isPlayer
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isPlayer) Color(0xFF10B981).copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(award.category.iconEmoji, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(award.category.title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${award.winner.companyName} — ${award.winner.modelName}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isPlayer) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = award.ceremonyReview,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
