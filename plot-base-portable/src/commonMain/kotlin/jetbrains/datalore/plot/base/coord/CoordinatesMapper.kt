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

class CoordinatesMapper constructor(
    adjustedDomain: DoubleRectangle,
    clientSize: DoubleVector,
    internal val projection: Projection
) {
    val hScaleMapper: ScaleMapper<Double>
    val vScaleMapper: ScaleMapper<Double>
    val clientBounds: DoubleRectangle

    init {
        // ToDo: if 'flipped', "adjustedDomain" is not suitable for map projection.

        val leftTop = projection.project(adjustedDomain.origin)
        check(leftTop != null) {
            "Can't project domain left-top: ${adjustedDomain.origin}"
        }
        val domainRB = adjustedDomain.origin.add(adjustedDomain.dimension)
        val rightBottom = projection.project(domainRB)
        check(rightBottom != null) {
            "Can't project domain right-bottom: ${domainRB}"
        }
        val domainProjected = DoubleRectangle.span(leftTop, rightBottom)
        check(domainProjected.xRange().length != 0.0) {
            "Can't create coordinates mapper: X-domain size is 0.0"
        }
        check(domainProjected.yRange().length != 0.0) {
            "Can't create coordinates mapper: Y-domain size is 0.0"
        }

        hScaleMapper = Mappers.mul(domainProjected.xRange(), clientSize.x)
        vScaleMapper = Mappers.mul(domainProjected.yRange(), clientSize.y)

        val clientOrigin = DoubleVector(
            hScaleMapper(domainProjected.origin.x)!!,
            vScaleMapper(domainProjected.origin.y)!!,
        )
        clientBounds = DoubleRectangle(clientOrigin, clientSize)
    }

    fun toClient(p: DoubleVector): DoubleVector? {
        // ToDo: if 'flipped', "p" is not suitable for map projection.

        val projected = projection.project(p)
        if (projected != null) {
            val mappedX = hScaleMapper(projected.x)
            val mappedY = vScaleMapper(projected.y)
            if (mappedX != null && mappedY != null) {
                return DoubleVector(mappedX, mappedY)
            }
        }
        return null
    }
}