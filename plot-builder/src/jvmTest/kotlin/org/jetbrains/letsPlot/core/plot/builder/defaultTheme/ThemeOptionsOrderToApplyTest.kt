/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption
import org.jetbrains.letsPlot.core.plot.builder.presentation.DefaultFontFamilyRegistry
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.config.ThemeConfig
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertEquals


@RunWith(Parameterized::class)
class ThemeOptionsOrderToApplyTest(
    themeValues: Map<String, Any>,
    private val expectedBackground: Color,
    private val expectedYAxisColor: Color?
) {
    private val theme: Theme = ThemeConfig(themeValues, DefaultFontFamilyRegistry()).theme

    // The order should be as follows:
    //      named theme options
    //      + flavor colors
    //      + custom 'theme' options

    @Test
    fun eval() {
        assertEquals(expectedBackground, theme.plot().backgroundFill(), "Wrong '${ThemeOption.PLOT_BKGR_RECT}'")
        val isAxisBlank = expectedYAxisColor == null
        assertEquals(
            isAxisBlank,
            !theme.verticalAxis(flipAxis = false).showLine(),
            "Wrong '${ThemeOption.AXIS_LINE_Y}'"
        )
        if (!isAxisBlank) {
            assertEquals(
                expectedYAxisColor,
                theme.verticalAxis(flipAxis = false).lineColor(),
                "Wrong '${ThemeOption.AXIS_LINE_Y}'"
            )
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun params(): Collection<Array<Any?>> {
            val namedThemeOption = mapOf(Option.Meta.NAME to ThemeOption.Name.LP_MINIMAL)
            val flavorOption = mapOf(Option.Theme.FLAVOR to ThemeOption.Flavor.DARCULA)

            return listOf(
                arrayOf(namedThemeOption, Color.WHITE, null),
                arrayOf(namedThemeOption + flavorOption, Color.parseHex("#303030"), null),
                arrayOf(
                    namedThemeOption + flavorOption + mapOf(
                        ThemeOption.AXIS_LINE_Y to mapOf(ThemeOption.Elem.BLANK to false)
                    ),
                    Color.parseHex("#303030"), Color.parseHex("#BBBBBB")
                ),
                arrayOf(
                    namedThemeOption + flavorOption + mapOf(
                        ThemeOption.PLOT_BKGR_RECT to mapOf(ThemeOption.Elem.FILL to Color.RED),
                        ThemeOption.AXIS_LINE_Y to mapOf(
                            ThemeOption.Elem.COLOR to Color.BLUE,
                            ThemeOption.Elem.BLANK to false
                        )
                    ),
                    Color.RED, Color.BLUE
                )
            )
        }
    }
}