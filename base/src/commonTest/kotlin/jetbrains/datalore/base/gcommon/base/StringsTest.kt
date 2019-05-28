package jetbrains.datalore.base.gcommon.base

import kotlin.test.Test
import kotlin.test.assertEquals

class StringsTest {

    @Test
    fun repeat() {
        assertEquals("heyheyhey", Strings.repeat("hey", 3))
    }

}
