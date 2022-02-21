/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.typedGeometry.*
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.World
import jetbrains.livemap.WorldPoint
import jetbrains.livemap.WorldRectangle
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.chart.Renderers.PathRenderer
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.layers.LayerGroup
import jetbrains.livemap.geocoding.NeedCalculateLocationComponent
import jetbrains.livemap.geocoding.NeedGeocodeLocationComponent
import jetbrains.livemap.geocoding.NeedLocationComponent
import jetbrains.livemap.geometry.WorldGeometryComponent
import jetbrains.livemap.mapengine.LayerEntitiesComponent
import jetbrains.livemap.mapengine.MapProjection
import jetbrains.livemap.mapengine.RenderableComponent
import jetbrains.livemap.mapengine.placement.ScreenLoopComponent
import jetbrains.livemap.mapengine.placement.ScreenOriginComponent
import jetbrains.livemap.mapengine.placement.WorldDimensionComponent
import jetbrains.livemap.mapengine.placement.WorldOriginComponent

@LiveMapDsl
class Lines(
    val factory: MapEntityFactory,
    val mapProjection: MapProjection,
    val horizontal: Boolean,
)

private fun LayersBuilder.lines(horizontal: Boolean, block: Lines.() -> Unit) {
    val layerEntity = myComponentManager
        .createEntity("map_layer_line")
        .addComponents {
            +layerManager.addLayer("geom_line", LayerGroup.FEATURES)
            +LayerEntitiesComponent()
        }

    Lines(
        MapEntityFactory(layerEntity),
        mapProjection,
        horizontal
    ).apply(block)
}

fun LayersBuilder.hLines(block: Lines.() -> Unit) = lines(true, block)
fun LayersBuilder.vLines(block: Lines.() -> Unit) = lines(false, block)

fun Lines.line(block: LineBuilder.() -> Unit) {
    LineBuilder(factory, mapProjection)
        .apply(block)
        .build(horizontal)
}

@LiveMapDsl
class LineBuilder(
    private val factory: MapEntityFactory,
    private val mapProjection: MapProjection,
) {
    var sizeScalingRange: ClosedRange<Int>? = null
    var alphaScalingEnabled: Boolean = false
    var point: Vec<LonLat>? = null
    var lineDash: List<Double> = emptyList()
    var strokeColor: Color = Color.BLACK
    var strokeWidth: Double = 1.0

    fun build(horizontal: Boolean): EcsEntity = when {
        point != null -> factory.createStaticEntity("map_ent_s_line", point!!)
        else -> error("Can't create line entity. Coord is null.")
    }.setInitializer { worldPoint ->
        val line = createLineGeometry(worldPoint, horizontal, mapProjection.mapRect)
        val bbox = createLineBBox(worldPoint, strokeWidth, horizontal, mapProjection.mapRect)

        +RenderableComponent().apply {
            renderer = PathRenderer()
        }
        +ChartElementComponent().apply {
            sizeScalingRange = this@LineBuilder.sizeScalingRange
            alphaScalingEnabled = this@LineBuilder.alphaScalingEnabled
            strokeColor = this@LineBuilder.strokeColor
            strokeWidth = this@LineBuilder.strokeWidth
            lineDash = this@LineBuilder.lineDash.toDoubleArray()
        }
        +WorldOriginComponent(bbox.origin)
        +WorldGeometryComponent().apply { geometry = line }
        +WorldDimensionComponent(bbox.dimension)
        +ScreenLoopComponent()
        +ScreenOriginComponent()
    }
        .remove<NeedLocationComponent>()
        .remove<NeedCalculateLocationComponent>()
        .remove<NeedGeocodeLocationComponent>()

}

private fun createLineGeometry(point: WorldPoint, horizontal: Boolean, mapRect: WorldRectangle): MultiPolygon<World> {
    return if (horizontal) {
        listOf(
            point.transform(newX = { mapRect.scalarLeft }),
            point.transform(newX = { mapRect.scalarRight })

        )
    } else {
        listOf(
            point.transform(newY = { mapRect.scalarTop }),
            point.transform(newY = { mapRect.scalarBottom })
        )
    }
        .run { listOf(Ring(this)) }
        .run { listOf(Polygon(this)) }
        .run { MultiPolygon(this) }
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
