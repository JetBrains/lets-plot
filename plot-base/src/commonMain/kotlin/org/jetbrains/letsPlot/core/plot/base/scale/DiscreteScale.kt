/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale

import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.jetbrains.letsPlot.core.plot.base.DiscreteTransform
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.Transform

internal class DiscreteScale : AbstractScale<Any> {

    private val discreteTransform: DiscreteTransform

    override val transform: Transform
        get() = discreteTransform

    override val isContinuous: Boolean = false
    override val isContinuousDomain: Boolean = false

    constructor(
        name: String,
        discreteTransform: DiscreteTransform,
    ) : super(name) {
        this.discreteTransform = discreteTransform

        // see: https://ggplot2.tidyverse.org/reference/scale_continuous.html
        // defaults for discrete scale.
        multiplicativeExpand = 0.0
        additiveExpand = 0.2 //0.6
    }

    private constructor(b: MyBuilder) : super(b) {
        discreteTransform = b.discreteTransform
    }

    override fun getBreaksGenerator(): BreaksGenerator {
        throw IllegalStateException("No breaks generator for discrete scale '$name'")
    }

    override fun hasBreaks(): Boolean {
        // Discrete scale always has breaks: either "defined" or "effective domain".
        return true
    }

    override fun createScaleBreaks(shortenLabels: Boolean): ScaleBreaks {
        // Discrete scale ignores 'providedScaleBreaks'

        // Breaks
        val breaksEffective = if (providedBreaks != null) {
            // Intersect, preserve the order in the 'domain'.
            val breaksSet = providedBreaks.toSet()
            discreteTransform.effectiveDomain.filter { it in breaksSet }
        } else {
            discreteTransform.effectiveDomain
        }

        // Labels
        val labels = providedLabels?.let { it ->
            if (!transform.hasDomainLimits()) {
                val breaksFull = providedBreaks ?: discreteTransform.initialDomain
                alignLablesAndBreaks(breaksFull, it)
            } else if (providedBreaks == null) {
                alignLablesAndBreaks(breaksEffective, it)
            } else {
                // Limits + provived breaks - allign labels with the limits
                // Associate 'defined labels' with 'defined breaks', then re-order according to the domain order.
                val labelsAligned = alignLablesAndBreaks(providedBreaks, it)

                // Filter and preserve the order in "limits".
                val labelByBreak = providedBreaks.zip(labelsAligned).toMap()
                discreteTransform.effectiveDomain
                    .filter { labelByBreak.containsKey(it) }
                    .map { labelByBreak.getValue(it) }
            }
        }

        return ScaleBreaks.Fixed.withTransform(
            breaksEffective,
            transform = transform,
            formatter = providedFormatter ?: ScaleBreaks.IDENTITY_FORMATTER,
            alternativeLabels = labels,
            labelLengthLimit = if (shortenLabels) labelLengthLimit else null
        )
    }

    override fun with(): Scale.Builder {
        return MyBuilder(this)
    }

    private class MyBuilder(scale: DiscreteScale) : AbstractBuilder<Any>(scale) {
        internal var discreteTransform: DiscreteTransform = scale.discreteTransform

        override fun breaksGenerator(v: BreaksGenerator): Scale.Builder {
            throw IllegalStateException("Not applicable to scale with discrete domain")
        }

        override fun continuousTransform(v: ContinuousTransform): Scale.Builder {
            // ignore
            return this
        }

        override fun discreteTransform(v: DiscreteTransform): Scale.Builder {
            discreteTransform = v
            return this
        }

        override fun build(): Scale {
            return DiscreteScale(this)
        }
    }
}
