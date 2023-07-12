/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale.provider

import org.jetbrains.letsPlot.core.plot.base.DiscreteTransform
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import jetbrains.datalore.plot.builder.scale.DiscreteOnlyMapperProvider
import jetbrains.datalore.plot.builder.scale.mapper.GuideMappers

open class IdentityDiscreteMapperProvider<T>(
    private val inputConverter: (Any?) -> T?,
) : DiscreteOnlyMapperProvider<T>() {

    override fun createDiscreteMapper(discreteTransform: DiscreteTransform): ScaleMapper<T> {
        val outputValues: List<T> = discreteTransform.effectiveDomain.map {
            inputConverter(it) ?: throw IllegalStateException("Can't map input value $it to output type.")
        }
        return GuideMappers.discreteToDiscrete(discreteTransform, outputValues, null)
    }
}
