/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.base.stat.math3.BlockRealMatrix
import jetbrains.datalore.plot.common.data.SeriesUtil

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

        val xVector = data.getNumeric(TransformVar.X)
        val yVector = data.getNumeric(TransformVar.Y)

        // if no data, return empty
        if (xVector.isEmpty()) {
            return DataFrame.Builder.emptyFrame()
        }

        // if length of x and y doesn't match, throw error
        if (xVector.size != yVector.size) {
            throw RuntimeException("len(x)= " + xVector.size + " and len(y)= " + yVector.size + " doesn't match!")
        }

        val xRange = statCtx.overallXRange()
        val yRange = statCtx.overallYRange()

        val statX = ArrayList<Double>()
        val statY = ArrayList<Double>()
        val statDensity = ArrayList<Double>()

        val bandWidth = DoubleArray(2)
//        bandWidth[0] = if (bandWidths != null) bandWidths!![0] else DensityStatUtil.bandWidth(
//            bandWidthMethod,
//            xVector
//        )
        bandWidth[0] = getBandWidthX(xVector)

//        bandWidth[1] = if (bandWidths != null) bandWidths!![1] else DensityStatUtil.bandWidth(
//            bandWidthMethod,
//            yVector
//        )
        bandWidth[1] = getBandWidthY(yVector)

        val stepsX = DensityStatUtil.createStepValues(xRange!!, nX)
        val stepsY = DensityStatUtil.createStepValues(yRange!!, nY)

        // weight aesthetics
        val groupWeight = BinStatUtil.weightVector(xVector.size, data)

        val matrixX = BlockRealMatrix(
            DensityStatUtil.createRawMatrix(
                xVector,
                stepsX,
                kernelFun,
                bandWidth[0],
                adjust,
                groupWeight
            )
        )
        val matrixY = BlockRealMatrix(
            DensityStatUtil.createRawMatrix(
                yVector,
                stepsY,
                kernelFun,
                bandWidth[1],
                adjust,
                groupWeight
            )
        )
        // size: nY * nX
        val matrixFinal = matrixY.multiply(matrixX.transpose())

        for (row in 0 until nY) {
            for (col in 0 until nX) {
                statX.add(stepsX[col])
                statY.add(stepsY[row])
                statDensity.add(matrixFinal.getEntry(row, col) / SeriesUtil.sum(groupWeight))
            }
        }

        if (isContour) {
            // ToDo: change zrange into the range of z over entire dataset
            val zRange = SeriesUtil.range(statDensity)
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
            return DataFrame.Builder()
                .putNumeric(Stats.X, statX)
                .putNumeric(Stats.Y, statY)
                .putNumeric(Stats.DENSITY, statDensity)
                .build()
        }
    }
}
