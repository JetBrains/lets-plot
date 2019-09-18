package jetbrains.livemap.obj2entity


import jetbrains.datalore.maps.cell.mapobjects.MapPath
import jetbrains.datalore.maps.livemap.entities.geometry.Renderers
import jetbrains.datalore.maps.livemap.entities.geometry.WorldGeometryComponent
import jetbrains.gis.geoprotocol.GeometryUtil
import jetbrains.livemap.core.animation.Animation
import jetbrains.livemap.core.animation.Animations
import jetbrains.livemap.core.ecs.AnimationComponent
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.rendering.layers.LayerManager
import jetbrains.livemap.effects.GrowingPath
import jetbrains.livemap.entities.Entities
import jetbrains.livemap.entities.geometry.LonLatGeometry
import jetbrains.livemap.entities.geometry.WorldGeometry
import jetbrains.livemap.entities.placement.WorldDimensionComponent
import jetbrains.livemap.entities.rendering.LayerEntitiesComponent
import jetbrains.livemap.entities.rendering.RendererComponent
import jetbrains.livemap.entities.rendering.StyleComponent
import jetbrains.livemap.entities.rendering.setStrokeColor
import jetbrains.livemap.mapobjects.MapObject
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.ProjectionUtil.transformMultipolygon
import kotlin.collections.Map.Entry


internal class MapPathProcessor(
    private val myComponentManager: EcsComponentManager,
    layerManager: LayerManager,
    private val myMapProjection: MapProjection
) {
    private val myLayerEntitiesComponent = LayerEntitiesComponent()
    private val myObjectsMap = HashMap<MapPath, EcsEntity>()
    private val myFactory: Entities.MapEntityFactory
    private val toMapProjection: (LonLatGeometry) -> WorldGeometry

    init {

        val layerEntity = myComponentManager
            .createEntity("map_layer_path")
            .addComponent(layerManager.createRenderLayerComponent("geom_path"))
            .addComponent(myLayerEntitiesComponent)
        myFactory = Entities.MapEntityFactory(layerEntity)
        toMapProjection = { geometry ->
            geometry.asMultipolygon()
                .run { transformMultipolygon(this, myMapProjection::project) }
                .run { WorldGeometry.create(this) }
        }
    }

    fun process(mapObjects: List<MapObject>) {
        createEntities(mapObjects)
        processAnimation()
    }

    private fun createEntities(mapObjects: List<MapObject>) {
        for (obj in mapObjects) {
            val mapPath = obj as MapPath

            val coordinates = toMapProjection(mapPath.geometry)

            GeometryUtil.bbox(coordinates.asMultipolygon())?.let { bbox ->
                val pathEntity = myFactory
                    .createMapEntity(bbox.origin, SIMPLE_RENDERER, "map_ent_path")
                    .addComponent(WorldGeometryComponent().apply {
                        geometry = coordinates
                    })
                    //.addComponent(ScaleComponent())
                    .addComponent(WorldDimensionComponent(bbox.dimension))
                    .addComponent(
                        StyleComponent().apply {
                            setStrokeColor(mapPath.strokeColor)
                            strokeWidth = mapPath.strokeWidth
                            lineDash = mapPath.lineDash.toDoubleArray()
                        }
                    )

                myObjectsMap[mapPath] = pathEntity
                myLayerEntitiesComponent.add(pathEntity.id)
            }
        }
    }

    private fun processAnimation() {
        for (entry in myObjectsMap.entries) {
            if (mapPath(entry).animation == 2) {

                val animation = myComponentManager
                    .createEntity("map_ent_path_animation")
                    .addComponent(
                        AnimationComponent().apply {
                            duration = 5_000.0
                            easingFunction = Animations.LINEAR
                            direction = Animation.Direction.FORWARD
                            loop = Animation.Loop.KEEP_DIRECTION
                        }
                    )

                entity(entry)
                    .setComponent(RendererComponent(GrowingPath.GrowingPathRenderer()))
                    .addComponent(GrowingPath.GrowingPathEffectComponent().setAnimationId(animation.id))
            }
        }
    }

    companion object {
        private val SIMPLE_RENDERER = Renderers.PathRenderer()

        private fun mapPath(entry: Entry<MapPath, EcsEntity>): MapPath {
            return entry.key
        }

        private fun entity(entry: Entry<MapPath, EcsEntity>): EcsEntity {
            return entry.value
        }
    }


}
