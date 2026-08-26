package com.example.data

import kotlinx.coroutines.flow.Flow

/**
 * [GameSaveDao] üzerine ince bir soyutlama katmanı. [GameViewModel] doğrudan
 * Room DAO'suyla değil, bu repository ile konuşur.
 */
class GameSaveRepository(private val dao: GameSaveDao) {

    val allSaves: Flow<List<GameSaveEntity>> = dao.getAllSaves()

    suspend fun getSave(slotId: Int): GameSaveEntity? = dao.getSaveById(slotId)

    suspend fun saveGame(entity: GameSaveEntity) = dao.upsert(entity)

    suspend fun deleteSave(slotId: Int) = dao.deleteBySlotId(slotId)
}
