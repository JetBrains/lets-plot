/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.util

import jetbrains.datalore.plot.base.DataFrame
import kotlin.random.Random

object SamplingUtil {

    fun generateIndexSampleWithoutReplacement(popSize: Int, sampleSize: Int, rand: Random): Set<Int> {
        val indexSet = HashSet<Int>()

        while (indexSet.size < sampleSize) {
            indexSet.add(rand.nextInt(popSize))
        }

        return indexSet
    }

    fun <T> sampleWithoutReplacement(
        popSize: Int, sampleSize: Int, rand: Random, onPick: (Set<Int>) -> T, onDrop: (Set<Int>) -> T
    ): T {
        val pick = sampleSize <= popSize / 2
        val indexCount = if (pick) sampleSize else popSize - sampleSize
        val indexSet = generateIndexSampleWithoutReplacement(popSize, indexCount, rand)

        return if (pick) onPick(indexSet) else onDrop(indexSet)
    }

    fun sampleWithoutReplacement(sampleSize: Int, rand: Random, data: DataFrame): DataFrame {
        val pick = sampleSize <= data.rowCount() / 2
        val indexCount = if (pick) sampleSize else data.rowCount() - sampleSize
        val indexSet = generateIndexSampleWithoutReplacement(data.rowCount(), indexCount, rand)

        return if (pick) data.selectIndices(indexSet) else data.dropIndices(indexSet)
    }
}
