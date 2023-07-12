/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import org.jetbrains.letsPlot.core.plot.base.Aes
import jetbrains.datalore.plot.builder.scale.DefaultMapperProvider
import jetbrains.datalore.plot.builder.scale.MapperProvider

internal object PlotConfigMapperProviders {
    internal fun createMapperProviders(
        layerConfigs: List<LayerConfig>,
        scaleConfigs: List<ScaleConfig<Any>>,
        excludeStatVariables: Boolean
    ): Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, MapperProvider<*>> {

        val setup = PlotConfigUtil.createPlotAesBindingSetup(layerConfigs, excludeStatVariables)

        // All aes used in bindings and x/y aes.
        val aesSet = setup.mappedAesWithoutStatPositional() + setOf(org.jetbrains.letsPlot.core.plot.base.Aes.X, org.jetbrains.letsPlot.core.plot.base.Aes.Y)

        val defaultProviders = aesSet.associateWith { DefaultMapperProvider[it] }
        val configuredProviders = scaleConfigs.map {
            it.aes to it.createMapperProvider()
        }.toMap()

        return defaultProviders + configuredProviders
    }
}