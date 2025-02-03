package org.jetbrains.letsPlot.core.util

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
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

    fun preferredFigureSize(
        figureSpec: Map<String, Any>,
        preserveAspectRatio: Boolean,
        containerSize: DoubleVector
    ): DoubleVector {

        if (PlotConfig.isFailure(figureSpec)) {
            // just keep given size
            return containerSize
        }

        return when (PlotConfig.figSpecKind(figureSpec)) {

            FigKind.GG_BUNCH_SPEC -> throw IllegalStateException("Unsupported: GGBunch")

            FigKind.SUBPLOTS_SPEC -> {
                // Subplots figure has flexible size.
                if (!preserveAspectRatio) {
                    return containerSize
                }

                val compositeFigureConfig = CompositeFigureConfig(figureSpec, containerTheme = null) {
                    // ignore message when computing a figure size.
                }

                val defaultSize = PlotSizeHelper.compositeFigureSizeDefault(compositeFigureConfig)
                fitPlotInContainer(plotSize = defaultSize, containerSize)
            }

            FigKind.PLOT_SPEC -> {
                // Singe plot has flexible size.
                if (!preserveAspectRatio) {
                    return containerSize
                }

                val config = PlotConfigFrontend.create(figureSpec, containerTheme = null) { /*ignore messages*/ }
                val defaultSize = PlotSizeHelper.singlePlotSizeDefault(
                    figureSpec,
                    config.facets,
                    config.containsLiveMap
                )

                fitPlotInContainer(plotSize = defaultSize, containerSize)
            }
        }
    }

    private fun fitPlotInContainer(plotSize: DoubleVector, containerSize: DoubleVector): DoubleVector {
        return DoubleRectangle(DoubleVector.ZERO, containerSize)
            .shrinkToAspectRatio(plotSize)
            .dimension

    }
}