
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvas.Font
import kotlin.math.PI
import kotlin.test.Test

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

class CanvasClipTest : CanvasTestBase() {

    private fun nwRect(ctx: Context2d) {
        ctx.moveTo(0, 0)
        ctx.lineTo(50, 0)
        ctx.lineTo(50, 50)
        ctx.lineTo(0, 50)
    }

    private fun nwRectWithTranslates(ctx: Context2d) {
        ctx.save()
        ctx.moveTo(0, 0)
        ctx.translate(50, 0)
        ctx.lineTo(0, 0)
        ctx.translate(0, 50)
        ctx.lineTo(0, 0)
        ctx.translate(-50, 0)
        ctx.lineTo(0, 0)
        ctx.restore()
    }

    private fun seRect(ctx: Context2d) {
        ctx.moveTo(50, 50)
        ctx.lineTo(100, 50)
        ctx.lineTo(100, 100)
        ctx.lineTo(50, 100)
    }

    @Test
    fun clip_and_fill() {
        // Clip and fill without rebuilding a path
        val (canvas, ctx) = createCanvas()

        ctx.fillStyle = "black"
        ctx.beginPath()
        nwRect(ctx)
        ctx.closePath()
        ctx.clip()

        ctx.fill()
        
        assertCanvas("clip_and_fill.png", canvas)
    }


    @Test
    fun clip_path() {
        val (canvas, ctx) = createCanvas()

        ctx.fillStyle = "black"
        ctx.beginPath()
        nwRect(ctx)
        ctx.closePath()
        ctx.clip()

        ctx.beginPath()
        ctx.arc(50, 50, 50, 0, 2 * PI)
        ctx.fill()
        assertCanvas("clip_path.png", canvas)
    }

    @Test
    fun clip_with_two_polygons() {
        val (canvas, ctx) = createCanvas()

        ctx.fillStyle = "black"

        // NW clip rect
        ctx.beginPath()
        nwRect(ctx)

        // SE clip rect
        seRect(ctx)
        ctx.closePath()
        ctx.clip()

        ctx.beginPath()
        ctx.arc(50, 50, 50, 0, 2 * PI)
        ctx.fill()
        assertCanvas("clip_with_two_polygons.png", canvas)
    }

    @Test
    fun clip_restore() {
        val (canvas, ctx) = createCanvas()

        ctx.fillStyle = "rgba(0, 0, 0, 0.5)"
        ctx.save()

        // NW clip rect
        ctx.beginPath()
        nwRect(ctx)
        ctx.closePath()
        ctx.clip()

        // Draws NW sector of the circle
        ctx.beginPath()
        ctx.arc(50, 50, 50, 0, 2 * PI)
        ctx.fill()

        // disables clipping
        ctx.restore()

        // Draw whole circle - NW sector should be darker because alpha and double fill
        ctx.beginPath()
        ctx.arc(50, 50, 50, 0, 2 * PI)
        ctx.fill()

        assertCanvas("clip_restore.png", canvas)
    }

    @Test
    fun clip_after_transform() {
        val (canvas, ctx) = createCanvas()

        ctx.fillStyle = "black"
        ctx.transform(sx = 1, ry = 0, rx = -0.33, sy = 1, tx = 25, ty = 0)

        ctx.beginPath()
        nwRect(ctx)
        ctx.closePath()
        ctx.clip()

        ctx.beginPath()
        ctx.arc(50, 50, 50, 0, 2 * PI)
        ctx.fill()

        assertCanvas("clip_after_transform.png", canvas)
    }

    @Test
    fun clip_before_transform() {
        val (canvas, ctx) = createCanvas()

        ctx.fillStyle = "black"

        ctx.beginPath()
        nwRect(ctx)
        ctx.closePath()
        ctx.clip()

        ctx.transform(sx = 1, ry = 0, rx = -0.33, sy = 1, tx = 25, ty = 0)

        ctx.beginPath()
        ctx.arc(50, 50, 50, 0, 2 * PI)
        ctx.fill()

        assertCanvas("clip_before_transform.png", canvas)
    }

    @Test
    fun clip_text() {
        val (canvas, ctx) = createCanvas()

        ctx.fillStyle = "black"
        ctx.strokeStyle = "black"
        ctx.setFont(Font(fontFamily = "serif", fontSize = 50.0))

        ctx.beginPath()
        nwRect(ctx)
        ctx.closePath()
        ctx.clip()

        ctx.fillText("Test", 0.0, 47.0)

        assertCanvas("clip_text.png", canvas)
    }

    @Test
    fun clip_text_before_transform() {
        val (canvas, ctx) = createCanvas()

        ctx.fillStyle = "black"
        ctx.strokeStyle = "black"
        ctx.setFont(Font(fontFamily = "serif", fontSize = 50.0))

        ctx.beginPath()
        nwRect(ctx)
        ctx.closePath()
        ctx.clip()

        ctx.transform(sx = 1, ry = 0, rx = -0.33, sy = 1, tx = 25, ty = 0)

        ctx.stroke()
        ctx.fillText("Test", 0.0, 47.0)

        assertCanvas("clip_text_before_transform.png", canvas)
    }


    @Test
    fun clip_text_with_translates_before_transform() {
        val (canvas, ctx) = createCanvas()

        ctx.fillStyle = "black"
        ctx.strokeStyle = "black"
        ctx.setFont(Font(fontFamily = "serif", fontSize = 50.0))

        ctx.beginPath()
        nwRectWithTranslates(ctx)
        ctx.closePath()
        ctx.clip()

        ctx.transform(sx = 1, ry = 0, rx = -0.33, sy = 1, tx = 25, ty = 0)

        ctx.stroke()
        ctx.fillText("Test", 0.0, 47.0)

        assertCanvas("clip_text_with_translates_before_transform.png", canvas)
    }


    @Test
    fun clip_text_after_transform() {
        val (canvas, ctx) = createCanvas()

        ctx.fillStyle = "black"
        ctx.strokeStyle = "black"
        ctx.setFont(Font(fontFamily = "serif", fontSize = 50.0))

        ctx.transform(sx = 1, ry = 0, rx = -0.33, sy = 1, tx = 25, ty = 0)

        ctx.beginPath()
        nwRect(ctx)
        ctx.closePath()
        ctx.clip()

        ctx.stroke()
        ctx.fillText("Test", 0.0, 47.0)

        assertCanvas("clip_text_after_transform.png", canvas)
    }


    @Test
    fun clip_text_with_translates_after_transform() {
        val (canvas, ctx) = createCanvas()

        ctx.fillStyle = "black"
        ctx.strokeStyle = "black"
        ctx.setFont(Font(fontFamily = "serif", fontSize = 50.0))

        ctx.transform(sx = 1, ry = 0, rx = -0.33, sy = 1, tx = 25, ty = 0)

        ctx.beginPath()
        nwRectWithTranslates(ctx)
        ctx.closePath()
        ctx.clip()

        ctx.stroke()
        ctx.fillText("Test", 0.0, 47.0)

        assertCanvas("clip_text_with_translates_after_transform.png", canvas)
    }

}