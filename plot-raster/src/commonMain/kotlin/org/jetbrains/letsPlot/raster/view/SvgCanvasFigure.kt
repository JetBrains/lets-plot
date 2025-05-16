/*
 * Copyright (c) 2025 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.view

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.Rectangle
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.observable.property.ReadableProperty
import org.jetbrains.letsPlot.commons.intern.observable.property.ValueProperty
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.canvas.*
import org.jetbrains.letsPlot.core.canvasFigure.CanvasFigure
import org.jetbrains.letsPlot.datamodel.mapping.framework.MappingContext
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNodeContainer
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.raster.mapping.svg.SvgCanvasPeer
import org.jetbrains.letsPlot.raster.mapping.svg.SvgSvgElementMapper
import org.jetbrains.letsPlot.raster.mapping.svg.TextMeasurer
import org.jetbrains.letsPlot.raster.shape.Container
import org.jetbrains.letsPlot.raster.shape.Element
import org.jetbrains.letsPlot.raster.shape.Pane
import kotlin.math.ceil

class SvgCanvasFigure(
    val svgSvgElement: SvgSvgElement
) : CanvasFigure {
    internal lateinit var rootMapper: SvgSvgElementMapper // = SvgSvgElementMapper(svgSvgElement, canvasPeer)
    private val rootElement: Pane get() = rootMapper.target
    private lateinit var canvasControl: CanvasControl

    val width = svgSvgElement.width().get()?.let { ceil(it).toInt() } ?: 0
    val height = svgSvgElement.height().get()?.let { ceil(it).toInt() } ?: 0
    private val myBounds = ValueProperty(Rectangle(0, 0, width, height))

    override fun bounds(): ReadableProperty<Rectangle> {
        return myBounds
    }

    override fun mapToCanvas(canvasControl: CanvasControl): Registration {
        this.canvasControl = canvasControl
        val canvasPeer = SvgCanvasPeer(
            textMeasurer = TextMeasurer.create(canvasControl)
        )

        rootMapper = SvgSvgElementMapper(svgSvgElement, canvasPeer)
        @Suppress("unused")
        val nodeContainer = SvgNodeContainer(svgSvgElement)  // attach root
        rootMapper.attachRoot(MappingContext())

        val canvas = canvasControl.createCanvas(Vector(width, height))
        val anim = canvasControl.createAnimationTimer(object : AnimationProvider.AnimationEventHandler {
            override fun onEvent(millisTime: Long): Boolean {
                canvas.context2d.clearRect(DoubleRectangle(0.0, 0.0, width.toDouble(), height.toDouble()))
                render(rootElement, canvas)
                return true
            }
        })

        canvasControl.addChild(canvas)

        anim.start()

        return Registration.Companion.EMPTY
    }

    private fun render(elements: List<Element>, canvas: Canvas) {
        elements.forEach { element ->
            render(element, canvas)
        }
    }

    private fun render(element: Element, canvas: Canvas) {
        if (!element.isVisible) {
            return
        }

        val ctx = canvas.context2d

        var needRestore = false
        if (!element.transform.isIdentity) {
            if (!needRestore) {
                ctx.save()
                needRestore = true
            }
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
            canvas.context2d.clip()
        }

        //val globalAlphaSet = element.opacity?.let {
        //    val paint = Paint().apply {
        //        setAlphaf(it)
        //    }
        //    ctx.saveLayer(null, paint)
        //}

        element.render(canvas)
        if (element is Container) {
            render(element.children, canvas)
        }

        //globalAlphaSet?.let { canvas.restore() }

        if (needRestore) {
            ctx.restore()
        }
    }

    @Suppress("unused")
    fun makeSnapshot(): Canvas.Snapshot {
        val canvas = canvasControl.createCanvas(Vector(width, height))
        canvas.context2d.clearRect(DoubleRectangle(0.0, 0.0, width.toDouble(), height.toDouble()))
        render(rootElement, canvas)
        return canvas.immidiateSnapshot()
    }

}