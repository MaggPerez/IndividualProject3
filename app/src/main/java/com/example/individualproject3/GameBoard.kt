package com.example.individualproject3

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.individualproject3.ui.theme.*
import com.example.individualproject3.viewmodels.*
import kotlinx.coroutines.delay

/**
 * Main game board composable
 */
@Composable
fun GameBoard(
    puzzle: PuzzleConfig,
    robotState: RobotState?,
    commandQueue: List<Direction>,
    gameState: GameState,
    onAddCommand: (Direction) -> Unit,
    onRemoveLastCommand: () -> Unit,
    onClearCommands: () -> Unit,
    onRun: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Game board grid
        GameGrid(
            puzzle = puzzle,
            robotState = robotState,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Command queue display
        CommandQueueDisplay(
            commands = commandQueue,
            maxCommands = puzzle.maxCommands,
            onRemoveLast = onRemoveLastCommand,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Command palette
        CommandPalette(
            onCommandSelected = onAddCommand,
            enabled = gameState != GameState.Running && commandQueue.size < puzzle.maxCommands,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Control buttons
        ControlButtons(
            gameState = gameState,
            hasCommands = commandQueue.isNotEmpty(),
            onRun = onRun,
            onReset = onReset,
            onClear = onClearCommands
        )

        // Game state message
        GameStateMessage(gameState = gameState)
    }
}

/**
 * Game grid display
 */
@Composable
fun GameGrid(
    puzzle: PuzzleConfig,
    robotState: RobotState?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (row in 0 until puzzle.gridSize) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (col in 0 until puzzle.gridSize) {
                        val position = Position(row, col)
                        val cellType = puzzle.board[row][col]
                        val hasRobot = robotState?.position == position

                        GameCell(
                            cellType = cellType,
                            hasRobot = hasRobot,
                            isRobotActive = robotState?.isActive ?: false,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Individual game cell
 */
@Composable
fun GameCell(
    cellType: CellType,
    hasRobot: Boolean,
    isRobotActive: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (cellType) {
        CellType.EMPTY -> Color.White
        CellType.WALL -> Color.Gray
        CellType.START -> BrightGreen.copy(alpha = 0.3f)
        CellType.GOAL -> SunnyYellow.copy(alpha = 0.5f)
    }

    val borderColor = when (cellType) {
        CellType.START -> BrightGreen
        CellType.GOAL -> SunnyYellow
        else -> Color.LightGray
    }

    // Robot animation
    val infiniteTransition = rememberInfiniteTransition(label = "robot")
    val robotScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "robotScale"
    )

    Box(
        modifier = modifier
            .padding(2.dp)
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .border(2.dp, borderColor, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        when {
            hasRobot -> {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "Robot",
                    tint = BrightBlue,
                    modifier = Modifier
                        .size(40.dp)
                        .scale(if (isRobotActive) robotScale else 1f)
                )
            }
            cellType == CellType.START -> {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start",
                    tint = BrightGreen,
                    modifier = Modifier.size(30.dp)
                )
            }
            cellType == CellType.GOAL -> {
                Icon(
                    imageVector = Icons.Default.Flag,
                    contentDescription = "Goal",
                    tint = PlayfulOrange,
                    modifier = Modifier.size(30.dp)
                )
            }
            cellType == CellType.WALL -> {
                // Wall pattern
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Wall",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

/**
 * Command queue display
 */
@Composable
fun CommandQueueDisplay(
    commands: List<Direction>,
    maxCommands: Int,
    onRemoveLast: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Command Queue",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "${commands.size}/$maxCommands",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (commands.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tap arrows below to add commands",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(commands) { index, direction ->
                        CommandChip(
                            direction = direction,
                            index = index + 1,
                            isLast = index == commands.lastIndex,
                            onRemove = if (index == commands.lastIndex) onRemoveLast else null
                        )
                    }
                }
            }
        }
    }
}

/**
 * Individual command chip
 */
@Composable
fun CommandChip(
    direction: Direction,
    index: Int,
    isLast: Boolean,
    onRemove: (() -> Unit)?
) {
    val rotation = when (direction) {
        Direction.UP -> 0f
        Direction.RIGHT -> 90f
        Direction.DOWN -> 180f
        Direction.LEFT -> 270f
    }

    Box(
        modifier = Modifier
            .size(50.dp)
            .shadow(2.dp, CircleShape)
            .background(
                color = if (isLast) BrightOrange else BrightBlue,
                shape = CircleShape
            )
            .clickable(enabled = onRemove != null) { onRemove?.invoke() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.ArrowUpward,
                contentDescription = direction.name,
                tint = TextOnColor,
                modifier = Modifier
                    .size(24.dp)
                    .rotate(rotation)
            )
            Text(
                text = "$index",
                fontSize = 10.sp,
                color = TextOnColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Command palette for selecting directions
 */
@Composable
fun CommandPalette(
    onCommandSelected: (Direction) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = "Command Palette",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DirectionButton(
                    direction = Direction.UP,
                    icon = Icons.Default.ArrowUpward,
                    rotation = 0f,
                    onClick = { onCommandSelected(Direction.UP) },
                    enabled = enabled
                )
                DirectionButton(
                    direction = Direction.RIGHT,
                    icon = Icons.Default.ArrowForward,
                    rotation = 0f,
                    onClick = { onCommandSelected(Direction.RIGHT) },
                    enabled = enabled
                )
                DirectionButton(
                    direction = Direction.DOWN,
                    icon = Icons.Default.ArrowDownward,
                    rotation = 0f,
                    onClick = { onCommandSelected(Direction.DOWN) },
                    enabled = enabled
                )
                DirectionButton(
                    direction = Direction.LEFT,
                    icon = Icons.Default.ArrowBack,
                    rotation = 0f,
                    onClick = { onCommandSelected(Direction.LEFT) },
                    enabled = enabled
                )
            }
        }
    }
}

/**
 * Direction button
 */
@Composable
fun DirectionButton(
    direction: Direction,
    icon: ImageVector,
    rotation: Float,
    onClick: () -> Unit,
    enabled: Boolean
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Button(
        onClick = {
            isPressed = true
            onClick()
        },
        enabled = enabled,
        modifier = Modifier
            .size(70.dp)
            .scale(scale),
        colors = ButtonDefaults.buttonColors(
            containerColor = BrightGreen,
            disabledContainerColor = Color.Gray
        ),
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = direction.name,
            tint = TextOnColor,
            modifier = Modifier
                .size(36.dp)
                .rotate(rotation)
        )
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
        }
    }
}

/**
 * Control buttons (Run, Reset, Clear)
 */
@Composable
fun ControlButtons(
    gameState: GameState,
    hasCommands: Boolean,
    onRun: () -> Unit,
    onReset: () -> Unit,
    onClear: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Run button
        Button(
            onClick = onRun,
            enabled = hasCommands && gameState != GameState.Running,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrightGreen,
                disabledContainerColor = Color.Gray
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Run",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Run", fontWeight = FontWeight.Bold)
        }

        // Clear button
        Button(
            onClick = onClear,
            enabled = hasCommands && gameState != GameState.Running,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PlayfulOrange,
                disabledContainerColor = Color.Gray
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Clear",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Clear", fontWeight = FontWeight.Bold)
        }

        // Reset button
        Button(
            onClick = onReset,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ErrorRed,
                disabledContainerColor = Color.Gray
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Reset",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Reset", fontWeight = FontWeight.Bold)
        }
    }
}

/**
 * Game state message display
 */
@Composable
fun GameStateMessage(gameState: GameState) {
    when (gameState) {
        is GameState.Success -> {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .shadow(4.dp, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = BrightGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = TextOnColor,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Success! Goal Reached! 🎉",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextOnColor
                    )
                }
            }
        }
        is GameState.Failed -> {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .shadow(4.dp, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = ErrorRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Failed",
                        tint = TextOnColor,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Try Again! You can do it! 💪",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextOnColor
                    )
                }
            }
        }
        else -> { /* No message for Idle or Running states */ }
    }
}
