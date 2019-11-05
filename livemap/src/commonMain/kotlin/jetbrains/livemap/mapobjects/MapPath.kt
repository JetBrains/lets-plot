/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapobjects

import jetbrains.datalore.base.values.Color
import jetbrains.livemap.entities.geometry.LonLatGeometry

class MapPath(
    index: Int,
    mapId: String?,
    regionId: String?,

    override val geometry: LonLatGeometry,

    val animation: Int,
    val speed: Double,

    val flow: Double,
    val lineDash: List<Double>,
    val strokeColor: Color,
    val strokeWidth: Double
    //val arrowSpec: ArrowSpec,

) : MapObject(index, mapId, regionId), MapGeometry
