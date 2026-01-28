/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.canvas

import org.jetbrains.letsPlot.commons.SystemTime
import org.jetbrains.letsPlot.commons.event.MouseEventSource
import org.jetbrains.letsPlot.commons.registration.*
import org.jetbrains.letsPlot.core.canvasFigure.CanvasFigure2
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JComponent
import javax.swing.Timer

@Deprecated("Migrate to CanvasPane", ReplaceWith("CanvasPane", "org.jetbrains.letsPlot.awt.canvas.CanvasPane"))
typealias CanvasPane2 = CanvasPane

class CanvasPane(
    figure: CanvasFigure2? = null,
    pixelDensity: Double = 1.0
) : DisposingHub, Disposable, JComponent() {
    private val registrations = CompositeRegistration()
    private var figureRegistration: Registration = Registration.EMPTY
    private val canvasPeer: AwtCanvasPeer = AwtCanvasPeer(pixelDensity)
    private val mouseEventSource: MouseEventSource = AwtMouseEventMapper(this)
    private val systemTime: SystemTime = SystemTime()

    var figure: CanvasFigure2? = null
        set(canvasFigure) {
            if (field == canvasFigure) {
                return
            }

            figureRegistration.remove()
            if (canvasFigure != null) {
                canvasFigure.resize(width, height)
                canvasFigure.mouseEventPeer.addEventSource(mouseEventSource)
                val animationTimer = Timer(1000 / 60) {
                    canvasFigure.onFrame(systemTime.getTimeMs())
                }
                animationTimer.start()

                figureRegistration = CompositeRegistration(
                    Registration.onRemove(animationTimer::stop),
                    canvasFigure.mapToCanvas(canvasPeer),
                    canvasFigure.onRepaintRequested(::repaint),
                )
            }
            field = canvasFigure
        }

    init {
        isOpaque = false
        this.figure = figure
    }

    override fun getPreferredSize(): Dimension? {
        return figure?.size?.let { s -> Dimension(s.x, s.y) }
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
            val ctx = AwtContext2d(g2d, contentScale = g2d.transform.scaleX)
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
