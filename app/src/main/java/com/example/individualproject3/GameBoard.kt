package com.example.individualproject3

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.individualproject3.ui.theme.*
import com.example.individualproject3.viewmodels.*
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

/**
 * Data class to hold drag state
 */
data class DragState(
    val isDragging: Boolean = false,
    val direction: Direction? = null,
    val dragOffset: Offset = Offset.Zero,
    val startPosition: Offset = Offset.Zero
)

/**
 * Main game board composable
 */
@Composable
fun GameBoard(
    puzzle: PuzzleConfig,
    robotState: RobotState?,
    commandQueue: List<Direction>,
    gameState: GameState,
    keysCollected: Int = 0,
    remainingKeys: List<Position> = emptyList(),
    trapsActivated: Boolean = false,
    onAddCommand: (Direction) -> Unit,
    onRemoveLastCommand: () -> Unit,
    onClearCommands: () -> Unit,
    onRun: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Drag state
    var dragState by remember { mutableStateOf(DragState()) }
    var queuePosition by remember { mutableStateOf(Offset.Zero) }

    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Key counter card (for Hard difficulty)
            if (puzzle.keys.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KeyCounterCard(
                        keysCollected = keysCollected,
                        totalKeys = puzzle.keys.size,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                    TrapWarningCard(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }
            }

            // Game board grid
            GameGrid(
                puzzle = puzzle,
                robotState = robotState,
                remainingKeys = remainingKeys,
                trapsActivated = trapsActivated,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Command queue display
            CommandQueueDisplay(
                commands = commandQueue,
                maxCommands = puzzle.maxCommands,
                onRemoveLast = onRemoveLastCommand,
                isDragOver = dragState.isDragging,
                onPositionChanged = { queuePosition = it },
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Command palette
            CommandPalette(
                onCommandSelected = onAddCommand,
                enabled = gameState != GameState.Running && commandQueue.size < puzzle.maxCommands,
                onDragStart = { direction, position ->
                    dragState = DragState(
                        isDragging = true,
                        direction = direction,
                        dragOffset = Offset.Zero,
                        startPosition = position
                    )
                },
                onDrag = { offset ->
                    dragState = dragState.copy(dragOffset = dragState.dragOffset + offset)
                },
                onDragEnd = {
                    // Check if dropped on queue
                    val currentPosition = dragState.startPosition + dragState.dragOffset
                    dragState.direction?.let { direction ->
                        if (dragState.isDragging) {
                            // Simple overlap check - if drag ended near the queue area
                            if (currentPosition.y < queuePosition.y + 100 && currentPosition.y > queuePosition.y - 100) {
                                onAddCommand(direction)
                            }
                        }
                    }
                    dragState = DragState()
                },
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

        // Drag preview overlay
        dragState.direction?.let { direction ->
            if (dragState.isDragging) {
                DragPreview(
                    direction = direction,
                    offset = dragState.startPosition + dragState.dragOffset
                )
            }
        }
    }
}

/**
 * Game grid display
 */
@Composable
fun GameGrid(
    puzzle: PuzzleConfig,
    robotState: RobotState?,
    remainingKeys: List<Position> = emptyList(),
    trapsActivated: Boolean = false,
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
                        val hasKey = position in remainingKeys
                        val isTrap = position in puzzle.traps

                        GameCell(
                            cellType = cellType,
                            hasRobot = hasRobot,
                            hasKey = hasKey,
                            isTrap = isTrap,
                            trapActivated = trapsActivated,
                            isRobotActive = robotState?.isActive ?: false,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
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
    hasKey: Boolean = false,
    isTrap: Boolean = false,
    trapActivated: Boolean = false,
    isRobotActive: Boolean,
    modifier: Modifier = Modifier
) {
    // Determine background color based on trap state
    val backgroundColor = when {
        isTrap && trapActivated -> ErrorRed.copy(alpha = 0.8f) // Solid red when activated
        isTrap && !trapActivated -> ErrorRed.copy(alpha = 0.2f) // Faint red when inactive
        cellType == CellType.EMPTY -> Color.White
        cellType == CellType.WALL -> Color.Gray
        cellType == CellType.START -> BrightGreen.copy(alpha = 0.3f)
        cellType == CellType.GOAL -> SunnyYellow.copy(alpha = 0.5f)
        cellType == CellType.TRAP -> ErrorRed.copy(alpha = 0.2f) // Faint red for TRAP cell type (fallback)
        else -> Color.White
    }

    val borderColor = when {
        isTrap && trapActivated -> ErrorRed // Solid red border when activated
        isTrap && !trapActivated -> ErrorRed.copy(alpha = 0.4f) // Faint red border when inactive
        cellType == CellType.START -> BrightGreen
        cellType == CellType.GOAL -> SunnyYellow
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
            hasKey -> {
                // Purple key icon for Hard difficulty
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = "Key",
                    tint = LightPurple,
                    modifier = Modifier.size(30.dp)
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
    isDragOver: Boolean = false,
    onPositionChanged: (Offset) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .onGloballyPositioned { coordinates ->
                onPositionChanged(coordinates.positionInRoot())
            }
            .border(
                width = if (isDragOver) 3.dp else 0.dp,
                color = if (isDragOver) BrightGreen else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            ),
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
    onDragStart: (Direction, Offset) -> Unit = { _, _ -> },
    onDrag: (Offset) -> Unit = {},
    onDragEnd: () -> Unit = {},
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
                    enabled = enabled,
                    onDragStart = onDragStart,
                    onDrag = onDrag,
                    onDragEnd = onDragEnd
                )
                DirectionButton(
                    direction = Direction.RIGHT,
                    icon = Icons.Default.ArrowForward,
                    rotation = 0f,
                    onClick = { onCommandSelected(Direction.RIGHT) },
                    enabled = enabled,
                    onDragStart = onDragStart,
                    onDrag = onDrag,
                    onDragEnd = onDragEnd
                )
                DirectionButton(
                    direction = Direction.DOWN,
                    icon = Icons.Default.ArrowDownward,
                    rotation = 0f,
                    onClick = { onCommandSelected(Direction.DOWN) },
                    enabled = enabled,
                    onDragStart = onDragStart,
                    onDrag = onDrag,
                    onDragEnd = onDragEnd
                )
                DirectionButton(
                    direction = Direction.LEFT,
                    icon = Icons.Default.ArrowBack,
                    rotation = 0f,
                    onClick = { onCommandSelected(Direction.LEFT) },
                    enabled = enabled,
                    onDragStart = onDragStart,
                    onDrag = onDrag,
                    onDragEnd = onDragEnd
                )
            }
        }
    }
}

/**
 * Direction button with drag and drop support
 */
@Composable
fun DirectionButton(
    direction: Direction,
    icon: ImageVector,
    rotation: Float,
    onClick: () -> Unit,
    enabled: Boolean,
    onDragStart: (Direction, Offset) -> Unit = { _, _ -> },
    onDrag: (Offset) -> Unit = {},
    onDragEnd: () -> Unit = {}
) {
    var isPressed by remember { mutableStateOf(false) }
    var buttonPosition by remember { mutableStateOf(Offset.Zero) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(70.dp)
            .onGloballyPositioned { coordinates ->
                buttonPosition = coordinates.positionInRoot()
            }
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput

                // Detect tap gestures for click
                detectTapGestures(
                    onTap = {
                        isPressed = true
                        onClick()
                    }
                )
            }
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput

                // Detect long press and drag
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        onDragStart(direction, buttonPosition + Offset(35f, 35f)) // Center of button
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount)
                    },
                    onDragEnd = {
                        onDragEnd()
                    },
                    onDragCancel = {
                        onDragEnd()
                    }
                )
            }
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        // Button background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = if (enabled) BrightGreen else Color.Gray,
                    shape = CircleShape
                )
                .shadow(4.dp, CircleShape),
            contentAlignment = Alignment.Center
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
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
        }
    }
}

