/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.imagick.canvas

import kotlinx.cinterop.*
import org.jetbrains.letsPlot.commons.geometry.AffineTransform
import org.jetbrains.letsPlot.commons.geometry.AffineTransform.Companion.makeShear
import org.jetbrains.letsPlot.commons.geometry.AffineTransform.Companion.makeTranslation
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.*
import org.jetbrains.letsPlot.core.canvas.Path2d.*
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil.cloneMagickWand
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil.destroyDrawingWand
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil.destroyMagickWand
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil.destroyPixelWand
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil.newDrawingWand
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil.newPixelWand
import kotlin.math.tan

class MagickContext2d(
    private val img: CPointer<ImageMagick.MagickWand>?,
    pixelDensity: Double,
    private val fontManager: MagickFontManager,
    private val stateDelegate: ContextStateDelegate = ContextStateDelegate(),
) : Context2d by stateDelegate, Disposable {
    private val none = newPixelWand()
    private val pixelWand = newPixelWand()
    private val currentFillWand = newPixelWand()
    private val currentStrokeWand = newPixelWand()

    val wand = newDrawingWand()
    private var currentFillRule: ImageMagick.FillRule // perf: reduce the number of calls to DrawSetFillRule

    private var dirtyFont = true
    private var fontPath: String? = null
    private var emulateBoldWeight: Boolean = false
    private var emulateItalicStyle: Boolean = false

    init {
        ImageMagick.DrawSetFillRule(wand, ImageMagick.FillRule.NonZeroRule)
        currentFillRule = ImageMagick.FillRule.NonZeroRule

        ImageMagick.PixelSetColor(none, "none")
        transform(wand, AffineTransform.makeScale(pixelDensity, pixelDensity))
    }

    override fun clearRect(rect: DoubleRectangle) {
        ImageMagick.DrawGetFillColor(wand, currentFillWand)
        ImageMagick.DrawGetStrokeColor(wand, currentStrokeWand)

        ImageMagick.DrawSetFillColor(wand, none)
        ImageMagick.DrawSetStrokeColor(wand, none)

        ImageMagick.DrawRectangle(wand, rect.left, rect.top, rect.right, rect.bottom)

        ImageMagick.DrawSetFillColor(wand, currentFillWand)
        ImageMagick.DrawSetStrokeColor(wand, currentStrokeWand)
    }

    override fun drawImage(snapshot: Canvas.Snapshot) {
        require(snapshot is MagickSnapshot) { "Snapshot must be of type MagickSnapshot" }
        ImageMagick.DrawComposite(wand, ImageMagick.CompositeOperator.OverCompositeOp, 0.0, 0.0, snapshot.size.x.toDouble(), snapshot.size.y.toDouble(), snapshot.img)
    }

    override fun drawImage(snapshot: Canvas.Snapshot, x: Double, y: Double) {
        require(snapshot is MagickSnapshot) { "Snapshot must be of type MagickSnapshot" }
        ImageMagick.DrawComposite(wand, ImageMagick.CompositeOperator.OverCompositeOp, x, y, snapshot.size.x.toDouble(), snapshot.size.y.toDouble(), snapshot.img)
    }

    override fun drawImage(snapshot: Canvas.Snapshot, x: Double, y: Double, dw: Double, dh: Double) {
        require(snapshot is MagickSnapshot) { "Snapshot must be of type MagickSnapshot" }
        if (dw != snapshot.size.x.toDouble() || dh != snapshot.size.y.toDouble()) {
            // Resize the image if the dimensions do not match
            val scaledImage = cloneMagickWand(snapshot.img)
            ImageMagick.MagickScaleImage(scaledImage, dw.toULong(), dh.toULong())
            ImageMagick.DrawComposite(wand, ImageMagick.CompositeOperator.OverCompositeOp, x, y, dw, dh, scaledImage)
            destroyMagickWand(scaledImage)
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

        dirtyFont = true
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
        dirtyFont = true
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
            if (currentFillRule != ImageMagick.FillRule.NonZeroRule) {
                ImageMagick.DrawSetFillRule(fillWand, ImageMagick.FillRule.NonZeroRule)
                currentFillRule = ImageMagick.FillRule.NonZeroRule
            }

            drawPath(fillWand, stateDelegate.getCurrentPath(), inverseCtmTransform)
        }
    }

    override fun fillEvenOdd() {
        // Make ctm identity. null for degenerate case, e.g., scale(0, 0) - skip drawing.
        val inverseCtmTransform = stateDelegate.getCTM().inverse() ?: return

        withFillWand { fillWand ->
            if (currentFillRule != ImageMagick.FillRule.EvenOddRule) {
                ImageMagick.DrawSetFillRule(fillWand, ImageMagick.FillRule.EvenOddRule)
                currentFillRule = ImageMagick.FillRule.EvenOddRule
            }

            drawPath(fillWand, stateDelegate.getCurrentPath(), inverseCtmTransform)
        }
    }

    private fun withTextWand(fill: Boolean, painter: (CPointer<ImageMagick.DrawingWand>) -> Unit) {
        val font = stateDelegate.getFont()

        if (dirtyFont) {
            dirtyFont = false
            val fontSet = fontManager.resolveFont(font.fontFamily)

            fontPath = when {
                font.isNormal && fontSet.regularFontPath != null -> fontSet.regularFontPath
                font.isItalic && fontSet.italicFontPath != null -> fontSet.italicFontPath
                font.isBold && fontSet.boldFontPath != null -> fontSet.boldFontPath
                font.isBoldItalic -> when {
                    fontSet.boldItalicFontPath != null -> fontSet.boldItalicFontPath
                    fontSet.boldFontPath != null -> fontSet.boldFontPath // take bold, emulate italic
                    fontSet.italicFontPath != null -> fontSet.italicFontPath // take italic, emulate bold
                    else -> fontSet.regularFontPath
                }
                else -> fontSet.regularFontPath
            }

            emulateBoldWeight = when {
                !font.isBold && !font.isBoldItalic -> false
                font.isBold && fontSet.boldFontPath != null -> false
                font.isBoldItalic && fontSet.boldItalicFontPath != null -> false
                font.isBoldItalic && fontSet.boldFontPath != null -> false
                else -> true
            }

            emulateItalicStyle = when {
                !font.isItalic && !font.isBoldItalic -> false
                font.isItalic && fontSet.italicFontPath != null -> false
                font.isBoldItalic && fontSet.boldItalicFontPath != null -> false
                font.isBoldItalic && fontSet.italicFontPath != null -> false
                else -> true
            }
        }

        ImageMagick.DrawSetFont(wand, fontPath)
        ImageMagick.DrawSetFontSize(wand, font.fontSize)
        ImageMagick.DrawSetFontStyle(wand, font.fontStyle.convert())
        ImageMagick.DrawSetFontWeight(wand, font.fontWeight.convert())

        if (emulateItalicStyle) {
            transform(wand, italicShearTransform)
        }

        when {
            fill -> withFillWand { fillWand ->
                if (emulateBoldWeight) {
                    ImageMagick.DrawSetStrokeWidth(fillWand, 0.1) // Faux bold stroke width
                    ImageMagick.DrawGetFillColor(fillWand, currentFillWand)
                    ImageMagick.DrawSetStrokeColor(fillWand, currentFillWand)
                }
                fillWand.apply(painter)
            }

            else -> withStrokeWand { strokeWand -> strokeWand.apply(painter) }
        }

        if (emulateItalicStyle) {
            transform(wand, italicShearTransform.inverse() ?: error("Failed to inverse italic shear transform"))
        }
    }

    override fun fillText(text: String, x: Double, y: Double) {
        withTextWand(fill = true) { fillWand ->
            drawText(fillWand, text, x, y)
        }
    }

    override fun strokeText(text: String, x: Double, y: Double) {
        withTextWand(fill = false) { strokeWand ->
            drawText(strokeWand, text, x, y)
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
        var ascent = 0.0
        var descent = 0.0
        var width = 0.0
        var height = 0.0

        withTextWand(fill = true) {
            val metrics = ImageMagick.MagickQueryFontMetrics(img, wand, str) ?: error("Failed to measure text")
            ascent = metrics[2]
            descent = metrics[3]
            width = metrics[4]
            height = metrics[5]
        }

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

    override fun dispose() {
        //destroyMagickWand(img)  DO NOT destroy img here - MagickCanvas is the owner of it.
        destroyPixelWand(pixelWand)
        destroyPixelWand(currentFillWand)
        destroyPixelWand(currentStrokeWand)
        destroyPixelWand(none)
        destroyDrawingWand(wand)
    }

    private fun drawText(wand: CPointer<ImageMagick.DrawingWand>, text: String, x: Double, y: Double) {
        fun italicOffsetAdjustment() = makeTranslation(-tan(ITALIC_SHEAR_ANGLE) * y, 0)

        if (emulateItalicStyle) {
            transform(wand, italicOffsetAdjustment())
        }

        memScoped {
            val textCStr = text.cstr.ptr.reinterpret<UByteVar>()
            ImageMagick.DrawAnnotation(wand, x, y, textCStr)
        }

        if (emulateItalicStyle) {
            transform(wand, italicOffsetAdjustment().inverse()!!)
        }
    }


    companion object {
        private const val ITALIC_SHEAR_ANGLE = -0.25 // radians, approx. 14 degrees
        private val italicShearTransform = makeShear(rx = ITALIC_SHEAR_ANGLE, ry = 0.0)

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
