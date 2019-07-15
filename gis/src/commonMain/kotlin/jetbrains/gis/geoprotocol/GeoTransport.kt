package jetbrains.gis.geoprotocol

import jetbrains.datalore.base.async.Async

interface GeoTransport {
    fun send(request: GeoRequest): Async<GeoResponse>
}
