import ImageMagick.DrawingWand
import kotlinx.cinterop.*
import kotlin.test.Test

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

class MagickWandClippingSandbox {
    @Test
    fun restoreAfterClipPath() {
        val outFile = "restore_after_clip_path.bmp"

        fun drawTriangle(wand: CPointer<DrawingWand>) {
            ImageMagick.DrawPathStart(wand)
            ImageMagick.DrawPathMoveToAbsolute(wand, 50.0, 10.0)
            ImageMagick.DrawPathLineToAbsolute(wand, 90.0, 90.0)
            ImageMagick.DrawPathLineToAbsolute(wand, 10.0, 90.0)
            ImageMagick.DrawPathClose(wand)
            ImageMagick.DrawPathFinish(wand)
        }

        ImageMagick.MagickWandGenesis()


        val img = ImageMagick.NewMagickWand() ?: error("Failed to create MagickWand")
        ImageMagick.MagickNewImage(img, 100.convert(), 100.convert(), white)

        val clipPathId = "clip_42"

        run {
            val wand = ImageMagick.NewDrawingWand() ?: error("Failed to create DrawingWand")
            ImageMagick.DrawSetFillColor(wand, alphaBlack)

            ImageMagick.PushDrawingWand(wand)
            ImageMagick.DrawPushDefs(wand)
            ImageMagick.DrawPushClipPath(wand, clipPathId)
            //ImageMagick.PushDrawingWand(wand)
            ImageMagick.DrawPathStart(wand)
            ImageMagick.DrawPathMoveToAbsolute(wand, 0.0, 0.0)
            ImageMagick.DrawPathLineToAbsolute(wand, 100.0, 0.0)
            ImageMagick.DrawPathLineToAbsolute(wand, 100.0, 50.0)
            ImageMagick.DrawPathLineToAbsolute(wand, 0.0, 50.0)
            ImageMagick.DrawPathClose(wand)
            ImageMagick.DrawPathFinish(wand)
            //ImageMagick.PopDrawingWand(wand)
            ImageMagick.DrawPopClipPath(wand)
            ImageMagick.DrawPopDefs(wand)

            ImageMagick.DrawSetClipPath(wand, clipPathId)
            drawTriangle(wand)
            ImageMagick.PopDrawingWand(wand)

            ImageMagick.MagickDrawImage(img, wand)
            ImageMagick.DestroyDrawingWand(wand)
        }


        run {
            val wand = ImageMagick.NewDrawingWand() ?: error("Failed to create DrawingWand")
            ImageMagick.DrawSetFillColor(wand, alphaBlack)

            ImageMagick.PushDrawingWand(wand)
            drawTriangle(wand)
            ImageMagick.PopDrawingWand(wand)
            ImageMagick.MagickDrawImage(img, wand)
            ImageMagick.DestroyDrawingWand(wand)
        }

        ImageMagick.MagickWriteImage(img, outFile)

        ImageMagick.DestroyMagickWand(img)
        ImageMagick.MagickWandTerminus()
    }

    @Test
    fun simpleClipPath() {
        val outFile = "simple_clip_path.bmp"

        memScoped {
            ImageMagick.MagickWandGenesis()
            val wand = ImageMagick.NewMagickWand() ?: error("Failed to create MagickWand")

            val w = 100
            val h = 100

            ImageMagick.MagickNewImage(wand, w.convert(), h.convert(), white)
            val drawingWand = ImageMagick.NewDrawingWand() ?: error("Failed to create DrawingWand")

            val clipPathId = "clip_42"

            run {
                ImageMagick.DrawPushDefs(drawingWand)
                ImageMagick.DrawPushClipPath(drawingWand, clipPathId)
                ImageMagick.PushDrawingWand(drawingWand)
                ImageMagick.DrawPathStart(drawingWand)
                ImageMagick.DrawPathMoveToAbsolute(drawingWand, 25.0, 25.0)
                ImageMagick.DrawPathLineToAbsolute(drawingWand, 75.0, 25.0)
                ImageMagick.DrawPathLineToAbsolute(drawingWand, 75.0, 75.0)
                ImageMagick.DrawPathLineToAbsolute(drawingWand, 25.0, 75.0)
                ImageMagick.DrawPathClose(drawingWand)
                ImageMagick.DrawPathFinish(drawingWand)
                ImageMagick.PopDrawingWand(drawingWand)

                ImageMagick.DrawPopClipPath(drawingWand)
                ImageMagick.DrawPopDefs(drawingWand)
            }

            ImageMagick.DrawSetClipPath(drawingWand, clipPathId)
            ImageMagick.DrawPathStart(drawingWand)
            ImageMagick.DrawPathMoveToAbsolute(drawingWand, 50.0, 10.0)
            ImageMagick.DrawPathLineToAbsolute(drawingWand, 90.0, 90.0)
            ImageMagick.DrawPathLineToAbsolute(drawingWand, 10.0, 90.0)
            ImageMagick.DrawPathClose(drawingWand)
            ImageMagick.DrawPathFinish(drawingWand)

            ImageMagick.DrawSetFillColor(drawingWand, black)
            ImageMagick.MagickDrawImage(wand, drawingWand)

            ImageMagick.MagickWriteImage(wand, outFile)

            ImageMagick.DestroyMagickWand(wand)
            ImageMagick.DestroyDrawingWand(drawingWand)
            ImageMagick.MagickWandTerminus()
        }
    }

