/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipLineSpec.DataPoint
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.assemble.TestingPlotContext
import org.jetbrains.letsPlot.core.plot.builder.tooltip.spec.TooltipSpec.Line
import jetbrains.datalore.plot.config.Option.Layer.DISABLE_SPLITTING
import jetbrains.datalore.plot.config.Option.Layer.GEOM
import jetbrains.datalore.plot.config.Option.Layer.TOOLTIPS
import jetbrains.datalore.plot.config.Option.Plot.LAYERS
import jetbrains.datalore.plot.config.Option.PlotBase.DATA
import jetbrains.datalore.plot.config.Option.PlotBase.MAPPING
import jetbrains.datalore.plot.config.Option.LinesSpec.FORMATS
import jetbrains.datalore.plot.config.Option.LinesSpec.LINES
import jetbrains.datalore.plot.config.Option.LinesSpec.TITLE
import jetbrains.datalore.plot.config.Option.LinesSpec.VARIABLES
import jetbrains.datalore.plot.config.Option.LinesSpec.Format.FIELD
import jetbrains.datalore.plot.config.Option.LinesSpec.Format.FORMAT
import jetbrains.datalore.plot.config.Option.Meta.KIND
import jetbrains.datalore.plot.config.Option.Meta.Kind.PLOT
import jetbrains.datalore.plot.config.TestUtil.buildGeomLayer
import jetbrains.datalore.plot.config.TestUtil.buildPointLayer
import org.jetbrains.letsPlot.core.spec.back.ServerSideTestUtil
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class TooltipConfigTest {

    private val data = mapOf(
        "displ" to listOf(1.6),
        "hwy" to listOf(160.0),
        "cty" to listOf(15.0),
        "year" to listOf(1998),
        "class" to listOf("suv"),
        "origin" to listOf("US"),
        "model name" to listOf("dodge")
    )
    private val mapping = mapOf(
        org.jetbrains.letsPlot.core.plot.base.Aes.X.name to "displ",
        org.jetbrains.letsPlot.core.plot.base.Aes.Y.name to "hwy",
        org.jetbrains.letsPlot.core.plot.base.Aes.COLOR.name to "cty",
        org.jetbrains.letsPlot.core.plot.base.Aes.SHAPE.name to "class"
    )

    @Test
    fun defaultTooltips() {
        val geomLayer = buildPointLayer(data, mapping, tooltips = null)

        val expectedLines = listOf(
            "cty: 15.00"
        )
        val lines = getGeneralTooltipStrings(geomLayer)
        assertTooltipStrings(expectedLines, lines)

        val axisTooltips = getAxisTooltips(geomLayer)
        assertEquals(2, axisTooltips.size, "Wrong number of axis tooltips")
    }

    @Test
    fun hideTooltips() {
        val geomLayer = buildPointLayer(data, mapping, tooltips = "none")

        val lines = getGeneralTooltipStrings(geomLayer)
        assertTooltipStrings(emptyList(), lines)

        val axisTooltips = getAxisTooltips(geomLayer)
        assertEquals(0, axisTooltips.size, "Wrong number of axis tooltips")
    }

    @Test
    fun useNamesToConfigTooltipLines() {
        val myData = data + mapOf(
            "shape" to listOf("shape"),
            "foo^" to listOf("foo"),
            "^bar" to listOf("bar"),
            "foo^bar" to listOf("foobar"),
            "foo@" to listOf("foo with @"),
            "@bar" to listOf("bar with @"),
            "foo@bar" to listOf("foobar with @")
        )
        val tooltipConfig = mapOf(
            LINES to listOf(
                "^shape",           // aes
                "@shape",            // variable with name that match to the name of aes
                "\\^shape",         // '^' is part of the string (it's not the name) => string is "^shape"
                "\\@shape",          // '@' is part of the string (it's not the name) => string is "@shape"
                "@{model name}",     // space in the name
                "@foo^",             // with '^' at the end
                "@^bar",            // with '^' at the beginning
                "@foo^bar",         // with '^' at the middle
                "@foo@",             // with '@' at the end
                "@@bar",             // with '@' at the beginning
                "@foo@bar",          // with '@' at the middle
                "@foo^\\^",          // the second '^' is part of the result string
                "(@foo^)",          // with brackets as part of the result string
                "^shape, ^shape"   // result string comma separated
            )
        )
        val geomLayer = buildPointLayer(myData, mapping, tooltips = tooltipConfig)

        val expectedLines = listOf(
            "suv",
            "shape",
            "^shape",
            "@shape",
            "dodge",
            "foo",
            "bar",
            "foobar",
            "foo with @",
            "bar with @",
            "foobar with @",
            "foo^",
            "(foo)",
            "suv, suv"
        )
        val lines = getGeneralTooltipStrings(geomLayer)
        assertTooltipStrings(expectedLines, lines)
    }

    @Test
    fun changeFormatForTheDefault() {
        val tooltipConfig = mapOf(
            FORMATS to listOf(
                mapOf(
                    FIELD to "^color",
                    FORMAT to ".4f"         // number format
                )
            )
        )
        val geomLayer = buildPointLayer(data, mapping, tooltips = tooltipConfig)

        val expectedLines = listOf(
            "cty: 15.0000"
        )
        val lines = getGeneralTooltipStrings(geomLayer)
        assertTooltipStrings(expectedLines, lines)
    }

    @Test
    fun changeFormatForTheAes() {
        val mappingWithColor = mapOf(
            org.jetbrains.letsPlot.core.plot.base.Aes.X.name to "displ",
            org.jetbrains.letsPlot.core.plot.base.Aes.Y.name to "hwy",
            org.jetbrains.letsPlot.core.plot.base.Aes.COLOR.name to "year"
        )

        // default
        val defaultGeomLayer = buildPointLayer(data, mappingWithColor, tooltips = null)
        assertTooltipStrings(
            listOf(
                "year: 1,998.00"
            ),
            getGeneralTooltipStrings(defaultGeomLayer)
        )

        // redefine format for the 'color' aes
        val geomLayerWithAesInTooltip = buildPointLayer(
            data, mappingWithColor, tooltips = mapOf(
                FORMATS to listOf(
                    mapOf(
                        FIELD to "^color",
                        FORMAT to "{d}"
                    )
                )
            )
        )
        assertTooltipStrings(
            listOf("year: 1998"),
            getGeneralTooltipStrings(geomLayerWithAesInTooltip)
        )

        // redefine format for the 'year' variable
        val geomLayerWithVarInTooltip = buildPointLayer(
            data, mappingWithColor, tooltips = mapOf(
                LINES to listOf("@|@year"),
                FORMATS to listOf(
                    mapOf(
                        FIELD to "year",
                        FORMAT to "{d}"
                    )
                )
            )
        )
        assertTooltipStrings(
            listOf("year: 1998"),
            getGeneralTooltipStrings(geomLayerWithVarInTooltip)
        )
    }

    @Test
    fun configLabels() {
        val tooltipConfig = mapOf(
            LINES to listOf(
                "@{model name}",             // no label
                "|@{model name}",            // empty label
                "@|@{model name}",           // default = the variable name
                "the model|@{model name}"    // specified
            )
        )
        val geomLayer = buildPointLayer(data, mapping, tooltips = tooltipConfig)

        val expectedLines = listOf(
            Line.withLabelAndValue(label = null, "dodge"),
            Line.withLabelAndValue("", "dodge"),
            Line.withLabelAndValue("model name", "dodge"),
            Line.withLabelAndValue("the model", "dodge")
        )
        assertTooltipLines(expectedLines, getGeneralTooltipLines(geomLayer))
    }

    @Test
    fun userComplicatedTooltipLines() {
        val tooltipConfig = mapOf(
            LINES to listOf(
                "mpg data set info",             // static text
                "^color (mpg)",                 // formatted
                "@{model name} car (@origin)",   // multiple sources in the line
                "x/y|^x x ^y"                  // specify label
            ),
            FORMATS to listOf(          //define formats
                mapOf(FIELD to "^color", FORMAT to ".1f"),
                mapOf(FIELD to "^x", FORMAT to ".3f"),
                mapOf(FIELD to "^y", FORMAT to ".1f")
            )
        )
        val geomLayer = buildPointLayer(data, mapping, tooltips = tooltipConfig)

        val expectedLines = listOf(
            "mpg data set info",
            "15.0 (mpg)",
            "dodge car (US)",
            "x/y: 1.600 x 160.0"
        )
        val lines = getGeneralTooltipStrings(geomLayer)
        assertTooltipStrings(expectedLines, lines)
    }

    // Side tooltips
    private val boxPlotData = mapOf(
        "x" to List(6) { 'A' },
        "y" to listOf(4.2, 11.5, 7.3, 5.8, 6.4, 10.0)
    )

    @Test
    fun defaultSideTooltips() {
        val geomLayer = buildGeomLayer(
            geom = "boxplot",
            data = boxPlotData,
            mapping = mapOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.X.name to "x",
                org.jetbrains.letsPlot.core.plot.base.Aes.Y.name to "y"
            ),
            tooltips = null
        )
        val expectedLines = mapOf(
            org.jetbrains.letsPlot.core.plot.base.Aes.YMAX to "11.5",
            org.jetbrains.letsPlot.core.plot.base.Aes.UPPER to "8.7",
            org.jetbrains.letsPlot.core.plot.base.Aes.MIDDLE to "6.9",
            org.jetbrains.letsPlot.core.plot.base.Aes.LOWER to "6.1",
            org.jetbrains.letsPlot.core.plot.base.Aes.YMIN to "4.2"
        )
        val lines = getSideTooltips(geomLayer)

        assertEquals(expectedLines.size, lines.size, "Wrong number of side tooltips")
        for (aes in lines.keys) {
            assertEquals(expectedLines[aes], lines[aes], "Wrong line for ${aes.name} in the side tooltips")
        }
    }

    @Test
    fun configSideTooltips() {
        val tooltipConfig = mapOf(
            LINES to listOf(
                "lower/upper|^lower, ^upper"
            ),
            FORMATS to listOf(
                mapOf(
                    FIELD to "^Y",
                    FORMAT to ".1f"
                ),       // all positionals
                mapOf(FIELD to "^middle", FORMAT to ".3f"),    // number format
                mapOf(FIELD to "^ymax", FORMAT to "{.1f}")     // line pattern
            )
        )

        val geomLayer = buildGeomLayer(
            geom = "boxplot",
            data = boxPlotData,
            mapping = mapOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.X.name to "x",
                org.jetbrains.letsPlot.core.plot.base.Aes.Y.name to "y"
            ),
            tooltips = tooltipConfig
        )

        // upper, lower will be in the general tooltip and will be removed from side tooltips;
        // side tooltips - without label
        val expectedLines = mapOf(
            org.jetbrains.letsPlot.core.plot.base.Aes.YMAX to "11.5",
            org.jetbrains.letsPlot.core.plot.base.Aes.MIDDLE to "6.850",
            org.jetbrains.letsPlot.core.plot.base.Aes.YMIN to "4.2"
        )
        val generalExpectedLine = "lower/upper: 6.1, 8.7"

        val sideTooltips = getSideTooltips(geomLayer)
        assertEquals(expectedLines.size, sideTooltips.size, "Wrong number of side tooltips")
        for (aes in sideTooltips.keys) {
            assertEquals(expectedLines[aes], sideTooltips[aes], "Wrong line for ${aes.name} in the side tooltip")
        }

        val generalLines = getGeneralTooltipStrings(geomLayer)
        assertTooltipStrings(listOf(generalExpectedLine), generalLines)
    }

    @Test
    fun `'disable_splitting' moves side tooltips to general tooltip`() {
        val geomLayer = buildGeomLayer(
            geom = "boxplot",
            data = boxPlotData,
            mapping = mapOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.X.name to "x",
                org.jetbrains.letsPlot.core.plot.base.Aes.Y.name to "y"
            ),
            tooltips = mapOf(DISABLE_SPLITTING to true)
        )

        assertEquals(0, getSideTooltips(geomLayer).size, "Wrong number of side tooltips")

        val generalExpectedLine = listOf(
            "y max: 11.5",
            "upper: 8.7",
            "middle: 6.9",
            "lower: 6.1",
            "y min: 4.2"
        )
        val generalLines = getGeneralTooltipStrings(geomLayer)
        assertTooltipStrings(generalExpectedLine, generalLines)
    }

    @Test
    fun `'disable_splitting' with specified lines hides side tooltips`() {
        val geomLayer = buildGeomLayer(
            geom = "boxplot",
            data = boxPlotData,
            mapping = mapOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.X.name to "x",
                org.jetbrains.letsPlot.core.plot.base.Aes.Y.name to "y"
            ),
            tooltips = mapOf(
                LINES to listOf("tooltip line"),
                DISABLE_SPLITTING to true
            )
        )

        assertEquals(0, getSideTooltips(geomLayer).size, "Wrong number of side tooltips")

        val generalExpectedLine = listOf("tooltip line")
        val generalLines = getGeneralTooltipStrings(geomLayer)
        assertTooltipStrings(generalExpectedLine, generalLines)
    }

    @Test
    fun `numeric format for non-numeric value will be ignored`() {
        val tooltipConfig = mapOf(
            LINES to listOf(
                "class is @class"
            ),
            FORMATS to listOf(
                mapOf(
                    FIELD to "class",
                    FORMAT to ".2f"
                )
            )
        )
        val geomLayer = buildPointLayer(data, mapping, tooltips = tooltipConfig)
        val expectedLines = listOf(
            "class is suv"
        )
        assertTooltipStrings(expectedLines, getGeneralTooltipStrings(geomLayer))
    }

    @Test
    fun `geom_text by default should not show tooltip`() {
        val geomTextLayer = buildGeomLayer(
            geom = "text",
            data = mapOf("label" to listOf("my text")),
            mapping = mapOf(org.jetbrains.letsPlot.core.plot.base.Aes.LABEL.name to "label"),
            tooltips = null
        )
        assertTooltipStrings(
            expected = emptyList(),
            actual = getGeneralTooltipStrings(geomTextLayer)
        )
    }

    @Test
    fun `geom_text should support tooltips configuration`() {
        val geomTextLayer = buildGeomLayer(
            geom = "text",
            data = mapOf("label" to listOf("my text")),
            mapping = mapOf(org.jetbrains.letsPlot.core.plot.base.Aes.LABEL.name to "label"),
            tooltips = mapOf(
                LINES to listOf("^label")
            )
        )
        assertTooltipStrings(
            expected = listOf("my text"),
            actual = getGeneralTooltipStrings(geomTextLayer)
        )
    }

    @Test
    fun `geom_text by default should not show axis tooltips`() {
        val geomTextLayer = buildGeomLayer(
            geom = "text",
            data = mapOf(
                "x" to listOf(1.0),
                "y" to listOf(1.0),
                "label" to listOf("my text")
            ),
            mapping = mapOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.X.name to "x",
                org.jetbrains.letsPlot.core.plot.base.Aes.Y.name to "y",
                org.jetbrains.letsPlot.core.plot.base.Aes.LABEL.name to "label"
            ),
            tooltips = null
        )
        assertTooltipStrings(
            expected = emptyList(),
            actual = getGeneralTooltipStrings(geomTextLayer)
        )
        val axisTooltips = getAxisTooltips(geomTextLayer)
        assertEquals(0, axisTooltips.size, "Wrong number of axis tooltips")
    }

    @Test
    fun `geom_text should add axis tooltips if tooltip lines are defined`() {
        val geomTextLayer = buildGeomLayer(
            geom = "text",
            data = mapOf(
                "x" to listOf(1.0),
                "y" to listOf(1.0),
                "label" to listOf("my text")
            ),
            mapping = mapOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.X.name to "x",
                org.jetbrains.letsPlot.core.plot.base.Aes.Y.name to "y",
                org.jetbrains.letsPlot.core.plot.base.Aes.LABEL.name to "label"
            ),
            tooltips = mapOf(
                LINES to listOf("^label")
            )
        )
        assertTooltipStrings(
            expected = listOf("my text"),
            actual = getGeneralTooltipStrings(geomTextLayer)
        )
        val axisTooltips = getAxisTooltips(geomTextLayer)
        assertEquals(2, axisTooltips.size, "Wrong number of axis tooltips")
    }

    @Test
    fun `wrong tooltip format (no arguments)`() {
        assertFailTooltipSpec(
            tooltipConfig = mapOf(FORMATS to listOf(emptyMap<String, String>())),
            expectedMessage = "Invalid 'format' arguments: 'field' and 'format' are expected"
        )
    }

    @Test
    fun `wrong tooltip format (list instead of map)`() {
        assertFailTooltipSpec(
            tooltipConfig = mapOf(
                FORMATS to listOf(
                    listOf(
                        FIELD to "^color",
                        FORMAT to ".2f"
                    )
                )
            ),
            expectedMessage = "Wrong 'format' arguments"
        )
    }

    @Test
    fun `wrong tooltip format (wrong number of arguments)`() {
        assertFailTooltipSpec(
            tooltipConfig = mapOf(
                FORMATS to listOf(
                    mapOf(
                        FIELD to "^color",
                        FORMAT to "{.2f} {.2f}"
                    )
                )
            ),
            expectedMessage = "Wrong number of arguments in pattern '{.2f} {.2f}' to format 'color'. Expected 1 argument instead of 2"
        )
    }

    private fun assertFailTooltipSpec(
        tooltipConfig: Any?,
        expectedMessage: String
    ) {
        val plotOpts = mutableMapOf(
            KIND to PLOT,
            DATA to data,
            MAPPING to mapping,
            LAYERS to listOf(
                mapOf(
                    GEOM to "point",
                    TOOLTIPS to tooltipConfig
                )
            )
        )
        val plotSpec = ServerSideTestUtil.backendSpecTransform(plotOpts.toMutableMap())

        assertTrue(PlotConfig.isFailure(plotSpec))
        assertEquals(expectedMessage, PlotConfig.getErrorMessage(plotSpec))
    }

    @Test
    fun `tooltip with strings similar to number format`() {
        val tooltipConfig = mapOf(
            LINES to listOf(
                "^x",   // aes x
                "x",    // static text "x"
                "\$x",  // static text "$x"
                ".1f"   // static text ".1f"
            )
        )
        val geomLayer = buildPointLayer(data, mapping, tooltips = tooltipConfig)
        val expectedLines = listOf(
            "1.60",
            "x",
            "\$x",
            ".1f"
        )
        val lines = getGeneralTooltipStrings(geomLayer)
        assertTooltipStrings(expectedLines, lines)
    }

    @Test
    fun `use the 'group' in tooltips`() {
        val geomLayer = buildPointLayer(
            data = mapOf(
                "x" to listOf(1),
                "y" to listOf(1),
                "id" to listOf("a")
            ),
            mapping = mapOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.X.name to "x",
                org.jetbrains.letsPlot.core.plot.base.Aes.Y.name to "y",
                "group" to "id"
            ),
            tooltips = mapOf(
                LINES to listOf("^group")
            )
        )
        assertTooltipStrings(
            expected = listOf("a"),
            actual = getGeneralTooltipStrings(geomLayer)
        )
    }

    @Test
    fun `specified tooltip format for variable should be applied to the aes`() {
        val tooltipConfig = mapOf(
            FORMATS to listOf(
                mapOf(
                    FIELD to "cty",
                    FORMAT to "{.1f} (mpg)"
                )
            )
        )
        val geomLayer = buildPointLayer(data, mapping, tooltips = tooltipConfig)

        val expectedLines = listOf(
            "cty: 15.0 (mpg)"
        )
        val lines = getGeneralTooltipStrings(geomLayer)
        assertTooltipStrings(expectedLines, lines)
    }

    // Variable list tests

    @Test
    fun `variable list to place in a tooltip`() {
        val tooltipConfig = mapOf(
            VARIABLES to listOf(
                "model name",
                "class",
                "origin"
            )
        )
        val geomLayer = buildPointLayer(data, mapping, tooltips = tooltipConfig)
        val expectedLines = listOf(
            "model name: dodge",
            "class: suv",
            "origin: US"
        )
        val lines = getGeneralTooltipStrings(geomLayer)
        assertTooltipStrings(expectedLines, lines)
    }

    @Test
    fun `tooltip lines should be formed by variable list and line functions`() {
        val tooltipConfig = mapOf(
            VARIABLES to listOf(
                "model name",
                "class"
            ),
            LINES to listOf(
                "@|@origin"
            )
        )
        val geomLayer = buildPointLayer(data, mapping, tooltips = tooltipConfig)
        val expectedLines = listOf(
            "model name: dodge",
            "class: suv",
            "origin: US"
        )
        val lines = getGeneralTooltipStrings(geomLayer)
        assertTooltipStrings(expectedLines, lines)
    }

    @Test
    fun `variable from the list without specified format() should use aes formatting - default or specified`() {
        run {
            // use default aes formatting

            val tooltipConfig = mapOf(
                VARIABLES to listOf(
                    "cty"
                )
            )
            val geomLayer = buildPointLayer(data, mapping, tooltips = tooltipConfig)
            val expectedLines = listOf(
                "cty: 15.00"
            )
            val lines = getGeneralTooltipStrings(geomLayer)
            assertTooltipStrings(expectedLines, lines)
        }
        run {
            // use specified aes formatting

            val tooltipConfig = mapOf(
                VARIABLES to listOf(
                    "cty"
                ),
                FORMATS to listOf(
                    mapOf(
                        FIELD to "^color",
                        FORMAT to ".4f"
                    )
                )
            )
            val geomLayer = buildPointLayer(data, mapping, tooltips = tooltipConfig)
            val expectedLines = listOf(
                "cty: 15.0000"
            )
            val lines = getGeneralTooltipStrings(geomLayer)
            assertTooltipStrings(expectedLines, lines)
        }
    }

    @Test
    fun `variable from the 'line' without specified format() should use aes formatting - default or specified`() {
        run {
            // use default aes formatting
            val tooltipConfig = mapOf(
                LINES to listOf(
                    "@cty"
                )
            )
            val geomLayer = buildPointLayer(data, mapping, tooltips = tooltipConfig)
            val expectedLines = listOf(
                "15.00"
            )
            val lines = getGeneralTooltipStrings(geomLayer)
            assertTooltipStrings(expectedLines, lines)
        }
        run {
            // use specified aes formatting
            val tooltipConfig = mapOf(
                LINES to listOf(
                    "@cty"
                ),
                FORMATS to listOf(
                    mapOf(
                        FIELD to "^color",
                        FORMAT to ".4f"
                    )
                )
            )
            val geomLayer = buildPointLayer(data, mapping, tooltips = tooltipConfig)
            val expectedLines = listOf(
                "15.0000"
            )
            val lines = getGeneralTooltipStrings(geomLayer)
            assertTooltipStrings(expectedLines, lines)
        }
    }

    @Test
    fun `variable should always choose own formatting if it's specified`() {
        run {
            // variable list
            val tooltipConfig = mapOf(
                VARIABLES to listOf(
                    "cty"
                ),
                FORMATS to listOf(
                    mapOf(
                        FIELD to "cty",
                        FORMAT to "{.1f} (var format)"
                    ),
                    mapOf(
                        FIELD to "^color",
                        FORMAT to "{.1f} (aes format)"
                    )
                )
            )
            val geomLayer = buildPointLayer(data, mapping, tooltips = tooltipConfig)
            val expectedLines = listOf(
                "cty: 15.0 (var format)"
            )
            val lines = getGeneralTooltipStrings(geomLayer)
            assertTooltipStrings(expectedLines, lines)
        }
        run {
            // in line function
            val tooltipConfig = mapOf(
                LINES to listOf(
                    "@|@cty"
                ),
                FORMATS to listOf(
                    mapOf(
                        FIELD to "cty",
                        FORMAT to "{.1f} (var format)"
                    ),
                    mapOf(
                        FIELD to "^color",
                        FORMAT to "{.1f} (aes format)"
                    )
                )
            )
            val geomLayer = buildPointLayer(data, mapping, tooltips = tooltipConfig)
            val expectedLines = listOf(
                "cty: 15.0 (var format)"
            )
            val lines = getGeneralTooltipStrings(geomLayer)
            assertTooltipStrings(expectedLines, lines)
        }
    }

    @Test
    fun `variable mapped to two aes will choose aes formatting - specified or first`() {
        val curMapping = mapOf(
            org.jetbrains.letsPlot.core.plot.base.Aes.X.name to "displ",
            org.jetbrains.letsPlot.core.plot.base.Aes.Y.name to "hwy",
            org.jetbrains.letsPlot.core.plot.base.Aes.COLOR.name to "cty",
            org.jetbrains.letsPlot.core.plot.base.Aes.SHAPE.name to "cty"
        )

        run {
            val tooltipConfig = mapOf(
                LINES to listOf(
                    "@|@cty"
                )
            )
            val geomLayer = buildPointLayer(data, curMapping, tooltips = tooltipConfig)
            val expectedLines = listOf(
                "cty: 15.00"
            )
            val lines = getGeneralTooltipStrings(geomLayer)
            assertTooltipStrings(expectedLines, lines)
        }
        run {
            // use color formatting
            val tooltipConfig = mapOf(
                LINES to listOf(
                    "@|@cty"
                ),
                FORMATS to listOf(
                    mapOf(
                        FIELD to "^color",
                        FORMAT to "{.1f} (color format)"
                    )
                )
            )
            val geomLayer = buildPointLayer(data, curMapping, tooltips = tooltipConfig)
            val expectedLines = listOf(
                "cty: 15.0 (color format)"
            )
            val lines = getGeneralTooltipStrings(geomLayer)
            assertTooltipStrings(expectedLines, lines)
        }
        run {
            // use shape formatting
            val tooltipConfig = mapOf(
                LINES to listOf(
                    "@|@cty"
                ),
                FORMATS to listOf(
                    mapOf(
                        FIELD to "^shape",
                        FORMAT to "{.1f} (shape format)"
                    )
                )
            )
            val geomLayer = buildPointLayer(data, curMapping, tooltips = tooltipConfig)
            val expectedLines = listOf(
                "cty: 15.0 (shape format)"
            )
            val lines = getGeneralTooltipStrings(geomLayer)
            assertTooltipStrings(expectedLines, lines)
        }
        run {
            // should choose the first alphabetically
            val tooltipConfig = mapOf(
                LINES to listOf(
                    "@|@cty"
                ),
                FORMATS to listOf(
                    mapOf(
                        FIELD to "^color",
                        FORMAT to "{.1f} (color format)"
                    ),
                    mapOf(
                        FIELD to "^shape",
                        FORMAT to "{.1f} (shape format)"
                    )
                )
            )
            val geomLayer = buildPointLayer(data, curMapping, tooltips = tooltipConfig)
            val expectedLines = listOf(
                "cty: 15.0 (color format)"
            )
            val lines = getGeneralTooltipStrings(geomLayer)
            assertTooltipStrings(expectedLines, lines)
        }
    }

    @Test
    fun `use variable mapped to the aes consumed by stat`() {
        val geomLayer = buildGeomLayer(
            geom = Option.GeomName.PIE,
            data = mapOf(
                "name" to listOf("a"),
                "value" to listOf(1)
            ),
            mapping = mapOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.FILL.name to "name",
                org.jetbrains.letsPlot.core.plot.base.Aes.WEIGHT.name to "value",
            ),
            tooltips = mapOf(
                LINES to listOf("@value")
            )
        )
        assertTooltipStrings(
            expected = listOf("1.0"),
            actual = getGeneralTooltipStrings(geomLayer)
        )
    }

    @Test
    fun `use aes consumed by stat in tooltip - will be ignored`() {
        val geomLayer = buildGeomLayer(
            geom = Option.GeomName.PIE,
            data = mapOf(
                "name" to listOf("a"),
                "value" to listOf(1)
            ),
            mapping = mapOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.FILL.name to "name",
                org.jetbrains.letsPlot.core.plot.base.Aes.WEIGHT.name to "value",
            ),
            tooltips = mapOf(
                LINES to listOf("^weight")
            )
        )
        assertTooltipStrings(
            expected = emptyList(),
            actual = getGeneralTooltipStrings(geomLayer)
        )
    }

    @Test
    fun `format() should understand DateTime format`() {
        val geomLayer = buildPointLayer(
            data = mapOf(
                "x" to listOf(1609459200000),
                "y" to listOf(0.0)
            ),
            mapping = mapOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.X.name to "x",
                org.jetbrains.letsPlot.core.plot.base.Aes.Y.name to "y",
            ),
            tooltips = mapOf(
                LINES to listOf("^x"),
                FORMATS to listOf(
                    mapOf(
                        FIELD to "^x",
                        FORMAT to "%d.%m.%y"
                    )
                )
            )
        )
        assertTooltipStrings(
            expected = listOf("01.01.21"),
            actual = getGeneralTooltipStrings(geomLayer)
        )
    }

    @Test
    fun `tooltip format() should be applied to axis tooltips`() {
        val formats = listOf(
            mapOf(FIELD to "^x", FORMAT to "x = {.1f}"), //  x-axis tooltip
            mapOf(FIELD to "^Y", FORMAT to "y = {.1f}")  //  all positionals including y-axis tooltip
        )
        val expected = listOf("x = 1.6", "y = 160.0")

        run {
            val tooltipConfig = mapOf(FORMATS to formats)
            val geomLayer = buildPointLayer(data, mapping, tooltips = tooltipConfig)
            val axisTooltips = getAxisTooltips(geomLayer).map(DataPoint::value)
            assertEquals(expected.size, axisTooltips.size, "Wrong number of axis tooltips")
            for (index in expected.indices) {
                assertEquals(expected[index], axisTooltips[index])
            }
        }
        run {
            // with specified lines
            val tooltipConfig = mapOf(
                LINES to listOf("@class"),
                FORMATS to formats
            )
            val geomLayer = buildPointLayer(data, mapping, tooltips = tooltipConfig)
            val axisTooltips = getAxisTooltips(geomLayer).map(DataPoint::value)
            assertEquals(expected.size, axisTooltips.size, "Wrong number of axis tooltips")
            for (index in expected.indices) {
                assertEquals(expected[index], axisTooltips[index])
            }
        }
    }

    @Test
    fun `add title to default lines`() {
        val tooltipConfig = mapOf(
            TITLE to "@{model name} car (@origin)"
        )
        val geomLayer = buildPointLayer(data, mapping, tooltips = tooltipConfig)
        val expectedLines = listOf(
            "dodge car (US)",
            "cty: 15.00"
        )
        val lines = getGeneralTooltipStrings(geomLayer).toMutableList().also { lines ->
            getTitleString(geomLayer)?.let { title -> lines.add(0, title) }
        }
        assertTooltipStrings(expectedLines, lines)
    }

    @Test
    fun `add title to users lines`() {
        val tooltipConfig = mapOf(
            LINES to listOf("^color (mpg)"),
            TITLE to "@{model name} car (@origin)"
        )
        val geomLayer = buildPointLayer(data, mapping, tooltips = tooltipConfig)
        val expectedLines = listOf(
            "dodge car (US)",
            "15.00 (mpg)"
        )
        val lines = getGeneralTooltipStrings(geomLayer).toMutableList().also { lines ->
            getTitleString(geomLayer)?.let { title -> lines.add(0, title) }
        }
        assertTooltipStrings(expectedLines, lines)
    }

    @Test
    fun `last specified title should be used`() {
        val tooltipConfig = mapOf(
            TITLE to "title 1",
            LINES to listOf("^color (mpg)"),
            TITLE to "title 2"
        )
        val geomLayer = buildPointLayer(data, mapping, tooltips = tooltipConfig)
        val expectedLines = listOf(
            "title 2",
            "15.00 (mpg)"
        )
        val lines = getGeneralTooltipStrings(geomLayer).toMutableList().also { lines ->
            getTitleString(geomLayer)?.let { title -> lines.add(0, title) }
        }
        assertTooltipStrings(expectedLines, lines)
    }

    @Test
    fun `issue579 - geom_boxplot(aes(alphabet, coeff))`() {
        // MIDDLE, LOWER, YMIN were formatted with exponential notation, while YMAX and UPPER were formatted
        // with regular number format

        val geomLayer = buildGeomLayer(
            geom = Option.GeomName.BOX_PLOT,
            data = mapOf<String, List<Any>>(
                "alphabet" to listOf("a", "a", "b", "a", "a", "a", "b", "b", "b", "a", "a", "a"),
                "coeff" to listOf(0.119, 0.289, 0.387, 0.491, 0.588, 0.694, 0.791, 0.888, 0.994, 0.0191, 0.988, 0.994)
            ),
            mapping = mapOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.X.name to "alphabet",
                org.jetbrains.letsPlot.core.plot.base.Aes.Y.name to "coeff"
            )
        )

        val expected = mapOf(
            org.jetbrains.letsPlot.core.plot.base.Aes.YMAX to "0.99",
            org.jetbrains.letsPlot.core.plot.base.Aes.UPPER to "0.84",
            org.jetbrains.letsPlot.core.plot.base.Aes.MIDDLE to "0.54",
            org.jetbrains.letsPlot.core.plot.base.Aes.LOWER to "0.20",
            org.jetbrains.letsPlot.core.plot.base.Aes.YMIN to "0.02",
        )
        val ctx = TestingPlotContext.create(geomLayer)
        geomLayer.createContextualMapping().getDataPoints(0, ctx).filter { it.isSide && !it.isAxis }.forEach {
            assertEquals(expected[it.aes], it.value, "Wrong tooltip for ${it.aes}")
        }
    }

    @Test
    fun `issue579 - geom_boxplot(aes(coeff, alphabet), orientation=y)`() {
        // MIDDLE, LOWER, YMIN were formatted with exponential notation, while YMAX and UPPER were formatted
        // with regular number format

        val geomLayer = buildGeomLayer(
            geom = Option.GeomName.BOX_PLOT,
            data = mapOf<String, List<Any>>(
                "alphabet" to listOf("a", "a", "b", "a", "a", "a", "b", "b", "b", "a", "a", "a"),
                "coeff" to listOf(0.119, 0.289, 0.387, 0.491, 0.588, 0.694, 0.791, 0.888, 0.994, 0.0191, 0.988, 0.994)
            ),
            mapping = mapOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.X.name to "coeff",
                org.jetbrains.letsPlot.core.plot.base.Aes.Y.name to "alphabet",
            ),
            orientationY = true
        )

        val expected = mapOf(
            org.jetbrains.letsPlot.core.plot.base.Aes.YMAX to "0.99",
            org.jetbrains.letsPlot.core.plot.base.Aes.UPPER to "0.84",
            org.jetbrains.letsPlot.core.plot.base.Aes.MIDDLE to "0.54",
            org.jetbrains.letsPlot.core.plot.base.Aes.LOWER to "0.20",
            org.jetbrains.letsPlot.core.plot.base.Aes.YMIN to "0.02",
        )
        val ctx = TestingPlotContext.create(geomLayer)
        geomLayer.createContextualMapping().getDataPoints(0, ctx).filter { it.isSide && !it.isAxis }.forEach {
            assertEquals(expected[it.aes], it.value, "Wrong tooltip for ${it.aes}")
        }
    }

    @Test
    fun `issue579 - geom_boxplot(aes(coeff), orientation=y)`() {
        // MIDDLE, LOWER, YMIN were formatted with exponential notation, while YMAX and UPPER were formatted
        // with regular number format

        val geomLayer = buildGeomLayer(
            geom = Option.GeomName.BOX_PLOT,
            data = mapOf<String, List<Any>>(
                "alphabet" to listOf("a", "a", "b", "a", "a", "a", "b", "b", "b", "a", "a", "a"),
                "coeff" to listOf(0.119, 0.289, 0.387, 0.491, 0.588, 0.694, 0.791, 0.888, 0.994, 0.0191, 0.988, 0.994)
            ),
            mapping = mapOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.X.name to "coeff"
            ),
            orientationY = true
        )

        val expected = mapOf(
            org.jetbrains.letsPlot.core.plot.base.Aes.YMAX to "0.99",
            org.jetbrains.letsPlot.core.plot.base.Aes.UPPER to "0.94",
            org.jetbrains.letsPlot.core.plot.base.Aes.MIDDLE to "0.64",
            org.jetbrains.letsPlot.core.plot.base.Aes.LOWER to "0.34",
            org.jetbrains.letsPlot.core.plot.base.Aes.YMIN to "0.02",
        )
        val ctx = TestingPlotContext.create(geomLayer)
        geomLayer.createContextualMapping().getDataPoints(0, ctx).filter { it.isSide && !it.isAxis }.forEach {
            assertEquals(expected[it.aes], it.value, "Wrong tooltip for ${it.aes}")
        }
    }

    companion object {
        private fun getTitleString(geomLayer: GeomLayer): String? {
            val ctx = TestingPlotContext.create(geomLayer)
            return geomLayer.createContextualMapping().getTitle(index = 0, ctx)
        }

        private fun getGeneralTooltipStrings(geomLayer: GeomLayer): List<String> {
            return getGeneralTooltipLines(geomLayer).map(Line::toString)
        }

        private fun getGeneralTooltipLines(geomLayer: GeomLayer): List<Line> {
            val ctx = TestingPlotContext.create(geomLayer)
            val dataPoints = geomLayer.createContextualMapping().getDataPoints(index = 0, ctx)
            return dataPoints.filterNot(DataPoint::isSide).map { Line.withLabelAndValue(it.label, it.value) }
        }

        private fun getAxisTooltips(geomLayer: GeomLayer): List<DataPoint> {
            val ctx = TestingPlotContext.create(geomLayer)
            val dataPoints = geomLayer.createContextualMapping().getDataPoints(index = 0, ctx)
            return dataPoints.filter(DataPoint::isAxis)
        }

        private fun getSideTooltips(geomLayer: GeomLayer): Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, String> {
            val ctx = TestingPlotContext.create(geomLayer)
            val dataPoints = geomLayer.createContextualMapping().getDataPoints(index = 0, ctx)
            return dataPoints.filter { it.isSide && !it.isAxis }.associateBy({ it.aes!! }, { it.value })
        }

        private fun assertTooltipStrings(expected: List<String>, actual: List<String>) {
            assertEquals(expected.size, actual.size, "Wrong number of lines in the general tooltip")
            for (index in expected.indices) {
                assertEquals(expected[index], actual[index], "Wrong line #$index in the general tooltip")
            }
        }

        private fun assertTooltipLines(expectedLines: List<Line>, actualLines: List<Line>) {
            assertEquals(expectedLines.size, actualLines.size, "Wrong number of lines in the general tooltip")
            for (index in expectedLines.indices) {
                assertEquals(
                    expectedLines[index].label,
                    actualLines[index].label,
                    "Wrong label in line #$index in the general tooltip"
                )
                assertEquals(
                    expectedLines[index].value,
                    actualLines[index].value,
                    "Wrong value in line #$index in the general tooltip"
                )
            }
        }
    }
}