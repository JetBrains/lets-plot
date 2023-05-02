/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import jetbrains.datalore.base.math.toRadians
import jetbrains.datalore.base.spatial.Geodesic
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.wrapPath
import jetbrains.datalore.base.typedGeometry.Geometry
import jetbrains.datalore.base.typedGeometry.LineString
import jetbrains.datalore.base.typedGeometry.MultiLineString
import jetbrains.datalore.base.typedGeometry.Transforms.transform
import jetbrains.datalore.base.typedGeometry.Transforms.transformPoints
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.World
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
import jetbrains.livemap.core.util.EasingFunctions.LINEAR
import jetbrains.livemap.geocoding.NeedCalculateLocationComponent
import jetbrains.livemap.geocoding.NeedLocationComponent
import jetbrains.livemap.geometry.MicroTasks.RESAMPLING_PRECISION
import jetbrains.livemap.geometry.WorldGeometryComponent
import jetbrains.livemap.mapengine.LayerEntitiesComponent
import jetbrains.livemap.mapengine.MapProjection
import jetbrains.livemap.mapengine.RenderableComponent
import jetbrains.livemap.mapengine.placement.WorldDimensionComponent
import jetbrains.livemap.mapengine.placement.WorldOriginComponent
import jetbrains.livemap.searching.IndexComponent
import jetbrains.livemap.searching.LocatorComponent
import jetbrains.livemap.searching.PathLocator

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
    var flat: Boolean = false
    var geodesic: Boolean = false
    var animation: Int = 0
    var speed: Double = 0.0
    var flow: Double = 0.0

    // Arrow specification
    var arrowAngle: Double? = null
    var arrowLength: Double? = null
    var arrowAtEnds: String? = null
    var arrowType: String? = null

    fun build(nonInteractive: Boolean): EcsEntity? {
        // flat can't be geodesic
        val geodesic = if (flat) false else geodesic

        fun transformPath(points: List<Vec<LonLat>>): MultiLineString<World> =
            when {
                flat ->
                    transformPoints(points, myMapProjection::apply, resamplingPrecision = null)
                        .let { wrapPath(it, World.DOMAIN) }
                        .let { MultiLineString(it.map(::LineString)) }

                else ->
                    wrapPath(points, LonLat.DOMAIN)
                        .let { MultiLineString(it.map(::LineString)) }
                        .let { transform(it, myMapProjection::apply, RESAMPLING_PRECISION.takeUnless { geodesic }) }
            }

        // location is never built on geodesic points - they alter minimal bbox too much
        val locGeometry = transformPath(points)
        val visGeometry = transformPath(points.takeUnless { geodesic } ?: Geodesic.createArcPath(points))

        return visGeometry.bbox?.let { bbox ->
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
                        geometry = Geometry.of(locGeometry)
                    }
                    +WorldOriginComponent(bbox.origin)
                    +WorldGeometryComponent().apply { geometry = Geometry.of(visGeometry) }
                    +WorldDimensionComponent(bbox.dimension)
                    +NeedLocationComponent
                    +NeedCalculateLocationComponent
                    if (!nonInteractive) {
                        +LocatorComponent(PathLocator)
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
    require(
        ends in listOf(
            "last",
            "first",
            "both"
        )
    ) { "Expected ends to draw arrows values: 'first'|'last'|'both', but was '$ends'" }
    arrowAtEnds = ends
    require(type in listOf("open", "closed")) { "Expected arrowhead type values: 'open'|'closed', but was '$type'" }
    arrowType = type
}

