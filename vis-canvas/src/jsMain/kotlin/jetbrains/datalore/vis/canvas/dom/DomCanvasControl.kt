/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas.dom

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.SimpleAsync
import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.event.dom.DomEventUtil
import jetbrains.datalore.base.event.dom.DomEventUtil.translateInPageCoord
import jetbrains.datalore.base.geometry.Rectangle
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.js.css.enumerables.CssPosition
import jetbrains.datalore.base.js.css.setPosition
import jetbrains.datalore.base.js.dom.DomEventListener
import jetbrains.datalore.base.js.dom.DomEventType
import jetbrains.datalore.base.js.dom.DomEventType.Companion.CLICK
import jetbrains.datalore.base.js.dom.DomEventType.Companion.DOUBLE_CLICK
import jetbrains.datalore.base.js.dom.DomEventType.Companion.MOUSE_DOWN
import jetbrains.datalore.base.js.dom.DomEventType.Companion.MOUSE_ENTER
import jetbrains.datalore.base.js.dom.DomEventType.Companion.MOUSE_LEAVE
import jetbrains.datalore.base.js.dom.DomEventType.Companion.MOUSE_MOVE
import jetbrains.datalore.base.js.dom.DomEventType.Companion.MOUSE_UP
import jetbrains.datalore.base.js.dom.DomMouseEvent
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
import org.w3c.dom.events.Event
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag
import kotlinx.browser.document
import org.w3c.dom.events.MouseEvent as W3cMouseEvent

class DomCanvasControl(
    private val myRootElement: HTMLElement,
    override val size: Vector,
    private val myEventPeer: EventPeer<MouseEventSpec, MouseEvent>
) : CanvasControl {

    override fun createAnimationTimer(eventHandler: AnimationEventHandler): AnimationTimer {
        return object : DomAnimationTimer(myRootElement) {
            override fun handle(millisTime: Long) {
                eventHandler.onEvent(millisTime)
            }
        }
    }

    override fun addEventHandler(eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        return myEventPeer.addEventHandler(
            eventSpec,
            handler {
                eventHandler.onEvent(it)
            }
        )
    }

    override fun createCanvas(size: Vector): Canvas {
        val domCanvas = DomCanvas.create(size, DEVICE_PIXEL_RATIO)
        domCanvas.canvasElement.style.setPosition(CssPosition.ABSOLUTE)
        return domCanvas
    }

    override fun createSnapshot(dataUrl: String): Async<Canvas.Snapshot> {
        return createSnapshotAsync(dataUrl, null)
    }

    override fun createSnapshot(bytes: ByteArray, size: Vector): Async<Canvas.Snapshot> {
        return Blob(arrayOf(bytes), BlobPropertyBag("image/png"))
            .let { URL.createObjectURL(it) }
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

        domCanvas.takeSnapshot().onSuccess { consumer(it) }
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

    class DomEventPeer (private val myEventTarget: Element, private val myTargetBounds: Rectangle) :
        EventPeer<MouseEventSpec, MouseEvent>(MouseEventSpec::class) {
        private var myButtonPressed = false
        private var myWasDragged = false

        init {
            handle(MOUSE_ENTER) {
                if (!isHitOnTarget(it)) return@handle

                dispatch(MouseEventSpec.MOUSE_ENTERED, translate(it))
            }

            handle(MOUSE_LEAVE) {
                if (!isHitOnTarget(it)) return@handle

                dispatch(MouseEventSpec.MOUSE_LEFT, translate(it))
            }

            handle(CLICK) {
                if (!myWasDragged) {
                    if (!isHitOnTarget(it)) return@handle

                    dispatch(MouseEventSpec.MOUSE_CLICKED, translate(it))
                }
                myWasDragged = false
            }

            handle(DOUBLE_CLICK) {
                if (!isHitOnTarget(it)) return@handle

                dispatch(MouseEventSpec.MOUSE_DOUBLE_CLICKED, translate(it))
            }

            handle(MOUSE_DOWN) {
                if (!isHitOnTarget(it)) return@handle

                myButtonPressed = true
                dispatch(MouseEventSpec.MOUSE_PRESSED, translateInPageCoord(it))
            }

            handle(MOUSE_UP) {
                myButtonPressed = false
                dispatch(MouseEventSpec.MOUSE_RELEASED, translate(it))
            }

            handle(MOUSE_MOVE) {
                if (myButtonPressed) {
                    myWasDragged = true

                    dispatch(MouseEventSpec.MOUSE_DRAGGED, translateInPageCoord(it))
                } else {
                    if (!isHitOnTarget(it)) return@handle

                    dispatch(MouseEventSpec.MOUSE_MOVED, translate(it))
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
            MOUSE_MOVE, MOUSE_UP -> document
            else -> myEventTarget
        }

        override fun onSpecAdded(spec: MouseEventSpec) {}

        override fun onSpecRemoved(spec: MouseEventSpec) {}

        private fun isHitOnTarget(event: DomMouseEvent): Boolean {
            return myTargetBounds.contains(Vector(event.offsetX.toInt(), event.offsetY.toInt()))
        }

        private fun translate(event: DomMouseEvent) : MouseEvent {
            return DomEventUtil.translateInTargetCoordWithOffset(event, myEventTarget, myTargetBounds.origin)
        }
    }
}

private operator fun Vector.times(value: Int): Vector {
    return Vector(x * value, y * value)
}
