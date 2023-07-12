/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.ALPHA
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.ANGLE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.BINWIDTH
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.COLOR
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.EXPLODE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.FAMILY
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.FILL
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.FLOW
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.FONTFACE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.FRAME
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.HEIGHT
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.HJUST
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.INTERCEPT
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.LABEL
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.LINEHEIGHT
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.LINETYPE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.LOWER
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.MAP_ID
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.MIDDLE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.PAINT_A
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.PAINT_B
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.PAINT_C
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SAMPLE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.QUANTILE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SHAPE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SIZE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.STROKE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.LINEWIDTH
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SLICE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SLOPE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SPEED
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.STACKSIZE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.UPPER
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.VIOLINWIDTH
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.VJUST
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.WEIGHT
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.WIDTH
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.X
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.XEND
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.XINTERCEPT
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.XMAX
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.XMIN
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.Y
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.YEND
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.YINTERCEPT
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.YMAX
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.YMIN
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.Z
import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.jetbrains.letsPlot.core.plot.base.DiscreteTransform
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.scale.Mappers
import jetbrains.datalore.plot.builder.scale.DefaultMapperProviderUtil.createColorMapperProvider
import jetbrains.datalore.plot.builder.scale.DefaultMapperProviderUtil.createObjectIdentity
import jetbrains.datalore.plot.builder.scale.DefaultMapperProviderUtil.createStringIdentity
import jetbrains.datalore.plot.builder.scale.DefaultMapperProviderUtil.createWithDiscreteOutput
import jetbrains.datalore.plot.builder.scale.mapper.GuideMappers
import jetbrains.datalore.plot.builder.scale.mapper.LineTypeMapper
import jetbrains.datalore.plot.builder.scale.mapper.ShapeMapper
import jetbrains.datalore.plot.builder.scale.provider.AlphaMapperProvider
import jetbrains.datalore.plot.builder.scale.provider.LinewidthMapperProvider
import jetbrains.datalore.plot.builder.scale.provider.SizeMapperProvider
import jetbrains.datalore.plot.builder.scale.provider.StrokeMapperProvider


object DefaultMapperProvider {

    private val PROVIDER_MAP = TypedMapperProviderMap()

    operator fun <T> get(aes: org.jetbrains.letsPlot.core.plot.base.Aes<T>): MapperProvider<T> {
        return PROVIDER_MAP[aes]
    }

    /**
     * For tests
     */
    internal fun hasDefault(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): Boolean {
        return PROVIDER_MAP.containsKey(aes)
    }

    private class TypedMapperProviderMap internal constructor() {

        private var myMap: MutableMap<org.jetbrains.letsPlot.core.plot.base.Aes<*>, MapperProvider<*>> = HashMap()

