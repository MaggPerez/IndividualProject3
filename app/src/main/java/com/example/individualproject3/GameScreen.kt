package com.example.individualproject3

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.example.individualproject3.ui.theme.*
import com.example.individualproject3.viewmodels.*

/**
 * Game screen for a specific difficulty level
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    level: Int,
    navController: NavController,
    username: String,
    userId: Int,
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = viewModel(
        factory = GameViewModelFactory(
            gameRepository = GameRepository(
                gameSessionDao = UserDatabase.getDatabase(LocalContext.current).gameSessionDao(),
                context = LocalContext.current
            ),
            username = username,
            userId = userId
        )
    )
) {
    // Get puzzles for this level
    val puzzles = remember { getPuzzlesForLevel(level) }
    var currentPuzzleIndex by remember { mutableStateOf(0) }

    // Load first puzzle when screen opens
    LaunchedEffect(currentPuzzleIndex) {
        viewModel.loadPuzzle(puzzles[currentPuzzleIndex])
    }

    val currentPuzzle = viewModel.currentPuzzle
    val levelTitle = getLevelTitle(level)
    val levelColor = getLevelColor(level)

    PuzzleBotAnimatedBackground(modifier = modifier) {
        FloatingStars()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Top bar
            TopAppBar(
                title = {
                    Text(
                        text = levelTitle,
                        fontWeight = FontWeight.Bold,
                        color = TextOnColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextOnColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = levelColor
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Puzzle selector
                PuzzleSelector(
                    currentPuzzleIndex = currentPuzzleIndex,
                    totalPuzzles = puzzles.size,
                    onPuzzleSelected = { index ->
                        currentPuzzleIndex = index
                        viewModel.nextPuzzle()
                    },
                    levelColor = levelColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Score display
                if (viewModel.gameState is GameState.Success) {
                    ScoreDisplay(
                        score = viewModel.score,
                        attempts = viewModel.attempts
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Game board
                currentPuzzle?.let { puzzle ->
                    GameBoard(
                        puzzle = puzzle,
                        robotState = viewModel.robotState,
                        commandQueue = viewModel.commandQueue,
                        gameState = viewModel.gameState,
                        onAddCommand = { direction -> viewModel.addCommand(direction) },
                        onRemoveLastCommand = { viewModel.removeLastCommand() },
                        onClearCommands = { viewModel.clearCommands() },
                        onRun = { viewModel.runCommands() },
                        onReset = { viewModel.resetGame() }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Next puzzle button (shown on success)
                if (viewModel.gameState is GameState.Success && currentPuzzleIndex < puzzles.size - 1) {
                    Button(
                        onClick = {
                            currentPuzzleIndex++
                            viewModel.nextPuzzle()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = levelColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Next Puzzle",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(8.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Completion message (all puzzles completed)
                if (viewModel.gameState is GameState.Success && currentPuzzleIndex == puzzles.size - 1) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CompletionCard(levelTitle = levelTitle, levelColor = levelColor)
                }
            }
        }
    }
}

/**
 * Puzzle selector with dots
 */
@Composable
fun PuzzleSelector(
    currentPuzzleIndex: Int,
    totalPuzzles: Int,
    onPuzzleSelected: (Int) -> Unit,
    levelColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Puzzle ${currentPuzzleIndex + 1} of $totalPuzzles",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Puzzle dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(totalPuzzles) { index ->
                    PuzzleDot(
                        isSelected = index == currentPuzzleIndex,
                        isCompleted = index < currentPuzzleIndex,
                        color = levelColor,
                        onClick = { onPuzzleSelected(index) }
                    )
                }
            }
        }
    }
}

/**
 * Individual puzzle dot indicator
 */
