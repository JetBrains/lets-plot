/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.core.animation.Animation
import jetbrains.livemap.core.animation.Animations
import jetbrains.livemap.core.animation.Animations.AnimationBuilder
import jetbrains.livemap.core.ecs.AnimationObjectComponent
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.rendering.TransformComponent
import jetbrains.livemap.core.rendering.layers.LayerGroup
import jetbrains.livemap.core.rendering.layers.ParentLayerComponent
import jetbrains.livemap.entities.Entities.MapEntityFactory
import jetbrains.livemap.entities.placement.*
import jetbrains.livemap.entities.rendering.*
import jetbrains.livemap.entities.rendering.Renderers.PointRenderer
import jetbrains.livemap.projection.MapProjection

@LiveMapDsl
class Points(
    val factory: MapEntityFactory,
    val mapProjection: MapProjection,
    val pointScaling: Boolean,
    val animationBuilder: AnimationBuilder
)

fun LayersBuilder.points(block: Points.() -> Unit) {
    val layerEntity = myComponentManager
        .createEntity("map_layer_point")
        .addComponents {
            + layerManager.addLayer("geom_point", LayerGroup.FEATURES)
            + LayerEntitiesComponent()
        }

    val animationBuilder = AnimationBuilder(500.0)
        .setDirection(Animation.Direction.FORWARD)
        .setLoop(Animation.Loop.SWITCH_DIRECTION)

    Points(
        MapEntityFactory(layerEntity),
        mapProjection,
        pointScaling,
        animationBuilder
    ).apply(block)

    myComponentManager
        .createEntity("map_ent_point_animation")
        .setComponent(
            AnimationObjectComponent(animationBuilder.build())
        )
}

fun Points.point(block: PointBuilder.() -> Unit) {
    PointBuilder(factory)
        .apply(block)
        .build(pointScaling, animationBuilder)
}

@LiveMapDsl
class PointBuilder(
    private val myFactory: MapEntityFactory
) {
    var index: Int = 0
    var mapId: String? = null
    var point: Vec<LonLat>? = null

    var radius: Double = 4.0
    var fillColor: Color = Color.WHITE
    var strokeColor: Color = Color.BLACK
    var strokeWidth: Double = 1.0

    var animation: Int = 0
    var label: String = ""
    var shape: Int = 1

    fun build(
        pointScaling: Boolean,
        animationBuilder: AnimationBuilder
    ): EcsEntity {

        val size = radius * 2.0

        return when {
                point != null ->
                    myFactory.createStaticEntityWithLocation("map_ent_s_point", point!!)
                mapId != null ->
                    myFactory.createDynamicEntityWithLocation("map_ent_d_point_$mapId", mapId!!)
                else ->
                    error("Can't create point entity. [point] and [mapId] is null.")
            }.run {
                setInitializer { worldPoint ->
                    + ShapeComponent().apply { shape = this@PointBuilder.shape }
                    + createStyle()
                    + if (pointScaling) {
                        WorldDimensionComponent(explicitVec(size, size))
                    } else {
                        ScreenDimensionComponent().apply {
                            dimension = explicitVec(size, size)
                        }
                    }
                    + WorldOriginComponent(worldPoint)
                    + RendererComponent(PointRenderer())
                    + ScreenLoopComponent()
                    + ScreenOriginComponent()

                    if (animation == 2) {
                        val transformComponent = TransformComponent()
                        val scaleAnimator = Animations.DoubleAnimator(0.0, 1.0) {
                            transformComponent.scale = it
                            ParentLayerComponent.tagDirtyParentLayer(this@run)
                        }

                        animationBuilder.addAnimator(scaleAnimator)

                        + transformComponent
                    }
                }
        }
    }

    private fun createStyle(): StyleComponent {
        return when(shape) {
            in 1..14 -> StyleComponent().apply { setStrokeColor(this@PointBuilder.strokeColor); strokeWidth = this@PointBuilder.strokeWidth }
            in 15..18, 20 -> StyleComponent().apply { setFillColor(this@PointBuilder.strokeColor); strokeWidth = Double.NaN }
            19 -> StyleComponent().apply { setFillColor(this@PointBuilder.strokeColor); setStrokeColor(this@PointBuilder.strokeColor); strokeWidth = this@PointBuilder.strokeWidth }
            in 21..25 -> StyleComponent().apply { setFillColor(this@PointBuilder.fillColor); setStrokeColor(this@PointBuilder.strokeColor); strokeWidth = this@PointBuilder.strokeWidth }
            else -> error("Not supported shape: ${this@PointBuilder.shape}")
        }
    }
}