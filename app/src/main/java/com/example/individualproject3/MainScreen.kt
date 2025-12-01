package com.example.individualproject3

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.individualproject3.ui.theme.IndividualProject3Theme

@Composable
fun MainScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    // Playful animations
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient"
    )

    // Bouncing animation for robot
    val bounceAnimation = rememberInfiniteTransition(label = "bounce")
    val bounce by bounceAnimation.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    // Rotating stars
    val starRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "star"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF87CEEB), // Sky blue
                        Color(0xFFB0E0E6), // Powder blue
                        Color(0xFFE0F6FF)  // Light cyan
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(animatedOffset, animatedOffset + 1000f)
                )
            )
    ) {
        // Decorative floating stars
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = Color(0xFFFFD700).copy(alpha = 0.6f),
            modifier = Modifier
                .size(40.dp)
                .offset(x = 30.dp, y = 60.dp)
                .rotate(starRotation)
        )

        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = Color(0xFFFF69B4).copy(alpha = 0.5f),
            modifier = Modifier
                .size(35.dp)
                .offset(x = 320.dp, y = 100.dp)
                .rotate(-starRotation)
        )

        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = Color(0xFF98FB98).copy(alpha = 0.6f),
            modifier = Modifier
                .size(30.dp)
                .offset(x = 50.dp, y = 500.dp)
                .rotate(starRotation * 0.7f)
        )

        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = Color(0xFFDDA0DD).copy(alpha = 0.5f),
            modifier = Modifier
                .size(38.dp)
                .offset(x = 300.dp, y = 550.dp)
                .rotate(-starRotation * 0.5f)
        )

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
        ) {
            // Robot Mascot with Bounce Animation
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .offset(y = bounce.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = CircleShape,
                        spotColor = Color(0xFFFF6B35).copy(alpha = 0.4f)
                    )
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFFFFF),
                                Color(0xFFFFF8DC)
                            )
                        ),
                        shape = CircleShape
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.robot),
                    contentDescription = "PuzzleBot Mascot",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Game Title with Fun Colors
            Text(
                text = "PuzzleBot",
                fontSize = 64.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color = Color(0xFFFF6B35), // Bright orange
                style = MaterialTheme.typography.displayLarge,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tagline with playful style
            Text(
                text = "🌟 Learn to Code Through Play! 🚀",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A90E2), // Bright blue
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(56.dp))

            // Login Button - Bright and Playful
            Button(
                onClick = { navController.navigate("login_screen") },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(70.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(35.dp),
                        spotColor = Color(0xFF4CAF50).copy(alpha = 0.5f)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50), // Bright green
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(35.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Start Playing!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Register Button - Fun Secondary Style
            Button(
                onClick = { navController.navigate("register_screen") },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(70.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(35.dp),
                        spotColor = Color(0xFFFF9800).copy(alpha = 0.5f)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800), // Bright orange
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(35.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Join the Fun!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Fun Footer Text with more emojis
            Text(
                text = "🎮 Ready for Adventures? 🧩",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE91E63), // Bright pink
                textAlign = TextAlign.Center
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