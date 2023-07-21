/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis.tileprotocol.json

import org.jetbrains.letsPlot.commons.intern.json.FluentArray
import org.jetbrains.letsPlot.commons.intern.json.FluentObject
import org.jetbrains.letsPlot.commons.intern.json.Obj
import org.jetbrains.letsPlot.commons.intern.typedGeometry.bottom
import org.jetbrains.letsPlot.commons.intern.typedGeometry.left
import org.jetbrains.letsPlot.commons.intern.typedGeometry.right
import org.jetbrains.letsPlot.commons.intern.typedGeometry.top
import org.jetbrains.letsPlot.gis.tileprotocol.Request
import org.jetbrains.letsPlot.gis.tileprotocol.Request.*
import org.jetbrains.letsPlot.gis.tileprotocol.json.RequestJsonParser.X
import org.jetbrains.letsPlot.gis.tileprotocol.json.RequestJsonParser.Y
import org.jetbrains.letsPlot.gis.tileprotocol.json.RequestJsonParser.Z
import org.jetbrains.letsPlot.gis.tileprotocol.json.RequestKeys.BBOX
import org.jetbrains.letsPlot.gis.tileprotocol.json.RequestKeys.DATA
import org.jetbrains.letsPlot.gis.tileprotocol.json.RequestKeys.KEY
import org.jetbrains.letsPlot.gis.tileprotocol.json.RequestKeys.STYLE
import org.jetbrains.letsPlot.gis.tileprotocol.json.RequestKeys.TYPE
import org.jetbrains.letsPlot.gis.tileprotocol.json.RequestKeys.ZOOM
import org.jetbrains.letsPlot.gis.tileprotocol.json.RequestTypes.CONFIGURE_CONNECTION
import org.jetbrains.letsPlot.gis.tileprotocol.json.RequestTypes.GET_BINARY_TILE


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
