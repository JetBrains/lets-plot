/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.mapengine.placement

import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.livemap.Client
import org.jetbrains.letsPlot.livemap.WorldPoint
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponent

class WorldDimensionComponent(var dimension: org.jetbrains.letsPlot.livemap.WorldPoint) : EcsComponent
class WorldOriginComponent(var origin: org.jetbrains.letsPlot.livemap.WorldPoint) : EcsComponent

class ScreenDimensionComponent : EcsComponent {
    var dimension: Vec<org.jetbrains.letsPlot.livemap.Client> = org.jetbrains.letsPlot.livemap.Client.ZERO_VEC
}
