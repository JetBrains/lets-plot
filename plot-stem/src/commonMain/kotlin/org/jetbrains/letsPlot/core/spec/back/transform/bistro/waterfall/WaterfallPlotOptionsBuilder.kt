/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.plot.base.render.linetype.LineType
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.corr.DataUtil.standardiseData
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.*
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.Option.Waterfall.Keyword.COLOR_FLOW_TYPE
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.Option.WaterfallBox
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.Option.WaterfallBox.DEF_MEASURE_VAR_NAME
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.Option.WaterfallConnector
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.Option.WaterfallLabel
import org.jetbrains.letsPlot.core.spec.conversion.LineTypeOptionConverter

class WaterfallPlotOptionsBuilder(
    data: Map<*, *>,
    private val x: String?,
    private val y: String?,
    private val measure: String?,
    private val group: String?,
    private val color: String?,
    private val fill: String?,
    private val size: Double?,
    private val alpha: Double?,
    private val lineType: Any?,
    private val width: Double,
    private val showLegend: Boolean?,
    private val relativeTooltipsOptions: TooltipsOptions?,
    private val absoluteTooltipsOptions: TooltipsOptions?,
    private val calcTotal: Boolean,
    private val totalTitle: String?,
    private val sortedValue: Boolean,
    private val threshold: Double?,
    private val maxValues: Int?,
    private val hLineOptions: ElementLineOptions,
    private val hLineOnTop: Boolean,
    private val connectorOptions: ElementLineOptions,
    private val labelOptions: ElementTextOptions,
    private val labelFormat: String
) {
    private val data = standardiseData(data)

    fun build(): PlotOptions {
        val layerData = getLayerData()
        val flowTypeData = getFlowTypeDataForLegend(layerData.box)
        val relativeBoxOptions = boxOptions(
            WaterfallUtil.markSkipBoxes(DataFrameUtil.toMap(layerData.box), WaterfallBox.Var.MEASURE.name) { it == Measure.RELATIVE.value },
            relativeTooltipsOptions
        )
        val absoluteBoxOptions = boxOptions(
            WaterfallUtil.markSkipBoxes(DataFrameUtil.toMap(layerData.box), WaterfallBox.Var.MEASURE.name) { it != Measure.RELATIVE.value },
            absoluteTooltipsOptions
        )
        return plot {
            layerOptions = if (hLineOnTop) {
                listOfNotNull(
                    connectorOptions(layerData.connector),
                    relativeBoxOptions,
                    absoluteBoxOptions,
                    labelOptions(layerData.label),
                    hLineOptions()
                )
            } else {
                listOfNotNull(
                    hLineOptions(),
                    connectorOptions(layerData.connector),
                    relativeBoxOptions,
                    absoluteBoxOptions,
                    labelOptions(layerData.label)
                )
            }
            scaleOptions = listOf(
                scale {
                    aes = Aes.X
                    name = x
                    @Suppress("UNCHECKED_CAST")
                    breaks = layerData.box[WaterfallBox.Var.X] as List<Any>
                    @Suppress("UNCHECKED_CAST")
                    labels = layerData.box[WaterfallBox.Var.XLAB] as List<String>
                },
                scale {
                    aes = Aes.Y
                    name = y
                },
                scale {
                    aes = Aes.COLOR
                    name = FLOW_TYPE_NAME
                    breaks = flowTypeData.map(FlowType.FlowTypeData::title)
                    values = flowTypeData.map(FlowType.FlowTypeData::color)
                },
                scale {
                    aes = Aes.FILL
                    name = FLOW_TYPE_NAME
                    breaks = flowTypeData.map(FlowType.FlowTypeData::title)
                    values = flowTypeData.map(FlowType.FlowTypeData::color)
                }
            )
            themeOptions = theme {
                axisTooltip = ThemeOptions.Element.BLANK
            }
        }
    }

    private fun getLayerData(): LayerData {
        val dataGroups = mutableListOf<LayerData>()
        var initialX = 0
        DataUtil.groupBy(DataFrameUtil.fromMap(data), group)
            .forEach { groupData ->
                val boxLayerData = boxLayerGroupData(groupData, initialX)
                val connectorData = WaterfallUtil.calculateConnectorStat(boxLayerData, 1.0 - width)
                val labelData = WaterfallUtil.calculateLabelStat(boxLayerData, FlowType.list(totalTitle)[FlowType.TOTAL]?.title)
                initialX += boxLayerData[WaterfallBox.Var.X]?.size ?: 0
                dataGroups.add(LayerData(boxLayerData, connectorData, labelData))
            }
        return LayerData(
            box = dataGroups.map(LayerData::box).let { DataUtil.concat(it, WaterfallUtil.emptyBoxStat()) },
            connector = dataGroups.map(LayerData::connector).let { DataUtil.concat(it, WaterfallUtil.emptyConnectorStat()) },
            label = dataGroups.map(LayerData::label).let { DataUtil.concat(it, WaterfallUtil.emptyLabelStat()) },
        )
    }

    private fun boxLayerGroupData(groupData: DataFrame, initialX: Int): DataFrame {
        val xVar = x ?: error("Parameter x should be specified")
        val yVar = y ?: error("Parameter y should be specified")
        var measureInitialX = initialX
        var measureInitialY = BASE
        // Need to calculate total for each measure group separately because of sorting and thresholding
        return DataUtil.groupBy(WaterfallUtil.prepareData(groupData, measure, calcTotal), WaterfallBox.MEASURE_GROUP_VAR_NAME)
            .map { measureGroupData ->
                val statData = WaterfallUtil.calculateBoxStat(
                    measureGroupData,
                    x = xVar,
                    y = yVar,
                    measure = measure ?: DEF_MEASURE_VAR_NAME,
                    sortedValue = sortedValue,
                    threshold = threshold,
                    maxValues = maxValues,
                    initialX = measureInitialX,
                    initialY = measureInitialY,
                    base = BASE,
                    flowTypeTitles = FlowType.list(totalTitle)
                )
                measureInitialX += statData[WaterfallBox.Var.X].size
                measureInitialY = statData[WaterfallBox.Var.VALUE].lastOrNull() as? Double ?: BASE
                statData
            }
            .let { datasets ->
                DataUtil.concat(datasets, WaterfallUtil.emptyBoxStat())
            }
    }

    private fun getFlowTypeDataForLegend(boxData: DataFrame): List<FlowType.FlowTypeData> {
        return boxData[WaterfallBox.Var.MEASURE].let {
            if (it.contains(Measure.TOTAL.value)) {
                emptySet()
            } else {
                setOf(FlowType.TOTAL)
            } + if (it.contains(Measure.ABSOLUTE.value)) {
                emptySet()
            } else {
                setOf(FlowType.ABSOLUTE)
            }
        }.let {
            FlowType.list(totalTitle, it).values.toList()
        }
    }

    private fun boxOptions(boxData: Map<String, List<Any?>>, tooltipsOptions: TooltipsOptions?): LayerOptions {
        return LayerOptions().also {
            it.geom = GeomKind.CROSS_BAR
            it.data = boxData
            it.mappings = boxMappings()
            it.color = color.takeUnless { color == COLOR_FLOW_TYPE }
            it.fill = fill.takeUnless { fill == COLOR_FLOW_TYPE }
            it.size = size
            it.alpha = alpha
            it.linetype = LineTypeOptionConverter().apply(lineType)
            it.width = width
            it.showLegend = showLegend
            if (tooltipsOptions != null) {
                it.tooltipsOptions = tooltipsOptions
            } else {
                it.setParameter(Option.Layer.TOOLTIPS, Option.Layer.NONE)
            }
        }
    }

    private fun boxMappings(): Map<Aes<*>, String> {
        val mappings = mutableMapOf<Aes<*>, String>(
            Aes.X to WaterfallBox.Var.X.name,
            Aes.YMIN to WaterfallBox.Var.YMIN.name,
            Aes.YMAX to WaterfallBox.Var.YMAX.name
        )
        if (color == COLOR_FLOW_TYPE) {
            mappings[Aes.COLOR] = WaterfallBox.Var.FLOW_TYPE.name
        }
        if (fill == COLOR_FLOW_TYPE) {
            mappings[Aes.FILL] = WaterfallBox.Var.FLOW_TYPE.name
        }
        return mappings
    }

    private fun hLineOptions(): LayerOptions? {
        if (hLineOptions.blank) return null
        return LayerOptions().apply {
            geom = GeomKind.H_LINE
            yintercept = BASE
            color = hLineOptions.color
            size = hLineOptions.size
            linetype = hLineOptions.lineType
            setParameter(Option.Layer.TOOLTIPS, Option.Layer.NONE)
        }
    }

    private fun connectorOptions(connectorData: DataFrame): LayerOptions? {
        if (connectorOptions.blank) return null
        return LayerOptions().also {
            it.geom = GeomKind.SPOKE
            it.data = DataFrameUtil.toMap(connectorData)
            it.mappings = mapOf(
                Aes.X to WaterfallConnector.Var.X.name,
                Aes.Y to WaterfallConnector.Var.Y.name,
                Aes.RADIUS to WaterfallConnector.Var.RADIUS.name
            )
            it.angle = 0.0
            it.position = position {
                name = CONNECTOR_POSITION_NAME
                x = 0.5 - (1 - width) / 2.0
            }
            it.color = connectorOptions.color
            it.size = connectorOptions.size
            it.linetype = connectorOptions.lineType
        }
    }

    private fun labelOptions(labelData: DataFrame): LayerOptions? {
        if (labelOptions.blank) return null
        return LayerOptions().also {
            it.geom = GeomKind.TEXT
            it.data = DataFrameUtil.toMap(labelData)
            it.mappings = labelMappings()
            it.color = labelOptions.color.takeUnless { labelOptions.color == COLOR_FLOW_TYPE }
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
            Aes.X to WaterfallLabel.Var.X.name,
            Aes.Y to WaterfallLabel.Var.Y.name,
            Aes.LABEL to WaterfallLabel.Var.LABEL.name,
        )
        if (labelOptions.color == COLOR_FLOW_TYPE) {
            mappings[Aes.COLOR] = WaterfallLabel.Var.FLOW_TYPE.name
        }
        return mappings
    }

    enum class Measure(val value: String) {
        RELATIVE("relative"),
        ABSOLUTE("absolute"),
        TOTAL("total")
    }

    enum class FlowType(val title: String, val color: String) {
        INCREASE("Increase", "#4daf4a"),
        DECREASE("Decrease", "#e41a1c"),
        ABSOLUTE("Absolute", "#377eb8"),
        TOTAL("Total","#377eb8");

        data class FlowTypeData(val title: String, val color: String)

        companion object {
            fun list(totalTitle: String?, skip: Set<FlowType> = emptySet()): Map<FlowType, FlowTypeData> {
                return entries
                    .filter { it !in skip }
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

    data class LayerData(val box: DataFrame, val connector: DataFrame, val label: DataFrame)

    companion object {
        const val OTHER_NAME = "Other"
        const val FLOW_TYPE_NAME = "Flow type"
        private const val BASE = 0.0
        private const val INITIAL_TOOLTIP_NAME = "Initial"
        private const val DIFFERENCE_TOOLTIP_NAME = "Difference"
        private const val CUMULATIVE_SUM_TOOLTIP_NAME = "Cumulative sum"
        private const val VALUE_TOOLTIP_NAME = "Value"
        private const val CONNECTOR_POSITION_NAME = "nudge"
        private const val TOOLTIPS_VALUE_FORMAT = ".2~f"

        const val DEF_COLOR = "black"
        const val DEF_SIZE = 0.0
        const val DEF_WIDTH = 0.9
        const val DEF_SHOW_LEGEND = false
        const val DEF_CALC_TOTAL = true
        const val DEF_SORTED_VALUE = false
        val DEF_RELATIVE_TOOLTIPS = tooltips {
            lines = listOf(
                "@${WaterfallBox.Var.DIFFERENCE}",
            )
            formats = listOf(
                TooltipsOptions.format {
                    field = WaterfallBox.Var.DIFFERENCE.name
                    format = TOOLTIPS_VALUE_FORMAT
                }
            )
        }
        val DETAILED_RELATIVE_TOOLTIPS = tooltips {
            title = "@${WaterfallBox.Var.XLAB}"
            disableSplitting = true
            lines = listOf(
                "$INITIAL_TOOLTIP_NAME|@${WaterfallBox.Var.INITIAL}",
                "$DIFFERENCE_TOOLTIP_NAME|@${WaterfallBox.Var.DIFFERENCE}",
                "$CUMULATIVE_SUM_TOOLTIP_NAME|@${WaterfallBox.Var.VALUE}",
            )
            formats = listOf(
                WaterfallBox.Var.INITIAL,
                WaterfallBox.Var.DIFFERENCE,
                WaterfallBox.Var.VALUE,
            ).map { f ->
                TooltipsOptions.format {
                    field = f.name
                    format = TOOLTIPS_VALUE_FORMAT
                }
            }
        }
        val DEF_ABSOLUTE_TOOLTIPS = tooltips {
            disableSplitting = true
            lines = listOf(
                "@${WaterfallBox.Var.VALUE}",
            )
            formats = listOf(
                TooltipsOptions.format {
                    field = WaterfallBox.Var.VALUE.name
                    format = TOOLTIPS_VALUE_FORMAT
                }
            )
        }
        val DETAILED_ABSOLUTE_TOOLTIPS = tooltips {
            title = "@${WaterfallBox.Var.XLAB}"
            disableSplitting = true
            lines = listOf(
                "$VALUE_TOOLTIP_NAME|@${WaterfallBox.Var.VALUE}",
            )
            formats = listOf(
                TooltipsOptions.format {
                    field = WaterfallBox.Var.VALUE.name
                    format = TOOLTIPS_VALUE_FORMAT
                }
            )
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