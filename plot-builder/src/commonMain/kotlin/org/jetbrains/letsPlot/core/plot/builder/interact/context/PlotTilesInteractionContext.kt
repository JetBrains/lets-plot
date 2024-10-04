/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.context

import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.interact.*
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.core.plot.builder.PlotTile
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode

internal class PlotTilesInteractionContext(
    override val decorationsLayer: SvgNode,
    override val eventsManager: EventsManager,
    val tiles: List<Pair<DoubleRectangle, PlotTile>>,
    val dataSelectionStrategy: DataSelectionStrategy
) : InteractionContext {

    override fun findTarget(plotCoord: DoubleVector): InteractionTarget? {
        val target = tiles.find { (geomBounds, _) -> plotCoord in geomBounds } ?: return null
        val (geomBounds, tile) = target
        return object : InteractionTarget {
            override val geomBounds: DoubleRectangle = geomBounds

            override fun applyViewport(
                screenViewport: DoubleRectangle,
                ctx: InteractionContext
            ): Pair<DoubleRectangle, Boolean> {
                val (scale, translate) = InteractionUtil.viewportToTransform(geomBounds, screenViewport)
                tile.transientState.applyDelta(scale, translate, this@PlotTilesInteractionContext)
                return Pair(
                    tile.transientState.dataBounds,
                    tile.transientState.isCoordFlip,
                )
            }
        }
    }

    override fun clientRectToDataBounds(clientRect: DoubleRectangle, coord: CoordinateSystem): DoubleRectangle {
        return dataSelectionStrategy.clientRectToDataBounds(clientRect, coord)
    }

    override fun checkSupported(events: List<MouseEventSpec>) {
        events.firstOrNull { _ ->
            tiles.any { (_, tile) ->
                tile.liveMapFigure != null
            }
        }?.let { eventSpec ->
            throw UnsupportedInteractionException("$eventSpec denied by LiveMap component.")
        }
    }
}