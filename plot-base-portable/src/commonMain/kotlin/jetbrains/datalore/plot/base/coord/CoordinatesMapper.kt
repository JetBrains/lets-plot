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

class CoordinatesMapper(
    domain: DoubleRectangle,
    clientSize: DoubleVector,
    internal val projection: Projection
) {
    val hScaleMapper: ScaleMapper<Double>
    val vScaleMapper: ScaleMapper<Double>
    val clientBounds: DoubleRectangle

    init {
        val validDomain = projection.validDomain().intersect(domain)
        if (validDomain != null) {
            val leftTop = projection.project(validDomain.origin)!!
            val rightBottom = projection.project(validDomain.origin.add(validDomain.dimension))!!
            val domainProjected = DoubleRectangle.span(leftTop, rightBottom)
            hScaleMapper = Mappers.mul(domainProjected.xRange(), clientSize.x)
            vScaleMapper = Mappers.mul(domainProjected.yRange(), clientSize.y)

            val clientOrigin = DoubleVector(
                hScaleMapper(domainProjected.origin.x)!!,
                vScaleMapper(domainProjected.origin.y)!!,
            )
            clientBounds = DoubleRectangle(clientOrigin, clientSize)
        } else {
            // ToDo: ???
            vScaleMapper = NullMapper()
            hScaleMapper = NullMapper()
            clientBounds = DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)
        }
    }

    fun toClient(p: DoubleVector): DoubleVector? {
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

    private class NullMapper : ScaleMapper<Double> {
        override fun invoke(v: Double?): Double? = null
    }
}