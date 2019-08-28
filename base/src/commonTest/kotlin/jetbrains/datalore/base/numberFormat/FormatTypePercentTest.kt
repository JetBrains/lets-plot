package jetbrains.datalore.base.numberFormat

import kotlin.test.Test
import kotlin.test.assertEquals

class FormatTypePercentTest {
    @Test
    fun percentage() {
        val f = Format(".0%")
        assertEquals("0%", f.apply(0))
        assertEquals("4%", f.apply(0.042))
        assertEquals("42%", f.apply(0.42))
        assertEquals("420%", f.apply(4.2))
        assertEquals("-4%", f.apply(-0.042))
        assertEquals("-42%", f.apply(-0.42))
        assertEquals("-420%", f.apply(-4.2))
    }

    @Test
    fun withPrecision() {
        assertEquals("23.4%", Format(".1%").apply(.234))
        assertEquals("23.40%", Format(".2%").apply(.234))
    }

    @Test
    fun withFill() {
        assertEquals("0000000000000004200%", Format("020.0%").apply(42))
        assertEquals("               4200%", Format("20.0%").apply(42))
    }

    @Test
    fun alignCenter() {
        assertEquals("         42%         ", Format("^21.0%").apply(.42))
        assertEquals("       42,200%       ", Format("^21,.0%").apply(422))
        assertEquals("      -42,200%       ", Format("^21,.0%").apply(-422))
    }
}