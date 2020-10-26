/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleVector

interface PlotLayout {
    fun doLayout(preferredSize: DoubleVector): PlotLayoutInfo

    fun setPadding(top: Double, right: Double, bottom: Double, left: Double)
}
