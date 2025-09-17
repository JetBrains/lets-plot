/*
 * Copyright (c) 2025 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.view

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.Rectangle
import org.jetbrains.letsPlot.commons.intern.observable.property.ReadableProperty
import org.jetbrains.letsPlot.commons.intern.observable.property.ValueProperty
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.canvas.*
import org.jetbrains.letsPlot.core.canvasFigure.CanvasFigure
import org.jetbrains.letsPlot.datamodel.mapping.framework.MappingContext
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.jetbrains.letsPlot.datamodel.svg.event.SvgAttributeEvent
import org.jetbrains.letsPlot.raster.mapping.svg.SvgCanvasPeer
import org.jetbrains.letsPlot.raster.mapping.svg.SvgSvgElementMapper
import org.jetbrains.letsPlot.raster.mapping.svg.TextMeasurer
import org.jetbrains.letsPlot.raster.shape.Container
import org.jetbrains.letsPlot.raster.shape.Element
import kotlin.math.ceil

class SvgCanvasFigure(svg: SvgSvgElement = SvgSvgElement()) : CanvasFigure {
    var svgSvgElement: SvgSvgElement = svg
        set(value) {
            field = value
            val width = value.width().get()
            val height = value.height().get()
            if (width != null && height != null) {
                svgBounds.set(Rectangle(0, 0, width.toInt(), height.toInt()))
            }
            needMapSvgSvgElement = true
            needResizeContentCanvas = true
        }

    private var needMapSvgSvgElement: Boolean = true // The whole svgSvgElement was replaced
    private var needRedraw: Boolean = true // Some svg elements may change their appearance (e.g., text) and require redraw.
    private var needResizeContentCanvas: Boolean = true // The size of svgSvgElement was changed, so we need to resize contentCanvas.

    private var nodeContainer: SvgNodeContainer? = null
    private var canvasPeer: SvgCanvasPeer? = null
    private var contentCanvas: Canvas? = null // Should have the same size as Figure
    private var canvasControl: CanvasControl? = null
    private var textMeasureCanvas: Canvas? = null

    internal lateinit var rootMapper: SvgSvgElementMapper // = SvgSvgElementMapper(svgSvgElement, canvasPeer)
    private val svgBounds = ValueProperty(Rectangle(0, 0, 0, 0))

    override fun bounds(): ReadableProperty<Rectangle> {
        return svgBounds
    }

    override fun mapToCanvas(canvasControl: CanvasControl): Registration {
        this.canvasControl = canvasControl

        textMeasureCanvas = canvasControl.createCanvas(0, 0)
        canvasControl.addChild(textMeasureCanvas ?: error("Should not happen - textMeasureCanvas is null"))

        val textMeasurer = TextMeasurer.create(textMeasureCanvas ?: error("Should not happen - textMeasureCanvas is null"))
        canvasPeer = SvgCanvasPeer(textMeasurer, canvasControl)

        // TODO: for native export. There is no timer to trigger redraw, draw explicitly on attach to canvas.
        onAnimationFrame()

        val anim = canvasControl.createAnimationTimer(AnimationProvider.AnimationEventHandler.toHandler { onAnimationFrame() })
        anim.start()

        return object : Registration() {
            override fun doRemove() {
                contentCanvas?.let(canvasControl::removeChild)
                textMeasureCanvas?.let(canvasControl::removeChild)
                rootMapper.detachRoot()
                anim.stop()
                this@SvgCanvasFigure.canvasControl = null
            }
        }
    }

    private fun mapSvgSvgElement() {
        val canvasPeer = canvasPeer ?: return // not yet attached

        nodeContainer = SvgNodeContainer(svgSvgElement)  // attach root
        nodeContainer!!.addListener(object : SvgNodeContainerListener {
            override fun onAttributeSet(element: SvgElement, event: SvgAttributeEvent<*>) = requestRedraw()
            override fun onNodeAttached(node: SvgNode) = requestRedraw()
            override fun onNodeDetached(node: SvgNode) = requestRedraw()
        })
        rootMapper = SvgSvgElementMapper(svgSvgElement, canvasPeer)
        rootMapper.attachRoot(MappingContext())

        needMapSvgSvgElement = false
    }

    private fun resizeContentCanvas() {
        //println("SvgCanvasFigure.resizeContentCanvas: width=$width, height=$height")
        val canvasControl = canvasControl ?: return

        contentCanvas?.let {
            canvasControl.removeChild(it)
        }

        val contentWidth = svgSvgElement.width().get()?.let { ceil(it).toInt() } ?: 0
        val contentHeight = svgSvgElement.height().get()?.let { ceil(it).toInt() } ?: 0

        val newContentCanvas = canvasControl.createCanvas(contentWidth, contentHeight)
        canvasControl.addChild(newContentCanvas)
        contentCanvas = newContentCanvas

        needResizeContentCanvas = false
        svgBounds.set(Rectangle(0, 0, contentWidth, contentHeight))

        //println("SvgCanvasFigure.resizeContentCanvas: done")
        return
    }

    private fun onAnimationFrame(): Boolean {
        if (needMapSvgSvgElement) {
            mapSvgSvgElement()
            needRedraw = true
        }

        if (needResizeContentCanvas) {
            resizeContentCanvas()
            needRedraw = true
        }

        if (!needRedraw) {
            return false
        }

        val canvas = contentCanvas ?: return false

        canvas.context2d.clearRect(DoubleRectangle.XYWH(0.0, 0.0, canvas.size.x, canvas.size.y))

        renderElement(rootMapper.target, canvas.context2d)

        needRedraw = false
        return true
    }

    private fun render(elements: List<Element>, ctx: Context2d) {
        elements.forEach { element ->
            renderElement(element, ctx)
        }
    }

    private fun renderElement(element: Element, ctx: Context2d) {
        if (!element.isVisible) {
            return
        }

        var needRestore = false
        if (!element.transform.isIdentity) {
            needRestore = true
            ctx.save()
            ctx.affineTransform(element.transform)
        }

        element.clipPath?.let { clipPath ->
            if (!needRestore) {
                ctx.save()
                needRestore = true
            }
            ctx.beginPath()
            ctx.applyPath(clipPath.getCommands())
            ctx.closePath()
            ctx.clip()
        }

        element.render(ctx)
        if (element is Container) {
            render(element.children, ctx)
        }

        if (needRestore) {
            ctx.restore()
        }
    }

    private fun requestRedraw() {
        needRedraw = true
    }
}
