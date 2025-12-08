package com.example.individualproject3
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.individualproject3.datamodels.UserTypeConverter


/**
 * Data model representing a user in the application.
 *
 * @property id Unique identifier for the user (auto-generated).
 * @property firstName User's first name.
 * @property lastName User's last name.
 * @property email User's email address.
 * @property password User's password.
 * @property username User's chosen username.
 * @property userType Type of user (PARENT or KID).
 * @property parentId ID of the parent user if this user is a KID; null if the user is a PARENT.
 * @property inviteCode Invite code for parents to share with their children; null for KIDs.
 */
@Entity(tableName = "users")
@TypeConverters(UserTypeConverter::class)
data class UserModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var password: String = "",
    var username: String = "",
    var userType: UserType = UserType.KID,
    var parentId: Int? = null,
    var inviteCode: String? = null
)

/**
 * Enum representing the type of user (parent or kid).
 */
enum class UserType {
    PARENT,
    KID
}
