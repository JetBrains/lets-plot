/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import org.jetbrains.letsPlot.commons.formatting.number.BigFloat.Companion.ZERO
import org.jetbrains.letsPlot.commons.formatting.number.BigFloat.Companion.fromNumber
import org.jetbrains.letsPlot.commons.formatting.number.BigFloat.Companion.fromScientific
import kotlin.test.Test
import kotlin.test.assertEquals

class FloatingTest {
    @Test
    fun invariants() {
        assertEquals(ZERO, fromScientific(0, "", 0))
        assertEquals(ZERO, fromScientific(0, "000000", 0))
        assertEquals(ZERO, fromScientific(0, "000000", -5))

        assertEquals(fromNumber(1.0e-1), fromScientific(1, "0", -1))
        assertEquals(fromNumber(1.0e-2), fromScientific(1, "0", -2))
        assertEquals(fromNumber(1.0e-3), fromScientific(1, "0", -3))

        runCatching { fromScientific(10, "0", 0) }.onFailure { assertEquals("i should be in 0..9, but was 10", it.message) }
    }

    @Test
    fun zeroDecimalPart() {
        assertEquals("0", ZERO.decimalPart)
        assertEquals("0", fromScientific(1, "0", 0).decimalPart)
        assertEquals("1", fromScientific(1, "0", -1).decimalPart)
        assertEquals("01", fromScientific(1, "0", -2).decimalPart)
        assertEquals("001", fromScientific(1, "0", -3).decimalPart)
        assertEquals("0", fromScientific(1, "0", 0).decimalPart)
        assertEquals("0", fromScientific(1, "0", 1).decimalPart)
        assertEquals("0", fromScientific(1, "0", 2).decimalPart)
    }

    @Test
    fun nonZeroDecimalPart() {
        assertEquals("2", fromScientific(2, "0", -1).decimalPart)
        assertEquals("2", fromScientific(1, "2", 0).decimalPart)
        assertEquals("12", fromScientific(1, "2", -1).decimalPart)
        assertEquals("012", fromScientific(1, "2", -2).decimalPart)
        assertEquals("0012", fromScientific(1, "2", -3).decimalPart)

        assertEquals("23", fromScientific(1, "23", 0).decimalPart)
        assertEquals("3", fromScientific(1, "23", 1).decimalPart)
        assertEquals("0", fromScientific(1, "23", 2).decimalPart)
        assertEquals("0", fromScientific(1, "23", 3).decimalPart)

        assertEquals("00000000001234", fromNumber(1.234e-11)!!.decimalPart)
        assertEquals("89", fromNumber(1.23456789e6)!!.decimalPart)
    }

    @Test
    fun wholePart() {
        assertEquals("100", fromScientific(1, "0", 2).wholePart)
        assertEquals("0", ZERO.wholePart)
        assertEquals("1", fromScientific(1, "0", 0).wholePart)
        assertEquals("0", fromScientific(1, "0", -1).wholePart)
        assertEquals("0", fromScientific(1, "0", -2).wholePart)
        assertEquals("0", fromScientific(1, "0", -3).wholePart)
        assertEquals("10", fromScientific(1, "0", 1).wholePart)
        assertEquals("1000", fromScientific(1, "0", 3).wholePart)

        assertEquals("123400000000", fromNumber(1.234e11)!!.wholePart)
        assertEquals("1234567", fromNumber(1.234567e6)!!.wholePart)
        assertEquals("1234567", fromNumber(1.23456789e6)!!.wholePart)
    }

    @Test
    fun fromNumber() {
        assertEquals(fromNumber(0.0e0), fromNumber(0))
        assertEquals(fromNumber(1.234e0), fromNumber(1.234))
        assertEquals(fromNumber(1.234e-1), fromNumber(0.1234))
        assertEquals(fromNumber(1.234e1), fromNumber(12.34))
        assertEquals(fromNumber(1.234e11), fromNumber(1.234E11))
        assertEquals(fromNumber(1.234e-11), fromNumber(1.234E-11))
    }

    @Test
    fun toPrecisionZero() {
        assertEquals(ZERO, ZERO.toPrecision(0))
    }

    @Test
    fun toPrecision_precision_0() {
        assertEquals(fromScientific(2, "0", 0), fromScientific(2, "0", 0).toPrecision(0))
        assertEquals(fromScientific(9, "0", -1), fromScientific(9, "0", -1).toPrecision(0))
        assertEquals(fromScientific(1, "0", 0), fromScientific(1, "2", 0).toPrecision(0))
        assertEquals(fromScientific(2, "0", 0), fromScientific(1, "9", 0).toPrecision(0))
        assertEquals(fromScientific(1, "0", 1), fromScientific(9, "9", 0).toPrecision(0))
    }

