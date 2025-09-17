/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.canvas

import org.jetbrains.letsPlot.commons.event.MouseEventSource
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.core.canvasFigure.CanvasFigure
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JComponent

class CanvasPane(
    figure: CanvasFigure? = null,
    private val pixelDensity: Double = 1.0
) : JComponent() {
    private val canvasPeer: CanvasPeer = AwtCanvasPeer(pixelDensity)
    private var figureRegistration: Registration = Registration.EMPTY
    val mouseEventSource: MouseEventSource = AwtMouseEventMapper(this)

    var figure: CanvasFigure? = null
        set(canvasFigure) {
            if (field == canvasFigure) {
                return
            }

            figureRegistration.remove()
            if (canvasFigure != null) {
                canvasFigure.eventPeer.addEventSource(mouseEventSource)
                figureRegistration = CompositeRegistration(
                    canvasFigure.mapToCanvas(canvasPeer),
                    canvasFigure.onRepaintRequested(::repaint),
                )
            }
            field = canvasFigure
        }

    init {
        isOpaque = true
        this.figure = figure
    }

    override fun setBounds(x: Int, y: Int, width: Int, height: Int) {
        val sizeChanged = this.width != width || this.height != height
        super.setBounds(x, y, width, height)

        // Intercept the size change immediately and update the figure.
        // This happens before componentResized and before any repaint triggered by the resize.
        if (sizeChanged) {
            figure?.resize(width, height)
        }
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)

        if (width <= 0 || height <= 0) {
            return
        }

        val g2d = g as Graphics2D

        if (figure != null) {
            figure!!.paint(AwtContext2d(g2d))
        }
    }
}
