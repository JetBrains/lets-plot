/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.LonLatPoint
import jetbrains.datalore.base.typedGeometry.Rect
import jetbrains.datalore.base.typedGeometry.Vec

/*
 Map projection, like mercator, WSG89, Albers and so on, configured to be used by LiveMap.
 */
interface Geographic

typealias GeographicPoint = Vec<Geographic>

interface GeoProjection : UnsafeTransform<LonLatPoint, GeographicPoint> {
    fun validRect(): Rect<LonLat>
    val cylindrical: Boolean
}