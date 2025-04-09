/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.imagick.canvas

import kotlinx.cinterop.*
import org.jetbrains.letsPlot.commons.geometry.AffineTransform
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.intern.math.toRadians
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.*
import org.jetbrains.letsPlot.core.canvas.ContextState.*
import kotlin.math.cos
import kotlin.math.sin


class MagickContext2d(
    private val magickWand: CPointer<ImageMagick.MagickWand>?
) : Context2d by Context2dDelegate(true) {
    private val pixelWand = ImageMagick.NewPixelWand() ?: error { "Failed to create PixelWand" }
    private val contextState = ContextState()

    override fun setTransform(m00: Double, m10: Double, m01: Double, m11: Double, m02: Double, m12: Double) {
        contextState.setTransform(m00 = m00, m10 = m10, m01 = m01, m11 = m11, m02 = m02, m12 = m12)
    }

    override fun transform(sx: Double, ry: Double, rx: Double, sy: Double, tx: Double, ty: Double) {
        contextState.transform(sx = sx, ry = ry, rx = rx, sy = sy, tx = tx, ty = ty)
    }

    override fun scale(x: Double, y: Double) {
        contextState.scale(x, y)
    }

    override fun rotate(angle: Double) {
        contextState.rotate(angle)
    }

    override fun translate(x: Double, y: Double) {
        contextState.translate(x, y)
    }

    override fun setFont(f: Font) {
        contextState.setFont(f)
    }

    override fun setFillStyle(color: Color?) {
        contextState.setFillStyle(color ?: Color.BLACK)
    }

    override fun setStrokeStyle(color: Color?) {
        contextState.setStrokeStyle(color ?: Color.BLACK)
    }

    override fun setLineWidth(lineWidth: Double) {
        contextState.setLineWidth(lineWidth)
    }

    override fun setLineDash(lineDash: DoubleArray) {
        contextState.setLineDashPattern(lineDash.toList())
    }

    override fun setLineDashOffset(lineDashOffset: Double) {
        contextState.setLineDashOffset(lineDashOffset)
    }

    override fun setStrokeMiterLimit(miterLimit: Double) {
        contextState.setStrokeMiterLimit(miterLimit)
    }

    override fun setLineCap(lineCap: LineCap) {
        contextState.setLineCap(lineCap)
    }

    override fun setLineJoin(lineJoin: LineJoin) {
        contextState.setLineJoin(lineJoin)
    }

    override fun fillText(text: String, x: Double, y: Double) {
        //println("FillText(\'$text\') [${state.affineMatrix.sx}, ${state.affineMatrix.rx}, ${state.affineMatrix.tx}, ${state.affineMatrix.ry}, ${state.affineMatrix.sy}, ${state.affineMatrix.ty}]")
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

    override fun beginPath() {
        contextState.beginPath()
    }

    override fun moveTo(x: Double, y: Double) {
        contextState.moveTo(x, y)
    }

    override fun lineTo(x: Double, y: Double) {
        contextState.lineTo(x, y)
    }

    override fun arc(
        x: Double,
        y: Double,
        radius: Double,
        startAngle: Double,
        endAngle: Double,
        anticlockwise: Boolean
    ) {
        contextState.arc(x, y, radius, startAngle, endAngle, anticlockwise)
    }

    override fun ellipse(
        x: Double, y: Double,
        radiusX: Double, radiusY: Double,
        rotation: Double,
        startAngle: Double, endAngle: Double,
        anticlockwise: Boolean
    ) {
        contextState.ellipse(x, y, radiusX, radiusY, rotation, startAngle, endAngle, anticlockwise)
    }

    override fun closePath() {
        contextState.closePath()
    }

    override fun stroke() {
        withStrokeWand() { strokeWand ->
            drawPath(contextState.getCurrentPath(), strokeWand)
        }
    }

    override fun fill() {
        withFillWand { fillWand ->
            drawPath(contextState.getCurrentPath(), fillWand)
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
        contextState.save()
    }

    override fun restore() {
        contextState.restore()
    }

    private fun withWand(affineTransform: AffineTransform? = null, block: (CPointer<ImageMagick.DrawingWand>) -> Unit) {
        val wand = ImageMagick.NewDrawingWand() ?: error { "DrawingWand was null" }
        val state = contextState.getCurrentState()

        DrawAffineTransofrm(wand, affineTransform ?: state.transform)
        ImageMagick.DrawSetFontSize(wand, state.font.fontSize)
        ImageMagick.DrawSetFontFamily(wand, state.font.fontFamily)

        val fontStyle = when (state.font.fontStyle) {
            FontStyle.NORMAL -> ImageMagick.StyleType.NormalStyle
            FontStyle.ITALIC -> ImageMagick.StyleType.ItalicStyle
        }
        ImageMagick.DrawSetFontStyle(wand, fontStyle)

        val fontWeight = when (state.font.fontWeight) {
            FontWeight.NORMAL -> 400.toULong()
            FontWeight.BOLD -> 800.toULong()
        }
        ImageMagick.DrawSetFontWeight(wand, fontWeight)

        block(wand)
        ImageMagick.DestroyDrawingWand(wand)
    }

    private fun withStrokeWand(
        affineTransform: AffineTransform? = null,
        block: (CPointer<ImageMagick.DrawingWand>) -> Unit
    ) {
        withWand(affineTransform) { strokeWand ->
            val state = contextState.getCurrentState()
            ImageMagick.PixelSetColor(pixelWand, state.strokeColor.toCssColor())
            ImageMagick.DrawSetStrokeColor(strokeWand, pixelWand)
            ImageMagick.DrawSetStrokeWidth(strokeWand, state.strokeWidth)
            ImageMagick.DrawSetStrokeMiterLimit(strokeWand, state.miterLimit.toULong())
            val lineCap = when (state.lineCap) {
                LineCap.BUTT -> ImageMagick.LineCap.ButtCap
                LineCap.ROUND -> ImageMagick.LineCap.RoundCap
                LineCap.SQUARE -> ImageMagick.LineCap.SquareCap
            }
            ImageMagick.DrawSetStrokeLineCap(strokeWand, lineCap)

            val lineJoin = when (state.lineJoin) {
                LineJoin.BEVEL -> ImageMagick.LineJoin.BevelJoin
                LineJoin.ROUND -> ImageMagick.LineJoin.RoundJoin
                LineJoin.MITER -> ImageMagick.LineJoin.MiterJoin
            }
            ImageMagick.DrawSetStrokeLineJoin(strokeWand, lineJoin)

            ImageMagick.DrawSetStrokeDashOffset(strokeWand, state.lineDashOffset)

            state.lineDashPattern?.let { lineDashPattern ->
                memScoped {
                    val lineDashPatternSize = lineDashPattern.size
                    val lineDashArray = allocArray<DoubleVar>(lineDashPatternSize) { i ->
                        value = lineDashPattern[i]
                    }

                    ImageMagick.DrawSetStrokeDashArray(strokeWand, lineDashPatternSize.toULong(), lineDashArray)
                }
            }

            ImageMagick.PixelSetColor(pixelWand, Color.TRANSPARENT.toCssColor())
            ImageMagick.DrawSetFillColor(strokeWand, pixelWand)

            block(strokeWand)

            ImageMagick.MagickDrawImage(magickWand, strokeWand)
        }
    }

    private fun withFillWand(
        affineTransform: AffineTransform? = null,
        block: (CPointer<ImageMagick.DrawingWand>) -> Unit
    ) {
        withWand(affineTransform) { fillWand ->
            val state = contextState.getCurrentState()
            ImageMagick.PixelSetColor(pixelWand, state.fillColor.toCssColor())
            ImageMagick.DrawSetFillColor(fillWand, pixelWand)
            ImageMagick.PixelSetColor(pixelWand, Color.TRANSPARENT.toCssColor())
            ImageMagick.DrawSetStrokeColor(fillWand, pixelWand)

            block(fillWand)

            ImageMagick.MagickDrawImage(magickWand, fillWand)
        }
    }

    fun drawPath(commands: List<ContextState.PathCommand>, drawingWand: CPointer<ImageMagick.DrawingWand>) {
        var started = false

        ImageMagick.DrawPathStart(drawingWand)

        commands.forEach { command ->
            //ImageMagick.PushDrawingWand(drawingWand)
            //DrawAffineTransofrm(drawingWand, command.transform)
            when (command) {
                is MoveTo -> with(command) {
                    ImageMagick.DrawPathMoveToAbsolute(drawingWand, x, y)
                    started = true
                }

                is LineTo -> with(command) {
                    ImageMagick.DrawPathLineToAbsolute(drawingWand, x, y)
                }

                is Ellipse -> with(command) {
                    val startRad = toRadians(startAngleDeg)
                    val endRad = toRadians(endAngleDeg)

                    var startX = x + radiusX * cos(startRad)
                    var startY = y + radiusY * sin(startRad)
                    var endX = x + radiusX * cos(endRad)
                    var endY = y + radiusY * sin(endRad)

                    println("startX: $startX, startY: $startY, endX: $endX, endY: $endY")

                    val delta = endAngleDeg - startAngleDeg

                    if (!started) {
                        ImageMagick.DrawPathMoveToAbsolute(drawingWand, startX, startY)
                        started = true
                    } else {
                        ImageMagick.DrawPathLineToAbsolute(drawingWand, startX, startY)
                    }

                    if (delta >= 360.0) {
                        // Full circle: break into two arcs

                        val midAngle = startAngleDeg + (if (anticlockwise) -180.0 else 180.0)
                        val midRad = toRadians(midAngle)
                        val midX = x + radiusX * cos(midRad)
                        val midY = y + radiusY * sin(midRad)

                        val sweepFlag = if (anticlockwise) 0u else 1u
                        val largeArcFlag = 0u

                        ImageMagick.DrawPathEllipticArcAbsolute(
                            drawingWand,
                            radiusX,
                            radiusY,
                            rotation,
                            largeArcFlag,
                            sweepFlag,
                            midX,
                            midY
                        )
                        ImageMagick.DrawPathEllipticArcAbsolute(
                            drawingWand,
                            radiusX,
                            radiusY,
                            rotation,
                            largeArcFlag,
                            sweepFlag,
                            endX,
                            endY
                        )
                    } else {
                        val largeArcFlag = if (delta > 180.0) 1u else 0u
                        val sweepFlag = if (anticlockwise) 0u else 1u

                        ImageMagick.DrawPathEllipticArcAbsolute(
                            drawingWand,
                            radiusX,
                            radiusY,
                            rotation,
                            largeArcFlag,
                            sweepFlag,
                            endX,
                            endY
                        )
                    }
                }

                is ClosePath -> ImageMagick.DrawPathClose(drawingWand)
            }

        }
        //ImageMagick.PopDrawingWand(drawingWand)
        ImageMagick.DrawPathFinish(drawingWand)

    }


    companion object {
        val logEnabled = true
        fun log(str: () -> String) {
            if (logEnabled)
                println(str())
        }

        fun DrawAffineTransofrm(wand: CPointer<ImageMagick.DrawingWand>, affine: AffineTransform) {
            memScoped {
                val affineMatrix = alloc<ImageMagick.AffineMatrix>()
                affineMatrix.sx = affine.sx
                affineMatrix.ry = affine.rx
                affineMatrix.rx = affine.ry
                affineMatrix.sy = affine.sy
                affineMatrix.tx = affine.tx
                affineMatrix.ty = affine.ty
                ImageMagick.DrawAffine(wand, affineMatrix.ptr)
            }
        }
    }

}
