/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.ContinuousTransform
import jetbrains.datalore.plot.base.DiscreteTransform
import jetbrains.datalore.plot.base.scale.MapperUtil
import jetbrains.datalore.plot.builder.scale.mapper.GuideMappers
import jetbrains.datalore.plot.builder.scale.provider.ColorBrewerMapperProvider
import jetbrains.datalore.plot.builder.scale.provider.ColorGradientMapperProvider
import jetbrains.datalore.plot.builder.scale.provider.IdentityDiscreteMapperProvider

object DefaultMapperProviderUtil {

    internal fun createColorMapperProvider(): MapperProvider<Color> {
        val discrete = ColorBrewerMapperProvider(null, null, null, Color.GRAY)
        val continuous = ColorGradientMapperProvider.DEFAULT
        return object : MapperProvider<Color> {
            override fun createDiscreteMapper(discreteTransform: DiscreteTransform): GuideMapper<Color> {
                return discrete.createDiscreteMapper(discreteTransform)
            }

            override fun createContinuousMapper2(
                domain: ClosedRange<Double>,
                trans: ContinuousTransform
            ): GuideMapper<Color> {
                return continuous.createContinuousMapper2(domain, trans)
            }
        }
    }

    fun <T> createWithDiscreteOutput(outputValues: List<T>, naValue: T): MapperProvider<T> {
        return object : MapperProvider<T> {
            override fun createDiscreteMapper(discreteTransform: DiscreteTransform): GuideMapper<T> {
                return GuideMappers.discreteToDiscrete(discreteTransform, outputValues, naValue)
            }

            override fun createContinuousMapper2(
                domain: ClosedRange<Double>,
                trans: ContinuousTransform
            ): GuideMapper<T> {
                return GuideMappers.continuousToDiscrete(
                    MapperUtil.rangeWithLimitsAfterTransform2(domain, trans),
                    outputValues, naValue
                )
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    internal fun createObjectIdentity(aes: Aes<Any?>): MapperProvider<Any?> {
        return object : IdentityDiscreteMapperProvider<Any?>({ it }, null) {
            override fun createContinuousMapper2(
                domain: ClosedRange<Double>,
                trans: ContinuousTransform
            ): GuideMapper<Any?> {
                return GuideMappers.asContinuous { it }
            }
        }
    }

    internal fun createObjectIdentityDiscrete(aes: Aes<Any>): MapperProvider<Any> {
        val converter: (Any?) -> Any? = { it }
        return createIdentityMapperProvider(aes, converter, null)
    }

    internal fun createStringIdentity(aes: Aes<String>): MapperProvider<String> {
        val converter = { it: Any? -> it?.toString() }
        val continuousMapper = { it: Double? -> it?.toString() }
        return createIdentityMapperProvider(
            aes,
            converter,
            continuousMapper
        )
    }

    private fun <T> createIdentityMapperProvider(
        aes: Aes<T>,
        converter: (Any?) -> T?,
        continuousMapper: ((Double?) -> T?)?
    ): MapperProvider<T> {
        return object : IdentityDiscreteMapperProvider<T>(converter, DefaultNaValue[aes]) {
            override fun createContinuousMapper2(
                domain: ClosedRange<Double>,
                trans: ContinuousTransform
            ): GuideMapper<T> {
                if (continuousMapper != null) {
                    return GuideMappers.asContinuous(continuousMapper)
                }
                throw IllegalStateException("Can't create $aes mapper for continuous domain $domain")
            }
        }
    }
}
