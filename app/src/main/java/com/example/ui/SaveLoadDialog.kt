package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.GameSaveEntity
import com.example.viewmodel.GameState

private const val TOTAL_SLOTS = 5

/**
 * Kayıt yuvalarını (0 = otomatik kayıt, 1-4 = manuel) listeleyen, yükleme/kaydetme/
 * silme işlemlerini ve yeni oyun başlatmayı sağlayan diyalog.
 */
@Composable
fun SaveLoadDialog(
    currentState: GameState,
    savedGames: List<GameSaveEntity>,
    onDismiss: () -> Unit,
    onSaveSlot: (slotId: Int, name: String) -> Unit,
    onLoadSlot: (slotId: Int) -> Unit,
    onDeleteSlot: (slotId: Int) -> Unit,
    onNewGame: () -> Unit
) {
    var confirmingNewGame by remember { mutableStateOf(false) }
    var namingSlot by remember { mutableStateOf<Int?>(null) }
    var slotNameInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Kayıt & Yükleme", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "Slot 0 otomatik kayıttır. Diğer slotlara manuel kayıt yapabilirsin.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier.heightIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items((0 until TOTAL_SLOTS).toList()) { slotId ->
                        val entity = savedGames.find { it.slotId == slotId }
                        SaveSlotRow(
                            slotId = slotId,
                            entity = entity,
                            onLoad = { onLoadSlot(slotId) },
                            onSave = {
                                if (slotId == 0) {
                                    onSaveSlot(slotId, "Otomatik Kayıt")
                                } else {
                                    slotNameInput = entity?.slotName ?: "Kayıt Slotu $slotId"
                                    namingSlot = slotId
                                }
                            },
                            onDelete = { onDeleteSlot(slotId) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                Spacer(modifier = Modifier.height(14.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = { confirmingNewGame = true }) {
                        Text("Yeni Oyun Başlat", color = MaterialTheme.colorScheme.error)
                    }
                    Button(onClick = onDismiss) { Text("Kapat") }
                }
            }
        }
    }

    namingSlot?.let { slotId ->
        AlertDialog(
            onDismissRequest = { namingSlot = null },
            title = { Text("Kayıt Adı") },
            text = {
                OutlinedTextField(
                    value = slotNameInput,
                    onValueChange = { if (it.length <= 24) slotNameInput = it },
                    singleLine = true,
                    label = { Text("Slot $slotId için isim") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onSaveSlot(slotId, slotNameInput.trim().ifEmpty { "Kayıt Slotu $slotId" })
                    namingSlot = null
                }) { Text("Kaydet") }
            },
            dismissButton = {
                TextButton(onClick = { namingSlot = null }) { Text("Vazgeç") }
            }
        )
    }

    if (confirmingNewGame) {
        AlertDialog(
            onDismissRequest = { confirmingNewGame = false },
            title = { Text("Yeni Oyuna Başla?") },
            text = { Text("Mevcut ilerlemen kaydedilmediyse kaybolur. Yeni bir şirketle sıfırdan başlamak istediğine emin misin?") },
            confirmButton = {
                TextButton(onClick = {
                    onNewGame()
                    confirmingNewGame = false
                    onDismiss()
                }) { Text("Evet, Yeni Oyun", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { confirmingNewGame = false }) { Text("Vazgeç") }
            }
        )
    }
}

@Composable
private fun SaveSlotRow(
    slotId: Int,
    entity: GameSaveEntity?,
    onLoad: () -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entity?.slotName ?: (if (slotId == 0) "Otomatik Kayıt (Boş)" else "Boş Slot $slotId"),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                if (entity != null) {
                    Text(
                        text = "${entity.companyName} • ${entity.year}/${entity.month} • $${"%,d".format(entity.budget)}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "Henüz kayıt yok",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row {
                IconButton(onClick = onSave) {
                    Icon(Icons.Default.Save, contentDescription = "Kaydet", tint = MaterialTheme.colorScheme.primary)
                }
                if (entity != null) {
                    IconButton(onClick = onLoad) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Yükle", tint = Color(0xFF10B981))
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Sil", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
