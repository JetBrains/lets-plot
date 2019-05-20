package jetbrains.datalore.visualization.plot.base.data

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.common.geometry.Utils
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Assertions
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object TestUtil {

    internal fun createCircle(pointsCount: Int, r: Double): List<DoubleVector> {
        @Suppress("NAME_SHADOWING")
        var pointsCount = pointsCount
        val circle = ArrayList<DoubleVector>()
        val step = 2 * PI / pointsCount++
        var angle = 0.0
        while (pointsCount-- > 0) {
            circle.add(DoubleVector(r * cos(angle), r * sin(angle)))
            angle += step
        }

        circle[circle.size - 1] = circle[0]

        return circle
    }

    internal fun getPointsCount(rings: List<List<DoubleVector>>): Int {
        return rings.map { it.size }.sum()
    }

    internal class RingAssertion internal constructor(ring: List<DoubleVector>) : AbstractAssert<RingAssertion, List<DoubleVector>>(ring, TestUtil.RingAssertion::class.java) {

        val isClosed: RingAssertion
            get() {
                Assertions.assertThat(actual[0]).isEqualTo(actual[actual.size - 1])
                return this
            }

        fun hasSize(expected: Int): RingAssertion {
            Assertions.assertThat(actual).hasSize(expected)
            return this
        }

        fun hasArea(expected: Double): RingAssertion {
            return hasArea(expected, 0.001)
        }

        private fun hasArea(expected: Double, epsilon: Double): RingAssertion {
            Assertions.assertThat(Utils.calculateArea(actual)).isEqualTo(expected, Assertions.offset(epsilon))
            return this
        }

        companion object {

            fun assertThatRing(ring: List<DoubleVector>): RingAssertion {
                return RingAssertion(ring)
            }
        }
    }
}