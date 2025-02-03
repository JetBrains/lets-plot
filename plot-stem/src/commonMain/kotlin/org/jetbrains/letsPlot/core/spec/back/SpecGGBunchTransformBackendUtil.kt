/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotFacets
import org.jetbrains.letsPlot.core.spec.FigKind
import org.jetbrains.letsPlot.core.spec.Option.Meta.KIND
import org.jetbrains.letsPlot.core.spec.Option.Meta.Kind.SUBPLOTS
import org.jetbrains.letsPlot.core.spec.Option.SubPlots
import org.jetbrains.letsPlot.core.spec.config.BunchConfig
import org.jetbrains.letsPlot.core.spec.config.PlotConfig
import org.jetbrains.letsPlot.core.util.PlotSizeHelper.singlePlotSizeDefault

/**
 * Transforms old 'GGBanch' specs to new 'ggbanch' composite figure specs
 * January 15, 2025 : remove all after a reasonable period of time.
 */
internal object SpecGGBunchTransformBackendUtil {
    fun ggbunchFromGGBunch(bunchSpecOld: MutableMap<String, Any>): MutableMap<String, Any> {
        // Make sure it's a bunch (old)
        check(PlotConfig.figSpecKind(bunchSpecOld) == FigKind.GG_BUNCH_SPEC) {
            "Plot Bunch is expected but was kind: ${PlotConfig.figSpecKind(bunchSpecOld)}"
        }

        // estimate bunch size
        val wasBunchSize = plotBunchSize(bunchSpecOld)

        // transform to new format
        val wasBunchConfig = BunchConfig(bunchSpecOld)
        val wasItemBoundsList = wasBunchConfig.bunchItems.map { item ->
            val size = bunchItemSize(item)
            DoubleRectangle.XYWH(
                item.x, item.y, size.x, size.y
            )
        }

        val scalerX = 1.0 / (if (wasBunchSize.x > 0) wasBunchSize.x else 1.0)
        val scalerY = 1.0 / (if (wasBunchSize.y > 0) wasBunchSize.y else 1.0)
        val relativeItemBoundsList = wasItemBoundsList.map {
            listOf(
                it.left * scalerX, it.top * scalerY, it.width * scalerX, it.height * scalerY
            )
        }

        val figureSpecs: List<Map<String, Any>> = wasBunchConfig.bunchItems.map {
            it.featureSpec
        }

        val bunchSpecNew = mapOf(
            KIND to SUBPLOTS, SubPlots.FIGURES to figureSpecs, SubPlots.LAYOUT to mapOf(
                SubPlots.Layout.NAME to SubPlots.Layout.SUBPLOTS_FREE, SubPlots.Free.REGIONS to relativeItemBoundsList
            )
        )
        return HashMap(bunchSpecNew)
    }


    private fun plotBunchSize(plotBunchFpec: Map<String, Any>): DoubleVector {
        require(PlotConfig.figSpecKind(plotBunchFpec) == FigKind.GG_BUNCH_SPEC) {
            "Plot Bunch is expected but was kind: ${PlotConfig.figSpecKind(plotBunchFpec)}"
        }
        return plotBunchSize(bunchItemBoundsList(plotBunchFpec))
    }

    private fun plotBunchSize(bunchItemBoundsIterable: Iterable<DoubleRectangle>): DoubleVector {
        return bunchItemBoundsIterable.fold(DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)) { acc, bounds ->
                acc.union(bounds)
            }.dimension
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
                    DoubleVector(bunchItem.x, bunchItem.y), bunchItemSize(bunchItem)
                )
            )
        }
        return plotBounds
    }

    private fun bunchItemSize(bunchItem: BunchConfig.BunchItem): DoubleVector {
        return if (bunchItem.hasSize()) {
            bunchItem.size
        } else {
            singlePlotSizeDefault(
                bunchItem.featureSpec, PlotFacets.UNDEFINED, false
            )
        }
    }
}