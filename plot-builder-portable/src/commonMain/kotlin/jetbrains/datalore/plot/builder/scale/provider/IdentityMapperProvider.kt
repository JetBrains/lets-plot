/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale.provider

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.Transform
import jetbrains.datalore.plot.builder.scale.GuideMapper
import jetbrains.datalore.plot.builder.scale.MapperProvider
import jetbrains.datalore.plot.builder.scale.mapper.GuideMappers

class IdentityMapperProvider<T>(
    private val discreteMapperProvider: IdentityDiscreteMapperProvider<T>,
    private val continuousMapper: (Double?) -> T?
) : MapperProvider<T> {

    override fun createDiscreteMapper(domainValues: Collection<*>): GuideMapper<T> {
        return discreteMapperProvider.createDiscreteMapper(domainValues)
    }

    override fun createContinuousMapper(
        domain: ClosedRange<Double>,
        lowerLimit: Double?,
        upperLimit: Double?,
        trans: Transform?
    ): GuideMapper<T> {
        return GuideMappers.adaptContinuous(continuousMapper)
    }
}
