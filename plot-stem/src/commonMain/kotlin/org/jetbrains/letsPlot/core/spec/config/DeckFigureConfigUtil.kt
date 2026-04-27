/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.DefaultTheme
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption
import org.jetbrains.letsPlot.core.spec.Option.Plot.THEME
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.ELEMENT_BLANK_SHORTHAND as BLANK

/**
 * In ggdeck, appearence/visibility of shared axes is primarily controlled by the ggdeck-level theme settings.
 */
internal object DeckFigureConfigUtil {
    private val DECK_OVERLAY_THEME_BASE = mapOf(
        ThemeOption.PLOT_BKGR_RECT to BLANK,
        ThemeOption.PANEL_BKGR_RECT to BLANK,
        ThemeOption.PANEL_BORDER_RECT to BLANK,
        ThemeOption.PANEL_GRID to BLANK,
    )

    @Suppress("UNCHECKED_CAST")
    private fun axisBlankX(deckTheme: DefaultTheme): Map<String, Any> {
        val deckAxisTicksOption = deckTheme.options[ThemeOption.AXIS_TICKS_X] as? Map<String, Any> ?: emptyMap()
        val deckAxisTextOption = deckTheme.options[ThemeOption.AXIS_TEXT_X] as? Map<String, Any> ?: emptyMap()
        val deckAxisTitleOption = deckTheme.options[ThemeOption.AXIS_TITLE_X] as? Map<String, Any> ?: emptyMap()
        // Use transparency instead of BLANK to preserve the space for axis text and ticks.
        val axisTicksOption = deckAxisTicksOption + mapOf(ThemeOption.Elem.COLOR to "transparent")
        val axisTextsOption = deckAxisTextOption + mapOf(ThemeOption.Elem.COLOR to "transparent")
        val axisTitleOption = deckAxisTitleOption + mapOf(ThemeOption.Elem.COLOR to "transparent")
        return mapOf(
            ThemeOption.AXIS_TICKS_X to axisTicksOption,
            ThemeOption.AXIS_TEXT_X to axisTextsOption,
            ThemeOption.AXIS_TITLE_X to axisTitleOption,
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun axisBlankY(deckTheme: DefaultTheme): Map<String, Any> {
        val deckAxisTicksOption = deckTheme.options[ThemeOption.AXIS_TICKS_Y] as? Map<String, Any> ?: emptyMap()
        val deckAxisTextOption = deckTheme.options[ThemeOption.AXIS_TEXT_Y] as? Map<String, Any> ?: emptyMap()
        val deckAxisTitleOption = deckTheme.options[ThemeOption.AXIS_TITLE_Y] as? Map<String, Any> ?: emptyMap()
        // Use transparency instead of BLANK to preserve the space for axis text and ticks.
        val axisTicksOption = deckAxisTicksOption + mapOf(ThemeOption.Elem.COLOR to "transparent")
        val axisTextsOption = deckAxisTextOption + mapOf(ThemeOption.Elem.COLOR to "transparent")
        val axisTitleOption = deckAxisTitleOption + mapOf(ThemeOption.Elem.COLOR to "transparent")
        return mapOf(
            ThemeOption.AXIS_TICKS_Y to axisTicksOption,
            ThemeOption.AXIS_TEXT_Y to axisTextsOption,
            ThemeOption.AXIS_TITLE_Y to axisTitleOption,
        )
    }


    private fun axisLineBlankX(deckTheme: DefaultTheme): Map<String, Any> {
        @Suppress("UNCHECKED_CAST")
        val deckAxisLineOption = deckTheme.options[ThemeOption.AXIS_LINE_X] as? Map<String, Any> ?: emptyMap()
        // Use transparency instead of BLANK to preserve the space for the axis line.
        val axisLineOption = deckAxisLineOption + mapOf(ThemeOption.Elem.COLOR to "transparent")
        return mapOf(
            ThemeOption.AXIS_LINE_X to axisLineOption,
        )
    }

    private fun axisLineBlankY(deckTheme: DefaultTheme): Map<String, Any> {
        @Suppress("UNCHECKED_CAST")
        val deckAxisLineOption = deckTheme.options[ThemeOption.AXIS_LINE_Y] as? Map<String, Any> ?: emptyMap()
        // Use transparency instead of BLANK to preserve the space for the axis line.
        val axisLineOption = deckAxisLineOption + mapOf(ThemeOption.Elem.COLOR to "transparent")
        return mapOf(
            ThemeOption.AXIS_LINE_Y to axisLineOption,
        )
    }

    internal fun applyDeckOverlayTheme(
        spec: Map<String, Any>,
        deckTheme: DefaultTheme,
        shareX: Boolean,
        shareY: Boolean,
        position: DeckPosition,
        ontopX: Boolean,
        ontopY: Boolean
    ): Map<String, Any> {

        val axisLineBlankX = axisLineBlankX(deckTheme)
        val axisLineBlankY = axisLineBlankY(deckTheme)

        val overlayTheme = if (position == DeckPosition.FIRST) {
            // The first figure (base plot) retains all axis elements with one possible exception: shared axis line.
            val axisLineX = if (shareX && ontopX) axisLineBlankX else emptyMap()
            val axisLineY = if (shareY && ontopY) axisLineBlankY else emptyMap()
            axisLineX + axisLineY
        } else {
            val axisBlankX = axisBlankX(deckTheme)
            val axisBlankY = axisBlankY(deckTheme)

            if (position == DeckPosition.MIDDLE) {
                // Middle figures in the deck have blank axes.
                val sharedAxesTheme = when {
                    shareX && shareY -> axisBlankX + axisBlankY + axisLineBlankX + axisLineBlankY
                    shareX -> axisBlankX + axisLineBlankX
                    shareY -> axisBlankY + axisLineBlankY
                    else -> emptyMap()
                }

                DECK_OVERLAY_THEME_BASE + sharedAxesTheme
            } else {
                // The last figure (topmost layer) has blank axes, except the axis line, which is shown if shared and ontop=True.
                val sharedAxesTheme = when {
                    shareX && shareY -> axisBlankX + axisBlankY
                    shareX -> axisBlankX
                    shareY -> axisBlankY
                    else -> emptyMap()
                }
                val axisLineX = if (shareX && !ontopX) axisLineBlankX else emptyMap()
                val axisLineY = if (shareY && !ontopY) axisLineBlankY else emptyMap()

                DECK_OVERLAY_THEME_BASE + sharedAxesTheme + axisLineX + axisLineY
            }
        }

        @Suppress("UNCHECKED_CAST")
        val existingTheme = (spec[THEME] as? Map<String, Any>) ?: emptyMap()
        return spec + (THEME to overlayTheme + existingTheme)
    }
}

internal enum class DeckPosition() {
    FIRST,
    MIDDLE,
    LAST;  // topmost layer in the deck, drawn last

    companion object {
        fun ofIndex(index: Int, maxIndex: Int): DeckPosition {
            return when (index) {
                0 -> FIRST
                maxIndex -> LAST
                else -> MIDDLE
            }
        }
    }
}
