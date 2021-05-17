/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.PlotUtil
import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plot.server.config.ServerSideTestUtil
import kotlin.test.Test
import kotlin.test.assertEquals

class ScaleOrderingTest {
    private val myData = """{
        'x'   :  [ "B", "A", "B", "B", "A", "A", "A", "B", "B", "C", "C" ],
        'fill':  [ '4', '2', '3', '3', '2', '3', '1', '1', '3', '4', '2' ]
    }"""

    private fun makePlotSpec(
        annotations: String,
        data: String = myData,
        mapping: String = """{ "x": "x", "fill": "fill" }""",
        sampling: String? = null,
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
        assertScaleBreaks(geomLayer, Aes.X, listOf("B", "C", "A"))
        assertScaleBreaks(geomLayer, Aes.FILL, listOf("4", "2", "3", "1"))

        assertOrderInBar(
            geomLayer,
            expected = listOf(
                listOf(COLOR0, COLOR2, COLOR3),    // B : 4, 3, 1
                listOf(COLOR0, COLOR1),            // C : 4, 2
                listOf(COLOR1, COLOR2, COLOR3)     // A : 2, 3, 1
            )
        )
    }

    @Test
    fun `order x`() {
        run {
            //ascending
            val orderingSettings = makeOrderingSettings("x", null, 1)
            val geomLayer = buildGeomLayer(makePlotSpec(orderingSettings))
            assertScaleBreaks(geomLayer, Aes.X, listOf("A", "B", "C"))
            assertScaleBreaks(geomLayer, Aes.FILL, listOf("2", "3", "1", "4"))

            assertOrderInBar(
                geomLayer,
                expected = listOf(
                    listOf(COLOR0, COLOR1, COLOR2), // A : 2, 3, 1
                    listOf(COLOR3, COLOR1, COLOR2), // B : 4, 3, 1
                    listOf(COLOR3, COLOR0)          // C : 4, 2
                )
            )
        }
        run {
            //descending
            val orderingSettings = makeOrderingSettings("x", null, -1)
            val geomLayer = buildGeomLayer(makePlotSpec(orderingSettings))
            assertScaleBreaks(geomLayer, Aes.X, listOf("C", "B", "A"))
            assertScaleBreaks(geomLayer, Aes.FILL, listOf("4", "2", "3", "1"))

            assertOrderInBar(
                geomLayer,
                expected = listOf(
                    listOf(COLOR0, COLOR1),         // C : 4, 2
                    listOf(COLOR0, COLOR2, COLOR3), // B : 4, 3, 1
                    listOf(COLOR1, COLOR2, COLOR3)  // A : 2, 3, 1
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

            assertScaleBreaks(geomLayer, Aes.FILL, listOf("1", "2", "3", "4"))
            assertScaleBreaks(geomLayer, Aes.X, listOf("A", "B", "C"))

            assertOrderInBar(
                geomLayer,
                expected = listOf(
                    listOf(COLOR0, COLOR1, COLOR2),  // A : 1,2,3
                    listOf(COLOR0, COLOR2, COLOR3),  // B : 1,3,4
                    listOf(COLOR1, COLOR3)           // C : 2,4
                )
            )
        }

        run {
            //descending
            val orderingSettings = makeOrderingSettings("fill", null, -1)
            val geomLayer = buildGeomLayer(makePlotSpec(orderingSettings))

            assertScaleBreaks(geomLayer, Aes.FILL, listOf("4", "3", "2", "1"))
            assertScaleBreaks(geomLayer, Aes.X, listOf("B", "C", "A"))

            assertOrderInBar(
                geomLayer,
                expected = listOf(
                    listOf(COLOR0, COLOR1, COLOR3),  // B : 4,3,1
                    listOf(COLOR0, COLOR2),          // C : 4,2
                    listOf(COLOR1, COLOR2, COLOR3)   // A : 3,2,1
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
            assertScaleBreaks(geomLayer, Aes.X, listOf("C", "A", "B"))
            assertScaleBreaks(geomLayer, Aes.FILL, listOf("4", "2", "3", "1"))
            assertOrderInBar(
                geomLayer,
                expected = listOf(
                    listOf(COLOR0, COLOR1),          // C : 4,2
                    listOf(COLOR1, COLOR2, COLOR3),  // A : 2,3,1
                    listOf(COLOR0, COLOR2, COLOR3)   // B : 4,3,1

                )
            )
        }
        run {
            //descending
            val orderingSettings = makeOrderingSettings("x", "..count..", -1)
            val geomLayer = buildGeomLayer(makePlotSpec(orderingSettings))
            assertScaleBreaks(geomLayer, Aes.X, listOf("B", "A", "C"))
            assertScaleBreaks(geomLayer, Aes.FILL, listOf("4", "3", "1", "2"))
            assertOrderInBar(
                geomLayer,
                expected = listOf(
                    listOf(COLOR0, COLOR1, COLOR2),   // B : 4,3,1
                    listOf(COLOR3, COLOR1, COLOR2),   // A : 2,3,1
                    listOf(COLOR0, COLOR3),           // C : 4,2
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

            assertScaleBreaks(geomLayer, Aes.X, listOf("C", "B", "A"))
            assertScaleBreaks(geomLayer, Aes.FILL, listOf("1", "2", "3", "4"))

            assertOrderInBar(
                geomLayer,
                expected = listOf(
                    listOf(COLOR0, COLOR1),          // C : 2,4
                    listOf(COLOR2, COLOR3, COLOR1),  // B : 1,3,4
                    listOf(COLOR2, COLOR0, COLOR3),  // A : 1,2,3
                )
            )
        }
        run {
            //x descending - fill descending
            val orderingSettings =
                makeOrderingSettings("x", null, -1) + "," + makeOrderingSettings("fill", null, -1)
            val geomLayer = buildGeomLayer(makePlotSpec(orderingSettings))

            assertScaleBreaks(geomLayer, Aes.X, listOf("C", "B", "A"))
            assertScaleBreaks(geomLayer, Aes.FILL, listOf("4", "3", "2", "1"))

            assertOrderInBar(
                geomLayer,
                expected = listOf(
                    listOf(COLOR0, COLOR1),          // C : 4, 2
                    listOf(COLOR0, COLOR2, COLOR3),  // B : 4,3,1
                    listOf(COLOR2, COLOR1, COLOR3),  // A : 3,2,1
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
            assertScaleBreaks(geomLayer, Aes.X, listOf("C", "A", "B"))
            assertScaleBreaks(geomLayer, Aes.FILL, listOf("1", "2", "3", "4"))
            assertOrderInBar(
                geomLayer,
                expected = listOf(
                    listOf(COLOR0, COLOR1),          // C : 2,4
                    listOf(COLOR2, COLOR0, COLOR3),  // A : 1,2,3
                    listOf(COLOR2, COLOR3, COLOR1)   // B : 1,3,4
                )
            )
        }
        run {
            //descending
            val orderingSettings =
                makeOrderingSettings("x", "..count..", -1) + "," + makeOrderingSettings("fill", null, -1)
            val geomLayer = buildGeomLayer(makePlotSpec(orderingSettings))
            assertScaleBreaks(geomLayer, Aes.X, listOf("B", "A", "C"))
            assertScaleBreaks(geomLayer, Aes.FILL, listOf("4", "3", "2", "1"))
            assertOrderInBar(
                geomLayer,
                expected = listOf(
                    listOf(COLOR0, COLOR1, COLOR2),  // B : 4,3,1
                    listOf(COLOR1, COLOR3, COLOR2),  // A : 3,2,1
                    listOf(COLOR0, COLOR3)           // C : 4,2
                )
            )
        }
    }

    @Test
    fun `pick sampling`() {
        val sampling = """{ "name": "pick", "n": 2 }"""

        run {
            // no ordering
            val geomLayer = buildGeomLayer(makePlotSpec(annotations = "", sampling = sampling))
            assertScaleBreaks(geomLayer, Aes.X, listOf("B", "C"))
            assertScaleBreaks(geomLayer, Aes.FILL, listOf("4", "2", "3", "1"))

            assertOrderInBar(
                geomLayer,
                expected = listOf(
                    listOf(COLOR0, COLOR2, COLOR3),   // B : 4, 3, 1
                    listOf(COLOR0, COLOR1)            // C : 4, 2
                )
            )
        }
        run {
            // Order fill
            val orderingSettings = makeOrderingSettings("fill", null, 1)
            val geomLayer = buildGeomLayer(makePlotSpec(orderingSettings, sampling = sampling))

            assertScaleBreaks(geomLayer, Aes.FILL, listOf("1", "2", "3", "4"))
            assertScaleBreaks(geomLayer, Aes.X, listOf("A", "B"))

            assertOrderInBar(
                geomLayer,
                expected = listOf(
                    listOf(COLOR0, COLOR1, COLOR2),  // A : 1, 2, 3
                    listOf(COLOR0, COLOR2, COLOR3)   // B : 1, 3, 4
                )
            )
        }

        run {
            // Order x
            val orderingSettings = makeOrderingSettings("x", null, -1)
            val geomLayer = buildGeomLayer(makePlotSpec(orderingSettings, sampling = sampling))

            assertScaleBreaks(geomLayer, Aes.X, listOf("C", "B"))
            assertScaleBreaks(geomLayer, Aes.FILL, listOf("4", "2", "3", "1"))

            assertOrderInBar(
                geomLayer,
                expected = listOf(
                    listOf(COLOR0, COLOR1),          // C : 4, 2
                    listOf(COLOR0, COLOR2, COLOR3)   // B : 4, 3, 1
                )
            )
        }

        run {
            // Order x and fill
            val orderingSettings =
                makeOrderingSettings("x", null, -1) + "," + makeOrderingSettings("fill", null, 1)
            val geomLayer = buildGeomLayer(makePlotSpec(orderingSettings, sampling = sampling))

            assertScaleBreaks(geomLayer, Aes.X, listOf("C", "B"))
            assertScaleBreaks(geomLayer, Aes.FILL, listOf("1", "2", "3", "4"))

            assertOrderInBar(
                geomLayer,
                expected = listOf(
                    listOf(COLOR0, COLOR1),           // C : 2, 4
                    listOf(COLOR2, COLOR3, COLOR1)    // B : 1, 3, 4
                )
            )
        }

        // Order x by count
        run {
            val orderingSettings = makeOrderingSettings("x", "..count..", 1)
            val geomLayer = buildGeomLayer(makePlotSpec(orderingSettings, sampling = sampling))

            assertScaleBreaks(geomLayer, Aes.X, listOf("C", "A"))
            assertScaleBreaks(geomLayer, Aes.FILL, listOf("4", "2", "3", "1"))

            assertOrderInBar(
                geomLayer,
                expected = listOf(
                    listOf(COLOR0, COLOR1),          // C : 4, 2
                    listOf(COLOR1, COLOR2, COLOR3)   // A : 2, 3, 1
                )
            )
        }
    }

    @Test
    fun `group sampling`() {
        val sampling = """{ "name": "group_systematic", "n": 3 }"""
        run {
            // Default - no ordering
            val geomLayer = buildGeomLayer(makePlotSpec(annotations = "", sampling = sampling))
            assertScaleBreaks(geomLayer, Aes.X, listOf("B", "C", "A"))
            assertScaleBreaks(geomLayer, Aes.FILL, listOf("4", "3"))

            assertOrderInBar(
                geomLayer,
                expected = listOf(
                    listOf(COLOR0, COLOR1),    // B : 4, 3
                    listOf(COLOR0),            // C : 4
                    listOf(COLOR1)             // A : 3
                )
            )
        }

        run {
            // Order fill
            val orderingSettings = makeOrderingSettings("fill", null, 1)
            val geomLayer = buildGeomLayer(makePlotSpec(orderingSettings, sampling = sampling))

            assertScaleBreaks(geomLayer, Aes.FILL, listOf("1", "3"))
            assertScaleBreaks(geomLayer, Aes.X, listOf("A", "B"))

            assertOrderInBar(
                geomLayer,
                expected = listOf(
                    listOf(COLOR0, COLOR1),    // A : 1, 3
                    listOf(COLOR0, COLOR1),    // B : 1, 3
                )
            )
        }

        run {
            // Order x
            val orderingSettings = makeOrderingSettings("x", null, 1)
            val geomLayer = buildGeomLayer(makePlotSpec(orderingSettings, sampling = sampling))

            assertScaleBreaks(geomLayer, Aes.X, listOf("A", "B", "C"))
            assertScaleBreaks(geomLayer, Aes.FILL, listOf("3", "4"))

            assertOrderInBar(
                geomLayer,
                expected = listOf(
                    listOf(COLOR0),             // A : 3
                    listOf(COLOR1, COLOR0),     // B : 4, 3
                    listOf(COLOR1)              // C : 4
                )
            )
        }

        run {
            // Order x (descending) and fill (ascending)
            val orderingSettings =
                makeOrderingSettings("x", null, -1) + "," + makeOrderingSettings("fill", null, 1)
            val geomLayer = buildGeomLayer(makePlotSpec(orderingSettings, sampling = sampling))

            assertScaleBreaks(geomLayer, Aes.X, listOf("B", "A"))
            assertScaleBreaks(geomLayer, Aes.FILL, listOf("1", "3"))

            assertOrderInBar(
                geomLayer,
                expected = listOf(
                    listOf(COLOR0, COLOR1),    // B : 1, 3
                    listOf(COLOR0, COLOR1)     // A : 1, 3
                )
            )
        }

        run {
            // Order x by count
            val orderingSettings = makeOrderingSettings("x", "..count..", -1)
            val geomLayer = buildGeomLayer(makePlotSpec(orderingSettings, sampling = sampling))

            assertScaleBreaks(geomLayer, Aes.X, listOf("B", "C", "A"))
            assertScaleBreaks(geomLayer, Aes.FILL, listOf("4", "3"))

            assertOrderInBar(
                geomLayer,
                expected = listOf(
                    listOf(COLOR0, COLOR1),    // B : 4, 3
                    listOf(COLOR0),            // C : 4
                    listOf(COLOR1)             // A : 3
                )
            )
        }
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
            assertEquals(breaks, scale.breaks)
        }

        private const val COLOR0 = "ORDER0"
        private const val COLOR1 = "ORDER1"
        private const val COLOR2 = "ORDER2"
        private const val COLOR3 = "ORDER3"

        private fun getBarColumns(geomLayer: GeomLayer): List<List<Any?>> {
            val orderValues = mutableMapOf<Any?, Int>()
            fun getOrderVal(prop: Any?): String {
                if (orderValues[prop] == null) {
                    orderValues[prop] = orderValues.size
                }
                return "ORDER${orderValues[prop]}"
            }

            val fillOrders = mutableMapOf<Double?, ArrayList<Any>>()

            val aesthetics = PlotUtil.createLayerDryRunAesthetics(geomLayer)
            for (index in 0 until aesthetics.dataPointCount()) {
                val p = aesthetics.dataPointAt(index)
                val x = p.x()
                val fill = p.fill()
                fillOrders.getOrPut(x, ::ArrayList).add(getOrderVal(fill))
            }
            return fillOrders.map(Map.Entry<Double?, ArrayList<Any>>::value)
        }

        private fun assertOrderInBar(geomLayer: GeomLayer, expected: List<List<Any>>) {
            val actual = getBarColumns(geomLayer)
            assertEquals(expected.size, actual.size)
            for (i in expected.indices) {
                assertEquals(expected[i], actual[i])
            }
        }
    }
}