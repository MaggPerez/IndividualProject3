package com.example.individualproject3.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
 * Factory for creating GameViewModel with repository dependencies
 */
class GameViewModelFactory(
    private val gameRepository: GameRepository,
    private val username: String,
    private val userId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(gameRepository, username, userId) as T
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