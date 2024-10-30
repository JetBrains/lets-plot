/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.demo

import demo.livemap.model.Cities.GERMANY
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.livemap.api.*
import org.jetbrains.letsPlot.livemap.core.Projections

class FragmentDemoModel(dimension: DoubleVector) : DemoModelBase(dimension) {
    override fun createLiveMapSpec(): LiveMapBuilder {
        return basicLiveMap {
            projection = Projections.conicEqualArea()
            geocodingService = Services.devGeocodingService()
            layers {

                polygons {
                    polygon {
                        fillColor = Color.PACIFIC_BLUE
                        strokeColor = Color.RED
                        strokeWidth = 1.0
                        sizeScalingRange = -2..Int.MAX_VALUE
                        alphaScalingEnabled = true
                        geoObject = GeoObject(
                            id = GERMANY.id,
                            centroid = GERMANY.centroid,
                            bbox = GERMANY.bbox,
                            position = GERMANY.position
                        )
                    }
                }
            }
        }
    }

}