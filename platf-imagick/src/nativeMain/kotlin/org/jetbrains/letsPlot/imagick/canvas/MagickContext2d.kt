/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.imagick.canvas

import kotlinx.cinterop.*
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.intern.math.toDegrees
import org.jetbrains.letsPlot.commons.intern.math.toRadians
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.*


class MagickContext2d(
    private val magickWand: CPointer<ImageMagick.MagickWand>?
) : Context2d by Context2dDelegate(true) {
    private val pixelWand = ImageMagick.NewPixelWand() ?: error { "Failed to create PixelWand" }
    private var currentPath: MagickPath = MagickPath()
    private var state = MagickContextState.create()
    private val contextStates = mutableListOf<MagickContextState>()


    override fun setTransform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double) {
        //println("setTransform(m11=$m11, m12=$m12, m21=$m21, m22=$m22, dx=$dx, dy=$dy)")
       // println("\tfrom: [${state.affineMatrix.sx}, ${state.affineMatrix.rx}, ${state.affineMatrix.tx}, ${state.affineMatrix.ry}, ${state.affineMatrix.sy}, ${state.affineMatrix.ty}]")
        state.setTransform(m11,m12,m21,m22,dx,dy)
        //println("\t  to: [${state.affineMatrix.sx}, ${state.affineMatrix.rx}, ${state.affineMatrix.tx}, ${state.affineMatrix.ry}, ${state.affineMatrix.sy}, ${state.affineMatrix.ty}]")
    }

    override fun transform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double) {
        //println("transform(m11=$m11, m12=$m12, m21=$m21, m22=$m22, dx=$dx, dy=$dy)")
        //println("\tfrom: [${state.affineMatrix.sx}, ${state.affineMatrix.rx}, ${state.affineMatrix.tx}, ${state.affineMatrix.ry}, ${state.affineMatrix.sy}, ${state.affineMatrix.ty}]")

        state.transform(sx = m11, rx = m21, ry = m12, sy = m22, dx = dx, dy = dy)
        //println("\t  to: [${state.affineMatrix.sx}, ${state.affineMatrix.rx}, ${state.affineMatrix.tx}, ${state.affineMatrix.ry}, ${state.affineMatrix.sy}, ${state.affineMatrix.ty}]")
    }

    override fun scale(x: Double, y: Double) {
        return transform(x, 0.0, 0.0, y, 0.0, 0.0)
    }

    override fun rotate(angle: Double) {
        val radians = toRadians(angle)
        val cos = kotlin.math.cos(radians)
        val sin = kotlin.math.sin(radians)
        return transform(cos, sin, -sin, cos, 0.0, 0.0)
    }

    override fun translate(x: Double, y: Double) {
        return transform(1.0, 0.0, 0.0, 1.0, x, y)
    }

    override fun setFont(f: Font) {
        state.fontStyle = when (f.fontStyle) {
            FontStyle.NORMAL -> ImageMagick.StyleType.NormalStyle
            FontStyle.ITALIC -> ImageMagick.StyleType.ItalicStyle
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
                ImageMagick.DrawAnnotation(fillWand, x, y, textCStr)
            }
            ImageMagick.MagickDrawImage(magickWand, fillWand)
        }
    }

    override fun strokeText(text: String, x: Double, y: Double) {
        withStrokeWand { strokeWand ->
            memScoped {
                val textCStr = text.cstr.ptr.reinterpret<UByteVar>()
                ImageMagick.DrawAnnotation(strokeWand, x, y, textCStr)
            }
            ImageMagick.MagickDrawImage(magickWand, strokeWand)
        }
    }

    override fun beginPath() {
        currentPath = MagickPath()
    }

    override fun moveTo(x: Double, y: Double) {
        currentPath.moveTo(x, y)
    }

    override fun lineTo(x: Double, y: Double) {
        currentPath.lineTo(x, y)
    }

    override fun arc(
        x: Double,
        y: Double,
        radius: Double,
        startAngle: Double,
        endAngle: Double,
        anticlockwise: Boolean
    ) {
        currentPath.arc(x, y, radius, toDegrees(startAngle), toDegrees(endAngle), anticlockwise)
    }

    override fun ellipse(
        x: Double, y: Double,
        radiusX: Double, radiusY: Double,
        rotation: Double,
        startAngle: Double, endAngle: Double,
        anticlockwise: Boolean
    ) {
        currentPath.ellipse(x, y, radiusX, radiusY, toDegrees(rotation), toDegrees(startAngle), toDegrees(endAngle), anticlockwise)
    }

    override fun closePath() {
        currentPath.closePath()
    }

    override fun stroke() {
        //println("Stroke: [${state.affineMatrix.sx}, ${state.affineMatrix.rx}, ${state.affineMatrix.tx}, ${state.affineMatrix.ry}, ${state.affineMatrix.sy}, ${state.affineMatrix.ty}]")
        withStrokeWand { strokeWand ->
            currentPath.draw(strokeWand)
            ImageMagick.MagickDrawImage(magickWand, strokeWand)
        }
    }

    override fun fill() {
        //println("Fill: [${state.affineMatrix.sx}, ${state.affineMatrix.rx}, ${state.affineMatrix.tx}, ${state.affineMatrix.ry}, ${state.affineMatrix.sy}, ${state.affineMatrix.ty}]")
        withFillWand { fillWand ->
            currentPath.draw(fillWand)
            ImageMagick.MagickDrawImage(magickWand, fillWand)
        }
    }

    override fun fillRect(x: Double, y: Double, w: Double, h: Double) {
        withFillWand { fillWand ->
            ImageMagick.DrawRectangle(fillWand, x, y, x + w, y + h)
            ImageMagick.MagickDrawImage(magickWand, fillWand)
        }
    }

    override fun strokeRect(x: Double, y: Double, w: Double, h: Double) {
        withStrokeWand { strokeWand ->
            ImageMagick.DrawRectangle(strokeWand, x, y, x + w, y + h)
            ImageMagick.MagickDrawImage(magickWand, strokeWand)
        }
    }

    override fun measureText(str: String): TextMetrics {
        var metrics: CPointer<DoubleVar>? = null
        memScoped {
            withStrokeWand { strokeWand ->
                metrics = ImageMagick.MagickQueryFontMetrics(magickWand, strokeWand, str)
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
        //println("save")
        contextStates += state
        state = state.copy()
        val old = contextStates.last().affineMatrix
        //println("\tfrom: [${old.sx}, ${old.rx}, ${old.tx}, ${old.ry}, ${old.sy}, ${old.ty}]")
        //println("\t  to: [${state.affineMatrix.sx}, ${state.affineMatrix.rx}, ${state.affineMatrix.tx}, ${state.affineMatrix.ry}, ${state.affineMatrix.sy}, ${state.affineMatrix.ty}]")
    }

    override fun restore() {
        //println("restore()")
        //println("\tfrom: [${state.affineMatrix.sx}, ${state.affineMatrix.rx}, ${state.affineMatrix.tx}, ${state.affineMatrix.ry}, ${state.affineMatrix.sy}, ${state.affineMatrix.ty}]")
        state = contextStates.lastOrNull() ?: MagickContextState.create()
        contextStates.removeLastOrNull()?.destroy()
        //println("\t  to: [${state.affineMatrix.sx}, ${state.affineMatrix.rx}, ${state.affineMatrix.tx}, ${state.affineMatrix.ry}, ${state.affineMatrix.sy}, ${state.affineMatrix.ty}]")
    }

    private fun withWand(block: (CPointer<ImageMagick.DrawingWand>) -> Unit) {
        val wand = ImageMagick.NewDrawingWand() ?: error { "DrawingWand was null" }

        ImageMagick.DrawAffine(wand, state.affineMatrix.ptr)
        ImageMagick.DrawSetFontSize(wand, state.fontSize)
        ImageMagick.DrawSetFontFamily(wand, state.fontFamily)
        ImageMagick.DrawSetFontStyle(wand, state.fontStyle)
        ImageMagick.DrawSetFontWeight(wand, state.fontWeight)

        block(wand)
        ImageMagick.DestroyDrawingWand(wand)
    }

    private fun withStrokeWand(block: (CPointer<ImageMagick.DrawingWand>) -> Unit) {
        withWand { strokeWand ->
            ImageMagick.PixelSetColor(pixelWand, state.strokeColor)
            ImageMagick.DrawSetStrokeColor(strokeWand, pixelWand)
            ImageMagick.DrawSetStrokeWidth(strokeWand, state.strokeWidth)
            ImageMagick.DrawSetStrokeMiterLimit(strokeWand, state.miterLimit)
            ImageMagick.DrawSetStrokeLineCap(strokeWand, state.lineCap)
            ImageMagick.DrawSetStrokeLineJoin(strokeWand, state.lineJoin)

            ImageMagick.DrawSetStrokeDashOffset(strokeWand, state.lineDashOffset)
            ImageMagick.DrawSetStrokeDashArray(strokeWand, state.lineDashPatternSize, state.lineDashPattern)

            ImageMagick.PixelSetColor(pixelWand, Color.TRANSPARENT.toCssColor())
            ImageMagick.DrawSetFillColor(strokeWand, pixelWand)

            block(strokeWand)
        }
    }

    private fun withFillWand(block: (CPointer<ImageMagick.DrawingWand>) -> Unit) {
        withWand { fillWand ->
            ImageMagick.PixelSetColor(pixelWand, state.fillColor)
            ImageMagick.DrawSetFillColor(fillWand, pixelWand)

            ImageMagick.PixelSetColor(pixelWand, Color.TRANSPARENT.toCssColor())
            ImageMagick.DrawSetStrokeColor(fillWand, pixelWand)

            block(fillWand)
        }
    }

    companion object {
        val IDENTITY = nativeHeap.alloc<ImageMagick.AffineMatrix>().apply {
            sx = 1.0  // Scale X (no change)
            sy = 1.0  // Scale Y (no change)
            rx = 0.0  // Shear X
            ry = 0.0  // Shear Y
            tx = 0.0 // Translate X
            ty = 0.0  // Translate Y
        }
    }

}
