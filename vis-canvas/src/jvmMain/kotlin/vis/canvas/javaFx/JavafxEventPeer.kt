/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas.javaFx

import javafx.scene.Node
import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.geometry.Rectangle
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.vis.canvas.EventPeer
import org.jetbrains.letsPlot.platf.swing.jfx.JfxEventUtil
import javafx.event.EventHandler as jfxHandler
import javafx.scene.input.MouseEvent as JfxMouseEvent


class JavafxEventPeer(node: Node, private val myTargetBounds: Rectangle) :
    EventPeer<MouseEventSpec, MouseEvent>(MouseEventSpec::class) {

    init {
        node.onMouseEntered = jfxHandler {
            if (!isHitOnTarget(it)) return@jfxHandler

            dispatch(MouseEventSpec.MOUSE_ENTERED, JfxEventUtil.translate(it))
        }

        node.onMouseExited = jfxHandler {
            if (!isHitOnTarget(it)) return@jfxHandler

            dispatch(MouseEventSpec.MOUSE_LEFT, translate(it))
        }

        node.onMouseMoved = jfxHandler {
            if (!isHitOnTarget(it)) return@jfxHandler

            dispatch(MouseEventSpec.MOUSE_MOVED, translate(it))
        }

        node.onMouseDragged = jfxHandler {
            dispatch(MouseEventSpec.MOUSE_DRAGGED, translate(it))
        }

        node.onMouseClicked = jfxHandler {
            if (!isHitOnTarget(it)) return@jfxHandler

            if (it.clickCount % 2 == 1) {
                dispatch(MouseEventSpec.MOUSE_CLICKED, translate(it))
            } else {
                dispatch(MouseEventSpec.MOUSE_DOUBLE_CLICKED, translate(it))
            }
        }

        node.onMousePressed = jfxHandler {
            if (!isHitOnTarget(it)) return@jfxHandler

            dispatch(MouseEventSpec.MOUSE_PRESSED, translate(it))
        }

        node.onMouseReleased = jfxHandler {
            dispatch(MouseEventSpec.MOUSE_RELEASED, translate(it))
        }
    }

    override fun onSpecAdded(spec: MouseEventSpec) {}

    override fun onSpecRemoved(spec: MouseEventSpec) {}

    private fun isHitOnTarget(event: JfxMouseEvent): Boolean {
        return myTargetBounds.contains(Vector(event.x.toInt(), event.y.toInt()))
    }

    private fun translate(event: JfxMouseEvent): MouseEvent {
        return JfxEventUtil.translate(event, myTargetBounds.origin)
    }
}