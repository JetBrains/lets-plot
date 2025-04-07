/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.canvas

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.Canvas.Snapshot

internal class ScaledContext2d(
    private val ctx: Context2d,
    private val myScale: Double
) : Context2d {

    private fun scaled(value: Double) = myScale * value
    private fun descaled(value: Double) = value / myScale

    private fun scaled(values: DoubleArray): DoubleArray {
        if (myScale == 1.0) {
            return values
        }
        val res = DoubleArray(values.size)
        for (i in values.indices) {
            res[i] = scaled(values[i])
        }
        return res
    }

    private fun scaled(font: Font): Font = font.copy(fontSize = scaled(font.fontSize))

    override fun drawImage(snapshot: Snapshot) = drawImage(snapshot, 0.0, 0.0)

    override fun drawImage(snapshot: Snapshot, x: Double, y: Double) = ctx.drawImage(snapshot, scaled(x), scaled(y))

    override fun drawImage(snapshot: Snapshot, x: Double, y: Double, dw: Double, dh: Double) {
        ctx.drawImage(snapshot, scaled(x), scaled(y), scaled(dw), scaled(dh))
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
        ctx.drawImage(
            snapshot,
            scaled(sx),
            scaled(sy),
            scaled(sw),
            scaled(sh),
            scaled(dx),
            scaled(dy),
            scaled(dw),
            scaled(dh)
        )
    }

    override fun beginPath() = ctx.beginPath()
    override fun closePath() = ctx.closePath()
    override fun stroke() = ctx.stroke()
    override fun fill() = ctx.fill()

    override fun fillRect(x: Double, y: Double, w: Double, h: Double) {
        ctx.fillRect(scaled(x), scaled(y), scaled(w), scaled(h))
    }

    override fun moveTo(x: Double, y: Double) = ctx.moveTo(scaled(x), scaled(y))
    override fun lineTo(x: Double, y: Double) = ctx.lineTo(scaled(x), scaled(y))

    override fun arc(x: Double, y: Double, radius: Double, startAngle: Double, endAngle: Double, anticlockwise: Boolean) {
        ctx.arc(scaled(x), scaled(y), scaled(radius), startAngle, endAngle, anticlockwise)
    }

    override fun ellipse(x: Double, y: Double, radiusX: Double, radiusY: Double, rotation: Double, startAngle: Double, endAngle: Double, anticlockwise: Boolean) {
        ctx.ellipse(scaled(x), scaled(y), scaled(radiusX), scaled(radiusY), rotation, startAngle, endAngle, anticlockwise)
    }

    override fun save() = ctx.save()
    override fun restore() = ctx.restore()
    override fun setFillStyle(color: Color?) = ctx.setFillStyle(color)
    override fun setStrokeStyle(color: Color?) = ctx.setStrokeStyle(color)
    override fun setGlobalAlpha(alpha: Double) = ctx.setGlobalAlpha(alpha)
    override fun setFont(f: Font) = ctx.setFont(scaled(f))
    override fun setLineWidth(lineWidth: Double) = ctx.setLineWidth(scaled(lineWidth))

    override fun strokeRect(x: Double, y: Double, w: Double, h: Double) {
        ctx.strokeRect(scaled(x), scaled(y), scaled(w), scaled(h))
    }

    override fun strokeText(text: String, x: Double, y: Double) = ctx.strokeText(text, scaled(x), scaled(y))
    override fun fillText(text: String, x: Double, y: Double) = ctx.fillText(text, scaled(x), scaled(y))
    override fun scale(x: Double, y: Double) = ctx.scale(x, y)
    override fun scale(xy: Double) = scale(xy, xy)
    override fun rotate(angle: Double) = ctx.rotate(angle)
    override fun translate(x: Double, y: Double) = ctx.translate(scaled(x), scaled(y))

    override fun transform(sx: Double, ry: Double, rx: Double, sy: Double, tx: Double, ty: Double) {
        ctx.transform(sx, ry, rx, sy, scaled(tx), scaled(ty))
    }

    override fun bezierCurveTo(cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, x: Double, y: Double) {
        ctx.bezierCurveTo(scaled(cp1x), scaled(cp1y), scaled(cp2x), scaled(cp2y), scaled(x), scaled(y))
    }

    override fun setLineJoin(lineJoin: LineJoin) = ctx.setLineJoin(lineJoin)
    override fun setLineCap(lineCap: LineCap) = ctx.setLineCap(lineCap)
    override fun setStrokeMiterLimit(miterLimit: Double) = ctx.setStrokeMiterLimit(miterLimit)
    override fun setTextBaseline(baseline: TextBaseline) = ctx.setTextBaseline(baseline)
    override fun setTextAlign(align: TextAlign) = ctx.setTextAlign(align)

    override fun setTransform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double) {
        ctx.setTransform(m11, m12, m21, m22, scaled(dx), scaled(dy))
    }

    override fun fillEvenOdd() = ctx.fillEvenOdd()
    override fun setLineDash(lineDash: DoubleArray) = ctx.setLineDash(scaled(lineDash))
    override fun setLineDashOffset(lineDashOffset: Double) = ctx.setLineDashOffset(lineDashOffset)
    override fun measureTextWidth(str: String): Double = descaled(ctx.measureTextWidth(str))
    override fun measureText(str: String): TextMetrics = ctx.measureText(str)

    override fun clearRect(rect: DoubleRectangle) {
        ctx.clearRect(
            DoubleRectangle(
                scaled(rect.left),
                scaled(rect.top),
                scaled(rect.width),
                scaled(rect.height)
            )
        )
    }
}
