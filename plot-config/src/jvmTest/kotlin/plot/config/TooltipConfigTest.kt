/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.interact.ValueSource
import jetbrains.datalore.plot.builder.GeomLayer
import kotlin.test.Test
import kotlin.test.assertEquals


class TooltipConfigTest {

    private val data = mapOf(
        "displ" to listOf(1.6),
        "hwy" to listOf(160.0),
        "cty" to listOf(15.0),
        "class" to listOf("suv"),
        "origin" to listOf("US"),
        "model name" to listOf("dodge")
    )
    private val mapping = mapOf(
        Aes.X.name to "displ",
        Aes.Y.name to "hwy",
        Aes.COLOR.name to "cty",
        Aes.SHAPE.name to "class"
    )


    @Test
    fun defaultTooltips() {
        val geomLayer = buildGeomPointLayer(data, mapping, tooltips = null)

        val expectedLines = listOf(
            "cty: 15.00",
            "class: suv"
        )
        val lines = getGeneralTooltipLines(geomLayer)
        assertTooltipLines(expectedLines, lines)
    }

    @Test
    fun resetTooltips() {
        val geomLayer = buildGeomPointLayer(data, mapping, tooltips = "none")

        val lines = getGeneralTooltipLines(geomLayer)
        assertTooltipLines(emptyList(), lines)
    }

    @Test
    fun useNamesToConfigTooltipLines() {
        val myData = data + mapOf(
            "shape" to listOf("var shape value"),
            "variable$" to listOf("variable\$ value"),
            "\$variable" to listOf("\$variable value"),
            "vari\$able" to listOf("vari\$able value")
        )
        val tooltipConfig = mapOf(
            Option.Layer.TOOLTIP_LINES to listOf(
                "\$shape",                 // aes
                "\$var@shape",             // variable with name that match to the name of aes
                "\${var@model name}",      // space in the name
                "\$var@variable$",         // with '$' at the end
                "\$var@\$variable",        // with '$' at the beginning
                "\$var@vari\$able"         // with '$' at the middle
            )
        )
        val geomLayer = buildGeomPointLayer(myData, mapping, tooltips = tooltipConfig)

        val expectedLines = listOf(
            "suv",
            "var shape value",
            "dodge",
            "variable\$ value",
            "\$variable value",
            "vari\$able value"
        )
        val lines = getGeneralTooltipLines(geomLayer)
        assertTooltipLines(expectedLines, lines)
    }


    @Test
    fun changeFormatForTheDefault() {
        val tooltipConfig = mapOf(
            Option.Layer.TOOLTIP_FORMATS to mapOf(
                "\$color" to ".4f"
            )
        )
        val geomLayer = buildGeomPointLayer(data, mapping, tooltips = tooltipConfig)

        val expectedLines = listOf(
            "cty: 15.0000",
            "class: suv"
        )
        val lines = getGeneralTooltipLines(geomLayer)
        assertTooltipLines(expectedLines, lines)
    }

    @Test
    fun configLabels() {
        val tooltipConfig = mapOf(
            Option.Layer.TOOLTIP_LINES to listOf(
                "\${var@model name}",             // no label
                "|\${var@model name}",            // empty label (now it's the same as 'no label')
                "@|\${var@model name}",           // default = the variable name
                "the model|\${var@model name}"    // specified
            )
        )
        val geomLayer = buildGeomPointLayer(data, mapping, tooltips = tooltipConfig)

        val expectedLines = listOf(
            "dodge",
            "dodge",
            "model name: dodge",
            "the model: dodge"
        )
        val lines = getGeneralTooltipLines(geomLayer)
        assertTooltipLines(expectedLines, lines)
    }

    @Test
    fun userComplicatedTooltipLines() {
        val tooltipConfig = mapOf(
            Option.Layer.TOOLTIP_LINES to listOf(
                "mpg data set info",                        // static text
                "\$color (mpg)",                            // formatted
                "\${var@model name} car (\${var@origin})",  // multiple sources in the line
                "x/y|\$x x \$y"                             // specify label
            ),
            Option.Layer.TOOLTIP_FORMATS to mapOf(          //define formats
                "\$color" to ".1f",
                "\$x" to ".3f",
                "\$y" to ".1f"
            )
        )
        val geomLayer = buildGeomPointLayer(data, mapping, tooltips = tooltipConfig)

        val expectedLines = listOf(
            "mpg data set info",
            "15.0 (mpg)",
            "dodge car (US)",
            "x/y: 1.600 x 160.0"
        )
        val lines = getGeneralTooltipLines(geomLayer)
        assertTooltipLines(expectedLines, lines)
    }

    companion object {

        private fun buildGeomPointLayer(
            data: Map<String, Any?>,
            mapping: Map<String, String>,
            tooltips: Any?
        ): GeomLayer {
            val plotOpts = mapOf(
                Option.PlotBase.DATA to data,
                Option.PlotBase.MAPPING to mapping,
                Option.Plot.LAYERS to listOf(
                    mapOf(
                        Option.Layer.GEOM to Option.GeomName.POINT,
                        Option.Layer.TOOLTIPS to tooltips
                    )
                )
            )
            return PlotConfigClientSideUtil.createPlotAssembler(plotOpts).layersByTile.single().single()
        }

        private fun getGeneralTooltipLines(geomLayer: GeomLayer): List<String> {
            val dataPoints = geomLayer.contextualMapping.getDataPoints(index = 0)
            return dataPoints.filterNot(ValueSource.DataPoint::isOutlier).map(ValueSource.DataPoint::line)
        }

        private fun assertTooltipLines(expectedLines: List<String>, actualLines: List<String>) {
            assertEquals(expectedLines.size, actualLines.size, "Wrong number of lines in the general tooltip")
            for (index in expectedLines.indices) {
                assertEquals(expectedLines[index], actualLines[index], "Wrong line #$index in the general tooltip")
            }
        }
    }
}