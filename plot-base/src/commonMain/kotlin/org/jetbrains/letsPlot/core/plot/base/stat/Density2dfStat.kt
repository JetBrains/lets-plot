/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.StatContext
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar

class Density2dfStat(
    bandWidthX: Double?,
    bandWidthY: Double?,
    bandWidthMethod: DensityStat.BandWidthMethod,  // Used is `bandWidth` is not set.
    adjust: Double,
    kernel: DensityStat.Kernel,
    nX: Int,
    nY: Int,
    isContour: Boolean,
    binCount: Int,
    binWidth: Double
) : AbstractDensity2dStat(
    defaultMappings = DEF_MAPPING,
    bandWidthX = bandWidthX,
    bandWidthY = bandWidthY,
    bandWidthMethod = bandWidthMethod,
    adjust = adjust,
    kernel = kernel,
    nX = nX,
    nY = nY,
    isContour = isContour,
    binCount = binCount,
    binWidth = binWidth
) {

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, Aes.X, Aes.Y)) {
            return withEmptyStatValues()
        }

        val xs = data.getNumeric(TransformVar.X)
        val ys = data.getNumeric(TransformVar.Y)
        val (xVector, yVector) = (xs zip ys)
            .filter { SeriesUtil.allFinite(it.first, it.second) }
            .unzip()

        // if no data, return empty
        if (xVector.isEmpty()) {
            return withEmptyStatValues()
        }

        // if length of x and y doesn't match, throw error
        if (xVector.size != yVector.size) {
            throw RuntimeException("len(x)= " + xVector.size + " and len(y)= " + yVector.size + " doesn't match!")
        }

        val groupWeight = BinStatUtil.weightVector(xVector.size, data)
        val xRange = statCtx.overallXRange()!!
        val yRange = statCtx.overallYRange()!!

        val statData = getStatData(xVector, yVector, groupWeight, xRange, yRange)

        if (isContour) {
            // ToDo: change zrange into the range of z over entire dataset
            val zRange = DoubleSpan.encloseAllQ(statData.getValue(Stats.DENSITY))
            val levels = ContourStatUtil.computeLevels(zRange, binOptions)
                ?: return DataFrame.Builder.emptyFrame()

            val pathListByLevel = ContourStatUtil.computeContours(
                xRange,
                yRange,
                nX,
                nY,
                statData.getValue(Stats.DENSITY),
                levels
            )
            val helper = ContourFillHelper(xRange, yRange)
            val fillLevels =
                ContourFillHelper.computeFillLevels(zRange!!, levels)
            val polygonListByFillLevel = helper.createPolygons(pathListByLevel, levels, fillLevels)

            return Contour.getPolygonDataFrame(
                fillLevels.subList(
                    1,
                    fillLevels.size
                ), polygonListByFillLevel
            )
        } else {
            val builder = DataFrame.Builder()
            for ((variable, series) in statData) {
                builder.putNumeric(variable, series)
            }
            return builder.build()
        }
    }

    companion object {
        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y
        )
    }
}
