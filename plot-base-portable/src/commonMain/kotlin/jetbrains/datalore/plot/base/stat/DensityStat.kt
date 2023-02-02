/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.base.stat.DensityStat.BandWidthMethod.NRD0
import jetbrains.datalore.plot.common.data.SeriesUtil

/**
 * Computes kernel density estimate for 'n' values evenly distributed throughout the range of the input series.
 *
 * If size of the input series exceeds the 'fullScanMax' value, then the less accurate but more efficient computation replaces
 * highly inefficient 'full scan' computation.
 */
class DensityStat(
    private val trim: Boolean,
    private val bandWidth: Double?,
    private val bandWidthMethod: BandWidthMethod,  // Used is `bandWidth` is not set.
    private val adjust: Double,
    private val kernel: Kernel,
    private val n: Int,
    private val fullScanMax: Int,
    private val quantiles: List<Double>
) : BaseStat(DEF_MAPPING) {

    init {
        require(n <= MAX_N) { "The input n = $n > $MAX_N is too large!" }
    }

    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X, Aes.WEIGHT)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, Aes.X)) {
            return withEmptyStatValues()
        }

        val xs: List<Double>
        val weights: List<Double>
        if (data.has(TransformVar.WEIGHT)) {
            val filtered = SeriesUtil.filterFinite(
                data.getNumeric(TransformVar.X),
                data.getNumeric(TransformVar.WEIGHT)
            )
            val xsFiltered = filtered[0]
            val weightsFiltered = filtered[1]

            val (xsSorted, weightsSorted) = xsFiltered
                .zip(weightsFiltered).sortedBy { it.first }
                .unzip()
            xs = xsSorted
            weights = weightsSorted

        } else {
            xs = data.getNumeric(TransformVar.X)
                .filterNotNull().filter { it.isFinite() }
                .sorted()
            weights = List(xs.size) { 1.0 }
        }

        if (xs.isEmpty()) return withEmptyStatValues()

        val rangeX = if (trim) {
            val xSummary = FiveNumberSummary(xs)
            DoubleSpan(xSummary.min, xSummary.max)
        } else {
            statCtx.overallXRange() ?: DoubleSpan(-0.5, 0.5)
        }

        val statX = DensityStatUtil.createStepValues(rangeX, n)
        val statDensity = ArrayList<Double>()
        val statCount = ArrayList<Double>()
        val statScaled = ArrayList<Double>()
        val densityFunction = DensityStatUtil.densityFunction(
            xs, weights,
            bandWidth, bandWidthMethod, adjust, kernel, fullScanMax
        )

        val nTotal = weights.sum()
        for (x in statX) {
            val d = densityFunction(x)
            statCount.add(d)
            statDensity.add(d / nTotal)
        }

        val maxm = statCount.maxOrNull()!!
        for (d in statCount) {
            statScaled.add(d / maxm)
        }

        val statQuantile = DensityStatUtil.calculateQuantiles(statX, statCount, quantiles)

        val statData = DensityStatUtil.expandByGroupEnds(mutableMapOf(
            Stats.X to statX,
            Stats.DENSITY to statDensity,
            Stats.COUNT to statCount,
            Stats.SCALED to statScaled,
            Stats.QUANTILE to statQuantile
        ), Stats.X, Stats.QUANTILE)

        val builder = DataFrame.Builder()
        for ((variable, series) in statData) {
            builder.putNumeric(variable, series)
        }
        return builder.build()
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
        const val DEF_TRIM = false
        val DEF_KERNEL = Kernel.GAUSSIAN
        const val DEF_ADJUST = 1.0
        const val DEF_N = 512
        val DEF_BW = NRD0
        const val DEF_FULL_SCAN_MAX = 5000
        val DEF_QUANTILES = listOf(0.25, 0.5, 0.75)

        const val MAX_N = 1024

        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.DENSITY,
            Aes.QUANTILE to Stats.QUANTILE
        )
    }
}
