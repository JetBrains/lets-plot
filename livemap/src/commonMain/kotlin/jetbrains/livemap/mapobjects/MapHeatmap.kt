/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapobjects

import jetbrains.datalore.base.projectionGeometry.LonLat
import jetbrains.datalore.base.projectionGeometry.Vec

class MapHeatmap(
    index: Int,
    mapId: String?,
    regionId: String?,

    override val point: Vec<LonLat>

) : MapObject(index, mapId, regionId), MapPointGeometry