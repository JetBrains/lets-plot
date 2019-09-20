package jetbrains.livemap.obj2entity

import jetbrains.datalore.base.projectionGeometry.explicitVec
import jetbrains.datalore.maps.livemap.entities.point.PointComponent
import jetbrains.datalore.maps.livemap.entities.point.PointRenderer
import jetbrains.livemap.DevParams
import jetbrains.livemap.DevParams.Companion.POINT_SCALING
import jetbrains.livemap.core.animation.Animation
import jetbrains.livemap.core.animation.Animations.AnimationBuilder
import jetbrains.livemap.core.animation.Animations.DoubleAnimator
import jetbrains.livemap.core.ecs.AnimationObjectComponent
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.rendering.TransformComponent
import jetbrains.livemap.core.rendering.layers.DirtyRenderLayerComponent
import jetbrains.livemap.core.rendering.layers.LayerManager
import jetbrains.livemap.entities.Entities.MapEntityFactory
import jetbrains.livemap.entities.placement.ScreenDimensionComponent
import jetbrains.livemap.entities.placement.WorldDimensionComponent
import jetbrains.livemap.entities.rendering.LayerEntitiesComponent
import jetbrains.livemap.entities.rendering.StyleComponent
import jetbrains.livemap.entities.rendering.setFillColor
import jetbrains.livemap.entities.rendering.setStrokeColor
import jetbrains.livemap.mapobjects.MapObject
import jetbrains.livemap.mapobjects.MapPoint
import jetbrains.livemap.projections.Client
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.World


internal class MapPointProcessor(
    private val myComponentManager: EcsComponentManager,
    layerManager: LayerManager,
    private val myDevParams: DevParams,
    private val myMapProjection: MapProjection
) {
    private val myObjectsMap = HashMap<MapPoint, EcsEntity>()
    private val myLayerEntity: EcsEntity
    private val myLayerEntitiesComponent = LayerEntitiesComponent()
    private val myFactory: MapEntityFactory

    init {
        myLayerEntity = myComponentManager
            .createEntity("map_layer_point")
            .addComponent(layerManager.createRenderLayerComponent("geom_point"))
            .addComponent(myLayerEntitiesComponent)
        myFactory = MapEntityFactory(myLayerEntity)
    }

    fun process(mapObjects: List<MapObject>) {
        for (mapObject in mapObjects) {
            val pointEntity = createEntity(mapObject as MapPoint)

            myObjectsMap[mapObject] = pointEntity
            myLayerEntitiesComponent.add(pointEntity.id)
        }

        processAnimation()
    }

    private fun createEntity(mapPoint: MapPoint): EcsEntity {
        val size = mapPoint.radius * 2.0

        return myFactory
            .createMapEntity(myMapProjection.project(mapPoint.point), PointRenderer(), "map_ent_point")
            .addComponent(PointComponent().apply { shape = mapPoint.shape })
            .addComponent(createStyle(mapPoint))
            .addComponent(
                if (myDevParams.isSet(POINT_SCALING)) {
                    WorldDimensionComponent(explicitVec<World>(size, size))
                } else {
                    ScreenDimensionComponent().apply {
                        dimension = explicitVec<Client>(size, size)
                    }
                }
            )
    }

    private fun processAnimation() {
        val animation = AnimationBuilder(500.0)
                .setDirection(Animation.Direction.FORWARD)
                .setLoop(Animation.Loop.SWITCH_DIRECTION)

        for ((point, entity) in myObjectsMap.entries) {
            if (point.animation == 2) {
                val transformComponent = TransformComponent()
                val scaleAnimator = DoubleAnimator(0.0,1.0) {
                    transformComponent.scale = it
                    myLayerEntity.tag(::DirtyRenderLayerComponent)
                }

                animation.addAnimator(scaleAnimator)
                entity.addComponent(transformComponent)
            }
        }

        myComponentManager
            .createEntity("map_ent_point_animation")
            .setComponent(
                AnimationObjectComponent(animation.build()))
    }

    private fun createStyle(mapPoint: MapPoint): StyleComponent {
        return when(mapPoint.shape) {
            in 1..14 -> StyleComponent().apply { setStrokeColor(mapPoint.strokeColor); strokeWidth = mapPoint.strokeWidth }
            in 15..18, 20 -> StyleComponent().apply { setFillColor(mapPoint.strokeColor); strokeWidth = Double.NaN }
            19 -> StyleComponent().apply { setFillColor(mapPoint.strokeColor); setStrokeColor(mapPoint.strokeColor); strokeWidth = mapPoint.strokeWidth }
            in 21..25 -> StyleComponent().apply { setFillColor(mapPoint.fillColor); setStrokeColor(mapPoint.strokeColor); strokeWidth = mapPoint.strokeWidth }
            else -> error("Not supported shape: ${mapPoint.shape}")
        }
    }
}
