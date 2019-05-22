package jetbrains.datalore.visualization.plot.builder.scale

import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.Aes.Companion.ALPHA
import jetbrains.datalore.visualization.plot.base.Aes.Companion.ANGLE
import jetbrains.datalore.visualization.plot.base.Aes.Companion.COLOR
import jetbrains.datalore.visualization.plot.base.Aes.Companion.FAMILY
import jetbrains.datalore.visualization.plot.base.Aes.Companion.FILL
import jetbrains.datalore.visualization.plot.base.Aes.Companion.FLOW
import jetbrains.datalore.visualization.plot.base.Aes.Companion.FONTFACE
import jetbrains.datalore.visualization.plot.base.Aes.Companion.FRAME
import jetbrains.datalore.visualization.plot.base.Aes.Companion.HEIGHT
import jetbrains.datalore.visualization.plot.base.Aes.Companion.HJUST
import jetbrains.datalore.visualization.plot.base.Aes.Companion.INTERCEPT
import jetbrains.datalore.visualization.plot.base.Aes.Companion.LABEL
import jetbrains.datalore.visualization.plot.base.Aes.Companion.LINETYPE
import jetbrains.datalore.visualization.plot.base.Aes.Companion.LOWER
import jetbrains.datalore.visualization.plot.base.Aes.Companion.MAP_ID
import jetbrains.datalore.visualization.plot.base.Aes.Companion.MIDDLE
import jetbrains.datalore.visualization.plot.base.Aes.Companion.SHAPE
import jetbrains.datalore.visualization.plot.base.Aes.Companion.SIZE
import jetbrains.datalore.visualization.plot.base.Aes.Companion.SLOPE
import jetbrains.datalore.visualization.plot.base.Aes.Companion.SPEED
import jetbrains.datalore.visualization.plot.base.Aes.Companion.UPPER
import jetbrains.datalore.visualization.plot.base.Aes.Companion.VJUST
import jetbrains.datalore.visualization.plot.base.Aes.Companion.WEIGHT
import jetbrains.datalore.visualization.plot.base.Aes.Companion.WIDTH
import jetbrains.datalore.visualization.plot.base.Aes.Companion.X
import jetbrains.datalore.visualization.plot.base.Aes.Companion.XEND
import jetbrains.datalore.visualization.plot.base.Aes.Companion.XINTERCEPT
import jetbrains.datalore.visualization.plot.base.Aes.Companion.XMAX
import jetbrains.datalore.visualization.plot.base.Aes.Companion.XMIN
import jetbrains.datalore.visualization.plot.base.Aes.Companion.Y
import jetbrains.datalore.visualization.plot.base.Aes.Companion.YEND
import jetbrains.datalore.visualization.plot.base.Aes.Companion.YINTERCEPT
import jetbrains.datalore.visualization.plot.base.Aes.Companion.YMAX
import jetbrains.datalore.visualization.plot.base.Aes.Companion.YMIN
import jetbrains.datalore.visualization.plot.base.Aes.Companion.Z
import jetbrains.datalore.visualization.plot.base.data.DataFrame
import jetbrains.datalore.visualization.plot.base.scale.Transform
import jetbrains.datalore.visualization.plot.builder.scale.DefaultMapperProviderUtil.createColor
import jetbrains.datalore.visualization.plot.builder.scale.DefaultMapperProviderUtil.createObjectIdentityDiscrete
import jetbrains.datalore.visualization.plot.builder.scale.DefaultMapperProviderUtil.createStringIdentity
import jetbrains.datalore.visualization.plot.builder.scale.DefaultMapperProviderUtil.createWithDiscreteOutput
import jetbrains.datalore.visualization.plot.builder.scale.mapper.GuideMappers
import jetbrains.datalore.visualization.plot.builder.scale.mapper.LineTypeMapper
import jetbrains.datalore.visualization.plot.builder.scale.mapper.ShapeMapper
import jetbrains.datalore.visualization.plot.builder.scale.provider.AlphaMapperProvider
import jetbrains.datalore.visualization.plot.builder.scale.provider.SizeMapperProvider

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
            this.put(X, NUMERIC_IDENTITY)
            this.put(Y, NUMERIC_IDENTITY)

            this.put(Z, NUMERIC_IDENTITY)
            this.put(YMIN, NUMERIC_IDENTITY)
            this.put(YMAX, NUMERIC_IDENTITY)
            this.put(COLOR, createColor())
            this.put(FILL, createColor())
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

            this.put(MAP_ID, createObjectIdentityDiscrete(MAP_ID))
            this.put(FRAME, createStringIdentity(FRAME))

            this.put(SPEED, NUMERIC_IDENTITY)
            this.put(FLOW, NUMERIC_IDENTITY)

            this.put(XMIN, NUMERIC_IDENTITY)
            this.put(XMAX, NUMERIC_IDENTITY)
            this.put(XEND, NUMERIC_IDENTITY)
            this.put(YEND, NUMERIC_IDENTITY)

            this.put(LABEL, createStringIdentity(LABEL))
            this.put(FAMILY, createStringIdentity(FAMILY))
            this.put(FONTFACE, createStringIdentity(FONTFACE))

            // text horizontal justification (numbers [0..1] or predefined strings, DOUBLE_CVT; not positional)
            this.put(HJUST, createObjectIdentityDiscrete(HJUST))

            // text vertical justification (numbers [0..1] or predefined strings, not positional)
            this.put(VJUST, createObjectIdentityDiscrete(VJUST))
            this.put(ANGLE, NUMERIC_IDENTITY)
        }

        internal operator fun <T> get(aes: Aes<T>): MapperProvider<T> {
            // Safe cast if 'put' is used responsibly.
            return myMap[aes] as MapperProvider<T>
        }

        internal fun <T> put(aes: Aes<T>, value: MapperProvider<T>): MapperProvider<T>? {
            // Used responsibly, private access
            return myMap.put(aes, value) as MapperProvider<T>?
        }

        internal fun containsKey(aes: Aes<*>): Boolean {
            return myMap.containsKey(aes)
        }

        companion object {
            // For most of numeric (positional) aesthetics the initial mapper is IDENTITY mapper as we don't yet know the range of positional aesthetics.
            private val NUMERIC_IDENTITY: MapperProvider<Double> = object : MapperProvider<Double> {
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
