/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.geometry

import jetbrains.datalore.base.projectionGeometry.LonLat
import jetbrains.gis.geoprotocol.Boundary
import jetbrains.livemap.projections.Client
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.ProjectionUtil
import jetbrains.livemap.projections.World

typealias LonLatBoundary = Boundary<LonLat>

typealias WorldBoundary = Boundary<World>

typealias ClientBoundary = Boundary<Client>

fun LonLatBoundary.toWorldBoundary(mapProjection: MapProjection): WorldBoundary {
    return asMultipolygon()
        .run { ProjectionUtil.transformMultiPolygon(this, mapProjection::project) }
        .run { WorldBoundary.create(this) }
}