        init {
            for (aes in org.jetbrains.letsPlot.core.plot.base.Aes.allPositional()) {
                put(aes, NUMERIC_UNDEFINED)
            }

            this.put(X, NUMERIC_IDENTITY)
            this.put(Y, NUMERIC_IDENTITY)

            this.put(Z, NUMERIC_IDENTITY)
            this.put(YMIN, NUMERIC_IDENTITY)
            this.put(YMAX, NUMERIC_IDENTITY)
            this.put(COLOR, createColorMapperProvider())
            this.put(FILL, createColorMapperProvider())
            this.put(PAINT_A, createColorMapperProvider())
            this.put(PAINT_B, createColorMapperProvider())
            this.put(PAINT_C, createColorMapperProvider())
            this.put(ALPHA, AlphaMapperProvider.DEFAULT)
            this.put(SHAPE, createWithDiscreteOutput(ShapeMapper.allShapes(), ShapeMapper.NA_VALUE))
            this.put(LINETYPE, createWithDiscreteOutput(LineTypeMapper.allLineTypes(), LineTypeMapper.NA_VALUE))

            this.put(SIZE, SizeMapperProvider.DEFAULT)
            this.put(STROKE, StrokeMapperProvider.DEFAULT)
            this.put(LINEWIDTH, LinewidthMapperProvider.DEFAULT)
            this.put(STACKSIZE, NUMERIC_IDENTITY)
            this.put(WIDTH, NUMERIC_IDENTITY)
            this.put(HEIGHT, NUMERIC_IDENTITY)
            this.put(WEIGHT, NUMERIC_IDENTITY)
            this.put(BINWIDTH, NUMERIC_IDENTITY)
            this.put(VIOLINWIDTH, NUMERIC_IDENTITY)
            this.put(INTERCEPT, NUMERIC_IDENTITY)
            this.put(SLOPE, NUMERIC_IDENTITY)
            this.put(XINTERCEPT, NUMERIC_IDENTITY)
            this.put(YINTERCEPT, NUMERIC_IDENTITY)
            this.put(LOWER, NUMERIC_IDENTITY)
            this.put(MIDDLE, NUMERIC_IDENTITY)
            this.put(UPPER, NUMERIC_IDENTITY)
            this.put(SAMPLE, NUMERIC_IDENTITY)
            this.put(QUANTILE, NUMERIC_IDENTITY)

            this.put(MAP_ID, createObjectIdentity())
            this.put(FRAME, createStringIdentity())

            this.put(SPEED, NUMERIC_IDENTITY)
            this.put(FLOW, NUMERIC_IDENTITY)

            this.put(XMIN, NUMERIC_IDENTITY)
            this.put(XMAX, NUMERIC_IDENTITY)
            this.put(XEND, NUMERIC_IDENTITY)
            this.put(YEND, NUMERIC_IDENTITY)

            this.put(LABEL, createObjectIdentity())
            this.put(FAMILY, createStringIdentity())
            this.put(FONTFACE, createStringIdentity())
            this.put(LINEHEIGHT, NUMERIC_IDENTITY)

            // text horizontal justification (numbers [0..1] or predefined strings, DOUBLE_CVT; not positional)
            this.put(HJUST, createObjectIdentity())

            // text vertical justification (numbers [0..1] or predefined strings, not positional)
            this.put(VJUST, createObjectIdentity())
            this.put(ANGLE, NUMERIC_IDENTITY)

            this.put(SLICE, NUMERIC_IDENTITY)
            this.put(EXPLODE, NUMERIC_IDENTITY)
        }

        internal operator fun <T> get(aes: org.jetbrains.letsPlot.core.plot.base.Aes<T>): MapperProvider<T> {
            @Suppress("UNCHECKED_CAST")
            return myMap[aes] as MapperProvider<T>
        }

        private fun <T> put(aes: org.jetbrains.letsPlot.core.plot.base.Aes<T>, value: MapperProvider<T>) {
            myMap[aes] = value
        }

        internal fun containsKey(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): Boolean {
            return myMap.containsKey(aes)
        }

        companion object {
            // For most numeric (positional) aesthetics the initial mapper is UNDEFINED mapper as we don't yet know the range of positional aesthetics.
            private val NUMERIC_UNDEFINED: MapperProvider<Double> = object : MapperProvider<Double> {
                override fun createDiscreteMapper(discreteTransform: DiscreteTransform): ScaleMapper<Double> {
                    return Mappers.NUMERIC_UNDEFINED
                }

                override fun createContinuousMapper(
                    domain: DoubleSpan,
                    trans: ContinuousTransform
                ): GuideMapper<Double> {
                    return GuideMappers.NUMERIC_UNDEFINED
                }
            }

            private val NUMERIC_IDENTITY: MapperProvider<Double> = object : MapperProvider<Double> {
                override fun createDiscreteMapper(discreteTransform: DiscreteTransform): ScaleMapper<Double> {
                    return Mappers.IDENTITY
                }

                override fun createContinuousMapper(
                    domain: DoubleSpan,
                    trans: ContinuousTransform
                ): GuideMapper<Double> {
                    return GuideMappers.IDENTITY
                }
            }
        }
    }
}
