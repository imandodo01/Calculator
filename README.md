# Calculator

A simple Android calculator project used as a Kotlin learning example.

## Current focus

The calculator logic is separated from the Android UI so the arithmetic code can be understood and tested independently.

### Supported arithmetic

- Addition: `+`
- Subtraction: `-`
- Multiplication: `*`
- Division: `/`
- Decimal numbers
- Parentheses
- Unary minus
- Operator precedence

Examples:

```
2 + 3 * 4      = 14
(2 + 3) * 4    = 20
-2 * 5         = -10
```

## Project structure

```
app/
├── src/main/java/com/cl3ntions/calculator/
│   ├── MainActivity.kt       # Android UI entry point
│   └── CalculatorEngine.kt   # Pure arithmetic logic
└── src/test/java/com/cl3ntions/calculator/
    └── CalculatorEngineTest.kt
```

## Why separate the calculator engine?

`CalculatorEngine` does not depend on Android classes. This keeps the calculation logic:

- easier to understand
- easier to test
- reusable from different UI implementations

The engine deliberately handles only arithmetic. The Android UI should pass an expression to the engine and display either the result or an appropriate error.
