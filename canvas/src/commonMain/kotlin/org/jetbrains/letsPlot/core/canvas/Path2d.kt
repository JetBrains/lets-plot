/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.canvas

import org.jetbrains.letsPlot.commons.geometry.AffineTransform
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import kotlin.math.*

class Path2d() {
    private constructor(commands: List<PathCommand>) : this() {
        this.commands += commands
    }

    private val commands = mutableListOf<PathCommand>()

    fun copy(): Path2d {
        return Path2d(commands)
    }

    fun transform(affineTransform: AffineTransform): Path2d {
        return Path2d(commands.map { it.transform(affineTransform) })
    }

    fun append(cmd: PathCommand) {
        commands += cmd
    }

    fun getCommands() = commands.toList()

    fun closePath(): Path2d {
        commands += ClosePath
        return this
    }

    fun moveTo(x: Double, y: Double): Path2d {
        commands += MoveTo(x, y)
        return this
    }

    fun lineTo(x: Double, y: Double): Path2d {
        commands += LineTo(x, y)
        return this
    }

    fun arc(x: Double, y: Double, radius: Double, startAngle: Double, endAngle: Double, anticlockwise: Boolean): Path2d {
        commands += arc(
            x = x,
            y = y,
            radiusX = radius,
            radiusY = radius,
            rotation = 0.0,
            startAngle = startAngle,
            endAngle = endAngle,
            anticlockwise = anticlockwise
        )
        return this
    }

    fun ellipse(
        x: Double, y: Double,
        radiusX: Double, radiusY: Double,
        rotation: Double,
        startAngle: Double, endAngle: Double,
        anticlockwise: Boolean
    ): Path2d {
        commands += arc(
            x = x,
            y = y,
            radiusX = radiusX,
            radiusY = radiusY,
            rotation = rotation,
            startAngle = startAngle,
            endAngle = endAngle,
            anticlockwise = anticlockwise
        )
        return this
    }

    fun bezierCurveTo(
        cp1x: Double,
        cp1y: Double,
        cp2x: Double,
        cp2y: Double,
        x: Double,
        y: Double
    ): Path2d {
        commands += Arc(
            start = null,
            controlPoints = listOf(
                DoubleVector(cp1x, cp1y),
                DoubleVector(cp2x, cp2y),
                DoubleVector(x, y)
            )
        )

        return this
    }

