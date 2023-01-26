/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas.awt

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.math.toDegrees
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.*
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.datalore.vis.canvas.Font
import java.awt.*
import java.awt.AlphaComposite.SRC_OVER
import java.awt.font.GlyphVector
import java.awt.geom.*
import java.awt.geom.Arc2D.OPEN
import java.awt.Color as AwtColor

typealias AwtFont = java.awt.Font

internal class AwtContext2d(private val graphics: Graphics2D) : Context2d {
    private var currentPath: GeneralPath? = null
    private var state = ContextState()
    private val stack = ArrayList<ContextState>()

    init {
        RenderingHints(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        ).let(graphics::setRenderingHints)

        graphics.background = Color.TRANSPARENT.toAwtColor()
        setLineCap(LineCap.BUTT)
    }

    private data class ContextState(
        var strokeColor: AwtColor = AwtColor.BLACK,
        var fillColor: AwtColor = AwtColor.BLACK,
        var stroke: BasicStroke = BasicStroke(),
        var textBaseline: TextBaseline = TextBaseline.ALPHABETIC,
        var textAlign: TextAlign = TextAlign.START,
        var font: AwtFont = AwtFont(AwtFont.SERIF, AwtFont.PLAIN, 10),
        var globalAlpha: Float = 1f,
        var transform: AffineTransform = AffineTransform(1.0, 0.0, 0.0, 1.0, 0.0, 0.0)
    )

    private fun convertLineJoin(lineJoin: LineJoin): Int {
        return when (lineJoin) {
            LineJoin.BEVEL -> BasicStroke.JOIN_BEVEL
            LineJoin.MITER -> BasicStroke.JOIN_MITER
            LineJoin.ROUND -> BasicStroke.JOIN_ROUND
        }
    }

    private fun convertLineCap(lineCap: LineCap): Int {
        return when (lineCap) {
            LineCap.BUTT -> BasicStroke.CAP_BUTT
            LineCap.ROUND -> BasicStroke.CAP_ROUND
            LineCap.SQUARE -> BasicStroke.CAP_SQUARE
        }
    }

