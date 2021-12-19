package com.locker.simplecalc.model

import org.junit.Assert.*
import org.junit.Test

class ExpressionTest {
    val add = OperatorType.ADD.symbol
    val sub = OperatorType.SUBTRACT.symbol
    val div = OperatorType.DIVISION.symbol
    val mul = OperatorType.PRODUCT.symbol

    @Test
    fun `adds ints`() {
        assertEquals(4, "2${add}2".parseExpression().value.value.toInt())
    }

    @Test
    fun `adds decimals`() {
        assertEquals(5.75, "4${add}1${add}0.75".parseExpression().value.value, 0.001)
    }

    @Test
    fun `adds negatives`() {
        assertEquals(-20, "${sub}30${add}10".parseExpression().value.value.toInt())
    }

    @Test
    fun `subtracts ints`() {
        assertEquals(10, "20${sub}10".parseExpression().value.value.toInt())
    }

    @Test
    fun `subtracts decimals`() {
        assertEquals(10.5, "20${sub}9.5".parseExpression().value.value, 0.001)
    }

    @Test
    fun `subtracts negatives`() {
        assertEquals(-60, "${sub}10${sub}20${sub}30".parseExpression().value.value.toInt())
    }

    @Test
    fun `multiplies ints`() {
        assertEquals(150, "3${OperatorType.PRODUCT.symbol}50".parseExpression().value.toInt())
    }

    @Test
    fun `divides ints`() {
        assertEquals(30, "150${OperatorType.DIVISION.symbol}5".parseExpression().value.toInt())
    }

    @Test
    fun `adds and subtracts`() {
        assertEquals(100, "10${add}5${sub}10${add}95${add}5${sub}5".parseExpression().value.toInt())
    }

    @Test
    fun `multiplies and divides`() {
        assertEquals(100, "10${div}5${mul}50".parseExpression().value.toInt())
    }

    @Test
    fun `complex expression concat_prod_concat`() {
        assertEquals(15, "1${add}5${add}3${mul}5${div}2${sub}7${add}8.5".parseExpression().value.toInt())
    }

    @Test
    fun `complex expression prod_concat_prod`() {
        assertEquals(7, "3${mul}5${div}3${add}5${sub}1${mul}6${div}2".parseExpression().value.toInt())
    }

    @Test
    fun `check single parenthesis`() {
        assertEquals(22, "(5${add}6)${mul}2".parseExpression().value.toInt())
    }

    @Test
    fun `check double parenthesis`() {
        assertEquals(96, "(5${mul}(5${add}1)${add}2)${mul}3".parseExpression().value.toInt())
    }

    @Test
    fun `multiply parenthesis`() {
        assertEquals(50, "5${mul}(5${add}5)".parseExpression().value.toInt())
    }

    @Test
    fun `multiply parenthesis without operator`() {
        assertEquals(50, "5(5${add}5)".parseExpression().value.toInt())
    }

    @Test
    fun `multiply parenthesis without operator complex`() {
        assertEquals(-150, "5(5${add}5)${add}10(10${sub}20)${sub}50(2)".parseExpression().value.toInt())
    }

    @Test
    fun `multiply negative parens`() {
        assertEquals(-300, "${sub}10(50${sub}20)".parseExpression().value.toInt())
    }

    @Test
    fun `multiply negative negative parens`() {
        assertEquals(300, "-10(20${sub}50)".parseExpression().value.toInt())
    }

    @Test(expected = ParseException::class)
    fun `unmatched closed paren throws exception`() {
        ")".parseExpression()
    }

    @Test
    fun `complex paren expression`() {
        assertEquals("560", "7(8(5${add}5))".parseExpression().value.toString())
    }

    @Test
    fun `divide by negative number`() {
        assertEquals("-2", "${sub}5(${sub}5${add}3)${div}${sub}5".parseExpression().value.toString())
    }

    @Test
    fun `negate parens`() {
        assertEquals("7", "${sub}(${sub}7)".parseExpression().value.toString())
    }

    @Test
    fun `subtraction after parens`() {
        assertEquals("-28", "${sub}(4${mul}5)${sub}8".parseExpression().value.toString())
    }
}