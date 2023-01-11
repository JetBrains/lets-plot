/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.layout.util.Insets

internal interface AxisLayout {
    val orientation: Orientation
    fun initialThickness(): Double

    fun doLayout(
        axisDomain: DoubleSpan,
        axisLength: Double,
        geomAreaInsets: Insets
    ): AxisLayoutInfo
}
