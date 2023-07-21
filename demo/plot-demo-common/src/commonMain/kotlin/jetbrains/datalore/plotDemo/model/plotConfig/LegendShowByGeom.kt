/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import demoAndTestShared.parsePlotSpec

open class LegendShowByGeom {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            defaultLegend(),
            noLinesLegend(),
            noBothLegends()
        )
    }


    companion object {
        private const val LINES_DATA = "   {" +
                "      'c': ['Line A', 'Line A', 'Line A', 'Line B', 'Line B', 'Line B']," +
                "      'x': [1, 2, 3, 1, 2, 3]," +
                "      'y': [1, 2, 1.5, 1.5, 0.5, 2.5]" +
                "   }"

        private const val POINTS_DATA = "   {" +
                "      'c': ['Point X', 'Point Y', 'Point Z']," +
                "      'x': [1.5, 2, 3]," +
                "      'y': [2, 1.5, 1]" +
                "   }"

        private fun linesLayer(disableLegend: Boolean): String {

            return "{" +
                    (if (disableLegend) "'show_legend': false," else "") +
                    "  'geom':  'line'," +
                    "  'stat': 'identity'," +
                    "  'data': " + LINES_DATA + "," +
                    "  'mapping': {" +
                    "            'x': 'x'," +
                    "            'y': 'y'," +
                    "            'color': 'c'" +
                    "             }" +
                    "}"
        }

        private fun pointsLayer(disableLegend: Boolean): String {

            return "{" +
                    (if (disableLegend) "'show_legend': false," else "") +
                    "  'geom':  'point'," +
                    "  'stat': 'identity'," +
                    "  'data': " + POINTS_DATA + "," +
                    "  'mapping': {" +
                    "            'x': 'x'," +
                    "            'y': 'y'," +
                    "            'color': 'c'" +
                    "          }," +
                    "  'size': 5" +
                    "}"
        }


        //===========================


        fun defaultLegend(): MutableMap<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "'layers': [" +
                    linesLayer(false) +
                    "," +
                    pointsLayer(false) +
                    "  ]" +
                    "}"

            return parsePlotSpec(spec)
        }

        fun noLinesLegend(): MutableMap<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "'layers': [" +
                    linesLayer(true) +
                    "," +
                    pointsLayer(false) +
                    "  ]" +
                    "}"

            return parsePlotSpec(spec)
        }

        fun noBothLegends(): MutableMap<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "'layers': [" +
                    linesLayer(true) +
                    "," +
                    pointsLayer(true) +
                    "  ]" +
                    "}"

            return parsePlotSpec(spec)
        }
    }
}
