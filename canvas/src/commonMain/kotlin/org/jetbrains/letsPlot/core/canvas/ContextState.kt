/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.canvas

import org.jetbrains.letsPlot.commons.geometry.AffineTransform
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.toDegrees
import org.jetbrains.letsPlot.commons.values.Color

private const val logEnabled = true
private const val pathTransform = false
private fun log(str: () -> String) {
    if (logEnabled)
        println(str())
}


class ContextState {
    private val states = ArrayList<StateEntry>()
    private var currentState = StateEntry.create()
    private var currentPath: Path = Path()

    class StateEntry(
        var strokeColor: String,
        var strokeWidth: Double,
        var lineDashPattern: List<Double>?,
        var lineDashOffset: Double,
        var miterLimit: Int,
        var lineCap: LineCap,
        var lineJoin: LineJoin,
        var fillColor: String,
        var font: Font,
        var transform: AffineTransform
    ) {
        fun copy(): StateEntry {
            return StateEntry(
                strokeColor = strokeColor,
                strokeWidth = strokeWidth,
                lineDashOffset = lineDashOffset,
                miterLimit = miterLimit,
                lineCap = lineCap,
                lineJoin = lineJoin,
                fillColor = fillColor,
                font = font,
                transform = transform,
                lineDashPattern = lineDashPattern,
            )
        }

        companion object {
            fun create(
                strokeColor: String = Color.TRANSPARENT.toCssColor(),
                strokeWidth: Double = 1.0,
                lineDashPatternSize: ULong = 0u,
                lineDashOffset: Double = 0.0,
                miterLimit: Int = 10,
                lineCap: LineCap = LineCap.BUTT,
                lineJoin: LineJoin = LineJoin.MITER,
                fillColor: String = Color.TRANSPARENT.toCssColor(),
                font: Font = Font(),
                transform: AffineTransform = AffineTransform.IDENTITY,
                lineDashPattern: List<Double>? = null
            ): StateEntry {
                return StateEntry(
                    strokeColor = strokeColor,
                    strokeWidth = strokeWidth,
                    lineDashPattern = lineDashPattern,
                    lineDashOffset = lineDashOffset,
                    miterLimit = miterLimit,
                    lineCap = lineCap,
                    lineJoin = lineJoin,
                    fillColor = fillColor,
                    font = font,
                    transform = transform
                )
            }

        }
    }

    class Path {
        private sealed class PathCommand
        private data class MoveTo(val x: Double, val y: Double) : PathCommand()
        private data class LineTo(val x: Double, val y: Double) : PathCommand()
        private data class Ellipse(
            val x: Double,
            val y: Double,
            val radiusX: Double,
            val radiusY: Double,
            val rotation: Double,
            val startAngleDeg: Double,
            val endAngleDeg: Double,
            val anticlockwise: Boolean
        ) : PathCommand()

        private data object ClosePath : PathCommand()

        private val commands = mutableListOf<PathCommand>()

        fun closePath() {
            commands.add(ClosePath)
        }

        fun moveTo(x: Double, y: Double) {
            commands.add(MoveTo(x, y))
        }

        fun lineTo(x: Double, y: Double) {
            commands.add(LineTo(x, y))
        }

        fun arc(
            x: Double,
            y: Double,
            radius: Double,
            startAngleDeg: Double,
            endAngleDeg: Double,
            anticlockwise: Boolean
        ) {
            commands.add(Ellipse(x, y, radius, radius, 0.0, startAngleDeg, endAngleDeg, anticlockwise))
        }

        fun ellipse(
            x: Double, y: Double,
            radiusX: Double, radiusY: Double,
            rotation: Double,
            startAngle: Double, endAngle: Double,
            anticlockwise: Boolean
        ) {
            commands.add(Ellipse(x, y, radiusX, radiusY, rotation, startAngle, endAngle, anticlockwise))
        }
    }

    fun setTransform(m00: Double, m10: Double, m01: Double, m11: Double, m02: Double, m12: Double) {
        log { "setTransform(m11=$m00, m12=$m10, m21=$m01, m22=$m11, dx=$m02, dy=$m12)" }
        log { "\tfrom: [${currentState.transform.repr()}]" }
        currentState.transform = AffineTransform.makeMatrix(m00 = m00, m10 = m10, m01 = m01, m11 = m11, m02 = m02, m12 = m12)
        log { "\t  to: [${currentState.transform.repr()}]" }
    }

