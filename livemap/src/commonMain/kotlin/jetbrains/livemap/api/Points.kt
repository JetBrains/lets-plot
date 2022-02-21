/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.chart.Renderers
import jetbrains.livemap.chart.SymbolComponent
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.layers.LayerGroup
import jetbrains.livemap.mapengine.LayerEntitiesComponent
import jetbrains.livemap.mapengine.MapProjection
import jetbrains.livemap.mapengine.RenderableComponent
import jetbrains.livemap.mapengine.placement.ScreenDimensionComponent
import jetbrains.livemap.mapengine.placement.ScreenLoopComponent
import jetbrains.livemap.mapengine.placement.ScreenOriginComponent
import jetbrains.livemap.mapengine.placement.WorldOriginComponent
import jetbrains.livemap.searching.IndexComponent
import jetbrains.livemap.searching.LocatorComponent
import jetbrains.livemap.searching.PointLocatorHelper

@LiveMapDsl
class Points(
    val factory: MapEntityFactory,
    val mapProjection: MapProjection
)

fun LayersBuilder.points(block: Points.() -> Unit) {
    val layerEntity = myComponentManager
        .createEntity("map_layer_point")
        .addComponents {
            + layerManager.addLayer("geom_point", LayerGroup.FEATURES)
            + LayerEntitiesComponent()
        }

    Points(
        MapEntityFactory(layerEntity),
        mapProjection
    ).apply(block)
}

fun Points.point(block: PointBuilder.() -> Unit) {
    PointBuilder(factory)
        .apply(block)
        .build()
}

@LiveMapDsl
class PointBuilder(
    private val myFactory: MapEntityFactory,
) {
    var sizeScalingRange: ClosedRange<Int>? = null
    var alphaScalingEnabled: Boolean = false
    var layerIndex: Int? = null
    var radius: Double = 4.0
    var point: Vec<LonLat>? = null

    var strokeColor: Color = Color.BLACK
    var strokeWidth: Double = 1.0

    var index: Int? = null
    var fillColor: Color = Color.WHITE
    var animation: Int = 0
    var label: String = ""
    var shape: Int = 1

    fun build(nonInteractive: Boolean = false): EcsEntity {

        val d = radius * 2.0
        return when {
            point != null -> myFactory.createStaticEntityWithLocation("map_ent_s_point", point!!)
            else -> error("Can't create point entity. Coord is null.")
        }.run {
            setInitializer { worldPoint ->
                if (layerIndex != null && index != null) {
                    +IndexComponent(layerIndex!!, index!!)
                }
                + RenderableComponent().apply {
                    renderer = Renderers.PointRenderer(shape)
                }
                +ChartElementComponent().apply {
                    sizeScalingRange = this@PointBuilder.sizeScalingRange
                    alphaScalingEnabled = this@PointBuilder.alphaScalingEnabled
                    when (shape) {
                        in 1..14 -> {
                            strokeColor = this@PointBuilder.strokeColor
                            strokeWidth = this@PointBuilder.strokeWidth
                        }
                        in 15..18, 20 -> {
                            fillColor = this@PointBuilder.strokeColor
                            strokeWidth = Double.NaN
                        }
                        19 -> {
                            fillColor = this@PointBuilder.strokeColor
                            strokeColor = this@PointBuilder.strokeColor
                            strokeWidth = this@PointBuilder.strokeWidth
                        }
                        in 21..25 -> {
                            fillColor = this@PointBuilder.fillColor
                            strokeColor = this@PointBuilder.strokeColor
                            strokeWidth = this@PointBuilder.strokeWidth
                        }
                        else -> error("Not supported shape: ${this@PointBuilder.shape}")
                    }
                }
                +SymbolComponent().apply {
                    size = explicitVec(d, d)
                    indices = index?.let { listOf(it) } ?: emptyList()
                    values = emptyList()
                    colors = emptyList()
                }

                +WorldOriginComponent(worldPoint)
                +ScreenDimensionComponent()
                +ScreenLoopComponent()
                +ScreenOriginComponent()

                if (!nonInteractive) {
                    +LocatorComponent(PointLocatorHelper())
                }
            }
        }
    }
}
