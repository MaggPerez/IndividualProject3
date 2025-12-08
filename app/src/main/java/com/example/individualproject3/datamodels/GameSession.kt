package com.example.individualproject3.datamodels

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.individualproject3.UserModel


/**
 * Entity representing a game session played by a kid.
 */
@Entity(
    tableName = "game_sessions",
    foreignKeys = [
        ForeignKey(entity = UserModel::class,
            parentColumns = ["id"],
            childColumns = ["kidId"],
            //if user is deleted, their sessions are too
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["kidId"])]
)

/**
 * Data model representing a game session played by a kid.
 * @property sessionId Unique identifier for the game session (auto-generated).
 * @property kidId Identifier of the kid who played the session.
 * @property level Level of the game played.
 * @property gameNumber Specific game number within the level.
 * @property score Score achieved in the game session.
 * @property success Boolean indicating if the game session was successful.
 * @property attempts Number of attempts taken to complete the game session.
 * @property timestamp Timestamp of when the game session was played.
 */
data class GameSession(
    @PrimaryKey(autoGenerate = true)
    var sessionId: Int = 0,
    var kidId: Int = 0,
    var level: Int = 0,
    var gameNumber: Int = 0,
    var score: Int = 0,
    var success: Boolean = false,
    var attempts: Int = 1,
    var timestamp: Long = System.currentTimeMillis()
)
