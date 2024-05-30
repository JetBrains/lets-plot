/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector

interface InteractionTarget {
    // viewport is in plot coordinates
    fun setViewport(viewportPlotRect: DoubleRectangle)

    fun toDataBounds(clientRect: DoubleRectangle): DoubleRectangle

    val geomSize: DoubleVector

    fun toGeomCoords(plotCoords: DoubleVector): DoubleVector
    fun toPlotCoords(geomCoords: DoubleVector): DoubleVector
}

fun InteractionTarget.toPlotCoords(geomRect: DoubleRectangle): DoubleRectangle {
    val plotOrigin = toPlotCoords(geomRect.origin)
    return DoubleRectangle(plotOrigin, geomRect.dimension)
}

fun InteractionTarget.toGeomCoords(geomRect: DoubleRectangle): DoubleRectangle {
    val geomOrigin = toGeomCoords(geomRect.origin)
    return DoubleRectangle(geomOrigin, geomRect.dimension)
}

val InteractionTarget.geomPlotRect: DoubleRectangle get() = toPlotCoords(DoubleRectangle(DoubleVector.ZERO, geomSize))
