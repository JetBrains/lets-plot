/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale.provider

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.jetbrains.letsPlot.core.plot.base.DiscreteTransform
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import jetbrains.datalore.plot.builder.scale.GuideMapper
import jetbrains.datalore.plot.builder.scale.MapperProvider
import jetbrains.datalore.plot.builder.scale.mapper.GuideMappers

class IdentityMapperProvider<T>(
    private val discreteMapperProvider: IdentityDiscreteMapperProvider<T>,
    private val continuousMapper: ScaleMapper<T>
) : MapperProvider<T> {

    override fun createDiscreteMapper(discreteTransform: DiscreteTransform): ScaleMapper<T> {
        return discreteMapperProvider.createDiscreteMapper(discreteTransform)
    }

    override fun createContinuousMapper(domain: DoubleSpan, trans: ContinuousTransform): GuideMapper<T> {
        return GuideMappers.asContinuous(continuousMapper)
    }
}
