/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.scale.DefaultMapperProvider
import jetbrains.datalore.plot.builder.scale.MapperProvider

internal object PlotConfigMapperProviders {
    internal fun createMapperProviders(
        layerConfigs: List<LayerConfig>,
        scaleConfigs: List<ScaleConfig<Any>>,
        excludeStatVariables: Boolean
    ): Map<Aes<*>, MapperProvider<*>> {

        val varBindings = PlotConfigUtil.getVarBindings(layerConfigs, excludeStatVariables)
        val aesSet = varBindings.map(VarBinding::aes).toSet() + setOf(Aes.X, Aes.Y)

        val defaultProviders = aesSet.associateWith { DefaultMapperProvider[it] }
        val configuredProviders = scaleConfigs.map {
            it.aes to it.createMapperProvider()
        }.toMap()

        return defaultProviders + configuredProviders
    }
}