/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.lineSlope
import org.jetbrains.letsPlot.commons.intern.math.toRadians
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil.finiteOrNull
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.ArrowSpec
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathDataBuilder
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathElement
import kotlin.math.*

class CurveGeom : GeomBase() {

    var curvature: Double = DEF_CURVATURE   // amount of curvature
    var angle: Double = DEF_ANGLE           // amount to skew the control points of the curve
    var ncp: Int = DEF_NCP                  // number of control points used to draw the curve
    var arrowSpec: ArrowSpec? = null
    var spacer: Double = DEF_SPACER         // additional space to shorten curve by moving the start/end

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = HLineGeom.LEGEND_KEY_ELEMENT_FACTORY

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val geomHelper = GeomHelper(pos, coord, ctx)

        for (p in aesthetics.dataPoints()) {
            val x = finiteOrNull(p.x()) ?: continue
            val y = finiteOrNull(p.y()) ?: continue
            val xend = finiteOrNull(p.xend()) ?: continue
            val yend = finiteOrNull(p.yend()) ?: continue

            val clientStart = geomHelper.toClient(DoubleVector(x, y), p) ?: continue
            val clientEnd = geomHelper.toClient(DoubleVector(xend, yend), p) ?: continue

            // Apply padding to curve geometry based on the target size, spacer and arrow spec
            val startPadding = SegmentGeom.padding(p, arrowSpec, spacer, atStart = true)
            val endPadding = SegmentGeom.padding(p, arrowSpec, spacer, atStart = false)

            // Create curve geometry
            val adjustedGeometry = createGeometry(clientStart, clientEnd).let { geometry ->
                SegmentGeom.padLineString(geometry, startPadding, endPadding)
            }

            // Draw curve
            SvgPathElement().apply {
                d().set(
                    SvgPathDataBuilder().apply {
                        moveTo(adjustedGeometry.first())
                        interpolatePoints(
                            adjustedGeometry,
                            SvgPathDataBuilder.Interpolation.BSPLINE
                        )
                    }.build()
                )
                GeomHelper.decorate(this, p, applyAlphaToAll = true, filled = false)
            }
                .also(root::add)

            // arrows
            arrowSpec
                ?.let { ArrowSpec.createArrows(p, adjustedGeometry, it) }
                ?.forEach(root::add)
        }
    }


    /*
        Calculates a set of control points based on:
        'curvature', ' angle', and 'ncp' and the start and end point locations.
    */
    private fun createGeometry(start: DoubleVector, end: DoubleVector): List<DoubleVector> {
        val controlPoints = calcControlPoints(
            start,
            end,
            curvature,
            angle,
            ncp
        )
        return listOf(start) + controlPoints + listOf(end)
    }


    // https://svn.r-project.org/R/trunk/src/library/grid/R/curve.R

    private fun calcControlPoints(
        start: DoubleVector,
        end: DoubleVector,
        curvature: Double,
        angle: Double,
        ncp: Int
    ): List<DoubleVector> {
        // straight line
        if (curvature == 0.0) {
            return emptyList()
        }
        // Angle: make between 0 and 180
        var degreeAngle = angle % 180
        if (degreeAngle < 0) degreeAngle += 180
        // straight line
        if (degreeAngle < 1 || degreeAngle > 179) {
            return emptyList()
        }
        // Inverse angle because of using client coordinates
        degreeAngle *= -1

        val mid = start.add(end).mul(0.5)
        val d = end.subtract(start)

        val rAngle = toRadians(degreeAngle)
        val corner = mid.add(
            start.subtract(mid).rotate(rAngle)
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
        val oxy = calcOrigin(
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

    private fun calcOrigin(
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
        const val DEF_SPACER = 0.0
    }
}

