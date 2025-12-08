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
import androidx.compose.runtime.saveable.rememberSaveable
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
    var currentPuzzleIndex by rememberSaveable { mutableStateOf(0) }

    // Start background music
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.playMusic(context)
    }

    // Load puzzle when screen opens or when puzzle index changes
    LaunchedEffect(currentPuzzleIndex) {
        // Check if we need to restore the current puzzle
        val savedPuzzleId = viewModel.getSavedPuzzleId()
        val currentPuzzle = puzzles[currentPuzzleIndex]

        if (savedPuzzleId == currentPuzzle.puzzleId) {
            // Restoring: load puzzle but skip resetting state
            viewModel.loadPuzzle(currentPuzzle, skipIfAlreadyLoaded = true)
        } else {
            // New puzzle: load and reset state
            viewModel.loadPuzzle(currentPuzzle)
        }
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
                        keysCollected = viewModel.keysCollected,
                        remainingKeys = viewModel.remainingKeys,
                        trapsActivated = viewModel.trapsActivated,
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
                    CompletionCard(
                        levelTitle = levelTitle,
                        levelColor = levelColor,
                        currentLevel = level,
                        navController = navController
                    )
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
fun CompletionCard(
    levelTitle: String,
    levelColor: Color,
    currentLevel: Int,
    navController: NavController
) {
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

            Spacer(modifier = Modifier.height(20.dp))

            // Back to Dashboard button (all levels)
            Button(
                onClick = { navController.navigate("dashboard_screen")  },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = levelColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Dashboard",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Back to Dashboard", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
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
 * Normal level puzzles (Level 2)
 */
fun getNormalPuzzles(): List<PuzzleConfig> {
    return listOf(
        // Puzzle 1: "The Corridor Maze" - Navigate through narrow corridors
        PuzzleConfig(
            puzzleId = 4,
            level = 2,
            gameNumber = 1,
            gridSize = 6,
            board = listOf(
                listOf(CellType.START, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.WALL, CellType.WALL),
                listOf(CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.WALL, CellType.WALL, CellType.EMPTY, CellType.WALL, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.GOAL)
            ),
            startPosition = Position(0, 0),
            goalPosition = Position(5, 5),
            maxCommands = 14
        ),

        // Puzzle 2: "The Zigzag Challenge" - Must navigate in a zigzag pattern
        PuzzleConfig(
            puzzleId = 5,
            level = 2,
            gameNumber = 2,
            gridSize = 6,
            board = listOf(
                listOf(CellType.START, CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.WALL, CellType.WALL),
                listOf(CellType.WALL, CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.WALL),
                listOf(CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.WALL, CellType.WALL, CellType.EMPTY),
                listOf(CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.WALL, CellType.WALL, CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.GOAL)
            ),
            startPosition = Position(0, 0),
            goalPosition = Position(5, 5),
            maxCommands = 13
        ),

        // Puzzle 3: "The U-Turn Challenge" - Must navigate around a U-shaped wall barrier
        PuzzleConfig(
            puzzleId = 6,
            level = 2,
            gameNumber = 3,
            gridSize = 6,
            board = listOf(
                listOf(CellType.START, CellType.EMPTY, CellType.WALL, CellType.WALL, CellType.WALL, CellType.WALL),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.GOAL),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.EMPTY),
                listOf(CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.WALL, CellType.WALL, CellType.EMPTY)
            ),
            startPosition = Position(0, 0),
            goalPosition = Position(1, 5),
            maxCommands = 12
        )
    )
}

/**
 * Hard level puzzles (Level 3) - With key collection mechanics
 */
fun getHardPuzzles(): List<PuzzleConfig> {
    return listOf(
        // Puzzle 1: "The First Key" - Introduction to key mechanics (1 key) with trap
        PuzzleConfig(
            puzzleId = 7,
            level = 3,
            gameNumber = 1,
            gridSize = 7,
            board = listOf(
                listOf(CellType.START, CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.WALL, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.WALL, CellType.WALL, CellType.EMPTY, CellType.WALL, CellType.WALL, CellType.WALL, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.WALL, CellType.WALL, CellType.EMPTY, CellType.WALL, CellType.WALL, CellType.GOAL)
            ),
            startPosition = Position(0, 0),
            goalPosition = Position(6, 6),
            keys = listOf(Position(3, 2)), // 1 key to collect
            traps = listOf(Position(4, 2)), // Trap blocks the direct path after collecting the key
            maxCommands = 18
        ),

        // Puzzle 2: "The Twin Keys" - Collect 2 keys before reaching goal with traps
        PuzzleConfig(
            puzzleId = 8,
            level = 3,
            gameNumber = 2,
            gridSize = 7,
            board = listOf(
                listOf(CellType.START, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.WALL, CellType.WALL, CellType.EMPTY),
                listOf(CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.WALL, CellType.WALL, CellType.EMPTY, CellType.WALL),
                listOf(CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.WALL, CellType.EMPTY, CellType.WALL),
                listOf(CellType.WALL, CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.GOAL)
            ),
            startPosition = Position(0, 0),
            goalPosition = Position(6, 6),
            keys = listOf(Position(2, 4), Position(4, 5)), // 2 keys to collect
            traps = listOf(Position(3, 1), Position(5, 5)), // Two strategic traps
            maxCommands = 20
        ),

        // Puzzle 3: "The Triple Challenge" - Collect all 3 keys with multiple traps
        PuzzleConfig(
            puzzleId = 9,
            level = 3,
            gameNumber = 3,
            gridSize = 7,
            board = listOf(
                listOf(CellType.START, CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.WALL, CellType.WALL, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.WALL, CellType.WALL, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.WALL, CellType.WALL),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.WALL, CellType.WALL, CellType.WALL, CellType.EMPTY, CellType.WALL, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.GOAL)
            ),
            startPosition = Position(0, 0),
            goalPosition = Position(6, 6),
            keys = listOf(Position(1, 4), Position(2, 6), Position(4, 1)), // 3 keys to collect
            traps = listOf(Position(2, 1), Position(4, 6), Position(5, 4)), // Three strategic traps
            maxCommands = 26
        )
    )
}

