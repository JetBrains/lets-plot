/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.mapengine.placement

import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.livemap.Client
import org.jetbrains.letsPlot.livemap.WorldPoint
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponent

class WorldDimensionComponent(var dimension: WorldPoint) : EcsComponent
class WorldOriginComponent(var origin: WorldPoint) : EcsComponent

class ScreenDimensionComponent : EcsComponent {
    var dimension: Vec<Client> = Client.ZERO_VEC
}
