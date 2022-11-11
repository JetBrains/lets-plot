/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.chart.DonutChart
import jetbrains.livemap.chart.PieSpecComponent
import jetbrains.livemap.chart.SymbolComponent
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.layers.LayerKind
import jetbrains.livemap.mapengine.LayerEntitiesComponent
import jetbrains.livemap.mapengine.RenderableComponent
import jetbrains.livemap.mapengine.placement.ScreenDimensionComponent
import jetbrains.livemap.mapengine.placement.ScreenLoopComponent
import jetbrains.livemap.mapengine.placement.ScreenOriginComponent
import jetbrains.livemap.mapengine.placement.WorldOriginComponent
import jetbrains.livemap.searching.IndexComponent
import jetbrains.livemap.searching.LocatorComponent
import kotlin.math.PI
import kotlin.math.abs

@LiveMapDsl
class Pies(factory: MapEntityFactory) {
    val piesFactory = PiesFactory(factory)
}

fun LayersBuilder.pies(block: Pies.() -> Unit) {
    val layerEntity = myComponentManager
        .createEntity("map_layer_pie")
        .addComponents {
            + layerManager.addLayer("geom_pie", LayerKind.FEATURES)
            + LayerEntitiesComponent()
        }

    Pies(MapEntityFactory(layerEntity)).apply {
        block()
        piesFactory.produce()
    }
}

fun Pies.pie(block: Symbol.() -> Unit) {
    piesFactory.add(Symbol().apply(block))
}

@LiveMapDsl
class PiesFactory(
    private val myFactory: MapEntityFactory
) {
    private val mySymbols = ArrayList<Symbol>()

    fun add(source: Symbol) {
        mySymbols.add(source)
    }

    fun produce(): List<EcsEntity> {
        return mySymbols.map(this::symbolToEntity)
    }

    private fun symbolToEntity(symbol: Symbol): EcsEntity {
        return when {
            symbol.point != null -> myFactory.createStaticEntityWithLocation("map_ent_s_pie_sector", symbol.point!!)
            else -> error("Can't create pieSector entity. Coord is null.")
        }.setInitializer { worldPoint ->
            if (symbol.layerIndex != null) {
                + IndexComponent(symbol.layerIndex!!, 0)
            }
            + LocatorComponent(DonutChart.Locator())
            + RenderableComponent().apply {
                renderer = DonutChart.Renderer()
            }
            + ChartElementComponent().apply {
                sizeScalingRange = symbol.sizeScalingRange
                alphaScalingEnabled = symbol.alphaScalingEnabled
                strokeColor = symbol.strokeColor
                strokeWidth = symbol.strokeWidth
            }
            + SymbolComponent().apply {
                size = explicitVec(symbol.radius * 2, symbol.radius * 2)
                values = transformValues2Angles(symbol.values)
                colors = symbol.colors
                indices = symbol.indices
                explodeValues = symbol.explodeValues
            }
            + PieSpecComponent().apply {
                holeRatio = symbol.holeRatio
            }
            + WorldOriginComponent(worldPoint)
            + ScreenDimensionComponent()
            + ScreenLoopComponent()
            + ScreenOriginComponent()
        }
    }
}

internal fun transformValues2Angles(values: List<Double>): List<Double> {
    val sum = values.sumOf(::abs)

    return if (sum == 0.0) {
        MutableList(values.size) { 2 * PI / values.size }
    } else {
        values.map { 2 * PI * abs(it) / sum }
    }
}
