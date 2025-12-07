package com.example.individualproject3.datamodels

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.individualproject3.UserModel

@Database(entities = [UserModel::class, GameSession::class], version = 2, exportSchema = false)
@TypeConverters(UserTypeConverter::class)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserModelDao
    abstract fun gameSessionDao(): GameSessionDao


    companion object {
        @Volatile
        private var Instance: UserDatabase? = null

        fun getDatabase(context: Context): UserDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, UserDatabase::class.java, "user_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }

    }

}


