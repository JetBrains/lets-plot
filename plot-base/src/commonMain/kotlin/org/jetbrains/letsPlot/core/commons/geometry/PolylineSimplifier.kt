/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.geometry

import org.jetbrains.letsPlot.commons.geometry.DoubleVector

class PolylineSimplifier private constructor(
    private val myPoints: List<List<DoubleVector>>,
    strategy: RankingStrategy
) {
    private val myWeights: List<WeightedPoint> = myPoints.mapIndexed { ringIndex, sub ->
        val weights = strategy.getWeights(sub)
        weights.mapIndexed() { pointIndex, weight -> WeightedPoint(ringIndex, pointIndex, weight) }
    }.flatten()

    private var myWeightLimit = Double.NaN
    private var myCountLimit = -1

    val points: List<DoubleVector> by lazy { myPoints.slice(indices) }

    val indices: List<Int>
        get() {
            val sorted = myWeights.withIndex()
                .filter { (_, weight) -> weight.isFinite() }
                .sortedByDescending { (_, weight) -> weight }

            val filtered = when (isWeightLimitSet) {
                true -> sorted.filter { (_, weight) -> weight > myWeightLimit }
                false -> sorted.take(myCountLimit)
            }

            return filtered
                .map { (index, _) -> index }
                .sorted()
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

    private fun getWeight(p: Pair<Int, Double>): Double {
        return p.second
    }

    private fun getIndex(p: Pair<Int, Double>): Int {
        return p.first
    }

    interface RankingStrategy {
        fun getWeights(points: List<DoubleVector>): List<Double>
    }

    companion object {
        const val DOUGLAS_PEUCKER_PIXEL_THRESHOLD = 0.25

        fun visvalingamWhyatt(points: List<DoubleVector>): PolylineSimplifier {
            return PolylineSimplifier(
                points,
                VisvalingamWhyattSimplification()
            )
        }

        fun douglasPeucker(points: List<DoubleVector>): PolylineSimplifier {
            return PolylineSimplifier(
                points,
                DouglasPeuckerSimplification()
            )
        }

        fun douglasPeucker(points: List<DoubleVector>, threshold: Double): List<DoubleVector> {
            return douglasPeucker(points).setWeightLimit(threshold).points
        }

    }

    private data class WeightedPoint(
        val ringIndex: Int,
        val pointIndex: Int,
        val weight: Double
    )
}
