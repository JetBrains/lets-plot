/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.scale.transform.Transforms
import org.jetbrains.letsPlot.core.plot.builder.VarBinding
import org.jetbrains.letsPlot.core.plot.builder.scale.ContinuousOnlyMapperProvider
import org.jetbrains.letsPlot.core.plot.builder.scale.DiscreteOnlyMapperProvider
import org.jetbrains.letsPlot.core.plot.builder.scale.MapperProvider
import org.jetbrains.letsPlot.core.plot.builder.scale.ScaleProvider
import org.jetbrains.letsPlot.core.spec.PlotConfigUtil.createPlotAesBindingSetup

internal object PlotConfigTransforms {
    internal fun createTransforms(
        layerConfigs: List<LayerConfig>,
        scaleProviderByAes: Map<Aes<*>, ScaleProvider>,
        mapperProviderByAes: Map<Aes<*>, MapperProvider<*>>,
        excludeStatVariables: Boolean
    ): Map<Aes<*>, Transform> {
        // X,Y scale - always.
        check(scaleProviderByAes.containsKey(Aes.X))
        check(scaleProviderByAes.containsKey(Aes.Y))
        check(mapperProviderByAes.containsKey(Aes.X))
        check(mapperProviderByAes.containsKey(Aes.Y))

        val setup = createPlotAesBindingSetup(layerConfigs, excludeStatVariables)

        // All aes used in bindings and x/y aes.
        // Exclude "stat positional" because we don't know which of axis they will use (i.e. orientation="y").
        val aesSet = setup.mappedAesWithoutStatPositional() + setOf(Aes.X, Aes.Y)
        val xAesSet = aesSet.filter { Aes.isPositionalX(it) }.toSet()
        val yAesSet = aesSet.filter { Aes.isPositionalY(it) }.toSet()

        val dataByVarBinding = setup.dataByVarBinding
        val variablesByMappedAes = setup.variablesByMappedAes

        // Compute domains for all aes with discrete input.

        // Extract "discrete" aes set.
        val discreteAesSet: MutableSet<Aes<*>> = HashSet()
        for (aes in aesSet) {
            val scaleProvider = scaleProviderByAes.getValue(aes)
            if (scaleProvider.discreteDomain) {
                discreteAesSet.add(aes)
            } else if (variablesByMappedAes.containsKey(aes)) {
                val variables = variablesByMappedAes.getValue(aes)
                val anyNotNumericData = variables.any { variable ->
                    val varBinding = VarBinding(variable, aes)
                    val data = dataByVarBinding.find { it.first == varBinding }?.second
                        ?: error("Missing binding $varBinding")
                    if (data.isEmpty(variable)) {
                        isDiscreteScaleForEmptyData(scaleProvider, mapperProviderByAes.getValue(aes))
                    } else {
                        data.isDiscrete(variable)
                    }
                }
                if (anyNotNumericData) {
                    discreteAesSet.add(aes)
                }
            }
        }

        // If axis is 'discrete' then put all 'positional' aes to 'discrete' aes set.
        val discreteX: Boolean = discreteAesSet.any { it in xAesSet }
        val discreteY: Boolean = discreteAesSet.any { it in yAesSet }
        if (discreteX) {
            discreteAesSet.addAll(xAesSet)
        }
        if (discreteY) {
            discreteAesSet.addAll(yAesSet)
        }

        // Discrete domains from 'data'.
        val discreteDomainByAes = discreteDomainByAes(
            discreteAesSet,
            dataByVarBinding
        )

        // create discrete transforms.
        val discreteTransformByAes = HashMap<Aes<*>, DiscreteTransform>()
        for (aes in discreteAesSet) {
            val scaleProvider = scaleProviderByAes.getValue(aes)
            val scaleBreaks = scaleProvider.breaks ?: emptyList()
            val domainValues = if (discreteDomainByAes.containsKey(aes)) {
                discreteDomainByAes.getValue(aes)
//            } else if (aes in setOf(Aes.X, Aes.Y)) {
//                // Aes x/y are always in the list, thus it's possible there is no data associated with x/y aes.
//                emptySet()
            } else {
                throw IllegalStateException("No discrete data found for aes $aes")
            }
            val effectiveDomain = (scaleBreaks + domainValues).distinct()

            val transformDomainValues = if (scaleProvider.discreteDomainReverse) {
                effectiveDomain.reversed()
            } else {
                effectiveDomain
            }
            val transformDomainLimits = (scaleProvider.limits ?: emptyList()).filterNotNull().let {
                if (scaleProvider.discreteDomainReverse) {
                    it.reversed()
                } else {
                    it
                }
            }

            val transform = DiscreteTransform(
                domainValues = transformDomainValues, domainLimits = transformDomainLimits
            )
            discreteTransformByAes[aes] = transform
        }

        // Create continuous transforms.
        val continuousTransformByAes = HashMap<Aes<*>, ContinuousTransform>()
        val continuousAesSet = aesSet - discreteAesSet
        for (aes in continuousAesSet) {
            if (Aes.isPositionalXY(aes) && !(aes == Aes.X || aes == Aes.Y)) {
                // Exclude all 'positional' aes except X, Y.
                continue
            }
            val scaleProvider = scaleProviderByAes.getValue(aes)
            val transform = scaleProvider.continuousTransform
            val limits = toContinuousLims(scaleProvider.limits, transform)
            val effectiveTransform = limits?.let {
                Transforms.continuousWithLimits(transform, it)
            } ?: transform
            continuousTransformByAes[aes] = effectiveTransform
        }

        // All 'positional' aes must use the same transform.
        fun joinDiscreteTransforms(axisAes: List<Aes<*>>): Transform {
            return DiscreteTransform.join(axisAes.map { discreteTransformByAes.getValue(it) })
        }

        val xAxisTransform = when (discreteX) {
            true -> joinDiscreteTransforms(xAesSet.toList())
            false -> continuousTransformByAes.getValue(Aes.X)
        }
        val yAxisTransform = when (discreteY) {
            true -> joinDiscreteTransforms(yAesSet.toList())
            false -> continuousTransformByAes.getValue(Aes.Y)
        }

        // Replace all 'positional' transforms with the 'axis' transform.
        val transformByPositionalAes: Map<Aes<*>, Transform> =
            xAesSet.associateWith { xAxisTransform } +
                    yAesSet.associateWith { yAxisTransform }

        return discreteTransformByAes + continuousTransformByAes + transformByPositionalAes
    }

