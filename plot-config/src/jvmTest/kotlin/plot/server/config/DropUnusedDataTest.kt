/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.config.GeoConfig.Companion.POINT_X
import jetbrains.datalore.plot.config.GeoConfig.Companion.POINT_Y
import jetbrains.datalore.plot.config.PlotConfig
import jetbrains.datalore.plot.config.TestUtil
import jetbrains.datalore.plot.config.assertBinding
import jetbrains.datalore.plot.parsePlotSpec
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DropUnusedDataTest {

    private fun assertVarPresent(varName: String, dataSize: Int, data: Map<String, Any>) {
        assertTrue(data.containsKey(varName), "Not found '$varName'")
        assertTrue(data[varName] is List<*>, "Not a list '$varName'")
        if (dataSize > 0) {
            assertEquals(dataSize, (data[varName] as List<*>).size, "Size '$varName'")
        }
    }

    private fun assertVarNotPresent(varName: String, data: Map<String, Any>) {
        assertTrue(!data.containsKey(varName), "Present '$varName'")
    }

    private fun assertNoTransformVars(data: Map<String, Any>) {
        for (name in data.keys) {
            assertFalse(TransformVar.isTransformVar(name), "Transform variable '$name'")
        }
    }

    private fun assertEmptyPlotData(opts: Map<String, Any>) {
        val plotData = TestUtil.getPlotData(opts)
        assertEquals(0, plotData.size)
    }

    private fun checkSingleLayerData(opts: Map<String, Any>,
                                     expectedVarCount: Int,
                                     expectedVars: Map<String, Int>) {
        checkLayerData(0, opts, expectedVarCount, expectedVars, emptyList())
    }

    private fun checkSingleLayerData(opts: Map<String, Any>,
                                     expectedVarCount: Int,
                                     expectedVars: Map<String, Int>,
                                     unexpectedVars: Iterable<String>) {
        checkLayerData(0, opts, expectedVarCount, expectedVars, unexpectedVars)
    }

    private fun checkLayerData(layerIndex: Int, opts: Map<String, Any>,
                               expectedVarCount: Int,
                               expectedVars: Map<String, Int>,
                               unexpectedVars: Iterable<String>) {
        val data = TestUtil.getLayerData(opts, layerIndex)
        checkData(data, expectedVarCount, expectedVars, unexpectedVars)
    }

    private fun checkData(data: Map<String, Any>,
                          expectedVarCount: Int,
                          expectedVars: Map<String, Int>,
                          unexpectedVars: Iterable<String>) {
        assertNoTransformVars(data)
        assertEquals(expectedVarCount, data.size)
        for (`var` in expectedVars.keys) {
            val size = expectedVars[`var`]
            assertVarPresent(`var`, size!!, data)
        }
        for (`var` in unexpectedVars) {
            assertVarNotPresent(`var`, data)
        }
    }

    @Test
    fun specialVariables() {
        val x = listOf(0.0, 0.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 3.0, 3.0)
        val group = listOf("0", "0", "1", "0", "0", "1", "1", "0", "1", "1")
        val facetX = listOf("a", "a", "b", "a", "a", "b", "b", "a", "b", "b")

        val data = HashMap<String, List<*>>()
        data["group"] = group
        data["x"] = x
        data["facetX"] = facetX

        val bins = 6
        val spec = "{" +
                "   'mapping': {" +
                "             'x': 'x'" +
                "           }," +
                "   'layers': [" +
                "               {" +
                "                  'geom':  {" +
                "                             'name': 'histogram'," +
                "                             'mapping': {" +
                "                                          'fill': 'group'" +
                "                                         }," +
                "                             'bins': " + bins + "," +
                "                             'center': 0" +
                "                           }" +
                "               }" +
                "           ]," +
                "   'facet': {" +
                "             'name': 'grid'," +
                "             'x': 'facetX'" +
                "            }" +
                "}"

        val opts = ServerSideTestUtil.parseOptionsServerSide(spec, data)
        TestUtil.checkOptionsClientSide(opts, 1)

        assertEmptyPlotData(opts)

        val statSize = bins * 2 // two groups
        val droppedVars = listOf("..x..", "..density..", "..group..")
        checkSingleLayerData(opts, 4,
                mapOf(
                        "x" to statSize,
                        "group" to statSize,
                        "facetX" to statSize,
                        "..count.." to statSize
                ),
                droppedVars
        )
    }

    @Test
    fun keepLayerDataIfIdentityStat() {
        val data = "{" +
                "  'x': [0,0,1,1]" +
                "}"

        val spec = "{" +
                "   'layers': [" +
                "               {" +
                "                  'geom':  {" +
                "                             'name': 'point'," +
                "                             'data': " + data + "," +
                "                             'mapping': {" +
                "                                          'x': 'x'," +
                "                                          'z': 'x'" +   // aes Z is not rendered by point but var 'x' must not be dropped

                "                                        }" +
                "                           }" +
                "               }" +
                "           ]" +
                "}"

        val opts = ServerSideTestUtil.parseOptionsServerSide(spec)
        TestUtil.checkOptionsClientSide(opts, 1)

        assertEmptyPlotData(opts)
        checkSingleLayerData(opts, 1, mapOf("x" to 4))
    }

    @Test
    fun plot_clear_layer_addX_continuous_barCount() {
        val data = "{" +
                "  'x': [0,0,1,1]" +
                "}"

        val spec = "{" +
                "   'data': " + data +
                "           ," +
                "   'layers': [" +
                "               {" +
                "                  'geom':  {" +
                "                             'name': 'bar'," +
                "                             'stat': 'count'," +
                "                             'data': " + data + "," +
                "                             'mapping': {" +
                "                                          'x': 'x'," +
                "                                          'fill': 'x'" +  // 'x' --> '..x..'

                "                                        }" +
                "                           }" +
                "               }" +
                "           ]" +
                "}"

        val opts = ServerSideTestUtil.parseOptionsServerSide(spec)
        TestUtil.checkOptionsClientSide(opts, 1)

        assertEmptyPlotData(opts)

        val statSize = 2
        val droppedVars = listOf("..group..")
        checkSingleLayerData(opts, 2,
                mapOf(
                        "x" to statSize,
                        "..count.." to statSize
                ),
                droppedVars
        )
    }

    @Test
    fun plot_clear_layer_addX_barCount() {
        val data = "{" +
                "  'x': [0,0,1,1]," +
                "  'c': ['a','b','a','b']" +
                "}"

        val spec = "{" +
                "   'data': " + data +
                "           ," +
                "   'layers': [" +
                "               {" +
                "                  'geom':  {" +
                "                             'name': 'bar'," +
                "                             'stat': 'count'," +
                "                             'data': " + data + "," +
                "                             'mapping': {" +
                "                                          'x': 'x'" +
                "                                        }" +
                "                           }" +
                "               }" +
                "           ]" +
                "}"

        val opts = ServerSideTestUtil.parseOptionsServerSide(spec)
        TestUtil.checkOptionsClientSide(opts, 1)

        assertEmptyPlotData(opts)

        val statSize = 2
        val droppedVars = listOf("c", "..x..", "..group..")
        checkSingleLayerData(opts, 2,
                mapOf(
                        "x" to statSize,
                        "..count.." to statSize
                ),
                droppedVars
        )
    }

    @Test
    fun plot_keepXC__barCount() {
        val data = "{" +
                "  'x': [0,0,1,1]," +
                "  'c': ['a','b','a','b']" +
                "}"

        val spec = "{" +
                "   'data': " + data +
                "           ," +
                "   'layers': [" +
                "               {" +
                "                  'geom':  {" +
                "                             'name': 'bar'," +
                "                             'stat': 'count'," +
                "                             'data': " + data + "," +
                "                             'mapping': {" +
                "                                          'x': 'x'," +
                "                                          'fill': 'c'" +  // the reason 'c' must be kept at geom level

                "                                        }" +
                "                           }" +
                "               }" +
                "           ]" +
                "}"

        val opts = ServerSideTestUtil.parseOptionsServerSide(spec)
        TestUtil.checkOptionsClientSide(opts, 1)

        assertEmptyPlotData(opts)

        val statSize = 4
        val droppedVars = listOf("..x..", "..group..")
        checkSingleLayerData(opts, 3,
                mapOf(
                        "x" to statSize,
                        "c" to statSize,
                        "..count.." to statSize
                ),
                droppedVars
        )
    }

    @Test
    fun plot_keepXYC__explicitGrouping_statIdentity() {
        val data = "{" +
                "  'x': [0,0,1,1]," +
                "  'y': [0,0,1,1]," +
                "  'c': ['a','b','a','b']" +
                "}"

        val spec = "{" +
                "   'data': " + data +
                "           ," +
                "   'layers': [" +
                "               {" +
                "                  'geom':  {" +
                "                             'name': 'polygon'," +      // uses x,y

                "                             'stat': 'identity'," +
                "                             'data': " + data + "," +
                "                             'mapping': {" +
                "                                          'x': 'x'," +
                "                                          'y': 'y'," +
                "                                          'group': 'c'" +  // not an aesthetic

                "                                        }" +
                "                           }" +
                "               }" +
                "           ]" +
                "}"

        val opts = ServerSideTestUtil.parseOptionsServerSide(spec)
        TestUtil.checkOptionsClientSide(opts, 1)

        assertEmptyPlotData(opts)
        checkSingleLayerData(opts, 3,
                mapOf(
                        "x" to 4,
                        "y" to 4,
                        "c" to 4
                )
        )
    }

    @Test
    fun plot_dropXYZ_layer_dropLevel_contour() {
        val bins = 20

        val spec = "{" +
                "   'mapping': {" +
                "             'x': 'x'," +
                "             'y': 'y'," +
                "             'z': 'z'" +
                "           }," +
                "   'layers': [" +
                "               {" +
                "                  'geom':  {" +
                "                             'name': 'contour'," +
                "                             'bins': " + bins +
                "                           }" +
                "               }" +
                "           ]" +
                "}"


        val opts = ServerSideTestUtil.parseOptionsServerSide(spec, TestUtil.contourData())
        TestUtil.checkOptionsClientSide(opts, 1)

        assertEmptyPlotData(opts)

        val unknownSize = -1
        val droppedVars = listOf("z", "..level..")
        checkSingleLayerData(opts, 3,
                mapOf(
                        "x" to unknownSize,
                        "y" to unknownSize,
                        "..group.." to unknownSize
                ),
                droppedVars
        )
    }

    @Test
    fun plot_dropXYZ_layer_keepLevel_contour() {
        val bins = 20

        val spec = "{" +
                "   'mapping': {" +
                "             'x': 'x'," +
                "             'y': 'y'," +
                "             'z': 'z'" +
                "           }," +
                "   'layers': [" +
                "               {" +
                "                  'geom':  {" +
                "                             'name': 'contour'," +
                "                             'mapping': {" +
                "                                        'color': '..level..'" +
                "                                      }," +
                "                             'bins': " + bins +
                "                           }" +
                "               }" +
                "           ]" +
                "}"


        val opts = ServerSideTestUtil.parseOptionsServerSide(spec, TestUtil.contourData())
        TestUtil.checkOptionsClientSide(opts, 1)

        assertEmptyPlotData(opts)

        val unknownSize = -1
        val droppedVars = listOf("z")
        checkSingleLayerData(opts, 4,
                mapOf(
                        "x" to unknownSize,
                        "y" to unknownSize,
                        "..group.." to unknownSize,
                        "..level.." to unknownSize
                ),
                droppedVars
        )
    }

    @Test
    fun plot_clear_layer_addX_discrete_barCount() {
        val data = "{" +
                "      'time': ['Lunch','Lunch', 'Dinner', 'Dinner', 'Dinner']" +    // preserve this discrete X

                "}"

        val spec = "{" +
                "   'data': " + data +
                "           ," +
                "   'mapping': {" +
                "             'x': 'time'" +
                "           }," +
                "   'layers': [" +
                "               {" +
                "                  'geom':  {" +
                "                             'name': 'bar'" +
                "                           }" +
                "               }" +
                "           ]" +
                "}"


        val opts = ServerSideTestUtil.parseOptionsServerSide(spec)
        TestUtil.checkOptionsClientSide(opts, 1)

        assertEmptyPlotData(opts)

        val layerData = TestUtil.getLayerData(opts, 0)
        assertNoTransformVars(layerData)

        val statSize = 2
        val droppedVars = listOf("..x..", "..group..")
        checkData(layerData, 2,
                mapOf(
                        "time" to statSize,
                        "..count.." to statSize
                ),
                droppedVars
        )

        assertEquals(listOf("Lunch", "Dinner"), layerData["time"])
    }

    @Test
    fun plot_clear_layer_addDensity_histogram() {
        val x = listOf(0.0, 0.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 3.0, 3.0)
        val group = listOf("0", "0", "1", "0", "0", "1", "1", "0", "1", "1")

        val data = HashMap<String, List<*>>()
        data["group"] = group
        data["x"] = x

        val bins = 6
        val spec = "{" +
                "   'mapping': {" +
                "             'x': 'x'" +
                "           }," +
                "   'layers': [" +
                "               {" +
                "                  'geom':  {" +
                "                             'name': 'histogram'," +
                "                             'mapping': {" +
                "                                          'y': '..density..'," +
                "                                          'fill': 'group'" +
                "                                         }," +
                "                             'bins': " + bins + "," +
                "                             'center': 0" +
                "                           }" +
                "               }" +
                "           ]" +
                "}"

        val opts = ServerSideTestUtil.parseOptionsServerSide(spec, data)
        TestUtil.checkOptionsClientSide(opts, 1)

        assertEmptyPlotData(opts)

        val statSize = bins * 2 // two groups
        val droppedVars = listOf("..x..", "..count..", "..group..")
        checkSingleLayerData(opts, 3,
                mapOf(
                        "x" to statSize,
                        "group" to statSize,
                        "..density.." to statSize
                ),
                droppedVars
        )
    }

    @Test
    fun plot_keepXYZ__point_contour() {
        val bins = 20

        val spec = "{" +
                "   'mapping': {" +
                "             'x': 'x'," +
                "             'y': 'y'" +
                "           }," +
                "   'layers': [" +
                "               {" +
                "                  'geom':  {" +
                "                             'name': 'point'," +
                "                             'mapping': {" +
                "                                          'color': 'z'" +
                "                                        }" +
                "                           }" +
                "               }," +
                "               {" +
                "                  'geom':  {" +
                "                             'name': 'contour'," +
                "                             'mapping': {" +
                "                                          'z': 'z'" +
                "                                        }," +
                "                             'bins': " + bins +
                "                           }" +
                "               }" +
                "           ]" +
                "}"


        val opts = ServerSideTestUtil.parseOptionsServerSide(spec, TestUtil.contourData())
        TestUtil.checkOptionsClientSide(opts, 2)

        // keep X, Y, Z in shared data
        val plotData = TestUtil.getPlotData(opts)
        val numPoints = 400
        checkData(plotData, 3,
                mapOf(
                        "x" to numPoints,
                        "y" to numPoints,
                        "z" to numPoints
                ),
                emptyList()
        )

        // points layer: no layer data (identity stat)
        val pointsData = TestUtil.getLayerData(opts, 0)
        assertTrue(pointsData.isEmpty())

        // contour data
        val contourData = TestUtil.getLayerData(opts, 1)
        val contourSizeUnknown = -1
        val contourDroppedVars = listOf("z", "..level..")
        checkData(contourData, 3,
                mapOf(
                        "x" to contourSizeUnknown,
                        "y" to contourSizeUnknown,
                        "..group.." to contourSizeUnknown
                ),
                contourDroppedVars
        )
    }

    @Test
    fun `map_join with GeoDataFrame should not drop data variable`() {
        val spec = """
{
  "kind": "plot",
  "layers": [
    {
      "geom": "polygon",
      "data": {
        "name": ["A", "B", "C"],
        "value": [42, 23, 87]
      },
      "mapping": { "fill": "value" },
      "map_data_meta": {
        "geodataframe": {
          "geometry": "coord"
        }
      },
      "map": {
        "id": ["A", "B", "C"],
        "coord": [
          "{\"type\": \"Point\", \"coordinates\": [-5.0, 17.0]}",
          "{\"type\": \"Polygon\", \"coordinates\": [[[1.0, 1.0], [1.0, 9.0], [9.0, 9.0], [9.0, 1.0], [1.0, 1.0]], [[2.0, 2.0], [3.0, 2.0], [3.0, 3.0], [2.0, 3.0], [2.0, 2.0]], [[4.0, 4.0], [6.0, 4.0], [6.0, 6.0], [4.0, 6.0], [4.0, 4.0]]]}",
          "{\"type\": \"MultiPolygon\", \"coordinates\": [[[[11.0, 12.0], [13.0, 14.0], [15.0, 13.0], [11.0, 12.0]]]]}"
        ]
      },
      "map_join": ["name", "id"]
    }
  ]
}
}"""


        parsePlotSpec(spec)
            .let(ServerSideTestUtil::serverTransformWithoutEncoding)
            .also { require(!PlotConfig.isFailure(it)) { PlotConfig.getErrorMessage(it) } }
            .let(TestUtil::assertClientWontFail)

    }

    @Test
    fun `map_join with GeoDataFrame should not drop ggplot data variable`() {
        val spec = """
{
  "kind": "plot",
    "data": {
    "name": ["A", "B", "C"],
    "value": [42, 23, 87]
  },
  "layers": [
    {
      "geom": "polygon",

      "mapping": { "fill": "value" },
      "map_data_meta": {
        "geodataframe": {
          "geometry": "coord"
        }
      },
      "map": {
        "id": ["A", "B", "C"],
        "coord": [
          "{\"type\": \"Point\", \"coordinates\": [-5.0, 17.0]}",
          "{\"type\": \"Polygon\", \"coordinates\": [[[1.0, 1.0], [1.0, 9.0], [9.0, 9.0], [9.0, 1.0], [1.0, 1.0]], [[2.0, 2.0], [3.0, 2.0], [3.0, 3.0], [2.0, 3.0], [2.0, 2.0]], [[4.0, 4.0], [6.0, 4.0], [6.0, 6.0], [4.0, 6.0], [4.0, 4.0]]]}",
          "{\"type\": \"MultiPolygon\", \"coordinates\": [[[[11.0, 12.0], [13.0, 14.0], [15.0, 13.0], [11.0, 12.0]]]]}"
        ]
      },
      "map_join": ["name", "id"]
    }
  ]
}
}"""

        parsePlotSpec(spec)
            .let(ServerSideTestUtil::serverTransformWithoutEncoding)
            .also { require(!PlotConfig.isFailure(it)) { PlotConfig.getErrorMessage(it) } }
            .let(TestUtil::assertClientWontFail)

    }

    @Test
    fun `should not drop geometry column when GeoDataFrame in data`() {
        val spec = """
{
  "kind": "plot",
  "layers": [
    {
      "geom": "polygon",
      "data": {
        "id": ["A", "B", "C"],
        "coord": [
          "{\"type\": \"Point\", \"coordinates\": [-5.0, 17.0]}",
          "{\"type\": \"Polygon\", \"coordinates\": [[[1.0, 1.0], [1.0, 9.0], [9.0, 9.0], [9.0, 1.0], [1.0, 1.0]], [[2.0, 2.0], [3.0, 2.0], [3.0, 3.0], [2.0, 3.0], [2.0, 2.0]], [[4.0, 4.0], [6.0, 4.0], [6.0, 6.0], [4.0, 6.0], [4.0, 4.0]]]}",
          "{\"type\": \"MultiPolygon\", \"coordinates\": [[[[11.0, 12.0], [13.0, 14.0], [15.0, 13.0], [11.0, 12.0]]]]}"
        ]
      },
      "mapping": { "fill": "id" },
      "data_meta": {
        "geodataframe": {
          "geometry": "coord"
        }
      } 
    }
  ]
}
}"""


        parsePlotSpec(spec)
            .let(ServerSideTestUtil::serverTransformWithoutEncoding)
            .also { require(!PlotConfig.isFailure(it)) { PlotConfig.getErrorMessage(it) } }
            .let(TestUtil::assertClientWontFail)
            .assertBinding(Aes.X, POINT_X)
            .assertBinding(Aes.Y, POINT_Y)

    }

    @Test
    fun `map_join with GeoDict should not drop data variable`() {
        val spec = """
{
  "ggsize": {
    "width": 500,
    "height": 300
  },
  "kind": "plot",
  "layers": [
    {
      "geom": "polygon",
      "data": {
        "Country": ["UK", "Germany", "France"],
        "Population": [66650000.0, 83020000.0, 66990000.0]
      },
      "mapping": {"fill": "Population"},
      "map": {
        "lon": [-2.598046, -2.37832, -2.46621, -3.169335, -1.938867, -0.576562, -0.31289, 0.873632, 0.082617, -2.598046, 7.685156, 9.926367, 13.661718, 14.101171, 11.464453, 12.870703, 8.564062, 8.651953, 6.80625, 6.938085, 7.685156, -2.246484, 2.367773, 7.245703, 5.268164, 6.586523, 2.895117, 2.279882, -0.532617, -0.356835, -2.246484],
        "lat": [ 51.030349, 51.797754, 53.94575, 54.561879, 55.193929, 53.816229, 52.924809, 52.525588, 51.113188, 51.030349, 53.294124, 54.049078, 53.60816, 51.305902, 50.221916, 48.679365, 48.007575, 49.485266, 50.024691, 51.552493, 53.294124, 48.095702, 50.586036, 48.795295, 46.365136, 44.169607, 43.663114, 43.088157, 43.631315, 46.51655, 48.095702],
        "country": [ "UK", "UK", "UK", "UK", "UK", "UK", "UK", "UK", "UK", "UK", "Germany", "Germany", "Germany", "Germany", "Germany", "Germany", "Germany", "Germany", "Germany", "Germany", "Germany", "France", "France", "France", "France", "France", "France", "France", "France", "France", "France"]
      },
      "map_join": ["Country", "country"],
      "map_data_meta": {"geodict": {}},
      "alpha": 0.3
    }
  ]
}                
            """.trimIndent()

        parsePlotSpec(spec)
            .let(ServerSideTestUtil::serverTransformWithoutEncoding)
            .also { require(!PlotConfig.isFailure(it)) { PlotConfig.getErrorMessage(it) } }
            .let(TestUtil::assertClientWontFail)
    }

    @Test
    fun `should not drop data variable used in tooltips`() {
        val spec = """{
                  "kind": "plot",
                  "layers": [
                    {
                      "geom": "point",
                      "data": {
                        "x": [0,  1],
                        "name": ["a", "b"]
                      },
                      "mapping": {
                        "x": "x",
                        "y": "x"
                      },
                      "tooltips": {
                        "tooltip_lines": [
                          "${'$'}var@name"
                        ]
                      }
                    }
                  ]
        }"""

        val opts = ServerSideTestUtil.parseOptionsServerSide(spec)
        TestUtil.checkOptionsClientSide(opts, 1)

        assertEmptyPlotData(opts)
        checkSingleLayerData(
            opts, 2, mapOf("x" to 2, "name" to 2)
        )
    }
}
