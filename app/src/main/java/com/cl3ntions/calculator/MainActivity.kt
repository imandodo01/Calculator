package com.cl3ntions.calculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * Android UI for the calculator.
 *
 * The activity owns only screen interaction and delegates arithmetic to
 * [CalculatorEngine].
 */
class MainActivity : AppCompatActivity() {

    private lateinit var workingsText: TextView
    private lateinit var resultText: TextView

    private val calculator = CalculatorEngine()
    private var expression = ""
    private var showingResult = false
    private var state = CalculatorUiState()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        workingsText = findViewById(R.id.workingsTV)
        resultText = findViewById(R.id.resultTV)

        expression = savedInstanceState?.getString(KEY_EXPRESSION).orEmpty()
        showingResult = savedInstanceState?.getBoolean(KEY_SHOWING_RESULT, false) ?: false
        render()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_EXPRESSION, expression)
        outState.putBoolean(KEY_SHOWING_RESULT, showingResult)
    }

    fun numberAction(view: View) {
        val value = (view as Button).text.toString()

        if (showingResult) {
            expression = ""
            showingResult = false
        }

        if (value == ".") {
            val currentNumber = expression.takeLastWhile { it.isDigit() || it == '.' }
            if (currentNumber.contains(".")) return
            expression += if (currentNumber.isEmpty()) "0." else "."
        } else {
            expression += value
        }

        render()
    }

    fun operationAction(view: View) {
        val operation = normalizeOperator((view as Button).text.toString())
        showingResult = false

        when {
            expression.isEmpty() && operation == "-" -> expression = "-"
            expression.isEmpty() -> return
            expression.last() in "+-*/" -> expression = expression.dropLast(1) + operation
            expression.last() == '(' && operation == "-" -> expression += operation
            expression.last() != '(' -> expression += operation
        }

        render()
    }

    fun parenthesisAction(view: View) {
        val value = (view as Button).text.toString()

        if (showingResult && value == "(") {
            expression = ""
            showingResult = false
        }

        if (value == "(") {
            if (expression.isNotEmpty() && (expression.last().isDigit() || expression.last() == ')')) {
                expression += "*"
            }
            expression += "("
        } else {
            val opens = expression.count { it == '(' }
            val closes = expression.count { it == ')' }

            if (opens > closes && expression.isNotEmpty() &&
                (expression.last().isDigit() || expression.last() == ')')
            ) {
                expression += ")"
            }
        }

        render()
    }

    fun allClearAction(view: View) {
        expression = ""
        showingResult = false
        render()
    }

    fun backSpaceAction(view: View) {
        if (showingResult) {
            expression = ""
            showingResult = false
        } else if (expression.isNotEmpty()) {
            expression = expression.dropLast(1)
        }
        render()
    }

    fun equalsAction(view: View) {
        when (val evaluation = evaluateExpression()) {
            is Evaluation.Success -> {
                expression = formatResult(evaluation.value)
                showingResult = true
                state = CalculatorUiState(expression = expression, result = expression)
                renderState()
            }

            is Evaluation.Error -> {
                state = CalculatorUiState(
                    expression = expression,
                    result = evaluation.message,
                    error = evaluation.message
                )
                renderState()
            }

            Evaluation.Incomplete -> {
                state = CalculatorUiState(
                    expression = expression,
                    result = "Invalid expression",
                    error = "Invalid expression"
                )
                renderState()
            }
        }
    }

    private fun render() {
        if (expression.isEmpty()) {
            state = CalculatorUiState()
            renderState()
            return
        }

        state = when (val evaluation = evaluateExpression()) {
            is Evaluation.Success -> CalculatorUiState(
                expression = expression,
                result = formatResult(evaluation.value)
            )

            is Evaluation.Error -> CalculatorUiState(
                expression = expression,
                result = evaluation.message,
                error = evaluation.message
            )

            Evaluation.Incomplete -> CalculatorUiState(
                expression = expression,
                result = ""
            )
        }

        renderState()
    }

    private fun renderState() {
        workingsText.text = if (state.expression.isEmpty()) "0" else formatExpression(state.expression)
        resultText.text = state.result
        resultText.setTextColor(
            getColor(if (state.error == null) R.color.text_primary else R.color.danger_text)
        )
    }

    private fun evaluateExpression(): Evaluation {
        return try {
            Evaluation.Success(calculator.evaluate(expression))
        } catch (exception: ArithmeticException) {
            Evaluation.Error(exception.message ?: "Calculation error")
        } catch (_: IllegalArgumentException) {
            // Invalid or unfinished expressions are normal while the user is typing.
            Evaluation.Incomplete
        }
    }

    private fun normalizeOperator(value: String): String = when (value) {
        "×" -> "*"
        "÷" -> "/"
        "−" -> "-"
        else -> value
    }

    private fun formatExpression(value: String): String =
        value.replace("*", "×").replace("/", "÷")

    private fun formatResult(value: Double): String =
        if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()

    private sealed interface Evaluation {
        data class Success(val value: Double) : Evaluation
        data class Error(val message: String) : Evaluation
        data object Incomplete : Evaluation
    }

    companion object {
        private const val KEY_EXPRESSION = "expression"
        private const val KEY_SHOWING_RESULT = "showing_result"
    }
}
