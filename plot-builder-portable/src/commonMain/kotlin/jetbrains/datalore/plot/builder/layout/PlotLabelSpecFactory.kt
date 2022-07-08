/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.plot.builder.presentation.PlotLabelSpec
import jetbrains.datalore.plot.builder.theme.AxisTheme
import jetbrains.datalore.plot.builder.theme.LegendTheme
import jetbrains.datalore.plot.builder.theme.PlotTheme
import jetbrains.datalore.vis.TextStyle

object PlotLabelSpecFactory {
    fun plotTitle(theme: PlotTheme) = plotLabelSpec(theme.titleTextStyle())

    fun plotSubtitle(theme: PlotTheme) = plotLabelSpec(theme.subtitleTextStyle())

    fun plotCaption(theme: PlotTheme) = plotLabelSpec(theme.captionTextStyle())

    fun legendTitle(theme: LegendTheme) = plotLabelSpec(theme.titleTextStyle())

    fun legendItem(theme: LegendTheme) = plotLabelSpec(theme.textTextStyle())

    fun axisTick(theme: AxisTheme) = plotLabelSpec(theme.labelTextStyle())

    fun axisTitle(theme: AxisTheme) = plotLabelSpec(theme.titleTextStyle())

    private fun plotLabelSpec(textStyle: TextStyle) = PlotLabelSpec(textStyle.size, textStyle.face.bold)
}