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
        )
    }

    private fun basic(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data' : {'x': [null, 0, 1, 2, 3, 4, 1, 2, 3, 4]," +
                "             'y': [0, 0, 0, 0, 0, null, 1, 1, 1, 1]," +
                "             'rh': [1.0, 0.6, 1.5, 0.9, 1.2, 1.0, 0.6, 0.8, 0.4, 0.6]" +
                "            }," +
                "   'mapping': {" +
                "                'x': 'x'," +
                "                'y': 'y'," +
                "                'ridgeheight': 'rh'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'Basic demo'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'area_ridges'" +
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
                "             'rh': [0.2, 0.4, 0.2, 0.1, 0.1, 0.2, 0.3, 0.2, 0.2, 0.2, 0.3, 0.1]," +
                "             'g': ['A', 'A', 'B', 'B', 'C', 'C', 'A', 'A', 'B', 'B', 'C', 'C']" +
                "            }," +
                "   'mapping': {" +
                "                'x': 'x'," +
                "                'y': 'y'," +
                "                'ridgeheight': 'rh'," +
                "                'fill': 'g'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'Additional grouping'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'area_ridges'," +
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
                "             'h': [0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5]," +
                "             'rh': [0.3, 0.75, 0.45, 0.6, 0.3, 0.4, 0.2, 0.3]" +
                "            }," +
                "   'mapping': {" +
                "                'x': 'x'," +
                "                'y': 'y'," +
                "                'height': 'h'," +
                "                'ridgeheight': 'rh'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'Flip coordinates'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'area_ridges'," +
                "                 'scale': 2," +
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
                "             'rh': [0.6, 1.5, -1.0, 0.9, 1.2, -0.1, -1.2, 1.2, 0.1, 1.2]" +
                "            }," +
                "   'mapping': {" +
                "                'x': 'x'," +
                "                'y': 'y'," +
                "                'ridgeheight': 'rh'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'Negative height'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'area_ridges'" +
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
                "             'rh': [1, 4, 9, 16, 25, 36, 49, 64, 81, 100, 109, 113, 114, 113, 109, 100, 81, 64, 49, 36, 25, 16, 9, 4, 1]" +
                "            }," +
                "   'mapping': {" +
                "                'x': 'x'," +
                "                'y': 'y'," +
                "                'ridgeheight': 'rh'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'Quantiles'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'area_ridges'," +
                "                 'draw_quantiles': [0.25, 0.5, 0.75]," +
                "                 'color': 'black'," +
                "                 'fill': 'white'" +
                "               }" +
                "             ]" +
                "}"

        return HashMap(parsePlotSpec(spec))

    }
}