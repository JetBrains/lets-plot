package jetbrains.gis.tileprotocol

import jetbrains.datalore.base.geometry.DoubleRectangle

interface Request {

    class ConfigureConnectionRequest(val styleName: String) : Request

    class GetBinaryGeometryRequest(val key: String, val zoom: Int, val bbox: DoubleRectangle) : Request

    class CancelBinaryTileRequest(val coordinates: Set<TileCoordinates>) : Request
}
