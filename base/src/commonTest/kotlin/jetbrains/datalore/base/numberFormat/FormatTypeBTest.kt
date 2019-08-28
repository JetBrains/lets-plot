package jetbrains.datalore.base.numberFormat

import kotlin.test.Test
import kotlin.test.assertEquals

class FormatTypeBTest {
    @Test
    fun binary() {
        assertEquals("1010", Format("b").apply(10))
    }

    @Test
    fun binaryWithPrefix() {
        assertEquals("0b1010", Format("#b").apply(10))
    }
}