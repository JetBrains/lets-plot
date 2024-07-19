/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.render.linetype.LineType
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.corr.DataUtil
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.*
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.Option.WaterfallBox
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.Option.WaterfallConnector
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.Option.WaterfallLabel
import org.jetbrains.letsPlot.core.spec.conversion.LineTypeOptionConverter

class WaterfallPlotOptionsBuilder(
    private val data: Map<*, *>,
    private val x: String?,
    private val y: String?,
    private val color: String?,
    private val fill: String?,
    private val size: Double?,
    private val alpha: Double?,
    private val lineType: Any?,
    private val width: Double,
    private val showLegend: Boolean?,
    private val tooltipsOptions: TooltipsOptions?,
    private val calcTotal: Boolean,
    totalTitle: String?,
    private val sortedValue: Boolean,
    private val threshold: Double?,
    private val maxValues: Int?,
    private val hLineOptions: ElementLineOptions,
    private val hLineOnTop: Boolean,
    private val connectorOptions: ElementLineOptions,
    private val labelOptions: ElementTextOptions,
    private val labelFormat: String
) {
    private val flowTypes = FlowType.list(calcTotal, totalTitle)

    fun build(): PlotOptions {
        val boxLayerData = boxLayerData()
        val boxOptions = LayerOptions().also {
            it.geom = GeomKind.CROSS_BAR
            it.data = boxLayerData
            it.mappings = boxMappings()
            it.color = color.takeUnless { color == FLOW_TYPE_COLOR_VALUE }
            it.fill = fill.takeUnless { fill == FLOW_TYPE_COLOR_VALUE }
            it.size = size
            it.alpha = alpha
            it.linetype = LineTypeOptionConverter().apply(lineType)
            it.width = width
            it.showLegend = showLegend
            if (tooltipsOptions != null) {
                it.tooltipsOptions = tooltipsOptions
            } else {
                it.setParameter(Option.Layer.TOOLTIPS, "none")
            }
        }
        return plot {
            layerOptions = if (hLineOnTop) {
                listOfNotNull(connectorOptions(boxLayerData), boxOptions, labelOptions(boxLayerData), hLineOptions())
            } else {
                listOfNotNull(hLineOptions(), connectorOptions(boxLayerData), boxOptions, labelOptions(boxLayerData))
            }
            scaleOptions = listOf(
                scale {
                    aes = Aes.X
                    name = x
                },
                scale {
                    aes = Aes.Y
                    name = y
                },
                scale {
                    aes = Aes.COLOR
                    name = FLOW_TYPE_NAME
                    breaks = flowTypes.values.map(FlowType.FlowTypeData::title)
                    values = flowTypes.values.map(FlowType.FlowTypeData::color)
                },
                scale {
                    aes = Aes.FILL
                    name = FLOW_TYPE_NAME
                    breaks = flowTypes.values.map(FlowType.FlowTypeData::title)
                    values = flowTypes.values.map(FlowType.FlowTypeData::color)
                }
            )
        }
    }

    private fun boxLayerData(): Map<String, List<Any?>> {
        val xVar = x ?: error("Parameter x should be specified")
        val yVar = y ?: error("Parameter y should be specified")
        return WaterfallUtil.calculateBoxStat(
            DataUtil.standardiseData(data),
            x = xVar,
            y = yVar,
            calcTotal = calcTotal,
            sortedValue = sortedValue,
            threshold = threshold,
            maxValues = maxValues,
            initialY = INITIAL_Y,
            flowTypeTitles = flowTypes
        )
    }

    private fun boxMappings(): Map<Aes<*>, String> {
        val mappings = mutableMapOf<Aes<*>, String>(
            WaterfallBox.AES_X to WaterfallBox.Var.X,
            WaterfallBox.AES_YMIN to WaterfallBox.Var.YMIN,
            WaterfallBox.AES_YMAX to WaterfallBox.Var.YMAX,
        )
        if (color == FLOW_TYPE_COLOR_VALUE) {
            mappings[WaterfallBox.AES_COLOR] = WaterfallBox.Var.FLOW_TYPE
        }
        if (fill == FLOW_TYPE_COLOR_VALUE) {
            mappings[WaterfallBox.AES_FILL] = WaterfallBox.Var.FLOW_TYPE
        }
        return mappings
    }

    private fun hLineOptions(): LayerOptions? {
        if (hLineOptions.blank) return null
        return LayerOptions().apply {
            geom = GeomKind.H_LINE
            yintercept = INITIAL_Y
            color = hLineOptions.color
            size = hLineOptions.size
            linetype = hLineOptions.lineType
            setParameter(Option.Layer.TOOLTIPS, "none")
        }
    }

    private fun connectorOptions(boxLayerData: Map<String, List<Any?>>): LayerOptions? {
        if (connectorOptions.blank) return null
        return LayerOptions().also {
            it.geom = GeomKind.SPOKE
            it.data = WaterfallUtil.calculateConnectorStat(boxLayerData)
            it.mappings = mapOf(
                WaterfallConnector.AES_X to WaterfallConnector.Var.X,
                WaterfallConnector.AES_Y to WaterfallConnector.Var.Y,
            )
            it.angle = 0.0
            it.radius = 1.0 - width
            it.position = position {
                name = CONNECTOR_POSITION_NAME
                x = 0.5 - (1 - width) / 2.0
            }
            it.color = connectorOptions.color
            it.size = connectorOptions.size
            it.linetype = connectorOptions.lineType
        }
    }

    private fun labelOptions(boxLayerData: Map<String, List<Any?>>): LayerOptions? {
        if (labelOptions.blank) return null
        return LayerOptions().also {
            it.geom = GeomKind.TEXT
            it.data = WaterfallUtil.calculateLabelStat(boxLayerData, calcTotal)
            it.mappings = labelMappings()
            it.color = labelOptions.color.takeUnless { labelOptions.color == FLOW_TYPE_COLOR_VALUE }
            it.family = labelOptions.family
            it.fontface = labelOptions.face
            it.size = labelOptions.size
            it.angle = labelOptions.angle
            it.hjust = labelOptions.hjust
            it.vjust = labelOptions.vjust
            it.showLegend = showLegend
            it.labelFormat = labelFormat
        }
    }

    private fun labelMappings(): Map<Aes<*>, String> {
        val mappings = mutableMapOf<Aes<*>, String>(
            WaterfallLabel.AES_X to WaterfallLabel.Var.X,
            WaterfallLabel.AES_Y to WaterfallLabel.Var.Y,
            WaterfallLabel.AES_LABEL to WaterfallLabel.Var.LABEL,
        )
        if (labelOptions.color == FLOW_TYPE_COLOR_VALUE) {
            mappings[WaterfallLabel.AES_COLOR] = WaterfallLabel.Var.FLOW_TYPE
        }
        return mappings
    }

    enum class FlowType(val title: String, val color: String) {
        INCREASE( "Increase", "#4daf4a"),
        DECREASE("Decrease", "#e41a1c"),
        TOTAL( "Total","#377eb8");

        data class FlowTypeData(val title: String, val color: String)

        companion object {
            fun list(withTotal: Boolean, totalTitle: String?): Map<FlowType, FlowTypeData> {
                return entries
                    .filter { withTotal || it != TOTAL }
                    .associateWith { flowType ->
                        when (flowType) {
                            TOTAL -> FlowTypeData(totalTitle ?: flowType.title, flowType.color)
                            else -> FlowTypeData(flowType.title, flowType.color)
                        }
                    }
            }
        }
    }

    data class ElementLineOptions(
        var color: String? = null,
        var size: Double? = null,
        var lineType: LineType? = null,
        var blank: Boolean = false
    ) {
        fun merge(other: ElementLineOptions): ElementLineOptions {
            return ElementLineOptions(
                color = other.color ?: color,
                size = other.size ?: size,
                lineType = other.lineType ?: lineType,
                blank = other.blank
            )
        }
    }

    data class ElementTextOptions(
        var color: String? = null,
        var family: String? = null,
        var face: String? = null,
        var size: Double? = null,
        var angle: Double? = null,
        var hjust: Double? = null,
        var vjust: Double? = null,
        var blank: Boolean = false
    ) {
        fun merge(other: ElementTextOptions): ElementTextOptions {
            return ElementTextOptions(
                color = other.color ?: color,
                family = other.family ?: family,
                face = other.face ?: face,
                size = other.size ?: size,
                angle = other.angle ?: angle,
                hjust = other.hjust ?: hjust,
                vjust = other.vjust ?: vjust,
                blank = other.blank
            )
        }
    }

    companion object {
        const val OTHER_NAME = "Other"
        const val FLOW_TYPE_NAME = "Flow type"
        const val FLOW_TYPE_COLOR_VALUE = "flow_type"
        private const val INITIAL_Y = 0.0
        private const val INITIAL_TOOLTIP_NAME = "Initial"
        private const val DIFFERENCE_TOOLTIP_NAME = "Difference"
        private const val CUMULATIVE_SUM_TOOLTIP_NAME = "Cumulative sum"
        private const val CONNECTOR_POSITION_NAME = "nudge"
        private const val TOOLTIPS_VALUE_FORMAT = ".2~f"

        const val DEF_COLOR = "black"
        const val DEF_SIZE = 0.0
        const val DEF_WIDTH = 0.9
        const val DEF_SHOW_LEGEND = false
        const val DEF_CALC_TOTAL = true
        const val DEF_SORTED_VALUE = false
        val DEF_TOOLTIPS = tooltips {
            title = "@x"
            disableSplitting = true
            lines = listOf(
                "$INITIAL_TOOLTIP_NAME|@${WaterfallBox.Var.INITIAL}",
                "$DIFFERENCE_TOOLTIP_NAME|@${WaterfallBox.Var.DIFFERENCE}",
                "$CUMULATIVE_SUM_TOOLTIP_NAME|@${WaterfallBox.Var.CUMULATIVE_SUM}",
            )
            formats = listOf(
                WaterfallBox.Var.INITIAL,
                WaterfallBox.Var.DIFFERENCE,
                WaterfallBox.Var.CUMULATIVE_SUM,
            ).map { f ->
                TooltipsOptions.format {
                    field = f
                    format = TOOLTIPS_VALUE_FORMAT
                }
            }
        }
        val DEF_H_LINE = ElementLineOptions(
            lineType = LineTypeOptionConverter().apply("dashed"),
            blank = true
        )
        const val DEF_H_LINE_ON_TOP = true
        val DEF_CONNECTOR = ElementLineOptions()
        val DEF_LABEL = ElementTextOptions(
            color = "white"
        )
        const val DEF_LABEL_FORMAT = ".2~f"
    }
}