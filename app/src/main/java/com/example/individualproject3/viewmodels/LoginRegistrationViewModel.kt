package com.example.individualproject3.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.individualproject3.UserModel
import com.example.individualproject3.UserType
import com.example.individualproject3.datamodels.GameRepository
import com.example.individualproject3.datamodels.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * sealed class for UI events that need to be handled by the composable
 */
sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
    data object NavigateToMain : UiEvent()
    data object NavigateToDashboard : UiEvent()
}

class LoginRegistrationViewModel(
    private val userRepository: UserRepository,
    private val gameRepository: GameRepository
): ViewModel() {


    //login
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var passwordVisible by mutableStateOf(false)
        private set

    //store logged-in user's username for dashboard display
    var loggedInUsername by mutableStateOf("")
        private set

    //registration
    var createFirstName by mutableStateOf("")
    var createLastName by mutableStateOf("")
    var createUsername by mutableStateOf("")
    var createEmail by mutableStateOf("")
    var createPassword by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var userType by mutableStateOf(UserType.KID)


    //event flow for one-time UI events like Toast messages
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()


    /**
     * function that handles user type (kid or parent)
     */
    fun onUserTypeSelected(selectedType: UserType) {
        userType = selectedType
    }


    fun onHandleLogin(): Unit {
        //logic for login
        if(username.isBlank() && password.isBlank()) {
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.ShowToast("Please enter username and password"))
            }
            return
        }

        viewModelScope.launch {
            val user = userRepository.getUserByUsername(username)
            if(user == null || user.password != password) {
                _uiEvent.emit(UiEvent.ShowToast("Invalid username or password"))
            } else {

                //store the logged-in user's username in ViewModel
                loggedInUsername = user.username

                //navigate to dashboard
                _uiEvent.emit(UiEvent.NavigateToDashboard)
            }
        }
    }

    fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
    }


    /**
     * function that handles registration
     */
    fun onHandleRegistration(): Boolean {
        //logic for registration
        if(createFirstName.isBlank() && createLastName.isBlank() &&
            createUsername.isBlank() && createEmail.isBlank() &&
            createPassword.isBlank() && confirmPassword.isBlank()) {
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.ShowToast("Please fill in all fields"))
            }
            return false
        }

        if(createPassword != confirmPassword){
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.ShowToast("Passwords do not match"))
            }
            return false
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
            _uiEvent.emit(UiEvent.ShowToast("Registration successful"))

            //store the new user's username in ViewModel
            loggedInUsername = createUsername

            //navigate to dashboard
            _uiEvent.emit(UiEvent.NavigateToDashboard)
        }

        return true


    }


}