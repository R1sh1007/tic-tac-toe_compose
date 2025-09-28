package com.rp.tictoc.gamestate

/**
 * Created by Rishi Porwal
 */
sealed class GameEvent {
    data class CellClick(val index: Int) : GameEvent() // User clicks on cell
    object ResetGame : GameEvent() // Reset current game
    object ResetScores : GameEvent() // Reset all scores
}