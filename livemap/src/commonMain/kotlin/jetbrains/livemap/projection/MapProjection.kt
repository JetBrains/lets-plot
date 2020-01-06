/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.projection

import jetbrains.datalore.base.spatial.LonLatPoint
import jetbrains.livemap.core.projections.Projection

interface MapProjection : Projection<LonLatPoint, WorldPoint> {
    val mapRect: WorldRectangle
}