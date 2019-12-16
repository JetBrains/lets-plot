/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.typedGeometry.MultiPolygon
import jetbrains.datalore.base.values.Color
import jetbrains.gis.geoprotocol.GeometryUtil
import jetbrains.livemap.core.animation.Animation
import jetbrains.livemap.core.animation.Animations
import jetbrains.livemap.core.ecs.AnimationComponent
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.rendering.layers.LayerGroup
import jetbrains.livemap.effects.GrowingPath.GrowingPathEffectComponent
import jetbrains.livemap.effects.GrowingPath.GrowingPathRenderer
import jetbrains.livemap.entities.Entities
import jetbrains.livemap.entities.geocoding.NeedCalculateLocationComponent
import jetbrains.livemap.entities.geocoding.NeedLocationComponent
import jetbrains.livemap.entities.geometry.WorldGeometryComponent
import jetbrains.livemap.entities.placement.ScreenLoopComponent
import jetbrains.livemap.entities.placement.ScreenOriginComponent
import jetbrains.livemap.entities.placement.WorldDimensionComponent
import jetbrains.livemap.entities.placement.WorldOriginComponent
import jetbrains.livemap.entities.rendering.LayerEntitiesComponent
import jetbrains.livemap.entities.rendering.RendererComponent
import jetbrains.livemap.entities.rendering.Renderers.PathRenderer
import jetbrains.livemap.entities.rendering.StyleComponent
import jetbrains.livemap.entities.rendering.setStrokeColor
import jetbrains.livemap.projections.LonLatPoint
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.ProjectionUtil.transformMultiPolygon

@LiveMapDsl
class Paths(
    val factory: Entities.MapEntityFactory,
    val mapProjection: MapProjection
)

fun LayersBuilder.paths(block: Paths.() -> Unit) {
    val layerEntity = myComponentManager
        .createEntity("map_layer_path")
        .addComponents {
            + layerManager.addLayer("geom_path", LayerGroup.FEATURES)
            + LayerEntitiesComponent()
        }

    Paths(
        Entities.MapEntityFactory(layerEntity),
        mapProjection
    ).apply(block)
}

fun Paths.path(block: PathBuilder.() -> Unit) {
    PathBuilder(factory, mapProjection)
        .apply(block)
        .build()
}

@LiveMapDsl
class PathBuilder(
    private val myFactory: Entities.MapEntityFactory,
    private val myMapProjection: MapProjection
) {
    var index: Int = 0
    var mapId: String = ""
    var regionId: String = ""

    var lineDash: List<Double> = emptyList()
    var strokeColor: Color = Color.BLACK
    var strokeWidth: Double = 1.0

    lateinit var multiPolygon: MultiPolygon<LonLat>

    var animation: Int = 0
    var speed: Double = 0.0
    var flow: Double = 0.0

    fun build(): EcsEntity? {
        val coord = transformMultiPolygon(multiPolygon, myMapProjection::project)

        return coord
            .run { GeometryUtil.bbox(this) }
            ?.let { bbox ->
                val entity = myFactory
                    .createMapEntity("map_ent_path")
                    .addComponents {
                        + RendererComponent(PathRenderer())
                        + WorldOriginComponent(bbox.origin)
                        + WorldGeometryComponent().apply { geometry = coord }
                        + WorldDimensionComponent(bbox.dimension)
                        + ScreenLoopComponent()
                        + ScreenOriginComponent()
                        + StyleComponent().apply {
                            setStrokeColor(this@PathBuilder.strokeColor)
                            strokeWidth = this@PathBuilder.strokeWidth
                            lineDash = this@PathBuilder.lineDash.toDoubleArray()
                        }
                        + NeedLocationComponent()
                        + NeedCalculateLocationComponent()
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
        return add(AnimationComponent().apply(block))
    }

    private  fun EcsEntity.addGrowingPathEffectComponent(block: GrowingPathEffectComponent.() -> Unit): EcsEntity {
        return add(GrowingPathEffectComponent().apply(block))
    }
}

fun PathBuilder.geometry(points: List<LonLatPoint>, isGeodesic: Boolean) {
    multiPolygon = geometry(points, isGeodesic, isClosed = false)
}