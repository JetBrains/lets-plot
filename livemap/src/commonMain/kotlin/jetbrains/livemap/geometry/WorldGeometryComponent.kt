/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.geometry

import org.jetbrains.letsPlot.commons.intern.typedGeometry.Geometry
import jetbrains.livemap.World
import jetbrains.livemap.core.ecs.EcsComponent

class WorldGeometryComponent : EcsComponent {
    lateinit var geometry: Geometry<World>
}
