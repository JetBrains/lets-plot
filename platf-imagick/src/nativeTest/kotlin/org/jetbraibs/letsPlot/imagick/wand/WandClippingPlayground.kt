package org.jetbraibs.letsPlot.imagick.wand

import ImageMagick.*
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.convert
import org.jetbraibs.letsPlot.imagick.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

class WandClippingPlayground {
    private lateinit var img: CPointer<MagickWand>
    private lateinit var wand: CPointer<DrawingWand>
    private var outFile: String? = null
        set(value) {
            field = value?.let { "build/reports/actual-images/$it" }
        }

    private var saveFile = false
    private val forceSaveFile = true

    @BeforeTest
    fun setUp() {
        MagickWandGenesis()
        img = NewMagickWand() ?: error("Failed to create MagickWand")

        val w = 100
        val h = 100

        MagickNewImage(img, w.convert(), h.convert(), white)
        wand = NewDrawingWand() ?: error("Failed to create DrawingWand")
        outFile = null
    }

    @AfterTest
    fun tearDown() {
        if (forceSaveFile || saveFile) {
            check(outFile != null) { "outFile is null" }
            MagickWriteImage(img, outFile)
        }
        saveFile = false

        DestroyMagickWand(img)
        DestroyDrawingWand(wand)
        MagickWandTerminus()
    }

    @Test
    fun clipRestore() {
        outFile = "magickwand_clip_restore.bmp"

        val clipPathId = "clip_42"

        run {
            val wand = NewDrawingWand() ?: error("Failed to create DrawingWand")
            DrawSetFillColor(wand, alphaBlack)

            PushDrawingWand(wand)
            DrawPushDefs(wand)
            DrawPushClipPath(wand, clipPathId)

            drawNWRect(wand)

            DrawPopClipPath(wand)
            DrawPopDefs(wand)

            DrawSetClipPath(wand, clipPathId)
            DrawCircle(wand, 50.0, 50.0, 100.0, 50.0)
            PopDrawingWand(wand)

            MagickDrawImage(img, wand)
            DestroyDrawingWand(wand)
        }

        run {
            val wand = NewDrawingWand() ?: error("Failed to create DrawingWand")
            DrawSetFillColor(wand, alphaBlack)

            PushDrawingWand(wand)
            DrawCircle(wand, 50.0, 50.0, 100.0, 50.0)

            PopDrawingWand(wand)
            MagickDrawImage(img, wand)
            DestroyDrawingWand(wand)
        }
    }

    @Test
    fun clipSimple() {
        outFile = "magickwand_clip_simple.bmp"
        val clipPathId = "clip_42"

        run {
            DrawPushDefs(wand)
            DrawPushClipPath(wand, clipPathId)
            PushDrawingWand(wand)

            drawNWRect(wand)

            PopDrawingWand(wand)

            DrawPopClipPath(wand)
            DrawPopDefs(wand)
        }

        DrawSetClipPath(wand, clipPathId)
        DrawCircle(wand, 50.0, 50.0, 100.0, 50.0)
        DrawSetFillColor(wand, black)
        MagickDrawImage(img, wand)
    }

    @Test
    fun clipText() {
        outFile = "magickwand_clip_text.bmp"

        val clipPathId = "clip_42"

        defineClipPath(wand, clipPathId) {
            drawNWRect(wand)
        }

        DrawSetClipPath(wand, clipPathId)

        DrawSetFontSize(wand, 50.0)
        DrawSetFont(wand, notoSerifRegularFontPath)
        DrawSetFillColor(wand, black)

        drawAnnotation(wand, 0.0, 47.0, "Test")

        MagickDrawImage(img, wand)
    }


    @Test
    fun clipTextBeforeTransform() {
        outFile = "magickwand_clip_text_before_transform.bmp"

        val clipPathId = "clip_42"

        defineClipPath(wand, clipPathId) {
            drawNWRect(wand)
        }
        DrawSetStrokeColor(wand, black)
        DrawSetFillColor(wand, none)
        drawNWRect(wand)

        DrawSetClipPath(wand, clipPathId)

        drawAffine(wand, ry = -0.33, tx = 25)

        DrawSetStrokeColor(wand, none)
        DrawSetFillColor(wand, black)
        DrawSetFontSize(wand, 50.0)
        DrawSetFont(wand, notoSerifRegularFontPath)
        drawAnnotation(wand, 0.0, 47.0, "Test")

        MagickDrawImage(img, wand)
    }


    @Test
    fun clipTextAfterTransform() {
        outFile = "magickwand_clip_text_after_transform.bmp"

        val clipPathId = "clip_42"

        drawAffine(wand, ry = -0.33, tx = 25)

        defineClipPath(wand, clipPathId) {
            drawNWRect(wand)
        }

        DrawSetClipPath(wand, clipPathId)

        DrawSetStrokeColor(wand, black)
        DrawSetFillColor(wand, none)
        drawNWRect(wand)

        DrawSetStrokeColor(wand, none)
        DrawSetFillColor(wand, black)
        DrawSetFontSize(wand, 50.0)
        DrawSetFont(wand, notoSerifRegularFontPath)
        drawAnnotation(wand, 0.0, 47.0, "Test")

        MagickDrawImage(img, wand)
    }

    private fun drawNWRect(ctx: CPointer<DrawingWand>, startPath: Boolean = true, endPath: Boolean = true) {
        if (startPath) {
            DrawPathStart(ctx)
        }
        DrawPathMoveToAbsolute(ctx, 0.0, 0.0)
        DrawPathLineToAbsolute(ctx, 50.0, 0.0)
        DrawPathLineToAbsolute(ctx, 50.0, 50.0)
        DrawPathLineToAbsolute(ctx, 0.0, 50.0)

        if (endPath) {
            DrawPathClose(ctx)
            DrawPathFinish(ctx)
        }
    }
}
