package org.jetbrains.letsPlot.nat.util.canvas


import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.canvas.AnimationProvider
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.CanvasControl
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.ImageInfo

/*
 * Copyright (c) 2025 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

class SkiaCanvasControl(
    override val size: Vector,
) : CanvasControl {

    override fun addChild(index: Int, canvas: Canvas) {
        println("addChild() - not implemented")
    }

    override fun addChild(canvas: Canvas) {
        println("addChild() - not implemented")
    }

    override fun removeChild(canvas: Canvas) {
        println("removeChild() - not implemented")
    }

    override fun createAnimationTimer(eventHandler: AnimationProvider.AnimationEventHandler): AnimationProvider.AnimationTimer {
        return object : AnimationProvider.AnimationTimer {
            override fun start() {}
            override fun stop() {}
        }
    }

    override fun createCanvas(size: Vector): SkiaCanvas {
        return SkiaCanvas(
            SkCanvas(
                Bitmap().apply {
                    setImageInfo(
                        ImageInfo(
                            width = size.x.toInt(),
                            height = size.y.toInt(),
                            colorType = ColorType.RGBA_8888,
                            alphaType = ColorAlphaType.UNPREMUL,
                        )
                    )
                    allocPixels()
                }

            ),
            size
        )
    }

    override fun createSnapshot(
        bytes: ByteArray,
        size: Vector
    ): Async<Canvas.Snapshot> {
        TODO("Not yet implemented")
    }

    override fun createSnapshot(dataUrl: String): Async<Canvas.Snapshot> {
        TODO("Not yet implemented")
    }

    override fun addEventHandler(
        eventSpec: MouseEventSpec,
        eventHandler: EventHandler<MouseEvent>
    ): Registration {
        TODO("Not yet implemented")
    }

    override fun <T> schedule(f: () -> T) {
        TODO("Not yet implemented")
    }
}