/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle

interface AxisLayout {
    fun initialThickness(): Double

    fun doLayout(
        axisDomain: ClosedRange<Double>,
        axisLength: Double,
        maxTickLabelsBoundsStretched: DoubleRectangle?
    ): AxisLayoutInfo
}
