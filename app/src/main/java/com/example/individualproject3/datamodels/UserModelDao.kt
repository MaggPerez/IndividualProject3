package com.example.individualproject3.datamodels


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.individualproject3.UserModel

@Dao
interface UserModelDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: UserModel)

    @Update
    suspend fun update(user: UserModel)

    @Delete
    suspend fun delete(user: UserModel)

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserModel>


    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Int): UserModel


    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): UserModel?


    @Query("SELECT * FROM users WHERE userType = 'kid'")
    suspend fun getAllKids(): List<UserModel>


    @Query("SELECT * FROM users WHERE parentId = :parentId")
    suspend fun getKidsForParent(parentId: Int): List<UserModel>

    @Query("SELECT * FROM users WHERE inviteCode = :inviteCode AND userType = 'PARENT'")
    suspend fun getParentByInviteCode(inviteCode: String): UserModel?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserModel?

}