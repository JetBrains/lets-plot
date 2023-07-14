/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.StatContext

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