/**
 * Very Hard level puzzles (Level 4) - Ultimate challenge with keys and traps
 */
fun getVeryHardPuzzles(): List<PuzzleConfig> {
    return listOf(
        // Puzzle 1: "The Gauntlet" - Navigate through multiple trap zones (3 keys, 4 traps)
        PuzzleConfig(
            puzzleId = 10,
            level = 4,
            gameNumber = 1,
            gridSize = 8,
            board = listOf(
                listOf(CellType.START, CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.WALL, CellType.EMPTY),
                listOf(CellType.WALL, CellType.WALL, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.WALL),
                listOf(CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.WALL, CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.WALL, CellType.GOAL)
            ),
            startPosition = Position(0, 0),
            goalPosition = Position(7, 7),
            keys = listOf(Position(1, 6), Position(3, 5), Position(5, 2)), // 3 keys strategically placed
            traps = listOf(Position(2, 3), Position(4, 2), Position(1, 7), Position(6, 5)), // 4 traps blocking key routes
            maxCommands = 28
        ),

        // Puzzle 2: "The Maze Runner" - Complex maze with maximum keys (4 keys, 5 traps)
        PuzzleConfig(
            puzzleId = 11,
            level = 4,
            gameNumber = 2,
            gridSize = 8,
            board = listOf(
                listOf(CellType.START, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.WALL, CellType.EMPTY),
                listOf(CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.WALL, CellType.WALL, CellType.EMPTY, CellType.WALL, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.WALL, CellType.WALL, CellType.EMPTY),
                listOf(CellType.WALL, CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.WALL, CellType.EMPTY, CellType.WALL, CellType.GOAL)
            ),
            startPosition = Position(0, 0),
            goalPosition = Position(7, 7),
            keys = listOf(Position(1, 5), Position(2, 7), Position(4, 6), Position(6, 3)), // 4 keys requiring extensive navigation
            traps = listOf(Position(1, 1), Position(3, 5), Position(4, 2), Position(5, 4), Position(6, 6)), // 5 traps creating danger zones (fixed: moved trap from wall at 5,5 to empty cell at 5,4)
            maxCommands = 32
        ),

        // Puzzle 3: "The Ultimate Challenge" - Maximum difficulty (4 keys, 6 traps)
        PuzzleConfig(
            puzzleId = 12,
            level = 4,
            gameNumber = 3,
            gridSize = 8,
            board = listOf(
                listOf(CellType.START, CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.WALL, CellType.WALL, CellType.EMPTY),
                listOf(CellType.WALL, CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.WALL, CellType.EMPTY, CellType.WALL, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY),
                listOf(CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.WALL),
                listOf(CellType.WALL, CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.GOAL)
            ),
            startPosition = Position(0, 0),
            goalPosition = Position(7, 7),
            keys = listOf(Position(1, 5), Position(2, 2), Position(4, 5), Position(6, 6)), // 4 keys requiring perfect planning
            traps = listOf(Position(1, 3), Position(3, 6), Position(4, 1), Position(5, 4), Position(6, 1), Position(7, 5)), // 6 traps creating ultimate challenge
            maxCommands = 36
        )
    )
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
