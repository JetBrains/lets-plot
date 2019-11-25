/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.tileprotocol.json

import jetbrains.datalore.base.projectionGeometry.Rect
import jetbrains.datalore.base.projectionGeometry.explicitVec
import jetbrains.datalore.base.projectionGeometry.newSpanRectangle
import jetbrains.datalore.base.spatial.LonLat
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

    private fun parseBBox(bbox: FluentArray): Rect<LonLat> {
        return newSpanRectangle(
            explicitVec<LonLat>(bbox.getDouble(0), bbox.getDouble(1)),
            explicitVec<LonLat>(bbox.getDouble(2), bbox.getDouble(3))
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
