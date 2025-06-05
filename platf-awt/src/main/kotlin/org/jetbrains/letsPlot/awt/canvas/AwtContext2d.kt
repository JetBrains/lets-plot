/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.canvas

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.toDegrees
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.*
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.Font
import java.awt.*
import java.awt.AlphaComposite.SRC_OVER
import java.awt.font.GlyphVector
import java.awt.geom.*
import java.awt.geom.AffineTransform.getRotateInstance
import java.awt.geom.Arc2D.OPEN
import kotlin.math.abs
import java.awt.Color as AwtColor
import java.awt.Font as AwtFont

internal class AwtContext2d(private val graphics: Graphics2D) : Context2d {
    private var currentPath: GeneralPath = GeneralPath()
    private var state = ContextState()
    private val stateStack = ArrayList<ContextState>()
    private var clipStack = ArrayList<GeneralPath>()

    init {
        RenderingHints(
            mapOf(
                RenderingHints.KEY_ANTIALIASING to RenderingHints.VALUE_ANTIALIAS_ON,
                RenderingHints.KEY_FRACTIONALMETRICS to RenderingHints.VALUE_FRACTIONALMETRICS_ON,
                RenderingHints.KEY_STROKE_CONTROL to RenderingHints.VALUE_STROKE_PURE
            )
        ).let(graphics::setRenderingHints)

        graphics.background = Color.TRANSPARENT.toAwtColor()
        setLineCap(LineCap.BUTT)
    }

    internal data class ContextState(
        var numClipPath: Int = 0,
        var strokeColor: AwtColor = AwtColor.BLACK,
        var fillColor: AwtColor = AwtColor.BLACK,
        var stroke: BasicStroke = BasicStroke(),
        var textBaseline: TextBaseline = TextBaseline.ALPHABETIC,
        var textAlign: TextAlign = TextAlign.START,
        var font: AwtFont = AwtFont(AwtFont.SERIF, AwtFont.PLAIN, 10),
        var globalAlpha: Float = 1f,
        var transform: AffineTransform = AffineTransform(1.0, 0.0, 0.0, 1.0, 0.0, 0.0)
    )

    private fun ContextState.applyTransform(x: Double, y: Double): Point2D {
        return transform.transform(Point2D.Double(x, y), null)
    }

    private fun Graphics2D.glyphVector(str: String): GlyphVector = font.createGlyphVector(fontRenderContext, str)

    private fun paintText(text: String, x: Double, y: Double, fill: Boolean) {
        val gv = graphics.glyphVector(text)
        val position = textPosition(gv, x, y)

        val savedTransform = graphics.transform
        graphics.translate(position.x, position.y)
        if (fill) {
            graphics.color = state.fillColor
            graphics.fill(gv.outline)
        } else {
            graphics.color = state.strokeColor
            graphics.draw(gv.outline)
        }
        graphics.transform = savedTransform
    }

    private fun textPosition(glyphVector: GlyphVector, x: Double, y: Double): DoubleVector {
        val box: Rectangle2D = glyphVector.visualBounds
        val fm = graphics.fontMetrics

        val offsetX = when (state.textAlign) {
            TextAlign.START -> x
            TextAlign.CENTER -> x - box.width / 2
            TextAlign.END -> x - box.width
        }

        val offsetY = when (state.textBaseline) {
            TextBaseline.ALPHABETIC -> y
            TextBaseline.BOTTOM -> y - fm.descent
            TextBaseline.MIDDLE -> y + (fm.leading + fm.ascent - fm.descent) / 2
            TextBaseline.TOP -> y + fm.leading + fm.ascent
        }

        return DoubleVector(offsetX, offsetY)
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
        graphics.drawImage(
            awtSnapshot.image,
            dx.toInt(), dy.toInt(), dw.toInt() + dx.toInt(), dh.toInt() + dy.toInt(),
            sx.toInt(), sy.toInt(), sw.toInt() + sx.toInt(), sh.toInt() + sy.toInt(), null
        )
    }

    override fun beginPath() {
        currentPath = GeneralPath()
    }

    override fun closePath() {
        currentPath.closePath()
    }

