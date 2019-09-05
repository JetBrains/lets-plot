package jetbrains.datalore.visualization.plot.config

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.scale.Mappers.nullable
import jetbrains.datalore.visualization.plot.base.scale.transform.DateTimeBreaksGen
import jetbrains.datalore.visualization.plot.base.scale.transform.Transforms
import jetbrains.datalore.visualization.plot.builder.scale.*
import jetbrains.datalore.visualization.plot.builder.scale.mapper.ShapeMapper
import jetbrains.datalore.visualization.plot.builder.scale.provider.*
import jetbrains.datalore.visualization.plot.config.Option.Scale.AES
import jetbrains.datalore.visualization.plot.config.Option.Scale.BREAKS
import jetbrains.datalore.visualization.plot.config.Option.Scale.CHROMA
import jetbrains.datalore.visualization.plot.config.Option.Scale.DIRECTION
import jetbrains.datalore.visualization.plot.config.Option.Scale.END
import jetbrains.datalore.visualization.plot.config.Option.Scale.EXPAND
import jetbrains.datalore.visualization.plot.config.Option.Scale.GUIDE
import jetbrains.datalore.visualization.plot.config.Option.Scale.HIGH
import jetbrains.datalore.visualization.plot.config.Option.Scale.HUE_RANGE
import jetbrains.datalore.visualization.plot.config.Option.Scale.LABELS
import jetbrains.datalore.visualization.plot.config.Option.Scale.LIMITS
import jetbrains.datalore.visualization.plot.config.Option.Scale.LOW
import jetbrains.datalore.visualization.plot.config.Option.Scale.LUMINANCE
import jetbrains.datalore.visualization.plot.config.Option.Scale.MAX_SIZE
import jetbrains.datalore.visualization.plot.config.Option.Scale.MID
import jetbrains.datalore.visualization.plot.config.Option.Scale.MIDPOINT
import jetbrains.datalore.visualization.plot.config.Option.Scale.NAME
import jetbrains.datalore.visualization.plot.config.Option.Scale.NA_VALUE
import jetbrains.datalore.visualization.plot.config.Option.Scale.OUTPUT_VALUES
import jetbrains.datalore.visualization.plot.config.Option.Scale.PALETTE
import jetbrains.datalore.visualization.plot.config.Option.Scale.PALETTE_TYPE
import jetbrains.datalore.visualization.plot.config.Option.Scale.RANGE
import jetbrains.datalore.visualization.plot.config.Option.Scale.SCALE_MAPPER_KIND
import jetbrains.datalore.visualization.plot.config.Option.Scale.SHAPE_SOLID
import jetbrains.datalore.visualization.plot.config.Option.Scale.START
import jetbrains.datalore.visualization.plot.config.Option.Scale.START_HUE
import jetbrains.datalore.visualization.plot.config.aes.AesOptionConversion
import jetbrains.datalore.visualization.plot.config.aes.TypedContinuousIdentityMappers

/**
 * @param <T> - target aesthetic type of the configured scale
 */
class ScaleConfig<T>(options: Map<*, *>) : OptionsAccessor(options) {

    @Suppress("UNCHECKED_CAST")
    val aes: Aes<T> = aesOrFail(options) as Aes<T>


    fun createScaleProvider(): ScaleProvider<T> {
        return createScaleProviderBuilder().build()
    }

