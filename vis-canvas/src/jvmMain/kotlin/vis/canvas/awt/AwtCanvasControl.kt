/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas.awt

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.Asyncs
import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.vis.canvas.AnimationProvider
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.datalore.vis.canvas.CanvasControl
import jetbrains.datalore.vis.canvas.EventPeer
import java.awt.Graphics2D
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JPanel


class AwtCanvasControl(
    private val myRoot: JPanel,
    override val size: Vector,
    private val myPixelRatio: Double,
    private val myEventPeer: EventPeer<MouseEventSpec, MouseEvent>
) : CanvasControl {
    override fun addChild(canvas: Canvas) {
        ImageIcon((canvas as AwtCanvas).image)
            .run(::JLabel)
            .run(myRoot::add)
    }

    override fun addChild(index: Int, canvas: Canvas) {
        TODO("Not yet implemented")
    }

    override fun removeChild(canvas: Canvas) {
        TODO("Not yet implemented")
    }

    override fun createAnimationTimer(eventHandler: AnimationProvider.AnimationEventHandler): AnimationProvider.AnimationTimer {
        TODO("Not yet implemented")
    }

    override fun createCanvas(size: Vector): Canvas {
        return AwtCanvas.create(size, myPixelRatio)
    }

    private fun imagePngBase64ToImage(dataUrl: String): Image {
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

        return Asyncs.constant(
            AwtCanvas.AwtSnapshot(dst)
        )
    }

    override fun addEventHandler(eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        TODO("Not yet implemented")
    }

    override fun <T> schedule(f: () -> T) {
        TODO("Not yet implemented")
    }
}