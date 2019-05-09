package jetbrains.datalore.visualization.plot.gog.server.config

import jetbrains.datalore.visualization.plot.gog.config.TestUtil
import jetbrains.datalore.visualization.plot.gog.core.data.TransformVar
import org.junit.Assert.*
import org.junit.Test
import java.util.*
import java.util.Arrays.asList
import java.util.Collections.emptyList

class DropUnusedDataTest {

    private fun assertVarPresent(varName: String, dataSize: Int, data: Map<String, Any>) {
        assertTrue("Not found '$varName'", data.containsKey(varName))
        assertTrue("Not a list '$varName'", data[varName] is List<*>)
        if (dataSize > 0) {
            assertEquals("Size '$varName'", dataSize, (data[varName] as List<*>).size)
        }
    }

    private fun assertVarNotPresent(varName: String, data: Map<String, Any>) {
        assertTrue("Present '$varName'", !data.containsKey(varName))
    }

    private fun assertNoTransformVars(data: Map<String, Any>) {
        for (name in data.keys) {
            assertFalse("Transform variable '$name'", TransformVar.isTransformVar(name))
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

    init {
        val x = asList(0.0, 0.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 3.0, 3.0)
        val group = asList("0", "0", "1", "0", "0", "1", "1", "0", "1", "1")
        val facetX = asList("a", "a", "b", "a", "a", "b", "b", "a", "b", "b")

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
        val droppedVars = asList("..x..", "..density..", "..group..")
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
    fun defaultMapping_point() {
        val data = "{" +
                "  'x': [0,0,1,1]" +
                "}"

        val spec = "{" +
                "   'layers': [" +
                "               {" +
                "                  'geom':  {" +
                "                             'name': 'point'," +
                "                             'data': " + data +
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
        val droppedVars = asList("..group..")
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
        val droppedVars = asList("c", "..x..", "..group..")
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
        val droppedVars = asList("..x..", "..group..")
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
        val droppedVars = asList("z", "..level..")
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
        val droppedVars = asList("z")
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
        val droppedVars = asList("..x..", "..group..")
        checkData(layerData, 2,
                mapOf(
                        "time" to statSize,
                        "..count.." to statSize
                ),
                droppedVars
        )

        assertEquals(asList("Lunch", "Dinner"), layerData["time"])
    }

    @Test
    fun plot_clear_layer_addDensity_histogram() {
        val x = asList(0.0, 0.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 3.0, 3.0)
        val group = asList("0", "0", "1", "0", "0", "1", "1", "0", "1", "1")

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
        val droppedVars = asList("..x..", "..count..", "..group..")
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
        val contourDroppedVars = asList("z", "..level..")
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
    fun shouldNotDropMapIdMappingData() {
        val spec = "{" +
                "   'layers': [" +
                "               {" +
                "                  'data': {" +
                "                             'name': ['New York']" +
                "                          }," +
                "                  'geom':  {" +
                "                             'name': 'polygon'," +
                "                             'mapping': {" +
                "                                        'map_id': 'name'" +
                "                                      }" +
                "                           }" +
                "               }" +
                "           ]" +
                "}"


        val opts = ServerSideTestUtil.parseOptionsServerSide(spec,
                mapOf(
                        "name" to asList("New York"))
        )

        TestUtil.checkOptionsClientSide(opts, 1)

        val unknownSize = -1
        val droppedVars = emptyList<String>()
        checkSingleLayerData(opts, 1,
                mapOf(
                        "name" to unknownSize
                ),
                droppedVars
        )
    }
}
