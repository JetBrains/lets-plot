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
import org.jetbrains.letsPlot.core.plot.base.stat.math3.BlockRealMatrix

abstract class AbstractDensity2dStat(
    private val bandWidthX: Double?,
    private val bandWidthY: Double?,
    private val bandWidthMethod: DensityStat.BandWidthMethod,  // Used is `bandWidth` is not set.
    protected val adjust: Double,
    private val kernel: DensityStat.Kernel,
    protected val nX: Int,
    protected val nY: Int,
    protected val isContour: Boolean,
    private val binCount: Int,
    private val binWidth: Double

) : BaseStat(DEF_MAPPING) {

    //    var adjust = DEF_ADJUST
//    var nx = DEF_N
//        set(n) {
//            if (n > MAX_N) {
//                throw IllegalArgumentException("The input Nx " + n + " > " + MAX_N + "is too large!")
//            }
//            field = n
//        }
//    var ny = DEF_N
//        set(n) {
//            if (n > MAX_N) {
//                throw IllegalArgumentException("The input Ny " + n + " > " + MAX_N + "is too large!")
//            }
//            field = n
//        }
//    var isContour = DEF_CONTOUR
//    var bandWidthMethod: DensityStat.BandWidthMethod =
//        DensityStat.BandWidthMethod.NRD
//        set(bw) {
//            field = bw
//            bandWidths = null
//        }
//    private var myBinCount = DEF_BIN_COUNT
//    private var myBinWidth: Double = 0.toDouble()

//    protected val bandWidths: DoubleArray

    protected val kernelFun: ((Double) -> Double) = DensityStatUtil.kernel(kernel)

    protected val binOptions: BinStatUtil.BinOptions = BinStatUtil.BinOptions(binCount, binWidth)

    init {
        require(nX <= MAX_N) { "The input nX = $nX  > $MAX_N is too large!" }
        require(nY <= MAX_N) { "The input nY = $nY  > $MAX_N is too large!" }
    }

    fun getBandWidthX(xs: List<Double?>): Double {
        return bandWidthX ?: DensityStatUtil.bandWidth(
            bandWidthMethod,
            xs
        )
    }

    fun getBandWidthY(ys: List<Double?>): Double {
        return bandWidthY ?: DensityStatUtil.bandWidth(
            bandWidthMethod,
            ys
        )
    }

//    fun setBinCount(bin: Int) {
//        myBinCount = bin
//    }

//    fun setBinWidth(bin: Double) {
//        myBinWidth = bin
//    }

//    fun setBandWidthX(bw: Double) {
//        //myBW = BandWidth.DOUBLE;
//        bandWidths = DoubleArray(2)
//        bandWidths?.set(0, bw)
//    }

//    fun setBandWidthY(bw: Double) {
//        //myBW = BandWidth.DOUBLE;
//        bandWidths?.set(1, bw)
//    }

//    fun setKernel(kernel: DensityStat.Kernel) {
//        this.kernel = DensityStatUtil.kernel(kernel)
//    }

    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X, Aes.Y, Aes.WEIGHT)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        // ToDo: ???
        throw IllegalStateException("'density2d' statistic can't be executed on the client side")
    }

    protected fun density2dGrid(
        xVector: List<Double?>,
        yVector: List<Double?>,
        groupWeight: List<Double?>,
        xRange: DoubleSpan,
        yRange: DoubleSpan
    ): Map<DataFrame.Variable, List<Double>> {
        val statX = ArrayList<Double>()
        val statY = ArrayList<Double>()
        val statCount = ArrayList<Double>()
        val statDensity = ArrayList<Double>()
        val statScaled = ArrayList<Double>()

        val bandWidth = DoubleArray(2)
        bandWidth[0] = getBandWidthX(xVector)
        bandWidth[1] = getBandWidthY(yVector)

        val stepsX = DensityStatUtil.createStepValues(xRange, nX)
        val stepsY = DensityStatUtil.createStepValues(yRange, nY)

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
        val matrixFinal = matrixY.multiply(matrixX.transpose())

        for (row in 0 until nY) {
            for (col in 0 until nX) {
                statX.add(stepsX[col])
                statY.add(stepsY[row])
                val count = matrixFinal.getEntry(row, col)
                statCount.add(count)
                statDensity.add(count / SeriesUtil.sum(groupWeight))
            }
        }

        statCount.maxOrNull()?.let { maxCount ->
            for (d in statCount) {
                statScaled.add(d / maxCount)
            }
        }

        return mapOf(
            Stats.X to statX,
            Stats.Y to statY,
            Stats.COUNT to statCount,
            Stats.DENSITY to statDensity,
            Stats.SCALED to statScaled
        )
    }

    companion object {
        //        const val DEF_KERNEL = "gaussian"
        val DEF_KERNEL = DensityStat.Kernel.GAUSSIAN
        const val DEF_ADJUST = 1.0
        const val DEF_N = 100

        //        const val DEF_BW = "nrd"
        val DEF_BW = DensityStat.BandWidthMethod.NRD0
        const val DEF_CONTOUR = true
        const val DEF_BIN_COUNT = 10
        const val DEF_BIN_WIDTH = 0.0

        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y
        )
        private const val MAX_N = 999
    }
}
