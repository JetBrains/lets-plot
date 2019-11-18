/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import jetbrains.datalore.base.projectionGeometry.Vec
import jetbrains.datalore.base.projectionGeometry.explicitVec
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.DevParams
import jetbrains.livemap.core.animation.Animation
import jetbrains.livemap.core.animation.Animations
import jetbrains.livemap.core.ecs.AnimationObjectComponent
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.rendering.TransformComponent
import jetbrains.livemap.core.rendering.layers.LayerGroup
import jetbrains.livemap.core.rendering.layers.ParentLayerComponent
import jetbrains.livemap.entities.Entities.MapEntityFactory
import jetbrains.livemap.entities.placement.ScreenDimensionComponent
import jetbrains.livemap.entities.placement.WorldDimensionComponent
import jetbrains.livemap.entities.rendering.*
import jetbrains.livemap.projections.Client
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.World

@LiveMapDsl
class Points(
    val factory: MapEntityFactory,
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
            + layerManager.addLayer("geom_point", LayerGroup.FEATURES)
            + layerEntitiesComponent
        }

    val animationBuilder = Animations.AnimationBuilder(500.0)
        .setDirection(Animation.Direction.FORWARD)
        .setLoop(Animation.Loop.SWITCH_DIRECTION)

    Points(
        MapEntityFactory(layerEntity),
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
}

fun Points.point(block: PointBuilder.() -> Unit) {
    PointBuilder()
        .apply(block)
        .build(factory, mapProjection, devParams, animationBuilder)
        .let { entity -> layerEntitiesComponent.add(entity.id) }
}

@LiveMapDsl
class PointBuilder {
    var index: Int = 0
    var mapId: String = ""
    var regionId: String = ""

    lateinit var point: Vec<LonLat>

    var radius: Double = 4.0
    var fillColor: Color = Color.WHITE
    var strokeColor: Color = Color.BLACK
    var strokeWidth: Double = 1.0

    var animation: Int = 0
    var label: String = ""
    var shape: Int = 1

    fun build(
        factory: MapEntityFactory,
        mapProjection: MapProjection,
        devParams: DevParams,
        animationBuilder: Animations.AnimationBuilder
    ): EcsEntity {
        val size = radius * 2.0

        val entity = factory
            .createMapEntity(mapProjection.project(point), Renderers.PointRenderer(), "map_ent_point")
            .addComponents {
                + PointComponent().apply { shape = this@PointBuilder.shape }
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
            in 1..14 -> StyleComponent().apply { setStrokeColor(this@PointBuilder.strokeColor); strokeWidth = this@PointBuilder.strokeWidth }
            in 15..18, 20 -> StyleComponent().apply { setFillColor(this@PointBuilder.strokeColor); strokeWidth = Double.NaN }
            19 -> StyleComponent().apply { setFillColor(this@PointBuilder.strokeColor); setStrokeColor(this@PointBuilder.strokeColor); strokeWidth = this@PointBuilder.strokeWidth }
            in 21..25 -> StyleComponent().apply { setFillColor(this@PointBuilder.fillColor); setStrokeColor(this@PointBuilder.strokeColor); strokeWidth = this@PointBuilder.strokeWidth }
            else -> error("Not supported shape: ${this@PointBuilder.shape}")
        }
    }
}