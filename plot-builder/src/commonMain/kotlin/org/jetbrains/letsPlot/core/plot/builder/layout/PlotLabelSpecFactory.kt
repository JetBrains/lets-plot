/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout

import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.base.theme.FacetStripTheme
import org.jetbrains.letsPlot.core.plot.base.theme.LegendTheme
import org.jetbrains.letsPlot.core.plot.base.theme.PlotTheme
import org.jetbrains.letsPlot.core.plot.base.theme.ThemeTextStyle
import org.jetbrains.letsPlot.core.plot.builder.presentation.LabelSpec
import org.jetbrains.letsPlot.core.plot.builder.presentation.PlotLabelSpec

object PlotLabelSpecFactory {
    const val DISTANCE_TO_LABEL_IN_CHARS = "_"

    fun plotTitle(theme: PlotTheme) = plotLabelSpec(theme.titleStyle())

    fun plotSubtitle(theme: PlotTheme) = plotLabelSpec(theme.subtitleStyle())

    fun plotCaption(theme: PlotTheme) = plotLabelSpec(theme.captionStyle())

    fun legendTitle(theme: LegendTheme) = plotLabelSpec(theme.titleStyle())

    fun legendItem(theme: LegendTheme) = plotLabelSpec(theme.textStyle())

    fun axisTick(theme: AxisTheme) = plotLabelSpec(theme.labelStyle())

    fun axisTitle(theme: AxisTheme) = plotLabelSpec(theme.titleStyle())

    fun facetText(theme: FacetStripTheme) = plotLabelSpec(theme.stripTextStyle())

    private fun plotLabelSpec(textStyle: ThemeTextStyle): LabelSpec {
        return PlotLabelSpec(
            Font(
                textStyle.family,
                textStyle.size.toInt(), //?
                textStyle.face.bold,
                textStyle.face.italic
            )
        )
    }
}