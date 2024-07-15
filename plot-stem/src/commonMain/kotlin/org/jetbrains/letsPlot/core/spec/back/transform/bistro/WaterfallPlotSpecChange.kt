/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro

import org.jetbrains.letsPlot.core.spec.*
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.Option.Waterfall
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.OptionsUtil
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.TooltipsOptions
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.tooltips
import org.jetbrains.letsPlot.core.spec.conversion.LineTypeOptionConverter
import org.jetbrains.letsPlot.core.spec.transform.SpecChange
import org.jetbrains.letsPlot.core.spec.transform.SpecChangeContext
import org.jetbrains.letsPlot.core.spec.transform.SpecSelector

class WaterfallPlotSpecChange : SpecChange {
    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
        val waterfallPlotSpec = buildWaterfallPlotSpec(spec)

        // Set layers
        spec[Option.Plot.LAYERS] = waterfallPlotSpec.get(Option.Plot.LAYERS) ?: error("Missing layers in waterfall plot")

        // Merge scales
        val waterfallScales = waterfallPlotSpec.getList(Option.Plot.SCALES) ?: error("Missing scales in waterfall plot")
        val plotScales = spec.getList(Option.Plot.SCALES) ?: emptyList<Any>()
        spec[Option.Plot.SCALES] = (waterfallScales + plotScales).toMutableList()

