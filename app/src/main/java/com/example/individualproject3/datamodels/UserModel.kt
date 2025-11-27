package com.example.individualproject3
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.individualproject3.datamodels.UserTypeConverter

@Entity(tableName = "users")
@TypeConverters(UserTypeConverter::class) // Add this for the enum
data class UserModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var password: String = "",
    var username: String = "",
    var userType: UserType = UserType.KID, // Correctly typed to the enum

    //links kids to a parent. Null if the user is a parent
    var parentId: Int? = null
)

enum class UserType {
    PARENT,
    KID
}
