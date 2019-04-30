package jetbrains.datalore.visualization.plot.gog.common.geometry

import jetbrains.datalore.base.gcommon.collect.Stack
import jetbrains.datalore.base.geometry.DoubleSegment
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Pair
import jetbrains.datalore.visualization.plot.gog.common.geometry.PolylineSimplifier.RankingStrategy

internal class DouglasPeuckerSimplification : RankingStrategy {
    private val myEpsilon = Double.MIN_VALUE

    private fun calculateWeights(points: List<DoubleVector>): List<Double> {
        val stack = Stack<Pair<Int, Int>>()

        val weights = MutableList(points.size) { Double.NaN }
        weights[0] = Double.MAX_VALUE
        weights[points.size - 1] = Double.MAX_VALUE
        stack.push(Pair(0, points.size - 1))

        while (!stack.empty()) {
            val startIndex = stack.peek()!!.first!!
            val endIndex = stack.peek()!!.second!!
            stack.pop()

            var dMax = 0.0
            var index = startIndex
            val doubleSegment = DoubleSegment(points[startIndex], points[endIndex])
            val distance: (DoubleVector) -> Double

            if (doubleSegment.length() == 0.0) {
                distance = { p -> DoubleSegment(points[startIndex], p).length() }
            } else {
                distance = { doubleSegment.distance(it) }
            }

            var i = startIndex + 1
            while (i < endIndex) {
                val d = distance(points[i])
                if (d > dMax) {
                    index = i
                    dMax = d
                }
                ++i
            }

            if (dMax >= myEpsilon) {
                stack.push(Pair(startIndex, index))
                stack.push(Pair(index, endIndex))
                weights[index] = dMax
            }
        }

        return weights
    }

    override fun getWeights(points: List<DoubleVector>): List<Double> {
        return calculateWeights(points)
    }
}
