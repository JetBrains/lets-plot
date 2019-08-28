package jetbrains.datalore.base.numberFormat

import kotlin.test.Test
import kotlin.test.assertEquals

class FormatSignTest {
    @Test
    fun padAfterSign() {
        assertEquals("+0", Format("=+1,d").apply(0))
        assertEquals("+0", Format("=+2,d").apply(0))
        assertEquals("+ 0", Format("=+3,d").apply(0))
        assertEquals("+   0", Format("=+5,d").apply(0))
        assertEquals("+      0", Format("=+8,d").apply(0))
        assertEquals("+           0", Format("=+13,d").apply(0))
        assertEquals("+                   0", Format("=+21,d").apply(0))
        assertEquals("+               1e+21", Format("=+21,d").apply(1e21))
    }

    @Test
    fun onlyUseSignForNegativeNumbers() {
        assertEquals("-1", Format("-1,d").apply(-1))
        assertEquals("0", Format("-1,d").apply(0))
        assertEquals(" 0", Format("-2,d").apply(0))
        assertEquals("  0", Format("-3,d").apply(0))
        assertEquals("    0", Format("-5,d").apply(0))
        assertEquals("       0", Format("-8,d").apply(0))
        assertEquals("            0", Format("-13,d").apply(0))
        assertEquals("                    0", Format("-21,d").apply(0))
    }
}