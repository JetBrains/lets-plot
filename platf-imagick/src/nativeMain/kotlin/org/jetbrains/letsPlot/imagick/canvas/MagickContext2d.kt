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

class MagickContext2d(
    private val img: CPointer<ImageMagick.MagickWand>?,
    pixelDensity: Double,
    private val stateDelegate: ContextStateDelegate = ContextStateDelegate(),
) : Context2d by stateDelegate {
    private val none = ImageMagick.NewPixelWand() ?: error { "Failed to create PixelWand" }
    private val pixelWand = ImageMagick.NewPixelWand() ?: error { "Failed to create PixelWand" }
    val wand = ImageMagick.NewDrawingWand() ?: error { "Failed to create DrawingWand" }

    init {
        ImageMagick.PixelSetColor(none, "none")
        transform(wand, AffineTransform.makeScale(pixelDensity, pixelDensity))
    }

    override fun drawImage(snapshot: Canvas.Snapshot) {
        require(snapshot is MagickCanvas.MagickSnapshot) { "Snapshot must be of type MagickSnapshot" }
        ImageMagick.DrawComposite(wand, ImageMagick.CompositeOperator.OverCompositeOp, 0.0, 0.0, snapshot.size.x.toDouble(), snapshot.size.y.toDouble(), snapshot.img)
    }

    override fun drawImage(snapshot: Canvas.Snapshot, x: Double, y: Double) {
        require(snapshot is MagickCanvas.MagickSnapshot) { "Snapshot must be of type MagickSnapshot" }
        ImageMagick.DrawComposite(wand, ImageMagick.CompositeOperator.OverCompositeOp, x, y, snapshot.size.x.toDouble(), snapshot.size.y.toDouble(), snapshot.img)
    }

    override fun drawImage(snapshot: Canvas.Snapshot, x: Double, y: Double, dw: Double, dh: Double) {
        require(snapshot is MagickCanvas.MagickSnapshot) { "Snapshot must be of type MagickSnapshot" }
        if (dw != snapshot.size.x.toDouble() || dh != snapshot.size.y.toDouble()) {
            // Resize the image if the dimensions do not match
            val scaledImage = ImageMagick.CloneMagickWand(snapshot.img)
            ImageMagick.MagickScaleImage(scaledImage, dw.toULong(), dh.toULong())
            ImageMagick.DrawComposite(wand, ImageMagick.CompositeOperator.OverCompositeOp, x, y, dw, dh, scaledImage)
            ImageMagick.DestroyMagickWand(scaledImage)
        } else {
            ImageMagick.DrawComposite(wand, ImageMagick.CompositeOperator.OverCompositeOp, x, y, dw, dh, snapshot.img)
        }
    }

    override fun save() {
        stateDelegate.save()
        ImageMagick.PushDrawingWand(wand)
    }

    override fun restore() {
        stateDelegate.restore()

        ImageMagick.PopDrawingWand(wand)
    }

    override fun setFillStyle(color: Color?) {
        stateDelegate.setFillStyle(color)

        ImageMagick.PixelSetColor(pixelWand, color?.toCssColor() ?: "none")
        ImageMagick.DrawSetFillColor(wand, pixelWand)
    }

    override fun setStrokeStyle(color: Color?) {
        stateDelegate.setStrokeStyle(color)

        ImageMagick.PixelSetColor(pixelWand, color?.toCssColor() ?: "none")
        ImageMagick.DrawSetStrokeColor(wand, pixelWand)
    }

    override fun setLineWidth(lineWidth: Double) {
        stateDelegate.setLineWidth(lineWidth)

        ImageMagick.DrawSetStrokeWidth(wand, lineWidth)
    }

    override fun setLineDash(lineDash: DoubleArray) {
        stateDelegate.setLineDash(lineDash)

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

    override fun setLineCap(lineCap: LineCap) {
        stateDelegate.setLineCap(lineCap)
        ImageMagick.DrawSetStrokeLineCap(wand, lineCap.convert())
    }

    override fun setLineJoin(lineJoin: LineJoin) {
        stateDelegate.setLineJoin(lineJoin)
        ImageMagick.DrawSetStrokeLineJoin(wand, lineJoin.convert())
    }

    override fun setStrokeMiterLimit(miterLimit: Double) {
        stateDelegate.setStrokeMiterLimit(miterLimit)

        ImageMagick.DrawSetStrokeMiterLimit(wand, miterLimit.toULong())
    }

    override fun setFont(f: Font) {
        stateDelegate.setFont(f)

        ImageMagick.DrawSetFontSize(wand, f.fontSize)
        ImageMagick.DrawSetFontFamily(wand, f.fontFamily)

        val fontStyle = when (f.fontStyle) {
            FontStyle.NORMAL -> ImageMagick.StyleType.NormalStyle
            FontStyle.ITALIC -> ImageMagick.StyleType.ItalicStyle
        }
        ImageMagick.DrawSetFontStyle(wand, fontStyle)

        val fontWeight = when (f.fontWeight) {
            FontWeight.NORMAL -> 400.toULong()
            FontWeight.BOLD -> 800.toULong()
        }
        ImageMagick.DrawSetFontWeight(wand, fontWeight)
    }

    override fun setGlobalAlpha(alpha: Double) {
        stateDelegate.setGlobalAlpha(alpha)

        ImageMagick.DrawSetFillOpacity(wand, alpha)
        ImageMagick.DrawSetStrokeOpacity(wand, alpha)
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

        val clipPath = stateDelegate.getClipPath()
        if (clipPath.isEmpty) {
            // No clip path defined, nothing to do.
            return
        }

        val inverseCTMTransform = stateDelegate.getCTM().inverse() ?: return
        val clipId = clipPath.hashCode().toUInt().toString(16)

        ImageMagick.DrawPushDefs(wand)
        ImageMagick.DrawPushClipPath(wand, clipId)

        // DrawAffine transforms clipPath, but a path already has transform applied.
        // So we need to inversely transform it to prevent double transform.
        drawPath(wand, clipPath.getCommands(), inverseCTMTransform)

        ImageMagick.DrawPopClipPath(wand)
        ImageMagick.DrawPopDefs(wand)

        ImageMagick.DrawSetClipPath(wand, clipId)
    }

    override fun transform(sx: Double, ry: Double, rx: Double, sy: Double, tx: Double, ty: Double) {
        stateDelegate.transform(sx, ry, rx, sy, tx, ty)
        transform(wand, AffineTransform.makeTransform(sx = sx, ry = ry, rx = rx, sy = sy, tx = tx, ty = ty))
    }

    override fun scale(xy: Double) {
        stateDelegate.scale(xy)
        transform(wand, AffineTransform.makeScale(xy, xy))
    }

    override fun scale(x: Double, y: Double) {
        stateDelegate.scale(x, y)
        transform(wand, AffineTransform.makeScale(x, y))
    }

    override fun rotate(angle: Double) {
        stateDelegate.rotate(angle)
        transform(wand, AffineTransform.makeRotation(angle))
    }

    override fun translate(x: Double, y: Double) {
        stateDelegate.translate(x, y)
        transform(wand, AffineTransform.makeTranslation(x, y))
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

        internal fun drawPath(wand: CPointer<ImageMagick.DrawingWand>, commands: List<PathCommand>, transform: AffineTransform) {
            if (commands.isEmpty()) {
                return
            }

            log { "drawPath: commands=${commands.joinToString { it.toString() }}, transform=${transform.repr()}" }


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

        internal fun transform(wand: CPointer<ImageMagick.DrawingWand>, affine: AffineTransform) {
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

        fun LineJoin.convert(): ImageMagick.LineJoin {
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
