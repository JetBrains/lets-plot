/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.coord

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangles.boundingBox
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.spatial.projections.Projection
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.AdaptiveResampler.Companion.PIXEL_PRECISION
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.AdaptiveResampler.Companion.resample
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.scale.Mappers

class CoordinatesMapper(
    val hScaleMapper: ScaleMapper<Double>,
    val vScaleMapper: ScaleMapper<Double>,
    val clientBounds: DoubleRectangle,
    internal val projection: Projection,
    private val flipAxis: Boolean,
) {
    private var cachedUnitSize: DoubleVector? = null

    val isLinear: Boolean = !projection.nonlinear

    fun toClient(p: DoubleVector): DoubleVector? {
        val projected = projection.project(p)
        if (projected != null) {
            val mappedX = hScaleMapper(
                if (!flipAxis) projected.x else projected.y
            )
            val mappedY = vScaleMapper(
                if (!flipAxis) projected.y else projected.x
            )
            if (mappedX != null && mappedY != null) {
                return DoubleVector(mappedX, mappedY)
            }
        }
        return null
    }


    fun unitSize(p: DoubleVector): DoubleVector {
        return if (projection.nonlinear) {
            unitSizeIntern(p)
        } else {
            if (cachedUnitSize == null) {
                cachedUnitSize = unitSizeIntern(p)
            }
            cachedUnitSize!!
        }
    }

    private fun unitSizeIntern(p: DoubleVector): DoubleVector {
        val c = toValidUnitSquareCenter(p, projection)
        val width = run {
            val p0 = toClient(DoubleVector(c.x - 0.5, c.y))!!
            val p1 = toClient(DoubleVector(c.x + 0.5, c.y))!!
            p1.subtract(p0).length()
        }
        val height = run {
            val p0 = toClient(DoubleVector(c.x, c.y - 0.5))!!
            val p1 = toClient(DoubleVector(c.x, c.y + 0.5))!!
            p1.subtract(p0).length()
        }
        return DoubleVector(width, height)
    }

    fun flip(): CoordinatesMapper {
        return CoordinatesMapper(hScaleMapper, vScaleMapper, clientBounds, projection, !flipAxis)
    }

    companion object {
        fun create(
            adjustedDomain: DoubleRectangle,
            clientSize: DoubleVector,
            projection: Projection,
            flipAxis: Boolean,
        ): CoordinatesMapper {
            val validDomain = when (flipAxis) {
                true -> adjustedDomain.flip()  // un-flip before projecting.
                false -> adjustedDomain
            }

            val domainProjected = projectDomain(projection, validDomain).let {
                when (flipAxis) {
                    true -> it.flip()  // un-flip the domain
                    false -> it
                }
            }
            check(domainProjected.xRange().length != 0.0) {
                "Can't create coordinates mapper: X-domain size is 0.0"
            }
            check(domainProjected.yRange().length != 0.0) {
                "Can't create coordinates mapper: Y-domain size is 0.0"
            }

            val hScaleMapper = Mappers.mul(domainProjected.xRange(), clientSize.x)
            val vScaleMapper = Mappers.mul(domainProjected.yRange(), clientSize.y)

            val clientOrigin = DoubleVector(
                hScaleMapper(domainProjected.origin.x)!!,
                vScaleMapper(domainProjected.origin.y)!!,
            )
            val clientBounds = DoubleRectangle(clientOrigin, clientSize)
            return CoordinatesMapper(hScaleMapper, vScaleMapper, clientBounds, projection, flipAxis)
        }

        fun toValidUnitSquareCenter(p: DoubleVector, projection: Projection): DoubleVector {
            val validDomain = projection.validDomain()
            val x = if (p.x < validDomain.left + 0.5) validDomain.left + 0.5
            else if (p.x > validDomain.right - 0.5) validDomain.right - 0.5
            else p.x

            val y = if (p.y < validDomain.top + 0.5) validDomain.top + 0.5
            else if (p.y > validDomain.bottom - 0.5) validDomain.bottom - 0.5
            else p.y

            return DoubleVector(x, y)
        }
    }
}

fun projectDomain(
    projection: Projection,
    domain: DoubleRectangle
): DoubleRectangle {
    val leftTop: DoubleVector
    val rightBottom: DoubleVector

    if (projection.nonlinear) {
        // TODO: better to use transformed data points instead of grid.
        // Grid can produce projected domain that is much bigger than the actual data points domain.
        fun points(min: Double, max: Double, n: Int = 10): List<Double> {
            val step = (max - min) / n
            return listOf(min) + (0..n).map { min + it * step } + listOf(max)
        }

        val hLines = points(domain.top, domain.bottom).map { DoubleVector(domain.left, it) to DoubleVector(domain.right, it) }
        val vLines = points(domain.left, domain.right).map { DoubleVector(it, domain.top) to DoubleVector(it, domain.bottom) }
        val grid = (hLines + vLines).map { (p1, p2) -> resample(p1, p2, PIXEL_PRECISION, projection::project) }

        val projectedDomain = boundingBox(grid.flatten()) ?: error("Can't calculate bounding box for projected domain")

        leftTop = DoubleVector(projectedDomain.left, projectedDomain.top)
        rightBottom = DoubleVector(projectedDomain.right, projectedDomain.bottom)

    } else {
        val domainRB = domain.origin.add(domain.dimension)
        leftTop = projection.project(domain.origin) ?: error("Can't project domain left-top: ${domain.origin}")
        rightBottom = projection.project(domainRB) ?: error("Can't project domain right-bottom: $domainRB")
    }
    return DoubleRectangle.span(leftTop, rightBottom)
}
