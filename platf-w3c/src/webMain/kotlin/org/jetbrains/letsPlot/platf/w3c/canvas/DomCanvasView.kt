/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(ExperimentalWasmJsInterop::class)

package org.jetbrains.letsPlot.platf.w3c.canvas

import kotlinx.browser.window
import org.jetbrains.letsPlot.commons.SystemTime
import org.jetbrains.letsPlot.commons.event.MouseEventSource
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.logging.identityString
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.canvas.CanvasDrawable
import org.jetbrains.letsPlot.core.platf.dom.DomMouseEventMapper
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.Node
import kotlin.js.ExperimentalWasmJsInterop

class DomCanvasView(
    content: CanvasDrawable? = null
) : Disposable {

    fun attachTo(parent: Node) {
        parent.appendChild(domCanvasElement)
    }

    val domCanvasElement: HTMLCanvasElement =
        DomCanvas.createNativeCanvas(Vector(1, 1), DomCanvas.DEVICE_PIXEL_RATIO).apply {
            style.display = "block"
        }

    private val context2d = DomContext2d(
        domCanvasElement.getContext("2d") as CanvasRenderingContext2D,
        DomCanvas.DEVICE_PIXEL_RATIO
    )

    private val systemTime = SystemTime()
    private val animationTimer = object : DomAnimationTimer() {
        override fun handle(millisTime: Long) {
            this@DomCanvasView.content?.onFrame(systemTime.getTimeMs())
        }
    }

    private val mouseEventSource: MouseEventSource = DomMouseEventMapper(domCanvasElement)

    internal var canvasPeer: DomCanvasPeer = DomCanvasPeer()
        set(value) {
            check(!isContentAttached) { "Can't change canvasPeer after figure is attached" }
            field = value
        }

    private var isContentAttached = false
    private var contentReg: Registration = Registration.EMPTY
    private var disposables = CompositeRegistration()
    private var repaintRequestHandle: Int? = null

    var width: Int = 1
        private set

    var height: Int = 1
        private set

    var content: CanvasDrawable? = null
        set(value) {
            if (field == value) return

            contentReg.dispose()

            if (value == null) {
                contentReg = Registration.EMPTY
            } else {
                isContentAttached = true
                animationTimer.start()
                value.resize(width, height)

                contentReg = CompositeRegistration(
                    value.mouseEventPeer.addEventSource(mouseEventSource),
                    value.mapToCanvas(canvasPeer),
                    value.onRepaintRequested {
                        requestRepaint()
                    },
                    Registration.onRemove(animationTimer::stop),
                    Registration.onRemove { log { "Content removed: ${value.identityString}" } }
                )
            }

            field = value
            log { "Content set: ${value?.identityString ?: "null"}" }
            requestRepaint()
        }

    init {
        this.content = content
    }

    fun setSize(width: Int, height: Int) {
        val sizeChanged = this.width != width || this.height != height
        if (!sizeChanged) return

        this.width = width
        this.height = height

        resizeNativeCanvas(width, height)
        content?.resize(width, height)
        repaintNow()
    }

    fun repaint() {
        repaintRequestHandle = null

        val content = content ?: return
        if (width <= 0 || height <= 0) return

        context2d.clearRect(0.0, 0.0, width.toDouble(), height.toDouble())
        content.paint(context2d)
    }

    fun requestRepaint() {
        if (repaintRequestHandle != null) {
            return
        }

        repaintRequestHandle = window.requestAnimationFrame {
            repaint()
        }
    }

    private fun repaintNow() {
        repaintRequestHandle?.let(window::cancelAnimationFrame)
        repaint()
    }

    private fun resizeNativeCanvas(width: Int, height: Int) {
        val pixelRatio = DomCanvas.DEVICE_PIXEL_RATIO
        domCanvasElement.style.width = "${width}px"
        domCanvasElement.style.height = "${height}px"
        domCanvasElement.width = (width * pixelRatio).toInt()
        domCanvasElement.height = (height * pixelRatio).toInt()
        context2d.onCanvasResized()
    }

    override fun dispose() {
        repaintRequestHandle?.let(window::cancelAnimationFrame)
        repaintRequestHandle = null
        contentReg.dispose()
        disposables.dispose()
    }

    private fun log(message: () -> String) {
        if (LOG_ENABLED) {
            println("[${this.identityString}] ${message()}")
        }
    }

    companion object {
        private const val LOG_ENABLED = false
    }
}
