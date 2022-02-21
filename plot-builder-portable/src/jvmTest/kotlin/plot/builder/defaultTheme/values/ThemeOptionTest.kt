/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.defaultTheme.values

import jetbrains.datalore.plot.builder.defaultTheme.*
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.Elem.COLOR
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.Elem.FILL
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.Elem.SIZE
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.ForTest.elemWithColorAndSize
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.ForTest.elemWithColorOnly
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.ForTest.elemWithFill
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.ForTest.numericOptions
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.ForTest.themeNames
import jetbrains.datalore.plot.builder.theme.Theme
import kotlin.test.Test

internal class ThemeOptionTest {

    @Test
    fun checkElements() {
        for (themeName in themeNames) {
            val themeValues = ThemeValues.forName(themeName)
            val theme = DefaultTheme(themeValues.values)

            for (elem in elemWithColorAndSize + elemWithColorOnly + elemWithFill) {
                val elemKey = accessKeyForOption(theme, elem)
                checkElemProperty(themeName, elemKey, COLOR)
                checkElemProperty(themeName, elemKey, SIZE)
                if (elem in elemWithFill) {
                    checkElemProperty(themeName, elemKey, FILL)
                }
            }
        }
    }

    @Test
    fun checkOptions() {
        for (themeName in themeNames) {
            val themeValues = ThemeValues.forName(themeName)
            val theme = DefaultTheme(themeValues.values)

            for (option in numericOptions) {
                val optionKey = accessKeyForOption(theme, option)
                checkNumericOption(themeName, optionKey)
            }
        }
    }

    private fun checkElemProperty(theme: String, elemKey: List<String>, elemProperty: String) {
        val themeValues = ThemeValues.forName(theme)
        val acccess = object : ThemeValuesAccess(themeValues.values) {
            fun check() {
                when (elemProperty) {
                    COLOR, FILL -> this.getColor(getElemValue(elemKey), elemProperty)
                    SIZE -> this.getNumber(getElemValue(elemKey), elemProperty)
                    else -> throw IllegalArgumentException("Unknown element property: $elemProperty")
                }
            }
        }
        try {
            acccess.check()
        } catch (e: Exception) {
            throw RuntimeException("'$elemProperty' failed. Theme: '$theme', elem: $elemKey", e)
        }
    }

    private fun checkNumericOption(theme: String, optionKey: List<String>) {
        val themeValues = ThemeValues.forName(theme)
        val acccess = object : ThemeValuesAccess(themeValues.values) {
            fun check() {
                this.getNumber(optionKey)
            }
        }
        try {
            acccess.check()
        } catch (e: Exception) {
            throw RuntimeException("Numeric failed. Theme: '$theme', option: $optionKey", e)
        }
    }


    private fun accessKeyForOption(theme: Theme, option: String): List<String> {
        return when (option) {
            // Elements
            ThemeOption.AXIS_TICKS_X -> (theme.axisX() as DefaultAxisTheme).tickKey
            ThemeOption.AXIS_LINE_X -> (theme.axisX() as DefaultAxisTheme).lineKey
            ThemeOption.AXIS_TOOLTIP_X -> (theme.axisX() as DefaultAxisTheme).tooltipKey
            ThemeOption.AXIS_TITLE_X -> (theme.axisX() as DefaultAxisTheme).titleKey
            ThemeOption.AXIS_TEXT_X -> (theme.axisX() as DefaultAxisTheme).textKey
            ThemeOption.AXIS_TOOLTIP_TEXT_X -> (theme.axisX() as DefaultAxisTheme).tooltipKey
            ThemeOption.AXIS_TICKS_Y -> (theme.axisY() as DefaultAxisTheme).tickKey
            ThemeOption.AXIS_LINE_Y -> (theme.axisY() as DefaultAxisTheme).lineKey
            ThemeOption.AXIS_TOOLTIP_Y -> (theme.axisY() as DefaultAxisTheme).tooltipKey
            ThemeOption.AXIS_TITLE_Y -> (theme.axisY() as DefaultAxisTheme).titleKey
            ThemeOption.AXIS_TEXT_Y -> (theme.axisY() as DefaultAxisTheme).textKey
            ThemeOption.AXIS_TOOLTIP_TEXT_Y -> (theme.axisY() as DefaultAxisTheme).tooltipKey

            ThemeOption.PANEL_BKGR_RECT -> (theme.panel() as DefaultPanelTheme).rectKey
            ThemeOption.PANEL_GRID_MAJOR_X -> (theme.panel().gridX() as DefaultPanelGridTheme).majorLineKey
            ThemeOption.PANEL_GRID_MINOR_X -> (theme.panel().gridX() as DefaultPanelGridTheme).minorLineKey
            ThemeOption.PANEL_GRID_MAJOR_Y -> (theme.panel().gridY() as DefaultPanelGridTheme).majorLineKey
            ThemeOption.PANEL_GRID_MINOR_Y -> (theme.panel().gridY() as DefaultPanelGridTheme).minorLineKey

            ThemeOption.FACET_STRIP_BGR_RECT -> (theme.facets() as DefaultFacetsTheme).rectKey
            ThemeOption.FACET_STRIP_TEXT -> (theme.facets() as DefaultFacetsTheme).textKey

            ThemeOption.PLOT_BKGR_RECT -> (theme.plot() as DefaultPlotTheme).backgroundKey
            ThemeOption.PLOT_TITLE -> (theme.plot() as DefaultPlotTheme).titleKey
            ThemeOption.PLOT_SUBTITLE -> (theme.plot() as DefaultPlotTheme).subtitleKey
            ThemeOption.PLOT_CAPTION -> (theme.plot() as DefaultPlotTheme).captionKey
            ThemeOption.LEGEND_BKGR_RECT -> (theme.legend() as DefaultLegendTheme).backgroundKey
            ThemeOption.LEGEND_TEXT -> (theme.legend() as DefaultLegendTheme).textKey
            ThemeOption.LEGEND_TITLE -> (theme.legend() as DefaultLegendTheme).titleKey

            // Simple option
            ThemeOption.AXIS_TICKS_LENGTH_X -> (theme.axisX() as DefaultAxisTheme).tickLengthKey
            ThemeOption.AXIS_TICKS_LENGTH_Y -> (theme.axisY() as DefaultAxisTheme).tickLengthKey

            else -> throw IllegalStateException("Unknown theme option: $option")
        }

    }


}