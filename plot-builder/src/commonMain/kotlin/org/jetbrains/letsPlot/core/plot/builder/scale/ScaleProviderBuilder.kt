/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat
import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone
import org.jetbrains.letsPlot.core.commons.data.DataType
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.scale.BreaksGenerator
import org.jetbrains.letsPlot.core.plot.base.scale.Scales
import org.jetbrains.letsPlot.core.plot.base.scale.transform.Transforms
import org.jetbrains.letsPlot.core.plot.base.theme.ExponentFormat
import org.jetbrains.letsPlot.core.plot.base.theme.ExponentFormat.Companion.DEF_EXPONENT_FORMAT
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotAssembler

class ScaleProviderBuilder<T> constructor(private val aes: Aes<T>) {

    private var _mapperProvider: MapperProvider<T>? = null
    private var myName: String? = null
    private var myBreaks: List<Any>? = null
    private var myLabels: List<String>? = null
    private var myLabelLengthLimit: Int? = null
    private var myLabelFormat: String? = null
    private var myDataType: DataType = DataType.UNKNOWN
    private var tz: TimeZone? = null
    private var myExpFormat: ExponentFormat = DEF_EXPONENT_FORMAT
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

    fun labelLengthLimit(v: Int): ScaleProviderBuilder<T> {
        myLabelLengthLimit = v
        return this
    }

    fun labelFormat(format: String?): ScaleProviderBuilder<T> {
        myLabelFormat = format
        return this
    }

    fun dataType(dataType: DataType): ScaleProviderBuilder<T> {
        myDataType = dataType
        return this
    }

    fun timeZone(v: TimeZone?): ScaleProviderBuilder<T> {
        tz = v
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

    fun exponentFormat(v: ExponentFormat): ScaleProviderBuilder<T> {
        myExpFormat = v
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

    fun build(): ScaleProvider {
        return MyScaleProvider(this)
    }

    private class MyScaleProvider<T>(b: ScaleProviderBuilder<T>) : ScaleProvider {

        private val myName: String? = b.myName

        private val myLabels: List<String>? = b.myLabels?.let { ArrayList(it) }
        private val myLabelLengthLimit: Int? = b.myLabelLengthLimit
        private val myLabelFormat: String? = b.myLabelFormat
        private val myDataType: DataType = b.myDataType
        private val tz: TimeZone? = b.tz
        private val myMultiplicativeExpand: Double? = b.myMultiplicativeExpand
        private val myAdditiveExpand: Double? = b.myAdditiveExpand
        private val myBreaksGenerator: BreaksGenerator? = b.myBreaksGenerator
        private val myExpFormat: ExponentFormat = b.myExpFormat
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

        private fun scaleName(defaultName: String, guideTitle: String?): String {
            return guideTitle ?: myName ?: defaultName
        }

        /**
         * Discrete domain.
         */
        override fun createScale(
            defaultName: String,
            discreteTransform: DiscreteTransform,
            guideTitle: String?
        ): Scale {
            val scale: Scale = Scales.discreteDomain(
                scaleName(defaultName, guideTitle),
                discreteTransform,
            )

            return completeScale(scale)
        }

        override fun createScale(
            defaultName: String,
            continuousTransform: ContinuousTransform,
            continuousRange: Boolean,
            guideBreaks: WithGuideBreaks<Any>?,
            guideTitle: String?
        ): Scale {
            val name = scaleName(defaultName, guideTitle)
            var scale: Scale

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

        private fun completeScale(scale: Scale): Scale {
            val with = scale.with()
                .exponentFormat(PlotAssembler.extractExponentFormat(myExpFormat))

            with.dataType(myDataType)
            if (tz != null) {
                with.timeZone(tz)
            }
            if (breaks != null) {
                with.breaks(breaks)
            }
            if (myLabels != null) {
                with.labels(myLabels)
            }
            if (myLabelLengthLimit != null) {
                with.labelLengthLimit(myLabelLengthLimit)
            }
            if (myLabelFormat != null) {
                with.labelFormatter(
                    StringFormat.forOneArg(
                        myLabelFormat,
                        expFormat = PlotAssembler.extractExponentFormat(myExpFormat),
                        tz = tz
                    )::format
                )
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
