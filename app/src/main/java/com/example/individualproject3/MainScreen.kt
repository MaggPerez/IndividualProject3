package com.example.individualproject3

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.individualproject3.ui.theme.*

@Composable
fun MainScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    PuzzleBotAnimatedBackground(modifier = modifier) {
        // Add floating stars
        FloatingStars()

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
        ) {
            // Robot Mascot with Bounce Animation
            PuzzleBotMascotContainer(size = 240.dp, withBounce = true) {
                Image(
                    painter = painterResource(id = R.drawable.robot),
                    contentDescription = "PuzzleBot Mascot",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Game Title
            PuzzleBotTitle(text = "PuzzleBot")

            Spacer(modifier = Modifier.height(12.dp))

            // Tagline
            PuzzleBotSubtitle(text = "🌟 Learn to Code Through Play! 🚀")

            Spacer(modifier = Modifier.height(56.dp))

            // Login Button
            PuzzleBotPrimaryButton(
                text = "Start Playing!",
                icon = Icons.Default.AccountCircle,
                onClick = { navController.navigate("login_screen") }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Register Button
            PuzzleBotSecondaryButton(
                text = "Join the Fun!",
                icon = Icons.Default.Person,
                onClick = { navController.navigate("register_screen") }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Fun Footer Text
            Text(
                text = "🎮 Ready for Adventures? 🧩",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = BrightPink,
            )
        }
    }
}




@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    IndividualProject3Theme {
        MainScreen(navController = rememberNavController())
    }
}