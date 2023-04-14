/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext

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

    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X, Aes.Y, Aes.Z)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, Aes.X, Aes.Y, Aes.Z)) {
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

        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y
        )
    }
}
