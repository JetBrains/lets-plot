package jetbrains.datalore.base.numberFormat

import kotlin.test.Test
import kotlin.test.assertEquals

class NumberFormatTypeETest {

    @Test
    fun canOutputExponentNotation() {
        val f = NumberFormat("e")
        assertEquals("0.000000e+0", f.apply(0))
        assertEquals("4.200000e+1", f.apply(42))
        assertEquals("4.200000e+7", f.apply(42000000))
        assertEquals("4.200000e+8", f.apply(420000000))
        assertEquals("-4.000000e+0", f.apply(-4))
        assertEquals("-4.200000e+1", f.apply(-42))
        assertEquals("-4.200000e+6", f.apply(-4200000))
        assertEquals("-4.200000e+7", f.apply(-42000000))

        assertEquals("4e+1", NumberFormat(".0e").apply(42))
        assertEquals("4.200e+1", NumberFormat(".3e").apply(42))
    }

    @Test
    fun canFormatNegativeZeroAsZero() {
        assertEquals("0.000000e+0", NumberFormat("1e").apply(-0))
        assertEquals("-1.000000e-12", NumberFormat("1e").apply(-1e-12))
    }
}