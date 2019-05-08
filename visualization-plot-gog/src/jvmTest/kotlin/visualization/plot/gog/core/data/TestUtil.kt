package jetbrains.datalore.visualization.plot.gog.core.data

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.gog.common.geometry.Utils
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame.Builder
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame.Variable
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Assertions

object TestUtil {
    internal fun generateData(rowCount: Int, varNames: Collection<String>): DataFrame {
        val variables = varNames.map { Variable(it) }

        val builder = Builder()
        for (variable in variables) {
            builder.put(variable, toSerie(variable.name, indices(rowCount)))
        }

        return builder.build()
    }

    internal fun indices(count: Int): List<Int> {
        return (0 until count).toList()
    }

    internal fun toSerie(prefix: String, ints: Collection<Int>): List<*> {
        return ints.map { v -> prefix + v!! }
    }

    internal fun createCircle(pointsCount: Int, r: Double): List<DoubleVector> {
        @Suppress("NAME_SHADOWING")
        var pointsCount = pointsCount
        val circle = ArrayList<DoubleVector>()
        val step = 2 * Math.PI / pointsCount++
        var angle = 0.0
        while (pointsCount-- > 0) {
            circle.add(DoubleVector(r * Math.cos(angle), r * Math.sin(angle)))
            angle += step
        }

        circle[circle.size - 1] = circle[0]

        return circle
    }

    internal fun getPointsCount(rings: List<List<DoubleVector>>): Int {
        return rings.stream().mapToInt { it.size }.sum()
    }

    internal class RingAssertion internal constructor(ring: List<DoubleVector>) : AbstractAssert<RingAssertion, List<DoubleVector>>(ring, RingAssertion::class.java) {

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
