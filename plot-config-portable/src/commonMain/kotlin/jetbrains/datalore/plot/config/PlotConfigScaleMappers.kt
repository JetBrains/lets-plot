/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.scale.Mappers
import org.jetbrains.letsPlot.core.plot.base.scale.transform.Transforms
import jetbrains.datalore.plot.builder.scale.MapperProvider
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil

/**
 * Front-end.
 */
internal object PlotConfigScaleMappers {

    /**
     * Note: 'positional' mappers do not yet know the output range (i.e. axis length).
     */
    internal fun createMappers(
        layerConfigs: List<LayerConfig>,
        transformByAes: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, Transform>,
        mapperProviderByAes: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, MapperProvider<*>>,
    ): Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, ScaleMapper<*>> {
        // X,Y scale - always.
        check(transformByAes.containsKey(org.jetbrains.letsPlot.core.plot.base.Aes.X))
        check(transformByAes.containsKey(org.jetbrains.letsPlot.core.plot.base.Aes.Y))
        check(mapperProviderByAes.containsKey(org.jetbrains.letsPlot.core.plot.base.Aes.X))
        check(mapperProviderByAes.containsKey(org.jetbrains.letsPlot.core.plot.base.Aes.Y))

        val setup = PlotConfigUtil.createPlotAesBindingSetup(
            layerConfigs,
            excludeStatVariables = false
        )

        // All aes used in bindings and x/y aes.
        val aesSet = setup.mappedAesWithoutStatPositional() + setOf(org.jetbrains.letsPlot.core.plot.base.Aes.X, org.jetbrains.letsPlot.core.plot.base.Aes.Y)
        val dataByVarBinding = setup.dataByVarBindingWithoutStatPositional()

        val variablesByMappedAes = setup.variablesByMappedAes

        // Compute domains for 'continuous' data.
        //
        // Note: domains for positional Aes are not needed and not computed.
        // Effective domains for X, Y axis are computed later.
        //      See: PlotAssemblerUtil.computePlotDryRunXYRanges()

        val continuousDomainByAes = HashMap<org.jetbrains.letsPlot.core.plot.base.Aes<*>, DoubleSpan>()
        transformByAes.getValue(org.jetbrains.letsPlot.core.plot.base.Aes.X).let {
            if (it is ContinuousTransform) {
                continuousDomainByAes[org.jetbrains.letsPlot.core.plot.base.Aes.X] = it.createApplicableDomain()
            }
        }
        transformByAes.getValue(org.jetbrains.letsPlot.core.plot.base.Aes.Y).let {
            if (it is ContinuousTransform) {
                continuousDomainByAes[org.jetbrains.letsPlot.core.plot.base.Aes.Y] = it.createApplicableDomain()
            }
        }

        // Continuous domains from 'data'.
        for ((varBinding, data) in dataByVarBinding) {
            val aes = varBinding.aes
            val variable = varBinding.variable
            val transform = transformByAes.getValue(aes)

            if (transform is ContinuousTransform) {
                val domain = if (org.jetbrains.letsPlot.core.plot.base.Aes.isPositionalXY(aes)) {
                    transform.createApplicableDomain()
                } else {
                    val domainRaw = SeriesUtil.span(
                        continuousDomainByAes[aes], PlotConfigUtil.computeContinuousDomain(data, variable, transform)
                    )
                    Transforms.ensureApplicableDomain(domainRaw, transform)
                }
                continuousDomainByAes[aes] = domain
            }
        }

        // Create mappers for all aes.
        val mappers = HashMap<org.jetbrains.letsPlot.core.plot.base.Aes<*>, ScaleMapper<*>>()
        for (aes in aesSet) {
            val defaultName = PlotConfigUtil.defaultScaleName(aes, variablesByMappedAes)
            val mapperProvider = mapperProviderByAes.getValue(aes)
            val scaleMapper: ScaleMapper<*> = when (val transform = transformByAes.getValue(aes)) {
                is DiscreteTransform -> {
                    if (transform.effectiveDomain.isEmpty()) {
                        Mappers.emptyDataMapper(defaultName)
                    } else {
                        mapperProvider.createDiscreteMapper(transform)
                    }
                }
                else -> {
                    val continuousDomain = continuousDomainByAes.getValue(aes)
                    mapperProvider.createContinuousMapper(
                        continuousDomain,
                        transform as ContinuousTransform
                    )
                }
            }

            mappers[aes] = scaleMapper
        }

        return mappers
    }
}