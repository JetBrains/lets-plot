package jetbrains.gis.tileprotocol

import jetbrains.datalore.base.projectionGeometry.LonLat
import jetbrains.datalore.base.projectionGeometry.Rect

sealed class Request {

    class ConfigureConnectionRequest(val styleName: String) : Request()
    class GetBinaryGeometryRequest(val key: String, val zoom: Int, val bbox: Rect<LonLat>) : Request()
    class CancelBinaryTileRequest(val coordinates: Set<TileCoordinates>) : Request()
}
