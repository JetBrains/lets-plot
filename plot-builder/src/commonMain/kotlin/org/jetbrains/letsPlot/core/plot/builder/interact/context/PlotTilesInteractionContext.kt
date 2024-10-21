/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.context

import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.interact.EventsManager
import org.jetbrains.letsPlot.core.interact.InteractionContext
import org.jetbrains.letsPlot.core.interact.InteractionTarget
import org.jetbrains.letsPlot.core.interact.InteractionUtil
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.core.plot.builder.PlotTile
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode

internal class PlotTilesInteractionContext(
    override val decorationsLayer: SvgNode,
    override val eventsManager: EventsManager,
    val tiles: List<Pair<DoubleRectangle, PlotTile>>,
    private val dataSelectionStrategy: DataSelectionStrategy
) : InteractionContext {

    override fun findTarget(plotCoord: DoubleVector): InteractionTarget? {
        val target = tiles.find { (geomBounds, _) -> plotCoord in geomBounds } ?: return null
        val (geomBounds, tile) = target
        return object : InteractionTarget {
            override val geomBounds: DoubleRectangle = geomBounds
            override val id: String? = tile.plotSpecId

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

    /**
     * Throws UnsupportedInteractionException if not supported
     */
    override fun checkSupported(eventSpecs: List<MouseEventSpec>) {
        for (eventSpec in eventSpecs) {
            for ((_, tile) in tiles) {
                tile.checkMouseInteractionSupported(eventSpec)
            }
        }
    }
}