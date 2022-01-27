/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.spatial.GeoRectangle
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.api.*
import jetbrains.livemap.config.DevParams
import jetbrains.livemap.model.Cities.BOSTON
import jetbrains.livemap.model.Cities.FRISCO
import jetbrains.livemap.model.Cities.MOSCOW
import jetbrains.livemap.model.Cities.NEW_YORK
import jetbrains.livemap.model.Cities.SPB
import jetbrains.livemap.model.coord

class FeaturesDemoModel(dimension: DoubleVector) : DemoModelBase(dimension) {
    override fun createLiveMapSpec(): LiveMapBuilder {
        return basicLiveMap {
            zoom = 5
            devParams = DevParams(mapOf(DevParams.ENABLE_SCALING.key to true))
            geocodingService = Services.devGeocodingService()
            location { coordinate = explicitVec(96.37587535342406, 61.8742484121002) }
            layers {

                points {
                    point {
                        coord(96.37587535342406, 61.8742484121002) // Russia

                        shape = 21
                        radius = 10.0
                        fillColor = Color.GREEN
                    }

                    point {
                        coord(-101.44978535214234, 40.21841813198989) // USA

                        shape = 21
                        radius = 10.0
                        fillColor = Color.GREEN
                    }

                    point {
                        coord(10.0, 10.0)

                        shape = 21
                        radius = 10.0
                        fillColor = Color.LIGHT_CYAN
                    }

                    point {
                        coord(0.0, 0.0)

                        shape = 21
                        radius = 10.0
                        fillColor = Color.MAGENTA
                        //animation = 2
                    }
                }

                paths {
                    path {
                        geometry(listOf(MOSCOW, SPB).map(GeoObject::centroid), isGeodesic = false)

                        strokeWidth = 1.0
                    }

                    path {
                        geometry(listOf(BOSTON, FRISCO).map(GeoObject::centroid), isGeodesic = true)

                        strokeWidth = 1.0
                        //animation = 2
                    }
                }

                polygons {
                    polygon {
                        fillColor = Color.PACIFIC_BLUE
                        geoObject = GeoObject(
                            id = "148838",
                            centroid = Vec(-99.74261, 37.25026),
                            bbox = GeoRectangle(
                                startLongitude = 144.618412256241,
                                endLongitude = -64.56484794616701,
                                minLatitude = -14.3740922212601,
                                maxLatitude = 71.38780832290649
                            ),
                            position = GeoRectangle(
                                startLongitude = 144.618412256241,
                                endLongitude = -64.56484794616701,
                                minLatitude = -14.3740922212601,
                                maxLatitude = 71.38780832290649
                            )
                        )
                    }
                }

                polygons {
                    polygon {
                        geometry(listOf(BOSTON, SPB, MOSCOW).map(GeoObject::centroid), isGeodesic = false)

                        fillColor = Color.LIGHT_CYAN
                    }
                }

                hLines {
                    line {
                        coord(MOSCOW)
                    }

                    line {
                        coord(12.45326376667447, 41.90352424895908)// = "Vatican"
                        strokeColor = Color.PINK
                        strokeWidth = 3.0
                    }
                }

                vLines {
                    line {
                        coord(BOSTON)
                    }

                    line {
                        coord(0.011590487865875687, 51.324793768104506) // = "UK"
                        strokeColor = Color.PINK
                        strokeWidth = 3.0
                    }
                }

                bars {
                    bar {
                        indices = listOf(0, 1, 2)
                        coord(BOSTON)

                        radius = 30.0
                        values = listOf(3.0, 0.0, 2.0)
                        colors = listOf(Color.DARK_GREEN, Color.ORANGE, Color.DARK_MAGENTA)
                    }

                    bar {
                        coord(26.642449862865874, 63.339789715873216) // = "Finland"

                        indices = listOf(3, 4, 5)

                        radius = 30.0
                        values = listOf(-2.0, -1.0, 4.0)
                        colors = listOf(Color.DARK_GREEN, Color.ORANGE, Color.DARK_MAGENTA)
                    }
                }

                pies {
                    pie {
                        indices = listOf(0, 1, 2)
                        coord(17.439340121654936, 64.22860968236267) // = "Sweden"
                        radius = 20.0
                        values = listOf(-2.0, 5.0, 1.0)
                        colors = listOf(Color.DARK_GREEN, Color.ORANGE, Color.DARK_MAGENTA)
                    }

                    pie {
                        indices = listOf(0, 1, 2)
                        coord(NEW_YORK)
                        radius = 20.0
                        values = listOf(3.0, 1.0, 2.0)
                        colors = listOf(Color.DARK_GREEN, Color.ORANGE, Color.DARK_MAGENTA)
                    }
                }

                texts {
                    text {
                        label = "POLAND"
                        coord(20.19278860798306, 51.50358834244579) // = "99431"
                        size = 25.0
                        angle = 30.0
                    }

                    text {
                        label = "KIRIBATI"
                        coord(-157.3662, 1.8351)
                        size = 50.0
                    }
                }
            }
        }
    }
}