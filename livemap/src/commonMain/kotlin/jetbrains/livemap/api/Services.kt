/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import org.jetbrains.letsPlot.base.intern.async.Async
import org.jetbrains.letsPlot.base.intern.async.Asyncs
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.typedGeometry.Rect
import jetbrains.datalore.base.unsupported.UNSUPPORTED
import jetbrains.gis.geoprotocol.GeoRequest
import jetbrains.gis.geoprotocol.GeoResponse
import jetbrains.gis.geoprotocol.GeoTransport
import jetbrains.gis.geoprotocol.GeocodingService
import jetbrains.gis.tileprotocol.TileLayer
import jetbrains.gis.tileprotocol.TileService
import jetbrains.gis.tileprotocol.socket.Socket
import jetbrains.gis.tileprotocol.socket.SocketBuilder
import jetbrains.gis.tileprotocol.socket.SocketHandler

object Services {

    fun bogusGeocodingService(): GeocodingService = GeocodingService(
        object : GeoTransport {
            override fun send(request: GeoRequest): Async<GeoResponse> {
                return Asyncs.failure(RuntimeException("Geocoding is disabled."))
            }
        }
    )

    fun bogusTileProvider(): TileService {
        class DummySocketBuilder : SocketBuilder {
            override fun build(handler: SocketHandler): Socket {
                return object : Socket {
                    override fun connect(): Unit = UNSUPPORTED("DummySocketBuilder.connect")
                    override fun close(): Unit = UNSUPPORTED("DummySocketBuilder.close")
                    override fun send(msg: String): Unit = UNSUPPORTED("DummySocketBuilder.send")
                }
            }
        }

        return object : TileService(DummySocketBuilder(), Theme.COLOR) {
            override fun getTileData(bbox: Rect<LonLat>, zoom: Int): Async<List<TileLayer>> {
                return Asyncs.constant(emptyList())
            }
        }
    }

    fun devGeocodingService() = liveMapGeocoding {
        url = "http://10.0.0.127:3020/map_data/geocoding"
    }

    fun jetbrainsGeocodingService() = liveMapGeocoding {
        url = "https://geo2.datalore.jetbrains.com"
    }

    fun devTileProvider() = liveMapVectorTiles {
        url = "ws://10.0.0.127:3933"
    }

    fun jetbrainsTileProvider() = liveMapVectorTiles {
        url = "wss://tiles.datalore.jetbrains.com"
    }
}
