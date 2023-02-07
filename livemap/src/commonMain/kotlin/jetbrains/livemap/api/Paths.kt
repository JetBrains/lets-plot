/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import jetbrains.datalore.base.math.toRadians
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.typedGeometry.*
import jetbrains.datalore.base.typedGeometry.Transforms.transformMultiPolygon
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.chart.ChartElementLocationComponent
import jetbrains.livemap.chart.GrowingPathEffect.GrowingPathEffectComponent
import jetbrains.livemap.chart.GrowingPathEffect.GrowingPathRenderer
import jetbrains.livemap.chart.Renderers.PathRenderer
import jetbrains.livemap.chart.Renderers.PathRenderer.ArrowSpec
import jetbrains.livemap.core.animation.Animation
import jetbrains.livemap.core.ecs.AnimationComponent
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.layers.LayerKind
import jetbrains.livemap.core.projections.createArcPath
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
import kotlin.math.abs

@LiveMapDsl
class Paths(
    val factory: MapEntityFactory,
    val mapProjection: MapProjection
)

fun LayersBuilder.paths(block: Paths.() -> Unit) {
    val layerEntity = myComponentManager
        .createEntity("map_layer_path")
        .addComponents {
            +layerManager.addLayer("geom_path", LayerKind.FEATURES)
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

    lateinit var points: List<Vec<LonLat>>
    var flat: Boolean = true
    var animation: Int = 0
    var speed: Double = 0.0
    var flow: Double = 0.0

    // Arrow specification
    var arrowAngle: Double? = null
    var arrowLength: Double? = null
    var arrowAtEnds: String? = null
    var arrowType: String? = null

    fun build(nonInteractive: Boolean): EcsEntity? {
        // Build location component on original, non-curved, path data
        val locationGeometry = splitAndPackPath(points)
            .let { transformMultiPolygon(it, myMapProjection::project, resamplingPrecision = null) }

        // Build visualization component - bend if needed
        val multiPolygon = splitAndPackPath(if (flat) points else createArcPath(points))
        val coord = transformMultiPolygon(multiPolygon, myMapProjection::project, resamplingPrecision = null)

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
                        arrowSpec = ArrowSpec.create(
                            this@PathBuilder.arrowAngle,
                            this@PathBuilder.arrowLength,
                            this@PathBuilder.arrowAtEnds,
                            this@PathBuilder.arrowType,
                        )
                    }
                    +ChartElementLocationComponent().apply {
                        geometry = Geometry.createMultiPolygon(locationGeometry)
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
}

/**
 * @param angle - the angle of the arrow head in degrees
 * @param length - the length of the arrow head (px).
 * @param ends - {'last', 'first', 'both'}
 * @param type - {'open', 'closed'}
 * */
fun PathBuilder.arrow(
    angle: Double = 30.0,
    length: Double = 10.0,
    ends: String = "last",
    type: String = "open"
) {
    arrowAngle = toRadians(angle)
    arrowLength = length
    require(ends in listOf("last", "first", "both")) { "Expected ends to draw arrows values: 'first'|'last'|'both', but was '$ends'"}
    arrowAtEnds = ends
    require(type in listOf("open", "closed")) { "Expected arrowhead type values: 'open'|'closed', but was '$type'"}
    arrowType = type
}

internal fun splitAndPackPath(points: List<Vec<LonLat>>): MultiPolygon<LonLat> {
    return points
        .run(::splitPathByAntiMeridian)
        .map { path -> Polygon(listOf(Ring(path))) }
        .run(::MultiPolygon)
}

// TODO: looks outdated - does it even work?
private fun splitPathByAntiMeridian(path: List<Vec<LonLat>>): List<List<Vec<LonLat>>> {
    val pathList = ArrayList<List<Vec<LonLat>>>()
    var currentPath = ArrayList<Vec<LonLat>>()
    if (path.isNotEmpty()) {
        currentPath.add(path[0])

        for (i in 1 until path.size) {
            val prev = path[i - 1]
            val next = path[i]
            val lonDelta = abs(next.x - prev.x)

            if (lonDelta > 360.0 - lonDelta) {
                val sign = (if (prev.x < 0.0) -1 else +1).toDouble()

                val x1 = prev.x - sign * 180.0
                val x2 = next.x + sign * 180.0
                val lat = (next.y - prev.y) * (if (x2 == x1) 1.0 / 2.0 else x1 / (x1 - x2)) + prev.y

                currentPath.add(explicitVec(sign * 180.0, lat))
                pathList.add(currentPath)
                currentPath = ArrayList()
                currentPath.add(explicitVec(-sign * 180.0, lat))
            }

            currentPath.add(next)
        }
    }

    pathList.add(currentPath)
    return pathList
}
