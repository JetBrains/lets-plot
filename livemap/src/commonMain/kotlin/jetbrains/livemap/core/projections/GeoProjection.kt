/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.projections

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.LonLatPoint
import jetbrains.datalore.base.typedGeometry.Rect
import jetbrains.datalore.base.typedGeometry.Vec

interface Geographic

typealias GeographicPoint = Vec<Geographic>

interface GeoProjection : Projection<LonLatPoint, GeographicPoint> {
    fun validRect(): Rect<LonLat>
    val cylindrical: Boolean
}