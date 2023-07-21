/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core

import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.spatial.LonLatPoint
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Rect
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec

/*
 Map projection, like mercator, WSG89, Albers and so on, configured to be used by LiveMap.
 */
interface Geographic

typealias GeographicPoint = Vec<Geographic>

interface GeoProjection : UnsafeTransform<LonLatPoint, GeographicPoint> {
    fun validRect(): Rect<LonLat>
    val cylindrical: Boolean
}