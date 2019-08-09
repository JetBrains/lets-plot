package jetbrains.datalore.base.numberFormat

import kotlin.test.Test
import kotlin.test.assertEquals

class FormatAlignTest {
    @Test
    fun alignLeft() {
        assertEquals("0", Format("<1,d").apply(0))
        assertEquals("0 ", Format("<2,d").apply(0))
        assertEquals("0  ", Format("<3,d").apply(0))
        assertEquals("0    ", Format("<5,d").apply(0))
        assertEquals("0       ", Format("<8,d").apply(0))
        assertEquals("0            ", Format("<13,d").apply(0))
        assertEquals("0                    ", Format("<21,d").apply(0))
    }

    @Test
    fun alignRight() {
        assertEquals("0", Format(">1,d").apply(0))
        assertEquals(" 0", Format(">2,d").apply(0))
        assertEquals("  0", Format(">3,d").apply(0))
        assertEquals("    0", Format(">5,d").apply(0))
        assertEquals("       0", Format(">8,d").apply(0))
        assertEquals("            0", Format(">13,d").apply(0))
        assertEquals("                    0", Format(">21,d").apply(0))
        assertEquals("                1,000", Format(">21,d").apply(1000))
        assertEquals("                1e+21", Format(">21,d").apply(1e21))
    }

    @Test
    fun alignCenter() {
        assertEquals("0", Format("^1,d").apply(0))
        assertEquals("0 ", Format("^2,d").apply(0))
        assertEquals(" 0 ", Format("^3,d").apply(0))
        assertEquals("  0  ", Format("^5,d").apply(0))
        assertEquals("   0    ", Format("^8,d").apply(0))
        assertEquals("      0      ", Format("^13,d").apply(0))
        assertEquals("          0          ", Format("^21,d").apply(0))
        assertEquals("        1,000        ", Format("^21,d").apply(1000))
        assertEquals("        1e+21        ", Format("^21,d").apply(1e21))
    }
}