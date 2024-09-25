/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro

import org.jetbrains.letsPlot.core.spec.*
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.Option.Waterfall
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.Option.Waterfall.Keyword.COLOR_FLOW_TYPE
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.Option.Waterfall.Keyword.TOOLTIP_DETAILED
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.DEF_ABSOLUTE_TOOLTIPS
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.DEF_BASE
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.DEF_CALC_TOTAL
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.DEF_COLOR
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.DEF_CONNECTOR
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.DEF_H_LINE
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.DEF_H_LINE_ON_TOP
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.DEF_LABEL
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.DEF_LABEL_FORMAT
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.DEF_RELATIVE_TOOLTIPS
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.DEF_SHOW_LEGEND
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.DEF_SIZE
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.DEF_SORTED_VALUE
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.DEF_WIDTH
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.DETAILED_ABSOLUTE_TOOLTIPS
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.DETAILED_RELATIVE_TOOLTIPS
import org.jetbrains.letsPlot.core.spec.conversion.LineTypeOptionConverter
import org.jetbrains.letsPlot.core.spec.plotson.TooltipsOptions
import org.jetbrains.letsPlot.core.spec.plotson.toJson
import org.jetbrains.letsPlot.core.spec.plotson.tooltips
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

        // Merge theme
        val waterfallTheme = waterfallPlotSpec.getMap(Option.Plot.THEME) ?: emptyMap()
        val plotTheme = spec.getMap(Option.Plot.THEME) ?: emptyMap()
        spec[Option.Plot.THEME] = (waterfallTheme + plotTheme).toMutableMap()

        spec.remove("bistro")
    }

    private fun buildWaterfallPlotSpec(plotSpec: MutableMap<String, Any>): Map<String, Any> {
        val bistroSpec = plotSpec.getMap(Option.Plot.BISTRO) ?: error("'bistro' not found in PlotSpec")
        val waterfallPlotOptionsBuilder = WaterfallPlotOptionsBuilder(
            data = plotSpec.getMap(Option.PlotBase.DATA) ?: emptyMap<Any, Any>(),
            x = bistroSpec.getString(Waterfall.X),
            y = bistroSpec.getString(Waterfall.Y),
            measure = bistroSpec.getString(Waterfall.MEASURE),
            group = bistroSpec.getString(Waterfall.GROUP),
            color = bistroSpec.getString(Waterfall.COLOR) ?: DEF_COLOR,
            fill = bistroSpec.getString(Waterfall.FILL) ?: COLOR_FLOW_TYPE,
            size = bistroSpec.getDouble(Waterfall.SIZE) ?: DEF_SIZE,
            alpha = bistroSpec.getDouble(Waterfall.ALPHA),
            lineType = bistroSpec.read(Waterfall.LINE_TYPE),
            width = bistroSpec.getDouble(Waterfall.WIDTH) ?: DEF_WIDTH,
            showLegend = bistroSpec.getBool(Waterfall.SHOW_LEGEND) ?: DEF_SHOW_LEGEND,
            relativeTooltipsOptions = readBoxTooltipsOptions(
                bistroSpec,
                Waterfall.RELATIVE_TOOLTIPS,
                DEF_RELATIVE_TOOLTIPS,
                DETAILED_RELATIVE_TOOLTIPS
            ),
            absoluteTooltipsOptions = readBoxTooltipsOptions(
                bistroSpec,
                Waterfall.ABSOLUTE_TOOLTIPS,
                DEF_ABSOLUTE_TOOLTIPS,
                DETAILED_ABSOLUTE_TOOLTIPS
            ),
            calcTotal = bistroSpec.getBool(Waterfall.CALCULATE_TOTAL) ?: DEF_CALC_TOTAL,
            totalTitle = bistroSpec.getString(Waterfall.TOTAL_TITLE),
            sortedValue = bistroSpec.getBool(Waterfall.SORTED_VALUE) ?: DEF_SORTED_VALUE,
            threshold = bistroSpec.getDouble(Waterfall.THRESHOLD),
            maxValues = bistroSpec.getInt(Waterfall.MAX_VALUES),
            base = bistroSpec.getDouble(Waterfall.BASE) ?: DEF_BASE,
            hLineOptions = readElementLineOptions(bistroSpec, Waterfall.H_LINE, DEF_H_LINE),
            hLineOnTop = bistroSpec.getBool(Waterfall.H_LINE_ON_TOP) ?: DEF_H_LINE_ON_TOP,
            connectorOptions = readElementLineOptions(bistroSpec, Waterfall.CONNECTOR, DEF_CONNECTOR),
            labelOptions = readElementTextOptions(bistroSpec, Waterfall.LABEL, DEF_LABEL),
            labelFormat = bistroSpec.getString(Waterfall.LABEL_FORMAT) ?: DEF_LABEL_FORMAT
        )
        val waterfallPlotOptions = waterfallPlotOptionsBuilder.build()
        return waterfallPlotOptions.toJson()
    }

    private fun readBoxTooltipsOptions(
        bistroSpec: Map<String, Any>,
        optionName: String,
        defaultTooltips: TooltipsOptions,
        detailedTooltips: TooltipsOptions
    ): TooltipsOptions {
        when (bistroSpec.getString(optionName)) {
            Option.Layer.NONE -> return TooltipsOptions.NONE
            TOOLTIP_DETAILED -> return detailedTooltips
        }
        return bistroSpec.getMap(optionName)?.let { tooltipsOptions ->
            tooltips {
                anchor = tooltipsOptions.getString(Option.Layer.TOOLTIP_ANCHOR)
                minWidth = tooltipsOptions.getDouble(Option.Layer.TOOLTIP_MIN_WIDTH)
                title = tooltipsOptions.getString(Option.Layer.TOOLTIP_TITLE)
                disableSplitting = tooltipsOptions.getBool(Option.Layer.DISABLE_SPLITTING)
                lines = tooltipsOptions.getList(Option.LinesSpec.LINES)?.typed<String>()
                formats = tooltipsOptions.getMaps(Option.LinesSpec.FORMATS)?.map { formatOptions ->
                    TooltipsOptions.format {
                        field = formatOptions.getString(Option.LinesSpec.Format.FIELD)
                        format = formatOptions.getString(Option.LinesSpec.Format.FORMAT)
                    }
                }
            }
        } ?: defaultTooltips
    }

    private fun readElementLineOptions(
        bistroSpec: Map<String, Any>,
        option: String,
        defaults: WaterfallPlotOptionsBuilder.ElementLineOptions
    ): WaterfallPlotOptionsBuilder.ElementLineOptions {
        if (bistroSpec.getString(option) == Option.Theme.Elem.BLANK) {
            return WaterfallPlotOptionsBuilder.ElementLineOptions(blank = true)
        }
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
        if (bistroSpec.getString(option) == Option.Theme.Elem.BLANK) {
            return WaterfallPlotOptionsBuilder.ElementTextOptions(blank = true)
        }
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

    override fun isApplicable(spec: Map<String, Any>): Boolean {
        return spec.getString(Option.Plot.BISTRO, Option.Meta.NAME) == Waterfall.NAME
    }

    companion object {
        fun specSelector(): SpecSelector {
            return SpecSelector.root()
        }
    }
}