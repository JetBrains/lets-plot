package jetbrains.datalore.base.numberFormat

import kotlin.test.Test
import kotlin.test.assertEquals

class FormatTypeDTest {
    @Test
    fun alwaysUsesZeroPrecision() {
        val f = Format(".2d")
        assertEquals("0", f.apply(0))
        assertEquals("42", f.apply(42))
        assertEquals("-4", f.apply(-4.2))
    }

    @Test
    fun roundsNonIntegers() {
        assertEquals("4", Format("d").apply(4.2))
    }

    @Test
    fun groupThousands() {
        assertEquals("0", Format("01,d").apply(0))
        assertEquals("0", Format("01,d").apply(0))
        assertEquals("00", Format("02,d").apply(0))
        assertEquals("000", Format("03,d").apply(0))
        assertEquals("0,000", Format("04,d").apply(0))
        assertEquals("0,000", Format("05,d").apply(0))
        assertEquals("00,000", Format("06,d").apply(0))
        assertEquals("0,000,000", Format("08,d").apply(0))
        assertEquals("0,000,000,000", Format("013,d").apply(0))
        assertEquals("0,000,000,000,000,000", Format("021,d").apply(0))
        assertEquals("-0,042,000,000", Format("013,d").apply(-42000000))
        assertEquals("0,000,001e+21", Format("012,d").apply(1e21))
        assertEquals("0,000,001e+21", Format("013,d").apply(1e21))
        assertEquals("00,000,001e+21", Format("014,d").apply(1e21))
        assertEquals("000,000,001e+21", Format("015,d").apply(1e21))
    }

    @Test
    fun groupThousandsAndZeroFillWithOverflow() {
        assertEquals("1", Format("01,d").apply(1))
        assertEquals("1", Format("01,d").apply(1))
        assertEquals("12", Format("02,d").apply(12))
        assertEquals("123", Format("03,d").apply(123))
        assertEquals("12,345", Format("05,d").apply(12345))
        assertEquals("12,345,678", Format("08,d").apply(12345678))
        assertEquals("1,234,567,890,123", Format("013,d").apply(1234567890123))
    }

    @Test
    fun groupThousandsAndSpaceFill() {
        assertEquals("0", Format("1,d").apply(0))
        assertEquals("0", Format("1,d").apply(0))
        assertEquals(" 0", Format("2,d").apply(0))
        assertEquals("  0", Format("3,d").apply(0))
        assertEquals("    0", Format("5,d").apply(0))
        assertEquals("       0", Format("8,d").apply(0))
        assertEquals("            0", Format("13,d").apply(0))
        assertEquals("                    0", Format("21,d").apply(0))
    }

    @Test
    fun groupThousandsAndSpaceFillWithOverflow() {
        assertEquals("1", Format("1,d").apply(1))
        assertEquals("12", Format("2,d").apply(12))
        assertEquals("123", Format("3,d").apply(123))
        assertEquals("12,345", Format("5,d").apply(12345))
        assertEquals("12,345,678", Format("8,d").apply(12345678))
        assertEquals("1,234,567,890,123", Format("13,d").apply(1234567890123))
    }

    @Test
    fun padAfterSignWithCurrency() {
        assertEquals("+$0", Format("=+$1,d").apply(0))
        assertEquals("+$0", Format("=+$1,d").apply(0))
        assertEquals("+$0", Format("=+$2,d").apply(0))
        assertEquals("+$0", Format("=+$3,d").apply(0))
        assertEquals("+$  0", Format("=+$5,d").apply(0))
        assertEquals("+$     0", Format("=+$8,d").apply(0))
        assertEquals("+$          0", Format("=+$13,d").apply(0))
        assertEquals("+$                  0", Format("=+$21,d").apply(0))
        assertEquals("+$              1e+21", Format("=+$21,d").apply(1e21))
    }

    @Test
    fun aSpaceCanDenotePositiveNumbers() {
        assertEquals("-1", Format(" 1,d").apply(-1))
        assertEquals(" 0", Format(" 1,d").apply(0))
        assertEquals(" 0", Format(" 2,d").apply(0))
        assertEquals("  0", Format(" 3,d").apply(0))
        assertEquals("    0", Format(" 5,d").apply(0))
        assertEquals("       0", Format(" 8,d").apply(0))
        assertEquals("            0", Format(" 13,d").apply(0))
        assertEquals("                    0", Format(" 21,d").apply(0))
        assertEquals("                1e+21", Format(" 21,d").apply(1e21))
    }

    @Test
    fun formatNegativeZeroAsZero() {
        assertEquals("0", Format("1d").apply(-0))
        assertEquals("0", Format("1d").apply(-1e-12))
    }
}