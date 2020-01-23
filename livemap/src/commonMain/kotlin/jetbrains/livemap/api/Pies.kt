/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.rendering.layers.LayerGroup
import jetbrains.livemap.placement.ScreenDimensionComponent
import jetbrains.livemap.placement.ScreenLoopComponent
import jetbrains.livemap.placement.ScreenOriginComponent
import jetbrains.livemap.placement.WorldOriginComponent
import jetbrains.livemap.rendering.*
import jetbrains.livemap.rendering.Renderers.PieSectorRenderer
import jetbrains.livemap.searching.LocatorComponent
import jetbrains.livemap.searching.PieLocatorHelper
import kotlin.math.PI

@LiveMapDsl
class Pies(
    factory: MapEntityFactory
) {
    val piesFactory = PiesFactory(factory)
}

fun LayersBuilder.pies(block: Pies.() -> Unit) {
    val layerEntity = myComponentManager
        .createEntity("map_layer_pie")
        .addComponents {
            + layerManager.addLayer("livemap_pie", LayerGroup.FEATURES)
            + LayerEntitiesComponent()
        }

    Pies(
        MapEntityFactory(layerEntity)
    ).apply {
        block()
        piesFactory.produce()
    }
}

fun Pies.pie(block: ChartSource.() -> Unit) {
    piesFactory.add(ChartSource().apply(block))
}

@LiveMapDsl
class PiesFactory(
    private val myFactory: MapEntityFactory
) {
    private val myItems = ArrayList<ChartSource>()

    fun add(source: ChartSource) {
        myItems.add(source)
    }

    fun produce(): List<EcsEntity> {
        return myItems.flatMap { splitMapPieChart(it) }
    }

    private fun splitMapPieChart(source: ChartSource): List<EcsEntity> {
        val result = ArrayList<EcsEntity>()
        val angles = transformValues2Angles(source.values)
        var currentAngle = - PI / 2

        for (i in angles.indices) {
            // Do not inline - copy for closure.
            val startAngle = currentAngle
            val endAngle = currentAngle + angles[i]
            result.add(
                when {
                    source.point != null ->
                        myFactory.createStaticEntityWithLocation("map_ent_s_pie_sector", source.point!!)
                    source.mapId != null ->
                        myFactory.createDynamicEntityWithLocation("map_ent_d_pie_sector_${source.mapId}", source.mapId!!)
                    else ->
                        error("Can't create pieSector entity. [point] and [mapId] is null.")
                }.setInitializer { worldPoint ->
                    + RendererComponent(PieSectorRenderer())
                    + WorldOriginComponent(worldPoint)
                    + PieSectorComponent().apply {
                        this.radius = source.radius
                        this.startAngle = startAngle
                        this.endAngle = endAngle
                    }
                    + StyleComponent().apply {
                        setFillColor(source.colors[i])
                        setStrokeColor(source.strokeColor)
                        setStrokeWidth(source.strokeWidth)
                    }
                    + ScreenDimensionComponent()
                    + ScreenLoopComponent()
                    + ScreenOriginComponent()
                    + LocatorComponent(PieLocatorHelper())
                }
            )
            currentAngle = endAngle
        }

        return result
    }
}