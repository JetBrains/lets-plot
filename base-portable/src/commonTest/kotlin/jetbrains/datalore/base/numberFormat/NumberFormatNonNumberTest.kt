package jetbrains.datalore.base.numberFormat

import kotlin.test.Test
import kotlin.test.assertEquals

class NumberFormatNonNumberTest {
    @Test
    fun nonNumber() {
        val f = NumberFormat("d")
        assertEquals("NaN", f.apply(Double.NaN))
        assertEquals("+Infinity", f.apply(Double.POSITIVE_INFINITY))
        assertEquals("-Infinity", f.apply(Double.NEGATIVE_INFINITY))
    }
}