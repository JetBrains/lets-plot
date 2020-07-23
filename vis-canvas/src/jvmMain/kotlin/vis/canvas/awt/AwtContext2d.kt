/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas.awt

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.datalore.vis.canvas.Context2d
import java.awt.*
import java.awt.AlphaComposite.SRC_OVER
import java.awt.font.GlyphVector
import java.awt.geom.*
import java.awt.geom.Arc2D.OPEN
import java.awt.Color as AwtColor

internal class AwtContext2d(private val graphics: Graphics2D) : Context2d {
    private var currentPath: GeneralPath? = null
    private var strokeColor: AwtColor = AwtColor.BLACK
    private var fillColor: AwtColor = AwtColor.BLACK
    private var stroke: BasicStroke = BasicStroke()
    private var textBaseline: Context2d.TextBaseline = Context2d.TextBaseline.ALPHABETIC
    private var textAlign: Context2d.TextAlign = Context2d.TextAlign.START
    private var font: Font = Font(Font.SERIF, Font.PLAIN, 10)
    private var globalAlpha: Float = 1f

    init {
        graphics.background = Color.TRANSPARENT.toAwtColor()
    }

    private fun convertLineJoin(lineJoin: Context2d.LineJoin): Int {
        return when (lineJoin) {
            Context2d.LineJoin.BEVEL -> BasicStroke.JOIN_BEVEL
            Context2d.LineJoin.MITER -> BasicStroke.JOIN_MITER
            Context2d.LineJoin.ROUND -> BasicStroke.JOIN_ROUND
        }
    }

    private fun convertLineCap(lineCap: Context2d.LineCap): Int {
        return when (lineCap) {
            Context2d.LineCap.BUTT -> BasicStroke.CAP_BUTT
            Context2d.LineCap.ROUND -> BasicStroke.CAP_ROUND
            Context2d.LineCap.SQUARE -> BasicStroke.CAP_SQUARE
        }
    }

    private fun BasicStroke.change(
        width: Float? = null,
        join: Int? = null,
        cap: Int? = null,
        miterlimit: Float? = null,
        dash: FloatArray? = null,
        dashPhase: Float? = null
    ): BasicStroke {
        return BasicStroke(
            width ?: this.lineWidth,
            cap ?: this.endCap,
            join ?: this.lineJoin,
            miterlimit ?: this.miterLimit,
            dash ?: this.dashArray,
            dashPhase ?: this.dashPhase
        )
    }

    private fun Graphics2D.glyphVector(str: String): GlyphVector {
        return font.createGlyphVector(
            fontRenderContext,
            str
        )
    }

    private fun textPosition(glyphVector: GlyphVector, x: Double, y: Double): DoubleVector {
        val box: Rectangle2D = glyphVector.visualBounds
        val fm = graphics.fontMetrics

        val offsetX = when(textAlign) {
            Context2d.TextAlign.START,
            Context2d.TextAlign.LEFT -> x

            Context2d.TextAlign.CENTER -> x - box.width / 2

            Context2d.TextAlign.END,
            Context2d.TextAlign.RIGHT -> x - box.width
        }

        val offsetY = when(textBaseline) {
            Context2d.TextBaseline.ALPHABETIC -> y -  (fm.leading + fm.ascent)
            Context2d.TextBaseline.BOTTOM -> y - fm.height
            Context2d.TextBaseline.HANGING -> TODO()
            Context2d.TextBaseline.IDEOGRAPHIC -> TODO()
            Context2d.TextBaseline.MIDDLE -> y - (fm.leading + fm.ascent / 2)
            Context2d.TextBaseline.TOP -> y
        }

        return DoubleVector(offsetX, offsetY)
    }

    private fun paintText(text: String, x: Double, y: Double, painter: (Shape) -> Unit) {
        val savedTransform = graphics.transform
        val gv = graphics.glyphVector(text)

        val position = textPosition(gv, x, y)
        graphics.translate(position.x, position.y)

        painter(gv.outline)

        graphics.transform = savedTransform
    }

    private fun Color.toAwtColor(): AwtColor {
        return AwtColor(red, green, blue, alpha)
    }

    override fun clearRect(rect: DoubleRectangle) {
        graphics.clearRect(rect.left.toInt(), rect.top.toInt(), rect.width.toInt(), rect.height.toInt())
    }

    override fun drawImage(snapshot: Canvas.Snapshot, x: Double, y: Double) {
        val awtSnapshot = snapshot as AwtCanvas.AwtSnapshot
        graphics.drawImage(awtSnapshot.image, x.toInt(), y.toInt(), null)
    }

