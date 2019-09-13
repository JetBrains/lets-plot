package jetbrains.livemap.obj2entity

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.MultiPolygon
import jetbrains.datalore.base.projectionGeometry.Polygon
import jetbrains.datalore.base.projectionGeometry.Ring
import jetbrains.datalore.maps.livemap.entities.geometry.Renderers
import jetbrains.datalore.maps.livemap.entities.geometry.WorldGeometryComponent
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.rendering.layers.LayerManager
import jetbrains.livemap.entities.Entities
import jetbrains.livemap.entities.geometry.WorldGeometry
import jetbrains.livemap.entities.placement.Components
import jetbrains.livemap.entities.rendering.*
import jetbrains.livemap.mapobjects.MapLine
import jetbrains.livemap.mapobjects.MapObject
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.WorldPoint
import jetbrains.livemap.projections.toWorldPoint

class MapLineProcessor internal constructor(
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
            val worldPoint = myMapProjection.project(mapLine.point).toWorldPoint()
            val geometry = createLineGeometry(worldPoint, horizontal, myMapProjection.mapRect)
            val bbox = createLineBBox(worldPoint, mapLine.strokeWidth, horizontal, myMapProjection.mapRect)

            val lineEntity = myFactory
                .createMapEntity(bbox.origin.toWorldPoint(), SIMPLE_RENDERER, "map_ent_line")
                .addComponent(WorldGeometryComponent().apply { this.geometry = geometry })
                .addComponent(Components.WorldDimensionComponent(bbox.dimension.toWorldPoint()))
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

        private fun createLineGeometry(point: WorldPoint, horizontal: Boolean, mapRect: DoubleRectangle): WorldGeometry {
            return if (horizontal) {
                listOf(
                    DoubleVector(mapRect.left, point.y),
                    DoubleVector(mapRect.right, point.y)
                )
            } else {
                listOf(
                    DoubleVector(point.x, mapRect.top),
                    DoubleVector(point.x, mapRect.bottom)
                )
            }
                .run(::Ring)
                .run(::Polygon)
                .run(::MultiPolygon)
                .run(WorldGeometry.Companion::create)
        }

        private fun createLineBBox(
            point: WorldPoint,
            strokeWidth: Double,
            horizontal: Boolean,
            mapRect: DoubleRectangle
        ): DoubleRectangle {
            val origin: DoubleVector
            val dimension: DoubleVector
            if (horizontal) {
                origin = DoubleVector(mapRect.left, point.y - strokeWidth / 2)
                dimension = DoubleVector(mapRect.width, strokeWidth)
            } else {
                origin = DoubleVector(point.x - strokeWidth / 2, mapRect.top)
                dimension = DoubleVector(strokeWidth, mapRect.height)
            }
            return DoubleRectangle(origin, dimension)
        }
    }
}