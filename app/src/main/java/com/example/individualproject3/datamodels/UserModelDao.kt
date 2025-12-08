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

    // Inserts a new user into the database. If a user with the same primary key exists, the insertion is ignored.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: UserModel)


    //updates an existing user in the database.
    @Update
    suspend fun update(user: UserModel)


    // Deletes a user from the database.
    @Delete
    suspend fun delete(user: UserModel)


    // Retrieves all users from the database.
    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserModel>



    // Retrieves a user by their unique ID.
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Int): UserModel



    // Retrieves a user by their username.
    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): UserModel?


    // Retrieves all users who are of type 'kid'.
    @Query("SELECT * FROM users WHERE userType = 'kid'")
    suspend fun getAllKids(): List<UserModel>



    // Retrieves all kids associated with a specific parent ID.
    @Query("SELECT * FROM users WHERE parentId = :parentId")
    suspend fun getKidsForParent(parentId: Int): List<UserModel>


    // Retrieves a parent user by their invite code.
    @Query("SELECT * FROM users WHERE inviteCode = :inviteCode AND userType = 'PARENT'")
    suspend fun getParentByInviteCode(inviteCode: String): UserModel?


    // Retrieves a user by their email address.
    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserModel?

}