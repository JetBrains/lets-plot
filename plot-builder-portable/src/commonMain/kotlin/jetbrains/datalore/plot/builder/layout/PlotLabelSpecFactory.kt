/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.values.Font
import jetbrains.datalore.plot.builder.presentation.PlotLabelSpec
import jetbrains.datalore.plot.builder.theme.AxisTheme
import jetbrains.datalore.plot.builder.theme.LegendTheme
import jetbrains.datalore.plot.builder.theme.PlotTheme
import jetbrains.datalore.vis.TextStyle

object PlotLabelSpecFactory {
    fun plotTitle(theme: PlotTheme) = plotLabelSpec(theme.titleStyle())

    fun plotSubtitle(theme: PlotTheme) = plotLabelSpec(theme.subtitleStyle())

    fun plotCaption(theme: PlotTheme) = plotLabelSpec(theme.captionStyle())

    fun legendTitle(theme: LegendTheme) = plotLabelSpec(theme.titleStyle())

    fun legendItem(theme: LegendTheme) = plotLabelSpec(theme.textStyle())

    fun axisTick(theme: AxisTheme) = plotLabelSpec(theme.labelStyle())

    fun axisTitle(theme: AxisTheme) = plotLabelSpec(theme.titleStyle())

    private fun plotLabelSpec(textStyle: TextStyle) =
        PlotLabelSpec(Font(textStyle.family, textStyle.size.toInt(), textStyle.face.bold, textStyle.face.italic))
}