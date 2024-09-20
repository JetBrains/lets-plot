/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.context

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.interact.EventsManager
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.core.plot.builder.PlotTile
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode

internal class MouseWheelInteractionContext constructor(
    decorationsLayer: SvgNode,
    eventsManager: EventsManager,
    tiles: List<Pair<DoubleRectangle, PlotTile>>,
) : PlotTilesInteractionContext(
    decorationsLayer,
    eventsManager,
    tiles
) {

    override fun clientRectToDataBounds(clientRect: DoubleRectangle, coord: CoordinateSystem): DoubleRectangle {
        val domainPoint0 = coord.fromClient(clientRect.origin)
            ?: error("Can't translate client ${clientRect.origin} to data domain.")
        val clientBottomRight = clientRect.origin.add(clientRect.dimension)
        val domainPoint1 = coord.fromClient(clientBottomRight)
            ?: error("Can't translate client $clientBottomRight to data domain.")
        return DoubleRectangle.span(domainPoint0, domainPoint1)
    }
}