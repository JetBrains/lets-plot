/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale

import jetbrains.datalore.plot.base.ContinuousTransform
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.Transform
import jetbrains.datalore.plot.base.scale.transform.Transforms
import jetbrains.datalore.plot.base.scale.transform.Transforms.createBreaksGeneratorForTransformedDomain
import jetbrains.datalore.plot.common.data.SeriesUtil
import kotlin.math.max
import kotlin.math.min

internal class ContinuousScale<T> : AbstractScale<Double, T> {

    private val continuousTransform: ContinuousTransform
    private val customBreaksGenerator: BreaksGenerator?

    override val isContinuous: Boolean
    override val isContinuousDomain: Boolean = true
    val continuousDomainLimits: Pair<Double, Double>

    override val transform: Transform
        get() = continuousTransform

    constructor(
        name: String,
        mapper: ((Double?) -> T?),
        continuousOutput: Boolean
    ) : super(name, mapper) {
        isContinuous = continuousOutput
        continuousDomainLimits = Pair(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)
        continuousTransform = Transforms.IDENTITY
        customBreaksGenerator = null

        // see: https://ggplot2.tidyverse.org/reference/scale_continuous.html
        // defaults for continuous scale.
        multiplicativeExpand = 0.05
        additiveExpand = 0.0
    }

    private constructor(b: MyBuilder<T>) : super(b) {
        continuousTransform = b.myContinuousTransform
        customBreaksGenerator = b.myCustomBreaksGenerator
        isContinuous = b.myContinuousOutput
        val lower = if (SeriesUtil.isFinite(b.myLowerLimit)) b.myLowerLimit!! else Double.NEGATIVE_INFINITY
        val upper = if (SeriesUtil.isFinite(b.myUpperLimit)) b.myUpperLimit!! else Double.POSITIVE_INFINITY
        continuousDomainLimits = Pair(
            min(lower, upper),
            max(lower, upper)
        )
    }

    override fun getBreaksGenerator(): BreaksGenerator {
        return if (customBreaksGenerator != null) {
            Transforms.BreaksGeneratorForTransformedDomain(continuousTransform, customBreaksGenerator)
        } else {
            createBreaksGeneratorForTransformedDomain(continuousTransform, labelFormatter)
        }
    }

    override fun isInDomainLimits(v: Any): Boolean {
        return if (v is Number) {
            val d = v.toDouble()
            d.isFinite() && d >= continuousDomainLimits.first && d <= continuousDomainLimits.second
        } else {
            false
        }
    }

    override fun hasDomainLimits(): Boolean {
        return continuousDomainLimits.first.isFinite() || continuousDomainLimits.second.isFinite()
    }

    override fun with(): Scale.Builder<T> {
        return MyBuilder(this)
    }


    private class MyBuilder<T>(scale: ContinuousScale<T>) : AbstractBuilder<Double, T>(scale) {
        var myContinuousTransform: ContinuousTransform = scale.continuousTransform
        var myCustomBreaksGenerator: BreaksGenerator? = scale.customBreaksGenerator
        var myLowerLimit: Double? = scale.continuousDomainLimits.first
        var myUpperLimit: Double? = scale.continuousDomainLimits.second

        val myContinuousOutput: Boolean = scale.isContinuous

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

        override fun continuousTransform(v: ContinuousTransform): Scale.Builder<T> {
            myContinuousTransform = v
            return this
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
