/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.presentation

import org.jetbrains.letsPlot.commons.values.FontFace
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.base.theme.ThemeTextStyle
import org.jetbrains.letsPlot.core.plot.builder.presentation.Defaults.FONT_FAMILY_NORMAL
import org.jetbrains.letsPlot.datamodel.svg.style.StyleSheet
import org.jetbrains.letsPlot.datamodel.svg.style.TextStyle

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

        fun ts(themeStyle: ThemeTextStyle): TextStyle {
            return TextStyle(
                themeStyle.family.name,
                themeStyle.face,
                themeStyle.size,
                themeStyle.color
            )
        }

        val textStyles = mapOf(
            PLOT_TITLE to ts(theme.plot().titleStyle()),
            PLOT_SUBTITLE to ts(theme.plot().subtitleStyle()),
            PLOT_CAPTION to ts(theme.plot().captionStyle()),

            RichText.HYPERLINK_ELEMENT_CLASS to TextStyle(
                color = theme.colors().brush(),
                family = TextStyle.NONE_FAMILY,
                size = TextStyle.NONE_SIZE,
                face = FontFace.NORMAL,
            ),

            LEGEND_TITLE to ts(theme.legend().titleStyle()),
            LEGEND_ITEM to ts(theme.legend().textStyle()),

            "$AXIS_TITLE-$hAxisName" to ts(hAxisTheme.titleStyle()),
            "$AXIS_TEXT-$hAxisName" to ts(hAxisTheme.labelStyle()),
            "$AXIS_TOOLTIP_TEXT-$hAxisName" to ts(hAxisTheme.tooltipTextStyle()),

            "$AXIS_TITLE-$vAxisName" to ts(vAxisTheme.titleStyle()),
            "$AXIS_TEXT-$vAxisName" to ts(vAxisTheme.labelStyle()),
            "$AXIS_TOOLTIP_TEXT-$vAxisName" to ts(vAxisTheme.tooltipTextStyle()),

            "$FACET_STRIP_TEXT-x" to ts(theme.facets().horizontalFacetStrip().stripTextStyle()),
            "$FACET_STRIP_TEXT-y" to ts(theme.facets().verticalFacetStrip().stripTextStyle()),

            TOOLTIP_TEXT to ts(theme.tooltips().textStyle()),
            TOOLTIP_TITLE to ts(theme.tooltips().titleStyle()),
            TOOLTIP_LABEL to ts(theme.tooltips().labelStyle()),
        )

        return StyleSheet(textStyles, defaultFamily = FONT_FAMILY_NORMAL)
    }
}
