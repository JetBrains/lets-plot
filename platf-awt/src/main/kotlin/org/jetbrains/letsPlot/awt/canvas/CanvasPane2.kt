/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.canvas

import org.jetbrains.letsPlot.commons.event.MouseEventSource
import org.jetbrains.letsPlot.commons.registration.*
import org.jetbrains.letsPlot.core.canvas.ScaledContext2d
import org.jetbrains.letsPlot.core.canvasFigure.CanvasFigure2
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JComponent

class CanvasPane2(
    figure: CanvasFigure2? = null,
    private val pixelDensity: Double = 1.0
) : DisposingHub, Disposable, JComponent() {
    private val registrations = CompositeRegistration()
    private var figureRegistration: Registration = Registration.EMPTY
    private val canvasPeer: AwtCanvasPeer = AwtCanvasPeer(pixelDensity)
    private val mouseEventSource: MouseEventSource = AwtMouseEventMapper(this)

    var figure: CanvasFigure2? = null
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

        val g2d = g!!.create() as Graphics2D

        if (figure != null) {
            val scale = g2d.transform.scaleX
            val ctx = if (scale != 1.0) {
                // TODO: proper fix needed - just remove ScaledContext2d
                g2d.scale(1 / scale, 1 / scale)
                ScaledContext2d(AwtContext2d(g2d), scale)
            } else {
                AwtContext2d(g2d)
            }
            figure!!.paint(ctx)
        }
    }

    override fun registerDisposable(disposable: Disposable) {
        registrations.add(DisposableRegistration(disposable))
    }

    override fun dispose() {
        registrations.dispose()
        figureRegistration.dispose()
    }
}
