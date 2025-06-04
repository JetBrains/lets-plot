/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.w3c.canvas

import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSource
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.SimpleAsync
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.AnimationProvider.AnimationEventHandler
import org.jetbrains.letsPlot.core.canvas.AnimationProvider.AnimationTimer
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.CanvasControl
import org.jetbrains.letsPlot.platf.w3c.canvas.DomCanvas.Companion.DEVICE_PIXEL_RATIO
import org.jetbrains.letsPlot.platf.w3c.dom.css.enumerables.CssPosition
import org.jetbrains.letsPlot.platf.w3c.dom.css.setPosition
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLElement
import org.w3c.dom.Image
import org.w3c.dom.events.Event
import org.w3c.dom.get
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag

class DomCanvasControl(
    private val myRootElement: HTMLElement,
    override val size: Vector,
    private val mouseEventSource: MouseEventSource,
    override val pixelDensity: Double = DEVICE_PIXEL_RATIO
) : CanvasControl {

    override fun createAnimationTimer(eventHandler: AnimationEventHandler): AnimationTimer {
        return object : DomAnimationTimer() {
            override fun handle(millisTime: Long) {
                eventHandler.onEvent(millisTime)
            }
        }
    }

    override fun addEventHandler(eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        return mouseEventSource.addEventHandler(eventSpec, eventHandler)
    }

    override fun createCanvas(size: Vector): Canvas {
        val domCanvas = DomCanvas.create(size, pixelDensity)
        domCanvas.canvasElement.style.setPosition(CssPosition.ABSOLUTE)
        return domCanvas
    }

    override fun createSnapshot(bitmap: Bitmap): Canvas.Snapshot {
        val domCanvas = DomCanvas.create(Vector(bitmap.width, bitmap.height), pixelDensity)
        val ctx = domCanvas.canvasElement.getContext("2d") as CanvasRenderingContext2D

        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                val color = bitmap.argbInts[y * bitmap.width + x]
                // Extract ARGB components
                val alpha = (color shr 24) and 0xFF
                val red = (color shr 16) and 0xFF
                val green = (color shr 8) and 0xFF
                val blue = color and 0xFF

                // Set the pixel color
                ctx.fillStyle = "rgba($red, $green, $blue, ${alpha / 255.0})"
                // Draw a rectangle for the pixel
                ctx.fillRect(x.toDouble(), y.toDouble(), 1.0, 1.0)
            }
        }

        // Draw the bitmap onto the canvas
        //ctx.drawImage(
        //    Image().apply {
        //        src = URL.createObjectURL(Blob(arrayOf(bitmap.argbInts), BlobPropertyBag("image/png")))
        //    },
        //    0.0,
        //    0.0,
        //    bitmap.width.toDouble(),
        //    bitmap.height.toDouble()
        //)

        return domCanvas.takeSnapshot()
    }

    override fun decodeDataImageUrl(dataUrl: String): Async<Canvas.Snapshot> {
        return decode(dataUrl, null)
    }

    override fun decodePng(png: ByteArray, size: Vector): Async<Canvas.Snapshot> {
        return Blob(arrayOf(png), BlobPropertyBag("image/png"))
            .let(URL.Companion::createObjectURL)
            .let { decode(it, size) }
    }

    private fun decode(dataUrl: String, size: Vector? = null): Async<Canvas.Snapshot> {
        return SimpleAsync<Canvas.Snapshot>().apply {
            with(Image()) {
                onload = onLoad(this, size, ::success)
                src = dataUrl
            }
        }
    }

    private fun onLoad(image: Image, size: Vector?, consumer: (Canvas.Snapshot) -> Unit) = { _: Event ->

        val domCanvas = size
            ?.let { createCanvas(it) as DomCanvas }
            ?: DomCanvas.create(Vector(image.width, image.height), 1.0)

        val ctx = domCanvas.canvasElement.getContext("2d") as CanvasRenderingContext2D

        ctx.drawImage(
            image,
            0.0,
            0.0,
            domCanvas.canvasElement.width.toDouble(),
            domCanvas.canvasElement.height.toDouble()
        )

        consumer(domCanvas.takeSnapshot())
    }

    override fun addChild(canvas: Canvas) {
        myRootElement.appendChild((canvas as DomCanvas).canvasElement)
    }

    override fun addChild(index: Int, canvas: Canvas) {
        myRootElement.insertBefore((canvas as DomCanvas).canvasElement, myRootElement.childNodes[index])
    }

    override fun removeChild(canvas: Canvas) {
        myRootElement.removeChild((canvas as DomCanvas).canvasElement)
    }

    override fun onResize(listener: (Vector) -> Unit): Registration {
        println("DomCanvasControl.onResize() - NOT IMPLEMENTED")
        return Registration.EMPTY
    }

    override fun snapshot(): Canvas.Snapshot {
        TODO("Not yet implemented")
    }

    override fun <T> schedule(f: () -> T) {
        f()
    }
}
