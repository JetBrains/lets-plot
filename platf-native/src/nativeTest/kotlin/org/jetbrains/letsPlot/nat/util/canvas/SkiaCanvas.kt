package org.jetbrains.letsPlot.nat.util.canvas


import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.ImageInfo

/*
 * Copyright (c) 2025 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

class SkiaCanvas(
    private val canvas: SkCanvas,
    override val size: Vector
) : Canvas {
    override val context2d: SkiaContext2d = SkiaContext2d(canvas)
    override fun immidiateSnapshot(): Canvas.Snapshot {
        val bitmap = Bitmap().apply {
            setImageInfo(
                ImageInfo(
                    width = size.x,
                    height = size.y,
                    colorType = ColorType.RGBA_8888,
                    alphaType = ColorAlphaType.UNPREMUL,
                )
            )
            allocPixels()
        }

        val res = canvas.readPixels(bitmap, 0, 0)
        check(res) { "Failed to read pixels" }

        return SkSnapshot(bitmap)
    }

    override fun takeSnapshot(): Async<Canvas.Snapshot> {
        TODO("Not yet implemented")
    }
}

fun createBitmapCanvas(width: Int, height: Int): Canvas {
    val bitmap = Bitmap().apply {
        setImageInfo(
            ImageInfo(
                width = width,
                height = height,
                colorType = ColorType.RGBA_8888,
                alphaType = ColorAlphaType.UNPREMUL,
            )
        )
        allocPixels()
    }

    val skCanvas = SkiaCanvas(
        canvas = SkCanvas(bitmap),
        size = Vector(width, height)
    )

    return skCanvas
}

