package jetbrains.livemap.demo

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.canvas.CanvasControl
import jetbrains.livemap.LiveMapSpec
import jetbrains.livemap.api.*
import jetbrains.livemap.demo.model.Cities.BOSTON
import jetbrains.livemap.demo.model.Cities.MOSCOW
import jetbrains.livemap.demo.model.Cities.NEW_YORK
import jetbrains.livemap.demo.model.Cities.SPB
import jetbrains.livemap.demo.model.GeoObject

class FeaturesDemoModel(canvasControl: CanvasControl): DemoModelBase(canvasControl) {
    override fun createLiveMapSpec(): LiveMapSpec {
        return basicLiveMap {
            layers {
                points {
                    point {
                        lon = 0.0
                        lat = 0.0
                        shape = 21
                        radius = 10.0
                        fillColor = Color.MAGENTA
                    }
                }

                paths {
                    path {
                        geodesic = true
                        coordinates = listOf(BOSTON, SPB).map(GeoObject::geoCoord)
                        strokeWidth = 1.0
                    }
                }

                polygons {
                    polygon {
                        coordinates = listOf(BOSTON, SPB, MOSCOW).map(GeoObject::geoCoord)
                        fillColor = Color.LIGHT_CYAN
                    }
                }

                hLines {
                    line {
                        lon = MOSCOW.lon
                        lat = MOSCOW.lat
                        lineDash = listOf(8.0, 8.0)
                    }
                }

                vLines {
                    line {
                        lon = BOSTON.lon
                        lat = BOSTON.lat
                        lineDash = listOf(8.0, 8.0)
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