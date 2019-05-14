package jetbrains.datalore.visualization.base.canvas

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.canvas.Canvas.Snapshot

interface Context2d {
    fun clearRect(rect: DoubleRectangle)
    fun drawImage(snapshot: Snapshot, x: Int, y: Int)
    fun beginPath()
    fun closePath()
    fun stroke()
    fun fill()
    fun fillEvenOdd()
    fun fillRect(x: Double, y: Double, w: Double, h: Double)
    fun moveTo(x: Double, y: Double)
    fun lineTo(x: Double, y: Double)
    fun arc(x: Double, y: Double, radius: Double, startAngle: Double, endAngle: Double)
    fun save()
    fun restore()
    fun setFillColor(color: Color?)
    fun setStrokeColor(color: Color?)
    fun setGlobalAlpha(alpha: Double)
    fun setFont(f: String)
    fun setLineWidth(lineWidth: Double)
    fun strokeRect(x: Double, y: Double, w: Double, h: Double)
    fun strokeText(text: String, x: Double, y: Double)
    fun fillText(text: String, x: Double, y: Double)
    fun scale(x: Double, y: Double)
    fun rotate(angle: Double)
    fun translate(x: Double, y: Double)
    fun transform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double)
    fun bezierCurveTo(cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, x: Double, y: Double)
    fun quadraticCurveTo(cpx: Double, cpy: Double, x: Double, y: Double)
    fun setLineJoin(lineJoin: LineJoin)
    fun setLineCap(lineCap: LineCap)
    fun setTextBaseline(baseline: TextBaseline)
    fun setTextAlign(align: TextAlign)
    fun setTransform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double)
    fun setLineDash(lineDash: DoubleArray)
    fun measureText(s: String): Double

    enum class LineJoin {
        BEVEL, MITER, ROUND
    }

    enum class LineCap {
        BUTT, ROUND, SQUARE
    }

    enum class TextBaseline {
        ALPHABETIC, BOTTOM, HANGING, IDEOGRAPHIC, MIDDLE, TOP
    }

    enum class TextAlign {
        CENTER, END, LEFT, RIGHT, START
    }
}
