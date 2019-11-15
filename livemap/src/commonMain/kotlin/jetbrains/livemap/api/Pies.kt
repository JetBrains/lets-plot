/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.rendering.layers.LayerGroup
import jetbrains.livemap.entities.Entities.MapEntityFactory
import jetbrains.livemap.entities.placement.ScreenDimensionComponent
import jetbrains.livemap.entities.rendering.*
import jetbrains.livemap.projections.MapProjection

@LiveMapDsl
class Pies(
    factory: MapEntityFactory,
    mapProjection: MapProjection
) {
    val piesFactory = PiesFactory(factory, mapProjection)
}

fun LayersBuilder.pies(block: Pies.() -> Unit) {
    val layerEntitiesComponent = LayerEntitiesComponent()
    val layerEntity = myComponentManager
        .createEntity("map_layer_pie")
        .addComponents {
            + layerManager.addLayer("livemap_pie", LayerGroup.FEATURES)
            + layerEntitiesComponent
        }

    Pies(
        MapEntityFactory(layerEntity),
        mapProjection
    ).apply {
        block()
        piesFactory
            .produce()
            .forEach {
                layerEntitiesComponent.add(it.id)
            }
    }
}

fun Pies.pie(block: ChartSource.() -> Unit) {
    piesFactory.add(ChartSource().apply(block))
}

@LiveMapDsl
class PiesFactory(
    private val myEntityFactory: MapEntityFactory,
    private val myMapProjection: MapProjection
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
        var startAngle = 0.0

        for (i in angles.indices) {
            val endAngle = startAngle + angles[i]
            result.add(
                myEntityFactory
                    .createMapEntity(
                        myMapProjection.project(source.point),
                        Renderers.PieSectorRenderer(), "map_ent_pie_sector"
                    )
                    .addComponents {
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
                    }
            )
            startAngle = endAngle
        }

        return result
    }
}