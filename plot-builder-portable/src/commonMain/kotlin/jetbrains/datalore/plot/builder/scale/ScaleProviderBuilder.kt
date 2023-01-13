/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale

import jetbrains.datalore.base.stringFormat.StringFormat
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.scale.BreaksGenerator
import jetbrains.datalore.plot.base.scale.Scales
import jetbrains.datalore.plot.base.scale.transform.Transforms

class ScaleProviderBuilder<T> constructor(private val aes: Aes<T>) {

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

    var axisPosition: AxisPosition = when (aes) {
        Aes.X -> AxisPosition.BOTTOM  //
        Aes.Y -> AxisPosition.LEFT
        else -> AxisPosition.BOTTOM  // Doesn't matter - not used for aes other than x,y.
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

    private class MyScaleProvider<T>(b: ScaleProviderBuilder<T>) : ScaleProvider<T> {

        private val myName: String? = b.myName

        private val myLabels: List<String>? = b.myLabels?.let { ArrayList(it) }
        private val myLabelFormat: String? = b.myLabelFormat
        private val myMultiplicativeExpand: Double? = b.myMultiplicativeExpand
        private val myAdditiveExpand: Double? = b.myAdditiveExpand
        private val myBreaksGenerator: BreaksGenerator? = b.myBreaksGenerator
        private val myAes: Aes<T> = b.aes

        override val discreteDomain: Boolean = b.myDiscreteDomain
        override val discreteDomainReverse: Boolean = b.myDiscreteDomainReverse

        override val breaks: List<Any>? = b.myBreaks?.let { ArrayList(it) }
        override val limits: List<Any?>? = b.myLimits?.let { ArrayList(it) }

        override val continuousTransform: ContinuousTransform = b.myContinuousTransform
        override val axisPosition: AxisPosition = when (b.aes) {
            Aes.X -> {
                require(b.axisPosition.isHorizontal) { "Illegal X-axis position: ${b.axisPosition}" }
                b.axisPosition
            }

            Aes.Y -> {
                require(b.axisPosition.isVertical) { "Illegal Y-axis position: ${b.axisPosition}" }
                b.axisPosition
            }

            else -> b.axisPosition  // Doesn't matter for aes other than x,y.
        }


        private fun scaleName(variable: DataFrame.Variable): String {
            return myName ?: variable.label
        }

        /**
         * Discrete domain.
         */
        override fun createScale(defaultName: String, discreteTransform: DiscreteTransform): Scale<T> {
            var scale: Scale<T> = Scales.discreteDomain(
                myName ?: defaultName,
                discreteTransform,
            )

            return completeScale(scale)
        }

        override fun createScale(
            defaultName: String,
            continuousTransform: ContinuousTransform,
            continuousRange: Boolean,
            guideBreaks: WithGuideBreaks<Any>?
        ): Scale<T> {
            val name = myName ?: defaultName
            var scale: Scale<T>

            // continuous (numeric) domain
            scale = Scales.continuousDomain(
                name,
                continuousRange = continuousRange || myAes.isNumeric
            )

            guideBreaks?.let {
                scale = scale.with()
                    .breaks(it.breaks)
                    .labelFormatter(it.formatter)
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

            return completeScale(scale)
        }

        private fun completeScale(scale: Scale<T>): Scale<T> {
            val with = scale.with()
            if (breaks != null) {
                with.breaks(breaks)
            }
            if (myLabels != null) {
                with.labels(myLabels)
            }
            if (myLabelFormat != null) {
                with.labelFormatter(StringFormat.forOneArg(myLabelFormat)::format)
            }
            if (myMultiplicativeExpand != null) {
                with.multiplicativeExpand(myMultiplicativeExpand)
            }
            if (myAdditiveExpand != null) {
                with.additiveExpand(myAdditiveExpand)
            }
            return with.build()
        }

        private fun absentMapper(`var`: DataFrame.Variable): ScaleMapper<T> {
            // mapper for empty data is a special case - should never be used
            return object : ScaleMapper<T> {
                override fun invoke(v: Double?): T? {
                    throw IllegalStateException("Mapper for empty data series '" + `var`.name + "' was invoked with arg " + v)
                }
            }
        }
    }
}
