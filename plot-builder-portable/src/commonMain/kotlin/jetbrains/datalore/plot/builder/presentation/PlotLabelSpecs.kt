/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.presentation

import jetbrains.datalore.vis.StyleSheet

object PlotLabelSpecs {
    private val labelSpecs: MutableMap<String, LabelSpec> = mutableMapOf(
        Style.PLOT_TITLE to LabelMetrics(Defaults.Common.Title.FONT_SIZE.toDouble(), bold = true),
        Style.PLOT_SUBTITLE to LabelMetrics(Defaults.Common.Subtitle.FONT_SIZE.toDouble()),
        Style.PLOT_CAPTION to LabelMetrics(Defaults.Common.Caption.FONT_SIZE.toDouble()),

        Style.AXIS_TEXT to LabelMetrics(Defaults.Plot.Axis.TICK_FONT_SIZE.toDouble()),
        Style.AXIS_TITLE to LabelMetrics(Defaults.Plot.Axis.TITLE_FONT_SIZE.toDouble()),

        Style.LEGEND_TITLE to LabelMetrics(Defaults.Common.Legend.TITLE_FONT_SIZE.toDouble()),
        Style.LEGEND_ITEM to LabelMetrics(Defaults.Common.Legend.ITEM_FONT_SIZE.toDouble())
    )

    fun initWithStyleSheet(styleSheet: StyleSheet) {
        fun putLabelSpec(className: String) {
            val textStyle = styleSheet.getTextStyle(className)
            labelSpecs[className] = LabelMetrics(textStyle.size, textStyle.face.bold, monospaced = false)
        }

        putLabelSpec(Style.PLOT_TITLE)
        putLabelSpec(Style.PLOT_SUBTITLE)
        putLabelSpec(Style.PLOT_CAPTION)

        putLabelSpec(Style.AXIS_TEXT)
        putLabelSpec(Style.AXIS_TITLE)

        putLabelSpec(Style.LEGEND_TITLE)
        putLabelSpec(Style.LEGEND_ITEM)
    }

    fun get(className: String) = labelSpecs[className] ?: LabelMetrics()
}