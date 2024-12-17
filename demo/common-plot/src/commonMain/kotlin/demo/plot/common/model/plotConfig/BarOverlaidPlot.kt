/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

open class BarOverlaidPlot {

    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            overBars(),
            overBarsAndPoint(),
            overBarsAndLine(),
            histogram()
        )
    }

    companion object {

        private fun histogram(): MutableMap<String, Any> {
            val spec = """
                |{
                |  "data": {
                |    "x": [ 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0, 33.0, 34.0, 35.0, 36.0, 37.0, 38.0, 39.0 ],
                |    "g": [ 0.0, 1.0, 2.0, 3.0, 0.0, 1.0, 2.0, 3.0, 0.0, 1.0, 2.0, 3.0, 0.0, 1.0, 2.0, 3.0, 0.0, 1.0, 2.0, 3.0, 0.0, 1.0, 2.0, 3.0, 0.0, 1.0, 2.0, 3.0, 0.0, 1.0, 2.0, 3.0, 0.0, 1.0, 2.0, 3.0, 0.0, 1.0, 2.0, 3.0 ]
                |  },
                |  "data_meta": {
                |    "series_annotations": [
                |      { "type": "int", "column": "x" },
                |      { "type": "int", "column": "g" }
                |    ]
                |  },
                |  "ggsize": { "width": 400.0, "height": 200.0 },
                |  "kind": "plot",
                |  "layers": [
                |    {
                |      "geom": "histogram",
                |      "mapping": { "x": "x", "group": "g", "color": "g", "fill": "g" },
                |      "tooltips": { "lines": [ "foo", "bar", "baz" ] },
                |      "data_meta": {
                |        "mapping_annotations": [
                |          { "parameters": { "label": "g" }, "aes": "color", "annotation": "as_discrete" },
                |          { "parameters": { "label": "g" }, "aes": "fill", "annotation": "as_discrete" }
                |        ]
                |      }
                |    }
                |  ]
                |}                    
            """.trimMargin()

            return parsePlotSpec(spec)
        }

        private fun overBars(): MutableMap<String, Any> {
            val spec = """
            |{'kind': 'plot',
            | 'data': {'letter': ['A', 'B', 'C', 'D', 'E', 'F', 'A', 'B', 'C', 'D', 'E', 'F','A', 'B', 'C', 'D', 'E', 'F', 'A', 'B', 'C', 'D', 'E', 'F'],
            |          'x': [0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 1.5],
            |          'y':  [5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 3.0, 3.0, 3.0, 3.0, 3.0, 3.0, 2.0, 2.0, 2.0, 2.0, 2.0, 60.0]},
            | 'mapping': {'x': 'x',
            |             'y': 'y'},
            | 'scales': [],
            | 'layers': [
            |       {'geom': 'histogram',
            |        'position': {'name': 'stack'},
            |        'color': 'black'
            |       }
            |   ]
            |}
            """.trimMargin()
            return parsePlotSpec(spec)
        }

        private fun overBarsAndPoint(): MutableMap<String, Any> {
            val spec = """
            |{'kind': 'plot',
            | 'data': {'letter': ['A', 'B', 'C', 'D', 'E', 'F', 'A', 'B', 'C', 'D', 'E', 'F','A', 'B', 'C', 'D', 'E', 'F', 'A', 'B', 'C', 'D', 'E', 'F'],
            |          'x': [0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 1.5],
            |          'y':  [5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 3.0, 3.0, 3.0, 3.0, 3.0, 3.0, 2.0, 2.0, 2.0, 2.0, 2.0, 60.0]},
            | 'mapping': {'x': 'x',
            |             'y': 'y',  
            |             'color': 'letter'},
            | 'scales': [],
            | 'layers': [
            |       {'geom': 'bar',
            |        'mapping': {'fill': 'letter'},
            |        'position': {'name': 'stack'},
            |        'color': 'black',
            |        'stat': 'identity'
            |       }
            |      ,{'geom': 'point',
            |         'x' : 1.5,
            |         'y' : 80.0
            |       }
            |   ]
            |}
            """.trimMargin()
            return parsePlotSpec(spec)
        }

        private fun overBarsAndLine(): MutableMap<String, Any> {
            val spec = """
            |{'kind': 'plot',
            | 'data': {'letter': ['A', 'B', 'C', 'D', 'E', 'F', 'A', 'B', 'C', 'D', 'E', 'F','A', 'B', 'C', 'D', 'E', 'F', 'A', 'B', 'C', 'D', 'E', 'F'],
            |          'x': [0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 1.5],
            |          'y':  [5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 3.0, 3.0, 3.0, 3.0, 3.0, 3.0, 2.0, 2.0, 2.0, 2.0, 2.0, 60.0]},
            | 'scales': [],
            | 'layers': [
            |       {'geom': 'bar',
            |        'mapping': {'x': 'x',  'y': 'y',  'color': 'letter', 'fill': 'letter'},
            |        'position': {'name': 'stack'},
            |        'color': 'black',
            |        'stat': 'identity'
            |       },
            |       {'geom': 'line',
            |        'position': {'name': 'dodge', 'width': 0.1},
            |         'mapping': {
            |         'x': [1.0, 2.0, 1.0, 2.0, 1.0, 2.0],
            |         'y': [80.0, 80.0, 78.0, 78.0, 72.0, 72.0]}
            |       }
            |   ]
            |}
            """.trimMargin()
            return parsePlotSpec(spec)
        }

    }
}