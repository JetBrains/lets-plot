/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.spec.front.PlotConfigFrontendUtil
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GeoConfigWithStatApplyingTest {

    // Config plot with applying of statistical transformation and using of 'map_join' to join coordinates with data
    // Take a pie geom as an example ('count2d' is applied)

    private val myData = """
        "Name": ["A", "A", "B", "B"],
        "Vote": ["Yes", "No", "Yes", "No"],
        "Number": [120, 30, 20, 80 ],
        "Registered": [165, 165, 111, 111],
        "Full name": ["City A", "City A", "City B", "City B"]
    """
    private val coordA = """{\"type\": \"Point\", \"coordinates\": [-80.0, -40.0]}"""
    private val coordB = """{\"type\": \"Point\", \"coordinates\": [80.0, 40.0]}"""
    private val commonMapping = """ "fill": "Vote", "weight": "Number" """
    private val groupMapping = """ "group": "Name" """
    private val xMapping = """ "x": "Name" """

    // Add variables to tooltips just to keep them
    private fun pieLayer(mapping: String) = """
            |        "geom": "pie",
            |        "data": { $myData },
            |        "mapping": { $mapping },
            |        "tooltips": { "variables": ["..sum..", "Registered", "Full name"] },
            |        "map": {
            |            "name": ["A", "B"],
            |            "coord": ["$coordA", "$coordB"]
            |        },
            |        "map_data_meta": {"geodataframe": {"geometry": "coord"}},
            |        "map_join": [["Name"], ["name"]]            
        """.trimMargin()

    @Test
    fun `not map plot and no positional mapping - the sum will be calculated for all records`() {
        getGeomLayer(
            """
            |{
            |    "kind": "plot",
            |    "layers": [
            |       {
            |           ${pieLayer("$commonMapping, $groupMapping")}
            |       }
            |    ]
            |}
            """.trimMargin()
        )
            .assertValues("Name", listOf("A", "A", "B", "B"))
            .assertValues("transform.X", listOf(-80.0, -80.0, 80.0, 80.0)) // gdf coordinates
            .assertValues("transform.Y", listOf(-40.0, -40.0, 40.0, 40.0))
            .assertValues("..sum..", listOf(250.0, 250.0, 250.0, 250.0))   // sum by all records
    }

    @Test
    fun `not map plot with positional mapping - the sum will be calculated for each position`() {
        getGeomLayer(
            """
            |{
            |    "kind": "plot",
            |    "layers": [
            |       {
            |           ${pieLayer("$commonMapping, $xMapping")}
            |       }
            |    ]
            |}
            """.trimMargin()
        )
            .assertValues("Name", listOf("A", "B", "A", "B"))
            // GeoDataframe is not used because it's not a map plot and positional mapping is set:
            .assertValues("transform.X", listOf(0.0, 1.0, 0.0, 1.0))
            .assertValues("transform.Y", listOf(0.0, 0.0, 0.0, 0.0))
            // The sums were calculated for each Name:
            .assertValues("..sum..", listOf(150.0, 100.0, 150.0, 100.0))
    }

    @Test
    fun `map plot and no positional mapping - the sum will be calculated for all records`() {
        getGeomLayer(
            """
            |{
            |    "kind": "plot",
            |    "layers": [
            |       {
            |          "geom": "livemap"
            |       },
            |       {
            |            ${pieLayer("$commonMapping, $groupMapping")}
            |       }
            |    ]
            |}
            """.trimMargin()
        )
            .assertValues("Name", listOf("A", "A", "B", "B"))
            .assertValues("transform.X", listOf(-80.0, -80.0, 80.0, 80.0))
            .assertValues("transform.Y", listOf(-40.0, -40.0, 40.0, 40.0))
            .assertValues("..sum..", listOf(250.0, 250.0, 250.0, 250.0))
    }

    @Test
    fun `map plot with positional mapping - the sum will be calculated for each position`() {
        getGeomLayer(
            """
            |{
            |    "kind": "plot",
            |    "layers": [
            |       {
            |          "geom": "livemap"
            |       },
            |       {
            |            ${pieLayer("$commonMapping, $xMapping")}
            |       }
            |    ]
            |}
            """.trimMargin()
        )
            .assertValues("Name", listOf("A", "B", "A", "B"))
            // It's the map plot => will get GeoDataframe coordinates even with specified positional mapping:
            .assertValues("transform.X", listOf(-80.0, 80.0, -80.0, 80.0))
            .assertValues("transform.Y", listOf(-40.0, 40.0, -40.0, 40.0))
            // The sums were calculated for each Name:
            .assertValues("..sum..", listOf(150.0, 100.0, 150.0, 100.0))
    }

    // Check additional variables
    @Test
    fun `keep other variables after stat applying`() {
        getGeomLayer(
            """
            |{
            |    "kind": "plot",
            |    "layers": [
            |       { 
            |           ${pieLayer("$commonMapping, $xMapping")}
            |       }
            |    ]
            |}
            """.trimMargin()
        )
            .assertValues("Name", listOf("A", "B", "A", "B"))
             // Averages will be obtained for numeric or the first in the data for non-numeric values:
            .assertValues("Registered", listOf(138.0, 138.0, 138.0, 138.0))
            .assertValues("Full name", listOf("City A", "City A", "City A", "City A"))

        // Add grouping by Name - the result values will be corresponded to 'Name' values
        getGeomLayer(
            """
            |{
            |    "kind": "plot",
            |    "layers": [
            |       { 
            |           ${pieLayer("$commonMapping, $groupMapping")} 
            |       }
            |    ]
            |}
            """.trimMargin()
        )
            .assertValues("Name", listOf("A", "A", "B", "B"))
            .assertValues("Registered", listOf(165.0, 165.0, 111.0, 111.0))
            .assertValues("Full name", listOf("City A", "City A", "City B", "City B"))
    }

    private fun GeomLayer.assertValues(variable: String, values: List<*>): GeomLayer {
        assertEquals(values, dataFrame[DataFrameUtil.findVariableOrFail(dataFrame, variable)])
        return this
    }

    private fun getGeomLayer(spec: String): GeomLayer {
        val config = transformToClientPlotConfig(spec)
        val layers = PlotConfigFrontendUtil.createPlotAssembler(config).coreLayersByTile.single()
        val geomLayers = layers.filterNot(GeomLayer::isLiveMap)
        assertTrue(geomLayers.size == 1, "No layers")
        return geomLayers.single()
    }
}