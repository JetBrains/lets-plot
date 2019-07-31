package jetbrains.gis.tileprotocol.json

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.gis.common.json.FluentArray
import jetbrains.gis.common.json.FluentObject
import jetbrains.gis.common.json.Obj
import jetbrains.gis.common.json.parseEnum
import jetbrains.gis.tileprotocol.Request
import jetbrains.gis.tileprotocol.Request.*
import jetbrains.gis.tileprotocol.TileCoordinates
import jetbrains.gis.tileprotocol.json.RequestKeys.BBOX
import jetbrains.gis.tileprotocol.json.RequestKeys.DATA
import jetbrains.gis.tileprotocol.json.RequestKeys.KEY
import jetbrains.gis.tileprotocol.json.RequestKeys.STYLE
import jetbrains.gis.tileprotocol.json.RequestKeys.TYPE
import jetbrains.gis.tileprotocol.json.RequestKeys.ZOOM


object RequestJsonParser {
    internal val X = "x"
    internal val Y = "y"
    internal val Z = "z"

    fun parse(request: Obj): Request {
        FluentObject(request).apply {
            return when (parseEnum<RequestTypes>(getString(TYPE))) {
                RequestTypes.CONFIGURE_CONNECTION -> ConfigureConnectionRequest(getString(STYLE))

                RequestTypes.GET_BINARY_TILE -> GetBinaryGeometryRequest(
                    getString(KEY),
                    getInt(ZOOM),
                    parseBBox(getArray(BBOX))
                )

                RequestTypes.CANCEL_BINARY_TILE -> CancelBinaryTileRequest(
                    getArray(DATA)
                        .fluentObjectStream()
                        .map(this@RequestJsonParser::parseTileCoordinates)
                        .toSet()
                )
            }
        }
    }

    private fun parseBBox(bbox: FluentArray): DoubleRectangle {
        return DoubleRectangle.span(
            DoubleVector(bbox.getDouble(0), bbox.getDouble(1)),
            DoubleVector(bbox.getDouble(2), bbox.getDouble(3))
        )
    }

    private fun parseTileCoordinates(jsonCoordinates: FluentObject): TileCoordinates {
        return TileCoordinates(
            jsonCoordinates.getInt(X),
            jsonCoordinates.getInt(Y),
            jsonCoordinates.getInt(Z)
        )
    }
}
