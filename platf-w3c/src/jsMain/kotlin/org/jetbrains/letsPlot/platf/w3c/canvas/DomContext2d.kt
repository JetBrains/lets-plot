/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.w3c.canvas

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.*
import org.jetbrains.letsPlot.core.canvas.*
import org.jetbrains.letsPlot.core.canvas.Canvas.Snapshot
import org.jetbrains.letsPlot.platf.w3c.canvas.DomCanvas.DomSnapshot
import org.jetbrains.letsPlot.platf.w3c.dom.css.enumerables.CssLineCap
import org.jetbrains.letsPlot.platf.w3c.dom.css.enumerables.CssLineJoin
import org.jetbrains.letsPlot.platf.w3c.dom.css.enumerables.CssTextAlign
import org.jetbrains.letsPlot.platf.w3c.dom.css.enumerables.CssTextBaseLine
import org.w3c.dom.*

internal class DomContext2d(
    private val ctx: CanvasRenderingContext2D
) : Context2d {
    private fun convertLineJoin(lineJoin: LineJoin): CssLineJoin {
        return when (lineJoin) {
            LineJoin.BEVEL -> CssLineJoin.BEVEL
            LineJoin.MITER -> CssLineJoin.MITER
            LineJoin.ROUND -> CssLineJoin.ROUND
        }
    }

    private fun convertLineCap(lineCap: LineCap): CssLineCap {
        return when (lineCap) {
            LineCap.BUTT -> CssLineCap.BUTT
            LineCap.ROUND -> CssLineCap.ROUND
            LineCap.SQUARE -> CssLineCap.SQUARE
        }
    }

    private fun convertTextBaseline(baseline: TextBaseline): CssTextBaseLine {
        return when (baseline) {
            TextBaseline.ALPHABETIC -> CssTextBaseLine.ALPHABETIC
            TextBaseline.BOTTOM -> CssTextBaseLine.BOTTOM
            TextBaseline.MIDDLE -> CssTextBaseLine.MIDDLE
            TextBaseline.TOP -> CssTextBaseLine.TOP
        }
    }

    private fun convertTextAlign(align: TextAlign): CssTextAlign {
        return when (align) {
            TextAlign.CENTER -> CssTextAlign.CENTER
            TextAlign.END -> CssTextAlign.END
            TextAlign.START -> CssTextAlign.START
        }
    }

    override fun drawImage(snapshot: Snapshot) {
        drawImage(snapshot, 0.0, 0.0)
    }

    override fun drawImage(snapshot: Snapshot, x: Double, y: Double) {
        val domSnapshot = snapshot as DomSnapshot
        ctx.drawImage(domSnapshot.canvasElement, x, y)
    }

    override fun drawImage(snapshot: Snapshot, x: Double, y: Double, dw: Double, dh: Double) {
        val domSnapshot = snapshot as DomSnapshot
        ctx.drawImage(domSnapshot.canvasElement, x, y, dw, dh)
    }

    override fun drawImage(
        snapshot: Snapshot,
        sx: Double,
        sy: Double,
        sw: Double,
        sh: Double,
        dx: Double,
        dy: Double,
        dw: Double,
        dh: Double
    ) {
        val domSnapshot = snapshot as DomSnapshot
        ctx.drawImage(domSnapshot.canvasElement, sx, sy, sw, sh, dx, dy, dw, dh)
    }

    override fun beginPath() = ctx.beginPath()
    override fun scale(xy: Double) = scale(xy, xy)
    override fun closePath() = ctx.closePath()
    override fun stroke() = ctx.stroke()
    override fun fill() = ctx.fill(CanvasFillRule.NONZERO)
    override fun fillEvenOdd() = ctx.fill(CanvasFillRule.EVENODD)
    override fun fillRect(x: Double, y: Double, w: Double, h: Double) = ctx.fillRect(x, y, w, h)
    override fun moveTo(x: Double, y: Double) = ctx.moveTo(x, y)
    override fun lineTo(x: Double, y: Double) = ctx.lineTo(x, y)

    override fun arc(
        x: Double,
        y: Double,
        radius: Double,
        startAngle: Double,
        endAngle: Double,
        anticlockwise: Boolean
    ) {
        ctx.arc(x, y, radius, startAngle, endAngle, anticlockwise)
    }

    override fun save() = ctx.save()
    override fun restore() = ctx.restore()
    override fun setFillStyle(color: Color?) {
        ctx.fillStyle = color?.toCssColor()
    }

    override fun setStrokeStyle(color: Color?) {
        ctx.strokeStyle = color?.toCssColor()
    }

    override fun setGlobalAlpha(alpha: Double) {
        ctx.globalAlpha = alpha
    }

    private fun Font.toCssString(): String {
        val weight: String = when (fontWeight) {
            FontWeight.NORMAL -> "normal"
            FontWeight.BOLD -> "bold"
        }

        val style: String = when (fontStyle) {
            FontStyle.NORMAL -> "normal"
            FontStyle.ITALIC -> "italic"
        }

        return "$style $weight ${fontSize}px $fontFamily"
    }

    override fun setFont(f: Font) {
        ctx.font = f.toCssString()
    }

    override fun setLineWidth(lineWidth: Double) {
        ctx.lineWidth = lineWidth
    }

    override fun strokeRect(x: Double, y: Double, w: Double, h: Double) = ctx.strokeRect(x, y, w, h)
    override fun strokeText(text: String, x: Double, y: Double) = ctx.strokeText(text, x, y)
    override fun fillText(text: String, x: Double, y: Double) = ctx.fillText(text, x, y)
    override fun scale(x: Double, y: Double) = ctx.scale(x, y)
    override fun rotate(angle: Double) = ctx.rotate(angle)
    override fun translate(x: Double, y: Double) = ctx.translate(x, y)

    override fun transform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double) {
        ctx.transform(m11, m12, m21, m22, dx, dy)
    }

    override fun bezierCurveTo(cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, x: Double, y: Double) {
        ctx.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, x, y)
    }

    override fun setLineJoin(lineJoin: LineJoin) {
        ctx.lineJoin = convertLineJoin(lineJoin)
    }

    override fun setLineCap(lineCap: LineCap) {
        ctx.lineCap = convertLineCap(lineCap)
    }

    override fun setTextBaseline(baseline: TextBaseline) {
        ctx.textBaseline = convertTextBaseline(baseline)
    }

    override fun setTextAlign(align: TextAlign) {
        ctx.textAlign = convertTextAlign(align)
    }

    override fun setTransform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double) {
        ctx.setTransform(m11, m12, m21, m22, dx, dy)
    }

    override fun setLineDash(lineDash: DoubleArray) = ctx.setLineDash(lineDash.toTypedArray())
    override fun measureText(str: String): Double = ctx.measureText(str).width
    override fun clearRect(rect: DoubleRectangle) = ctx.clearRect(rect.left, rect.top, rect.width, rect.height)
}
