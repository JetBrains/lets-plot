/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.plot.builder.presentation.Defaults
import jetbrains.datalore.plot.builder.presentation.PlotLabelSpec
import jetbrains.datalore.plot.builder.theme.AxisTheme
import jetbrains.datalore.plot.builder.theme.LegendTheme
import jetbrains.datalore.plot.builder.theme.PlotTheme

object PlotLabelSpecFactory {
    fun plotTitle(theme: PlotTheme) = PlotLabelSpec(
        Defaults.Common.Title.FONT_SIZE.toDouble(),
        theme.titleFontFace().bold
    )

    fun plotSubtitle(theme: PlotTheme) = PlotLabelSpec(
        Defaults.Common.Subtitle.FONT_SIZE.toDouble(),
        theme.subtitleFontFace().bold
    )

    fun plotCaption(theme: PlotTheme) = PlotLabelSpec(
        Defaults.Common.Caption.FONT_SIZE.toDouble(),
        theme.captionFontFace().bold
    )

    fun legendTitle(theme: LegendTheme) = PlotLabelSpec(
        Defaults.Common.Legend.TITLE_FONT_SIZE.toDouble(),
        theme.titleFontFace().bold
    )

    fun legendItem(theme: LegendTheme) = PlotLabelSpec(
        Defaults.Common.Legend.ITEM_FONT_SIZE.toDouble(),
        theme.textFontFace().bold
    )

    fun axisTick(theme: AxisTheme) = PlotLabelSpec(
        Defaults.Plot.Axis.TICK_FONT_SIZE.toDouble(),
        theme.labelFontFace().bold
    )

    fun axisTitle(theme: AxisTheme) = PlotLabelSpec(
        Defaults.Plot.Axis.TITLE_FONT_SIZE.toDouble(),
        theme.titleFontFace().bold
    )
}