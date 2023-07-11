/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapengine.placement

import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import jetbrains.livemap.Client
import jetbrains.livemap.WorldPoint
import jetbrains.livemap.core.ecs.EcsComponent

class WorldDimensionComponent(var dimension: WorldPoint) : EcsComponent
class WorldOriginComponent(var origin: WorldPoint) : EcsComponent

class ScreenDimensionComponent : EcsComponent {
    var dimension: Vec<Client> = Client.ZERO_VEC
}
