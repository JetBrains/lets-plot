/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.tiles

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.projectionGeometry.Rect
import jetbrains.datalore.base.projectionGeometry.times
import jetbrains.datalore.base.spatial.GeoUtils
import jetbrains.datalore.base.spatial.GeoUtils.getTileCount
import jetbrains.datalore.base.spatial.GeoUtils.getTileOrigin
import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.ProjectionUtil

fun <TypeT> getTileRect(mapRect: Rect<TypeT>, tileKey: String): Rect<TypeT> {
    val origin = getTileOrigin(mapRect, tileKey)
    val dimension = mapRect.dimension * (1.0 / getTileCount(tileKey.length))

    return Rect(origin, dimension)
}


fun convertCellKeyToQuadKeys(mapProjection: MapProjection, cellKey: CellKey): Set<QuadKey> {
    val cellRect = getTileRect(mapProjection.mapRect, cellKey.key)
    val geoRect = ProjectionUtil.transformBBox(cellRect, mapProjection::invert)
    return GeoUtils.calculateQuadKeys(geoRect, cellKey.length)
}

internal fun calculateCellKeys(mapRect: Rect<*>, rect: DoubleRectangle, zoom: Int): Set<CellKey> {
    return ProjectionUtil.calculateTileKeys(mapRect, rect, zoom, ::CellKey)
}
