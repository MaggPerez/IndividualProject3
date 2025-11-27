package com.example.individualproject3.datamodels

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.individualproject3.UserModel

@Entity(
    tableName = "game_sessions",
    foreignKeys = [
        ForeignKey(entity = UserModel::class,
            parentColumns = ["id"],
            childColumns = ["kidId"],
            //if user is deleted, their sessions are too
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class GameSession(
    @PrimaryKey(autoGenerate = true)
    var sessionId: Int = 0,
    var kidId: Int = 0, //foreign key to UserModel
    var level: Int = 0, //e.g., Level 1, 2, 3, 4
    var gameNumber: Int = 0, //e.g., Game 1, 2, 3 within the level
    var score: Int = 0,
    var success: Boolean = false, //To handle "success and reattempt"
    var attempts: Int = 1,
    var timestamp: Long = System.currentTimeMillis() // To track when it was played
)
