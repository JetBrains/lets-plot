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
import jetbrains.datalore.vis.StyleProperties
import jetbrains.datalore.vis.TextStyle


class ThemeStyleProperties: StyleProperties(defaultTextStyles, Defaults.FONT_FAMILY_NORMAL, DEFAULT_SIZE) {

    fun applyTheme(theme: Theme, flippedAxis: Boolean): StyleProperties {
        with(textStyles) {
            setColor(PLOT_TITLE, theme.plot().titleColor())
            setColor(PLOT_SUBTITLE, theme.plot().subtitleColor())
            setColor(PLOT_CAPTION, theme.plot().captionColor())

            setColor(LEGEND_TITLE, theme.legend().titleColor())
            setColor(LEGEND_ITEM, theme.legend().textColor())

            val hAxisTheme = theme.horizontalAxis(flippedAxis)
            val hAxisName = if (flippedAxis) "y" else "x"
            setColor("$AXIS_TITLE-$hAxisName", hAxisTheme.titleColor())
            setColor("$AXIS_TEXT-$hAxisName", hAxisTheme.labelColor())
            setColor("$AXIS_TOOLTIP_TEXT-$hAxisName", hAxisTheme.tooltipTextColor())

            val vAxisTheme = theme.verticalAxis(flippedAxis)
            val vAxisName = if (flippedAxis) "x" else "y"
            setColor("$AXIS_TITLE-$vAxisName", vAxisTheme.titleColor())
            setColor("$AXIS_TEXT-$vAxisName", vAxisTheme.labelColor())
            setColor("$AXIS_TOOLTIP_TEXT-$vAxisName", vAxisTheme.tooltipTextColor())

            setColor("$FACET_STRIP_TEXT-x", theme.facets().stripTextColor())
            setColor("$FACET_STRIP_TEXT-y", theme.facets().stripTextColor())
        }

        return this
    }

    companion object {
        private val defaultTextStyles: MutableMap<String, TextStyle> by lazy {
            mutableMapOf(
                PLOT_TITLE to createTextStyle(size = Title.FONT_SIZE.toDouble(), face = FontFace.BOLD),
                PLOT_SUBTITLE to createTextStyle(size = Subtitle.FONT_SIZE.toDouble()),
                PLOT_CAPTION to createTextStyle(size = Caption.FONT_SIZE.toDouble()),
                LEGEND_TITLE to createTextStyle(size = Legend.TITLE_FONT_SIZE.toDouble()),
                LEGEND_ITEM to createTextStyle(size = Legend.ITEM_FONT_SIZE.toDouble()),
                TOOLTIP_TEXT to createTextStyle(size = Tooltip.DATA_TOOLTIP_FONT_SIZE.toDouble()),
                TOOLTIP_TITLE to createTextStyle(size = Tooltip.DATA_TOOLTIP_FONT_SIZE.toDouble(), face = FontFace.BOLD),
                TOOLTIP_LABEL to createTextStyle(size = Tooltip.DATA_TOOLTIP_FONT_SIZE.toDouble(), face = FontFace.BOLD),
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
                "$FACET_STRIP_TEXT-x" to createTextStyle(size = Defaults.FONT_MEDIUM.toDouble()),
                "$FACET_STRIP_TEXT-y" to createTextStyle(size = Defaults.FONT_MEDIUM.toDouble())
            )
        }

        private val DEFAULT_FAMILY = FontFamily.forName(Defaults.FONT_FAMILY_NORMAL)
        private const val DEFAULT_SIZE = Defaults.FONT_MEDIUM.toDouble()
        private val DEFAULT_FACE = FontFace.NORMAL
        private val DEFAULT_COLOR = Color.BLACK

        private fun createTextStyle(
            family: FontFamily = DEFAULT_FAMILY,
            face: FontFace = DEFAULT_FACE,
            size: Double = DEFAULT_SIZE,
            color: Color = DEFAULT_COLOR
        ) = TextStyle(family, face, size, color)

        private fun MutableMap<String, TextStyle>.setColor(key: String, color: Color) {
            this[key] = createTextStyle(
                this[key]?.family ?: DEFAULT_FAMILY,
                this[key]?.face ?: DEFAULT_FACE,
                this[key]?.size ?: DEFAULT_SIZE,
                color
            )
        }
    }
}