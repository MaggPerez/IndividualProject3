package com.example.individualproject3

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.individualproject3.datamodels.GameRepository
import com.example.individualproject3.datamodels.UserDatabase
import com.example.individualproject3.datamodels.UserRepository
import com.example.individualproject3.ui.theme.*
import com.example.individualproject3.viewmodels.LoginRegistrationViewModel
import com.example.individualproject3.viewmodels.LoginRegistrationViewModelFactory
//todo: fix link with your parents message styling where it shows a weird gray rectangle around the text
@Composable
fun DashboardScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: LoginRegistrationViewModel = viewModel(
        factory = LoginRegistrationViewModelFactory(
            userRepository = UserRepository(
                userDao = UserDatabase.getDatabase(LocalContext.current).userDao(),
                context = LocalContext.current
            ),
            gameRepository = GameRepository(
                gameSessionDao = UserDatabase.getDatabase(LocalContext.current).gameSessionDao(),
                context = LocalContext.current
            )
        )
    )
) {
    //get username from ViewModel (falls back to "Player" if empty)
    val username = viewModel.loggedInUsername.ifEmpty { "Player" }
    val linkedParentName = viewModel.linkedParentName
    val isModalVisible = viewModel.isLinkParentModalVisible

    // Fetch dashboard data when screen loads
    LaunchedEffect(Unit) {
        viewModel.fetchKidDashboardData()
    }

    // Modal for entering invite code
    if (isModalVisible) {
        AlertDialog(
            onDismissRequest = { viewModel.hideLinkParentModal() },
            title = {
                Text(
                    text = "Enter Parent Invite Code",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "Ask your parent for their 6-digit invite code found on their dashboard.",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = viewModel.linkParentInviteCode,
                        onValueChange = { viewModel.updateLinkParentCode(it.uppercase()) },
                        label = { Text("Invite Code") },
                        singleLine = true,
                        isError = viewModel.linkParentError != null,
                        supportingText = {
                            if (viewModel.linkParentError != null) {
                                Text(viewModel.linkParentError!!, color = ErrorRed)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.attemptLinkParent() },
                    colors = ButtonDefaults.buttonColors(containerColor = BrightBlue)
                ) {
                    Text("Link Account")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideLinkParentModal() }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceWhite,
            shape = RoundedCornerShape(20.dp)
        )
    }

    PuzzleBotAnimatedBackground(modifier = modifier) {
        FloatingStars()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //top Bar with Logout Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //small mascot
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .shadow(4.dp, CircleShape)
                        .background(SurfaceWhite, CircleShape)
                        .padding(6.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.robot),
                        contentDescription = "PuzzleBot",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                //logout Button
                IconButton(
                    onClick = { navController.navigate("main_screen") },
                    modifier = Modifier
                        .shadow(4.dp, CircleShape)
                        .background(ErrorRed, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Logout",
                        tint = TextOnColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            //welcome Header
            ProfileHeader(username = username)

            Spacer(modifier = Modifier.height(24.dp))

            //title
            PuzzleBotTitle(
                text = "Choose Your Level!",
                color = BrightOrange
            )

            Spacer(modifier = Modifier.height(8.dp))

            PuzzleBotSubtitle(
                text = "🎮 Pick a difficulty and start playing! 🚀",
                color = BrightBlue
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Level Selection Grid (2x2)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Row 1: Easy and Normal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LevelCard(
                        level = 1,
                        title = "Easy",
                        emoji = "🌟",
                        color = BrightGreen,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate("level_screen/1") }
                    )

                    LevelCard(
                        level = 2,
                        title = "Normal",
                        emoji = "🎯",
                        color = BrightBlue,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate("level_screen/2") }
                    )
                }

                // Row 2: Hard and Very Hard
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LevelCard(
                        level = 3,
                        title = "Hard",
                        emoji = "🔥",
                        color = PlayfulOrange,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate("level_screen/3") }
                    )

                    LevelCard(
                        level = 4,
                        title = "Very Hard",
                        emoji = "⚡",
                        color = BrightPink,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate("level_screen/4") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            //fun encouragement message
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .shadow(6.dp, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = SunnyYellow,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "You're doing great, ${username}!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = SunnyYellow,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Parent Linking Status Message
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .shadow(4.dp, RoundedCornerShape(16.dp))
                    .clickable(
                        enabled = linkedParentName == null,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        if (linkedParentName == null) {
                            viewModel.showLinkParentModal()
                        }
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (linkedParentName == null) PlayfulOrange.copy(alpha = 0.1f) else BrightGreen.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (linkedParentName == null) Icons.Default.Link else Icons.Default.Person,
                        contentDescription = null,
                        tint = if (linkedParentName == null) PlayfulOrange else BrightGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    if (linkedParentName == null) {
                        Text(
                            text = "You're not linked to a parent. Get linked now!",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PlayfulOrange,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            text = "You are linked with your parent: $linkedParentName",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrightGreen,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileHeader(username: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .shadow(8.dp, RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //avatar
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .shadow(4.dp, CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(BrightBlue, LightPurple)
                        ),
                        shape = CircleShape
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.robot),
                    contentDescription = "Player Avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "Welcome Back!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
                Text(
                    text = username,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = BrightOrange
                )
            }
        }
    }
}

@Composable
fun LevelCard(
    level: Int,
    title: String,
    emoji: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    //animation for scale effect
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    //shimmer animation
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .scale(scale)
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .clickable {
                isPressed = true
                onClick()
            },
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            color,
                            color.copy(alpha = 0.8f),
                            color
                        ),
                        start = androidx.compose.ui.geometry.Offset(shimmerOffset, shimmerOffset),
                        end = androidx.compose.ui.geometry.Offset(shimmerOffset + 200f, shimmerOffset + 200f)
                    )
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                //emoji
                Text(
                    text = emoji,
                    fontSize = 48.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                //level number
                Text(
                    text = "Level $level",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextOnColor.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )

                //title
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextOnColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                //"3 Games" indicator
                Text(
                    text = "3 Games",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextOnColor.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                //star rating placeholder
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(3) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = SunnyYellow,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }

    //reset pressed state
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    IndividualProject3Theme {
        DashboardScreen(
            navController = rememberNavController()
        )
    }
}