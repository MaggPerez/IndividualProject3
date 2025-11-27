package com.example.individualproject3

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.individualproject3.ui.theme.IndividualProject3Theme
import com.example.individualproject3.viewmodels.LoginRegistrationViewModel

@Composable
fun MainScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
            Image(
                painter = painterResource(id = R.drawable.robot),
                contentDescription = "Logo",
                modifier = Modifier.size(200.dp) // Control the image size
            )


        Text(
            text = "PuzzleBot",
            style = MaterialTheme.typography.headlineLarge
        )

        OutlinedButton(
            onClick = { navController.navigate("login_screen") }
        ) {
            Text(
                text = "Login"
            )
        }

        OutlinedButton(
            onClick = { navController.navigate("register_screen") }
        ) {
            Text(
                text = "Register"
            )
        }
    }
}




@Preview (showBackground = true)
@Composable
fun MainScreenPreview(){
    IndividualProject3Theme(){
        MainScreen(navController = rememberNavController())
    }
}
