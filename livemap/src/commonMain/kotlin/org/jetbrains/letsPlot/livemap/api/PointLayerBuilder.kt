/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.api

import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.livemap.chart.ChartElementComponent
import org.jetbrains.letsPlot.livemap.chart.IndexComponent
import org.jetbrains.letsPlot.livemap.chart.LocatorComponent
import org.jetbrains.letsPlot.livemap.chart.PointComponent
import org.jetbrains.letsPlot.livemap.chart.point.PointLocator
import org.jetbrains.letsPlot.livemap.chart.point.PointRenderer
import org.jetbrains.letsPlot.livemap.core.ecs.EcsEntity
import org.jetbrains.letsPlot.livemap.core.ecs.addComponents
import org.jetbrains.letsPlot.livemap.core.layers.LayerKind
import org.jetbrains.letsPlot.livemap.mapengine.LayerEntitiesComponent
import org.jetbrains.letsPlot.livemap.mapengine.MapProjection
import org.jetbrains.letsPlot.livemap.mapengine.RenderableComponent
import org.jetbrains.letsPlot.livemap.mapengine.placement.ScreenDimensionComponent
import org.jetbrains.letsPlot.livemap.mapengine.placement.WorldOriginComponent

@LiveMapDsl
class PointLayerBuilder(
    val factory: FeatureEntityFactory,
    val mapProjection: MapProjection
)

fun FeatureLayerBuilder.points(block: PointLayerBuilder.() -> Unit) {
    val layerEntity = myComponentManager
        .createEntity("map_layer_point")
        .addComponents {
            + layerManager.addLayer("geom_point", LayerKind.FEATURES)
            + LayerEntitiesComponent()
        }

    PointLayerBuilder(
        FeatureEntityFactory(layerEntity, panningPointsMaxCount = 200),
        mapProjection
    ).apply(block)
}

fun PointLayerBuilder.point(block: PointEntityBuilder.() -> Unit) {
    PointEntityBuilder(factory)
        .apply(block)
        .build()
}

@LiveMapDsl
class PointEntityBuilder(
    private val myFactory: FeatureEntityFactory,
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
            point != null -> myFactory.createStaticFeatureWithLocation("map_ent_s_point", point!!)
            else -> error("Can't create point entity. Coord is null.")
        }.run {
            myFactory.incrementLayerPointsTotalCount(1)
            setInitializer { worldPoint ->
                if (layerIndex != null && index != null) {
                    +IndexComponent(layerIndex!!, index!!)
                }
                + RenderableComponent().apply {
                    renderer = PointRenderer(shape)
                }
                +ChartElementComponent().apply {
                    sizeScalingRange = this@PointEntityBuilder.sizeScalingRange
                    alphaScalingEnabled = this@PointEntityBuilder.alphaScalingEnabled
                    when (shape) {
                        in 0..14 -> {
                            strokeColor = this@PointEntityBuilder.strokeColor
                            strokeWidth = this@PointEntityBuilder.strokeWidth
                        }
                        in 15..18, 20 -> {
                            fillColor = this@PointEntityBuilder.strokeColor
                            strokeWidth = Double.NaN
                        }
                        19 -> {
                            fillColor = this@PointEntityBuilder.strokeColor
                            strokeColor = this@PointEntityBuilder.strokeColor
                            strokeWidth = this@PointEntityBuilder.strokeWidth
                        }
                        in 21..25 -> {
                            fillColor = this@PointEntityBuilder.fillColor
                            strokeColor = this@PointEntityBuilder.strokeColor
                            strokeWidth = this@PointEntityBuilder.strokeWidth
                        }
                        else -> error("Not supported shape: ${this@PointEntityBuilder.shape}")
                    }
                }
                +PointComponent().apply {
                    size = d
                }

                +WorldOriginComponent(worldPoint)
                +ScreenDimensionComponent()

                if (!nonInteractive) {
                    +LocatorComponent(PointLocator)
                }
            }
        }
    }
}
