package jetbrains.livemap.demo

import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSource
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.canvas.CanvasControl
import jetbrains.datalore.visualization.plot.base.geom.LivemapGeom
import jetbrains.gis.geoprotocol.FeatureLevel
import jetbrains.gis.geoprotocol.MapRegion
import jetbrains.livemap.DevParams.Companion.DEBUG_GRID
import jetbrains.livemap.DevParams.Companion.MICRO_TASK_EXECUTOR
import jetbrains.livemap.LiveMapFactory
import jetbrains.livemap.api.*
import jetbrains.livemap.canvascontrols.LiveMapPresenter
import jetbrains.livemap.projections.ProjectionType

object LivemapDemoModel {

    val SPB = GeoObject(lon = 30.1751, lat = 59.5439)
    val MOSCOW = GeoObject(lon = 37.3659, lat = 55.4507)
    val BOSTON = GeoObject(lon = -71.0335, lat = 42.2130)
    val NEW_YORK = GeoObject(lon = -73.5939, lat = 40.4342)

    val VIEW_SIZE = Vector(800, 600)

    private fun mouseEvents(canvasControl: CanvasControl): MouseEventSource {
        return object : MouseEventSource {
            override fun addEventHandler(
                eventSpec: MouseEventSpec,
                eventHandler: EventHandler<MouseEvent>
            ): Registration {
                return canvasControl.addEventHandler(eventSpec, eventHandler)
            }
        }
    }

    fun createLivemapModel(canvasControl: CanvasControl): Registration {
        val mouse = mouseEvents(canvasControl)

        val livemapSpec = liveMapConfig {
            mouseEventSource = mouse

            tileService = tileService {
                theme = LivemapGeom.Theme.DARK
                // host = "localhost"
                // port = 3012
            }
            //tileService = { //HorisTileGen {
            //name = "HorisTileGen"
            //theme = "LivemapGeom.Theme.COLOR"
            //url = "tilegen.horis.ru:1234"
            //}

            geocodingService = dummyGeocodingService
            //geocodingService = HorisGeocoding {
            //url = "tilegen.horis.ru:4321"
            //}


            size = canvasControl.size.toDoubleVector()
            zoom = 1
            theme = LivemapGeom.Theme.COLOR
            interactive = true

            location {
                name = "boston"

                geocodingHint {
                    level = FeatureLevel.CITY
                    parent = MapRegion.withName("USA")
                }
            }

            projection {
                kind = ProjectionType.MERCATOR
                loopX = false
                loopY = false
            }

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

            params(
                DEBUG_GRID.key to true,
                MICRO_TASK_EXECUTOR.key to "ui_thread"
            )
        }

        val livemap = LiveMapFactory(livemapSpec).createLiveMap()
        val livemapPresenter = LiveMapPresenter()

        livemapPresenter.render(canvasControl, livemap)

        return Registration.from(livemapPresenter)
    }

//    private fun createCirclePointsLayer(count: Int): MapLayer {
//        return MapLayer(MapLayerKind.POINT, createCircle(count), null)
//    }

//    private fun createCenterPointLayer(): MapLayer {
//        return MapLayer(MapLayerKind.POINT, listOf(createMapPoint(0, DoubleVector.ZERO)), null)
//    }

//    private fun createPathLayer(): MapLayer {
//        return MapLayer(
//            MapLayerKind.PATH, listOf(
//                MapJsPath(
//                    0,
//                    "",
//                    emptyList(),
//                    Color.RED.changeAlpha(127),
//                    1.0,
//                    10.0,
//                    0.0,
//                    {
//                        MultiPolygon.create(
//                            Polygon.create(
//                                Ring.create(
//                                    DoubleVector(0.0, -45.0),
//                                    DoubleVector(40.0, 45.0),
//                                    DoubleVector(80.0, -45.0),
//                                    DoubleVector(120.0, 45.0),
//                                    DoubleVector(160.0, -45.0)
//                                )
//                            )
//                        )
//                    }, null,
//                    0
//                )
//            ), null
//        )
//    }

//    private fun createCircle(n: Int): List<MapObject> {
//        val r = 80.0
//        val step_angle = 3.14 * 2 / n
//        var angle = 0.0
//        val result = ArrayList<MapObject>()
//
//        for (i in 0 until n) {
//            result.add(createMapPoint(i, DoubleVector(cos(angle) * r, sin(angle) * r)))
//            angle += step_angle
//        }
//
//        return result
//    }

    // private fun createMapPoint(i: Int, center: DoubleVector): MapPoint {
    //     return MapJsPoint(i, "0", 13, Color.CONSOLE_YELLOW, Color.TRANSPARENT, 0, 50, null, center, 0)
    // }


    data class GeoObject(
        val lon: Double,
        val lat: Double
    ) {
        val geoCoord = DoubleVector(lon, lat)
    }

    fun PointBuilder.coord(geoObj: LivemapDemoModel.GeoObject) {
        lon = geoObj.lon
        lat = geoObj.lat
    }
}

fun Points.point(block: PointBuilder.() -> Unit) {
    items.add(
        PointBuilder().apply {
            animation = 0
            index = 0
            mapId = ""
            regionId = ""
            label = ""

            strokeWidth = 1.0
            strokeColor = Color.BLACK

            fillColor = Color.WHITE

            radius = 4.0
            shape = 1
        }
            .apply(block)
            .build()
    )
}

