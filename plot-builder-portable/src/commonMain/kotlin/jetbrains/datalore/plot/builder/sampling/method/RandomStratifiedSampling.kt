/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.sampling.method

import org.jetbrains.letsPlot.core.plot.base.DataFrame
import jetbrains.datalore.plot.builder.data.GroupUtil
import org.jetbrains.letsPlot.core.plot.base.util.SamplingUtil
import jetbrains.datalore.plot.builder.sampling.GroupAwareSampling
import kotlin.math.max
import kotlin.math.round
import kotlin.random.Random

internal class RandomStratifiedSampling(
        sampleSize: Int,
        private val mySeed: Long?,
        private val myMinSubsampleSize: Int?) : SamplingBase(sampleSize),
    GroupAwareSampling {

    override val expressionText: String
        get() = "sampling_" + ALIAS + "(" +
                "n=" + sampleSize +
                (if (mySeed != null) ", seed=$mySeed" else "") +
                (if (myMinSubsampleSize != null) ", min_subsample=$myMinSubsampleSize" else "") +
                ")"

    override fun isApplicable(population: DataFrame, groupMapper: (Int) -> Int): Boolean {
        return population.rowCount() > sampleSize
    }

    override fun apply(population: DataFrame, groupMapper: (Int) -> Int): DataFrame {
        require(isApplicable(population, groupMapper))
        val indicesByGroup = GroupUtil.indicesByGroup(population.rowCount(), groupMapper)

        var minSubSampleSize = myMinSubsampleSize ?: DEF_MIN_SUBSAMPLE_SIZE
        minSubSampleSize = max(0, minSubSampleSize)
        val popSize = population.rowCount()

        val pickIndices = ArrayList<Int>()
        val rand = mySeed?.let { Random(it) } ?: Random.Default
        for (group in indicesByGroup.keys) {
            val groupIndices = indicesByGroup[group]!!
            val stratumSize = groupIndices.size
            // proportionate allocation
            val ratio = stratumSize.toDouble() / popSize

            var stratumSampleSize = round(sampleSize * ratio).toInt()
            stratumSampleSize = max(stratumSampleSize, minSubSampleSize)

            if (stratumSampleSize >= stratumSize) {
                pickIndices.addAll(groupIndices)
            } else {

                val sampleGroupIndices =
                    SamplingUtil.sampleWithoutReplacement(stratumSize,
                        stratumSampleSize,
                        rand,
                        { indexSet ->
                            (0 until groupIndices.size)
                                .filter { indexSet.contains(it) }
                        },
                        { indexSet ->
                            (0 until groupIndices.size)
                                .filter { i -> !indexSet.contains(i) }
                        })

                for (i in sampleGroupIndices) {
                    pickIndices.add(groupIndices[i])
                }
            }
        }

        return population.selectIndices(pickIndices)
    }

    companion object {
        internal const val ALIAS = "random_stratified"
        private const val DEF_MIN_SUBSAMPLE_SIZE = 2 // min needed to draw a line
    }
}
