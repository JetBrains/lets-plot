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
        val message: String
    )

    private val theme: Theme = ThemeConfig(themeValues, DefaultFontFamilyRegistry()).theme

    // The order should be as follows:
    //      named theme options
    //      + flavor colors
    //      + custom 'theme' options

    @Test
    fun eval() {
        expected.forEach {
            assertEquals(it.expected, it.actualGetter(theme), it.message)
        }
    }

    companion object {
        private val DARK_GREY = Color.parseHex("#474747")
        private val LIGHT_GREY = Color.parseHex("#E9E9E9")

        @JvmStatic
        @Parameterized.Parameters
        fun params(): Collection<Array<Any>> {
            val minimalTheme = mapOf(Option.Meta.NAME to ThemeOption.Name.LP_MINIMAL)
            val lightTheme = mapOf(Option.Meta.NAME to ThemeOption.Name.R_LIGHT)
            val noneTheme = mapOf(Option.Meta.NAME to ThemeOption.Name.LP_NONE)
            val classicTheme = mapOf(Option.Meta.NAME to ThemeOption.Name.R_CLASSIC)
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
                    expectedYAxisColor = DARK_GREY,
                    expectedPanelBackground = panelBackgroundRect(
                        expectedColor = DARK_GREY,
                        expectedFill = LIGHT_GREY
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
                    expectedPanelBackground = panelBackgroundRect( // special flavor color
                        expectedColor = Color.parseHex("#BBBBBB"),
                        expectedFill = Color.parseHex("#3B3B3B")
                    )
                ),
                // check facet rect
                test(
                    themeOptions = minimalTheme + mapOf(
                        ThemeOption.FACET_STRIP_BGR_RECT to mapOf(ThemeOption.Elem.BLANK to false)
                    ),
                    expectedPlotBackground = Color.WHITE,
                    expectedYAxisColor = null,
                    expectedPanelBackground = null,
                    otherExpected = facetStripBackgroundRect(
                        expectedColor = DARK_GREY,
                        expectedFill = LIGHT_GREY
                    )
                ),
                test(
                    themeOptions = minimalTheme + flavorOption + mapOf(
                        ThemeOption.FACET_STRIP_BGR_RECT to mapOf(ThemeOption.Elem.BLANK to false)
                    ),
                    expectedPlotBackground = Color.parseHex("#303030"),
                    expectedYAxisColor = null,
                    expectedPanelBackground = null,
                    otherExpected = facetStripBackgroundRect(
                        expectedColor = Color.parseHex("#BBBBBB"),
                        expectedFill = Color.parseHex("#363636")
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
                    expectedPanelBackground = panelBackgroundRect(
                        expectedColor = Color.parseHex("#BBBBBB"), // special flavor color
                        expectedFill = Color.BLUE  // specified
                    )
                ),

                // Check the light theme
                test(
                    themeOptions = lightTheme,
                    expectedPlotBackground = Color.WHITE,
                    expectedYAxisColor = null,
                    expectedPanelBackground = panelBackgroundRect(
                        expectedColor = Color.parseHex("#C9C9C9"),
                        expectedFill = Color.WHITE
                    ),
                    otherExpected = xAxisTooltipColor(
                        expectedColor = Color.WHITE,
                        expectedFill = DARK_GREY
                    )
                ),
                // light theme: panel background and axis tooltip colors are equal to the plot background
                test(
                    themeOptions = lightTheme + flavorOption,
                    expectedPlotBackground = Color.parseHex("#303030"),
                    expectedYAxisColor = null,
                    expectedPanelBackground = panelBackgroundRect(
                        expectedColor = Color.parseHex("#BBBBBB"),
                        expectedFill = Color.parseHex("#303030")
                    ),
                    otherExpected = xAxisTooltipColor(
                        expectedColor = Color.parseHex("#303030"),
                        expectedFill = Color.parseHex("#BBBBBB")
                    )
                ),
                // Check the none theme
                test(
                    themeOptions = noneTheme,
                    expectedPlotBackground = Color.WHITE,
                    expectedYAxisColor = DARK_GREY,
                    expectedPanelBackground = panelBackgroundRect(
                        expectedColor = DARK_GREY,
                        expectedFill = LIGHT_GREY
                    )
                ),
                // 'none' theme with flavor: panel background is equal to the plot background
                test(
                    themeOptions = noneTheme + flavorOption,
                    expectedPlotBackground = Color.parseHex("#303030"),
                    expectedYAxisColor = Color.parseHex("#BBBBBB"),
                    expectedPanelBackground = panelBackgroundRect(
                        expectedColor = Color.parseHex("#BBBBBB"),
                        expectedFill = Color.parseHex("#303030")
                    )
                ),
                // The 'classic' theme: facet rect fill = plot background
                test(
                    themeOptions = classicTheme,
                    expectedPlotBackground = Color.WHITE,
                    expectedYAxisColor = DARK_GREY,
                    expectedPanelBackground = null,
                    otherExpected = facetStripBackgroundRect(
                        expectedColor = DARK_GREY,
                        expectedFill = Color.WHITE
                    )
                ),
                test(
                    themeOptions = classicTheme + flavorOption,
                    expectedPlotBackground = Color.parseHex("#303030"),
                    expectedYAxisColor = Color.parseHex("#BBBBBB"),
                    expectedPanelBackground = null,
                    otherExpected = facetStripBackgroundRect(
                        expectedColor = Color.parseHex("#BBBBBB"),
                        expectedFill = Color.parseHex("#303030")
                    )
                ),
            )
        }

        private fun plotBackground(expected: Color) = Expected(
            expected,
            { theme: Theme -> theme.plot().backgroundFill() },
            "Wrong ${ThemeOption.PLOT_BKGR_RECT}/fill"
        )

        private fun showYAxis(expected: Boolean) = Expected(
            expected,
            { theme: Theme -> theme.verticalAxis(flipAxis = false).showLine() },
            "Wrong ${ThemeOption.AXIS_LINE_Y}"
        )

        private fun yAxisColor(expected: Color) = Expected(
            expected,
            { theme: Theme -> theme.verticalAxis(flipAxis = false).lineColor() },
            "Wrong ${ThemeOption.AXIS_LINE_Y}/color"

        )

        private fun showPanelBackgroundRect(expected: Boolean) = Expected(
            expected,
            { theme: Theme -> theme.panel().showRect() },
            "Wrong ${ThemeOption.PANEL_BKGR_RECT}"
        )

        private fun panelBackgroundRect(expectedColor: Color, expectedFill: Color) = listOf(
            Expected(
                expectedColor,
                { theme: Theme -> theme.panel().rectColor() },
                "Wrong ${ThemeOption.PANEL_BKGR_RECT}/color"
            ),
            Expected(
                expectedFill,
                { theme: Theme -> theme.panel().rectFill() },
                "Wrong ${ThemeOption.PANEL_BKGR_RECT}/fill"
            )
        )

        private fun xAxisTooltipColor(expectedColor: Color, expectedFill: Color) = listOf(
            Expected(
                expectedColor,
                { theme: Theme -> theme.horizontalAxis(flipAxis = false).tooltipColor() },
                "Wrong ${ThemeOption.AXIS_LINE_X}/color"
            ),
            Expected(
                expectedFill,
                { theme: Theme -> theme.horizontalAxis(flipAxis = false).tooltipFill() },
                "Wrong ${ThemeOption.AXIS_LINE_X}/fill"
            )
        )

        private fun facetStripBackgroundRect(expectedColor: Color, expectedFill: Color) = listOf(
            Expected(
                expectedColor,
                { theme: Theme -> theme.facets().stripColor() },
                "Wrong ${ThemeOption.FACET_STRIP_BGR_RECT}/color"
            ),
            Expected(
                expectedFill,
                { theme: Theme -> theme.facets().stripFill() },
                "Wrong ${ThemeOption.FACET_STRIP_BGR_RECT}/fill"
            )
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
                showPanelBackgroundRect(expectedPanelBackground != null)
            ) + (expectedPanelBackground ?: emptyList())
                    + otherExpected
        )
    }
}