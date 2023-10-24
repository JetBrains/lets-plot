/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.builder.VarBinding
import org.jetbrains.letsPlot.core.plot.builder.scale.ScaleProvider
import org.jetbrains.letsPlot.core.plot.builder.scale.ScaleProviderBuilder
import org.jetbrains.letsPlot.core.plot.builder.scale.ScaleProviderHelper
import org.jetbrains.letsPlot.core.spec.PlotConfigUtil

internal object PlotConfigScaleProviders {

    internal fun createScaleProviders(
        layerConfigs: List<LayerConfig>,
        scaleConfigs: List<ScaleConfig<Any>>,
        excludeStatVariables: Boolean,
        zeroPositionalExpands: Boolean
    ): Map<Aes<*>, ScaleProvider> {

        val scaleProviderBuilderByAes = HashMap<Aes<*>, ScaleProviderBuilder<*>>()

        // Create 'configured' scale providers.
        for (scaleConfig in scaleConfigs) {
            scaleProviderBuilderByAes[scaleConfig.aes] = scaleConfig.createScaleProviderBuilder()
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
            .filter { aes -> aes !in scaleProviderBuilderByAes }
            .forEach { aes ->
                val name = PlotConfigUtil.defaultScaleName(aes, variablesByMappedAes)
                scaleProviderBuilderByAes[aes] = ScaleProviderHelper.createDateTimeScaleProviderBuilder(aes, name)
            }

        // All aes used in bindings and x/y aes.
        // Exclude "stat positional" because we don't know which of axis they will use (i.e. orientation="y").
        val aesSet = setup.mappedAesWithoutStatPositional() + setOf(Aes.X, Aes.Y)

        // Append all the rest scale providers.
        val scaleProviderBuilders = aesSet.associateWith { aes ->
            val scaleAes = when {
                Aes.isPositionalX(aes) -> Aes.X
                Aes.isPositionalY(aes) -> Aes.Y
                else -> aes
            }

            scaleProviderBuilderByAes.getOrElse(scaleAes) { ScaleProviderBuilder(scaleAes) }
        }

        if (zeroPositionalExpands) {
            scaleProviderBuilders.forEach { (aes, builder) ->
                if (Aes.isPositional(aes)) {
                    builder.additiveExpand(0.0)
                    builder.multiplicativeExpand(0.0)
                }
            }
        }

        return scaleProviderBuilders.mapValues { (_, builder) -> builder.build() }
    }
}
