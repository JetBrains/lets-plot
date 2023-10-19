/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip

import org.jetbrains.letsPlot.core.plot.base.Aes

interface LineSpec {
    fun getDataPoint(index: Int, formatterProvider: FormatterProvider): DataPoint?

    class DataPoint(
        val label: String?,
        val value: String,
        val aes: Aes<*>?,
        val isAxis: Boolean,
        val isSide: Boolean
    )
}