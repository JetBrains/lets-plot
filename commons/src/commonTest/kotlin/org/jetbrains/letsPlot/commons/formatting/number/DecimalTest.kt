/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

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
        assertEquals(Decimal("NaN", "", ""), Decimal.fromNumber(Double.NaN))
        assertEquals(Decimal("Infinity", "", ""), Decimal.fromNumber(Double.POSITIVE_INFINITY))
        assertEquals(Decimal("Infinity", "", "-"), Decimal.fromNumber(Double.NEGATIVE_INFINITY))
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
            Decimal("0", "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000049", ""),
            Decimal.fromNumber(Double.MIN_VALUE)
        )
        assertEquals(
            Decimal("0", "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000049", "-"),
            Decimal.fromNumber(-Double.MIN_VALUE)
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
            assertEquals(Floating(i = 1, fraction = "0", e = -1), it)
        }
    }

    @Test
    fun toFloating234_567() {
        Decimal.fromNumber(234.567).toFloating().let {
            assertEquals(Floating(i = 2, fraction = "34567", e = 2), it)
        }
    }

    @Test
    fun round_1_0_With_2() {
        Decimal.fromNumber(1.0).round(2).let {
            assertEquals(Decimal("1", "0", ""), it)
        }
    }

    @Test
    fun round_1_12_With_2() {
        Decimal.fromNumber(1.12).round(2).let {
            assertEquals(Decimal("1", "12", ""), it)
        }
    }

    @Test
    fun round_1_124_With_2() {
        Decimal.fromNumber(1.124).round(2).let {
            assertEquals(Decimal("1", "12", ""), it)
        }
    }

    @Test
    fun round_1_125_With_2() {
        Decimal.fromNumber(1.125).round(2).let {
            assertEquals(Decimal("1", "13", ""), it)
        }
    }

    @Test
    fun round_1_1251_With_2() {
        Decimal.fromNumber(1.1251).round(2).let {
            assertEquals(Decimal("1", "13", ""), it)
        }
    }

    @Test
    fun round_123456_51_With_0() {
        Decimal.fromNumber(123456.51).round(0).let {
            assertEquals(Decimal("123457", "0", ""), it)
        }
    }

    @Test
    fun round_9_51_With_0() {
        Decimal.fromNumber(9.51).round(0).let {
            assertEquals(Decimal("10", "0", ""), it)
        }
    }

    @Test
    fun round_0_51_With_0() {
        Decimal.fromNumber(0.51).round(0).let {
            assertEquals(Decimal("1", "0", ""), it)
        }
    }

    @Test
    fun round_123456_5_With_0() {
        Decimal.fromNumber(123456.5).round(0).let {
            assertEquals(Decimal("123457", "0", ""), it)
        }
    }

    @Test
    fun round_0_49_With_1() {
        Decimal.fromNumber(0.49).round(1).let {
            assertEquals(Decimal("0", "5", ""), it)
        }
    }

    @Test
    fun round_1_98_With_1() {
        Decimal.fromNumber(1.98).round(1).let {
            assertEquals(Decimal("2", "0", ""), it)
        }
    }

    @Test
    fun round_999_98_With_1() {
        Decimal.fromNumber(999.98).round(1).let {
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
    fun round_123_456_With_minus_2() {
        Decimal.fromNumber(123.456).round(-2).let {
            assertEquals(Decimal("100", "0", ""), it)
        }
    }

    @Test
    fun round_199_9_With_minus_2() {
        Decimal.fromNumber(199.9).round(-2).let {
            assertEquals(Decimal("200", "0", ""), it)
        }
    }
}
