/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.layout.util.Insets
import jetbrains.datalore.plot.builder.theme.AxisTheme

internal interface AxisLayout {
    val orientation: Orientation
    val theme: AxisTheme

    fun doLayout(
        axisDomain: DoubleSpan,
        axisLength: Double,
        geomAreaInsets: Insets
    ): AxisLayoutInfo
}
