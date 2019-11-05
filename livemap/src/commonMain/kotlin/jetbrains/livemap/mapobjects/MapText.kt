/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapobjects

import jetbrains.datalore.base.projectionGeometry.LonLat
import jetbrains.datalore.base.projectionGeometry.Vec
import jetbrains.datalore.base.values.Color

class MapText(
    index: Int,
    mapId: String?,
    regionId: String?,

    override val point: Vec<LonLat>,

    val fillColor: Color,
    val strokeColor: Color,
    val strokeWidth: Double,

    val label: String,
    val size: Double,
    val family: String,
    val fontface: String,
    val hjust: Double,
    val vjust: Double,
    val angle: Double
) : MapObject(index, mapId, regionId), MapPointGeometry