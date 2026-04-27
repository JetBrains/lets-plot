/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:Suppress("FunctionName")

package org.jetbrains.letsPlot.visualtesting.canvas

import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.core.canvas.Font
import org.jetbrains.letsPlot.core.canvas.FontStyle
import org.jetbrains.letsPlot.core.canvas.FontWeight
import org.jetbrains.letsPlot.visualtesting.ImageComparer
import org.jetbrains.letsPlot.visualtesting.ImageComparer.ComparisonProfile

class CanvasTextTest(
    override val canvasPeer: CanvasPeer,
    override val imageComparer: ImageComparer,
): CanvasTestBase() {

    override val defaultComparisonProfile: ComparisonProfile = ComparisonProfile.Text

    init {
        registerTest(::canvas_path_monospace)
        registerTest(::canvas_path_monospaceItalic)
        registerTest(::canvas_path_monospaceBold)
        registerTest(::canvas_path_monospaceBoldItalic)
        registerTest(::canvas_text_transparent)
    }

    fun canvas_path_monospace(): Bitmap {
        // Test that regular monospaced font is rendered correctly
        val (canvas, ctx) = createCanvas()

        ctx.fillStyle = "black"
        ctx.setFont(Font(fontFamily = "Noto Sans Mono", fontSize = 16.0))
        ctx.fillText("monospace", 5.0, 30.0)

        return paint(canvas)
    }

    fun canvas_path_monospaceItalic(): Bitmap {
        val fontSize = 16.0

        val (canvas, ctx) = createCanvas()

        ctx.fillStyle = "black"
        ctx.setFont(Font(fontFamily = "Noto Sans Mono", fontSize = fontSize, fontStyle = FontStyle.ITALIC))
        ctx.fillText("monospace", 5.0, 30.0)

        ctx.translate(5.0, 70.0)
        ctx.fillText("monospace", 0.0, 0.0)

        return paint(canvas)
    }

    fun canvas_path_monospaceBold(): Bitmap {
        val fontSize = 16.0

        val (canvas, ctx) = createCanvas()

        ctx.fillStyle = "black"
        ctx.setFont(Font(fontFamily = "Noto Sans Mono", fontSize = fontSize, fontWeight = FontWeight.BOLD))
        ctx.fillText("monospace", 5.0, 30.0)

        return paint(canvas)
    }

    fun canvas_path_monospaceBoldItalic(): Bitmap {
        val fontSize = 16.0
        val (canvas, ctx) = createCanvas()

        ctx.fillStyle = "black"
        ctx.setFont(Font(
            fontStyle = FontStyle.ITALIC,
            fontWeight = FontWeight.BOLD,
            fontSize = fontSize,
            fontFamily = "Noto Sans Mono"
        ))
        ctx.fillText("monospace", 0.0, 30.0)

        return paint(canvas)
    }

    fun canvas_text_transparent(): Bitmap {
        val (canvas, ctx) = createCanvas()

        ctx.fillStyle = Color.TRANSPARENT
        ctx.fillText("transparent", 5.0, 30.0)

        return paint(canvas)
    }
}