    @Test
    fun clipPathTextWithTransform() {
        val outFile = "clip_path_text_with_transform.bmp"

        ImageMagick.MagickWandGenesis()
        val wand = ImageMagick.NewMagickWand() ?: error("Failed to create MagickWand")

        val w = 100
        val h = 100

        ImageMagick.MagickNewImage(wand, w.convert(), h.convert(), white)
        val drawingWand = ImageMagick.NewDrawingWand() ?: error("Failed to create DrawingWand")

        val clipPathId = "clip_42"
        memScoped {
            val m = alloc<ImageMagick.AffineMatrix>()
            m.sx = 1.0
            m.sy = 1.0
            m.rx = 0.0
            m.ry = -0.33
            m.tx = 0.0
            m.ty = 0.0
            ImageMagick.DrawAffine(drawingWand, m.ptr)
        }

        run {
            ImageMagick.DrawPushDefs(drawingWand)
            ImageMagick.DrawPushClipPath(drawingWand, clipPathId)
            ImageMagick.PushDrawingWand(drawingWand)
            ImageMagick.DrawPathStart(drawingWand)
            ImageMagick.DrawPathMoveToAbsolute(drawingWand, w * 0.5, h * 0.5)
            ImageMagick.DrawPathLineToAbsolute(drawingWand, w * 0.5, h * 0.5 - 30)
            ImageMagick.DrawPathLineToAbsolute(drawingWand, w * 0.5 + 35, h * 0.5 - 30)
            ImageMagick.DrawPathLineToAbsolute(drawingWand, w * 0.5 + 35, h * 0.5)
            ImageMagick.DrawPathClose(drawingWand)
            ImageMagick.DrawPathFinish(drawingWand)
            ImageMagick.PopDrawingWand(drawingWand)

            ImageMagick.DrawPopClipPath(drawingWand)
            ImageMagick.DrawPopDefs(drawingWand)
        }

        ImageMagick.DrawSetClipPath(drawingWand, clipPathId)


        ImageMagick.DrawSetStrokeColor(drawingWand, black)
        ImageMagick.DrawSetFillColor(drawingWand, none)

        ImageMagick.DrawPathStart(drawingWand)
        ImageMagick.DrawPathMoveToAbsolute(drawingWand, w * 0.5, h * 0.5)
        ImageMagick.DrawPathLineToAbsolute(drawingWand, w * 0.5, h * 0.5 - 30)
        ImageMagick.DrawPathLineToAbsolute(drawingWand, w * 0.5 + 35, h * 0.5 - 30)
        ImageMagick.DrawPathLineToAbsolute(drawingWand, w * 0.5 + 35, h * 0.5)
        ImageMagick.DrawPathLineToAbsolute(drawingWand, w * 0.5, h * 0.5)
        ImageMagick.DrawPathClose(drawingWand)
        ImageMagick.DrawPathFinish(drawingWand)

        memScoped {
            ImageMagick.DrawSetFontSize(drawingWand, 30.0)
            ImageMagick.DrawSetFontFamily(drawingWand, "Times New Roman")
            ImageMagick.DrawSetStrokeColor(drawingWand, black)
            ImageMagick.DrawSetFillColor(drawingWand, black)

            val textCStr = "Test".cstr.ptr.reinterpret<UByteVar>()
            ImageMagick.DrawAnnotation(drawingWand, w * 0.5, h * 0.5, textCStr)
        }

        ImageMagick.MagickDrawImage(wand, drawingWand)

        ImageMagick.MagickWriteImage(wand, outFile)

        ImageMagick.DestroyMagickWand(wand)
        ImageMagick.DestroyDrawingWand(drawingWand)
        ImageMagick.MagickWandTerminus()
    }

}