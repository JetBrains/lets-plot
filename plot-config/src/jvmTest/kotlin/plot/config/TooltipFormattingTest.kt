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

class TooltipFormattingTest {

    // Tooltip lines parsing tests

    @Test
    fun checkVariableNameWithSpace() {
        // Formatting for a variable with a space in its name:
        //  'variable name' -> ${var@variable name}

        val variableName = "variable name"
        val configName = configVariableName(variableName)
        configLayerWithTooltips(configTooltipLines = listOf(configName))
            .haveVariableNamesInTooltips(listOf(variableName))
    }

    @Test
    fun checkVariableNamesWithDollarSymbol() {
        // Formatting variables with '$' inside names:
        //  variable$ -> $var@variable$
        //  $variable -> $var@$variable
        //  vari$able -> $var@vari$able

        val variableNames = listOf(
            "variable$", "\$variable", "vari\$able"
        )
        configLayerWithTooltips(configTooltipLines = variableNames.map(::configVariableName))
            .haveVariableNamesInTooltips(variableNames)
    }

    @Test
    fun checkVariableNamesEqualToAesNames() {
        // Variables with names which match to the names of aes:
        //  color -> $var@color
        //  fill  -> $var@fill

        val variableNames = listOf(
            Aes.COLOR.name, Aes.FILL.name
        )
        configLayerWithTooltips(configTooltipLines = variableNames.map(::configVariableName))
            .haveVariableNamesInTooltips(variableNames)
            .haveAesNamesInTooltips(emptyList())
    }

    @Test
    fun checkAesNamesInTooltips() {
        // Aes names are prefixed with '$':
        //  color -> $color
        //  fill  -> $fill

        val aesNames = listOf(
            Aes.COLOR.name, Aes.FILL.name
        )
        configLayerWithTooltips(configTooltipLines = aesNames.map(::configAesName))
            .haveAesNamesInTooltips(aesNames)
            .haveVariableNamesInTooltips(emptyList())
    }

    @Test
    fun checkBothTypeNamesInTooltips() {
        //  color -> $color     - aes
        //  fill  -> $var@fill  - variable name

        val configNames = listOf(
            configAesName(Aes.COLOR.name),
            configVariableName(Aes.FILL.name)
        )
        configLayerWithTooltips(configTooltipLines = configNames)
            .haveAesNamesInTooltips(listOf(Aes.COLOR.name))
            .haveVariableNamesInTooltips(listOf(Aes.FILL.name))
    }

    @Test
    fun configValueSourceLabelsInTooltips() {
        val variableNames = listOf(
            "variable1",
            "variable2",
            "variable3",
            "variable4"
        )
        val tooltipLines = listOf(
            "\$var@variable1",          // no label
            "|\$var@variable2",         // empty label (no it's the same as 'no label')
            "@|\$var@variable3",        // default label = the variable name
            "MyLabel|\$var@variable4"   // set value for the label

        )
        configLayerWithTooltips(configTooltipLines = tooltipLines)
            .haveNameWithLabelsInTooltips(
                mapOf(
                    variableNames[0] to "",
                    variableNames[1] to "",
                    variableNames[2] to variableNames[2],
                    variableNames[3] to "MyLabel"
                )
            )
    }

    @Test
    fun checkNamesInComplicatedTooltipLines() {
        // Tooltips with multiple sources in the line:
        //  $x and $y
        //  ${var@variable name #1} '${var@variable name #2}' ($var@variable#3)

        val configAesX = configAesName(Aes.X.name)
        val configAesY = configAesName(Aes.Y.name)

        val variableNames = listOf(
            "variable name #1",
            "variable name #2",
            "variable#3"
        )
        val configVarNames = variableNames.map(::configVariableName)

        val tooltipLines = listOf(
            "$configAesX and $configAesY",
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
                    Option.Layer.TOOLTIPS to mapOf(
                        Option.Layer.TOOLTIP_LINES to configTooltipLines
                    )
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