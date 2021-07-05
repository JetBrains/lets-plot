/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Colors
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.PlotUtil
import jetbrains.datalore.plot.common.color.ColorPalette
import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plot.server.config.ServerSideTestUtil
import kotlin.test.Test
import kotlin.test.assertEquals


class ScaleOrderingTest {
    private val myData = """{
        'x'   :  [ "B", "A", "B", "B", "A", "A", "A", "B", "B", "C", "C", "B" ],
        'fill':  [ '4', '2', '3', '3', '2', '3', '1', '1', '3', '4', '2', '2' ],
        'color': [ '1', '0', '2', '1', '1', '2', '1', '1', '0', '2', '0', '0' ]
    }"""
    private val myMappingFill: String = """{ "x": "x", "fill": "fill" }"""
    private val myMappingFillColor = """{ "x": "x", "fill": "fill", "color": "color" }"""

    private fun makePlotSpec(
        annotations: String,
        sampling: String? = null,
        data: String = myData,
        mapping: String = myMappingFill
    ): String {
        return """{
              "kind": "plot",
              "layers": [
                {
                  "data" : $data,
                  "mapping": $mapping,
                  "geom": "bar",
                  "data_meta": { "mapping_annotations": [ $annotations ] },
                  "sampling" : $sampling 
                }
              ]
            }""".trimIndent()
    }

    private fun makeOrderingSettings(aes: String, orderBy: String?, order: Int?): String {
        val orderByVar = if (orderBy != null) {
            "\"" + "$orderBy" + "\""
        } else null
        return """{
                "aes": "$aes",
                "annotation": "as_discrete",
                "parameters": {
                    "order" : $order,
                    "order_by" : $orderByVar
                 }
            }""".trimIndent()
    }

    @Test
    fun default() {
        val geomLayer = buildGeomLayer(makePlotSpec(""))
        assertScaleOrdering(
            geomLayer,
            expectedScaleBreaks = mapOf(
                Aes.X to listOf("B", "C", "A"),
                Aes.FILL to listOf("4", "2", "3", "1")
            ),
            expectedOrderInBar = mapOf(
                Aes.FILL to listOf(
                    listOf("4", "2", "3", "1"),  // B
                    listOf("4", "2"),            // C
                    listOf("2", "3", "1")        // A
                )
            )
        )
    }

    @Test
    fun `order x`() {
        run {
            //ascending
            val orderingSettings = makeOrderingSettings("x", null, 1)
            val geomLayer = buildGeomLayer(makePlotSpec(orderingSettings))
            assertScaleOrdering(
                geomLayer,
                expectedScaleBreaks = mapOf(
                    Aes.X to listOf("A", "B", "C"),
                    Aes.FILL to listOf("4", "2", "3", "1")
                ),
                expectedOrderInBar = mapOf(
                    Aes.FILL to listOf(
                        listOf("2", "3", "1"),       // A
                        listOf("4", "2", "3", "1"),  // B
                        listOf("4", "2")             // C
                    )
                )
            )
        }
        run {
            //descending
            val orderingSettings = makeOrderingSettings("x", null, -1)
            val geomLayer = buildGeomLayer(makePlotSpec(orderingSettings))
            assertScaleOrdering(
                geomLayer,
                expectedScaleBreaks = mapOf(
                    Aes.X to listOf("C", "B", "A"),
                    Aes.FILL to listOf("4", "2", "3", "1")
                ),
                expectedOrderInBar = mapOf(
                    Aes.FILL to listOf(
                        listOf("4", "2"),            // C
                        listOf("4", "2", "3", "1"),  // B
                        listOf("2", "3", "1"),       // A
                    )
                )
            )
        }
    }

