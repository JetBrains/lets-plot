/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec

class AreaRidges {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic(),
            withGroups(),
            flipCoord(),
            withNegativeHeight(),
            withQuantiles(),
            withStat()
        )
    }

    private fun basic(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data' : {'x': [null, 0, 1, 2, 3, 4, 1, 2, 3, 4]," +
                "             'y': [0, 0, 0, 0, 0, null, 1, 1, 1, 1]," +
                "             'h': [0.5, 0.3, 1.0, 0.4, 0.6, 0.5, 0.3, 0.4, 0.2, 0.3]" +
                "            }," +
                "   'mapping': {" +
                "                'x': 'x'," +
                "                'y': 'y'," +
                "                'height': 'h'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'Basic demo'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'area_ridges'," +
                "                 'stat': 'identity'" +
                "               }" +
                "             ]" +
                "}"

        return HashMap(parsePlotSpec(spec))

    }

    private fun withGroups(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data' : {'x': [0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3]," +
                "             'y': [0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2]," +
                "             'h': [0.1, 0.2, 0.1, 0.1, 0.1, 0.1, 0.2, 0.1, 0.1, 0.1, 0.2, 0.1]," +
                "             'g': ['A', 'A', 'B', 'B', 'C', 'C', 'A', 'A', 'B', 'B', 'C', 'C']" +
                "            }," +
                "   'mapping': {" +
                "                'x': 'x'," +
                "                'y': 'y'," +
                "                'height': 'h'," +
                "                'fill': 'g'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'Additional grouping'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'area_ridges'," +
                "                 'stat': 'identity'," +
                "                 'color': 'black'" +
                "               }" +
                "             ]" +
                "}"

        return HashMap(parsePlotSpec(spec))

    }

    private fun flipCoord(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data' : {'x': [0, 1, 2, 3, 1, 2, 3, 4]," +
                "             'y': [0, 0, 0, 0, 0.5, 0.5, 0.5, 0.5]," +
                "             'h': [0.6, 2.0, 0.8, 1.2, 0.6, 0.8, 0.4, 0.6]" +
                "            }," +
                "   'mapping': {" +
                "                'x': 'x'," +
                "                'y': 'y'," +
                "                'height': 'h'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'Flip coordinates'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'area_ridges'," +
                "                 'stat': 'identity'," +
                "                 'scale': 0.5," +
                "                 'color': 'white'" +
                "               }" +
                "             ]," +
                "   'coord': {" +
                "              'name': 'flip'," +
                "              'flip': true" +
                "            }" +
                "}"

        return HashMap(parsePlotSpec(spec))

    }

    private fun withNegativeHeight(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data' : {'x': [0, 1, 1.5, 2, 3, 4, 5, 6, 7, 8]," +
                "             'y': [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]," +
                "             'h': [0.3, 1.0, -0.5, 0.4, 0.6, -0.1, -0.6, 0.6, 0.1, 0.6]" +
                "            }," +
                "   'mapping': {" +
                "                'x': 'x'," +
                "                'y': 'y'," +
                "                'height': 'h'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'Negative height'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'area_ridges'," +
                "                 'min_height': -0.5," +
                "                 'stat': 'identity'" +
                "               }" +
                "             ]" +
                "}"

        return HashMap(parsePlotSpec(spec))

    }

    private fun withQuantiles(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data' : {'x': [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24]," +
                "             'y': [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]," +
                "             'h': [0.01, 0.04, 0.09, 0.16, 0.25, 0.36, 0.49, 0.64, 0.81, 1.00, 1.09, 1.13, 1.14, 1.13, 1.09, 1.00, 0.81, 0.64, 0.49, 0.36, 0.25, 0.16, 0.09, 0.04, 0.01]" +
                "            }," +
                "   'mapping': {" +
                "                'x': 'x'," +
                "                'y': 'y'," +
                "                'height': 'h'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'Quantiles'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'area_ridges'," +
                "                 'stat': 'identity'," +
                "                 'draw_quantiles': [0.25, 0.5, 0.75]," +
                "                 'color': 'black'," +
                "                 'fill': 'white'" +
                "               }" +
                "             ]" +
                "}"

        return HashMap(parsePlotSpec(spec))

    }

    private fun withStat(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data' : {'x': [0, 0, 1, 1, 0, 0, 0, 1]," +
                "             'y': [0, 0, 0, 0, 1, 1, 1, 1]" +
                "            }," +
                "   'mapping': {" +
                "                'x': 'x'," +
                "                'y': 'y'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'With density ridges stat'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'area_ridges'," +
                "                 'draw_quantiles': [0.1, 0.5, 0.9]," +
                "                 'scale': 0.75," +
                "                 'trim': false," +
                "                 'color': 'white'" +
                "               }" +
                "             ]" +
                "}"

        return HashMap(parsePlotSpec(spec))

    }
}