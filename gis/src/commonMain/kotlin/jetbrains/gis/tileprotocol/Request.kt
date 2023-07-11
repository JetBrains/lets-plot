/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.tileprotocol

import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Rect

sealed class Request {

    class ConfigureConnectionRequest(val styleName: String) : Request()
    class GetBinaryGeometryRequest(val key: String, val zoom: Int, val bbox: Rect<LonLat>) : Request()
    class CancelBinaryTileRequest(val coordinates: Set<TileCoordinates>) : Request()
}
