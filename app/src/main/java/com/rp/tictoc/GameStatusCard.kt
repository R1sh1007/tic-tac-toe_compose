package com.rp.tictoc

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rp.tictoc.gamestate.GameStatus
import com.rp.tictoc.gamestate.Player

/**
 * Unified Game Status Card with same design as previous status card
 */
@Composable
fun GameStatusCard(
    gameStatus: GameStatus,
    currentPlayer: Player,
    playerXScore: Int,
    playerOScore: Int,
    winningPlayer: Player? = null,
    modifier: Modifier = Modifier
) {
    val scaleAnim = remember { Animatable(1f) }
    val infiniteTransition = rememberInfiniteTransition()

    val pulseAnimation = infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(gameStatus) {
        when (gameStatus) {
            GameStatus.WON -> {
                // Celebration animation sequence
                repeat(3) {
                    scaleAnim.animateTo(1.2f, tween(200))
                    scaleAnim.animateTo(1f, tween(200))
                }
            }
            GameStatus.DRAW -> {
                scaleAnim.animateTo(1.1f, tween(300))
                scaleAnim.animateTo(1f, tween(300))
            }
            else -> scaleAnim.animateTo(1f)
        }
    }

    val (statusText, backgroundColor, textColor) = when (gameStatus) {
        GameStatus.ONGOING -> Triple(
            "Player ${currentPlayer.name} Turn",
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.onSurfaceVariant
        )
        GameStatus.WON -> Triple(
            "🎉 ${winningPlayer?.name} WINS! 🎉",
            if (winningPlayer == Player.X) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.secondaryContainer,
            if (winningPlayer == Player.X) MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.onSecondaryContainer
        )
        GameStatus.DRAW -> Triple(
            "🤝 It's a Draw!",
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer
        )
    }

    val gradientBrush = when (gameStatus) {
        GameStatus.WON -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFE0F2F1).copy(alpha = 0.8f),  // Light Teal
                Color(0xFFB2DFDB).copy(alpha = 0.9f),  // Soft Teal
                Color(0xFFF3E5F5).copy(alpha = 0.7f),  // Light Purple
                Color(0xFFE1BEE7).copy(alpha = 0.8f)   // Soft Purple
            )
        )
        GameStatus.DRAW -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFF8E1).copy(alpha = 0.9f),  // Light Amber
                Color(0xFFFFECB3).copy(alpha = 0.8f),  // Warm Yellow
                Color(0xFFFFE0B2).copy(alpha = 0.7f),  // Soft Orange
                Color(0xFFFFCCBC).copy(alpha = 0.6f)   // Peach
            )
        )
        else -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFEBEE).copy(alpha = 0.9f),  // Light Pink
                Color(0xFFFFCDD2).copy(alpha = 0.8f),  // Soft Coral
                Color(0xFFE3F2FD).copy(alpha = 0.7f),  // Light Blue
                Color(0xFFBBDEFB).copy(alpha = 0.6f)   // Sky Blue
            )
           // colors = listOf(backgroundColor, backgroundColor.copy(alpha = 0.7f))
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp,0.dp,10.dp,0.dp)
            .scale(if (gameStatus == GameStatus.WON) pulseAnimation.value else scaleAnim.value),

        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(15.dp))
                .background(gradientBrush)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "TIC TAC TOE",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.Monospace // 🎮 Gaming look
                )
                // Game Status Text (Same as previous design)
                Text(
                    text = statusText,
                    fontSize = when (gameStatus) {
                        GameStatus.WON -> 24.sp
                        GameStatus.DRAW -> 22.sp
                        else -> 20.sp
                    },
                    fontWeight = FontWeight.Black,
                    color = textColor,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp,
                            fontFamily = FontFamily.Monospace
                )

                // VS Score Section (Same design as previous ScoreCard)
                VSScoreSection(
                    playerXScore = playerXScore,
                    playerOScore = playerOScore,
                    currentPlayer = currentPlayer,
                    textColor = textColor
                )

                // Status Message (Same as previous design)
                if (gameStatus != GameStatus.ONGOING) {
                    Text(
                        text = "Tap New Game to continue the battle!",
                        fontSize = 12.sp,
                        color = textColor.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp),
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace
                    )
                } else {
                    Text(
                        text = "Make your move strategically!",
                        fontSize = 12.sp,
                        color = textColor.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp),
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

/**
 * VS Score Section - Same design as your previous ScoreCard
 */
@Composable
private fun VSScoreSection(
    playerXScore: Int,
    playerOScore: Int,
    currentPlayer: Player,
    textColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Player X Score Item
        PlayerScoreItem(
            playerName = "PLAYER X",
            score = playerXScore,
            isActive = currentPlayer == Player.X,
            isPlayerX = true,
            textColor = textColor,
        )

        // VS Separator with modern design (Same as previous)
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.primary
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "VS",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.scale(1.1f),
                fontFamily = FontFamily.Monospace
            )
        }

        // Player O Score Item
        PlayerScoreItem(
            playerName = "PLAYER O",
            score = playerOScore,
            isActive = currentPlayer == Player.O,
            isPlayerX = false,
            textColor = textColor,
        )
    }
}

