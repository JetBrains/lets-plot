/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.imagick.canvas

import MagickWand.*
import kotlinx.cinterop.CPointer
import org.jetbrains.letsPlot.commons.intern.math.toRadians

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
        val startAngle: Double,
        val endAngle: Double,
        val anticlockwise: Boolean
    ) : PathCommand()

    private object ClosePath : PathCommand()

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

    fun arc(x: Double, y: Double, radius: Double, startAngle: Double, endAngle: Double, anticlockwise: Boolean) {
        commands.add(Ellipse(x, y, radius, radius, 0.0, startAngle, endAngle, anticlockwise))
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

    fun draw(drawingWand: CPointer<DrawingWand>) {
        DrawPathStart(drawingWand)
        var started = false

        commands.forEach { command ->
            when (command) {
                is MoveTo -> {
                    DrawPathMoveToAbsolute(drawingWand, command.x, command.y)
                    started = true
                }
                is LineTo -> DrawPathLineToAbsolute(drawingWand, command.x, command.y)
                is Ellipse -> with(command) {
                    // Convert degrees to radians
                    val startRad = toRadians(startAngle)
                    val endRad = toRadians(endAngle)

                    // Compute start and end points of the arc
                    val startX = x + radiusX * kotlin.math.cos(startRad)
                    val startY = y + radiusY * kotlin.math.sin(startRad)
                    val endX = x + radiusX * kotlin.math.cos(endRad)
                    val endY = y + radiusY * kotlin.math.sin(endRad)

                    // Determine arc flags
                    val delta = ((endAngle - startAngle + 360) % 360).let { if (anticlockwise) 360 - it else it }
                    val largeArcFlag = if (delta > 180.0) 1u else 0u
                    val sweepFlag = if (anticlockwise) 0u else 1u
                    // Begin drawing path from the arc's starting point

                    if (!started) {
                        DrawPathMoveToAbsolute(drawingWand, startX, startY)
                        started = true
                    } else {
                        DrawPathLineToAbsolute(drawingWand, startX, startY)
                    }

                    // Draw the elliptical arc
                    DrawPathEllipticArcAbsolute(
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

                is ClosePath -> {
                    DrawPathClose(drawingWand)
                }
            }
        }
    }
}
