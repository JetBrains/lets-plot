/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.common.geometry

import org.jetbrains.letsPlot.commons.geometry.DoubleVector

class PolylineSimplifier private constructor(private val myPoints: List<DoubleVector>, strategy: RankingStrategy) {
    private val myWeights: List<Double>
    private var myWeightLimit = Double.NaN
    private var myCountLimit = -1

    val points: List<DoubleVector>
        get() =
            indices.map { myPoints[it] }

    val indices: List<Int>
        get() {
            val sorted = (0 until myPoints.size)
                    .map { i -> Pair(i, myWeights[i]) }
                    .filter { p -> !getWeight(p).isNaN() }
                    .sortedWith(compareBy<Pair<Int, Double>> { this.getWeight(it) }.reversed())

            val filtered: Collection<Pair<Int, Double>>
            if (isWeightLimitSet) {
                filtered = sorted.filter { p -> getWeight(p) > myWeightLimit }
            } else {
                filtered = sorted.take(myCountLimit)
            }

            return filtered
                    .map { this.getIndex(it) }
                    .sorted()
        }

    private val isWeightLimitSet: Boolean
        get() = !myWeightLimit.isNaN()

    init {
        myWeights = strategy.getWeights(myPoints)
    }

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
    }
}