/**
 * Drag preview that follows the user's finger
 */
@Composable
fun DragPreview(
    direction: Direction,
    offset: Offset
) {
    val rotation = when (direction) {
        Direction.UP -> 0f
        Direction.RIGHT -> 90f
        Direction.DOWN -> 180f
        Direction.LEFT -> 270f
    }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    (offset.x - 35).roundToInt(), // Center the preview
                    (offset.y - 35).roundToInt()
                )
            }
            .size(70.dp)
            .zIndex(1000f) // Ensure it's on top
            .shadow(8.dp, CircleShape)
            .background(BrightGreen.copy(alpha = 0.8f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.ArrowUpward,
            contentDescription = direction.name,
            tint = TextOnColor,
            modifier = Modifier
                .size(36.dp)
                .rotate(rotation)
        )
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
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Run",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Run", fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
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
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Clear",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Clear", fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
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
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Reset",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Reset", fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
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

/**
 * Key counter card for Hard difficulty
 */
@Composable
fun KeyCounterCard(
    keysCollected: Int,
    totalKeys: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = LightPurple),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Info text
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Collect Keys!",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextOnColor,
                    lineHeight = 14.sp,
                    maxLines = 1
                )
                Text(
                    text = "Required",
                    fontSize = 10.sp,
                    color = TextOnColor.copy(alpha = 0.9f),
                    lineHeight = 12.sp,
                    maxLines = 1
                )
            }

            // Key counter
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = "Keys",
                    tint = TextOnColor,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "$keysCollected/$totalKeys",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextOnColor
                )
            }
        }
    }
}

/**
 * Trap warning card
 */
@Composable
fun TrapWarningCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = ErrorRed),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Text(
                text = "Keys activate traps!",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 13.sp,
                color = TextOnColor,
                modifier = Modifier
                    .padding(end = 22.dp)
                    .align(Alignment.CenterStart)
            )
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                tint = TextOnColor,
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.CenterEnd)
            )
        }
    }
}
