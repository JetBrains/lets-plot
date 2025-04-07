/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.jfx.canvas

import javafx.geometry.VPos
import javafx.scene.canvas.GraphicsContext
import javafx.scene.shape.FillRule
import javafx.scene.shape.StrokeLineCap
import javafx.scene.shape.StrokeLineJoin
import javafx.scene.text.Font
import javafx.scene.text.FontPosture
import javafx.scene.text.Text
import javafx.scene.text.TextAlignment
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.intern.math.toDegrees
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.*

typealias JfxFont = Font
typealias JfxFontWeight = javafx.scene.text.FontWeight

internal class JavafxContext2d(private val myContext2d: GraphicsContext) : Context2d {

    init {
        setLineCap(LineCap.BUTT)
    }

    private fun convertLineJoin(lineJoin: LineJoin): StrokeLineJoin {
        return when (lineJoin) {
            LineJoin.BEVEL -> StrokeLineJoin.BEVEL
            LineJoin.MITER -> StrokeLineJoin.MITER
            LineJoin.ROUND -> StrokeLineJoin.ROUND
        }
    }

    private fun convertLineCap(lineCap: LineCap): StrokeLineCap {
        return when (lineCap) {
            LineCap.BUTT -> StrokeLineCap.BUTT
            LineCap.ROUND -> StrokeLineCap.ROUND
            LineCap.SQUARE -> StrokeLineCap.SQUARE
        }
    }

    private fun convertTextBaseline(baseline: TextBaseline): VPos {
        return when (baseline) {
            TextBaseline.ALPHABETIC -> VPos.BASELINE
            TextBaseline.BOTTOM -> VPos.BOTTOM
            TextBaseline.MIDDLE -> VPos.CENTER
            TextBaseline.TOP -> VPos.TOP
        }
    }

    private fun convertTextAlign(align: TextAlign): TextAlignment {
        return when (align) {
            TextAlign.CENTER -> TextAlignment.CENTER
            TextAlign.END -> TextAlignment.RIGHT
            TextAlign.START -> TextAlignment.LEFT
        }
    }

    private fun Color.toJavafxColor(): javafx.scene.paint.Color {
        return javafx.scene.paint.Color(red / 255.0, green / 255.0, blue / 255.0, alpha / 255.0)
    }

    override fun drawImage(snapshot: Canvas.Snapshot) {
        drawImage(snapshot, 0.0, 0.0)
    }

    override fun drawImage(snapshot: Canvas.Snapshot, x: Double, y: Double) {
        val javafxSnapshot = snapshot as JavafxCanvas.JavafxSnapshot
        myContext2d.drawImage(javafxSnapshot.image, x, y)
    }

    override fun drawImage(snapshot: Canvas.Snapshot, x: Double, y: Double, dw: Double, dh: Double) {
        val javafxSnapshot = snapshot as JavafxCanvas.JavafxSnapshot
        myContext2d.drawImage(javafxSnapshot.image, x, y, dw, dh)
    }

    override fun drawImage(
        snapshot: Canvas.Snapshot,
        sx: Double,
        sy: Double,
        sw: Double,
        sh: Double,
        dx: Double,
        dy: Double,
        dw: Double,
        dh: Double
    ) {
        val javafxSnapshot = snapshot as JavafxCanvas.JavafxSnapshot
        myContext2d.drawImage(javafxSnapshot.image, sx, sy, sw, sh, dx, dy, dw, dh)
    }

    override fun beginPath() {
        myContext2d.beginPath()
    }

    override fun scale(xy: Double) {
        scale(xy, xy)
    }

    override fun closePath() {
        myContext2d.closePath()
    }

    override fun stroke() {
        myContext2d.stroke()
    }

    override fun fill() {
        fill(FillRule.NON_ZERO)
    }

    override fun fillEvenOdd() {
        fill(FillRule.EVEN_ODD)
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
        var start = toDegrees(startAngle) % 360
        var end = toDegrees(endAngle) % 360
        var length: Double

        if (start == end && startAngle != endAngle) {
            length = 360.0
        } else {
            if (start > end && end < 0) {
                end += 360
            } else if (start > end && end >=0 ) {
                start -= 360
            }

            length = end - start
        }

        if (anticlockwise) {
            if (length != 0.0 && length != 360.0) {
                length -= 360
            }
        }

        myContext2d.arc(x, y, radius, radius, -start, -length )
    }

