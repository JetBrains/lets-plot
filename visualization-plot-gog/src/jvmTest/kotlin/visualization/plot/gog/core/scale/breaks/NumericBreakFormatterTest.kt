package jetbrains.datalore.visualization.plot.gog.core.scale.breaks

import kotlin.test.Test
import kotlin.test.assertEquals

class NumericBreakFormatterTest {
    @Test
    fun formatZero() {
        val formatter = NumericBreakFormatter(0.0, 0.0, true)
        assertEquals("0", formatter.apply(0), "format 0")
    }

}