package jetbrains.livemap.api

import jetbrains.datalore.base.projectionGeometry.LonLat
import jetbrains.datalore.base.projectionGeometry.Vec
import jetbrains.datalore.base.values.Color
import jetbrains.gis.geoprotocol.GeometryUtil
import jetbrains.livemap.core.animation.Animation
import jetbrains.livemap.core.animation.Animations
import jetbrains.livemap.core.ecs.AnimationComponent
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.effects.GrowingPath.GrowingPathEffectComponent
import jetbrains.livemap.effects.GrowingPath.GrowingPathRenderer
import jetbrains.livemap.entities.Entities
import jetbrains.livemap.entities.geometry.LonLatGeometry
import jetbrains.livemap.entities.geometry.Renderers
import jetbrains.livemap.entities.geometry.WorldGeometry
import jetbrains.livemap.entities.geometry.WorldGeometryComponent
import jetbrains.livemap.entities.placement.WorldDimensionComponent
import jetbrains.livemap.entities.rendering.LayerEntitiesComponent
import jetbrains.livemap.entities.rendering.RendererComponent
import jetbrains.livemap.entities.rendering.StyleComponent
import jetbrains.livemap.entities.rendering.setStrokeColor
import jetbrains.livemap.projections.*

@LiveMapDsl
class Paths(
    val factory: Entities.MapEntityFactory,
    val layerEntitiesComponent: LayerEntitiesComponent,
    val toMapProjection: (LonLatGeometry) -> WorldGeometry
)

fun LayersBuilder.paths(block: Paths.() -> Unit) {
    val layerEntitiesComponent = LayerEntitiesComponent()
    val layerEntity = myComponentManager
        .createEntity("map_layer_path")
        .addComponents {
            + layerManager.createRenderLayerComponent("geom_path")
            + layerEntitiesComponent
        }

    val toMapProjection = { geometry: LonLatGeometry ->
        geometry.asMultipolygon()
            .run { ProjectionUtil.transformMultiPolygon(this, mapProjection::project) }
            .run { WorldGeometry.create(this) }
    }

    Paths(
        Entities.MapEntityFactory(layerEntity),
        layerEntitiesComponent,
        toMapProjection
    ).apply(block)
}

fun Paths.path(block: PathBuilder.() -> Unit) {
    PathBuilder().apply {
        index = 0
        mapId = ""
        regionId = ""

        lineDash = emptyList()
        strokeColor = Color.BLACK
        strokeWidth = 1.0
        coordinates = emptyList()

        animation = 0
        speed = 0.0
        flow = 0.0

    }
        .apply(block)
        .build(factory, toMapProjection)
        ?.let { pathEntity ->
            layerEntitiesComponent.add(pathEntity.id)
        }
}

@LiveMapDsl
class PathBuilder {
    var index: Int? = null
    var mapId: String? = null
    var regionId: String? = null

    var lineDash: List<Double>? = null
    var strokeColor: Color? = null
    var strokeWidth: Double? = null
    var coordinates: List<Vec<LonLat>>? = null

    var animation: Int? = null
    var speed: Double? = null
    var flow: Double? = null

    var geodesic: Boolean? = null

    fun build(
        factory: Entities.MapEntityFactory,
        toMapProjection: (LonLatGeometry) -> WorldGeometry
    ): EcsEntity? {
        val coord = (coordinates.takeIf { !geodesic!! } ?: createArcPath(coordinates!!))
                .run { LonLatRing(this) }
                .run { LonLatPolygon(listOf(this)) }
                .run { LonLatMultiPolygon(listOf(this)) }
                .run { LonLatGeometry.create(this) }
                .run { toMapProjection(this) }

        return coord
            .run { asMultipolygon() }
            .run { GeometryUtil.bbox(this) }
            ?.let { bbox ->
                val entity = factory
                    .createMapEntity(bbox.origin, Renderers.PathRenderer(), "map_ent_path")
                    .addComponents {
                        + WorldGeometryComponent().apply {
                            geometry = coord
                        }
                        + WorldDimensionComponent(bbox.dimension)
                        + StyleComponent().apply {
                            setStrokeColor(this@PathBuilder.strokeColor!!)
                            strokeWidth = this@PathBuilder.strokeWidth!!
                            lineDash = this@PathBuilder.lineDash!!.toDoubleArray()
                        }
                    }

                if (animation == 2) {
                    val animationEntity = entity.componentManager
                        .createEntity("map_ent_path_animation")
                        .addAnimationComponent {
                            duration = 5_000.0
                            easingFunction = Animations.LINEAR
                            direction = Animation.Direction.FORWARD
                            loop = Animation.Loop.KEEP_DIRECTION
                        }

                    entity
                        .setComponent(RendererComponent(GrowingPathRenderer()))
                        .addGrowingPathEffectComponent { animationId = animationEntity.id }
                }

                return entity
            }
    }

    private fun EcsEntity.addAnimationComponent(block: AnimationComponent.() -> Unit): EcsEntity {
        this.componentManager.addComponent(this, AnimationComponent().apply(block))
        return this
    }

    private  fun EcsEntity.addGrowingPathEffectComponent(block: GrowingPathEffectComponent.() -> Unit): EcsEntity {
        this.componentManager.addComponent(this, GrowingPathEffectComponent().apply(block))
        return this
    }
}