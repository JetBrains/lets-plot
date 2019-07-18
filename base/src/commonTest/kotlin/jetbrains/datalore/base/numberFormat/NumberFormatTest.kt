package jetbrains.datalore.base.numberFormat

import kotlin.test.Test
import kotlin.test.assertEquals

class ListMapTest {
    private val pattern = "r<+#06,.5f"
    private val shortPattern = "r<+#0,.5f"

    @Test
    fun parse() {
        NumberFormat.formatNumber(123, shortPattern)
        assertEquals("1", "1")
    }
}