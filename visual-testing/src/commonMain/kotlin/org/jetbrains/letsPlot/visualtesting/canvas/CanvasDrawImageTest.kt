@file:Suppress("FunctionName")

package org.jetbrains.letsPlot.visualtesting.canvas

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.*
import org.jetbrains.letsPlot.visualtesting.ImageComparer

internal class CanvasDrawImageTest(
    override val canvasPeer: CanvasPeer,
    override val imageComparer: ImageComparer,
): CanvasTestBase() {
    init {
        registerTest(::drawImage_Simple)
        registerTest(::drawImage_Transformed)
        registerTest(::drawImage_Overlay)
        registerTest(::drawImage_Pixelated)
        registerTest(::drawImage_snapshotSeries)
        registerTest(::drawImage_srcToDstMatchingSizes)
        registerTest(::drawImage_cropFromSourceStretchToDest)
        registerTest(::drawImage_cropFromSourceToDest)
    }

    private fun drawImage_cropFromSourceStretchToDest() {
        val (tempCanvas, tempCtx) = createCanvas()
        tempCtx.fillStyle = Color.GREEN
        tempCtx.fillRect(x = 0, y = 0, width = 100, height = 100)

        tempCtx.fillStyle = Color.BLACK
        tempCtx.fillRect(x = 25, y = 25, width = 50, height = 50)

        val snapshot100x100 = tempCanvas.takeSnapshot()

        val (canvas, ctx) = createCanvas()
        ctx.drawImage(
            snapshot = snapshot100x100,
            sx = 20.0, sy = 20.0, sw = 60.0, sh = 60.0, // crop the central 60x60 part (black square with green border)
            dx = 5.0, dy = 5.0, dw = 90.0, dh = 90.0 // draw it stretched
        )

        assertCanvas("draw_image_crop_from_source_stretch_to_dest.png", canvas)
    }

    private fun drawImage_cropFromSourceToDest() {
        val (tempCanvas, tempCtx) = createCanvas()
        tempCtx.fillStyle = Color.GREEN
        tempCtx.fillRect(x = 0, y = 0, width = 100, height = 100)

        tempCtx.fillStyle = Color.BLACK
        tempCtx.fillRect(x = 25, y = 25, width = 50, height = 50)

        val snapshot = tempCanvas.takeSnapshot()

        val (canvas, ctx) = createCanvas()
        ctx.drawImage(
            snapshot = snapshot,
            sx = 20.0, sy = 20.0, sw = 60.0, sh = 60.0, // crop the central 60x60 part (black square with green border)
            dx = 5.0, dy = 5.0, dw = 60.0, dh = 60.0 // draw it without stretching
        )

        assertCanvas("draw_image_crop_from_source_to_dest.png", canvas)
    }

    private fun drawImage_srcToDstMatchingSizes() {
        val (tempCanvas, tempCtx) = createCanvas()
        tempCtx.fillStyle = Color.BLACK
        tempCtx.fillRect(25, 25, 50, 50)

        val snapshot = tempCanvas.takeSnapshot()

        val (canvas, ctx) = createCanvas()
        ctx.drawImage(snapshot, 0.0, 0.0, 100.0, 100.0, 0.0, 0.0, 100.0, 100.0)

        assertCanvas("draw_image_src_to_dst_matching_sizes.png", canvas)
    }

    private fun drawImage_Simple() {
        val (tempCanvas, tempCtx) = createCanvas()
        tempCtx.fillStyle = Color.BLACK
        tempCtx.fillRect(25, 25, 50, 50)

        val snapshot = tempCanvas.takeSnapshot()

        val (canvas, ctx) = createCanvas()
        ctx.drawImage(snapshot)

        assertCanvas("draw_image_simple.png", canvas)
    }

    private fun drawImage_snapshotSeries() {
        val (tempCanvas, tempCtx) = createCanvas()
        tempCtx.fillStyle = Color.BLACK.changeAlpha(0.5)
        tempCtx.fillRect(0, 0, 50, 50)

        tempCanvas.takeSnapshot()

        tempCtx.fillStyle = Color.RED.changeAlpha(0.5)
        tempCtx.fillRect(25, 25, 50, 50)

        tempCanvas.takeSnapshot()

        tempCtx.fillStyle = Color.BLUE.changeAlpha(0.5)
        tempCtx.fillRect(50, 50, 50, 50)
        val snapshot = tempCanvas.takeSnapshot()

        val (canvas, ctx) = createCanvas()
        ctx.drawImage(snapshot)
        ctx.clearRect(DoubleRectangle.WH(100, 100))
        ctx.drawImage(snapshot)
        assertCanvas("draw_image_snapshot_series.png", canvas)
    }

    private fun drawImage_Transformed() {
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

        assertCanvas("draw_image_transformed.png", canvas)
    }

    private fun drawImage_Overlay() {
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

        assertCanvas("draw_image_overlay.png", canvas)
    }

    private fun drawImage_Pixelated() {
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

        assertCanvas("draw_image_pixelated.png", canvas)
    }

}