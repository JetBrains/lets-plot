/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.api.*
import jetbrains.livemap.model.Cities.BOSTON
import jetbrains.livemap.model.Cities.FRISCO
import jetbrains.livemap.model.Cities.MOSCOW
import jetbrains.livemap.model.Cities.NEW_YORK
import jetbrains.livemap.model.Cities.SPB
import jetbrains.livemap.model.GeoObject
import jetbrains.livemap.model.coord

class FeaturesDemoModel(dimension: DoubleVector): DemoModelBase(dimension) {
    override fun createLiveMapSpec(): LiveMapBuilder {
        return basicLiveMap {
            layers {

                points {
                    point {
                        mapId = "Russia"

                        shape = 21
                        radius = 10.0
                        fillColor = Color.GREEN
                    }

                    point {
                        mapId = "USA"

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
                        animation = 2
                    }
                }

                paths {
                    path {
                        geometry(listOf(MOSCOW, SPB).map(GeoObject::geoCoord), isGeodesic = false)

                        strokeWidth = 1.0
                    }

                    path {
                        geometry(listOf(BOSTON, FRISCO).map(GeoObject::geoCoord), isGeodesic = true)

                        strokeWidth = 1.0
                        animation = 2
                    }
                }

                polygons {
                    polygon {
                        geometry(listOf(BOSTON, SPB, MOSCOW).map(GeoObject::geoCoord), isGeodesic = false)

                        fillColor = Color.LIGHT_CYAN
                    }

                    polygon {
                        mapId = "Canada"
                        fillColor = Color.GREEN
                    }
                }

                hLines {
                    line {
                        coord(MOSCOW)
                    }

                    line {
                        mapId = "Vatican"
                        strokeColor = Color.PINK
                        strokeWidth = 3.0
                    }
                }

                vLines {
                    line {
                        coord(BOSTON)
                    }

                    line {
                        mapId = "UK"
                        strokeColor = Color.PINK
                        strokeWidth = 3.0
                    }
                }

                bars {
                    bar {
                        indices = listOf(0, 1, 2)
                        coord(BOSTON)

                        radius = 50.0
                        values = listOf(3.0, 0.0, 2.0)
                        colors = listOf(Color.DARK_GREEN, Color.ORANGE, Color.DARK_MAGENTA)
                    }

                    bar {
                        mapId = "Finland"

                        indices = listOf(3, 4, 5)

                        radius = 50.0
                        values = listOf(-2.0, -1.0, 4.0)
                        colors = listOf(Color.DARK_GREEN, Color.ORANGE, Color.DARK_MAGENTA)
                    }
                }

                pies {
                    pie {
                        indices = listOf(0, 1, 2)
                        mapId = "Sweden"
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
                        mapId = "99431"
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