/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import jetbrains.datalore.base.projectionGeometry.*
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.entities.Entities
import jetbrains.livemap.entities.geometry.WorldGeometryComponent
import jetbrains.livemap.entities.placement.WorldDimensionComponent
import jetbrains.livemap.entities.rendering.*
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.World
import jetbrains.livemap.projections.WorldPoint
import jetbrains.livemap.projections.WorldRectangle

@LiveMapDsl
class Lines(
    val factory: Entities.MapEntityFactory,
    val layerEntitiesComponent: LayerEntitiesComponent,
    val mapProjection: MapProjection,
    val horizontal: Boolean
)

private fun LayersBuilder.lines(horizontal: Boolean, block: Lines.() -> Unit) {
    val layerEntitiesComponent = LayerEntitiesComponent()

    val layerEntity = myComponentManager
        .createEntity("map_layer_line")
        .addComponents {
            + layerManager.createRenderLayerComponent("geom_line")
            + layerEntitiesComponent
        }

    Lines(
        Entities.MapEntityFactory(layerEntity),
        layerEntitiesComponent,
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
        .let { entity -> layerEntitiesComponent.add(entity.id) }
}

@LiveMapDsl
class LineBuilder(
    private val myFactory: Entities.MapEntityFactory,
    private val myMapProjection: MapProjection
) {
    var index: Int = 0
    var mapId: String = ""
    var regionId: String = ""

    lateinit var point: Vec<LonLat>

    var lineDash: List<Double> = emptyList()
    var strokeColor: Color = Color.BLACK
    var strokeWidth: Double = 1.0

    fun build(
        horizontal: Boolean
    ): EcsEntity {
        val worldPoint = myMapProjection.project(point)
        val line = createLineGeometry(worldPoint, horizontal)
        val bbox = createLineBBox(worldPoint, strokeWidth, horizontal)

        return myFactory
            .createMapEntity(bbox.origin, Renderers.PathRenderer(), "map_ent_line")
            .addComponents {
                + WorldGeometryComponent().apply { geometry = line }
                + WorldDimensionComponent(bbox.dimension)
                + StyleComponent().apply {
                    setStrokeColor(this@LineBuilder.strokeColor)
                    setStrokeWidth(this@LineBuilder.strokeWidth)
                    setLineDash(this@LineBuilder.lineDash)
                }
            }
    }

    private fun createLineGeometry(point: WorldPoint, horizontal: Boolean): MultiPolygon<World> {
        val mapRect = myMapProjection.mapRect

        return if (horizontal) {
            listOf(
                point.transform(
                    newX = { mapRect.scalarLeft }
                ),
                point.transform(
                    newX = { mapRect.scalarRight }
                )

            )
        } else {
            listOf(
                point.transform(
                    newY = { mapRect.scalarTop }
                ),
                point.transform(
                    newY = { mapRect.scalarBottom }
                )
            )
        }
            .run { listOf(Ring(this)) }
            .run { listOf(Polygon(this)) }
            .run { MultiPolygon(this) }
             // World
    }

    private fun createLineBBox(
        point: WorldPoint,
        strokeWidth: Double,
        horizontal: Boolean
    ): WorldRectangle {
        val mapRect = myMapProjection.mapRect

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
}
