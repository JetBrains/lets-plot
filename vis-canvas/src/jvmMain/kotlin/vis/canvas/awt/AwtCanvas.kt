/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas.awt

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.Asyncs
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.datalore.vis.canvas.ScaledCanvas
import java.awt.Graphics2D
import java.awt.Image
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_ARGB

internal class AwtCanvas
private constructor(
    val image: BufferedImage,
    size: Vector,
    pixelRatio: Double
) : ScaledCanvas(AwtContext2d(image.createGraphics() as Graphics2D), size, pixelRatio) {

    companion object {
        fun create(size: Vector, pixelRatio: Double): Canvas {
            return AwtCanvas(BufferedImage(size.x, size.y, TYPE_INT_ARGB), size, pixelRatio)
        }
    }

    override fun takeSnapshot(): Async<Canvas.Snapshot> {
        return Asyncs.constant(AwtSnapshot(image))
    }

    internal data class AwtSnapshot(val image: Image) : Canvas.Snapshot
}