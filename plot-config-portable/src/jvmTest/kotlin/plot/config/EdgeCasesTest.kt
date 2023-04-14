/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.assertion.assertDoesNotFail
import jetbrains.datalore.plot.DemoAndTest
import jetbrains.datalore.plot.config.Option.GeomName
import jetbrains.datalore.plot.config.Option.GeomName.IMAGE
import jetbrains.datalore.plot.config.Option.GeomName.LIVE_MAP
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
    fun pointWithOnlyNullsInColorSeries() {
        val data = """
            {
                "x": [1],
                "y": [1],
                "c": [null]
            }
        """.trimIndent()

        val spec = """
             {
                "kind": "plot",
                "data": $data,
                "layers": [
                            {
                               "geom":  {
                                          "name": "point"
                                        },
                               "mapping": {
                                           "x": "x",
                                           "y": "y",
                                           "fill": "c"
                                          }
                            }
                        ]
             }
        """.trimIndent()


        val opts = parsePlotSpec(spec)
        assertDoesNotFail { DemoAndTest.createPlot(opts) }
    }

    @Test
    fun allWithNotFiniteValuesInXYSeries() {
        for (geomName in GeomName.values()) {
            if (LIVE_MAP == geomName || IMAGE == geomName) {
                continue
            }
            checkWithNaNInXYSeries(geomName)
            checkWithNullInXYSeries(geomName)
        }
    }

    @Test
    fun allWithEmptyData() {
        fun checkGeom(geom: String) {
            val spec = """
            |{
            |    'kind': 'plot',
            |    'data': { 'x': [], 'y': [] },
            |    'mapping': { 'x': 'x', 'y': 'y' },
            |    'layers': [ { 'geom':  { 'name': '$geom' } } ]
            |}""".trimMargin()

            assertDoesNotFail("geom $geom: ") { DemoAndTest.createPlot(parsePlotSpec(spec)) }
        }

        (GeomName.values() - listOf(LIVE_MAP, IMAGE)).forEach(::checkGeom)
    }

    @Test
    fun `issue681 - smooth with 2 points and stat var in tooltip`() {
        val spec = """
            |{
            |  "data": { "x": [1, 2], "y": [1, 2] },
            |  "kind": "plot",
            |  "layers": [ 
            |    { 
            |        "geom": "smooth", 
            |        "mapping": { "x": "x", "y": "y" },
            |        "tooltips": { "lines": [ "@..se.." ] }
            |    }
            |  ]
            |}""".trimMargin()

        assertDoesNotFail { DemoAndTest.createPlot(parsePlotSpec(spec)) }
    }

    @Test
    fun `issue681 - smooth 2 points`() {
        val spec = """
            |{
            |  "data": { "x": [1, 2], "y": [1, 2] },
            |  "kind": "plot",
            |  "layers": [ 
            |    { 
            |        "geom": "smooth", 
            |        "mapping": { "x": "x", "y": "y" }
            |    }
            |  ]
            |}""".trimMargin()

        assertDoesNotFail { DemoAndTest.createPlot(parsePlotSpec(spec)) }
    }

    @Test
    fun `issue681 - empty data`() {
        val spec = """
            |{
            |  "mapping": { "x": "x", "y": "y" },
            |  "kind": "plot",
            |  "layers": [
            |    { "geom": "point" },
            |    {
            |      "geom": "histogram",
            |      "mapping": { "y": "..density.." },
            |      "marginal": true,
            |      "margin_side": "t"
            |    }
            |  ]
            |}""".trimMargin()

        assertDoesNotFail { DemoAndTest.createPlot(parsePlotSpec(spec)) }
    }


    private fun checkWithNaNInXYSeries(geom: String) {
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

    private fun checkWithNullInXYSeries(geom: String) {
        val spec = """
            {
              'kind': 'plot',
              'data': {
                'x': [0, 1, 2, null, 4],
                'y': [0, 1, 4, 9, null]
              },
              'mapping': {
                'x': 'x',
                'y': 'y'
              },
              'layers': [
                {
                  'geom': '$geom'
                }
              ]
            }
        """.trimIndent()

        val plotSpec = parsePlotSpec(spec)

        assertDoesNotFail("geom $geom: ") { DemoAndTest.createPlot(plotSpec) }
    }

    @Test
    fun transformLog10WithNegativeValues() {
        // issue #292

        val spec = """
            {
             'kind': 'plot',
             'mapping': {'x': 'x', 'y': 'y'},
             'scales': [{'aesthetic': 'color',
               'trans': 'log10',
               'scale_mapper_kind': 'color_gradient'}],
             'layers': [
             {'geom': 'point',
               'mapping': {'color': 'c'}
                }]}
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = mapOf(
            "x" to listOf(0, 1, 2, 3, 4),
            "y" to listOf(0, 1, 4, 9, 12),
            "c" to listOf(-1, 0, 0.01, 1, 81),
        )

        assertDoesNotFail("log10 with negative data: ") { DemoAndTest.createPlot(plotSpec) }
    }
}
