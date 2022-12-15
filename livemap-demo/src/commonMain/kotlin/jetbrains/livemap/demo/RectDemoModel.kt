/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.spatial.GeoRectangle
import jetbrains.datalore.base.spatial.LonLatPoint
import jetbrains.datalore.base.typedGeometry.createMultiPolygon
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.datalore.base.typedGeometry.plus
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.api.LiveMapBuilder
import jetbrains.livemap.api.layers
import jetbrains.livemap.api.polygon
import jetbrains.livemap.api.polygons

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