    @Test
    fun `order fill`() {
        run {
            //ascending
            val orderingSettings = makeOrderingSettings("fill", null, 1)
            val geomLayer = buildGeomLayer(makePlotSpec(orderingSettings))
            assertScaleOrdering(
                geomLayer,
                expectedScaleBreaks = mapOf(
                    Aes.FILL to listOf("1", "2", "3", "4"),
                    Aes.X to listOf("A", "B", "C")
                ),
                expectedOrderInBar = mapOf(
                    Aes.FILL to listOf(
                        listOf("1", "2", "3"),       // A
                        listOf("1", "2", "3", "4"),  // B
                        listOf("2", "4"),
                    )
                )
            )
        }
        run {
            //descending
            val orderingSettings = makeOrderingSettings("fill", null, -1)
            val geomLayer = buildGeomLayer(makePlotSpec(orderingSettings))
            assertScaleOrdering(
                geomLayer,
                expectedScaleBreaks = mapOf(
                    Aes.FILL to listOf("4", "3", "2", "1"),
                    Aes.X to listOf("B", "C", "A")
                ),
                expectedOrderInBar = mapOf(
                    Aes.FILL to listOf(
                        listOf("4", "3", "2", "1"), // B
                        listOf("4", "2"),           // C
                        listOf("3", "2", "1")       // A
                    )
                )
            )
        }
    }

    @Test
    fun `order x by count`() {
        run {
            //ascending
            val orderingSettings = makeOrderingSettings("x", "..count..", 1)
            val geomLayer = buildGeomLayer(makePlotSpec(orderingSettings))
            assertScaleOrdering(
                geomLayer,
                expectedScaleBreaks = mapOf(
                    Aes.X to listOf("C", "A", "B"),
                    Aes.FILL to listOf("4", "2", "3", "1")
                ),
                expectedOrderInBar = mapOf(
                    Aes.FILL to listOf(
                        listOf("4", "2"),           // C
                        listOf("2", "3", "1"),      // A
                        listOf("4", "2", "3", "1")  // B
                    )
                )
            )
        }
        run {
            //descending
            val orderingSettings = makeOrderingSettings("x", "..count..", -1)
            val geomLayer = buildGeomLayer(makePlotSpec(orderingSettings))
            assertScaleOrdering(
                geomLayer,
                expectedScaleBreaks = mapOf(
                    Aes.X to listOf("B", "A", "C"),
                    Aes.FILL to listOf("4", "2", "3", "1")
                ),
                expectedOrderInBar = mapOf(
                    Aes.FILL to listOf(
                        listOf("4", "2", "3", "1"),  // B
                        listOf("2", "3", "1"),       // A
                        listOf("4", "2")             // C
                    )
                )
            )
        }
    }

    @Test
    fun `order x and fill`() {
        run {
            //x descending - fill ascending
            val orderingSettings =
                makeOrderingSettings("x", null, -1) + "," + makeOrderingSettings("fill", null, 1)
            val geomLayer = buildGeomLayer(makePlotSpec(orderingSettings))
            assertScaleOrdering(
                geomLayer,
                expectedScaleBreaks = mapOf(
                    Aes.X to listOf("C", "B", "A"),
                    Aes.FILL to listOf("1", "2", "3", "4")
                ),
                expectedOrderInBar = mapOf(
                    Aes.FILL to listOf(
                        listOf("2", "4"),           // C
                        listOf("1", "2", "3", "4"), // B
                        listOf("1", "2", "3")       // A
                    )
                )
            )
        }
        run {
            //x descending - fill descending
            val orderingSettings =
                makeOrderingSettings("x", null, -1) + "," + makeOrderingSettings("fill", null, -1)
            val geomLayer = buildGeomLayer(makePlotSpec(orderingSettings))
            assertScaleOrdering(
                geomLayer,
                expectedScaleBreaks = mapOf(
                    Aes.X to listOf("C", "B", "A"),
                    Aes.FILL to listOf("4", "3", "2", "1")
                ),
                expectedOrderInBar = mapOf(
                    Aes.FILL to listOf(
                        listOf("4", "2"),            // C
                        listOf("4", "3", "2", "1"),  // B
                        listOf("3", "2", "1")        // A
                    )
                )
            )
        }
    }

