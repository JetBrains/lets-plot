/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.assemble.PlotFacets
import jetbrains.datalore.plot.builder.presentation.Defaults.ASPECT_RATIO
import jetbrains.datalore.plot.builder.presentation.Defaults.DEF_LIVE_MAP_SIZE
import jetbrains.datalore.plot.builder.presentation.Defaults.DEF_PLOT_SIZE
import jetbrains.datalore.plot.builder.presentation.Defaults.MIN_PLOT_WIDTH
import jetbrains.datalore.plot.config.BunchConfig
import jetbrains.datalore.plot.config.Option
import jetbrains.datalore.plot.config.OptionsAccessor
import jetbrains.datalore.plot.config.PlotConfig
import kotlin.math.max

object PlotSizeHelper {
    /**
     * Plot spec can be either raw or processed
     */
    fun singlePlotSize(
        plotSpec: Map<*, *>,
        plotSize: DoubleVector?,
        plotMaxWidth: Double?,
        facets: PlotFacets,
        containsLiveMap: Boolean
    ): DoubleVector {
        return if (plotSize != null) {
            plotSize
        } else {
            val preferredSize = getSizeOptionOrNull(plotSpec) ?: defaultSinglePlotSize(facets, containsLiveMap)
            if (plotMaxWidth != null && plotMaxWidth < preferredSize.x) {
                preferredSize.mul(max(MIN_PLOT_WIDTH, plotMaxWidth) / preferredSize.x)
            } else {
                preferredSize
            }
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
                null, null,
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
        return when {
            PlotConfig.isPlotSpec(figureFpec) -> {
                // single plot
                getSizeOptionOrNull(figureFpec)?.let { it.x / it.y } ?: ASPECT_RATIO
            }
            PlotConfig.isGGBunchSpec(figureFpec) -> {
                // bunch
                @Suppress("UNCHECKED_CAST")
                val bunchSize = plotBunchSize(figureFpec as Map<String, Any>)
                bunchSize.x / bunchSize.y
            }
            else -> throw RuntimeException("Unexpected plot spec kind: " + PlotConfig.specKind(figureFpec))
        }
    }

    fun plotBunchSize(plotBunchFpec: Map<String, Any>): DoubleVector {
        require(PlotConfig.isGGBunchSpec(plotBunchFpec)) {
            "Plot Bunch is expected but was kind: ${PlotConfig.specKind(plotBunchFpec)}"
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

    fun fetchPlotSizeFromSvg(svg: String): DoubleVector {
        val svgTagMatch = Regex("<svg (.*)>").find(svg)
        require(svgTagMatch != null && svgTagMatch.groupValues.size == 2) {
            "Couldn't find 'svg' tag"
        }

        val svgTag = svgTagMatch.groupValues[1]

        val width = extractDouble(Regex(".*width=\"(\\d+)\\.?(\\d+)?\""), svgTag)
        val height = extractDouble(Regex(".*height=\"(\\d+)\\.?(\\d+)?\""), svgTag)
        return DoubleVector(width, height)
    }

    private fun extractDouble(regex: Regex, text: String): Double {
        val matchResult = regex.find(text)!!
        val values = matchResult.groupValues
        return if (values.size < 3)
            "${values[1]}".toDouble()
        else
            "${values[1]}.${values[2]}".toDouble()
    }
}