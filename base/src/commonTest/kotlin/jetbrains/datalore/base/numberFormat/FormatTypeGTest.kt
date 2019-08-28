package jetbrains.datalore.base.numberFormat

import kotlin.test.Test
import kotlin.test.assertEquals

class FormatTypeGTest {
    @Test
    fun canOutputGeneralNotation() {
        assertEquals("0.05", Format(".1g").apply(0.049))
        assertEquals("0.5", Format(".1g").apply(0.49))
        assertEquals("0.45", Format(".2g").apply(0.449))
        assertEquals("0.445", Format(".3g").apply(0.4449))
        assertEquals("0.44445", Format(".5g").apply(0.444449))
        assertEquals("1e+2", Format(".1g").apply(100))
        assertEquals("1.0e+2", Format(".2g").apply(100))
        assertEquals("100", Format(".3g").apply(100))
        assertEquals("100.00", Format(".5g").apply(100))
        assertEquals("100.20", Format(".5g").apply(100.2))
        assertEquals("0.0020", Format(".2g").apply(0.002))
    }

    @Test
    fun canGroupThousandsWithGeneralNotation() {
        val f = Format(",.12g")
        assertEquals("0.00000000000", f.apply(0))
        assertEquals("42.0000000000", f.apply(42))
        assertEquals("42,000,000.0000", f.apply(42000000))
        assertEquals("420,000,000.000", f.apply(420000000))
        assertEquals("-4.00000000000", f.apply(-4))
        assertEquals("-42.0000000000", f.apply(-42))
        assertEquals("-4,200,000.00000", f.apply(-4200000))
        assertEquals("-42,000,000.0000", f.apply(-42000000))
    }
}