    override fun clip() {
        clipStack.add(currentPath)
        state.numClipPath = clipStack.size

        val currentTransform = graphics.transform
        graphics.transform = AffineTransform()
        graphics.clip = currentPath
        graphics.transform = currentTransform
    }

    override fun stroke() {
        graphics.color = state.strokeColor
        graphics.stroke = state.stroke.copy(width = (state.stroke.lineWidth * minOf(state.transform.scaleX, state.transform.scaleY)).toFloat())
        graphics.paintPath(currentPath, Graphics2D::draw)
        graphics.stroke = state.stroke
    }

    override fun fill() {
        graphics.color = state.fillColor
        graphics.fillPath(currentPath)
    }

    override fun fillEvenOdd() {
        currentPath.windingRule = Path2D.WIND_EVEN_ODD
        graphics.color = state.fillColor
        graphics.fillPath(currentPath)
    }

    override fun fillRect(x: Double, y: Double, w: Double, h: Double) {
        graphics.color = state.fillColor
        graphics.fillRect(x.toInt(), y.toInt(), w.toInt(), h.toInt())
    }

    override fun moveTo(x: Double, y: Double) {
        val p = state.applyTransform(x, y)
        currentPath.moveTo(p.x, p.y)
    }

    override fun lineTo(x: Double, y: Double) {
        val p = state.applyTransform(x, y)
        currentPath.lineTo(p.x, p.y)
    }

    override fun bezierCurveTo(cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, x: Double, y: Double) {
        val trCp1x: Double
        val trCp1y: Double
        state.applyTransform(cp1x, cp1y).let { trCp1x = it.x; trCp1y = it.y }

        val trCp2x: Double
        val trCp2y: Double
        state.applyTransform(cp2x, cp2y).let { trCp2x = it.x; trCp2y = it.y }

        val trX: Double
        val trY: Double
        state.applyTransform(x, y).let { trX = it.x; trY = it.y }

        currentPath.curveTo(trCp1x, trCp1y, trCp2x, trCp2y, trX, trY)
    }

    override fun arc(
        x: Double,
        y: Double,
        radius: Double,
        startAngle: Double,
        endAngle: Double,
        anticlockwise: Boolean
    ) {
        val path = buildArc(x, y, radius, radius, 0.0, toDegrees(startAngle), toDegrees(endAngle), anticlockwise)
        currentPath.append(path, true)
    }

    override fun ellipse(
        x: Double,
        y: Double,
        radiusX: Double,
        radiusY: Double,
        rotation: Double,
        startAngle: Double,
        endAngle: Double,
        anticlockwise: Boolean
    ) {
        val path = buildArc(
            x,
            y,
            radiusX,
            radiusY,
            toDegrees(rotation),
            toDegrees(startAngle),
            toDegrees(endAngle),
            anticlockwise
        )
        currentPath.append(path, true)
    }

    private fun buildArc(
        x: Double, y: Double,
        radiusX: Double, radiusY: Double,
        rotation: Double,
        startAngle: Double, endAngle: Double,
        anticlockwise: Boolean
    ): Path2D.Double {
        var start = startAngle % 360
        var end = endAngle % 360
        val fullCircle = (start == end && startAngle != endAngle)

        if (!anticlockwise && start > end) start -= 360.0
        if (anticlockwise && start < end) end -= 360.0

        var length = end - start
        if (anticlockwise) length = -abs(length)

        val path = Path2D.Double()

        fun appendArcSegment(startDeg: Double, extentDeg: Double) {
            val arc = Arc2D.Double(
                x - radiusX, y - radiusY,
                radiusX * 2, radiusY * 2,
                -startDeg, -extentDeg,
                OPEN
            )
            path.append(arc, true)
        }

        if (fullCircle) {
            appendArcSegment(start, 180.0)
            appendArcSegment(start + 180.0, 180.0)
        } else {
            appendArcSegment(start, length)
        }

        if (rotation != 0.0) {
            path.transform(getRotateInstance(Math.toRadians(rotation), x, y))
        }

        path.transform(graphics.transform)
        return path
    }

    override fun save() {
        stateStack.add(state.copy())
    }

