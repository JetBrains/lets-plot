/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.geometry

import org.jetbrains.letsPlot.commons.intern.typedGeometry.Geometry
import org.jetbrains.letsPlot.livemap.World
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponent

class WorldGeometryComponent : EcsComponent {
    lateinit var geometry: Geometry<World>
}
