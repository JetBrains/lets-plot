/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale

import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.scale.transform.Transforms
import org.jetbrains.letsPlot.core.plot.base.scale.transform.Transforms.createBreaksGeneratorForTransformedDomain

internal class ContinuousScale : AbstractScale<Double> {

    private val continuousTransform: ContinuousTransform
    private val customBreaksGenerator: BreaksGenerator?

    override val isContinuous: Boolean
    override val isContinuousDomain: Boolean = true

    override val transform: ContinuousTransform
        get() = continuousTransform

    constructor(
        name: String,
        continuousOutput: Boolean
    ) : super(name) {
        isContinuous = continuousOutput
        continuousTransform = Transforms.IDENTITY
        customBreaksGenerator = null

        // see: https://ggplot2.tidyverse.org/reference/scale_continuous.html
        // defaults for continuous scale.
        multiplicativeExpand = 0.05
        additiveExpand = 0.0
    }

    private constructor(b: MyBuilder) : super(b) {
        continuousTransform = b.myContinuousTransform
        customBreaksGenerator = b.myCustomBreaksGenerator
        isContinuous = b.myContinuousOutput
    }

    override fun getBreaksGenerator(): BreaksGenerator {
        return if (customBreaksGenerator != null) {
            Transforms.BreaksGeneratorForTransformedDomain(continuousTransform, customBreaksGenerator)
        } else {
            createBreaksGeneratorForTransformedDomain(continuousTransform, labelFormatter, superscriptExponent)
        }
    }

    override fun with(): Scale.Builder {
        return MyBuilder(this)
    }


    private class MyBuilder(scale: ContinuousScale) : AbstractBuilder<Double>(scale) {
        var myContinuousTransform: ContinuousTransform = scale.continuousTransform
        var myCustomBreaksGenerator: BreaksGenerator? = scale.customBreaksGenerator

        val myContinuousOutput: Boolean = scale.isContinuous

        override fun continuousTransform(v: ContinuousTransform): Scale.Builder {
            myContinuousTransform = v
            return this
        }

        override fun breaksGenerator(v: BreaksGenerator): Scale.Builder {
            myCustomBreaksGenerator = v
            return this
        }

        override fun build(): Scale {
            return ContinuousScale(this)
        }
    }
}
