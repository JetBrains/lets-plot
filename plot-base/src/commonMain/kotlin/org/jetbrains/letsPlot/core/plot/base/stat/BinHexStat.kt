/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.StatContext

// TODO: Add parameters as in the Bin2dStat
class BinHexStat : BaseStat(DEF_MAPPING) {
    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X, Aes.Y, Aes.WEIGHT)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, Aes.X, Aes.Y)) {
            return withEmptyStatValues()
        }

        // TODO: Implement the stat
        return DataFrame.Builder()
            .putNumeric(Stats.X, listOf(-1.0, 1.0, 0.0))
            .putNumeric(Stats.Y, listOf(0.0, 0.0, 1.0))
            .putNumeric(Stats.COUNT, listOf(1.0, 3.0, 6.0))
            .putNumeric(Stats.DENSITY, listOf(0.1, 0.3, 0.6))
            .build()
    }

    companion object {
        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y,
            Aes.FILL to Stats.COUNT
        )
    }
}