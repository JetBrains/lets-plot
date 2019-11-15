package jetbrains.livemap.api

import jetbrains.datalore.base.projectionGeometry.Vec
import jetbrains.datalore.base.projectionGeometry.explicitVec
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.rendering.layers.LayerGroup
import jetbrains.livemap.entities.Entities.MapEntityFactory
import jetbrains.livemap.entities.placement.ScreenDimensionComponent
import jetbrains.livemap.entities.placement.ScreenOffsetComponent
import jetbrains.livemap.entities.rendering.*
import jetbrains.livemap.projections.Client
import jetbrains.livemap.projections.MapProjection
import kotlin.math.abs

@LiveMapDsl
class Bars(
    factory: MapEntityFactory,
    mapProjection: MapProjection
) {
    val barsFactory = BarsFactory(factory, mapProjection)
}

fun LayersBuilder.bars(block: Bars.() -> Unit) {
    val layerEntitiesComponent = LayerEntitiesComponent()
    val layerEntity = myComponentManager
        .createEntity("map_layer_bar")
        .addComponents {
            + layerManager.addLayer("livemap_bar", LayerGroup.FEATURES)
            + layerEntitiesComponent
        }

    Bars(
        MapEntityFactory(layerEntity),
        mapProjection
    ).apply {
        block()
        barsFactory
            .produce()
            .forEach {
                layerEntitiesComponent.add(it.id)
            }
    }
}

fun Bars.bar(block: ChartSource.() -> Unit) {
    barsFactory.add(ChartSource().apply(block))
}

@LiveMapDsl
class BarsFactory(
    private val myEntityFactory: MapEntityFactory,
    private val myMapProjection: MapProjection
) {
    private val myItems = ArrayList<ChartSource>()

    fun add(source: ChartSource) {
        myItems.add(source)
    }

    fun produce(): List<EcsEntity> {
        val maxAbsValue = myItems
            .asSequence()
            .mapNotNull { it.values }
            .flatten()
            .maxBy { abs(it) }
            ?: error("Failed to calculate maxAbsValue.")

        val result = ArrayList<EcsEntity>()

        myItems.forEach { source ->
            splitMapBarChart(source, abs(maxAbsValue)) { barOffset, barDimension, color->
                result.add(
                    myEntityFactory
                        .createMapEntity(myMapProjection.project(source.point), Renderers.BarRenderer(), "map_ent_bar")
                        .addComponents {
                            + ScreenOffsetComponent().apply { offset = barOffset}
                            + ScreenDimensionComponent().apply { dimension = barDimension }
                            + StyleComponent().apply {
                                setFillColor(color)
                                setStrokeColor(source.strokeColor)
                                setStrokeWidth(source.strokeWidth)
                            }
                        }
                )
            }
        }

        return result
    }
}

fun splitMapBarChart(source: ChartSource, maxAbsValue: Double, consumer: (Vec<Client>, Vec<Client>, Color) -> Unit) {
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
        consumer(barOffset, barDimension, source.colors[i])
    }
}