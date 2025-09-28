package com.rp.tictoc.viewmodel

import androidx.lifecycle.ViewModel
import com.rp.tictoc.gamestate.CellValue
import com.rp.tictoc.gamestate.GameEvent
import com.rp.tictoc.gamestate.GameState
import com.rp.tictoc.gamestate.GameStatus
import com.rp.tictoc.gamestate.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

/**
 * Created by Rishi Porwal
 */
class GameViewModel : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())

    val gameState: StateFlow<GameState> = _gameState

    fun onEvent(event: GameEvent) {
        when (event) {
            is GameEvent.CellClick -> handleCellClick(event.index)
            GameEvent.ResetGame -> resetGame()
            GameEvent.ResetScores -> resetScores()
        }
    }

    // Handle cell click event
    private fun handleCellClick(index: Int) {
        val currentState = _gameState.value

        // Check if move is valid
        if (currentState.board[index] != CellValue.EMPTY ||
            currentState.gameStatus != GameStatus.ONGOING) {
            return
        }

        // Update board with current player's move
        val newBoard = currentState.board.toMutableList().apply {
            this[index] = when (currentState.currentPlayer) {
                Player.X -> CellValue.X
                Player.O -> CellValue.O
            }
        }

        // Check game status after move
        val gameStatus = checkGameStatus(newBoard)
        val winningCells = if (gameStatus == GameStatus.WON) getWinningCells(newBoard) else emptyList()

        // Update scores if game is won
        val (newXScore, newOScore) = if (gameStatus == GameStatus.WON) {
            when (currentState.currentPlayer) {
                Player.X -> Pair(currentState.playerXScore + 1, currentState.playerOScore)
                Player.O -> Pair(currentState.playerXScore, currentState.playerOScore + 1)
            }
        } else {
            Pair(currentState.playerXScore, currentState.playerOScore)
        }

        // Update state with new values
        _gameState.update { state ->
            state.copy(
                board = newBoard,
                currentPlayer = if (gameStatus == GameStatus.ONGOING) {
                    // Switch player if game continues
                    when (state.currentPlayer) {
                        Player.X -> Player.O
                        Player.O -> Player.X
                    }
                } else state.currentPlayer,
                gameStatus = gameStatus,
                winningCells = winningCells,
                playerXScore = newXScore,
                playerOScore = newOScore
            )
        }
    }

    // Reset current game board
    private fun resetGame() {
        _gameState.update { state ->
            state.copy(
                board = List(9) { CellValue.EMPTY },
                currentPlayer = Player.X,
                gameStatus = GameStatus.ONGOING,
                winningCells = emptyList()
            )
        }
    }

    // Reset all scores
    private fun resetScores() {
        _gameState.update { state ->
            state.copy(
                board = List(9) { CellValue.EMPTY },
                currentPlayer = Player.X,
                gameStatus = GameStatus.ONGOING,
                winningCells = emptyList(),
                playerXScore = 0,
                playerOScore = 0
            )
        }
    }

    // Check if game is won or drawn
    private fun checkGameStatus(board: List<CellValue>): GameStatus {
        // All possible winning combinations
        val winningCombinations = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // Rows
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // Columns
            listOf(0, 4, 8), listOf(2, 4, 6) // Diagonals
        )

        // Check for winner
        winningCombinations.forEach { combination ->
            if (board[combination[0]] != CellValue.EMPTY &&
                board[combination[0]] == board[combination[1]] &&
                board[combination[1]] == board[combination[2]]) {
                return GameStatus.WON
            }
        }

        // Check for draw
        return if (board.none { it == CellValue.EMPTY }) {
            GameStatus.DRAW
        } else {
            GameStatus.ONGOING
        }
    }

    // Get winning cell indices
    private fun getWinningCells(board: List<CellValue>): List<Int> {
        val winningCombinations = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
            listOf(0, 4, 8), listOf(2, 4, 6)
        )

        winningCombinations.forEach { combination ->
            if (board[combination[0]] != CellValue.EMPTY &&
                board[combination[0]] == board[combination[1]] &&
                board[combination[1]] == board[combination[2]]) {
                return combination
            }
        }
        return emptyList()
    }
}