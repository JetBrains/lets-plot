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
    const val DISTANCE_TO_LABEL_IN_CHARS = "_"

    fun plotTitle(theme: PlotTheme) = plotLabelSpec(theme.titleStyle(), theme.textWidthScale())

    fun plotSubtitle(theme: PlotTheme) = plotLabelSpec(theme.subtitleStyle(), theme.textWidthScale())

    fun plotCaption(theme: PlotTheme) = plotLabelSpec(theme.captionStyle(), theme.textWidthScale())

    fun legendTitle(theme: LegendTheme) = plotLabelSpec(theme.titleStyle(), theme.textWidthScale())

    fun legendItem(theme: LegendTheme) = plotLabelSpec(theme.textStyle(), theme.textWidthScale())

    fun axisTick(theme: AxisTheme) = plotLabelSpec(theme.labelStyle(), theme.textWidthScale())

    fun axisTitle(theme: AxisTheme) = plotLabelSpec(theme.titleStyle(), theme.textWidthScale())

    private fun plotLabelSpec(textStyle: TextStyle, widthScaleFactor: Double) =
        PlotLabelSpec(Font(textStyle.family, textStyle.size.toInt(), textStyle.face.bold, textStyle.face.italic), textStyle.monospaced, widthScaleFactor)
}