package com.example.individualproject3.datamodels
import android.content.Context
import com.example.individualproject3.UserModel
import com.example.individualproject3.UserType
import com.example.individualproject3.datamodels.UserModelDao
import java.util.UUID

class UserRepository(
    private val userDao: UserModelDao,
    private val context: Context
) {

    //initializing log manager
    private val logManager = LogManager(context)


    /**
     * Inserts a new user into the database.
     */
    suspend fun insertUser(user: UserModel) {
        userDao.insert(user)

        //log the registration
        logManager.logUserRegistration(
            username = user.username,
            userType = user.userType.name
        )
    }


    /**
     * Registers a new user.
     */
    suspend fun registerUser(user: UserModel) {
        userDao.insert(user)

        //log the registration
        logManager.logUserRegistration(
            username = user.username,
            userType = user.userType.name
        )
    }


    /**
     * Retrieves a user by their username.
     */
    suspend fun getUserByUsername(username: String): UserModel? {
        val user = userDao.getUserByUsername(username)

        //log successful login
        if (user != null) {
            logManager.logUserLogin(username)
        }

        return user
    }


    /**
     * Retrieves a user by their ID.
     */
    suspend fun getUserById(id: Int): UserModel {
        return userDao.getUserById(id)
    }


    /**
     * Retrieves all kids from the database.
     */
    suspend fun getAllKids(): List<UserModel> {
        return userDao.getAllKids()
    }



    // Generate a unique 6-character invite code for a parent
    fun generateInviteCode(): String {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 6).uppercase()
    }

    // Get or generate invite code for a parent
    suspend fun getOrGenerateInviteCode(parentUser: UserModel): String {
        if (parentUser.userType != UserType.PARENT) {
            throw IllegalArgumentException("Only parents can have invite codes")
        }

        if (parentUser.inviteCode != null) {
            return parentUser.inviteCode!!
        }

        // Generate new invite code
        val inviteCode = generateInviteCode()
        parentUser.inviteCode = inviteCode
        userDao.update(parentUser)

        return inviteCode
    }

    // Link a child to a parent using the invite code
    suspend fun linkChildToParent(childUser: UserModel, inviteCode: String): Boolean {
        if (childUser.userType != UserType.KID) {
            throw IllegalArgumentException("Only kids can be linked to parents")
        }

        val parent = userDao.getParentByInviteCode(inviteCode)

        if (parent != null) {
            childUser.parentId = parent.id
            userDao.update(childUser)

            logManager.logMessage("Child ${childUser.username} linked to parent ${parent.username}")
            return true
        }

        return false
    }

    // Get all children for a specific parent
    suspend fun getKidsForParent(parentId: Int): List<UserModel> {
        return userDao.getKidsForParent(parentId)
    }

    // Get parent by invite code
    suspend fun getParentByInviteCode(inviteCode: String): UserModel? {
        return userDao.getParentByInviteCode(inviteCode)
    }
}
