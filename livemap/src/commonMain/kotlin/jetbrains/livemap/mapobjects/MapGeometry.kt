/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapobjects

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.gis.geoprotocol.Boundary

interface MapGeometry {
    val geometry: Boundary<LonLat>?
}