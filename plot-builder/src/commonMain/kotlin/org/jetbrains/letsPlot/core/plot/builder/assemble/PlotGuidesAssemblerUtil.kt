/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.theme.LegendTheme
import org.jetbrains.letsPlot.core.plot.builder.assemble.tiles.GeomLayerInfo

internal object PlotGuidesAssemblerUtil {
    fun mappedRenderedAesToCreateGuides(
        layer: GeomLayerInfo,
        guideOptionsMap: Map<String, GuideOptionsList>
    ): List<Aes<*>> {
        if (layer.isLegendDisabled) {
            // ToDo: add support for:
            // show_legend = True     : show all aesthetics in legend
            // show_legend = [.. list of aesthetics to show ..]     : show selected aesthetics in legend
            // see: https://ggplot2.tidyverse.org/reference/geom_point.html
            return emptyList()
        }

        val result = ArrayList<Aes<*>>()
        for (aes in layer.renderedAes()) {
            if (Aes.noGuideNeeded(aes)) {
                continue
            }
            if (layer.hasConstant(aes)) {
                // constants mask aes mappings
                continue
            }
            if (!layer.hasBinding(aes)) {
                continue
            }
            if (guideOptionsMap[aes.name]?.hasNone() == true) {
                continue
            }

            result.add(aes)
        }

        return result
    }

    fun createColorBarAssembler(
        scaleName: String,
        transformedDomain: DoubleSpan,
        scale: Scale,
        scaleMapper: ScaleMapper<Color>,
        options: ColorBarOptions?,
        theme: LegendTheme
    ) = ColorBarAssembler(
        scaleName,
        transformedDomain,
        scale,
        scaleMapper,
        theme,
        options
    )

    fun fitsColorBar(aes: Aes<*>, scale: Scale): Boolean {
        return aes.isColor && scale.isContinuous
    }

    fun checkFitsColorBar(aes: Aes<*>, scale: Scale) {
        check(aes.isColor) { "Color-bar is not applicable to $aes aesthetic" }
        check(scale.isContinuous) { "Color-bar is only applicable when both domain and color palette are continuous" }
    }
}
