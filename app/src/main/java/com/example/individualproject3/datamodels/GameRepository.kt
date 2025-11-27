package com.example.individualproject3.datamodels

import GameSession

class GameRepository(private val gameSessionDao: GameSessionDao) {

    suspend fun getSessionsForKid(kidId: Int): List<GameSession> {
        return gameSessionDao.getSessionsForKid(kidId)
    }

    suspend fun insertSession(session: GameSession) {
        gameSessionDao.insertSession(session)
    }
}
