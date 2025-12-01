package com.example.individualproject3

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.individualproject3.datamodels.GameRepository
import com.example.individualproject3.datamodels.UserDatabase
import com.example.individualproject3.datamodels.UserRepository
import com.example.individualproject3.ui.theme.*
import com.example.individualproject3.viewmodels.LoginRegistrationViewModel
import com.example.individualproject3.viewmodels.LoginRegistrationViewModelFactory
import com.example.individualproject3.viewmodels.UiEvent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RegisterScreen(
    navController: NavHostController,
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
    val context = LocalContext.current

    //observe UI events from ViewModel
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is UiEvent.NavigateToMain -> {
                    navController.navigate("main_screen")
                }
            }
        }
    }

    PuzzleBotAnimatedBackground(modifier = modifier) {
        FloatingStars()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            //robot Mascot Header
            PuzzleBotMascotContainer(size = 160.dp, withBounce = false) {
                Image(
                    painter = painterResource(R.drawable.robot),
                    contentDescription = "PuzzleBot Mascot",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            //title
            Text(
                text = "Join PuzzleBot! 🎉",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color = PlayfulOrange,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))


            //create your account
            Text(
                text = "Let's create your account!",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = BrightBlue,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            //user Type Selector (Kid or Parent)
            Text(
                text = "I am a...",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))


            //button to choose user type
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {

                //button for Kid
                SegmentedButton(
                    selected = viewModel.userType == UserType.KID,
                    onClick = { viewModel.onUserTypeSelected(UserType.KID) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    icon = {
                        SegmentedButtonDefaults.Icon(active = viewModel.userType == UserType.KID) {
                            Icon(
                                imageVector = Icons.Default.ChildCare,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                ) {
                    Text("Kid", style = MaterialTheme.typography.titleMedium)
                }


                //button for Parent
                SegmentedButton(
                    selected = viewModel.userType == UserType.PARENT,
                    onClick = { viewModel.onUserTypeSelected(UserType.PARENT) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    icon = {
                        SegmentedButtonDefaults.Icon(active = viewModel.userType == UserType.PARENT) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                ) {
                    Text("Parent", style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            //registration Form Card
            PuzzleBotCard {

                //first Name Field
                PuzzleBotTextField(
                    value = viewModel.createFirstName,
                    onValueChange = { viewModel.createFirstName = it },
                    label = "First Name",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )


                Spacer(modifier = Modifier.height(16.dp))


                //last Name Field
                PuzzleBotTextField(
                    value = viewModel.createLastName,
                    onValueChange = { viewModel.createLastName = it },
                    label = "Last Name",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))



                //username Field
                PuzzleBotTextField(
                    value = viewModel.createUsername,
                    onValueChange = { viewModel.createUsername = it },
                    label = "Username",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )


                Spacer(modifier = Modifier.height(16.dp))


                //email Field
                PuzzleBotTextField(
                    value = viewModel.createEmail,
                    onValueChange = { viewModel.createEmail = it },
                    label = "Email",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))


                //password Field
                PuzzleBotTextField(
                    value = viewModel.createPassword,
                    onValueChange = { viewModel.createPassword = it },
                    label = "Password",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                            Text(
                                text = if (viewModel.passwordVisible) "Hide" else "Show",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrightBlue
                            )
                        }
                    },
                    visualTransformation = if (viewModel.passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))


                //confirm Password Field
                PuzzleBotTextField(
                    value = viewModel.confirmPassword,
                    onValueChange = { viewModel.confirmPassword = it },
                    label = "Confirm Password",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null
                        )
                    },
                    visualTransformation = if (viewModel.passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (viewModel.onHandleRegistration()) {
                                navController.navigate("main_screen")
                            }
                        }
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))


                //register Button - Smaller version
                Button(
                    onClick = {
                        if (viewModel.onHandleRegistration()) {
                            navController.navigate("main_screen")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PlayfulOrange,
                        contentColor = TextOnColor
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        text = "Create Account! 🎮",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))



                //login Link - Compact version
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Already have an account?",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(onClick = { navController.navigate("login_screen") }) {
                        Text(
                            text = "Log In",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrightBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(navController = rememberNavController())
}
