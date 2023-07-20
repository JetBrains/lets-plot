/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.transform.migration

import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.core.spec.front.PlotConfigFrontend
import kotlin.test.Test
import kotlin.test.assertEquals


class MoveGeomPropertiesToLayerMigrationTest {
    @Test
    fun plotLayers() {
        val input = "{" +
                "  'kind': 'plot'," +
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
                "  'kind': 'plot'," +
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

        val transformed = PlotConfigFrontend.processTransform(inputSpec)
        assertEquals(expectedSpec, transformed)

        // if specs are in new form - nothing should happen
        val transformed2 = PlotConfigFrontend.processTransform(transformed)
        assertEquals(expectedSpec, transformed2)
    }
}