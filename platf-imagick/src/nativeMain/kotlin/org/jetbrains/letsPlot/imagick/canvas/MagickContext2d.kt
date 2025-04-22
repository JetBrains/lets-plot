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
import org.jetbrains.letsPlot.core.canvas.Path.*


class MagickContext2d(
    private val magickWand: CPointer<ImageMagick.MagickWand>?,
    private val stateDelegate: ContextStateDelegate = ContextStateDelegate()
) : Context2d by stateDelegate {
    private val contextState: ContextState get() = stateDelegate.state
    private val pixelWand = ImageMagick.NewPixelWand() ?: error { "Failed to create PixelWand" }

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

    override fun stroke() {
        // Path is already transformed - no need to apply transform again.
        withStrokeWand(AffineTransform.IDENTITY) { strokeWand ->
            drawPath(contextState.getCurrentPath(), strokeWand)
        }
    }

    override fun fill() {
        // Path is already transformed - no need to apply transform again.
        withFillWand(AffineTransform.IDENTITY) { fillWand ->
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

    private fun withWand(affineTransform: AffineTransform? = null, block: (CPointer<ImageMagick.DrawingWand>) -> Unit) {
        val wand = ImageMagick.NewDrawingWand() ?: error { "DrawingWand was null" }
        ImageMagick.PushDrawingWand(wand)

        val state = contextState.getCurrentState()
        log { "withWand(${wand.hashCode()}: clipPath: ${state.clipPath.joinToString()}" }

        state.clipPath.takeIf { it.isNotEmpty() }?.let { clipPath ->
            ImageMagick.DrawPushDefs(wand)
            ImageMagick.DrawPushClipPath(wand, clipPath.hashCode().toString())

            log { "DrawingWand pushed to clip path: ${clipPath.joinToString()}" }
            drawPath(clipPath, wand)

            ImageMagick.DrawPopClipPath(wand)
            ImageMagick.DrawPopDefs(wand)

            ImageMagick.DrawSetClipPath(wand, clipPath.hashCode().toString())
        }

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
        DrawAffineTransofrm(wand, affineTransform ?: state.transform)

        block(wand)

        ImageMagick.PopDrawingWand(wand)

        ImageMagick.MagickDrawImage(magickWand, wand)
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
        }
    }

    private fun drawPath(commands: List<PathCommand>, drawingWand: CPointer<ImageMagick.DrawingWand>) {
        ImageMagick.DrawPathStart(drawingWand)

        commands.forEach { cmd ->
            when (cmd) {
                is MoveTo -> ImageMagick.DrawPathMoveToAbsolute(drawingWand, cmd.x, cmd.y)
                is LineTo -> ImageMagick.DrawPathLineToAbsolute(drawingWand, cmd.x, cmd.y)
                is BezierCurveTo -> cmd.controlPoints.asSequence()
                    .windowed(size = 3, step = 3)
                    .forEach { (cp1, cp2, cp3) ->
                        ImageMagick.DrawPathCurveToAbsolute(
                            drawingWand,
                            cp1.x,
                            cp1.y,
                            cp2.x,
                            cp2.y,
                            cp3.x,
                            cp3.y
                        )
                    }

                is ClosePath -> ImageMagick.DrawPathClose(drawingWand)
            }
        }
        ImageMagick.DrawPathFinish(drawingWand)
    }


    companion object {
        const val logEnabled = false
        fun log(str: () -> String) {
            if (logEnabled)
                println(str())
        }

        fun DrawAffineTransofrm(wand: CPointer<ImageMagick.DrawingWand>, affine: AffineTransform) {
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
    }

}
