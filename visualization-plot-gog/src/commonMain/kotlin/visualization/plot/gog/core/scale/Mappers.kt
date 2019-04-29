package jetbrains.datalore.visualization.plot.gog.core.scale

import jetbrains.datalore.base.function.Function
import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.visualization.plot.gog.common.data.SeriesUtil
import kotlin.math.round

object Mappers {
    val IDENTITY = { v: Double -> v }

    fun <T> nullable(f: (Double) -> T, ifNull: T): (Double?) -> T {
        return { n ->
            if (n == null) {
                ifNull
            } else {
                f(n)
            }
        }
    }

    fun constant(v: Double): (Double) -> Double = { v }

    fun mul(domain: ClosedRange<Double>, rangeSpan: Double): (Double) -> Double {
        val factor = rangeSpan / (domain.upperEndpoint() - domain.lowerEndpoint())
        checkState(!(factor.isInfinite() || factor.isNaN()), "Can't create mapper with ratio: $factor")
        return { MulFunction(factor).apply(it) }
    }

    fun mul(factor: Double): Function<Double, Double> {
        return MulFunction(factor)
    }

    fun linear(domain: ClosedRange<Double>, range: ClosedRange<Double>): (Double) -> Double {
        return linear(domain, range.lowerEndpoint(), range.upperEndpoint(), Double.NaN)
    }

    fun linear(domain: ClosedRange<Double>, range: ClosedRange<Double>, defaultValue: Double): (Double) -> Double {
        return linear(domain, range.lowerEndpoint(), range.upperEndpoint(), defaultValue)
    }

    fun linear(domain: ClosedRange<Double>, rangeLow: Double, rangeHigh: Double, defaultValue: Double): (Double) -> Double {
        val slop = (rangeHigh - rangeLow) / (domain.upperEndpoint() - domain.lowerEndpoint())
        if (!SeriesUtil.isFinite(slop)) {
            // no slop
            val v = (rangeHigh - rangeLow) / 2 + rangeLow
            return constant(v)
        }
        val intersect = rangeLow - domain.lowerEndpoint() * slop
        return { input ->
            if (SeriesUtil.isFinite(input))
                input!! * slop + intersect
            else
                defaultValue
        }
    }

    fun discreteToContinuous(domainValues: Collection<*>, range: ClosedRange<Double>, naValue: Double?): (Double) -> Double {
        val numberByDomainValue = MapperUtil.mapDiscreteDomainValuesToNumbers(domainValues)
        val dataRange = SeriesUtil.range(numberByDomainValue.values) ?: return IDENTITY
        return linear(dataRange, range, naValue!!)
    }

    fun <T> discrete(outputValues: List<T>, defaultOutputValue: T): (Double?) -> T {
        return { DiscreteFun(outputValues, defaultOutputValue).apply(it) }
    }

    // todo: extract quantizer
    fun <T> quantized(domain: ClosedRange<Double>?, outputValues: Collection<T>, defaultOutputValue: T): Nothing = throw IllegalStateException("Not implemented")
//    fun <T> quantized(domain: ClosedRange<Double>?, outputValues: Collection<T>, defaultOutputValue: T): (Double) -> T {
//        if (domain == null) {
//            return { defaultOutputValue }
//        }
//
//        // todo: extract quantizer
//        val quantizer = QuantizeScale<T>()
//        quantizer.domain(domain.lowerEndpoint(), domain.upperEndpoint())
//        quantizer.range(outputValues)
//
//        return QuantizedFun(quantizer, defaultOutputValue)
//    }

    private class DiscreteFun<T> internal constructor(private val myOutputValues: List<T>, private val myDefaultOutputValue: T) : Function<Double?, T> {
        override fun apply(value: Double?): T {
            if (!SeriesUtil.isFinite(value)) {
                return myDefaultOutputValue
            }
            // ToDo: index-based discrete fun won't work for discrete numeric onput (see: MapperUtil#mapDiscreteDomainValuesToNumbers())
            var index = round(value!!).toInt()
            index %= myOutputValues.size
            if (index < 0) {
                index += myOutputValues.size
            }
            return myOutputValues[index]
        }
    }

    // todo: extract quantizer
//    private class QuantizedFun<T> internal constructor(private val myQuantizer: QuantizeScale<T>, private val myDefaultOutputValue: T) : (Double) -> T {
//        override fun invoke(input: Double): T {
//            return if (!SeriesUtil.isFinite(input)) myDefaultOutputValue else myQuantizer.quantize(input)
//        }
//    }
}
