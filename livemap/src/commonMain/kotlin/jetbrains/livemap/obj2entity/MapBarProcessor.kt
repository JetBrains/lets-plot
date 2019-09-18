package jetbrains.livemap.obj2entity

import jetbrains.datalore.maps.livemap.entities.geometry.Renderers.BarRenderer
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.rendering.layers.LayerManager
import jetbrains.livemap.entities.Entities
import jetbrains.livemap.entities.placement.ScreenDimensionComponent
import jetbrains.livemap.entities.placement.ScreenOffsetComponent
import jetbrains.livemap.entities.rendering.*
import jetbrains.livemap.mapobjects.MapBar
import jetbrains.livemap.mapobjects.MapObject
import jetbrains.livemap.projections.MapProjection

class MapBarProcessor(
    componentManager: EcsComponentManager,
    layerManager: LayerManager,
    private val myMapProjection: MapProjection
) {

    private val myObjectsMap = HashMap<MapBar, EcsEntity>()
    private val myLayerEntitiesComponent = LayerEntitiesComponent()
    private val myFactory: Entities.MapEntityFactory

    init {
        val layerEntity = componentManager
            .createEntity("map_layer_bar")
            .addComponent(layerManager.createRenderLayerComponent("livemap_bar"))
            .addComponent(myLayerEntitiesComponent)
        myFactory = Entities.MapEntityFactory(layerEntity)
    }

    internal fun process(mapObjects: List<MapObject>) {
        createEntities(mapObjects)
    }

    private fun createEntities(mapObjects: List<MapObject>) {
        for (mapObject in mapObjects) {
            val mapBar = mapObject as MapBar

            val barEntity = myFactory
                .createMapEntity(myMapProjection.project(mapBar.point), BarRenderer(), "map_ent_bar")
                .addComponent(
                    ScreenOffsetComponent().apply { offset = mapBar.offset}
                )
                .addComponent(
                    ScreenDimensionComponent().apply { dimension = mapBar.dimension }
                )
                .addComponent(
                    StyleComponent().apply {
                        setFillColor(mapBar.fillColor)
                        setStrokeColor(mapBar.strokeColor)
                        setStrokeWidth(mapBar.strokeWidth)
                    }
                )

            myObjectsMap[mapBar] = barEntity
            myLayerEntitiesComponent.add(barEntity.id)
        }
    }
}