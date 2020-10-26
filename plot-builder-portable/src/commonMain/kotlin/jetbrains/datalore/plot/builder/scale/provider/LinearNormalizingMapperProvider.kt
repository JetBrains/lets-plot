/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale.provider

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.Transform
import jetbrains.datalore.plot.base.scale.MapperUtil
import jetbrains.datalore.plot.builder.scale.GuideMapper
import jetbrains.datalore.plot.builder.scale.mapper.GuideMappers

open class LinearNormalizingMapperProvider(
    private val outputRange: ClosedRange<Double>,
    naValue: Double
) : MapperProviderBase<Double>(naValue) {

    override fun createDiscreteMapper(domainValues: Collection<*>): GuideMapper<Double> {
        return GuideMappers.discreteToContinuous(domainValues, outputRange, naValue)
    }

    override fun createContinuousMapper(
        domain: ClosedRange<Double>,
        lowerLimit: Double?,
        upperLimit: Double?,
        trans: Transform?
    ): GuideMapper<Double> {
        val dataRange = MapperUtil.rangeWithLimitsAfterTransform(domain, lowerLimit, upperLimit, trans)
        return GuideMappers.continuousToContinuous(dataRange, outputRange, naValue)
    }
}
