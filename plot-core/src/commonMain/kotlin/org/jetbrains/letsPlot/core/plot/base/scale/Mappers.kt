/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale

import org.jetbrains.letsPlot.commons.intern.function.Function
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.DiscreteTransform
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.scale.breaks.QuantizeScale
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil

object Mappers {
    val IDENTITY = object : ScaleMapper<Double> {
        override fun invoke(v: Double?): Double? = v
    }

    val NUMERIC_UNDEFINED: ScaleMapper<Double> = undefined<Double>()

    fun <T> undefined(): ScaleMapper<T> = object : ScaleMapper<T> {
        override fun invoke(v: Double?): T? {
            throw IllegalStateException("Undefined mapper")
        }
    }

    fun <T> emptyDataMapper(label: String): ScaleMapper<T> {
        // mapper for empty data is a special case - should never be used
        return object : ScaleMapper<T> {
            override fun invoke(v: Double?): T? {
                throw throw IllegalStateException("Mapper for empty data series '$label' was invoked with arg " + v)
            }
        }
    }

    fun <T> constant(constant: T): ScaleMapper<T> = object : ScaleMapper<T> {
        override fun invoke(v: Double?): T? = constant
    }

    fun mul(domain: DoubleSpan, rangeSpan: Double): ScaleMapper<Double> {
        val factor = rangeSpan / domain.length
        return mul(factor)
    }

    fun mul(factor: Double): ScaleMapper<Double> {
        check(factor.isFinite()) { "Can't create mapper with ratio: $factor" }
        return object : ScaleMapper<Double> {
            override fun invoke(v: Double?): Double? {
                return if (v != null) factor * v
                else null
            }
        }
    }

    fun linear(domain: DoubleSpan, range: DoubleSpan, reverse: Boolean = false): ScaleMapper<Double> {
        return linear(
            domain,
            rangeLow = if (reverse) range.upperEnd else range.lowerEnd,
            rangeHigh = if (reverse) range.lowerEnd else range.upperEnd,
            null
        )
    }

    fun linear(domain: DoubleSpan, range: DoubleSpan, defaultValue: Double): ScaleMapper<Double> {
        return linear(
            domain,
            range.lowerEnd,
            range.upperEnd,
            defaultValue
        )
    }

    fun linear(
        domain: DoubleSpan,
        rangeLow: Double,
        rangeHigh: Double,
        defaultValue: Double?
    ): ScaleMapper<Double> {
        val slop = (rangeHigh - rangeLow) / (domain.upperEnd - domain.lowerEnd)
        if (!SeriesUtil.isFinite(slop)) {
            // no slop
            val v = (rangeHigh - rangeLow) / 2 + rangeLow
            return constant(v)
        }
        val intersect = rangeLow - domain.lowerEnd * slop
        return object : ScaleMapper<Double> {
            override fun invoke(v: Double?): Double? {
                return if (v != null && v.isFinite())
                    v * slop + intersect
                else
                    defaultValue
            }
        }
    }

    fun discreteToContinuous(
        transformedDomain: List<Double>,
        outputRange: DoubleSpan,
        naValue: Double
    ): ScaleMapper<Double> {
        val dataRange = SeriesUtil.range(transformedDomain) ?: return IDENTITY
        return linear(dataRange, outputRange, naValue)
    }

    fun <T> discrete(
        discreteTransform: DiscreteTransform,
        outputValues: List<T>,
        defaultOutputValue: T?
    ): ScaleMapper<T> {
        return object : ScaleMapper<T> {
            override fun invoke(v: Double?): T? {
                // The input is a transformed domain value.
                val domainValue = discreteTransform.applyInverse(v)
                val index: Int = domainValue?.let { discreteTransform.indexOf(it) } ?: return defaultOutputValue
                return outputValues[index % outputValues.size]
            }
        }
    }

    fun <T> quantized(
        domain: DoubleSpan?,
        outputValues: Collection<T>,
        defaultOutputValue: T
    ): ScaleMapper<T> {
        if (domain == null) {
            return constant(defaultOutputValue)
        }

        // todo: extract quantizer
        val quantizer = QuantizeScale<T>()
        quantizer.domain(domain.lowerEnd, domain.upperEnd)
        quantizer.range(outputValues)

        val f = QuantizedFun(quantizer, defaultOutputValue)
        return object : ScaleMapper<T> {
            override fun invoke(v: Double?): T? {
                return f.apply(v)
            }
        }
    }

    private class QuantizedFun<T> internal constructor(
        private val myQuantizer: QuantizeScale<T>,
        private val myDefaultOutputValue: T
    ) : Function<Double?, T> {
        override fun apply(value: Double?): T {
            return if (!SeriesUtil.isFinite(value)) myDefaultOutputValue else myQuantizer.quantize(value!!)
        }
    }
}