/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.sampling.method

import jetbrains.datalore.base.algorithms.calculateArea
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.DataFrame.Variable
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.common.util.MutableDouble
import jetbrains.datalore.plot.common.util.MutableInteger
import jetbrains.datalore.plot.builder.sampling.method.VertexSampling.DoubleVectorComponentsList
import kotlin.math.min
import kotlin.math.roundToInt

internal object SamplingUtil {

    fun groupCount(groupMapper: (Int) -> Int, size: Int): Int {
        return (0 until size).map { groupMapper(it) }.distinct().count()
    }

    fun distinctGroups(groupMapper: (Int) -> Int, size: Int): MutableList<Int> {
        return (0 until size).map { groupMapper(it) }.distinct().toMutableList()
    }

    fun xVar(variables: Set<Variable>): Variable? {
        return when {
            Stats.X in variables -> Stats.X
            TransformVar.X in variables -> TransformVar.X
            else -> null
        }
    }
    fun xVar(data: DataFrame): Variable {
        return xVar(data.variables())
            ?: throw IllegalStateException("Can't apply sampling: couldn't deduce the (X) variable.")
    }

    fun yVar(data: DataFrame): Variable {
        if (data.has(Stats.Y)) {
            return Stats.Y
        } else if (data.has(TransformVar.Y)) {
            return TransformVar.Y
        }
        throw IllegalStateException("Can't apply sampling: couldn't deduce the (Y) variable.")
    }

    fun splitRings(population: DataFrame): List<List<DoubleVector>> {
        val rings = ArrayList<List<DoubleVector>>()
        var lastPoint: DoubleVector? = null
        var start = -1

        @Suppress("UNCHECKED_CAST")
        val xValues = population[xVar(population)] as List<Any>

        @Suppress("UNCHECKED_CAST")
        val yValues = population[yVar(population)] as List<Any>
        val points = DoubleVectorComponentsList(xValues, yValues)
        for (i in points.indices) {
            val point = points[i]
            if (start < 0) {
                start = i
                lastPoint = point
            } else if (lastPoint == point) {
                rings.add(points.subList(start, i + 1))
                start = -1
                lastPoint = null
            }
        }
        if (start >= 0) {
            // not closed
            rings.add(points.subList(start, points.size))
        }
        return rings
    }

    fun calculateRingLimits(rings: List<List<DoubleVector>>, totalPointsLimit: Int): List<Int> {
        val totalArea = rings.map { calculateArea(it) }.sum()

        val areaProceed = MutableDouble(0.0)
        val pointsProceed = MutableInteger(0)

        return rings.indices
            .asSequence()
            .map { Pair(it, calculateArea(rings[it])) }
            .sortedWith(compareBy<Pair<*, Double>> {
                getRingArea(
                    it
                )
            }.reversed())
            .map { p ->
                var limit = min(
                    (p.second / (totalArea - areaProceed.get()) * (totalPointsLimit - pointsProceed.get())).roundToInt(),
                    rings[getRingIndex(p)].size
                )

                if (limit >= 4) {
                    areaProceed.getAndAdd(getRingArea(p))
                    pointsProceed.getAndAdd(limit)
                } else {
                    limit = 0
                }

                Pair(getRingIndex(p), limit)
            }
            .sortedWith(compareBy { getRingIndex(it) })
            .map { getRingLimit(it) }
            .toList()
    }

    fun getRingIndex(pair: Pair<Int, *>): Int {
        return pair.first
    }

    private fun getRingArea(pair: Pair<*, Double>): Double {
        return pair.second
    }

    fun getRingLimit(pair: Pair<*, Int>): Int {
        return pair.second
    }
}
