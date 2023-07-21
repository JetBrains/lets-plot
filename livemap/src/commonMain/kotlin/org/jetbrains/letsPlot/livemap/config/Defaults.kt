/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.config

import org.jetbrains.letsPlot.commons.intern.spatial.GeoRectangle
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Rect
import org.jetbrains.letsPlot.livemap.World
import org.jetbrains.letsPlot.livemap.core.GeoProjection

const val MIN_ZOOM = 1
const val MAX_ZOOM = 15
const val TILE_PIXEL_SIZE = 256.0
val WORLD_RECTANGLE = Rect.XYWH<org.jetbrains.letsPlot.livemap.World>(0.0, 0.0, TILE_PIXEL_SIZE, TILE_PIXEL_SIZE)
val DEFAULT_LOCATION = GeoRectangle(-124.76, 25.52, -66.94, 49.39)

fun createMapProjection(geoProjection: GeoProjection) =
    MapProjectionBuilder(geoProjection, WORLD_RECTANGLE).apply {
        reverseY = true
    }.create()

