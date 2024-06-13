/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

import org.jetbrains.letsPlot.core.plot.base.Aes

class LegendOptions constructor(
    val colCount: Int? = null,
    val rowCount: Int? = null,
    val byRow: Boolean = false,
    title: String? = null,
    val overrideAesValues: Map<Aes<*>, Any>? = null,
    isReverse: Boolean = false
) : GuideOptions(title, isReverse) {
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
            colCount, rowCount, byRow, title, overrideAesValues, isReverse = reverse
        )
    }

    override fun withTitle(title: String?): LegendOptions {
        return LegendOptions(
            colCount, rowCount, byRow, title = title, isReverse
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as LegendOptions

        if (colCount != other.colCount) return false
        if (rowCount != other.rowCount) return false
        if (byRow != other.byRow) return false
        if (title != other.title) return false
        if (overrideAesValues != other.overrideAesValues) return false

        return true
    }

    override fun hashCode(): Int {
        var result = colCount ?: 0
        result = 31 * result + (rowCount ?: 0)
        result = 31 * result + byRow.hashCode()
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (overrideAesValues?.hashCode() ?: 0)
        return result
    }


    companion object {
        fun combine(optionsList: List<LegendOptions>): LegendOptions {
            var colCount: Int? = null
            var rowCount: Int? = null
            var byRow = false
            var title: String? = null
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
                if (options.title != null) {
                    title = options.title
                }
            }
            return LegendOptions(colCount, rowCount, byRow, title)
        }
    }
}
