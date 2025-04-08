/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.imagick.canvas

import kotlinx.cinterop.CPointer
import org.jetbrains.letsPlot.commons.intern.math.toRadians
import kotlin.math.cos
import kotlin.math.sin

class MagickPath {
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

    fun arc(x: Double, y: Double, radius: Double, startAngleDeg: Double, endAngleDeg: Double, anticlockwise: Boolean) {
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

    fun draw(drawingWand: CPointer<ImageMagick.DrawingWand>) {
        ImageMagick.DrawPathStart(drawingWand)
        var started = false

        commands.forEach { command ->
            when (command) {
                is MoveTo -> {
                    ImageMagick.DrawPathMoveToAbsolute(drawingWand, command.x, command.y)
                    started = true
                }

                is LineTo -> ImageMagick.DrawPathLineToAbsolute(drawingWand, command.x, command.y)
                is Ellipse -> with(command) {
                    val startRad = toRadians(startAngleDeg)
                    val endRad = toRadians(endAngleDeg)

                    val startX = x + radiusX * cos(startRad)
                    val startY = y + radiusY * sin(startRad)
                    val endX = x + radiusX * cos(endRad)
                    val endY = y + radiusY * sin(endRad)

                    val delta = endAngleDeg - startAngleDeg

                    if (!started) {
                        ImageMagick.DrawPathMoveToAbsolute(drawingWand, startX, startY)
                        started = true
                    } else {
                        ImageMagick.DrawPathLineToAbsolute(drawingWand, startX, startY)
                    }

                    if (delta >= 360.0) {
                        // Full circle: break into two arcs

                        val midAngle = startAngleDeg + (if (anticlockwise) -180.0 else 180.0)
                        val midRad = toRadians(midAngle)
                        val midX = x + radiusX * cos(midRad)
                        val midY = y + radiusY * sin(midRad)

                        val sweepFlag = if (anticlockwise) 0u else 1u
                        val largeArcFlag = 0u

                        ImageMagick.DrawPathEllipticArcAbsolute(
                            drawingWand,
                            radiusX,
                            radiusY,
                            rotation,
                            largeArcFlag,
                            sweepFlag,
                            midX,
                            midY
                        )
                        ImageMagick.DrawPathEllipticArcAbsolute(
                            drawingWand,
                            radiusX,
                            radiusY,
                            rotation,
                            largeArcFlag,
                            sweepFlag,
                            endX,
                            endY
                        )
                    } else {
                        val largeArcFlag = if (delta > 180.0) 1u else 0u
                        val sweepFlag = if (anticlockwise) 0u else 1u

                        ImageMagick.DrawPathEllipticArcAbsolute(
                            drawingWand,
                            radiusX,
                            radiusY,
                            rotation,
                            largeArcFlag,
                            sweepFlag,
                            endX,
                            endY
                        )
                    }

                }

                is ClosePath -> ImageMagick.DrawPathClose(drawingWand)
            }
        }

        ImageMagick.DrawPathFinish(drawingWand)
    }
}
