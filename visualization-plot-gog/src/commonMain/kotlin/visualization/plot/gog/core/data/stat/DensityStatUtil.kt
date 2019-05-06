package jetbrains.datalore.visualization.plot.gog.core.data.stat

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import kotlin.math.*

object DensityStatUtil {

    private val DEF_STEP_SIZE = 0.5

    fun stdDev(data: List<Double>): Double {
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

    fun bandWidth(bw: DensityStat.BandWidthMethod, valuesX: List<Double>): Double {
        val mySize = valuesX.size
        val dataSummary = FiveNumberSummary(valuesX)
        val myIQR = dataSummary.thirdQuartile - dataSummary.firstQuartile
        val myStdD = stdDev(valuesX)

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
            valuesX: List<Double>, ker: (Double) -> Double, bw: Double, ad: Double, weightX: List<Double>): (Double) -> Double {
        val a = bw * ad
        return { d ->
            var sum = 0.0
            var value: Double
            for (i in valuesX.indices) {
                value = valuesX[i]
                sum += ker((d - value) / a) * weightX[i]
            }
            sum / a
        }
    }

    fun createStepValues(range: ClosedRange<Double>, n: Int): List<Double> {
        val x = ArrayList<Double>()
        var min = range.lowerEndpoint()
        var max = range.upperEndpoint()
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
            else -> throw IllegalArgumentException("Unsupported kernel method: $method")
        }
    }

    fun toBandWidthMethod(bw: String): DensityStat.BandWidthMethod {
        return when (bw) {
            "nrd0" -> DensityStat.BandWidthMethod.NRD0
            "nrd" -> DensityStat.BandWidthMethod.NRD
            else -> throw IllegalArgumentException("Unsupported bandwidth method: $bw")
        }
    }

    fun createRawMatrix(
            values: List<Double>, list: List<Double>, ker: (Double) -> Double, bw: Double, ad: Double, weight: List<Double>): Array<DoubleArray> {
        val a = bw * ad
        val n = values.size
        val x = list.size
        val result = Array(x) { DoubleArray(n) }

        for (row in 0 until x) {
            for (col in 0 until n) {
                result[row][col] = ker((list[row] - values[col]) / a) * sqrt(weight[col]) / a
            }
        }
        return result
    }
}
