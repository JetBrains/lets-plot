/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import org.jetbrains.letsPlot.commons.formatting.number.Util.DOUBLE_ALMOST_MIN_VALUE
import kotlin.test.Test
import kotlin.test.assertEquals

class DecimalTest {
    @Test
    fun expLessThanFracPart() {
        assertEquals(Decimal("52345678", "4449", ""), Decimal.fromNumber(5.23456784449E7))
    }

    @Test
    fun expMoreThanFracPart() {
        assertEquals(Decimal("523", "456784449", ""), Decimal.fromNumber(5.23456784449E2))
    }

    @Test
    fun noFrac() {
        assertEquals(Decimal("500000", "0", ""), Decimal.fromNumber(5E5))
    }

    @Test
    fun specialValues() {
        assertEquals(Decimal("0", "0", ""), Decimal.fromNumber(-0.0))
    }

    @Test
    fun simple() {
        assertEquals(Decimal("0", "0", ""), Decimal.fromNumber(0))
        assertEquals(Decimal("1", "0", ""), Decimal.fromNumber(1))
        assertEquals(Decimal("1", "0", ""), Decimal.fromNumber(1.0))

        assertEquals(Decimal("1", "0", "-"), Decimal.fromNumber(-1))
        assertEquals(Decimal("1", "0", "-"), Decimal.fromNumber(-1.0))
    }

    @Test
    fun positiveExponent() {
        assertEquals(Decimal("1000", "0", ""), Decimal.fromNumber(1e3))
        assertEquals(Decimal("1000", "0", "-"), Decimal.fromNumber(-1e3))
    }

    @Test
    fun negativeExponent() {
        assertEquals(Decimal("0", "001", ""), Decimal.fromNumber(1e-3))
        assertEquals(Decimal("0", "001", "-"), Decimal.fromNumber(-1e-3))
    }

    @Test
    fun longNumbers() {
        assertEquals(Decimal("123456789012345680000", "0", ""), Decimal.fromNumber(1.2345678901234568E20))
        assertEquals(Decimal("123456789012345680000", "0", "-"), Decimal.fromNumber(-1.2345678901234568E20))
    }

    @Test
    fun minMaxDouble() {
        assertEquals(
            Decimal("179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000", "0", ""),
            Decimal.fromNumber(Double.MAX_VALUE)
        )
        assertEquals(
            Decimal("179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000", "0", "-"),
            Decimal.fromNumber(-Double.MAX_VALUE)
        )
        assertEquals(
            Decimal("0", "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001", ""),
            Decimal.fromNumber(DOUBLE_ALMOST_MIN_VALUE)
        )
        assertEquals(
            Decimal("0", "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001", "-"),
            Decimal.fromNumber(-DOUBLE_ALMOST_MIN_VALUE)
        )
    }

    @Test
    fun toFloating1_0() {
        Decimal.fromNumber(1.0).toFloating().let {
            assertEquals(Floating(1, "0", 0), it)
        }
    }

    @Test
    fun toFloating0_1() {
        Decimal.fromNumber(0.1).toFloating().let {
            assertEquals(Floating(i = 1, fraction = "0", exp = -1), it)
        }
    }

    @Test
    fun toFloating234_567() {
        Decimal.fromNumber(234.567).toFloating().let {
            assertEquals(Floating(i = 2, fraction = "34567", exp = 2), it)
        }
    }

    @Test
    fun round_1_0_With_2() {
        Decimal.fromNumber(1.0).fRound(2).let {
            assertEquals(Decimal("1", "0", ""), it)
        }
    }

    @Test
    fun round_1_12_With_2() {
        Decimal.fromNumber(1.12).fRound(2).let {
            assertEquals(Decimal("1", "12", ""), it)
        }
    }

    @Test
    fun round_1_124_With_2() {
        Decimal.fromNumber(1.124).fRound(2).let {
            assertEquals(Decimal("1", "12", ""), it)
        }
    }

    @Test
    fun round_1_125_With_2() {
        Decimal.fromNumber(1.125).fRound(2).let {
            assertEquals(Decimal("1", "13", ""), it)
        }
    }

    @Test
    fun round_1_1251_With_2() {
        Decimal.fromNumber(1.1251).fRound(2).let {
            assertEquals(Decimal("1", "13", ""), it)
        }
    }

    @Test
    fun round_123456_51_With_0() {
        Decimal.fromNumber(123456.51).fRound(0).let {
            assertEquals(Decimal("123457", "0", ""), it)
        }
    }

    @Test
    fun round_9_51_With_0() {
        Decimal.fromNumber(9.51).fRound(0).let {
            assertEquals(Decimal("10", "0", ""), it)
        }
    }

    @Test
    fun round_0_51_With_0() {
        Decimal.fromNumber(0.51).fRound(0).let {
            assertEquals(Decimal("1", "0", ""), it)
        }
    }

    @Test
    fun round_123456_5_With_0() {
        Decimal.fromNumber(123456.5).fRound(0).let {
            assertEquals(Decimal("123457", "0", ""), it)
        }
    }

