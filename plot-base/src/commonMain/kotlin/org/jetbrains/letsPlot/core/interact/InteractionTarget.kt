/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle

interface InteractionTarget {
    // viewport is in plot coordinates
    fun setViewport(viewportPlotRect: DoubleRectangle)

    fun toDataBounds(clientRect: DoubleRectangle): DoubleRectangle
    fun getDataBounds(): DoubleRectangle

    val geomBounds: DoubleRectangle

    fun toGeomCoords(plotRect: DoubleRectangle): DoubleRectangle
}


