/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat
import org.jetbrains.letsPlot.commons.formatting.string.StringFormat.FormatType.DATETIME_FORMAT
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Colors
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.scale.transform.DateTimeBreaksGen
import org.jetbrains.letsPlot.core.plot.base.scale.transform.TimeBreaksGen
import org.jetbrains.letsPlot.core.plot.base.scale.transform.Transforms
import org.jetbrains.letsPlot.core.plot.builder.scale.*
import org.jetbrains.letsPlot.core.plot.builder.scale.mapper.ShapeMapper
import org.jetbrains.letsPlot.core.plot.builder.scale.provider.*
import org.jetbrains.letsPlot.core.spec.Option.Scale.AES
import org.jetbrains.letsPlot.core.spec.Option.Scale.BREAKS
import org.jetbrains.letsPlot.core.spec.Option.Scale.CHROMA
import org.jetbrains.letsPlot.core.spec.Option.Scale.COLORS
import org.jetbrains.letsPlot.core.spec.Option.Scale.DIRECTION
import org.jetbrains.letsPlot.core.spec.Option.Scale.END
import org.jetbrains.letsPlot.core.spec.Option.Scale.EXPAND
import org.jetbrains.letsPlot.core.spec.Option.Scale.FORMAT
import org.jetbrains.letsPlot.core.spec.Option.Scale.GUIDE
import org.jetbrains.letsPlot.core.spec.Option.Scale.HIGH
import org.jetbrains.letsPlot.core.spec.Option.Scale.HUE_RANGE
import org.jetbrains.letsPlot.core.spec.Option.Scale.LABELS
import org.jetbrains.letsPlot.core.spec.Option.Scale.LIMITS
import org.jetbrains.letsPlot.core.spec.Option.Scale.LOW
import org.jetbrains.letsPlot.core.spec.Option.Scale.LUMINANCE
import org.jetbrains.letsPlot.core.spec.Option.Scale.MAX_SIZE
import org.jetbrains.letsPlot.core.spec.Option.Scale.MID
import org.jetbrains.letsPlot.core.spec.Option.Scale.MIDPOINT
import org.jetbrains.letsPlot.core.spec.Option.Scale.MapperKind.COLOR_BREWER
import org.jetbrains.letsPlot.core.spec.Option.Scale.MapperKind.COLOR_CMAP
import org.jetbrains.letsPlot.core.spec.Option.Scale.MapperKind.COLOR_GRADIENT
import org.jetbrains.letsPlot.core.spec.Option.Scale.MapperKind.COLOR_GRADIENT2
import org.jetbrains.letsPlot.core.spec.Option.Scale.MapperKind.COLOR_GRADIENTN
import org.jetbrains.letsPlot.core.spec.Option.Scale.MapperKind.COLOR_GREY
import org.jetbrains.letsPlot.core.spec.Option.Scale.MapperKind.COLOR_HUE
import org.jetbrains.letsPlot.core.spec.Option.Scale.MapperKind.IDENTITY
import org.jetbrains.letsPlot.core.spec.Option.Scale.MapperKind.SIZE_AREA
import org.jetbrains.letsPlot.core.spec.Option.Scale.NAME
import org.jetbrains.letsPlot.core.spec.Option.Scale.NA_VALUE
import org.jetbrains.letsPlot.core.spec.Option.Scale.OUTPUT_VALUES
import org.jetbrains.letsPlot.core.spec.Option.Scale.PALETTE
import org.jetbrains.letsPlot.core.spec.Option.Scale.PALETTE_TYPE
import org.jetbrains.letsPlot.core.spec.Option.Scale.RANGE
import org.jetbrains.letsPlot.core.spec.Option.Scale.SCALE_MAPPER_KIND
import org.jetbrains.letsPlot.core.spec.Option.Scale.SHAPE_SOLID
import org.jetbrains.letsPlot.core.spec.Option.Scale.START
import org.jetbrains.letsPlot.core.spec.Option.Scale.START_HUE
import org.jetbrains.letsPlot.core.spec.Option.Scale.Viridis
import org.jetbrains.letsPlot.core.spec.Option.TransformName
import org.jetbrains.letsPlot.core.spec.conversion.AesOptionConversion
import org.jetbrains.letsPlot.core.spec.conversion.TypedContinuousIdentityMappers
import org.jetbrains.letsPlot.core.spec.Option

