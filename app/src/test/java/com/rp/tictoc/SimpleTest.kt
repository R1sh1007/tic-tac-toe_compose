package com.rp.tictoc

import org.junit.Test
import org.junit.Assert.*

/**
 * Created by Rishi Porwal
 */
class SimpleTest {

    @Test
    fun `simple addition test`() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun `simple string test`() {
        val message = "Hello"
        assertEquals("Hello", message)
    }
}