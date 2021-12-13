/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.scale.transform.Transforms
import jetbrains.datalore.plot.base.scale.transform.Transforms.ensureApplicableDomain
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.assemble.PlotFacets
import jetbrains.datalore.plot.builder.assemble.TypedScaleMap
import jetbrains.datalore.plot.builder.scale.ContinuousOnlyMapperProvider
import jetbrains.datalore.plot.builder.scale.DiscreteOnlyMapperProvider
import jetbrains.datalore.plot.builder.scale.ScaleProvider
import jetbrains.datalore.plot.builder.scale.ScaleProviderHelper
import jetbrains.datalore.plot.common.data.SeriesUtil
import jetbrains.datalore.plot.config.PlotConfig.Companion.PLOT_COMPUTATION_MESSAGES

object PlotConfigUtil {

    fun toLayersDataByTile(dataByLayer: List<DataFrame>, facets: PlotFacets): List<List<DataFrame>> {
        // Plot consists of one or more tiles,
        // each tile consists of layers

        val layersDataByTile: List<MutableList<DataFrame>> =
            if (facets.isDefined) {
                List(facets.numTiles) { ArrayList() }
            } else {
                // Just one tile.
                listOf(ArrayList())
            }

        for (layerData in dataByLayer) {
            if (facets.isDefined) {
                val dataByTile = facets.dataByTile(layerData)
                for ((tileIndex, tileData) in dataByTile.withIndex()) {
                    layersDataByTile[tileIndex].add(tileData)
                }
            } else {
                layersDataByTile[0].add(layerData)
            }
        }
        return layersDataByTile
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
    fun findComputationMessages(spec: Map<String, Any>): List<String> {
        val result: List<String> =
            when {
                PlotConfig.isPlotSpec(spec) -> getComputationMessages(spec)
                PlotConfig.isGGBunchSpec(spec) -> {
                    val bunchConfig = BunchConfig(spec)
                    bunchConfig.bunchItems.flatMap { getComputationMessages(it.featureSpec) }
                }
                else -> throw RuntimeException("Unexpected plot spec kind: ${PlotConfig.specKind(spec)}")
            }

        return result.distinct()
    }

    private fun getComputationMessages(opts: Map<String, Any>): List<String> {
        return getComputationMessages(OptionsAccessor(opts))
    }

    private fun getComputationMessages(accessor: OptionsAccessor): List<String> {
        return accessor.getList(PLOT_COMPUTATION_MESSAGES).map { it as String }
    }

    private fun getVarBindings(
        layerConfigs: List<LayerConfig>,
        excludeStatVariables: Boolean
    ): List<VarBinding> {
        return layerConfigs
            .flatMap { it.varBindings }
            .filter { !(excludeStatVariables && it.variable.isStat) }
    }

    internal fun createScaleProviders(
        layerConfigs: List<LayerConfig>,
        scaleConfigs: List<ScaleConfig<Any>>,
        excludeStatVariables: Boolean
    ): Map<Aes<*>, ScaleProvider<*>> {

        val varBindings = getVarBindings(layerConfigs, excludeStatVariables)
        val aesSet = varBindings.map(VarBinding::aes).toSet() + setOf(Aes.X, Aes.Y)

        val scaleProviderByAes = HashMap<Aes<*>, ScaleProvider<*>>()

        // Create 'configured' scale providers.
        for (scaleConfig in scaleConfigs) {
            val scaleProvider = scaleConfig.createScaleProvider()
            scaleProviderByAes[scaleConfig.aes] = scaleProvider
        }

        // Append date-time scale provider
        val variablesByMappedAes = associateAesWithMappedVariables(varBindings)
        associateVarBindingsWithData(layerConfigs, excludeStatVariables)
            .filter { (varBinding, df) -> df.isDateTime(varBinding.variable) }
            .map { (varBinding, _) -> varBinding.aes }
            .filter { aes -> aes in setOf(Aes.X, Aes.Y) }
            .filter { aes -> aes !in scaleProviderByAes }
            .forEach { aes ->
                val name = defaultScaleName(aes, variablesByMappedAes)
                scaleProviderByAes[aes] = ScaleProviderHelper.createDateTimeScaleProvider(aes, name)
            }

        // Append all the rest scale providers.
        return aesSet.associateWith {
            ScaleProviderHelper.getOrCreateDefault(it, scaleProviderByAes)
        }
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
        layerConfigs: List<LayerConfig>,
        excludeStatVariables: Boolean
    ): Map<VarBinding, DataFrame> {
        val dataByVarBinding: Map<VarBinding, DataFrame> = layerConfigs
            .flatMap { layer ->
                layer.varBindings
                    .filter { !(excludeStatVariables && it.variable.isStat) }
                    .map { it to layer.combinedData }
            }.toMap()

        // Check that all variables in bindings are mapped to data.
        for ((varBinding, data) in dataByVarBinding) {
            val variable = varBinding.variable
            require(data.has(variable)) {
                "Undefined variable: '${variable.name}'. Variables in data frame: ${
                    data.variables().map { "'${it.name}'" }
                }"
            }
        }

        return dataByVarBinding
    }

