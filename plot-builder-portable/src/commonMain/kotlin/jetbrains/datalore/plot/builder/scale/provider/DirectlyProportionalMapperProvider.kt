/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale.provider

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.Transform
import jetbrains.datalore.plot.base.scale.MapperUtil
import jetbrains.datalore.plot.builder.scale.GuideMapper
import jetbrains.datalore.plot.builder.scale.mapper.GuideMappers

open class DirectlyProportionalMapperProvider
/**
 * @param naValue value used when size is not defined
 */
(private val myMax: Double, naValue: Double) : MapperProviderBase<Double>(naValue) {

    override fun createContinuousMapper(data: DataFrame, variable: DataFrame.Variable, lowerLimit: Double?, upperLimit: Double?, trans: Transform?): GuideMapper<Double> {
        val dataMax = MapperUtil.rangeWithLimitsAfterTransform(data, variable, lowerLimit, upperLimit, trans).upperEnd
        return GuideMappers.continuousToContinuous(ClosedRange(0.0, dataMax), ClosedRange(0.0, myMax), naValue)
    }
}
