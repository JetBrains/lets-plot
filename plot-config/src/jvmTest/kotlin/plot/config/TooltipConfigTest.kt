/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.interact.ValueSource
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.server.config.ServerSideTestUtil
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

        val axisTooltips = getAxisTooltips(geomLayer)
        assertEquals(2, axisTooltips.size, "Wrong number of axis tooltips")
    }

    @Test
    fun resetTooltips() {
        val geomLayer = buildGeomPointLayer(data, mapping, tooltips = "none")

        val lines = getGeneralTooltipLines(geomLayer)
        assertTooltipLines(emptyList(), lines)

        val axisTooltips = getAxisTooltips(geomLayer)
        assertEquals(0, axisTooltips.size, "Wrong number of axis tooltips")
    }

    @Test
    fun useNamesToConfigTooltipLines() {
        val myData = data + mapOf(
            "shape" to listOf("shape"),
            "foo$" to listOf("foo"),
            "\$bar" to listOf("bar"),
            "foo\$bar" to listOf("foobar")
        )
        val tooltipConfig = mapOf(
            Option.Layer.TOOLTIP_LINES to listOf(
                "\$shape",              // aes
                "\$var@shape",          // variable with name that match to the name of aes
                "\${var@model name}",   // space in the name
                "\$var@foo$",           // with '$' at the end
                "\$var@\$bar",          // with '$' at the beginning
                "\$var@foo\$bar"        // with '$' at the middle
            )
        )
        val geomLayer = buildGeomPointLayer(myData, mapping, tooltips = tooltipConfig)

        val expectedLines = listOf(
            "suv",
            "shape",
            "dodge",
            "foo",
            "bar",
            "foobar"
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

    // Outliers
    private val boxPlotData = mapOf(
        "x" to List(6) { 'A' },
        "y" to listOf(4.2, 11.5, 7.3, 5.8, 6.4, 10.0)
    )

    @Test
    fun defaultOutlierTooltips() {
        val geomLayer = buildGeomLayer(
            geom = "boxplot",
            data = boxPlotData,
            mapping = mapOf(
                Aes.X.name to "x",
                Aes.Y.name to "y"
            ),
            tooltips = null
        )
        val expectedLines = mapOf(
            Aes.YMAX to "y max: 11.50",
            Aes.UPPER to "upper: 8.65",
            Aes.MIDDLE to "middle: 6.85",
            Aes.LOWER to "lower: 6.10",
            Aes.YMIN to "y min: 4.20"
        )
        val lines = getOutlierLines(geomLayer)

        assertEquals(expectedLines.size, lines.size, "Wrong number of outlier tooltips")
        for (aes in lines.keys) {
            assertEquals(expectedLines[aes], lines[aes], "Wrong line for ${aes.name} in the outliers")
        }
    }

    @Test
    fun configOutlierTooltips() {
        val tooltipConfig = mapOf(
            Option.Layer.TOOLTIP_LINES to listOf(
                "min|\$ymin",
                "\$middle",
                "max|\$ymax"
            ),
            Option.Layer.TOOLTIP_FORMATS to mapOf(
                "\$ymin" to ".1f",
                "\$middle" to ".4f",
                "\$ymax" to ".1f"
            )
        )

        val geomLayer = buildGeomLayer(
            geom = "boxplot",
            data = boxPlotData,
            mapping = mapOf(
                Aes.X.name to "x",
                Aes.Y.name to "y"
            ),
            tooltips = tooltipConfig
        )
        val expectedLines = mapOf(
            Aes.YMAX to "max: 11.5",
            Aes.UPPER to "upper: 8.65",
            Aes.MIDDLE to "6.8500",
            Aes.LOWER to "lower: 6.10",
            Aes.YMIN to "min: 4.2"
        )
        val lines = getOutlierLines(geomLayer)

        assertEquals(expectedLines.size, lines.size, "Wrong number of outlier tooltips")
        for (aes in lines.keys) {
            assertEquals(expectedLines[aes], lines[aes], "Wrong line for ${aes.name} in the outliers")
        }
    }


    companion object {

        private fun buildGeomPointLayer(
            data: Map<String, Any?>,
            mapping: Map<String, String>,
            tooltips: Any?
        ): GeomLayer {
            return buildGeomLayer(Option.GeomName.POINT, data, mapping, tooltips)
        }

        private fun buildGeomLayer(
            geom: String,
            data: Map<String, Any?>,
            mapping: Map<String, String>,
            tooltips: Any?
        ): GeomLayer {
            val plotOpts = mutableMapOf(
                Option.PlotBase.DATA to data,
                Option.PlotBase.MAPPING to mapping,
                Option.Plot.LAYERS to listOf(
                    mapOf(
                        Option.Layer.GEOM to geom,
                        Option.Layer.TOOLTIPS to tooltips
                    )
                )
            )
            val transformed = ServerSideTestUtil.serverTransformWithoutEncoding(plotOpts)
            return PlotConfigClientSideUtil.createPlotAssembler(transformed).layersByTile.single().single()
        }

        private fun getGeneralTooltipLines(geomLayer: GeomLayer): List<String> {
            val dataPoints = geomLayer.contextualMapping.getDataPoints(index = 0)
            return dataPoints.filterNot(ValueSource.DataPoint::isOutlier).map(ValueSource.DataPoint::line)
        }

        private fun getAxisTooltips(geomLayer: GeomLayer): List<ValueSource.DataPoint> {
            val dataPoints = geomLayer.contextualMapping.getDataPoints(index = 0)
            return dataPoints.filter(ValueSource.DataPoint::isAxis)
        }

        private fun getOutlierLines(geomLayer: GeomLayer): Map<Aes<*>, String> {
            val dataPoints = geomLayer.contextualMapping.getDataPoints(index = 0)
            return dataPoints.filter { it.isOutlier && !it.isAxis }.associateBy({ it.aes!! }, { it.line })
        }

        private fun assertTooltipLines(expectedLines: List<String>, actualLines: List<String>) {
            assertEquals(expectedLines.size, actualLines.size, "Wrong number of lines in the general tooltip")
            for (index in expectedLines.indices) {
                assertEquals(expectedLines[index], actualLines[index], "Wrong line #$index in the general tooltip")
            }
        }
    }
}