/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.builder.scale.ScaleProvider
import jetbrains.datalore.plot.builder.scale.ScaleProviderHelper

internal object PlotConfigScaleProviders {

    internal fun createScaleProviders(
        layerConfigs: List<LayerConfig>,
        scaleConfigs: List<ScaleConfig<Any>>,
        excludeStatVariables: Boolean
    ): Map<Aes<*>, ScaleProvider<*>> {

        val scaleProviderByAes = HashMap<Aes<*>, ScaleProvider<*>>()

        // Create 'configured' scale providers.
        for (scaleConfig in scaleConfigs) {
            val scaleProvider = scaleConfig.createScaleProvider()
            scaleProviderByAes[scaleConfig.aes] = scaleProvider
        }

        val setup = PlotConfigUtil.createPlotAesBindingSetup(layerConfigs, excludeStatVariables)

        // All aes used in bindings and x/y aes.
        val aesSet: Set<Aes<*>> = setup.mappedAesSet + setOf(Aes.X, Aes.Y)
        val dataByVarBinding = setup.dataByVarBinding
        val variablesByMappedAes = setup.variablesByMappedAes

        // Append date-time scale provider
        val dateTimeDataByVarBinding = dataByVarBinding
            .filter { (varBinding, df) ->
                df.isDateTime(varBinding.variable)
            }

        dateTimeDataByVarBinding
            .map { (varBinding, _) -> varBinding.aes }
            .filter { aes -> aes in setOf(Aes.X, Aes.Y) }
            .filter { aes -> aes !in scaleProviderByAes }
            .forEach { aes ->
                val name = PlotConfigUtil.defaultScaleName(aes, variablesByMappedAes)
                scaleProviderByAes[aes] = ScaleProviderHelper.createDateTimeScaleProvider(aes, name)
            }

        // Append all the rest scale providers.
        return aesSet.associateWith { aes ->
            val scaleAes = when {
                setup.isXAxis(aes) -> Aes.X
                setup.isYAxis(aes) -> Aes.Y
                else -> aes
            }

            scaleProviderByAes.getOrElse(scaleAes) {
                ScaleProviderHelper.createDefault(scaleAes)
            }
        }
    }
}