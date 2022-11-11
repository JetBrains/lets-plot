/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.common.data.SeriesUtil
import kotlin.math.*

object DensityStatUtil {

    private const val DEF_STEP_SIZE = 0.5

    private fun stdDev(data: List<Double>): Double {
        var sum = 0.0
        var counter = 0.0

        for (i in data) {
            sum += i
        }
        val mean = sum / data.size
        for (i in data) {
            counter += (i - mean).pow(2.0)
        }
        return sqrt(counter / data.size)
    }

    fun binnedStat(
        xs: List<Double?>,
        ys: List<Double?>,
        ws: List<Double?>,
        trim: Boolean,
        bandWidth: Double?,
        bandWidthMethod: DensityStat.BandWidthMethod,
        adjust: Double,
        kernel: DensityStat.Kernel,
        n: Int,
        fullScanMax: Int,
        drawQuantiles: List<Double> = emptyList()
    ): MutableMap<DataFrame.Variable, List<Double>> {
        val binnedData = (xs zip (ys zip ws))
            .filter { it.first?.isFinite() == true }
            .groupBy({ it.first!! }, { it.second })
            .mapValues { it.value.unzip() }

        val statX = ArrayList<Double>()
        val statY = ArrayList<Double>()
        val statDensity = ArrayList<Double>()
        val statCount = ArrayList<Double>()
        val statScaled = ArrayList<Double>()
        val statQuantile = ArrayList<Double>()

        for ((x, bin) in binnedData) {
            val (filteredY, filteredW) = SeriesUtil.filterFinite(bin.first, bin.second)
            val (binY, binW) = (filteredY zip filteredW)
                .sortedBy { it.first }
                .unzip()
            if (binY.isEmpty()) continue
            val ySummary = FiveNumberSummary(binY)
            val modifier = if (trim) 0.0 else 3.0
            val bw = bandWidth ?: bandWidth(bandWidthMethod, binY)
            val rangeY = DoubleSpan(
                ySummary.min - modifier * bw,
                ySummary.max + modifier * bw
            )
            val binStatY = createStepValues(rangeY, n)
            val densityFunction = densityFunction(
                binY, binW,
                bandWidth, bandWidthMethod, adjust, kernel, fullScanMax
            )
            val binStatCount = binStatY.map { densityFunction(it) }
            val widthsSum = binW.sum()
            val maxBinCount = binStatCount.maxOrNull()!!

            statX += MutableList(binStatY.size) { x }
            statY += binStatY
            statDensity += binStatCount.map { it / widthsSum }
            statCount += binStatCount
            statScaled += binStatCount.map { it / maxBinCount }
            statQuantile += calculateQuantiles(binStatY, binStatCount, drawQuantiles)
        }

        return mutableMapOf(
            Stats.X to statX,
            Stats.Y to statY,
            Stats.DENSITY to statDensity,
            Stats.COUNT to statCount,
            Stats.SCALED to statScaled,
            Stats.QUANTILE to statQuantile
        )
    }

    private fun calculateQuantiles(
        sample: List<Double>,
        density: List<Double>,
        drawQuantiles: List<Double>
    ): List<Double> {
        if (sample.isEmpty()) return emptyList()
        val maxSampleValue = sample.maxOrNull()!!
        val densityValuesSum = density.sum()
        val dens = density.runningReduce { cumSum, elem -> cumSum + elem }.map { it / densityValuesSum }
        val quantilesSample = drawQuantiles.sorted().map { pwLinInterp(dens, sample)(it) }
        val quantilesItr = quantilesSample.iterator()
        if (!quantilesItr.hasNext()) return List(sample.size) { maxSampleValue }
        var quantile = quantilesItr.next()
        return sample.map { sampleValue ->
            if (sampleValue <= quantile)
                quantile
            else {
                if (quantilesItr.hasNext()) {
                    quantile = quantilesItr.next()
                    quantile
                } else {
                    maxSampleValue
                }
            }
        }
    }

    private fun pwLinInterp(x: List<Double>, y: List<Double>): (Double) -> Double {
        // Returns (bounded) piecewise linear interpolation function
        return fun(t: Double): Double {
            val i = x.indexOfFirst { it >= t }
            if (i == 0) return y.first()
            if (i == -1) return y.last()
            val a = (y[i] - y[i - 1]) / (x[i] - x[i - 1])
            val b = y[i - 1] - a * x[i - 1]
            return a * t + b
        }
    }

    fun bandWidth(bw: DensityStat.BandWidthMethod, valuesX: List<Double?>): Double {
        val mySize = valuesX.size

        @Suppress("UNCHECKED_CAST")
        val valuesXFinite = valuesX.filter { SeriesUtil.isFinite(it) } as List<Double>
        val dataSummary = FiveNumberSummary(valuesXFinite)
        val myIQR = dataSummary.thirdQuartile - dataSummary.firstQuartile
        val myStdD = stdDev(valuesXFinite)

        when (bw) {
            DensityStat.BandWidthMethod.NRD0 -> {
                if (myIQR > 0) {
                    return 0.9 * min(myStdD, myIQR / 1.34) * mySize.toDouble().pow(-0.2)
                }
                if (myStdD > 0) {
                    return 0.9 * myStdD * mySize.toDouble().pow(-0.2)
                }
            }
            DensityStat.BandWidthMethod.NRD -> {
                if (myIQR > 0) {
                    return 1.06 * min(myStdD, myIQR / 1.34) * mySize.toDouble().pow(-0.2)
                }
                if (myStdD > 0) {
                    return 1.06 * myStdD * mySize.toDouble().pow(-0.2)
                }
            }
        }
        return 1.0
    }

