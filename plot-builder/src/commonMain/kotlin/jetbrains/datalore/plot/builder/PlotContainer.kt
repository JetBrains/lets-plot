/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec.*
import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.property.ReadableProperty
import jetbrains.datalore.plot.builder.interact.render.TooltipLayer
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.vis.svg.SvgGElement
import jetbrains.datalore.vis.svg.SvgRectElement

class PlotContainer(
    plot: Plot,
    preferredSize: ReadableProperty<DoubleVector>
) : PlotContainerPortable(plot, preferredSize) {

    private val myDecorationLayer = SvgGElement()
    private val myMouseMoveRect = SvgRectElement()

    val mouseEventPeer: jetbrains.datalore.plot.builder.event.MouseEventPeer
        get() = plot.mouseEventPeer

    init {
        // this rect blocks mouse_left events while cursor moves above svg tree elements (in GWT only)
        myMouseMoveRect.addClass(Style.PLOT_GLASS_PANE)
        myMouseMoveRect.opacity().set(0.0)
    }

    protected override fun buildContent() {
        super.buildContent()
        if (plot.isInteractionsEnabled) {
            svg.children().add(myDecorationLayer)
            svg.children().add(myMouseMoveRect)
            hookupInteractions()
        }
    }


    override fun clearContent() {
        myDecorationLayer.children().clear()
        super.clearContent()
    }

    private fun hookupInteractions() {
        checkState(plot.isInteractionsEnabled)

        val viewport = DoubleRectangle(DoubleVector.ZERO, plot.laidOutSize().get())
        val tooltipLayer = TooltipLayer(myDecorationLayer, viewport)

        val onMouseMoved = { e: MouseEvent ->
            val coord = DoubleVector(e.x.toDouble(), e.y.toDouble())
            val tooltipSpecs = plot.createTooltipSpecs(coord)
            tooltipLayer.showTooltips(coord, tooltipSpecs)
        }
        reg(plot.mouseEventPeer.addEventHandler(MOUSE_MOVED, object : EventHandler<MouseEvent> {
            override fun onEvent(event: MouseEvent) {
                onMouseMoved(event)
            }
        }))
        reg(plot.mouseEventPeer.addEventHandler(MOUSE_DRAGGED, object : EventHandler<MouseEvent> {
            override fun onEvent(event: MouseEvent) {
                tooltipLayer.hideTooltip()
            }
        }))
        reg(plot.mouseEventPeer.addEventHandler(MOUSE_LEFT, object : EventHandler<MouseEvent> {
            override fun onEvent(event: MouseEvent) {
                tooltipLayer.hideTooltip()
            }
        }))
    }
}
