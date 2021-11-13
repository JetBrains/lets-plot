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
import jetbrains.datalore.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.plot.builder.event.MouseEventPeer
import jetbrains.datalore.plot.builder.interact.tool.DragFeedback
import jetbrains.datalore.plot.builder.interact.tool.InteractionContext
import jetbrains.datalore.plot.builder.interact.tool.InteractionTarget
import jetbrains.datalore.plot.builder.interact.tool.ToolFeedback
import jetbrains.datalore.plot.builder.interact.ui.EventsManager
import jetbrains.datalore.plot.builder.theme.Theme
import jetbrains.datalore.vis.svg.SvgNode

internal class Interactor(
    val decorationLayer: SvgNode,
    mouseEventPeer: MouseEventPeer,
    val plotSize: DoubleVector,
    flippedAxis: Boolean,
    theme: Theme
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
            theme.axisX(flippedAxis),
            theme.axisY(flippedAxis),
            mouseEventPeer
        )
        reg.add(Registration.from(tooltipRenderer))
    }

    override fun onTileAdded(
        geomBounds: DoubleRectangle,
        tooltipBounds: PlotTooltipBounds,
        targetLocators: List<GeomTargetLocator>,
    ) {
        tooltipRenderer.addTileInfo(geomBounds, tooltipBounds, targetLocators)
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
