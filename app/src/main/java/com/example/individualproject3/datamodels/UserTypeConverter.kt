package com.example.individualproject3.datamodels
import androidx.room.TypeConverter
import com.example.individualproject3.UserType

class UserTypeConverter {

    // Converts UserType enum to String for database storage
    @TypeConverter
    fun fromUserType(value: UserType): String {
        return value.name
    }

    // Converts String back to UserType enum when retrieving from database
    @TypeConverter
    fun toUserType(value: String): UserType {
        return UserType.valueOf(value)
    }
}
