package jetbrains.livemap.demo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.api.*
import jetbrains.livemap.demo.model.Cities.BOSTON
import jetbrains.livemap.demo.model.Cities.FRISCO
import jetbrains.livemap.demo.model.Cities.MOSCOW
import jetbrains.livemap.demo.model.Cities.NEW_YORK
import jetbrains.livemap.demo.model.Cities.SPB
import jetbrains.livemap.demo.model.GeoObject

class FeaturesDemoModel(dimension: DoubleVector): DemoModelBase(dimension) {
    override fun createLiveMapSpec(): LiveMapBuilder {
        return basicLiveMap {
            layers {
                points {
                    point {
                        lon = 10.0
                        lat = 10.0
                        shape = 21
                        radius = 10.0
                        fillColor = Color.LIGHT_CYAN
                    }

                    point {
                        lon = 0.0
                        lat = 0.0
                        shape = 21
                        radius = 10.0
                        fillColor = Color.MAGENTA
                        animation = 2
                    }
                }

                paths {
                    path {
                        geodesic = true
                        coordinates = listOf(BOSTON, SPB).map(GeoObject::geoCoord)
                        strokeWidth = 1.0
                    }

                    path {
                        geodesic = true
                        coordinates = listOf(BOSTON, FRISCO).map(GeoObject::geoCoord)
                        strokeWidth = 1.0
                        animation = 2
                    }
                }

                polygons {
                    polygon {
                        coordinates = listOf(BOSTON, SPB, MOSCOW).map(GeoObject::geoCoord)
                        fillColor = Color.LIGHT_CYAN
                    }
                }

                //polygons {
                //    polygon {
                //        mapId = "texas"
                //        fillColor = Color.GREEN
                //    }
                //}

                hLines {
                    line {
                        lon = MOSCOW.lon
                        lat = MOSCOW.lat
                    }
                }

                vLines {
                    line {
                        lon = BOSTON.lon
                        lat = BOSTON.lat
                    }
                }

                bars {
                    bar {
                        indices = listOf(0, 1, 2)
                        lon = BOSTON.lon
                        lat = BOSTON.lat
                        radius = 50.0
                        values = listOf(3.0, 0.0, 2.0)
                        colors = listOf(Color.DARK_GREEN, Color.ORANGE, Color.DARK_MAGENTA)
                    }

                    bar {
                        indices = listOf(3, 4, 5)
                        lon = SPB.lon
                        lat = SPB.lat
                        radius = 50.0
                        values = listOf(-2.0, -1.0, 4.0)
                        colors = listOf(Color.DARK_GREEN, Color.ORANGE, Color.DARK_MAGENTA)
                    }
                }

                pies {
                    pie {
                        indices = listOf(0, 1, 2)
                        lon = NEW_YORK.lon
                        lat = NEW_YORK.lat
                        radius = 20.0
                        values = listOf(3.0, 1.0, 2.0)
                        colors = listOf(Color.DARK_GREEN, Color.ORANGE, Color.DARK_MAGENTA)
                    }
                }

                texts {
                    text {
                        label = "KIRIBATI"
                        lon = -157.3662
                        lat = 1.8351
                        size = 50.0
                    }
                }
            }
        }
    }
}