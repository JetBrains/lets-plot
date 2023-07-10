/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.scale.Mappers
import jetbrains.datalore.plot.base.scale.transform.Transforms
import jetbrains.datalore.plot.builder.scale.MapperProvider
import jetbrains.datalore.plot.common.data.SeriesUtil

/**
 * Front-end.
 */
internal object PlotConfigScaleMappers {

    /**
     * Note: 'positional' mappers do not yet know the output range (i.e. axis length).
     */
    internal fun createMappers(
        layerConfigs: List<LayerConfig>,
        transformByAes: Map<Aes<*>, Transform>,
        mapperProviderByAes: Map<Aes<*>, MapperProvider<*>>,
    ): Map<Aes<*>, ScaleMapper<*>> {
        // X,Y scale - always.
        check(transformByAes.containsKey(Aes.X))
        check(transformByAes.containsKey(Aes.Y))
        check(mapperProviderByAes.containsKey(Aes.X))
        check(mapperProviderByAes.containsKey(Aes.Y))

        val setup = PlotConfigUtil.createPlotAesBindingSetup(
            layerConfigs,
            excludeStatVariables = false
        )

        // All aes used in bindings and x/y aes.
        val aesSet = setup.mappedAesWithoutStatPositional() + setOf(Aes.X, Aes.Y)
        val dataByVarBinding = setup.dataByVarBindingWithoutStatPositional()

        val variablesByMappedAes = setup.variablesByMappedAes

        // Compute domains for 'continuous' data.
        //
        // Note: domains for positional Aes are not needed and not computed.
        // Effective domains for X, Y axis are computed later.
        //      See: PlotAssemblerUtil.computePlotDryRunXYRanges()

        val continuousDomainByAes = HashMap<Aes<*>, DoubleSpan>()
        transformByAes.getValue(Aes.X).let {
            if (it is ContinuousTransform) {
                continuousDomainByAes[Aes.X] = it.createApplicableDomain()
            }
        }
        transformByAes.getValue(Aes.Y).let {
            if (it is ContinuousTransform) {
                continuousDomainByAes[Aes.Y] = it.createApplicableDomain()
            }
        }

        // Continuous domains from 'data'.
        for ((varBinding, data) in dataByVarBinding) {
            val aes = varBinding.aes
            val variable = varBinding.variable
            val transform = transformByAes.getValue(aes)

            if (transform is ContinuousTransform) {
                val domain = if (Aes.isPositionalXY(aes)) {
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
        val mappers = HashMap<Aes<*>, ScaleMapper<*>>()
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