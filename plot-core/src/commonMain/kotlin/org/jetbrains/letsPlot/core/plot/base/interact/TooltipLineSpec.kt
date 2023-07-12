/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.interact

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.PlotContext

interface TooltipLineSpec {
    fun getDataPoint(index: Int, ctx: PlotContext): DataPoint?

    class DataPoint(
        val label: String?,
        val value: String,
        val aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>?,
        val isAxis: Boolean,
        val isSide: Boolean
    )
}