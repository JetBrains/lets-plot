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
import org.jetbrains.letsPlot.imagick.canvas.MagickContext2d.Companion.convert

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
        MagickContext2d.transform(wand, AffineTransform.makeScale(pixelDensity, pixelDensity))
    }

    private fun onStateChange(stateChange: ContextStateDelegate.StateChange) {
        println("Magick2Context2d.onStateChange: $stateChange")

        val clipPath = stateChange.clipPath
        if (clipPath?.isEmpty == true) {
            // The only way to unset clip-path is to re-create the wand.
            MagickContext2d.log { "Magick2Context2d.onStateChange: clipPath is empty, re-creating wand." }
            ImageMagick.ClearDrawingWand(wand)
            val fullStateChange = stateDelegate.restartStateChange()
            applyStateChange(fullStateChange)
        } else {
            applyStateChange(stateChange)
        }
    }

    private fun applyStateChange(stateChange: ContextStateDelegate.StateChange) {
        stateChange.strokeColor?.let { strokeColor ->
            ImageMagick.PixelSetColor(pixelWand, strokeColor.toCssColor())
            ImageMagick.DrawSetStrokeColor(wand, pixelWand)
        }

        stateChange.strokeWidth?.let { strokeWidth ->
            println("Magick2Context2d.onStateChange: strokeWidth = $strokeWidth")
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
            MagickContext2d.log { "Magick2Context2d.onStateChange: transform = ${transform.repr()}" }
            MagickContext2d.transform(wand, transform)
        }

        stateChange.globalAlpha?.let { globalAlpha ->
            ImageMagick.DrawSetFillOpacity(wand, globalAlpha)
            ImageMagick.DrawSetStrokeOpacity(wand, globalAlpha)
        }

        stateChange.clipPath?.let { clipPath ->
            if (!clipPath.isEmpty) {
                val inverseCTMTransform = stateDelegate.getCTM().inverse()
                if (inverseCTMTransform == null) {
                    MagickContext2d.log { "Magick2Context2d.onStateChange: clipPath ignored, CTM is degenerate." }
                    return@let
                }
                val clipId = clipPath.hashCode().toUInt().toString(16)

                ImageMagick.DrawPushDefs(wand)
                ImageMagick.DrawPushClipPath(wand, clipId)

                // DrawAffine transforms clipPath, but a path already has the transform applied.
                // So we need to inversely transform it to prevent double transform.
                MagickContext2d.drawPath(wand, clipPath.getCommands(), inverseCTMTransform)

                ImageMagick.DrawPopClipPath(wand)
                ImageMagick.DrawPopDefs(wand)

                ImageMagick.DrawSetClipPath(wand, clipId)

                MagickContext2d.log { "Magick2Context2d.onStateChange: clipPath set with id = $clipId" }
            } else {
                // Unset clip-path by re-creating the wand.
                // Should be handled in onStateChange.
            }
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

    override fun drawImage(snapshot: Canvas.Snapshot, x: Double, y: Double, dw: Double, dh: Double) {
        val snap = snapshot as MagickCanvas.MagickSnapshot
        val srcWand = snap.img

        // Resize the source wand to desired width and height
        val successScale = ImageMagick.MagickScaleImage(
            srcWand,
            dw.toULong(),
            dh.toULong()
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
            MagickContext2d.drawPath(strokeWand, stateDelegate.getCurrentPath(), inverseCtmTransform)
        }
    }

    override fun fill() {
        // Make ctm identity. null for degenerate case, e.g., scale(0, 0) - skip drawing.
        val inverseCtmTransform = stateDelegate.getCTM().inverse() ?: return

        withFillWand { fillWand ->
            MagickContext2d.drawPath(fillWand, stateDelegate.getCurrentPath(), inverseCtmTransform)
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
}
