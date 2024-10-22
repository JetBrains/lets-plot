/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact

import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode

interface InteractionContext {
    val decorationsLayer: SvgNode
    val eventsManager: EventsManager

    fun findTarget(plotCoord: DoubleVector): InteractionTarget?

    fun clientRectToDataBounds(clientRect: DoubleRectangle, coord: CoordinateSystem): DoubleRectangle

    fun checkSupported(eventSpecs: List<MouseEventSpec>)
}