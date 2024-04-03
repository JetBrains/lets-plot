/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

class LegendOptions constructor(
    val colCount: Int? = null,
    val rowCount: Int? = null,
    val byRow: Boolean = false,
    isReverse: Boolean = false
) : GuideOptions(isReverse) {
    init {
        require(colCount == null || colCount > 0) { "Invalid value: colCount=$colCount" }
        require(rowCount == null || rowCount > 0) { "Invalid value: colCount=$rowCount" }
    }

    fun hasColCount(): Boolean {
        return colCount != null
    }

    fun hasRowCount(): Boolean {
        return rowCount != null
    }

    override fun withReverse(reverse: Boolean): LegendOptions {
        return LegendOptions(
            colCount, rowCount, byRow, isReverse = reverse
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as LegendOptions

        if (colCount != other.colCount) return false
        if (rowCount != other.rowCount) return false
        if (byRow != other.byRow) return false

        return true
    }

    override fun hashCode(): Int {
        var result = colCount ?: 0
        result = 31 * result + (rowCount ?: 0)
        result = 31 * result + byRow.hashCode()
        return result
    }


    companion object {
        fun combine(optionsList: List<LegendOptions>): LegendOptions {
            var colCount: Int? = null
            var rowCount: Int? = null
            var byRow = false
            for (options in optionsList) {
                if (options.byRow) {
                    byRow = true
                }
                if (options.hasColCount()) {
                    colCount = options.colCount
                }
                if (options.hasRowCount()) {
                    rowCount = options.rowCount
                }
            }
            return LegendOptions(colCount, rowCount, byRow)
        }
    }
}
