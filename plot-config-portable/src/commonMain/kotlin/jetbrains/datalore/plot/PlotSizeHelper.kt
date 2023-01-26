/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.unsupported.UNSUPPORTED
import jetbrains.datalore.plot.builder.assemble.PlotFacets
import jetbrains.datalore.plot.builder.presentation.Defaults.ASPECT_RATIO
import jetbrains.datalore.plot.builder.presentation.Defaults.DEF_LIVE_MAP_SIZE
import jetbrains.datalore.plot.builder.presentation.Defaults.DEF_PLOT_SIZE
import jetbrains.datalore.plot.builder.presentation.Defaults.MIN_PLOT_WIDTH
import jetbrains.datalore.plot.config.BunchConfig
import jetbrains.datalore.plot.config.Option
import jetbrains.datalore.plot.config.FigKind
import jetbrains.datalore.plot.config.OptionsAccessor
import jetbrains.datalore.plot.config.PlotConfig
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

object PlotSizeHelper {

    /**
     * Semi-open API.
     * Used in Lets-Plot-Kotlin, IDEA Plugin(?)
     */
    fun scaledFigureSize(
        figureSpec: Map<String, Any>,
        containerWidth: Int,
        containerHeight: Int
    ): Pair<Int, Int> {

        if (PlotConfig.isFailure(figureSpec)) {
            // just keep given size
            return Pair(containerWidth, containerHeight)
        }

        return when (val kind = PlotConfig.figSpecKind(figureSpec)) {
            FigKind.SUBPLOTS_SPEC -> UNSUPPORTED("NOT YET SUPPORTED: $kind")

            FigKind.GG_BUNCH_SPEC -> {
                // don't scale GGBunch size
                val bunchSize = plotBunchSize(figureSpec)
                Pair(ceil(bunchSize.x).toInt(), ceil(bunchSize.y).toInt())
            }

            FigKind.PLOT_SPEC -> {
                // for single plot: scale component to fit in requested size
                val aspectRatio = figureAspectRatio(figureSpec)
                if (aspectRatio >= 1.0) {
                    val plotHeight = containerWidth / aspectRatio
                    val scaling = if (plotHeight > containerHeight) containerHeight / plotHeight else 1.0
                    Pair(floor(containerWidth * scaling).toInt(), floor(plotHeight * scaling).toInt())
                } else {
                    val plotWidth = containerHeight * aspectRatio
                    val scaling = if (plotWidth > containerWidth) containerWidth / plotWidth else 1.0
                    Pair(floor(plotWidth * scaling).toInt(), floor(containerHeight * scaling).toInt())
                }
            }
        }
    }

    /**
     * Plot spec can be either raw or processed
     */
    fun singlePlotSize(
        plotSpec: Map<*, *>,
        plotSize: DoubleVector?,
        plotMaxWidth: Double?,
        plotPreferredWidth: Double?,
        facets: PlotFacets,
        containsLiveMap: Boolean
    ): DoubleVector {
        if (plotSize != null) {
            return plotSize
        }

        val defaultSize = getSizeOptionOrNull(plotSpec) ?: defaultSinglePlotSize(facets, containsLiveMap)
        val scaledSize = plotPreferredWidth?.let { w ->
            defaultSize.mul(max(MIN_PLOT_WIDTH, w) / defaultSize.x)
        } ?: defaultSize

        return if (plotMaxWidth != null && plotMaxWidth < scaledSize.x) {
            scaledSize.mul(max(MIN_PLOT_WIDTH, plotMaxWidth) / scaledSize.x)
        } else {
            scaledSize
        }
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
            singlePlotSize(
                bunchItem.featureSpec,
                null, null, null,
                PlotFacets.undefined(), false
            )
        }
    }

    private fun defaultSinglePlotSize(facets: PlotFacets, containsLiveMap: Boolean): DoubleVector {
        var plotSize = DEF_PLOT_SIZE
        if (facets.isDefined) {
            val panelWidth = DEF_PLOT_SIZE.x * (0.5 + 0.5 / facets.colCount)
            val panelHeight = DEF_PLOT_SIZE.y * (0.5 + 0.5 / facets.rowCount)
            plotSize = DoubleVector(panelWidth * facets.colCount, panelHeight * facets.rowCount)
        } else if (containsLiveMap) {
            plotSize = DEF_LIVE_MAP_SIZE
        }
        return plotSize
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

    /**
     * @param figureFpec Plot or plot bunch specification (can be 'raw' or processed).
     * @return Figure dimatsions width/height ratio.
     */
    fun figureAspectRatio(figureFpec: Map<*, *>): Double {
        return when (val kind = PlotConfig.figSpecKind(figureFpec)) {
            FigKind.PLOT_SPEC -> {
                // single plot
                getSizeOptionOrNull(figureFpec)?.let { it.x / it.y } ?: ASPECT_RATIO
            }

            FigKind.SUBPLOTS_SPEC -> UNSUPPORTED("NOT YET SUPPORTED: $kind")

            FigKind.GG_BUNCH_SPEC -> {
                // bunch
                @Suppress("UNCHECKED_CAST")
                val bunchSize = plotBunchSize(figureFpec as Map<String, Any>)
                bunchSize.x / bunchSize.y
            }
        }
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