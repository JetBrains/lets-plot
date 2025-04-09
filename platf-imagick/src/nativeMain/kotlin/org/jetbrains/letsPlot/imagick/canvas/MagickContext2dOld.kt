/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.imagick.canvas

import ImageMagick.*
import kotlinx.cinterop.*
import org.jetbrains.letsPlot.commons.geometry.AffineTransform
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.toDegrees
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.*
import org.jetbrains.letsPlot.core.canvas.LineCap
import org.jetbrains.letsPlot.core.canvas.LineJoin
import org.jetbrains.letsPlot.raster.shape.applyTransform
import kotlin.math.cos
import kotlin.math.sin


class MagickContext2dOld(
    private val magickWand: CPointer<MagickWand>?
) : Context2d by Context2dDelegate(true) {
    private val pixelWand = NewPixelWand() ?: error { "Failed to create PixelWand" }
    private var currentPath: MagickPath = MagickPath()
    private var state = MagickContextState.create()
    private val contextStates = mutableListOf<MagickContextState>()
    private val contextState = ContextState()

    val pathTransform = true

    override fun setTransform(m00: Double, m10: Double, m01: Double, m11: Double, m02: Double, m12: Double) {
        log { "setTransform(m11=$m00, m12=$m10, m21=$m01, m22=$m11, dx=$m02, dy=$m12)" }
        log { "\tfrom: [${state.affineMatrix.repr()}]" }
        state.setTransform(m00, m10, m01, m11, m02, m12)
        log { "\t  to: [${state.affineMatrix.repr()}]" }
    }

    override fun transform(sx: Double, ry: Double, rx: Double, sy: Double, tx: Double, ty: Double) {
        log { "transform(m11=$sx, m12=$ry, m21=$rx, m22=$sy, dx=$tx, dy=$ty)" }
        log { "\tfrom: [${state.affineMatrix.repr()}]" }

        state.transform(sx = sx, ry = ry, rx = rx, sy = sy, dx = tx, dy = ty)
        log { "\t  to: [${state.affineMatrix.repr()}]" }
    }

    override fun scale(x: Double, y: Double) {
        return transform(x, 0.0, 0.0, y, 0.0, 0.0)
    }

    override fun rotate(angle: Double) {
        val angle = -angle // ImageMagick uses clockwise rotation
        val cos = cos(angle)
        val sin = sin(angle)
        return transform(cos, -sin, sin, cos, 0.0, 0.0)
    }

    override fun translate(x: Double, y: Double) {
        return transform(1.0, 0.0, 0.0, 1.0, x, y)
    }

    override fun setFont(f: Font) {
        state.fontStyle = when (f.fontStyle) {
            FontStyle.NORMAL -> StyleType.NormalStyle
            FontStyle.ITALIC -> StyleType.ItalicStyle
        }

        state.fontWeight = when (f.fontWeight) {
            FontWeight.NORMAL -> 400U
            FontWeight.BOLD -> 800U
        }

        state.fontFamily = f.fontFamily
        state.fontSize = f.fontSize
    }

    override fun setFillStyle(color: Color?) {
        state.fillColor = color?.toCssColor() ?: Color.BLACK.toCssColor()
    }

    override fun setStrokeStyle(color: Color?) {
        state.strokeColor = color?.toCssColor() ?: Color.BLACK.toCssColor()
    }

    override fun setLineWidth(lineWidth: Double) {
        state.strokeWidth = lineWidth
    }

    override fun setLineDash(lineDash: DoubleArray) {
        val patternArray2 = nativeHeap.allocArray<DoubleVar>(lineDash.size) { i -> value = lineDash[i] }
        state.lineDashPattern = patternArray2
        state.lineDashPatternSize = lineDash.size.toULong()
    }

    override fun setLineDashOffset(lineDashOffset: Double) {
        state.lineDashOffset = lineDashOffset
    }

    override fun setStrokeMiterLimit(miterLimit: Double) {
        state.miterLimit = miterLimit.toULong()
    }

    override fun setLineCap(lineCap: LineCap) {
        state.lineCap = when (lineCap) {
            LineCap.BUTT -> ImageMagick.LineCap.ButtCap
            LineCap.ROUND -> ImageMagick.LineCap.RoundCap
            LineCap.SQUARE -> ImageMagick.LineCap.SquareCap
        }
    }

    override fun setLineJoin(lineJoin: LineJoin) {
        state.lineJoin = when (lineJoin) {
            LineJoin.BEVEL -> ImageMagick.LineJoin.BevelJoin
            LineJoin.ROUND -> ImageMagick.LineJoin.RoundJoin
            LineJoin.MITER -> ImageMagick.LineJoin.MiterJoin
        }
    }

    override fun fillText(text: String, x: Double, y: Double) {
        //println("FillText(\'$text\') [${state.affineMatrix.sx}, ${state.affineMatrix.rx}, ${state.affineMatrix.tx}, ${state.affineMatrix.ry}, ${state.affineMatrix.sy}, ${state.affineMatrix.ty}]")
        withFillWand { fillWand ->
            memScoped {
                val textCStr = text.cstr.ptr.reinterpret<UByteVar>()
                DrawAnnotation(fillWand, x, y, textCStr)
            }
            MagickDrawImage(magickWand, fillWand)
        }
    }

    override fun strokeText(text: String, x: Double, y: Double) {
        withStrokeWand { strokeWand ->
            memScoped {
                val textCStr = text.cstr.ptr.reinterpret<UByteVar>()
                DrawAnnotation(strokeWand, x, y, textCStr)
            }
            MagickDrawImage(magickWand, strokeWand)
        }
    }

    override fun beginPath() {
        currentPath = MagickPath()
    }

    override fun moveTo(x: Double, y: Double) {
        val (tx, ty) = if (!pathTransform) DoubleVector(x, y) else state.transformMatrix.applyTransform(x, y)

        log { "moveTo($x, $y) -> [$tx, $ty]" }

        currentPath.moveTo(tx, ty)
    }

    override fun lineTo(x: Double, y: Double) {
        val (tx, ty) = if (!pathTransform) DoubleVector(x, y) else state.transformMatrix.applyTransform(x, y)

        log { "lineTo($x, $y) -> [$tx, $ty]" }

        currentPath.lineTo(tx, ty)
    }

    override fun arc(
        x: Double,
        y: Double,
        radius: Double,
        startAngle: Double,
        endAngle: Double,
        anticlockwise: Boolean
    ) {
        val (tx, ty) = if (!pathTransform) DoubleVector(x, y) else state.transformMatrix.applyTransform(x, y)

        log { "arc($x, $y, $radius, $startAngle, $endAngle) -> [$tx, $ty]" }

        currentPath.arc(tx, ty, radius, toDegrees(startAngle), toDegrees(endAngle), anticlockwise)
    }

    override fun ellipse(
        x: Double, y: Double,
        radiusX: Double, radiusY: Double,
        rotation: Double,
        startAngle: Double, endAngle: Double,
        anticlockwise: Boolean
    ) {
        val (tX, tY) = if (!pathTransform) DoubleVector(x, y) else state.transformMatrix.applyTransform(x, y)
        val (trX, trY) = if (!pathTransform) DoubleVector(radiusX, radiusY) else state.transformMatrix.applyTransform(
            radiusX,
            radiusY
        )

        log { "ellipse($x, $y, $radiusX, $radiusY, $rotation, $startAngle, $endAngle, $anticlockwise) -> c: [$tX, $tY], r: [$trX, $trY]" }

        currentPath.ellipse(
            tX,
            tY,
            radiusX,
            radiusY,
            toDegrees(rotation),
            toDegrees(startAngle),
            toDegrees(endAngle),
            anticlockwise
        )
    }

    override fun closePath() {
        currentPath.closePath()
    }

    override fun stroke() {
        log { "Stroke: [${state.affineMatrix.repr()}]" }
        withStrokeWand(AffineTransform.IDENTITY) { strokeWand ->
            if (pathTransform) {
                currentPath.draw(strokeWand)
            } else {
                currentPath.draw(strokeWand)
            }

            MagickDrawImage(magickWand, strokeWand)
        }
    }

    override fun fill() {
        log { "Fill: [${state.affineMatrix.repr()}]" }
        withFillWand { fillWand ->
            if (pathTransform) {
                DrawAffine(fillWand, IDENTITY.ptr)
                currentPath.draw(fillWand)
                DrawAffine(fillWand, state.affineMatrix.ptr)
            } else {
                currentPath.draw(fillWand)
            }
            MagickDrawImage(magickWand, fillWand)
        }
    }

    override fun fillRect(x: Double, y: Double, w: Double, h: Double) {
        withFillWand { fillWand ->
            DrawRectangle(fillWand, x, y, x + w, y + h)
            MagickDrawImage(magickWand, fillWand)
        }
    }

    override fun strokeRect(x: Double, y: Double, w: Double, h: Double) {
        withStrokeWand { strokeWand ->
            DrawRectangle(strokeWand, x, y, x + w, y + h)
            MagickDrawImage(magickWand, strokeWand)
        }
    }

    override fun measureText(str: String): TextMetrics {
        var metrics: CPointer<DoubleVar>? = null
        memScoped {
            withStrokeWand { strokeWand ->
                metrics = MagickQueryFontMetrics(magickWand, strokeWand, str)
                    ?: error("Failed to measure text")
            }
        }

        val m = metrics ?: error("Failed to measure text")
        val ascent = m[2]
        val descent = m[3]
        val width = m[4]
        val height = m[5]

        return TextMetrics(ascent, descent, DoubleRectangle.XYWH(0, 0, width, height))
    }

    override fun measureTextWidth(str: String): Double {
        return measureText(str).bbox.width
    }

    override fun save() {
        log { "save(${state.affineMatrix.repr()})" }
        contextStates += state
        state = state.copy()
    }

    override fun restore() {
        log { "restore()" }
        log { "\tfrom: [${state.affineMatrix.repr()}]" }
        state = contextStates.lastOrNull() ?: MagickContextState.create()
        contextStates.removeLastOrNull()?.destroy()
        log { "\t  to: [${state.affineMatrix.repr()}]" }
    }

    private fun withWand(affineTransform: AffineTransform? = null, block: (CPointer<DrawingWand>) -> Unit) {
        val wand = NewDrawingWand() ?: error { "DrawingWand was null" }
        DrawAffine(wand, state.affineMatrix.ptr)
        DrawSetFontSize(wand, state.fontSize)
        DrawSetFontFamily(wand, state.fontFamily)
        DrawSetFontStyle(wand, state.fontStyle)
        DrawSetFontWeight(wand, state.fontWeight)

        block(wand)
        DestroyDrawingWand(wand)
    }

    private fun withStrokeWand(affineTransform: AffineTransform? = null, block: (CPointer<DrawingWand>) -> Unit) {
        withWand(affineTransform) { strokeWand ->
            PixelSetColor(pixelWand, state.strokeColor)
            DrawSetStrokeColor(strokeWand, pixelWand)
            DrawSetStrokeWidth(strokeWand, state.strokeWidth)
            DrawSetStrokeMiterLimit(strokeWand, state.miterLimit)
            DrawSetStrokeLineCap(strokeWand, state.lineCap)
            DrawSetStrokeLineJoin(strokeWand, state.lineJoin)

            DrawSetStrokeDashOffset(strokeWand, state.lineDashOffset)
            DrawSetStrokeDashArray(strokeWand, state.lineDashPatternSize, state.lineDashPattern)

            PixelSetColor(pixelWand, Color.TRANSPARENT.toCssColor())
            DrawSetFillColor(strokeWand, pixelWand)

            block(strokeWand)
        }
    }

    private fun withFillWand(affineTransform: AffineTransform? = null, block: (CPointer<DrawingWand>) -> Unit) {
        withWand(affineTransform) { fillWand ->
            PixelSetColor(pixelWand, state.fillColor)
            DrawSetFillColor(fillWand, pixelWand)

            PixelSetColor(pixelWand, Color.TRANSPARENT.toCssColor())
            DrawSetStrokeColor(fillWand, pixelWand)
            block(fillWand)
        }
    }

    companion object {
        val IDENTITY = nativeHeap.alloc<AffineMatrix>().apply {
            sx = 1.0  // Scale X (no change)
            sy = 1.0  // Scale Y (no change)
            rx = 0.0  // Shear X
            ry = 0.0  // Shear Y
            tx = 0.0 // Translate X
            ty = 0.0  // Translate Y
        }

        val logEnabled = true
        fun log(str: () -> String) {
            if (logEnabled)
                println(str())
        }
    }

}
