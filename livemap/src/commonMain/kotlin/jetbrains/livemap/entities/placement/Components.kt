package jetbrains.livemap.entities.placement

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.ecs.EcsEntity

object Components {

    class ScreenLoopComponent : EcsComponent {

        var origins: List<DoubleVector> = ArrayList()

        companion object {
            fun provide(entity: EcsEntity): ScreenLoopComponent {
                return entity.provide(::ScreenLoopComponent)
            }

            operator fun get(entity: EcsEntity): ScreenLoopComponent {
                return entity.getComponent()
            }

            fun getOrigins(entity: EcsEntity): List<DoubleVector> {
                return get(entity).origins
            }

            fun setOrigins(entity: EcsEntity, origins: List<DoubleVector>) {
                get(entity).origins = origins
            }
        }
    }

    class ScreenDimensionComponent : EcsComponent {
        var dimension: DoubleVector = DoubleVector.ZERO

        companion object {
            fun getDimension(entity: EcsEntity): DoubleVector {
                return entity.get<ScreenDimensionComponent>().dimension
            }

            fun provide(entity: EcsEntity): ScreenDimensionComponent {
                return entity.provide(::ScreenDimensionComponent)
            }
        }
    }

    class WorldDimensionComponent(var dimension: DoubleVector) : EcsComponent {
        companion object {
            operator fun get(entity: EcsEntity): WorldDimensionComponent {
                return entity.getComponent()
            }

            fun getDimension(entity: EcsEntity): DoubleVector {
                return get(entity).dimension
            }
        }
    }

    class WorldOriginComponent(var origin: DoubleVector) : EcsComponent {
        companion object {
            operator fun get(entity: EcsEntity): WorldOriginComponent {
                return entity.getComponent()
            }

            fun getOrigin(entity: EcsEntity): DoubleVector {
                return get(entity).origin
            }
        }
    }

    class ScreenOriginComponent : EcsComponent {

        var origin: DoubleVector = DoubleVector.ZERO

        companion object {
            fun provide(entity: EcsEntity): ScreenOriginComponent {
                return entity.provide(::ScreenOriginComponent)
            }

            fun getOrigin(entity: EcsEntity): DoubleVector {
                return get(entity).origin
            }

            operator fun get(entity: EcsEntity): ScreenOriginComponent {
                return entity.getComponent()
            }

            fun setOrigin(entity: EcsEntity, origin: DoubleVector) {
                get(entity).origin = origin
            }
        }
    }

    class ScreenOffsetComponent : EcsComponent {

        var screenOffset: DoubleVector = DoubleVector.ZERO

        companion object {
            fun provide(entity: EcsEntity): ScreenOffsetComponent {
                return entity.provide(::ScreenOffsetComponent)
            }

            fun getScreenOffset(entity: EcsEntity): DoubleVector {
                return get(entity).screenOffset
            }

            operator fun get(entity: EcsEntity): ScreenOffsetComponent {
                return entity.getComponent()
            }

            fun setScreenOffset(entity: EcsEntity, offset: DoubleVector) {
                get(entity).screenOffset = offset
            }
        }
    }
}