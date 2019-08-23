package jetbrains.livemap.demo

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.Asyncs
import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSource
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.projectionGeometry.GeoRectangle
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.canvas.CanvasControl
import jetbrains.datalore.visualization.plot.base.geom.LivemapGeom
import jetbrains.gis.geoprotocol.GeoRequest
import jetbrains.gis.geoprotocol.GeoResponse
import jetbrains.gis.geoprotocol.GeoTransport
import jetbrains.gis.geoprotocol.GeocodingService
import jetbrains.gis.tileprotocol.TileLayer
import jetbrains.gis.tileprotocol.TileService
import jetbrains.gis.tileprotocol.socket.Socket
import jetbrains.gis.tileprotocol.socket.SocketBuilder
import jetbrains.gis.tileprotocol.socket.SocketHandler
import jetbrains.livemap.DevParams
import jetbrains.livemap.LiveMapFactory
import jetbrains.livemap.LiveMapSpec
import jetbrains.livemap.MapLocation
import jetbrains.livemap.canvascontrols.LiveMapPresenter
import jetbrains.livemap.projections.ProjectionType

object LivemapDemoModel {
    val VIEW_SIZE = Vector(800, 600)

    fun createLivemapModel(canvasControl: CanvasControl): Registration {
        val livemapSpec = LiveMapSpec(
            GeocodingService(
                object : GeoTransport {
                    override fun send(request: GeoRequest): Async<GeoResponse> {
                        TODO("not implemented")
                    }
                }
            ) ,
            object : TileService(DummySocketBuilder(), LivemapGeom.Theme.COLOR.name) {
                override fun getTileData(bbox: DoubleRectangle, zoom: Int): Async<List<TileLayer>> {
                    return Asyncs.constant(emptyList())
                }
            },
            DoubleVector(canvasControl.size.x.toDouble(), canvasControl.size.y.toDouble()),
            false,
            true,
            false,
            false,
            true,
            true,
            false,
            LivemapGeom.Theme.COLOR,
            ProjectionType.MERCATOR,
            MapLocation.create(
                GeoRectangle(-90.0, -45.0, 90.0, 45.0)),
            1,
            null,
            null,
            emptyList(),
            object : MouseEventSource {
                override fun addEventHandler(
                    eventSpec: MouseEventSpec,
                    eventHandler: EventHandler<MouseEvent>
                ): Registration {
                    return canvasControl.addEventHandler(eventSpec, eventHandler)
                }

            },
            isLoopX = true,
            isLoopY = false,
            mapLocationConsumer = { Unit },
            devParams = DevParams(mapOf(Pair("debug_grid", true)))
        )

        val livemapFactory = LiveMapFactory(livemapSpec)
        val livemapPresenter = LiveMapPresenter()

        livemapPresenter.render(
            canvasControl,
            livemapFactory.createLiveMap()
        )

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

    internal class DummySocketBuilder : SocketBuilder {
        override fun build(handler: SocketHandler): Socket {
            return object : Socket {
                override fun connect() {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun close() {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun send(msg: String) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            }
        }

    }
}