    internal fun createTransforms(
        layerConfigs: List<LayerConfig>,
        scaleProviderByAes: Map<Aes<*>, ScaleProvider<*>>,
        excludeStatVariables: Boolean
    ): Map<Aes<*>, Transform> {
        val dataByVarBinding = associateVarBindingsWithData(
            layerConfigs,
            excludeStatVariables
        )

        val variablesByMappedAes = associateAesWithMappedVariables(
            getVarBindings(layerConfigs, excludeStatVariables)
        )

        // All aes used in bindings.
        val aesSet: Set<Aes<*>> = dataByVarBinding.keys.map { it.aes }.toSet()

        // Compute domains for all aes with discrete input.

        // Extract "discrete" aes set.
        val discreteAesSet: MutableSet<Aes<*>> = HashSet()
        for (aes in aesSet) {
            val scaleProvider = scaleProviderByAes.getValue(aes)
            if (scaleProvider.discreteDomain) {
                discreteAesSet.add(aes)
            } else if (variablesByMappedAes.containsKey(aes)) {
                val variables = variablesByMappedAes.getValue(aes)
                val anyNotNumericData = variables.any {
                    val data = dataByVarBinding.getValue(VarBinding(it, aes))
                    if (data.isEmpty(it)) {
                        isDiscreteScaleForEmptyData(scaleProvider)
                    } else {
                        !data.isNumeric(it)
                    }
                }
                if (anyNotNumericData) {
                    discreteAesSet.add(aes)
                }
            }
        }

        // If axis is 'discrete' then put all 'positional' aes to 'discrete' aes set.
        val discreteX: Boolean = discreteAesSet.any { Aes.isPositionalX(it) }
        val discreteY: Boolean = discreteAesSet.any { Aes.isPositionalY(it) }
        for (aes in aesSet) {
            if (discreteX && Aes.isPositionalX(aes)) {
                discreteAesSet.add(aes)
            }
            if (discreteY && Aes.isPositionalY(aes)) {
                discreteAesSet.add(aes)
            }
        }

        // Discrete domains from 'data'.
        val discreteDataByVarBinding: Map<VarBinding, DataFrame> = dataByVarBinding.filterKeys {
            it.aes in discreteAesSet
        }
        val discreteDomainByAes = HashMap<Aes<*>, LinkedHashSet<Any>>()
        for ((varBinding, data) in discreteDataByVarBinding) {
            val aes = varBinding.aes
            val variable = varBinding.variable
            val factors = data.distinctValues(variable)
            discreteDomainByAes.getOrPut(aes) { LinkedHashSet() }.addAll(factors)
        }

        // create discrete transforms.
        val discreteTransformByAes = HashMap<Aes<*>, DiscreteTransform>()
        for (aes in discreteAesSet) {
            val scaleProvider = scaleProviderByAes.getValue(aes)
            val scaleBreaks = scaleProvider.breaks ?: emptyList()
            val domainValues = discreteDomainByAes.getValue(aes)
            val effectiveDomain = (scaleBreaks + domainValues).distinct()
            val transform = DiscreteTransform(
                domainValues = effectiveDomain,
                domainLimits = (scaleProvider.limits ?: emptyList()).filterNotNull()
            )
            discreteTransformByAes[aes] = transform
        }

        // create continuous transforms.
        val continuousTransformByAes = HashMap<Aes<*>, ContinuousTransform>()
        val continuousAesSet = aesSet - discreteAesSet
        for (aes in continuousAesSet) {
            continuousTransformByAes[aes] = scaleProviderByAes.getValue(aes).continuousTransform
        }

        // All 'positional' aes must use the same transform.
        fun axisTransform(axisAes: List<Aes<*>>, discrete: Boolean): Transform {
            @Suppress("CascadeIf")
            return if (discrete) {
                val domainValues = LinkedHashSet<Any>()
                val domainLimits = LinkedHashSet<Any>()
                for (aes in axisAes) {
                    val transform = discreteTransformByAes.getValue(aes)
                    domainValues.addAll(transform.domainValues)
                    domainLimits.addAll(transform.domainLimits)
                }
                DiscreteTransform(domainValues, domainLimits.toList())
            } else if (axisAes.isEmpty()) {
                Transforms.IDENTITY
            } else {
                continuousTransformByAes.getValue(axisAes.first())
            }
        }

        val xAxisTransform = axisTransform(aesSet.filter { Aes.isPositionalX(it) }, discreteX)
        val yAxisTransform = axisTransform(aesSet.filter { Aes.isPositionalY(it) }, discreteY)

        // Replace 'positional' transforms with 'axis' transform
        // and make sure that the mpp contains Aes.X and Aes.Y keys.
        @Suppress("UnnecessaryVariable")
        val allTransformsByAes: Map<Aes<*>, Transform> = (discreteTransformByAes + continuousTransformByAes)
            .mapValues { (aes, trans) ->
                when {
                    Aes.isPositionalX(aes) -> xAxisTransform
                    Aes.isPositionalY(aes) -> yAxisTransform
                    else -> trans
                }
            } + mapOf(
            Aes.X to xAxisTransform,
            Aes.Y to yAxisTransform,
        )

        return allTransformsByAes
    }

