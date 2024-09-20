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

internal class MouseDragInteractionContext constructor(
    decorationsLayer: SvgNode,
    eventsManager: EventsManager,
    tiles: List<Pair<DoubleRectangle, PlotTile>>,
) : PlotTilesInteractionContext(
    decorationsLayer,
    eventsManager,
    tiles
) {


    override fun clientRectToDataBounds(clientRect: DoubleRectangle, coord: CoordinateSystem): DoubleRectangle {
        val domainPoint0Estimate = coord.fromClient(clientRect.origin)
            ?: error("Can't translate client ${clientRect.origin} to data domain.")

        // inverse (in case the client rect origin was beyond the coord system valid bounds)
        val clientTopLeft = coord.toClient(domainPoint0Estimate)
            ?: clientRect.origin // this should not happen

        val clientBottomRight = clientTopLeft.add(clientRect.dimension)

        val domainPoint1 = coord.fromClient(clientBottomRight)
            ?: error("Can't translate client $clientBottomRight to data domain.")

        // inverse (in case the client rect bottom-right was beyond the coord system valid bounds)
        val clientBottomRightValid = coord.toClient(domainPoint1)
            ?: clientBottomRight // this should not happen

        // Re-evaluate the domain point "0".
        val domainPoint0 = clientBottomRightValid.subtract(clientRect.dimension).let {
            coord.fromClient(it)
                ?: error("Can't translate client $it to data domain.")
        }

        return DoubleRectangle.span(domainPoint0, domainPoint1)
    }
}