/**
 * Player Score Item - Same design as your previous ScoreCard
 */
@Composable
private fun PlayerScoreItem(
    playerName: String,
    score: Int,
    isActive: Boolean,
    isPlayerX: Boolean,
    textColor: Color
) {
    val scaleAnim = remember { Animatable(1f) }

    LaunchedEffect(isActive) {
        if (isActive) {
            scaleAnim.animateTo(
                targetValue = 1.05f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        } else {
            scaleAnim.animateTo(1f)
        }
    }

    val playerColor = if (isPlayerX) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.secondary

    val gradientBrush = if (isActive) {
        Brush.verticalGradient(
            colors = listOf(
                playerColor.copy(alpha = 0.2f),
                playerColor.copy(alpha = 0.1f)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
                Color.Transparent
            )
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(scaleAnim.value)
            .clip(RoundedCornerShape(16.dp))
            .background(gradientBrush)
            .padding(16.dp)
    ) {
        Text(
            text = playerName,
            fontSize = 14.sp,
            fontWeight = if (isActive) FontWeight.Black else FontWeight.Medium,
            color = if (isActive) playerColor
            else textColor.copy(alpha = 0.6f),
            letterSpacing = 1.sp,
            fontFamily = FontFamily.Monospace
        )

        Text(
            text = score.toString(),
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            color = playerColor,
            modifier = Modifier.padding(top = 4.dp),
            fontFamily = FontFamily.Monospace
        )
    }
}

/**
 * Alternative compact version with horizontal layout
 */
@Composable
fun CompactGameStatusCard(
    gameStatus: GameStatus,
    currentPlayer: Player,
    playerXScore: Int,
    playerOScore: Int,
    winningPlayer: Player? = null,
    modifier: Modifier = Modifier
) {
    val (statusText, backgroundColor, textColor) = when (gameStatus) {
        GameStatus.ONGOING -> Triple(
            "Current: ${currentPlayer.name}",
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.onSurfaceVariant
        )
        GameStatus.WON -> Triple(
            "🎉 ${winningPlayer?.name} WINS!",
            if (winningPlayer == Player.X) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.secondaryContainer,
            if (winningPlayer == Player.X) MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.onSecondaryContainer
        )
        GameStatus.DRAW -> Triple(
            "🤝 Draw!",
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(backgroundColor, backgroundColor.copy(alpha = 0.8f))
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status Text
                Column {
                    Text(
                        text = statusText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (gameStatus == GameStatus.ONGOING) "Your turn!" else "New game?",
                        fontSize = 12.sp,
                        color = textColor.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )
                }

                // VS Scores
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ScoreChip(
                        player = "X",
                        score = playerXScore,
                        isActive = currentPlayer == Player.X,
                        isPlayerX = true
                    )

                    Text(
                        text = "VS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor.copy(alpha = 0.6f)
                    )

                    ScoreChip(
                        player = "O",
                        score = playerOScore,
                        isActive = currentPlayer == Player.O,
                        isPlayerX = false
                    )
                }
            }
        }
    }
}

@Composable
private fun ScoreChip(
    player: String,
    score: Int,
    isActive: Boolean,
    isPlayerX: Boolean
) {
    val playerColor = if (isPlayerX) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.secondary

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isActive) playerColor.copy(alpha = 0.2f) else Color.Transparent)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = player,
                fontSize = 14.sp,
                fontWeight = if (isActive) FontWeight.Black else FontWeight.Medium,
                color = if (isActive) playerColor else playerColor.copy(alpha = 0.7f),
                fontFamily = FontFamily.Monospace

            )
            Text(
                text = score.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = playerColor,
                modifier = Modifier.padding(start = 4.dp),
                fontFamily = FontFamily.Monospace

            )
        }
    }
}