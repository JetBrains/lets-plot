/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.builder.VarBinding
import org.jetbrains.letsPlot.core.plot.builder.scale.ScaleProvider
import org.jetbrains.letsPlot.core.plot.builder.scale.ScaleProviderHelper
import org.jetbrains.letsPlot.core.spec.PlotConfigUtil

internal object PlotConfigScaleProviders {

    internal fun createScaleProviders(
        layerConfigs: List<LayerConfig>,
        scaleConfigs: List<ScaleConfig<Any>>,
        excludeStatVariables: Boolean
    ): Map<Aes<*>, ScaleProvider> {

        val scaleProviderByAes = HashMap<Aes<*>, ScaleProvider>()

        // Create 'configured' scale providers.
        for (scaleConfig in scaleConfigs) {
            val scaleProvider = scaleConfig.createScaleProvider()
            scaleProviderByAes[scaleConfig.aes] = scaleProvider
        }

        val setup = PlotConfigUtil.createPlotAesBindingSetup(layerConfigs, excludeStatVariables)

        val dataByVarBinding = setup.dataByVarBinding
        val variablesByMappedAes = setup.variablesByMappedAes

        // Append date-time scale provider
        val dateTimeAesByVarBinding = dataByVarBinding
            .filter { (varBinding, df) -> df.isDateTime(varBinding.variable) }
            .keys
            .map(VarBinding::aes)

        // Axis that don't have an explicit mapping but have a corresponding positional mapping to a datetime variable
        val dateTimeAxisAesByPositionalVarBinding = listOfNotNull(
            if (dateTimeAesByVarBinding.any(Aes.Companion::isPositionalX)) Aes.X else null,
            if (dateTimeAesByVarBinding.any(Aes.Companion::isPositionalY)) Aes.Y else null,
        )

        (dateTimeAesByVarBinding + dateTimeAxisAesByPositionalVarBinding)
            .distinct()
            .filter { aes -> aes !in scaleProviderByAes }
            .forEach { aes ->
                val name = PlotConfigUtil.defaultScaleName(aes, variablesByMappedAes)
                scaleProviderByAes[aes] = ScaleProviderHelper.createDateTimeScaleProvider(aes, name)
            }

        // All aes used in bindings and x/y aes.
        // Exclude "stat positional" because we don't know which of axis they will use (i.e. orientation="y").
        val aesSet = setup.mappedAesWithoutStatPositional() + setOf(Aes.X, Aes.Y)

        // Append all the rest scale providers.
        return aesSet.associateWith { aes ->
            val scaleAes = when {
                Aes.isPositionalX(aes) -> Aes.X
                Aes.isPositionalY(aes) -> Aes.Y
                else -> aes
            }

            scaleProviderByAes.getOrElse(scaleAes) {
                ScaleProviderHelper.createDefault(scaleAes)
            }
        }
    }
}