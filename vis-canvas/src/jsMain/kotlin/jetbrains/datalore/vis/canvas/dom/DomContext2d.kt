/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas.dom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.js.css.enumerables.CssLineCap
import jetbrains.datalore.base.js.css.enumerables.CssLineJoin
import jetbrains.datalore.base.js.css.enumerables.CssTextAlign
import jetbrains.datalore.base.js.css.enumerables.CssTextBaseLine
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.Canvas.Snapshot
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.datalore.vis.canvas.dom.DomCanvas.DomSnapshot
import org.w3c.dom.*

internal class DomContext2d(private val myContext2d: CanvasRenderingContext2D) :
    Context2d {
    private fun convertLineJoin(lineJoin: Context2d.LineJoin): CssLineJoin {
        return when (lineJoin) {
            Context2d.LineJoin.BEVEL -> CssLineJoin.BEVEL
            Context2d.LineJoin.MITER -> CssLineJoin.MITER
            Context2d.LineJoin.ROUND -> CssLineJoin.ROUND
        }
    }

    private fun convertLineCap(lineCap: Context2d.LineCap): CssLineCap {
        return when (lineCap) {
            Context2d.LineCap.BUTT -> CssLineCap.BUTT
            Context2d.LineCap.ROUND -> CssLineCap.ROUND
            Context2d.LineCap.SQUARE -> CssLineCap.SQUARE
        }
    }

    private fun convertTextBaseline(baseline: Context2d.TextBaseline): CssTextBaseLine {
        return when (baseline) {
            Context2d.TextBaseline.ALPHABETIC -> CssTextBaseLine.ALPHABETIC
            Context2d.TextBaseline.BOTTOM -> CssTextBaseLine.BOTTOM
            Context2d.TextBaseline.MIDDLE -> CssTextBaseLine.MIDDLE
            Context2d.TextBaseline.TOP -> CssTextBaseLine.TOP
        }
    }

    private fun convertTextAlign(align: Context2d.TextAlign): CssTextAlign {
        return when (align) {
            Context2d.TextAlign.CENTER -> CssTextAlign.CENTER
            Context2d.TextAlign.END -> CssTextAlign.END
            Context2d.TextAlign.START -> CssTextAlign.START
        }
    }

    override fun drawImage(snapshot: Snapshot, x: Double, y: Double) {
        val domSnapshot = snapshot as DomSnapshot
        myContext2d.drawImage(domSnapshot.canvasElement, x, y)
    }

    override fun drawImage(snapshot: Snapshot, x: Double, y: Double, dw: Double, dh: Double) {
        val domSnapshot = snapshot as DomSnapshot
        myContext2d.drawImage(domSnapshot.canvasElement, x, y, dw, dh)
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
        myContext2d.drawImage(domSnapshot.canvasElement, sx, sy, sw, sh, dx, dy, dw, dh)
    }

    override fun beginPath() {
        myContext2d.beginPath()
    }

    override fun closePath() {
        myContext2d.closePath()
    }

    override fun stroke() {
        myContext2d.stroke()
    }

    override fun fill() {
        myContext2d.fill(CanvasFillRule.NONZERO)
    }

    override fun fillEvenOdd() {
        myContext2d.fill(CanvasFillRule.EVENODD)
    }

    override fun fillRect(x: Double, y: Double, w: Double, h: Double) {
        myContext2d.fillRect(x, y, w, h)
    }

    override fun moveTo(x: Double, y: Double) {
        myContext2d.moveTo(x, y)
    }

    override fun lineTo(x: Double, y: Double) {
        myContext2d.lineTo(x, y)
    }

    override fun arc(x: Double, y: Double, radius: Double, startAngle: Double, endAngle: Double, anticlockwise: Boolean) {
        myContext2d.arc(x, y, radius, startAngle, endAngle, anticlockwise)
    }

    override fun save() {
        myContext2d.save()
    }

    override fun restore() {
        myContext2d.restore()
    }

    override fun setFillStyle(color: Color?) {
        myContext2d.fillStyle = color?.toCssColor()
    }

    override fun setStrokeStyle(color: Color?) {
        myContext2d.strokeStyle = color?.toCssColor()
    }

    override fun setGlobalAlpha(alpha: Double) {
        myContext2d.globalAlpha = alpha
    }

    private fun Context2d.Font.toCssString(): String {
        val weight: String = when (fontWeight) {
            Context2d.Font.FontWeight.NORMAL -> "normal"
            Context2d.Font.FontWeight.BOLD -> "bold"
        }

        val style: String = when (fontStyle) {
            Context2d.Font.FontStyle.NORMAL -> "normal"
            Context2d.Font.FontStyle.ITALIC -> "italic"
        }

        return "$style $weight ${fontSize}px $fontFamily"
    }

    override fun setFont(f: Context2d.Font) {
        myContext2d.font = f.toCssString()
    }

    override fun setLineWidth(lineWidth: Double) {
        myContext2d.lineWidth = lineWidth
    }

    override fun strokeRect(x: Double, y: Double, w: Double, h: Double) {
        myContext2d.strokeRect(x, y, w, h)
    }

    override fun strokeText(text: String, x: Double, y: Double) {
        myContext2d.strokeText(text, x, y)
    }

    override fun fillText(text: String, x: Double, y: Double) {
        myContext2d.fillText(text, x, y)
    }

    override fun scale(x: Double, y: Double) {
        myContext2d.scale(x, y)
    }

    override fun rotate(angle: Double) {
        myContext2d.rotate(angle)
    }

    override fun translate(x: Double, y: Double) {
        myContext2d.translate(x, y)
    }

    override fun transform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double) {
        myContext2d.transform(m11, m12, m21, m22, dx, dy)
    }

    override fun bezierCurveTo(cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, x: Double, y: Double) {
        myContext2d.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, x, y)
    }

    override fun setLineJoin(lineJoin: Context2d.LineJoin) {
        myContext2d.lineJoin = convertLineJoin(lineJoin)
    }

    override fun setLineCap(lineCap: Context2d.LineCap) {
        myContext2d.lineCap = convertLineCap(lineCap)
    }

    override fun setTextBaseline(baseline: Context2d.TextBaseline) {
        myContext2d.textBaseline = convertTextBaseline(baseline)
    }

    override fun setTextAlign(align: Context2d.TextAlign) {
        myContext2d.textAlign = convertTextAlign(align)
    }

    override fun setTransform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double) {
        myContext2d.setTransform(m11, m12, m21, m22, dx, dy)
    }

    override fun setLineDash(lineDash: DoubleArray) {
        myContext2d.setLineDash(lineDash.toTypedArray())
    }

    override fun measureText(str: String): Double {
        return myContext2d.measureText(str).width
    }

    override fun clearRect(rect: DoubleRectangle) {
        myContext2d.clearRect(rect.left, rect.top, rect.width, rect.height)
    }
}
