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

    override fun createSnapshot(dataUrl: String): Async<Canvas.Snapshot> {
        return createSnapshotAsync(dataUrl, null)
    }

    override fun createSnapshot(bytes: ByteArray, size: Vector): Async<Canvas.Snapshot> {
        return Blob(arrayOf(bytes), BlobPropertyBag("image/png"))
            .let(URL.Companion::createObjectURL)
            .let { createSnapshotAsync(it, size) }
    }

    private fun createSnapshotAsync(dataUrl: String, size: Vector? = null): Async<Canvas.Snapshot> {
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

        domCanvas.takeSnapshot().onSuccess(consumer)
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

    override fun <T> schedule(f: () -> T) {
        f()
    }
}
