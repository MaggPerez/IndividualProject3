package com.example.individualproject3.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    EMPTY, WALL, START, GOAL
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
 */
data class PuzzleConfig(
    val puzzleId: Int,
    val level: Int,
    val gameNumber: Int,
    val gridSize: Int,
    val board: List<List<CellType>>,
    val startPosition: Position,
    val goalPosition: Position,
    val maxCommands: Int = 10
)

/**
 * Game execution state
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
    private val userId: Int
) : ViewModel() {

    // Current puzzle configuration
    var currentPuzzle by mutableStateOf<PuzzleConfig?>(null)
        private set

    // Command queue (list of directions)
    var commandQueue by mutableStateOf<List<Direction>>(emptyList())
        private set

    // Robot state
    var robotState by mutableStateOf<RobotState?>(null)
        private set

    // Game execution state
    var gameState by mutableStateOf<GameState>(GameState.Idle)
        private set

    // Attempts counter
    var attempts by mutableStateOf(0)
        private set

    // Score
    var score by mutableStateOf(0)
        private set

    /**
     * Load a puzzle
     */
    fun loadPuzzle(puzzle: PuzzleConfig) {
        currentPuzzle = puzzle
        robotState = RobotState(position = puzzle.startPosition)
        commandQueue = emptyList()
        gameState = GameState.Idle
        attempts = 0
        score = 0
    }

    /**
     * Add a command to the queue
     */
    fun addCommand(direction: Direction) {
        if (commandQueue.size < (currentPuzzle?.maxCommands ?: 10)) {
            commandQueue = commandQueue + direction
        }
    }

    /**
     * Remove last command from the queue
     */
    fun removeLastCommand() {
        if (commandQueue.isNotEmpty()) {
            commandQueue = commandQueue.dropLast(1)
        }
    }

    /**
     * Clear all commands
     */
    fun clearCommands() {
        commandQueue = emptyList()
    }

    /**
     * Execute the command queue (run the robot)
     */
    fun runCommands() {
        if (currentPuzzle == null || commandQueue.isEmpty() || gameState == GameState.Running) {
            return
        }

        attempts++
        gameState = GameState.Running

        viewModelScope.launch {
            val puzzle = currentPuzzle!!
            var currentPosition = puzzle.startPosition
            robotState = RobotState(position = currentPosition, isActive = true)

            // Execute each command with animation delay
            for (command in commandQueue) {
                delay(500) // Animation delay

                val newPosition = getNextPosition(currentPosition, command)

                // Check if the new position is valid
                if (isValidPosition(newPosition, puzzle)) {
                    currentPosition = newPosition
                    robotState = RobotState(position = currentPosition, isActive = true)

                    // Check if goal is reached
                    if (currentPosition == puzzle.goalPosition) {
                        gameState = GameState.Success
                        calculateScore(puzzle)
                        saveGameSession(puzzle, success = true)
                        robotState = RobotState(position = currentPosition, isActive = false)
                        return@launch
                    }
                } else {
                    // Hit a wall or invalid position
                    gameState = GameState.Failed
                    saveGameSession(puzzle, success = false)
                    robotState = RobotState(position = currentPosition, isActive = false)
                    return@launch
                }
            }

            // Commands executed but goal not reached
            if (currentPosition != puzzle.goalPosition) {
                gameState = GameState.Failed
                saveGameSession(puzzle, success = false)
            }

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
    private fun calculateScore(puzzle: PuzzleConfig) {
        // Base score
        var baseScore = 100

        // Deduct points for extra commands
        val optimalMoves = calculateOptimalMoves(puzzle)
        val extraMoves = commandQueue.size - optimalMoves
        if (extraMoves > 0) {
            baseScore -= (extraMoves * 10)
        }

        // Deduct points for extra attempts
        if (attempts > 1) {
            baseScore -= ((attempts - 1) * 20)
        }

        score = baseScore.coerceAtLeast(10) // Minimum score of 10
    }

    /**
     * Calculate optimal number of moves (Manhattan distance)
     */
    private fun calculateOptimalMoves(puzzle: PuzzleConfig): Int {
        val start = puzzle.startPosition
        val goal = puzzle.goalPosition
        return kotlin.math.abs(goal.row - start.row) + kotlin.math.abs(goal.col - start.col)
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
    }
}
