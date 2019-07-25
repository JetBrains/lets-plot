package jetbrains.datalore.base.numberFormat

import kotlin.test.Test
import kotlin.test.assertEquals

class FormatTypeXTest {
    @Test
    fun hexLowercaseString() {
        assertEquals("deadbeef", Format("x").apply(0xdeadbeef))
    }

    @Test
    fun hexLowercaseStringWithPrefix() {
        assertEquals("0xdeadbeef", Format("#x").apply(0xdeadbeef))
    }

    @Test
    fun groupsThousands() {
        assertEquals("de,adb,eef", Format(",x").apply(0xdeadbeef))
    }

    @Test
    fun doesNotGroupPrefix() {
        assertEquals("0xade,adb,eef", Format("#,x").apply(0xadeadbeef))
    }

    @Test
    fun putsSignBeforePrefix() {
        assertEquals("+0xdeadbeef", Format("+#x").apply(0xdeadbeef))
        assertEquals("-0xdeadbeef", Format("+#x").apply(-0xdeadbeef))
        assertEquals(" 0xdeadbeef", Format(" #x").apply(0xdeadbeef))
        assertEquals("-0xdeadbeef", Format(" #x").apply(-0xdeadbeef))
    }

    @Test
    fun currency() {
        assertEquals("\$de,adb,eef", Format("$,x").apply(0xdeadbeef))
    }

    @Test
    fun alwaysHasPrecisionZero() {
        assertEquals("deadbeef", Format(".2x").apply(0xdeadbeef))
        assertEquals("-4", Format(".2x").apply(-4.2))
    }

    @Test
    fun roundsNonIntegers() {
        assertEquals("2", Format("x").apply(2.4))
    }

    @Test
    fun canFormatNegativeZeroAsZero() {
        assertEquals("0", Format("x").apply(-0))
        assertEquals("0", Format("x").apply(-1e-12))
    }

    @Test
    fun hexUppercaseString() {
        assertEquals("DEADBEEF", Format("X").apply(0xdeadbeef))
    }

    @Test
    fun hexUppercaseStringWithPrefix() {
        assertEquals("0xDEADBEEF", Format("#X").apply(0xdeadbeef))
    }

    @Test
    fun prefix() {
        assertEquals("            deadbeef", Format("20x").apply(0xdeadbeef))
        assertEquals("          0xdeadbeef", Format("#20x").apply(0xdeadbeef))
        assertEquals("000000000000deadbeef", Format("020x").apply(0xdeadbeef))
        assertEquals("0x0000000000deadbeef", Format("#020x").apply(0xdeadbeef))
    }
}