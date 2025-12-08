package com.example.individualproject3.viewmodels

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.individualproject3.R
import com.example.individualproject3.datamodels.GameRepository
import com.example.individualproject3.datamodels.GameSession
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Direction enum for robot movement
 */
enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

/**
 * Cell types for the game board
 */
enum class CellType {
    EMPTY, WALL, START, GOAL, TRAP
}

/**
 * Position on the game board
 */
data class Position(val row: Int, val col: Int)



/**
 * Robot state
 */
data class RobotState(
    val position: Position,
    val isActive: Boolean = false
)





/**
 * Puzzle configuration
 * @param puzzleId Unique identifier for the puzzle
 * @param level Difficulty level of the puzzle
 * @param gameNumber Specific game number within the level
 * @param gridSize Size of the game board (gridSize x gridSize)
 * @param board 2D list representing the game board with cell types
 * @param startPosition Starting position of the robot
 * @param goalPosition Goal position to reach
 * @param maxCommands Maximum number of commands allowed
 * @param keys List of collectible key positions (for Hard difficulty)
 * @param traps List of trap positions that activate when a key is collected
 * @param optimalMoves Actual optimal number of moves (accounts for walls), defaults to Manhattan distance
 */
data class PuzzleConfig(
    val puzzleId: Int,
    val level: Int,
    val gameNumber: Int,
    val gridSize: Int,
    val board: List<List<CellType>>,
    val startPosition: Position,
    val goalPosition: Position,
    val maxCommands: Int = 10,
    val keys: List<Position> = emptyList(),
    val traps: List<Position> = emptyList(),
    val optimalMoves: Int? = null
)

/**
 * Game execution state
 * Idle - waiting to run
 * Running - executing commands
 * Success - reached goal
 * Failed - hit wall or trap
 */
sealed class GameState {
    data object Idle : GameState()
    data object Running : GameState()
    data object Success : GameState()
    data object Failed : GameState()
}

/**
 * ViewModel for game logic, scoped to the game screen
 */
