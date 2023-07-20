/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Colors
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.PlotUtil
import org.jetbrains.letsPlot.core.commons.color.ColorPalette
import jetbrains.datalore.plot.config.TestUtil.getSingleGeomLayer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull


class ScaleOrderingTest {
    private val myData = """{
        'x'   :  [ "B", "A", "B", "B", "A", "A", "A", "B", "B", "C", "C", "B", "C" ],
        'fill':  [ '4', '2', '3', '3', '2', '3', '1', '1', '3', '4', '2', '2', '2' ],
        'color': [ '1', '0', '2', '1', '1', '2', '1', '1', '0', '2', '0', '0', '0' ]
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
        val geomLayer = getSingleGeomLayer(makePlotSpec(""))
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
            val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings))
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
            val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings))
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
            val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings))
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
                        listOf("2", "4"),            // C
                    )
                )
            )
        }
        run {
            //descending
            val orderingSettings = makeOrderingSettings("fill", null, -1)
            val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings))
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
            val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings))
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
            val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings))
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
            val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings))
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
            val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings))
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
            val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings))
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
            val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings))
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
        val orderingSettings = makeOrderingSettings("color", null, 1) + "," + makeOrderingSettings("fill", null, 1)
        val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings, mapping = myMappingFillColor))
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
            val geomLayer = getSingleGeomLayer(makePlotSpec("", samplingPick))
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
            val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings, samplingPick))
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
            val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings, samplingPick))
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
            val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings, samplingPick))
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
        val samplingPick = """{ "name": "pick", "n": 2 }"""
        val sampling = """{
                 "feature-list": [ 
                    { "sampling": $samplingGroup }, 
                    { "sampling": $samplingPick } 
                 ] 
            }"""

        // After group sampling: B, C, A

        run {
            // No ordering.
            val geomLayer = getSingleGeomLayer(makePlotSpec("", sampling))
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
            val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings, sampling))
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
            val geomLayer = getSingleGeomLayer(makePlotSpec("", samplingGroup))
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
            val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings, samplingGroup))
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
        val geomLayer = getSingleGeomLayer(spec)
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
        val geomLayer = getSingleGeomLayer(spec)
        assertScaleOrdering(
            geomLayer,
            expectedScaleBreaks = mapOf(Aes.FILL to null),
            expectedOrderInBar = mapOf(Aes.FILL to null)
        )
    }

    @Test
    fun `'order by' variable has null value`() {
        val data = """{
            'x'   :  [ "A", "A",  "A", "A"],
            'fill':  [ "3", null, "1", "2"]
        }"""
        run {
            //ascending
            val orderingSettings = makeOrderingSettings("fill", null, 1)
            val spec = makePlotSpec(orderingSettings, data = data)
            val geomLayer = getSingleGeomLayer(spec)
            assertScaleOrdering(
                geomLayer,
                expectedScaleBreaks = mapOf(Aes.FILL to listOf("1", "2", "3")),
                expectedOrderInBar = mapOf(
                    Aes.FILL to listOf(listOf("1", "2", "3", null))
                )
            )
        }
        run {
            //descending
            val orderingSettings = makeOrderingSettings("fill", null, -1)
            val spec = makePlotSpec(orderingSettings, data = data)
            val geomLayer = getSingleGeomLayer(spec)
            assertScaleOrdering(
                geomLayer,
                expectedScaleBreaks = mapOf(Aes.FILL to listOf("3", "2", "1")),
                expectedOrderInBar = mapOf(
                    Aes.FILL to listOf(listOf("3", "2", "1", null))
                )
            )
        }
    }

    @Test
    fun `few ordering fields with null values`() {
        val data = """{
            'x'   :  [ "A", "A", "A"],
            'fill':  [ null, "v", null],
            'color': [ '2', null, '1']
        }"""
        run {
            // color ascending
            val orderingSettings = makeOrderingSettings("color", null, 1) + "," + makeOrderingSettings("fill", null, 1)
            val spec = makePlotSpec(orderingSettings, data = data, mapping = myMappingFillColor)
            val geomLayer = getSingleGeomLayer(spec)
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
        run {
            // color descending
            val orderingSettings = makeOrderingSettings("color", null, -1) + "," + makeOrderingSettings("fill", null, 1)
            val spec = makePlotSpec(orderingSettings, data = data, mapping = myMappingFillColor)
            val geomLayer = getSingleGeomLayer(spec)
            assertScaleOrdering(
                geomLayer,
                expectedScaleBreaks = mapOf(
                    Aes.COLOR to listOf("2", "1"),
                    Aes.FILL to listOf("v")

                ),
                expectedOrderInBar = mapOf(
                    Aes.COLOR to listOf(listOf("2", "1", null)),
                    Aes.FILL to listOf(listOf(null, null, "v"))
                )
            )
        }
        run {
            // color ascending
            val orderingSettings = makeOrderingSettings("fill", null, 1) + "," + makeOrderingSettings("color", null, 1)
            val spec = makePlotSpec(orderingSettings, data = data, mapping = myMappingFillColor)
            val geomLayer = getSingleGeomLayer(spec)
            assertScaleOrdering(
                geomLayer,
                expectedScaleBreaks = mapOf(
                    Aes.COLOR to listOf("1", "2"),
                    Aes.FILL to listOf("v")

                ),
                expectedOrderInBar = mapOf(
                    Aes.FILL to listOf(listOf("v", null, null)),
                    Aes.COLOR to listOf(listOf(null, "1", "2"))
                )
            )
        }
        run {
            // color descending
            val orderingSettings = makeOrderingSettings("fill", null, 1) + "," + makeOrderingSettings("color", null, -1)
            val spec = makePlotSpec(orderingSettings, data = data, mapping = myMappingFillColor)
            val geomLayer = getSingleGeomLayer(spec)
            assertScaleOrdering(
                geomLayer,
                expectedScaleBreaks = mapOf(
                    Aes.COLOR to listOf("2", "1"),
                    Aes.FILL to listOf("v")

                ),
                expectedOrderInBar = mapOf(
                    Aes.FILL to listOf(listOf("v", null, null)),
                    Aes.COLOR to listOf(listOf(null, "2", "1"))
                )
            )
        }
    }

    // The variable is mapped to different aes

    @Test
    fun `x='x', fill='x' - default`() {
        val mapping = """{ "x": "x", "fill": "x" }"""
        val geomLayer = getSingleGeomLayer(makePlotSpec(annotations = "", mapping = mapping))
        assertScaleBreaks(geomLayer, Aes.X, listOf("B", "A", "C"))
        assertScaleBreaks(geomLayer, Aes.FILL, listOf("B", "A", "C"))
    }

    @Test
    // Now 'x' and 'fill' mapped to different variables ("x.x" and "fill.x") => should not inherit options
    fun `x=as_discrete('x', order=1), fill='x' - the ordering does no apply to the 'fill'`() {
        val mapping = """{ "x": "x", "fill": "x" }"""
        val orderingSettings = makeOrderingSettings("x", null, 1)

        val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings, mapping = mapping))
        assertScaleBreaks(geomLayer, Aes.X, listOf("A", "B", "C"))
        assertScaleBreaks(geomLayer, Aes.FILL, listOf("B", "A", "C"))
    }

    @Test
    // Now 'x' and 'fill' mapped to different variables ("x.x" and "fill.x") => should not combine options
    fun `x=as_discrete('x', order=1), fill=as_discrete('x') - should not apply the ordering to the 'fill'`() {
        val mapping = """{ "x": "x", "fill": "x" }"""
        val orderingSettings = makeOrderingSettings("x", null, 1) + "," +
                makeOrderingSettings("fill", null, null)

        val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings, mapping = mapping))
        assertScaleBreaks(geomLayer, Aes.X, listOf("A", "B", "C"))
        assertScaleBreaks(geomLayer, Aes.FILL, listOf("B", "A", "C"))
     }

    @Test
    // Now 'x' and 'fill' mapped to different variables ("x.x" and "fill.x") => should not combine options
    fun `x=as_discrete('x', order_by='count'), fill=as_discrete('x', order=1) - should not combine order options`() {
        val mapping = """{ "x": "x", "fill": "x" }"""
        val orderingSettings = makeOrderingSettings("x", "..count..", order = null) + "," +
                makeOrderingSettings("fill", orderBy = null, order = 1)
        val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings, mapping = mapping))
        assertScaleBreaks(geomLayer, Aes.X, listOf("B", "A", "C"))
        assertScaleBreaks(geomLayer, Aes.FILL, listOf("A", "B", "C"))
    }


    // variable in plot and layer

    @Test
    // Now 'x' and 'fill' mapped to different variables ("x.x" and "x") => should not inherit options
    fun `ggplot(aes(as_discrete('x',order=1))) + geom_bar(aes(fill='x')) - should not apply the ordering to the 'fill'`() {
        val spec = """{
              "kind": "plot",
              "data" : $myData,              
              "mapping": { "x": "x" },
              "data_meta": { "mapping_annotations": [ ${makeOrderingSettings("x", null, 1)} ] },             
              "layers": [
                {
                  "mapping": { "fill": "x" },
                  "geom": "bar"
                }
              ]
            }""".trimIndent()
        val geomLayer = getSingleGeomLayer(spec)
        assertScaleBreaks(geomLayer, Aes.X, listOf("A", "B", "C"))
        assertScaleBreaks(geomLayer, Aes.FILL, listOf("B", "A", "C"))
    }

    @Test
    // Now 'x' and 'fill' mapped to different variables ("x" and "fill.x") => should not inherit options
    fun `ggplot(aes('x')) + geom_bar(aes(fill=as_discrete('x',order=1))) - should not apply the ordering to the 'x'`() {
        val spec = """{
              "kind": "plot",
              "data" : $myData,              
              "mapping": { "x": "x" },
              "layers": [
                {
                  "mapping": { "fill": "x" },
                  "geom": "bar",
                  "data_meta": { "mapping_annotations": [ ${makeOrderingSettings("fill", null, 1)} ] }
                }
              ]
            }""".trimIndent()
        val geomLayer = getSingleGeomLayer(spec)
        assertScaleBreaks(geomLayer, Aes.X, listOf("B", "A", "C"))
        assertScaleBreaks(geomLayer, Aes.FILL, listOf("A", "B", "C"))
    }

    companion object {
        private fun assertScaleBreaks(
            layer: GeomLayer,
            aes: Aes<*>,
            expectedScaleBreaks: List<Any>?
        ) {
            val scale = layer.scaleMap[aes]
            if (expectedScaleBreaks == null) {
                assertNull(scale, "Scale for ${aes.name.uppercase()} should not be present")
            } else {
                assertEquals(
                    expectedScaleBreaks,
                    scale!!.getScaleBreaks().domainValues,
                    "Wrong ticks order on ${aes.name.uppercase()}."
                )
            }
        }

        private fun getBarColumnValues(
            geomLayer: GeomLayer,
            valueToColors: Map<Color, Any>,
            colorFactory: (DataPointAesthetics) -> Color?
        ): Map<Int, List<Any?>> {
            val colorInColumns = mutableMapOf<Int, ArrayList<Color>>()
//            val aesthetics = PlotUtil.createLayerDryRunAesthetics(geomLayer)
            val aesthetics = PlotUtil.DemoAndTest.layerAestheticsWithoutLayout(geomLayer)
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

        internal fun assertScaleOrdering(
            geomLayer: GeomLayer,
            expectedScaleBreaks: Map<Aes<*>, List<String>?>,
            expectedOrderInBar: Map<Aes<*>, List<List<*>>?>
        ) {
            expectedScaleBreaks.forEach { (aes, breaks) ->
                assertScaleBreaks(geomLayer, aes, breaks)
            }

            expectedOrderInBar.forEach { (aes, expected) ->
                val scale = geomLayer.scaleMap[aes]
                if (expected == null) {
                    assertNull(scale, "Scale for ${aes.name.uppercase()} should not be present")
                } else {
                    val breaks = scale!!.getScaleBreaks().domainValues
                    val breakColors = breaks.zip(legendColors).associate { it.second to it.first }
                    val actual: Map<Int, List<Any?>> =
                        getBarColumnValues(geomLayer, breakColors) { p: DataPointAesthetics ->
                            if (aes == Aes.FILL) p.fill() else p.color()
                        }
                    assertEquals(expected.size, actual.size)
                    for (i in expected.indices) {
                        assertEquals(expected[i], actual[i], "Wrong color order in ${aes.name.uppercase()}.")
                    }
                }
            }
        }
    }
}