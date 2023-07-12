/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.sampling.method

import org.jetbrains.letsPlot.core.plot.base.DataFrame
import jetbrains.datalore.plot.builder.sampling.PointSampling
import kotlin.math.round

/**
 * Take points at indices selected at regular intervals starting from index 0
 */
internal class SystematicSampling(sampleSize: Int) : SamplingBase(sampleSize),
    PointSampling {

    override val expressionText: String
        get() = "sampling_" + ALIAS + "(" +
                "n=" + sampleSize +
                ")"

    override fun isApplicable(population: DataFrame): Boolean {
        return super.isApplicable(population) && computeStep(population.rowCount()) >= 2
    }

    override fun apply(population: DataFrame): DataFrame {
        require(isApplicable(population))
        val popSize = population.rowCount()

        val step = computeStep(popSize)
        val pickedIndices = ArrayList<Int>()
        var i = 0
        while (i < popSize) {
            pickedIndices.add(i)
            i += step
        }

        return population.selectIndices(pickedIndices)
    }

    private fun computeStep(popSize: Int): Int {
        return round(popSize.toDouble() / (sampleSize - 1)).toInt()
    }

    companion object {
        const val ALIAS = "systematic"

        fun computeStep(popSize: Int, sampleSize: Int): Int {
            return round((popSize - 1).toDouble() / (sampleSize - 1)).toInt()
        }
    }
}
