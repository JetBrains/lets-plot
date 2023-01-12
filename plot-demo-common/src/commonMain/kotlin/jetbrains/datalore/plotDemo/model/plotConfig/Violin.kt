/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.data.Iris

class Violin {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic(),
            withNan(),
            withGroups(),
            halfViolins(),
        )
    }

    private fun basic(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'mapping': {" +
                "                'x': 'target'," +
                "                'y': 'sepal length (cm)'," +
                "                'fill': 'target'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'Basic demo'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'violin'," +
                "                 'alpha': 0.7," +
                "                 'quantiles': [0.1, 0.5, 0.9]," +
                "                 'quantile_lines': true" +
                "               }" +
                "             ]" +
                "}"

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec

    }

    private fun withNan(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data' : {'class': [0, 0, 0, null, 1, 1, 1, 1]," +
                "             'value': [0, 0, 2, 2, 1, 1, 3, null]" +
                "            }," +
                "   'mapping': {" +
                "                'x': 'class'," +
                "                'y': 'value'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'Violin demo'," +
                "                'subtitle': 'NaNs in data'" +
                "              }," +
                "   'theme': {" +
                "              'title': {" +
                "                         'family': 'Verdana'," +
                "                         'face': 'bold_italic'," +
                "                         'blank': false" +
                "                       }," +
                "              'text_width_scale': 0.9" +
                "            }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'violin'" +
                "               }" +
                "             ]" +
                "}"

        return HashMap(parsePlotSpec(spec))

    }

    private fun withGroups(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'data' : {'class': ['A', 'A', 'A', 'A', 'A', 'A', 'B', 'B', 'B', 'B', 'B', 'B']," +
                "             'group': ['x', 'x', 'x', 'y', 'y', 'y', 'x', 'x', 'x', 'x', 'y', 'y']," +
                "             'value': [0, 0, 2, 1, 1, 3, 1, 3, 3, 5, 2, 4]" +
                "            }," +
                "   'mapping': {" +
                "                'x': 'class'," +
                "                'y': 'value'," +
                "                'fill': 'group'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'Violin demo'" +
                "              }," +
                "   'caption': {" +
                "                'text': 'Additional grouping'" +
                "              }," +
                "   'theme': {" +
                "              'title': {" +
                "                         'family': 'Courier'," +
                "                         'size': 18," +
                "                         'monospaced': true," +
                "                         'blank': false" +
                "                       }" +
                "            }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'violin'," +
                "                 'quantile_lines': true" +
                "               }" +
                "             ]" +
                "}"

        return HashMap(parsePlotSpec(spec))

    }

    private fun halfViolins(): MutableMap<String, Any> {
        val spec = "{" +
                "   'kind': 'plot'," +
                "   'mapping': {" +
                "                'x': 'target'," +
                "                'y': 'sepal length (cm)'," +
                "                'fill': 'target'" +
                "              }," +
                "   'ggtitle': {" +
                "                'text': 'Ridgeline'" +
                "              }," +
                "   'layers': [" +
                "               {" +
                "                 'geom': 'violin'," +
                "                 'show_half': -1," +
                "                 'quantile_lines': true," +
                "                 'trim': false" +
                "               }," +
                "               {" +
                "                 'geom': 'violin'," +
                "                 'show_half': 1," +
                "                 'trim': false," +
                "                 'quantiles': [0.1, 0.5, 0.9]," +
                "                 'fill': '#ffffb2'," +
                "                 'mapping': {" +
                "                   'color': '..quantile..'" +
                "                 }" +
                "               }" +
                "             ]," +
                "   'scales': [" +
                "               {" +
                "                 'aesthetic': 'color'," +
                "                 'low': '#d73027'," +
                "                 'high': '#1a9850'," +
                "                 'scale_mapper_kind': 'color_gradient'" +
                "               }" +
                "             ]" +
                "}"

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = Iris.df
        return plotSpec

    }
}