package jetbrains.datalore.base.numberFormat

import kotlin.test.Test
import kotlin.test.assertEquals

class FormatTypeOTest {
    @Test
    fun octal() {
        assertEquals("12", Format("o").apply(10))
    }

    @Test
    fun octalWithPrefix() {
        assertEquals("0o12", Format("#o").apply(10))
    }
}