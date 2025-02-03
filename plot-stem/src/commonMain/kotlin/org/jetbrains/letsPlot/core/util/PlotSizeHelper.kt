/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.util

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotFacets
import org.jetbrains.letsPlot.core.plot.builder.presentation.Defaults.DEF_LARGE_PLOT_SIZE
import org.jetbrains.letsPlot.core.plot.builder.presentation.Defaults.DEF_PLOT_SIZE
import org.jetbrains.letsPlot.core.spec.FigKind
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.config.BunchConfig
import org.jetbrains.letsPlot.core.spec.config.CompositeFigureConfig
import org.jetbrains.letsPlot.core.spec.config.OptionsAccessor
import org.jetbrains.letsPlot.core.spec.config.PlotConfig
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

    private fun bunchItemBoundsList(bunchSpec: Map<String, Any>): List<DoubleRectangle> {
        val bunchConfig = BunchConfig(bunchSpec)
        if (bunchConfig.bunchItems.isEmpty()) {
            throw IllegalArgumentException("No plots in the bunch")
        }

        val plotBounds = ArrayList<DoubleRectangle>()
        for (bunchItem in bunchConfig.bunchItems) {
            plotBounds.add(
                DoubleRectangle(
                    DoubleVector(bunchItem.x, bunchItem.y),
                    bunchItemSize(bunchItem)
                )
            )
        }
        return plotBounds
    }

    /**
     * Expects 'processed specs' (aka client specs)
     */
    internal fun bunchItemSize(bunchItem: BunchConfig.BunchItem): DoubleVector {
        return if (bunchItem.hasSize()) {
            bunchItem.size
        } else {
            singlePlotSizeDefault(
                bunchItem.featureSpec,
                PlotFacets.UNDEFINED, false
            )
        }
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

    fun plotBunchSize(plotBunchFpec: Map<String, Any>): DoubleVector {
        require(PlotConfig.figSpecKind(plotBunchFpec) == FigKind.GG_BUNCH_SPEC) {
            "Plot Bunch is expected but was kind: ${PlotConfig.figSpecKind(plotBunchFpec)}"
        }
        return plotBunchSize(bunchItemBoundsList(plotBunchFpec))
    }

    private fun plotBunchSize(bunchItemBoundsIterable: Iterable<DoubleRectangle>): DoubleVector {
        return bunchItemBoundsIterable
            .fold(DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)) { acc, bounds ->
                acc.union(bounds)
            }
            .dimension
    }
}