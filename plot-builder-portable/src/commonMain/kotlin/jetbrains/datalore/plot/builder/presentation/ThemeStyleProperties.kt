/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.presentation

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.presentation.Style.AXIS_TEXT
import jetbrains.datalore.plot.builder.presentation.Style.AXIS_TITLE
import jetbrains.datalore.plot.builder.presentation.Style.AXIS_TOOLTIP_TEXT
import jetbrains.datalore.plot.builder.presentation.Style.FACET_STRIP_TEXT
import jetbrains.datalore.plot.builder.presentation.Style.LEGEND_ITEM
import jetbrains.datalore.plot.builder.presentation.Style.LEGEND_TITLE
import jetbrains.datalore.plot.builder.presentation.Style.PLOT_CAPTION
import jetbrains.datalore.plot.builder.presentation.Style.PLOT_SUBTITLE
import jetbrains.datalore.plot.builder.presentation.Style.PLOT_TITLE
import jetbrains.datalore.plot.builder.presentation.StyleProperties.Companion.FontProperties
import jetbrains.datalore.plot.builder.theme.Theme


class ThemeStyleProperties(theme: Theme, flippedAxis: Boolean) : StyleProperties() {

    init {
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
    }

    companion object {
        private fun MutableMap<String, FontProperties>.setColor(key: String, color: Color) {
            this[key] = FontProperties.create(
                this[key]?.family,
                this[key]?.face,
                this[key]?.size,
                color
            )
        }
    }
}