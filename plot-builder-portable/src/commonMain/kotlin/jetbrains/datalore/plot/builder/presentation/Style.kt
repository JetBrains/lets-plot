/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.presentation

import jetbrains.datalore.plot.builder.presentation.Defaults.FONT_FAMILY_NORMAL
import jetbrains.datalore.plot.builder.theme.Theme
import jetbrains.datalore.vis.StyleSheet
import jetbrains.datalore.vis.StyleSheet.Companion.toCSS
import jetbrains.datalore.vis.TextStyle

object Style {
    // classes
    const val PLOT_CONTAINER = "plt-container"
    const val PLOT_TITLE = "plot-title"
    const val PLOT_SUBTITLE = "plot-subtitle"
    const val PLOT_CAPTION = "plot-caption"

    const val AXIS_TITLE = "axis-title"
    const val AXIS_TEXT = "axis-text"

    const val LEGEND_TITLE = "legend-title"
    const val LEGEND_ITEM = "legend-item"

    const val TOOLTIP_TEXT = "tooltip-text"
    const val TOOLTIP_TITLE = "tooltip-title"
    const val TOOLTIP_LABEL = "tooltip-label"
    const val AXIS_TOOLTIP_TEXT = "axis-tooltip-text"

    const val FACET_STRIP_TEXT = "facet-strip-text"

    private val CSS = """
        |.$PLOT_CONTAINER {
        |   font-family: $FONT_FAMILY_NORMAL;
        |   user-select: none;
        |   -webkit-user-select: none;
        |   -moz-user-select: none;
        |   -ms-user-select: none;
        |}
        |text {
        |   fill: ${StyleSheet.UNDEFINED_FONT_COLOR.toHexColor()};
        |   ${StyleSheet.UNDEFINED_FONT_FACE.toCSS()}   
        |
        |   text-rendering: optimizeLegibility;
        |}
    """.trimMargin()

    fun generateCSS(styleSheet: StyleSheet, plotId: String?, decorationLayerId: String?): String {
        val css = StringBuilder(CSS)
        css.append('\n')
        styleSheet.getClasses().forEach { className ->
            val id = when (className) {
                TOOLTIP_TEXT,
                TOOLTIP_TITLE,
                TOOLTIP_LABEL,
                "$AXIS_TOOLTIP_TEXT-x",
                "$AXIS_TOOLTIP_TEXT-y" -> decorationLayerId
                else -> plotId
            }
            css.append(styleSheet.toCSS(className, id))
        }
        return css.toString()
    }

    fun default(): StyleSheet {
        return StyleSheet(
            Defaults.DEFAULT_TEXT_STYLES,
            defaultFamily = FONT_FAMILY_NORMAL
        )
    }

    fun fromTheme(theme: Theme, flippedAxis: Boolean): StyleSheet {
        val hAxisTheme = theme.horizontalAxis(flippedAxis)
        val hAxisName = if (flippedAxis) "y" else "x"
        val vAxisTheme = theme.verticalAxis(flippedAxis)
        val vAxisName = if (flippedAxis) "x" else "y"

        val textStyles = mapOf(
            PLOT_TITLE to theme.plot().titleTextStyle(),
            PLOT_SUBTITLE to theme.plot().subtitleTextStyle(),
            PLOT_CAPTION to theme.plot().captionTextStyle(),

            LEGEND_TITLE to theme.legend().titleTextStyle(),
            LEGEND_ITEM to theme.legend().textTextStyle(),

            "$AXIS_TITLE-$hAxisName" to hAxisTheme.titleTextStyle(),
            "$AXIS_TEXT-$hAxisName" to hAxisTheme.labelTextStyle(),
            "$AXIS_TOOLTIP_TEXT-$hAxisName" to hAxisTheme.tooltipTextStyle(),

            "$AXIS_TITLE-$vAxisName" to vAxisTheme.titleTextStyle(),
            "$AXIS_TEXT-$vAxisName" to vAxisTheme.labelTextStyle(),
            "$AXIS_TOOLTIP_TEXT-$vAxisName" to vAxisTheme.tooltipTextStyle(),

            "$FACET_STRIP_TEXT-x" to theme.facets().stripTextStyle(),
            "$FACET_STRIP_TEXT-y" to theme.facets().stripTextStyle(),

            TOOLTIP_TEXT to theme.tooltips().textStyle(),
            TOOLTIP_TITLE to theme.tooltips().titleTextStyle(),
            TOOLTIP_LABEL to theme.tooltips().labelTextStyle(),
        )

        return StyleSheet(textStyles, defaultFamily = FONT_FAMILY_NORMAL)
    }
}
