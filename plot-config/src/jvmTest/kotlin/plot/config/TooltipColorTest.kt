/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.interact.ContextualMapping
import jetbrains.datalore.plot.base.interact.GeomTarget
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.interact.TooltipSpec
import jetbrains.datalore.plot.builder.interact.TooltipSpecFactory
import jetbrains.datalore.plot.config.Option.Layer.GEOM
import jetbrains.datalore.plot.config.Option.Layer.TOOLTIPS
import jetbrains.datalore.plot.config.Option.Layer.TOOLTIP_COLOR
import jetbrains.datalore.plot.config.Option.Layer.TOOLTIP_LINES
import jetbrains.datalore.plot.config.Option.Meta
import jetbrains.datalore.plot.config.Option.Plot.LAYERS
import jetbrains.datalore.plot.config.Option.PlotBase.MAPPING
import jetbrains.datalore.plot.server.config.ServerSideTestUtil
import kotlin.test.Test
import kotlin.test.assertEquals

class TooltipColorTest {

    @Test
    fun `default tooltip color will be used`() {
        val layer = buildGeomPointLayer(tooltipColor = null)
        assertGeneralTooltipColor(layer, DEFAULT_COLOR)
    }

    @Test
    fun `override the default tooltip color`() {
        val layer = buildGeomPointLayer(tooltipColor = "red")
        assertGeneralTooltipColor(layer, Color.RED)
    }

    companion object {
        val DEFAULT_COLOR = Color.BLACK

        private fun buildGeomPointLayer(
            tooltipColor: String?
        ): GeomLayer {
            val tooltips = mapOf(
                TOOLTIP_LINES to listOf("text"),
                TOOLTIP_COLOR to tooltipColor
            )
            val plotOpts = mutableMapOf(
                Meta.KIND to Meta.Kind.PLOT,
                MAPPING to mapOf(Aes.X.name to listOf(1.0), Aes.Y.name to listOf(1.0)),
                LAYERS to listOf(
                    mapOf(
                        GEOM to Option.GeomName.POINT,
                        TOOLTIPS to tooltips
                    )
                )
            )
            val transformed = ServerSideTestUtil.serverTransformWithoutEncoding(plotOpts)
            val config = PlotConfigClientSide.create(transformed) {}
            return PlotConfigClientSideUtil.createPlotAssembler(config).layersByTile.single().single()
        }

        private fun createTooltipSpecs(contextualMapping: ContextualMapping): List<TooltipSpec> {
            val factory = TooltipSpecFactory(contextualMapping, DoubleVector.ZERO)
            return factory.create(
                GeomTarget(
                    hitIndex = 0,
                    tipLayoutHint = TipLayoutHint.cursorTooltip(DoubleVector.ZERO, DEFAULT_COLOR),
                    aesTipLayoutHints = emptyMap()
                )
            )
        }

        private fun assertGeneralTooltipColor(layer: GeomLayer, expectedColor: Color) {
            val tooltipSpecs = createTooltipSpecs(layer.contextualMapping)
            tooltipSpecs
                .filterNot(TooltipSpec::isOutlier)
                .forEach { tooltipSpec -> assertEquals(expectedColor, tooltipSpec.fill) }
        }
    }
}