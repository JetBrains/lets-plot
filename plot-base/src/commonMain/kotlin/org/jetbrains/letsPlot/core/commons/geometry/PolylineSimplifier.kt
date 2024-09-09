/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.geometry

import org.jetbrains.letsPlot.commons.geometry.DoubleVector

class PolylineSimplifier private constructor(
    private val geometry: List<List<DoubleVector>>,
    strategy: RankingStrategy
) {
    private val myWeights: List<WeightedPoint> = geometry.mapIndexed { subPathIndex, subPath ->
        val weights = strategy.computeWeights(subPath)
        weights.mapIndexed() { pointIndex, weight -> WeightedPoint(subPathIndex, pointIndex, weight) }
    }.flatten()

    private var myWeightLimit = Double.NaN
    private var myCountLimit = -1

    val points: List<List<DoubleVector>> by lazy {
        indices.zip(geometry)
            .map { (indices, points) -> points.slice(indices) }
    }

    val indices: List<List<Int>>
        get() {
            val filtered = when (isWeightLimitSet) {
                true -> myWeights.filter { it.weight > myWeightLimit }
                false -> myWeights.sortedByDescending(WeightedPoint::weight).take(myCountLimit)
            }

            return filtered
                .groupBy(WeightedPoint::subPathIndex)
                .entries
                .sortedBy { (subPathIndex, _) -> subPathIndex }
                .map { (_, weightedPoints) ->
                    weightedPoints
                        .map(WeightedPoint::pointIndex)
                        .sorted()
                }
        }

    private val isWeightLimitSet: Boolean
        get() = !myWeightLimit.isNaN()

    fun setWeightLimit(weightLimit: Double): PolylineSimplifier {
        myWeightLimit = weightLimit
        myCountLimit = -1
        return this
    }

    fun setCountLimit(countLimit: Int): PolylineSimplifier {
        myWeightLimit = Double.NaN
        myCountLimit = countLimit
        return this
    }

    interface RankingStrategy {
        fun computeWeights(points: List<DoubleVector>): List<Double>
    }

    companion object {
        const val DOUGLAS_PEUCKER_PIXEL_THRESHOLD = 0.25

        fun visvalingamWhyatt(points: List<DoubleVector>): PolylineSimplifier {
            return PolylineSimplifier(
                listOf(points),
                VisvalingamWhyattSimplification()
            )
        }

        fun visvalingamWhyattMultipath(points: List<List<DoubleVector>>): PolylineSimplifier {
            return PolylineSimplifier(
                points,
                VisvalingamWhyattSimplification()
            )
        }

        fun douglasPeucker(points: List<DoubleVector>): PolylineSimplifier {
            return PolylineSimplifier(
                listOf(points),
                DouglasPeuckerSimplification()
            )
        }

        fun douglasPeuckerMultipath(points: List<List<DoubleVector>>): PolylineSimplifier {
            return PolylineSimplifier(
                points,
                DouglasPeuckerSimplification()
            )
        }

        fun douglasPeucker(points: List<DoubleVector>, threshold: Double): List<DoubleVector> {
            return douglasPeucker(points).setWeightLimit(threshold).points.single()
        }

        fun douglasPeuckerMultipath(points: List<List<DoubleVector>>, threshold: Double): List<List<DoubleVector>> {
            return douglasPeuckerMultipath(points).setWeightLimit(threshold).points
        }

    }

    internal data class WeightedPoint(
        val subPathIndex: Int,
        val pointIndex: Int,
        val weight: Double
    )
}
