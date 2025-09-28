package com.rp.tictoc

import com.rp.tictoc.gamestate.GameEvent
import org.junit.Assert.*
import org.junit.Test

/**
 * Created by Rishi Porwal
 */
class GameEventTest {

    @Test
    fun `cell click event should store correct index`() {
        val index = 5
        val event = GameEvent.CellClick(index)

        assertEquals(index, event.index)
    }

    @Test
    fun `reset game event should be singleton`() {
        val event1 = GameEvent.ResetGame
        val event2 = GameEvent.ResetGame

        assertSame(event1, event2)
    }

    @Test
    fun `reset scores event should be singleton`() {
        val event1 = GameEvent.ResetScores
        val event2 = GameEvent.ResetScores

        assertSame(event1, event2)
    }

    @Test
    fun `all game event types should be covered`() {
        val events = listOf(
            GameEvent.CellClick(0),
            GameEvent.ResetGame,
            GameEvent.ResetScores
        )

        assertEquals(3, events.size)

        // Test type checking
        assertTrue(events[0] is GameEvent.CellClick)
        assertTrue(events[1] is GameEvent.ResetGame)
        assertTrue(events[2] is GameEvent.ResetScores)
    }
}