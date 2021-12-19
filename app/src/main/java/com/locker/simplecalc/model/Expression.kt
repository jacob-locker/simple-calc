package com.locker.simplecalc.model

import java.lang.NumberFormatException
import java.lang.StringBuilder
import java.util.*

class ValueWrapper(val value: Double) {
    override fun toString(): String {
        val valueAsString = value.toString()
        if (valueAsString[valueAsString.length - 2] == '.' && valueAsString[valueAsString.length - 1] == '0') {
            return value.toInt().toString()
        }
        return valueAsString
    }

    fun toInt() = value.toInt()
}
sealed class Expression {
    abstract val value: ValueWrapper
}

class Literal(valAsDouble: Double) : Expression() {
    override val value: ValueWrapper = ValueWrapper(valAsDouble)
}

abstract class Operator(val left: Expression, val right: Expression, val symbol: Char) :
    Expression()

class AddOperator(left: Expression, right: Expression) : Operator(left, right, '+') {
    override val value = ValueWrapper(left.value.value + right.value.value)
}

class SubtractOperator(left: Expression, right: Expression) : Operator(left, right, '-') {
    override val value = ValueWrapper(left.value.value - right.value.value)
}

class ProductOperator(left: Expression, right: Expression) : Operator(left, right, '\u2A2F') {
    override val value = ValueWrapper(left.value.value * right.value.value)
}

class DivisionOperator(left: Expression, right: Expression) : Operator(left, right, '\u00F7') {
    override val value = ValueWrapper(left.value.value / right.value.value)
}

fun Char.toOperatorType() = OperatorType.values().find { it.symbol == this }

fun Char.isOperator() = toOperatorType() != null

enum class OperatorType(val symbol: Char, val order: Int) {
    ADD('+', 3),
    SUBTRACT('\u2212', 3),
    PRODUCT('\u2A2F', 2),
    DIVISION('\u00F7', 2)
}

enum class OperatorEffectiveType {
    CONCAT,
    MULT
}

fun Operator(type: OperatorType, left: Expression, right: Expression) = when (type) {
    OperatorType.ADD -> AddOperator(left, right)
    OperatorType.SUBTRACT -> SubtractOperator(left, right)
    OperatorType.PRODUCT -> ProductOperator(left, right)
    OperatorType.DIVISION -> DivisionOperator(left, right)
}

val orderOfOperations = OperatorType.values().sortedBy { it.order }

val reverseOrderOfOperations = orderOfOperations
    .reversed()
    .map { it.symbol }

class ParseException(msg: String) : Exception(msg)

private fun String.digitsBeforeAllSubOps(): Boolean {
    if (isEmpty()) {
        return true
    }

    if (get(0) == OperatorType.SUBTRACT.symbol) {
        return false
    }

    forEachIndexed { index, c ->
        if (index < lastIndex && get(index + 1) == OperatorType.SUBTRACT.symbol && !c.isDigit() && c != ')') {
            return false
        }
    }

    return true
}

private fun String.sanitizeSubtractionOps(): String {
    if (isEmpty()) {
        return this
    }

    val sanitizedBuilder = StringBuilder()
    forEachIndexed { index, c ->
        if (c == OperatorType.SUBTRACT.symbol && index == 0) {
            sanitizedBuilder.append('0')
        }

        if (c == OperatorType.SUBTRACT.symbol && (index > 0 && !get(index - 1).isDigit() && get(index - 1) != ')')) {
            sanitizedBuilder.append('-')
        } else {
            sanitizedBuilder.append(c)
        }
    }

    return sanitizedBuilder.toString()
}

private fun String.sanitizeParentheticExpression(): String {
    val sanitizedBuilder = StringBuilder()
    forEachIndexed { index, c ->
        sanitizedBuilder.append(c)
        if (index < lastIndex && c.isDigit() && get(index + 1) == '(') {
            sanitizedBuilder.append(OperatorType.PRODUCT.symbol)
        }  else if (index < lastIndex && c == ')' && get(index + 1).isDigit()) {
            sanitizedBuilder.append(OperatorType.PRODUCT.symbol)
        }
    }
    return sanitizedBuilder.toString()
}


private fun String.condenseFromParentheses(): String {
    var condensedString = sanitizeParentheticExpression()
    val openParenIdxStack = Stack<Int>()
    if (contains('(') || contains(')')) {
        var idx = condensedString.indexOfFirst { it == '(' }

        if (idx != -1) {
            openParenIdxStack.push(idx)
            while (openParenIdxStack.isNotEmpty()) {
                idx++
                if (idx >= condensedString.length) {
                    throw ParseException("Invalid parenthesis format")
                }
                if (condensedString[idx] == ')') {
                    val startIdx = openParenIdxStack.pop()
                    val newString = condensedString.replaceRange(
                        IntRange(startIdx, idx),
                        condensedString.substring(startIdx + 1, idx)
                            .parseExpression().value.value.toString()
                    )
                    idx -= (condensedString.length - newString.length)
                    condensedString = newString
                } else if (condensedString[idx] == '(') {
                    openParenIdxStack.push(idx)
                }
            }
        } else {
            throw ParseException("Invalid parenthesis format")
        }
    }

    return condensedString
}

fun String.parseExpression(): Expression {
    if (!digitsBeforeAllSubOps()) {
        return sanitizeSubtractionOps().parseExpression()
    }

    if (contains('(') || contains(')')) {
        return condenseFromParentheses().parseExpression()
    }

    var opIndex = indexOfFirstReversedOp()
    return if (opIndex == -1) {
        try {
            Literal(toDouble())
        } catch (e: NumberFormatException) {
            throw ParseException("Invalid expression")
        }
    } else {
        val currentOpOrder = get(opIndex).toOperatorType()!!.order
        var nextOp = get(opIndex).toOperatorType()
        var nextOpIndex: Int
        while (nextOp != null && nextOp.order == currentOpOrder) {
            nextOpIndex = indexOfFirstReversedOp(start = opIndex + 1)
            nextOp = if (nextOpIndex != -1) get(nextOpIndex).toOperatorType() else null
            if (nextOp != null && nextOp.order == currentOpOrder) {
                opIndex = nextOpIndex
            }
        }

        Operator(
            get(opIndex).toOperatorType()!!,
            left = substring(0, opIndex).parseExpression(),
            right = substring(opIndex + 1, length).parseExpression()
        )
    }
}

fun String.indexOfFirstReversedOp(start: Int = 0, end: Int = lastIndex): Int {
    val eval = substring(IntRange(start, end))
    reverseOrderOfOperations.forEach { op ->
        eval.indexOfFirst { it == op }.let { idx ->
            if (idx != -1) {
                return start + idx
            }
        }
    }
    return -1
}