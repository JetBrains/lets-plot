import ImageMagick.DrawGetVectorGraphics
import ImageMagick.DrawingWand
import ImageMagick.MagickWand
import kotlinx.cinterop.*
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.io.Native
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
            field = value?.let { "build/reports/$it" }
        }
    private var saveFile = true

    @BeforeTest
    fun setUp() {
        ImageMagick.MagickWandGenesis()
        img = ImageMagick.NewMagickWand() ?: error("Failed to create MagickWand")

        val w = 100
        val h = 100

        ImageMagick.MagickNewImage(img, w.convert(), h.convert(), none)
        wand = ImageMagick.NewDrawingWand() ?: error("Failed to create DrawingWand")
        outFile = null
    }

    @AfterTest
    fun tearDown() {
        if (saveFile) {
            check(outFile != null) { "outFile is null" }
            ImageMagick.MagickWriteImage(img, outFile)
            println("Image saved to: file://${Native.getCurrentDir()}/$outFile")
        }
        saveFile = false

        ImageMagick.DestroyMagickWand(img)
        ImageMagick.DestroyDrawingWand(wand)
        ImageMagick.MagickWandTerminus()
    }


    @Test
    fun simple() {
        outFile = "magickwand_simple.bmp"

        ImageMagick.DrawSetFillColor(wand, black)
        ImageMagick.DrawRectangle(wand, 10.0, 10.0, 90.0, 90.0)

        ImageMagick.DrawSetFillColor(wand, white)
        ImageMagick.DrawCircle(wand, 50.0, 50.0, 100.0, 50.0)

        // Draw text with a font name
        ImageMagick.DrawSetFillColor(wand, black)
        ImageMagick.DrawSetFont(wand, notoSerifRegularFontPath) // Use font name
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

    @Test
    fun textStroke() {
        saveFile = true
        outFile = "magickwand_text_stroke.bmp"
        val fontSize = 28.0

        ImageMagick.DrawSetFillColor(wand, black)
        ImageMagick.DrawSetStrokeColor(wand, none)
        ImageMagick.DrawSetFont(wand, notoSerifRegularFontPath) // Use font name
        ImageMagick.DrawSetFontSize(wand, fontSize) // Set font size

        //ImageMagick.DrawSetStrokeWidth(wand, 2.0)

        memScoped {
            ImageMagick.DrawAnnotation(wand, 5.0, fontSize, "Stroke 0.0".cstr.ptr.reinterpret())
        }

        // Draw text with stroke
        ImageMagick.DrawSetStrokeColor(wand, black)

        ImageMagick.DrawSetStrokeWidth(wand, 1.0) // Set stroke width
        memScoped {
            ImageMagick.DrawAnnotation(wand, 5.0, fontSize * 2, "Stroke 1.0".cstr.ptr.reinterpret())
        }

        ImageMagick.DrawSetStrokeWidth(wand, 0.1) // Set stroke width
        memScoped {
            ImageMagick.DrawAnnotation(wand, 5.0, fontSize * 3, "Stroke 0.1".cstr.ptr.reinterpret())
        }

        // Apply the drawing to the MagickWand
        ImageMagick.MagickDrawImage(img, wand)
    }

    @Test
    fun transparentFill_NoOp() {
        // Output as PNG to ensure we see transparency behavior correctly
        outFile = "magickwand_transparent_fill_noop.bmp"

        val transparent = newPixelWand("WandPlayground.transparentFill_NoOp.transparent")
        ImageMagick.PixelSetColor(transparent, "rgba(255,255,0,0)")

        wand = ImageMagick.NewDrawingWand() ?: error("Failed to create DrawingWand")
        ImageMagick.DrawSetFillColor(wand, transparent)
        ImageMagick.DrawRectangle(wand, 25.0, 25.0, 75.0, 75.0)

        println(DrawGetVectorGraphics(wand)?.toKString() ?: "MagicWand: MVG is null")

        ImageMagick.MagickDrawImage(img, wand)

        destroyPixelWand(transparent)
    }

    @Test
    fun transparentClear_HolePunch() {
        outFile = "magickwand_transparent_hole.bmp"

        val blue = newPixelWand("WandPlayground.transparentClear_HolePunch.blue")
        ImageMagick.PixelSetColor(blue, "blue")

        // 1. Draw solid Blue background
        ImageMagick.DrawSetFillColor(wand, blue)
        ImageMagick.DrawSetStrokeColor(wand, none)
        ImageMagick.DrawRectangle(wand, 0.0, 0.0, 100.0, 100.0)

        // Render the blue box first so we have pixels to erase
        ImageMagick.MagickDrawImage(img, wand)

        // 2. "Erase" the center using Composite Operator (CopyCompositeOp)
        // We need a source wand that is transparent
        val eraserSource = newMagickWand("WandPlayground.transparentClear_HolePunch.eraserSource")
        val alphaBg = newPixelWand("WandPlayground.transparentClear_HolePunch.alphaBg")
        ImageMagick.PixelSetColor(alphaBg, "none")

        // Create a small 1x1 transparent image to use as the "eraser brush"
        ImageMagick.MagickNewImage(eraserSource, 1u, 1u, alphaBg)

        // Punch the hole
        // Note: DrawComposite uses x, y, width, height
        ImageMagick.DrawComposite(
            wand,
            ImageMagick.CompositeOperator.CopyCompositeOp,
            25.0, 25.0, 50.0, 50.0,
            eraserSource
        )

        // Render the hole
        ImageMagick.MagickDrawImage(img, wand)

        destroyPixelWand(blue)
        destroyPixelWand(alphaBg)
        destroyMagickWand(eraserSource)
    }
}

//<drawing-wand><clip-path/><clip-units>Undefined</clip-units><decorate>None</decorate><encoding/><fill>#000000FF</fill><fill-opacity>1</fill-opacity><fill-rule>NonZero</fill-rule><font/><font-family/><font-size>12</font-size><font-stretch>Undefined</font-stretch><font-style>Undefined</font-style><font-weight>0</font-weight><gravity>Undefined</gravity><stroke>#000000</stroke><stroke-antialias>1</stroke-antialias><stroke-dasharray/><stroke-dashoffset>0</stroke-dashoffset><stroke-linecap>Butt</stroke-linecap><stroke-linejoin>Miter</stroke-linejoin><stroke-miterlimit>10</stroke-miterlimit><stroke-opacity>1</stroke-opacity><stroke-width>1</stroke-width><text-align>Undefined</text-align><text-antialias>1</text-antialias><text-undercolor>#000000</text-undercolor><vector-graphics>fill-rule 'NonZero'
//<drawing-wand><clip-path/><clip-units>Undefined</clip-units><decorate>None</decorate><encoding/><fill>#FFFF0000</fill><fill-opacity>0</fill-opacity><fill-rule>Evenodd</fill-rule><font/><font-family/><font-size>12</font-size><font-stretch>Undefined</font-stretch><font-style>Undefined</font-style><font-weight>0</font-weight><gravity>Undefined</gravity><stroke>#FFFFFF00</stroke><stroke-antialias>1</stroke-antialias><stroke-dasharray/><stroke-dashoffset>0</stroke-dashoffset><stroke-linecap>Butt</stroke-linecap><stroke-linejoin>Miter</stroke-linejoin><stroke-miterlimit>10</stroke-miterlimit><stroke-opacity>0</stroke-opacity><stroke-width>1</stroke-width><text-align>Undefined</text-align><text-antialias>1</text-antialias><text-undercolor>#000000</text-undercolor><vector-graphics>fill '#FFFF0000'