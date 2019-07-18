package jetbrains.datalore.visualization.plot.config.transform.migration

import jetbrains.datalore.visualization.plot.config.PlotConfigClientSide
import jetbrains.datalore.visualization.plot.parsePlotSpec
import kotlin.test.Test
import kotlin.test.assertEquals


class MoveGeomPropertiesToLayerMigrationTest {
    @Test
    fun plotLayers() {
        val input = "{" +
                "  'layers': [" +
                "               {" +
                "                 'geom':  {" +
                "                               'name': 'point'," +
                "                               'mapping': {'x' : 'x'}" +
                "                             }" +
                "               }" +
                "               ," +
                "               {" +
                "                 'geom':  {" +
                "                               'name': 'map'," +
                "                               'mapping': {'x' : 'x'}" +
                "                             }" +
                "               }" +
                "             ]" +
                "}"

        val expected = "{" +
                "  'layers': [" +
                "               {" +
                "                 'geom': 'point'," +
                "                 'mapping': {'x' : 'x'}" +
                "               }" +
                "               ," +
                "               {" +
                "                 'geom': 'map'," +
                "                 'mapping': {'x' : 'x'}" +
                "               }" +
                "             ]" +
                "}"

        val inputSpec = parsePlotSpec(input)
        val expectedSpec = parsePlotSpec(expected)

        val transformed = PlotConfigClientSide.processTransform(inputSpec)
        assertEquals(expectedSpec, transformed)

        // if specs are in new form - nothing should happen
        val transformed2 = PlotConfigClientSide.processTransform(transformed)
        assertEquals(expectedSpec, transformed2)
    }
}