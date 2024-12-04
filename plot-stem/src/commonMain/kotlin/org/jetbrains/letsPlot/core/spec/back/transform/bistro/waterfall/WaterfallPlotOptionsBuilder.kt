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
import org.jetbrains.letsPlot.core.spec.Option.Meta
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.DataUtil.standardiseData
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.groupBy
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.replace
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.Option.Waterfall
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.Option.Waterfall.Keyword.COLOR_FLOW_TYPE
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.Option.Waterfall.Var.DEF_MEASURE
import org.jetbrains.letsPlot.core.spec.conversion.LineTypeOptionConverter
import org.jetbrains.letsPlot.core.spec.getMaps
import org.jetbrains.letsPlot.core.spec.plotson.*
import kotlin.collections.first

class WaterfallPlotOptionsBuilder(
    data: Map<*, *>,
    private val dataMeta: Map<*, *>?,
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
    private val relativeTooltipsOptions: TooltipsOptions,
    private val absoluteTooltipsOptions: TooltipsOptions,
    private val calcTotal: Boolean,
    private val totalTitle: String?,
    private val sortedValue: Boolean,
    private val threshold: Double?,
    private val maxValues: Int?,
    private val base: Double,
    private val hLineOptions: ElementLineOptions,
    private val hLineOnTop: Boolean,
    private val connectorOptions: ElementLineOptions,
    private val labelOptions: ElementTextOptions,
    private val labelFormat: String?
) {
    private val data = standardiseData(data)

    fun build(): PlotOptions {
        val statDf = getStatData()
        val flowTypeData = getFlowTypeDataForLegend(statDf)
        val (absoluteStatDf, relativeStatDf) = splitStatDfToAbsoluteAndRelative(statDf)
        return plot {
            dataMeta = dataMetaOptions()
            layerOptions =
                listOfNotNull(
                    if (hLineOnTop) null else hLineOptions(),
                    connectorOptions(statDf),
                    boxOptions(relativeStatDf, relativeTooltipsOptions),
                    boxOptions(absoluteStatDf, absoluteTooltipsOptions),
                    labelOptions(statDf),
                    if (hLineOnTop) hLineOptions() else null
                )
            scaleOptions = listOf(
                scale {
                    aes = Aes.X
                    name = x
                    @Suppress("UNCHECKED_CAST")
                    breaks = statDf[Waterfall.Var.Stat.X] as List<Any>
                    @Suppress("UNCHECKED_CAST")
                    labels = statDf[Waterfall.Var.Stat.XLAB] as List<String>
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

    private fun getStatData(): DataFrame {
        val dataGroups = mutableListOf<DataFrame>()
        var initialX = 0
        val df = DataFrameUtil.fromMap(data)
        df.groupBy(group)
            .forEach { (groupValue, groupData) ->
                val statDf = getGroupData(groupData, groupValue, initialX).let { groupStatDf ->
                    WaterfallUtil.appendRadius(groupStatDf, 1.0 - width)
                }
                initialX += statDf[Waterfall.Var.Stat.X].size
                dataGroups.add(statDf)
            }
        return if (dataGroups.isEmpty()) {
            WaterfallUtil.emptyStat(df.variables())
        } else {
            DataFrameUtil.concat(dataGroups)
        }
    }

    private fun getGroupData(groupData: DataFrame, groupValue: Any, initialX: Int): DataFrame {
        val xVar = x ?: error("Parameter x should be specified")
        val yVar = y ?: error("Parameter y should be specified")
        var measureInitialX = initialX
        var measureInitialY = base
        val groupVar = group?.let { DataFrameUtil.findVariableOrNull(groupData, it) }
        val newRowValues = { variable: DataFrame.Variable ->
            when (variable) {
                groupVar -> groupValue
                else -> null
            }
        }
        val df = WaterfallUtil.prepareData(groupData, measure, calcTotal, totalRowValues = newRowValues)
        // Need to calculate total for each measure group separately because of sorting and thresholding
        return df.groupBy(Waterfall.Var.MEASURE_GROUP.name)
            .map { (_, measureGroupData) ->
                val statData = WaterfallUtil.calculateStat(
                    measureGroupData,
                    x = xVar,
                    y = yVar,
                    measure = measure ?: DEF_MEASURE.name,
                    sortedValue = sortedValue,
                    threshold = threshold,
                    maxValues = maxValues,
                    initialX = measureInitialX,
                    initialY = measureInitialY,
                    base = base,
                    flowTypeTitles = FlowType.list(totalTitle),
                    otherRowValues = newRowValues
                )
                measureInitialX += statData[Waterfall.Var.Stat.X].size
                measureInitialY = statData[Waterfall.Var.Stat.VALUE].lastOrNull() as? Double ?: base
                statData
            }
            .let { datasets ->
                if (datasets.isEmpty()) {
                    WaterfallUtil.emptyStat(df.variables())
                } else {
                    DataFrameUtil.concat(datasets)
                }
            }
    }

    private fun splitStatDfToAbsoluteAndRelative(statDf: DataFrame): Pair<DataFrame, DataFrame> {
        fun replaceYToNull(variable: DataFrame.Variable): (Any?) -> Any? {
            return { value ->
                when (variable) {
                    Waterfall.Var.Stat.YMIN,
                    Waterfall.Var.Stat.YMAX,
                    Waterfall.Var.Stat.YMIDDLE -> null
                    else -> value
                }
            }
        }
        return Pair(
            statDf.replace(Waterfall.Var.Stat.MEASURE, { it == Measure.RELATIVE.value }, ::replaceYToNull),
            statDf.replace(Waterfall.Var.Stat.MEASURE, { it != Measure.RELATIVE.value }, ::replaceYToNull)
        )
    }

    private fun getFlowTypeDataForLegend(statData: DataFrame): List<FlowType.FlowTypeData> {
        val measures = statData[Waterfall.Var.Stat.MEASURE]
        val skipFlowTypes = setOfNotNull(
            FlowType.TOTAL.takeUnless { measures.contains(Measure.TOTAL.value) },
            FlowType.ABSOLUTE.takeUnless { measures.contains(Measure.ABSOLUTE.value) }
        )
        return FlowType.list(totalTitle, skipFlowTypes).values.toList()
    }

    private fun dataMetaOptions(): DataMetaOptions? {
        @Suppress("UNCHECKED_CAST")
        val seriesAnnotation = dataMeta?.getMaps(Meta.SeriesAnnotation.TAG) ?: return null
        val yMeta = seriesAnnotation.firstOrNull { it[Meta.SeriesAnnotation.COLUMN] == y } ?: return null
        val yTypeStr = yMeta[Meta.SeriesAnnotation.TYPE] ?: return null
        val yType = SeriesAnnotationOptions.Types.from(yTypeStr.toString()) ?: return null
        val variables = listOf(
            Waterfall.Var.Stat.YMIN,
            Waterfall.Var.Stat.YMIDDLE,
            Waterfall.Var.Stat.YMAX,
            Waterfall.Var.Stat.INITIAL,
            Waterfall.Var.Stat.VALUE,
            Waterfall.Var.Stat.DIFFERENCE,
            Waterfall.Var.Stat.LABEL,
        )
        return dataMeta {
            variables.forEach { variable ->
                appendSeriesAnnotation {
                    type = yType
                    column = variable.name
                }
            }
        }
    }

    private fun boxOptions(statDf: DataFrame, tooltipsOptions: TooltipsOptions): LayerOptions {
        return LayerOptions().also {
            it.geom = GeomKind.CROSS_BAR
            it.data = DataFrameUtil.toMap(statDf)
            it.mapping = boxMappings()
            it.color = color.takeUnless { color == COLOR_FLOW_TYPE }
            it.fill = fill.takeUnless { fill == COLOR_FLOW_TYPE }
            it.size = size
            it.alpha = alpha
            it.linetype = LineTypeOptionConverter().apply(lineType)
            it.width = width
            it.showLegend = showLegend
            it.tooltipsOptions = tooltipsOptions
        }
    }

    private fun boxMappings(): Mapping {
        var mapping = Mapping(
            Aes.X to Waterfall.Var.Stat.X.name,
            Aes.YMIN to Waterfall.Var.Stat.YMIN.name,
            Aes.YMAX to Waterfall.Var.Stat.YMAX.name
        )
        if (color == COLOR_FLOW_TYPE) {
            mapping += Aes.COLOR to Waterfall.Var.Stat.FLOW_TYPE.name
        }
        if (fill == COLOR_FLOW_TYPE) {
            mapping += Aes.FILL to Waterfall.Var.Stat.FLOW_TYPE.name
        }
        return mapping
    }

    private fun hLineOptions(): LayerOptions? {
        if (hLineOptions.blank) return null
        return LayerOptions().also {
            it.geom = GeomKind.H_LINE
            it.yintercept = base
            it.color = hLineOptions.color
            it.size = hLineOptions.size
            it.linetype = hLineOptions.lineType
            it.tooltipsOptions = TooltipsOptions.NONE
        }
    }

    private fun connectorOptions(df: DataFrame): LayerOptions? {
        if (connectorOptions.blank) return null
        return LayerOptions().also {
            it.geom = GeomKind.SPOKE
            it.data = DataFrameUtil.toMap(df)
            it.mapping = Mapping(
                Aes.X to Waterfall.Var.Stat.X.name,
                Aes.Y to Waterfall.Var.Stat.VALUE.name,
                Aes.RADIUS to Waterfall.Var.Stat.RADIUS.name
            )
            it.angle = 0.0
            it.position = nudge {
                x = 0.5 - (1 - width) / 2.0
            }
            it.color = connectorOptions.color
            it.size = connectorOptions.size
            it.linetype = connectorOptions.lineType
        }
    }

    private fun labelOptions(labelData: DataFrame): LayerOptions? {
        if (labelOptions.blank) return null
        return TextLayer().also {
            it.data = DataFrameUtil.toMap(labelData)
            it.mapping = labelMappings()
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

    private fun labelMappings(): Mapping {
        var mapping = Mapping(
            Aes.X to Waterfall.Var.Stat.X.name,
            Aes.Y to Waterfall.Var.Stat.YMIDDLE.name,
            Aes.LABEL to Waterfall.Var.Stat.LABEL.name,
        )
        if (labelOptions.color == COLOR_FLOW_TYPE) {
            mapping += Aes.COLOR to Waterfall.Var.Stat.FLOW_TYPE.name
        }

        return mapping
    }

    enum class Measure(val value: String) {
        RELATIVE("relative"),
        ABSOLUTE("absolute"),
        TOTAL("total");

        companion object {
            fun byValue(value: String): Measure {
                return entries.first { it.value == value }
            }
        }
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

    companion object {
        const val OTHER_NAME = "Other"
        const val FLOW_TYPE_NAME = "Flow type"
        private const val INITIAL_TOOLTIP_NAME = "Initial"
        private const val DIFFERENCE_TOOLTIP_NAME = "Difference"
        private const val CUMULATIVE_SUM_TOOLTIP_NAME = "Cumulative sum"
        private const val VALUE_TOOLTIP_NAME = "Value"

        const val DEF_COLOR = "black"
        const val DEF_SIZE = 0.0
        const val DEF_WIDTH = 0.9
        const val DEF_SHOW_LEGEND = false
        const val DEF_CALC_TOTAL = true
        const val DEF_SORTED_VALUE = false
        const val DEF_BASE = 0.0
        val DEF_RELATIVE_TOOLTIPS = tooltips {
            lines = listOf(
                "@${Waterfall.Var.Stat.DIFFERENCE}",
            )
        }
        val DETAILED_RELATIVE_TOOLTIPS = tooltips {
            title = "@${Waterfall.Var.Stat.XLAB}"
            disableSplitting = true
            lines = listOf(
                "$INITIAL_TOOLTIP_NAME|@${Waterfall.Var.Stat.INITIAL}",
                "$DIFFERENCE_TOOLTIP_NAME|@${Waterfall.Var.Stat.DIFFERENCE}",
                "$CUMULATIVE_SUM_TOOLTIP_NAME|@${Waterfall.Var.Stat.VALUE}",
            )
        }
        val DEF_ABSOLUTE_TOOLTIPS = tooltips {
            disableSplitting = true
            lines = listOf(
                "@${Waterfall.Var.Stat.VALUE}",
            )
        }
        val DETAILED_ABSOLUTE_TOOLTIPS = tooltips {
            title = "@${Waterfall.Var.Stat.XLAB}"
            disableSplitting = true
            lines = listOf(
                "$VALUE_TOOLTIP_NAME|@${Waterfall.Var.Stat.VALUE}",
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
    }
}