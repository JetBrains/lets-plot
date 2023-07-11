/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.common.geometry

import org.jetbrains.letsPlot.commons.intern.gcommon.collect.Stack
import org.jetbrains.letsPlot.commons.geometry.DoubleSegment
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.plot.common.geometry.PolylineSimplifier.RankingStrategy

internal class DouglasPeuckerSimplification : RankingStrategy {
    private val myEpsilon = Double.MIN_VALUE

    private fun calculateWeights(points: List<DoubleVector>): List<Double> {
        val stack = Stack<Pair<Int, Int>>()

        val weights = MutableList(points.size) { Double.NaN }
        weights[0] = Double.MAX_VALUE
        weights[points.size - 1] = Double.MAX_VALUE
        stack.push(Pair(0, points.size - 1))

        while (!stack.empty()) {
            val startIndex = stack.peek()!!.first
            val endIndex = stack.peek()!!.second
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
