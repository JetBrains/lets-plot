/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.base.stat.DensityStat.BandWidthMethod.NRD0
import jetbrains.datalore.plot.common.data.SeriesUtil

/**
 * Calculates the density function.
 * (or if the weight aesthetic is supplied, the sum of the weights, **not yet implemented**)
 */
class DensityStat : BaseStat(DEF_MAPPING) {
    private var myAdjust = DEF_ADJUST
    private var myN = DEF_N
    private var myBandWidthMethod = NRD0
    private var myBandWidth: Double? = null
    private var myKernel: (Double) -> Double = DensityStatUtil.kernel(Kernel.GAUSSIAN)

    fun setKernel(kernel: Kernel) {
        myKernel = DensityStatUtil.kernel(kernel)
    }

    fun setAdjust(adjust: Double) {
        myAdjust = adjust
    }

    fun setN(n: Int) {
        if (n > MAX_N) {
            throw IllegalArgumentException("The input n " + n + " > " + MAX_N + "is too large!")
        }
        myN = n
    }

    fun setBandWidthMethod(bw: BandWidthMethod) {
        myBandWidthMethod = bw
        myBandWidth = null
    }

    fun setBandWidth(bw: Double) {
        myBandWidth = bw
    }

    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X, Aes.WEIGHT)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, Aes.X)) {
            return withEmptyStatValues()
        }

        val valuesX = data.getNumeric(TransformVar.X)
        val statX = DensityStatUtil.createStepValues(statCtx.overallXRange()!!, myN)
        val statDensity = ArrayList<Double>()
        val statCount = ArrayList<Double>()
        val statScaled = ArrayList<Double>()

        // weight aesthetics
        val weight = BinStatUtil.weightVector(valuesX.size, data)

        val bandWidth: Double
        val densityFunction: (Double) -> Double
        //bandWidth = myBandWidthMethod.equals(BandWidthMethod.DOUBLE) ? myBandWidth : DensityStatUtil.bandWidth(myBandWidthMethod, groupX);
        bandWidth = myBandWidth ?: DensityStatUtil.bandWidth(
            myBandWidthMethod,
            valuesX
        )
        densityFunction = DensityStatUtil.densityFunction(
            valuesX,
            myKernel,
            bandWidth,
            myAdjust,
            weight
        )

        for (x in statX) {
            val d = densityFunction(x)
            statCount.add(d)
            statDensity.add(d / SeriesUtil.sum(weight))
        }
        val maxm = statCount.max()!!
        for (d in statCount) {
            statScaled.add(d / maxm)
        }

        return DataFrame.Builder()
            .putNumeric(Stats.X, statX)
            .putNumeric(Stats.DENSITY, statDensity)
            .putNumeric(Stats.COUNT, statCount)
            .putNumeric(Stats.SCALED, statScaled)
            //.putNumericVar(Stats.GROUP, newGroups)
            .build()
    }

    enum class Kernel {
        GAUSSIAN,
        RECTANGULAR,
        TRIANGULAR,
        BIWEIGHT,
        EPANECHNIKOV,
        OPTCOSINE,
        COSINE
    }

    enum class BandWidthMethod {
        NRD0,
        NRD
    }

    companion object {

        const val DEF_KERNEL = "gaussian"
        const val DEF_ADJUST = 1.0
        const val DEF_N = 512
        const val DEF_BW = "nrd0"
        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.DENSITY
        )
        private const val MAX_N = 9999
    }
}
