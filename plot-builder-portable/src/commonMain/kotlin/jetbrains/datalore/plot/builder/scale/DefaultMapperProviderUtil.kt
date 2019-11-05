/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.DataFrame.Variable
import jetbrains.datalore.plot.base.Transform
import jetbrains.datalore.plot.base.scale.MapperUtil
import jetbrains.datalore.plot.builder.scale.mapper.GuideMappers
import jetbrains.datalore.plot.builder.scale.provider.ColorBrewerMapperProvider
import jetbrains.datalore.plot.builder.scale.provider.ColorGradientMapperProvider
import jetbrains.datalore.plot.builder.scale.provider.IdentityDiscreteMapperProvider

object DefaultMapperProviderUtil {

    internal fun createColor(): MapperProvider<Color> {
        val discrete =
            ColorBrewerMapperProvider("qual", "Dark2", null, Color.GRAY)
        val continuous = ColorGradientMapperProvider.DEFAULT
        return object : MapperProvider<Color> {
            override fun createDiscreteMapper(data: DataFrame, variable: Variable): GuideMapper<Color> {
                return discrete.createDiscreteMapper(data, variable)
            }

            override fun createContinuousMapper(
                data: DataFrame, variable: Variable, lowerLimit: Double?, upperLimit: Double?, trans: Transform?): GuideMapper<Color> {
                return continuous.createContinuousMapper(data, variable, lowerLimit, upperLimit, trans)
            }
        }
    }

    fun <T> createWithDiscreteOutput(outputValues: List<T>, naValue: T): MapperProvider<T> {
        return object : MapperProvider<T> {
            override fun createDiscreteMapper(data: DataFrame, variable: Variable): GuideMapper<T> {
                return GuideMappers.discreteToDiscrete(data, variable, outputValues, naValue)
            }

            override fun createContinuousMapper(data: DataFrame, variable: Variable, lowerLimit: Double?, upperLimit: Double?, trans: Transform?): GuideMapper<T> {
                return GuideMappers.continuousToDiscrete(
                        MapperUtil.rangeWithLimitsAfterTransform(data, variable, lowerLimit, upperLimit, trans),
                        outputValues, naValue
                )
            }
        }
    }

    internal fun createObjectIdentityDiscrete(aes: Aes<Any>): MapperProvider<Any> {
        val converter = { it: Any? -> it }
        return createIdentityMapperProvider(
            aes,
            converter,
            null
        )
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

    private fun <T> createIdentityMapperProvider(aes: Aes<T>, converter: (Any?) -> T?, continuousMapper: ((Double?) -> T?)?): MapperProvider<T> {
        return object : IdentityDiscreteMapperProvider<T>(converter, DefaultNaValue[aes]) {
            override fun createContinuousMapper(data: DataFrame, variable: Variable, lowerLimit: Double?, upperLimit: Double?, trans: Transform?): GuideMapper<T> {
                if (continuousMapper != null) {
                    return GuideMappers.adaptContinuous(continuousMapper)
                }
                throw IllegalStateException("Can't create $aes mapper for continuous domain ($variable)")
            }
        }
    }
}
