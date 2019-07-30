package jetbrains.datalore.base.numberFormat

import kotlin.test.Test
import kotlin.test.assertEquals

class FormatTypeNoneTest {
    @Test
    fun usesSignificantPrecisionAndTrimsInsignificantZeros() {
        //assertEquals("5", Format(".1").apply(4.9))
        assertEquals("0.5", Format(".1").apply(0.49))
        assertEquals("4.9", Format(".2").apply(4.9))
        assertEquals("0.49", Format(".2").apply(0.49))
        assertEquals("0.45", Format(".2").apply(0.449))
        assertEquals("4.9", Format(".3").apply(4.9))
        assertEquals("0.49", Format(".3").apply(0.49))
        assertEquals("0.449", Format(".3").apply(0.449))
        assertEquals("0.445", Format(".3").apply(0.4449))
        assertEquals("0.44445", Format(".5").apply(0.444449))
    }

    @Test
    fun doesNotTrimSignificantZeros() {
        assertEquals("10", Format(".5").apply(10))
        assertEquals("100", Format(".5").apply(100))
        assertEquals("1000", Format(".5").apply(1000))
        assertEquals("21010", Format(".5").apply(21010))
        assertEquals("1.1", Format(".5").apply(1.10001))
        assertEquals("1.1e+6", Format(".5").apply(1.10001e6))
        assertEquals("1.10001", Format(".6").apply(1.10001))
        assertEquals("1.10001e+6", Format(".6").apply(1.10001e6))
    }

    @Test
    fun alsoTrimsDecimalPointIfThereAreOnlyInsignificantZeros() {
        assertEquals("1", Format(".5").apply(1.00001))
        assertEquals("1e+6", Format(".5").apply(1.00001e6))
        assertEquals("1.00001", Format(".6").apply(1.00001))
        assertEquals("1.00001e+6", Format(".6").apply(1.00001e6))
    }

    @Test
    fun canOutputCurrency() {
        val f = Format("$")
        assertEquals("$0", f.apply(0))
        assertEquals("$0.042", f.apply(.042))
        assertEquals("$0.42", f.apply(.42))
        assertEquals("$4.2", f.apply(4.2))
        assertEquals("-$0.042", f.apply(-.042))
        assertEquals("-$0.42", f.apply(-.42))
        assertEquals("-$4.2", f.apply(-4.2))
    }


    @Test
    fun canFormatNegativeZeroAsZero() {
        assertEquals("0", Format("").apply(-0))
    }
}