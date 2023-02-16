/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapengine

import jetbrains.datalore.base.spatial.LonLatPoint
import jetbrains.livemap.WorldPoint
import jetbrains.livemap.WorldRectangle
import jetbrains.livemap.core.UnsafeTransform

interface MapProjection : UnsafeTransform<LonLatPoint, WorldPoint> {
    val mapRect: WorldRectangle
}