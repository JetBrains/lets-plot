/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.ContinuousTransform
import jetbrains.datalore.plot.base.DiscreteTransform
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.base.scale.MapperUtil
import jetbrains.datalore.plot.builder.scale.mapper.GuideMappers
import jetbrains.datalore.plot.builder.scale.provider.ColorBrewerMapperProvider
import jetbrains.datalore.plot.builder.scale.provider.ColorGradientMapperProvider
import jetbrains.datalore.plot.builder.scale.provider.IdentityDiscreteMapperProvider
import jetbrains.datalore.plot.builder.scale.provider.IdentityMapperProvider

object DefaultMapperProviderUtil {

    internal fun createColorMapperProvider(): MapperProvider<Color> {
        val discrete = ColorBrewerMapperProvider(null, null, null, Color.GRAY)
        val continuous = ColorGradientMapperProvider.DEFAULT
        return object : MapperProvider<Color> {
            override fun createDiscreteMapper(discreteTransform: DiscreteTransform): ScaleMapper<Color> {
                return discrete.createDiscreteMapper(discreteTransform)
            }

            override fun createContinuousMapper(
                domain: DoubleSpan,
                trans: ContinuousTransform
            ): GuideMapper<Color> {
                return continuous.createContinuousMapper(domain, trans)
            }
        }
    }

    fun <T> createWithDiscreteOutput(outputValues: List<T>, naValue: T): MapperProvider<T> {
        return object : MapperProvider<T> {
            override fun createDiscreteMapper(discreteTransform: DiscreteTransform): ScaleMapper<T> {
                return GuideMappers.discreteToDiscrete(discreteTransform, outputValues, naValue)
            }

            override fun createContinuousMapper(
                domain: DoubleSpan,
                trans: ContinuousTransform
            ): GuideMapper<T> {
                return GuideMappers.continuousToDiscrete(
                    MapperUtil.rangeWithLimitsAfterTransform(domain, trans),
                    outputValues, naValue
                )
            }
        }
    }

    internal fun createObjectIdentity(): MapperProvider<Any> {
        val converter: (Any?) -> Any? = { it }
        val discreteMapperProvider = IdentityDiscreteMapperProvider<Any>(converter)
        val continuousMapper = object : ScaleMapper<Any> {
            override fun invoke(v: Double?): Any? = v
        }
        return IdentityMapperProvider<Any>(discreteMapperProvider, continuousMapper)
    }

    internal fun createStringIdentity(): MapperProvider<String> {
        val converter = { it: Any? -> it?.toString() }
        return IdentityDiscreteMapperProvider<String>(converter)
    }
}