    private fun createScaleProviderBuilder(): ScaleProviderBuilder<T> {
        var mapperProvider: MapperProvider<*>? = null

        val naValue: T = when {
            has(NA_VALUE) -> getValue(aes, NA_VALUE)!!
            else -> DefaultNaValue[aes]
        }

        // all 'manual' scales
        if (has(OUTPUT_VALUES)) {
            val outputValues = getList(OUTPUT_VALUES)
            val mapperOutputValues = AesOptionConversion.applyToList(aes, outputValues)
            mapperProvider = DefaultMapperProviderUtil.createWithDiscreteOutput(mapperOutputValues, naValue)
        }

        if (aes == Aes.SHAPE) {
            val solid = get(SHAPE_SOLID)
            // False - show only hollow shapes, otherwise - all (default)
            if (solid is Boolean && solid == false) {
                mapperProvider = DefaultMapperProviderUtil.createWithDiscreteOutput(ShapeMapper.hollowShapes(), ShapeMapper.NA_VALUE)
            }
        } else if (aes == Aes.ALPHA && has(RANGE)) {
            mapperProvider = AlphaMapperProvider(getRange(RANGE), (naValue as Double))
        } else if (aes == Aes.SIZE && has(RANGE)) {
            mapperProvider = SizeMapperProvider(getRange(RANGE), (naValue as Double))
        }

        if (has(SCALE_MAPPER_KIND)) {
            when (val mapperKind = getString(SCALE_MAPPER_KIND)) {
                IDENTITY ->
                    mapperProvider = createIdentityMapperProvider(aes, naValue)
                COLOR_GRADIENT ->
                    mapperProvider = ColorGradientMapperProvider(getColor(LOW), getColor(HIGH), (naValue as Color))
                COLOR_GRADIENT2 ->
                    mapperProvider = ColorGradient2MapperProvider(
                            getColor(LOW),
                            getColor(MID),
                            getColor(HIGH),
                            getDouble(MIDPOINT), naValue as Color)
                COLOR_HUE ->
                    mapperProvider = ColorHueMapperProvider(
                            getDoubleList(HUE_RANGE),
                            getDouble(CHROMA),
                            getDouble(LUMINANCE),
                            getDouble(START_HUE),
                            getDouble(DIRECTION), naValue as Color
                    )
                COLOR_GREY ->
                    mapperProvider = ColorLuminanceMapperProvider(getDouble(START), getDouble(END), naValue as Color)
                COLOR_BREWER ->
                    mapperProvider = ColorBrewerMapperProvider(getString(PALETTE_TYPE), get(PALETTE), getDouble(DIRECTION), naValue as Color)
                SIZE_AREA ->
                    mapperProvider = SizeAreaMapperProvider(getDouble(MAX_SIZE), naValue as Double)
                else ->
                    throw IllegalArgumentException("Aes '" + aes.name + "' - unexpected scale mapper kind: '" + mapperKind + "'")
            }
        }

        val b = ScaleProviderBuilder(aes)
        if (mapperProvider != null) {
            @Suppress("UNCHECKED_CAST")
            b.mapperProvider(mapperProvider as MapperProvider<T>)
        }

        // used in scale_x_discrete, scale_y_discrete
        val discreteDomain = getBoolean(Option.Scale.DISCRETE_DOMAIN)
        b.discreteDomain(discreteDomain)

        if (getBoolean(Option.Scale.DATE_TIME)) {
            // ToDo: add support for 'date_breaks', 'date_labels' (see: http://docs.ggplot2.org/current/scale_date.html)
            b.transform(Transforms.identityWithBreaksGen(DateTimeBreaksGen()))
        } else if (!discreteDomain && has(Option.Scale.CONTINUOUS_TRANSFORM)) {
            val transformConfig = ScaleTransformConfig.create(get(Option.Scale.CONTINUOUS_TRANSFORM)!!)
            b.transform(transformConfig.transform)
        }

        return applyCommons(b)
    }

    private fun applyCommons(b: ScaleProviderBuilder<T>): ScaleProviderBuilder<T> {
        if (has(NAME)) {
            b.name(getString(NAME)!!)
        }
        if (has(BREAKS)) {
            b.breaks(getList(BREAKS))
        }
        if (has(LABELS)) {
            b.labels(getStringList(LABELS))
        }
        if (has(EXPAND)) {
            val list = getList(EXPAND)
            if (list.isNotEmpty()) {
                val multiplicativeExpand = list[0] as Number
                b.multiplicativeExpand(multiplicativeExpand.toDouble())
                if (list.size > 1) {
                    val additiveExpand = list[1] as Number
                    b.additiveExpand(additiveExpand.toDouble())
                }
            }
        }
        if (has(LIMITS)) {
            b.limits(this.getList(LIMITS))
        }

        return b
    }

    fun hasGuideOptions(): Boolean {
        return has(GUIDE)
    }

    fun gerGuideOptions(): GuideConfig {
        return GuideConfig.create(get(GUIDE)!!)
    }

    companion object {
        private const val IDENTITY = "identity"
        private const val COLOR_GRADIENT = "color_gradient"
        private const val COLOR_GRADIENT2 = "color_gradient2"
        private const val COLOR_HUE = "color_hue"
        private const val COLOR_GREY = "color_grey"
        private const val COLOR_BREWER = "color_brewer"
        private const val SIZE_AREA = "size_area"

        fun aesOrFail(options: Map<*, *>): Aes<*> {
            val accessor = OptionsAccessor(options)
            checkArgument(accessor.has(AES), "Required parameter '$AES' is missing")
            return Option.Mapping.toAes(accessor.getString(AES)!!)
        }

        fun <T> createIdentityMapperProvider(aes: Aes<T>, naValue: T): MapperProvider<T> {
            // There is an option value converter for every AES (which can be used as discrete identity mapper)
            val cvt = AesOptionConversion.getConverter(aes)
            val discreteMapperProvider = IdentityDiscreteMapperProvider(cvt, naValue)

            // For some AES there is also a continuous identity mapper
            if (TypedContinuousIdentityMappers.contain(aes)) {
                val continuousMapper = TypedContinuousIdentityMappers[aes]
                return IdentityMapperProvider(
                        discreteMapperProvider,
                        nullable(continuousMapper, naValue))
            }

            return discreteMapperProvider
        }
    }
}
