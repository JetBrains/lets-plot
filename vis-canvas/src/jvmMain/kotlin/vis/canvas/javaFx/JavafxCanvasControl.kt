/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas.javaFx

import javafx.embed.swing.JFXPanel
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Scene
import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.Asyncs
import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.event.MouseEventSpec.*
import jetbrains.datalore.base.event.jfx.JfxEventUtil
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.event.handler
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.vis.canvas.AnimationProvider.AnimationEventHandler
import jetbrains.datalore.vis.canvas.AnimationProvider.AnimationTimer
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.datalore.vis.canvas.CanvasControl
import jetbrains.datalore.vis.canvas.EventPeer
import jetbrains.datalore.vis.canvas.javaFx.JavafxCanvasUtil.imagePngBase64ToImage
import jetbrains.datalore.vis.canvas.javaFx.JavafxCanvasUtil.imagePngByteArrayToImage
import javafx.event.EventHandler as jfxHandler

class JavafxCanvasControl(override val size: Vector, private val myPixelRatio: Double) : CanvasControl {
    private val myRoot = Group()
    private val myEventPeer = JavafxEventPeer(myRoot)
    val component: JFXPanel = JFXPanel()

    init {
        component.scene = Scene(myRoot)
    }

    fun dispatch(eventSpec: MouseEventSpec, mouseEvent: MouseEvent) {
        myEventPeer.dispatch(eventSpec, mouseEvent)
    }

    override fun createAnimationTimer(eventHandler: AnimationEventHandler): AnimationTimer {
        return object : JavafxAnimationTimer() {
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
        return JavafxCanvas.create(size, myPixelRatio)
    }

    override fun createSnapshot(dataUrl: String): Async<Canvas.Snapshot> {
        return Asyncs.constant(
            JavafxCanvas.JavafxSnapshot(
                imagePngBase64ToImage(
                    dataUrl
                )
            )
        )
    }

    override fun createSnapshot(bytes: ByteArray): Async<Canvas.Snapshot> {
        return Asyncs.constant(
            JavafxCanvas.JavafxSnapshot(
                imagePngByteArrayToImage(bytes)
            )
        )
    }

    override fun createSnapshot(bytes: ByteArray, size: Vector): Async<Canvas.Snapshot> {
        return Asyncs.constant(
            JavafxCanvas.JavafxSnapshot(
                imagePngByteArrayToImage(bytes, size * myPixelRatio.toInt())
            )
        )
    }

    override fun addChild(canvas: Canvas) {
        myRoot.children.add((canvas as JavafxCanvas).nativeCanvas)
    }

    override fun addChild(index: Int, canvas: Canvas) {
        myRoot.children.add(index, (canvas as JavafxCanvas).nativeCanvas)
    }

    override fun removeChild(canvas: Canvas) {
        myRoot.children.remove((canvas as JavafxCanvas).nativeCanvas)
    }

    override fun <T> schedule(f: () -> T) {
        JavafxCanvasUtil.runInJavafxThread(f)
    }

    private class JavafxEventPeer(node: Node) : EventPeer<MouseEventSpec, MouseEvent>(MouseEventSpec::class) {

        init {
            node.onMouseEntered = jfxHandler {
                dispatch(MOUSE_ENTERED, JfxEventUtil.translate(it) )
            }
            node.onMouseExited = jfxHandler {
                dispatch(MOUSE_LEFT, JfxEventUtil.translate(it))
            }
            node.onMouseMoved = jfxHandler {
                dispatch(MOUSE_MOVED, JfxEventUtil.translate(it))
            }
            node.onMouseDragged = jfxHandler {
                dispatch(MOUSE_DRAGGED, JfxEventUtil.translate(it))
            }
            node.onMouseClicked = jfxHandler {
                if (it.clickCount % 2 == 1) {
                    dispatch(MOUSE_CLICKED, JfxEventUtil.translate(it))
                } else {
                    dispatch(MOUSE_DOUBLE_CLICKED, JfxEventUtil.translate(it))
                }
            }
            node.onMousePressed = jfxHandler {
                dispatch(MOUSE_PRESSED, JfxEventUtil.translate(it))
            }
            node.onMouseReleased = jfxHandler {
                dispatch(MOUSE_RELEASED, JfxEventUtil.translate(it))
            }
        }

        override fun onSpecAdded(spec: MouseEventSpec) {}

        override fun onSpecRemoved(spec: MouseEventSpec) {}
    }

    private operator fun Vector.times(value: Int): Vector {
        return Vector(x * value, y * value)
    }
}