    companion object {
        fun arc(
            x: Double,
            y: Double,
            radiusX: Double,
            radiusY: Double,
            rotation: Double,
            startAngle: Double,
            endAngle: Double,
            anticlockwise: Boolean
        ): Arc {
            val (moveTo, controlPoints) = approximateEllipseWithBezierCurve(
                x = x,
                y = y,
                radiusX = radiusX,
                radiusY = radiusY,
                rotation = rotation,
                startAngle = startAngle,
                endAngle = endAngle,
                anticlockwise = anticlockwise
            )

            return Arc(moveTo, controlPoints)
        }

        private fun approximateEllipseWithBezierCurve(
            x: Double,
            y: Double,
            radiusX: Double,
            radiusY: Double,
            rotation: Double,
            startAngle: Double,
            endAngle: Double,
            anticlockwise: Boolean
        ): Pair<DoubleVector, List<DoubleVector>> {
            if (radiusX < 0 || radiusY < 0) {
                return DoubleVector(x, y) to emptyList()
            }

            val sweepAngle = normalizeAnglesAndSweep(startAngle, endAngle, anticlockwise)

            val segments = approximateEllipticalArcWithBezier(
                cx = x,
                cy = y,
                rx = radiusX,
                ry = radiusY,
                rotation = rotation,
                arcStartAngle = startAngle, // Use an original start angle
                sweepAngle = sweepAngle,    // Use calculated sweep
            )

            return segments
        }

        private fun normalizeAnglesAndSweep(
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

            // Ensure non-zero sweep if angles are distinct but normalize to the same value (e.g., 0 and 2PI)
            // Use original angle difference to detect this
            if (abs(sweepMagnitude) < 1e-9 && abs(angleDiff) > 1e-9) {
                sweepMagnitude = twoPi
            }

            return if (anticlockwise) -sweepMagnitude else sweepMagnitude
        }

        private fun approximateEllipticalArcWithBezier(
            cx: Double,
            cy: Double,
            rx: Double,
            ry: Double,
            rotation: Double,
            arcStartAngle: Double, // Use the original start angle for calculations
            sweepAngle: Double,    // Use the calculated signed sweep angle
        ): Pair<DoubleVector, List<DoubleVector>> {
            if (rx <= 0 || ry <= 0 || abs(sweepAngle) < 1e-9) {
                // Handle degenerate case: MoveTo start point
                val cosA = cos(arcStartAngle);
                val sinA = sin(arcStartAngle)
                val p0xLocal = rx * cosA;
                val p0yLocal = ry * sinA
                val p0e = transformEllipsePoint(p0xLocal, p0yLocal, cx, cy, rotation)

                return p0e to emptyList()
            }

            // Max angle per Bezier segment (e.g., 90 degrees)
            val maxAnglePerSegment = PI / 2.0
            val numSegments = ceil(abs(sweepAngle) / maxAnglePerSegment).toInt().coerceAtLeast(1)
            val deltaAngle = sweepAngle / numSegments // Angle step per segment (signed)

            // Correction factor for a control point direction based on a sweep direction
            val dir = sign(deltaAngle) // +1.0 for clockwise, -1.0 for anticlockwise

            val kappa = (4.0 / 3.0) * tan(abs(deltaAngle) / 4.0) // Kappa based on segment angle magnitude

            var currentAngle = arcStartAngle

            var moveTo: DoubleVector? = null
            val controlPoints = mutableListOf<DoubleVector>()

            repeat(numSegments) {
                val angle1 = currentAngle
                val angle2 = currentAngle + deltaAngle

                // Calculate points for this segment
                val cosA1 = cos(angle1); val sinA1 = sin(angle1)
                val cosA2 = cos(angle2); val sinA2 = sin(angle2)

                // Start and End points (local)
                val p0xLocal = rx * cosA1; val p0yLocal = ry * sinA1 // P0
                val p3xLocal = rx * cosA2; val p3yLocal = ry * sinA2 // P3

                // Calculate tangent vector components at angle1 and angle2
                val tx1 = -rx * sinA1 // Tangent component X at angle1
                val ty1 = ry * cosA1 // Tangent component Y at angle1
                val tx2 = -rx * sinA2 // Tangent component X at angle2
                val ty2 = ry * cosA2 // Tangent component Y at angle2

                val p1xLocal = p0xLocal + dir * kappa * tx1 // P0 + scaled tangent T1
                val p1yLocal = p0yLocal + dir * kappa * ty1
                val p2xLocal = p3xLocal - dir * kappa * tx2 // P3 - scaled tangent T2 (note sign)
                val p2yLocal = p3yLocal - dir * kappa * ty2

                // Set the moveTo point for the first segment
                if (moveTo == null) {
                    moveTo = transformEllipsePoint(p0xLocal, p0yLocal, cx, cy, rotation) // P0
                }

                controlPoints += transformEllipsePoint(p1xLocal, p1yLocal, cx, cy, rotation) // P1 (CP1)
                controlPoints += transformEllipsePoint(p2xLocal, p2yLocal, cx, cy, rotation) // P2 (CP2)
                controlPoints += transformEllipsePoint(p3xLocal, p3yLocal, cx, cy, rotation) // P3

                currentAngle = angle2
            }

            return moveTo!! to controlPoints
        }

        private fun transformEllipsePoint(
            px: Double, py: Double, // Point in a local ellipse space (center 0,0, no rotation)
            cx: Double, cy: Double, // Ellipse center
            rotation: Double // Ellipse rotation
        ): DoubleVector {
            val cosRot = cos(rotation)
            val sinRot = sin(rotation)
            val pxRotated = px * cosRot - py * sinRot
            val pyRotated = px * sinRot + py * cosRot
            return DoubleVector(pxRotated + cx, pyRotated + cy)
        }
    }

    sealed class PathCommand {
        abstract fun transform(at: AffineTransform): PathCommand
    }

    object ClosePath : PathCommand() {
        override fun transform(at: AffineTransform): PathCommand = this
    }

    class MoveTo(val x: Double, val y: Double) : PathCommand() {
        override fun transform(at: AffineTransform): PathCommand {
            val (tx, ty) = at.transform(x, y)
            return MoveTo(tx, ty)
        }
    }

    class LineTo(val x: Double, val y: Double) : PathCommand() {
        override fun transform(at: AffineTransform): PathCommand {
            val (tx, ty) = at.transform(x, y)
            return LineTo(tx, ty)
        }
    }

    class Arc(
        val start: DoubleVector?, // start point of the arc, null if no start point, e.g., consecutive Bézier curves
        val controlPoints: List<DoubleVector>
    ) : PathCommand() {
        override fun transform(affineTransform: AffineTransform): PathCommand {
            return Arc(
                start = start?.let { affineTransform.transform(it) },
                controlPoints = controlPoints.map { affineTransform.transform(it) }
            )
        }
    }
}
