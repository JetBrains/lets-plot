/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.canvas

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.*
import org.jetbrains.letsPlot.core.canvas.Path2d.*
import java.awt.AlphaComposite
import java.awt.AlphaComposite.SRC_OVER
import java.awt.BasicStroke
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.font.GlyphVector
import java.awt.geom.*
import java.util.*
import java.awt.Color as AwtColor
import java.awt.Font as AwtFont

internal class AwtContext2d(
    initialGraphics: Graphics2D,
    contentScale: Double,
    private val stateDelegate: ContextStateDelegate = ContextStateDelegate(contentScale = contentScale),
) : Context2d by stateDelegate {
    private var graphics: Graphics2D = (initialGraphics.create() as Graphics2D).apply {
        stroke = BasicStroke()
    }

    private val graphicsStack = Stack<Graphics2D>()

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

    override fun dispose() {
        graphics.dispose()
    }

    override fun clearRect(rect: DoubleRectangle) {
        graphics.clearRect(rect.left.toInt(), rect.top.toInt(), rect.width.toInt(), rect.height.toInt())
    }

    override fun drawImage(snapshot: Canvas.Snapshot) {
        log { "AwtContext2d.drawImage(snapshot) size=${snapshot.size}, transform=${graphics.transform}" }

        drawImage(snapshot, 0.0, 0.0)
    }

    override fun drawImage(snapshot: Canvas.Snapshot, x: Double, y: Double) {
        log { "AwtContext2d.drawImage(snapshot, x=$x, y=$y) size=${snapshot.size}, transform=${graphics.transform}" }

        val awtSnapshot = snapshot as AwtCanvas.AwtSnapshot
        graphics.drawImage(awtSnapshot.image, x.toInt(), y.toInt(), null)
    }

    override fun drawImage(snapshot: Canvas.Snapshot, x: Double, y: Double, dw: Double, dh: Double) {
        log { "AwtContext2d.drawImage(snapshot, x=$x, y=$y, dw=$dw, dh=$dh) size=${snapshot.size}, transform=${graphics.transform}" }

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
        log {
            "AwtContext2d.drawImage(snapshot, sx=$sx, sy=$sy, sw=$sw, sh=$sh, dx=$dx, dy=$dy, dw=$dw, dh=$dh) " +
                    "size=${snapshot.size}, transform=${graphics.transform}"
        }

        val awtSnapshot = snapshot as AwtCanvas.AwtSnapshot
        graphics.drawImage(
            awtSnapshot.image,
            dx.toInt(), dy.toInt(), dw.toInt() + dx.toInt(), dh.toInt() + dy.toInt(),
            sx.toInt(), sy.toInt(), sw.toInt() + sx.toInt(), sh.toInt() + sy.toInt(), null
        )
    }

    override fun drawCircle(x: Double, y: Double, radius: Double) {
        val circle = Arc2D.Double(x - radius, y - radius, 2 * radius, 2 * radius, 0.0, 360.0, Arc2D.OPEN)

        withFillGraphics { g -> g.fill(circle) }
        withStrokeGraphics { g -> g.draw(circle) }
    }

    override fun save() {
        stateDelegate.save()
        graphicsStack.push(graphics.create() as Graphics2D)
    }

    override fun restore() {
        stateDelegate.restore()

        graphics = graphicsStack.pop()
    }

    override fun setFillStyle(color: Color?) {
        stateDelegate.setFillStyle(color)
    }

    override fun setStrokeStyle(color: Color?) {
        stateDelegate.setStrokeStyle(color)
    }

    override fun setLineWidth(lineWidth: Double) {
        stateDelegate.setLineWidth(lineWidth)

        graphics.stroke = (graphics.stroke as BasicStroke).copy(width = lineWidth.toFloat())
    }

    override fun setLineDash(lineDash: DoubleArray) {
        stateDelegate.setLineDash(lineDash)

        graphics.stroke = (graphics.stroke as BasicStroke)
            .copy(dash = if (lineDash.isEmpty()) null else lineDash.map(Double::toFloat).toFloatArray())
    }

    override fun setLineDashOffset(lineDashOffset: Double) {
        stateDelegate.setLineDashOffset(lineDashOffset)

        graphics.stroke = (graphics.stroke as BasicStroke)
            .copy(dashPhase = lineDashOffset.toFloat())
    }

    override fun setLineCap(lineCap: LineCap) {
        stateDelegate.setLineCap(lineCap)

        graphics.stroke = (graphics.stroke as BasicStroke).copy(
            cap = convertLineCap(lineCap)
        )
    }

    override fun setLineJoin(lineJoin: LineJoin) {
        stateDelegate.setLineJoin(lineJoin)

        graphics.stroke = (graphics.stroke as BasicStroke).copy(
            join = convertLineJoin(lineJoin)
        )
    }

    override fun setStrokeMiterLimit(miterLimit: Double) {
        stateDelegate.setStrokeMiterLimit(miterLimit)

        graphics.stroke = (graphics.stroke as BasicStroke).copy(
            miterlimit = miterLimit.toFloat()
        )
    }

    override fun setFont(f: Font) {
        stateDelegate.setFont(f)

        graphics.font = f.toAwtFont()
    }

    override fun setGlobalAlpha(alpha: Double) {
        stateDelegate.setGlobalAlpha(alpha)

        graphics.composite = AlphaComposite.getInstance(SRC_OVER, alpha.toFloat())
    }

    override fun stroke() {
        // Make ctm identity. null for degenerate case, e.g., scale(0, 0) - skip drawing.
        val inverseCtmTransform = stateDelegate.getCTM().inverse() ?: return

        withStrokeGraphics { g ->
            val path = drawPath(stateDelegate.getCurrentPath(), inverseCtmTransform)
            g.draw(path)
        }
    }

    override fun fill() {
        // Make ctm identity. null for degenerate case, e.g., scale(0, 0) - skip drawing.
        val inverseCtmTransform = stateDelegate.getCTM().inverse() ?: return

        withFillGraphics { g ->
            val path = drawPath(stateDelegate.getCurrentPath(), inverseCtmTransform)
            g.fill(path)
        }
    }

    override fun fillEvenOdd() {
        // Make ctm identity. null for degenerate case, e.g., scale(0, 0) - skip drawing.
        val inverseCtmTransform = stateDelegate.getCTM().inverse() ?: return

        withFillGraphics { g ->
            val path = drawPath(stateDelegate.getCurrentPath(), inverseCtmTransform)
            path.windingRule = Path2D.WIND_EVEN_ODD

            g.fill(path)
        }
    }

    private fun drawText(g: Graphics2D, text: String, x: Double, y: Double, fill: Boolean) {
        val gv = g.glyphVector(text)
        val fm = g.fontMetrics

        val offsetX = when (stateDelegate.getTextAlign()) {
            TextAlign.START -> 0.0
            TextAlign.CENTER -> -gv.visualBounds.width / 2
            TextAlign.END -> -gv.visualBounds.width
        }

        val offsetY = when (stateDelegate.getTextBaseline()) {
            TextBaseline.ALPHABETIC -> 0
            TextBaseline.BOTTOM -> -fm.descent
            TextBaseline.MIDDLE -> (fm.leading + fm.ascent - fm.descent) / 2
            TextBaseline.TOP -> fm.leading + fm.ascent
        }

        g.translate(x + offsetX, y + offsetY)

        if (fill) {
            g.fill(gv.outline)
        } else {
            g.draw(gv.outline)
        }
    }

    override fun fillText(text: String, x: Double, y: Double) {
        withFillGraphics { g ->
            drawText(g, text, x, y, fill = true)
        }
    }

    override fun strokeText(text: String, x: Double, y: Double) {
        withStrokeGraphics { g ->
            drawText(g, text, x, y, fill = false)
        }
    }

    override fun fillRect(x: Double, y: Double, w: Double, h: Double) {
        withFillGraphics { g ->
            val rect = Rectangle2D.Double(x, y, w, h)
            g.fill(rect)
        }
    }

    override fun strokeRect(x: Double, y: Double, w: Double, h: Double) {
        withStrokeGraphics { g ->
            val rect = Rectangle2D.Double(x, y, w, h)
            g.draw(rect)
        }
    }

    override fun clip() {
        stateDelegate.clip()

        val clipPath = stateDelegate.getClipPath()
        if (clipPath.isEmpty) {
            // No clip path defined, nothing to do.
            return
        }

        val inverseCTMTransform = stateDelegate.getCTM().inverse() ?: return

        val path = drawPath(clipPath.getCommands(), inverseCTMTransform)
        graphics.clip(path)
    }

    override fun transform(sx: Double, ry: Double, rx: Double, sy: Double, tx: Double, ty: Double) {
        stateDelegate.transform(sx, ry, rx, sy, tx, ty)

        val t = AffineTransform(sx, ry, rx, sy, tx, ty)
        graphics.transform(t)
    }

    override fun scale(xy: Double) {
        stateDelegate.scale(xy)

        graphics.scale(xy, xy)
    }

    override fun scale(x: Double, y: Double) {
        stateDelegate.scale(x, y)

        graphics.scale(x, y)
    }

    override fun rotate(angle: Double) {
        stateDelegate.rotate(angle)

        graphics.rotate(angle)
    }

    override fun translate(x: Double, y: Double) {
        stateDelegate.translate(x, y)

        graphics.translate(x, y)
    }

    override fun setTransform(m00: Double, m10: Double, m01: Double, m11: Double, m02: Double, m12: Double) {
        stateDelegate.setTransform(m00, m10, m01, m11, m02, m12)
        graphics.transform = AffineTransform(m00, m10, m01, m11, m02, m12)
    }

    override fun measureText(str: String): TextMetrics {
        return TextMetrics(
            ascent = graphics.fontMetrics.ascent.toDouble(),
            descent = graphics.fontMetrics.descent.toDouble(),
            bbox = graphics.glyphVector(str).logicalBounds.let { DoubleRectangle.XYWH(it.x, it.y, it.width, it.height) }
        )
    }


    override fun measureTextWidth(str: String): Double {
        return graphics.glyphVector(str).visualBounds.width
    }

    private fun withStrokeGraphics(block: (Graphics2D) -> Unit) {
        val g = graphics.create() as Graphics2D
        g.color = stateDelegate.getStrokeColor().toAwtColor()
        block(g)
        g.dispose()
    }

    private fun withFillGraphics(block: (Graphics2D) -> Unit) {
        val g = graphics.create() as Graphics2D
        g.color = stateDelegate.getFillColor().toAwtColor()
        block(g)
        g.dispose()
    }

    private companion object {
        private const val LOG_ENABLED = false
        private fun log(str: () -> String) {
            if (LOG_ENABLED)
                println(str())
        }

        fun drawPath(commands: List<PathCommand>, transform: org.jetbrains.letsPlot.commons.geometry.AffineTransform): GeneralPath {
            if (commands.isEmpty()) {
                return GeneralPath()
            }

            log { "drawPath: commands=${commands.joinToString { it.toString() }}, transform=${transform.repr()}" }

            val path = GeneralPath()

            commands
                .asSequence()
                .map { cmd -> cmd.transform(transform) }
                .forEach { cmd ->
                    when (cmd) {
                        is MoveTo -> path.moveTo(cmd.x, cmd.y)
                        is LineTo -> path.lineTo(cmd.x, cmd.y)
                        is CubicCurveTo -> {
                            cmd.controlPoints.asSequence()
                                .windowed(size = 3, step = 3)
                                .forEach { (cp1, cp2, cp3) ->
                                    path.curveTo(cp1.x, cp1.y, cp2.x, cp2.y, cp3.x, cp3.y)
                                }
                        }

                        is ClosePath -> path.closePath()
                    }
                }

            return path
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


    private fun Graphics2D.glyphVector(str: String): GlyphVector = font.createGlyphVector(fontRenderContext, str)
}
