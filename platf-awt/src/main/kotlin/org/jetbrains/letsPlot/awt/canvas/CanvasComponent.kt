/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.canvas

import org.jetbrains.letsPlot.commons.SystemTime
import org.jetbrains.letsPlot.commons.event.MouseEventSource
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
    private val animationTimer = Timer(1000 / 60) { onTimer() }
    private val mouseEventSource: MouseEventSource = AwtMouseEventMapper(this)
    private val systemTime: SystemTime = SystemTime()

    private var isContentAttached = false
    private var contentReg: Registration = Registration.EMPTY
    private var disposables = CompositeRegistration()

    internal var canvasPeer: AwtCanvasPeer = AwtCanvasPeer(fontManager = FontManager.DEFAULT)
        set(value) {
            check(!isContentAttached) { "Can't change canvasPeer after figure is attached" }
            field = value
        }

    var content: CanvasDrawable? = content
        set(content) {
            if (field == content) {
                return
            }

            field = content

            detachContent()
            attachContent()
        }

    init {
        isOpaque = false
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
        detachContent()

        disposables.dispose()
        disposables = CompositeRegistration()
    }

    override fun addNotify() {
        super.addNotify()
        attachContent()
    }

    override fun removeNotify() {
        super.removeNotify()
        detachContent()
    }

    private fun onTimer() {
        content?.onFrame(systemTime.getTimeMs())
    }

    private fun detachContent() {
        if (isContentAttached) {
            isContentAttached = false
            contentReg.dispose()
            contentReg = Registration.EMPTY
        }
    }

    private fun attachContent() {
        if (!isContentAttached) {
            isContentAttached = true

            val content = content
            if (content == null) {
                contentReg = Registration.EMPTY
            } else {
                animationTimer.start()
                content.resize(width, height)
                contentReg = CompositeRegistration(
                    content.mouseEventPeer.addEventSource(mouseEventSource),
                    content.mapToCanvas(canvasPeer),
                    content.onRepaintRequested(::repaint),
                    Registration.onRemove(animationTimer::stop),
                )
            }
        }
    }
}
