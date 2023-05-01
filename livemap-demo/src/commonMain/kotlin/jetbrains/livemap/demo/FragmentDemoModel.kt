/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.api.*
import jetbrains.livemap.model.Cities.GERMANY

class FragmentDemoModel(dimension: DoubleVector) : DemoModelBase(dimension) {
    override fun createLiveMapSpec(): LiveMapBuilder {
        return basicLiveMap {
            geocodingService = Services.devGeocodingService()
            layers {

                polygons {
                    polygon {
                        fillColor = Color.PACIFIC_BLUE
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