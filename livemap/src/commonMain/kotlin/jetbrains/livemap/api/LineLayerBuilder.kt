/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.typedGeometry.*
import org.jetbrains.letsPlot.commons.values.Color
import jetbrains.livemap.World
import jetbrains.livemap.WorldPoint
import jetbrains.livemap.WorldRectangle
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.chart.path.PathRenderer
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.layers.LayerKind
import jetbrains.livemap.geocoding.NeedCalculateLocationComponent
import jetbrains.livemap.geocoding.NeedGeocodeLocationComponent
import jetbrains.livemap.geocoding.NeedLocationComponent
import jetbrains.livemap.geometry.WorldGeometryComponent
import jetbrains.livemap.mapengine.LayerEntitiesComponent
import jetbrains.livemap.mapengine.MapProjection
import jetbrains.livemap.mapengine.RenderableComponent
import jetbrains.livemap.mapengine.placement.WorldDimensionComponent
import jetbrains.livemap.mapengine.placement.WorldOriginComponent
import org.jetbrains.letsPlot.commons.intern.typedGeometry.*

@LiveMapDsl
class LineLayerBuilder(
    val factory: FeatureEntityFactory,
    val mapProjection: MapProjection,
    val horizontal: Boolean,
)

private fun FeatureLayerBuilder.lines(horizontal: Boolean, block: LineLayerBuilder.() -> Unit) {
    val layerEntity = myComponentManager
        .createEntity("map_layer_line")
        .addComponents {
            +layerManager.addLayer("geom_line", LayerKind.FEATURES)
            +LayerEntitiesComponent()
        }

    LineLayerBuilder(
        FeatureEntityFactory(layerEntity, panningPointsMaxCount = 15_000),
        mapProjection,
        horizontal
    ).apply(block)
}

fun FeatureLayerBuilder.hLines(block: LineLayerBuilder.() -> Unit) = lines(true, block)
fun FeatureLayerBuilder.vLines(block: LineLayerBuilder.() -> Unit) = lines(false, block)

fun LineLayerBuilder.line(block: LineEntityBuilder.() -> Unit) {
    LineEntityBuilder(factory, mapProjection)
        .apply(block)
        .build(horizontal)
}

@LiveMapDsl
class LineEntityBuilder(
    private val factory: FeatureEntityFactory,
    private val mapProjection: MapProjection,
) {
    var sizeScalingRange: ClosedRange<Int>? = null
    var alphaScalingEnabled: Boolean = false
    var point: Vec<LonLat>? = null
    var lineDash: List<Double> = emptyList()
    var strokeColor: Color = Color.BLACK
    var strokeWidth: Double = 1.0

    fun build(horizontal: Boolean): EcsEntity = when {
        point != null -> factory.createStaticFeature("map_ent_s_line", point!!)
        else -> error("Can't create line entity. Coord is null.")
    }.setInitializer { worldPoint ->
        val line = createLineGeometry(worldPoint, horizontal, mapProjection.mapRect)
        val bbox = createLineBBox(worldPoint, strokeWidth, horizontal, mapProjection.mapRect)

        factory.incrementLayerPointsTotalCount(line.size)

        +RenderableComponent().apply {
            renderer = PathRenderer()
        }
        +ChartElementComponent().apply {
            sizeScalingRange = this@LineEntityBuilder.sizeScalingRange
            alphaScalingEnabled = this@LineEntityBuilder.alphaScalingEnabled
            strokeColor = this@LineEntityBuilder.strokeColor
            strokeWidth = this@LineEntityBuilder.strokeWidth
            lineDash = this@LineEntityBuilder.lineDash.toDoubleArray()
        }
        +WorldOriginComponent(bbox.origin)
        +WorldGeometryComponent().apply { geometry = Geometry.of(line) }
        +WorldDimensionComponent(bbox.dimension)
    }
        .remove<NeedLocationComponent>()
        .remove<NeedCalculateLocationComponent>()
        .remove<NeedGeocodeLocationComponent>()

}

private fun createLineGeometry(point: WorldPoint, horizontal: Boolean, mapRect: WorldRectangle): LineString<World> =
    if (horizontal) {
        LineString.of(point.transform(newX = { mapRect.scalarLeft }), point.transform(newX = { mapRect.scalarRight }))
    } else {
        LineString.of(point.transform(newY = { mapRect.scalarTop }), point.transform(newY = { mapRect.scalarBottom }))
    }

private fun createLineBBox(
    point: WorldPoint,
    strokeWidth: Double,
    horizontal: Boolean,
    mapRect: WorldRectangle,
): WorldRectangle {
    return if (horizontal) {
        WorldRectangle(
            explicitVec(mapRect.left, point.y - strokeWidth / 2),
            explicitVec(mapRect.width, strokeWidth)
        )
    } else {
        WorldRectangle(
            explicitVec(point.x - strokeWidth / 2, mapRect.top),
            explicitVec(strokeWidth, mapRect.height)
        )
    }
}
