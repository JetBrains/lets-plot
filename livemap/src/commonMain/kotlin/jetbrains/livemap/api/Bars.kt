package jetbrains.livemap.api

import jetbrains.datalore.base.projectionGeometry.Vec
import jetbrains.datalore.base.projectionGeometry.explicitVec
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.rendering.layers.LayerGroup
import jetbrains.livemap.entities.Entities.MapEntityFactory
import jetbrains.livemap.entities.geocoding.CentroidComponent
import jetbrains.livemap.entities.geocoding.CentroidTag
import jetbrains.livemap.entities.placement.*
import jetbrains.livemap.entities.regions.MapIdComponent
import jetbrains.livemap.entities.rendering.*
import jetbrains.livemap.entities.rendering.Renderers.BarRenderer
import jetbrains.livemap.projections.Client
import jetbrains.livemap.projections.LonLatPoint
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
    val layerEntity = myComponentManager
        .createEntity("map_layer_bar")
        .addComponents {
            + layerManager.addLayer("livemap_bar", LayerGroup.FEATURES)
            + LayerEntitiesComponent()
        }

    Bars(
        MapEntityFactory(layerEntity),
        mapProjection
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
                    when {
                        source.point != null -> createStaticEntity(source.point!!)
                        source.mapId != null -> createDynamicEntity(source.mapId!!)
                        else -> error("Can't create bar entity. [point] and [mapId] is null.")
                    }.addComponents { worldPoint ->
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
                    }
                )
            }
        }

        return result
    }

    private fun createStaticEntity(point: LonLatPoint): EcsEntity {
        return myEntityFactory
            .createMapEntity("map_ent_s_bar")
            .add(CentroidComponent(myMapProjection.project(point)))
    }

    private fun createDynamicEntity(mapId: String): EcsEntity {
        return myEntityFactory
            .createMapEntity("map_ent_d_bar_$mapId")
            .addComponents {
                + CentroidTag()
                + MapIdComponent(mapId)
            }
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