    private fun BasicStroke.change(
        width: Float = this.lineWidth,
        join: Int = this.lineJoin,
        cap: Int = this.endCap,
        miterlimit: Float = this.miterLimit,
        dash: FloatArray? = this.dashArray,
        dashPhase: Float = this.dashPhase
    ): BasicStroke {
        return BasicStroke(width, cap, join, miterlimit, dash, dashPhase)
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

        val offsetX = when(state.textAlign) {
            TextAlign.START -> x

            TextAlign.CENTER -> x - box.width / 2

            TextAlign.END -> x - box.width
        }

        val offsetY = when(state.textBaseline) {
            TextBaseline.ALPHABETIC -> y
            TextBaseline.BOTTOM -> y - fm.descent
            TextBaseline.MIDDLE -> y + (fm.leading + fm.ascent - fm.descent) / 2
            TextBaseline.TOP -> y + fm.leading + fm.ascent
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

    private fun Font.toAwtFont(): AwtFont {
        val weight = when (fontWeight) {
            FontWeight.NORMAL -> AwtFont.PLAIN
            FontWeight.BOLD -> AwtFont.BOLD
        }

        val style = when (fontStyle) {
            FontStyle.NORMAL -> AwtFont.PLAIN
            FontStyle.ITALIC -> AwtFont.ITALIC
        }

        return AwtFont(fontFamily,weight or style, fontSize.toInt())
    }

    override fun clearRect(rect: DoubleRectangle) {
        graphics.clearRect(rect.left.toInt(), rect.top.toInt(), rect.width.toInt(), rect.height.toInt())
    }

    override fun drawImage(snapshot: Canvas.Snapshot) {
        drawImage(snapshot, 0.0, 0.0)
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
        graphics.color = state.strokeColor
        graphics.draw(currentPath)
    }

    override fun fill() {
        graphics.color = state.fillColor
        graphics.fill(currentPath)
    }

    override fun fillEvenOdd() {
        graphics.color = state.fillColor
        currentPath?.windingRule = Path2D.WIND_EVEN_ODD
        graphics.fill(currentPath)
    }

    override fun fillRect(x: Double, y: Double, w: Double, h: Double) {
        graphics.color = state.fillColor
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

        Arc2D.Double(x - radius, y - radius, radius * 2, radius * 2, -start, -length, OPEN).let {
            currentPath?.append(it, true)
        }
    }

    override fun save() {
        stack.add(state.copy())
    }

    override fun restore() {
        stack.lastOrNull()
            ?.let {
                state = it

                graphics.transform = state.transform
                graphics.stroke = state.stroke
                graphics.font = state.font
                graphics.composite = AlphaComposite.getInstance(SRC_OVER, state.globalAlpha)

                stack.removeAt(stack.lastIndex)
            }
    }

    override fun setFillStyle(color: Color?) {
        state.fillColor = color?.toAwtColor() ?: AwtColor.BLACK
    }

    override fun setStrokeStyle(color: Color?) {
        state.strokeColor = color?.toAwtColor() ?: AwtColor.BLACK
    }

    override fun setGlobalAlpha(alpha: Double) {
        state.globalAlpha = alpha.toFloat()
        graphics.composite = AlphaComposite.getInstance(SRC_OVER, state.globalAlpha)
    }

    override fun setFont(f: Font) {
        state.font = f.toAwtFont()
        graphics.font = state.font
    }

    override fun setLineWidth(lineWidth: Double) {
        state.stroke = state.stroke.change(
            width = lineWidth.toFloat()
        )

        graphics.stroke = state.stroke
    }

    override fun strokeRect(x: Double, y: Double, w: Double, h: Double) {
        graphics.color = state.strokeColor
        graphics.drawRect(x.toInt(), y.toInt(), w.toInt(), h.toInt())
    }

    override fun strokeText(text: String, x: Double, y: Double) {
        graphics.color = state.strokeColor
        paintText(text, x, y, graphics::draw)
    }

    override fun scale(xy: Double) {
        scale(xy, xy)
    }

    override fun fillText(text: String, x: Double, y: Double) {
        graphics.color = state.fillColor
        paintText(text, x, y, graphics::fill)
    }

    override fun scale(x: Double, y: Double) {
        graphics.scale(x, y)
        state.transform = graphics.transform
    }

    override fun rotate(angle: Double) {
        graphics.rotate(angle)
        state.transform = graphics.transform
    }

    override fun translate(x: Double, y: Double) {
        graphics.translate(x, y)
        state.transform = graphics.transform
    }

    override fun transform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double) {
        graphics.transform(AffineTransform(m11, m12, m21, m22, dx, dy))
        state.transform = graphics.transform
    }

    override fun bezierCurveTo(cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, x: Double, y: Double) {
        currentPath?.curveTo(cp1x, cp1y, cp2x, cp2y, x, y)
    }

    override fun setLineJoin(lineJoin: LineJoin) {
        state.stroke = state.stroke.change(
            join = convertLineJoin(lineJoin)
        )

        graphics.stroke = state.stroke
    }

    override fun setLineCap(lineCap: LineCap) {
        state.stroke = state.stroke.change(
            cap = convertLineCap(lineCap)
        )

        graphics.stroke = state.stroke
    }

    override fun setTextBaseline(baseline: TextBaseline) {
        state.textBaseline = baseline
    }

    override fun setTextAlign(align: TextAlign) {
        state.textAlign = align
    }

    override fun setTransform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double) {
        graphics.transform = AffineTransform(m11, m12, m21, m22, dx, dy)
        state.transform = graphics.transform
    }

    override fun setLineDash(lineDash: DoubleArray) {
        state.stroke = state.stroke.change(
            dash = if (lineDash.isEmpty()) null else lineDash.map(Double::toFloat).toFloatArray()
        )

        graphics.stroke = state.stroke
    }

    override fun measureText(str: String): Double {
        return graphics.glyphVector(str).visualBounds.width
    }
}