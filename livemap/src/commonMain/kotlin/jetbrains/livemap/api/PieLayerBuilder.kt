/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.commons.values.Color
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.chart.IndexComponent
import jetbrains.livemap.chart.LocatorComponent
import jetbrains.livemap.chart.PieSpecComponent
import jetbrains.livemap.chart.donut.Locator
import jetbrains.livemap.chart.donut.Renderer
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.layers.LayerKind
import jetbrains.livemap.mapengine.LayerEntitiesComponent
import jetbrains.livemap.mapengine.MapProjection
import jetbrains.livemap.mapengine.RenderableComponent
import jetbrains.livemap.mapengine.placement.ScreenDimensionComponent
import jetbrains.livemap.mapengine.placement.WorldOriginComponent

@LiveMapDsl
class PieLayerBuilder(
    val factory: FeatureEntityFactory,
    val mapProjection: MapProjection
)

fun FeatureLayerBuilder.pies(block: PieLayerBuilder.() -> Unit) {
    val layerEntity = myComponentManager
        .createEntity("map_layer_pie")
        .addComponents {
            + layerManager.addLayer("geom_pie", LayerKind.FEATURES)
            + LayerEntitiesComponent()
        }

    PieLayerBuilder(
        FeatureEntityFactory(layerEntity, panningPointsMaxCount = 100),
        mapProjection
    ).apply(block)
}

fun PieLayerBuilder.pie(block: PieEntityBuilder.() -> Unit) {
    PieEntityBuilder(factory)
        .apply(block)
        .build()
}

@LiveMapDsl
class PieEntityBuilder(
    private val myFactory: FeatureEntityFactory
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
            point != null -> myFactory.createStaticFeatureWithLocation("map_ent_s_pie_sector", point!!)
            else -> error("Can't create pieSector entity. Coord is null.")
        }.run {
            myFactory.incrementLayerPointsTotalCount(1)
            setInitializer { worldPoint ->
                if (layerIndex != null) {
                    +IndexComponent(layerIndex!!, 0)
                }
                +LocatorComponent(Locator)
                +RenderableComponent().apply {
                    renderer = Renderer()
                }
                +ChartElementComponent().apply {
                    sizeScalingRange = this@PieEntityBuilder.sizeScalingRange
                    alphaScalingEnabled = this@PieEntityBuilder.alphaScalingEnabled
                    strokeColor = this@PieEntityBuilder.strokeColor
                    strokeWidth = this@PieEntityBuilder.strokeWidth
                }
                + PieSpecComponent().apply {
                    radius = this@PieEntityBuilder.radius
                    holeSize = this@PieEntityBuilder.holeSize
                    sliceValues = this@PieEntityBuilder.values
                    colors = this@PieEntityBuilder.colors
                    indices = this@PieEntityBuilder.indices
                    explodeValues = this@PieEntityBuilder.explodes
                }
                +WorldOriginComponent(worldPoint)
                +ScreenDimensionComponent()
            }
        }
    }
}