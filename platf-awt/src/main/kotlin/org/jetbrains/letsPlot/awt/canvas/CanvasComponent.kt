/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.canvas

import org.jetbrains.letsPlot.commons.SystemTime
import org.jetbrains.letsPlot.commons.event.MouseEventSource
import org.jetbrains.letsPlot.commons.registration.*
import org.jetbrains.letsPlot.core.canvas.Drawable2
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JComponent
import javax.swing.Timer

@Deprecated("Migrate to CanvasComponent", ReplaceWith("CanvasComponent", "org.jetbrains.letsPlot.awt.canvas.CanvasComponent"))
typealias CanvasPane2 = CanvasComponent

class CanvasComponent(
    content: Drawable2? = null,
    pixelDensity: Double = 1.0
) : DisposingHub, Disposable, JComponent() {
    private var isFigureAttached = false
    private val registrations = CompositeRegistration()
    private var figureRegistration: Registration = Registration.EMPTY
    internal var canvasPeer: AwtCanvasPeer = AwtCanvasPeer(fontManager = FontManager.DEFAULT, pixelDensity)
        set(value) {
            if (isFigureAttached) {
                throw IllegalStateException("Can't change canvasPeer after figure is attached")
            }
            field = value
        }
    private val mouseEventSource: MouseEventSource = AwtMouseEventMapper(this)
    private val systemTime: SystemTime = SystemTime()

    var content: Drawable2? = null
        set(content) {
            if (field == content) {
                return
            }

            figureRegistration.remove()
            if (content != null) {
                isFigureAttached = true
                content.resize(width, height)
                content.mouseEventPeer.addEventSource(mouseEventSource)
                val animationTimer = Timer(1000 / 60) {
                    content.onFrame(systemTime.getTimeMs())
                }
                animationTimer.start()

                figureRegistration = CompositeRegistration(
                    Registration.onRemove(animationTimer::stop),
                    content.mapToCanvas(canvasPeer),
                    content.onRepaintRequested(::repaint),
                )
            }
            field = content
        }

    init {
        isOpaque = false
        this.content = content
    }

    override fun getPreferredSize(): Dimension? {
        return content?.size?.let { s -> Dimension(s.x, s.y) }
    }

    override fun setBounds(x: Int, y: Int, width: Int, height: Int) {
        val sizeChanged = this.width != width || this.height != height
        super.setBounds(x, y, width, height)

        // Intercept the size change immediately and update the figure.
        // This happens before componentResized and before any repaint triggered by the resize.
        if (sizeChanged) {
            content?.resize(width, height)
        }
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)

        if (width <= 0 || height <= 0) {
            return
        }

        val g2d = g!!.create() as Graphics2D

        if (content != null) {
            val ctx = AwtContext2d(g2d, contentScale = g2d.transform.scaleX, fontManager = canvasPeer.fontManager)
            content!!.paint(ctx)
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
