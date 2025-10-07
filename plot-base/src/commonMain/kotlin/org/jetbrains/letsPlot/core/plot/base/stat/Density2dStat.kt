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

class Density2dStat constructor(
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
    bandWidthX = bandWidthX,
    bandWidthY = bandWidthY,
    bandWidthMethod = bandWidthMethod,
    adjust = adjust,
    kernel = kernel,
    nX = nX,
    nY = nY,
    isContour = isContour,
    binCount = binCount,
    binWidth = binWidth,
    defaultMappings = DEF_MAPPING
) {

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, Aes.X, Aes.Y)) {
            return withEmptyStatValues()
        }

        val (xVector, yVector, groupWeight) = SeriesUtil.filterFinite(
            data.getNumeric(TransformVar.X),
            data.getNumeric(TransformVar.Y),
            BinStatUtil.weightVector(data.rowCount(), data)
        )

        // if no data, return empty
        if (xVector.isEmpty()) {
            return withEmptyStatValues()
        }

        // if length of x and y doesn't match, throw error
        if (xVector.size != yVector.size) {
            throw RuntimeException("len(x)= " + xVector.size + " and len(y)= " + yVector.size + " doesn't match!")
        }

        val xRange = statCtx.overallXRange()
        val yRange = statCtx.overallYRange()

        val (stepsX, stepsY, densityMatrix) = density2dGrid(xVector, yVector, groupWeight, xRange!!, yRange!!)

        val statX = ArrayList<Double>()
        val statY = ArrayList<Double>()
        val statDensity = ArrayList<Double>()

        for (row in 0 until nY) {
            for (col in 0 until nX) {
                statX.add(stepsX[col])
                statY.add(stepsY[row])
                statDensity.add(densityMatrix.getEntry(row, col) / SeriesUtil.sum(groupWeight))
                //newGroups.add((double) (int) group);
            }
        }

        if (isContour) {
            val zRange = DoubleSpan.encloseAllQ(statDensity)
            val levels = ContourStatUtil.computeLevels(zRange, binOptions)
                ?: return DataFrame.Builder.emptyFrame()

            val pathListByLevel = ContourStatUtil.computeContours(
                xRange,
                yRange,
                nX,
                nY,
                statDensity,
                levels
            )

            return Contour.getPathDataFrame(levels, pathListByLevel)
        } else {
            return DataFrame.Builder()
                .putNumeric(Stats.X, statX)
                .putNumeric(Stats.Y, statY)
                .putNumeric(Stats.DENSITY, statDensity)
                .build()
        }
    }

    companion object {
        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y
        )
    }
}
