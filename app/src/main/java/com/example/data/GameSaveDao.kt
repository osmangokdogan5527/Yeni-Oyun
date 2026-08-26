package com.example.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface GameSaveDao {

    @Query("SELECT * FROM game_saves ORDER BY slotId ASC")
    fun getAllSaves(): Flow<List<GameSaveEntity>>

    @Query("SELECT * FROM game_saves WHERE slotId = :slotId LIMIT 1")
    suspend fun getSaveById(slotId: Int): GameSaveEntity?

    @Upsert
    suspend fun upsert(save: GameSaveEntity)

    @Query("DELETE FROM game_saves WHERE slotId = :slotId")
    suspend fun deleteBySlotId(slotId: Int)
}
