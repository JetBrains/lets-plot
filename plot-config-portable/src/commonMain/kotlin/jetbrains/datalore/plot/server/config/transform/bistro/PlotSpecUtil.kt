/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform.bistro

import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption
import jetbrains.datalore.plot.config.Option
import jetbrains.datalore.plot.config.Option.Mapping.toOption

object PlotSpecUtil {

    fun toPlotSpec(plot: PlotOptions): Map<String, Any> {
        return mapOf(
            Option.Meta.KIND to Option.Meta.Kind.PLOT,
            Option.PlotBase.DATA to plot.data,
            Option.PlotBase.MAPPING to plot.mappings,
            Option.Plot.LAYERS to serializeLayers(plot.layerOptions),
            Option.Plot.SCALES to serializeScales(plot.scaleOptions),
            Option.Plot.COORD to plot.coord?.let(this::serializeCoord),
            Option.Plot.THEME to plot.themeOptions?.let(this::serializeTheme),
            Option.Plot.SIZE to plot.size?.let(this::serializeSize),
            Option.Plot.TITLE to mapOf(Option.Plot.TITLE_TEXT to plot.title),
        ).filterNonNullValues()
    }

    private fun serializeLayers(layers: List<LayerOptions>): List<Map<String, Any?>> {
        val layerObjects = layers.map { layer ->
            val layerSpec = mutableMapOf<String, Any?>(
                Option.Layer.GEOM to layer.geom?.name?.lowercase(),
                Option.PlotBase.DATA to layer.data,
                Option.PlotBase.MAPPING to layer.mappings?.mapKeys { (aes, _) -> toOption(aes) },
                Option.Layer.STAT to layer.stat,
                Option.Layer.POS to layer.position,
                Option.Layer.SHOW_LEGEND to layer.showLegend,

                // geom specific options
                Option.Geom.Point.SIZE_UNIT to layer.sizeUnit,

                Option.Geom.Text.SIZE_UNIT to layer.sizeUnit,
                Option.Geom.Text.NA_TEXT to layer.naText,
                Option.Geom.Text.LABEL_FORMAT to layer.labelFormat,
            )

            layer.contants?.forEach { (aes, value) -> layerSpec[toOption(aes)] = value }

            layer.samplingOptions?.let { sampling ->
                layerSpec[Option.Layer.SAMPLING] = when (sampling.kind) {
                    Option.Sampling.NONE -> Option.Sampling.NONE
                    else -> mapOf(
                        Option.Meta.NAME to sampling.kind,
                        Option.Sampling.N to sampling.n,
                        Option.Sampling.MIN_SUB_SAMPLE to sampling.minSubsample,
                        Option.Sampling.SEED to sampling.seed
                    ).filterNonNullValues()
                }
            }

            layer.tooltipsOptions?.let { tooltips ->
                layerSpec[Option.Layer.TOOLTIPS] = mutableMapOf(
                    Option.Layer.TOOLTIP_ANCHOR to tooltips.anchor,
                    Option.Layer.TOOLTIP_COLOR to tooltips.color,
                    Option.Layer.TOOLTIP_MIN_WIDTH to tooltips.minWidth,
                    Option.Layer.TOOLTIP_FORMATS to tooltips.formats?.map { (field, format) ->
                        mapOf(
                            Option.TooltipFormat.FIELD to field,
                            Option.TooltipFormat.FORMAT to format
                        )
                    },
                    Option.Layer.TOOLTIP_LINES to tooltips.lines
                ).filterNonNullValues()
            }
            layerSpec.filterNonNullValues()
        }
        return layerObjects
    }

    private fun serializeScales(scaleOptions: List<ScaleOptions>): List<Map<String, Any?>> {
        return scaleOptions.map { scale ->
            mapOf<String, Any?>(
                Option.Scale.NAME to scale.name,
                Option.Scale.AES to scale.aes?.let(::toOption),
                Option.Scale.NA_VALUE to scale.naValue,
                Option.Scale.LIMITS to scale.limits,
                Option.Scale.BREAKS to scale.breaks,
                Option.Scale.LABELS to scale.labels,
                Option.Scale.EXPAND to scale.expand,
                Option.Scale.LOW to scale.low,
                Option.Scale.MID to scale.mid,
                Option.Scale.HIGH to scale.high,
                Option.Scale.MIDPOINT to scale.midpoint,
                Option.Scale.DISCRETE_DOMAIN to scale.isDiscrete,
                Option.Scale.DISCRETE_DOMAIN_REVERSE to scale.isReverse,
                Option.Scale.GUIDE to scale.guide,
                Option.Scale.SCALE_MAPPER_KIND to scale.mapperKind,
                Option.Scale.PALETTE to scale.palette
            ).filterNonNullValues()
        }
    }

    private fun serializeCoord(coordOptions: CoordOptions): Map<String, Any?> {
        return mutableMapOf<String, Any?>(
            Option.Meta.NAME to coordOptions.name,
            Option.Coord.RATIO to coordOptions.ratio,
            Option.Coord.X_LIM to coordOptions.xLim?.let(this::convertDoublePair),
            Option.Coord.Y_LIM to coordOptions.yLim?.let(this::convertDoublePair)
        ).filterNonNullValues()
    }

    private fun serializeTheme(themeOptions: ThemeOptions): Map<String, Any?> {
        return mutableMapOf<String, Any?>(
            ThemeOption.AXIS_TITLE to serializeThemeElement(themeOptions.axisTitle),
            ThemeOption.AXIS_LINE to serializeThemeElement(themeOptions.axisLine)
        ).filterNonNullValues()
    }

    private fun serializeThemeElement(element: ThemeOptions.Element?): Map<String, String> {
        return when (element) {
            ThemeOptions.ELEMENT_BLANK -> mapOf(Option.Meta.NAME to Option.Theme.ELEMENT_BLANK)
            else -> throw IllegalArgumentException("Only element_blank is supported")
        }
    }

    private fun serializeSize(size: Vector): Map<String, Any> {
        return mapOf(
            Option.Plot.WIDTH to size.x,
            Option.Plot.HEIGHT to size.y
        )
    }

    private fun convertDoublePair(value: Pair<Double, Double>): List<Double> {
        return listOf(value.first, value.second)
    }
}

@Suppress("UNCHECKED_CAST")
private fun Map<String, Any?>.filterNonNullValues(): Map<String, Any> {
    return filter { it.value != null } as Map<String, Any>
}
