/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.presentation

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.FontFace
import jetbrains.datalore.base.values.FontFamily
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.*
import jetbrains.datalore.plot.builder.presentation.Defaults.FONT_FAMILY_NORMAL
import jetbrains.datalore.plot.builder.presentation.Defaults.FONT_MEDIUM
import jetbrains.datalore.plot.builder.presentation.Defaults.Plot
import jetbrains.datalore.plot.builder.theme.Theme
import jetbrains.datalore.vis.StyleSheet
import jetbrains.datalore.vis.StyleSheet.Companion.toCSS
import jetbrains.datalore.vis.TextStyle

object Style {
    // classes
    const val PLOT_CONTAINER = "plt-container"
    const val PLOT = "plt-plot"
    const val PLOT_TITLE = "plot-title"
    const val PLOT_SUBTITLE = "plot-subtitle"
    const val PLOT_CAPTION = "plot-caption"

    const val AXIS = "plt-axis"
    const val AXIS_TITLE = "axis-title"
    const val AXIS_TEXT = "axis-text"
    const val TICK = "tick"

    const val LEGEND = "legend"
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


    private val DEFAULT_FAMILY = FontFamily.forName(FONT_FAMILY_NORMAL)
    private const val DEFAULT_SIZE = FONT_MEDIUM.toDouble()
    private val DEFAULT_FACE = FontFace.NORMAL
    private val DEFAULT_COLOR = Color.BLACK

    private fun createTextStyle(
        family: FontFamily = DEFAULT_FAMILY,
        face: FontFace = DEFAULT_FACE,
        size: Double = DEFAULT_SIZE,
        color: Color = DEFAULT_COLOR
    ) = TextStyle(family, face, size, color)

    private val DEFAULT_TEXT_STYLES = mapOf(
        PLOT_TITLE to createTextStyle(size = Title.FONT_SIZE.toDouble(), face = FontFace.BOLD),
        PLOT_SUBTITLE to createTextStyle(size = Subtitle.FONT_SIZE.toDouble()),
        PLOT_CAPTION to createTextStyle(size = Caption.FONT_SIZE.toDouble()),
        LEGEND_TITLE to createTextStyle(size = Legend.TITLE_FONT_SIZE.toDouble()),
        LEGEND_ITEM to createTextStyle(size = Legend.ITEM_FONT_SIZE.toDouble()),
        TOOLTIP_TEXT to createTextStyle(size = Tooltip.DATA_TOOLTIP_FONT_SIZE.toDouble()),
        TOOLTIP_TITLE to createTextStyle(
            size = Tooltip.DATA_TOOLTIP_FONT_SIZE.toDouble(),
            face = FontFace.BOLD
        ),
        TOOLTIP_LABEL to createTextStyle(
            size = Tooltip.DATA_TOOLTIP_FONT_SIZE.toDouble(),
            face = FontFace.BOLD
        ),
        "$AXIS_TITLE-x" to createTextStyle(size = Plot.Axis.TITLE_FONT_SIZE.toDouble()),
        "$AXIS_TITLE-y" to createTextStyle(size = Plot.Axis.TITLE_FONT_SIZE.toDouble()),
        "$AXIS_TEXT-x" to createTextStyle(size = Plot.Axis.TICK_FONT_SIZE.toDouble()),
        "$AXIS_TEXT-y" to createTextStyle(size = Plot.Axis.TICK_FONT_SIZE.toDouble()),
        "$AXIS_TOOLTIP_TEXT-x" to createTextStyle(
            size = Tooltip.AXIS_TOOLTIP_FONT_SIZE.toDouble(),
            color = Color.WHITE
        ),
        "$AXIS_TOOLTIP_TEXT-y" to createTextStyle(
            size = Tooltip.AXIS_TOOLTIP_FONT_SIZE.toDouble(),
            color = Color.WHITE
        ),
        "$FACET_STRIP_TEXT-x" to createTextStyle(size = FONT_MEDIUM.toDouble()),
        "$FACET_STRIP_TEXT-y" to createTextStyle(size = FONT_MEDIUM.toDouble())
    )

    fun default(): StyleSheet {
        return StyleSheet(
            DEFAULT_TEXT_STYLES,
            defaultFamily = FONT_FAMILY_NORMAL,
            defaultSize = DEFAULT_SIZE
        )
    }

    fun fromTheme(theme: Theme, flippedAxis: Boolean): StyleSheet {
        fun MutableMap<String, TextStyle>.put(className: String, color: Color, fontFace: FontFace) {
            this[className] = (DEFAULT_TEXT_STYLES[className] ?: createTextStyle()).copy(face = fontFace, color = color)
        }

        val textStyles = mutableMapOf<String, TextStyle>()
        with(textStyles) {
            put(PLOT_TITLE, theme.plot().titleColor(), theme.plot().titleFontFace() )
            put(PLOT_SUBTITLE, theme.plot().subtitleColor(), theme.plot().subtitleFontFace())
            put(PLOT_CAPTION, theme.plot().captionColor(), theme.plot().captionFontFace())

            put(LEGEND_TITLE, theme.legend().titleColor(), theme.legend().titleFontFace())
            put(LEGEND_ITEM, theme.legend().textColor(), theme.legend().textFontFace())

            val hAxisTheme = theme.horizontalAxis(flippedAxis)
            val hAxisName = if (flippedAxis) "y" else "x"
            put("$AXIS_TITLE-$hAxisName", hAxisTheme.titleColor(), hAxisTheme.titleFontFace())
            put("$AXIS_TEXT-$hAxisName", hAxisTheme.labelColor(), hAxisTheme.labelFontFace())
            put("$AXIS_TOOLTIP_TEXT-$hAxisName", hAxisTheme.tooltipTextColor(), hAxisTheme.tooltipFontFace())

            val vAxisTheme = theme.verticalAxis(flippedAxis)
            val vAxisName = if (flippedAxis) "x" else "y"
            put("$AXIS_TITLE-$vAxisName", vAxisTheme.titleColor(), vAxisTheme.titleFontFace())
            put("$AXIS_TEXT-$vAxisName", vAxisTheme.labelColor(), vAxisTheme.labelFontFace())
            put("$AXIS_TOOLTIP_TEXT-$vAxisName", vAxisTheme.tooltipTextColor(), vAxisTheme.tooltipFontFace())

            put("$FACET_STRIP_TEXT-x", theme.facets().stripTextColor(), theme.facets().stripFontFace())
            put("$FACET_STRIP_TEXT-y", theme.facets().stripTextColor(), theme.facets().stripFontFace())

            put(TOOLTIP_TEXT, theme.tooltips().textColor(), theme.tooltips().textFontFace())
            put(TOOLTIP_TITLE, theme.tooltips().titleTextColor(), theme.tooltips().titleTextFontFace())
            put(TOOLTIP_LABEL, theme.tooltips().textColor(), FontFace.BOLD + theme.tooltips().textFontFace())
        }
        return StyleSheet(
            textStyles,
            defaultFamily = FONT_FAMILY_NORMAL,
            defaultSize = DEFAULT_SIZE
        )
    }
}
