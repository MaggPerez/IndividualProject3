package com.example.individualproject3.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.individualproject3.UserModel
import com.example.individualproject3.UserType
import com.example.individualproject3.datamodels.GameRepository
import com.example.individualproject3.datamodels.UserDatabase
import com.example.individualproject3.datamodels.UserRepository
import kotlinx.coroutines.launch

class LoginRegistrationViewModel(private val context: Context): ViewModel() {
    //login
    var username by mutableStateOf("")
    var password by mutableStateOf("")

    //password
    var createFirstName by mutableStateOf("")
    var createLastName by mutableStateOf("")
    var createUsername by mutableStateOf("")
    var createEmail by mutableStateOf("")
    var createPassword by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var userType by mutableStateOf(UserType.KID)


    //database instances
    private val database = UserDatabase.getDatabase(context)
    private val gameRepository = GameRepository(
        database.gameSessionDao(),
        context
    )

    private val userRepository = UserRepository(
        database.userDao(),
        context
    )


    /**
     * function that handles user type (kid or parent)
     */
    fun onUserTypeSelected(selectedType: UserType) {
        userType = selectedType
    }


    fun onHandleLogin(){
        //logic for login

    }


    /**
     * function that handles registration
     */
    fun onHandleRegistration(){
        //logic for registration
        if(createFirstName.isBlank() && createLastName.isBlank() &&
            createUsername.isBlank() && createEmail.isBlank() &&
            createPassword.isBlank() && confirmPassword.isBlank()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if(createPassword != confirmPassword){
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        //creating user object/model
        val newUser = UserModel(
            firstName = createFirstName,
            lastName = createLastName,
            username = createUsername,
            email = createEmail,
            password = createPassword,
            userType = userType

        )


        viewModelScope.launch {
            userRepository.registerUser(newUser)

            Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()

        }


    }


}