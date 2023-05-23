/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.math.toRadians
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.geom.util.HintColorUtil
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.common.data.SeriesUtil
import jetbrains.datalore.vis.svg.SvgPathDataBuilder
import jetbrains.datalore.vis.svg.SvgPathElement
import kotlin.math.*

class CurveGeom : GeomBase() {

    var curvature: Double = DEF_CURVATURE   // amount of curvature
    var angle: Double = DEF_ANGLE           // amount to skew the control points of the curve
    var ncp: Int = DEF_NCP                  // number of control points used to draw the curve

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
                            geometry.forEach(::lineTo)
                        }.build()
                    )
                    strokeColor().set(p.color())
                    strokeOpacity().set(p.alpha())
                    fillOpacity().set(0.0)
                }
                root.add(curve)

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

    /*
        Calculates a set of control points based on:
        'curvature', ' angle', and 'ncp'
        and the start and end point locations.
     */
    private fun createGeometry(
        start: DoubleVector,
        end: DoubleVector
    ): List<DoubleVector> {

        if (curvature == 0.0) {
            return listOf(start, end)
        }

        val degreeAngle = angle % 180
        // Treat any angle less than 1 or greater than 179 degrees as a straight line
        // Takes care of some nasty limit effects as well as simplifying things
        if (degreeAngle < 1 || degreeAngle > 179) {
            return listOf(start, end)
        }

        val controlPoints = calcControlPoints(start.x, start.y, end.x, end.y, curvature, degreeAngle, ncp)
        //println(controlPoints)
        return listOf(start) + controlPoints + listOf(end)

        /*
                val curvature = 1

                // mid-point of line:
                val midPoint = start.add(end).mul(0.5)

                // angle of perpendicular to line:
                val theta = 90.0// atan2(end.y - start.y, end.x - start.x) - PI / 2;

                val segmentLength = start.subtract(end).length()
                // distance of control point from mid-point of line:
                val offset = segmentLength * curvature

                // location of control point:
                val c1x = midPoint.x + offset * cos(theta);
                val c1y = midPoint.y + offset * sin(theta);

                val data = SvgPathDataBuilder().apply {
                    moveTo(start.x, start.y)
                    //quadraticBezierCurveTo(c1x, c1y, end.x, end.y)
                    curveTo(start, DoubleVector(c1x, c1y), end)
                }.build()



                val curve =  SvgPathElement().apply {
                    d().set(data)
                    strokeColor().set(p.color())
                    fillOpacity().set(0.0)
                }
         */
    }

    // https://svn.r-project.org/R/trunk/src/library/grid/R/curve.R

    /*
        Find origin of rotation
        Rotate around that origin
    */
    private fun calcControlPoints(
        x1: Double, y1: Double, x2: Double, y2: Double,
        curvature: Double, degreeAngle: Double, ncp: Int
    ): List<DoubleVector> {

        // Negative curvature means curve to the left
        // Positive curvature means curve to the right
        // Special case curvature = 0 (straight line) has been handled

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
        // FIXME:  special case of vertical or horizontal line ?
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
        // dir <- switch(hand, left=-1, right=1)
        val dir = sign(curvature)

        // Angle of rotation depends on location of origin
        val maxTheta = PI + sign(origin * dir) * 2 * atan(abs(origin))

        fun seq(from: Double, to: Double, step: Double): List<Double> {
            require(sign(to) == sign(step))
            val result = ArrayList<Double>()
            var v = from
            fun dir(v: Double) = if (from > to) (v >= to) else (v <= to)
            while (dir(v)) {
                result.add(v)
                v += step
            }
            return result
        }

        // theta <- seq(0, dir*maxtheta, dir*maxtheta/(ncp + 1))[c(-1, -(ncp + 2))]
        val theta = seq(
            from = 0.0,
            to = dir * maxTheta,
            step = dir * maxTheta / (ncp + 1)
        ).toMutableList().apply {
            this.removeAt(0)
            this.removeAt(ncp)
        }

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

    companion object {
        const val HANDLES_GROUPS = false

        const val DEF_ANGLE = 90.0
        const val DEF_CURVATURE = 0.5
        const val DEF_NCP = 5
    }
}

