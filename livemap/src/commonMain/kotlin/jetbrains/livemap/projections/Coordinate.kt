/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.projections

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.projectionGeometry.*
import jetbrains.datalore.base.spatial.LonLat

/**
 * Coordinates in [0.0.. 256.0]
 */
interface World

/**
 * Coordinates used by Context2d
 */
interface Client


typealias LonLatPoint = Vec<LonLat>
typealias LonLatRing = Ring<LonLat>
typealias LonLatPolygon = Polygon<LonLat>
typealias LonLatMultiPolygon = MultiPolygon<LonLat>

typealias ClientPoint = Vec<Client>
typealias ClientRectangle = Rect<Client>

typealias WorldPoint = Vec<World>
typealias WorldRectangle = Rect<World>


object Coordinates {
    val ZERO_LONLAT_POINT = explicitVec<LonLat>(0.0, 0.0)
    val ZERO_WORLD_POINT = explicitVec<World>(0.0, 0.0)
    val ZERO_CLIENT_POINT = explicitVec<Client>(0.0, 0.0)
}

fun newDoubleRectangle(origin: Vec<*>, dimension: Vec<*>): DoubleRectangle {
    return DoubleRectangle(origin.x, origin.y, dimension.x, dimension.y)
}

fun Vector.toClientPoint() = ClientPoint(x, y)
fun DoubleVector.toClientPoint() = explicitVec<Client>(x, y)
