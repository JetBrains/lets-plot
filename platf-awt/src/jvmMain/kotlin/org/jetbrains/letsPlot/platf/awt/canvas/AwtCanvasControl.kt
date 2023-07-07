/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.awt.canvas

import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.Asyncs
import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.event.handler
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.vis.canvas.AnimationProvider.AnimationEventHandler
import jetbrains.datalore.vis.canvas.AnimationProvider.AnimationTimer
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.datalore.vis.canvas.CanvasControl
import jetbrains.datalore.vis.canvas.EventPeer
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*
import javax.imageio.ImageIO
import javax.swing.JComponent


class AwtCanvasControl(
    override val size: Vector,
    private val myEventPeer: EventPeer<MouseEventSpec, MouseEvent>,
    private val myAnimationTimerPeer: AwtAnimationTimerPeer,
    private val myPixelRatio: Double = 1.0
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

    override fun createAnimationTimer(eventHandler: AnimationEventHandler): AnimationTimer {
        return object : AnimationTimer {
            override fun start() {
                myAnimationTimerPeer.addHandler(::handle)
            }

            override fun stop() {
                myAnimationTimerPeer.removeHandler(::handle)
            }

            fun handle(millisTime: Long) {
                if (eventHandler.onEvent(millisTime)) {
                    myComponent.repaint()
                }
            }
        }
    }

    override fun createCanvas(size: Vector): Canvas {
        return AwtCanvas.create(size, myPixelRatio)
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

    override fun createSnapshot(dataUrl: String): Async<Canvas.Snapshot> {
        return Asyncs.constant(
            AwtCanvas.AwtSnapshot(imagePngBase64ToImage(dataUrl))
        )
    }

    override fun createSnapshot(bytes: ByteArray, size: Vector): Async<Canvas.Snapshot> {
        val src = ImageIO.read(ByteArrayInputStream(bytes))
        val dst = BufferedImage(size.x, size.y, BufferedImage.TYPE_INT_ARGB)
        val graphics2D = dst.createGraphics() as Graphics2D
        graphics2D.drawImage(src, 0, 0, size.x, size.y, null)
        graphics2D.dispose()

        return Asyncs.constant(
            AwtCanvas.AwtSnapshot(dst)
        )
    }

    override fun addEventHandler(eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        return myEventPeer.addEventHandler(
            eventSpec,
            handler {
                eventHandler.onEvent(it)
            }
        )
    }

    override fun <T> schedule(f: () -> T) {
//        invokeLater { f() }
        myAnimationTimerPeer.executor { f() }
    }
}