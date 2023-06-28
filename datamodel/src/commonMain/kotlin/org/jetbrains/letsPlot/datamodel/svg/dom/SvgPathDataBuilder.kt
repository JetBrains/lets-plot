/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import jetbrains.datalore.base.geometry.DoubleVector
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathData.Action

import kotlin.jvm.JvmOverloads
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class SvgPathDataBuilder @JvmOverloads constructor(private val myDefaultAbsolute: Boolean = true) {

    private val myStringBuilder: StringBuilder
    private var myTension = .7

    enum class Interpolation {
        LINEAR, CARDINAL, MONOTONE
    }

    init {
        myStringBuilder = StringBuilder()
    }

    fun build(): SvgPathData {
        return SvgPathData(myStringBuilder.toString())
    }

    // FIXME: varargs has bad performance, use verbose appends in building methods
    private fun addAction(action: Action, absolute: Boolean, vararg coordinates: Double) {
        if (absolute) {
            myStringBuilder.append(action.absoluteCmd())
        } else {
            myStringBuilder.append(action.relativeCmd())
        }
        for (coord in coordinates) {
            myStringBuilder.append(coord).append(' ')
        }
    }

    private fun addActionWithStringTokens(action: Action, absolute: Boolean, vararg tokens: String) {
        if (absolute) {
            myStringBuilder.append(action.absoluteCmd())
        } else {
            myStringBuilder.append(action.relativeCmd())
        }
        for (token in tokens) {
            myStringBuilder.append(token).append(' ')
        }
    }

    @JvmOverloads
    fun moveTo(x: Double, y: Double, absolute: Boolean = myDefaultAbsolute): SvgPathDataBuilder {
        addAction(Action.MOVE_TO, absolute, x, y)
        return this
    }

    fun moveTo(point: DoubleVector, absolute: Boolean): SvgPathDataBuilder {
        return moveTo(point.x, point.y, absolute)
    }

    fun moveTo(point: DoubleVector): SvgPathDataBuilder {
        return moveTo(point.x, point.y)
    }

    @JvmOverloads
    fun lineTo(x: Double, y: Double, absolute: Boolean = myDefaultAbsolute): SvgPathDataBuilder {
        addAction(Action.LINE_TO, absolute, x, y)
        return this
    }

    fun lineTo(point: DoubleVector, absolute: Boolean): SvgPathDataBuilder {
        return lineTo(point.x, point.y, absolute)
    }

    fun lineTo(point: DoubleVector): SvgPathDataBuilder {
        return lineTo(point.x, point.y)
    }

    @JvmOverloads
    fun horizontalLineTo(x: Double, absolute: Boolean = myDefaultAbsolute): SvgPathDataBuilder {
        addAction(Action.HORIZONTAL_LINE_TO, absolute, x)
        return this
    }

    @JvmOverloads
    fun verticalLineTo(y: Double, absolute: Boolean = myDefaultAbsolute): SvgPathDataBuilder {
        addAction(Action.VERTICAL_LINE_TO, absolute, y)
        return this
    }

    @JvmOverloads
    fun curveTo(x1: Double, y1: Double, x2: Double, y2: Double, x: Double, y: Double, absolute: Boolean = myDefaultAbsolute): SvgPathDataBuilder {
        addAction(Action.CURVE_TO, absolute, x1, y1, x2, y2, x, y)
        return this
    }

    fun curveTo(controlStart: DoubleVector, controlEnd: DoubleVector, to: DoubleVector, absolute: Boolean): SvgPathDataBuilder {
        return curveTo(controlStart.x, controlStart.y, controlEnd.x, controlEnd.y, to.x, to.y, absolute)
    }

    fun curveTo(controlStart: DoubleVector, controlEnd: DoubleVector, to: DoubleVector): SvgPathDataBuilder {
        return curveTo(controlStart.x, controlStart.y, controlEnd.x, controlEnd.y, to.x, to.y)
    }

    @JvmOverloads
    fun smoothCurveTo(x2: Double, y2: Double, x: Double, y: Double, absolute: Boolean = myDefaultAbsolute): SvgPathDataBuilder {
        addAction(Action.SMOOTH_CURVE_TO, absolute, x2, y2, x, y)
        return this
    }

    fun smoothCurveTo(controlEnd: DoubleVector, to: DoubleVector, absolute: Boolean): SvgPathDataBuilder {
        return smoothCurveTo(controlEnd.x, controlEnd.y, to.x, to.y, absolute)
    }

    fun smoothCurveTo(controlEnd: DoubleVector, to: DoubleVector): SvgPathDataBuilder {
        return smoothCurveTo(controlEnd.x, controlEnd.y, to.x, to.y)
    }

    @JvmOverloads
    fun quadraticBezierCurveTo(x1: Double, y1: Double, x: Double, y: Double, absolute: Boolean = myDefaultAbsolute): SvgPathDataBuilder {
        addAction(Action.QUADRATIC_BEZIER_CURVE_TO, absolute, x1, y1, x, y)
        return this
    }

    fun quadraticBezierCurveTo(control: DoubleVector, to: DoubleVector, absolute: Boolean): SvgPathDataBuilder {
        return quadraticBezierCurveTo(control.x, control.y, to.x, to.y, absolute)
    }

    fun quadraticBezierCurveTo(control: DoubleVector, to: DoubleVector): SvgPathDataBuilder {
        return quadraticBezierCurveTo(control.x, control.y, to.x, to.y)
    }

    @JvmOverloads
    fun smoothQuadraticBezierCurveTo(x: Double, y: Double, absolute: Boolean = myDefaultAbsolute): SvgPathDataBuilder {
        addAction(Action.SMOOTH_QUADRATIC_BEZIER_CURVE_TO, absolute, x, y)
        return this
    }

    fun smoothQuadraticBezierCurveTo(to: DoubleVector, absolute: Boolean): SvgPathDataBuilder {
        return smoothQuadraticBezierCurveTo(to.x, to.y, absolute)
    }

    fun smoothQuadraticBezierCurveTo(to: DoubleVector): SvgPathDataBuilder {
        return smoothQuadraticBezierCurveTo(to.x, to.y)
    }

    @JvmOverloads
    fun ellipticalArc(rx: Double, ry: Double, xAxisRotation: Double, largeArc: Boolean, sweep: Boolean,
                      x: Double, y: Double, absolute: Boolean = myDefaultAbsolute): SvgPathDataBuilder {
        addActionWithStringTokens(Action.ELLIPTICAL_ARC, absolute,
                rx.toString(), ry.toString(), xAxisRotation.toString(),
                if (largeArc) "1" else "0", if (sweep) "1" else "0",
                x.toString(), y.toString())
        return this
    }

    fun ellipticalArc(rx: Double, ry: Double, xAxisRotation: Double, largeArc: Boolean, sweep: Boolean,
                      to: DoubleVector, absolute: Boolean): SvgPathDataBuilder {
        return ellipticalArc(rx, ry, xAxisRotation, largeArc, sweep, to.x, to.y, absolute)
    }

    fun ellipticalArc(rx: Double, ry: Double, xAxisRotation: Double, largeArc: Boolean, sweep: Boolean,
                      to: DoubleVector): SvgPathDataBuilder {
        return ellipticalArc(rx, ry, xAxisRotation, largeArc, sweep, to.x, to.y)
    }

    fun closePath(): SvgPathDataBuilder {
        addAction(Action.CLOSE_PATH, myDefaultAbsolute)
        return this
    }

    fun setTension(tension: Double) {
        if (0 > tension || tension > 1) {
            throw IllegalArgumentException("Tension should be within [0, 1] interval")
        }
        myTension = tension
    }

    private fun lineSlope(v1: DoubleVector, v2: DoubleVector): Double {
        return (v2.y - v1.y) / (v2.x - v1.x)
    }

    private fun finiteDifferences(points: List<DoubleVector>): MutableList<Double> {
        val result = ArrayList<Double>(points.size)
        var curSlope = lineSlope(points[0], points[1])
        result.add(curSlope)

        for (i in 1 until points.size - 1) {
            val newSlope = lineSlope(points[i], points[i + 1])
            result.add((curSlope + newSlope) / 2)
            curSlope = newSlope
        }

        result.add(curSlope)

        return result
    }

    private fun doLinearInterpolation(points: Iterable<DoubleVector>) {
        for (point in points) {
            lineTo(point.x, point.y)
        }
    }

    private fun doCardinalInterpolation(points: List<DoubleVector>, tension: Double = myTension) {
        doHermiteInterpolation(points, cardinalTangents(points, tension))
    }

    private fun doHermiteInterpolation(points: List<DoubleVector>, tangents: List<DoubleVector>) {
        if (tangents.size < 1 || points.size != tangents.size && points.size != tangents.size + 2) {
            doLinearInterpolation(points)
        }

        val quad = points.size != tangents.size
        var initPoint = points[0]
        var curPoint = points[1]
        val initTangent = tangents[0]
        var curTangent = initTangent
        var pointIndex = 1

        if (quad) {
            quadraticBezierCurveTo(points[1].x - tangents[0].x * 2 / 3, curPoint.y - initTangent.y * 2 / 3, curPoint.x, curPoint.y, true)
            initPoint = points[1]
            pointIndex = 2
        }

        if (tangents.size > 1) {
            curTangent = tangents[1]
            curPoint = points[pointIndex]
            pointIndex++
            curveTo(initPoint.x + initTangent.x, initPoint.y + initTangent.y, curPoint.x - curTangent.x, curPoint.y - curTangent.y, curPoint.x, curPoint.y, true)

            var tangentIndex = 2
            while (tangentIndex < tangents.size) {
                curPoint = points[pointIndex]
                curTangent = tangents[tangentIndex]
                smoothCurveTo(curPoint.x - curTangent.x, curPoint.y - curTangent.y, curPoint.x, curPoint.y)
                ++tangentIndex
                ++pointIndex
            }
        }

        if (quad) {
            val lastPoint = points[pointIndex]
            quadraticBezierCurveTo(curPoint.x + curTangent.x * 2 / 3, curPoint.y + curTangent.y * 2 / 3, lastPoint.x, lastPoint.y, true)
        }
    }

    private fun cardinalTangents(points: List<DoubleVector>, tension: Double): List<DoubleVector> {
        val tangents = ArrayList<DoubleVector>()
        val a = (1 - tension) / 2
        var prevPoint: DoubleVector
        var curPoint = points[0]
        var nextPoint = points[1]

        for (i in 2 until points.size) {
            prevPoint = curPoint
            curPoint = nextPoint
            nextPoint = points[i]
            tangents.add(DoubleVector(a * (nextPoint.x - prevPoint.x), a * (nextPoint.y - prevPoint.y)))
        }

        return tangents
    }

    private fun monotoneTangents(points: List<DoubleVector>): List<DoubleVector> {
        val m = finiteDifferences(points)
        val eps = 1e-7

        for (i in 0 until points.size - 1) {
            val slope = lineSlope(points[i], points[i + 1])

            if (abs(slope) < eps) {
                m[i] = 0.0
                m[i + 1] = 0.0
            } else {
                val a = m[i] / slope
                val b = m[i + 1] / slope

                var s = a * a + b * b
                if (s > 9) {
                    s = slope * 3 / sqrt(s)
                    m[i] = s * a
                    m[i + 1] = s * b
                }
            }
        }

        val tangents = ArrayList<DoubleVector>()

        for (i in points.indices) {
            val slope = (points[min(i + 1, points.size - 1)].x - points[max(i - 1, 0)].x) / (6 * (1 + m[i] * m[i]))
            tangents.add(DoubleVector(slope, m[i] * slope))
        }

        return tangents
    }

    // see https://github.com/d3/d3/blob/9364923ee2b35ec2eb80ffc4bdac12a7930097fc/src/svg/line.js for reference
    fun interpolatePoints(xs: Collection<Double>, ys: Collection<Double>, interpolation: Interpolation): SvgPathDataBuilder {
        // NOTE: only absolute commands will be produced

        if (xs.size != ys.size) {
            throw IllegalArgumentException("Sizes of xs and ys must be equal")
        }

        val points = ArrayList<DoubleVector>(xs.size)
        val xsArray = ArrayList(xs)
        val ysArray = ArrayList(ys)

        for (i in xs.indices) {
            points.add(DoubleVector(xsArray[i], ysArray[i]))
        }

        when (interpolation) {
            Interpolation.LINEAR -> doLinearInterpolation(points)
            Interpolation.CARDINAL -> if (points.size < 3) {
                doLinearInterpolation(points)
            } else {
                doCardinalInterpolation(points)
            }
            Interpolation.MONOTONE -> if (points.size < 3) {
                doLinearInterpolation(points)
            } else {
                doHermiteInterpolation(points, monotoneTangents(points))
            }
        }

        return this
    }

    fun interpolatePoints(points: Collection<DoubleVector>, interpolation: Interpolation): SvgPathDataBuilder {
        val xs = ArrayList<Double>(points.size)
        val ys = ArrayList<Double>(points.size)
        for (point in points) {
            xs.add(point.x)
            ys.add(point.y)
        }

        return interpolatePoints(xs, ys, interpolation)
    }
}