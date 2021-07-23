/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.stringFormat.StringFormat
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.ContinuousTransform
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.scale.BreaksGenerator
import jetbrains.datalore.plot.base.scale.Scales
import jetbrains.datalore.plot.base.scale.transform.Transforms
import jetbrains.datalore.plot.common.data.SeriesUtil.ensureApplicableRange

class ScaleProviderBuilder<T>(private val aes: Aes<T>) {

    private var _mapperProvider: MapperProvider<T>? = null
    private var myName: String? = null
    private var myBreaks: List<Any>? = null
    private var myLabels: List<String>? = null
    private var myLabelFormat: String? = null
    private var myMultiplicativeExpand: Double? = null
    private var myAdditiveExpand: Double? = null
    private var myLimits: List<Any?>? = null
    private var myContinuousTransform: ContinuousTransform = Transforms.IDENTITY
    private var myBreaksGenerator: BreaksGenerator? = null

    private var myDiscreteDomain = false
    private var myDiscreteDomainReverse = false

    var mapperProvider: MapperProvider<T>
        get() {
            if (_mapperProvider == null) {
                _mapperProvider = DefaultMapperProvider[aes]
            }
            return _mapperProvider ?: throw AssertionError("Set to null by another thread")
        }
        set(p: MapperProvider<T>) {
            _mapperProvider = p
        }

    fun mapperProvider(mapperProvider: MapperProvider<T>): ScaleProviderBuilder<T> {
        this.mapperProvider = mapperProvider
        return this
    }

    fun name(name: String): ScaleProviderBuilder<T> {
        myName = name
        return this
    }

    fun breaks(breaks: List<Any>): ScaleProviderBuilder<T> {
        myBreaks = breaks
        return this
    }

    @Suppress("FunctionName")
    fun minorBreaks_NI(
        @Suppress("UNUSED_PARAMETER") minorBreaks: List<Double>
    ): ScaleProviderBuilder<T> {
        // continuous scale
        throw IllegalStateException("Not implemented")
    }

    fun labels(labels: List<String>): ScaleProviderBuilder<T> {
        myLabels = ArrayList(labels)
        return this
    }

    fun labelFormat(format: String?): ScaleProviderBuilder<T> {
        myLabelFormat = format
        return this
    }

    fun multiplicativeExpand(v: Double): ScaleProviderBuilder<T> {
        myMultiplicativeExpand = v
        return this
    }

    fun additiveExpand(v: Double): ScaleProviderBuilder<T> {
        myAdditiveExpand = v
        return this
    }

    fun limits(v: List<*>): ScaleProviderBuilder<T> {
        // Limits for continuous scale : list(min, max)
        // Limits for discrete scale : list ("a", "b", "c")
        myLimits = v
        return this
    }

    @Suppress("FunctionName")
    fun rescaler_NI(
        @Suppress("UNUSED_PARAMETER") v: Any
    ): ScaleProviderBuilder<T> {
        throw IllegalStateException("Not implemented")
    }

    @Suppress("FunctionName")
    fun oob_NI(
        @Suppress("UNUSED_PARAMETER") v: Any
    ): ScaleProviderBuilder<T> {
        throw IllegalStateException("Not implemented")
    }

    fun continuousTransform(v: ContinuousTransform): ScaleProviderBuilder<T> {
        myContinuousTransform = v
        return this
    }

    fun breaksGenerator(v: BreaksGenerator): ScaleProviderBuilder<T> {
        myBreaksGenerator = v
        return this
    }

    @Suppress("FunctionName")
    fun guide_NI(
        @Suppress("UNUSED_PARAMETER") v: Any
    ): ScaleProviderBuilder<T> {
        // Name of guide object, or object itself.
        throw IllegalStateException("Not implemented")
    }

    fun discreteDomain(b: Boolean): ScaleProviderBuilder<T> {
        myDiscreteDomain = b
        return this
    }

    fun discreteDomainReverse(b: Boolean): ScaleProviderBuilder<T> {
        myDiscreteDomainReverse = b
        return this
    }

    fun build(): ScaleProvider<T> {
        return MyScaleProvider(this)
    }

