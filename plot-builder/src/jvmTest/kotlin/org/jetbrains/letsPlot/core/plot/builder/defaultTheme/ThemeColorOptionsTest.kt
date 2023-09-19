/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.FLAVOR
import org.jetbrains.letsPlot.core.plot.builder.presentation.DefaultFontFamilyRegistry
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.config.ThemeConfig
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertEquals


@RunWith(Parameterized::class)
class ThemeColorOptionsTest(
    themeValues: Map<String, Any>,
    private val expected: List<Expected>
) {
    data class Expected(
        val expected: Any,
        val actualGetter: (Theme) -> Any,
        val paramName: String
    )

    private val theme: Theme = ThemeConfig(themeValues, DefaultFontFamilyRegistry()).theme

    // The order should be as follows:
    //      named theme options
    //      + flavor colors
    //      + custom 'theme' options

    @Test
    fun eval() {
        expected.forEach {
            assertEquals(it.expected, it.actualGetter(theme), "Wrong '${it.paramName}'")
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun params(): Collection<Array<Any>> {
            val minimalTheme = mapOf(Option.Meta.NAME to ThemeOption.Name.LP_MINIMAL)
            val lightTheme = mapOf(Option.Meta.NAME to ThemeOption.Name.R_LIGHT)
            val flavorOption = mapOf(FLAVOR to ThemeOption.Flavor.DARCULA)

            return listOf(
                test(
                    themeOptions = minimalTheme,
                    expectedPlotBackground = Color.WHITE,
                    expectedYAxisColor = null,
                    expectedPanelBackground = null
                ),
                test(
                    themeOptions = minimalTheme + mapOf(
                        ThemeOption.AXIS_LINE_Y to mapOf(ThemeOption.Elem.BLANK to false),
                        ThemeOption.PANEL_BKGR_RECT to mapOf(ThemeOption.Elem.BLANK to false)
                    ),
                    expectedPlotBackground = Color.WHITE,
                    expectedYAxisColor = Color.parseHex("#474747"),
                    expectedPanelBackground = panelBackground(
                        expectedColor = Color.parseHex("#474747"),
                        expectedFill = Color.parseHex("#EBEBEB")
                    )
                ),
               test(
                    themeOptions = minimalTheme + flavorOption,
                    expectedPlotBackground = Color.parseHex("#303030"),
                    expectedYAxisColor = null,
                    expectedPanelBackground = null
                ),
                test(
                    themeOptions = minimalTheme + flavorOption + mapOf(
                        ThemeOption.AXIS_LINE_Y to mapOf(ThemeOption.Elem.BLANK to false),
                        ThemeOption.PANEL_BKGR_RECT to mapOf(ThemeOption.Elem.BLANK to false)
                    ),
                    expectedPlotBackground = Color.parseHex("#303030"),
                    expectedYAxisColor = Color.parseHex("#BBBBBB"),
                    expectedPanelBackground = panelBackground( // special flavor color
                        expectedColor = Color.parseHex("#BBBBBB"),
                        expectedFill = Color.parseHex("#3B3B3B")
                    )
                ),
                // custom settings override predefined options
                test(
                    themeOptions = minimalTheme + flavorOption + mapOf(
                        ThemeOption.PLOT_BKGR_RECT to mapOf(ThemeOption.Elem.FILL to Color.RED),
                        ThemeOption.AXIS_LINE_Y to mapOf(
                            ThemeOption.Elem.COLOR to Color.GREEN,
                            ThemeOption.Elem.BLANK to false
                        ),
                        ThemeOption.PANEL_BKGR_RECT to mapOf(
                            ThemeOption.Elem.FILL to Color.BLUE,
                            ThemeOption.Elem.BLANK to false
                        ),
                    ),
                    expectedPlotBackground = Color.RED,
                    expectedYAxisColor = Color.GREEN,
                    expectedPanelBackground = panelBackground(
                        expectedColor = Color.parseHex("#BBBBBB"), // special flavor color
                        expectedFill = Color.BLUE  // specified
                    )
                ),

                // Check the light theme
                test(
                    themeOptions = lightTheme,
                    expectedPlotBackground = Color.WHITE,
                    expectedYAxisColor = null,
                    expectedPanelBackground = panelBackground(
                        expectedColor = Color.parseHex("#C9C9C9"),
                        expectedFill = Color.WHITE
                    ),
                    otherExpected = xAxisTooltipColor(
                        expectedColor = Color.WHITE,
                        expectedFill = Color.parseHex("#474747")
                    )
                ),
                // The original panel background and axis tooltip colors are equal to the plot background
                // => the result colors should also be the same color as the plot background
                test(
                    themeOptions = lightTheme + flavorOption,
                    expectedPlotBackground = Color.parseHex("#303030"),
                    expectedYAxisColor = null,
                    expectedPanelBackground = panelBackground(
                        expectedColor = Color.parseHex("#BBBBBB"),
                        expectedFill = Color.parseHex("#303030")
                    ),
                    otherExpected = xAxisTooltipColor(
                        expectedColor = Color.parseHex("#303030"),
                        expectedFill = Color.parseHex("#BBBBBB")
                    )
                )
            )
        }

        private fun plotBackground(expected: Color) = Expected(
            expected,
            { theme: Theme -> theme.plot().backgroundFill() },
            ThemeOption.PLOT_BKGR_RECT
        )

        private fun showYAxis(expected: Boolean) = Expected(
            expected,
            { theme: Theme -> theme.verticalAxis(flipAxis = false).showLine() },
            ThemeOption.AXIS_LINE_Y
        )

        private fun yAxisColor(expected: Color) = Expected(
            expected,
            { theme: Theme -> theme.verticalAxis(flipAxis = false).lineColor() },
            ThemeOption.AXIS_LINE_Y
        )

        private fun showPanelRect(expected: Boolean) = Expected(
            expected,
            { theme: Theme -> theme.panel().showRect() },
            ThemeOption.PANEL_BKGR_RECT
        )

        private fun panelBackground(expectedColor: Color, expectedFill: Color) = listOf(
            Expected(
                expectedColor,
                { theme: Theme -> theme.panel().rectColor() },
                ThemeOption.PANEL_BKGR_RECT
            ),
            Expected(
                expectedFill,
                { theme: Theme -> theme.panel().rectFill() },
                ThemeOption.PANEL_BKGR_RECT
            )
        )

        private fun xAxisTooltipColor(expectedColor: Color, expectedFill: Color) = listOf(
            Expected(
                expectedColor,
                { theme: Theme -> theme.horizontalAxis(flipAxis = false).tooltipColor() },
                ThemeOption.AXIS_LINE_X
            ),
            Expected(
                expectedFill,
                { theme: Theme -> theme.horizontalAxis(flipAxis = false).tooltipFill() },
                ThemeOption.AXIS_LINE_X
            ),
        )

        private fun test(
            themeOptions: Map<String, Any>,
            expectedPlotBackground: Color,
            expectedYAxisColor: Color?,
            expectedPanelBackground: List<Expected>?,
            otherExpected: List<Expected> = emptyList()
        ) = arrayOf(
            themeOptions,
            listOfNotNull(
                plotBackground(expectedPlotBackground),
                showYAxis(expectedYAxisColor != null),
                expectedYAxisColor?.let(this::yAxisColor),
                showPanelRect(expectedPanelBackground != null)
            ) + (expectedPanelBackground ?: emptyList())
                    + otherExpected
        )
    }
}