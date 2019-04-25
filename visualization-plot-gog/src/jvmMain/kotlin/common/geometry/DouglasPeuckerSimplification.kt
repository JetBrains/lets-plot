package jetbrains.datalore.visualization.plot.gog.common.geometry

import jetbrains.datalore.base.geometry.DoubleSegment
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Pair
import jetbrains.datalore.visualization.plot.gog.common.geometry.PolylineSimplifier.RankingStrategy
import java.util.*
import java.util.Collections.nCopies
import java.util.function.Function

internal class DouglasPeuckerSimplification : RankingStrategy {
    private val myEpsilon = java.lang.Double.MIN_VALUE

    private fun calculateWeights(points: List<DoubleVector>): List<Double> {
        val stack = Stack<Pair<Int, Int>>()
        val pointsToKeep = BitSet(points.size)
        pointsToKeep.set(0)
        pointsToKeep.set(points.size - 1)

        val weights = ArrayList(nCopies(points.size, java.lang.Double.NaN))
        weights[0] = java.lang.Double.MAX_VALUE
        weights[points.size - 1] = java.lang.Double.MAX_VALUE
        stack.push(Pair(0, points.size - 1))

        while (!stack.empty()) {
            val startIndex = stack.peek().first!!
            val endIndex = stack.peek().second!!
            stack.pop()

            var dMax = 0.0
            var index = startIndex
            val doubleSegment = DoubleSegment(points[startIndex], points[endIndex])
            val distance: Function<DoubleVector, Double>

            if (doubleSegment.length() == 0.0) {
                distance = Function { p -> DoubleSegment(points[startIndex], p).length() }
            } else {
                distance = Function { doubleSegment.distance(it) }
            }

            var i = startIndex + 1
            while (i < endIndex) {
                val d = distance.apply(points[i])
                if (d > dMax) {
                    index = i
                    dMax = d
                }
                ++i
            }

            if (dMax >= myEpsilon) {
                stack.push(Pair(startIndex, index))
                stack.push(Pair(index, endIndex))
                pointsToKeep.set(index)
                weights[index] = dMax
            }
        }

        return weights
    }

    override fun getWeights(points: List<DoubleVector>): List<Double> {
        return calculateWeights(points)
    }
}
