package jetbrains.livemap.api

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.Client
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.chart.Renderers
import jetbrains.livemap.chart.SymbolComponent
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.rendering.layers.LayerGroup
import jetbrains.livemap.mapengine.LayerEntitiesComponent
import jetbrains.livemap.mapengine.placement.ScreenDimensionComponent
import jetbrains.livemap.mapengine.placement.ScreenLoopComponent
import jetbrains.livemap.mapengine.placement.ScreenOriginComponent
import jetbrains.livemap.mapengine.placement.WorldOriginComponent
import jetbrains.livemap.searching.IndexComponent
import jetbrains.livemap.searching.LocatorComponent
import jetbrains.livemap.searching.PieLocatorHelper
import kotlin.math.abs
import kotlin.math.sign

@LiveMapDsl
class Bars(
    zoomable: Boolean,
    factory: MapEntityFactory
) {
    val barsFactory = BarsFactory(zoomable, factory)
}

fun LayersBuilder.bars(block: Bars.() -> Unit) {
    val layerEntity = myComponentManager
        .createEntity("map_layer_bar")
        .addComponents {
            + layerManager.addLayer("livemap_bar", LayerGroup.FEATURES)
            + LayerEntitiesComponent()
        }

    Bars(zoomable, MapEntityFactory(layerEntity)).apply {
        block()
        barsFactory.produce()
    }
}

fun Bars.bar(block: Symbol.() -> Unit) {
    barsFactory.add(Symbol().apply(block))
}

fun splitMapBarChart(symbol: Symbol, maxAbsValue: Double, consumer: (Int, Vec<Client>, Vec<Client>, Color) -> Unit) {
    val heights = symbol.values.map { barValue ->
        val height = when (maxAbsValue) {
            0.0 -> 0.0
            else -> barValue / maxAbsValue
        }

        when {
            abs(height) >= MIN_HEIGHT -> height //
            else -> height.sign * MIN_HEIGHT
        }
    }
    val barWidth = (2 * symbol.radius) / symbol.values.size

    heights.forEachIndexed { index, height ->
        val barDimension =  explicitVec<Client>(barWidth, symbol.radius * abs(height))
        val barOffset = explicitVec<Client>(
            x = barWidth * index - symbol.radius,
            y = if (height > 0) -barDimension.y else 0.0
        )
        consumer(symbol.indices[index], barOffset, barDimension, symbol.colors[index])
    }
}

@LiveMapDsl
class BarsFactory(
    private val zoomable: Boolean,
    private val myFactory: MapEntityFactory
) {
    private val mySymbols = ArrayList<Symbol>()

    fun add(source: Symbol) {
        mySymbols.add(source)
    }

    fun produce(): List<EcsEntity> {
        val maxAbsValue = mySymbols
            .asSequence()
            .mapNotNull(Symbol::values)
            .flatten()
            .map(::abs)
            .maxOrNull()
            ?: error("Failed to calculate maxAbsValue.")


        return mySymbols.map {
            val heights = it.values.map { value ->
                val height = when (maxAbsValue) {
                    0.0 -> 0.0
                    else -> value / maxAbsValue
                }

                when {
                    abs(height) >= MIN_HEIGHT -> height //
                    else -> height.sign * MIN_HEIGHT
                }
            }

            when {
                it.point != null -> this.myFactory.createStaticEntityWithLocation("map_ent_s_bar_sector", it.point!!)
                else -> error("Can't create barSector entity. Coord is null.")
            }.setInitializer { worldPoint ->
                if (it.layerIndex != null) {
                    + IndexComponent(it.layerIndex!!, 0)
                }
                + ChartElementComponent().apply {
                    renderer = Renderers.BarRenderer()
                    scalable = this@BarsFactory.zoomable
                    strokeColor = it.strokeColor
                    strokeWidth = it.strokeWidth
                }
                + SymbolComponent().apply {
                    size = explicitVec(2 * it.radius, it.radius)
                    values = heights
                    colors = it.colors
                    indices = it.indices
                }
                + WorldOriginComponent(worldPoint)
                + ScreenDimensionComponent()
                + ScreenLoopComponent()
                + ScreenOriginComponent()
                + LocatorComponent(PieLocatorHelper())
            }
        }
    }
}

private const val MIN_HEIGHT = 0.05
