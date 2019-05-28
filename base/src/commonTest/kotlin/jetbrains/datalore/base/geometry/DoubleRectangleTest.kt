package jetbrains.datalore.base.geometry

import kotlin.test.Test
import kotlin.test.assertEquals


class DoubleRectangleTest {
    @Test
    fun hashCodeWorks() {
        assertEquals(DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO).hashCode(),
                DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO).hashCode())
    }

}