        spec.remove("bistro")
    }

    private fun buildWaterfallPlotSpec(plotSpec: MutableMap<String, Any>): Map<String, Any> {
        val bistroSpec = plotSpec.getMap(Option.Plot.BISTRO) ?: error("'bistro' not found in PlotSpec")
        val waterfallPlotOptionsBuilder = WaterfallPlotOptionsBuilder(
            data = plotSpec.getMap(Option.PlotBase.DATA) ?: emptyMap<Any, Any>(),
            x = bistroSpec.getString(Waterfall.X),
            y = bistroSpec.getString(Waterfall.Y),
            color = bistroSpec.getString(Waterfall.COLOR) ?: WaterfallPlotOptionsBuilder.DEF_COLOR,
            fill = bistroSpec.getString(Waterfall.FILL) ?: WaterfallPlotOptionsBuilder.FLOW_TYPE_COLOR_VALUE,
            size = bistroSpec.getDouble(Waterfall.SIZE) ?: WaterfallPlotOptionsBuilder.DEF_SIZE,
            alpha = bistroSpec.getDouble(Waterfall.ALPHA),
            lineType = bistroSpec.read(Waterfall.LINE_TYPE),
            width = bistroSpec.getDouble(Waterfall.WIDTH) ?: WaterfallPlotOptionsBuilder.DEF_WIDTH,
            showLegend = bistroSpec.getBool(Waterfall.SHOW_LEGEND) ?: WaterfallPlotOptionsBuilder.DEF_SHOW_LEGEND,
            tooltipsOptions = readBoxTooltipsOptions(bistroSpec),
            calcTotal = bistroSpec.getBool(Waterfall.CALCULATE_TOTAL) ?: WaterfallPlotOptionsBuilder.DEF_CALC_TOTAL,
            totalTitle = bistroSpec.getString(Waterfall.TOTAL_TITLE),
            sortedValue = bistroSpec.getBool(Waterfall.SORTED_VALUE) ?: WaterfallPlotOptionsBuilder.DEF_SORTED_VALUE,
            threshold = bistroSpec.getDouble(Waterfall.THRESHOLD),
            maxValues = bistroSpec.getInt(Waterfall.MAX_VALUES),
            hLineOptions = readElementLineOptions(bistroSpec, Waterfall.H_LINE, WaterfallPlotOptionsBuilder.DEF_H_LINE),
            hLineOnTop = bistroSpec.getBool(Waterfall.H_LINE_ON_TOP) ?: WaterfallPlotOptionsBuilder.DEF_H_LINE_ON_TOP,
            connectorOptions = readElementLineOptions(bistroSpec, Waterfall.CONNECTOR, WaterfallPlotOptionsBuilder.DEF_CONNECTOR),
            labelOptions = readElementTextOptions(bistroSpec, Waterfall.LABEL, WaterfallPlotOptionsBuilder.DEF_LABEL),
            labelFormat = bistroSpec.getString(Waterfall.LABEL_FORMAT)
        )
        val waterfallPlotOptions = waterfallPlotOptionsBuilder.build()
        return OptionsUtil.toSpec(waterfallPlotOptions)
    }

    private fun readBoxTooltipsOptions(bistroSpec: Map<String, Any>): TooltipsOptions {
        val tooltipsOptions = bistroSpec.getMap(Waterfall.TOOLTIPS) ?: WaterfallPlotOptionsBuilder.DEF_TOOLTIPS
        return tooltips {
            anchor = tooltipsOptions.getString(Option.Layer.TOOLTIP_ANCHOR)
            minWidth = tooltipsOptions.getDouble(Option.Layer.TOOLTIP_MIN_WIDTH)
            title = tooltipsOptions.getString(Option.Layer.TOOLTIP_TITLE)
            disableSplitting = tooltipsOptions.getBool(Option.Layer.DISABLE_SPLITTING)
            lines = tooltipsOptions.getList(Option.LinesSpec.LINES) as? List<String>?
            formats = (tooltipsOptions.getList(Option.LinesSpec.FORMATS) as? List<Map<String, String>>?)?.map { formatOptions ->
                TooltipsOptions.format {
                    field = formatOptions[Option.LinesSpec.Format.FIELD]
                    format = formatOptions[Option.LinesSpec.Format.FORMAT]
                }
            }
        }
    }

    private fun readElementLineOptions(
        bistroSpec: Map<String, Any>,
        option: String,
        defaults: WaterfallPlotOptionsBuilder.ElementLineOptions
    ): WaterfallPlotOptionsBuilder.ElementLineOptions {
        return bistroSpec.getMap(option)?.let { elementLineSpec ->
            defaults.merge(
                WaterfallPlotOptionsBuilder.ElementLineOptions(
                    color = elementLineSpec.getString(Option.Theme.Elem.COLOR),
                    size = elementLineSpec.getDouble(Option.Theme.Elem.SIZE),
                    lineType = elementLineSpec.read(Option.Theme.Elem.LINETYPE)?.let { LineTypeOptionConverter().apply(it) },
                    blank = elementLineSpec.getBool(Option.Theme.Elem.BLANK) ?: false
                )
            )
        } ?: defaults
    }

    private fun readElementTextOptions(
        bistroSpec: Map<String, Any>,
        option: String,
        defaults: WaterfallPlotOptionsBuilder.ElementTextOptions
    ): WaterfallPlotOptionsBuilder.ElementTextOptions {
        return bistroSpec.getMap(option)?.let { elementTextSpec ->
            defaults.merge(
                WaterfallPlotOptionsBuilder.ElementTextOptions(
                    color = elementTextSpec.getString(Option.Theme.Elem.COLOR),
                    family = elementTextSpec.getString(Option.Theme.Elem.FONT_FAMILY),
                    face = elementTextSpec.getString(Option.Theme.Elem.FONT_FACE),
                    size = elementTextSpec.getDouble(Option.Theme.Elem.SIZE),
                    angle = elementTextSpec.getDouble(Option.Theme.Elem.ANGLE),
                    hjust = elementTextSpec.getDouble(Option.Theme.Elem.HJUST),
                    vjust = elementTextSpec.getDouble(Option.Theme.Elem.VJUST),
                    blank = elementTextSpec.getBool(Option.Theme.Elem.BLANK) ?: false
                )
            )
        } ?: defaults
    }

    companion object {
        fun specSelector(): SpecSelector {
            return SpecSelector.root()
        }
    }
}