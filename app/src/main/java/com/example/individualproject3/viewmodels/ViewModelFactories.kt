package com.example.individualproject3.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.individualproject3.datamodels.GameRepository
import com.example.individualproject3.datamodels.UserRepository

/**
 * Factory for creating LoginRegistrationViewModel with repository dependencies
 */
class LoginRegistrationViewModelFactory(
    private val userRepository: UserRepository,
    private val gameRepository: GameRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginRegistrationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginRegistrationViewModel(userRepository, gameRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

/**
 * Factory for creating GameViewModel with repository dependencies and SavedStateHandle
 */
class GameViewModelFactory(
    private val gameRepository: GameRepository,
    private val username: String,
    private val userId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            val savedStateHandle = extras.createSavedStateHandle()
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(gameRepository, username, userId, savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

//ViewModelFactories templates
// class AnotherViewModelFactory(
//     private val application: Application
// ) : ViewModelProvider.Factory {
//     override fun <T : ViewModel> create(modelClass: Class<T>): T {
//         if (modelClass.isAssignableFrom(AnotherViewModel::class.java)) {
//             @Suppress("UNCHECKED_CAST")
//             return AnotherViewModel(application) as T
//         }
//         throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
//     }
// }