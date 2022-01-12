/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale.provider

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.ContinuousTransform
import jetbrains.datalore.plot.base.scale.MapperUtil
import jetbrains.datalore.plot.builder.scale.ContinuousOnlyMapperProvider
import jetbrains.datalore.plot.builder.scale.GuideMapper
import jetbrains.datalore.plot.builder.scale.mapper.GuideMappers

/**
 * @param naValue value used when size is not defined
 */
open class DirectlyProportionalMapperProvider(
    private val max: Double,
    naValue: Double
) : ContinuousOnlyMapperProvider<Double>(naValue) {
    override fun createContinuousMapper2(domain: ClosedRange<Double>, trans: ContinuousTransform): GuideMapper<Double> {
        val dataMax = MapperUtil.rangeWithLimitsAfterTransform2(domain, trans).upperEnd
        return GuideMappers.continuousToContinuous(ClosedRange(0.0, dataMax), ClosedRange(0.0, max), naValue)
    }
}
