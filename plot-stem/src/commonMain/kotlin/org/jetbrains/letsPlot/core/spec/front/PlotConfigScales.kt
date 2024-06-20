/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.front

import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.builder.assemble.GuideOptionsList
import org.jetbrains.letsPlot.core.plot.builder.scale.GuideMapper
import org.jetbrains.letsPlot.core.plot.builder.scale.ScaleProvider
import org.jetbrains.letsPlot.core.plot.builder.scale.WithGuideBreaks
import org.jetbrains.letsPlot.core.spec.PlotConfigUtil
import org.jetbrains.letsPlot.core.spec.config.LayerConfig

/**
 * Front-end.
 */
internal object PlotConfigScales {

    internal fun createScales(
        layerConfigs: List<LayerConfig>,
        transformByAes: Map<Aes<*>, Transform>,
        mappersByAes: Map<Aes<*>, ScaleMapper<*>>,
        scaleProviderByAes: Map<Aes<*>, ScaleProvider>,
        guideOptionsMap: Map<Aes<*>, GuideOptionsList>,
    ): Map<Aes<*>, Scale> {

        val setup = PlotConfigUtil.createPlotAesBindingSetup(
            layerConfigs,
            excludeStatVariables = false
        )

        // All aes used in bindings and x/y aes.
        // Exclude "stat positional" because we don't know which of axis they will use (i.e. orientation="y").
        // See also: PlotConfigClientSideUtil.createScalesForStatPositionalBindings()
        val aesSet = setup.mappedAesWithoutStatPositional() + setOf(Aes.X, Aes.Y)

        val variablesByMappedAes = setup.variablesByMappedAes

        // Create scales for all aes.
        val scaleByAes = HashMap<Aes<*>, Scale>()
        for (aes in aesSet) {
            val guideTitle = guideOptionsMap[aes]?.getTitle()
            val defaultName = PlotConfigUtil.defaultScaleName(aes, variablesByMappedAes)
            val scaleProvider = scaleProviderByAes.getValue(aes)

            val scale = when (val transform = transformByAes.getValue(aes)) {
                is DiscreteTransform -> scaleProvider.createScale(defaultName, transform, guideTitle)
                else -> {
                    transform as ContinuousTransform
                    val mapper = mappersByAes.getValue(aes)
                    val continuousRange = (mapper is GuideMapper && mapper.isContinuous)

                    @Suppress("UNCHECKED_CAST")
                    val guideBreaks: WithGuideBreaks<Any>? =
                        if (mapper is WithGuideBreaks<*>) mapper as WithGuideBreaks<Any>
                        else null

                    scaleProvider.createScale(defaultName, transform, continuousRange, guideBreaks, guideTitle)
                }
            }

            scaleByAes[aes] = scale
        }

        return scaleByAes
    }
}