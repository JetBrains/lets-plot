/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.view

import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.canvas.CanvasControl
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.jetbrains.letsPlot.datamodel.svg.event.SvgAttributeEvent
import org.jetbrains.letsPlot.raster.shape.Element
import org.jetbrains.letsPlot.raster.shape.Pane
import org.jetbrains.letsPlot.raster.shape.reversedDepthFirstTraversal
import kotlin.math.ceil

abstract class SvgCanvasView() : Disposable {
    private var eventReg: Registration = Registration.EMPTY
    private var clickedElement: Element? = null

    var eventDispatcher: CanvasEventDispatcher? = null
        set(value) {
            eventReg.remove()
            if (value != null) {
                eventReg = value.addEventHandler(MouseEventSpec.MOUSE_CLICKED, object : EventHandler<MouseEvent> {
                    override fun onEvent(event: MouseEvent) {
                        reversedDepthFirstTraversal(rootElement)
                            .filterNot { it.isMouseTransparent }
                            .firstOrNull() { it.screenBounds.contains(DoubleVector(event.x, event.y)) }
                            ?.let {
                                clickedElement = if (clickedElement == it) null else it
                                needRedraw()
                            }
                    }
                })
            }
            field = value
        }

    //private val fontManager = FontManager()

    var svg: SvgSvgElement = SvgSvgElement()
        set(value) {
            //nodeContainer.root().set(value)
            val svgCanvasFigure = SvgCanvasFigure(value)
            svgCanvasFigure.mapToCanvas(canvasControl)
            rootElement = svgCanvasFigure.rootMapper.target

            width = value.width().get()?.let { ceil(it).toInt() } ?: 0
            height = value.height().get()?.let { ceil(it).toInt() } ?: 0

            updateCanvasSize(width, height)

            needRedraw()
        }

    private val nodeContainer = SvgNodeContainer(SvgSvgElement())  // attach root
    private var rootElement: Pane = Pane()
    private lateinit var _canvasControl: CanvasControl

    private var disposed = false

    val canvasControl: CanvasControl
        get() {
            check(!disposed) { "SvgSkikoView is disposed." }
            if (!this::_canvasControl.isInitialized) {
                _canvasControl = createCanvasControl(this)
            }
            return _canvasControl
        }

    var width: Int = 0
        private set
    var height: Int = 0
        private set

    init {
        nodeContainer.addListener(object : SvgNodeContainerListener {
            override fun onAttributeSet(element: SvgElement, event: SvgAttributeEvent<*>) = needRedraw()
            override fun onNodeAttached(node: SvgNode) = needRedraw()
            override fun onNodeDetached(node: SvgNode) = needRedraw()
        })
    }

    protected abstract fun createCanvasControl(view: SvgCanvasView): CanvasControl
    protected abstract fun updateCanvasSize(width: Int, height: Int)

    override fun dispose() {
        if (disposed) {
            return
        }

        disposed = true

        //fontManager.dispose()

        // Detach svg root.
        nodeContainer.root().set(SvgSvgElement())

        //if (this::_nativeLayer.isInitialized) {
        //    _nativeLayer.detach()
        //}
    }

    private fun needRedraw() {
        if (!disposed) {
            //skiaLayer.needRedraw()
        }
    }

    protected fun dispatchEvent(spec: MouseEventSpec, event: MouseEvent) {
        if (spec == MouseEventSpec.MOUSE_CLICKED) {
            reversedDepthFirstTraversal(rootElement)
                .filterNot { it.isMouseTransparent }
                .firstOrNull() { it.screenBounds.contains(DoubleVector(event.x, event.y)) }
                ?.let { it.href?.let(::onHrefClick) }
        }
        eventDispatcher?.dispatchMouseEvent(spec, event)
    }

    abstract fun onHrefClick(href: String)
}
