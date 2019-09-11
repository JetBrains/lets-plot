package jetbrains.livemap.entities.placement

import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.projections.ClientPoint
import jetbrains.livemap.projections.Coordinates
import jetbrains.livemap.projections.WorldPoint

object Components {

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
        var screenOffset: ClientPoint = Coordinates.ZERO_CLIENT_POINT
    }
}