/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.util

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotFacets
import org.jetbrains.letsPlot.core.plot.builder.presentation.Defaults.DEF_LARGE_PLOT_SIZE
import org.jetbrains.letsPlot.core.plot.builder.presentation.Defaults.DEF_PLOT_SIZE
import org.jetbrains.letsPlot.core.plot.builder.presentation.Defaults.TOOLBAR_HEIGHT
import org.jetbrains.letsPlot.core.spec.FigKind
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.config.CompositeFigureConfig
import org.jetbrains.letsPlot.core.spec.config.OptionsAccessor
import org.jetbrains.letsPlot.core.spec.config.PlotConfig
import org.jetbrains.letsPlot.core.spec.front.PlotConfigFrontend
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy

object PlotSizeHelper {

    /**
     * `plotSpec` can be either raw or processed.
     */
    fun singlePlotSize(
        plotSpec: Map<*, *>,
        containerSize: DoubleVector?,
        sizingPolicy: SizingPolicy,
        facets: PlotFacets,
        containsLiveMap: Boolean
    ): DoubleVector {
        if (sizingPolicy.isFixedSize()) {
            return sizingPolicy.getFixedSize()
        }

        val defaultSize = singlePlotSizeDefault(plotSpec, facets, containsLiveMap)
        return sizingPolicy.resize(defaultSize, containerSize)
    }

    /**
     * `plotSpec` can be either raw or processed.
     */
    fun singlePlotSizeDefault(
        plotSpec: Map<*, *>,
        facets: PlotFacets,
        containsLiveMap: Boolean
    ): DoubleVector {
        return getSizeOptionOrNull(plotSpec) ?: if (facets.isDefined) {
            defaultFacetedPlotSize(facets.colCount, facets.rowCount)
        } else if (containsLiveMap) {
            DEF_LARGE_PLOT_SIZE
        } else {
            DEF_PLOT_SIZE
        }
    }

    /**
     * `plotSpec` can be either raw or processed.
     */
    fun compositeFigureSize(
        config: CompositeFigureConfig,
        containerSize: DoubleVector?,
        sizingPolicy: SizingPolicy,
    ): DoubleVector {
        if (sizingPolicy.isFixedSize()) {
            return sizingPolicy.getFixedSize()
        }

        val defaultSize = compositeFigureSizeDefault(config)
        return sizingPolicy.resize(defaultSize, containerSize)
    }

    /**
     * `plotSpec` can be either raw or processed.
     */
    fun compositeFigureSizeDefault(
        config: CompositeFigureConfig,
    ): DoubleVector {
        val specifiedFigureSize = getSizeOptionOrNull(config.toMap())
        return specifiedFigureSize ?: config.layout.defaultSize()
    }

    private fun defaultFacetedPlotSize(ncol: Int, nrow: Int): DoubleVector {
        val panelWidth = DEF_PLOT_SIZE.x * (0.5 + 0.5 / ncol)
        val panelHeight = DEF_PLOT_SIZE.y * (0.5 + 0.5 / nrow)
        return DoubleVector(panelWidth * ncol, panelHeight * nrow)
    }

    private fun getSizeOptionOrNull(singlePlotSpec: Map<*, *>): DoubleVector? {
        if (!singlePlotSpec.containsKey(Option.Plot.SIZE)) {
            return null
        }
        @Suppress("UNCHECKED_CAST")
        val map = OptionsAccessor(singlePlotSpec as Map<String, Any>)
            .getMap(Option.Plot.SIZE)
        val sizeSpec = OptionsAccessor.over(map)
        val width = sizeSpec.getDouble("width")
        val height = sizeSpec.getDouble("height")
        if (width == null || height == null) {
            return null
        }
        return DoubleVector(width, height)
    }

    fun figureSizeDefault(
        figureSpec: Map<String, Any>,
    ): DoubleVector {
        if (PlotConfig.isFailure(figureSpec)) {
            return DEF_PLOT_SIZE
        }
        return when (PlotConfig.figSpecKind(figureSpec)) {
            FigKind.PLOT_SPEC -> {
                val config = PlotConfigFrontend.create(figureSpec, containerTheme = null) { /*ignore messages*/ }
                singlePlotSizeDefault(
                    figureSpec,
                    config.facets,
                    config.containsLiveMap
                )
            }

            FigKind.SUBPLOTS_SPEC -> {
                val compositeFigureConfig = CompositeFigureConfig(figureSpec, containerTheme = null) {
                    // ignore a message when computing a figure size.
                }

                compositeFigureSizeDefault(compositeFigureConfig)
            }

            FigKind.GG_BUNCH_SPEC -> throw IllegalStateException("Unsupported: GGBunch")
        }
    }

    /**
     *  Overall default figure size including toolbar height (if any).
     */
    fun figurePanelSizeDefault(
        processedSpec: Map<String, Any>,
    ): DoubleVector {
        return figureSizeDefault(processedSpec).let { size ->
            if (processedSpec.containsKey(Option.Meta.Kind.GG_TOOLBAR)) {
                DoubleVector(size.x, size.y + TOOLBAR_HEIGHT)
            } else {
                size
            }
        }
    }

    /**
     * Overall figure size given the container size and sizing policy.
     */
    fun figurePanelSizeInContainer(
        figurePanelDefaultSize: DoubleVector,  // Overall default figure size including toolbar height (if any).
        containerSize: DoubleVector,
        sizingPolicy: SizingPolicy,
        hasToolbar: Boolean,
    ): DoubleVector {

        if (sizingPolicy.isFixedSize()) {
            return sizingPolicy.getFixedSize()
        }
        // Adjust container size if the toolbar is present - subtract toolbar height first
        @Suppress("NAME_SHADOWING")
        val containerSize = if (hasToolbar) {
            DoubleVector(containerSize.x, containerSize.y - TOOLBAR_HEIGHT)
        } else {
            containerSize
        }

        val resized = sizingPolicy.resize(figurePanelDefaultSize, containerSize)
        return resized.let { size ->
            // Add back the toolbar height to get the total figure size
            if (hasToolbar) {
                DoubleVector(size.x, size.y + TOOLBAR_HEIGHT)
            } else {
                size
            }
        }
    }
}