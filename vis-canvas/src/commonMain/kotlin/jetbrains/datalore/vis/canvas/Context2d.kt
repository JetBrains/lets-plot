/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.Canvas.Snapshot

interface Context2d {
    fun clearRect(rect: DoubleRectangle)
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
    fun rotate(angle: Double)
    fun translate(x: Double, y: Double)
    fun transform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double)
    fun bezierCurveTo(cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, x: Double, y: Double)
    fun setLineJoin(lineJoin: LineJoin)
    fun setLineCap(lineCap: LineCap)
    fun setTextBaseline(baseline: TextBaseline)
    fun setTextAlign(align: TextAlign)
    fun setTransform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double)
    fun setLineDash(lineDash: DoubleArray)
    fun measureText(str: String): Double

    enum class LineJoin {
        BEVEL, MITER, ROUND
    }

    enum class LineCap {
        BUTT, ROUND, SQUARE
    }

    enum class TextBaseline {
        ALPHABETIC, BOTTOM, MIDDLE, TOP
    }

    enum class TextAlign {
        CENTER, END, START
    }

    data class Font(
        val fontStyle: FontStyle = FontStyle.NORMAL,
        val fontWeight: FontWeight = FontWeight.NORMAL,
        val fontSize: Double = DEFAULT_SIZE,
        val fontFamily: String = DEFAULT_FAMILY
    ) {
        constructor(
            style: FontStyle?,
            weight: FontWeight?,
            size:Double?,
            family: String?
        ): this(
            style ?: FontStyle.NORMAL,
            weight ?: FontWeight.NORMAL,
            size ?: DEFAULT_SIZE,
            family ?: DEFAULT_FAMILY
        )

        enum class FontStyle {
            NORMAL, ITALIC
        }

        enum class FontWeight {
            NORMAL, BOLD
        }

        companion object {
            const val DEFAULT_SIZE = 10.0
            const val DEFAULT_FAMILY = "serif"
        }
    }
}
