/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.placement

import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.projection.ClientPoint
import jetbrains.livemap.projection.Coordinates
import jetbrains.livemap.projection.WorldPoint

class WorldDimensionComponent(var dimension: WorldPoint) : EcsComponent
class WorldOriginComponent(var origin: WorldPoint) : EcsComponent

class ScreenLoopComponent : EcsComponent {
    var origins: List<ClientPoint> = ArrayList()
}

class ScreenDimensionComponent : EcsComponent {
    var dimension: ClientPoint = Coordinates.ZERO_CLIENT_POINT
}

class ScreenOriginComponent : EcsComponent {
    var origin: ClientPoint = Coordinates.ZERO_CLIENT_POINT
}

class ScreenOffsetComponent : EcsComponent {
    var offset: ClientPoint = Coordinates.ZERO_CLIENT_POINT
}