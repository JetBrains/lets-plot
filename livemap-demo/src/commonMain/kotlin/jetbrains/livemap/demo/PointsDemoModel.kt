package jetbrains.livemap.demo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.LiveMapSpec
import jetbrains.livemap.api.layers
import jetbrains.livemap.api.point
import jetbrains.livemap.api.points
import jetbrains.livemap.demo.model.Cities.BOSTON
import jetbrains.livemap.demo.model.Cities.MOSCOW
import jetbrains.livemap.demo.model.Cities.NEW_YORK
import jetbrains.livemap.demo.model.Cities.SPB
import jetbrains.livemap.model.coord

class PointsDemoModel(dimension: DoubleVector) : DemoModelBase(dimension) {

    override fun createLiveMapSpec(): LiveMapSpec {
        return basicLiveMap {
            layers {
                points {
                    point {
                        coord(SPB)
                        strokeColor = Color.WHITE
                    }
                    point {
                        coord(MOSCOW)
                        strokeColor = Color.RED
                    }
                    point {
                        coord(BOSTON)
                        strokeColor = Color.BLUE
                    }
                    point {
                        coord(NEW_YORK)
                        strokeColor = Color.GREEN
                    }
                }
                points {
                    point {
                        lon = 0.0
                        lat = 0.0
                        shape = 21
                        radius = 10.0
                        fillColor = Color.MAGENTA
                    }
                }
            }
        }
    }
}


