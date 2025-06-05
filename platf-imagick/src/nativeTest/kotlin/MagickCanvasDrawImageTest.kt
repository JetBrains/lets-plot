import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvas
import org.jetbrains.letsPlot.imagick.canvas.MagickContext2d
import kotlin.test.Ignore
import kotlin.test.Test

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */


@Ignore
class MagickCanvasDrawImageTest {
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

        imageComparer.assertImageEquals("scaled_canvas.bmp", canvas.img)
    }

    @Test
    fun simpleDrawImage() {
        val (tempCanvas, tempCtx) = createCanvas()
        tempCtx.fillStyle = Color.BLACK
        tempCtx.fillRect(25, 25, 50, 50)

        tempCanvas.img

        val snapshot = tempCanvas.takeSnapshot()

        val (canvas, ctx) = createCanvas()
        ctx.drawImage(snapshot)

        imageComparer.assertImageEquals("draw_image_simple.bmp", canvas.img)
    }

    @Test
    fun drawImage_WithImageDataUrl() {
//        val (tempCanvas, tempCtx) = createCanvas()
//        tempCtx.fillStyle = Color.BLACK
//        // draw star
//        tempCtx.beginPath()
//        tempCtx.moveTo(50.0, 10.0)
//        tempCtx.lineTo(61.8, 35.4)
//        tempCtx.lineTo(90.0, 35.4)
//        tempCtx.lineTo(67.1, 57.6)
//        tempCtx.lineTo(79.5, 82.0)
//        tempCtx.lineTo(50.0, 65.0)
//        tempCtx.lineTo(20.5, 82.0)
//        tempCtx.lineTo(32.9, 57.6)
//        tempCtx.lineTo(10.0, 35.4)
//        tempCtx.lineTo(38.2, 35.4)
//        tempCtx.closePath()
//        tempCtx.fill()
//
//        val imageDataUrl = tempCanvas.takeSnapshot().toDataUrl()
//
//        println("ImageDataURL:\n$imageDataUrl")
//
//        val snapshot = MagickCanvasControl(100, 100, 1.0).immediateSnapshot(imageDataUrl)
//
//        val (canvas, ctx) = createCanvas()
//        ctx.drawImage(snapshot)
//
//        imageComparer.assertImageEquals("draw_image_image_data_url.bmp", canvas.img)
    }
}