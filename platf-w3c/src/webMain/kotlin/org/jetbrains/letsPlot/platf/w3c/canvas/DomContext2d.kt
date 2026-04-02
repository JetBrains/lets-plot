/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(ExperimentalWasmJsInterop::class)

package org.jetbrains.letsPlot.platf.w3c.canvas

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.*
import org.jetbrains.letsPlot.core.canvas.Canvas.Snapshot
import org.jetbrains.letsPlot.platf.w3c.canvas.DomCanvas.DomSnapshot
import org.jetbrains.letsPlot.platf.w3c.dom.css.enumerables.CssLineCap
import org.jetbrains.letsPlot.platf.w3c.dom.css.enumerables.CssLineJoin
import org.jetbrains.letsPlot.platf.w3c.dom.css.enumerables.CssTextAlign
import org.jetbrains.letsPlot.platf.w3c.dom.css.enumerables.CssTextBaseLine
import org.jetbrains.letsPlot.platf.w3c.interop.toJsArray
import org.w3c.dom.*
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.toJsString
import kotlin.math.PI

internal class DomContext2d(
    private val ctx: CanvasRenderingContext2D,
    override val contentScale: Double,
    private val fontManager: DomFontManager
) : Context2d {

    init {
        applyContentScale()
    }

    fun onCanvasResized() {
        applyContentScale()
    }

    private fun applyContentScale() {
        ctx.setTransform(1.0, 0.0, 0.0, 1.0, 0.0, 0.0)
        if (contentScale != 1.0) {
            ctx.scale(contentScale, contentScale)
        }
    }

    private val logEnabled = false
    private fun log(msg: () -> String) {
        if (logEnabled)
            println(msg())
    }

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
        log { "DomContext2d.drawImage(snapshot) size=${snapshot.size}, transform=${ctx.getTransform()}" }
        drawImage(snapshot, 0.0, 0.0)
    }

    override fun drawImage(snapshot: Snapshot, x: Double, y: Double) {
        log { "DomContext2d.drawImage(snapshot) x=$x, y=$y, size=${snapshot.size}, transform=${ctx.getTransform()}" }
        val domSnapshot = snapshot as DomSnapshot
        ctx.imageSmoothingEnabled = false
        ctx.drawImage(domSnapshot.canvasElement, x, y)
    }

    override fun drawImage(snapshot: Snapshot, x: Double, y: Double, dw: Double, dh: Double) {
        log { "DomContext2d.drawImage(snapshot) x=$x, y=$y, dw=$dw, dh=$dh, size=${snapshot.size}, transform=${ctx.getTransform()}" }
        val domSnapshot = snapshot as DomSnapshot
        ctx.imageSmoothingEnabled = false
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
        log { "DomContext2d.drawImage(snapshot) sx=$sx, sy=$sy, sw=$sw, sh=$sh, dx=$dx, dy=$dy, dw=$dw, dh=$dh, size=${snapshot.size}, transform=${ctx.getTransform()}" }
        val domSnapshot = snapshot as DomSnapshot
        ctx.imageSmoothingEnabled = false
        ctx.drawImage(domSnapshot.canvasElement, sx, sy, sw, sh, dx, dy, dw, dh)
    }

    override fun beginPath() = ctx.beginPath()
    override fun scale(xy: Double) = scale(xy, xy)
    override fun closePath() = ctx.closePath()
    override fun clip() =  ctx.clip()
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

    override fun drawCircle(x: Double, y: Double, radius: Double) {
        ctx.beginPath()
        ctx.arc(x, y, radius, 0.0, 2 * PI, false)
        ctx.fill(CanvasFillRule.NONZERO)
        ctx.stroke()
    }

    override fun ellipse(x: Double, y: Double, radiusX: Double, radiusY: Double, rotation: Double, startAngle: Double, endAngle: Double, anticlockwise: Boolean) {
        ctx.ellipse(
            x = x,
            y = y,
            radiusX = radiusX,
            radiusY = radiusY,
            rotation = rotation,
            startAngle = startAngle,
            endAngle = endAngle,
            anticlockwise = anticlockwise
        )
    }
    override fun measureText(str: String): org.jetbrains.letsPlot.core.canvas.TextMetrics {
        val metrics = ctx.measureText(str)
        val ascent = when {
            metrics.actualBoundingBoxAscent > 0.0 -> metrics.actualBoundingBoxAscent
            metrics.fontBoundingBoxAscent > 0.0 -> metrics.fontBoundingBoxAscent
            else -> metrics.emHeightAscent
        }
        val descent = when {
            metrics.actualBoundingBoxDescent > 0.0 -> metrics.actualBoundingBoxDescent
            metrics.fontBoundingBoxDescent > 0.0 -> metrics.fontBoundingBoxDescent
            else -> metrics.emHeightDescent
        }
        val left = metrics.actualBoundingBoxLeft
        val right = if (metrics.actualBoundingBoxRight > 0.0) {
            metrics.actualBoundingBoxRight
        } else {
            (metrics.width - left).coerceAtLeast(0.0)
        }

        return org.jetbrains.letsPlot.core.canvas.TextMetrics(
            ascent = ascent,
            descent = descent,
            bbox = DoubleRectangle.XYWH(
                -left,
                -ascent,
                left + right,
                ascent + descent
            )
        )
    }

    override fun save() = ctx.save()
    override fun restore() = ctx.restore()
    override fun setFillStyle(color: Color?) {
        ctx.fillStyle = color?.toCssColor()?.toJsString()
    }

    override fun setStrokeStyle(color: Color?) {
        ctx.strokeStyle = color?.toCssColor()?.toJsString()
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
        ctx.font = fontManager.resolveFont(f).toCssString()
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

    override fun transform(sx: Double, ry: Double, rx: Double, sy: Double, tx: Double, ty: Double) {
        ctx.transform(sx, ry, rx, sy, tx, ty)
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

    override fun setStrokeMiterLimit(miterLimit: Double) {
        ctx.miterLimit = miterLimit
    }

    override fun setTextBaseline(baseline: TextBaseline) {
        ctx.textBaseline = convertTextBaseline(baseline)
    }

    override fun setTextAlign(align: TextAlign) {
        ctx.textAlign = convertTextAlign(align)
    }

    override fun setTransform(m00: Double, m10: Double, m01: Double, m11: Double, m02: Double, m12: Double) {
        ctx.setTransform(m00, m10, m01, m11, m02, m12)
    }

    override fun setLineDash(lineDash: DoubleArray) = ctx.setLineDash(lineDash.toJsArray())
    override fun setLineDashOffset(lineDashOffset: Double) {
        ctx.lineDashOffset = lineDashOffset
    }
    override fun measureTextWidth(str: String): Double = ctx.measureText(str).width

    override fun clearRect(rect: DoubleRectangle) = clearRect(rect.left, rect.top, rect.width, rect.height)
    override fun clearRect(x: Double, y: Double, w: Double, h: Double) {
        ctx.clearRect(x, y, w, h)
    }

    override fun dispose() {
        // no-op
    }
}
