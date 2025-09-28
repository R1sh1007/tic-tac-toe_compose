// TicTacToeScreen.kt
package com.rp.tictoc

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rp.tictoc.viewmodel.GameViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rp.tictoc.gamestate.GameEvent
import com.rp.tictoc.gamestate.GameStatus


@Composable
fun TicTacToeScreen(
    viewModel: GameViewModel
) {
    val gameState = viewModel.gameState.collectAsStateWithLifecycle().value
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.background
        )
    )

    val titleScale by animateFloatAsState(
        targetValue = if (gameState.gameStatus == GameStatus.WON) 1.05f else 1f,
        label = "titleScale"
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
        ) {
            // Background pattern/dots
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.03f),
                                Color.Transparent
                            ),
                            radius = 800f
                        )
                    )
            )

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                color = Color.Transparent
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    GameStatusCard(
                        gameStatus = gameState.gameStatus,
                        currentPlayer = gameState.currentPlayer,
                        playerXScore = gameState.playerXScore,
                        playerOScore = gameState.playerOScore,
                        winningPlayer = if (gameState.gameStatus == GameStatus.WON) gameState.currentPlayer else null,
                        modifier = Modifier.fillMaxWidth()
                    )


                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),  // This ensures it takes available space without scrolling
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        itemsIndexed(gameState.board) { index, cellValue ->
                            GameCell(
                                cellValue = cellValue,
                                isWinningCell = gameState.winningCells.contains(index),
                                onClick = {
                                    if (gameState.gameStatus == GameStatus.ONGOING) {
                                        viewModel.onEvent(GameEvent.CellClick(index))
                                    }
                                }
                            )
                        }
                    }




                    // Modern Action Buttons in Row layout
                    CompactActionButtonsRow(
                        onNewGame = { viewModel.onEvent(GameEvent.ResetGame) },
                        onResetScores = { viewModel.onEvent(GameEvent.ResetScores) },
                        modifier = Modifier.fillMaxWidth()
                    )

                   // Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}