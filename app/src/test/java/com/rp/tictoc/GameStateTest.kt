package com.rp.tictoc

import com.rp.tictoc.gamestate.CellValue
import com.rp.tictoc.gamestate.GameState
import com.rp.tictoc.gamestate.GameStatus
import com.rp.tictoc.gamestate.Player
import org.junit.Assert.*
import org.junit.Test

/**
 * Created by Rishi Porwal
 */
class GameStateTest {

    @Test
    fun `game state should have correct default values`() {
        val gameState = GameState()

        assertEquals(9, gameState.board.size)
        assertTrue(gameState.board.all { it == CellValue.EMPTY })
        assertEquals(Player.X, gameState.currentPlayer)
        assertEquals(GameStatus.ONGOING, gameState.gameStatus)
        assertTrue(gameState.winningCells.isEmpty())
        assertEquals(0, gameState.playerXScore)
        assertEquals(0, gameState.playerOScore)
    }

    @Test
    fun `game state copy should work correctly`() {
        val original = GameState()
        val copied = original.copy(
            currentPlayer = Player.O,
            playerXScore = 2,
            playerOScore = 1
        )

        assertEquals(Player.O, copied.currentPlayer)
        assertEquals(2, copied.playerXScore)
        assertEquals(1, copied.playerOScore)
        assertEquals(GameStatus.ONGOING, copied.gameStatus)
    }

    @Test
    fun `cell value enum should have correct values`() {
        assertEquals(3, CellValue.values().size)
        assertTrue(CellValue.values().contains(CellValue.EMPTY))
        assertTrue(CellValue.values().contains(CellValue.X))
        assertTrue(CellValue.values().contains(CellValue.O))
    }

    @Test
    fun `player enum should have correct values`() {
        assertEquals(2, Player.values().size)
        assertEquals("X", Player.X.name)
        assertEquals("O", Player.O.name)
    }

    @Test
    fun `game status enum should have correct values`() {
        assertEquals(3, GameStatus.values().size)
        assertEquals("ONGOING", GameStatus.ONGOING.name)
        assertEquals("WON", GameStatus.WON.name)
        assertEquals("DRAW", GameStatus.DRAW.name)
    }
}