package com.cl3ntions.calculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var workingsText: TextView
    private lateinit var resultText: TextView
    private val calculator = CalculatorEngine()
    private var expression = ""
    private var state = CalculatorUiState()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        workingsText = findViewById(R.id.workingsTV)
        resultText = findViewById(R.id.resultTV)

        render()
    }

    fun numberAction(view: View) {
        val value = (view as Button).text.toString()

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
        val operation = when ((view as Button).text.toString()) {
            "×" -> "*"
            "÷" -> "/"
            "−" -> "-"
            else -> view.text.toString()
        }

        when {
            expression.isEmpty() && operation == "-" -> expression = "-"
            expression.isEmpty() -> return
            expression.last() in "+-*/" -> expression = expression.dropLast(1) + operation
            expression.last() == '(' -> {
                if (operation == "-") expression += operation else return
            }
            else -> expression += operation
        }

        render()
    }

    fun parenthesisAction(view: View) {
        val value = (view as Button).text.toString()

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
        render()
    }

    fun backSpaceAction(view: View) {
        if (expression.isNotEmpty()) {
            expression = expression.dropLast(1)
            render()
        }
    }

    fun equalsAction(view: View) {
        val evaluation = evaluateExpression()

        if (evaluation.error != null) {
            state = CalculatorUiState(
                expression = expression,
                result = evaluation.error,
                error = evaluation.error
            )
            render()
            return
        }

        expression = formatResult(requireNotNull(evaluation.value))
        render()
    }

    private fun render() {
        val evaluation = if (expression.isEmpty()) {
            Evaluation(value = 0.0)
        } else {
            evaluateExpression()
        }

        state = when {
            expression.isEmpty() -> CalculatorUiState()
            evaluation.error != null -> CalculatorUiState(
                expression = expression,
                result = evaluation.error,
                error = evaluation.error
            )
            else -> CalculatorUiState(
                expression = expression,
                result = formatResult(requireNotNull(evaluation.value))
            )
        }

        workingsText.text = if (state.expression.isEmpty()) "0" else formatExpression(state.expression)
        resultText.text = state.result
        resultText.setTextColor(
            getColor(if (state.error == null) R.color.text_primary else R.color.danger_text)
        )
    }

    private fun evaluateExpression(): Evaluation {
        return try {
            Evaluation(value = calculator.evaluate(expression))
        } catch (exception: ArithmeticException) {
            Evaluation(error = exception.message ?: "Calculation error")
        } catch (_: IllegalArgumentException) {
            // Incomplete input is normal while the user is typing.
            Evaluation(error = null)
        }
    }

    private fun formatExpression(value: String): String =
        value.replace("*", "×").replace("/", "÷")

    private fun formatResult(value: Double): String {
        return if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()
    }

    private data class Evaluation(
        val value: Double? = null,
        val error: String? = null
    )
}
