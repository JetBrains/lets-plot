/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder

import org.jetbrains.letsPlot.commons.event.MouseEventPeer
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.interact.*
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator
import org.jetbrains.letsPlot.core.plot.builder.tooltip.HorizontalAxisTooltipPosition
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TooltipRenderer
import org.jetbrains.letsPlot.core.plot.builder.tooltip.VerticalAxisTooltipPosition
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode

internal class PlotInteractor(
    val decorationLayer: SvgNode,
    mouseEventPeer: MouseEventPeer,
    val plotSize: DoubleVector,
    flippedAxis: Boolean,
    theme: Theme,
    plotContext: PlotContext
) : ToolInteractor, Disposable {
    val eventsManager: EventsManager = EventsManager()

    private val reg = CompositeRegistration()
    private val tooltipRenderer: TooltipRenderer

    private val tiles = ArrayList<Pair<DoubleRectangle, PlotTile>>()

    init {
        reg.add(Registration.from(eventsManager))
        eventsManager.setEventSource(mouseEventPeer)

        tooltipRenderer = TooltipRenderer(
            decorationLayer,
            flippedAxis,
            plotSize,
            theme.horizontalAxis(flippedAxis),
            theme.verticalAxis(flippedAxis),
            theme.tooltips(),
            theme.plot().backgroundFill(),
            plotContext,
            mouseEventPeer
        )
        reg.add(Registration.from(tooltipRenderer))
    }

    fun onTileAdded(
        plotTile: PlotTile,
        geomBounds: DoubleRectangle,
        targetLocators: List<GeomTargetLocator>,
        layerYOrientations: List<Boolean>,
        axisOrigin: DoubleVector,
        hAxisTooltipPosition: HorizontalAxisTooltipPosition,
        vAxisTooltipPosition: VerticalAxisTooltipPosition
    ) {
        tooltipRenderer.addTileInfo(
            geomBounds,
            targetLocators,
            layerYOrientations,
            axisOrigin,
            hAxisTooltipPosition,
            vAxisTooltipPosition
        )
        tiles.add(geomBounds to plotTile)
    }

    override fun startToolFeedback(toolFeedback: ToolFeedback): Registration {
        val disposable: Disposable = when (toolFeedback) {
            is DragFeedback -> toolFeedback.start(
                DragInteractionContext(
                    decorationLayer,
                    eventsManager,
                    tiles
                )
            )

            is WheelZoomFeedback -> toolFeedback.start(
                DragInteractionContext(
                    decorationLayer,
                    eventsManager,
                    tiles
                )
            )
            else -> throw IllegalArgumentException("Unknown tool feedback type: ${toolFeedback::class.simpleName}")
        }
        return Registration.from(disposable)
    }

    override fun reset() {
        tiles.forEach { (_, tile) -> tile.interactionSupport.reset() }
    }

    override fun dispose() {
        reg.dispose()
    }

    private class DragInteractionContext(
        override val decorationsLayer: SvgNode,
        override val eventsManager: EventsManager,
        val tiles: List<Pair<DoubleRectangle, PlotTile>>
    ) : InteractionContext {

        override fun findTarget(plotCoord: DoubleVector): InteractionTarget? {
            val target = tiles.find { (bbox, _) -> plotCoord in bbox } ?: return null
            val (bbox, tile) = target
            return object : InteractionTarget {
                override val geomSize: DoubleVector = bbox.dimension

                override fun toGeomCoords(plotCoords: DoubleVector): DoubleVector {
                    return plotCoords.subtract(bbox.origin)
                }

                override fun toPlotCoords(geomCoords: DoubleVector): DoubleVector {
                    return geomCoords.add(bbox.origin)
                }

                override fun setViewport(viewportPlotRect: DoubleRectangle) {
                    val viewportGeomRect = toGeomCoords(viewportPlotRect)
                        .shrinkToAspectRatio(bbox.dimension) // temp fix for aspect ratio on backends w/o plot rebuild
                    val (scale, translate) = calculateTransform(viewportGeomRect, geomSize)
                    tile.interactionSupport.update(scale, translate)
                }

                override fun toDataBounds(clientRect: DoubleRectangle): DoubleRectangle {
                    return tile.toDataBounds(clientRect)
                }

                // Viewport is zero-based
                private fun calculateTransform(viewport: DoubleRectangle, rectSize: DoubleVector): Pair<Double, DoubleVector> {
                    val scale = minOf(rectSize.x / viewport.width, rectSize.y / viewport.height)
                    val scaledSize = viewport.dimension.mul(scale)
                    val newPosition = rectSize.subtract(scaledSize).mul(0.5)
                    val translate = newPosition.subtract(viewport.origin)

                    return scale to translate
                }

            }
        }
    }
}
