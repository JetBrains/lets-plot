/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.LonLatPoint
import jetbrains.datalore.base.typedGeometry.MultiPolygon
import jetbrains.datalore.base.values.Color
import jetbrains.gis.geoprotocol.GeometryUtil
import jetbrains.livemap.core.animation.Animation
import jetbrains.livemap.core.animation.Animations
import jetbrains.livemap.core.ecs.AnimationComponent
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.projections.ProjectionUtil.transformMultiPolygon
import jetbrains.livemap.core.rendering.layers.LayerGroup
import jetbrains.livemap.effects.GrowingPath.GrowingPathEffectComponent
import jetbrains.livemap.effects.GrowingPath.GrowingPathRenderer
import jetbrains.livemap.geocoding.NeedCalculateLocationComponent
import jetbrains.livemap.geocoding.NeedLocationComponent
import jetbrains.livemap.geometry.WorldGeometryComponent
import jetbrains.livemap.placement.ScreenLoopComponent
import jetbrains.livemap.placement.ScreenOriginComponent
import jetbrains.livemap.placement.WorldDimensionComponent
import jetbrains.livemap.placement.WorldOriginComponent
import jetbrains.livemap.projection.MapProjection
import jetbrains.livemap.rendering.LayerEntitiesComponent
import jetbrains.livemap.rendering.RendererComponent
import jetbrains.livemap.rendering.Renderers.PathRenderer
import jetbrains.livemap.rendering.StyleComponent
import jetbrains.livemap.rendering.setStrokeColor
import jetbrains.livemap.searching.IndexComponent
import jetbrains.livemap.searching.LocatorComponent
import jetbrains.livemap.searching.PathLocatorHelper

@LiveMapDsl
class Paths(
    val factory: MapEntityFactory,
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
        MapEntityFactory(layerEntity),
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
    private val myFactory: MapEntityFactory,
    private val myMapProjection: MapProjection
) {
    var layerIndex: Int? = null
    var index: Int? = null
    var mapId: String = ""
    var regionId: String = ""

    var lineDash: List<Double> = emptyList()
    var strokeColor: Color = Color.BLACK
    var strokeWidth: Double = 1.0

    lateinit var multiPolygon: MultiPolygon<LonLat>

    var animation: Int = 0
    var speed: Double = 0.0
    var flow: Double = 0.0

    fun build(nonInteractive: Boolean = false): EcsEntity? {
        val coord = transformMultiPolygon(multiPolygon, myMapProjection::project)

        return coord
            .run { GeometryUtil.bbox(this) }
            ?.let { bbox ->
                val entity = myFactory
                    .createMapEntity("map_ent_path")
                    .addComponents {
                        if (layerIndex != null && index != null) {
                            + IndexComponent(layerIndex!!, index!!)
                        }
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
                        if (!nonInteractive) {
                            + LocatorComponent(PathLocatorHelper())
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
        return add(AnimationComponent().apply(block))
    }

    private  fun EcsEntity.addGrowingPathEffectComponent(block: GrowingPathEffectComponent.() -> Unit): EcsEntity {
        return add(GrowingPathEffectComponent().apply(block))
    }
}

fun PathBuilder.geometry(points: List<LonLatPoint>, isGeodesic: Boolean) {
    multiPolygon = geometry(points, isClosed = false, isGeodesic = isGeodesic)
}