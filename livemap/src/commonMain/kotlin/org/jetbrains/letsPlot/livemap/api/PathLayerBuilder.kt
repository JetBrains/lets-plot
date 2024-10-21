/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.api

import org.jetbrains.letsPlot.commons.intern.math.toRadians
import org.jetbrains.letsPlot.commons.intern.spatial.Geodesic
import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.spatial.wrapPath
import org.jetbrains.letsPlot.commons.intern.typedGeometry.*
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Transforms.transform
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Transforms.transformPoints
import org.jetbrains.letsPlot.commons.intern.util.ArrowSupport
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.livemap.Client
import org.jetbrains.letsPlot.livemap.Client.Companion.px
import org.jetbrains.letsPlot.livemap.World
import org.jetbrains.letsPlot.livemap.chart.ChartElementComponent
import org.jetbrains.letsPlot.livemap.chart.ChartElementLocationComponent
import org.jetbrains.letsPlot.livemap.chart.GrowingPathEffect.GrowingPathEffectComponent
import org.jetbrains.letsPlot.livemap.chart.GrowingPathEffect.GrowingPathRenderer
import org.jetbrains.letsPlot.livemap.chart.IndexComponent
import org.jetbrains.letsPlot.livemap.chart.LocatorComponent
import org.jetbrains.letsPlot.livemap.chart.path.ArrowSpec
import org.jetbrains.letsPlot.livemap.chart.path.CurveRenderer
import org.jetbrains.letsPlot.livemap.chart.path.PathLocator
import org.jetbrains.letsPlot.livemap.chart.path.PathRenderer
import org.jetbrains.letsPlot.livemap.core.animation.Animation
import org.jetbrains.letsPlot.livemap.core.ecs.AnimationComponent
import org.jetbrains.letsPlot.livemap.core.ecs.EcsEntity
import org.jetbrains.letsPlot.livemap.core.ecs.addComponents
import org.jetbrains.letsPlot.livemap.core.layers.LayerKind
import org.jetbrains.letsPlot.livemap.core.util.EasingFunctions.LINEAR
import org.jetbrains.letsPlot.livemap.geocoding.NeedCalculateLocationComponent
import org.jetbrains.letsPlot.livemap.geocoding.NeedLocationComponent
import org.jetbrains.letsPlot.livemap.geometry.MicroTasks.RESAMPLING_PRECISION
import org.jetbrains.letsPlot.livemap.geometry.WorldGeometryComponent
import org.jetbrains.letsPlot.livemap.mapengine.LayerEntitiesComponent
import org.jetbrains.letsPlot.livemap.mapengine.MapProjection
import org.jetbrains.letsPlot.livemap.mapengine.RenderableComponent
import org.jetbrains.letsPlot.livemap.mapengine.placement.WorldDimensionComponent
import org.jetbrains.letsPlot.livemap.mapengine.placement.WorldOriginComponent

@LiveMapDsl
class PathLayerBuilder(
    val factory: FeatureEntityFactory,
    val mapProjection: MapProjection
)

fun FeatureLayerBuilder.paths(block: PathLayerBuilder.() -> Unit) {
    val layerEntity = myComponentManager
        .createEntity("map_layer_path")
        .addComponents {
            +layerManager.addLayer("geom_path", LayerKind.FEATURES)
            +LayerEntitiesComponent()
        }

    PathLayerBuilder(
        FeatureEntityFactory(layerEntity, panningPointsMaxCount = 15_000),
        mapProjection
    ).apply(block)
}

fun PathLayerBuilder.path(block: PathEntityBuilder.() -> Unit) {
    PathEntityBuilder(factory, mapProjection)
        .apply(block)
        .build(nonInteractive = false)
}

@LiveMapDsl
class PathEntityBuilder(
    private val myFactory: FeatureEntityFactory,
    private val myMapProjection: MapProjection
) {
    var sizeScalingRange: ClosedRange<Int>? = null
    var alphaScalingEnabled: Boolean = false
    var layerIndex: Int? = null
    var index: Int? = null
    var regionId: String = ""

    var lineDash: List<Double> = emptyList()
    var lineDashOffset: Double = 0.0
    var strokeColor: Color = Color.BLACK
    var strokeWidth: Double = 1.0

    lateinit var points: List<Vec<LonLat>>
    var flat: Boolean = false
    var geodesic: Boolean = false
    var animation: Int = 0
    var speed: Double = 0.0
    var flow: Double = 0.0
    var isCurve: Boolean = false

    // Arrow specification
    var arrowSpec: ArrowSpec? = null

    var sizeStart = 0.px
    var sizeEnd = 0.px
    var strokeStart = 0.px
    var strokeEnd = 0.px
    var spacer = 0.px

    fun build(nonInteractive: Boolean): EcsEntity? {
        // flat can't be geodesic
        val geodesic = if (flat) false else geodesic

        fun transformPath(points: List<Vec<LonLat>>): MultiLineString<World> = when {
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

        // Calculate paddings based on the target size, spacer and arrow spec
        val targetSizeStart = sizeStart / 2.0 + strokeStart
        val targetSizeEnd = sizeEnd / 2.0 + strokeEnd

        val startArrowPadding = arrowSpec?.let {
            ArrowSupport.arrowPadding(
                angle = it.angle,
                onStart = it.isOnFirstEnd,
                onEnd = it.isOnLastEnd,
                atStart = true,
                strokeSize = strokeWidth
            ).px
        } ?: 0.px

        val endArrowPadding = arrowSpec?.let {
            ArrowSupport.arrowPadding(
                angle = it.angle,
                onStart = it.isOnFirstEnd,
                onEnd = it.isOnLastEnd,
                atStart = false,
                strokeSize = strokeWidth
            ).px
        } ?: 0.px

        // Total offsets
        val startPadding = targetSizeStart + spacer + startArrowPadding
        val endPadding = targetSizeEnd + spacer + endArrowPadding

        myFactory.incrementLayerPointsTotalCount(visGeometry.sumOf(LineString<World>::size))
        return visGeometry.bbox?.let { bbox ->
            val entity = myFactory
                .createFeature("map_ent_path")
                .addComponents {
                    if (layerIndex != null && index != null) {
                        +IndexComponent(layerIndex!!, index!!)
                    }
                    +RenderableComponent().apply {
                        renderer = if (isCurve) CurveRenderer() else PathRenderer()
                    }
                    +ChartElementComponent().apply {
                        sizeScalingRange = this@PathEntityBuilder.sizeScalingRange
                        alphaScalingEnabled = this@PathEntityBuilder.alphaScalingEnabled
                        strokeColor = this@PathEntityBuilder.strokeColor
                        strokeWidth = this@PathEntityBuilder.strokeWidth
                        lineDash = this@PathEntityBuilder.lineDash.toDoubleArray()
                        lineDashOffset = this@PathEntityBuilder.lineDashOffset
                        arrowSpec = this@PathEntityBuilder.arrowSpec
                        this.startPadding = startPadding
                        this.endPadding = endPadding
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
fun PathEntityBuilder.arrow(
    angle: Double = 30.0,
    length: Scalar<Client> = 10.px,
    ends: ArrowSpec.End = ArrowSpec.End.LAST,
    type: ArrowSpec.Type = ArrowSpec.Type.OPEN
) {
    arrowSpec = ArrowSpec(toRadians(angle), length, ends, type)
}
