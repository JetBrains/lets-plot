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

    override val isContinuous: Boolean = false
    override val isContinuousDomain: Boolean = false

    constructor(
        name: String,
        discreteTransform: DiscreteTransform,
    ) : super(name, breaks = null) {
        this.discreteTransform = discreteTransform

        // see: https://ggplot2.tidyverse.org/reference/scale_continuous.html
        // defaults for discrete scale.
        multiplicativeExpand = 0.0
        additiveExpand = 0.2 //0.6
    }

    private constructor(b: MyBuilder<T>) : super(b) {
        discreteTransform = b.discreteTransform
    }

    override fun getBreaksGenerator(): BreaksGenerator {
        throw IllegalStateException("No breaks generator for discrete scale '$name'")
    }

    override fun hasBreaks(): Boolean {
        // Discrete scale always has breaks: either "defined" or "effective domain".
        return true
    }

    protected override fun getBreaksIntern(): List<Any> {
        return if (hasDefinedBreaks()) {
            // Intersect, preserve the order in the 'domain'.
            val breaksSet = super.getBreaksIntern().toSet()
            discreteTransform.effectiveDomain.filter { it in breaksSet }
        } else {
            discreteTransform.effectiveDomain
        }
    }

    protected override fun getLabelsIntern(): List<String> {
        val labels = super.getLabelsIntern()
        return if (!transform.hasDomainLimits() || labels.isEmpty()) {
            labels
        } else if (!hasDefinedBreaks()) {
            labels
        } else {
            // Associate 'defined labels' with 'defined breaks', then re-order according to the domain order.
            val breaks = super.getBreaksIntern()  // Defined breaks!
            val breakLabels = List(breaks.size) { i -> if (i < labels.size) labels[i] else "" }

            // Filter and preserve the order.
            val labelByBreak = breaks.zip(breakLabels).toMap()
            discreteTransform.effectiveDomain
                .filter { labelByBreak.containsKey(it) }
                .map { labelByBreak.getValue(it) }
        }
    }

    override fun with(): Scale.Builder<T> {
        return MyBuilder(this)
    }

    private class MyBuilder<T>(scale: DiscreteScale<T>) : AbstractBuilder<Any, T>(scale) {
        internal val discreteTransform: DiscreteTransform = scale.discreteTransform

        override fun breaksGenerator(v: BreaksGenerator): Scale.Builder<T> {
            throw IllegalStateException("Not applicable to scale with discrete domain")
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
