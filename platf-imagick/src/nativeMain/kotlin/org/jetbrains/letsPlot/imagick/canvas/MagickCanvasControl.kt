/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.imagick.canvas

import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.canvas.AnimationProvider
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.CanvasControl

class MagickCanvasControl(
    w: Int,
    h: Int,
    override val pixelDensity: Double,
) : CanvasControl {
    val children = mutableListOf<Canvas>()

    override val size: Vector
        get() = TODO("Not yet implemented")

    override fun addChild(canvas: Canvas) {
        children.add(canvas)
    }

    override fun addChild(index: Int, canvas: Canvas) {
        children.add(index, canvas)
    }

    override fun removeChild(canvas: Canvas) {
        children.remove(canvas)
    }

    override fun createAnimationTimer(eventHandler: AnimationProvider.AnimationEventHandler): AnimationProvider.AnimationTimer {
        return object : AnimationProvider.AnimationTimer {
            override fun start() {

            }

            override fun stop() {

            }
        }
    }

    override fun createCanvas(size: Vector): Canvas {
        return MagickCanvas.create(size, pixelDensity)
    }

    override fun createSnapshot(dataUrl: String): Async<Canvas.Snapshot> {
        TODO("Not yet implemented")
    }

    override fun createSnapshot(
        bytes: ByteArray,
        size: Vector
    ): Async<Canvas.Snapshot> {
        TODO("Not yet implemented")
    }

    override fun addEventHandler(
        eventSpec: MouseEventSpec,
        eventHandler: EventHandler<MouseEvent>
    ): Registration {
        println("MagickCanvasControl.addEventHandler: $eventSpec")
        return Registration.EMPTY
    }

    override fun <T> schedule(f: () -> T) {
        TODO("Not yet implemented")
    }
}
