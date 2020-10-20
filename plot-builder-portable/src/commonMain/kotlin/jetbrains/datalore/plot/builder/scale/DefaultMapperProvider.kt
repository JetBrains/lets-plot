/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Aes.Companion.ALPHA
import jetbrains.datalore.plot.base.Aes.Companion.ANGLE
import jetbrains.datalore.plot.base.Aes.Companion.COLOR
import jetbrains.datalore.plot.base.Aes.Companion.FAMILY
import jetbrains.datalore.plot.base.Aes.Companion.FILL
import jetbrains.datalore.plot.base.Aes.Companion.FLOW
import jetbrains.datalore.plot.base.Aes.Companion.FONTFACE
import jetbrains.datalore.plot.base.Aes.Companion.FRAME
import jetbrains.datalore.plot.base.Aes.Companion.HEIGHT
import jetbrains.datalore.plot.base.Aes.Companion.HJUST
import jetbrains.datalore.plot.base.Aes.Companion.INTERCEPT
import jetbrains.datalore.plot.base.Aes.Companion.LABEL
import jetbrains.datalore.plot.base.Aes.Companion.LINETYPE
import jetbrains.datalore.plot.base.Aes.Companion.LOWER
import jetbrains.datalore.plot.base.Aes.Companion.MIDDLE
import jetbrains.datalore.plot.base.Aes.Companion.SHAPE
import jetbrains.datalore.plot.base.Aes.Companion.SIZE
import jetbrains.datalore.plot.base.Aes.Companion.SLOPE
import jetbrains.datalore.plot.base.Aes.Companion.SPEED
import jetbrains.datalore.plot.base.Aes.Companion.SYM_X
import jetbrains.datalore.plot.base.Aes.Companion.SYM_Y
import jetbrains.datalore.plot.base.Aes.Companion.UPPER
import jetbrains.datalore.plot.base.Aes.Companion.VJUST
import jetbrains.datalore.plot.base.Aes.Companion.WEIGHT
import jetbrains.datalore.plot.base.Aes.Companion.WIDTH
import jetbrains.datalore.plot.base.Aes.Companion.X
import jetbrains.datalore.plot.base.Aes.Companion.XEND
import jetbrains.datalore.plot.base.Aes.Companion.XINTERCEPT
import jetbrains.datalore.plot.base.Aes.Companion.XMAX
import jetbrains.datalore.plot.base.Aes.Companion.XMIN
import jetbrains.datalore.plot.base.Aes.Companion.Y
import jetbrains.datalore.plot.base.Aes.Companion.YEND
import jetbrains.datalore.plot.base.Aes.Companion.YINTERCEPT
import jetbrains.datalore.plot.base.Aes.Companion.YMAX
import jetbrains.datalore.plot.base.Aes.Companion.YMIN
import jetbrains.datalore.plot.base.Aes.Companion.Z
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.Transform
import jetbrains.datalore.plot.builder.scale.DefaultMapperProviderUtil.createColorMapperProvider
import jetbrains.datalore.plot.builder.scale.DefaultMapperProviderUtil.createObjectIdentityDiscrete
import jetbrains.datalore.plot.builder.scale.DefaultMapperProviderUtil.createObjectIdentity
import jetbrains.datalore.plot.builder.scale.DefaultMapperProviderUtil.createStringIdentity
import jetbrains.datalore.plot.builder.scale.DefaultMapperProviderUtil.createWithDiscreteOutput
import jetbrains.datalore.plot.builder.scale.mapper.GuideMappers
import jetbrains.datalore.plot.builder.scale.mapper.LineTypeMapper
import jetbrains.datalore.plot.builder.scale.mapper.ShapeMapper
import jetbrains.datalore.plot.builder.scale.provider.AlphaMapperProvider
import jetbrains.datalore.plot.builder.scale.provider.SizeMapperProvider

object DefaultMapperProvider {

    private val PROVIDER_MAP = TypedMapperProviderMap()

    operator fun <T> get(aes: Aes<T>): MapperProvider<T> {
        return PROVIDER_MAP[aes]
    }

    /**
     * For tests
     */
    internal fun hasDefault(aes: Aes<*>): Boolean {
        return PROVIDER_MAP.containsKey(aes)
    }

    private class TypedMapperProviderMap internal constructor() {

        private var myMap: MutableMap<Aes<*>, MapperProvider<*>> = HashMap()

