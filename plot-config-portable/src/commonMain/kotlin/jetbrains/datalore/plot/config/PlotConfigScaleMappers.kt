/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.scale.Mappers
import jetbrains.datalore.plot.base.scale.transform.Transforms
import jetbrains.datalore.plot.builder.scale.MapperProvider
import jetbrains.datalore.plot.common.data.SeriesUtil

/**
 * Front-end.
 */
internal object PlotConfigScaleMappers {
    internal fun createNotPositionalMappers(
        layerConfigs: List<LayerConfig>,
        transformByAes: Map<Aes<*>, Transform>,
        mapperProviderByAes: Map<Aes<*>, MapperProvider<*>>,
    ): Map<Aes<*>, ScaleMapper<*>> {
        val dataByVarBinding = PlotConfigUtil.associateVarBindingsWithData(
            layerConfigs,
            excludeStatVariables = false
        ).filterKeys { !Aes.isPositional(it.aes) }

        val variablesByMappedAes = PlotConfigUtil.associateAesWithMappedVariables(
            PlotConfigUtil.getVarBindings(
                layerConfigs,
                excludeStatVariables = false
            )
        )

        // All aes used in bindings, except positional.
        val aesSet: Set<Aes<*>> = dataByVarBinding.keys.map { it.aes }.toSet()

        // Compute domains for 'continuous' data
        // but exclude all 'positional' aes.
        //
        // Domains for X, Y axis are computed later.
        //      See: PlotAssemblerUtil.computePlotDryRunXYRanges()

        val continuousDomainByAesRaw = HashMap<Aes<*>, ClosedRange<Double>?>()

        // Continuous domains from 'data'.
        for ((varBinding, data) in dataByVarBinding) {
            val aes = varBinding.aes
            val variable = varBinding.variable
            val transform = transformByAes.getValue(aes)

            if (transform is ContinuousTransform) {
                continuousDomainByAesRaw[aes] = SeriesUtil.span(
                    continuousDomainByAesRaw[aes], PlotConfigUtil.computeContinuousDomain(data, variable, transform)
                )
            }
        }

        // make sure all continuous domains are 'applicable range' (not emprty and not null)
        val continuousDomainByAes = continuousDomainByAesRaw.mapValues {
            val aes = it.key
            val transform: ContinuousTransform = transformByAes.getValue(aes) as ContinuousTransform
            Transforms.ensureApplicableDomain(it.value, transform)
        }

        // Create mappers for all aes.
        val mappers = HashMap<Aes<*>, ScaleMapper<*>>()
        for (aes in aesSet) {
            val defaultName = PlotConfigUtil.defaultScaleName(aes, variablesByMappedAes)
            val mapperProvider = mapperProviderByAes.getValue(aes)

            @Suppress("MoveVariableDeclarationIntoWhen")
            val transform = transformByAes.getValue(aes)

            val scaleMapper: ScaleMapper<*> = when (transform) {
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