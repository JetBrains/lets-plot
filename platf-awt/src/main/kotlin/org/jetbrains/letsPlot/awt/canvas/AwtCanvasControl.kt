/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.canvas

import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSource
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.Asyncs
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.canvas.AnimationProvider.AnimationEventHandler
import org.jetbrains.letsPlot.core.canvas.AnimationProvider.AnimationTimer
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.CanvasControl
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*
import javax.imageio.ImageIO
import javax.swing.JComponent

// TODO: remove this class and use CanvasPane instead
class AwtCanvasControl(
    override val size: Vector,
    private val animationTimerPeer: AwtAnimationTimerPeer,
    private val mouseEventSource: MouseEventSource,
    override val pixelDensity: Double = 1.0
) : CanvasControl {

    private val myComponent = CanvasContainerPanel(size)
    private val myMappedCanvases = HashMap<Canvas, JComponent>()

    fun component(): JComponent = myComponent

    override fun addChild(canvas: Canvas) {
        addChild(myComponent.componentCount, canvas)
    }

    override fun addChild(index: Int, canvas: Canvas) {
        val canvasComponent = CanvasComponent(canvas as AwtCanvas)
        myComponent.add(canvasComponent, myComponent.componentCount - index)
        myComponent.revalidate()
        myMappedCanvases[canvas] = canvasComponent
    }

    override fun removeChild(canvas: Canvas) {
        myComponent.remove(myMappedCanvases[canvas])
        myComponent.revalidate()
        myMappedCanvases.remove(canvas)
    }

    override fun onResize(listener: (Vector) -> Unit): Registration {
        TODO() // never happens. LiveMap (the only user of this class) uses another approach to map canvas to component
        // See CanvasPane for proper implementation
    }

    override fun snapshot(): Canvas.Snapshot {
        TODO("Not yet implemented")
    }

    override fun createAnimationTimer(eventHandler: AnimationEventHandler): AnimationTimer {
        return object : AnimationTimer {
            override fun start() {
                animationTimerPeer.addHandler(::handle)
            }

            override fun stop() {
                animationTimerPeer.removeHandler(::handle)
            }

            fun handle(millisTime: Long) {
                if (eventHandler.onEvent(millisTime)) {
                    myComponent.repaint()
                }
            }
        }
    }

    override fun createCanvas(size: Vector): Canvas {
        return AwtCanvas.create(size, pixelDensity)
    }

    private fun imagePngBase64ToImage(dataUrl: String): BufferedImage {
        val mediaType = "data:image/png;base64,"
        val imageString = dataUrl.replace(mediaType, "")

        val bytes = imageString.toByteArray(StandardCharsets.UTF_8)
        val byteArrayInputStream = ByteArrayInputStream(bytes)

        try {
            return Base64.getDecoder().wrap(byteArrayInputStream).let(ImageIO::read)
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
    }

    override fun decodeDataImageUrl(dataUrl: String): Async<Canvas.Snapshot> {
        return Asyncs.constant(
            AwtCanvas.AwtSnapshot(imagePngBase64ToImage(dataUrl))
        )
    }

    override fun decodePng(png: ByteArray, size: Vector): Async<Canvas.Snapshot> {
        val src = ImageIO.read(ByteArrayInputStream(png))
        val dst = BufferedImage(size.x, size.y, BufferedImage.TYPE_INT_ARGB)
        val graphics2D = dst.createGraphics() as Graphics2D
        graphics2D.drawImage(src, 0, 0, size.x, size.y, null)
        graphics2D.dispose()
        val snapshot = AwtCanvas.AwtSnapshot(dst)
        return Asyncs.constant(snapshot)
    }

    override fun addEventHandler(eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        return mouseEventSource.addEventHandler(eventSpec, eventHandler)
    }

    override fun <T> schedule(f: () -> T) {
//        invokeLater { f() }
        animationTimerPeer.executor { f() }
    }
}