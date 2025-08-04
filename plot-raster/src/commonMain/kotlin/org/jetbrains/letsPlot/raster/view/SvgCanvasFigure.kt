/*
 * Copyright (c) 2025 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.view

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
import org.jetbrains.letsPlot.raster.shape.Container
import org.jetbrains.letsPlot.raster.shape.Element
import kotlin.math.ceil

class SvgCanvasFigure(svg: SvgSvgElement = SvgSvgElement()) : CanvasFigure {
    var svgSvgElement: SvgSvgElement = svg
        set(value) {
            field = value
            mapSvgSvgElement()
            val contentWidth = value.width().get()?.let { ceil(it).toInt() } ?: 0
            val contentHeight = value.height().get()?.let { ceil(it).toInt() } ?: 0
            svgBounds.set(Rectangle(0, 0, contentWidth, contentHeight))

            requestRedraw()
        }

    private var nodeContainer: SvgNodeContainer? = null
    private var canvasPeer: SvgCanvasPeer? = null

    internal lateinit var rootMapper: SvgSvgElementMapper // = SvgSvgElementMapper(svgSvgElement, canvasPeer)
    private val svgBounds = ValueProperty(Rectangle(0, 0, 0, 0))
    private val repaintRequestListeners = mutableListOf<() -> Unit>()

    override fun bounds(): ReadableProperty<Rectangle> {
        return svgBounds
    }

    override fun mapToCanvas(canvasControl: CanvasControl): Registration {
        canvasPeer = SvgCanvasPeer(canvasControl)

        return object : Registration() {
            override fun doRemove() {
                rootMapper.detachRoot()
            }
        }
    }

    override fun mapToCanvas(canvasProvider: CanvasProvider): Registration {
        canvasPeer = SvgCanvasPeer(canvasProvider)

        return object : Registration() {
            override fun doRemove() {
                rootMapper.detachRoot()
            }
        }
    }

    override fun draw(context2d: Context2d) {
        renderElement(rootMapper.target, context2d)
    }

    override fun onRepaintRequest(handler: () -> Unit): Registration {
        repaintRequestListeners.add(handler)
        return Registration.onRemove {
            repaintRequestListeners.remove(handler)
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
        repaintRequestListeners.forEach { it() }
    }
}
