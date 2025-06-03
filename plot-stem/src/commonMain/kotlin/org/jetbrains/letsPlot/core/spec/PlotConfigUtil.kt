/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec

import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.builder.VarBinding
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotFacets
import org.jetbrains.letsPlot.core.spec.config.*
import org.jetbrains.letsPlot.core.spec.config.PlotConfig.Companion.PLOT_COMPUTATION_MESSAGES
import org.jetbrains.letsPlot.core.spec.conversion.AesOptionConversion

internal object PlotConfigUtil {

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

    fun containsToolbar(figSpec: Map<String, Any>): Boolean {
        // For now only check the top figure
        return figSpec.containsKey(Option.Meta.Kind.GG_TOOLBAR)
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
        if (PlotConfig.isFailure(figSpec)) return  // Not a plot spec

        when (PlotConfig.figSpecKind(figSpec)) {
            FigKind.PLOT_SPEC -> plotSpecHandler(figSpec)
            FigKind.GG_BUNCH_SPEC -> throw IllegalStateException("Unsupported: GGBunch")
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
        return createPlotAesBindingSetup(
            bindingsByLayer = layerConfigs.map { it.varBindings },
            dataByLayer = layerConfigs.map { it.combinedData },
            excludeStatVariables
        )
    }

    internal fun createPlotAesBindingSetup(
        bindingsByLayer: List<List<VarBinding>>,
        dataByLayer: List<DataFrame>,
        excludeStatVariables: Boolean
    ): PlotAesBindingSetup {

        @Suppress("NAME_SHADOWING")
        val bindingsByLayer = bindingsByLayer.map { it.filter { !(excludeStatVariables && it.variable.isStat) } }
        val dataByVarBinding = associateVarBindingsWithData(
            bindingsByLayer,
            dataByLayer,
        )

        val varBindings = bindingsByLayer.flatten()
        val variablesByMappedAes = associateAesWithMappedVariables(varBindings)

        return PlotAesBindingSetup(
            varBindings = varBindings,
            dataByVarBinding = dataByVarBinding,
            variablesByMappedAes = variablesByMappedAes,
        )
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
        bindingsByLayer: List<List<VarBinding>>,
        dataByLayer: List<DataFrame>,
    ): List<Pair<VarBinding, DataFrame>> {
        val dataByVarBinding = bindingsByLayer.zip(dataByLayer)
            .flatMap { (varBindings, data) ->
                varBindings.map { it to data }
            } //.toMap()  Don't do it. See VarBinding.equals()

        // Check that all variables in bindings are mapped to data.
        for ((varBinding, data) in dataByVarBinding) {
            val variable = varBinding.variable
            data.assertDefined(variable)
        }

        return dataByVarBinding
    }

    internal fun defaultScaleName(aes: Aes<*>, variablesByMappedAes: Map<Aes<*>, List<DataFrame.Variable>>): String {
        val variables = variablesByMappedAes[aes] ?: emptyList()
        val labels = variables.map(DataFrame.Variable::label)
            .distinct()
            // Give preference to more descriptive labels.
            // This also prevents overriding axis/legend title by the `expand_limits()` function.
            .filter { it != aes.name }

        return if (labels.isEmpty()) {
            aes.name
        } else if (labels.size > 1 && (aes == Aes.X || aes == Aes.Y)) {
            // Don't show multiple labels on X,Y axis.
            aes.name
        } else {
            labels.joinToString()
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
            DoubleSpan.encloseAllQ(filtered)
        }
    }

    internal fun mergeScaleOptions(scaleOptionsList: List<*>): HashMap<Aes<Any>, MutableMap<String, Any>> {
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
        return mergedOpts
    }

    internal fun createScaleConfigs(
        scaleOptionsList: List<*>,
        aopConversion: AesOptionConversion,
        tz: TimeZone?,
    ): List<ScaleConfig<Any>> {
        val mergedOpts = mergeScaleOptions(scaleOptionsList)

        return mergedOpts.map { (aes, options) ->
            ScaleConfig(aes, options, aopConversion, tz)
        }
    }
}
