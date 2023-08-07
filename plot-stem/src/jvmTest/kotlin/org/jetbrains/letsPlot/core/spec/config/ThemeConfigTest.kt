/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.commons.values.Color
import kotlin.test.Test
import kotlin.test.assertEquals

class ThemeConfigTest {

    @Test
    fun default() {
        val spec = plotSpec()
        val colors = transformToClientPlotConfig(spec).theme.colors()

        assertEquals(Color.parseHex("#474747"), colors.pen())
        assertEquals(Color.WHITE, colors.paper())
        assertEquals(Color.PACIFIC_BLUE, colors.brush())
    }

    @Test
    fun withThemeName() {
        val spec = plotSpec(themeName = "none")
        val colors = transformToClientPlotConfig(spec).theme.colors()

        assertEquals(Color.parseHex("#474747"), colors.pen())
        assertEquals(Color.WHITE, colors.paper())
        assertEquals(Color.PACIFIC_BLUE, colors.brush())
    }

    @Test
    fun withFlavor() {
        val spec = plotSpec(flavorName = "darcula")
        val colors = transformToClientPlotConfig(spec).theme.colors()

        assertEquals(Color.parseHex("#BBBBBB"), colors.pen())
        assertEquals(Color.parseHex("#303030"), colors.paper())
        assertEquals(Color.PACIFIC_BLUE, colors.brush())
    }

    @Test
    fun withCustomColors() {
        val customColors = "'geom': { 'pen': 'red', 'paper': 'green', 'brush': 'blue' }"
        val spec = plotSpec(customColors = customColors)
        val colors = transformToClientPlotConfig(spec).theme.colors()

        assertEquals(Color.RED, colors.pen())
        assertEquals(Color.GREEN, colors.paper())
        assertEquals(Color.BLUE, colors.brush())
    }

    @Test
    fun `theme(geom) + flavor = use flavor colors`() {
        val customColors = "'geom': { 'pen': 'red', 'paper': 'green', 'brush': 'blue' }"
        val spec = plotSpec(flavorName = "darcula", customColors = customColors, flavorOverCustomColors = true)
        val colors = transformToClientPlotConfig(spec).theme.colors()

        assertEquals(Color.parseHex("#BBBBBB"), colors.pen())
        assertEquals(Color.parseHex("#303030"), colors.paper())
        assertEquals(Color.PACIFIC_BLUE, colors.brush())
    }

    @Test
    fun `flavor + theme(geom) = use custom colors`() {
        val customColors = "'geom': { 'pen': 'red', 'paper': 'green', 'brush': 'blue' }"
        val spec = plotSpec(flavorName = "darcula", customColors = customColors, flavorOverCustomColors = false)
        val colors = transformToClientPlotConfig(spec).theme.colors()

        assertEquals(Color.RED, colors.pen())
        assertEquals(Color.GREEN, colors.paper())
        assertEquals(Color.BLUE, colors.brush())
    }

    private fun plotSpec(
        themeName: String? = null,
        flavorName: String? = null,
        customColors: String? = null,
        flavorOverCustomColors: Boolean = true
    ): String {
        val themeNameOpts = themeName?.let { "'name': '$themeName'" } ?: ""
        val flavorOpts = flavorName?.let { "'flavor': '$flavorName'" } ?: ""

        val themeSettings = (listOf(
            themeNameOpts
        ) + if (flavorOverCustomColors) {
            listOf(customColors, flavorOpts)
        } else {
            listOf(flavorOpts, customColors)
        }
                ).filterNot(String?::isNullOrEmpty).joinToString()

        return """
            {
              'theme': { $themeSettings },
              'kind': 'plot',
              'layers': []
            }""".trimIndent()
    }
}