    fun transform(sx: Double, ry: Double, rx: Double, sy: Double, tx: Double, ty: Double) {
        log { "transform(m11=$sx, m12=$ry, m21=$rx, m22=$sy, dx=$tx, dy=$ty)" }
        log { "\tfrom: [${currentState.transform.repr()}]" }

        currentState.transform = AffineTransform.makeTransform(sx = sx, ry = ry, rx = rx, sy = sy, tx = tx, ty = ty)
        log { "\t  to: [${currentState.transform.repr()}]" }
    }

    fun scale(x: Double, y: Double) {
        return transform(x, 0.0, 0.0, y, 0.0, 0.0)
    }

    fun rotate(angle: Double) {
        val cos = kotlin.math.cos(angle)
        val sin = kotlin.math.sin(angle)
        return transform(cos, -sin, sin, cos, 0.0, 0.0)
    }

    fun translate(x: Double, y: Double) {
        return transform(1.0, 0.0, 0.0, 1.0, x, y)
    }

    fun save() {
        log { "save([${currentState.transform.repr()}])" }
        states += currentState.copy()
    }

    fun restore() {
        log{ "restore()"}
        log{ "\tfrom: [${currentState.transform.repr()}]"}
        if (states.isNotEmpty()) {
            currentState = states.removeAt(states.size - 1)
        }
        log{ "\t  to: [${currentState.transform.repr()}]" }
    }


    fun beginPath() {
        currentPath = Path()
    }

    fun closePath() {
        currentPath.closePath()
    }

    fun moveTo(x: Double, y: Double) {
        val (tx, ty) = if (!pathTransform) DoubleVector(x, y) else currentState.transform.transform(x, y)

        log { "moveTo($x, $y) -> [$tx, $ty]" }

        currentPath.moveTo(tx, ty)
    }

    fun lineTo(x: Double, y: Double) {
        val (tx, ty) = if (!pathTransform) DoubleVector(x, y) else currentState.transform.transform(x, y)

        log { "lineTo($x, $y) -> [$tx, $ty]" }

        currentPath.lineTo(tx, ty)
    }

    fun arc(
        x: Double,
        y: Double,
        radius: Double,
        startAngle: Double,
        endAngle: Double,
        anticlockwise: Boolean = false
    ) {
        val (tx, ty) = if (!pathTransform) DoubleVector(x, y) else currentState.transform.transform(x, y)

        log { "arc($x, $y, $radius, $startAngle, $endAngle) -> [$tx, $ty]" }

        currentPath.arc(tx, ty, radius, toDegrees(startAngle), toDegrees(endAngle), anticlockwise)
    }

    fun ellipse(
        x: Double, y: Double,
        radiusX: Double, radiusY: Double,
        rotation: Double,
        startAngle: Double, endAngle: Double,
        anticlockwise: Boolean
    ) {
        val (tX, tY) = if (!pathTransform) DoubleVector(x, y) else currentState.transform.transform(x, y)
        val (trX, trY) = if (!pathTransform) DoubleVector(radiusX, radiusY) else currentState.transform.transform(
            radiusX,
            radiusY
        )

        log { "ellipse($x, $y, $radiusX, $radiusY, $rotation, $startAngle, $endAngle, $anticlockwise) -> c: [$tX, $tY], r: [$trX, $trY]" }

        currentPath.ellipse(
            tX,
            tY,
            radiusX,
            radiusY,
            toDegrees(rotation),
            toDegrees(startAngle),
            toDegrees(endAngle),
            anticlockwise
        )
    }

    fun setStrokeStyle(color: String) {
        log { "setStrokeStyle($color)" }
        currentState.strokeColor = color
    }

    fun setFillStyle(color: String) {
        log { "setFillStyle($color)" }
        currentState.fillColor = color
    }

    fun setFont(font: Font) {
        log { "setFont($font)" }
        currentState.font = font
    }

    fun setLineWidth(lineWidth: Double) {
        log { "setLineWidth($lineWidth)" }
        currentState.strokeWidth = lineWidth
    }

    fun setLineCap(lineCap: LineCap) {
        log { "setLineCap($lineCap)" }
        currentState.lineCap = lineCap
    }

    fun setLineJoin(lineJoin: LineJoin) {
        log { "setLineJoin($lineJoin)" }
        currentState.lineJoin = lineJoin
    }

    fun setLineDashPattern(lineDashPattern: List<Double>?) {
        log { "setLineDashPattern($lineDashPattern)" }
        currentState.lineDashPattern = lineDashPattern
    }

    fun setLineDashOffset(lineDashOffset: Double) {
        log { "setLineDashOffset($lineDashOffset)" }
        currentState.lineDashOffset = lineDashOffset
    }

    fun setStrokeMiterLimit(miterLimit: Int) {
        log { "setStrokeMiterLimit($miterLimit)" }
        currentState.miterLimit = miterLimit
    }
}
