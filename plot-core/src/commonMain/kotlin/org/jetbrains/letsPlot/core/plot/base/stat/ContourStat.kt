/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.StatContext

/**
 * see doc for geom_contour/stat_contour
 *
 *
 * see also examples: https://www.inside-r.org/packages/cran/ggplot2/docs/stat_contour
 *
 *
 *
 *
 * Defaults:
 *
 *
 * geom = "path"
 * position = "identity"
 *
 *
 * Other params:
 *
 *
 * bins (def - 10) - Number of bins (overridden by binwidth)
 * binwidth - distance between contours.
 *
 *
 *
 *
 * Adds columns:
 *
 *
 * level - height of contour
 */
class ContourStat(binCount: Int, binWidth: Double?) : BaseStat(DEF_MAPPING) {

    private val myBinOptions = BinStatUtil.BinOptions(binCount, binWidth)

    override fun consumes(): List<org.jetbrains.letsPlot.core.plot.base.Aes<*>> {
        return listOf(org.jetbrains.letsPlot.core.plot.base.Aes.X, org.jetbrains.letsPlot.core.plot.base.Aes.Y, org.jetbrains.letsPlot.core.plot.base.Aes.Z)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, org.jetbrains.letsPlot.core.plot.base.Aes.X, org.jetbrains.letsPlot.core.plot.base.Aes.Y, org.jetbrains.letsPlot.core.plot.base.Aes.Z)) {
            return withEmptyStatValues()
        }

        val levels = ContourStatUtil.computeLevels(data, myBinOptions)
            ?: return withEmptyStatValues()

        val pathListByLevel = ContourStatUtil.computeContours(data, levels)

        // transform paths to x/y data
        return Contour.getPathDataFrame(levels, pathListByLevel)
    }

    companion object {
        const val DEF_BIN_COUNT = 10

        private val DEF_MAPPING: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, DataFrame.Variable> = mapOf(
            org.jetbrains.letsPlot.core.plot.base.Aes.X to Stats.X,
            org.jetbrains.letsPlot.core.plot.base.Aes.Y to Stats.Y
        )
    }
}
