/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale

import jetbrains.datalore.plot.base.ContinuousTransform
import jetbrains.datalore.plot.base.DiscreteTransform
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.Transform

internal class DiscreteScale<T> : AbstractScale<Any, T> {

    private val discreteTransform: DiscreteTransform

    override val transform: Transform
        get() = discreteTransform

    constructor(
        name: String,
        domainValues: Collection<Any>,
        mapper: ((Double?) -> T?)
    ) : super(name, mapper, breaks = domainValues.toList()) {
        discreteTransform = DiscreteTransform(domainValues, emptyList())

        // see: https://ggplot2.tidyverse.org/reference/scale_continuous.html
        // defaults for discrete scale.
        multiplicativeExpand = 0.0
        additiveExpand = 0.6
    }

    private constructor(b: MyBuilder<T>) : super(b) {
        discreteTransform = DiscreteTransform(b.myDomainValues, b.myDomainLimits)
    }

    override fun getBreaksGenerator(): BreaksGenerator {
        throw IllegalStateException("No breaks generator for discrete scale '$name'")
    }

    override fun hasDomainLimits(): Boolean {
        return discreteTransform.hasDomainLimits()
    }

    override fun isInDomainLimits(v: Any): Boolean {
        return discreteTransform.isInDomain(v)
    }

    protected override fun getBreaksIntern(): List<Any> {
        return if (!hasDomainLimits()) {
            super.getBreaksIntern()
        } else {
            // Filter and preserve the order defined by limits.
            val breaksSet = super.getBreaksIntern().toSet()
            discreteTransform.domainLimits.filter { it in breaksSet }
        }
    }

    override fun getLabelsIntern(): List<String> {
        val labels = super.getLabelsIntern()
        return if (!hasDomainLimits() || labels.isEmpty()) {
            labels
        } else {
            val breaks = super.getBreaksIntern()
            val breakLabels = breaks.mapIndexed { i, _ -> labels[i % labels.size] }

            // Filter and preserve the order defined by limits.
            val labelByBreak = breaks.zip(breakLabels).toMap()
            discreteTransform.domainLimits
                .filter { labelByBreak.containsKey(it) }
                .map { labelByBreak.getValue(it) }
        }
    }

    override fun with(): Scale.Builder<T> {
        return MyBuilder(this)
    }

    private class MyBuilder<T>(scale: DiscreteScale<T>) : AbstractBuilder<Any, T>(scale) {
        internal val myDomainValues: Collection<Any> = scale.discreteTransform.domainValues
        internal var myDomainLimits: List<Any> = scale.discreteTransform.domainLimits

        override fun breaksGenerator(v: BreaksGenerator): Scale.Builder<T> {
            throw IllegalStateException("Not applicable to scale with discrete domain")
        }

        override fun lowerLimit(v: Double): Scale.Builder<T> {
            throw IllegalStateException("Not applicable to scale with discrete domain")
        }

        override fun upperLimit(v: Double): Scale.Builder<T> {
            throw IllegalStateException("Not applicable to scale with discrete domain")
        }

        override fun limits(domainValues: List<Any>): Scale.Builder<T> {
            myDomainLimits = domainValues
            return this
        }

        override fun continuousTransform(v: ContinuousTransform): Scale.Builder<T> {
            // ignore
            return this
        }

        override fun build(): Scale<T> {
            return DiscreteScale(this)
        }
    }
}
