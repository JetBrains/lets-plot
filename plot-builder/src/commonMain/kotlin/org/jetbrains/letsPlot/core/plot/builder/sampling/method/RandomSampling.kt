/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.sampling.method

import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.util.SamplingUtil
import org.jetbrains.letsPlot.core.plot.builder.sampling.PointSampling
import kotlin.random.Random

/**
 * Take any point with equal probability without replacement
 */
internal class RandomSampling(sampleSize: Int, private val mySeed: Long?) : SamplingBase(sampleSize),
    PointSampling {

    override val expressionText: String
        get() = "sampling_" + ALIAS + "(" +
                "n=" + sampleSize +
                (if (mySeed != null) ", seed=$mySeed" else "") +
                ")"

    override fun apply(population: DataFrame): DataFrame {
        require(isApplicable(population))
        val rand = mySeed?.let { Random(it) } ?: Random.Default

        return SamplingUtil.sampleWithoutReplacement(sampleSize, rand, population)
    }

    companion object {
        const val ALIAS = "random"
    }
}