    @Test
    fun `order x by count and fill by itself`() {
        run {
            //ascending
            val orderingSettings =
                makeOrderingSettings("x", "..count..", 1) + "," + makeOrderingSettings("fill", null, 1)
            val geomLayer = buildGeomLayer(makePlotSpec(orderingSettings))
            assertScaleOrdering(
                geomLayer,
                expectedScaleBreaks = mapOf(
                    Aes.X to listOf("C", "A", "B"),
                    Aes.FILL to listOf("1", "2", "3", "4")
                ),
                expectedOrderInBar = mapOf(
                    Aes.FILL to listOf(
                        listOf("2", "4"),            // C
                        listOf("1", "2", "3"),       // A
                        listOf("1", "2", "3", "4")   // B
                    )
                )
            )
        }
        run {
            //descending
            val orderingSettings =
                makeOrderingSettings("x", "..count..", -1) + "," + makeOrderingSettings("fill", null, -1)
            val geomLayer = buildGeomLayer(makePlotSpec(orderingSettings))
            assertScaleOrdering(
                geomLayer,
                expectedScaleBreaks = mapOf(
                    Aes.X to listOf("B", "A", "C"),
                    Aes.FILL to listOf("4", "3", "2", "1")
                ),
                expectedOrderInBar = mapOf(
                    Aes.FILL to listOf(
                        listOf("4", "3", "2", "1"), // B
                        listOf("3", "2", "1"),      // A
                        listOf("4", "2")            // C
                    )
                )
            )
        }
    }

    @Test
    fun `order by fill and color`() {
        val orderingSettings = makeOrderingSettings("fill", null, 1) + "," + makeOrderingSettings("color", null, 1)
        val geomLayer = buildGeomLayer(makePlotSpec(orderingSettings, mapping = myMappingFillColor))
        assertScaleOrdering(
            geomLayer,
            expectedScaleBreaks = mapOf(
                Aes.COLOR to listOf("0", "1", "2"),
                Aes.FILL to listOf("1", "2", "3", "4"),
                Aes.X to listOf("A", "C", "B")
            ),
            expectedOrderInBar = mapOf(
                Aes.FILL to listOf(
                    listOf("2", "1", "2", "3"),             // A
                    listOf("2", "4"),                       // C
                    listOf("2", "3", "1", "3", "4", "3"),   // B
                ),
                Aes.COLOR to listOf(
                    listOf("0", "1", "1", "2"),              // A
                    listOf("0", "2"),                        // C
                    listOf("0", "0", "1", "1", "1", "2"),    // B
                ),
            )
        )
    }


    @Test
    fun `pick sampling`() {
        val samplingPick = """{ "name": "pick", "n": 2 }"""
        run {
            // no ordering
            val geomLayer = buildGeomLayer(makePlotSpec("", samplingPick))
            assertScaleOrdering(
                geomLayer,
                expectedScaleBreaks = mapOf(
                    Aes.X to listOf("B", "C"),
                    Aes.FILL to listOf("4", "2", "3", "1")
                ),
                expectedOrderInBar = mapOf(
                    Aes.FILL to listOf(
                        listOf("4", "2", "3", "1"), // B
                        listOf("4", "2")            // C
                    )
                )
            )
        }
        run {
            // Order x
            val orderingSettings = makeOrderingSettings("x", null, -1)
            val geomLayer = buildGeomLayer(makePlotSpec(orderingSettings, samplingPick))
            assertScaleOrdering(
                geomLayer,
                expectedScaleBreaks = mapOf(
                    Aes.X to listOf("C", "B"),
                    Aes.FILL to listOf("4", "2", "3", "1")
                ),
                expectedOrderInBar = mapOf(
                    Aes.FILL to listOf(
                        listOf("4", "2"),            // C
                        listOf("4", "2", "3", "1")   // B
                    )
                )
            )
        }
        run {
            // Order x and fill
            val orderingSettings =
                makeOrderingSettings("x", null, -1) + "," + makeOrderingSettings("fill", null, 1)
            val geomLayer = buildGeomLayer(makePlotSpec(orderingSettings, samplingPick))
            assertScaleOrdering(
                geomLayer,
                expectedScaleBreaks = mapOf(
                    Aes.X to listOf("C", "B"),
                    Aes.FILL to listOf("1", "2", "3", "4")
                ),
                expectedOrderInBar = mapOf(
                    Aes.FILL to listOf(
                        listOf("2", "4"),            // C
                        listOf("1", "2", "3", "4")   // B
                    )
                )
            )
        }
        run {
            // Order x by count
            val orderingSettings = makeOrderingSettings("x", "..count..", 1)
            val geomLayer = buildGeomLayer(makePlotSpec(orderingSettings, samplingPick))
            assertScaleOrdering(
                geomLayer,
                expectedScaleBreaks = mapOf(
                    Aes.X to listOf("C", "A"),
                    Aes.FILL to listOf("4", "2", "3", "1")
                ),
                expectedOrderInBar = mapOf(
                    Aes.FILL to listOf(
                        listOf("4", "2"),       // C
                        listOf("2", "3", "1")   // A
                    )
                )
            )
        }
    }

