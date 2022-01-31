/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas.dom

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.SimpleAsync
import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.event.dom.DomEventUtil.getButton
import jetbrains.datalore.base.event.dom.DomEventUtil.getModifiers
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
import kotlinx.browser.document
import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag
import org.w3c.dom.events.MouseEvent as W3cMouseEvent

class DomCanvasControl(
    private val myRootElement: HTMLElement,
    override val size: Vector,
    private val myEventPeer: EventPeer<MouseEventSpec, MouseEvent>
) : CanvasControl {

    override fun createAnimationTimer(eventHandler: AnimationEventHandler): AnimationTimer {
        return object : DomAnimationTimer() {
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
        private var myLastDragEndEventTimestamp: Number = 0
        private var myWasDragged = false
        private var myButtonPressCoord: Vector? = null
        private val myDragToleranceDistance = 3.0

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
                if (myLastDragEndEventTimestamp == it.timeStamp) return@handle
                if (!isHitOnTarget(it)) return@handle

                dispatch(MouseEventSpec.MOUSE_CLICKED, translate(it))
                myWasDragged = false
            }

            handle(DOUBLE_CLICK) {
                if (myLastDragEndEventTimestamp == it.timeStamp) return@handle
                if (!isHitOnTarget(it)) return@handle

                dispatch(MouseEventSpec.MOUSE_DOUBLE_CLICKED, translate(it))
            }

            handle(MOUSE_DOWN) {
                if (!isHitOnTarget(it)) return@handle

                myButtonPressed = true
                myButtonPressCoord = Vector(it.x.toInt(), it.y.toInt())
                dispatch(MouseEventSpec.MOUSE_PRESSED, translate(it))
            }

            handle(MOUSE_UP) {
                if (myWasDragged) {
                    myLastDragEndEventTimestamp = it.timeStamp
                }
                myWasDragged = false
                myButtonPressed = false
                myButtonPressCoord = null
                dispatch(MouseEventSpec.MOUSE_RELEASED, translate(it))
            }

            handle(MOUSE_MOVE) {
                if (myWasDragged) {
                    dispatch(MouseEventSpec.MOUSE_DRAGGED, translate(it))
                }
                else if (myButtonPressed && !myWasDragged) {
                    val distance = myButtonPressCoord?.sub(Vector(it.x.toInt(), it.y.toInt()))?.length() ?: 0.0
                    if (distance > myDragToleranceDistance) {
                        myWasDragged = true
                        dispatch(MouseEventSpec.MOUSE_DRAGGED, translate(it))
                    } else {
                        // Just in case do not generate move event. Can be changed if needed.
                    }
                } else if (!myButtonPressed && !myWasDragged) {
                    if (!isHitOnTarget(it)) return@handle

                    dispatch(MouseEventSpec.MOUSE_MOVED, translate(it))
                } else {
                    error("MOUSE_MOVE: unknown internal state")
                }
            }
        }

        private fun handle(eventSpec: DomEventType<W3cMouseEvent>, handler: (W3cMouseEvent) -> Unit) {
            when (eventSpec) {
                MOUSE_MOVE, MOUSE_UP -> document
                else -> myEventTarget
            }.addEventListener(eventSpec.name, DomEventListener<W3cMouseEvent> {
                handler(it)
                false
            })
        }

        override fun onSpecAdded(spec: MouseEventSpec) {}

        override fun onSpecRemoved(spec: MouseEventSpec) {}

        private fun isHitOnTarget(event: DomMouseEvent): Boolean {
            val v = Vector(event.offsetX.toInt(), event.offsetY.toInt())
            return myTargetBounds.contains(v)
        }

        private fun translate(event: DomMouseEvent): MouseEvent {
            val targetRect = myEventTarget.getBoundingClientRect()
            return MouseEvent(
                event.clientX - targetRect.x.toInt() - myTargetBounds.origin.x,
                event.clientY - targetRect.y.toInt() - myTargetBounds.origin.y,
                getButton(event),
                getModifiers(event))
        }
    }
}

private operator fun Vector.times(value: Int): Vector {
    return Vector(x * value, y * value)
}
