/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.figure.composite

import jetbrains.datalore.plot.builder.FigureBuildInfo

internal object FigureGridLayoutUtil {
    fun indexToRow(index: Int, ncol: Int) = index.floorDiv(ncol)
    fun indexToCol(index: Int, ncol: Int) = index.mod(ncol)

    fun rowElements(row: Int, figs: List<FigureBuildInfo?>, ncol: Int): List<FigureBuildInfo?> {
        return figs.filterIndexed { index, _ ->
            row == indexToRow(index, ncol)
        }
    }

    fun colElements(col: Int, figs: List<FigureBuildInfo?>, ncol: Int): List<FigureBuildInfo?> {
        return figs.filterIndexed { index, _ ->
            col == indexToCol(index, ncol)
        }
    }
}