class GameViewModel(
    private val gameRepository: GameRepository,
    private val username: String,
    private val userId: Int,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    /**
     * Companion object for saved state keys
     */
    companion object {
        private const val KEY_PUZZLE_ID = "puzzle_id"
        private const val KEY_PUZZLE_LEVEL = "puzzle_level"
        private const val KEY_PUZZLE_GAME_NUMBER = "puzzle_game_number"
        private const val KEY_COMMAND_QUEUE = "command_queue"
        private const val KEY_ROBOT_ROW = "robot_row"
        private const val KEY_ROBOT_COL = "robot_col"
        private const val KEY_GAME_STATE = "game_state"
        private const val KEY_ATTEMPTS = "attempts"
        private const val KEY_SCORE = "score"
        private const val KEY_KEYS_COLLECTED = "keys_collected"
        private const val KEY_REMAINING_KEYS = "remaining_keys"
        private const val KEY_TRAPS_ACTIVATED = "traps_activated"
    }


    // Media player for background music
    private var mediaPlayer: MediaPlayer? = null



    // Current puzzle configuration
    var currentPuzzle by mutableStateOf<PuzzleConfig?>(null)
        private set



    // Command queue (list of directions)
    var commandQueue by mutableStateOf<List<Direction>>(
        savedStateHandle.get<Array<String>>(KEY_COMMAND_QUEUE)?.mapNotNull {
            try { Direction.valueOf(it) } catch (e: Exception) { null }
        } ?: emptyList()
    )
        private set



    // Robot state
    var robotState by mutableStateOf<RobotState?>(
        savedStateHandle.get<Int>(KEY_ROBOT_ROW)?.let { row ->
            savedStateHandle.get<Int>(KEY_ROBOT_COL)?.let { col ->
                RobotState(Position(row, col))
            }
        }
    )
        private set



    // Game execution state
    var gameState by mutableStateOf<GameState>(
        when (savedStateHandle.get<String>(KEY_GAME_STATE)) {
            "Success" -> GameState.Success
            "Failed" -> GameState.Failed
            "Running" -> GameState.Idle // Reset running state
            else -> GameState.Idle
        }
    )
        private set



    // Attempts counter
    var attempts by mutableStateOf(savedStateHandle.get<Int>(KEY_ATTEMPTS) ?: 0)
        private set



    // Score
    var score by mutableStateOf(savedStateHandle.get<Int>(KEY_SCORE) ?: 0)
        private set



    // Keys collected (for Hard difficulty)
    var keysCollected by mutableStateOf(savedStateHandle.get<Int>(KEY_KEYS_COLLECTED) ?: 0)
        private set



    // Remaining key positions (for Hard difficulty)
    var remainingKeys by mutableStateOf<List<Position>>(emptyList())
        private set



    // Trap activation state (false = faint/inactive, true = solid/active)
    var trapsActivated by mutableStateOf(savedStateHandle.get<Boolean>(KEY_TRAPS_ACTIVATED) ?: false)
        private set



    /**
     * Initialize and play background music
     */
    fun playMusic(context: Context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context.applicationContext, R.raw.puzzlebot_music)
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        } else if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }

    /**
     * Stop and release background music
     */
    fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }


    /**
     * Clean up resources on ViewModel clearance
     */
    override fun onCleared() {
        super.onCleared()
        stopMusic()
    }



    /**
     * Get saved puzzle ID for restoration
     */
    fun getSavedPuzzleId(): Int? = savedStateHandle.get<Int>(KEY_PUZZLE_ID)



    /**
     * Load a puzzle
     */
    fun loadPuzzle(puzzle: PuzzleConfig, skipIfAlreadyLoaded: Boolean = false) {
        // Skip loading if we're restoring and the puzzle is already loaded
        if (skipIfAlreadyLoaded && currentPuzzle?.puzzleId == puzzle.puzzleId) {
            return
        }

        // Only reset state if loading a new puzzle
        val isNewPuzzle = currentPuzzle?.puzzleId != puzzle.puzzleId
        currentPuzzle = puzzle


        // Reset state if it's a new puzzle
        if (isNewPuzzle) {
            // Reset everything for a new puzzle
            robotState = RobotState(position = puzzle.startPosition)
            commandQueue = emptyList()
            gameState = GameState.Idle
            attempts = 0
            score = 0
            keysCollected = 0
            remainingKeys = puzzle.keys.toList()
            trapsActivated = false

            // Save puzzle info to state
            savedStateHandle[KEY_PUZZLE_ID] = puzzle.puzzleId
            savedStateHandle[KEY_PUZZLE_LEVEL] = puzzle.level
            savedStateHandle[KEY_PUZZLE_GAME_NUMBER] = puzzle.gameNumber
            savedStateHandle[KEY_ROBOT_ROW] = puzzle.startPosition.row
            savedStateHandle[KEY_ROBOT_COL] = puzzle.startPosition.col
            savedStateHandle[KEY_COMMAND_QUEUE] = emptyArray<String>()
            savedStateHandle[KEY_GAME_STATE] = "Idle"
            savedStateHandle[KEY_ATTEMPTS] = 0
            savedStateHandle[KEY_SCORE] = 0
            savedStateHandle[KEY_KEYS_COLLECTED] = 0
            savedStateHandle[KEY_TRAPS_ACTIVATED] = false
        } else {
            //update puzzle reference, keep existing state
            savedStateHandle[KEY_PUZZLE_ID] = puzzle.puzzleId
            savedStateHandle[KEY_PUZZLE_LEVEL] = puzzle.level
            savedStateHandle[KEY_PUZZLE_GAME_NUMBER] = puzzle.gameNumber
        }
    }

    /**
     * Add a command to the queue
     */
    fun addCommand(direction: Direction) {
        if (commandQueue.size < (currentPuzzle?.maxCommands ?: 10)) {
            commandQueue = commandQueue + direction
            savedStateHandle[KEY_COMMAND_QUEUE] = commandQueue.map { it.name }.toTypedArray()
        }
    }



    /**
     * Remove last command from the queue
     */
    fun removeLastCommand() {
        if (commandQueue.isNotEmpty()) {
            commandQueue = commandQueue.dropLast(1)
            savedStateHandle[KEY_COMMAND_QUEUE] = commandQueue.map { it.name }.toTypedArray()
        }
    }



    /**
     * Clear all commands
     */
    fun clearCommands() {
        commandQueue = emptyList()
        savedStateHandle[KEY_COMMAND_QUEUE] = emptyArray<String>()
    }



    /**
     * Execute the command queue (run the robot)
     */
    fun runCommands() {

        // Prevent multiple runs or invalid state
        if (currentPuzzle == null || commandQueue.isEmpty() || gameState == GameState.Running) {
            return
        }

        // Increment attempts
        attempts++
        savedStateHandle[KEY_ATTEMPTS] = attempts
        gameState = GameState.Running
        savedStateHandle[KEY_GAME_STATE] = "Running"


        // Launch coroutine for command execution
        viewModelScope.launch {

            // Initialize robot position
            val puzzle = currentPuzzle!!
            var currentPosition = puzzle.startPosition
            robotState = RobotState(position = currentPosition, isActive = true)

            // Execute each command with animation delay
            for (command in commandQueue) {

                // Animation delay
                delay(500)


                // Get new position based on command
                val newPosition = getNextPosition(currentPosition, command)

                // Check if the new position is valid
                if (isValidPosition(newPosition, puzzle)) {

                    // Update position
                    currentPosition = newPosition
                    robotState = RobotState(position = currentPosition, isActive = true)
                    savedStateHandle[KEY_ROBOT_ROW] = currentPosition.row
                    savedStateHandle[KEY_ROBOT_COL] = currentPosition.col


                    // Check if robot stepped on a key
                    if (currentPosition in remainingKeys) {
                        remainingKeys = remainingKeys.filter { it != currentPosition }
                        keysCollected++
                        savedStateHandle[KEY_KEYS_COLLECTED] = keysCollected

                        // Activate traps when first key is collected. Only shown on hard and very hard levels
                        if (keysCollected == 1 && puzzle.traps.isNotEmpty() && !trapsActivated) {
                            trapsActivated = true
                            savedStateHandle[KEY_TRAPS_ACTIVATED] = true
                        }
                    }

                    // Check if robot hit an activated trap
                    if (trapsActivated && currentPosition in puzzle.traps) {
                        gameState = GameState.Failed
                        savedStateHandle[KEY_GAME_STATE] = "Failed"
                        saveGameSession(puzzle, success = false)
                        robotState = RobotState(position = currentPosition, isActive = false)
                        return@launch
                    }



                    // Check if goal is reached
                    if (currentPosition == puzzle.goalPosition) {
                        // For Hard difficulty, check if all keys are collected
                        val allKeysCollected = puzzle.keys.isEmpty() || keysCollected == puzzle.keys.size


                        // If all keys collected or not required, success
                        if (allKeysCollected) {
                            gameState = GameState.Success

                            // Save success state
                            savedStateHandle[KEY_GAME_STATE] = "Success"
                            calculateScore()
                            savedStateHandle[KEY_SCORE] = score
                            saveGameSession(puzzle, success = true)
                            robotState = RobotState(position = currentPosition, isActive = false)
                            return@launch
                        } else {
                            // Reached goal but haven't collected all keys - continue execution
                            // Player needs to collect remaining keys first
                        }
                    }
                } else {
                    // Hit a wall or invalid position
                    gameState = GameState.Failed
                    savedStateHandle[KEY_GAME_STATE] = "Failed"
                    saveGameSession(puzzle, success = false)
                    robotState = RobotState(position = currentPosition, isActive = false)
                    return@launch
                }
            }

            // Commands executed but goal not reached
            if (currentPosition != puzzle.goalPosition) {
                gameState = GameState.Failed
                savedStateHandle[KEY_GAME_STATE] = "Failed"
                saveGameSession(puzzle, success = false)
            }

            // Update robot state to inactive
            robotState = RobotState(position = currentPosition, isActive = false)
        }
    }

    /**
     * Get next position based on direction
     */
    private fun getNextPosition(current: Position, direction: Direction): Position {
        return when (direction) {
            Direction.UP -> Position(current.row - 1, current.col)
            Direction.DOWN -> Position(current.row + 1, current.col)
            Direction.LEFT -> Position(current.row, current.col - 1)
            Direction.RIGHT -> Position(current.row, current.col + 1)
        }
    }

    /**
     * Check if position is valid (not wall, within bounds)
     */
    private fun isValidPosition(position: Position, puzzle: PuzzleConfig): Boolean {
        if (position.row !in 0 until puzzle.gridSize || position.col !in 0 until puzzle.gridSize) {
            return false
        }

        return puzzle.board[position.row][position.col] != CellType.WALL
    }



    /**
     * Calculate score based on efficiency
     */
    private fun calculateScore() {
        // Give full points if completed on first try
        if (attempts == 1) {
            score = 100
            return
        }

        // Penalize for multiple attempts
        var baseScore = 100
        baseScore -= ((attempts - 1) * 20)

        score = baseScore.coerceAtLeast(10) // Minimum score of 10
    }



    /**
     * Save game session to database
     */
    private fun saveGameSession(puzzle: PuzzleConfig, success: Boolean) {
        viewModelScope.launch {
            val session = GameSession(
                kidId = userId,
                level = puzzle.level,
                gameNumber = puzzle.gameNumber,
                score = if (success) score else 0,
                success = success,
                attempts = attempts,
                timestamp = System.currentTimeMillis()
            )

            gameRepository.insertSession(session, username)
        }
    }



    /**
     * Reset the game
     */
    fun resetGame() {
        currentPuzzle?.let { puzzle ->
            robotState = RobotState(position = puzzle.startPosition)
            commandQueue = emptyList()
            gameState = GameState.Idle
            keysCollected = 0
            remainingKeys = puzzle.keys.toList()
            trapsActivated = false

            savedStateHandle[KEY_ROBOT_ROW] = puzzle.startPosition.row
            savedStateHandle[KEY_ROBOT_COL] = puzzle.startPosition.col
            savedStateHandle[KEY_COMMAND_QUEUE] = emptyArray<String>()
            savedStateHandle[KEY_GAME_STATE] = "Idle"
            savedStateHandle[KEY_KEYS_COLLECTED] = 0
            savedStateHandle[KEY_TRAPS_ACTIVATED] = false
        }
    }



    /**
     * Reset for next puzzle
     */
    fun nextPuzzle() {
        robotState = null
        commandQueue = emptyList()
        gameState = GameState.Idle
        attempts = 0
        score = 0
        keysCollected = 0
        remainingKeys = emptyList()
        trapsActivated = false

        savedStateHandle[KEY_COMMAND_QUEUE] = emptyArray<String>()
        savedStateHandle[KEY_GAME_STATE] = "Idle"
        savedStateHandle[KEY_ATTEMPTS] = 0
        savedStateHandle[KEY_SCORE] = 0
        savedStateHandle[KEY_KEYS_COLLECTED] = 0
        savedStateHandle[KEY_TRAPS_ACTIVATED] = false
    }
}
