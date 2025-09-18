/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.canvas

import org.jetbrains.letsPlot.commons.encoding.Png
import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSource
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.Asyncs
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.commons.values.awt.BitmapUtil
import org.jetbrains.letsPlot.core.canvas.AnimationProvider.AnimationEventHandler
import org.jetbrains.letsPlot.core.canvas.AnimationProvider.AnimationTimer
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.CanvasControl
import org.jetbrains.letsPlot.core.canvasFigure.CanvasFigure
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO
import javax.swing.JComponent
import javax.swing.JLayeredPane

class CanvasPane(
    figure: CanvasFigure? = null,
    private val pixelDensity: Double = 1.0,
    private val disableAnimationTimer: Boolean = false
) : JLayeredPane() {
    private val canvasControl: CanvasControl = AwtCanvasControl()
    private var figureRegistration: Registration = Registration.EMPTY
    val mouseEventSource: MouseEventSource = AwtMouseEventMapper(this)

    var figure: CanvasFigure? = null
        set(value) {
            if (field == value) {
                return
            }

            figureRegistration.remove()
            if (value != null) {
                figureRegistration = value.mapToCanvas(canvasControl)
                bounds = Rectangle(0, 0, value.bounds().get().dimension.x, value.bounds().get().dimension.y)
            }
            field = value
        }

    init {
        this.figure = figure
    }


    override fun isPaintingOrigin(): Boolean = true

    internal inner class AwtCanvasControl : CanvasControl {
        override val pixelDensity: Double
            get() = this@CanvasPane.pixelDensity

        override val size: Vector
            get() = Vector(
                x = this@CanvasPane.width,
                y = this@CanvasPane.height
            )

        private val animationTimerPeer: AwtAnimationTimerPeer = AwtAnimationTimerPeer()
        private val myMappedCanvases = HashMap<Canvas, JComponent>()

        override fun addChild(canvas: Canvas) {
            addChild(componentCount, canvas)
        }

        override fun addChild(index: Int, canvas: Canvas) {
            val canvasComponent = CanvasComponent(canvas as AwtCanvas)
            add(canvasComponent, componentCount - index)
            revalidate()
            myMappedCanvases[canvas] = canvasComponent
        }

        override fun removeChild(canvas: Canvas) {
            remove(myMappedCanvases[canvas])
            revalidate()
            myMappedCanvases.remove(canvas)
        }

        override fun onResize(listener: (Vector) -> Unit): Registration {
            val sizeListener = object : ComponentAdapter() {
                override fun componentResized(e: ComponentEvent?) {
                    listener(size)
                }
            }

            addComponentListener(sizeListener)

            return object : Registration() {
                override fun doRemove() {
                    this@CanvasPane.removeComponentListener(sizeListener)
                }
            }
        }

        override fun snapshot(): Canvas.Snapshot {
            TODO("Not yet implemented")
        }

        override fun createAnimationTimer(eventHandler: AnimationEventHandler): AnimationTimer {
            if (disableAnimationTimer) {
                // Timer is not needed - return a dummy implementation.
                return object : AnimationTimer {
                    override fun start() {}
                    override fun stop() {}
                }
            }

            return object : AnimationTimer {
                override fun start() {
                    animationTimerPeer.addHandler(::handle)
                }

                override fun stop() {
                    animationTimerPeer.removeHandler(::handle)
                }

                fun handle(millisTime: Long) {
                    if (eventHandler.onEvent(millisTime)) {
                        repaint()
                    }
                }
            }
        }

        override fun createCanvas(size: Vector): Canvas {
            return AwtCanvas.create(size, pixelDensity)
        }

        override fun createSnapshot(bitmap: Bitmap): Canvas.Snapshot {
            val img = BitmapUtil.toBufferedImage(bitmap)
            return AwtCanvas.AwtSnapshot(img)
        }

        private fun imagePngBase64ToImage(dataUrl: String): BufferedImage {
            val img = Png.decodeDataImage(dataUrl)
            val bufImg = BitmapUtil.toBufferedImage(img)
            return bufImg
        }

        override fun decodeDataImageUrl(dataUrl: String): Async<Canvas.Snapshot> {
            println("CanvasPane.CanvasControl.createSnapshot(dataUrl): dataUrl.size = ${dataUrl.length}")
            return Asyncs.constant(
                AwtCanvas.AwtSnapshot(imagePngBase64ToImage(dataUrl))
            )
        }

        override fun decodePng(png: ByteArray): Async<Canvas.Snapshot> {
            val src = ImageIO.read(ByteArrayInputStream(png))
            val snapshot = AwtCanvas.AwtSnapshot(src)
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

    companion object {
        fun paint(figure: CanvasFigure, pixelDensity: Double, graphics: Graphics2D) {
            val canvasPane = CanvasPane(figure, pixelDensity, disableAnimationTimer = true)
            val plotComponentSize = figure.bounds().get().dimension
            canvasPane.bounds = Rectangle(0, 0, plotComponentSize.x, plotComponentSize.y)
            canvasPane.doLayout()
            canvasPane.paint(graphics)
        }
    }
}
