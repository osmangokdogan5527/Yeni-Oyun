package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Bir kayıt yuvasını (slot) temsil eden Room tablosu satırı. Slot 0, otomatik
 * kayıt (autosave) için ayrılmıştır; diğer slotlar manuel kayıtlar içindir.
 * [gameStateJson], tüm [com.example.viewmodel.GameState]'in serileştirilmiş halidir.
 */
@Entity(tableName = "game_saves")
data class GameSaveEntity(
    @PrimaryKey val slotId: Int,
    val slotName: String,
    val companyName: String,
    val year: Int,
    val month: Int,
    val budget: Long,
    val reputation: Int,
    val modelCount: Int,
    val lastSavedTimestamp: Long,
    val gameStateJson: String
)
