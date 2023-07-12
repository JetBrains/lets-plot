/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.util

import org.jetbrains.letsPlot.core.plot.base.DataFrame
import kotlin.random.Random

object SamplingUtil {

    fun <T> sampleWithoutReplacement(
        popSize: Int, sampleSize: Int, rand: Random, onPick: (Set<Int>) -> T, onDrop: (Set<Int>) -> T
    ): T {
        val pick = sampleSize <= popSize / 2
        val indexCount = if (pick) sampleSize else popSize - sampleSize

        val indexSet = HashSet<Int>()

        while (indexSet.size < indexCount) {
            indexSet.add(rand.nextInt(popSize))
        }

        return if (pick) onPick(indexSet) else onDrop(indexSet)
    }

    fun sampleWithoutReplacement(sampleSize: Int, rand: Random, data: DataFrame): DataFrame {
        return sampleWithoutReplacement(
            data.rowCount(),
            sampleSize,
            rand,
            { data.selectIndices(it) },
            { data.dropIndices(it) }
        )
    }
}
