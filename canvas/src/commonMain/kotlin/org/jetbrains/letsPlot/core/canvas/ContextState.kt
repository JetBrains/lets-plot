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

        fun toBezierControlPoints(): List<BezierSegment> {
            val startAngleRad = toRadians(startAngleDeg)
            val endAngleRad = toRadians(endAngleDeg)
            if (radiusX < 0 || radiusY < 0) {
                println("Warning: ellipse radii must be non-negative.")
                // Or potentially call drawEllipticalArcPathBezier with 0 radius which handles MoveTo
                drawEllipticalArcPathBezier(x, y, 0.0, 0.0, rotation, startAngleRad, 0.0) { x, y -> x to y }
                error("asd")
            }

            val (normStartRad, normEndRad, sweepAngle) = normalizeAnglesAndSweep(startAngleRad, endAngleRad, anticlockwise)

            val segments = drawEllipticalArcPathBezier(
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

        /**
         * Calculates the start/end angles normalized to [0, 2*PI) and the total sweep angle.
         * Handles anticlockwise direction and angle wrapping.
         *
         * @return Triple(normalizedStart, normalizedEnd, sweepAngle)
         *         sweepAngle is positive for clockwise, negative for anticlockwise.
         */
        fun normalizeAnglesAndSweep(
            startAngle: Double,
            endAngle: Double,
            anticlockwise: Boolean
        ): Triple<Double, Double, Double> {
            val twoPi = 2.0 * PI

            // Normalize angles to [0, 2*PI)
            val normStart = (startAngle % twoPi + twoPi) % twoPi
            val normEnd = (endAngle % twoPi + twoPi) % twoPi

            var sweep = if (!anticlockwise) { // Clockwise
                if (normEnd >= normStart) {
                    normEnd - normStart
                } else {
                    twoPi - normStart + normEnd // Wrap around
                }
            } else { // Anticlockwise
                if (normStart >= normEnd) {
                    normStart - normEnd
                } else {
                    twoPi - normEnd + normStart // Wrap around
                }
            }

            // Adjust sweep if the original angles indicated multiple full circles
            val angleDiff = endAngle - startAngle
            if (!anticlockwise && angleDiff >= twoPi) {
                sweep += floor(angleDiff / twoPi) * twoPi
            } else if (anticlockwise && angleDiff <= -twoPi) {
                // For anticlockwise, a large negative diff means more sweep
                sweep += floor(-angleDiff / twoPi) * twoPi // Add positive sweep magnitude
            }

            // Ensure non-zero sweep if angles are different but normalized same value (e.g. 0 and 2*PI)
            if (abs(sweep) < 1e-9 && abs(angleDiff) > 1e-9) {
                sweep = if (angleDiff > 0 && !anticlockwise || angleDiff < 0 && anticlockwise) twoPi else -twoPi
            }

            // Apply direction sign to sweep
            if (anticlockwise) {
                sweep = -abs(sweep) // Make sweep negative for anticlockwise
            } else {
                sweep = abs(sweep) // Make sweep positive for clockwise
            }

            return Triple(normStart, normEnd, sweep)
        }

        /**
         * Draws an elliptical arc path using Bezier curve approximation onto the DrawingWand.
         * Takes pre-calculated start angle and sweep angle.
         * Applies ellipse rotation/translation, then the canvas transform lambda.
         *
         * @param drawingWand The wand to draw on.
         * @param cx Ellipse center X.
         * @param cy Ellipse center Y.
         * @param rx Ellipse radius X.
         * @param ry Ellipse radius Y.
         * @param rotation Ellipse rotation in radians.
         * @param arcStartAngle Starting angle of the arc in radians (normalized or actual start).
         * @param sweepAngle Total sweep angle in radians (+ve clockwise, -ve anticlockwise).
         * @param transform Lambda to apply the current canvas transformation.
         * @return True if path definition succeeded without detected errors.
         */
        fun drawEllipticalArcPathBezier(
            cx: Double,
            cy: Double,
            rx: Double,
            ry: Double,
            rotation: Double,
            arcStartAngle: Double,
            sweepAngle: Double,
            transform: (x: Double, y: Double) -> Pair<Double, Double>
        ): List<BezierSegment> {
            if (rx <= 0 || ry <= 0 || abs(sweepAngle) < 1e-9) {
                // Nothing to draw, but not an error state for path definition itself
                // Though, might need a single MoveTo if this is the only element? Handle in caller.
                // Let's just move to the start point in this case.
                val cosA = cos(arcStartAngle)
                val sinA = sin(arcStartAngle)
                val p0x_local = rx * cosA
                val p0y_local = ry * sinA
                val (p0e_x, p0e_y) = transformEllipsePoint(p0x_local, p0y_local, cx, cy, rotation)
                val (tp0x, tp0y) = transform(p0e_x, p0e_y)

                //ImageMagick.DrawClearException(drawingWand)
                //ImageMagick.DrawPathStart(drawingWand)
                //ImageMagick.DrawPathMoveToAbsolute(drawingWand, tp0x, tp0y)
                //ImageMagick.DrawPathFinish(drawingWand)
                return emptyList()
            }

            // Max angle per Bezier segment (e.g., 90 degrees)
            val maxAnglePerSegment = PI / 2.0
            val numSegments = ceil(abs(sweepAngle) / maxAnglePerSegment).toInt().coerceAtLeast(1)
            val deltaAngle = sweepAngle / numSegments.toDouble() // Angle step per segment (signed)
            val kappa = (4.0 / 3.0) * tan(abs(deltaAngle) / 4.0) // Bezier approximation factor

            var currentAngle = arcStartAngle
            var errorOccurred = false

            val segments = mutableListOf<BezierSegment>()

            for (i in 0 until numSegments) {
                val angle1 = currentAngle
                val angle2 = currentAngle + deltaAngle

                    // Calculate points for this segment in ellipse local space (0,0 center, no rotation)
                    val cosA1 = cos(angle1); val sinA1 = sin(angle1)
                    val cosA2 = cos(angle2); val sinA2 = sin(angle2)

                    val p0x_local = rx * cosA1;    val p0y_local = ry * sinA1 // Start P0
                    val p3x_local = rx * cosA2;    val p3y_local = ry * sinA2 // End P3

                    // Control points (local)
                    val p1x_local = p0x_local - kappa * ry * sinA1 // Use ry for x tangent calc with ellipse aspect ratio
                    val p1y_local = p0y_local + kappa * rx * cosA1 // Use rx for y tangent calc
                    val p2x_local = p3x_local + kappa * ry * sinA2
                    val p2y_local = p3y_local - kappa * rx * cosA2

                    // Apply ellipse's own transform (rotation + translation)
                    val (p0e_x, p0e_y) = transformEllipsePoint(p0x_local, p0y_local, cx, cy, rotation)
                    val (p1e_x, p1e_y) = transformEllipsePoint(p1x_local, p1y_local, cx, cy, rotation)
                    val (p2e_x, p2e_y) = transformEllipsePoint(p2x_local, p2y_local, cx, cy, rotation)
                    val (p3e_x, p3e_y) = transformEllipsePoint(p3x_local, p3y_local, cx, cy, rotation)

                    // Apply the current canvas transform
                    val (tp0x, tp0y) = transform(p0e_x, p0e_y)
                    val (tp1x, tp1y) = transform(p1e_x, p1e_y)
                    val (tp2x, tp2y) = transform(p2e_x, p2e_y)
                    val (tp3x, tp3y) = transform(p3e_x, p3e_y)

                    println("Bezier segment $i: $tp0x, $tp0y -> $tp1x, $tp1y -> $tp2x, $tp2y -> $tp3x, $tp3y")

                segments += BezierSegment(
                    cp1 = DoubleVector(tp0x, tp0y),
                    cp2 = DoubleVector(tp1x, tp1y),
                    cp3 = DoubleVector(tp2x, tp2y),
                    cp4 = DoubleVector(tp3x, tp3y)
                )


                currentAngle = angle2

                // Optional early exit on error
                // if (checkDrawingWandError(drawingWand, "DrawBezier segment $i")) { errorOccurred = true; break }
            } // End loop


            return segments
        }

        // Helper function to apply ellipse rotation and translation
// (Assumes this exists or is added)
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

        data class BezierSegment(
            val cp1: DoubleVector,
            val cp2: DoubleVector,
            val cp3: DoubleVector,
            val cp4: DoubleVector
        ) {
            fun toList(): List<DoubleVector> {
                return listOf(cp1, cp2, cp3, cp4)
            }
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
