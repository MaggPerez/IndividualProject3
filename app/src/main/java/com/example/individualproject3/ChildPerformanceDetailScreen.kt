package com.example.individualproject3

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.individualproject3.datamodels.GameRepository
import com.example.individualproject3.datamodels.GameSession
import com.example.individualproject3.datamodels.UserDatabase
import com.example.individualproject3.datamodels.UserRepository
import com.example.individualproject3.ui.theme.*
import com.example.individualproject3.viewmodels.LoginRegistrationViewModel
import com.example.individualproject3.viewmodels.LoginRegistrationViewModelFactory
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter

@Composable
fun ChildPerformanceDetailScreen(
    navController: NavController,
    childUsername: String,
    childId: Int,
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
    var gameSessions by remember { mutableStateOf<List<GameSession>>(emptyList()) }
    val context = LocalContext.current
    val gameRepository = remember {
        GameRepository(
            gameSessionDao = UserDatabase.getDatabase(context).gameSessionDao(),
            context = context
        )
    }

    // Fetch game sessions when screen loads
    LaunchedEffect(childId) {
        gameSessions = gameRepository.getSessionsForKid(childId)
    }

    PuzzleBotAnimatedBackground(modifier = modifier) {
        FloatingStars()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Top Bar with Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier
                        .shadow(4.dp, CircleShape)
                        .background(BrightBlue, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextOnColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = "$childUsername's Performance",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                // Spacer for balance
                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Child Info Card
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
                            .size(60.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        androidx.compose.ui.graphics.Color(BrightGreen.toArgb()),
                                        androidx.compose.ui.graphics.Color(BrightBlue.toArgb())
                                    )
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = TextOnColor,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = childUsername,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Total Sessions: ${gameSessions.size}",
                            fontSize = 16.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Statistics Card
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
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Statistics",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatisticItem(
                            label = "Total Games",
                            value = gameSessions.size.toString(),
                            color = BrightBlue
                        )
                        StatisticItem(
                            label = "Success Rate",
                            value = if (gameSessions.isNotEmpty()) {
                                "${(gameSessions.count { it.success } * 100 / gameSessions.size)}%"
                            } else "0%",
                            color = BrightGreen
                        )
                        StatisticItem(
                            label = "Avg Score",
                            value = if (gameSessions.isNotEmpty()) {
                                "${gameSessions.sumOf { it.score } / gameSessions.size}"
                            } else "0",
                            color = PlayfulOrange
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Score Progress Chart
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
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Score Progress Over Time",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (gameSessions.isNotEmpty()) {
                        ScoreProgressChart(gameSessions)
                    } else {
                        Text(
                            text = "No game data available yet",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(vertical = 32.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Success Rate by Level Chart
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
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Success Rate by Level",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (gameSessions.isNotEmpty()) {
                        SuccessRateByLevelChart(gameSessions)
                    } else {
                        Text(
                            text = "No game data available yet",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(vertical = 32.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatisticItem(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary
        )
    }
}

@Composable
fun ScoreProgressChart(gameSessions: List<GameSession>) {
    // Sort sessions by timestamp
    val sortedSessions = gameSessions.sortedBy { it.timestamp }

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)
                setDrawGridBackground(false)

                // Configure X axis
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    granularity = 1f
                    textColor = Color.GRAY
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return "Game ${value.toInt() + 1}"
                        }
                    }
                }

                // Configure left Y axis
                axisLeft.apply {
                    textColor = Color.GRAY
                    setDrawGridLines(true)
                    gridColor = Color.LTGRAY
                }

                // Disable right Y axis
                axisRight.isEnabled = false

                // Configure legend
                legend.isEnabled = true
                legend.textColor = Color.GRAY
            }
        },
        update = { chart ->
            // Create entries for the chart
            val entries = sortedSessions.mapIndexed { index, session ->
                Entry(index.toFloat(), session.score.toFloat())
            }

            // Create dataset
            val dataSet = LineDataSet(entries, "Score").apply {
                color = BrightBlue.toArgb()
                setCircleColor(BrightBlue.toArgb())
                lineWidth = 3f
                circleRadius = 5f
                setDrawCircleHole(false)
                valueTextSize = 10f
                valueTextColor = Color.DKGRAY
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setDrawFilled(true)
                fillColor = BrightBlue.toArgb()
                fillAlpha = 50
            }

            chart.data = LineData(dataSet)
            chart.invalidate()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    )
}

@Composable
fun SuccessRateByLevelChart(gameSessions: List<GameSession>) {
    // Group sessions by level and calculate success rate
    val levelStats = gameSessions.groupBy { it.level }
        .mapValues { (_, sessions) ->
            val successCount = sessions.count { it.success }
            (successCount.toFloat() / sessions.size.toFloat()) * 100f
        }
        .toSortedMap()

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)
                setDrawGridBackground(false)

                // Configure X axis
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    granularity = 1f
                    textColor = Color.GRAY
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return "Level ${value.toInt()}"
                        }
                    }
                }

                // Configure left Y axis
                axisLeft.apply {
                    textColor = Color.GRAY
                    setDrawGridLines(true)
                    gridColor = Color.LTGRAY
                    axisMinimum = 0f
                    axisMaximum = 100f
                }

                // Disable right Y axis
                axisRight.isEnabled = false

                // Configure legend
                legend.isEnabled = true
                legend.textColor = Color.GRAY
            }
        },
        update = { chart ->
            // Create entries for the chart
            val entries = levelStats.map { (level, successRate) ->
                Entry(level.toFloat(), successRate)
            }

            // Create dataset
            val dataSet = LineDataSet(entries, "Success Rate (%)").apply {
                color = BrightGreen.toArgb()
                setCircleColor(BrightGreen.toArgb())
                lineWidth = 3f
                circleRadius = 5f
                setDrawCircleHole(false)
                valueTextSize = 10f
                valueTextColor = Color.DKGRAY
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setDrawFilled(true)
                fillColor = BrightGreen.toArgb()
                fillAlpha = 50
            }

            chart.data = LineData(dataSet)
            chart.invalidate()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    )
}