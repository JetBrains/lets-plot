/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.chart.DonutChart
import jetbrains.livemap.chart.PieSpecComponent
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.layers.LayerKind
import jetbrains.livemap.mapengine.LayerEntitiesComponent
import jetbrains.livemap.mapengine.MapProjection
import jetbrains.livemap.mapengine.RenderableComponent
import jetbrains.livemap.mapengine.placement.ScreenDimensionComponent
import jetbrains.livemap.mapengine.placement.WorldOriginComponent
import jetbrains.livemap.searching.IndexComponent
import jetbrains.livemap.searching.LocatorComponent

@LiveMapDsl
class Pies(
    val factory: MapEntityFactory,
    val mapProjection: MapProjection
)

fun LayersBuilder.pies(block: Pies.() -> Unit) {
    val layerEntity = myComponentManager
        .createEntity("map_layer_pie")
        .addComponents {
            + layerManager.addLayer("geom_pie", LayerKind.FEATURES)
            + LayerEntitiesComponent()
        }

    Pies(
        MapEntityFactory(layerEntity),
        mapProjection
    ).apply(block)
}

fun Pies.pie(block: PieBuilder.() -> Unit) {
    PieBuilder(factory)
        .apply(block)
        .build()
}

@LiveMapDsl
class PieBuilder(
    private val myFactory: MapEntityFactory
) {
    var sizeScalingRange: ClosedRange<Int>? = null
    var alphaScalingEnabled: Boolean = false

    var layerIndex: Int? = null
    var radius: Double = 0.0
    var holeSize: Double = 0.0
    var point: Vec<LonLat>? = null

    var strokeColor: Color = Color.WHITE
    var strokeWidth: Double = 0.0

    var indices: List<Int> = emptyList()
    var values: List<Double> = emptyList()
    var colors: List<Color> = emptyList()
    var explodes: List<Double>? = null

    fun build(): EcsEntity {
        return when {
            point != null -> myFactory.createStaticEntityWithLocation("map_ent_s_pie_sector", point!!)
            else -> error("Can't create pieSector entity. Coord is null.")
        }.run {
            setInitializer { worldPoint ->
                if (layerIndex != null) {
                    +IndexComponent(layerIndex!!, 0)
                }
                +LocatorComponent(DonutChart.DonutLocator)
                +RenderableComponent().apply {
                    renderer = DonutChart.Renderer()
                }
                +ChartElementComponent().apply {
                    sizeScalingRange = this@PieBuilder.sizeScalingRange
                    alphaScalingEnabled = this@PieBuilder.alphaScalingEnabled
                    strokeColor = this@PieBuilder.strokeColor
                    strokeWidth = this@PieBuilder.strokeWidth
                }
                + PieSpecComponent().apply {
                    radius = this@PieBuilder.radius
                    holeSize = this@PieBuilder.holeSize
                    sliceValues = this@PieBuilder.values
                    colors = this@PieBuilder.colors
                    indices = this@PieBuilder.indices
                    explodeValues = this@PieBuilder.explodes
                }
                +WorldOriginComponent(worldPoint)
                +ScreenDimensionComponent()
            }
        }
    }
}