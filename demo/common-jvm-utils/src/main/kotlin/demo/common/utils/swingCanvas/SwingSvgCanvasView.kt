/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.common.utils.swingCanvas

import org.jetbrains.letsPlot.awt.canvas.AwtAnimationTimerPeer
import org.jetbrains.letsPlot.awt.canvas.AwtCanvasControl
import org.jetbrains.letsPlot.awt.canvas.AwtMouseEventMapper
import org.jetbrains.letsPlot.awt.util.AwtEventUtil.translate
import org.jetbrains.letsPlot.commons.event.MouseEventSpec.*
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.core.canvas.CanvasControl
import org.jetbrains.letsPlot.raster.view.SvgCanvasView
import java.awt.Color
import java.awt.Rectangle
import java.awt.event.*
import javax.swing.BorderFactory
import javax.swing.JPanel

// TODO: remove
class SwingSvgCanvasView : SvgCanvasView() {
    val container = JPanel(null)
    private var awtCanvasControl: AwtCanvasControl? = null

    override fun createCanvasControl(view: SvgCanvasView): CanvasControl {
        if (awtCanvasControl != null) return awtCanvasControl!!
        val w = 600
        val h = 400
        val awtCanvasControl = AwtCanvasControl(
            size = Vector(w, h),
            animationTimerPeer = AwtAnimationTimerPeer(),
            mouseEventSource = AwtMouseEventMapper(container)
        )
        val canvasComponent = awtCanvasControl.component()
        canvasComponent.bounds = Rectangle(0, 0, w, h)
        canvasComponent.background = Color.WHITE
        canvasComponent.border = BorderFactory.createLineBorder(Color.RED, 5)
        container.add(canvasComponent)
        container.bounds = Rectangle(0, 0, w, h)
        this.awtCanvasControl = awtCanvasControl

        canvasComponent.addMouseListener(object : MouseListener {
            override fun mouseClicked(e: MouseEvent) {
                val event = when (e.clickCount) {
                    1 -> MOUSE_CLICKED
                    2 -> MOUSE_DOUBLE_CLICKED
                    else -> return
                }

                dispatchEvent(event, translate(e))
            }
            override fun mousePressed(e: MouseEvent) { dispatchEvent(MOUSE_PRESSED, translate(e)) }
            override fun mouseReleased(e: MouseEvent) { dispatchEvent(MOUSE_RELEASED, translate(e)) }
            override fun mouseEntered(e: MouseEvent) { dispatchEvent(MOUSE_ENTERED, translate(e)) }
            override fun mouseExited(e: MouseEvent) { dispatchEvent(MOUSE_LEFT, translate(e)) }
        })
        canvasComponent.addMouseMotionListener(object : MouseMotionListener {
            override fun mouseDragged(e: MouseEvent) { dispatchEvent(MOUSE_DRAGGED, translate(e)) }
            override fun mouseMoved(e: MouseEvent) { dispatchEvent(MOUSE_MOVED, translate(e)) }
        })
        canvasComponent.addMouseWheelListener(object : MouseWheelListener {
            override fun mouseWheelMoved(e: MouseWheelEvent) {  dispatchEvent(MOUSE_WHEEL_ROTATED, translate(e)) }
        })


        return awtCanvasControl
    }

    override fun updateCanvasSize(width: Int, height: Int) {
        println("updateCanvasSize: $width x $height")
    }

    override fun onHrefClick(href: String) {
        TODO("Not yet implemented")
    }

}
