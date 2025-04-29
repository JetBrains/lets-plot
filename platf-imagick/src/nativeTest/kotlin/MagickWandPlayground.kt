import ImageMagick.DrawingWand
import ImageMagick.MagickWand
import kotlinx.cinterop.*
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

// This test class is used to demonstrate the usage of the ImageMagick library
class MagickWandPlayground {
    lateinit var img: CPointer<MagickWand>
    lateinit var wand: CPointer<DrawingWand>
    var outFile: String? = null
    var saveFile = false

    @BeforeTest
    fun setUp() {
        ImageMagick.MagickWandGenesis()
        img = ImageMagick.NewMagickWand() ?: error("Failed to create MagickWand")

        val w = 100
        val h = 100

        ImageMagick.MagickNewImage(img, w.convert(), h.convert(), white)
        wand = ImageMagick.NewDrawingWand() ?: error("Failed to create DrawingWand")
        outFile = null
    }

    @AfterTest
    fun tearDown() {
        if (saveFile) {
            check(outFile != null) { "outFile is null" }
            ImageMagick.MagickWriteImage(img, outFile)
        }
        saveFile = false

        ImageMagick.DestroyMagickWand(img)
        ImageMagick.DestroyDrawingWand(wand)
        ImageMagick.MagickWandTerminus()
    }


    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun simple() {
        outFile = "magickwand_simple.wand"

        ImageMagick.DrawSetFillColor(wand, black)
        ImageMagick.DrawRectangle(wand, 10.0, 10.0, 90.0, 90.0)

        ImageMagick.DrawSetFillColor(wand, white)
        ImageMagick.DrawCircle(wand, 50.0, 50.0, 100.0, 50.0)

        // Draw text with a font name
        ImageMagick.DrawSetFillColor(wand, black)
        ImageMagick.DrawSetFont(wand, "DejaVu-Sans-Bold") // Use font name
        ImageMagick.DrawSetFontSize(wand, 36.0) // Set font size

        drawAnnotation(wand, 150.0, 300.0, "Hello, MagicWand!")

        // Apply the drawing to the MagickWand
        ImageMagick.MagickDrawImage(img, wand)
    }

    @Test
    fun miterJoinWithOppositeSegments() {
        outFile = "magickwand_bug_miter_join.bmp"

        ImageMagick.DrawSetStrokeColor(wand, black)
        ImageMagick.DrawSetStrokeWidth(wand, 20.0)

        // MiterJoin with a line in the opposite direction
        ImageMagick.DrawSetStrokeLineJoin(wand, ImageMagick.LineJoin.MiterJoin)
        ImageMagick.DrawPathStart(wand)
        ImageMagick.DrawPathMoveToAbsolute(wand, 5.0, 50.0)
        ImageMagick.DrawPathLineToAbsolute(wand, 95.0, 50.0)
        ImageMagick.DrawPathLineToAbsolute(wand, 5.0, 50.0)
        ImageMagick.DrawPathFinish(wand)
        ImageMagick.MagickDrawImage(img, wand)
    }

    @Test
    fun affine_ry() {
        outFile = "magickwand_bug_affine_ry.bmp"

        ImageMagick.DrawSetFillColor(wand, black)
        ImageMagick.DrawRectangle(wand, 30.0, 30.0, 70.0, 70.0)

        drawAffine(wand, ry = 0.3420201241970062)

        ImageMagick.DrawSetFillColor(wand, green)
        ImageMagick.DrawSetFillOpacity(wand, 0.7)
        ImageMagick.DrawRectangle(wand, 30.0, 30.0, 70.0, 70.0)

        ImageMagick.MagickDrawImage(img, wand)
    }

    @Test
    fun simpleBezierCurve() {
        outFile = "magickwand_bezier.bmp"

        ImageMagick.DrawSetFillColor(wand, none)
        ImageMagick.DrawSetStrokeColor(wand, black)
        ImageMagick.DrawSetStrokeWidth(wand, 2.0)

        // Draw a simple Bezier curve
        memScoped {
            val bezierPoints = allocArray<ImageMagick.PointInfo>(3)
            bezierPoints[0].x = 0.0; bezierPoints[0].y = 0.0
            bezierPoints[1].x = 100.0; bezierPoints[1].y = 0.0
            bezierPoints[2].x = 100.0; bezierPoints[2].y = 100.0

            ImageMagick.DrawBezier(wand, 3.convert(), bezierPoints)
        }

        ImageMagick.MagickDrawImage(img, wand)
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

        ImageMagick.DrawPathStart(wand)
        ImageMagick.DrawPathMoveToAbsolute(wand, cps[0].x, cps[0].y)

        cps.drop(1)
            .windowed(size = 3, step = 3)
            .forEach { (cp1, cp2, cp3) ->
                ImageMagick.DrawPathCurveToAbsolute(wand, cp1.x, cp1.y, cp2.x, cp2.y, cp3.x, cp3.y)
            }

        ImageMagick.DrawPathFinish(wand)
        ImageMagick.MagickDrawImage(img, wand)
    }

    @Test
    fun curve() {
        outFile = "magickwand_curve.bmp"

        val cp0 = DoubleVector(50.0, 0.0)
        val cp1 = DoubleVector(-100.0, 50.0)
        val cp2 = DoubleVector(200.0, 50.0)
        val cp3 = DoubleVector(50.0, 100.0)

        ImageMagick.DrawSetFillColor(wand, none)
        ImageMagick.DrawSetStrokeColor(wand, black)
        ImageMagick.DrawSetStrokeWidth(wand, 2.0)

        ImageMagick.DrawPathStart(wand)
        ImageMagick.DrawPathMoveToAbsolute(wand, cp0.x, cp0.y)
        ImageMagick.DrawPathCurveToAbsolute(wand, cp1.x, cp1.y, cp2.x, cp2.y, cp3.x, cp3.y)
        ImageMagick.DrawPathFinish(wand)

        ImageMagick.MagickDrawImage(img, wand)
    }
}
