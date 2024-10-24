/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.canvas

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.Canvas.Snapshot

interface Context2d {
    fun clearRect(rect: DoubleRectangle)
    fun drawImage(snapshot: Snapshot)
    fun drawImage(snapshot: Snapshot, x: Double, y: Double)
    fun drawImage(snapshot: Snapshot, x: Double, y: Double, dw: Double, dh: Double)
    fun drawImage(
        snapshot: Snapshot,
        sx: Double,
        sy: Double,
        sw: Double,
        sh: Double,
        dx: Double,
        dy: Double,
        dw: Double,
        dh: Double
    )

    fun beginPath()
    fun closePath()
    fun clip()
    fun stroke()
    fun fill()
    fun fillEvenOdd()
    fun fillRect(x: Double, y: Double, w: Double, h: Double)
    fun moveTo(x: Double, y: Double)
    fun lineTo(x: Double, y: Double)
    fun arc(x: Double, y: Double, radius: Double, startAngle: Double, endAngle: Double, anticlockwise: Boolean = false)
    fun save()
    fun restore()
    fun setFillStyle(color: Color?)
    fun setStrokeStyle(color: Color?)
    fun setGlobalAlpha(alpha: Double)
    fun setFont(f: Font)
    fun setLineWidth(lineWidth: Double)
    fun strokeRect(x: Double, y: Double, w: Double, h: Double)
    fun strokeText(text: String, x: Double, y: Double)
    fun fillText(text: String, x: Double, y: Double)
    fun scale(x: Double, y: Double)
    fun scale(xy: Double)
    fun rotate(angle: Double)
    fun translate(x: Double, y: Double)
    fun transform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double)
    fun bezierCurveTo(cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, x: Double, y: Double)
    fun setLineJoin(lineJoin: LineJoin)
    fun setLineCap(lineCap: LineCap)
    fun setStrokeMiterLimit(miterLimit: Double)
    fun setTextBaseline(baseline: TextBaseline)
    fun setTextAlign(align: TextAlign)
    fun setTransform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double)
    fun setLineDash(lineDash: DoubleArray)
    fun setLineDashOffset(lineDashOffset: Double)
    fun measureText(str: String): Double

    // https://github.com/d3/d3/blob/9364923ee2b35ec2eb80ffc4bdac12a7930097fc/src/svg/line.js#L236
    fun drawBezierCurve(points: List<Vec<*>>) {
        fun lineDot4(a: List<Double>, b: List<Double>): Double {
            // returns the dot product of the given four-element vectors
            return a[0] * b[0] + a[1] * b[1] + a[2] * b[2] + a[3] * b[3]
        }
        // Matrix to transform basis (b-spline) control points to bezier control points.
        val lineBasisBezier1 = listOf(0.0, 2.0 / 3.0, 1.0 / 3.0, 0.0)
        val lineBasisBezier2 = listOf(0.0, 1.0 / 3.0, 2.0 / 3.0, 0.0)
        val lineBasisBezier3 = listOf(0.0, 1.0 / 6.0, 2.0 / 3.0, 1.0 / 6.0)

        val px = arrayListOf(points[0].x, points[0].x, points[0].x, points[1].x)
        val py = arrayListOf(points[0].y, points[0].y, points[0].y, points[1].y)

        moveTo(points[0].x, points[0].y)
        lineTo(
            lineDot4(lineBasisBezier3, px),
            lineDot4(lineBasisBezier3, py)
        )
        for (i in 2..points.size) {
            val curPoint = if (i < points.size) points[i] else points.last()
            px.removeFirst(); px.add(curPoint.x)
            py.removeFirst(); py.add(curPoint.y)
            bezierCurveTo(
                lineDot4(lineBasisBezier1, px),
                lineDot4(lineBasisBezier1, py),
                lineDot4(lineBasisBezier2, px),
                lineDot4(lineBasisBezier2, py),
                lineDot4(lineBasisBezier3, px),
                lineDot4(lineBasisBezier3, py)
            )
        }
        lineTo(points.last().x, points.last().y)
    }
}

fun Context2d.drawImage(snapshot: Snapshot, p: Vec<*>) = drawImage(snapshot, p.x, p.y)


enum class TextBaseline {
    ALPHABETIC, BOTTOM, MIDDLE, TOP
}

enum class TextAlign {
    CENTER, END, START
}

enum class LineJoin {
    BEVEL, MITER, ROUND
}

enum class LineCap {
    BUTT, ROUND, SQUARE
}

enum class FontWeight {
    NORMAL, BOLD
}

enum class FontStyle {
    NORMAL, ITALIC
}
