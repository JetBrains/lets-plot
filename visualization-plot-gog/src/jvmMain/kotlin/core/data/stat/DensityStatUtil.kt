package jetbrains.datalore.visualization.plot.gog.core.data.stat

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import java.util.*
import java.util.function.Function

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
            counter += Math.pow(i - mean, 2.0)
        }
        return Math.sqrt(counter / data.size)
    }

    fun bandWidth(bw: DensityStat.BandWidthMethod, valuesX: List<Double>): Double {
        val mySize = valuesX.size
        val dataSummary = FiveNumberSummary(valuesX)
        val myIQR = dataSummary.thirdQuartile - dataSummary.firstQuartile
        val myStdD = stdDev(valuesX)

        when (bw) {
            DensityStat.BandWidthMethod.NRD0 -> {
                if (myIQR > 0) {
                    return 0.9 * Math.min(myStdD, myIQR / 1.34) * Math.pow(mySize.toDouble(), -0.2)
                }
                if (myStdD > 0) {
                    return 0.9 * myStdD * Math.pow(mySize.toDouble(), -0.2)
                }
            }
            DensityStat.BandWidthMethod.NRD -> {
                if (myIQR > 0) {
                    return 1.06 * Math.min(myStdD, myIQR / 1.34) * Math.pow(mySize.toDouble(), -0.2)
                }
                if (myStdD > 0) {
                    return 1.06 * myStdD * Math.pow(mySize.toDouble(), -0.2)
                }
            }
        }
        return 1.0
    }

    fun kernel(ker: DensityStat.Kernel): Function<Double, Double> {
        val myKernel: Function<Double, Double>
        when (ker) {
            DensityStat.Kernel.GAUSSIAN -> myKernel = Function { value -> 1 / Math.sqrt(2 * Math.PI) * Math.exp(-0.5 * Math.pow(value, 2.0)) }
            DensityStat.Kernel.RECTANGULAR -> myKernel = Function { value -> if (Math.abs(value) <= 1) 0.5 else 0.0 }
            DensityStat.Kernel.TRIANGULAR -> myKernel = Function { value -> if (Math.abs(value) <= 1) 1 - Math.abs(value) else 0.0 }
            DensityStat.Kernel.BIWEIGHT -> myKernel = Function { value -> if (Math.abs(value) <= 1) .9375 * Math.pow(1 - value * value, 2.0) else 0.0 }
            DensityStat.Kernel.EPANECHNIKOV -> myKernel = Function { value -> if (Math.abs(value) <= 1) .75 * (1 - value * value) else 0.0 }
            DensityStat.Kernel.OPTCOSINE -> myKernel = Function { value -> if (Math.abs(value) <= 1) Math.PI / 4 * Math.cos(Math.PI / 2 * value) else 0.0 }
            else //case COSINE
            -> myKernel = Function { value -> if (Math.abs(value) <= 1) (Math.cos(Math.PI * value) + 1) / 2 else 0.0 }
        }
        return myKernel
    }

    internal fun densityFunction(
            valuesX: List<Double>, ker: Function<Double, Double>, bw: Double, ad: Double, weightX: List<Double>): Function<Double, Double> {
        val a = bw * ad
        return Function { d ->
            var sum = 0.0
            var value: Double
            for (i in valuesX.indices) {
                value = valuesX[i]
                sum += ker.apply((d - value) / a) * weightX[i]
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
        val ker: DensityStat.Kernel
        when (method) {
            "gaussian" -> ker = DensityStat.Kernel.GAUSSIAN
            "rectangular", "uniform" -> ker = DensityStat.Kernel.RECTANGULAR
            "triangular" -> ker = DensityStat.Kernel.TRIANGULAR
            "biweight", "quartic" -> ker = DensityStat.Kernel.BIWEIGHT
            "epanechikov", "parabolic" -> ker = DensityStat.Kernel.EPANECHNIKOV
            "optcosine" -> ker = DensityStat.Kernel.OPTCOSINE
            "cosine" -> ker = DensityStat.Kernel.COSINE
            else -> throw IllegalArgumentException("Unsupported kernel method: $method")
        }
        return ker
    }

    fun toBandWidthMethod(bw: String): DensityStat.BandWidthMethod {
        val bandWidth: DensityStat.BandWidthMethod
        when (bw) {
            "nrd0" -> bandWidth = DensityStat.BandWidthMethod.NRD0
            "nrd" -> bandWidth = DensityStat.BandWidthMethod.NRD
            else -> throw IllegalArgumentException("Unsupported bandwidth method: $bw")
        }
        return bandWidth
    }

    fun createRawMatrix(
            values: List<Double>, list: List<Double>, ker: Function<Double, Double>, bw: Double, ad: Double, weight: List<Double>): Array<DoubleArray> {
        val a = bw * ad
        val n = values.size
        val x = list.size
        val result = Array(x) { DoubleArray(n) }

        for (row in 0 until x) {
            for (col in 0 until n) {
                result[row][col] = ker.apply((list[row] - values[col]) / a) * Math.sqrt(weight[col]) / a
            }
        }
        return result
    }
}
