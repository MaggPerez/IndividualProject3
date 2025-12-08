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

/**
 * Navigation composable to manage app navigation using NavHost
 */
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

    /**
     * NavHost to manage navigation between different screens
     */
    NavHost(navController = navController, startDestination = "main_screen"){

        // main screen composable
        composable("main_screen"){
            MainScreen(navController)
        }


        // login screen composable
        composable("login_screen") {
            LoginScreen(navController, viewModel = sharedViewModel)
        }


        // registration screen composable
        composable("register_screen") {
            RegisterScreen(navController, viewModel = sharedViewModel)
        }


        // dashboard screen composable
        composable("dashboard_screen") {
            DashboardScreen(navController, viewModel = sharedViewModel)
        }


        // parent dashboard screen composable
        composable("parent_dashboard_screen") {
            ParentDashboardScreen(navController, viewModel = sharedViewModel)
        }


        // child performance detail screen composable
        composable(
            // route with arguments for child username and ID
            route = "child_performance/{childUsername}/{childId}",
            arguments = listOf(
                navArgument("childUsername") { type = NavType.StringType },
                navArgument("childId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            // retrieve arguments from backStackEntry
            val childUsername = backStackEntry.arguments?.getString("childUsername") ?: ""
            val childId = backStackEntry.arguments?.getInt("childId") ?: 0

            // navigate to ChildPerformanceDetailScreen with arguments
            ChildPerformanceDetailScreen(
                navController = navController,
                childUsername = childUsername,
                childId = childId,
                viewModel = sharedViewModel
            )
        }


        // game screen composable
        composable(
            // route with argument for level
            route = "level_screen/{level}",
            arguments = listOf(navArgument("level") { type = NavType.IntType })
        ) { backStackEntry ->

            // retrieve level argument from backStackEntry
            val level = backStackEntry.arguments?.getInt("level") ?: 1

            // navigate to GameScreen with level argument
            GameScreen(
                level = level,
                navController = navController,
                username = sharedViewModel.loggedInUsername,
                userId = sharedViewModel.loggedInUserId
            )
        }

    }
}