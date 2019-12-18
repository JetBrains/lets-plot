/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.projections

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.typedGeometry.Rect
import jetbrains.datalore.base.typedGeometry.Vec

interface Geographic

typealias GeographicPoint = Vec<Geographic>

internal interface GeoProjection : Projection<LonLatPoint, GeographicPoint> {
    fun validRect(): Rect<LonLat>
}