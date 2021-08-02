/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.sampling.method

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.builder.sampling.PointSampling
import jetbrains.datalore.plot.builder.sampling.method.SamplingUtil.xVar

/**
 * Picks first N data points with unique X-values. In addition scoops all data-points with X-values
 * which are already being accepted to the sample (to account for grouping)
 */
internal class PickSampling(sampleSize: Int) : SamplingBase(sampleSize),
    PointSampling {

    override val expressionText: String
        get() = "sampling_" + ALIAS + "(" +
                "n=" + sampleSize + ")"

    override fun apply(population: DataFrame): DataFrame {
        require(isApplicable(population))

        val xVar = xVar(population)
        val xFactors = population.distinctValues(xVar)
        if (xFactors.size <= sampleSize) {
            return population
        }

        val pickX = xFactors.take(sampleSize).toSet()

        val xValues = population[xVar]
        val pickedIndices = ArrayList<Int>()
        for ((index, v) in xValues.withIndex()) {
            if (v in pickX) {
                pickedIndices.add(index)
            }
        }

        return population.selectIndices(pickedIndices)
    }

    companion object {
        const val ALIAS = "pick"
    }
}
