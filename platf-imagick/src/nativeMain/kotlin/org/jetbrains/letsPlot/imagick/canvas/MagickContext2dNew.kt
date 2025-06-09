/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.imagick.canvas

import kotlinx.cinterop.*
import org.jetbrains.letsPlot.commons.geometry.AffineTransform
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.*
import org.jetbrains.letsPlot.core.canvas.Path2d.*

class MagickContext2dNew(
    private val img: CPointer<ImageMagick.MagickWand>?,
    pixelDensity: Double,
    private val stateDelegate: ContextStateDelegate = ContextStateDelegate(),
) : Context2d by stateDelegate {
    private val none = ImageMagick.NewPixelWand() ?: error { "Failed to create PixelWand" }
    private val pixelWand = ImageMagick.NewPixelWand() ?: error { "Failed to create PixelWand" }
    val wand = ImageMagick.NewDrawingWand() ?: error { "Failed to create DrawingWand" }

    init {
        stateDelegate.setStateChangeListener(::onStateChange)
        ImageMagick.PixelSetColor(none, "none")
        transform(wand, AffineTransform.makeScale(pixelDensity, pixelDensity))
    }

    private fun onStateChange(stateChange: ContextStateDelegate.StateChange) {
        stateChange.strokeColor?.let { strokeColor ->
            ImageMagick.PixelSetColor(pixelWand, strokeColor.toCssColor())
            ImageMagick.DrawSetStrokeColor(wand, pixelWand)
        }

        stateChange.strokeWidth?.let { strokeWidth ->
            ImageMagick.DrawSetStrokeWidth(wand, strokeWidth)
        }

        stateChange.lineDashPattern?.let { lineDash ->
            if (lineDash.isNotEmpty()) {
                memScoped {
                    val lineDashPatternSize = lineDash.size
                    val lineDashArray = allocArray<DoubleVar>(lineDashPatternSize) { i -> value = lineDash[i] }
                    ImageMagick.DrawSetStrokeDashArray(wand, lineDashPatternSize.toULong(), lineDashArray)
                }
            } else {
                ImageMagick.DrawSetStrokeDashArray(wand, 0u, null)
            }
        }

        stateChange.lineDashOffset?.let { lineDashOffset ->
            ImageMagick.DrawSetStrokeDashOffset(wand, lineDashOffset)
        }

        stateChange.miterLimit?.let { miterLimit ->
            ImageMagick.DrawSetStrokeMiterLimit(wand, miterLimit.toLong().convert())
        }

        stateChange.lineCap?.let { lineCap ->
            ImageMagick.DrawSetStrokeLineCap(wand, lineCap.convert())
        }

        stateChange.lineJoin?.let { lineJoin ->
            ImageMagick.DrawSetStrokeLineJoin(wand, lineJoin.convert())
        }

        stateChange.fillColor?.let { fillColor ->
            ImageMagick.PixelSetColor(pixelWand, fillColor.toCssColor())
            ImageMagick.DrawSetFillColor(wand, pixelWand)
        }

        stateChange.font?.let { font ->
            ImageMagick.DrawSetFontSize(wand, font.fontSize)
            ImageMagick.DrawSetFontFamily(wand, font.fontFamily)
            ImageMagick.DrawSetFontStyle(wand, font.fontStyle.convert())
            ImageMagick.DrawSetFontWeight(wand, font.fontWeight.convert())
        }

        stateChange.transform?.let { transform ->
            transform(wand, transform)
        }

        stateChange.globalAlpha?.let { globalAlpha ->
            ImageMagick.DrawSetFillOpacity(wand, globalAlpha)
            ImageMagick.DrawSetStrokeOpacity(wand, globalAlpha)
        }

        stateChange.clipPath?.let { clipPath ->
            val inverseCTMTransform = stateDelegate.getCTM().inverse()
            if (inverseCTMTransform == null) {
                log { "Magick2Context2d.onStateChange: clipPath ignored, CTM is degenerate." }
                return@let
            }
            val clipId = clipPath.hashCode().toUInt().toString(16)

            ImageMagick.DrawPushDefs(wand)
            ImageMagick.DrawPushClipPath(wand, clipId)

            // DrawAffine transforms clipPath, but a path already has the transform applied.
            // So we need to inversely transform it to prevent double transform.
            drawPath(wand, clipPath.getCommands(), inverseCTMTransform)

            ImageMagick.DrawPopClipPath(wand)
            ImageMagick.DrawPopDefs(wand)

            ImageMagick.DrawSetClipPath(wand, clipId)
        }

    }

    override fun drawImage(snapshot: Canvas.Snapshot) {
        val snap = snapshot as MagickCanvas.MagickSnapshot
        val srcWand = snap.img

        val success = ImageMagick.MagickCompositeImage(
            img,
            srcWand,
            ImageMagick.CompositeOperator.OverCompositeOp,
            ImageMagick.MagickTrue,
            0,
            0
        )

        ImageMagick.DestroyMagickWand(srcWand)

        if (success == ImageMagick.MagickFalse) {
            val err = ImageMagick.MagickGetException(img, null)
            throw RuntimeException("MagickCompositeImage failed: $err")
        }
    }

    override fun drawImage(snapshot: Canvas.Snapshot, x: Double, y: Double) {
        val snap = snapshot as MagickCanvas.MagickSnapshot
        val srcWand = snap.img

        val success = ImageMagick.MagickCompositeImage(
            img,
            srcWand,
            ImageMagick.CompositeOperator.OverCompositeOp,
            ImageMagick.MagickTrue,
            x.toLong().convert(),
            y.toLong().convert()
        )

        if (success == ImageMagick.MagickFalse) {
            val err = ImageMagick.MagickGetException(img, null)
            throw RuntimeException("MagickCompositeImage failed: $err")
        }
    }

    override fun drawImage(snapshot: Canvas.Snapshot, x: Double, y: Double, w: Double, h: Double) {
        val snap = snapshot as MagickCanvas.MagickSnapshot
        val srcWand = snap.img

        // Resize the source wand to desired width and height
        val successScale = ImageMagick.MagickScaleImage(
            srcWand,
            w.toULong(),
            h.toULong()
        )

        if (successScale == ImageMagick.MagickFalse) {
            ImageMagick.DestroyMagickWand(srcWand)
            val err = ImageMagick.MagickGetException(img, null)
            throw RuntimeException("MagickScaleImage failed: $err")
        }

        // Composite the resized image onto the base image
        val success = ImageMagick.MagickCompositeImage(
            img,
            srcWand,
            ImageMagick.CompositeOperator.OverCompositeOp,
            ImageMagick.MagickTrue,
            x.toULong().convert(),
            y.toULong().convert()
        )

        if (success == ImageMagick.MagickFalse) {
            val err = ImageMagick.MagickGetException(img, null)
            throw RuntimeException("MagickCompositeImage failed: $err")
        }
    }

    override fun save() {
        stateDelegate.save()
    }

    override fun restore() {
        stateDelegate.restore()
    }

    override fun setFillStyle(color: Color?) {
        stateDelegate.setFillStyle(color)
    }

    override fun setStrokeStyle(color: Color?) {
        stateDelegate.setStrokeStyle(color)
    }

    override fun setLineWidth(lineWidth: Double) {
        stateDelegate.setLineWidth(lineWidth)
    }

    override fun setLineDash(lineDash: DoubleArray) {
        stateDelegate.setLineDash(lineDash)
    }

    override fun setLineCap(lineCap: LineCap) {
        stateDelegate.setLineCap(lineCap)
    }

    override fun setLineJoin(lineJoin: LineJoin) {
        stateDelegate.setLineJoin(lineJoin)
    }

    override fun setStrokeMiterLimit(miterLimit: Double) {
        stateDelegate.setStrokeMiterLimit(miterLimit)
    }

    override fun setFont(f: Font) {
        stateDelegate.setFont(f)
    }

    override fun setGlobalAlpha(alpha: Double) {
        stateDelegate.setGlobalAlpha(alpha)
    }

    override fun stroke() {
        // Make ctm identity. null for degenerate case, e.g., scale(0, 0) - skip drawing.
        val inverseCtmTransform = stateDelegate.getCTM().inverse() ?: return

        withStrokeWand { strokeWand ->
            drawPath(strokeWand, stateDelegate.getCurrentPath(), inverseCtmTransform)
        }
    }

    override fun fill() {
        // Make ctm identity. null for degenerate case, e.g., scale(0, 0) - skip drawing.
        val inverseCtmTransform = stateDelegate.getCTM().inverse() ?: return

        withFillWand { fillWand ->
            drawPath(fillWand, stateDelegate.getCurrentPath(), inverseCtmTransform)
        }
    }

    override fun fillText(text: String, x: Double, y: Double) {
        withFillWand { fillWand ->
            memScoped {
                val textCStr = text.cstr.ptr.reinterpret<UByteVar>()
                ImageMagick.DrawAnnotation(fillWand, x, y, textCStr)
            }
        }
    }

    override fun strokeText(text: String, x: Double, y: Double) {
        withStrokeWand { strokeWand ->
            memScoped {
                val textCStr = text.cstr.ptr.reinterpret<UByteVar>()
                ImageMagick.DrawAnnotation(strokeWand, x, y, textCStr)
            }
        }
    }

    override fun fillRect(x: Double, y: Double, w: Double, h: Double) {
        withFillWand { fillWand ->
            ImageMagick.DrawRectangle(fillWand, x, y, x + w, y + h)
        }
    }

    override fun strokeRect(x: Double, y: Double, w: Double, h: Double) {
        withStrokeWand { strokeWand ->
            ImageMagick.DrawRectangle(strokeWand, x, y, x + w, y + h)
        }
    }

    override fun clip() {
        stateDelegate.clip()
    }

    override fun transform(sx: Double, ry: Double, rx: Double, sy: Double, tx: Double, ty: Double) {
        stateDelegate.transform(sx, ry, rx, sy, tx, ty)
    }

    override fun scale(xy: Double) {
        stateDelegate.scale(xy)
    }

    override fun scale(x: Double, y: Double) {
        stateDelegate.scale(x, y)
    }

    override fun rotate(angle: Double) {
        stateDelegate.rotate(angle)
    }

    override fun translate(x: Double, y: Double) {
        stateDelegate.translate(x, y)
    }

    override fun measureText(str: String): TextMetrics {
        val metrics = ImageMagick.MagickQueryFontMetrics(img, wand, str) ?: error("Failed to measure text")
        val ascent = metrics[2]
        val descent = metrics[3]
        val width = metrics[4]
        val height = metrics[5]

        return TextMetrics(ascent, descent, DoubleRectangle.XYWH(0, 0, width, height))
    }

    override fun measureTextWidth(str: String): Double {
        return measureText(str).bbox.width
    }

    // Faster than using PushDrawingWand/PopDrawingWand
    private fun withStrokeWand(block: (CPointer<ImageMagick.DrawingWand>) -> Unit) {
        ImageMagick.DrawGetFillColor(wand, pixelWand)
        ImageMagick.DrawSetFillColor(wand, none)
        block(wand)
        ImageMagick.DrawSetFillColor(wand, pixelWand)
    }

    // Faster than using PushDrawingWand/PopDrawingWand
    private fun withFillWand(block: (CPointer<ImageMagick.DrawingWand>) -> Unit) {
        ImageMagick.DrawGetStrokeColor(wand, pixelWand)
        ImageMagick.DrawSetStrokeColor(wand, none)
        block(wand)
        ImageMagick.DrawSetStrokeColor(wand, pixelWand)
    }

    companion object {
        const val logEnabled = false
        fun log(str: () -> String) {
            if (logEnabled)
                println(str())
        }

        private fun drawPath(
            wand: CPointer<ImageMagick.DrawingWand>,
            commands: List<PathCommand>,
            transform: AffineTransform
        ) {
            if (commands.isEmpty()) {
                return
            }

            var started = commands.first() is MoveTo

            fun lineTo(x: Double, y: Double) {
                if (started) {
                    ImageMagick.DrawPathLineToAbsolute(wand, x, y)
                } else {
                    ImageMagick.DrawPathMoveToAbsolute(wand, x, y)
                    started = true
                }
            }

            ImageMagick.DrawPathStart(wand)

            commands
                .asSequence()
                .map { cmd -> cmd.transform(transform) }
                .forEach { cmd ->
                    when (cmd) {
                        is MoveTo -> ImageMagick.DrawPathMoveToAbsolute(wand, cmd.x, cmd.y)
                        is LineTo -> lineTo(cmd.x, cmd.y)
                        is CubicCurveTo -> {
                            //cmd.start?.let { (x, y) -> lineTo(x, y) }
                            cmd.controlPoints.asSequence()
                                .windowed(size = 3, step = 3)
                                .forEach { (cp1, cp2, cp3) ->
                                    ImageMagick.DrawPathCurveToAbsolute(wand, cp1.x, cp1.y, cp2.x, cp2.y, cp3.x, cp3.y)
                                }
                        }

                        is ClosePath -> ImageMagick.DrawPathClose(wand)
                    }
                }
            ImageMagick.DrawPathFinish(wand)
        }

        fun transform(wand: CPointer<ImageMagick.DrawingWand>, affine: AffineTransform) {
            memScoped {
                val affineMatrix = alloc<ImageMagick.AffineMatrix>()
                affineMatrix.sx = affine.sx
                affineMatrix.ry = affine.rx // https://github.com/ImageMagick/ImageMagick/issues/8091
                affineMatrix.rx = affine.ry // https://github.com/ImageMagick/ImageMagick/issues/8091
                affineMatrix.sy = affine.sy
                affineMatrix.tx = affine.tx
                affineMatrix.ty = affine.ty
                ImageMagick.DrawAffine(wand, affineMatrix.ptr)
            }
        }

        private fun LineJoin.convert(): ImageMagick.LineJoin {
            return when (this) {
                LineJoin.BEVEL -> ImageMagick.LineJoin.BevelJoin
                LineJoin.MITER -> ImageMagick.LineJoin.MiterJoin
                LineJoin.ROUND -> ImageMagick.LineJoin.RoundJoin
            }
        }

        fun LineCap.convert(): ImageMagick.LineCap {
            return when (this) {
                LineCap.BUTT -> ImageMagick.LineCap.ButtCap
                LineCap.ROUND -> ImageMagick.LineCap.RoundCap
                LineCap.SQUARE -> ImageMagick.LineCap.SquareCap
            }
        }

        fun FontStyle.convert(): ImageMagick.StyleType {
            return when (this) {
                FontStyle.NORMAL -> ImageMagick.StyleType.NormalStyle
                FontStyle.ITALIC -> ImageMagick.StyleType.ItalicStyle
            }
        }

        fun FontWeight.convert(): ULong {
            return when (this) {
                FontWeight.NORMAL -> 400.toULong()
                FontWeight.BOLD -> 800.toULong()
            }
        }

    }
}
