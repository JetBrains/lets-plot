/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.ContinuousTransform
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.assemble.PlotFacets
import jetbrains.datalore.plot.common.data.SeriesUtil
import jetbrains.datalore.plot.config.PlotConfig.Companion.PLOT_COMPUTATION_MESSAGES

object PlotConfigUtil {

    fun splitLayerDataByTile(
        layerData: DataFrame,
        facets: PlotFacets,
    ): List<DataFrame> {
        // Plot (i.e. each plot layer) consists of one or more tiles,
        return if (facets.isDefined) {
            facets.dataByTile(layerData)
        } else {
            listOf(layerData)
        }
    }

    // backend
    fun addComputationMessage(accessor: OptionsAccessor, message: String?) {
        require(message != null)
        val computationMessages = ArrayList(
            getComputationMessages(
                accessor
            )
        )
        computationMessages.add(message)
        accessor.update(PLOT_COMPUTATION_MESSAGES, computationMessages)
    }

    // frontend
    fun findComputationMessages(figSpec: Map<String, Any>): List<String> {
        val messages = mutableListOf<String>()
        enumPlots(figSpec) {
            messages.addAll(getComputationMessages(it))
        }
        return messages.distinct()
    }

    fun removeComputationMessages(figSpec: MutableMap<String, Any>) {
        enumPlots(figSpec) {
            (it as MutableMap<String, Any>).remove(PLOT_COMPUTATION_MESSAGES)
        }
    }

    private fun enumPlots(figSpec: Map<String, Any>, plotSpecHandler: (Map<String, Any>) -> Unit) {
        when (PlotConfig.figSpecKind(figSpec)) {
            FigKind.PLOT_SPEC -> plotSpecHandler(figSpec)
            FigKind.GG_BUNCH_SPEC -> BunchConfig(figSpec).bunchItems.forEach { plotSpecHandler(it.featureSpec) }
            FigKind.SUBPLOTS_SPEC -> {
                OptionsAccessor(figSpec)
                    .getList(Option.SubPlots.FIGURES)
                    .mapNotNull { it as? Map<*, *> } // skip "blank"
                    .forEach {
                        @Suppress("UNCHECKED_CAST")
                        enumPlots(it as Map<String, Any>, plotSpecHandler)
                    }
            }
        }
    }

    private fun getComputationMessages(opts: Map<String, Any>): List<String> {
        return getComputationMessages(OptionsAccessor(opts))
    }

    private fun getComputationMessages(accessor: OptionsAccessor): List<String> {
        return accessor.getList(PLOT_COMPUTATION_MESSAGES).map { it as String }
    }

    internal fun createPlotAesBindingSetup(
        layerConfigs: List<LayerConfig>,
        excludeStatVariables: Boolean
    ): PlotAesBindingSetup {

        val dataByVarBinding = associateVarBindingsWithData(
            layerConfigs, excludeStatVariables
        )

        val varBindings = getVarBindings(layerConfigs, excludeStatVariables)
        val variablesByMappedAes = associateAesWithMappedVariables(
            varBindings
        )

        return PlotAesBindingSetup(
            varBindings = varBindings,
            dataByVarBinding = dataByVarBinding,
            variablesByMappedAes = variablesByMappedAes,
        )
    }

    private fun getVarBindings(
        layerConfigs: List<LayerConfig>, excludeStatVariables: Boolean
    ): List<VarBinding> {
        return layerConfigs.flatMap { it.varBindings }.filter { !(excludeStatVariables && it.variable.isStat) }
    }

    private fun associateAesWithMappedVariables(varBindings: List<VarBinding>): Map<Aes<*>, List<DataFrame.Variable>> {
        val variablesByMappedAes: MutableMap<Aes<*>, MutableList<DataFrame.Variable>> = HashMap()
        for (varBinding in varBindings) {
            val aes = varBinding.aes
            val variable = varBinding.variable
            variablesByMappedAes.getOrPut(aes) { ArrayList() }.add(variable)
        }
        return variablesByMappedAes
    }

    private fun associateVarBindingsWithData(
        layerConfigs: List<LayerConfig>, excludeStatVariables: Boolean
    ): Map<VarBinding, DataFrame> {
        val dataByVarBinding: Map<VarBinding, DataFrame> = layerConfigs.flatMap { layer ->
            layer.varBindings.filter { !(excludeStatVariables && it.variable.isStat) }
                .map { it to layer.combinedData }
        }.toMap()

        // Check that all variables in bindings are mapped to data.
        for ((varBinding, data) in dataByVarBinding) {
            val variable = varBinding.variable
            data.assertDefined(variable)
        }

        return dataByVarBinding
    }

    internal fun defaultScaleName(aes: Aes<*>, variablesByMappedAes: Map<Aes<*>, List<DataFrame.Variable>>): String {
        return if (variablesByMappedAes.containsKey(aes)) {
            val variables = variablesByMappedAes.getValue(aes)
            val labels = variables.map(DataFrame.Variable::label).distinct()
            if (labels.size > 1 && (aes == Aes.X || aes == Aes.Y)) {
                // Don't show multiple labels on X,Y axis.
                aes.name
            } else {
                labels.joinToString()
            }
        } else {
            aes.name
        }
    }

    /**
     * Front-end.
     * ToDo: 'domans' should be computed on 'transformed' data.
     */
    internal fun computeContinuousDomain(
        data: DataFrame,
        variable: DataFrame.Variable,
        transform: ContinuousTransform
    ): DoubleSpan? {
        return if (!transform.hasDomainLimits()) {
            data.range(variable)
        } else {
            val filtered = data.getNumeric(variable).filter {
                transform.isInDomain(it)
            }
            SeriesUtil.range(filtered)
        }
    }

    internal fun createScaleConfigs(scaleOptionsList: List<*>): List<ScaleConfig<Any>> {
        // merge options by 'aes'
        val mergedOpts = HashMap<Aes<Any>, MutableMap<String, Any>>()
        for (opts in scaleOptionsList) {
            @Suppress("UNCHECKED_CAST")
            val optsMap = opts as Map<String, Any>

            @Suppress("UNCHECKED_CAST")
            val aes = ScaleConfig.aesOrFail(optsMap) as Aes<Any>
            if (!mergedOpts.containsKey(aes)) {
                mergedOpts[aes] = HashMap()
            }

            mergedOpts[aes]!!.putAll(optsMap)
        }

        return mergedOpts.map { (aes, options) ->
            ScaleConfig(aes, options)
        }
    }
}
