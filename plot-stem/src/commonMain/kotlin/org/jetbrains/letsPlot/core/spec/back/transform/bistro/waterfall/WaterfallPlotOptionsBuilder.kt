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
    private val tooltipsOptions: TooltipsOptions,
    private val calcTotal: Boolean,
    private val totalTitle: String?,
    private val sortedValue: Boolean,
    private val threshold: Double?,
    private val maxValues: Int?,
    private val hLineOptions: ElementLineOptions,
    private val hLineOnTop: Boolean,
    private val connectorOptions: ElementLineOptions,
    private val labelOptions: ElementTextOptions
) {
    fun build(): PlotOptions {
        if (totalTitle != null) {
            FlowType.TOTAL.changeTitle(totalTitle)
        }
        val boxLayerData = boxLayerData()
        val boxOptionsList = listOf(
            LayerOptions().apply {
                geom = GeomKind.CROSS_BAR
                this.data = boxLayerData
                mappings = boxMappings()
                color = when (this@WaterfallPlotOptionsBuilder.color) {
                    FLOW_TYPE_COLOR_VALUE -> null
                    else -> this@WaterfallPlotOptionsBuilder.color
                }
                fill = when (this@WaterfallPlotOptionsBuilder.fill) {
                    FLOW_TYPE_COLOR_VALUE -> null
                    else -> this@WaterfallPlotOptionsBuilder.fill
                }
                size = this@WaterfallPlotOptionsBuilder.size
                alpha = this@WaterfallPlotOptionsBuilder.alpha
                linetype = LineTypeOptionConverter().apply(this@WaterfallPlotOptionsBuilder.lineType)
                width = this@WaterfallPlotOptionsBuilder.width
                showLegend = this@WaterfallPlotOptionsBuilder.showLegend
                tooltipsOptions = this@WaterfallPlotOptionsBuilder.tooltipsOptions
            },
        )
        return plot {
            layerOptions = if (hLineOnTop) {
                connectorOptionsList(boxLayerData) + boxOptionsList + labelOptionsList(boxLayerData) +
                        hLineOptionsList()
            } else {
                hLineOptionsList() +
                        connectorOptionsList(boxLayerData) + boxOptionsList + labelOptionsList(boxLayerData)
            }
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
            initialY = INITIAL_Y
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

    private fun hLineOptionsList(): List<LayerOptions> {
        if (hLineOptions.blank) return emptyList()
        return listOf(
            LayerOptions().apply {
                geom = GeomKind.H_LINE
                yintercept = INITIAL_Y
                color = hLineOptions.color
                size = hLineOptions.size
                linetype = hLineOptions.lineType
            }
        )
    }

    private fun connectorOptionsList(boxLayerData: Map<String, List<Any?>>): List<LayerOptions> {
        if (connectorOptions.blank) return emptyList()
        return listOf(
            LayerOptions().apply {
                geom = GeomKind.SPOKE
                this.data = WaterfallUtil.calculateConnectorStat(boxLayerData)
                mappings = mapOf(
                    WaterfallConnector.AES_X to WaterfallConnector.Var.X,
                    WaterfallConnector.AES_Y to WaterfallConnector.Var.Y,
                )
                angle = 0.0
                radius = 1.0 - this@WaterfallPlotOptionsBuilder.width
                position = position {
                    name = CONNECTOR_POSITION_NAME
                    x = 0.5 - (1 - this@WaterfallPlotOptionsBuilder.width) / 2.0
                }
                color = connectorOptions.color
                size = connectorOptions.size
                linetype = connectorOptions.lineType
            }
        )
    }

    private fun labelOptionsList(boxLayerData: Map<String, List<Any?>>): List<LayerOptions> {
        if (labelOptions.blank) return emptyList()
        return listOf(
            LayerOptions().apply {
                geom = GeomKind.TEXT
                this.data = WaterfallUtil.calculateLabelStat(boxLayerData, calcTotal)
                mappings = labelMappings()
                color = when (labelOptions.color) {
                    FLOW_TYPE_COLOR_VALUE -> null
                    else -> labelOptions.color
                }
                family = labelOptions.family
                fontface = labelOptions.face
                size = labelOptions.size
                angle = labelOptions.angle
                hjust = labelOptions.hjust
                vjust = labelOptions.vjust
                showLegend = this@WaterfallPlotOptionsBuilder.showLegend
            }
        )
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

    enum class FlowType(private var title: String) {
        INCREASE("Increase"),
        DECREASE("Decrease"),
        TOTAL("Total");

        fun changeTitle(title: String) {
            this.title = title
        }

        override fun toString(): String {
            return title
        }
    }

    data class ElementLineOptions(
        var color: String? = null,
        var size: Double? = null,
        var lineType: LineType? = null,
        var blank: Boolean = false
    ) {
        fun merge(other: ElementLineOptions): ElementLineOptions {
            color = other.color ?: color
            size = other.size ?: size
            lineType = other.lineType ?: lineType
            blank = other.blank
            return this
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
            color = other.color ?: color
            family = other.family ?: family
            face = other.face ?: face
            size = other.size ?: size
            angle = other.angle ?: angle
            hjust = other.hjust ?: hjust
            vjust = other.vjust ?: vjust
            blank = other.blank
            return this
        }
    }

    companion object {
        const val OTHER_NAME = "Other"
        const val FLOW_TYPE_COLOR_VALUE = "flow_type"
        private const val INITIAL_Y = 0.0
        private const val INITIAL_TOOLTIP_NAME = "Initial"
        private const val DIFFERENCE_TOOLTIP_NAME = "Difference"
        private const val CUMULATIVE_SUM_TOOLTIP_NAME = "Cumulative sum"
        private const val CONNECTOR_POSITION_NAME = "nudge"

        const val DEF_COLOR = "black"
        const val DEF_SIZE = 0.0
        const val DEF_WIDTH = 0.9
        const val DEF_SHOW_LEGEND = false
        const val DEF_CALC_TOTAL = true
        const val DEF_SORTED_VALUE = false
        val DEF_TOOLTIPS = mapOf(
            Option.Layer.TOOLTIP_TITLE to "^x",
            Option.LinesSpec.LINES to listOf(
                "$INITIAL_TOOLTIP_NAME|@${WaterfallBox.Var.INITIAL}",
                "$DIFFERENCE_TOOLTIP_NAME|@${WaterfallBox.Var.DIFFERENCE}",
                "$CUMULATIVE_SUM_TOOLTIP_NAME|@${WaterfallBox.Var.CUMULATIVE_SUM}",
            ),
            Option.Layer.DISABLE_SPLITTING to true
        )
        val DEF_H_LINE = ElementLineOptions(
            lineType = LineTypeOptionConverter().apply("dashed"),
            blank = true
        )
        const val DEF_H_LINE_ON_TOP = true
        val DEF_CONNECTOR = ElementLineOptions()
        val DEF_LABEL = ElementTextOptions(
            color = "white"
        )
    }
}