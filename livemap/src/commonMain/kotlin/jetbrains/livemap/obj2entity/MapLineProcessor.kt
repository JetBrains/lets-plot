package jetbrains.livemap.obj2entity

import jetbrains.datalore.base.projectionGeometry.*
import jetbrains.datalore.maps.livemap.entities.geometry.Renderers
import jetbrains.datalore.maps.livemap.entities.geometry.WorldGeometryComponent
import jetbrains.gis.geoprotocol.TypedGeometry
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.rendering.layers.LayerManager
import jetbrains.livemap.entities.Entities
import jetbrains.livemap.entities.geometry.WorldGeometry
import jetbrains.livemap.entities.placement.WorldDimensionComponent
import jetbrains.livemap.entities.rendering.*
import jetbrains.livemap.mapobjects.MapLine
import jetbrains.livemap.mapobjects.MapObject
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.World
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
        val layerEntity = componentManager
            .createEntity("map_layer_line")
            .addComponent(layerManager.createRenderLayerComponent("geom_line"))
            .addComponent(myLayerEntitiesComponent)
        myFactory = Entities.MapEntityFactory(layerEntity)
    }

    internal fun process(mapObjects: List<MapObject>, horizontal: Boolean) {
        createEntities(mapObjects, horizontal)
    }

    private fun createEntities(mapObjects: List<MapObject>, horizontal: Boolean) {
        for (obj in mapObjects) {
            val mapLine = obj as MapLine
            val worldPoint = myMapProjection.project(mapLine.point)
            val geometry = createLineGeometry(worldPoint, horizontal, myMapProjection.mapRect)
            val bbox = createLineBBox(worldPoint, mapLine.strokeWidth, horizontal, myMapProjection.mapRect)

            val lineEntity = myFactory
                .createMapEntity(bbox.origin, SIMPLE_RENDERER, "map_ent_line")
                .addComponent(WorldGeometryComponent().apply { this.geometry = geometry })
                .addComponent(WorldDimensionComponent(bbox.dimension))
                .addComponent(
                    StyleComponent().apply {
                        setStrokeColor(mapLine.strokeColor)
                        setStrokeWidth(mapLine.strokeWidth)
                        setLineDash(mapLine.lineDash)
                    }
                )

            myLayerEntitiesComponent.add(lineEntity.id)
        }
    }

    companion object {
        private val SIMPLE_RENDERER = Renderers.PathRenderer()

        private fun createLineGeometry(point: WorldPoint, horizontal: Boolean, mapRect: WorldRectangle): WorldGeometry {
            return if (horizontal) {
                listOf(
                    point.transform(
                        fx = { mapRect.scalarLeft }
                    ),
                    point.transform(
                        fx = { mapRect.scalarRight / 2.0 }
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
            horizontal: Boolean,
            mapRect: WorldRectangle
        ): WorldRectangle {
            val origin: Vec<World>
            val dimension: Vec<World>
            if (horizontal) {
                origin = explicitVec<World>(mapRect.left, point.y - strokeWidth / 2)
                dimension = explicitVec<World>(mapRect.width, strokeWidth)
            } else {
                origin = explicitVec<World>(point.x - strokeWidth / 2, mapRect.top)
                dimension = explicitVec<World>(strokeWidth, mapRect.height)
            }
            return WorldRectangle(origin, dimension)
        }
    }
}