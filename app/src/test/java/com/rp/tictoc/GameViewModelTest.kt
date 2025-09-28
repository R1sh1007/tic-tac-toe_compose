package com.rp.tictoc

import com.rp.tictoc.gamestate.*
import com.rp.tictoc.viewmodel.GameViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test


/**
 * Created by Rishi Porwal
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {


    private lateinit var viewModel: GameViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        viewModel = GameViewModel()
    }

    @Test
    fun `initial game state should be correct`() = runTest {
        val initialState = viewModel.gameState.value

        assertEquals(9, initialState.board.size)
        assertTrue(initialState.board.all { it == CellValue.EMPTY })
        assertEquals(Player.X, initialState.currentPlayer)
        assertEquals(GameStatus.ONGOING, initialState.gameStatus)
        assertEquals(0, initialState.playerXScore)
        assertEquals(0, initialState.playerOScore)
    }

    @Test
    fun `cell click should place X on empty cell`() = runTest {
        viewModel.onEvent(GameEvent.CellClick(0))

        val state = viewModel.gameState.value
        assertEquals(CellValue.X, state.board[0])
        assertEquals(Player.O, state.currentPlayer)
    }

    @Test
    fun `cell click should not place on occupied cell`() = runTest {
        // First click places X
        viewModel.onEvent(GameEvent.CellClick(0))
        // Second click on same cell should not change it
        viewModel.onEvent(GameEvent.CellClick(0))

        val state = viewModel.gameState.value
        assertEquals(CellValue.X, state.board[0]) // Should remain X, not O
    }

    @Test
    fun `player should alternate after each move`() = runTest {
        viewModel.onEvent(GameEvent.CellClick(0)) // X moves
        assertEquals(Player.O, viewModel.gameState.value.currentPlayer)

        viewModel.onEvent(GameEvent.CellClick(1)) // O moves
        assertEquals(Player.X, viewModel.gameState.value.currentPlayer)
    }

    @Test
    fun `winning condition for row should be detected`() = runTest {
        // Create winning condition for X in first row
        viewModel.onEvent(GameEvent.CellClick(0)) // X
        viewModel.onEvent(GameEvent.CellClick(3)) // O
        viewModel.onEvent(GameEvent.CellClick(1)) // X
        viewModel.onEvent(GameEvent.CellClick(4)) // O
        viewModel.onEvent(GameEvent.CellClick(2)) // X wins

        val state = viewModel.gameState.value
        assertEquals(GameStatus.WON, state.gameStatus)
        assertEquals(Player.X, state.currentPlayer)
        assertEquals(listOf(0, 1, 2), state.winningCells)
        assertEquals(1, state.playerXScore)
        assertEquals(0, state.playerOScore)
    }

    @Test
    fun `winning condition for column should be detected`() = runTest {
        // Create winning condition for O in first column
        viewModel.onEvent(GameEvent.CellClick(1)) // X
        viewModel.onEvent(GameEvent.CellClick(0)) // O
        viewModel.onEvent(GameEvent.CellClick(2)) // X
        viewModel.onEvent(GameEvent.CellClick(3)) // O
        viewModel.onEvent(GameEvent.CellClick(5)) // X
        viewModel.onEvent(GameEvent.CellClick(6)) // O wins

        val state = viewModel.gameState.value
        assertEquals(GameStatus.WON, state.gameStatus)
        assertEquals(Player.O, state.currentPlayer)
        assertEquals(listOf(0, 3, 6), state.winningCells)
    }

    @Test
    fun `winning condition for diagonal should be detected`() = runTest {
        // Create winning condition for X in diagonal
        viewModel.onEvent(GameEvent.CellClick(0)) // X
        viewModel.onEvent(GameEvent.CellClick(1)) // O
        viewModel.onEvent(GameEvent.CellClick(4)) // X
        viewModel.onEvent(GameEvent.CellClick(2)) // O
        viewModel.onEvent(GameEvent.CellClick(8)) // X wins

        val state = viewModel.gameState.value
        assertEquals(GameStatus.WON, state.gameStatus)
        assertEquals(listOf(0, 4, 8), state.winningCells)
    }

    @Test
    fun `draw condition should be detected`() = runTest {
        // Create a draw scenario
        val moves = listOf(0, 1, 2, 4, 3, 5, 7, 6, 8)
        moves.forEach { index ->
            viewModel.onEvent(GameEvent.CellClick(index))
        }

        val state = viewModel.gameState.value
        assertEquals(GameStatus.DRAW, state.gameStatus)
        assertTrue(state.winningCells.isEmpty())
        assertEquals(0, state.playerXScore)
        assertEquals(0, state.playerOScore)
    }

    @Test
    fun `reset game should clear board but keep scores`() = runTest {
        // Make some moves and win
        viewModel.onEvent(GameEvent.CellClick(0)) // X
        viewModel.onEvent(GameEvent.CellClick(3)) // O
        viewModel.onEvent(GameEvent.CellClick(1)) // X
        viewModel.onEvent(GameEvent.CellClick(4)) // O
        viewModel.onEvent(GameEvent.CellClick(2)) // X wins

        // Reset game
        viewModel.onEvent(GameEvent.ResetGame)

        val state = viewModel.gameState.value
        assertTrue(state.board.all { it == CellValue.EMPTY })
        assertEquals(Player.X, state.currentPlayer)
        assertEquals(GameStatus.ONGOING, state.gameStatus)
        assertTrue(state.winningCells.isEmpty())
        // Scores should remain
        assertEquals(1, state.playerXScore)
        assertEquals(0, state.playerOScore)
    }

    @Test
    fun `reset scores should clear everything`() = runTest {
        // Make some moves and win
        viewModel.onEvent(GameEvent.CellClick(0)) // X
        viewModel.onEvent(GameEvent.CellClick(3)) // O
        viewModel.onEvent(GameEvent.CellClick(1)) // X
        viewModel.onEvent(GameEvent.CellClick(4)) // O
        viewModel.onEvent(GameEvent.CellClick(2)) // X wins

        // Reset scores
        viewModel.onEvent(GameEvent.ResetScores)

        val state = viewModel.gameState.value
        assertTrue(state.board.all { it == CellValue.EMPTY })
        assertEquals(Player.X, state.currentPlayer)
        assertEquals(GameStatus.ONGOING, state.gameStatus)
        assertTrue(state.winningCells.isEmpty())
        assertEquals(0, state.playerXScore)
        assertEquals(0, state.playerOScore)
    }

    @Test
    fun `no moves should be allowed after game is won`() = runTest {
        // Create winning condition
        viewModel.onEvent(GameEvent.CellClick(0)) // X
        viewModel.onEvent(GameEvent.CellClick(3)) // O
        viewModel.onEvent(GameEvent.CellClick(1)) // X
        viewModel.onEvent(GameEvent.CellClick(4)) // O
        viewModel.onEvent(GameEvent.CellClick(2)) // X wins

        val stateAfterWin = viewModel.gameState.value
        assertEquals(GameStatus.WON, stateAfterWin.gameStatus)

        // Try to make another move
        viewModel.onEvent(GameEvent.CellClick(5))

        // Board should not change
        val stateAfterInvalidMove = viewModel.gameState.value
        assertEquals(CellValue.EMPTY, stateAfterInvalidMove.board[5])
    }

//    @Test
//    fun `multiple wins should accumulate scores correctly`() = runTest {
//        // Collect states to track changes
//        val states = mutableListOf<GameState>()
//        val job = launch {
//            viewModel.gameState.collect { state ->
//                states.add(state)
//            }
//        }
//
//        try {
//            // First win for X
//            viewModel.onEvent(GameEvent.CellClick(0)) // X
//            viewModel.onEvent(GameEvent.CellClick(3)) // O
//            viewModel.onEvent(GameEvent.CellClick(1)) // X
//            viewModel.onEvent(GameEvent.CellClick(4)) // O
//            viewModel.onEvent(GameEvent.CellClick(2)) // X wins
//
//            // Wait a bit for state to update
//            advanceUntilIdle()
//
//            val firstWinState = viewModel.gameState.value
//            assertEquals(1, firstWinState.playerXScore)
//
//            // Reset game
//            viewModel.onEvent(GameEvent.ResetGame)
//            advanceUntilIdle()
//
//            // Second win for O
//            viewModel.onEvent(GameEvent.CellClick(0)) // X
//            viewModel.onEvent(GameEvent.CellClick(1)) // O
//            viewModel.onEvent(GameEvent.CellClick(3)) // X
//            viewModel.onEvent(GameEvent.CellClick(4)) // O
//            viewModel.onEvent(GameEvent.CellClick(6)) // X
//            viewModel.onEvent(GameEvent.CellClick(7)) // O wins
//
//            advanceUntilIdle()
//
//            val finalState = viewModel.gameState.value
//            assertEquals(1, finalState.playerXScore)
//            assertEquals(1, finalState.playerOScore)
//        } finally {
//            job.cancel()
//        }
//    }
//    }
}