/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.geometry

import jetbrains.datalore.base.typedGeometry.Geometry
import jetbrains.livemap.World
import jetbrains.livemap.core.ecs.EcsComponent

class WorldGeometryComponent : EcsComponent {
    var geometry: Geometry<World>? = null
}
