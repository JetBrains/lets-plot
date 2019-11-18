/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapobjects

import jetbrains.datalore.base.projectionGeometry.Vec
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.values.Color

class MapPoint(
    index: Int,
    mapId: String?,
    regionId: String?,

    override var point: Vec<LonLat>,

    val label: String,
    val animation: Int,

    val shape: Int,
    val radius: Double,
    val fillColor: Color,
    val strokeColor: Color,
    val strokeWidth: Double
) : MapObject(index, mapId, regionId), MapPointGeometry
