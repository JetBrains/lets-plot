/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.awt.canvas


import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.event.MouseEventSpec.*
import org.jetbrains.letsPlot.commons.geometry.Rectangle
import org.jetbrains.letsPlot.commons.geometry.Vector
import jetbrains.datalore.vis.canvas.EventPeer
import org.jetbrains.letsPlot.platf.awt.util.AwtEventUtil
import java.awt.Component
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.awt.event.MouseEvent as AwtMouseEvent

class AwtEventPeer(component: Component, private val myTargetBounds: Rectangle) :
    EventPeer<MouseEventSpec, MouseEvent>(MouseEventSpec::class), MouseListener, MouseMotionListener {

    init {
        component.addMouseListener(this)
        component.addMouseMotionListener(this)
    }

    override fun onSpecAdded(spec: MouseEventSpec) {}

    override fun onSpecRemoved(spec: MouseEventSpec) {}

    override fun mouseClicked(e: AwtMouseEvent) {
        if (!isHitOnTarget(e)) return

        if (e.clickCount % 2 == 1) {
            dispatch(MOUSE_CLICKED, translate(e))
        } else {
            dispatch(MOUSE_DOUBLE_CLICKED, translate(e))
        }
    }

    override fun mousePressed(e: AwtMouseEvent) {
        if (!isHitOnTarget(e)) return

        dispatch(MOUSE_PRESSED, translate(e))
    }

    override fun mouseReleased(e: AwtMouseEvent) {
        dispatch(MOUSE_RELEASED, translate(e))
    }

    override fun mouseEntered(e: AwtMouseEvent) {
        if (!isHitOnTarget(e)) return

        dispatch(MOUSE_ENTERED, translate(e))
    }

    override fun mouseExited(e: AwtMouseEvent) {
        if (!isHitOnTarget(e)) return

        dispatch(MOUSE_LEFT, translate(e))
    }

    override fun mouseDragged(e: AwtMouseEvent) {
        dispatch(MOUSE_DRAGGED, translate(e))
    }

    override fun mouseMoved(e: AwtMouseEvent) {
        if (!isHitOnTarget(e)) return

        dispatch(MOUSE_MOVED, translate(e))
    }

    private fun isHitOnTarget(event: AwtMouseEvent): Boolean {
        return myTargetBounds.contains(Vector(event.x, event.y))
    }

    private fun translate(event: AwtMouseEvent): MouseEvent {
        return AwtEventUtil.translate(event, myTargetBounds.origin)
    }
}