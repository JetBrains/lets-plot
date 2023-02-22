/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.figure.composite

import jetbrains.datalore.plot.builder.FigureBuildInfo

internal object FigureGridLayoutUtil {
    fun indexToRow(index: Int, ncol: Int) = index.floorDiv(ncol)
    fun indexToCol(index: Int, ncol: Int) = index.mod(ncol)

    fun rowElements(
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

    fun colElements(
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