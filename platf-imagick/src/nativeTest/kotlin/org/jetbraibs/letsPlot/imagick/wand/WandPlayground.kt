package org.jetbraibs.letsPlot.imagick.wand

import ImageMagick.*
import kotlinx.cinterop.*
import org.jetbraibs.letsPlot.imagick.*
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.io.NativeIO
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil.destroyMagickWand
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil.destroyPixelWand
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil.newMagickWand
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil.newPixelWand
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

// This test class is used to demonstrate the usage of the ImageMagick library
class WandPlayground {
    private lateinit var img: CPointer<MagickWand>
    private lateinit var wand: CPointer<DrawingWand>
    private var outFile: String? = null
        set(value) {
            field = value?.let { "build/reports/actual-images/$it" }
        }
    private var saveFile = true

    @BeforeTest
    fun setUp() {
        MagickWandGenesis()
        img = NewMagickWand() ?: error("Failed to create MagickWand")

        val w = 100
        val h = 100

        MagickNewImage(img, w.convert(), h.convert(), none)
        wand = NewDrawingWand() ?: error("Failed to create DrawingWand")
        outFile = null
    }

    @AfterTest
    fun tearDown() {
        if (saveFile) {
            check(outFile != null) { "outFile is null" }
            MagickWriteImage(img, outFile)
            println("Image saved to: file://${NativeIO.getCurrentDir()}/$outFile")
        }
        saveFile = false

        DestroyMagickWand(img)
        DestroyDrawingWand(wand)
        MagickWandTerminus()
    }


    @Test
    fun simple() {
        outFile = "magickwand_simple.bmp"

        DrawSetFillColor(wand, black)
        DrawRectangle(wand, 10.0, 10.0, 90.0, 90.0)

        DrawSetFillColor(wand, white)
        DrawCircle(wand, 50.0, 50.0, 100.0, 50.0)

        // Draw text with a font name
        DrawSetFillColor(wand, black)
        DrawSetFont(wand, notoSerifRegularFontPath) // Use font name
        DrawSetFontSize(wand, 36.0) // Set font size

        drawAnnotation(wand, 150.0, 300.0, "Hello, MagicWand!")

        // Apply the drawing to the MagickWand
        MagickDrawImage(img, wand)
    }

    @Test
    fun miterJoinWithOppositeSegments() {
        outFile = "magickwand_bug_miter_join.bmp"

        DrawSetStrokeColor(wand, black)
        DrawSetStrokeWidth(wand, 20.0)

        // MiterJoin with a line in the opposite direction
        DrawSetStrokeLineJoin(wand, LineJoin.MiterJoin)
        DrawPathStart(wand)
        DrawPathMoveToAbsolute(wand, 5.0, 50.0)
        DrawPathLineToAbsolute(wand, 95.0, 50.0)
        DrawPathLineToAbsolute(wand, 5.0, 50.0)
        DrawPathFinish(wand)
        MagickDrawImage(img, wand)
    }

    @Test
    fun affine_ry() {
        outFile = "magickwand_bug_affine_ry.bmp"

        DrawSetFillColor(wand, black)
        DrawRectangle(wand, 30.0, 30.0, 70.0, 70.0)

        drawAffine(wand, ry = 0.3420201241970062)

        DrawSetFillColor(wand, green)
        DrawSetFillOpacity(wand, 0.7)
        DrawRectangle(wand, 30.0, 30.0, 70.0, 70.0)

        MagickDrawImage(img, wand)
    }

    @Test
    fun simpleBezierCurve() {
        outFile = "magickwand_bezier.bmp"

        DrawSetFillColor(wand, none)
        DrawSetStrokeColor(wand, black)
        DrawSetStrokeWidth(wand, 2.0)

        // Draw a simple Bezier curve
        memScoped {
            val bezierPoints = allocArray<PointInfo>(3)
            bezierPoints[0].x = 0.0; bezierPoints[0].y = 0.0
            bezierPoints[1].x = 100.0; bezierPoints[1].y = 0.0
            bezierPoints[2].x = 100.0; bezierPoints[2].y = 100.0

            DrawBezier(wand, 3.convert(), bezierPoints)
        }

        MagickDrawImage(img, wand)
    }

    @Test
    fun circleWihCurve() {
        outFile = "magickwand_circle_with_curve.bmp"

        val cps = listOf(
            DoubleVector(100.0, 50.0),
            DoubleVector(100.0, 77.614237),
            DoubleVector(77.614237, 100.0),
            DoubleVector(50.0, 100.0),
            DoubleVector(22.38576250846033, 100.0),
            DoubleVector(0.0, 77.61423749153968),
            DoubleVector(0.0, 50.0),
            DoubleVector(0.0, 22.38576250846033),
            DoubleVector(22.38576250846033, 0.0),
            DoubleVector(50.0, 0.0),
            DoubleVector(77.61423749153968, 0.0),
            DoubleVector(100.0, 22.38576250846033),
            DoubleVector(100.0, 50.0)
        )

        DrawPathStart(wand)
        DrawPathMoveToAbsolute(wand, cps[0].x, cps[0].y)

        cps.drop(1)
            .windowed(size = 3, step = 3)
            .forEach { (cp1, cp2, cp3) ->
                DrawPathCurveToAbsolute(wand, cp1.x, cp1.y, cp2.x, cp2.y, cp3.x, cp3.y)
            }

        DrawPathFinish(wand)
        MagickDrawImage(img, wand)
    }

