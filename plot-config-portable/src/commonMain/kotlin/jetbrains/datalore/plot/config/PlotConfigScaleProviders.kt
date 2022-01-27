/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.scale.ScaleProvider
import jetbrains.datalore.plot.builder.scale.ScaleProviderHelper

internal object PlotConfigScaleProviders {

    internal fun createScaleProviders(
        layerConfigs: List<LayerConfig>,
        scaleConfigs: List<ScaleConfig<Any>>,
        excludeStatVariables: Boolean
    ): Map<Aes<*>, ScaleProvider<*>> {

        val varBindings = PlotConfigUtil.getVarBindings(layerConfigs, excludeStatVariables)
        val aesSet = varBindings.map(VarBinding::aes).toSet() + setOf(Aes.X, Aes.Y)

        val scaleProviderByAes = HashMap<Aes<*>, ScaleProvider<*>>()

        // Create 'configured' scale providers.
        for (scaleConfig in scaleConfigs) {
            val scaleProvider = scaleConfig.createScaleProvider()
            scaleProviderByAes[scaleConfig.aes] = scaleProvider
        }

        // Append date-time scale provider
        val variablesByMappedAes = PlotConfigUtil.associateAesWithMappedVariables(varBindings)
        val dateTimeDataByVarBinding = PlotConfigUtil.associateVarBindingsWithData(layerConfigs, excludeStatVariables)
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
        return aesSet.associateWith {
            ScaleProviderHelper.getOrCreateDefault(it, scaleProviderByAes)
        }
    }
}