@Composable
fun PuzzleDot(
    isSelected: Boolean,
    isCompleted: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(if (isSelected) 40.dp else 30.dp)
            .shadow(if (isSelected) 4.dp else 2.dp, CircleShape)
            .background(
                color = when {
                    isSelected -> color
                    isCompleted -> BrightGreen
                    else -> Color.LightGray
                },
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isCompleted) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Completed",
                tint = TextOnColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Score display card
 */
@Composable
fun ScoreDisplay(score: Int, attempts: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = BrightGreen
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Score
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Score",
                    tint = SunnyYellow,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = "Score: $score",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextOnColor
                )
            }

            // Attempts
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Replay,
                    contentDescription = "Attempts",
                    tint = TextOnColor,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = "Attempts: $attempts",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextOnColor
                )
            }
        }
    }
}

/**
 * Completion card shown when all puzzles are completed
 */
@Composable
fun CompletionCard(levelTitle: String, levelColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWhite
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(levelColor.copy(alpha = 0.3f), SurfaceWhite)
                    )
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = "Trophy",
                tint = SunnyYellow,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "🎉 Congratulations! 🎉",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "You completed all puzzles in $levelTitle!",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Ready for the next challenge?",
                fontSize = 14.sp,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Get level title
 */
fun getLevelTitle(level: Int): String {
    return when (level) {
        1 -> "Easy Level"
        2 -> "Normal Level"
        3 -> "Hard Level"
        4 -> "Very Hard Level"
        else -> "Level $level"
    }
}

/**
 * Get level color
 */
fun getLevelColor(level: Int): Color {
    return when (level) {
        1 -> BrightGreen
        2 -> BrightBlue
        3 -> PlayfulOrange
        4 -> BrightPink
        else -> BrightBlue
    }
}

/**
 * Get puzzles for a specific level
 */
fun getPuzzlesForLevel(level: Int): List<PuzzleConfig> {
    return when (level) {
        1 -> getEasyPuzzles()
        2 -> getNormalPuzzles()
        3 -> getHardPuzzles()
        4 -> getVeryHardPuzzles()
        else -> getEasyPuzzles()
    }
}

/**
 * Easy level puzzles (Level 1)
 */
fun getEasyPuzzles(): List<PuzzleConfig> {
    return listOf(
        // Puzzle 1: Simple straight line
        PuzzleConfig(
            puzzleId = 1,
            level = 1,
            gameNumber = 1,
            gridSize = 5,
            board = listOf(
                listOf(CellType.START, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.GOAL),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY)
            ),
            startPosition = Position(0, 0),
            goalPosition = Position(0, 4),
            maxCommands = 10
        ),

        // Puzzle 2: L-shaped path
        PuzzleConfig(
            puzzleId = 2,
            level = 1,
            gameNumber = 2,
            gridSize = 5,
            board = listOf(
                listOf(CellType.START, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.GOAL)
            ),
            startPosition = Position(0, 0),
            goalPosition = Position(4, 4),
            maxCommands = 10
        ),

        // Puzzle 3: Path with one wall
        PuzzleConfig(
            puzzleId = 3,
            level = 1,
            gameNumber = 3,
            gridSize = 5,
            board = listOf(
                listOf(CellType.START, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.GOAL)
            ),
            startPosition = Position(0, 0),
            goalPosition = Position(4, 4),
            maxCommands = 12
        )
    )
}

/**
 * Normal level puzzles (Level 2) - Placeholder
 */
fun getNormalPuzzles(): List<PuzzleConfig> {
    // TODO: Implement normal puzzles
    return getEasyPuzzles() // Temporary
}

/**
 * Hard level puzzles (Level 3) - Placeholder
 */
fun getHardPuzzles(): List<PuzzleConfig> {
    // TODO: Implement hard puzzles
    return getEasyPuzzles() // Temporary
}

/**
 * Very Hard level puzzles (Level 4) - Placeholder
 */
fun getVeryHardPuzzles(): List<PuzzleConfig> {
    // TODO: Implement very hard puzzles
    return getEasyPuzzles() // Temporary
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    IndividualProject3Theme {
        GameScreen(
            level = 1,
            navController = rememberNavController(),
            username = "TestUser",
            userId = 1
        )
    }
}
