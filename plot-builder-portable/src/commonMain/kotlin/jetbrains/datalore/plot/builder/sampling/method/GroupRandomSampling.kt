/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.sampling.method

import org.jetbrains.letsPlot.core.plot.base.DataFrame
import kotlin.random.Random

internal class GroupRandomSampling(sampleSize: Int, private val mySeed: Long?) : GroupSamplingBase(sampleSize) {

    override val expressionText: String
        get() = "sampling_" + ALIAS + "(" +
                "n=" + sampleSize +
                (if (mySeed != null) ", seed=$mySeed" else "") +
                ")"

    override fun apply(population: DataFrame, groupMapper: (Int) -> Int): DataFrame {
        require(isApplicable(population, groupMapper))
        val distinctGroups = SamplingUtil.distinctGroups(
            groupMapper,
            population.rowCount()
        )

        distinctGroups.shuffle(createRandom())
        val pickedGroups = distinctGroups.take(sampleSize).toSet()
        return doSelect(population, pickedGroups, groupMapper)
    }

    private fun createRandom(): Random {
        return mySeed?.let { Random(it) } ?: Random.Default
    }

    companion object {
        const val ALIAS = "group_random"
    }
}
