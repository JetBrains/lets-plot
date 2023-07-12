/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.StatContext
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar

class ContourfStat(binCount: Int, binWidth: Double?) : BaseStat(DEF_MAPPING) {

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

        val xRange = data.range(TransformVar.X)!!
        val yRange = data.range(TransformVar.Y)!!
        val zRange = data.range(TransformVar.Z)!!
        val helper = ContourFillHelper(xRange, yRange)
        val fillLevels = ContourFillHelper.computeFillLevels(zRange, levels)
        val polygonListByFillLevel = helper.createPolygons(pathListByLevel, levels, fillLevels)

        return Contour.getPolygonDataFrame(
            fillLevels,
            polygonListByFillLevel
        )
    }

    companion object {
        private val DEF_MAPPING: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, DataFrame.Variable> = mapOf(
            org.jetbrains.letsPlot.core.plot.base.Aes.X to Stats.X,
            org.jetbrains.letsPlot.core.plot.base.Aes.Y to Stats.Y
        )
    }
}
