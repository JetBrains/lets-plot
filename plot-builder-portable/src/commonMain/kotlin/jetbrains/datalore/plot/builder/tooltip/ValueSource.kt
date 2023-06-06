/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.PlotContext
import jetbrains.datalore.plot.base.interact.MappedDataAccess
import jetbrains.datalore.plot.base.interact.TooltipLineSpec.DataPoint

interface ValueSource {

    val isSide: Boolean

    val isAxis: Boolean

    fun initDataContext(data: DataFrame, mappedDataAccess: MappedDataAccess)

    fun getDataPoint(index: Int, ctx: PlotContext): DataPoint?

    fun copy(): ValueSource

    fun getAnnotationText(index: Int): String?
}