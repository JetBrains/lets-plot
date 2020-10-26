/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.projection.Client

interface LocatorHelper {
    fun isCoordinateInTarget(coord: Vec<Client>, target: EcsEntity): Boolean
    fun getColor(target: EcsEntity): Color?
}