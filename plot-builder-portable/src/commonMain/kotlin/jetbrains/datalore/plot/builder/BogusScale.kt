/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.Transform
import jetbrains.datalore.plot.base.scale.BreaksGenerator

internal class BogusScale : Scale<Double> {
    override val name: String
        get() = throw IllegalStateException("Bogus scale is not supposed to be used.")

    override val breaks: List<Any>
        get() = throw IllegalStateException("Bogus scale is not supposed to be used.")

    override val labels: MutableList<String>
        get() = throw IllegalStateException("Bogus scale is not supposed to be used.")

    override val labelFormatter: ((Any) -> String)?
        get() = throw IllegalStateException("Bogus scale is not supposed to be used.")

    override val isContinuous: Boolean
        get() = throw IllegalStateException("Bogus scale is not supposed to be used.")

    override val isContinuousDomain: Boolean
        get() = throw IllegalStateException("Bogus scale is not supposed to be used.")

    override val domainLimits: Pair<Double, Double>
        get() = throw IllegalStateException("Bogus scale is not supposed to be used.")

    override val multiplicativeExpand: Double
        get() = throw IllegalStateException("Bogus scale is not supposed to be used.")

    override val additiveExpand: Double
        get() = throw IllegalStateException("Bogus scale is not supposed to be used.")

    override val transform: Transform
        get() = throw IllegalStateException("Bogus scale is not supposed to be used.")

    override val mapper: (Double?) -> Double?
        get() = throw IllegalStateException("Bogus scale is not supposed to be used.")

    override val breaksGenerator: BreaksGenerator
        get() = throw IllegalStateException("Bogus scale is not supposed to be used.")

    override fun hasBreaksGenerator(): Boolean {
        throw IllegalStateException("Bogus scale is not supposed to be used.")
    }

    override fun hasBreaks(): Boolean {
        throw IllegalStateException("Bogus scale is not supposed to be used.")
    }

    override fun hasLabels(): Boolean {
        throw IllegalStateException("Bogus scale is not supposed to be used.")
    }

    override fun hasDomainLimits(): Boolean {
        throw IllegalStateException("Bogus scale is not supposed to be used.")
    }

    override fun isInDomainLimits(v: Any): Boolean {
        throw IllegalStateException("Bogus scale is not supposed to be used.")
    }

    override fun asNumber(input: Any?): Double {
        throw IllegalStateException("Bogus scale is not supposed to be used.")
    }

    override fun with(): Scale.Builder<Double> {
        throw IllegalStateException("Bogus scale is not supposed to be used.")
    }
}
