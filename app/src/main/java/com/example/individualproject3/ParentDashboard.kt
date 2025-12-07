package com.example.individualproject3

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.individualproject3.datamodels.GameRepository
import com.example.individualproject3.datamodels.UserDatabase
import com.example.individualproject3.datamodels.UserRepository
import com.example.individualproject3.ui.theme.*
import com.example.individualproject3.viewmodels.LoginRegistrationViewModel
import com.example.individualproject3.viewmodels.LoginRegistrationViewModelFactory

@Composable
fun ParentDashboardScreen(
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
    val username = viewModel.loggedInUsername
    val inviteCode = viewModel.inviteCode
    val linkedKids = viewModel.linkedKids
    val clipboardManager = LocalClipboardManager.current
    
    // Fetch data when screen loads
    LaunchedEffect(Unit) {
        viewModel.fetchParentDashboardData()
    }

    PuzzleBotAnimatedBackground(modifier = modifier) {
        FloatingStars()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar with Logout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Title
                Text(
                    text = "Parent Dashboard",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                // Logout Button
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

            Spacer(modifier = Modifier.height(16.dp))

            // Welcome Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(BrightBlue, LightPurple)
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = TextOnColor,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Welcome,",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = username,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Invite Code Section
            if (linkedKids.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = PlayfulOrange,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Link Your Child's Account",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Share this code with your child so they can add you as their parent.",
                            fontSize = 16.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Code Display
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Transparent),
                            colors = CardDefaults.cardColors(containerColor = SurfaceCream),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = inviteCode.ifEmpty { "LOADING..." },
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BrightBlue,
                                    letterSpacing = 4.sp
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { 
                                clipboardManager.setText(AnnotatedString(inviteCode))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrightBlue),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text("Copy Code")
                        }
                    }
                }
            } else {
                // Linked Kids List (Placeholder for now as per requirements to focus on prompt)
                Text(
                    text = "Your Linked Children",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(linkedKids) { kid ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(2.dp, RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(BrightGreen, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = kid.username.first().uppercase(),
                                        color = TextOnColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = kid.username,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                    Text(
                                        text = "Level: 1 (Coming Soon)", // Placeholder
                                        fontSize = 14.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Show invite code at bottom even if linked
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCream),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Add Another Child",
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Text(
                            text = "Code: $inviteCode",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrightBlue
                        )
                    }
                }
            }
        }
    }
}
