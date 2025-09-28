package com.rp.tictoc

import com.rp.tictoc.gamestate.CellValue
import com.rp.tictoc.gamestate.GameEvent
import com.rp.tictoc.gamestate.GameStatus
import com.rp.tictoc.viewmodel.GameViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

/**
 * Created by Rishi Porwal
 */


@OptIn(ExperimentalCoroutinesApi::class)
class GameIntegrationTest {

    @Test
    fun `complete game flow from start to win to reset`() = runTest {
        val viewModel = GameViewModel()

        // Initial state
        var state = viewModel.gameState.value
        assertTrue(state.board.all { it == CellValue.EMPTY })
        assertEquals(0, state.playerXScore)
        assertEquals(0, state.playerOScore)

        // Play until X wins
        viewModel.onEvent(GameEvent.CellClick(0)) // X
        viewModel.onEvent(GameEvent.CellClick(3)) // O
        viewModel.onEvent(GameEvent.CellClick(1)) // X
        viewModel.onEvent(GameEvent.CellClick(4)) // O
        viewModel.onEvent(GameEvent.CellClick(2)) // X wins

        state = viewModel.gameState.value
        assertEquals(GameStatus.WON, state.gameStatus)
        assertEquals(1, state.playerXScore)
        assertEquals(0, state.playerOScore)

        // Reset game
        viewModel.onEvent(GameEvent.ResetGame)

        state = viewModel.gameState.value
        assertTrue(state.board.all { it == CellValue.EMPTY })
        assertEquals(GameStatus.ONGOING, state.gameStatus)
        assertEquals(1, state.playerXScore) // Scores remain

        // Reset scores
        viewModel.onEvent(GameEvent.ResetScores)

        state = viewModel.gameState.value
        assertEquals(0, state.playerXScore)
        assertEquals(0, state.playerOScore)
    }

    @Test
    fun `edge cases for cell indices`() = runTest {
        val viewModel = GameViewModel()

        // Test valid indices
        viewModel.onEvent(GameEvent.CellClick(0))  // First cell
        viewModel.onEvent(GameEvent.CellClick(8))  // Last cell
        viewModel.onEvent(GameEvent.CellClick(4))  // Center cell

        var state = viewModel.gameState.value
        assertEquals(CellValue.X, state.board[0])
        assertEquals(CellValue.O, state.board[8])
        assertEquals(CellValue.X, state.board[4])

        // Test that game continues normally
        assertEquals(GameStatus.ONGOING, state.gameStatus)
    }
}