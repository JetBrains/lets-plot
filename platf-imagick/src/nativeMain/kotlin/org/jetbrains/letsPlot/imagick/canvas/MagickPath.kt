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
    private data class Arc(val x: Double, val y: Double, val radius: Double, val startAngle: Double, val endAngle: Double, val anticlockwise: Boolean) : PathCommand()
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
        commands.add(Arc(x, y, radius, startAngle, endAngle, anticlockwise))
    }

    fun draw(drawingWand: CPointer<DrawingWand>) {
        DrawPathStart(drawingWand)

        commands.forEach { command ->
            when (command) {
                is MoveTo -> DrawPathMoveToAbsolute(drawingWand, command.x, command.y)
                is LineTo -> DrawPathLineToAbsolute(drawingWand, command.x, command.y)
                is Arc -> {
                    val startAngle = command.startAngle
                    val endAngle = command.endAngle
                    val anticlockwise = command.anticlockwise
                    val x = command.x
                    val y = command.y
                    val radius = command.radius

                    // Convert angles to radians
                    val startRad = toRadians(startAngle)
                    val endRad = toRadians(endAngle)

                    // Compute start and end points
                    val startX = x + radius * kotlin.math.cos(startRad)
                    val startY = y + radius * kotlin.math.sin(startRad)
                    val endX = x + radius * kotlin.math.cos(endRad)
                    val endY = y + radius * kotlin.math.sin(endRad)

                    // Determine large-arc and sweep flags
                    val largeArcFlag = if (kotlin.math.abs(endAngle - startAngle) > 180) 1U else 0U
                    val sweepFlag = if (anticlockwise) 0U else 1U

                    // Move to start point
                    DrawPathMoveToAbsolute(drawingWand, startX, startY)

                    // Draw elliptical arc
                    DrawPathEllipticArcAbsolute(drawingWand, radius, radius, 0.0, largeArcFlag, sweepFlag, endX, endY)
                }
                is ClosePath -> {
                    //DrawPathClose(drawingWand)
                }
            }
        }
    }
}