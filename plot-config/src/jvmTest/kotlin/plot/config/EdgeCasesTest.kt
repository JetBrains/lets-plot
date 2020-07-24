/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.assertion.assertDoesNotFail
import jetbrains.datalore.plot.DemoAndTest
import jetbrains.datalore.plot.config.Option.GeomName
import jetbrains.datalore.plot.parsePlotSpec
import kotlin.test.Test


class EdgeCasesTest {

    @Test
    fun histogramWithOnlyNullsInXSerie() {
        val data = "{" +
                "  'x': [null, null]" +
                "}"

        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data': " + data +
                "           ," +
                "   'layers': [" +
                "               {" +
                "                  'geom':  {" +
                "                             'name': 'histogram'" +
                "                           }" +
                "                   ," +
                "                  'mapping': {" +
                "                              'x': 'x'" +
                "                             }" +
                "               }" +
                "           ]" +
                "}"

        val opts = parsePlotSpec(spec)
        assertDoesNotFail { DemoAndTest.createPlot(opts) }
    }

    @Test
    fun pointWithOnlyNullsInXYSeries() {
        val data = "{" +
                "  'x': [null]," +
                "  'y': [null]" +
                "}"

        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data': " + data +
                "           ," +
                "   'layers': [" +
                "               {" +
                "                  'geom':  {" +
                "                             'name': 'point'" +
                "                           }" +
                "                   ," +
                "                  'mapping': {" +
                "                              'x': 'x'," +
                "                              'y': 'y'" +
                "                             }" +
                "               }" +
                "           ]" +
                "}"

        val opts = parsePlotSpec(spec)
        assertDoesNotFail { DemoAndTest.createPlot(opts) }
    }

    @Test
    fun allWithNaNInXYSeries() {
        for (geomName in GeomName.values()) {
            if (GeomName.LIVE_MAP == geomName || GeomName.IMAGE == geomName) {
                continue
            }
            checkWithNaNInXYSeries(geomName)
        }
    }

    internal fun checkWithNaNInXYSeries(geom: String) {
        val spec = "{" +
                "   'kind': 'plot'," +
                //"   'data': " + data +
                //"           ," +
                "   'layers': [" +
                "               {" +
                "                  'geom':  {" +
                "                             'name': '" + geom + "'" +
                "                           }" +
                "                   ," +
                "                  'mapping': {" +
                "                              'x': 'x'," +
                "                              'y': 'y'" +
                "                             }" +
                "               }" +
                "           ]" +
                "}"

        val plotSpec = parsePlotSpec(spec)

        val data = mapOf(
            "x" to listOf(0.0, Double.NaN, 1.0, 2.0),
            "y" to listOf(0.0, Double.NaN, 1.0, 2.0)
        )

        plotSpec["data"] = data
        assertDoesNotFail("geom $geom: ") { DemoAndTest.createPlot(plotSpec) }
    }
}
