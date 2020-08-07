/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.config.Option.PlotBase.MAPPING
import jetbrains.datalore.plot.server.config.ServerSideTestUtil
import jetbrains.datalore.plot.server.config.SingleLayerAssert
import kotlin.test.Test

class TooltipConfigTest {

    // Tooltip lines parsing tests

    @Test
    fun checkVariableNameWithSpace() {
        val variableName = "variable name"
        val configName = configVariableName(variableName)
        configLayerWithTooltips(configTooltipLines = listOf(configName))
            .haveVariableNamesInTooltips(listOf(variableName))
    }

    @Test
    fun checkVariableNamesWithDollarSymbol() {
        val variableNames = listOf(
            "variable$", "\$variable", "vari\$able"
        )
        configLayerWithTooltips(configTooltipLines = variableNames.map(::configVariableName))
            .haveVariableNamesInTooltips(variableNames)
    }

    @Test
    fun checkVariableNamesEqualToAesNames() {
        val variableNames = listOf(
            Aes.COLOR.name, Aes.FILL.name
        )
        configLayerWithTooltips(configTooltipLines = variableNames.map(::configVariableName))
            .haveVariableNamesInTooltips(variableNames)
            .haveAesNamesInTooltips(emptyList())
    }

    @Test
    fun checkAesNamesInTooltips() {
        val aesNames = listOf(
            Aes.COLOR.name, Aes.FILL.name
        )
        configLayerWithTooltips(configTooltipLines = aesNames.map(::configAesName))
            .haveAesNamesInTooltips(aesNames)
            .haveVariableNamesInTooltips(emptyList())
    }

    @Test
    fun checkBothTypeNamesInTooltips() {
        val configNames = listOf(
            configAesName(Aes.COLOR.name),
            configVariableName(Aes.FILL.name)
        )
        configLayerWithTooltips(configTooltipLines = configNames)
            .haveAesNamesInTooltips(listOf(Aes.COLOR.name))
            .haveVariableNamesInTooltips(listOf(Aes.FILL.name))
    }

    @Test
    fun checkNamesInComplicatedTooltipLines() {
        val configAesX = configAesName(Aes.X.name)
        val configAesY = configAesName(Aes.Y.name)

        val variableNames = listOf(
            "variable name #1",
            "variable name #2",
            "variable#3"
        )
        val configVarNames = variableNames.map(::configVariableName)

        val tooltipLines = listOf(
            "x and y|$configAesX and $configAesY",
            "${configVarNames[0]} '${configVarNames[1]}' (${configVarNames[2]})"
        )

        configLayerWithTooltips(configTooltipLines = tooltipLines)
            .haveAesNamesInTooltips(listOf(Aes.X.name, Aes.Y.name))
            .haveVariableNamesInTooltips(variableNames)
    }

    @Test
    fun noTooltipList() {
        configLayerWithTooltips(configTooltipLines = null)
            .haveAesNamesInTooltips(emptyList())
            .haveVariableNamesInTooltips(emptyList())
    }

    @Test
    fun withEmptyTooltipList() {
         configLayerWithTooltips(configTooltipLines = emptyList())
             .haveAesNamesInTooltips(emptyList())
             .haveVariableNamesInTooltips(emptyList())
    }

    private fun configLayerWithTooltips(configTooltipLines: List<String>?): SingleLayerAssert {
        val plotOpts = mutableMapOf(
            MAPPING to mapOf<String, Any>(),
            Option.Plot.LAYERS to listOf(
                mapOf(
                    Option.Layer.GEOM to Option.GeomName.POINT,
                    Option.Layer.TOOLTIP_LINES to configTooltipLines
                )
            )
        )
        val layerConfigs = ServerSideTestUtil.createLayerConfigsWithoutEncoding(plotOpts)
        return SingleLayerAssert.assertThat(layerConfigs)
    }

    private fun configVariableName(name: String): String {
        return if (name.contains(' ')) {
            "\${var@$name}"
        } else {
            "\$var@$name"
        }
    }

    private fun configAesName(name: String) = "\$$name"
}