/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.api

import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.FontStyle
import org.jetbrains.letsPlot.core.canvas.FontWeight
import org.jetbrains.letsPlot.livemap.Client
import org.jetbrains.letsPlot.livemap.chart.ChartElementComponent
import org.jetbrains.letsPlot.livemap.chart.TextSpecComponent
import org.jetbrains.letsPlot.livemap.chart.text.TextRenderer
import org.jetbrains.letsPlot.livemap.chart.text.TextSpec
import org.jetbrains.letsPlot.livemap.core.ecs.EcsEntity
import org.jetbrains.letsPlot.livemap.core.ecs.addComponents
import org.jetbrains.letsPlot.livemap.core.graphics.TextMeasurer
import org.jetbrains.letsPlot.livemap.core.layers.LayerKind
import org.jetbrains.letsPlot.livemap.mapengine.LayerEntitiesComponent
import org.jetbrains.letsPlot.livemap.mapengine.RenderableComponent
import org.jetbrains.letsPlot.livemap.mapengine.placement.ScreenDimensionComponent
import org.jetbrains.letsPlot.livemap.mapengine.placement.WorldOriginComponent

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
    var point: Vec<LonLat> = LonLat.ZERO_VEC

    var sizeScalingRange: ClosedRange<Int>? = null
    var alphaScalingEnabled: Boolean = false

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

    var nudgeClient: Vec<Client> = Vec(0.0, 0.0)
    var enableNudgeScaling = false

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

        return myFactory.createStaticFeatureWithLocation("map_ent_s_text", point)
            .setInitializer { worldPoint ->
                +RenderableComponent().apply {
                    renderer = TextRenderer()
                }
                +ChartElementComponent().apply {
                    sizeScalingRange = this@TextEntityBuilder.sizeScalingRange
                    alphaScalingEnabled = this@TextEntityBuilder.alphaScalingEnabled

                    fillColor = this@TextEntityBuilder.fillColor
                    strokeColor = this@TextEntityBuilder.strokeColor
                    strokeWidth = this@TextEntityBuilder.strokeWidth
                    lineheight = this@TextEntityBuilder.lineheight

                    nudgeClient = this@TextEntityBuilder.nudgeClient
                    enableNudgeScaling = this@TextEntityBuilder.enableNudgeScaling
                }
                +TextSpecComponent().apply { this.textSpec = textSpec }
                +WorldOriginComponent(worldPoint)
                +ScreenDimensionComponent().apply {
                    dimension = textSpec.dimension
                }
            }
    }

}