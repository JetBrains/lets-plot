/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.presentation

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.FontFace
import jetbrains.datalore.base.values.FontFamily
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.*
import jetbrains.datalore.plot.builder.presentation.Defaults.Plot
import jetbrains.datalore.plot.builder.presentation.Style.AXIS_TEXT
import jetbrains.datalore.plot.builder.presentation.Style.AXIS_TITLE
import jetbrains.datalore.plot.builder.presentation.Style.AXIS_TOOLTIP_TEXT
import jetbrains.datalore.plot.builder.presentation.Style.FACET_STRIP_TEXT
import jetbrains.datalore.plot.builder.presentation.Style.LEGEND_ITEM
import jetbrains.datalore.plot.builder.presentation.Style.LEGEND_TITLE
import jetbrains.datalore.plot.builder.presentation.Style.PLOT_CAPTION
import jetbrains.datalore.plot.builder.presentation.Style.PLOT_SUBTITLE
import jetbrains.datalore.plot.builder.presentation.Style.PLOT_TITLE
import jetbrains.datalore.plot.builder.presentation.Style.TOOLTIP_LABEL
import jetbrains.datalore.plot.builder.presentation.Style.TOOLTIP_TEXT
import jetbrains.datalore.plot.builder.presentation.Style.TOOLTIP_TITLE
import jetbrains.datalore.plot.builder.theme.Theme
import jetbrains.datalore.vis.TextStyle


class StyleProperties {

    private val myTextStyles: MutableMap<String, TextStyle> = mutableMapOf(
        PLOT_TITLE to createTexStyle(size = Title.FONT_SIZE.toDouble(), face = FontFace.BOLD),
        PLOT_SUBTITLE to createTexStyle(size = Subtitle.FONT_SIZE.toDouble()),
        PLOT_CAPTION to createTexStyle(size = Caption.FONT_SIZE.toDouble()),
        LEGEND_TITLE to createTexStyle(size = Legend.TITLE_FONT_SIZE.toDouble()),
        LEGEND_ITEM to createTexStyle(size = Legend.ITEM_FONT_SIZE.toDouble()),
        TOOLTIP_TEXT to createTexStyle(size = Tooltip.DATA_TOOLTIP_FONT_SIZE.toDouble()),
        TOOLTIP_TITLE to createTexStyle(size = Tooltip.DATA_TOOLTIP_FONT_SIZE.toDouble(), face = FontFace.BOLD),
        TOOLTIP_LABEL to createTexStyle(size = Tooltip.DATA_TOOLTIP_FONT_SIZE.toDouble(), face = FontFace.BOLD),
        "$AXIS_TITLE-x" to createTexStyle(size = Plot.Axis.TITLE_FONT_SIZE.toDouble()),
        "$AXIS_TITLE-y" to createTexStyle(size = Plot.Axis.TITLE_FONT_SIZE.toDouble()),
        "$AXIS_TEXT-x" to createTexStyle(size = Plot.Axis.TICK_FONT_SIZE.toDouble()),
        "$AXIS_TEXT-y" to createTexStyle(size = Plot.Axis.TICK_FONT_SIZE.toDouble()),
        "$AXIS_TOOLTIP_TEXT-x" to createTexStyle(
            size = Tooltip.AXIS_TOOLTIP_FONT_SIZE.toDouble(),
            color = Color.WHITE
        ),
        "$AXIS_TOOLTIP_TEXT-y" to createTexStyle(
            size = Tooltip.AXIS_TOOLTIP_FONT_SIZE.toDouble(),
            color = Color.WHITE
        ),
        "$FACET_STRIP_TEXT-x" to createTexStyle(size = Defaults.FONT_MEDIUM.toDouble()),
        "$FACET_STRIP_TEXT-y" to createTexStyle(size = Defaults.FONT_MEDIUM.toDouble())
    )

    fun applyTheme(theme: Theme, flippedAxis: Boolean): StyleProperties {
        myTextStyles.setColor(PLOT_TITLE, theme.plot().titleColor())
        myTextStyles.setColor(PLOT_SUBTITLE, theme.plot().subtitleColor())
        myTextStyles.setColor(PLOT_CAPTION, theme.plot().captionColor())

        myTextStyles.setColor(LEGEND_TITLE, theme.legend().titleColor())
        myTextStyles.setColor(LEGEND_ITEM, theme.legend().textColor())

        val hAxisTheme = theme.horizontalAxis(flippedAxis)
        val hAxisName = if (flippedAxis) "y" else "x"
        myTextStyles.setColor("$AXIS_TITLE-$hAxisName", hAxisTheme.titleColor())
        myTextStyles.setColor("$AXIS_TEXT-$hAxisName", hAxisTheme.labelColor())
        myTextStyles.setColor("$AXIS_TOOLTIP_TEXT-$hAxisName", hAxisTheme.tooltipTextColor())

        val vAxisTheme = theme.verticalAxis(flippedAxis)
        val vAxisName = if (flippedAxis) "x" else "y"
        myTextStyles.setColor("$AXIS_TITLE-$vAxisName", vAxisTheme.titleColor())
        myTextStyles.setColor("$AXIS_TEXT-$vAxisName", vAxisTheme.labelColor())
        myTextStyles.setColor("$AXIS_TOOLTIP_TEXT-$vAxisName", vAxisTheme.tooltipTextColor())

        myTextStyles.setColor("$FACET_STRIP_TEXT-x", theme.facets().stripTextColor())
        myTextStyles.setColor("$FACET_STRIP_TEXT-y", theme.facets().stripTextColor())

        return this
    }

    fun getClasses(): List<String> = myTextStyles.keys.toList()

    fun getProperties(className: String): TextStyle {
        return myTextStyles[className] ?: createTexStyle()
    }

    companion object {
        private val DEFAULT_FAMILY = FontFamily.forName(Defaults.FONT_FAMILY_NORMAL)
        private const val DEFAULT_SIZE = Defaults.FONT_MEDIUM.toDouble()
        private val DEFAULT_FACE = FontFace.NORMAL
        private val DEFAULT_COLOR = Color.BLACK

        private fun createTexStyle(
            family: FontFamily? = null,
            face: FontFace? = null,
            size: Double? = null,
            color: Color? = null
        ) = TextStyle(
            family ?: DEFAULT_FAMILY,
            face ?: DEFAULT_FACE,
            size ?: DEFAULT_SIZE,
            color ?: DEFAULT_COLOR
        )

        private fun MutableMap<String, TextStyle>.setColor(key: String, color: Color) {
            this[key] = createTexStyle(
                this[key]?.family,
                this[key]?.face,
                this[key]?.size,
                color
            )
        }
    }
}