/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.figure.composite

import org.jetbrains.letsPlot.core.plot.builder.buildinfo.FigureBuildInfo
import org.jetbrains.letsPlot.core.plot.builder.buildinfo.PlotFigureBuildInfo

internal object FigureGridLayoutUtil {
    fun indexToRow(index: Int, ncol: Int) = index.floorDiv(ncol)
    fun indexToCol(index: Int, ncol: Int) = index.mod(ncol)

    fun rowPlotElements(
        row: Int,
        figs: List<FigureBuildInfo?>,
        ncol: Int,
    ): List<PlotFigureBuildInfo?> {
        @Suppress("UNCHECKED_CAST")
        return rowElements(row, figs, ncol, inclideComposite = false) as List<PlotFigureBuildInfo?>
    }

    private fun rowElements(
        row: Int,
        figs: List<FigureBuildInfo?>,
        ncol: Int,
        inclideComposite: Boolean
    ): List<FigureBuildInfo?> {
        val figList = figs.filterIndexed { index, _ ->
            row == indexToRow(index, ncol)
        }
        return if (inclideComposite) {
            figList
        } else {
            nullifyComposites(figList)
        }
    }

    fun colPlotElements(
        col: Int,
        figs: List<FigureBuildInfo?>,
        ncol: Int,
    ): List<PlotFigureBuildInfo?> {
        @Suppress("UNCHECKED_CAST")
        return colElements(col, figs, ncol, inclideComposite = false) as List<PlotFigureBuildInfo?>
    }

    private fun colElements(
        col: Int,
        figs: List<FigureBuildInfo?>,
        ncol: Int,
        inclideComposite: Boolean
    ): List<FigureBuildInfo?> {
        val figList = figs.filterIndexed { index, _ ->
            col == indexToCol(index, ncol)
        }
        return if (inclideComposite) {
            figList
        } else {
            nullifyComposites(figList)
        }
    }

    private fun nullifyComposites(figures: List<FigureBuildInfo?>): List<FigureBuildInfo?> {
        return figures.map {
            // Exclude composite figures
            when (it?.isComposite) {
                false -> it
                else -> null
            }
        }
    }

    fun toCellOrigin(index: Int, sizes: List<Double>, space: Double): Double {
        return sizes.take(index).sum() + space * index
    }
}