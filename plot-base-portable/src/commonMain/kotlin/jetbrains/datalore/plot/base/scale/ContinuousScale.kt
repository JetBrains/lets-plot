/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale

import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.Transform
import jetbrains.datalore.plot.base.scale.transform.Transforms
import jetbrains.datalore.plot.base.scale.transform.Transforms.createBreaksGeneratorForTransformedDomain
import jetbrains.datalore.plot.common.data.SeriesUtil
import kotlin.math.max
import kotlin.math.min

internal class ContinuousScale<T> : AbstractScale<Double, T> {
    override val isContinuous: Boolean
    override val isContinuousDomain: Boolean = true
    override val domainLimits: Pair<Double, Double>

    override var transform: Transform = Transforms.IDENTITY

    private var customBreaksGenerator: BreaksGenerator? = null

    override var breaksGenerator: BreaksGenerator
        private set(v) {
            customBreaksGenerator = v
        }
        get() {
            return if (customBreaksGenerator != null) {
                Transforms.BreaksGeneratorForTransformedDomain(transform, customBreaksGenerator!!)
            } else {
                createBreaksGeneratorForTransformedDomain(transform, labelFormatter)
            }
        }

    constructor(
        name: String,
        mapper: ((Double?) -> T?),
        continuousOutput: Boolean
    ) : super(name, mapper) {
        isContinuous = continuousOutput
        domainLimits = Pair(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)

        // see: https://ggplot2.tidyverse.org/reference/scale_continuous.html
        // defaults for continuous scale.
        multiplicativeExpand = 0.05
        additiveExpand = 0.0
    }

    private constructor(b: MyBuilder<T>) : super(b) {
        transform = b.myTransform
        customBreaksGenerator = b.myCustomBreaksGenerator
        isContinuous = b.myContinuousOutput
        val lower = if (SeriesUtil.isFinite(b.myLowerLimit)) b.myLowerLimit!! else Double.NEGATIVE_INFINITY
        val upper = if (SeriesUtil.isFinite(b.myUpperLimit)) b.myUpperLimit!! else Double.POSITIVE_INFINITY
        domainLimits = Pair(
            min(lower, upper),
            max(lower, upper)
        )
    }

    override fun hasBreaksGenerator() = true

    override fun isInDomainLimits(v: Any): Boolean {
        return if (v is Number) {
            val d = v.toDouble()
            d.isFinite() && d >= domainLimits.first && d <= domainLimits.second
        } else {
            false
        }
    }

    override fun hasDomainLimits(): Boolean {
        return domainLimits.first.isFinite() || domainLimits.second.isFinite()
    }

    override fun with(): Scale.Builder<T> {
        return MyBuilder(this)
    }


    private class MyBuilder<T>(scale: ContinuousScale<T>) : AbstractBuilder<Double, T>(scale) {
        internal var myCustomBreaksGenerator: BreaksGenerator? = scale.customBreaksGenerator
        internal val myContinuousOutput: Boolean = scale.isContinuous
        internal var myLowerLimit: Double? = scale.domainLimits.first
        internal var myUpperLimit: Double? = scale.domainLimits.second

        override fun lowerLimit(v: Double): Scale.Builder<T> {
            require(v.isFinite()) { "`lower` can't be $v" }
            myLowerLimit = v
            return this
        }

        override fun upperLimit(v: Double): Scale.Builder<T> {
            require(v.isFinite()) { "`upper` can't be $v" }
            myUpperLimit = v
            return this
        }

        override fun limits(domainValues: List<Any>): Scale.Builder<T> {
            throw IllegalArgumentException("Can't apply discrete limits to scale with continuous domain")
        }

        override fun continuousTransform(v: Transform): Scale.Builder<T> {
            return transform(v)
        }

        override fun breaksGenerator(v: BreaksGenerator): Scale.Builder<T> {
            myCustomBreaksGenerator = v
            return this
        }

        override fun build(): Scale<T> {
            return ContinuousScale(this)
        }
    }
}
