/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:Suppress("FunctionName")

package org.jetbrains.letsPlot.visualtesting.canvas

import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.*
import org.jetbrains.letsPlot.visualtesting.ImageComparer
import org.jetbrains.letsPlot.visualtesting.ImageComparer.ComparisonProfile
import kotlin.math.PI


class CanvasClipTest(
    override val canvasPeer: CanvasPeer,
    override val imageComparer: ImageComparer,
): CanvasTestBase() {

    override val defaultComparisonProfile: ComparisonProfile = ComparisonProfile.Geometries

    init {
        registerTest(::canvas_clip_fill)
        registerTest(::canvas_clip_path)
        registerTest(::canva_clip_twoPolygons)
        registerTest(::canvas_clip_restore)
        registerTest(::canva_clip_afterTransform)
        registerTest(::canvas_clip_beforeTransform)
        registerTest(::canvas_clip_text, ComparisonProfile.Text)
        registerTest(::canvas_clip_textBeforeTransform, ComparisonProfile.Text)
        registerTest(::canvas_clip_textWithTranslatesBeforeTransform, ComparisonProfile.Text)
        registerTest(::canvas_clip_textAfterTransform, ComparisonProfile.Text)
        registerTest(::canvas_clip_textWithTranslatesAfterTransform, ComparisonProfile.Text)
    }

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

    fun canvas_clip_fill(): Bitmap {
        // Clip and fill without rebuilding a path
        val (canvas, ctx) = createCanvas()

        ctx.fillStyle = "black"
        ctx.beginPath()
        nwRect(ctx)
        ctx.closePath()
        ctx.clip()

        ctx.fill()

        return paint(canvas)
    }

    fun canvas_clip_path(): Bitmap {
        val (canvas, ctx) = createCanvas()

        ctx.fillStyle = "black"
        ctx.beginPath()
        nwRect(ctx)
        ctx.closePath()
        ctx.clip()

        ctx.beginPath()
        ctx.arc(50, 50, 50, 0, 2 * PI)
        ctx.fill()

        return paint(canvas)
    }

    fun canva_clip_twoPolygons(): Bitmap {
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

        return paint(canvas)
    }

    fun canvas_clip_restore(): Bitmap {
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

        return paint(canvas)
    }

    fun canva_clip_afterTransform(): Bitmap {
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

        return paint(canvas)
    }

    fun canvas_clip_beforeTransform(): Bitmap {
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

        return paint(canvas)
    }

    fun canvas_clip_text(): Bitmap {
        val (canvas, ctx) = createCanvas()

        ctx.fillStyle = "black"
        ctx.strokeStyle = "black"
        ctx.setFont(Font(fontFamily = "Noto Serif", fontSize = 50.0))

        ctx.beginPath()
        nwRect(ctx)
        ctx.closePath()
        ctx.clip()

        ctx.fillText("Test", 0.0, 47.0)

        return paint(canvas)
    }

    fun canvas_clip_textBeforeTransform(): Bitmap {
        val (canvas, ctx) = createCanvas()

        ctx.fillStyle = "black"
        ctx.strokeStyle = "black"
        ctx.setFont(Font(fontFamily = "Noto Serif", fontSize = 50.0))

        ctx.beginPath()
        nwRect(ctx)
        ctx.closePath()
        ctx.clip()

        ctx.transform(sx = 1, ry = 0, rx = -0.33, sy = 1, tx = 25, ty = 0)

        ctx.stroke()
        ctx.fillText("Test", 0.0, 47.0)

        return paint(canvas)
    }


    fun canvas_clip_textWithTranslatesBeforeTransform(): Bitmap {
        val (canvas, ctx) = createCanvas()

        ctx.fillStyle = "black"
        ctx.strokeStyle = "black"
        ctx.setFont(Font(fontFamily = "Noto Serif", fontSize = 50.0))

        ctx.beginPath()
        nwRectWithTranslates(ctx)
        ctx.closePath()
        ctx.clip()

        ctx.transform(sx = 1, ry = 0, rx = -0.33, sy = 1, tx = 25, ty = 0)

        ctx.stroke()
        ctx.fillText("Test", 0.0, 47.0)

        return paint(canvas)
    }

    fun canvas_clip_textAfterTransform(): Bitmap {
        val (canvas, ctx) = createCanvas()

        ctx.fillStyle = "black"
        ctx.strokeStyle = "black"
        ctx.setFont(Font(fontFamily = "Noto Serif", fontSize = 50.0))

        ctx.transform(sx = 1, ry = 0, rx = -0.33, sy = 1, tx = 25, ty = 0)

        ctx.beginPath()
        nwRect(ctx)
        ctx.closePath()
        ctx.clip()

        ctx.stroke()
        ctx.fillText("Test", 0.0, 47.0)

        return paint(canvas)
    }


    fun canvas_clip_textWithTranslatesAfterTransform(): Bitmap {
        val (canvas, ctx) = createCanvas()

        ctx.fillStyle = "black"
        ctx.strokeStyle = "black"
        ctx.setFont(Font(fontFamily = "Noto Serif", fontSize = 50.0))

        ctx.transform(sx = 1, ry = 0, rx = -0.33, sy = 1, tx = 25, ty = 0)

        ctx.beginPath()
        nwRectWithTranslates(ctx)
        ctx.closePath()
        ctx.clip()

        ctx.stroke()
        ctx.fillText("Test", 0.0, 47.0)

        return paint(canvas)
    }
}
