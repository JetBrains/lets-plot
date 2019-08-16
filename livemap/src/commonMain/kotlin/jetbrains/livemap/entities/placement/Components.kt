package jetbrains.livemap.entities.placement

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.ecs.EcsEntity

object Components {

    class ScreenLoopComponent : EcsComponent {

        private var myOrigins: List<DoubleVector> = ArrayList<DoubleVector>()

        fun getOrigins(): List<DoubleVector> {
            return myOrigins
        }

        fun setOrigins(origins: List<DoubleVector>): ScreenLoopComponent {
            myOrigins = origins
            return this
        }

        companion object {
            fun provide(entity: EcsEntity): ScreenLoopComponent {
                return entity.provideComponent(::ScreenLoopComponent)
            }

            operator fun get(entity: EcsEntity): ScreenLoopComponent {
                return entity.getComponent()
            }

            fun getOrigins(entity: EcsEntity): List<DoubleVector> {
                return get(entity).getOrigins()
            }

            fun setOrigins(entity: EcsEntity, origins: List<DoubleVector>) {
                get(entity).setOrigins(origins)
            }
        }
    }

    class ScreenDimensionComponent : EcsComponent {

        private var myDimension: DoubleVector? = null

        fun getDimension(): DoubleVector? {
            return myDimension
        }

        fun setDimension(dimension: DoubleVector): ScreenDimensionComponent {
            myDimension = dimension
            return this
        }

        companion object {
            fun getDimension(entity: EcsEntity): DoubleVector? {
                return get(entity).getDimension()
            }

            private operator fun get(entity: EcsEntity): ScreenDimensionComponent {
                return entity.getComponent()
            }

            fun provide(entity: EcsEntity): ScreenDimensionComponent {
                return entity.provideComponent(::ScreenDimensionComponent)
            }
        }
    }

    class WorldDimensionComponent : EcsComponent {

        private var myDimension: DoubleVector? = null

        fun getDimension(): DoubleVector? {
            return myDimension
        }

        fun setDimension(dimension: DoubleVector): WorldDimensionComponent {
            myDimension = dimension
            return this
        }

        companion object {
            operator fun get(entity: EcsEntity): WorldDimensionComponent {
                return entity.getComponent()
            }

            fun getDimension(entity: EcsEntity): DoubleVector? {
                return get(entity).getDimension()
            }
        }
    }

    class WorldOriginComponent : EcsComponent {

        private var myOrigin: DoubleVector? = null

        fun getOrigin(): DoubleVector? {
            return myOrigin
        }

        fun setOrigin(origin: DoubleVector): WorldOriginComponent {
            myOrigin = origin
            return this
        }

        companion object {
            operator fun get(entity: EcsEntity): WorldOriginComponent {
                return entity.getComponent()
            }

            fun getOrigin(entity: EcsEntity): DoubleVector? {
                return get(entity).getOrigin()
            }
        }
    }

    class ScreenOriginComponent : EcsComponent {

        private var myOrigin: DoubleVector? = null

        fun getOrigin(): DoubleVector? {
            return myOrigin
        }

        fun setOrigin(origin: DoubleVector): ScreenOriginComponent {
            myOrigin = origin
            return this
        }

        companion object {
            fun provide(entity: EcsEntity): ScreenOriginComponent {
                return entity.provideComponent(::ScreenOriginComponent)
            }

            fun getOrigin(entity: EcsEntity): DoubleVector? {
                return get(entity).getOrigin()
            }

            operator fun get(entity: EcsEntity): ScreenOriginComponent {
                return entity.getComponent()
            }

            fun setOrigin(entity: EcsEntity, origin: DoubleVector) {
                get(entity).setOrigin(origin)
            }
        }
    }

    class ScreenOffsetComponent : EcsComponent {

        private var myScreenOffset: DoubleVector? = null

        fun getScreenOffset(): DoubleVector? {
            return myScreenOffset
        }

        fun setScreenOffset(screenOffset: DoubleVector): ScreenOffsetComponent {
            myScreenOffset = screenOffset
            return this
        }

        companion object {
            fun provide(entity: EcsEntity): ScreenOffsetComponent {
                return entity.provideComponent(::ScreenOffsetComponent)
            }

            fun getScreenOffset(entity: EcsEntity): DoubleVector? {
                return get(entity).getScreenOffset()
            }

            operator fun get(entity: EcsEntity): ScreenOffsetComponent {
                return entity.getComponent()
            }

            fun setScreenOffset(entity: EcsEntity, origin: DoubleVector) {
                get(entity).setScreenOffset(origin)
            }
        }
    }
}