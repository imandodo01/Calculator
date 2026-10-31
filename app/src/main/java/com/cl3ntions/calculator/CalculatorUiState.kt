package com.cl3ntions.calculator

/**
 * Immutable state rendered by the calculator screen.
 *
 * Keeping display state separate from the Android views makes the screen's
 * current state explicit and easier to evolve without adding a larger
 * architecture framework to this small project.
 */
data class CalculatorUiState(
    val expression: String = "",
    val result: String = "0",
    val error: String? = null
)
