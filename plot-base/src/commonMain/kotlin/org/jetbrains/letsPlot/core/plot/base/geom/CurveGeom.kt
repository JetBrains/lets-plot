/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.toRadians
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.ArrowSpec
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathDataBuilder
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathElement
import kotlin.math.*

class CurveGeom : GeomBase() {

    var curvature: Double = DEF_CURVATURE   // amount of curvature
    var angle: Double = DEF_ANGLE           // amount to skew the control points of the curve
    var ncp: Int = DEF_NCP                  // number of control points used to draw the curve

    var arrowSpec: ArrowSpec? = null

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = HLineGeom.LEGEND_KEY_ELEMENT_FACTORY

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val targetCollector = getGeomTargetCollector(ctx)
        val helper = GeomHelper(pos, coord, ctx)
        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.CURVE, ctx)

        for (p in aesthetics.dataPoints()) {
            if (SeriesUtil.allFinite(p.x(), p.y(), p.xend(), p.yend())) {

                val start = helper.toClient(DoubleVector(p.x()!!, p.y()!!), p) ?: continue
                val end = helper.toClient(DoubleVector(p.xend()!!, p.yend()!!), p) ?: continue

                val geometry = createGeometry(start, end)
                val curve = SvgPathElement().apply {
                    d().set(
                        SvgPathDataBuilder().apply {
                            moveTo(geometry.first())
                            //geometry.forEach(::lineTo)
                            interpolatePoints(
                                geometry,
                                SvgPathDataBuilder.Interpolation.CARDINAL
                            )
                        }.build()
                    )
                    strokeColor().set(p.color())
                    strokeOpacity().set(p.alpha())
                    fillOpacity().set(0.0)
                }
                root.add(curve)

                // arrows
                arrowSpec?.let { arrowSpec ->
                    createArrows(p, geometry, arrowSpec).forEach(root::add)
                }

                // hints
                targetCollector.addPath(
                    geometry,
                    { p.index() },
                    GeomTargetCollector.TooltipParams(
                        markerColors = colorsByDataPoint(p)
                    )
                )
            }
        }
    }

    private fun createArrows(
        p: DataPointAesthetics,
        geometry: List<DoubleVector>,
        arrowSpec: ArrowSpec
    ): List<SvgPathElement> {
        val arrows = mutableListOf<SvgPathElement?>()
        if (arrowSpec.isOnFirstEnd) {
            val (startPoint, endPoint) = geometry.take(2).reversed()
            arrows += ArrowSpec.createArrow(
                p,
                startPoint,
                endPoint,
                arrowSpec
            )
        }
        if (arrowSpec.isOnLastEnd) {
            val (startPoint, endPoint) = geometry.takeLast(2)
            arrows += ArrowSpec.createArrow(
                p,
                startPoint,
                endPoint,
                arrowSpec
            )
        }
        return arrows.filterNotNull()
    }

    /*
        Calculates a set of control points based on:
        'curvature', ' angle', and 'ncp' and the start and end point locations.
    */
    private fun createGeometry(start: DoubleVector, end: DoubleVector): List<DoubleVector> {

        val degreeAngle = angle % 180

        val controlPoints = calcControlPoints(
            start,
            end,
            if (degreeAngle < 0) -curvature else curvature,
            -degreeAngle,
            ncp
        )
        //println(controlPoints)
        return listOf(start) + controlPoints + listOf(end)
    }


    // https://svn.r-project.org/R/trunk/src/library/grid/R/curve.R

    private fun calcControlPoints(
        start: DoubleVector,
        end: DoubleVector,
        curvature: Double,
        degreeAngle: Double,
        ncp: Int
    ): List<DoubleVector> {
        if (curvature == 0.0) {  // straight line
            return emptyList()
        }

        // Negative curvature means curve to the left
        // Positive curvature means curve to the right
        // Special case curvature = 0 (straight line) has been handled

        val x1 = start.x
        val y1 = start.y
        val x2 = end.x
        val y2 = end.y

        val xm = (x1 + x2) / 2
        val ym = (y1 + y2) / 2
        val dx = x2 - x1
        val dy = y2 - y1

        val angle = toRadians(degreeAngle)
        val sina = sin(angle)
        val cosa = cos(angle)
        // # FIXME:  special case of vertical or horizontal line ?
        val cornerX = xm + (x1 - xm) * cosa - (y1 - ym) * sina
        val cornerY = ym + (y1 - ym) * cosa + (x1 - xm) * sina

        // Calculate angle to rotate region by to align it with x/y axes
        val beta = -atan((cornerY - y1) / (cornerX - x1))
        val sinb = sin(beta)
        val cosb = cos(beta)

        // Rotate end point about start point to align region with x/y axes
        var newX2 = x1 + dx * cosb - dy * sinb
        val newY2 = y1 + dy * cosb + dx * sinb

        // Calculate x-scale factor to make region "square"
        val scalex = (newY2 - y1) / (newX2 - x1)
        // Scale end points to make region "square"
        val newX1 = x1 * scalex
        newX2 *= scalex

        // Calculate the origin in the "square" region
        // (for rotating start point to produce control points)
        // (depends on 'curvature')
        // 'origin' calculated from 'curvature'
        val ratio = 2 * (sin(atan(curvature)).pow(2))
        val origin = curvature - curvature / ratio

        val oxy = calcOrigin(newX1, y1, newX2, newY2, origin)
        val ox = oxy.x
        val oy = oxy.y

        // Calculate control points

        // Direction of rotation depends on 'hand'
        val dir = sign(curvature)

        // Angle of rotation depends on location of origin
        val maxTheta = PI + sign(origin * dir) * 2 * atan(abs(origin))

        // theta <- seq(0, dir*maxtheta, dir*maxtheta/(ncp + 1))[c(-1, -(ncp + 2))]
        val theta = (0 until (ncp + 2))
            .map { it * dir * maxTheta / (ncp + 1) }
            .drop(1)
            .dropLast(1)

        val cosTheta = theta.map(::cos)
        val sinTheta = theta.map(::sin)

        // May have BOTH multiple end points AND multiple
        // control points to generate (per set of end points)
        // Generate consecutive sets of control points by performing
        // matrix multiplication

        val indices = List(theta.size) { index -> index }

        // cpx <- ox + ((newx1 - ox) %*% t(costheta)) - ((y1 - oy) %*% t(sintheta))
        var cpx = indices.map { index ->
            ox + (newX1 - ox) * cosTheta[index] - ((y1 - oy) * sinTheta[index])
        }
        // cpy <- oy + ((y1 - oy) %*% t(costheta)) + ((newx1 - ox) %*% t(sintheta))
        val cpy = indices.map { index ->
            oy + (y1 - oy) * cosTheta[index] + ((newX1 - ox) * sinTheta[index])
        }
        // Reverse transformations (scaling and rotation) to
        // produce control points in the original space
        cpx = cpx.map { it / scalex }

        val sinnb = sin(-beta)
        val cosnb = cos(-beta)
        val finalcpx = indices.map { index -> x1 + (cpx[index] - x1) * cosnb - (cpy[index] - y1) * sinnb }
        val finalcpy = indices.map { index -> y1 + (cpy[index] - y1) * cosnb + (cpx[index] - x1) * sinnb }

        return indices.map { index -> DoubleVector(finalcpx[index], finalcpy[index]) }
    }

    private fun calcOrigin(
        x1: Double,
        y1: Double,
        x2: Double,
        y2: Double,
        origin: Double
    ): DoubleVector {

        // Positive origin means origin to the "right"
        // Negative origin means origin to the "left"
        val xm = (x1 + x2) / 2
        val ym = (y1 + y2) / 2
        val dx = x2 - x1
        val dy = y2 - y1
        val slope = dy / dx
        val oSlope = -1 / slope

        // The origin is a point somewhere along the line between
        // the end points, rotated by 90 (or -90) degrees
        // Two special cases:
        // If slope is non-finite then the end points lie on a vertical line, so
        // the origin lies along a horizontal line (oSlope = 0)
        // If oSlope is non-finite then the end points lie on a horizontal line,
        // so the origin lies along a vertical line (oSlope = Inf)
        val tmpOX = when {
            !slope.isFinite() -> xm
            !oSlope.isFinite() -> xm + origin * (x2 - x1) / 2
            else -> xm + origin * (x2 - x1) / 2
        }

        val tmpOY = when {
            !slope.isFinite() -> ym + origin * (y2 - y1) / 2
            !oSlope.isFinite() -> ym
            else -> ym + origin * (y2 - y1) / 2
        }
        // ALWAYS rotate by -90 about midpoint between end points
        // Actually no need for "hand" because "origin" also encodes direction
        /* val sintheta = when (hand) {
         "left" -> -1
         else -> 1
     }*/
        val sinTheta = -1
        val ox = xm - (tmpOY - ym) * sinTheta
        val oy = ym + (tmpOX - xm) * sinTheta

        return DoubleVector(ox, oy)
    }

    //Refactored

    private fun lineSlope(v1: DoubleVector, v2: DoubleVector): Double {
        return (v2.y - v1.y) / (v2.x - v1.x)
    }

    private fun calcControlPoints2(
        start: DoubleVector,
        end: DoubleVector,
        curvature: Double,
        degreeAngle: Double,
        ncp: Int
    ): List<DoubleVector> {
        if (curvature == 0.0) {  // straight line
            return emptyList()
        }

        val mid = start.add(end).mul(0.5)
        val d = end.subtract(start)

        val angle = toRadians(degreeAngle)
        val corner = mid.add(
            start.subtract(mid).rotate(angle)
        )

        // Calculate angle to rotate region by to align it with x/y axes
        val beta = -atan(lineSlope(start, corner))

        // Rotate end point about start point to align region with x/y axes
        val new = start.add(
            d.rotate(beta)
        )

        // Calculate x-scale factor to make region "square"
        val scaleX = lineSlope(start, new)

        // Calculate the origin in the "square" region
        // (for rotating start point to produce control points)
        // (depends on 'curvature')
        // 'origin' calculated from 'curvature'
        val ratio = 2 * (sin(atan(curvature)).pow(2))
        val origin = curvature - curvature / ratio

        val ps = DoubleVector(start.x * scaleX, start.y)
        val oxy = calcOrigin2(
            ps = ps,
            pe = DoubleVector(new.x * scaleX, new.y),
            origin
        )

        // Direction of rotation
        val dir = sign(curvature)

        // Angle of rotation depends on location of origin
        val maxTheta = PI + sign(origin * dir) * 2 * atan(abs(origin))

        val theta = (0 until (ncp + 2))
            .map { it * dir * maxTheta / (ncp + 1) }
            .drop(1)
            .dropLast(1)

        // May have BOTH multiple end points AND multiple
        // control points to generate (per set of end points)
        // Generate consecutive sets of control points by performing
        // matrix multiplication

        val indices = List(theta.size) { index -> index }

        val p = ps.subtract(oxy)
        val cp = indices.map { index ->
            oxy.add(
                p.rotate(theta[index])
            )
        }
            // Reverse transformations (scaling and rotation) to
            // produce control points in the original space
            .map {
                DoubleVector(
                    it.x / scaleX,
                    it.y
                )
            }

        return indices.map { index ->
            start.add(
                cp[index].subtract(start).rotate(-beta)
            )
        }
    }

    private fun calcOrigin2(
        ps: DoubleVector,
        pe: DoubleVector,
        origin: Double
    ): DoubleVector {

        val mid = ps.add(pe).mul(0.5)
        val d = pe.subtract(ps)
        val slope = lineSlope(ps, pe)

        val oSlope = -1 / slope

        // The origin is a point somewhere along the line between
        // the end points, rotated by 90 (or -90) degrees
        // Two special cases:
        // If slope is non-finite then the end points lie on a vertical line, so
        // the origin lies along a horizontal line (oSlope = 0)
        // If oSlope is non-finite then the end points lie on a horizontal line,
        // so the origin lies along a vertical line (oSlope = Inf)
        val tmpOX = when {
            !slope.isFinite() -> 0.0
            !oSlope.isFinite() -> origin * d.x / 2
            else -> origin * d.x / 2
        }

        val tmpOY = when {
            !slope.isFinite() -> origin * d.y / 2
            !oSlope.isFinite() -> 0.0
            else -> origin * d.y / 2
        }

        return DoubleVector(mid.x + tmpOY, mid.y - tmpOX)
    }

    companion object {
        const val HANDLES_GROUPS = false

        const val DEF_ANGLE = 90.0
        const val DEF_CURVATURE = 0.5
        const val DEF_NCP = 5
    }
}

