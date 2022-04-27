/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.presentation

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.defaultTheme.values.FontProperties
import jetbrains.datalore.plot.builder.theme.Theme


class ThemeTextStyler(theme: Theme, flippedAxis: Boolean) : TextStyler() {

    init {
        myTextStyles.setColor(Style.PLOT_TITLE, theme.plot().titleColor())
        myTextStyles.setColor(Style.PLOT_SUBTITLE, theme.plot().subtitleColor())
        myTextStyles.setColor(Style.PLOT_CAPTION, theme.plot().captionColor())

        myTextStyles.setColor(Style.LEGEND_TITLE, theme.legend().titleColor())
        myTextStyles.setColor(Style.LEGEND_ITEM, theme.legend().textColor())

        val hAxisTheme = theme.horizontalAxis(flippedAxis)
        var suffix = "-" + hAxisTheme.suffix()
        myTextStyles.setColor(Style.AXIS_TITLE + suffix, hAxisTheme.titleColor())
        myTextStyles.setColor(Style.AXIS_TEXT + suffix, hAxisTheme.labelColor())
        myTextStyles.setColor(Style.AXIS_TOOLTIP_TEXT + suffix, hAxisTheme.tooltipTextColor())

        val vAxisTheme = theme.verticalAxis(flippedAxis)
        suffix = "-" + vAxisTheme.suffix()
        myTextStyles.setColor(Style.AXIS_TITLE + suffix, vAxisTheme.titleColor())
        myTextStyles.setColor(Style.AXIS_TEXT + suffix, vAxisTheme.labelColor())
        myTextStyles.setColor(Style.AXIS_TOOLTIP_TEXT + suffix, vAxisTheme.tooltipTextColor())

        myTextStyles.setColor("${Style.FACET_STRIP_TEXT}-x", theme.facets().stripTextColor())
        myTextStyles.setColor("${Style.FACET_STRIP_TEXT}-y", theme.facets().stripTextColor())
    }

    companion object {
        private fun MutableMap<String, FontProperties>.setColor(key: String, color: Color) {
            this[key] = fontProperties(
                this[key]?.family,
                this[key]?.face,
                this[key]?.size,
                color
            )
        }
    }
}