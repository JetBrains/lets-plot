package jetbrains.datalore.base.numberFormat

import kotlin.test.Test
import kotlin.test.assertEquals

class FormatFillTest {
    @Test
    fun zeroFill() {
        val f = Format("08d")
        assertEquals("00000000", f.apply(0))
        assertEquals("00000042", f.apply(42))
        assertEquals("42000000", f.apply(42000000))
        assertEquals("420000000", f.apply(420000000))

        assertEquals("-0000004", f.apply(-4))
        assertEquals("-0000042", f.apply(-42))
        assertEquals("-4200000", f.apply(-4200000))
        assertEquals("-42000000", f.apply(-42000000))
    }

    @Test
    fun spaceFill() {
        val f = Format("8d")
        assertEquals("       0", f.apply(0))
        assertEquals("      42", f.apply(42))
        assertEquals("42000000", f.apply(42000000))
        assertEquals("420000000", f.apply(420000000))
        assertEquals("      -4", f.apply(-4))
        assertEquals("     -42", f.apply(-42))
        assertEquals("-4200000", f.apply(-4200000))
        assertEquals("-42000000", f.apply(-42000000))
    }

    @Test
    fun underscoreFill() {
        val f = Format("_>8d")
        assertEquals("_______0", f.apply(0))
        assertEquals("______42", f.apply(42))
        assertEquals("42000000", f.apply(42000000))
        assertEquals("420000000", f.apply(420000000))
        assertEquals("______-4", f.apply(-4))
        assertEquals("_____-42", f.apply(-42))
        assertEquals("-4200000", f.apply(-4200000))
        assertEquals("-42000000", f.apply(-42000000))
    }

    @Test
    fun zeroFillWithSignAndGroup() {
        val f = Format("+08,d")
        assertEquals("+0,000,000", f.apply(0))
        assertEquals("+0,000,042", f.apply(42))
        assertEquals("+42,000,000", f.apply(42000000))
        assertEquals("+420,000,000", f.apply(420000000))
        assertEquals("-0,000,004", f.apply(-4))
        assertEquals("-0,000,042", f.apply(-42))
        assertEquals("-4,200,000", f.apply(-4200000))
        assertEquals("-42,000,000", f.apply(-42000000))
    }
}