    @Test
    fun round_0_49_With_1() {
        Decimal.fromNumber(0.49).fRound(1).let {
            assertEquals(Decimal("0", "5", ""), it)
        }
    }

    @Test
    fun round_1_98_With_1() {
        Decimal.fromNumber(1.98).fRound(1).let {
            assertEquals(Decimal("2", "0", ""), it)
        }
    }

    @Test
    fun round_999_98_With_1() {
        Decimal.fromNumber(999.98).fRound(1).let {
            assertEquals(Decimal("1000", "0", ""), it)
        }
    }

    @Test
    fun shift_123_456_RightBy_2() {
        Decimal.fromNumber(123.456).shiftDecimalPoint(2).let {
            assertEquals(Decimal("12345", "6", ""), it)
        }
    }


    @Test
    fun shift_123_456_RightBy_4() {
        Decimal.fromNumber(123.456).shiftDecimalPoint(4).let {
            assertEquals(Decimal("1234560", "0", ""), it)
        }
    }

    @Test
    fun shift_123_456_LeftBy_2() {
        Decimal.fromNumber(123.456).shiftDecimalPoint(-4).let {
            assertEquals(Decimal("0", "0123456", ""), it)
        }
    }

    @Test
    fun iRound_123_456789_With_1() {
        Decimal.fromNumber(123.456789).let {
            assertEquals(Decimal("100", "0", ""), it.iRound(0))
            assertEquals(Decimal("100", "0", ""), it.iRound(1))
            assertEquals(Decimal("120", "0", ""), it.iRound(2))
            assertEquals(Decimal("123", "0", ""), it.iRound(3))
            assertEquals(Decimal("123", "5", ""), it.iRound(4))
            assertEquals(Decimal("123", "46", ""), it.iRound(5))
            assertEquals(Decimal("123", "457", ""), it.iRound(6))
            assertEquals(Decimal("123", "4568", ""), it.iRound(7))
            assertEquals(Decimal("123", "45679", ""), it.iRound(8))
            assertEquals(Decimal("123", "456789", ""), it.iRound(9))
        }
    }

    @Test
    fun iRound_959_51946() {
        Decimal.fromNumber(959.51946).let {
            assertEquals(Decimal("1000", "0", ""), it.iRound(0))
            assertEquals(Decimal("1000", "0", ""), it.iRound(1))
            assertEquals(Decimal("960", "0", ""), it.iRound(2))
            assertEquals(Decimal("960", "0", ""), it.iRound(3))
            assertEquals(Decimal("959", "5", ""), it.iRound(4))
            assertEquals(Decimal("959", "52", ""), it.iRound(5))
            assertEquals(Decimal("959", "519", ""), it.iRound(6))
            assertEquals(Decimal("959", "5195", ""), it.iRound(7))
            assertEquals(Decimal("959", "51946", ""), it.iRound(8))
        }
    }

    @Test
    fun iround0() {
        assertEquals(Decimal("20", "0", ""), Decimal.fromNumber(16.5).iRound(0))
        assertEquals(Decimal("900", "0", ""), Decimal.fromNumber(929.51946).iRound(0))
        assertEquals(Decimal("1000", "0", ""), Decimal.fromNumber(959.51946).iRound(0))
        assertEquals(Decimal("100", "0", ""), Decimal.fromNumber(123.456789).iRound(0))

    }

    @Test
    fun specialCases() {
        assertEquals(Decimal("0", "0", ""), Decimal.fromNumber(0.0).iRound(0))
        assertEquals(Decimal("0", "0", ""), Decimal.fromNumber(0.0).iRound(1))
        assertEquals(Decimal("0", "0", ""), Decimal.fromNumber(0.0).iRound(2))
        assertEquals(Decimal("0", "0", ""), Decimal.fromNumber(0.0).iRound(3))
        assertEquals(Decimal("0", "1", ""), Decimal.fromNumber(0.1).iRound(3))
        assertEquals(Decimal("0", "9", ""), Decimal.fromNumber(0.9).iRound(3))

    }

    @Test
    fun toFloating_1234_56789() {
        Decimal("1234", "56789", "").toFloating().let {
            assertEquals(Floating(1, "23456789", 3), it)
        }
    }

    @Test
    fun toFloating_WithLongFraction() {
        Decimal("1234", "123456789123456789123456789123456789123456789", "").toFloating().let {
            assertEquals(Floating(1, "234123456789123456789123456789123456789123456789", 3), it)
        }
    }

    @Test
    fun toFloating_WithLongWholePart() {
        Decimal("123456789123456789123456789123456789123456789", "1234", "").toFloating().let {
            assertEquals(Floating(1, "234567891234567891234567891234567891234567891234", 44), it)
        }
    }

    @Test
    fun asd() {
        val v = Decimal.fromNumber(9.999999999999999e-9)
        println(v)
        println(v.toFloating())
        println(v.fRound(6))
    }
}
