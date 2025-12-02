package com.example.individualproject3.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


/**
 * Animated background with sky gradient to be used on all screens for consistency
 */
@Composable
fun PuzzleBotAnimatedBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(SkyBlue, PowderBlue, LightCyan),
                    start = Offset(0f, 0f),
                    end = Offset(animatedOffset, animatedOffset + 1000f)
                )
            )
    ) {
        content()
    }
}

/**
 * Floating decorative stars, adds playful elements to any screen
 */
@Composable
fun BoxScope.FloatingStars() {
    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    val starRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "star"
    )

    //top left star
    Icon(
        imageVector = Icons.Default.Star,
        contentDescription = null,
        tint = SunnyYellow.copy(alpha = 0.6f),
        modifier = Modifier
            .size(40.dp)
            .offset(x = 30.dp, y = 60.dp)
            .rotate(starRotation)
    )

    //top right star
    Icon(
        imageVector = Icons.Default.Star,
        contentDescription = null,
        tint = BrightPink.copy(alpha = 0.5f),
        modifier = Modifier
            .size(35.dp)
            .offset(x = 320.dp, y = 100.dp)
            .rotate(-starRotation)
    )

    //bottom left star
    Icon(
        imageVector = Icons.Default.Star,
        contentDescription = null,
        tint = MintGreen.copy(alpha = 0.6f),
        modifier = Modifier
            .size(30.dp)
            .offset(x = 50.dp, y = 500.dp)
            .rotate(starRotation * 0.7f)
    )

    //bottom right star
    Icon(
        imageVector = Icons.Default.Star,
        contentDescription = null,
        tint = LightPurple.copy(alpha = 0.5f),
        modifier = Modifier
            .size(38.dp)
            .offset(x = 300.dp, y = 550.dp)
            .rotate(-starRotation * 0.5f)
    )
}

/**
 * Primary button, bright green for main actions
 */
@Composable
fun PuzzleBotPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(0.9f)
            .height(70.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(35.dp),
                spotColor = BrightGreen.copy(alpha = 0.5f)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = BrightGreen,
            contentColor = TextOnColor,
            disabledContainerColor = Color(0xFFBDBDBD),
            disabledContentColor = Color(0xFFFFFFFF)
        ),
        shape = RoundedCornerShape(35.dp),
        enabled = enabled
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = TextOnColor
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Text(
                text = text,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
        }
    }
}

/**
 * Secondary button - bright orange for secondary actions
 */
@Composable
fun PuzzleBotSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(0.9f)
            .height(70.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(35.dp),
                spotColor = PlayfulOrange.copy(alpha = 0.5f)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = PlayfulOrange,
            contentColor = TextOnColor,
            disabledContainerColor = Color(0xFFBDBDBD),
            disabledContentColor = Color(0xFFFFFFFF)
        ),
        shape = RoundedCornerShape(35.dp),
        enabled = enabled
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = TextOnColor
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Text(
                text = text,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
        }
    }
}

/**
 * Playful card with rounded corners and shadow
 */
@Composable
fun PuzzleBotCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = SurfaceWhite,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = BrightBlue.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }
    }
}

/**
 * Circular mascot container with bounce animation
 */
@Composable
fun PuzzleBotMascotContainer(
    size: Dp = 240.dp,
    withBounce: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val bounce = if (withBounce) {
        val bounceAnimation = rememberInfiniteTransition(label = "bounce")
        bounceAnimation.animateFloat(
            initialValue = 0f,
            targetValue = -15f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = EaseInOutCubic),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bounce"
        ).value
    } else {
        0f
    }

    Box(
        modifier = Modifier
            .size(size)
            .offset(y = bounce.dp)
            .shadow(
                elevation = 16.dp,
                shape = CircleShape,
                spotColor = BrightOrange.copy(alpha = 0.4f)
            )
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(SurfaceWhite, SurfaceCream)
                ),
                shape = CircleShape
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

/**
 * Page title with fun styling
 */
@Composable
fun PuzzleBotTitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = BrightOrange
) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = 32.sp,
        fontWeight = FontWeight.ExtraBold,
        textAlign = TextAlign.Center,
        color = color,
        letterSpacing = 2.sp
    )
}

/**
 * Subtitle with emojis
 */
@Composable
fun PuzzleBotSubtitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = BrightBlue
) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        textAlign = TextAlign.Center
    )
}

/**
 * Kid-friendly text button (for "Already have account?" etc)
 */
@Composable
fun PuzzleBotTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = BrightBlue
        )
    }
}

/**
 * Kid-friendly outlined text field with rounded corners
 */
@Composable
fun PuzzleBotTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation =
        androidx.compose.ui.text.input.VisualTransformation.None,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions =
        androidx.compose.foundation.text.KeyboardOptions.Default,
    keyboardActions: androidx.compose.foundation.text.KeyboardActions =
        androidx.compose.foundation.text.KeyboardActions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                fontWeight = FontWeight.Bold
            )
        },
        modifier = modifier.fillMaxWidth(),
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        supportingText = supportingText,
        singleLine = singleLine,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BrightBlue,
            unfocusedBorderColor = Color(0xFFBDC3C7),
            focusedLabelColor = BrightBlue,
            cursorColor = BrightBlue,
            errorBorderColor = ErrorRed,
            errorLabelColor = ErrorRed
        ),
        textStyle = LocalTextStyle.current.copy(
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    )
}