    @Test
    fun `apply pick sampling after group sampling`() {
        val samplingGroup = """{ "name": "group_systematic", "n": 2 }"""
        val samplingPick  = """{ "name": "pick", "n": 2 }"""
        val sampling = """{
                 "feature-list": [ 
                    { "sampling": $samplingGroup }, 
                    { "sampling": $samplingPick } 
                 ] 
            }"""

        // After group sampling: B, C, A

        run {
            // No ordering.
            val geomLayer = buildGeomLayer(makePlotSpec("", sampling))
            assertScaleOrdering(
                geomLayer,
                expectedScaleBreaks = mapOf(
                    Aes.X to listOf("B", "C"),
                    Aes.FILL to listOf("4", "1")
                ),
                expectedOrderInBar = mapOf(
                    Aes.FILL to listOf(
                        listOf("4", "1"),   // B
                        listOf("4")         // C
                    )
                )
            )
        }
        run {
            // Order x.
            val orderingSettings = makeOrderingSettings("x", null, 1)
            val geomLayer = buildGeomLayer(makePlotSpec(orderingSettings, sampling))
            assertScaleOrdering(
                geomLayer,
                expectedScaleBreaks = mapOf(
                    Aes.X to listOf("A", "B"),
                    Aes.FILL to listOf("4", "1")
                ),
                expectedOrderInBar = mapOf(
                    Aes.FILL to listOf(
                        listOf("1"),       // A
                        listOf("4", "1")   // B
                    )
                )
            )
        }
    }

    @Test
    fun `group sampling`() {
        val samplingGroup = """{ "name": "group_systematic", "n": 3 }"""

        run {
            // Default - no ordering
            val geomLayer = buildGeomLayer(makePlotSpec("", samplingGroup))
            assertScaleOrdering(
                geomLayer,
                expectedScaleBreaks = mapOf(
                    Aes.X to listOf("B", "C", "A"),
                    Aes.FILL to listOf("4", "3")
                ),
                expectedOrderInBar = mapOf(
                    Aes.FILL to listOf(
                        listOf("4", "3"),   // B
                        listOf("4"),        // C
                        listOf("3")         // A
                    )
                )
            )
        }
        run {
            // Order x
            val orderingSettings = makeOrderingSettings("x", null, 1)
            val geomLayer = buildGeomLayer(makePlotSpec(orderingSettings, samplingGroup))
            assertScaleOrdering(
                geomLayer,
                expectedScaleBreaks = mapOf(
                    Aes.X to listOf("A", "B", "C"),
                    Aes.FILL to listOf("4", "3")
                ),
                expectedOrderInBar = mapOf(
                    Aes.FILL to listOf(
                        listOf("3"),        // A
                        listOf("4", "3"),   // B
                        listOf("4")         // C
                    )
                )
            )
        }
    }

    @Test
    fun `order in the bar and in the legend should be the same`() {
        val data = """{
            'x'   :  [ "A", "A", "A"],
            'fill':  [ "2", "1", "3"]
        }"""
        val orderingSettings = makeOrderingSettings("fill", "..count..", -1)
        val spec = makePlotSpec(orderingSettings, data = data)
        val geomLayer = buildGeomLayer(spec)
        val expectedOrder = listOf("3", "2", "1")
        assertScaleOrdering(
            geomLayer,
            expectedScaleBreaks = mapOf(Aes.FILL to expectedOrder),
            expectedOrderInBar = mapOf(
                Aes.FILL to listOf(expectedOrder)
            )
        )
    }

    @Test
    fun `all null values`() {
        val data = """{
            'x'   :  [ null, null ],
            'fill':  [ null, null ]
        }"""
        val orderingSettings = makeOrderingSettings("fill", null, 1)
        val spec = makePlotSpec(orderingSettings, data = data)
         val geomLayer = buildGeomLayer(spec)
         assertScaleOrdering(
             geomLayer,
             expectedScaleBreaks = mapOf(Aes.FILL to emptyList()),
             expectedOrderInBar = mapOf(
                 Aes.FILL to emptyList()
             )
         )
    }

