package com.example.individualproject3.datamodels
import androidx.room.TypeConverter
import com.example.individualproject3.UserType

class UserTypeConverter {
    @TypeConverter
    fun fromUserType(value: UserType): String {
        return value.name
    }

    @TypeConverter
    fun toUserType(value: String): UserType {
        return UserType.valueOf(value)
    }
}
