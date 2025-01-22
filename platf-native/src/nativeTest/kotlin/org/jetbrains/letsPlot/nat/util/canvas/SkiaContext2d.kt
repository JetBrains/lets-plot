package org.jetbrains.letsPlot.nat.util.canvas


import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.*
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.Font
import org.jetbrains.skia.*

/*
 * Copyright (c) 2025 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

typealias SkCanvas = org.jetbrains.skia.Canvas

class SkiaContext2d(
    private val canvas: SkCanvas
) : Context2d {
    class Style(
        val paint: Paint = Paint()
    ) {
        var fillStyle: Color? = null
        var strokeStyle: Color? = null
        var path: Path = Path()

        val fillPaint: Paint
            get() {
                fillStyle?.let {
                    paint.color4f = it.asSkiaColor
                }

                paint.setStroke(false)
                return paint
            }


        val strokePaint: Paint
            get() {
                strokeStyle?.let {
                    paint.color4f = it.asSkiaColor
                }

                paint.setStroke(true)
                return paint
            }

        fun copy(): Style {
            val copy = Style(paint.makeClone())
            copy.fillStyle = fillStyle
            copy.strokeStyle = strokeStyle
            return copy
        }
    }

    private val stack = mutableListOf<Style>(Style())
    private val style: Style
        get() = stack.last()

    override fun arc(x: Double, y: Double, radius: Double, startAngle: Double, endAngle: Double, anticlockwise: Boolean) {
        canvas.drawArc(
            left = x.toFloat() - radius.toFloat(),
            top = y.toFloat() - radius.toFloat(),
            right = x.toFloat() + radius.toFloat(),
            bottom = y.toFloat() + radius.toFloat(),
            startAngle.toFloat(),
            endAngle.toFloat().takeIf { anticlockwise } ?: -endAngle.toFloat(),
            false,
            style.strokePaint
        )
        canvas.drawArc(
            left = x.toFloat() - radius.toFloat(),
            top = y.toFloat() - radius.toFloat(),
            right = x.toFloat() + radius.toFloat(),
            bottom = y.toFloat() + radius.toFloat(),
            startAngle.toFloat(),
            endAngle.toFloat().takeIf { anticlockwise } ?: -endAngle.toFloat(),
            false,
            style.fillPaint
        )
    }

    override fun beginPath() {
        println("beginPath() - not implemented")
    }

    override fun bezierCurveTo(cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, x: Double, y: Double) {
        println("bezierCurveTo() - not implemented")
    }

    override fun clearRect(rect: DoubleRectangle) {
        canvas.clipRect(
            org.jetbrains.skia.Rect.makeXYWH(
                rect.left.toFloat(),
                rect.top.toFloat(),
                rect.width.toFloat(),
                rect.height.toFloat()
            )
        )
    }

    override fun closePath() {
        println("closePath() - not implemented")
    }

    override fun drawImage(snapshot: Canvas.Snapshot) {
        println("drawImage() - not implemented")
    }

    override fun drawImage(snapshot: Canvas.Snapshot, x: Double, y: Double) {
        println("drawImage() - not implemented")
    }

    override fun drawImage(snapshot: Canvas.Snapshot, x: Double, y: Double, dw: Double, dh: Double) {
        println("drawImage() - not implemented")
    }

    override fun drawImage(snapshot: Canvas.Snapshot, sx: Double, sy: Double, sw: Double, sh: Double, dx: Double, dy: Double, dw: Double, dh: Double) {
        println("drawImage() - not implemented")
    }

    override fun ellipse(x: Double, y: Double, radiusX: Double, radiusY: Double) {
        println("ellipse() - not implemented")
    }

    override fun fill() {
        println("fill() - not implemented")
    }

    override fun fillEvenOdd() {
        println("fillEvenOdd() - not implemented")
    }

    override fun fillRect(x: Double, y: Double, w: Double, h: Double) {
        canvas.drawRect(
            org.jetbrains.skia.Rect.makeXYWH(
                x.toFloat(),
                y.toFloat(),
                w.toFloat(),
                h.toFloat()
            ),
            style.fillPaint
        )
    }

    override fun fillText(text: String, x: Double, y: Double) {
        println("fillText() - not implemented")
        canvas.drawTextLine(
            TextLine.make(text, org.jetbrains.skia.Font()),
            x.toFloat(),
            y.toFloat(),
            style.fillPaint
        )
    }

    override fun lineTo(x: Double, y: Double) {
        println("lineTo() - not implemented")
    }

    override fun measureText(str: String): TextMetrics {
        println("measureText() - not implemented")
        return TextMetrics(0.0, 0.0, DoubleRectangle.XYWH(0, 0, 0, 0))
    }

    override fun measureTextWidth(str: String): Double {
        println("measureTextWidth() - not implemented")
        return 0.0
    }

    override fun moveTo(x: Double, y: Double) {
        println("moveTo() - not implemented")
    }

    override fun restore() {
        canvas.restore()
        stack.removeLast().also { check(stack.isNotEmpty()) }
    }

    override fun rotate(angle: Double) {
        canvas.rotate(angle.toFloat())
    }

    override fun save() {
        canvas.save()
        stack.add(style.copy())
    }

    override fun scale(xy: Double) {
        scale(xy, xy)
    }

    override fun scale(x: Double, y: Double) {
        canvas.scale(x.toFloat(), y.toFloat())
    }

    override fun setFillStyle(color: Color?) {
        style.fillStyle = color
    }

    override fun setFont(f: Font) {
        println("setFont() - not implemented")
    }

    override fun setGlobalAlpha(alpha: Double) {
        println("setGlobalAlpha() - not implemented")
    }

    override fun setLineCap(lineCap: LineCap) {
        println("setLineCap() - not implemented")
    }

    override fun setLineDash(lineDash: DoubleArray) {
        println("setLineDash() - not implemented")
    }

    override fun setLineDashOffset(lineDashOffset: Double) {
        println("setLineDashOffset() - not implemented")
    }

    override fun setLineJoin(lineJoin: LineJoin) {
        style.paint.strokeJoin = when (lineJoin) {
            LineJoin.BEVEL -> PaintStrokeJoin.BEVEL
            LineJoin.ROUND -> PaintStrokeJoin.ROUND
            LineJoin.MITER -> PaintStrokeJoin.MITER
        }
    }

    override fun setLineWidth(lineWidth: Double) {
        style.paint.strokeWidth = lineWidth.toFloat()
    }

    override fun setStrokeMiterLimit(miterLimit: Double) {
        style.paint.strokeMiter = miterLimit.toFloat()
    }

    override fun setStrokeStyle(color: Color?) {
        style.strokeStyle = color
    }

    override fun setTextAlign(align: TextAlign) {
        println("setTextAlign() - not implemented")
    }

    override fun setTextBaseline(baseline: TextBaseline) {
        println("setTextBaseline() - not implemented")
    }

    override fun setTransform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double) {
        canvas.setMatrix(
            org.jetbrains.skia.Matrix33(
                m11.toFloat(),
                m12.toFloat(),
                m21.toFloat(),
                m22.toFloat(),
                dx.toFloat(),
                dy.toFloat()
            )
        )
    }

    override fun stroke() {
        println("stroke() - not implemented")
    }

    override fun strokeRect(x: Double, y: Double, w: Double, h: Double) {
        println("strokeRect() - paint not implemented")
        canvas.drawRect(
            org.jetbrains.skia.Rect.makeXYWH(
                x.toFloat(),
                y.toFloat(),
                w.toFloat(),
                h.toFloat()
            ),
            Paint().apply {
                this.setStroke(true)
            }
        )
    }

    override fun strokeText(text: String, x: Double, y: Double) {
        println("strokeText() - not implemented")
        canvas.drawTextLine(
            TextLine.make(text, org.jetbrains.skia.Font()),
            x.toFloat(),
            y.toFloat(),
            style.strokePaint
        )
    }

    override fun transform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double) {
        println("transform() - not implemented")
    }

    override fun translate(x: Double, y: Double) {
        canvas.translate(x.toFloat(), y.toFloat())
    }
}

internal val Color.asSkiaColor
    get() = Color4f(
        r = (red / 255.0).toFloat(),
        g = (green / 255.0).toFloat(),
        b = (blue / 255.0).toFloat(),
        a = (alpha / 255.0).toFloat()
    )