    override fun ellipse(x: Double, y: Double, radiusX: Double, radiusY: Double, rotation: Double, startAngle: Double, endAngle: Double, anticlockwise: Boolean) {
        TODO("Fix ellipse in FX")
        myContext2d.beginPath()
        myContext2d.moveTo(x + radiusX, y)
        myContext2d.arc(x, y, radiusX, radiusY, 0.0, 360.0)
        myContext2d.closePath()
    }

    override fun save() {
        myContext2d.save()
    }

    override fun restore() {
        myContext2d.restore()
    }

    override fun setFillStyle(color: Color?) {
        myContext2d.fill = color?.toJavafxColor() ?: javafx.scene.paint.Color.BLACK
    }

    override fun setStrokeStyle(color: Color?) {
        myContext2d.stroke = color?.toJavafxColor() ?: javafx.scene.paint.Color.BLACK
    }

    override fun setGlobalAlpha(alpha: Double) {
        myContext2d.globalAlpha = alpha
    }

    private fun org.jetbrains.letsPlot.core.canvas.Font.toJavaFxFont(): JfxFont {
        val weight: JfxFontWeight = when (fontWeight) {
            FontWeight.NORMAL -> JfxFontWeight.NORMAL
            FontWeight.BOLD -> JfxFontWeight.BOLD
        }

        val posture: FontPosture = when (fontStyle) {
            FontStyle.NORMAL -> FontPosture.REGULAR
            FontStyle.ITALIC -> FontPosture.ITALIC
        }

        // In Javafx FontPosture will not work, for fonts without italics
        return Font.font(fontFamily, weight, posture, fontSize)
    }

    override fun setFont(f: org.jetbrains.letsPlot.core.canvas.Font) {
        myContext2d.font = f.toJavaFxFont()
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
        myContext2d.rotate(toDegrees(angle))
    }

    override fun translate(x: Double, y: Double) {
        myContext2d.translate(x, y)
    }

    override fun transform(sx: Double, ry: Double, rx: Double, sy: Double, tx: Double, ty: Double) {
        myContext2d.transform(sx, ry, rx, sy, tx, ty)
    }

    override fun bezierCurveTo(cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, x: Double, y: Double) {
        myContext2d.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, x, y)
    }

    override fun setLineJoin(lineJoin: LineJoin) {
        myContext2d.lineJoin = convertLineJoin(lineJoin)
    }

    override fun setLineCap(lineCap: LineCap) {
        myContext2d.lineCap = convertLineCap(lineCap)
    }

    override fun setStrokeMiterLimit(miterLimit: Double) {
        myContext2d.miterLimit = miterLimit
    }

    override fun setTextBaseline(baseline: TextBaseline) {
        myContext2d.textBaseline = convertTextBaseline(baseline)
    }

    override fun setTextAlign(align: TextAlign) {
        myContext2d.textAlign = convertTextAlign(align)
    }

    override fun setTransform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double) {
        myContext2d.setTransform(m11, m12, m21, m22, dx, dy)
    }

    override fun setLineDash(lineDash: DoubleArray) {
        myContext2d.setLineDashes(*lineDash)
    }

    override fun setLineDashOffset(lineDashOffset: Double) {
        myContext2d.lineDashOffset = lineDashOffset
    }

    override fun measureTextWidth(str: String): Double {
        val text = Text(str)
        text.font = myContext2d.font
        return text.layoutBounds.width
    }

    override fun measureText(str: String): TextMetrics {
        TODO("Not yet implemented")
    }

    override fun clearRect(rect: DoubleRectangle) {
        myContext2d.clearRect(rect.left, rect.top, rect.width, rect.height)
    }

    private fun fill(fillRule: FillRule) {
        myContext2d.fillRule = fillRule
        myContext2d.fill()
    }
}