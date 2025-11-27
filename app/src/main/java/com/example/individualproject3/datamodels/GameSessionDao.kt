package com.example.individualproject3.datamodels

import GameSession
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GameSessionDao {

    @Insert
    suspend fun insertSession(session: GameSession)

    //this is the key function for the parent's report
    @Query("SELECT * FROM game_sessions WHERE kidId = :kidId ORDER BY timestamp DESC")
    suspend fun getSessionsForKid(kidId: Int): List<GameSession>
}