/**
 * @param <T> - target aesthetic type of the configured scale
 */
class ScaleConfig<T> constructor(
    val aes: Aes<T>,
    options: Map<String, Any>
) : OptionsAccessor(options) {

    private fun enforceDiscreteDomain(): Boolean {
        // Discrete domain if
        //   - aes mapping is annotated with 'is_discrete';
        //   - scale_x_discrete, scale_y_discrete.
        return getBoolean(Option.Scale.DISCRETE_DOMAIN)
    }

    fun createMapperProvider(): MapperProvider<T> {
        var mapperProvider: MapperProvider<*> = DefaultMapperProvider[aes]

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
                mapperProvider = DefaultMapperProviderUtil.createWithDiscreteOutput(
                    ShapeMapper.hollowShapes(), ShapeMapper.NA_VALUE
                )
            }
        } else if (aes == Aes.ALPHA && has(RANGE)) {
            mapperProvider = AlphaMapperProvider(getRange(RANGE), (naValue as Double))
        } else if (aes == Aes.SIZE && has(RANGE)) {
            mapperProvider = SizeMapperProvider(getRange(RANGE), (naValue as Double))
        } else if (aes == Aes.LINEWIDTH && has(RANGE)) {
            mapperProvider = LinewidthMapperProvider(getRange(RANGE), (naValue as Double))
        } else if (aes == Aes.STROKE && has(RANGE)) {
            mapperProvider = StrokeMapperProvider(getRange(RANGE), (naValue as Double))
        }

        val scaleMapperKind = getString(SCALE_MAPPER_KIND) ?: if (
            !has(OUTPUT_VALUES) &&
            enforceDiscreteDomain() &&
            Aes.isColor(aes)
        ) {
            // Default palette type for discrete colors
            COLOR_BREWER
        } else {
            null
        }

        when (scaleMapperKind) {
            null -> {} // Nothing
            IDENTITY ->
                mapperProvider = createIdentityMapperProvider(aes, naValue)

            COLOR_GRADIENT ->
                mapperProvider = ColorGradientMapperProvider(
                    getColor(LOW),
                    getColor(HIGH),
                    (naValue as Color)
                )

            COLOR_GRADIENT2 ->
                mapperProvider = ColorGradient2MapperProvider(
                    getColor(LOW),
                    getColor(MID),
                    getColor(HIGH),
                    getDouble(MIDPOINT), naValue as Color
                )

            COLOR_GRADIENTN ->
                mapperProvider = ColorGradientnMapperProvider(
                    getStringList(COLORS).map(Colors::parseColor),
                    naValue as Color
                )

            COLOR_HUE ->
                mapperProvider = ColorHueMapperProvider(
                    getDoubleList(HUE_RANGE),
                    getDouble(CHROMA),
                    getDouble(LUMINANCE),
                    getDouble(START_HUE),
                    getDouble(DIRECTION), naValue as Color
                )

            COLOR_GREY ->
                mapperProvider = GreyscaleLightnessMapperProvider(
                    getDouble(START),
                    getDouble(END),
                    naValue as Color
                )

            COLOR_BREWER ->
                mapperProvider = ColorBrewerMapperProvider(
                    getString(PALETTE_TYPE),
                    get(PALETTE),
                    getDouble(DIRECTION),
                    naValue as Color
                )

            COLOR_CMAP ->
                mapperProvider = ColormapMapperProvider(
                    getString(Viridis.CMAP_NAME),
                    getDouble(Viridis.ALPHA),
                    getDouble(Viridis.BEGIN),
                    getDouble(Viridis.END),
                    getDouble(Viridis.DIRECTION),
                    naValue as Color
                )

            SIZE_AREA ->
                mapperProvider = SizeAreaMapperProvider(
                    getDouble(MAX_SIZE),
                    naValue as Double
                )

            else ->
                throw IllegalArgumentException("Aes '" + aes.name + "' - unexpected scale mapper kind: '" + scaleMapperKind + "'")
        }

        @Suppress("UNCHECKED_CAST")
        return mapperProvider as MapperProvider<T>
    }

    fun createScaleProvider(): ScaleProvider {
        return createScaleProviderBuilder().build()
    }

    private fun createScaleProviderBuilder(): ScaleProviderBuilder<T> {
        val discreteDomain = enforceDiscreteDomain()
        val reverse = getBoolean(Option.Scale.DISCRETE_DOMAIN_REVERSE)

        val b = ScaleProviderBuilder(aes)
        b.discreteDomain(discreteDomain)
        b.discreteDomainReverse(reverse)

        if (getBoolean(Option.Scale.DATE_TIME)) {
            val dateTimeFormatter = getString(FORMAT)?.let { pattern ->
                val stringFormat = StringFormat.forOneArg(pattern, type = DATETIME_FORMAT)
                return@let { value: Any -> stringFormat.format(value) }
            }
            b.breaksGenerator(DateTimeBreaksGen(dateTimeFormatter))
        } else if (getBoolean(Option.Scale.TIME)) {
            b.breaksGenerator(TimeBreaksGen())
        } else if (!discreteDomain && has(Option.Scale.CONTINUOUS_TRANSFORM)) {
            val transformName = getStringSafe(Option.Scale.CONTINUOUS_TRANSFORM)
            val transform = when (transformName.lowercase()) {
                TransformName.IDENTITY -> Transforms.IDENTITY
                TransformName.LOG10 -> Transforms.LOG10
                TransformName.REVERSE -> Transforms.REVERSE
                TransformName.SQRT -> Transforms.SQRT
                else -> throw IllegalArgumentException(
                    "Unknown transform name: '$transformName'. Supported: ${
                        listOf(
                            TransformName.IDENTITY,
                            TransformName.LOG10,
                            TransformName.REVERSE,
                            TransformName.SQRT
                        ).joinToString(transform = { "'$it'" })
                    }."
                )
            }
            b.continuousTransform(transform)
        }

        if (aes in listOf<Aes<*>>(Aes.X, Aes.Y) && has(Option.Scale.POSITION)) {
            b.axisPosition = axisPosition(aes)
        }

        return applyCommons(b)
    }

    private fun applyCommons(b: ScaleProviderBuilder<T>): ScaleProviderBuilder<T> {
        if (has(NAME)) {
            b.name(getString(NAME)!!)
        }
        if (has(BREAKS)) {
            b.breaks(getList(BREAKS).mapNotNull { it })
        }
        if (has(LABELS)) {
            b.labels(getStringList(LABELS))
        } else {
            // Skip format is labels are defined
            b.labelFormat(getString(FORMAT))
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

    fun getGuideOptions(): GuideConfig {
        return GuideConfig.create(get(GUIDE)!!)
    }

    private fun axisPosition(axis: Aes<*>): AxisPosition {
        val s = getStringSafe(Option.Scale.POSITION)
        return when (s.trim().lowercase()) {
            Option.Scale.POSITION_L -> AxisPosition.LEFT
            Option.Scale.POSITION_R -> AxisPosition.RIGHT
            Option.Scale.POSITION_T -> AxisPosition.TOP
            Option.Scale.POSITION_B -> AxisPosition.BOTTOM
            Option.Scale.POSITION_BOTH -> {
                if (axis == Aes.X) AxisPosition.TB
                else AxisPosition.LR
            }

            else -> throw IllegalArgumentException("'${Option.Scale.POSITION}' - unexpected value: '$s'. Valid values: left|right|top|bottom|both.")
        }
    }

    companion object {

        fun aesOrFail(options: Map<String, Any>): Aes<*> {
            val accessor = OptionsAccessor(options)
            require(accessor.has(AES)) { "Required parameter '$AES' is missing" }
            return Option.Mapping.toAes(accessor.getStringSafe(AES))
        }

        fun <T> createIdentityMapperProvider(aes: Aes<T>, naValue: T): MapperProvider<T> {
            // There is an option value converter for every AES (which can be used as discrete identity mapper)
            val cvt = AesOptionConversion.getConverter(aes)
            val discreteMapperProvider =
                IdentityDiscreteMapperProvider(cvt/*, naValue*/)

            // For some AES there is also a continuous identity mapper
            if (TypedContinuousIdentityMappers.contain(aes)) {
                val continuousMapper = TypedContinuousIdentityMappers[aes]
                return IdentityMapperProvider(
                    discreteMapperProvider,
                    ScaleMapper.wrap(continuousMapper, naValue)
                )
            }

            return discreteMapperProvider
        }
    }
}
