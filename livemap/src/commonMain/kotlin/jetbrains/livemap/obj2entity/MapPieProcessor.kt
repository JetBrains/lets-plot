package jetbrains.livemap.obj2entity

import jetbrains.datalore.maps.livemap.entities.geometry.Renderers.PieSectorRenderer
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.rendering.layers.LayerManager
import jetbrains.livemap.entities.Entities
import jetbrains.livemap.entities.geometry.PieSectorComponent
import jetbrains.livemap.entities.placement.ScreenDimensionComponent
import jetbrains.livemap.entities.rendering.*
import jetbrains.livemap.mapobjects.MapObject
import jetbrains.livemap.mapobjects.MapPieSector
import jetbrains.livemap.projections.MapProjection

class MapPieProcessor(
    componentManager: EcsComponentManager,
    layerManager: LayerManager,
    private val myMapProjection: MapProjection
) {

    private val myObjectsMap = HashMap<MapPieSector, EcsEntity>()
    private val myLayerEntitiesComponent = LayerEntitiesComponent()
    private val myFactory: Entities.MapEntityFactory

    init {
        componentManager
            .createEntity("map_layer_pie")
            .addComponent(layerManager.createRenderLayerComponent("livemap_pie"))
            .addComponent(myLayerEntitiesComponent)
            .run { myFactory = Entities.MapEntityFactory(this) }
    }

    internal fun process(mapObjects: List<MapObject>) {
        for (mapObject in mapObjects) {
            val pieSectorEntity = createEntity(mapObject as MapPieSector)

            myObjectsMap[mapObject] = pieSectorEntity
            myLayerEntitiesComponent.add(pieSectorEntity.id)
        }
    }

    private fun createEntity(mapPieSector: MapPieSector): EcsEntity {
        return myFactory
            .createMapEntity(myMapProjection.project(mapPieSector.point), PieSectorRenderer(), "map_ent_pie_sector")
            .addComponent(
                PieSectorComponent().apply {
                    radius = mapPieSector.radius
                    startAngle = mapPieSector.startAngle
                    endAngle = mapPieSector.endAngle
                }
            )
            .addComponent(
                StyleComponent().apply {
                    setFillColor(mapPieSector.fillColor)
                    setStrokeColor(mapPieSector.strokeColor)
                    setStrokeWidth(mapPieSector.strokeWidth)
                }
            )
            .addComponent(ScreenDimensionComponent())
    }
}