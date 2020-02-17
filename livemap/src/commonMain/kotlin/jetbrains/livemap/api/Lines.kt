/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.rendering.layers.LayerGroup
import jetbrains.livemap.geocoding.NeedCalculateLocationComponent
import jetbrains.livemap.geocoding.NeedGeocodeLocationComponent
import jetbrains.livemap.geocoding.NeedLocationComponent
import jetbrains.livemap.geometry.WorldGeometryComponent
import jetbrains.livemap.placement.ScreenLoopComponent
import jetbrains.livemap.placement.ScreenOriginComponent
import jetbrains.livemap.placement.WorldDimensionComponent
import jetbrains.livemap.placement.WorldOriginComponent
import jetbrains.livemap.rendering.*
import jetbrains.livemap.rendering.Renderers.PathRenderer
import jetbrains.livemap.projection.MapProjection
import jetbrains.livemap.searching.IndexComponent

@LiveMapDsl
class Lines(
    val factory: MapEntityFactory,
    val mapProjection: MapProjection,
    val horizontal: Boolean
)

private fun LayersBuilder.lines(horizontal: Boolean, block: Lines.() -> Unit) {
    val layerEntity = myComponentManager
        .createEntity("map_layer_line")
        .addComponents {
            + layerManager.addLayer("geom_line", LayerGroup.FEATURES)
            + LayerEntitiesComponent()
        }

    Lines(
        MapEntityFactory(layerEntity),
        mapProjection,
        horizontal
    ).apply(block)
}

fun LayersBuilder.hLines(block: Lines.() -> Unit) {
    lines(true, block)
}

fun LayersBuilder.vLines(block: Lines.() -> Unit) {
    lines(false, block)
}

fun Lines.line(block: LineBuilder.() -> Unit) {
    LineBuilder(factory, mapProjection)
        .apply(block)
        .build(horizontal)
}

@LiveMapDsl
class LineBuilder(
    private val myFactory: MapEntityFactory,
    private val myMapProjection: MapProjection
) {
    var index: Int = 0
    var mapId: String? = null
    var point: Vec<LonLat>? = null

    var lineDash: List<Double> = emptyList()
    var strokeColor: Color = Color.BLACK
    var strokeWidth: Double = 1.0

    fun build(
        horizontal: Boolean
    ): EcsEntity {

        return when {
            point != null ->
                myFactory.createStaticEntity("map_ent_s_line", point!!)
            mapId != null ->
                myFactory.createDynamicEntity("map_ent_d_line_$mapId", mapId!!)
            else ->
                error("Can't create line entity. [point] and [mapId] is null.")
        }
            .setInitializer { worldPoint ->
                val line = createLineGeometry(worldPoint, horizontal, myMapProjection.mapRect)
                val bbox = createLineBBox(worldPoint, strokeWidth, horizontal, myMapProjection.mapRect)

                + IndexComponent(index)
                + RendererComponent(PathRenderer())
                + WorldOriginComponent(bbox.origin)
                + WorldGeometryComponent().apply { geometry = line }
                + WorldDimensionComponent(bbox.dimension)
                + ScreenLoopComponent()
                + ScreenOriginComponent()
                + StyleComponent().apply {
                    setStrokeColor(this@LineBuilder.strokeColor)
                    setStrokeWidth(this@LineBuilder.strokeWidth)
                    setLineDash(this@LineBuilder.lineDash)
                }
            }
            .remove<NeedLocationComponent>()
            .remove<NeedCalculateLocationComponent>()
            .remove<NeedGeocodeLocationComponent>()
    }
}
