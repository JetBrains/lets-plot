/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.canvas

import org.jetbrains.letsPlot.commons.SystemTime
import org.jetbrains.letsPlot.commons.event.MouseEventSource
import org.jetbrains.letsPlot.commons.logging.identityString
import org.jetbrains.letsPlot.commons.registration.*
import org.jetbrains.letsPlot.core.canvas.CanvasDrawable
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JComponent
import javax.swing.Timer

@Deprecated(
    "Migrate to CanvasComponent",
    ReplaceWith("CanvasComponent", "org.jetbrains.letsPlot.awt.canvas.CanvasComponent")
)
typealias CanvasPane2 = CanvasComponent

class CanvasComponent(
    content: CanvasDrawable? = null
) : DisposingHub, Disposable, JComponent() {
    var content: CanvasDrawable? = content
        set(content) {
            if (field == content) {
                return
            }

            contentReg.dispose()

            if (content == null) {
                contentReg = Registration.EMPTY
            } else {
                isContentAttached = true

                animationTimer.start()
                content.resize(this.width, this.height)

                contentReg = CompositeRegistration(
                    content.mouseEventPeer.addEventSource(mouseEventSource),
                    content.mapToCanvas(canvasPeer),
                    content.onRepaintRequested(::repaint),
                    Registration.onRemove(animationTimer::stop),
                    Registration.onRemove { log { "Content removed: ${content.identityString}" } }
                )
            }

            log { "Content set: ${content?.identityString ?: "null"}" }
            field = content
        }

    init {
        isOpaque = false
        log { "created" }
    }

    private val animationTimer = Timer(1000 / 60) { onTimer() }
    private val mouseEventSource: MouseEventSource = AwtMouseEventMapper(this)
    private val systemTime: SystemTime = SystemTime()

    // For testing purposes, to check that canvasPeer is not changed after content is attached.
    // Can only change from false to true, never back to false
    private var isContentAttached = false
    private var contentReg: Registration = Registration.EMPTY
    private var disposables = CompositeRegistration()

    internal var canvasPeer: AwtCanvasPeer = AwtCanvasPeer(fontManager = FontManager.DEFAULT)
        set(value) {
            check(!isContentAttached) { "Can't change canvasPeer after figure is attached" }
            field = value
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

        val content = content ?: return

        if (width <= 0 || height <= 0) {
            return
        }

        val g2d = g!!.create() as Graphics2D
        val ctx = AwtContext2d(g2d, contentScale = g2d.transform.scaleX, fontManager = canvasPeer.fontManager)
        content.paint(ctx)
        g2d.dispose()
    }

    override fun registerDisposable(disposable: Disposable) {
        disposables.add(DisposableRegistration(disposable))
    }

    override fun dispose() {
        contentReg.dispose()
        disposables.dispose()
        log { "disposed" }
    }

    private fun onTimer() {
        content?.onFrame(systemTime.getTimeMs())
    }

    private fun log(message: () -> String) {
        if (LOG_ENABLED) {
            println("[${this.identityString}] ${message()}")
        }
    }

    companion object {
        private const val LOG_ENABLED = false
    }
}
