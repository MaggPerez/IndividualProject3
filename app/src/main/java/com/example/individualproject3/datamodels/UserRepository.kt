package com.example.individualproject3.datamodels
import android.content.Context
import com.example.individualproject3.UserModel
import com.example.individualproject3.datamodels.UserModelDao

class UserRepository(
    private val userDao: UserModelDao,
    private val context: Context
) {

    private val logManager = LogManager(context)


    suspend fun insertUser(user: UserModel) {
        userDao.insert(user)

        //log the registration
        logManager.logUserRegistration(
            username = user.username,
            userType = user.userType.name
        )
    }

    suspend fun getUserByUsername(username: String): UserModel? {
        val user = userDao.getUserByUsername(username)

        //log successful login
        if (user != null) {
            logManager.logUserLogin(username)
        }

        return user
    }

    suspend fun getAllKids(): List<UserModel> {
        return userDao.getAllKids()
    }
}
