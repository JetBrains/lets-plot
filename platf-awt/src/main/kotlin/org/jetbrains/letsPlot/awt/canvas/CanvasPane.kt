/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.canvas

import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.core.canvasFigure.CanvasFigure
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Rectangle
import javax.swing.JComponent

class CanvasPane(
    figure: CanvasFigure? = null
) : JComponent() {
    private val canvasPeer: CanvasPeer = AwtCanvasPeer()
    private var figureRegistration: Registration = Registration.EMPTY

    var figure: CanvasFigure? = null
        set(canvasFigure) {
            if (field == canvasFigure) {
                return
            }

            figureRegistration.remove()
            if (canvasFigure != null) {
                figureRegistration = CompositeRegistration(
                    canvasFigure.mapToCanvas(canvasPeer),
                    canvasFigure.onRepaintRequest(::repaint),
                )
                bounds = Rectangle(0, 0, canvasFigure.size.x, canvasFigure.size.y)
            }
            field = canvasFigure
        }

    init {
        this.figure = figure
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        val g2d = g as Graphics2D
        figure?.draw(AwtContext2d(g2d))
    }
}