    @Test
    fun curve() {
        outFile = "magickwand_curve.bmp"

        val cp0 = DoubleVector(50.0, 0.0)
        val cp1 = DoubleVector(-100.0, 50.0)
        val cp2 = DoubleVector(200.0, 50.0)
        val cp3 = DoubleVector(50.0, 100.0)

        DrawSetFillColor(wand, none)
        DrawSetStrokeColor(wand, black)
        DrawSetStrokeWidth(wand, 2.0)

        DrawPathStart(wand)
        DrawPathMoveToAbsolute(wand, cp0.x, cp0.y)
        DrawPathCurveToAbsolute(wand, cp1.x, cp1.y, cp2.x, cp2.y, cp3.x, cp3.y)
        DrawPathFinish(wand)

        MagickDrawImage(img, wand)
    }

    @Test
    fun textStroke() {
        saveFile = true
        outFile = "magickwand_text_stroke.bmp"
        val fontSize = 28.0

        DrawSetFillColor(wand, black)
        DrawSetStrokeColor(wand, none)
        DrawSetFont(wand, notoSerifRegularFontPath) // Use font name
        DrawSetFontSize(wand, fontSize) // Set font size

        //ImageMagick.DrawSetStrokeWidth(wand, 2.0)

        memScoped {
            DrawAnnotation(wand, 5.0, fontSize, "Stroke 0.0".cstr.ptr.reinterpret())
        }

        // Draw text with stroke
        DrawSetStrokeColor(wand, black)

        DrawSetStrokeWidth(wand, 1.0) // Set stroke width
        memScoped {
            DrawAnnotation(wand, 5.0, fontSize * 2, "Stroke 1.0".cstr.ptr.reinterpret())
        }

        DrawSetStrokeWidth(wand, 0.1) // Set stroke width
        memScoped {
            DrawAnnotation(wand, 5.0, fontSize * 3, "Stroke 0.1".cstr.ptr.reinterpret())
        }

        // Apply the drawing to the MagickWand
        MagickDrawImage(img, wand)
    }

    @Test
    fun transparentFill_NoOp() {
        // Output as PNG to ensure we see transparency behavior correctly
        outFile = "magickwand_transparent_fill_noop.bmp"

        val transparent = newPixelWand("WandPlayground.transparentFill_NoOp.transparent")
        PixelSetColor(transparent, "rgba(255,255,0,0)")

        wand = NewDrawingWand() ?: error("Failed to create DrawingWand")
        DrawSetFillColor(wand, transparent)
        DrawRectangle(wand, 25.0, 25.0, 75.0, 75.0)

        println(DrawGetVectorGraphics(wand)?.toKString() ?: "MagicWand: MVG is null")

        MagickDrawImage(img, wand)

        destroyPixelWand(transparent)
    }

    @Test
    fun transparentClear_HolePunch() {
        outFile = "magickwand_transparent_hole.bmp"

        val blue = newPixelWand("WandPlayground.transparentClear_HolePunch.blue")
        PixelSetColor(blue, "blue")

        // 1. Draw solid Blue background
        DrawSetFillColor(wand, blue)
        DrawSetStrokeColor(wand, none)
        DrawRectangle(wand, 0.0, 0.0, 100.0, 100.0)

        // Render the blue box first so we have pixels to erase
        MagickDrawImage(img, wand)

        // 2. "Erase" the center using Composite Operator (CopyCompositeOp)
        // We need a source wand that is transparent
        val eraserSource = newMagickWand("WandPlayground.transparentClear_HolePunch.eraserSource")
        val alphaBg = newPixelWand("WandPlayground.transparentClear_HolePunch.alphaBg")
        PixelSetColor(alphaBg, "none")

        // Create a small 1x1 transparent image to use as the "eraser brush"
        MagickNewImage(eraserSource, 1u, 1u, alphaBg)

        // Punch the hole
        // Note: DrawComposite uses x, y, width, height
        DrawComposite(
            wand,
            CompositeOperator.CopyCompositeOp,
            25.0, 25.0, 50.0, 50.0,
            eraserSource
        )

        // Render the hole
        MagickDrawImage(img, wand)

        destroyPixelWand(blue)
        destroyPixelWand(alphaBg)
        destroyMagickWand(eraserSource)
    }
}
