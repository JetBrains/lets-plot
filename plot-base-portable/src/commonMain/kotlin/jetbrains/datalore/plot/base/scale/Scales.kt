/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale

import jetbrains.datalore.plot.base.Aes
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

    fun <T> discreteDomain(name: String, domainValues: Collection<Any>): Scale<T> {
        return discreteDomain(
            name,
            domainValues,
            Mappers.undefined()
        )
    }

    fun <T> discreteDomain(name: String, domainValues: Collection<Any>, mapper: ((Double?) -> T?)): Scale<T> {
        return DiscreteScale(name, domainValues, mapper)
    }

    fun <T> pureDiscrete(
        name: String,
        domainValues: List<Any>,
        outputValues: List<T>,
        defaultOutputValue: T
    ): Scale<T> {
        return discreteDomain<T>(name, domainValues)
            .with()
            .mapper(Mappers.discrete(outputValues, defaultOutputValue))
            .build()
    }
}
