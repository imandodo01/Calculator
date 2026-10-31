package com.cl3ntions.calculator

/**
 * Evaluates basic arithmetic expressions without depending on Android UI classes.
 *
 * Supported operators:
 * +, -, *, /
 *
 * Parentheses and unary minus are also supported.
 */
class CalculatorEngine {

    fun evaluate(expression: String): Double {
        require(expression.isNotBlank()) { "Expression cannot be empty" }

        val parser = Parser(tokenize(expression))
        val result = parser.parseExpression()

        if (parser.hasNext()) {
            throw IllegalArgumentException("Unexpected token: ${parser.peek()}")
        }

        return result
    }

    private fun tokenize(expression: String): List<String> {
        val tokens = mutableListOf<String>()
        var index = 0
        var expectingValue = true

        while (index < expression.length) {
            val char = expression[index]

            when {
                char.isWhitespace() -> index++

                char.isDigit() || char == '.' -> {
                    val start = index
                    var dots = 0

                    while (index < expression.length &&
                        (expression[index].isDigit() || expression[index] == '.')
                    ) {
                        if (expression[index] == '.') dots++
                        index++
                    }

                    val number = expression.substring(start, index)
                    require(dots <= 1 && number != ".") {
                        "Invalid number: $number"
                    }

                    tokens += number
                    expectingValue = false
                }

                char == '-' && expectingValue -> {
                    tokens += "u-"
                    index++
                }

                char in "+-*/" -> {
                    require(!expectingValue) {
                        "Operator '$char' cannot appear here"
                    }
                    tokens += char.toString()
                    index++
                    expectingValue = true
                }

                char == '(' -> {
                    require(expectingValue) { "Missing operator before '('" }
                    tokens += "("
                    index++
                    expectingValue = true
                }

                char == ')' -> {
                    require(!expectingValue) { "Unexpected ')'" }
                    tokens += ")"
                    index++
                    expectingValue = false
                }

                else -> throw IllegalArgumentException("Unsupported character: $char")
            }
        }

        require(!expectingValue) { "Expression cannot end with an operator" }
        return tokens
    }

    private class Parser(private val tokens: List<String>) {
        private var position = 0

        fun hasNext(): Boolean = position < tokens.size

        fun peek(): String = tokens[position]

        private fun consume(): String = tokens[position++]

        fun parseExpression(): Double {
            var value = parseTerm()

            while (hasNext() && (peek() == "+" || peek() == "-")) {
                when (consume()) {
                    "+" -> value += parseTerm()
                    "-" -> value -= parseTerm()
                }
            }

            return value
        }

        private fun parseTerm(): Double {
            var value = parseFactor()

            while (hasNext() && (peek() == "*" || peek() == "/")) {
                when (consume()) {
                    "*" -> value *= parseFactor()
                    "/" -> {
                        val divisor = parseFactor()
                        if (divisor == 0.0) throw ArithmeticException("Division by zero")
                        value /= divisor
                    }
                }
            }

            return value
        }

        private fun parseFactor(): Double {
            if (!hasNext()) throw IllegalArgumentException("Expected a value")

            return when (val token = consume()) {
                "u-" -> -parseFactor()
                "(" -> {
                    val value = parseExpression()
                    require(hasNext() && consume() == ")") { "Missing closing parenthesis" }
                    value
                }
                ")" -> throw IllegalArgumentException("Unexpected ')'")
                else -> token.toDoubleOrNull()
                    ?: throw IllegalArgumentException("Expected a number but found: $token")
            }
        }
    }
}
