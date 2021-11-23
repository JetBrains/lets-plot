/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.coord.CoordProvider

interface AxisLayout {
    fun initialThickness(): Double

    fun doLayout(
        displaySize: DoubleVector,
        maxTickLabelsBoundsStretched: DoubleRectangle?,
        coordProvider: CoordProvider
    ): AxisLayoutInfo
}