    private fun isDiscreteScaleForEmptyData(
        scaleProvider: ScaleProvider,
        mapperProvider: MapperProvider<*>
    ): Boolean {
        // Empty data is neither 'discrete' nor 'numeric'.
        // Which scale to build?
        if (scaleProvider.discreteDomain) return true

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

    internal fun discreteDomainByAes(
        discreteAesSet: Set<Aes<*>>,
        dataByVarBinding: List<Pair<VarBinding, DataFrame>>,
    ): Map<Aes<*>, Collection<Any>> {
        val discreteDataByVarBinding: List<Pair<VarBinding, DataFrame>> = dataByVarBinding.filter {
            val binding = it.first
            val data = it.second
            val aes = binding.aes
            val variable = binding.variable
            val include = when (aes in discreteAesSet) {
                true -> when (Aes.isPositionalXY(aes)) {
                    true -> {
                        // Positional variable must be "strictly discrete"
                        // See issue: https://github.com/JetBrains/lets-plot/issues/1323
                        !data.isEmpty(variable) && data.isDiscrete(variable)
                    }

                    else -> true
                }

                else -> false
            }
            include
        }

        val discreteDomainByAes = HashMap<Aes<*>, LinkedHashSet<Any>>().apply {
            // Initialize because each of "discrete aes" must be present
            // and some might not be present in `discreteDataByVarBinding` due to the filter above.
            discreteAesSet.forEach { aes ->
                this[aes] = LinkedHashSet()
            }
        }
        for ((varBinding, data) in discreteDataByVarBinding) {
            val aes = varBinding.aes
            val variable = varBinding.variable
            val factors = data.distinctValues(variable)
//            discreteDomainByAes.getOrPut(aes) { LinkedHashSet() }.addAll(factors)
            discreteDomainByAes.getValue(aes).addAll(factors)
        }
        return discreteDomainByAes
    }

    /**
     * rawLims : A pair of 2 "nullable" numbers (the second num can be omitted).
     */
    private fun toContinuousLims(rawLims: List<Any?>?, transform: ContinuousTransform): Pair<Double?, Double?>? {
        if (rawLims == null) return null
        val lims2 = rawLims.take(2)
        val lims2d = lims2.map {
            if (it != null) {
                require(it is Number && it.toDouble().isFinite()) { "Numbers expected: limits=$lims2" }
                it.toDouble().let { if (transform.isInDomain(it)) it else null }
            } else {
                null
            }
        }

        val limsSorted = when (lims2d.filterNotNull().size) {
            0 -> null
            2 -> {
                @Suppress("UNCHECKED_CAST") (lims2d as List<Double>).sorted()
            }

            else -> lims2d + listOf(null)
        }

        return limsSorted?.let { Pair(it[0], it[1]) }
    }

}