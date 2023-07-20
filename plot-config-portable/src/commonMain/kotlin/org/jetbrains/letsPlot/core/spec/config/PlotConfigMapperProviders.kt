/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.builder.scale.DefaultMapperProvider
import org.jetbrains.letsPlot.core.plot.builder.scale.MapperProvider
import org.jetbrains.letsPlot.core.spec.PlotConfigUtil

internal object PlotConfigMapperProviders {
    internal fun createMapperProviders(
        layerConfigs: List<LayerConfig>,
        scaleConfigs: List<ScaleConfig<Any>>,
        excludeStatVariables: Boolean
    ): Map<Aes<*>, MapperProvider<*>> {

        val setup = PlotConfigUtil.createPlotAesBindingSetup(layerConfigs, excludeStatVariables)

        // All aes used in bindings and x/y aes.
        val aesSet = setup.mappedAesWithoutStatPositional() + setOf(Aes.X, Aes.Y)

        val defaultProviders = aesSet.associateWith { DefaultMapperProvider[it] }
        val configuredProviders = scaleConfigs.map {
            it.aes to it.createMapperProvider()
        }.toMap()

        return defaultProviders + configuredProviders
    }
}