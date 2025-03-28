/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.imagick.canvas

import kotlinx.cinterop.*
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
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
        state.transform = nativeHeap.alloc<ImageMagick.AffineMatrix>()
        state.transform.sx = m11
        state.transform.sy = m12
        state.transform.rx = m21
        state.transform.ry = m22
        state.transform.tx = dx
        state.transform.ty = dy
    }

    override fun transform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double) {
        state.transform(sx = m11, rx = m21, ry = m12, sy = m22, dx = dx, dy = dy)
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
        currentPath.arc(x, y, radius, startAngle, endAngle, anticlockwise)
    }

    override fun ellipse(
        x: Double, y: Double,
        radiusX: Double, radiusY: Double,
        rotation: Double,
        startAngle: Double, endAngle: Double,
        anticlockwise: Boolean
    ) {
        currentPath.ellipse(x, y, radiusX, radiusY, rotation, startAngle, endAngle, anticlockwise)
    }


    override fun closePath() {
        currentPath.closePath()
    }

    override fun stroke() {
        withStrokeWand { strokeWand ->
            currentPath.draw(strokeWand)
            ImageMagick.MagickDrawImage(magickWand, strokeWand)
        }
    }

    override fun fill() {
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
        contextStates += state
        state = state.copy()
    }

    override fun restore() {
        contextStates.removeLastOrNull()?.destroy()
        state = contextStates.lastOrNull() ?: MagickContextState.create()
    }

    private fun withWand(block: (CPointer<ImageMagick.DrawingWand>) -> Unit) {
        val wand = ImageMagick.NewDrawingWand() ?: error { "DrawingWand was null" }
        val at = state.transform.let {
            "sx=${it.sx}, sy=${it.sy}, rx=${it.rx}, ry=${it.ry}, tx=${it.tx}, ty=${it.ty}"
        }
        println("withWand() - transform: $at")
        ImageMagick.DrawAffine(wand, state.transform.ptr)
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
