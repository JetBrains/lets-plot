/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.Transform
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.builder.data.DataProcessing
import jetbrains.datalore.plot.builder.data.GroupingContext
import jetbrains.datalore.plot.builder.data.OrderOptionUtil
import jetbrains.datalore.plot.builder.data.StatInput
import jetbrains.datalore.plot.builder.tooltip.DataFrameValue
import jetbrains.datalore.plot.config.LayerConfig

internal object BackendDataProcUtil {
    fun applyStatisticalTransform(
        data: DataFrame,
        layerConfig: LayerConfig,
        statCtx: StatContext,
        transformByAes: Map<Aes<*>, Transform>,
        facetVariables: List<DataFrame.Variable>,
        massageHandler: (String) -> Unit
    ): DataFrame {
        val groupingVariables = DataProcessing.defaultGroupingVariables(
            data,
            layerConfig.varBindings,
            pathIdVarName = null // only on client side
        )
        val groupingContext = GroupingContext(
            data,
            groupingVariables,
            explicitGroupingVarName = layerConfig.explicitGroupingVarName,
            expectMultiple = true // ?
        )

        val groupingContextAfterStat: GroupingContext
        val stat = layerConfig.stat
        var tileLayerDataAfterStat: DataFrame
        if (stat === Stats.IDENTITY) {
            // Do not apply stat
            tileLayerDataAfterStat = data
            groupingContextAfterStat = groupingContext
        } else {
            // Need to keep variables without bindings (used in tooltips and for ordering)
            val varsWithoutBinding = layerConfig.run {
                (tooltips.valueSources + annotations.valueSources)
                    .filterIsInstance<DataFrameValue>()
                    .map(DataFrameValue::getVariableName) +
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

            val tileLayerDataAndGroupingContextAfterStat = DataProcessing.buildStatData(
                statInput,
                stat,
                groupingContext,
                facetVariables,
                varsWithoutBinding,
                layerConfig.orderOptions,
                layerConfig.aggregateOperation
            ) { message -> massageHandler(createStatMessage(message, layerConfig)) }

            tileLayerDataAfterStat = tileLayerDataAndGroupingContextAfterStat.data
            groupingContextAfterStat = tileLayerDataAndGroupingContextAfterStat.groupingContext
        }

        // Apply sampling to layer tile data if necessary
        tileLayerDataAfterStat =
            PlotSampling.apply(
                tileLayerDataAfterStat,
                layerConfig.samplings,
                groupingContextAfterStat.groupMapper
            ) { message -> massageHandler(createSamplingMessage(message, layerConfig)) }

        return tileLayerDataAfterStat
    }

    private fun getStatName(layerConfig: LayerConfig): String {
        var stat: String = layerConfig.stat::class.simpleName!!
        stat = stat.replace("Stat", " stat")
        stat = stat.replace("([a-z])([A-Z]+)".toRegex(), "$1_$2").lowercase()

        return stat
    }

    private fun createSamplingMessage(samplingExpression: String, layerConfig: LayerConfig): String {
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