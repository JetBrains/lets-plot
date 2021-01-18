/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.stringFormat.StringFormat
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.Transform
import jetbrains.datalore.plot.base.scale.transform.TransformKind
import jetbrains.datalore.plot.base.scale.transform.Transforms.createTransform

internal class ContinuousScale<T> : AbstractScale<Double, T> {
    override val isContinuous: Boolean
    override val isContinuousDomain: Boolean = true
    override val domainLimits: ClosedRange<Double>?

    val formatter: (Double) -> String

    override val defaultTransform: Transform
        get() = createTransform(TransformKind.IDENTITY).also {
            if (it is BreaksGenerator) {
                it.setLabelFormat(labelFormat)
            }
        }

    constructor(
        name: String,
        mapper: ((Double?) -> T?),
        continuousOutput: Boolean
    ) : super(name, mapper) {
        isContinuous = continuousOutput
        domainLimits = null
        formatter = if (labelFormat != null) {
            StringFormat(labelFormat!!)::format
        } else {
            { v -> v.toString() }
        }

        // see: https://ggplot2.tidyverse.org/reference/scale_continuous.html
        // defaults for continuous scale.
        multiplicativeExpand = 0.05
        additiveExpand = 0.0
    }

    private constructor(b: MyBuilder<T>) : super(b) {
        isContinuous = b.myContinuousOutput
        val lower = b.myLowerLimit
        val upper = b.myUpperLimit
        domainLimits = if (lower != null || upper != null) {
            ClosedRange(
                lower ?: Double.NEGATIVE_INFINITY,
                upper ?: Double.POSITIVE_INFINITY
            )
        } else null

        formatter = if (b.myLabelFormat != null) {
            StringFormat(b.myLabelFormat!!)::format
        } else {
            b.myFormatter
        }
    }

    override fun isInDomainLimits(v: Any): Boolean {
        return (v as? Number)?.run {
            // undefined domain limits - contains all
            return domainLimits?.contains(v.toDouble()) ?: true
        } ?: false
    }

    override fun hasDomainLimits(): Boolean {
        return domainLimits != null
    }

    override fun asNumber(input: Any?): Double? {
        if (input == null || input is Double) {
            return input as Double?
        }
        throw IllegalArgumentException("Double is expected but was " + input::class.simpleName + " : " + input.toString())
    }

    override fun with(): Scale.Builder<T> {
        return MyBuilder(this)
    }


    private class MyBuilder<T>(scale: ContinuousScale<T>) : AbstractBuilder<Double, T>(scale) {
        internal val myContinuousOutput: Boolean = scale.isContinuous
        internal var myFormatter: (Double) -> String = scale.formatter
        internal var myLowerLimit: Double? = scale.domainLimits?.lowerEnd
        internal var myUpperLimit: Double? = scale.domainLimits?.upperEnd

        override fun lowerLimit(v: Double): Scale.Builder<T> {
            require(!v.isNaN()) { "`lower` can't be $v" }
            myLowerLimit = v
            return this
        }

        override fun upperLimit(v: Double): Scale.Builder<T> {
            require(!v.isNaN()) { "`upper` can't be $v" }
            myUpperLimit = v
            return this
        }

        override fun limits(domainValues: List<Any>): Scale.Builder<T> {
            throw IllegalArgumentException("Can't apply discrete limits to scale with continuous domain")
        }

        override fun continuousTransform(v: Transform): Scale.Builder<T> {
            return transform(v)
        }

        override fun formatter(v: (Double) -> String): Scale.Builder<T> {
            myFormatter = v
            return this
        }

        override fun build(): Scale<T> {
            return ContinuousScale(this)
        }
    }
}
