package jetbrains.datalore.base.geometry

import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertTrue

class DoubleVectorOperationsTest {

    @Test
    fun simpleRotation() {
        val v = DoubleVector(1.0, 0.0)

        val delta = DoubleVector(0.0, 1.0).subtract(v.rotate(PI / 2))
        assertTrue(delta.length() < EPS)
    }

    companion object {
        private val EPS = 0.0001
    }
}