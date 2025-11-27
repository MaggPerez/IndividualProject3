package com.example.individualproject3.datamodels

import android.content.Context
import com.example.individualproject3.datamodels.GameSession
import com.example.individualproject3.datamodels.GameSessionDao

class GameRepository(
    private val gameSessionDao: GameSessionDao,
    private val context: Context
) {
    //initializing log manager
    private val logManager = LogManager(context)

    suspend fun getSessionsForKid(kidId: Int): List<GameSession> {
        return gameSessionDao.getSessionsForKid(kidId)
    }

    suspend fun insertSession(session: GameSession, username: String) {
        //saves to database
        gameSessionDao.insertSession(session)

        //log to file
        logManager.logGameSession(
            username = username,
            level = session.level,
            gameNumber = session.gameNumber,
            score = session.score,
            success = session.success,
            attempts = session.attempts
        )
    }


    /**
     * //get logs for a specific user
     */
    fun getLogsForUser(username: String): List<String> {
        return logManager.getLogsForUser(username)
    }


}
