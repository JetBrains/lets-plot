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
                override val geomBounds: DoubleRectangle = bbox

                override fun toGeomCoords(plotRect: DoubleRectangle): DoubleRectangle {
                    return plotRect.subtract(bbox.origin)
                }

                override fun setViewport(viewportPlotRect: DoubleRectangle) {
                    val (scale, translate) = viewportToTransform(viewportPlotRect, bbox)
                    tile.interactionSupport.updateTransform(scale, translate)
                }

                override fun getDataBounds(): DoubleRectangle {
                    val translate = tile.interactionSupport.pan.negate() // pan is a translation in the opposite direction
                    val geomBounds = toGeomCoords(bbox)
                    val viewport = transformToViewport(geomBounds, tile.interactionSupport.scale, translate)
                    return tile.toDataBounds(viewport)
                }

                override fun toDataBounds(clientRect: DoubleRectangle): DoubleRectangle {
                    return tile.toDataBounds(clientRect)
                }

                private fun viewportToTransform(viewport: DoubleRectangle, rect: DoubleRectangle): Pair<Double, DoubleVector> {
                    val scale = minOf(rect.width / viewport.width, rect.height / viewport.height)
                    val translate = rect.origin.subtract(viewport.origin)

                    return scale to translate
                }

                private fun transformToViewport(rect: DoubleRectangle, scaleFactor: Double, translate: DoubleVector): DoubleRectangle {
                    val newOrigin = rect.origin.add(translate)
                    val newDim = rect.dimension.mul(1 / scaleFactor)

                    return DoubleRectangle(newOrigin, newDim)
                }
            }
        }
    }
}
