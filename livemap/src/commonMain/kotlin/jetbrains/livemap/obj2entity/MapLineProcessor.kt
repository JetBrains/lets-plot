package jetbrains.livemap.obj2entity

import jetbrains.datalore.base.projectionGeometry.*
import jetbrains.datalore.maps.livemap.entities.geometry.Renderers.PathRenderer
import jetbrains.datalore.maps.livemap.entities.geometry.WorldGeometryComponent
import jetbrains.gis.geoprotocol.TypedGeometry
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.rendering.layers.LayerManager
import jetbrains.livemap.entities.Entities
import jetbrains.livemap.entities.geometry.WorldGeometry
import jetbrains.livemap.entities.placement.WorldDimensionComponent
import jetbrains.livemap.entities.rendering.*
import jetbrains.livemap.mapobjects.MapLine
import jetbrains.livemap.mapobjects.MapObject
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.WorldPoint
import jetbrains.livemap.projections.WorldRectangle

class MapLineProcessor(
    componentManager: EcsComponentManager,
    layerManager: LayerManager,
    private val myMapProjection: MapProjection
) {

    private val myLayerEntitiesComponent = LayerEntitiesComponent()
    private val myFactory: Entities.MapEntityFactory

    init {
        componentManager
            .createEntity("map_layer_line")
            .addComponent(layerManager.createRenderLayerComponent("geom_line"))
            .addComponent(myLayerEntitiesComponent)
            .run { myFactory = Entities.MapEntityFactory(this) }
    }

    fun process(mapObjects: List<MapObject>, horizontal: Boolean) {
        for (obj in mapObjects) {
            val lineEntity = createEntity(obj as MapLine, horizontal)
            myLayerEntitiesComponent.add(lineEntity.id)
        }
    }

    private fun createEntity(mapLine: MapLine, horizontal: Boolean): EcsEntity {
        val worldPoint = myMapProjection.project(mapLine.point)
        val line = createLineGeometry(worldPoint, horizontal)
        val bbox = createLineBBox(worldPoint, mapLine.strokeWidth, horizontal)

        return myFactory
            .createMapEntity(bbox.origin, PathRenderer(), "map_ent_line")
            .addComponent(WorldGeometryComponent().apply { geometry = line })
            .addComponent(WorldDimensionComponent(bbox.dimension))
            .addComponent(
                StyleComponent().apply {
                    setStrokeColor(mapLine.strokeColor)
                    setStrokeWidth(mapLine.strokeWidth)
                    setLineDash(mapLine.lineDash)
                }
            )
    }

    private fun createLineGeometry(point: WorldPoint, horizontal: Boolean): WorldGeometry {
        val mapRect = myMapProjection.mapRect

        return if (horizontal) {
            listOf(
                point.transform(
                    fx = { mapRect.scalarLeft }
                ),
                point.transform(
                    fx = { mapRect.scalarRight }
                )

            )
        } else {
            listOf(
                point.transform(
                    fy = { mapRect.scalarTop }
                ),
                point.transform(
                    fy = { mapRect.scalarBottom }
                )
            )
        }
            .run { listOf(Ring(this)) }
            .run { listOf(Polygon(this)) }
            .run { MultiPolygon(this) }
            .run(TypedGeometry.Companion::create) // World
    }

    private fun createLineBBox(
        point: WorldPoint,
        strokeWidth: Double,
        horizontal: Boolean
    ): WorldRectangle {
        val mapRect = myMapProjection.mapRect

        return if (horizontal) {
            WorldRectangle(
                explicitVec(mapRect.left, point.y - strokeWidth / 2),
                explicitVec(mapRect.width, strokeWidth)
            )
        } else {
            WorldRectangle(
                explicitVec(point.x - strokeWidth / 2, mapRect.top),
                explicitVec(strokeWidth, mapRect.height)
            )
        }
    }
}