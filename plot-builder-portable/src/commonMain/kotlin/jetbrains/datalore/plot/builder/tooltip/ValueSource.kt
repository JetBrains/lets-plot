/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.plot.base.interact.DataContext
import jetbrains.datalore.plot.base.interact.TooltipLineSpec.DataPoint

interface ValueSource {

    fun initDataContext(dataContext: DataContext)

    fun getDataPoint(index: Int): DataPoint?

    fun copy(): ValueSource

    fun isForGeneralTooltip(): Boolean
}