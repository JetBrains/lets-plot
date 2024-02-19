/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.mapengine

import org.jetbrains.letsPlot.commons.intern.spatial.LonLatPoint
import org.jetbrains.letsPlot.livemap.WorldPoint
import org.jetbrains.letsPlot.livemap.WorldRectangle
import org.jetbrains.letsPlot.livemap.core.UnsafeTransform

interface MapProjection : UnsafeTransform<LonLatPoint, WorldPoint> {
    val mapRect: WorldRectangle
}