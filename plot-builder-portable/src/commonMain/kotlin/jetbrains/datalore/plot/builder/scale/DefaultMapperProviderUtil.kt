/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Transform
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
            override fun createDiscreteMapper(domainValues: Collection<*>): GuideMapper<Color> {
                return discrete.createDiscreteMapper(domainValues)
            }

            override fun createContinuousMapper(
                domain: ClosedRange<Double>,
                lowerLimit: Double?,
                upperLimit: Double?,
                trans: Transform?
            ): GuideMapper<Color> {
                return continuous.createContinuousMapper(domain, lowerLimit, upperLimit, trans)
            }
        }
    }

    fun <T> createWithDiscreteOutput(outputValues: List<T>, naValue: T): MapperProvider<T> {
        return object : MapperProvider<T> {
            override fun createDiscreteMapper(domainValues: Collection<*>): GuideMapper<T> {
                return GuideMappers.discreteToDiscrete(domainValues, outputValues, naValue)
            }

            override fun createContinuousMapper(
                domain: ClosedRange<Double>,
                lowerLimit: Double?,
                upperLimit: Double?,
                trans: Transform?
            ): GuideMapper<T> {
                return GuideMappers.continuousToDiscrete(
                    MapperUtil.rangeWithLimitsAfterTransform(domain, lowerLimit, upperLimit, trans),
                    outputValues, naValue
                )
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    internal fun createObjectIdentity(aes: Aes<Any?>): MapperProvider<Any?> {
        return object : IdentityDiscreteMapperProvider<Any?>({ it }, null) {
            override fun createContinuousMapper(
                domain: ClosedRange<Double>,
                lowerLimit: Double?,
                upperLimit: Double?,
                trans: Transform?
            ): GuideMapper<Any?> {
                return GuideMappers.adaptContinuous { it }
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
            override fun createContinuousMapper(
                domain: ClosedRange<Double>,
                lowerLimit: Double?,
                upperLimit: Double?,
                trans: Transform?
            ): GuideMapper<T> {
                if (continuousMapper != null) {
                    return GuideMappers.adaptContinuous(continuousMapper)
                }
                throw IllegalStateException("Can't create $aes mapper for continuous domain $domain")
            }
        }
    }
}
