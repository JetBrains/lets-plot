/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas.dom

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.SimpleAsync
import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.event.dom.DomEventUtil.translateInPageCoord
import jetbrains.datalore.base.event.dom.DomEventUtil.translateInTargetCoord
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.js.css.enumerables.CssPosition
import jetbrains.datalore.base.js.css.setPosition
import jetbrains.datalore.base.js.dom.DomEventListener
import jetbrains.datalore.base.js.dom.DomEventType
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.event.handler
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.vis.canvas.AnimationProvider.AnimationEventHandler
import jetbrains.datalore.vis.canvas.AnimationProvider.AnimationTimer
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.datalore.vis.canvas.CanvasControl
import jetbrains.datalore.vis.canvas.EventPeer
import jetbrains.datalore.vis.canvas.dom.DomCanvas.Companion.DEVICE_PIXEL_RATIO
import org.w3c.dom.*
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag
import kotlin.browser.document
import org.w3c.dom.events.MouseEvent as W3cMouseEvent

class DomCanvasControl(override val size: Vector) : CanvasControl {
    val rootElement: HTMLElement = document.createElement("div") as HTMLElement
    private val eventPeer = DomEventPeer(rootElement)

    init {
        rootElement.style.setPosition(CssPosition.RELATIVE)
    }

    override fun createAnimationTimer(eventHandler: AnimationEventHandler): AnimationTimer {
        return object : DomAnimationTimer(rootElement) {
            override fun handle(millisTime: Long) {
                eventHandler.onEvent(millisTime)
            }
        }
    }

    override fun addEventHandler(eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        val translator = when (eventSpec) {
            MouseEventSpec.MOUSE_PRESSED, MouseEventSpec.MOUSE_DRAGGED -> ::translateInPageCoord
            else -> ::translateInTargetCoord
        }

        return eventPeer.addEventHandler(
            eventSpec,
            handler { eventHandler.onEvent(translator(it)) }
        )
    }

    override fun createCanvas(size: Vector): Canvas {
        val domCanvas = DomCanvas.create(size, DEVICE_PIXEL_RATIO)
        domCanvas.canvasElement.style.setPosition(CssPosition.ABSOLUTE)
        return domCanvas
    }

    override fun createSnapshot(dataUrl: String): Async<Canvas.Snapshot> {
        val async = SimpleAsync<Canvas.Snapshot>()

        val image = Image()

        image.onload = {
            val domCanvas =
                DomCanvas.create(Vector(image.width, image.height), 1.0)
            val ctx = domCanvas.canvasElement.getContext("2d") as CanvasRenderingContext2D
            ctx.drawImage(image, 0.0, 0.0)

            domCanvas.takeSnapshot().onSuccess { async.success(it) }
        }

        image.src = dataUrl

        return async
    }

    override fun createSnapshot(bytes: ByteArray): Async<Canvas.Snapshot> {
        return Blob(arrayOf(bytes), BlobPropertyBag("image/png"))
            .let { URL.createObjectURL(it) }
            .let(::createSnapshot)
    }

    override fun addChild(canvas: Canvas) {
        rootElement.appendChild((canvas as DomCanvas).canvasElement)
    }

    override fun addChild(index: Int, canvas: Canvas) {
        rootElement.insertBefore((canvas as DomCanvas).canvasElement, rootElement.childNodes[index])
    }

    override fun removeChild(canvas: Canvas) {
        rootElement.removeChild((canvas as DomCanvas).canvasElement)
    }

    override fun <T> schedule(f: () -> T) {
        f()
    }

    private class DomEventPeer (private val myRootElement: Node) :
        EventPeer<MouseEventSpec, W3cMouseEvent>(MouseEventSpec::class) {
        private var myButtonPressed = false

        init {
            handle(DomEventType.MOUSE_ENTER) { dispatch(MouseEventSpec.MOUSE_ENTERED, it) }

            handle(DomEventType.MOUSE_LEAVE) { dispatch(MouseEventSpec.MOUSE_LEFT, it) }

            handle(DomEventType.CLICK) { dispatch(MouseEventSpec.MOUSE_CLICKED, it) }

            handle(DomEventType.DOUBLE_CLICK) { dispatch(MouseEventSpec.MOUSE_DOUBLE_CLICKED, it) }

            handle(DomEventType.MOUSE_DOWN) {
                myButtonPressed = true
                dispatch(MouseEventSpec.MOUSE_PRESSED, it)
            }

            handle(DomEventType.MOUSE_UP) {
                myButtonPressed = false
                dispatch(MouseEventSpec.MOUSE_RELEASED, it)
            }

            handle(DomEventType.MOUSE_MOVE) {
                if (myButtonPressed) {
                    dispatch(MouseEventSpec.MOUSE_DRAGGED, it)
                } else {
                    dispatch(MouseEventSpec.MOUSE_MOVED, it)
                }
            }
        }

        private fun handle(eventSpec: DomEventType<W3cMouseEvent>, handler: (W3cMouseEvent) -> Unit) {
            targetNode(eventSpec).addEventListener(eventSpec.name, DomEventListener<W3cMouseEvent> {
                handler(it)
                false
            })
        }

        private fun targetNode(eventSpec: DomEventType<W3cMouseEvent>): Node = when (eventSpec) {
            DomEventType.MOUSE_MOVE, DomEventType.MOUSE_UP -> document
            else -> myRootElement
        }

        override fun onSpecAdded(spec: MouseEventSpec) {}

        override fun onSpecRemoved(spec: MouseEventSpec) {}
    }
}
