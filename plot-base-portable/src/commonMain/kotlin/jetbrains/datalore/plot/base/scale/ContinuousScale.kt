/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale

import jetbrains.datalore.plot.base.ContinuousTransform
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.scale.transform.Transforms
import jetbrains.datalore.plot.base.scale.transform.Transforms.createBreaksGeneratorForTransformedDomain

internal class ContinuousScale<T> : AbstractScale<Double, T> {

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

    private constructor(b: MyBuilder<T>) : super(b) {
        continuousTransform = b.myContinuousTransform
        customBreaksGenerator = b.myCustomBreaksGenerator
        isContinuous = b.myContinuousOutput
    }

    override fun getBreaksGenerator(): BreaksGenerator {
        return if (customBreaksGenerator != null) {
            Transforms.BreaksGeneratorForTransformedDomain(continuousTransform, customBreaksGenerator)
        } else {
            createBreaksGeneratorForTransformedDomain(continuousTransform, labelFormatter)
        }
    }

    override fun with(): Scale.Builder<T> {
        return MyBuilder(this)
    }


    private class MyBuilder<T>(scale: ContinuousScale<T>) : AbstractBuilder<Double, T>(scale) {
        var myContinuousTransform: ContinuousTransform = scale.continuousTransform
        var myCustomBreaksGenerator: BreaksGenerator? = scale.customBreaksGenerator

        val myContinuousOutput: Boolean = scale.isContinuous

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
