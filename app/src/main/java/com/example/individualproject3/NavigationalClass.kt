package com.example.individualproject3

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.individualproject3.datamodels.GameRepository
import com.example.individualproject3.datamodels.UserDatabase
import com.example.individualproject3.datamodels.UserRepository
import com.example.individualproject3.viewmodels.LoginRegistrationViewModel
import com.example.individualproject3.viewmodels.LoginRegistrationViewModelFactory

@Composable
fun Navigation(){
    val navController = rememberNavController()
    val context = LocalContext.current

    // Create a shared ViewModel instance at the NavHost level
    val sharedViewModel: LoginRegistrationViewModel = viewModel(
        factory = LoginRegistrationViewModelFactory(
            userRepository = UserRepository(
                userDao = UserDatabase.getDatabase(context).userDao(),
                context = context
            ),
            gameRepository = GameRepository(
                gameSessionDao = UserDatabase.getDatabase(context).gameSessionDao(),
                context = context
            )
        )
    )

    NavHost(navController = navController, startDestination = "main_screen"){
        composable("main_screen"){
            MainScreen(navController)
        }

        composable("login_screen") {
            LoginScreen(navController, viewModel = sharedViewModel)
        }

        composable("register_screen") {
            RegisterScreen(navController, viewModel = sharedViewModel)
        }

        composable("dashboard_screen") {
            DashboardScreen(navController, viewModel = sharedViewModel)
        }

        composable("parent_dashboard_screen") {
            ParentDashboardScreen(navController)
        }

        composable(
            route = "level_screen/{level}",
            arguments = listOf(navArgument("level") { type = NavType.IntType })
        ) { backStackEntry ->
            val level = backStackEntry.arguments?.getInt("level") ?: 1
            GameScreen(
                level = level,
                navController = navController,
                username = sharedViewModel.loggedInUsername,
                userId = sharedViewModel.loggedInUserId
            )
        }

    }
}