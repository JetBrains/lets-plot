/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import demoAndTestShared.parsePlotSpec

open class BarOverlaidPlot {

    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            overBars(),
            overBarsAndPoint(),
            overBarsAndLine()
        )
    }

    companion object {

        private fun overBars(): MutableMap<String, Any> {
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