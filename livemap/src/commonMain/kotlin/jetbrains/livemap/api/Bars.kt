package jetbrains.livemap.api

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.rendering.layers.LayerGroup
import jetbrains.livemap.placement.*
import jetbrains.livemap.rendering.*
import jetbrains.livemap.rendering.Renderers.BarRenderer
import jetbrains.livemap.projection.Client
import jetbrains.livemap.searching.BarLocatorHelper
import jetbrains.livemap.searching.IndexComponent
import jetbrains.livemap.searching.LocatorComponent
import kotlin.math.abs

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
                        source.point != null ->
                            myFactory.createStaticEntityWithLocation("map_ent_s_bar", source.point!!)
                        source.mapId != null ->
                            myFactory.createDynamicEntityWithLocation("map_ent_d_bar_${source.mapId}", source.mapId!!)
                        else ->
                            error("Can't create bar entity. [point] and [mapId] is null.")
                    }.setInitializer { worldPoint ->
                        if (source.layerIndex != null) {
                            + IndexComponent(source.layerIndex!!, index)
                        }
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

fun splitMapBarChart(source: ChartSource, maxAbsValue: Double, consumer: (Int, Vec<Client>, Vec<Client>, Color) -> Unit) {
    val percents = transformValues2Percents(source.values, maxAbsValue)

    val radius = source.radius
    val barCount = percents.size
    val spacing = 0.1 * radius
    val barWidth = (2 * radius - (barCount - 1) * spacing) / barCount

    for (i in percents.indices) {
        val barDimension =  explicitVec<Client>(barWidth, radius * abs(percents[i]))
        val barOffset = explicitVec<Client>(
            (barWidth + spacing) * i - radius,
            if (percents[i] > 0) -barDimension.y else 0.0
        )
        consumer(source.indices[i], barOffset, barDimension, source.colors[i])
    }
}