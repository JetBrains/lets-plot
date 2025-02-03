package org.jetbrains.letsPlot.core.util

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.builder.presentation.Defaults.DEF_PLOT_SIZE
import org.jetbrains.letsPlot.core.spec.FigKind
import org.jetbrains.letsPlot.core.spec.config.CompositeFigureConfig
import org.jetbrains.letsPlot.core.spec.config.PlotConfig
import org.jetbrains.letsPlot.core.spec.front.PlotConfigFrontend

object PlotSizeUtil {

    fun figureSizeDefault(
        figureSpec: Map<String, Any>,
    ): DoubleVector {
        if (PlotConfig.isFailure(figureSpec)) {
            return DEF_PLOT_SIZE
        }
        return when (PlotConfig.figSpecKind(figureSpec)) {
            FigKind.PLOT_SPEC -> {
                val config = PlotConfigFrontend.create(figureSpec, containerTheme = null) { /*ignore messages*/ }
                PlotSizeHelper.singlePlotSizeDefault(
                    figureSpec,
                    config.facets,
                    config.containsLiveMap
                )
            }

            FigKind.SUBPLOTS_SPEC -> {
                val compositeFigureConfig = CompositeFigureConfig(figureSpec, containerTheme = null) {
                    // ignore message when computing a figure size.
                }

                PlotSizeHelper.compositeFigureSizeDefault(compositeFigureConfig)
            }

            FigKind.GG_BUNCH_SPEC -> throw IllegalStateException("Unsupported: GGBunch")
        }
    }
}