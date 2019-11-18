/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas.javaFx

import javafx.geometry.VPos
import javafx.scene.canvas.GraphicsContext
import javafx.scene.shape.FillRule
import javafx.scene.shape.StrokeLineCap
import javafx.scene.shape.StrokeLineJoin
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.scene.text.TextAlignment
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.spatial.GeoUtils.toDegrees
import jetbrains.datalore.vis.canvas.Canvas.Snapshot
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.datalore.vis.canvas.CssFontParser
import javafx.scene.paint.Color as JavafxColor

internal class JavafxContext2d(private val myContext2d: GraphicsContext) : Context2d {
    private fun convertLineJoin(lineJoin: Context2d.LineJoin): StrokeLineJoin {
        return when (lineJoin) {
            Context2d.LineJoin.BEVEL -> StrokeLineJoin.BEVEL
            Context2d.LineJoin.MITER -> StrokeLineJoin.MITER
            Context2d.LineJoin.ROUND -> StrokeLineJoin.ROUND
        }
    }

    private fun convertLineCap(lineCap: Context2d.LineCap): StrokeLineCap {
        return when (lineCap) {
            Context2d.LineCap.BUTT -> StrokeLineCap.BUTT
            Context2d.LineCap.ROUND -> StrokeLineCap.ROUND
            Context2d.LineCap.SQUARE -> StrokeLineCap.SQUARE
        }
    }

    private fun convertTextBaseline(baseline: Context2d.TextBaseline): VPos {
        return when (baseline) {
            Context2d.TextBaseline.ALPHABETIC -> VPos.BOTTOM
            Context2d.TextBaseline.BOTTOM -> VPos.BOTTOM
            Context2d.TextBaseline.HANGING -> VPos.TOP
            Context2d.TextBaseline.IDEOGRAPHIC -> VPos.BOTTOM
            Context2d.TextBaseline.MIDDLE -> VPos.CENTER
            Context2d.TextBaseline.TOP -> VPos.TOP
        }
    }

    private fun convertTextAlign(align: Context2d.TextAlign): TextAlignment {
        return when (align) {
            Context2d.TextAlign.CENTER -> TextAlignment.CENTER
            Context2d.TextAlign.END -> TextAlignment.RIGHT
            Context2d.TextAlign.LEFT -> TextAlignment.LEFT
            Context2d.TextAlign.RIGHT -> TextAlignment.RIGHT
            Context2d.TextAlign.START -> TextAlignment.LEFT
        }
    }

    private fun convertCssFont(fontString: String): Font {
        val parser = CssFontParser.create(fontString)
                ?: throw IllegalStateException("Could not parse css font string: $fontString")

        val family = parser.fontFamily
        val size = parser.fontSize
        return if (size == null) Font.font(family) else Font.font(family, size)
    }

    override fun drawImage(snapshot: Snapshot, x: Double, y: Double) {
        val javafxSnapshot = snapshot as JavafxCanvas.JavafxSnapshot
        myContext2d.drawImage(javafxSnapshot.image, x, y)
    }

    override fun drawImage(snapshot: Snapshot, x: Double, y: Double, dw: Double, dh: Double) {
        val javafxSnapshot = snapshot as JavafxCanvas.JavafxSnapshot
        myContext2d.drawImage(javafxSnapshot.image, x, y, dw, dh)
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
        val javafxSnapshot = snapshot as JavafxCanvas.JavafxSnapshot
        myContext2d.drawImage(javafxSnapshot.image, sx, sy, sw, sh, dx, dy, dw, dh)
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

    override fun arc(x: Double, y: Double, radius: Double, startAngle: Double, endAngle: Double) {
        myContext2d.arc(x, y, radius, radius, -toDegrees(startAngle), toDegrees(startAngle) - toDegrees(endAngle))
    }

    override fun save() {
        myContext2d.save()
    }

    override fun restore() {
        myContext2d.restore()
    }

    override fun setFillStyle(color: String?) {
        myContext2d.fill = if (color != null) JavafxColor.valueOf(color) else null
    }

    override fun setStrokeStyle(color: String?) {
        myContext2d.stroke = if (color != null) JavafxColor.valueOf(color) else null
    }

    override fun setGlobalAlpha(alpha: Double) {
        myContext2d.globalAlpha = alpha
    }

    override fun setFont(f: String) {
        myContext2d.font = convertCssFont(f)
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

    override fun transform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double) {
        myContext2d.transform(m11, m12, m21, m22, dx, dy)
    }

    override fun bezierCurveTo(cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, x: Double, y: Double) {
        myContext2d.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, x, y)
    }

    override fun quadraticCurveTo(cpx: Double, cpy: Double, x: Double, y: Double) {
        myContext2d.quadraticCurveTo(cpx, cpy, x, y)
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
        myContext2d.setLineDashes(*lineDash)
    }

    override fun measureText(str: String): Double {
        val text = Text(str)
        text.font = myContext2d.font
        return text.layoutBounds.width
    }

    override fun measureText(str: String, font: String): DoubleVector {
        val text = Text(str)
        text.font = convertCssFont(font)
        return DoubleVector(text.layoutBounds.width, text.layoutBounds.height)
    }

    override fun clearRect(rect: DoubleRectangle) {
        myContext2d.clearRect(rect.left, rect.top, rect.width, rect.height)
    }

    private fun fill(fillRule: FillRule) {
        myContext2d.fillRule = fillRule
        myContext2d.fill()
    }
}
