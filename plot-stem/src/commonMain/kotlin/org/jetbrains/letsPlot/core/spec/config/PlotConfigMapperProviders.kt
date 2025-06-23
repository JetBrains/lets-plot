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
        val configuredProviders: MutableMap<Aes<*>, MapperProvider<*>> = scaleConfigs.associate {
            it.aes to it.createMapperProvider()
        }.toMutableMap()

        // Use the configured SIZE and STROKE providers for SIZE_START/END and STROKE_START/END
        // (e.g. scale_size_area() will also be applied to size_start and size_end)
        configuredProviders[Aes.SIZE]?.let { sizeMapper ->
            configuredProviders.getOrPut(Aes.SIZE_START) { sizeMapper }
            configuredProviders.getOrPut(Aes.SIZE_END) { sizeMapper }
            configuredProviders.getOrPut(Aes.POINT_SIZE) { sizeMapper }
        }
        configuredProviders[Aes.STROKE]?.let { strokeMapper ->
            configuredProviders.getOrPut(Aes.STROKE_START) { strokeMapper }
            configuredProviders.getOrPut(Aes.STROKE_END) { strokeMapper }
            configuredProviders.getOrPut(Aes.POINT_STROKE) { strokeMapper }
        }

        return defaultProviders + configuredProviders
    }
}