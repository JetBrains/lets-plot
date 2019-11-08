/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapobjects

import jetbrains.datalore.base.values.Color
import jetbrains.livemap.entities.geometry.LonLatBoundary

class MapPolygon(
    index: Int,
    mapId: String?,
    regionId: String?,

    val lineDash: List<Double>,
    val strokeColor: Color,
    val strokeWidth: Double,
    val fillColor: Color,
    override val geometry: LonLatBoundary?

) : MapObject(index, mapId, regionId), MapGeometry