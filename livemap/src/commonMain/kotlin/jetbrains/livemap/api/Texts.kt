/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import jetbrains.datalore.base.projectionGeometry.Vec
import jetbrains.datalore.base.projectionGeometry.times
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.rendering.layers.LayerGroup
import jetbrains.livemap.entities.Entities.MapEntityFactory
import jetbrains.livemap.entities.placement.ScreenDimensionComponent
import jetbrains.livemap.entities.placement.ScreenOffsetComponent
import jetbrains.livemap.entities.rendering.*
import jetbrains.livemap.entities.rendering.Renderers.TextRenderer
import jetbrains.livemap.projections.MapProjection

@LiveMapDsl
class Texts(
    val factory: MapEntityFactory,
    val mapProjection: MapProjection,
    val textMeasurer: TextMeasurer
)

fun LayersBuilder.texts(block: Texts.() -> Unit) {
    val layerEntity = myComponentManager
        .createEntity("map_layer_text")
        .addComponents {
            + layerManager.addLayer("livemap_text", LayerGroup.FEATURES)
            + LayerEntitiesComponent()
        }

    Texts(
        MapEntityFactory(layerEntity),
        mapProjection,
        textMeasurer
    ).apply(block)
}

fun Texts.text(block: TextBuilder.() -> Unit) {
    TextBuilder(factory, mapProjection)
        .apply(block)
        .build(textMeasurer)
}

@LiveMapDsl
class TextBuilder(
    private val myFactory: MapEntityFactory,
    private val myMapProjection: MapProjection
) {
    var index: Int = 0
    var mapId: String = ""
    var regionId: String = ""

    lateinit var point: Vec<LonLat>

    var fillColor: Color = Color.BLACK
    var strokeColor: Color = Color.TRANSPARENT
    var strokeWidth: Double = 0.0

    var label: String = ""
    var size: Double = 10.0
    var family: String = "Arial"
    var fontface: String = ""
    var hjust: Double = 0.0
    var vjust: Double = 0.0
    var angle: Double = 0.0

    fun build(
        textMeasurer: TextMeasurer
    ): EcsEntity {
        val textSpec = createTextSpec(textMeasurer)

        return myFactory
            .createMapEntity(myMapProjection.project(point), TextRenderer(), "map_ent_text")
            .addComponents {
                + ScreenOffsetComponent().apply {
                    offset = textSpec.dimension * -0.5
                }
                + ScreenDimensionComponent().apply {
                    dimension = textSpec.dimension
                }
                + TextComponent().apply { this.textSpec = textSpec }
                + StyleComponent().apply {
                    setFillColor(this@TextBuilder.fillColor)
                    setStrokeColor(this@TextBuilder.strokeColor)
                    setStrokeWidth(this@TextBuilder.strokeWidth)
                }
            }
    }

    private fun createTextSpec(textMeasurer: TextMeasurer): TextSpec {
        return TextSpec(
            label,
            fontface,
            size.toInt(),
            family,
            angle,
            hjust,
            vjust,
            textMeasurer
        )
    }
}