    fun kernel(ker: DensityStat.Kernel): (Double) -> Double {
        return when (ker) {
            DensityStat.Kernel.GAUSSIAN -> { value -> 1 / sqrt(2 * PI) * exp(-0.5 * value.pow(2.0)) }
            DensityStat.Kernel.RECTANGULAR -> { value -> if (abs(value) <= 1) 0.5 else 0.0 }
            DensityStat.Kernel.TRIANGULAR -> { value -> if (abs(value) <= 1) 1 - abs(value) else 0.0 }
            DensityStat.Kernel.BIWEIGHT -> { value -> if (abs(value) <= 1) .9375 * (1 - value * value).pow(2.0) else 0.0 }
            DensityStat.Kernel.EPANECHNIKOV -> { value -> if (abs(value) <= 1) .75 * (1 - value * value) else 0.0 }
            DensityStat.Kernel.OPTCOSINE -> { value -> if (abs(value) <= 1) PI / 4 * cos(PI / 2 * value) else 0.0 }
            else //case COSINE
            -> { value -> if (abs(value) <= 1) (cos(PI * value) + 1) / 2 else 0.0 }
        }
    }

    internal fun densityFunction(
        values: List<Double>,
        weights: List<Double>,
        bw: Double?,
        bwMethod: DensityStat.BandWidthMethod,
        ad: Double,
        ker: DensityStat.Kernel,
        fullScanMax: Int
    ): (Double) -> Double {
        val bandWidth = bw ?: bandWidth(bwMethod, values)
        val kernelFun: (Double) -> Double = kernel(ker)

        return when (values.size <= fullScanMax) {
            true -> densityFunctionFullScan(values, weights, kernelFun, bandWidth, ad)
            false -> densityFunctionFast(values, weights, kernelFun, bandWidth, ad)
        }
    }

    internal fun densityFunctionFullScan(
        xs: List<Double>,
        weights: List<Double>,
        ker: (Double) -> Double,
        bw: Double,
        ad: Double
    ): (Double) -> Double {
        val h = bw * ad
        return { x ->
            var sum = 0.0
            for (i in xs.indices) {
                sum += ker((x - xs[i]) / h) * weights[i]
            }
            sum / h
        }
    }

    internal fun densityFunctionFast(
        xs: List<Double>,  // must be ordered!
        weights: List<Double>,
        ker: (Double) -> Double,
        bw: Double,
        ad: Double
    ): (Double) -> Double {
        val h = bw * ad
        val cutoff = h * 5

        return { x ->
            var sum = 0.0
            var from = xs.binarySearch(x - cutoff)
            if (from < 0) {
                from = -from - 1
            }
            var to = xs.binarySearch(x + cutoff)
            if (to < 0) {
                to = -to - 1
            }

            for (i in (from until to)) {
                sum += ker((x - xs[i]) / h) * weights[i]
            }
            sum / h
        }
    }

    fun createStepValues(range: DoubleSpan, n: Int): List<Double> {
        val x = ArrayList<Double>()
        var min = range.lowerEnd
        var max = range.upperEnd
        val step: Double

        if (max == min) {
            max += DEF_STEP_SIZE
            min -= DEF_STEP_SIZE
        }
        step = (max - min) / (n - 1)
        for (i in 0 until n) {
            x.add(min + step * i)
        }
        return x
    }

    fun toKernel(method: String): DensityStat.Kernel {
        return when (method) {
            "gaussian" -> DensityStat.Kernel.GAUSSIAN
            "rectangular", "uniform" -> DensityStat.Kernel.RECTANGULAR
            "triangular" -> DensityStat.Kernel.TRIANGULAR
            "biweight", "quartic" -> DensityStat.Kernel.BIWEIGHT
            "epanechikov", "parabolic" -> DensityStat.Kernel.EPANECHNIKOV
            "optcosine" -> DensityStat.Kernel.OPTCOSINE
            "cosine" -> DensityStat.Kernel.COSINE
            else -> throw IllegalArgumentException(
                "Unsupported kernel method: '$method'.\n" +
                        "Use one of: gaussian, rectangular, triangular, biweight, epanechikov, optcosine, cos."
            )
        }
    }

    fun toBandWidthMethod(bw: String): DensityStat.BandWidthMethod {
        return when (bw) {
            "nrd0" -> DensityStat.BandWidthMethod.NRD0
            "nrd" -> DensityStat.BandWidthMethod.NRD
            else -> throw IllegalArgumentException(
                "Unsupported bandwidth method: '$bw'.\n" +
                        "Use one of: nrd0, nrd."
            )
        }
    }

    fun createRawMatrix(
        values: List<Double?>,
        list: List<Double>,
        ker: (Double) -> Double,
        bw: Double,
        ad: Double,
        weight: List<Double?>
    ): Array<DoubleArray> {
        val a = bw * ad
        val n = values.size
        val x = list.size
        val result = Array(x) { DoubleArray(n) }

        for (row in 0 until x) {
            for (col in 0 until n) {
                result[row][col] = ker((list[row] - values[col]!!) / a) * sqrt(weight[col]!!) / a
            }
        }
        return result
    }
}
