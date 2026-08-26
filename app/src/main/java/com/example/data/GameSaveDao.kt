package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameSaveDao {
    @Query("SELECT * FROM game_saves ORDER BY slotId ASC")
    fun getAllSaves(): Flow<List<GameSaveEntity>>

    @Query("SELECT * FROM game_saves WHERE slotId = :slotId LIMIT 1")
    suspend fun getSaveBySlot(slotId: Int): GameSaveEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSave(save: GameSaveEntity)

    @Query("DELETE FROM game_saves WHERE slotId = :slotId")
    suspend fun deleteSaveBySlot(slotId: Int)

    @Query("DELETE FROM game_saves")
    suspend fun deleteAllSaves()
}
