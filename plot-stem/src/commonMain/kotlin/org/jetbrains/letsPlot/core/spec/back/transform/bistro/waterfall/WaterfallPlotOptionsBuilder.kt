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
    private val width: Double?,
    private val showLegend: Boolean?,
    private val tooltipsOptions: TooltipsOptions,
    private val calcTotal: Boolean,
    private val totalTitle: String?,
    private val sortedValue: Boolean,
    private val threshold: Double?,
    private val maxValues: Int?,
    private val hLineOptions: ElementLineOptions,
    private val hLineOnTop: Boolean
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
                boxOptionsList + hLineOptionsList()
            } else {
                hLineOptionsList() + boxOptionsList
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

    data class ElementLineOptions(var color: String?, var size: Double?, var lineType: LineType?, var blank: Boolean) {
        fun merge(other: ElementLineOptions): ElementLineOptions {
            color = other.color ?: color
            size = other.size ?: size
            lineType = other.lineType ?: lineType
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

        const val DEF_COLOR = "black"
        const val DEF_SIZE = 0.0
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
            color = null,
            size = null,
            lineType = LineTypeOptionConverter().apply("dashed"),
            blank = true
        )
        const val DEF_H_LINE_ON_TOP = true
    }
}