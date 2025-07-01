
import org.jetbrains.letsPlot.commons.values.Color
import kotlin.test.Test

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

class MagickCanvasDrawImageTest {
    private val imageComparer = imageComparer()

    @Test
    fun scaledCanvas() {
        val (canvas, ctx) = createCanvas(100, 100, pixelDensity = 2.0)
        ctx.fillStyle = Color.BLACK
        ctx.fillRect(12.5, 12.5, 25.0, 25.0)

        assertCanvas("scaled_canvas.bmp", canvas)
    }

    @Test
    fun drawImage_Simple() {
        val (tempCanvas, tempCtx) = createCanvas()
        tempCtx.fillStyle = Color.BLACK
        tempCtx.fillRect(25, 25, 50, 50)

        val snapshot = tempCanvas.takeSnapshot()

        val (canvas, ctx) = createCanvas()
        ctx.drawImage(snapshot)

        assertCanvas("draw_image_simple.bmp", canvas)
    }

    @Test
    fun drawImage_Transformed() {
        val (rectCanvas, rectCtx) = createCanvas(20, 20)
        rectCtx.fillStyle = Color.BLUE
        rectCtx.fillRect(0, 0, 20, 20)

        val rectSnapshot = rectCanvas.takeSnapshot()

        val (canvas, ctx) = createCanvas()
        ctx.save()
        ctx.translate(15.0, 15.0)
        ctx.drawImage(rectSnapshot)
        ctx.restore()

        ctx.save()
        ctx.translate(65, 15)
        ctx.transform(sx=1, ry=0, rx=0.33, sy=1, tx=0, ty=0)
        ctx.drawImage(rectSnapshot)
        ctx.restore()

        ctx.save()
        ctx.translate(15.0, 65.0)
        ctx.fillStyle = Color.BLACK
        ctx.fillRect(0, 0, 20, 20)
        ctx.restore()

        ctx.save()
        ctx.translate(65, 65)
        ctx.transform(sx=1, ry=0, rx=0.33, sy=1, tx=0, ty=0)
        ctx.fillStyle = Color.BLACK
        ctx.fillRect(0, 0, 20, 20)
        ctx.restore()

        assertCanvas("draw_image_transformed.bmp", canvas)
    }

    @Test
    fun drawImage_Overlay() {
        val (rect, rectCtx) = createCanvas(50, 50)
        rectCtx.fillStyle = "rgba(0, 0, 255, 0.7)" // semi-transparent blue
        rectCtx.fillRect(0, 0, 50, 50)

        val rectSnapshot = rect.takeSnapshot()

        val (canvas, ctx) = createCanvas()
        ctx.strokeStyle = "rgba(255, 0, 0, 0.5)"
        ctx.lineWidth = 20.0
        ctx.save()

        ctx.strokeStyle = "rgba(0, 255, 0, 1.0)" // green stroke
        ctx.lineWidth = 15.0
        ctx.beginPath()
        ctx.moveTo(50, 0)
        ctx.lineTo(50, 100)
        ctx.stroke()

        ctx.save()
        ctx.translate(25.0, 25.0)
        ctx.drawImage(rectSnapshot)
        ctx.restore()

        ctx.restore() // red stroke with line width 20.0
        ctx.beginPath()
        ctx.moveTo(0, 50)
        ctx.lineTo(100, 50)
        ctx.stroke()

        assertCanvas("draw_image_overlay.bmp", canvas)
    }

    @Test
    fun drawImage_Pixelated() {
        val (img, imgCtx) = createCanvas(3, 2)
        imgCtx.fillStyle = "rgb(255, 0, 0)"
        imgCtx.fillRect(0, 0, 1, 1)

        imgCtx.fillStyle = "rgb(0, 255, 0)"
        imgCtx.fillRect(1, 0, 1, 1)

        imgCtx.fillStyle = "rgb(0, 0, 255)"
        imgCtx.fillRect(2, 0, 1, 1)

        imgCtx.fillStyle = "rgba(255, 0, 0, 0.5)"
        imgCtx.fillRect(0, 1, 1, 1)

        imgCtx.fillStyle = "rgba(0, 255, 0, 0.5)"
        imgCtx.fillRect(1, 1, 1, 1)

        imgCtx.fillStyle = "rgba(0, 0, 255, 0.5)"
        imgCtx.fillRect(2, 1, 1, 1)

        val snapshot = img.takeSnapshot()

        val (canvas, ctx) = createCanvas()
        ctx.drawImage(snapshot, x = 5.0, y = 20.0, dw = 90.0, dh = 60.0)

        assertCanvas("draw_image_pixelated.bmp", canvas)
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