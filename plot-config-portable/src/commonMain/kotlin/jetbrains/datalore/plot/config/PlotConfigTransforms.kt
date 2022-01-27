/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.scale.transform.Transforms
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.scale.ContinuousOnlyMapperProvider
import jetbrains.datalore.plot.builder.scale.DiscreteOnlyMapperProvider
import jetbrains.datalore.plot.builder.scale.MapperProvider
import jetbrains.datalore.plot.builder.scale.ScaleProvider

internal object PlotConfigTransforms {
    internal fun createTransforms(
        layerConfigs: List<LayerConfig>,
        scaleProviderByAes: Map<Aes<*>, ScaleProvider<*>>,
        mapperProviderByAes: Map<Aes<*>, MapperProvider<*>>,
        excludeStatVariables: Boolean
    ): Map<Aes<*>, Transform> {
        // X,Y scale - always.
        check(scaleProviderByAes.containsKey(Aes.X))
        check(scaleProviderByAes.containsKey(Aes.Y))
        check(mapperProviderByAes.containsKey(Aes.X))
        check(mapperProviderByAes.containsKey(Aes.Y))

        val dataByVarBinding = PlotConfigUtil.associateVarBindingsWithData(
            layerConfigs, excludeStatVariables
        )

        val variablesByMappedAes = PlotConfigUtil.associateAesWithMappedVariables(
            PlotConfigUtil.getVarBindings(layerConfigs, excludeStatVariables)
        )

        // All aes used in bindings and x/y aes.
        val aesSet: Set<Aes<*>> = dataByVarBinding.keys.map { it.aes }.toSet() +
                setOf(Aes.X, Aes.Y)

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
                        isDiscreteScaleForEmptyData(scaleProvider, mapperProviderByAes.getValue(aes))
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
        if (discreteX) {
            discreteAesSet.addAll(aesSet.filter { Aes.isPositionalX(it) })
        }
        if (discreteY) {
            discreteAesSet.addAll(aesSet.filter { Aes.isPositionalY(it) })
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
            val domainValues = if (discreteDomainByAes.containsKey(aes)) {
                discreteDomainByAes.getValue(aes)
            } else if (aes in setOf(Aes.X, Aes.Y)) {
                // We always add x/y wherefore it's possible there is no data associated with x/y aes.
                emptySet()
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
            true -> joinDiscreteTransforms(aesSet.filter { Aes.isPositionalX(it) })
            false -> continuousTransformByAes.getValue(Aes.X)
        }
        val yAxisTransform = when (discreteY) {
            true -> joinDiscreteTransforms(aesSet.filter { Aes.isPositionalY(it) })
            false -> continuousTransformByAes.getValue(Aes.Y)
        }

        // Replace all 'positional' transforms with the 'axis' transform.
        val transformByPositionalAes: Map<Aes<*>, Transform> =
            aesSet.filter { Aes.isPositionalX(it) }
                .associateWith { xAxisTransform } +
                    aesSet.filter { Aes.isPositionalY(it) }
                        .associateWith { yAxisTransform }

        return discreteTransformByAes + continuousTransformByAes + transformByPositionalAes
    }

    private fun isDiscreteScaleForEmptyData(
        scaleProvider: ScaleProvider<*>,
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