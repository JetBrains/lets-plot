/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.geometry

import jetbrains.datalore.base.typedGeometry.MultiPolygon
import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.projections.World

class WorldGeometryComponent : EcsComponent {
    var geometry: MultiPolygon<World>? = null
}
