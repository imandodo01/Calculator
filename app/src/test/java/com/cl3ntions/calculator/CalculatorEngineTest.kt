package com.cl3ntions.calculator

import org.junit.Assert.assertEquals
import org.junit.Test

class CalculatorEngineTest {

    private val calculator = CalculatorEngine()

    @Test
    fun `adds numbers`() {
        assertEquals(5.0, calculator.evaluate("2 + 3"), 0.000001)
    }

    @Test
    fun `respects multiplication precedence`() {
        assertEquals(14.0, calculator.evaluate("2 + 3 * 4"), 0.000001)
    }

    @Test
    fun `supports parentheses`() {
        assertEquals(20.0, calculator.evaluate("(2 + 3) * 4"), 0.000001)
    }

    @Test
    fun `supports decimal values`() {
        assertEquals(3.75, calculator.evaluate("1.5 * 2.5"), 0.000001)
    }

    @Test
    fun `supports unary minus`() {
        assertEquals(-10.0, calculator.evaluate("-2 * 5"), 0.000001)
    }

    @Test
    fun `supports negative values in parentheses`() {
        assertEquals(1.0, calculator.evaluate("5 + (-4)"), 0.000001)
    }

    @Test
    fun `handles nested parentheses`() {
        assertEquals(20.0, calculator.evaluate("((2 + 3) * 4)"), 0.000001)
    }

    @Test
    fun `handles repeated unary minus`() {
        assertEquals(5.0, calculator.evaluate("--5"), 0.000001)
    }

    @Test
    fun `ignores whitespace`() {
        assertEquals(14.0, calculator.evaluate("  2 + 3 * 4  "), 0.000001)
    }

    @Test
    fun `keeps floating point results`() {
        assertEquals(1.0 / 3.0, calculator.evaluate("1 / 3"), 0.000001)
    }

    @Test
    fun `handles large but valid numbers`() {
        assertEquals(3000000000.0, calculator.evaluate("1000000000 * 3"), 0.000001)
    }

    @Test(expected = ArithmeticException::class)
    fun `rejects division by zero`() {
        calculator.evaluate("10 / 0")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects invalid characters`() {
        calculator.evaluate("2 + hello")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects incomplete expressions`() {
        calculator.evaluate("2 +")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects malformed decimal numbers`() {
        calculator.evaluate("1.2.3 + 4")
    }
}
