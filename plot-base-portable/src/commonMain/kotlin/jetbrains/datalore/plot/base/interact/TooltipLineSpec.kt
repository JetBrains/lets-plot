/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.interact

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.PlotContext

interface TooltipLineSpec {
    fun getDataPoint(index: Int, ctx: PlotContext): DataPoint?

    class DataPoint(
        val label: String?,
        val value: String,
        val aes: Aes<*>?,
        val isAxis: Boolean,
        val isOutlier: Boolean
    )
}