/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.commons.data.DataType
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.theme.ExponentFormat
import org.jetbrains.letsPlot.core.plot.builder.scale.ScaleProvider
import org.jetbrains.letsPlot.core.plot.builder.scale.ScaleProviderBuilder
import org.jetbrains.letsPlot.core.plot.builder.scale.ScaleProviderHelper
import org.jetbrains.letsPlot.core.plot.builder.scale.ScaleProviderHelper.configureDateTimeScaleBreaks
import org.jetbrains.letsPlot.core.spec.Option.Scale.EXPAND
import org.jetbrains.letsPlot.core.spec.PlotConfigUtil

internal object PlotConfigScaleProviders {

    internal fun createScaleProviders(
        layerConfigs: List<LayerConfig>,
        scaleConfigs: List<ScaleConfig<Any>>,
        excludeStatVariables: Boolean,
        zeroPositionalExpands: Boolean,
        expFormat: ExponentFormat,
        dataType: (aes: Aes<*>) -> DataType,
    ): Map<Aes<*>, ScaleProvider> {

        val scaleProviderBuilderByAes = HashMap<Aes<*>, ScaleProviderBuilder<*>>()

        // Create 'configured' scale providers.
        for (scaleConfig in scaleConfigs) {
            scaleProviderBuilderByAes[scaleConfig.aes] = scaleConfig.createScaleProviderBuilder()
        }

        val setup = PlotConfigUtil.createPlotAesBindingSetup(layerConfigs, excludeStatVariables)

        val dataByVarBinding = setup.dataByVarBinding

        // Append date-time scale provider
        val dateTimeAesByVarBinding = dataByVarBinding
            .filter { (varBinding, df) -> df.isDateTime(varBinding.variable) }
            .map { (varBinding, _) -> varBinding.aes }

        // Axis that doesn't have an explicit mapping but have a corresponding positional mapping to a datetime variable
        val dateTimeAxisAesByPositionalVarBinding = listOfNotNull(
            if (dateTimeAesByVarBinding.any(Aes.Companion::isPositionalX)) Aes.X else null,
            if (dateTimeAesByVarBinding.any(Aes.Companion::isPositionalY)) Aes.Y else null,
        )

        // Date-time scale providers for all date-time aes.
        (dateTimeAesByVarBinding + dateTimeAxisAesByPositionalVarBinding)
            .distinct()
            .forEach { aes ->
                scaleProviderBuilderByAes[aes] = if (aes in scaleProviderBuilderByAes) {
                    // Update the existing scale provider (see issue #1348).
                    configureDateTimeScaleBreaks(
                        scaleProviderBuilderByAes.getValue(aes),
                        dateTimeFormatter = null,
                        dataType = dataType(aes),
                        tz = layerConfigs.firstOrNull()?.tz
                    )
                } else {
                    ScaleProviderHelper.createDateTimeScaleProviderBuilder(
                        aes,
                        dataType = dataType(aes),
                        tz = layerConfigs.firstOrNull()?.tz,
                    )
                }
            }

        // All aes used in bindings and x/y aes.
        // Exclude "stat positional" because we don't know which of axis they will use (i.e., orientation="y").
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
            val expandConfigs = scaleConfigs.associate { it.aes to it[EXPAND] }

            scaleProviderBuilders
                .filterKeys { Aes.isPositional(it) }
                .forEach { (aes, builder) ->
                    if (expandConfigs[aes] == null) {
                        builder.additiveExpand(0.0)
                        builder.multiplicativeExpand(0.0)
                    }
                }
        }

        val tz = layerConfigs.firstOrNull()?.tz
        return scaleProviderBuilders.mapValues { (aes, builder) ->
            builder
                .dataType(dataType(aes))
                .timeZone(tz)
                .exponentFormat(expFormat)
                .build()
        }
    }

}
