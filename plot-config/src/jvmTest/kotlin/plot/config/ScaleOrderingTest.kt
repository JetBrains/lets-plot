/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.config.TestUtil.getSingleGeomLayer
import kotlin.test.Test
import kotlin.test.assertEquals


class ScaleOrderingTest {
    private val myData = """{
        'x'   :  [ "B", "A", "B", "B", "A", "A", "A", "B", "B", "C", "C" ],
        'fill':  [ '4', '2', '3', '3', '2', '3', '1', '1', '3', '4', '2' ]
    }"""
    private val myMapping: String = """{ "x": "x", "fill": "fill" }"""

    private fun makePlotSpec(
        annotations: String,
        sampling: String? = null,
        mapping: String = myMapping
    ): String {
        return """{
              "kind": "plot",
              "layers": [
                {
                  "data" : $myData,
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
        assertScaleBreaks(geomLayer, Aes.X, listOf("B", "C", "A"))
        assertScaleBreaks(geomLayer, Aes.FILL, listOf("4", "2", "3", "1"))
    }

    @Test
    fun `order x`() {
        run {
            //ascending
            val orderingSettings = makeOrderingSettings("x", null, 1)
            val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings))
            assertScaleBreaks(geomLayer, Aes.X, listOf("A", "B", "C"))
            assertScaleBreaks(geomLayer, Aes.FILL, listOf("4", "2", "3", "1"))
        }
        run {
            //descending
            val orderingSettings = makeOrderingSettings("x", null, -1)
            val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings))
            assertScaleBreaks(geomLayer, Aes.X, listOf("C", "B", "A"))
            assertScaleBreaks(geomLayer, Aes.FILL, listOf("4", "2", "3", "1"))
        }
    }

    @Test
    fun `order fill`() {
        run {
            //ascending
            val orderingSettings = makeOrderingSettings("fill", null, 1)
            val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings))

            assertScaleBreaks(geomLayer, Aes.FILL, listOf("1", "2", "3", "4"))
            assertScaleBreaks(geomLayer, Aes.X, listOf("B", "C", "A"))
        }
        run {
            //descending
            val orderingSettings = makeOrderingSettings("fill", null, -1)
            val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings))

            assertScaleBreaks(geomLayer, Aes.FILL, listOf("4", "3", "2", "1"))
            assertScaleBreaks(geomLayer, Aes.X, listOf("B", "C", "A"))
        }
    }

    @Test
    fun `order x by count`() {
        run {
            //ascending
            val orderingSettings = makeOrderingSettings("x", "..count..", 1)
            val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings))
            assertScaleBreaks(geomLayer, Aes.X, listOf("C", "A", "B"))
            assertScaleBreaks(geomLayer, Aes.FILL, listOf("4", "2", "3", "1"))
        }
        run {
            //descending
            val orderingSettings = makeOrderingSettings("x", "..count..", -1)
            val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings))
            assertScaleBreaks(geomLayer, Aes.X, listOf("B", "A", "C"))
            assertScaleBreaks(geomLayer, Aes.FILL, listOf("4", "2", "3", "1"))
        }
    }

    @Test
    fun `order x and fill`() {
        run {
            //x descending - fill ascending
            val orderingSettings =
                makeOrderingSettings("x", null, -1) + "," + makeOrderingSettings("fill", null, 1)
            val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings))
            assertScaleBreaks(geomLayer, Aes.X, listOf("C", "B", "A"))
            assertScaleBreaks(geomLayer, Aes.FILL, listOf("1", "2", "3", "4"))
        }
        run {
            //x descending - fill descending
            val orderingSettings =
                makeOrderingSettings("x", null, -1) + "," + makeOrderingSettings("fill", null, -1)
            val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings))
            assertScaleBreaks(geomLayer, Aes.X, listOf("C", "B", "A"))
            assertScaleBreaks(geomLayer, Aes.FILL, listOf("4", "3", "2", "1"))
        }
    }

    @Test
    fun `order x by count and fill by itself`() {
        run {
            //ascending
            val orderingSettings =
                makeOrderingSettings("x", "..count..", 1) + "," + makeOrderingSettings("fill", null, 1)
            val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings))
            assertScaleBreaks(geomLayer, Aes.X, listOf("C", "A", "B"))
            assertScaleBreaks(geomLayer, Aes.FILL, listOf("1", "2", "3", "4"))
        }
        run {
            //descending
            val orderingSettings =
                makeOrderingSettings("x", "..count..", -1) + "," + makeOrderingSettings("fill", null, -1)
            val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings))
            assertScaleBreaks(geomLayer, Aes.X, listOf("B", "A", "C"))
            assertScaleBreaks(geomLayer, Aes.FILL, listOf("4", "3", "2", "1"))
        }
    }

    @Test
    fun `pick sampling`() {
        val samplingPick = """{ "name": "pick", "n": 2 }"""
        run {
            // no ordering
            val geomLayer = getSingleGeomLayer(makePlotSpec("", samplingPick))
            assertScaleBreaks(geomLayer, Aes.X, listOf("B", "C"))
            assertScaleBreaks(geomLayer, Aes.FILL, listOf("4", "2", "3", "1"))
        }
        run {
            // Order x
            val orderingSettings = makeOrderingSettings("x", null, -1)
            val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings, samplingPick))

            assertScaleBreaks(geomLayer, Aes.X, listOf("C", "B"))
            assertScaleBreaks(geomLayer, Aes.FILL, listOf("4", "2", "3", "1"))
        }
        run {
            // Order x and fill
            val orderingSettings =
                makeOrderingSettings("x", null, -1) + "," + makeOrderingSettings("fill", null, 1)
            val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings, samplingPick))

            assertScaleBreaks(geomLayer, Aes.X, listOf("C", "B"))
            assertScaleBreaks(geomLayer, Aes.FILL, listOf("1", "2", "3", "4"))
        }
        run {
            // Order x by count
            val orderingSettings = makeOrderingSettings("x", "..count..", 1)
            val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings, samplingPick))

            assertScaleBreaks(geomLayer, Aes.X, listOf("C", "A"))
            assertScaleBreaks(geomLayer, Aes.FILL, listOf("4", "2", "3", "1"))
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
            val geomLayer = getSingleGeomLayer(makePlotSpec("", sampling))
            assertScaleBreaks(geomLayer, Aes.FILL, listOf("4", "1"))
            assertScaleBreaks(geomLayer, Aes.X, listOf("B", "C"))
        }
        run {
            // Order x.
            val orderingSettings = makeOrderingSettings("x", null, 1)
            val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings, sampling))
            assertScaleBreaks(geomLayer, Aes.FILL, listOf("4", "1"))
            assertScaleBreaks(geomLayer, Aes.X, listOf("A", "B"))
        }
    }

    @Test
    fun `group sampling`() {
        val samplingGroup = """{ "name": "group_systematic", "n": 3 }"""

        run {
            // Default - no ordering
            val geomLayer = getSingleGeomLayer(makePlotSpec("", samplingGroup))
            assertScaleBreaks(geomLayer, Aes.X, listOf("B", "C", "A"))
            assertScaleBreaks(geomLayer, Aes.FILL, listOf("4", "3"))
        }
        run {
            // Order x
            val orderingSettings = makeOrderingSettings("x", null, 1)
            val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings, samplingGroup))
            assertScaleBreaks(geomLayer, Aes.X, listOf("A", "B", "C"))
            assertScaleBreaks(geomLayer, Aes.FILL, listOf("4", "3"))
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
    fun `x=as_discrete('x', order=1), fill='x' - now the ordering is not applied to the 'fill'`() {
        val mapping = """{ "x": "x", "fill": "x" }"""
        val orderingSettings = makeOrderingSettings("x", null, 1)

        val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings, mapping = mapping))
        assertScaleBreaks(geomLayer, Aes.X, listOf("A", "B", "C"))
        assertScaleBreaks(geomLayer, Aes.FILL, listOf("B", "A", "C")) // TODO  Should apply the ordering to the 'fill'?
    }

    @Test
    fun `x=as_discrete('x', order=1), fill=as_discrete('x') - should apply the ordering to the 'fill'`() {
        val mapping = """{ "x": "x", "fill": "x" }"""
        val orderingSettings = makeOrderingSettings("x", null, 1)  + "," +
                makeOrderingSettings("fill", null, null)

        val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings, mapping = mapping))
        assertScaleBreaks(geomLayer, Aes.FILL, listOf("A", "B", "C"))
        assertScaleBreaks(geomLayer, Aes.X, listOf("A", "B", "C"))
    }

    @Test
    fun `x=as_discrete('x', order_by='count'), fill=as_discrete('x', order=1) - should combine order options`() {
        val mapping = """{ "x": "x", "fill": "x" }"""
        val orderingSettings = makeOrderingSettings("x", "..count..", order = null) + "," +
                makeOrderingSettings("fill", orderBy = null, order = 1)
        val geomLayer = getSingleGeomLayer(makePlotSpec(orderingSettings, mapping = mapping))
        assertScaleBreaks(geomLayer, Aes.X, listOf("C", "A", "B"))
        assertScaleBreaks(geomLayer, Aes.FILL, listOf("C", "A", "B"))
    }

    companion object {
        private fun assertScaleBreaks(
            layer: GeomLayer,
            aes: Aes<*>,
            breaks: List<Any>
        ) {
            val scale = layer.scaleMap[aes]
            assertEquals(breaks, scale.breaks)
        }
    }
}