        init {
            for (aes in Aes.allPositional()) {
                put(aes, NUMERIC_UNDEFINED)
            }

            this.put(X, NUMERIC_IDENTITY)
            this.put(Y, NUMERIC_IDENTITY)

            this.put(Z, NUMERIC_IDENTITY)
            this.put(YMIN, NUMERIC_IDENTITY)
            this.put(YMAX, NUMERIC_IDENTITY)
            this.put(COLOR, createColorMapperProvider())
            this.put(FILL, createColorMapperProvider())
            this.put(ALPHA, AlphaMapperProvider.DEFAULT)
            this.put(SHAPE, createWithDiscreteOutput(ShapeMapper.allShapes(), ShapeMapper.NA_VALUE))
            this.put(LINETYPE, createWithDiscreteOutput(LineTypeMapper.allLineTypes(), LineTypeMapper.NA_VALUE))

            this.put(SIZE, SizeMapperProvider.DEFAULT)
            this.put(WIDTH, NUMERIC_IDENTITY)
            this.put(HEIGHT, NUMERIC_IDENTITY)
            this.put(WEIGHT, NUMERIC_IDENTITY)
            this.put(INTERCEPT, NUMERIC_IDENTITY)
            this.put(SLOPE, NUMERIC_IDENTITY)
            this.put(XINTERCEPT, NUMERIC_IDENTITY)
            this.put(YINTERCEPT, NUMERIC_IDENTITY)
            this.put(LOWER, NUMERIC_IDENTITY)
            this.put(MIDDLE, NUMERIC_IDENTITY)
            this.put(UPPER, NUMERIC_IDENTITY)

            this.put(FRAME, createStringIdentity(FRAME))

            this.put(SPEED, NUMERIC_IDENTITY)
            this.put(FLOW, NUMERIC_IDENTITY)

            this.put(XMIN, NUMERIC_IDENTITY)
            this.put(XMAX, NUMERIC_IDENTITY)
            this.put(XEND, NUMERIC_IDENTITY)
            this.put(YEND, NUMERIC_IDENTITY)

            this.put(LABEL, createObjectIdentity(LABEL))
            this.put(FAMILY, createStringIdentity(FAMILY))
            this.put(FONTFACE, createStringIdentity(FONTFACE))

            // text horizontal justification (numbers [0..1] or predefined strings, DOUBLE_CVT; not positional)
            this.put(HJUST, createObjectIdentityDiscrete(HJUST))

            // text vertical justification (numbers [0..1] or predefined strings, not positional)
            this.put(VJUST, createObjectIdentityDiscrete(VJUST))
            this.put(ANGLE, NUMERIC_IDENTITY)

            this.put(SYM_X, NUMERIC_IDENTITY)
            this.put(SYM_Y, NUMERIC_IDENTITY)
        }

        internal operator fun <T> get(aes: Aes<T>): MapperProvider<T> {
            @Suppress("UNCHECKED_CAST")
            return myMap[aes] as MapperProvider<T>
        }

        private fun <T> put(aes: Aes<T>, value: MapperProvider<T>) {
            myMap[aes] = value
        }

        internal fun containsKey(aes: Aes<*>): Boolean {
            return myMap.containsKey(aes)
        }

        companion object {
            // For most of numeric (positional) aesthetics the initial mapper is UNDEFINED mapper as we don't yet know the range of positional aesthetics.
            private val NUMERIC_UNDEFINED: MapperProvider<Double> = object :
                MapperProvider<Double> {
                override fun createDiscreteMapper(data: DataFrame, variable: DataFrame.Variable): GuideMapper<Double> {
                    return GuideMappers.UNDEFINED
                }

                override fun createContinuousMapper(data: DataFrame, variable: DataFrame.Variable, lowerLimit: Double?, upperLimit: Double?,
                                                    trans: Transform?): GuideMapper<Double> {
                    return GuideMappers.UNDEFINED
                }
            }

            private val NUMERIC_IDENTITY: MapperProvider<Double> = object :
                MapperProvider<Double> {
                override fun createDiscreteMapper(data: DataFrame, variable: DataFrame.Variable): GuideMapper<Double> {
                    return GuideMappers.IDENTITY
                }

                override fun createContinuousMapper(data: DataFrame, variable: DataFrame.Variable, lowerLimit: Double?, upperLimit: Double?,
                                                    trans: Transform?): GuideMapper<Double> {
                    return GuideMappers.IDENTITY
                }
            }
        }
    }
}
