package jetbrains.datalore.base.numberFormat

import kotlin.test.Test
import kotlin.test.assertEquals

class FormatTypeFTest {
    @Test
    fun canOutputFixedPointNotation() {
        assertEquals("0.5", Format(".1f").apply(0.49))
        assertEquals("0.45", Format(".2f").apply(0.449))
        assertEquals("0.445", Format(".3f").apply(0.4449))
        assertEquals("0.44445", Format(".5f").apply(0.444449))
        assertEquals("100.0", Format(".1f").apply(100))
        assertEquals("100.00", Format(".2f").apply(100))
        assertEquals("100.000", Format(".3f").apply(100))
        assertEquals("100.00000", Format(".5f").apply(100))
    }

    @Test
    fun canOutputCurrencyWithCommaGroupingAndSign() {
        val f = Format("+$,.2f")
        assertEquals("+$0.00", f.apply(0))
        assertEquals("+$0.43", f.apply(0.429))
        assertEquals("-$0.43", f.apply(-0.429))
        assertEquals("-$1.00", f.apply(-1))
        assertEquals("+$10,000.00", f.apply(1e4))
    }

    @Test
    fun canGroupThousandsSpaceFillAndRoundToSignificantDigits() {
        assertEquals(" 123,456.5", Format("10,.1f").apply(123456.49))
        assertEquals("1,234,567.45", Format("10,.2f").apply(1234567.449))
        assertEquals("12,345,678.445", Format("10,.3f").apply(12345678.4449))
        assertEquals("123,456,789.44445", Format("10,.5f").apply(123456789.444449))
        assertEquals(" 123,456.0", Format("10,.1f").apply(123456))
        assertEquals("1,234,567.00", Format("10,.2f").apply(1234567))
        assertEquals("12,345,678.000", Format("10,.3f").apply(12345678))
        assertEquals("123,456,789.00000", Format("10,.5f").apply(123456789))
    }

    @Test
    fun canDisplayIntegersInFixedPointNotation() {
        assertEquals("42.000000", Format("f").apply(42))
        assertEquals("42.000000", Format("f").apply(42))
    }

    @Test
    fun canFormatNegativeZerosAsZeros() {
        assertEquals("0.000000", Format("f").apply(-0))
        assertEquals("0.000000", Format("f").apply(-1e-12))
    }
}