import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvas
import org.jetbrains.letsPlot.imagick.canvas.MagickContext2d
import kotlin.test.Test

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */


class ContextImageTest {
    private val imageComparer = ImageComparer()
    private val w = 100.0
    private val h = 100.0

    private val strokeColor = "#000000"
    private val fillColor = "#000000"
    private val filledStrokeColor = "#000080"
    private val strokedFillColor = "#FFC000"

    fun createCanvas(width: Number = w, height: Number = h, pixelDensity: Double = 1.0): Pair<MagickCanvas, MagickContext2d> {
        val canvas = MagickCanvas.create(width = width, height = height, pixelDensity = pixelDensity)
        val context2d = canvas.context2d as MagickContext2d
        return canvas to context2d
    }

    @Test
    fun scaledCanvas() {
        val (canvas, ctx) = createCanvas(100, 100, pixelDensity = 2.0)
        ctx.fillStyle = Color.BLACK
        ctx.fillRect(12.5, 12.5, 25.0, 25.0)

        imageComparer.assertImageEquals("scaled_canvas.bmp", canvas.img!!)
    }

    @Test
    fun simpleDrawImage() {
        val (tempCanvas, tempCtx) = createCanvas()
        tempCtx.fillStyle = Color.BLACK
        tempCtx.fillRect(25, 25, 50, 50)

        tempCanvas.img

        val snapshot = tempCanvas.immidiateSnapshot()

        val (canvas, ctx) = createCanvas()
        ctx.drawImage(snapshot)

        imageComparer.assertImageEquals("draw_image_simple.bmp", canvas.img!!)
    }

}