/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.figure.composite

object GridScaleShareUtil {

    /**
     * Returns all sharing groups for a given policy.
     * Each group is a list of element indices that share an axis.
     */
    fun allGroups(
        sharePolicy: ScaleSharePolicy,
        elementCount: Int,
        ncols: Int
    ): List<List<Int>> {
        return when (sharePolicy) {
            ScaleSharePolicy.NONE -> listOf(emptyList())
            ScaleSharePolicy.ALL -> listOf(List(elementCount) { it })
            ScaleSharePolicy.ROW -> {
                val indexByRow = (0 until elementCount).map { indexToRow(it, ncols) to it }
                groupByFirst(indexByRow)
            }
            ScaleSharePolicy.COL -> {
                val indexByCol = (0 until elementCount).map { indexToCol(it, ncols) to it }
                groupByFirst(indexByCol)
            }
        }
    }

    /**
     * Returns the group of indices that share an axis with [sourceIndex].
     */
    fun groupOf(
        sourceIndex: Int,
        sharePolicy: ScaleSharePolicy,
        elementCount: Int,
        ncols: Int
    ): List<Int> {
        return when (sharePolicy) {
            ScaleSharePolicy.NONE -> emptyList()
            ScaleSharePolicy.ALL -> List(elementCount) { it }
            ScaleSharePolicy.ROW -> {
                val sourceRow = indexToRow(sourceIndex, ncols)
                (0 until elementCount).filter { indexToRow(it, ncols) == sourceRow }
            }
            ScaleSharePolicy.COL -> {
                val sourceCol = indexToCol(sourceIndex, ncols)
                (0 until elementCount).filter { indexToCol(it, ncols) == sourceCol }
            }
        }
    }

    private fun indexToRow(index: Int, ncol: Int) = index.floorDiv(ncol)
    private fun indexToCol(index: Int, ncol: Int) = index.mod(ncol)

    private fun groupByFirst(pairs: List<Pair<Int, Int>>): List<List<Int>> {
        val numGroups = pairs.distinctBy { it.first }.size
        val groupsList = List(numGroups) { ArrayList<Int>() }
        for ((group, value) in pairs) {
            groupsList[group].add(value)
        }
        return groupsList
    }
}