    override fun restore() {
        stateStack.lastOrNull()
            ?.let {
                state = it

                if (state.numClipPath < clipStack.size) {
                    clipStack.subList(state.numClipPath, clipStack.size).clear()
                }

                graphics.clip = clipStack.lastOrNull()
                graphics.transform = state.transform
                graphics.stroke = state.stroke
                graphics.font = state.font
                graphics.composite = AlphaComposite.getInstance(SRC_OVER, state.globalAlpha)

                stateStack.removeAt(stateStack.lastIndex)
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
        state.stroke = state.stroke.copy(
            width = lineWidth.toFloat()
        )

        graphics.stroke = state.stroke
    }

    override fun strokeRect(x: Double, y: Double, w: Double, h: Double) {
        graphics.color = state.strokeColor
        graphics.drawRect(x.toInt(), y.toInt(), w.toInt(), h.toInt())
    }

    override fun strokeText(text: String, x: Double, y: Double) {
        paintText(text, x, y, fill = false)
    }

    override fun fillText(text: String, x: Double, y: Double) {
        paintText(text, x, y, fill = true)
    }

    override fun scale(xy: Double) {
        scale(xy, xy)
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

    override fun transform(sx: Double, ry: Double, rx: Double, sy: Double, tx: Double, ty: Double) {
        val t = AffineTransform(sx, ry, rx, sy, tx, ty)

        graphics.transform(t)
        state.transform = graphics.transform
    }

    override fun setLineJoin(lineJoin: LineJoin) {
        state.stroke = state.stroke.copy(
            join = convertLineJoin(lineJoin)
        )

        graphics.stroke = state.stroke
    }

    override fun setLineCap(lineCap: LineCap) {
        state.stroke = state.stroke.copy(
            cap = convertLineCap(lineCap)
        )

        graphics.stroke = state.stroke
    }

    override fun setStrokeMiterLimit(miterLimit: Double) {
        state.stroke = state.stroke.copy(
            miterlimit = miterLimit.toFloat()
        )

        graphics.stroke = state.stroke
    }

    override fun setTextBaseline(baseline: TextBaseline) {
        state.textBaseline = baseline
    }

    override fun setTextAlign(align: TextAlign) {
        state.textAlign = align
    }

    override fun setTransform(m00: Double, m10: Double, m01: Double, m11: Double, m02: Double, m12: Double) {
        graphics.transform = AffineTransform(m00, m10, m01, m11, m02, m12)
        state.transform = graphics.transform
    }

    override fun setLineDash(lineDash: DoubleArray) {
        state.stroke = state.stroke.copy(
            dash = if (lineDash.isEmpty()) null else lineDash.map(Double::toFloat).toFloatArray()
        )

        graphics.stroke = state.stroke
    }

    override fun setLineDashOffset(lineDashOffset: Double) {
        state.stroke = state.stroke.copy(
            dashPhase = lineDashOffset.toFloat()
        )

        graphics.stroke = state.stroke
    }

    override fun measureTextWidth(str: String): Double {
        return graphics.glyphVector(str).visualBounds.width
    }

    override fun measureText(str: String): TextMetrics {
        return TextMetrics(
            ascent = graphics.fontMetrics.ascent.toDouble(),
            descent = graphics.fontMetrics.descent.toDouble(),
            bbox = graphics.glyphVector(str).logicalBounds.let { DoubleRectangle.XYWH(it.x, it.y, it.width, it.height) }
        )
    }

    private companion object {

        // path already transformed, to draw it correctly Graphics2D transform should be reset.
        private fun Graphics2D.paintPath(path: Path2D, painter: (Graphics2D, Shape) -> Unit) {
            val currentTransform = transform
            transform = AffineTransform()
            painter(this, path)
            transform = currentTransform
        }

        private fun Graphics2D.fillPath(path: Path2D) {
            paintPath(path, Graphics2D::fill)
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

            return AwtFont(fontFamily, weight or style, fontSize.toInt())
        }

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

        private fun BasicStroke.copy(
            width: Float = this.lineWidth,
            join: Int = this.lineJoin,
            cap: Int = this.endCap,
            miterlimit: Float = this.miterLimit,
            dash: FloatArray? = this.dashArray,
            dashPhase: Float = this.dashPhase
        ): BasicStroke {
            return BasicStroke(width, cap, join, maxOf(1f, miterlimit), dash, dashPhase)
        }


    }
}