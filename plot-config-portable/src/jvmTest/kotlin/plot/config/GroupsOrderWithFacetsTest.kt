/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.parsePlotSpec
import kotlin.test.Test
import kotlin.test.assertEquals

class GroupsOrderWithFacetsTest {

    private val myData = """
            |{
            |    "x": [0, 0, 0, 1, 1, 0, 0, 0, 1, 1],
            |    "f": ["B", "C", "A", "B", "A", "B", "D", "A", "A", "B"],
            |    "g": ["X", "X", "X", "X", "X", "Y", "Y", "Y", "Y", "Y"]
            |}""".trimMargin()

    @Test
    fun `groups should be ordered similarly when using facets`() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": $myData,
            |  "mapping": {
            |    "x": "x",
            |    "fill": "f"
            |  },
            |  "facet": {
            |    "name": "grid",
            |    "x": "g",
            |    "x_order": 1,
            |    "y_order": 1
            |   },  
            |  "layers": [
            |    {
            |      "geom": "bar"
            |    }
            |  ]
            |}""".trimMargin()

        val layersByTile = TestUtil.createMultiTileGeomLayers(parsePlotSpec(spec))
        assertEquals(2, layersByTile.size)

        // tile 1
        ScaleOrderingTest.assertScaleOrdering(
            layersByTile[0].single(),
            expectedScaleBreaks = mapOf(
                Aes.FILL to listOf("B", "C", "A", "D")
            ),
            expectedOrderInBar = mapOf(
                Aes.FILL to listOf(
                    listOf("B", "C", "A"),
                    listOf("B", "A")
                )
            )
        )
        //tile 2
        ScaleOrderingTest.assertScaleOrdering(
            layersByTile[1].single(),
            expectedScaleBreaks = mapOf(
                Aes.FILL to listOf("B", "C", "A", "D")
            ),
            expectedOrderInBar = mapOf(
                Aes.FILL to listOf(
                    listOf("B", "A", "D"),
                    listOf("B", "A")
                )
            )
        )
    }

    @Test
    fun `with specified ordering`() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": $myData,
            |  "mapping": {
            |    "x": "x",
            |    "fill": "f"
            |  },
            |  "facet": {
            |    "name": "grid",
            |    "x": "g",
            |    "x_order": 1,
            |    "y_order": 1
            |   },  
            |  "layers": [
            |    {
            |      "geom": "bar",
            |      "data_meta": {
            |         "mapping_annotations": [
            |           {
            |               "aes": "fill",
            |               "annotation": "as_discrete",
            |               "parameters": { "order": 1 }
            |           }
            |         ] 
            |      }
            |    }
            |  ]
            |}""".trimMargin()

        val layersByTile = TestUtil.createMultiTileGeomLayers(parsePlotSpec(spec))
        assertEquals(2, layersByTile.size)

        // tile 1
        ScaleOrderingTest.assertScaleOrdering(
            layersByTile[0].single(),
            expectedScaleBreaks = mapOf(
                Aes.FILL to listOf("A", "B", "C", "D")
            ),
            expectedOrderInBar = mapOf(
                Aes.FILL to listOf(
                    listOf("A", "B", "C"),
                    listOf("A", "B")
                )
            )
        )
        //tile 2
        ScaleOrderingTest.assertScaleOrdering(
            layersByTile[1].single(),
            expectedScaleBreaks = mapOf(
                Aes.FILL to listOf("A", "B", "C", "D")
            ),
            expectedOrderInBar = mapOf(
                Aes.FILL to listOf(
                    listOf("A", "B", "D"),
                    listOf("A", "B")
                )
            )
        )
    }

    @Test
    fun withNanValues() {
        val data = """
            |{
            |    "x": [0, 0, 0, 1, 1, 0, 0, 0, 1, 1],
            |    "f": ["B", null, "A", null, "A", null, "C", "A", "A", "B"],
            |    "g": ["X", "X", "X", "X", "X", "Y", "Y", "Y", "Y", "Y"]
            |}""".trimMargin()
        val spec = """
            |{
            |  "kind": "plot",
            |  "data": $data,
            |  "mapping": {
            |    "x": "x",
            |    "fill": "f"
            |  },
            |  "facet": {
            |    "name": "grid",
            |    "x": "g",
            |    "x_order": 1,
            |    "y_order": 1
            |   },  
            |  "layers": [
            |    {
            |      "geom": "bar"
            |    }
            |  ]
            |}""".trimMargin()

        val layersByTile = TestUtil.createMultiTileGeomLayers(parsePlotSpec(spec))
        assertEquals(2, layersByTile.size)

        // tile 1
        ScaleOrderingTest.assertScaleOrdering(
            layersByTile[0].single(),
            expectedScaleBreaks = mapOf(
                Aes.FILL to listOf("B", "A", "C")
            ),
            expectedOrderInBar = mapOf(
                Aes.FILL to listOf(
                    listOf("B", "A", null),
                    listOf("A", null)
                )
            )
        )
        //tile 2
        ScaleOrderingTest.assertScaleOrdering(
            layersByTile[1].single(),
            expectedScaleBreaks = mapOf(
                Aes.FILL to listOf("B", "A", "C")
            ),
            expectedOrderInBar = mapOf(
                Aes.FILL to listOf(
                    listOf("A", "C", null),
                    listOf("B", "A")
                )
            )
        )
    }
}