package com.example.individualproject3.datamodels
import com.example.individualproject3.UserModel
import com.example.individualproject3.datamodels.UserModelDao

class UserRepository(private val userDao: UserModelDao) {

    suspend fun insertUser(user: UserModel) {
        userDao.insert(user)
    }

    suspend fun getUserByUsername(username: String): UserModel? {
        return userDao.getUserByUsername(username)
    }

    suspend fun getAllKids(): List<UserModel> {
        return userDao.getAllKids()
    }
}
