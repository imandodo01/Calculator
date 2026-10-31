package com.cl3ntions.calculator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CalculatorUiStateTest {

    @Test
    fun `uses sensible default calculator values`() {
        val state = CalculatorUiState()

        assertEquals("", state.expression)
        assertEquals("0", state.result)
        assertNull(state.error)
    }

    @Test
    fun `copy creates a new state without changing the original`() {
        val initial = CalculatorUiState(expression = "2 + 2")
        val updated = initial.copy(result = "4")

        assertEquals("0", initial.result)
        assertEquals("4", updated.result)
    }
}
