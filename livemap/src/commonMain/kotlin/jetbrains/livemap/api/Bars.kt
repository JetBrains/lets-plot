package jetbrains.livemap.api

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.rendering.layers.LayerGroup
import jetbrains.livemap.placement.*
import jetbrains.livemap.projection.Client
import jetbrains.livemap.rendering.*
import jetbrains.livemap.rendering.Renderers.BarRenderer
import jetbrains.livemap.searching.BarLocatorHelper
import jetbrains.livemap.searching.IndexComponent
import jetbrains.livemap.searching.LocatorComponent
import kotlin.math.abs
import kotlin.math.sign

@LiveMapDsl
class Bars(
    factory: MapEntityFactory
) {
    val barsFactory = BarsFactory(factory)
}

fun LayersBuilder.bars(block: Bars.() -> Unit) {
    val layerEntity = myComponentManager
        .createEntity("map_layer_bar")
        .addComponents {
            + layerManager.addLayer("livemap_bar", LayerGroup.FEATURES)
            + LayerEntitiesComponent()
        }

    Bars(
        MapEntityFactory(layerEntity)
    ).apply {
        block()
        barsFactory.produce()
    }
}

fun Bars.bar(block: ChartSource.() -> Unit) {
    barsFactory.add(ChartSource().apply(block))
}

@LiveMapDsl
class BarsFactory(
    private val myFactory: MapEntityFactory
) {
    private val myItems = ArrayList<ChartSource>()

    fun add(source: ChartSource) {
        myItems.add(source)
    }

    fun produce(): List<EcsEntity> {
        val maxAbsValue = myItems
            .asSequence()
            .mapNotNull(ChartSource::values)
            .flatten()
            .map(::abs)
            .max()
            ?: error("Failed to calculate maxAbsValue.")

        val result = ArrayList<EcsEntity>()

        myItems.forEach { source ->
            splitMapBarChart(source, maxAbsValue) {index, barOffset, barDimension, color->
                result.add(
                    when {
                        source.point != null -> myFactory.createStaticEntityWithLocation("map_ent_s_bar", source.point!!)
                        else -> error("Can't create bar entity. Coord is null.")
                    }.setInitializer { worldPoint ->
                        source.layerIndex?.let { + IndexComponent(layerIndex = it, index = index) }
                        + RendererComponent(BarRenderer())
                        + WorldOriginComponent(worldPoint)
                        + ScreenLoopComponent()
                        + ScreenOriginComponent()
                        + ScreenOffsetComponent().apply { offset = barOffset}
                        + ScreenDimensionComponent().apply { dimension = barDimension }
                        + StyleComponent().apply {
                            setFillColor(color)
                            setStrokeColor(source.strokeColor)
                            setStrokeWidth(source.strokeWidth)
                        }
                        + LocatorComponent(BarLocatorHelper())
                    }
                )
            }
        }

        return result
    }
}

private const val MIN_HEIGHT = 0.05

fun splitMapBarChart(chart: ChartSource, maxAbsValue: Double, consumer: (Int, Vec<Client>, Vec<Client>, Color) -> Unit) {
    val heights = chart.values.map { barValue ->
        val height = when (maxAbsValue) {
            0.0 -> 0.0
            else -> barValue / maxAbsValue
        }

        when {
            abs(height) >= MIN_HEIGHT -> height //
            else -> height.sign * MIN_HEIGHT
        }
    }
    val barWidth = (2 * chart.radius) / chart.values.size

    heights.forEachIndexed { index, height ->
        val barDimension =  explicitVec<Client>(barWidth, chart.radius * abs(height))
        val barOffset = explicitVec<Client>(
            x = barWidth * index - chart.radius,
            y = if (height > 0) -barDimension.y else 0.0
        )
        consumer(chart.indices[index], barOffset, barDimension, chart.colors[index])
    }
}
