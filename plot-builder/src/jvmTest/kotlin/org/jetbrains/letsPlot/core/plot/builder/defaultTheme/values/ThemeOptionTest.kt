/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values

import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.*
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Elem.COLOR
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Elem.FILL
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Elem.FONT_FACE
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Elem.SIZE
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.ForTest.elemWithColorAndSize
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.ForTest.elemWithColorOnly
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.ForTest.elemWithFill
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.ForTest.elemWithFontOptions
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.ForTest.numericOptions
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.ForTest.themeNames
import org.jetbrains.letsPlot.core.plot.builder.presentation.DefaultFontFamilyRegistry
import kotlin.test.Test

internal class ThemeOptionTest {

    @Test
    fun checkElements() {
        for (themeName in themeNames) {
            val theme = ThemeUtil.buildTheme(themeName)
            for (elem in elemWithColorAndSize + elemWithColorOnly + elemWithFill) {
                val elemKey = accessKeyForOption(theme, elem)
                checkElemProperty(themeName, accessKeyForOption(theme, elem, COLOR), COLOR)
                checkElemProperty(themeName, elemKey, SIZE)
                if (elem in elemWithFill) {
                    checkElemProperty(themeName, elemKey, FILL)
                }
            }

            // font options
            for (elem in elemWithFontOptions) {
                val elemKey = accessKeyForOption(theme, elem, FONT_FACE)
                checkElemProperty(themeName, elemKey, FONT_FACE)
            }
        }
    }

    @Test
    fun checkOptions() {
        for (themeName in themeNames) {
            val theme = ThemeUtil.buildTheme(themeName)
            for (option in numericOptions) {
                val optionKey = accessKeyForOption(theme, option)
                checkNumericOption(themeName, optionKey)
            }
        }
    }

    private fun checkElemProperty(theme: String, elemKey: List<String>, elemProperty: String) {
        val themeValues = ThemeUtil.getThemeValues(theme)
        val access = object : ThemeValuesAccess(themeValues, DefaultFontFamilyRegistry()) {
            fun check() {
                when (elemProperty) {
                    COLOR, FILL -> this.getColor(getElemValue(elemKey), elemProperty)
                    SIZE -> this.getNumber(getElemValue(elemKey), elemProperty)
                    FONT_FACE -> this.getFontFace(getElemValue(elemKey))
                    else -> throw IllegalArgumentException("Unknown element property: $elemProperty")
                }
            }
        }
        try {
            access.check()
        } catch (e: Exception) {
            throw RuntimeException("'$elemProperty' failed. Theme: '$theme', elem: $elemKey", e)
        }
    }

    private fun checkNumericOption(theme: String, optionKey: List<String>) {
        val themeValues = ThemeUtil.getThemeValues(theme)
        val access = object : ThemeValuesAccess(themeValues, DefaultFontFamilyRegistry()) {
            fun check() {
                this.getNumber(optionKey)
            }
        }
        try {
            access.check()
        } catch (e: Exception) {
            throw RuntimeException("Numeric failed. Theme: '$theme', option: $optionKey", e)
        }
    }


    private fun accessKeyForOption(theme: Theme, option: String, elemProperty: String? = null): List<String> {
        return when (option) {
            // Elements
            ThemeOption.AXIS_TICKS_X -> (theme.horizontalAxis(flipAxis = false) as DefaultAxisTheme).tickKey
            ThemeOption.AXIS_LINE_X -> (theme.horizontalAxis(flipAxis = false) as DefaultAxisTheme).lineKey
            ThemeOption.AXIS_TOOLTIP_X -> (theme.horizontalAxis(flipAxis = false) as DefaultAxisTheme).tooltipKey
            ThemeOption.AXIS_TITLE_X -> (theme.horizontalAxis(flipAxis = false) as DefaultAxisTheme).titleKey
            ThemeOption.AXIS_TEXT_X -> (theme.horizontalAxis(flipAxis = false) as DefaultAxisTheme).textKey
            ThemeOption.AXIS_TOOLTIP_TEXT_X -> {
                val hAxis = (theme.horizontalAxis(flipAxis = false) as DefaultAxisTheme)
                when (elemProperty) {
                    COLOR -> hAxis.tooltipTextColorKey
                    FONT_FACE -> hAxis.tooltipTextKey
                    else -> hAxis.tooltipKey
                }
            }

            ThemeOption.AXIS_TICKS_Y -> (theme.verticalAxis(flipAxis = false) as DefaultAxisTheme).tickKey
            ThemeOption.AXIS_LINE_Y -> (theme.verticalAxis(flipAxis = false) as DefaultAxisTheme).lineKey
            ThemeOption.AXIS_TOOLTIP_Y -> (theme.verticalAxis(flipAxis = false) as DefaultAxisTheme).tooltipKey
            ThemeOption.AXIS_TITLE_Y -> (theme.verticalAxis(flipAxis = false) as DefaultAxisTheme).titleKey
            ThemeOption.AXIS_TEXT_Y -> (theme.verticalAxis(flipAxis = false) as DefaultAxisTheme).textKey
            ThemeOption.AXIS_TOOLTIP_TEXT_Y -> {
                val vAxis = (theme.verticalAxis(flipAxis = false) as DefaultAxisTheme)
                when (elemProperty) {
                    COLOR -> vAxis.tooltipTextColorKey
                    FONT_FACE -> vAxis.tooltipTextKey
                    else -> vAxis.tooltipKey
                }
            }

            ThemeOption.PANEL_BKGR_RECT -> (theme.panel() as DefaultPanelTheme).rectKey
            ThemeOption.PANEL_BORDER_RECT -> (theme.panel() as DefaultPanelTheme).borderKey
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

            ThemeOption.TOOLTIP_RECT -> (theme.tooltips() as DefaultTooltipsTheme).tooltipKey
            ThemeOption.TOOLTIP_TEXT -> (theme.tooltips() as DefaultTooltipsTheme).textKey
            ThemeOption.TOOLTIP_TITLE_TEXT -> (theme.tooltips() as DefaultTooltipsTheme).titleTextKey

            ThemeOption.ANNOTATION_TEXT -> (theme.annotations() as DefaultAnnotationsTheme).annotationTextKey

            // Simple option
            ThemeOption.AXIS_TICKS_LENGTH_X -> (theme.horizontalAxis(flipAxis = false) as DefaultAxisTheme).tickLengthKey
            ThemeOption.AXIS_TICKS_LENGTH_Y -> (theme.verticalAxis(flipAxis = false) as DefaultAxisTheme).tickLengthKey

            else -> throw IllegalStateException("Unknown theme option: $option")
        }
    }
}