    private fun defaultScaleName(aes: Aes<*>, variablesByMappedAes: Map<Aes<*>, List<DataFrame.Variable>>): String {
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

    internal fun createScales(
        layerConfigs: List<LayerConfig>,
        transformByAes: Map<Aes<*>, Transform>,
        scaleProviderByAes: Map<Aes<*>, ScaleProvider<*>>,
        excludeStatVariables: Boolean
    ): TypedScaleMap {

        val dataByVarBinding = associateVarBindingsWithData(
            layerConfigs,
            excludeStatVariables
        )

        val variablesByMappedAes = associateAesWithMappedVariables(
            getVarBindings(layerConfigs, excludeStatVariables)
        )

        // All aes used in bindings.
        val aesSet: Set<Aes<*>> = dataByVarBinding.keys.map { it.aes }.toSet()

        // Compute domains for 'continuous' data
        // but exclude all 'positional' aes.
        //
        // Domains for X, Y axis are computed later.
        //      See: PlotAssemblerUtil.computePlotDryRunXYRanges()

        val continuousDomainByAesRaw = HashMap<Aes<*>, ClosedRange<Double>?>()

        // Continuois domains from 'data'
        for ((varBinding, data) in dataByVarBinding) {
            val aes = varBinding.aes
            val variable = varBinding.variable
            val transform = transformByAes.getValue(aes)

            if (transform is ContinuousTransform && !Aes.isPositionalXY(aes)) {
                continuousDomainByAesRaw[aes] = SeriesUtil.span(
                    continuousDomainByAesRaw[aes],
                    computeContinuousDomain(data, variable, transform)
                )
            }
        }

        // make sure all continuous domains are 'applicable range' (not emprty and not null)
        val continuousDomainByAes = continuousDomainByAesRaw.mapValues {
            val aes = it.key
            val transform: ContinuousTransform = transformByAes.getValue(aes) as ContinuousTransform
            ensureApplicableDomain(it.value, transform)
        }

        // Create scales for all aes.
        val scaleByAes = HashMap<Aes<*>, Scale<*>>()
        for (aes in aesSet + setOf(Aes.X, Aes.Y)) {
            val defaultName = defaultScaleName(aes, variablesByMappedAes)
            val scaleProvider = scaleProviderByAes.getValue(aes)

            @Suppress("MoveVariableDeclarationIntoWhen")
            val transform = transformByAes.getValue(aes)

            val scale = when (transform) {
                is DiscreteTransform -> scaleProvider.createScale(defaultName, transform.domainValues)
                else -> if (continuousDomainByAes.containsKey(aes)) {
                    val continuousDomain = continuousDomainByAes.getValue(aes)
                    scaleProvider.createScale(defaultName, continuousDomain)
                } else {
                    // Positional aes & continuous domain.
                    // The domain doesn't matter - it will be computed later (see: PlotAssemblerUtil.computePlotDryRunXYRanges())
                    scaleProvider.createScale(defaultName, ClosedRange.singleton(0.0))
                }
            }

            scaleByAes[aes] = scale
        }

        return TypedScaleMap(scaleByAes)
    }

    /**
     * ToDo: 'domans' should be computed on 'transformed' data.
     */
    private fun computeContinuousDomain(
        data: DataFrame,
        variable: DataFrame.Variable,
        transform: ContinuousTransform
    ): ClosedRange<Double>? {
        return if (!transform.hasDomainLimits()) {
            data.range(variable)
        } else {
            val filtered = data.getNumeric(variable).filter {
                transform.isInDomain(it)
            }
            SeriesUtil.range(filtered)
        }
    }

    private fun isDiscreteScaleForEmptyData(scaleProvider: ScaleProvider<*>): Boolean {
        // Empty data is neither 'discrete' nor 'numeric'.
        // Which scale to build?
        if (scaleProvider.discreteDomain) return true

        val mapperProvider = scaleProvider.mapperProvider
        if (mapperProvider is DiscreteOnlyMapperProvider) return true
        if (mapperProvider is ContinuousOnlyMapperProvider) return false

        val breaks = scaleProvider.breaks
        val limits = scaleProvider.limits

        val breaksAreDiscrete = breaks?.let {
            it.any { !(it is Number) }
        } ?: false

        val limitsAreDiscrete = limits?.let {
            // Not a list of 2 numbers.
            when {
                it.size > 2 -> true
                else -> it.filterNotNull().any { !(it is Number) }
            }
        } ?: false

        return breaksAreDiscrete || limitsAreDiscrete
    }
}
