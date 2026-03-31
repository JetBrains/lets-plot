/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.text

import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.PlotContext

interface ValueSource {

    val isSide: Boolean

    val isAxis: Boolean

    fun initDataContext(data: DataFrame, mappedDataAccess: MappedDataAccess)

    fun getDataPoint(index: Int, ctx: PlotContext): LineSpec.DataPoint?

    fun copy(): ValueSource
}