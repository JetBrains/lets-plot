/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import kotlin.test.Test
import kotlin.test.assertEquals

class FloatingTest {
    @Test
    fun invariants() {
        assertEquals(NormalizedFloat.ZERO, NormalizedFloat.fromScientific(0, "", 0))
        assertEquals(NormalizedFloat.ZERO, NormalizedFloat.fromScientific(0, "000000", 0))
        assertEquals(NormalizedFloat.ZERO, NormalizedFloat.fromScientific(0, "000000", -5))

        assertEquals(NormalizedFloat.fromNumber(1.0e-1), NormalizedFloat.fromScientific(1, "0", -1))
        assertEquals(NormalizedFloat.fromNumber(1.0e-2), NormalizedFloat.fromScientific(1, "0", -2))
        assertEquals(NormalizedFloat.fromNumber(1.0e-3), NormalizedFloat.fromScientific(1, "0", -3))

        runCatching { NormalizedFloat.fromScientific(10, "0", 0) }.onFailure { assertEquals("i should be in 0..9, but was 10", it.message) }
    }

    @Test
    fun zeroDecimalPart() {
        assertEquals("0", NormalizedFloat.ZERO.decimalPart)
        assertEquals("0", NormalizedFloat.fromScientific(1, "0", 0).decimalPart)
        assertEquals("1", NormalizedFloat.fromScientific(1, "0", -1).decimalPart)
        assertEquals("01", NormalizedFloat.fromScientific(1, "0", -2).decimalPart)
        assertEquals("001", NormalizedFloat.fromScientific(1, "0", -3).decimalPart)
        assertEquals("0", NormalizedFloat.fromScientific(1, "0", 0).decimalPart)
        assertEquals("0", NormalizedFloat.fromScientific(1, "0", 1).decimalPart)
        assertEquals("0", NormalizedFloat.fromScientific(1, "0", 2).decimalPart)
    }

    @Test
    fun nonZeroDecimalPart() {
        assertEquals("2", NormalizedFloat.fromScientific(2, "0", -1).decimalPart)
        assertEquals("2", NormalizedFloat.fromScientific(1, "2", 0).decimalPart)
        assertEquals("12", NormalizedFloat.fromScientific(1, "2", -1).decimalPart)
        assertEquals("012", NormalizedFloat.fromScientific(1, "2", -2).decimalPart)
        assertEquals("0012", NormalizedFloat.fromScientific(1, "2", -3).decimalPart)

        assertEquals("23", NormalizedFloat.fromScientific(1, "23", 0).decimalPart)
        assertEquals("3", NormalizedFloat.fromScientific(1, "23", 1).decimalPart)
        assertEquals("0", NormalizedFloat.fromScientific(1, "23", 2).decimalPart)
        assertEquals("0", NormalizedFloat.fromScientific(1, "23", 3).decimalPart)

        assertEquals("00000000001234", NormalizedFloat.fromNumber(1.234e-11)!!.decimalPart)
        assertEquals("89", NormalizedFloat.fromNumber(1.23456789e6)!!.decimalPart)
    }

    @Test
    fun wholePart() {
        assertEquals("0", NormalizedFloat.ZERO.wholePart)
        assertEquals("1", NormalizedFloat.fromScientific(1, "0", 0).wholePart)
        assertEquals("0", NormalizedFloat.fromScientific(1, "0", -1).wholePart)
        assertEquals("0", NormalizedFloat.fromScientific(1, "0", -2).wholePart)
        assertEquals("0", NormalizedFloat.fromScientific(1, "0", -3).wholePart)
        assertEquals("10", NormalizedFloat.fromScientific(1, "0", 1).wholePart)
        assertEquals("100", NormalizedFloat.fromScientific(1, "0", 2).wholePart)
        assertEquals("1000", NormalizedFloat.fromScientific(1, "0", 3).wholePart)

        assertEquals("123400000000", NormalizedFloat.fromNumber(1.234e11)!!.wholePart)
        assertEquals("1234567", NormalizedFloat.fromNumber(1.234567e6)!!.wholePart)
        assertEquals("1234567", NormalizedFloat.fromNumber(1.23456789e6)!!.wholePart)
    }

    @Test
    fun fromNumber() {
        assertEquals(NormalizedFloat.fromNumber(0.0e0), NormalizedFloat.fromNumber(0))
        assertEquals(NormalizedFloat.fromNumber(1.234e0), NormalizedFloat.fromNumber(1.234))
        assertEquals(NormalizedFloat.fromNumber(1.234e-1), NormalizedFloat.fromNumber(0.1234))
        assertEquals(NormalizedFloat.fromNumber(1.234e1), NormalizedFloat.fromNumber(12.34))
        assertEquals(NormalizedFloat.fromNumber(1.234e11), NormalizedFloat.fromNumber(1.234E11))
        assertEquals(NormalizedFloat.fromNumber(1.234e-11), NormalizedFloat.fromNumber(1.234E-11))
    }

    @Test
    fun roundZero() {
        assertEquals(NormalizedFloat.ZERO, NormalizedFloat.ZERO.round(0))
    }

    @Test
    fun round_precision_0() {
        assertEquals(NormalizedFloat.fromScientific(2, "0", 0), NormalizedFloat.fromScientific(2, "0", 0).round(0))
        assertEquals(NormalizedFloat.fromScientific(9, "0", -1), NormalizedFloat.fromScientific(9, "0", -1).round(0))
        assertEquals(NormalizedFloat.fromScientific(1, "0", 0), NormalizedFloat.fromScientific(1, "2", 0).round(0))
        assertEquals(NormalizedFloat.fromScientific(2, "0", 0), NormalizedFloat.fromScientific(1, "9", 0).round(0))
        assertEquals(NormalizedFloat.fromScientific(1, "0", 1), NormalizedFloat.fromScientific(9, "9", 0).round(0))
    }

    @Test
    fun round_precision_1_e0() {
        assertEquals(NormalizedFloat.fromScientific(2, "0", -1), NormalizedFloat.fromScientific(2, "0", -1).round(1))
        assertEquals(NormalizedFloat.fromScientific(2, "3", -1), NormalizedFloat.fromScientific(2, "3", -1).round(1))
        assertEquals(NormalizedFloat.fromScientific(2, "5", -1), NormalizedFloat.fromScientific(2, "5", -1).round(1))
    }

    @Test
    fun round_9_9() {
        val f = NormalizedFloat.fromScientific(9, "9", 0)
        assertEquals(NormalizedFloat.fromScientific(1, "0", 1), f.round(0))

    }

    @Test
    fun round_123_4_with_precision_2() {
        // precision value is higher than the number of digits in the fractional part - no rounding
        val f = NormalizedFloat.fromScientific(1, "234", 2)
        assertEquals(NormalizedFloat.fromScientific(1, "23", 2), f.round(2))
    }


    @Test
    fun round() {
        val f = NormalizedFloat.fromScientific(1, "234", 0)
        assertEquals(NormalizedFloat.fromScientific(1, "23", 0), f.round(2))
    }
}