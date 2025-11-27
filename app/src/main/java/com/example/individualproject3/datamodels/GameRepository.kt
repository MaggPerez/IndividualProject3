package com.example.individualproject3.datamodels

import com.example.individualproject3.datamodels.GameSession
import com.example.individualproject3.datamodels.GameSessionDao

class GameRepository(private val gameSessionDao: GameSessionDao) {

    suspend fun getSessionsForKid(kidId: Int): List<GameSession> {
        return gameSessionDao.getSessionsForKid(kidId)
    }

    suspend fun insertSession(session: GameSession) {
        gameSessionDao.insertSession(session)
    }
}
