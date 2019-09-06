package jetbrains.livemap.demo

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.canvas.CanvasControl
import jetbrains.livemap.LiveMapSpec
import jetbrains.livemap.api.layers
import jetbrains.livemap.api.paths
import jetbrains.livemap.api.points
import jetbrains.livemap.demo.model.Cities.BOSTON
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
            }
        }
    }
}