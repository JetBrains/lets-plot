/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.coord

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.spatial.projections.Projection
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.base.scale.Mappers

class CoordinatesMapper private constructor(
    val hScaleMapper: ScaleMapper<Double>,
    val vScaleMapper: ScaleMapper<Double>,
    val clientBounds: DoubleRectangle,
    internal val projection: Projection,
    private val flipAxis: Boolean,
) {

    fun toClient(p: DoubleVector): DoubleVector? {
        // ToDo: if 'flipped', "p" is not suitable for map projection.

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

            val leftTop = projection.project(validDomain.origin)
            check(leftTop != null) {
                "Can't project domain left-top: ${validDomain.origin}"
            }
            val domainRB = validDomain.origin.add(validDomain.dimension)
            val rightBottom = projection.project(domainRB)
            check(rightBottom != null) {
                "Can't project domain right-bottom: ${domainRB}"
            }
            val domainProjected = DoubleRectangle.span(leftTop, rightBottom).let {
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
    }
}