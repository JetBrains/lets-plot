/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import jetbrains.datalore.base.math.toRadians
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.LonLatPoint
import jetbrains.datalore.base.typedGeometry.MultiPolygon
import jetbrains.datalore.base.typedGeometry.Transforms.transformMultiPolygon
import jetbrains.datalore.base.typedGeometry.bbox
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.chart.GrowingPathEffect.GrowingPathEffectComponent
import jetbrains.livemap.chart.GrowingPathEffect.GrowingPathRenderer
import jetbrains.livemap.chart.Renderers.PathRenderer
import jetbrains.livemap.core.animation.Animation
import jetbrains.livemap.core.ecs.AnimationComponent
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.layers.LayerGroup
import jetbrains.livemap.core.util.EasingFunctions.LINEAR
import jetbrains.livemap.geocoding.NeedCalculateLocationComponent
import jetbrains.livemap.geocoding.NeedLocationComponent
import jetbrains.livemap.geometry.WorldGeometryComponent
import jetbrains.livemap.mapengine.LayerEntitiesComponent
import jetbrains.livemap.mapengine.MapProjection
import jetbrains.livemap.mapengine.RenderableComponent
import jetbrains.livemap.mapengine.placement.ScreenLoopComponent
import jetbrains.livemap.mapengine.placement.ScreenOriginComponent
import jetbrains.livemap.mapengine.placement.WorldDimensionComponent
import jetbrains.livemap.mapengine.placement.WorldOriginComponent
import jetbrains.livemap.searching.IndexComponent
import jetbrains.livemap.searching.LocatorComponent
import jetbrains.livemap.searching.PathLocatorHelper
import kotlin.math.cos
import kotlin.math.sin

@LiveMapDsl
class Paths(
    val factory: MapEntityFactory,
    val mapProjection: MapProjection
)

fun LayersBuilder.paths(block: Paths.() -> Unit) {
    val layerEntity = myComponentManager
        .createEntity("map_layer_path")
        .addComponents {
            +layerManager.addLayer("geom_path", LayerGroup.FEATURES)
            +LayerEntitiesComponent()
        }

    Paths(
        MapEntityFactory(layerEntity),
        mapProjection
    ).apply(block)
}

fun Paths.path(block: PathBuilder.() -> Unit) {
    PathBuilder(factory, mapProjection)
        .apply(block)
        .build(nonInteractive = false)
}

@LiveMapDsl
class PathBuilder(
    private val myFactory: MapEntityFactory,
    private val myMapProjection: MapProjection
) {
    var sizeScalingRange: ClosedRange<Int>? = null
    var alphaScalingEnabled: Boolean = false
    var layerIndex: Int? = null
    var index: Int? = null
    var regionId: String = ""

    var lineDash: List<Double> = emptyList()
    var strokeColor: Color = Color.BLACK
    var strokeWidth: Double = 1.0

    lateinit var multiPolygon: MultiPolygon<LonLat>

    var animation: Int = 0
    var speed: Double = 0.0
    var flow: Double = 0.0

    var arrowSpec: ArrowSpec? = null

    fun build(nonInteractive: Boolean): EcsEntity? {
        val coord = transformMultiPolygon(multiPolygon, myMapProjection::project)

        return bbox(coord)?.let { bbox ->
            val entity = myFactory
                .createMapEntity("map_ent_path")
                .addComponents {
                    if (layerIndex != null && index != null) {
                        +IndexComponent(layerIndex!!, index!!)
                    }
                    +RenderableComponent().apply {
                        renderer = PathRenderer()
                    }
                    +ChartElementComponent().apply {
                        sizeScalingRange = this@PathBuilder.sizeScalingRange
                        alphaScalingEnabled = this@PathBuilder.alphaScalingEnabled
                        strokeColor = this@PathBuilder.strokeColor
                        strokeWidth = this@PathBuilder.strokeWidth
                        lineDash = this@PathBuilder.lineDash.toDoubleArray()
                        arrowSpec = this@PathBuilder.arrowSpec
                    }
                    +WorldOriginComponent(bbox.origin)
                    +WorldGeometryComponent().apply { geometry = coord }
                    +WorldDimensionComponent(bbox.dimension)
                    +ScreenLoopComponent()
                    +ScreenOriginComponent()
                    +NeedLocationComponent
                    +NeedCalculateLocationComponent
                    if (!nonInteractive) {
                        +LocatorComponent(PathLocatorHelper())
                    }
                }

            if (animation == 2) {
                val animationEntity = entity.componentManager
                    .createEntity("map_ent_path_animation")
                    .addAnimationComponent {
                        duration = 5_000.0
                        easingFunction = LINEAR
                        direction = Animation.Direction.FORWARD
                        loop = Animation.Loop.KEEP_DIRECTION
                    }

                entity
                    .setComponent(RenderableComponent().apply {
                        renderer = GrowingPathRenderer()
                    })
                    .addGrowingPathEffectComponent { animationId = animationEntity.id }
            }

            return entity
        }
    }

    private fun EcsEntity.addAnimationComponent(block: AnimationComponent.() -> Unit): EcsEntity {
        return add(AnimationComponent().apply(block))
    }

    private fun EcsEntity.addGrowingPathEffectComponent(block: GrowingPathEffectComponent.() -> Unit): EcsEntity {
        return add(GrowingPathEffectComponent().apply(block))
    }

    class ArrowSpec(val angle: Double, val length: Double, val end: End, val type: Type) {
        val isOnFirstEnd: Boolean
            get() = end == End.FIRST || end == End.BOTH

        val isOnLastEnd: Boolean
            get() = end == End.LAST || end == End.BOTH

        fun createGeometry(polarAngle: Double, x: Double, y: Double): Pair<DoubleArray, DoubleArray> {
            val xs = doubleArrayOf(x - length * cos(polarAngle - angle), x, x - length * cos(polarAngle + angle))
            val ys = doubleArrayOf(y - length * sin(polarAngle - angle), y, y - length * sin(polarAngle + angle))
            return xs to ys
        }

        enum class End {
            LAST, FIRST, BOTH
        }

        enum class Type {
            OPEN, CLOSED
        }

        companion object {
            private const val DEF_ANGLE = 30.0
            private const val DEF_LENGTH = 10.0

            /**
             * @param angle - the angle of the arrow head in degrees
             * @param length - the length of the arrow head (px).
             * @param ends - {'last', 'first', 'both'}
             * @param type - {'open', 'closed'}
             * */
            fun arrow(
                angle: Double = DEF_ANGLE,
                length: Double = DEF_LENGTH,
                ends: String = "last",
                type: String = "open"
            ): ArrowSpec {
                val arrowEnd = when (ends) {
                    "last" -> End.LAST
                    "first" -> End.FIRST
                    "both" -> End.BOTH
                    else -> throw IllegalArgumentException("Expected: first|last|both")
                }
                val arrowType = when (type) {
                    "open" -> Type.OPEN
                    "closed" -> Type.CLOSED
                    else -> throw IllegalArgumentException("Expected: open|closed")
                }
                return ArrowSpec(
                    toRadians(angle),
                    length,
                    arrowEnd,
                    arrowType
                )
            }
        }
    }
}

fun PathBuilder.geometry(points: List<LonLatPoint>, isGeodesic: Boolean) {
    multiPolygon = geometry(points, isClosed = false, isGeodesic = isGeodesic)
}
