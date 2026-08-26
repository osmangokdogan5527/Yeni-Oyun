package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_saves")
data class GameSaveEntity(
    @PrimaryKey val slotId: Int, // 0 = Otomatik Kayıt (AutoSave), 1 = Slot 1, 2 = Slot 2, 3 = Slot 3
    val slotName: String,
    val companyName: String,
    val year: Int,
    val month: Int,
    val budget: Long,
    val reputation: Int,
    val modelCount: Int,
    val lastSavedTimestamp: Long = System.currentTimeMillis(),
    val gameStateJson: String
)
