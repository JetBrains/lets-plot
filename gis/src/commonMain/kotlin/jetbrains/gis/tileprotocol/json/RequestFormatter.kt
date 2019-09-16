package jetbrains.gis.tileprotocol.json

import jetbrains.datalore.base.projectionGeometry.bottom
import jetbrains.datalore.base.projectionGeometry.left
import jetbrains.datalore.base.projectionGeometry.right
import jetbrains.datalore.base.projectionGeometry.top
import jetbrains.gis.common.json.FluentArray
import jetbrains.gis.common.json.FluentObject
import jetbrains.gis.common.json.Obj
import jetbrains.gis.tileprotocol.Request
import jetbrains.gis.tileprotocol.Request.*
import jetbrains.gis.tileprotocol.json.RequestJsonParser.X
import jetbrains.gis.tileprotocol.json.RequestJsonParser.Y
import jetbrains.gis.tileprotocol.json.RequestJsonParser.Z
import jetbrains.gis.tileprotocol.json.RequestKeys.BBOX
import jetbrains.gis.tileprotocol.json.RequestKeys.DATA
import jetbrains.gis.tileprotocol.json.RequestKeys.KEY
import jetbrains.gis.tileprotocol.json.RequestKeys.STYLE
import jetbrains.gis.tileprotocol.json.RequestKeys.TYPE
import jetbrains.gis.tileprotocol.json.RequestKeys.ZOOM
import jetbrains.gis.tileprotocol.json.RequestTypes.CONFIGURE_CONNECTION
import jetbrains.gis.tileprotocol.json.RequestTypes.GET_BINARY_TILE


object RequestFormatter {
    fun format(request: Request): Obj = when (request) {
        is ConfigureConnectionRequest -> FluentObject()
            .put(TYPE, CONFIGURE_CONNECTION.toString())
            .put(STYLE, request.styleName)

        is GetBinaryGeometryRequest ->  FluentObject()
            .put(TYPE, GET_BINARY_TILE.toString())
            .put(KEY, request.key)
            .put(ZOOM, request.zoom)
            .put(BBOX, FluentArray()
                .add(request.bbox.left)
                .add(request.bbox.top)
                .add(request.bbox.right)
                .add(request.bbox.bottom)
            )

        is CancelBinaryTileRequest -> FluentObject()
            .put(DATA, FluentArray()
                .addAll(request.coordinates.map { coord ->
                    FluentObject()
                        .put(X, coord.x)
                        .put(Y, coord.y)
                        .put(Z, coord.z)
                })
            )
    }.get()
}
