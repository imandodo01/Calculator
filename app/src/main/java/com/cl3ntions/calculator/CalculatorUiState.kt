package com.cl3ntions.calculator

/**
 * Immutable representation of the values currently shown on the calculator screen.
 */
data class CalculatorUiState(
    val expression: String = "",
    val result: String = "0",
    val error: String? = null
)
