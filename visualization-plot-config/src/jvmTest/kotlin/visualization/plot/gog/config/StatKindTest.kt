package jetbrains.datalore.visualization.plot.gog.config

import kotlin.test.Test
import kotlin.test.assertEquals

class StatKindTest {

    @Test
    fun valueOf() {
        assertEquals(StatKind.COUNT, StatKind.safeValueOf("count"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun unknownName() {
        StatKind.safeValueOf("coun")
    }
}
