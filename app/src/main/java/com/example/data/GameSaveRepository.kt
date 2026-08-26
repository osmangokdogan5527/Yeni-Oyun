package com.example.data

import kotlinx.coroutines.flow.Flow

class GameSaveRepository(private val dao: GameSaveDao) {
    val allSaves: Flow<List<GameSaveEntity>> = dao.getAllSaves()

    suspend fun getSave(slotId: Int): GameSaveEntity? = dao.getSaveBySlot(slotId)

    suspend fun saveGame(save: GameSaveEntity) = dao.insertSave(save)

    suspend fun deleteSave(slotId: Int) = dao.deleteSaveBySlot(slotId)

    suspend fun clearAll() = dao.deleteAllSaves()
}
