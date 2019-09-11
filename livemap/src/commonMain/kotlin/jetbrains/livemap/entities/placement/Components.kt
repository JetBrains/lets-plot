package jetbrains.livemap.entities.placement

import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.projections.ClientPoint
import jetbrains.livemap.projections.Coordinates
import jetbrains.livemap.projections.WorldPoint

object Components {

    class ScreenLoopComponent : EcsComponent {

        var origins: List<ClientPoint> = ArrayList()

        companion object {
            fun provide(entity: EcsEntity): ScreenLoopComponent {
                return entity.provide(::ScreenLoopComponent)
            }

            operator fun get(entity: EcsEntity): ScreenLoopComponent {
                return entity.getComponent()
            }

            fun getOrigins(entity: EcsEntity): List<ClientPoint> {
                return get(entity).origins
            }

            fun setOrigins(entity: EcsEntity, origins: List<ClientPoint>) {
                get(entity).origins = origins
            }
        }
    }

    class ScreenDimensionComponent : EcsComponent {
        var dimension: ClientPoint = Coordinates.ZERO_CLIENT_POINT

        companion object {
            fun getDimension(entity: EcsEntity): ClientPoint {
                return entity.get<ScreenDimensionComponent>().dimension
            }

            fun provide(entity: EcsEntity): ScreenDimensionComponent {
                return entity.provide(::ScreenDimensionComponent)
            }
        }
    }

    class WorldDimensionComponent(var dimension: WorldPoint) : EcsComponent {
        companion object {
            operator fun get(entity: EcsEntity): WorldDimensionComponent {
                return entity.getComponent()
            }

            fun getDimension(entity: EcsEntity): WorldPoint {
                return get(entity).dimension
            }
        }
    }

    class WorldOriginComponent(var origin: WorldPoint) : EcsComponent {
        companion object {
            operator fun get(entity: EcsEntity): WorldOriginComponent {
                return entity.getComponent()
            }

            fun getOrigin(entity: EcsEntity): WorldPoint {
                return get(entity).origin
            }
        }
    }

    class ScreenOriginComponent : EcsComponent {

        var origin: ClientPoint = Coordinates.ZERO_CLIENT_POINT

        companion object {
            fun provide(entity: EcsEntity): ScreenOriginComponent {
                return entity.provide(::ScreenOriginComponent)
            }

            fun getOrigin(entity: EcsEntity): ClientPoint {
                return get(entity).origin
            }

            operator fun get(entity: EcsEntity): ScreenOriginComponent {
                return entity.getComponent()
            }

            fun setOrigin(entity: EcsEntity, origin: ClientPoint) {
                get(entity).origin = origin
            }
        }
    }

    class ScreenOffsetComponent : EcsComponent {

        var screenOffset: ClientPoint = Coordinates.ZERO_CLIENT_POINT

        companion object {
            fun provide(entity: EcsEntity): ScreenOffsetComponent {
                return entity.provide(::ScreenOffsetComponent)
            }

            fun getScreenOffset(entity: EcsEntity): ClientPoint {
                return get(entity).screenOffset
            }

            operator fun get(entity: EcsEntity): ScreenOffsetComponent {
                return entity.getComponent()
            }

            fun setScreenOffset(entity: EcsEntity, offset: ClientPoint) {
                get(entity).screenOffset = offset
            }
        }
    }
}