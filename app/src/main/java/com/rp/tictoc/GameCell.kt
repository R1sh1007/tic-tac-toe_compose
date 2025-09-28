package com.rp.tictoc

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rp.tictoc.gamestate.CellValue

/**
 * Enhanced GameCell with light attractive colors and winning zoom-out effect
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameCell(
    cellValue: CellValue,
    isWinningCell: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animation values - simplified
    val scaleAnim = remember { Animatable(1f) }
    val winZoomAnim = remember { Animatable(1f) }

    // Animate when cell value changes or when it's a winning cell
    LaunchedEffect(cellValue, isWinningCell) {
        if (cellValue != CellValue.EMPTY) {
            // Simple scale animation when cell gets filled
            scaleAnim.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }

        if (isWinningCell) {
            // Winning zoom-out effect: zoom in then zoom out
            winZoomAnim.animateTo(
                targetValue = 1.3f,
                animationSpec = tween(durationMillis = 300)
            )
            winZoomAnim.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        } else {
            winZoomAnim.animateTo(1f)
        }
    }

    // Light attractive colors for cells
    val cellBackground = when (cellValue) {
        CellValue.X -> Color(0xFFE3F2FD).copy(alpha = 0.8f)  // Light Blue
        CellValue.O -> Color(0xFFFCE4EC).copy(alpha = 0.8f)  // Light Pink
        CellValue.EMPTY -> Color(0xFFFAFAFA).copy(alpha = 0.9f)  // Light Gray
    }

    val borderColor = when (cellValue) {
        CellValue.X -> Brush.linearGradient(
            colors = listOf(Color(0xFF2196F3), Color(0xFF64B5F6))  // Blue gradient
        )
        CellValue.O -> Brush.linearGradient(
            colors = listOf(Color(0xFFE91E63), Color(0xFFF48FB1))  // Pink gradient
        )
        CellValue.EMPTY -> Brush.linearGradient(
            colors = listOf(Color(0xFFBDBDBD), Color(0xFFE0E0E0))  // Gray gradient
        )
    }

    val textColor = when (cellValue) {
        CellValue.X -> Color(0xFF1976D2)  // Dark Blue
        CellValue.O -> Color(0xFFC2185B)  // Dark Pink
        CellValue.EMPTY -> Color.Transparent
    }

    Card(
        onClick = onClick,
        modifier = modifier
            .aspectRatio(1f)
            .padding(1.dp)  // Keep original padding
            .scale(scaleAnim.value * winZoomAnim.value),  // Apply winning zoom
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isWinningCell) 12.dp else 4.dp,  // Subtle elevation
            pressedElevation = 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = cellBackground
        ),
        shape = RoundedCornerShape(10.dp),  // Slightly rounded corners
        border = if (isWinningCell) {
            BorderStroke(3.dp, Color(0xFFB28B01))  // Gold border for winning cells
        } else {
            BorderStroke(1.dp, borderColor)
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
            ,
            contentAlignment = Alignment.Center

        ) {
            when (cellValue) {
                CellValue.X -> SimpleXMark(isWinningCell, textColor)
                CellValue.O -> SimpleOCircle(isWinningCell, textColor)
                CellValue.EMPTY -> { /* Empty cell shows just background */ }
            }
        }
    }
}

@Composable
private fun SimpleXMark(isWinningCell: Boolean, textColor: Color) {
    val scaleAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Text(
        text = "X",
        fontSize = 52.sp,  // Original size
        fontWeight = FontWeight.Bold,
        color = if (isWinningCell) Color(0xFFB28B01) else textColor,  // Gold for winning
        modifier = Modifier.scale(scaleAnim.value),
        fontFamily = FontFamily.Monospace

    )
}

@Composable
private fun SimpleOCircle(isWinningCell: Boolean, textColor: Color) {
    val scaleAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Text(
        text = "O",
        fontSize = 52.sp,  // Original size
        fontWeight = FontWeight.Bold,
        color = if (isWinningCell) Color(0xFFFFD700) else textColor,  // Gold for winning
        modifier = Modifier.scale(scaleAnim.value),
        fontFamily = FontFamily.Monospace

    )
}