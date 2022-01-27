/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.builder.assemble.TypedScaleMap
import jetbrains.datalore.plot.builder.scale.GuideMapper
import jetbrains.datalore.plot.builder.scale.ScaleProvider
import jetbrains.datalore.plot.builder.scale.WithGuideBreaks

/**
 * Front-end.
 */
internal object PlotConfigScales {

    internal fun createScales(
        layerConfigs: List<LayerConfig>,
        transformByAes: Map<Aes<*>, Transform>,
        mappersByAes: Map<Aes<*>, ScaleMapper<*>>,
        scaleProviderByAes: Map<Aes<*>, ScaleProvider<*>>,
    ): TypedScaleMap {

        val dataByVarBinding = PlotConfigUtil.associateVarBindingsWithData(
            layerConfigs,
            excludeStatVariables = false
        )

        val variablesByMappedAes = PlotConfigUtil.associateAesWithMappedVariables(
            PlotConfigUtil.getVarBindings(
                layerConfigs,
                excludeStatVariables = false
            )
        )

        // All aes used in bindings.
        val aesSet: Set<Aes<*>> = dataByVarBinding.keys.map { it.aes }.toSet()

        // Create scales for all aes.
        val scaleByAes = HashMap<Aes<*>, Scale<*>>()
        for (aes in aesSet + setOf(Aes.X, Aes.Y)) {
            val defaultName = PlotConfigUtil.defaultScaleName(aes, variablesByMappedAes)
            val scaleProvider = scaleProviderByAes.getValue(aes)

            @Suppress("MoveVariableDeclarationIntoWhen")
            val transform = transformByAes.getValue(aes)

            val scale = when (transform) {
                is DiscreteTransform -> scaleProvider.createScale(defaultName, transform)
                else -> {
                    transform as ContinuousTransform
                    val mapper = mappersByAes.getValue(aes)
                    val continuousRange = (mapper is GuideMapper && mapper.isContinuous)

                    @Suppress("UNCHECKED_CAST")
                    val guideBreaks: WithGuideBreaks<Any>? =
                        if (mapper is WithGuideBreaks<*>) mapper as WithGuideBreaks<Any>
                        else null

                    scaleProvider.createScale(defaultName, transform, continuousRange, guideBreaks)
                }
            }

            scaleByAes[aes] = scale
        }

        return TypedScaleMap(scaleByAes)
    }
}