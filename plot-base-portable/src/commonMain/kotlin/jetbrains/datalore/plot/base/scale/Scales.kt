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
        aes: Aes<T>
    ): Scale<T> {
        return ContinuousScale<T>(
            name,
            Mappers.undefined(),
            aes.isNumeric
        )
    }

    fun continuousDomainNumericRange(
        name: String
    ): Scale<Double> {
        return ContinuousScale<Double>(
            name,
            Mappers.undefined(),
            true
        )
    }

    fun <T> continuousDomain(
        name: String,
        mapper: (Double?) -> T?,
        continuousRange: Boolean
    ): Scale<T> {
        return ContinuousScale<T>(name, mapper, continuousRange)
    }

    fun <T> discreteDomain(name: String, discreteTransform: DiscreteTransform, mapper: ((Double?) -> T?)): Scale<T> {
        return DiscreteScale(name, discreteTransform, mapper)
    }

    /**
     * Functions to be used in demos and tests only.
     */
    object DemoAndTest {
        fun <T> discreteDomain(
            name: String,
            domainValues: List<Any>,
            domainLimits: List<Any> = emptyList(),
            mapper: ((Double?) -> T) = Mappers.undefined()
        ): Scale<T> {
            return DiscreteScale(
                name,
                DiscreteTransform(domainValues, domainLimits),
                mapper
            )
        }

        fun <T> pureDiscrete(
            name: String,
            domainValues: List<Any>,
            outputValues: List<T>,
            defaultOutputValue: T
        ): Scale<T> {
            val transform = DiscreteTransform(domainValues, emptyList())
            val mapper = Mappers.discrete(transform, outputValues, defaultOutputValue)
            return DiscreteScale(name, transform, mapper)
        }
    }
}
