/*
 * Copyright (c) 2025 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.view

import org.jetbrains.letsPlot.commons.event.MouseEventPeer
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvas.affineTransform
import org.jetbrains.letsPlot.core.canvas.applyPath
import org.jetbrains.letsPlot.core.canvasFigure.CanvasFigure2
import org.jetbrains.letsPlot.datamodel.mapping.framework.MappingContext
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.jetbrains.letsPlot.datamodel.svg.event.SvgAttributeEvent
import org.jetbrains.letsPlot.raster.mapping.svg.SvgCanvasPeer
import org.jetbrains.letsPlot.raster.mapping.svg.SvgSvgElementMapper
import org.jetbrains.letsPlot.raster.shape.Container
import org.jetbrains.letsPlot.raster.shape.Element
import kotlin.math.ceil

class SvgCanvasFigure2(svg: SvgSvgElement = SvgSvgElement()) : CanvasFigure2 {
    override val size: Vector get() {
        val contentWidth = svgSvgElement.width().get()?.let { ceil(it).toInt() } ?: 0
        val contentHeight = svgSvgElement.height().get()?.let { ceil(it).toInt() } ?: 0
        return Vector(contentWidth, contentHeight)
    }

    var svgSvgElement: SvgSvgElement = svg
        set(value) {
            field = value
            mapSvgSvgElement()
            requestRedraw()
        }

    private var nodeContainer: SvgNodeContainer? = null
    private var svgCanvasPeer: SvgCanvasPeer? = null

    internal lateinit var rootMapper: SvgSvgElementMapper // = SvgSvgElementMapper(svgSvgElement, canvasPeer)
    private val repaintRequestListeners = mutableListOf<() -> Unit>()

    override fun mapToCanvas(canvasPeer: CanvasPeer): Registration {
        svgCanvasPeer = SvgCanvasPeer(canvasPeer)

        mapSvgSvgElement()
        return object : Registration() {
            override fun doRemove() {
                rootMapper.detachRoot()
                svgCanvasPeer = null
            }
        }
    }

    override val eventPeer: MouseEventPeer = MouseEventPeer()

    override fun paint(context2d: Context2d) {
        renderElement(rootMapper.target, context2d)
    }

    private fun mapSvgSvgElement() {
        val canvasPeer = svgCanvasPeer ?: return // not yet attached

        nodeContainer = SvgNodeContainer(svgSvgElement)  // attach root
        nodeContainer!!.addListener(object : SvgNodeContainerListener {
            override fun onAttributeSet(element: SvgElement, event: SvgAttributeEvent<*>) = requestRedraw()
            override fun onNodeAttached(node: SvgNode) = requestRedraw()
            override fun onNodeDetached(node: SvgNode) = requestRedraw()
        })
        rootMapper = SvgSvgElementMapper(svgSvgElement, canvasPeer)
        rootMapper.attachRoot(MappingContext())
    }

    override fun onRepaintRequested(listener: () -> Unit): Registration {
        repaintRequestListeners.add(listener)
        return Registration.onRemove {
            repaintRequestListeners.remove(listener)
        }
    }

    override fun resize(width: Number, height: Number) {
        // nothing to do - size is defined by svgSvgElement width/height attributes
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

            // Make sure graphical objects will belong to the different save/restore block
            // to avoid perf issues in ImageMagick
            ctx.save()
        }

        element.render(ctx)
        if (element is Container) {
            render(element.children, ctx)
        }

        if (element.clipPath != null) {
            // Restore clip path save
            ctx.restore()
        }

        if (needRestore) {
            ctx.restore()
        }
    }

    private fun requestRedraw() {
        repaintRequestListeners.forEach { it() }
    }
}