    override fun drawImage(snapshot: Canvas.Snapshot, x: Double, y: Double, dw: Double, dh: Double) {
        val awtSnapshot = snapshot as AwtCanvas.AwtSnapshot
        graphics.drawImage(awtSnapshot.image, x.toInt(), y.toInt(), dw.toInt(), dh.toInt(), null)
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
        val awtSnapshot = snapshot as AwtCanvas.AwtSnapshot
        graphics.drawImage(awtSnapshot.image,
            dx.toInt(), dy.toInt(), dw.toInt() + dx.toInt(), dh.toInt() + dy.toInt(),
            sx.toInt(), sy.toInt(), sw.toInt() + sx.toInt(), sh.toInt() + sy.toInt(), null)
    }

    override fun beginPath() {
        currentPath = GeneralPath()
    }

    override fun closePath() {
        currentPath?.closePath() ?: error("Can't find path")
    }

    override fun stroke() {
        graphics.color = strokeColor
        graphics.draw(currentPath)
    }

    override fun fill() {
        graphics.color = fillColor
        graphics.fill(currentPath)
    }

    override fun fillEvenOdd() {
        graphics.color = fillColor
        currentPath?.windingRule = Path2D.WIND_EVEN_ODD
        graphics.fill(currentPath)
    }

    override fun fillRect(x: Double, y: Double, w: Double, h: Double) {
        graphics.fillRect(x.toInt(), y.toInt(), w.toInt(), h.toInt())
    }

    override fun moveTo(x: Double, y: Double) {
        currentPath?.moveTo(x, y)
    }

    override fun lineTo(x: Double, y: Double) {
        currentPath?.lineTo(x, y)
    }

    override fun arc(
        x: Double,
        y: Double,
        radius: Double,
        startAngle: Double,
        endAngle: Double,
        anticlockwise: Boolean
    ) {
        Arc2D.Double(x, y, radius * 2, radius * 2, startAngle, endAngle, OPEN).let {
            currentPath?.append(it, true)
        }
    }

    override fun save() {
        TODO("Not yet implemented")
    }

    override fun restore() {
        TODO("Not yet implemented")
    }

    override fun setFillStyle(color: Color?) {
        fillColor = color?.toAwtColor() ?: AwtColor.BLACK
    }

    override fun setStrokeStyle(color: Color?) {
        strokeColor = color?.toAwtColor() ?: AwtColor.BLACK
    }

    override fun setGlobalAlpha(alpha: Double) {
        globalAlpha = alpha.toFloat()
        graphics.composite = AlphaComposite.getInstance(SRC_OVER, globalAlpha)
    }

    override fun setFont(f: String) {
        font = Font.getFont(f)
        graphics.font = font
    }

    override fun setLineWidth(lineWidth: Double) {
        stroke = stroke.change(
            width = lineWidth.toFloat()
        )

        graphics.stroke = stroke
    }

    override fun strokeRect(x: Double, y: Double, w: Double, h: Double) {
        graphics.drawRect(x.toInt(), y.toInt(), w.toInt(), h.toInt())
    }

    override fun strokeText(text: String, x: Double, y: Double) {
        paintText(text, x, y, graphics::draw)
    }

    override fun fillText(text: String, x: Double, y: Double) {
        paintText(text, x, y, graphics::fill)
    }

    override fun scale(x: Double, y: Double) {
        graphics.scale(x, y)
    }

    override fun rotate(angle: Double) {
        graphics.rotate(angle)
    }

    override fun translate(x: Double, y: Double) {
        graphics.translate(x, y)
    }

    override fun transform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double) {
        graphics.transform(AffineTransform(m11, m12, m21, m22, dx, dy))
    }

    override fun bezierCurveTo(cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, x: Double, y: Double) {
        currentPath?.curveTo(cp1x, cp1y, cp2x, cp2y, x, y)
    }

    override fun quadraticCurveTo(cpx: Double, cpy: Double, x: Double, y: Double) {
        TODO("Not yet implemented")
    }

    override fun setLineJoin(lineJoin: Context2d.LineJoin) {
        stroke = stroke.change(
            join = convertLineJoin(lineJoin)
        )

        graphics.stroke = stroke
    }

    override fun setLineCap(lineCap: Context2d.LineCap) {
        stroke = stroke.change(
            join = convertLineCap(lineCap)
        )

        graphics.stroke = stroke
    }

    override fun setTextBaseline(baseline: Context2d.TextBaseline) {
        textBaseline = baseline
    }

    override fun setTextAlign(align: Context2d.TextAlign) {
        textAlign = align
    }

    override fun setTransform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double) {
        graphics.transform = AffineTransform(m11, m12, m21, m22, dx, dy)
    }

    override fun setLineDash(lineDash: DoubleArray) {
        stroke = stroke.change(
            dash = lineDash.map { it.toFloat() }.toFloatArray()
        )

        graphics.stroke = stroke
    }

    override fun measureText(str: String): Double {
        return graphics.glyphVector(str).visualBounds.width
    }

    override fun measureText(str: String, font: String): DoubleVector {
        TODO("Not yet implemented")
    }
}