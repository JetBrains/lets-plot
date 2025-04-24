/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.shape

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.intern.math.toRadians
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.raster.mapping.svg.SvgTransformParser
import kotlin.math.*


internal class Path : Figure() {
    //var fillRule: PathFillMode? by visualProp(null)
    var pathData: PathData? by visualProp(null, managed = true)

    override fun render(canvas: Canvas) {
        val path = pathData ?: return

        fillPaint?.let {
            drawPath(path, canvas.context2d)
            canvas.context2d.fill(it)
        }
        strokePaint?.let {
            drawPath(path, canvas.context2d)
            canvas.context2d.stroke(it)
        }
    }

    fun drawPath(pathData: PathData, context2d: Context2d) {
        var curX = 0.0
        var curY = 0.0

        context2d.beginPath()

        pathData.commands.forEach { cmd ->
            when (cmd.name) {
                "M" -> {
                    curX = cmd.args[0]!!.toDouble()
                    curY = cmd.args[1]!!.toDouble()
                    context2d.moveTo(curX, curY)
                }

                "m" -> {
                    curX += cmd.args[0]!!.toDouble()
                    curY += cmd.args[1]!!.toDouble()
                    context2d.moveTo(curX, curY)
                }

                "L" -> {
                    curX = cmd.args[0]!!.toDouble()
                    curY = cmd.args[1]!!.toDouble()
                    context2d.lineTo(curX, curY)
                }

                "l" -> {
                    curX += cmd.args[0]!!.toDouble()
                    curY += cmd.args[1]!!.toDouble()
                    context2d.lineTo(curX, curY)
                }

                "A", "a" -> {
                    val rx = cmd.args[0]!!.toDouble()
                    val ry = cmd.args[1]!!.toDouble()
                    val angle = toRadians(cmd.args[2]!!.toDouble())
                    val largeArcFlag = cmd.args[3]!!.toInt()
                    val sweepFlag = cmd.args[4]!!.toInt()
                    val x = cmd.args[5]!!.toDouble()
                    val y = cmd.args[6]!!.toDouble()

                    val (dx, dy) = when (cmd.name) {
                        "A" -> (x - curX) to (y - curY)
                        "a" -> x to y
                        else -> error("Should not happen")
                    }

                    val (newX, newY) = drawRelativeSvgEllipse(curX, curY, rx, ry, angle, largeArcFlag, sweepFlag, dx, dy, context2d)
                    curX += newX
                    curY += newY
                }

                "h" -> {
                    curX += cmd.args[0]!!
                    context2d.lineTo(curX, curY)
                }

                "V" -> {
                    curY = cmd.args[0]!!.toDouble()
                    context2d.lineTo(curX, curY)
                }

                "v" -> {
                    curY += cmd.args[0]!!
                    context2d.lineTo(curX, curY)
                }

                "C" -> {
                    val cp1x = cmd.args[0]!!.toDouble()
                    val cp1y = cmd.args[1]!!.toDouble()
                    val cp2x = cmd.args[2]!!.toDouble()
                    val cp2y = cmd.args[3]!!.toDouble()
                    curX = cmd.args[4]!!.toDouble()
                    curY = cmd.args[5]!!.toDouble()
                    context2d.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, curX, curY)
                }

                "c" -> {
                    val cp1x = cmd.args[0]!!.toDouble()
                    val cp1y = cmd.args[1]!!.toDouble()
                    val cp2x = cmd.args[2]!!.toDouble()
                    val cp2y = cmd.args[3]!!.toDouble()
                    curX += cmd.args[4]!!.toDouble()
                    curY += cmd.args[5]!!.toDouble()
                    context2d.bezierCurveTo(curX + cp1x, curY + cp1y, curX + cp2x, curY + cp2y, curX, curY)
                }

                "Z", "z" -> context2d.closePath()

                else -> println("Path - Unsupported command: ${cmd.name}")
            }
        }
    }

    // see:
    // https://www.w3.org/TR/SVG11/implnote.html#ArcImplementationNotes
    fun drawRelativeSvgEllipse(
        curX: Double, curY: Double,
        rxIn: Double, ryIn: Double,
        angle: Double, largeArcFlag: Int, sweepFlag: Int,
        dx: Double, dy: Double,
        context2d: Context2d
    ): Pair<Double, Double> {
        if (rxIn == 0.0 || ryIn == 0.0) {
            // If either radius is zero, draw a line
            context2d.lineTo(curX + dx, curY + dy)
            return Pair(curX + dx, curY + dy)
        }

        val x1 = curX
        val y1 = curY
        val x2 = curX + dx
        val y2 = curY + dy

        // Ensure radii are positive
        var rx = abs(rxIn)
        var ry = abs(ryIn)

        // Step 1: Transform start/end points into ellipse space
        val dx2 = (x1 - x2) / 2.0
        val dy2 = (y1 - y2) / 2.0

        val x1p = cos(angle) * dx2 + sin(angle) * dy2
        val y1p = -sin(angle) * dx2 + cos(angle) * dy2

        // Correct radii if they are too small
        val lambda = x1p * x1p / (rx * rx) + y1p * y1p / (ry * ry)
        if (lambda > 1) {
            val sqrtLambda = sqrt(lambda)
            rx *= sqrtLambda
            ry *= sqrtLambda
        }

        val rxSq = rx * rx
        val rySq = ry * ry
        val x1pSq = x1p * x1p
        val y1pSq = y1p * y1p

        // Step 2: Compute center (cx, cy) of the ellipse
        val denom = (rxSq * y1pSq + rySq * x1pSq)
        val num = (rxSq * rySq - rxSq * y1pSq - rySq * x1pSq)
        val factor = sqrt(max(0.0, num / denom)) * if (largeArcFlag == sweepFlag) -1 else 1

        val cxp = factor * (rx * y1p / ry)
        val cyp = factor * (-ry * x1p / rx)

        // Step 3: Transform center back to the original coordinate system
        val cx = cos(angle) * cxp - sin(angle) * cyp + (x1 + x2) / 2.0
        val cy = sin(angle) * cxp + cos(angle) * cyp + (y1 + y2) / 2.0

        // Step 4: Compute start angle and end angle
        val v1x = (x1p - cxp) / rx
        val v1y = (y1p - cyp) / ry
        val v2x = (-x1p - cxp) / rx
        val v2y = (-y1p - cyp) / ry

        val theta1 = atan2(v1y, v1x)
        var deltaTheta = atan2(v2y, v2x) - theta1

        // Ensure correct arc selection
        if (sweepFlag != 0) {
            if (deltaTheta < 0) deltaTheta += 2 * PI
        } else {
            if (deltaTheta > 0) deltaTheta -= 2 * PI
        }

        val startAngle = theta1
        val endAngle = theta1 + deltaTheta

        // Determine direction (anticlockwise = !sweepFlag)
        val anticlockwise = sweepFlag == 0

        // Call the canvas ellipse function
        context2d.ellipse(cx, cy, rx, ry, angle, startAngle, endAngle, anticlockwise)

        // Return new position (for updating curX, curY)
        return Pair(x2, y2)
    }

    override val localBounds: DoubleRectangle
        get() {
            // `paint.getFillPath()` is not available in skiko v. 0.7.63
//            return (strokePaint?.getFillPath(path) ?: path).bounds

            val path = pathData ?: return DoubleRectangle.XYWH(0, 0, 0, 0)
            val strokeWidth = strokePaint?.strokeWidth ?: return path.bounds

            return path.bounds.inflate(strokeWidth / 2.0)
        }

    class PathData(
        internal val commands: List<SvgTransformParser.Result>
    ) {
        val bounds: DoubleRectangle = DoubleRectangle.XYWH(0.0, 0.0, 0.0, 0.0)
    }
}
