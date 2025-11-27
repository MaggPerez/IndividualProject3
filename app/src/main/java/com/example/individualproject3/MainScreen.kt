package com.example.individualproject3

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.individualproject3.ui.theme.IndividualProject3Theme
import com.example.individualproject3.viewmodels.LoginRegistrationViewModel

@Composable
fun MainScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: LoginRegistrationViewModel = viewModel()
) {
    Column() {
        Text(text = "Hello")
    }
}


@Preview (showBackground = true)
@Composable
fun MainScreenPreview(){
    IndividualProject3Theme {
        MainScreen(navController = rememberNavController())
    }
}
