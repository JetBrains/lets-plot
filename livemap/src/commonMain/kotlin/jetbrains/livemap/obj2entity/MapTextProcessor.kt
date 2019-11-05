/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.obj2entity

import jetbrains.datalore.base.projectionGeometry.times
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.rendering.layers.LayerManager
import jetbrains.livemap.entities.Entities
import jetbrains.livemap.entities.geometry.Renderers.TextRenderer
import jetbrains.livemap.entities.geometry.TextComponent
import jetbrains.livemap.entities.placement.ScreenDimensionComponent
import jetbrains.livemap.entities.placement.ScreenOffsetComponent
import jetbrains.livemap.entities.rendering.*
import jetbrains.livemap.mapobjects.MapObject
import jetbrains.livemap.mapobjects.MapText
import jetbrains.livemap.projections.MapProjection

class MapTextProcessor(
    componentManager: EcsComponentManager,
    layerManager: LayerManager,
    private val myTextMeasurer: TextMeasurer,
    private val myMapProjection: MapProjection
) {

    private val myObjectsMap = HashMap<MapObject, EcsEntity>()
    private val myLayerEntitiesComponent = LayerEntitiesComponent()
    private val myFactory: Entities.MapEntityFactory

    init {
        componentManager
            .createEntity("map_layer_text")
            .addComponent(layerManager.createRenderLayerComponent("livemap_text"))
            .addComponent(myLayerEntitiesComponent)
            .run { myFactory = Entities.MapEntityFactory(this) }
    }

    internal fun process(mapObjects: List<MapObject>) {
        for (mapObject in mapObjects) {

            val textEntity = createEntity(mapObject as MapText)

            myObjectsMap[mapObject] = textEntity
            myLayerEntitiesComponent.add(textEntity.id)
        }
    }

    private fun createEntity(mapText: MapText): EcsEntity {
        val textSpec = TextSpec(mapText, myTextMeasurer)

        return myFactory
            .createMapEntity(myMapProjection.project(mapText.point), TextRenderer(), "map_ent_text")
            .addComponent(
                ScreenOffsetComponent().apply {
                    offset = textSpec.dimension * -0.5
                }
            )
            .addComponent(
                ScreenDimensionComponent().apply {
                    dimension = textSpec.dimension
                }
            )
            .addComponent(
                TextComponent().apply { this.textSpec = textSpec }
            )
            .addComponent(
                StyleComponent().apply {
                    setFillColor(mapText.fillColor)
                    setStrokeColor(mapText.strokeColor)
                    setStrokeWidth(mapText.strokeWidth)
                }
            )
    }
}