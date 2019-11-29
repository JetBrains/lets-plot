/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import jetbrains.datalore.base.projectionGeometry.Vec
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.rendering.layers.LayerGroup
import jetbrains.livemap.entities.Entities.MapEntityFactory
import jetbrains.livemap.entities.geocoding.LineOrientationComponent
import jetbrains.livemap.entities.geocoding.CentroidTag
import jetbrains.livemap.entities.geometry.WorldGeometryComponent
import jetbrains.livemap.entities.placement.WorldDimensionComponent
import jetbrains.livemap.entities.rendering.*
import jetbrains.livemap.entities.rendering.Renderers.PathRenderer
import jetbrains.livemap.projections.MapProjection

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
            point != null -> createStaticEntity(horizontal)
            mapId != null -> createDynamicEntity(horizontal)
            else -> error("Can't create line entity. [point] and [mapId] is null.")
        }
    }

    private fun createStaticEntity(horizontal: Boolean): EcsEntity {

        val worldPoint = myMapProjection.project(point!!)
        val line = createLineGeometry(worldPoint, horizontal, myMapProjection.mapRect)
        val bbox = createLineBBox(worldPoint, strokeWidth, horizontal, myMapProjection.mapRect)

        return myFactory
            .createMapEntity(bbox.origin, PathRenderer(), "map_ent_s_line")
            .addComponents {
                + WorldGeometryComponent().apply { geometry = line }
                + WorldDimensionComponent(bbox.dimension)
                + setStyle()
            }
    }

    private fun createDynamicEntity(horizontal: Boolean): EcsEntity {

        return myFactory
            .createDynamicMapEntity(mapId!!, PathRenderer(),"map_ent_d_line_$mapId")
            .addComponents {
                + CentroidTag()
                + LineOrientationComponent(horizontal)
                + setStyle()
            }
    }

    private fun setStyle(): StyleComponent {
        return StyleComponent().apply {
            setStrokeColor(this@LineBuilder.strokeColor)
            setStrokeWidth(this@LineBuilder.strokeWidth)
            setLineDash(this@LineBuilder.lineDash)
        }
    }
}