    @Test
    fun `'order by' variable has null value`() {
        val data = """{
            'x'   :  [ "A", "A",  "A", "A"],
            'fill':  [ "3", null, "1", "2"]
        }"""
        val orderingSettings = makeOrderingSettings("fill", null, 1)
        val spec = makePlotSpec(orderingSettings, data = data)
        val geomLayer = buildGeomLayer(spec)
        assertScaleOrdering(
            geomLayer,
            expectedScaleBreaks = mapOf(Aes.FILL to listOf("1", "2", "3")),
            expectedOrderInBar = mapOf(
                Aes.FILL to listOf(listOf("1", "2", "3", null))
            )
        )
    }

    @Test
    fun `few ordering fields with null values`() {
        val data = """{
            'x'   :  [ "A", "A", "A"],
            'fill':  [ null, "v", null],
            'color': [ '2', null, '1']
        }"""
        val orderingSettings = makeOrderingSettings("fill", null, 1) + "," + makeOrderingSettings("color", null, 1)
        val spec = makePlotSpec(orderingSettings, data = data, mapping = myMappingFillColor)
        val geomLayer = buildGeomLayer(spec)
        assertScaleOrdering(
            geomLayer,
            expectedScaleBreaks = mapOf(
                Aes.COLOR to listOf("1", "2"),
                Aes.FILL to listOf("v")

            ),
            expectedOrderInBar = mapOf(
                Aes.COLOR to listOf(listOf("1", "2", null)),
                Aes.FILL to listOf(listOf(null, null, "v"))
            )
        )
    }

    companion object {
        private fun buildGeomLayer(spec: String): GeomLayer {
            val plotOpts = parsePlotSpec(spec)
            val transformed = ServerSideTestUtil.serverTransformWithoutEncoding(plotOpts)
            val config = PlotConfigClientSide.create(transformed) {}
            return PlotConfigClientSideUtil.createPlotAssembler(config).layersByTile.single().single()
        }

        private fun assertScaleBreaks(
            layer: GeomLayer,
            aes: Aes<*>,
            breaks: List<Any>
        ) {
            val scale = layer.scaleMap[aes]
            assertEquals(breaks, scale.breaks, "Wrong ticks order on ${aes.name.toUpperCase()}.")
        }

        private fun getBarColumnValues(
            geomLayer: GeomLayer,
            valueToColors: Map<Color, Any>,
            colorFactory: (DataPointAesthetics) -> Color?
        ): Map<Int, List<Any?>> {
            val colorInColumns = mutableMapOf<Int, ArrayList<Color>>()
            val aesthetics = PlotUtil.createLayerDryRunAesthetics(geomLayer)
            for (index in 0 until aesthetics.dataPointCount()) {
                val p = aesthetics.dataPointAt(index)
                val x = p.x()!!
                val color = colorFactory(p)!!
                colorInColumns.getOrPut(x.toInt(), ::ArrayList).add(color)
            }
            return colorInColumns.map { (x, values) ->
                x to values.map { color -> valueToColors[color] }
            }.toMap()
        }

        private val legendColors = ColorPalette.Qualitative.Set2.getColors(4).map(Colors::parseColor)

        private fun assertScaleOrdering(
            geomLayer: GeomLayer,
            expectedScaleBreaks: Map<Aes<*>, List<String>>,
            expectedOrderInBar: Map<Aes<*>, List<List<*>>>
        ) {
            expectedScaleBreaks.forEach { (aes, breaks) ->
                assertScaleBreaks(geomLayer, aes, breaks)
            }

            expectedOrderInBar.forEach { (aes, expected) ->
                val breaks = geomLayer.scaleMap[aes].breaks
                val breakColors = breaks.zip(legendColors).map { it.second to it.first }.toMap()
                val actual: Map<Int, List<Any?>> = getBarColumnValues(geomLayer, breakColors) { p: DataPointAesthetics ->
                    if (aes == Aes.FILL) p.fill() else p.color()
                }
                assertEquals(expected.size, actual.size)
                for (i in expected.indices) {
                    assertEquals(expected[i], actual[i], "Wrong color order in ${aes.name.toUpperCase()}.")
                }
            }
        }
    }
}