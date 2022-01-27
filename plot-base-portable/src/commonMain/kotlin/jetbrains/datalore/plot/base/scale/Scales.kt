/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DiscreteTransform
import jetbrains.datalore.plot.base.Scale

object Scales {

    fun <T> continuousDomain(
        name: String,
        continuousRange: Boolean
    ): Scale<T> {
        return ContinuousScale<T>(name, continuousRange)
    }

    fun <T> discreteDomain(
        name: String,
        discreteTransform: DiscreteTransform,
    ): Scale<T> {
        return DiscreteScale(name, discreteTransform)
    }

    /**
     * Functions to be used in demos and tests only.
     */
    object DemoAndTest {
        fun <T> discreteDomain(
            name: String,
            domainValues: List<Any>,
            domainLimits: List<Any> = emptyList(),
        ): Scale<T> {
            return DiscreteScale(
                name,
                DiscreteTransform(domainValues, domainLimits),
            )
        }

        fun <T> pureDiscrete(
            name: String,
            domainValues: List<Any>,
        ): Scale<T> {
            val transform = DiscreteTransform(domainValues, emptyList())
            return DiscreteScale(name, transform)
        }

        fun <T> continuousDomain(name: String, aes: Aes<T>): Scale<T> {
            return ContinuousScale<T>(
                name,
                aes.isNumeric
            )
        }

        fun continuousDomainNumericRange(name: String): Scale<Double> {
            return ContinuousScale<Double>(
                name,
                true
            )
        }
    }
}
