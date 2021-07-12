/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.gcommon.collect.Lists
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.ContinuousTransform
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.assemble.PlotFacets
import jetbrains.datalore.plot.builder.assemble.TypedScaleMap
import jetbrains.datalore.plot.builder.assemble.TypedScaleProviderMap
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
        checkArgument(message != null)
        val computationMessages = ArrayList(
            getComputationMessages(
                accessor
            )
        )
        computationMessages.add(message!!)
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
        return Lists.transform(accessor.getList(PLOT_COMPUTATION_MESSAGES)) { it as String }
    }

    internal fun createScaleProviders(scaleConfigs: List<ScaleConfig<Any>>): TypedScaleProviderMap {
        val scaleProviderByAes = HashMap<Aes<*>, ScaleProvider<*>>()
        for (scaleConfig in scaleConfigs) {
            val scaleProvider = scaleConfig.createScaleProvider()
            scaleProviderByAes[scaleConfig.aes] = scaleProvider
        }
        return TypedScaleProviderMap(scaleProviderByAes)
    }

    internal fun createScales(
        layerConfigs: List<LayerConfig>,
        scaleProvidersMap: TypedScaleProviderMap,
        isClientSide: Boolean
    ): TypedScaleMap {
        val dataByVarBinding = layerConfigs
            .flatMap { layer ->
                layer.varBindings
                    .filter { isClientSide || !it.variable.isStat }
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

        val variablesByMappedAes: MutableMap<Aes<*>, MutableList<DataFrame.Variable>> = HashMap()
        for (varBinding in dataByVarBinding.keys) {
            val aes = varBinding.aes
            val variable = varBinding.variable
            variablesByMappedAes.getOrPut(aes) { ArrayList() }.add(variable)
        }

        // All (used) aes set.
        val aesSetAll: Set<Aes<*>> = dataByVarBinding.keys.map { it.aes }.toSet() +
                setOf(Aes.X, Aes.Y)  // allways create scales for X,Y aes.

        // Scale providers.
        val scaleProviderByAes: Map<Aes<*>, ScaleProvider<*>> = aesSetAll.map {
            val scaleProvider = ScaleProviderHelper.getOrCreateDefault(it, scaleProvidersMap)
            it to scaleProvider
        }.toMap()

        // Compute domains for some scales:
        //
        // - All "discrete" domains.
        //       "discrete" domains are needed for `DataProcessing.transformOriginals()`
        // - All "continuous" domains excluding "continuous positional" aes.
        //      Ranges for "continuous positional" aes (i.e. X,Y domains) are computed later.
        //      See: PlotAssemblerUtil.computePlotDryRunXYRanges()

        // Extract "discrete" aes set.
        val discreteAesSet: MutableSet<Aes<*>> = HashSet()
        for (aes in aesSetAll) {
            if (scaleProviderByAes.getValue(aes).discreteDomain) {
                discreteAesSet.add(aes)
            } else if (variablesByMappedAes.containsKey(aes)) {
                val variables = variablesByMappedAes.getValue(aes)
                val anyNotNumericData = variables.any {
                    val data = dataByVarBinding.getValue(VarBinding(it, aes))
                    !data.isNumeric(it)
                }
                if (anyNotNumericData) {
                    discreteAesSet.add(aes)
                }
            }
        }

        val discreteX: Boolean = discreteAesSet.any { Aes.isPositionalX(it) }
        val discreteY: Boolean = discreteAesSet.any { Aes.isPositionalY(it) }

        // Compute domains for all scales.
        // Combine all X,Y positional domains.
        val discreteDomainByReprAes = HashMap<Aes<*>, LinkedHashSet<Any>>()
        val continuousDomainByAesRaw = HashMap<Aes<*>, ClosedRange<Double>?>()

        fun isDiscrete(aes: Aes<*>): Boolean {
            return discreteAesSet.contains(aes) ||
                    (Aes.isPositionalX(aes) && discreteX) ||
                    (Aes.isPositionalY(aes) && discreteY)
        }

        fun reprAes(aes: Aes<*>): Aes<*> {
            return when {
                Aes.isPositionalX(aes) -> Aes.X
                Aes.isPositionalY(aes) -> Aes.Y
                else -> aes
            }
        }

        // domains from 'data'
        for ((varBinding, data) in dataByVarBinding) {
            val aes = varBinding.aes

            val variable = varBinding.variable
            if (isDiscrete(aes)) {
                val reprAes = reprAes(aes)
                // update discrete domain
                val factors = data.distinctValues(variable)
                discreteDomainByReprAes.getOrPut(reprAes) { LinkedHashSet() }.addAll(factors)
            } else if (!Aes.isPositionalXY(aes)) {
                // add domain for any "with continuous domain but not positional" aes.

                val scaleProvider = scaleProviderByAes.getValue(aes)

                continuousDomainByAesRaw[aes] = SeriesUtil.span(
                    continuousDomainByAesRaw[aes],
                    scaleProvider.computeContinuousDomain(data, variable)
                )
            }
        }

        // make sure all continuous domains are 'applicable range' (not emprty and not null)
        val continuousDomainByAes = continuousDomainByAesRaw.mapValues {
            val aes = it.key
            val transform: ContinuousTransform = scaleProviderByAes.getValue(aes).continuousTransform
            ensureApplicableDomain(it.value, transform)
        }

        // Create scales for all aes.
        fun defaultScaleName(aes: Aes<*>): String {
            return variablesByMappedAes[aes]
                ?.let { it.map { it.label }.distinct().joinToString() }
                ?: aes.name
        }

        val scaleByAes = HashMap<Aes<*>, Scale<*>>()
        for (aes in aesSetAll) {
            val defaultName = defaultScaleName(aes)
            val scaleProvider = scaleProviderByAes.getValue(aes)
            val reprAes = reprAes(aes)
            val scale = if (discreteDomainByReprAes.containsKey(reprAes)) {
                val discreteDomain = discreteDomainByReprAes.getValue(reprAes)
                scaleProvider.createScale(defaultName, discreteDomain)
            } else if (continuousDomainByAes.containsKey(aes)) {
                val continuousDomain = continuousDomainByAes.getValue(aes)
                scaleProvider.createScale(defaultName, continuousDomain)
            } else {
                // Must be positional-X,Y aes & continuous domain --> continuous scale.
                // The domain doesn't matter - it will be computed later (see: PlotAssemblerUtil.computePlotDryRunXYRanges())
                scaleProvider.createScale(defaultName, ClosedRange.singleton(0.0))
            }

            scaleByAes[aes] = scale
        }

        return TypedScaleMap(scaleByAes)
    }

    /**
     * ToDo: move to SeriesUtil (or better place)
     */
    fun ensureApplicableDomain(dataRange: ClosedRange<Double>?, transform: ContinuousTransform): ClosedRange<Double> {
        return if (dataRange == null) {
            transform.createApplicableDomain(0.0)
        } else if (SeriesUtil.isSubTiny(dataRange)) {
            transform.createApplicableDomain(dataRange.lowerEnd)
        } else {
            dataRange
        }
    }
}
