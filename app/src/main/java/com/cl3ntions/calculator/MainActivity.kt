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
        val result = evaluateExpression() ?: return
        expression = formatResult(result)
        render(finalResult = true)
    }

    private fun render(finalResult: Boolean = false) {
        workingsText.text = if (expression.isEmpty()) "0" else formatExpression(expression)

        if (expression.isEmpty()) {
            resultText.text = "0"
            return
        }

        val result = evaluateExpression()
        resultText.text = when {
            finalResult && result != null -> formatResult(result)
            result != null -> formatResult(result)
            else -> ""
        }
    }

    private fun evaluateExpression(): Double? {
        return try {
            calculator.evaluate(expression)
        } catch (_: IllegalArgumentException) {
            null
        } catch (_: ArithmeticException) {
            null
        }
    }

    private fun formatExpression(value: String): String =
        value.replace("*", "×").replace("/", "÷")

    private fun formatResult(value: Double): String {
        return if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()
    }
}
