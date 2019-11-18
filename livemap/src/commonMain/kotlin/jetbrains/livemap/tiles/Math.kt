/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.tiles

import jetbrains.datalore.base.projectionGeometry.Rect
import jetbrains.datalore.base.projectionGeometry.times
import jetbrains.datalore.base.spatial.GeoUtils.getQuadOrigin
import jetbrains.datalore.base.spatial.GeoUtils.getTileCount
import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.datalore.base.spatial.calculateQuadKeys
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.ProjectionUtil

fun <TypeT> getTileRect(mapRect: Rect<TypeT>, quadKey: String): Rect<TypeT> {
    val origin = getQuadOrigin(mapRect, quadKey)
    val dimension = mapRect.dimension * (1.0 / getTileCount(quadKey.length))

    return Rect(origin, dimension)
}


fun convertCellKeyToQuadKeys(mapProjection: MapProjection, cellKey: CellKey): Set<QuadKey> {
    val cellRect = getTileRect(mapProjection.mapRect, cellKey.key)
    val geoRect = ProjectionUtil.transformBBox(cellRect, mapProjection::invert)
    return calculateQuadKeys(geoRect, cellKey.length)
}

internal fun <T> calculateCellKeys(mapRect: Rect<T>, rect: Rect<T>, zoom: Int): Set<CellKey> {
    return calculateQuadKeys(mapRect, rect, zoom, ::CellKey)
}



