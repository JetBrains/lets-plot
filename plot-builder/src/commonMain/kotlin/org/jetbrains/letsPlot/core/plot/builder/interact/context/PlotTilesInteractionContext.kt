/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.context

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.interact.EventsManager
import org.jetbrains.letsPlot.core.interact.InteractionContext
import org.jetbrains.letsPlot.core.interact.InteractionTarget
import org.jetbrains.letsPlot.core.interact.InteractionUtil
import org.jetbrains.letsPlot.core.plot.builder.PlotTile
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode

internal abstract class PlotTilesInteractionContext (
    override val decorationsLayer: SvgNode,
    override val eventsManager: EventsManager,
    val tiles: List<Pair<DoubleRectangle, PlotTile>>,
) : InteractionContext {

    override fun findTarget(plotCoord: DoubleVector): InteractionTarget? {
        val target = tiles.find { (geomBounds, _) -> plotCoord in geomBounds } ?: return null
        val (geomBounds, tile) = target
        return object : InteractionTarget {
            override val geomBounds: DoubleRectangle = geomBounds

            override fun applyViewport(screenViewport: DoubleRectangle, ctx: InteractionContext): DoubleRectangle {
                val (scale, translate) = InteractionUtil.viewportToTransform(geomBounds, screenViewport)
                tile.transientState.applyDelta(scale, translate, this@PlotTilesInteractionContext)
                return tile.transientState.dataBounds
            }
        }
    }
}