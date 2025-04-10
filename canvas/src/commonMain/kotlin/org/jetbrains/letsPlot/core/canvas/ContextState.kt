/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.canvas

import org.jetbrains.letsPlot.commons.geometry.AffineTransform
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.toDegrees
import org.jetbrains.letsPlot.commons.intern.math.toRadians
import org.jetbrains.letsPlot.commons.values.Color
import kotlin.math.*

private const val logEnabled = true
private fun log(str: () -> String) {
    if (logEnabled)
        println(str())
}

class ContextState {
    private val states = ArrayList<StateEntry>()
    private var currentState = StateEntry.create()
    private var currentPath: Path = Path()

    fun getCurrentState(): StateEntry {
        return currentState.copy()
    }

    fun getCurrentPath(): List<PathCommand> {
        return currentPath.getCommands()
    }

    class StateEntry(
        var strokeColor: Color,
        var strokeWidth: Double,
        var lineDashPattern: List<Double>?,
        var lineDashOffset: Double,
        var miterLimit: Double,
        var lineCap: LineCap,
        var lineJoin: LineJoin,
        var fillColor: Color,
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
                strokeColor: Color = Color.TRANSPARENT,
                strokeWidth: Double = 1.0,
                lineDashOffset: Double = 0.0,
                miterLimit: Double = 10.0,
                lineCap: LineCap = LineCap.BUTT,
                lineJoin: LineJoin = LineJoin.MITER,
                fillColor: Color = Color.TRANSPARENT,
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

    sealed class PathCommand(
        val transform: AffineTransform
    )

    class ClosePath(transform: AffineTransform) : PathCommand(transform)
    class MoveTo(val x: Double, val y: Double, transform: AffineTransform) : PathCommand(transform)
    class LineTo(val x: Double, val y: Double, transform: AffineTransform) : PathCommand(transform)
    class Ellipse(
        val x: Double,
        val y: Double,
        val radiusX: Double,
        val radiusY: Double,
        val rotation: Double,
        val startAngleDeg: Double,
        val endAngleDeg: Double,
        val anticlockwise: Boolean,
        transform: AffineTransform
    ) : PathCommand(transform) {
        override fun toString(): String {
            return "Ellipse(x=$x, y=$y, radiusX=$radiusX, radiusY=$radiusY, rotation=$rotation, startAngleDeg=$startAngleDeg, endAngleDeg=$endAngleDeg, anticlockwise=$anticlockwise)"
        }

        fun approximateWithBezierCurve(): List<DoubleVector> {
            val startAngleRad = toRadians(startAngleDeg)
            val endAngleRad = toRadians(endAngleDeg)
            if (radiusX < 0 || radiusY < 0) {
                return listOf(DoubleVector(x, y))
            }

            val sweepAngle = normalizeAnglesAndSweep(startAngleRad, endAngleRad, anticlockwise)

            val segments = approximateEllipticalArcWithBezier(
                cx = x,
                cy = y,
                rx = radiusX,
                ry = radiusY,
                rotation = rotation,
                arcStartAngle = startAngleRad, // Use original start angle
                sweepAngle = sweepAngle,    // Use calculated sweep
                transform = { x, y -> x to y }
            )

            return segments
        }

        fun normalizeAnglesAndSweep(
            startAngle: Double,
            endAngle: Double,
            anticlockwise: Boolean
        ): Double {
            val twoPi = 2.0 * PI

            // Normalize angles to be within [0, 2*PI) - handles negative inputs
            val normStart = (startAngle % twoPi + twoPi) % twoPi
            val normEnd = (endAngle % twoPi + twoPi) % twoPi

            var sweepMagnitude: Double
            if (!anticlockwise) { // Clockwise sweep magnitude
                sweepMagnitude = if (normEnd >= normStart) normEnd - normStart else twoPi - normStart + normEnd
            } else { // Anticlockwise sweep magnitude
                sweepMagnitude = if (normStart >= normEnd) normStart - normEnd else twoPi - normEnd + normStart
            }

            // Adjust sweep magnitude for multiple wraps indicated by original angles
            val angleDiff = endAngle - startAngle
            val wraps = abs(angleDiff) / twoPi
            if (wraps >= 1.0) {
                // Check if the shortest path direction matches the requested direction
                val shortestSweepClockwise =
                    if (normEnd >= normStart) normEnd - normStart else twoPi - normStart + normEnd
                val wrapsClockwise = floor(angleDiff / twoPi)
                val wrapsAntiClockwise = floor(-angleDiff / twoPi) // How many full wraps in the other direction

                if (!anticlockwise && angleDiff > 1e-9) { // Wants clockwise, and diff is positive
                    sweepMagnitude = shortestSweepClockwise + wrapsClockwise * twoPi
                } else if (anticlockwise && angleDiff < -1e-9) { // Wants anticlockwise, and diff is negative
                    sweepMagnitude =
                        (twoPi - shortestSweepClockwise) + wrapsAntiClockwise * twoPi // Use the anticlockwise magnitude
                }
            }

            // Ensure non-zero sweep if angles are distinct but normalize to same value (e.g., 0 and 2PI)
            // Use original angle difference to detect this
            if (abs(sweepMagnitude) < 1e-9 && abs(angleDiff) > 1e-9) {
                sweepMagnitude = twoPi
            }

            return if (anticlockwise) -sweepMagnitude else sweepMagnitude
        }

        fun approximateEllipticalArcWithBezier(
            cx: Double,
            cy: Double,
            rx: Double,
            ry: Double,
            rotation: Double,
            arcStartAngle: Double, // Use the original start angle for calculations
            sweepAngle: Double,    // Use the calculated signed sweep angle
            transform: (x: Double, y: Double) -> Pair<Double, Double>
        ): List<DoubleVector> {
            if (rx <= 0 || ry <= 0 || abs(sweepAngle) < 1e-9) {
                // Handle degenerate case: MoveTo start point
                val cosA = cos(arcStartAngle);
                val sinA = sin(arcStartAngle)
                val p0xLocal = rx * cosA;
                val p0yLocal = ry * sinA
                val (p0eX, p0eY) = transformEllipsePoint(p0xLocal, p0yLocal, cx, cy, rotation)
                val (tp0x, tp0y) = transform(p0eX, p0eY)

                return listOf(DoubleVector(tp0x, tp0y))
            }

            // Max angle per Bezier segment (e.g., 90 degrees)
            val maxAnglePerSegment = PI / 2.0
            val numSegments = ceil(abs(sweepAngle) / maxAnglePerSegment).toInt().coerceAtLeast(1)
            val deltaAngle = sweepAngle / numSegments // Angle step per segment (signed)

            // Correction factor for control point direction based on sweep direction
            val dir = sign(deltaAngle) // +1.0 for clockwise, -1.0 for anticlockwise

            val kappa = (4.0 / 3.0) * tan(abs(deltaAngle) / 4.0) // Kappa based on segment angle magnitude

            var currentAngle = arcStartAngle

            val segments = mutableListOf<DoubleVector>()

            for (i in 0 until numSegments) {
                val angle1 = currentAngle
                val angle2 = currentAngle + deltaAngle

                // Calculate points for this segment
                val cosA1 = cos(angle1);
                val sinA1 = sin(angle1)
                val cosA2 = cos(angle2);
                val sinA2 = sin(angle2)

                // Start and End points (local)
                val p0xLocal = rx * cosA1;
                val p0ylocal = ry * sinA1 // P0
                val p3xLocal = rx * cosA2;
                val p3yLocal = ry * sinA2 // P3

                // Calculate tangent offsets scaled by kappa
                // These represent the vector from P0->P1 and P3->P2 if dir were +1
                val offsetX1 = -kappa * ry * sinA1
                val offsetY1 = +kappa * rx * cosA1
                val offsetX2 = +kappa * ry * sinA2
                val offsetY2 = -kappa * rx * cosA2

                // Control points (local) - Apply direction correction `dir`
                val p1xLocal = p0xLocal + dir * offsetX1
                val p1yLocal = p0ylocal + dir * offsetY1
                val p2xLocal = p3xLocal + dir * offsetX2 // P2 is relative to P3's tangent endpoint
                val p2yLocal = p3yLocal + dir * offsetY2

                // Apply ellipse's own transform (rotation + translation)
                val (p0eX, p0eY) = transformEllipsePoint(p0xLocal, p0ylocal, cx, cy, rotation) // P0
                val (p1eX, p1eY) = transformEllipsePoint(p1xLocal, p1yLocal, cx, cy, rotation) // P1 (CP1)
                val (p2eX, p2eY) = transformEllipsePoint(p2xLocal, p2yLocal, cx, cy, rotation) // P2 (CP2)
                val (p3eX, p3eY) = transformEllipsePoint(p3xLocal, p3yLocal, cx, cy, rotation) // P3

                // Apply the current canvas transform
                val (tp0x, tp0y) = transform(p0eX, p0eY) // Final P0
                val (tp1x, tp1y) = transform(p1eX, p1eY) // Final P1 (CP1)
                val (tp2x, tp2y) = transform(p2eX, p2eY) // Final P2 (CP2)
                val (tp3x, tp3y) = transform(p3eX, p3eY) // Final P3

                if (numSegments == 1) {
                    segments += DoubleVector(tp0x, tp0y)
                    segments += DoubleVector(tp1x, tp1y)
                    segments += DoubleVector(tp2x, tp2y)
                    segments += DoubleVector(tp3x, tp3y)
                } else {
                    if (i == 0) {
                        segments += DoubleVector(tp0x, tp0y)
                        segments += DoubleVector(tp1x, tp1y)
                        segments += DoubleVector(tp2x, tp2y)
                        segments += DoubleVector(tp3x, tp3y)
                    } else {
                        // Exclude last point of previous segment
                        segments += DoubleVector(tp1x, tp1y)
                        segments += DoubleVector(tp2x, tp2y)
                        segments += DoubleVector(tp3x, tp3y)
                    }
                }

                currentAngle = angle2
            }

            return segments
        }


        private fun transformEllipsePoint(
            px: Double, py: Double, // Point in local ellipse space (center 0,0, no rotation)
            cx: Double, cy: Double, // Ellipse center
            rotation: Double // Ellipse rotation
        ): Pair<Double, Double> {
            val cosRot = cos(rotation)
            val sinRot = sin(rotation)
            val pxRotated = px * cosRot - py * sinRot
            val pyRotated = px * sinRot + py * cosRot
            return Pair(pxRotated + cx, pyRotated + cy)
        }
    }

    internal inner class Path {
        private val commands = mutableListOf<PathCommand>()

        fun getCommands() = commands.toList()

        fun closePath() {
            commands.add(ClosePath(currentState.transform))
        }

        fun moveTo(x: Double, y: Double) {
            commands.add(MoveTo(x, y, currentState.transform))
        }

        fun lineTo(x: Double, y: Double) {
            commands.add(LineTo(x, y, currentState.transform))
        }

        fun arc(
            x: Double,
            y: Double,
            radius: Double,
            startAngleDeg: Double,
            endAngleDeg: Double,
            anticlockwise: Boolean
        ) {
            commands.add(
                Ellipse(
                    x,
                    y,
                    radius,
                    radius,
                    0.0,
                    startAngleDeg,
                    endAngleDeg,
                    anticlockwise,
                    currentState.transform
                )
            )
        }

        fun ellipse(
            x: Double, y: Double,
            radiusX: Double, radiusY: Double,
            rotation: Double,
            startAngle: Double, endAngle: Double,
            anticlockwise: Boolean
        ) {
            commands.add(
                Ellipse(
                    x,
                    y,
                    radiusX,
                    radiusY,
                    rotation,
                    startAngle,
                    endAngle,
                    anticlockwise,
                    currentState.transform
                )
            )
        }
    }

    fun setTransform(m00: Double, m10: Double, m01: Double, m11: Double, m02: Double, m12: Double) {
        log { "setTransform(m00=$m00, m10=$m10, m01=$m01, m11=$m11, m02=$m02, m12=$m12)" }
        log { "\tfrom: [${currentState.transform.repr()}]" }
        currentState.transform =
            AffineTransform.makeMatrix(m00 = m00, m10 = m10, m01 = m01, m11 = m11, m02 = m02, m12 = m12)
        log { "\t  to: [${currentState.transform.repr()}]" }
    }

    private fun transform(at: AffineTransform) {
        log { "transform(${at.repr()})" }
        log { "\tfrom: [${currentState.transform.repr()}]" }
        currentState.transform = currentState.transform.concat(at)
        log { "\t  to: [${currentState.transform.repr()}]" }
    }

    fun transform(sx: Double, ry: Double, rx: Double, sy: Double, tx: Double, ty: Double) {
        log { "transform(sx=$sx, ry=$ry, rx=$rx, sy=$sy, tx=$tx, ty=$ty)" }
        transform(AffineTransform.makeTransform(sx = sx, ry = ry, rx = rx, sy = sy, tx = tx, ty = ty))
    }

    fun scale(x: Double, y: Double) {
        log { "scale($x, $y)" }
        transform(AffineTransform.makeScale(x, y))
    }

    fun rotate(angle: Double) {
        log { "rotate($angle)" }
        transform(AffineTransform.makeRotation(angle))
    }

    fun translate(x: Double, y: Double) {
        log { "translate($x, $y)" }
        transform(AffineTransform.makeTranslation(x, y))
    }

    fun save() {
        log { "save([${currentState.transform.repr()}])" }
        states += currentState.copy()
    }

    fun restore() {
        log { "restore()" }
        log { "\tfrom: [${currentState.transform.repr()}]" }
        if (states.isNotEmpty()) {
            currentState = states.removeAt(states.size - 1)
        }
        log { "\t  to: [${currentState.transform.repr()}]" }
    }

    fun beginPath() {
        currentPath = Path()
    }

    fun closePath() {
        currentPath.closePath()
    }

    fun moveTo(x: Double, y: Double) {
        currentPath.moveTo(x, y)
    }

    fun lineTo(x: Double, y: Double) {
        currentPath.lineTo(x, y)
    }

    fun arc(
        x: Double,
        y: Double,
        radius: Double,
        startAngle: Double,
        endAngle: Double,
        anticlockwise: Boolean = false
    ) {
        currentPath.arc(x, y, radius, toDegrees(startAngle), toDegrees(endAngle), anticlockwise)
    }

    fun ellipse(
        x: Double, y: Double,
        radiusX: Double, radiusY: Double,
        rotation: Double,
        startAngle: Double, endAngle: Double,
        anticlockwise: Boolean
    ) {
        currentPath.ellipse(
            x,
            y,
            radiusX,
            radiusY,
            toDegrees(rotation),
            toDegrees(startAngle),
            toDegrees(endAngle),
            anticlockwise
        )
    }

    fun setStrokeStyle(color: Color?) {
        log { "setStrokeStyle($color)" }
        currentState.strokeColor = color ?: Color.TRANSPARENT
    }

    fun setFillStyle(color: Color?) {
        log { "setFillStyle($color)" }
        currentState.fillColor = color ?: Color.TRANSPARENT
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

    fun setStrokeMiterLimit(miterLimit: Double) {
        log { "setStrokeMiterLimit($miterLimit)" }
        currentState.miterLimit = miterLimit
    }

    fun setGlobalAlpha(d: Double) {
        TODO("Not yet implemented")
    }

    fun scale(d: Double) {
        scale(d, d)
    }

    fun bezierCurveTo(
        cp1x: Double,
        cp1y: Double,
        cp2x: Double,
        cp2y: Double,
        x: Double,
        y: Double
    ) {
        TODO("Not yet implemented")
    }

    fun setTextBaseline(baseline: TextBaseline) {
        TODO("Not yet implemented")
    }

    fun setTextAlign(align: TextAlign) {
        TODO("Not yet implemented")
    }
}
