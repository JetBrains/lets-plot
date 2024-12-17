/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.demo

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.spatial.GeoRectangle
import org.jetbrains.letsPlot.commons.intern.spatial.LonLatPoint
import org.jetbrains.letsPlot.commons.intern.typedGeometry.createMultiPolygon
import org.jetbrains.letsPlot.commons.intern.typedGeometry.explicitVec
import org.jetbrains.letsPlot.commons.intern.typedGeometry.plus
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.livemap.api.LiveMapBuilder
import org.jetbrains.letsPlot.livemap.api.layers
import org.jetbrains.letsPlot.livemap.api.polygon
import org.jetbrains.letsPlot.livemap.api.polygons

class RectDemoModel(dimension: DoubleVector) : DemoModelBase(dimension) {

    override fun createLiveMapSpec(): LiveMapBuilder {
        return basicLiveMap {
            layers {
                polygons {
                    polygon {
//                        geometry(rect(-104.032789, 35.309947, -80.237456, 48.979166), isGeodesic = false)
                        geometry = createMultiPolygon(rect(25.0, 69.0, 26.0, 70.0))

                        fillColor = Color(255, 175, 175, 153)
                        strokeColor = Color(128, 0, 128, 153)
                        strokeWidth = 6.0
                    }
                }
            }
        }
    }

    private fun rect(minLongitude: Double, minLatitude: Double, maxLongitude: Double, maxLatitude: Double): List<LonLatPoint> {
        return GeoRectangle(
            minLongitude,
            minLatitude,
            maxLongitude,
            maxLatitude
        )
            .splitByAntiMeridian()
            .flatMap {
                listOf(
                    it.origin,
                    it.origin + explicitVec(it.dimension.x, 0.0),
                    it.origin + it.dimension,
                    it.origin + explicitVec(0.0, it.dimension.y),
                    it.origin
                )
            }
    }
}