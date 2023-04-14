/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.data.TransformVar

class ContourfStat(binCount: Int, binWidth: Double?) : BaseStat(DEF_MAPPING) {

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
        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y
        )
    }
}
