/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.FontStyle
import jetbrains.datalore.vis.canvas.FontWeight
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.chart.TextSpecComponent
import jetbrains.livemap.chart.text.TextRenderer
import jetbrains.livemap.chart.text.TextSpec
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.graphics.TextMeasurer
import jetbrains.livemap.core.layers.LayerKind
import jetbrains.livemap.mapengine.LayerEntitiesComponent
import jetbrains.livemap.mapengine.RenderableComponent
import jetbrains.livemap.mapengine.placement.ScreenDimensionComponent
import jetbrains.livemap.mapengine.placement.WorldOriginComponent

@LiveMapDsl
class TextLayerBuilder(
    val factory: FeatureEntityFactory,
    val textMeasurer: TextMeasurer
)

fun FeatureLayerBuilder.texts(block: TextLayerBuilder.() -> Unit) {
    val layerEntity = myComponentManager
        .createEntity("map_layer_text")
        .addComponents {
            + layerManager.addLayer("livemap_text", LayerKind.FEATURES)
            + LayerEntitiesComponent()
        }

    TextLayerBuilder(
        FeatureEntityFactory(layerEntity, 500),
        textMeasurer
    ).apply(block)
}

fun TextLayerBuilder.text(block: TextEntityBuilder.() -> Unit) {
    TextEntityBuilder(factory)
        .apply(block)
        .build(textMeasurer)
}

@LiveMapDsl
class TextEntityBuilder(
    private val myFactory: FeatureEntityFactory
) {
    var index: Int = 0
    var point: Vec<LonLat>? = null

    var fillColor: Color = Color.TRANSPARENT
    var strokeColor: Color = Color.BLACK
    var strokeWidth: Double = 0.0

    var drawBorder: Boolean = false

    // label parameters
    var labelPadding: Double = 0.25
    var labelRadius: Double = 0.15
    var labelSize: Double = 1.0

    var label: String = ""
    var fontStyle: FontStyle = FontStyle.NORMAL
    var fontWeight: FontWeight = FontWeight.NORMAL
    var size: Double = 10.0
    var family: String = "Arial"
    var hjust: Double = 0.0
    var vjust: Double = 0.0
    var angle: Double = 0.0
    var lineheight: Double = 1.0

    fun build(
        textMeasurer: TextMeasurer
    ): EcsEntity {
        val textSpec = TextSpec(
            label,
            fontStyle,
            fontWeight,
            size.toInt(),
            family,
            angle,
            hjust,
            vjust,
            textMeasurer,
            drawBorder,
            labelPadding,
            labelRadius,
            labelSize,
            lineheight
        )

        return when {
            point != null -> myFactory.createStaticFeatureWithLocation("map_ent_s_text", point!!)
            else -> error("Can't create text entity. Coord is null.")
        }
            .setInitializer { worldPoint ->
                +RenderableComponent().apply {
                    renderer = TextRenderer()
                }
                +ChartElementComponent().apply {
                    fillColor = this@TextEntityBuilder.fillColor
                    strokeColor = this@TextEntityBuilder.strokeColor
                    strokeWidth = this@TextEntityBuilder.strokeWidth
                    lineheight = this@TextEntityBuilder.lineheight
                }
                +TextSpecComponent().apply { this.textSpec = textSpec }
                +WorldOriginComponent(worldPoint)
                +ScreenDimensionComponent().apply {
                    dimension = textSpec.dimension
                }
            }
    }

}