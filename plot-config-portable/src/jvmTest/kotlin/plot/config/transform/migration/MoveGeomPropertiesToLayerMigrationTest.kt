/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.transform.migration

import demoAndTestShared.parsePlotSpec
import jetbrains.datalore.plot.config.PlotConfigClientSide
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

        val transformed = PlotConfigClientSide.processTransform(inputSpec)
        assertEquals(expectedSpec, transformed)

        // if specs are in new form - nothing should happen
        val transformed2 = PlotConfigClientSide.processTransform(transformed)
        assertEquals(expectedSpec, transformed2)
    }
}