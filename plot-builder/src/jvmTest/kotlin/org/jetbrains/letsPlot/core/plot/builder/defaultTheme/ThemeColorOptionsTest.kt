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
                    expected = yAxisColor(null) +
                            plotBackground(Color.WHITE) +
                            panelBackgroundRect(null, null)
                ),
                test(
                    themeOptions = minimalTheme + mapOf(
                        ThemeOption.AXIS_LINE_Y to mapOf(ThemeOption.Elem.BLANK to false),
                        ThemeOption.PANEL_BKGR_RECT to mapOf(ThemeOption.Elem.BLANK to false)
                    ),
                    expected = yAxisColor(DARK_GREY) +
                            plotBackground(Color.WHITE) +
                            panelBackgroundRect(
                                expectedColor = DARK_GREY,
                                expectedFill = Color.WHITE
                            )
                ),
                test(
                    themeOptions = minimalTheme + flavorOption,
                    expected = yAxisColor(null) +
                            plotBackground(Color.parseHex("#303030")) +
                            panelBackgroundRect(null, null)
                ),
                test(
                    themeOptions = minimalTheme + flavorOption + mapOf(
                        ThemeOption.AXIS_LINE_Y to mapOf(ThemeOption.Elem.BLANK to false),
                        ThemeOption.PANEL_BKGR_RECT to mapOf(ThemeOption.Elem.BLANK to false)
                    ),
                    expected = yAxisColor(Color.parseHex("#BBBBBB")) +
                            plotBackground(Color.parseHex("#303030")) +
                            panelBackgroundRect(
                                expectedColor = Color.parseHex("#BBBBBB"), // special flavor color
                                expectedFill = Color.parseHex("#303030") // same as the plot bkgr
                            )
                ),
                // check facet rect
                test(
                    themeOptions = minimalTheme + mapOf(
                        ThemeOption.FACET_STRIP_BGR_RECT to mapOf(ThemeOption.Elem.BLANK to false)
                    ),
                    expected = facetStripBackgroundRect(
                        expectedColor = DARK_GREY,
                        expectedFill = LIGHT_GREY
                    )
                ),
                test(
                    themeOptions = minimalTheme + flavorOption + mapOf(
                        ThemeOption.FACET_STRIP_BGR_RECT to mapOf(ThemeOption.Elem.BLANK to false)
                    ),
                    expected = facetStripBackgroundRect(
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
                    expected = yAxisColor(Color.GREEN) +
                            plotBackground(Color.RED) +
                            panelBackgroundRect(
                                expectedColor = Color.parseHex("#BBBBBB"), // special flavor color
                                expectedFill = Color.BLUE  // specified
                            )
                ),

                // Check the light theme
                test(
                    themeOptions = lightTheme,
                    expected = yAxisColor(null) +
                            plotBackground(Color.WHITE) +
                            panelBackgroundRect(
                                expectedColor = Color.parseHex("#C9C9C9"),
                                expectedFill = Color.WHITE
                            ) +
                            xAxisTooltipColor(
                                expectedColor = Color.WHITE,
                                expectedFill = DARK_GREY
                            )
                ),
                // light theme: panel background and axis tooltip colors are equal to the plot background
                test(
                    themeOptions = lightTheme + flavorOption,
                    expected = yAxisColor(null) +
                            plotBackground(Color.parseHex("#303030")) +
                            panelBackgroundRect(
                                expectedColor = Color.parseHex("#BBBBBB"),
                                expectedFill = Color.parseHex("#303030")
                            ) +
                            xAxisTooltipColor(
                                expectedColor = Color.parseHex("#303030"),
                                expectedFill = Color.parseHex("#BBBBBB")
                            )
                ),
                // Check the none theme
                test(
                    themeOptions = noneTheme,
                    expected = yAxisColor(Color.BLUE) +
                            plotBackground(Color.LIGHT_BLUE) +
                            panelBackgroundRect(
                                expectedColor = Color.BLUE,
                                expectedFill = Color.LIGHT_BLUE
                            )
                ),
                // flavor is not applied to 'none' theme
                test(
                    themeOptions = noneTheme + flavorOption,
                    expected = yAxisColor(Color.BLUE) +
                            plotBackground(Color.LIGHT_BLUE) +
                            panelBackgroundRect(
                                expectedColor = Color.BLUE,
                                expectedFill = Color.LIGHT_BLUE
                            )
                ),
                // The 'classic' theme: facet rect fill = plot background
                test(
                    themeOptions = classicTheme,
                    expected = yAxisColor(DARK_GREY) +
                            panelBackgroundRect(null, null) +
                            plotBackground(Color.WHITE) +
                            facetStripBackgroundRect(
                                expectedColor = DARK_GREY,
                                expectedFill = Color.WHITE
                            )
                ),
                test(
                    themeOptions = classicTheme + flavorOption,
                    expected = yAxisColor(Color.parseHex("#BBBBBB")) +
                            plotBackground(Color.parseHex("#303030")) +
                            panelBackgroundRect(null, null) +
                            facetStripBackgroundRect(
                                expectedColor = Color.parseHex("#BBBBBB"),
                                expectedFill = Color.parseHex("#303030")
                            )
                ),
                // Check tooltip rect
                test(
                    themeOptions = minimalTheme,
                    expected = tooltipRectColor(
                        expectedColor = DARK_GREY,
                        expectedFill = Color.WHITE
                    )
                ),
                test(
                    themeOptions = minimalTheme + flavorOption,
                    expected = tooltipRectColor(
                        expectedColor = Color.parseHex("#BBBBBB"),
                        expectedFill = Color.parseHex("#141414")
                    )
                ),
                test(
                    themeOptions = minimalTheme + flavorOption + mapOf(
                        ThemeOption.TOOLTIP_RECT to mapOf(
                            ThemeOption.Elem.COLOR to Color.RED,
                            ThemeOption.Elem.FILL to Color.BLUE
                        )
                    ),
                    expected = tooltipRectColor(
                        expectedColor = Color.RED,
                        expectedFill = Color.BLUE
                    )
                ),
            )
        }

        private fun plotBackground(expected: Color) = Expected(
            expected,
            { theme: Theme -> theme.plot().backgroundFill() },
            "Wrong ${ThemeOption.PLOT_BKGR_RECT}/fill"
        )

        private fun yAxisColor(expected: Color?) = listOfNotNull(
            Expected(
                expected != null,
                { theme: Theme -> theme.verticalAxis(flipAxis = false).showLine() },
                "Wrong ${ThemeOption.AXIS_LINE_Y}"
            ),
            expected?.let {
                Expected(
                    expected,
                    { theme: Theme -> theme.verticalAxis(flipAxis = false).lineColor() },
                    "Wrong ${ThemeOption.AXIS_LINE_Y}/color"
                )
            }
        )


        private fun panelBackgroundRect(expectedColor: Color?, expectedFill: Color?) = listOfNotNull(
            Expected(
                expectedColor != null && expectedFill != null,
                { theme: Theme -> theme.panel().showRect() },
                "Wrong ${ThemeOption.PANEL_BKGR_RECT}"
            ),
            expectedColor?.let {
                Expected(
                    expectedColor,
                    { theme: Theme -> theme.panel().rectColor() },
                    "Wrong ${ThemeOption.PANEL_BKGR_RECT}/color"
                )
            },
            expectedFill?.let {
                Expected(
                    expectedFill,
                    { theme: Theme -> theme.panel().rectFill() },
                    "Wrong ${ThemeOption.PANEL_BKGR_RECT}/fill"
                )
            }
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

        private fun tooltipRectColor(expectedColor: Color, expectedFill: Color) = listOf(
            Expected(
                expectedColor,
                { theme: Theme -> theme.tooltips().tooltipColor() },
                "Wrong ${ThemeOption.TOOLTIP_RECT}/color"
            ),
            Expected(
                expectedFill,
                { theme: Theme -> theme.tooltips().tooltipFill() },
                "Wrong ${ThemeOption.TOOLTIP_RECT}/fill"
            )
        )

        private fun facetStripBackgroundRect(expectedColor: Color, expectedFill: Color) = listOf(
            Expected(
                expectedColor,
                { theme: Theme -> theme.facets().horizontalFacetStrip().stripColor() },
                "Wrong ${ThemeOption.FACET_STRIP_BGR_RECT}/color"
            ),
            Expected(
                expectedFill,
                { theme: Theme -> theme.facets().horizontalFacetStrip().stripFill() },
                "Wrong ${ThemeOption.FACET_STRIP_BGR_RECT}/fill"
            )
        )

        private fun test(
            themeOptions: Map<String, Any>,
            expected: List<Expected> = emptyList()
        ) = arrayOf(themeOptions, expected)
    }
}