    @Test
    fun toPrecision_precision_1_e0() {
        assertEquals(fromScientific(2, "0", -1), fromScientific(2, "0", -1).toPrecision(1))
        assertEquals(fromScientific(2, "3", -1), fromScientific(2, "3", -1).toPrecision(1))
        assertEquals(fromScientific(2, "5", -1), fromScientific(2, "5", -1).toPrecision(1))
    }

    @Test
    fun toPrecision_9_9() {
        val f = fromScientific(9, "9", 0)
        assertEquals(fromScientific(1, "0", 1), f.toPrecision(0))
    }

    @Test
    fun toPrecision_123_4_with_precision_2() {
        // precision value is higher than the number of digits in the fractional part - no rounding
        val f = fromScientific(1, "234", 2)
        assertEquals(fromScientific(1, "23", 2), f.toPrecision(2))
    }


    @Test
    fun toPrecision() {
        val f = fromScientific(1, "234", 0)
        assertEquals(fromScientific(1, "23", 0), f.toPrecision(2))
    }

    @Test
    fun toPrecision_5_decimalPoint() {
        val f = fromScientific(1, "234567", 3) // 1234.567
        assertEquals(fromScientific(1, "23457", 3), f.toPrecision(5))
    }

    @Test
    fun toPrecisionWithVerySmallNumber() {
        val number = fromNumber(1.93456e-39)!!
        assertEquals(fromNumber(2e-39), number.toPrecision(0))
        assertEquals(fromNumber(1.9e-39), number.toPrecision(1))
        assertEquals(fromNumber(1.93e-39), number.toPrecision(2))
        assertEquals(fromNumber(1.935e-39), number.toPrecision(3))
        assertEquals(fromNumber(1.9346e-39), number.toPrecision(4))
        assertEquals(fromNumber(1.93456e-39), number.toPrecision(5))
        assertEquals(fromNumber(1.93456e-39), number.toPrecision(6))
        assertEquals(fromNumber(1.93456e-39), number.toPrecision(7))
    }

    @Test
    fun toDecimalPrecision() {
        val number = fromNumber(1.2345678e3)!!
        assertEquals(fromNumber(1235.0e0), number.toDecimalPrecision(0))
        assertEquals(fromNumber(1234.57e0), number.toDecimalPrecision(2))
        assertEquals(fromNumber(1234.5678e0), number.toDecimalPrecision(5))
    }

    @Test
    fun toDecimalPrecisionWithPrecisionHigherThanExponent() {
        assertEquals(fromNumber(1e-10), fromNumber(1.23e-10)!!.toDecimalPrecision(3))
    }

    @Test
    fun toPrecisionVerySmallNumberWithCarryInSignificant() {
        assertEquals(fromNumber(1e-38), fromNumber(9.9e-39)!!.toPrecision(0))
    }

    @Test
    fun toPrecisionVerySmallNumberWithoutCarry() {
        assertEquals(fromNumber(1.234e-17), fromNumber(1.234e-17)!!.toPrecision(3))
        assertEquals(fromNumber(1.23e-17), fromNumber(1.234e-17)!!.toPrecision(2))
        assertEquals(fromNumber(1.2e-17), fromNumber(1.234e-17)!!.toPrecision(1))
        assertEquals(fromNumber(1e-17), fromNumber(1.234e-17)!!.toPrecision(0))
    }

    @Test
    fun formatDecimalStr() {
        assertEquals("0" to "0", ZERO.formatDecimalStr())
        assertEquals("1" to "0", fromNumber(1.0)!!.formatDecimalStr())
        assertEquals("0" to "1", fromNumber(0.1)!!.formatDecimalStr())
        assertEquals("0" to "01", fromNumber(0.01)!!.formatDecimalStr())
    }

    @Test
    fun formatDecimalStrWithLength() {
        val number = fromNumber(123.456)!!

        assertEquals("123" to "456", number.formatDecimalStr()) // default
        assertEquals("123" to "456000", number.formatDecimalStr(6)) // padding
        assertEquals("123" to "45", number.formatDecimalStr(2)) // truncation

        assertEquals("123" to "", number.formatDecimalStr(0)) // no decimal part
        assertEquals("123" to "456", number.formatDecimalStr(-1)) // full decimal part
    }

    @Test
    fun formatExpStr() {
        assertEquals("0" to "0", ZERO.formatScientificStr())
        assertEquals("1" to "0", fromNumber(1.0)!!.formatScientificStr())
        assertEquals("1" to "0", fromNumber(0.1)!!.formatScientificStr())
        assertEquals("1" to "0", fromNumber(0.01)!!.formatScientificStr())

        // (1.2399e2, -1) -> "1", to "2399"
        assertEquals("1" to "2399", fromNumber(1.2399e2)!!.formatScientificStr(-1))
        // (1.23e1, 4) -> "1", to "2300"
        assertEquals("1" to "2300", fromNumber(1.23e1)!!.formatScientificStr(4))
        // (1.2399e1, 2) -> "1", to "23"
        assertEquals("1" to "23", fromNumber(1.2399e1)!!.formatScientificStr(2))
    }
}
