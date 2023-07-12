/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.StatContext
import org.jetbrains.letsPlot.core.plot.base.Transform
import org.jetbrains.letsPlot.core.plot.base.stat.Stats
import jetbrains.datalore.plot.builder.data.DataProcessing
import jetbrains.datalore.plot.builder.data.GroupingContext
import jetbrains.datalore.plot.builder.data.OrderOptionUtil
import jetbrains.datalore.plot.builder.data.StatInput
import jetbrains.datalore.plot.builder.tooltip.DataFrameField
import jetbrains.datalore.plot.config.LayerConfig

internal object BackendDataProcUtil {
    fun createGroupingContext(
        data: DataFrame,
        layerConfig: LayerConfig,
    ): GroupingContext {
        val groupingVariables = DataProcessing.defaultGroupingVariables(
            data,
            layerConfig.varBindings,
            pathIdVarName = null // only on client side
        )
        return GroupingContext(
            data,
            groupingVariables,
            explicitGroupingVarName = layerConfig.explicitGroupingVarName,
            expectMultiple = true // ?
        )
    }

    fun applyStatisticTransform(
        data: DataFrame,
        layerConfig: LayerConfig,
        statCtx: StatContext,
        transformByAes: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, Transform>,
        facetVariables: List<DataFrame.Variable>,
        groupingContext: GroupingContext,
        messageHandler: (String) -> Unit
    ): DataProcessing.DataAndGroupMapper {

        val stat = layerConfig.stat
        check(stat != Stats.IDENTITY)

        // Need to keep variables without bindings (used in tooltips and for ordering)
        val varsWithoutBinding = layerConfig.run {
            (tooltips.valueSources + annotations.valueSources)
                .filterIsInstance<DataFrameField>()
                .map(DataFrameField::getVariableName) +
                    orderOptions.mapNotNull(OrderOptionUtil.OrderOption::byVariable) +
                    (layerConfig.getMapJoin()?.first?.map { it as String } ?: emptyList())
        }

        val statInput = StatInput(
            data,
            layerConfig.varBindings,
            transformByAes,
            statCtx,
            flipXY = layerConfig.isYOrientation
        )

        return DataProcessing.buildStatData(
            statInput,
            stat,
            groupingContext,
            facetVariables,
            varsWithoutBinding,
            layerConfig.orderOptions,
            layerConfig.aggregateOperation
        ) { message -> messageHandler(createStatMessage(message, layerConfig)) }
    }

    private fun getStatName(layerConfig: LayerConfig): String {
        var stat: String = layerConfig.stat::class.simpleName!!
        stat = stat.replace("Stat", " stat")
        stat = stat.replace("([a-z])([A-Z]+)".toRegex(), "$1_$2").lowercase()

        return stat
    }

    internal fun createSamplingMessage(samplingExpression: String, layerConfig: LayerConfig): String {
        val geomKind = layerConfig.geomProto.geomKind.name.lowercase()
        val stat = getStatName(layerConfig)

        return "$samplingExpression was applied to [$geomKind/$stat] layer"
    }

    private fun createStatMessage(statInfo: String, layerConfig: LayerConfig): String {
        val geomKind = layerConfig.geomProto.geomKind.name.lowercase()
        val stat = getStatName(layerConfig)

        return "$statInfo in [$geomKind/$stat] layer"
    }
}