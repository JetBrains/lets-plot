/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec

class Lollipop {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic(),
            perpendicular(),
        )
    }

    private fun basic(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data' : {'x': [0, 1, 2, 3]," +
                "             'y': [1, 3, -1, 2]," +
                "             'g': ['a', 'a', 'b', 'a']" +
                "            }," +
                "   'mapping': {" +
                "                'x': 'x'," +
                "                'y': 'y'," +
                "                'color': 'g'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'Default lollipop'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'lollipop'" +
                "               }" +
                "             ]" +
                "}"

        return HashMap(parsePlotSpec(spec))
    }

    private fun perpendicular(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data' : {'x': [0, 1, 2, 3]," +
                "             'y': [3, 5, -2, 7]" +
                "            }," +
                "   'mapping': {" +
                "                'x': 'x'," +
                "                'y': 'y'" +
                "              }," +
                "   'coord': {" +
                "                'name': 'fixed'," +
                "                'ratio': 1.0" +
                "             }," +
                "   'ggtitle': {" +
                "                'text': 'Perpendicular to the line'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'abline'," +
                "                 'slope': 1," +
                "                 'intercept': 1," +
                "                 'orientation': 'y'" +
                "               }," +
                "               {" +
                "                 'geom': 'lollipop'," +
                "                 'size': 4," +
                "                 'stroke': 5," +
                "                 'linewidth': 1," +
                "                 'shape': 21," +
                "                 'color': 'blue'," +
                "                 'fill': 'red'," +
                "                 'linetype': 'dotted'," +
                "                 'alpha': 0.5," +
                "                 'slope': 1," +
                "                 'intercept': 1," +
                "                 'orientation': 'y'," +
                "                 'dir': 's'" +
                "               }" +
                "             ]" +
                "}"

        return HashMap(parsePlotSpec(spec))
    }
}