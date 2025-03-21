/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider

interface TileLayout {
    val insideOut: Boolean
    fun doLayout(preferredSize: DoubleVector, coordProvider: CoordProvider): TileLayoutInfo
}
