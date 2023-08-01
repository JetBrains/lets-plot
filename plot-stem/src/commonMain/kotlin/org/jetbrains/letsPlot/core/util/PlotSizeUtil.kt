package org.jetbrains.letsPlot.core.util

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.spec.FigKind
import org.jetbrains.letsPlot.core.spec.config.CompositeFigureConfig
import org.jetbrains.letsPlot.core.spec.config.PlotConfig
import org.jetbrains.letsPlot.core.spec.front.PlotConfigFrontend

object PlotSizeUtil {

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

            FigKind.GG_BUNCH_SPEC -> {
                // Don't scale GGBunch.
                val bunchSize = PlotSizeHelper.plotBunchSize(figureSpec)
                DoubleVector(bunchSize.x, bunchSize.y)
            }

            FigKind.SUBPLOTS_SPEC -> {
                // Subplots figure has flexible size.
                if (!preserveAspectRatio) {
                    return containerSize
                }

                val compositeFigureConfig = CompositeFigureConfig(figureSpec) {
                    // ignore message when computing a figure size.
                }

                val defaultSize = PlotSizeHelper.compositeFigureSize(
                    compositeFigureConfig,
                    plotSize = null,
                    plotMaxWidth = null,
                    plotPreferredWidth = null,
                )
                fitPlotInContainer(plotSize = defaultSize, containerSize)
            }

            FigKind.PLOT_SPEC -> {
                // Singe plot has flexible size.
                if (!preserveAspectRatio) {
                    return containerSize
                }

                val config = PlotConfigFrontend.create(figureSpec) { /*ignore messages*/ }
                val defaultSize = PlotSizeHelper.singlePlotSize(
                    figureSpec,
                    plotSize = null,
                    plotMaxWidth = null,
                    plotPreferredWidth = null,
                    config.facets,
                    config.containsLiveMap
                )

                fitPlotInContainer(plotSize = defaultSize, containerSize)
            }
        }
    }

    private fun fitPlotInContainer(plotSize: DoubleVector, containerSize: DoubleVector): DoubleVector {
        val aspectRatio = plotSize.x / plotSize.y

        val width = containerSize.x
        val height = containerSize.y

        return if (aspectRatio >= 1.0) {
            val plotHeight = width / aspectRatio
            val scaling = if (plotHeight > height) height / plotHeight else 1.0
            DoubleVector(width * scaling, plotHeight * scaling)
        } else {
            val plotWidth = height * aspectRatio
            val scaling = if (plotWidth > width) width / plotWidth else 1.0
            DoubleVector(plotWidth * scaling, height * scaling)
        }
    }
}