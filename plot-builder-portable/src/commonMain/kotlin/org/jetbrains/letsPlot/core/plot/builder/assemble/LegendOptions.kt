/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

import kotlin.math.max

class LegendOptions : GuideOptions() {

    private var myColCount: Int? = null
    private var myRowCount: Int? = null
    var isByRow: Boolean = false

    var colCount: Int
        get() = myColCount!!
        set(colCount) {
            myColCount = max(1, colCount)
        }

    var rowCount: Int
        get() = myRowCount!!
        set(rowCount) {
            myRowCount = max(1, rowCount)
        }

    fun hasColCount(): Boolean {
        return myColCount != null
    }

    fun hasRowCount(): Boolean {
        return myRowCount != null
    }

    companion object {
        fun combine(optionsList: List<LegendOptions>): LegendOptions {
            val result = LegendOptions()
            for (options in optionsList) {
                if (options.isByRow) {
                    result.isByRow = true
                }
                if (options.hasColCount()) {
                    result.colCount = options.colCount
                }
                if (options.hasRowCount()) {
                    result.rowCount = options.rowCount
                }
            }
            return result
        }
    }
}
