package jetbrains.livemap.api

import jetbrains.datalore.base.projectionGeometry.LonLat
import jetbrains.datalore.base.projectionGeometry.explicitVec
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.maps.livemap.entities.point.PointComponent
import jetbrains.datalore.maps.livemap.entities.point.PointRenderer
import jetbrains.livemap.DevParams
import jetbrains.livemap.core.animation.Animation
import jetbrains.livemap.core.animation.Animations
import jetbrains.livemap.core.ecs.AnimationObjectComponent
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.rendering.TransformComponent
import jetbrains.livemap.core.rendering.layers.ParentLayerComponent
import jetbrains.livemap.entities.Entities
import jetbrains.livemap.entities.placement.ScreenDimensionComponent
import jetbrains.livemap.entities.placement.WorldDimensionComponent
import jetbrains.livemap.entities.rendering.LayerEntitiesComponent
import jetbrains.livemap.entities.rendering.StyleComponent
import jetbrains.livemap.entities.rendering.setFillColor
import jetbrains.livemap.entities.rendering.setStrokeColor
import jetbrains.livemap.projections.Client
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.World

@LiveMapDsl
class Points(
    val factory: Entities.MapEntityFactory,
    val layerEntitiesComponent: LayerEntitiesComponent,
    val mapProjection: MapProjection,
    val devParams: DevParams,
    val animationBuilder: Animations.AnimationBuilder
)

fun LayersBuilder.points(block: Points.() -> Unit) {
    val layerEntitiesComponent = LayerEntitiesComponent()
    val layerEntity = myComponentManager
        .createEntity("map_layer_point")
        .addComponents {
            + layerManager.createRenderLayerComponent("geom_point")
            + layerEntitiesComponent
        }

    val animationBuilder = Animations.AnimationBuilder(500.0)
        .setDirection(Animation.Direction.FORWARD)
        .setLoop(Animation.Loop.SWITCH_DIRECTION)

    Points(
        Entities.MapEntityFactory(layerEntity),
        layerEntitiesComponent,
        mapProjection,
        devParams,
        animationBuilder
    ).apply(block)

    myComponentManager
        .createEntity("map_ent_point_animation")
        .setComponent(
            AnimationObjectComponent(animationBuilder.build())
        )

    //items.add(MapLayer(POINT, Points().apply(block).items))
}

fun Points.point(block: PointBuilder.() -> Unit) {
    val entity = PointBuilder().apply {
        animation = 0
        index = 0
        mapId = ""
        regionId = ""
        label = ""

        strokeWidth = 1.0
        strokeColor = Color.BLACK

        fillColor = Color.WHITE

        radius = 4.0
        shape = 1
    }
        .apply(block)
        .build(factory, mapProjection, devParams, animationBuilder)

    layerEntitiesComponent.add(entity.id)
}

@LiveMapDsl
class PointBuilder {
    var animation: Int? = null
    var label: String? = null
    var shape: Int? = null
    var lat: Double? = null
    var lon: Double? = null
    var radius: Double? = null
    var fillColor: Color? = null
    var strokeColor: Color? = null
    var strokeWidth: Double? = null
    var index: Int? = null
    var mapId: String? = null
    var regionId: String? = null

    fun build(
        factory: Entities.MapEntityFactory,
        mapProjection: MapProjection,
        devParams: DevParams,
        animationBuilder: Animations.AnimationBuilder
    ): EcsEntity {
        val size = radius!! * 2.0

        val entity = factory
            .createMapEntity(mapProjection.project(explicitVec<LonLat>(lon!!, lat!!)), PointRenderer(), "map_ent_point")
            .addComponents {
                + PointComponent().apply { shape = this@PointBuilder.shape!! }
                + createStyle()
                + if (devParams.isSet(DevParams.POINT_SCALING)) {
                    WorldDimensionComponent(explicitVec<World>(size, size))
                } else {
                    ScreenDimensionComponent().apply {
                        dimension = explicitVec<Client>(size, size)
                    }
                }
            }

        if (animation == 2) {
            val transformComponent = TransformComponent()
            val scaleAnimator = Animations.DoubleAnimator(0.0, 1.0) {
                transformComponent.scale = it
                ParentLayerComponent.tagDirtyParentLayer(entity)
            }

            animationBuilder.addAnimator(scaleAnimator)
            entity.addComponents{ + transformComponent }
        }

        return entity
    }

    private fun createStyle(): StyleComponent {
        return when(shape) {
            in 1..14 -> StyleComponent().apply { setStrokeColor(this@PointBuilder.strokeColor!!); strokeWidth = this@PointBuilder.strokeWidth!! }
            in 15..18, 20 -> StyleComponent().apply { setFillColor(this@PointBuilder.strokeColor!!); strokeWidth = Double.NaN }
            19 -> StyleComponent().apply { setFillColor(this@PointBuilder.strokeColor!!); setStrokeColor(this@PointBuilder.strokeColor!!); strokeWidth = this@PointBuilder.strokeWidth!! }
            in 21..25 -> StyleComponent().apply { setFillColor(this@PointBuilder.fillColor!!); setStrokeColor(this@PointBuilder.strokeColor!!); strokeWidth = this@PointBuilder.strokeWidth!! }
            else -> error("Not supported shape: ${this@PointBuilder.shape}")
        }
    }
}