    private class MyScaleProvider<T>(b: ScaleProviderBuilder<T>) :
        ScaleProvider<T> {

        private val myName: String? = b.myName

        private val myBreaks: List<Any>? = b.myBreaks?.let { ArrayList(it) }
        private val myLabels: List<String>? = b.myLabels?.let { ArrayList(it) }
        private val myLabelFormat: String? = b.myLabelFormat
        private val myMultiplicativeExpand: Double? = b.myMultiplicativeExpand
        private val myAdditiveExpand: Double? = b.myAdditiveExpand
        private val myLimits: List<Any?>? = b.myLimits?.let { ArrayList(it) }
        private val discreteDomainReverse: Boolean = b.myDiscreteDomainReverse

        private val myBreaksGenerator: BreaksGenerator? = b.myBreaksGenerator

        private val myAes: Aes<T> = b.aes
        private val mapperProvider: MapperProvider<T> = b.mapperProvider

        override val discreteDomain: Boolean = b.myDiscreteDomain

        // This is only to use by 'discrete' transforms/scales
        override val discreteDomainLimits: List<Any>? = myLimits?.filterNotNull()

        override val continuousTransform: ContinuousTransform = b.myContinuousTransform

        private fun scaleName(variable: DataFrame.Variable): String {
            return myName ?: variable.label
        }

        override fun createScale(defaultName: String, discreteDomain: Collection<*>): Scale<T> {
            val name = myName ?: defaultName
            var scale: Scale<T>

            // discrete domain
            var domainValues = discreteDomain.filterNotNull()

            val mapper = if (discreteDomain.isEmpty()) {
                absentMapper(defaultName)
            } else {
                mapperProvider.createDiscreteMapper(domainValues)::apply
            }

            if (discreteDomainReverse) {
                domainValues = domainValues.reversed()
            }

            scale = Scales.discreteDomain(
                name,
                domainValues,
                mapper
            )

            val discreteLimits = discreteDomainLimits?.let {
                if (discreteDomainReverse) {
                    it.reversed()
                } else {
                    it
                }
            }
            if (discreteLimits != null) {
                scale = scale.with()
                    .limits(discreteLimits)
                    .build()
            }

            return completeScale(scale)
        }

        override fun createScale(defaultName: String, continuousDomain: ClosedRange<Double>): Scale<T> {
            val name = myName ?: defaultName
            var scale: Scale<T>

            // continuous (numeric) domain
            val dataRange = ensureApplicableRange(continuousDomain)

            var lowerLimit: Double? = null
            var upperLimit: Double? = null
            if (myLimits != null) {
                var lower = true
                for (limit in myLimits) {
                    if (limit is Number) {
                        val v = limit.toDouble()
                        if (v.isFinite()) {
                            if (lower) {
                                lowerLimit = v
                            } else {
                                upperLimit = v
                            }
                        }
                    }
                    lower = false
                }
            }

            val mapper = mapperProvider.createContinuousMapper(
                dataRange,
                lowerLimit,
                upperLimit,
                continuousTransform
            )
            val continuousRange = mapper.isContinuous || myAes.isNumeric

            scale = Scales.continuousDomain(name, { v -> mapper.apply(v) }, continuousRange)

            // ToDo: need to 'inverse transform' breaks.
            if (mapper is WithGuideBreaks<*>) {
                @Suppress("UNCHECKED_CAST")
                mapper as WithGuideBreaks<Any>
                scale = scale.with()
                    .breaks(mapper.breaks)
                    .labelFormatter(mapper.formatter)
                    .build()
            }

            scale = scale.with()
                .continuousTransform(continuousTransform)
                .build()

            if (myBreaksGenerator != null) {
                scale = scale.with()
                    .breaksGenerator(myBreaksGenerator)
                    .build()
            }

            if (myLimits != null) {
                val with = scale.with()
                if (lowerLimit != null) {
                    with.lowerLimit(lowerLimit)
                }
                if (upperLimit != null) {
                    with.upperLimit(upperLimit)
                }
                scale = with.build()
            }

            return completeScale(scale)
        }


        private fun completeScale(scale: Scale<T>): Scale<T> {
            val with = scale.with()
            if (myBreaks != null) {
                with.breaks(myBreaks)
            }
            if (myLabels != null) {
                with.labels(myLabels)
            }
            if (myLabelFormat != null) {
                with.labelFormatter(StringFormat.create(myLabelFormat)::format)
            }
            if (myMultiplicativeExpand != null) {
                with.multiplicativeExpand(myMultiplicativeExpand)
            }
            if (myAdditiveExpand != null) {
                with.additiveExpand(myAdditiveExpand)
            }
            return with.build()
        }

        private fun absentMapper(`var`: DataFrame.Variable): (Double?) -> T {
            // mapper for empty data is a special case - should never be used
            return { v -> throw IllegalStateException("Mapper for empty data series '" + `var`.name + "' was invoked with arg " + v) }
        }

        private fun absentMapper(label: String): (Double?) -> T {
            // mapper for empty data is a special case - should never be used
            return { v -> throw IllegalStateException("Mapper for empty data series '$label' was invoked with arg " + v) }
        }
    }
}
