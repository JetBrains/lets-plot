/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.ui

import jetbrains.datalore.base.projectionGeometry.Vec
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.livemap.core.ecs.EcsComponent

class MakeGeometryWidgetComponent: EcsComponent {
    val points: MutableList<Vec<LonLat>> = ArrayList()
}