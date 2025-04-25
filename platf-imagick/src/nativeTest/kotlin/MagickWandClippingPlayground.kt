import ImageMagick.DrawingWand
import ImageMagick.MagickWand
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.convert
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

class MagickWandClippingPlayground {
    lateinit var img: CPointer<MagickWand>
    lateinit var wand: CPointer<DrawingWand>
    var outFile: String? = null
    var saveFile = false
    val forceSaveFile = true

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
        if (forceSaveFile || saveFile) {
            check(outFile != null) { "outFile is null" }
            ImageMagick.MagickWriteImage(img, outFile)
        }
        saveFile = false

        ImageMagick.DestroyMagickWand(img)
        ImageMagick.DestroyDrawingWand(wand)
        ImageMagick.MagickWandTerminus()
    }

    @Test
    fun clipRestore() {
        outFile = "magickwand_clip_restore.bmp"

        val clipPathId = "clip_42"

        run {
            val wand = ImageMagick.NewDrawingWand() ?: error("Failed to create DrawingWand")
            ImageMagick.DrawSetFillColor(wand, alphaBlack)

            ImageMagick.PushDrawingWand(wand)
            ImageMagick.DrawPushDefs(wand)
            ImageMagick.DrawPushClipPath(wand, clipPathId)

            drawNWRect(wand)

            ImageMagick.DrawPopClipPath(wand)
            ImageMagick.DrawPopDefs(wand)

            ImageMagick.DrawSetClipPath(wand, clipPathId)
            ImageMagick.DrawCircle(wand, 50.0, 50.0, 100.0, 50.0)
            ImageMagick.PopDrawingWand(wand)

            ImageMagick.MagickDrawImage(img, wand)
            ImageMagick.DestroyDrawingWand(wand)
        }

        run {
            val wand = ImageMagick.NewDrawingWand() ?: error("Failed to create DrawingWand")
            ImageMagick.DrawSetFillColor(wand, alphaBlack)

            ImageMagick.PushDrawingWand(wand)
            ImageMagick.DrawCircle(wand, 50.0, 50.0, 100.0, 50.0)

            ImageMagick.PopDrawingWand(wand)
            ImageMagick.MagickDrawImage(img, wand)
            ImageMagick.DestroyDrawingWand(wand)
        }
    }

    @Test
    fun clipSimple() {
        outFile = "magickwand_clip_simple.bmp"
        val clipPathId = "clip_42"

        run {
            ImageMagick.DrawPushDefs(wand)
            ImageMagick.DrawPushClipPath(wand, clipPathId)
            ImageMagick.PushDrawingWand(wand)

            drawNWRect(wand)

            ImageMagick.PopDrawingWand(wand)

            ImageMagick.DrawPopClipPath(wand)
            ImageMagick.DrawPopDefs(wand)
        }

        ImageMagick.DrawSetClipPath(wand, clipPathId)
        ImageMagick.DrawCircle(wand, 50.0, 50.0, 100.0, 50.0)
        ImageMagick.DrawSetFillColor(wand, black)
        ImageMagick.MagickDrawImage(img, wand)
    }

    @Test
    fun clipText() {
        outFile = "magickwand_clip_text.bmp"

        val clipPathId = "clip_42"

        defineClipPath(wand, clipPathId) {
            drawNWRect(wand)
        }

        ImageMagick.DrawSetClipPath(wand, clipPathId)

        ImageMagick.DrawSetFontSize(wand, 50.0)
        ImageMagick.DrawSetFontFamily(wand, "Times New Roman")
        ImageMagick.DrawSetFillColor(wand, black)

        drawAnnotation(wand, 0.0, 47.0, "Test")

        ImageMagick.MagickDrawImage(img, wand)
    }


    @Test
    fun clipTextBeforeTransform() {
        outFile = "magickwand_clip_text_before_transform.bmp"

        val clipPathId = "clip_42"

        defineClipPath(wand, clipPathId) {
            drawNWRect(wand)
        }
        ImageMagick.DrawSetStrokeColor(wand, black)
        ImageMagick.DrawSetFillColor(wand, none)
        drawNWRect(wand)

        ImageMagick.DrawSetClipPath(wand, clipPathId)

        drawAffine(wand, ry = -0.33, tx = 25)

        ImageMagick.DrawSetStrokeColor(wand, none)
        ImageMagick.DrawSetFillColor(wand, black)
        ImageMagick.DrawSetFontSize(wand, 50.0)
        ImageMagick.DrawSetFontFamily(wand, "Times New Roman")
        drawAnnotation(wand, 0.0, 47.0, "Test")

        ImageMagick.MagickDrawImage(img, wand)
    }


    @Test
    fun clipTextAfterTransform() {
        outFile = "magickwand_clip_text_after_transform.bmp"

        val clipPathId = "clip_42"

        drawAffine(wand, ry = -0.33, tx = 25)

        defineClipPath(wand, clipPathId) {
            drawNWRect(wand)
        }

        ImageMagick.DrawSetClipPath(wand, clipPathId)

        ImageMagick.DrawSetStrokeColor(wand, black)
        ImageMagick.DrawSetFillColor(wand, none)
        drawNWRect(wand)

        ImageMagick.DrawSetStrokeColor(wand, none)
        ImageMagick.DrawSetFillColor(wand, black)
        ImageMagick.DrawSetFontSize(wand, 50.0)
        ImageMagick.DrawSetFontFamily(wand, "Times New Roman")
        drawAnnotation(wand, 0.0, 47.0, "Test")

        ImageMagick.MagickDrawImage(img, wand)
    }

    private fun drawNWRect(ctx: CPointer<DrawingWand>, startPath: Boolean = true, endPath: Boolean = true) {
        if (startPath) {
            ImageMagick.DrawPathStart(ctx)
        }
        ImageMagick.DrawPathMoveToAbsolute(ctx, 0.0, 0.0)
        ImageMagick.DrawPathLineToAbsolute(ctx, 50.0, 0.0)
        ImageMagick.DrawPathLineToAbsolute(ctx, 50.0, 50.0)
        ImageMagick.DrawPathLineToAbsolute(ctx, 0.0, 50.0)

        if (endPath) {
            ImageMagick.DrawPathClose(ctx)
            ImageMagick.DrawPathFinish(ctx)
        }
    }
}
