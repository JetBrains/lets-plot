/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.plot.base.PlotContext
import jetbrains.datalore.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.plot.builder.event.MouseEventPeer
import jetbrains.datalore.plot.builder.interact.tool.DragFeedback
import jetbrains.datalore.plot.builder.interact.tool.InteractionContext
import jetbrains.datalore.plot.builder.interact.tool.InteractionTarget
import jetbrains.datalore.plot.builder.interact.tool.ToolFeedback
import jetbrains.datalore.plot.builder.interact.ui.EventsManager
import jetbrains.datalore.plot.builder.theme.Theme
import jetbrains.datalore.plot.builder.tooltip.HorizontalAxisTooltipPosition
import jetbrains.datalore.plot.builder.tooltip.VerticalAxisTooltipPosition
import jetbrains.datalore.vis.svg.SvgNode

internal class Interactor constructor(
    val decorationLayer: SvgNode,
    mouseEventPeer: MouseEventPeer,
    val plotSize: DoubleVector,
    flippedAxis: Boolean,
    theme: Theme,
    plotContext: PlotContext
) : PlotInteractor {
    val eventsManager: EventsManager = EventsManager()

    private val reg = CompositeRegistration()
    private val tooltipRenderer: TooltipRenderer

    private val geomBoundsList = ArrayList<DoubleRectangle>()

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

    override fun onTileAdded(
        geomBounds: DoubleRectangle,
        targetLocators: List<GeomTargetLocator>,
        layerYOrientations: List<Boolean>,
        axisOrigin: DoubleVector,
        hAxisTooltipPosition: HorizontalAxisTooltipPosition,
        vAxisTooltipPosition: VerticalAxisTooltipPosition
    ) {
        tooltipRenderer.addTileInfo(geomBounds, targetLocators, layerYOrientations, axisOrigin, hAxisTooltipPosition, vAxisTooltipPosition)
        geomBoundsList.add(geomBounds)
    }

    override fun startToolFeedback(toolFeedback: ToolFeedback): Registration {
        val disposable: Disposable = when (toolFeedback) {
            is DragFeedback -> toolFeedback.start(
                DragInteractionContext(
                    decorationLayer,
                    eventsManager,
                    geomBoundsList
                )
            )

            else -> throw IllegalArgumentException("Unknown tool feedback type: ${toolFeedback::class.simpleName}")
        }
        return Registration.from(disposable)
    }


    override fun dispose() {
        reg.dispose()
    }

    private class DragInteractionContext(
        override val decorationsLayer: SvgNode,
        override val eventsManager: EventsManager,
        val geomBoundsList: List<DoubleRectangle>
    ) : InteractionContext {

        override fun findTarget(plotCoord: DoubleVector): InteractionTarget? {
            val geomBounds = geomBoundsList.find { it.contains(plotCoord) }
            return geomBounds?.let {
                object : InteractionTarget {
                    override val geomBounds: DoubleRectangle
                        get() = geomBounds

                    override fun zoom(geomBounds: DoubleRectangle) {
                        println("Target zoom: $geomBounds")
                    }
                }
            }
        }
    }
}
