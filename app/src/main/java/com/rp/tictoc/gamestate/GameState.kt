package com.rp.tictoc.gamestate

/**
* Created by Rishi Porwal 
*/
data class GameState(val board: List<CellValue> = List(9) { CellValue.EMPTY }, // 3x3 game board
                       val currentPlayer: Player = Player.X, // Track current player
                       val gameStatus: GameStatus = GameStatus.ONGOING, // Game status
                       val winningCells: List<Int> = emptyList(), // Winning combination indices
                       val playerXScore: Int = 0, // Player X score
                       val playerOScore: Int = 0, // Player O score)
)

// Represents value in each cell
enum class CellValue {
    EMPTY, X, O
}

// Represents players
enum class Player {
    X, O
}

// Game status possibilities
enum class GameStatus {
    ONGOING, WON, DRAW
}