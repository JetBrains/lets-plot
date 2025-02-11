/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

// see: www.cookbook-r.com/Graphs/Plotting_means_and_error_bars_(ggplot2)
@Suppress("DuplicatedCode")
class ErrorBar {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            withLinesAndPoints(),
            errorbar(),
            pointrange(),
            linerange(),
            withBars(), horizontalWithBars(),
            horizontalErrorBar(), horizontalErrorBarFlipped(),
            customWidth(20.0, "px")
        )
    }

    private fun withLinesAndPoints(): MutableMap<String, Any> {
        val spec = """
            |{'kind': 'plot',
            | 'data': {'supp': ['OJ', 'OJ', 'OJ', 'VC', 'VC', 'VC'],
            |           'dose': [0.5, 1.0, 2.0, 0.5, 1.0, 2.0],
            |           'len': [13.23, 22.7, 26.06, 7.98, 16.77, 26.14],
            |           'se': [2.4, 1.9, 2.05, 2.74, 1.51, 1.79]},
            | 'mapping': {'x': 'dose', 'y': 'len', 'color': 'supp'},
            | 'scales': [],
            | 'layers': [
            |       {'geom': 'errorbar',
            |        'mapping': {
            |         'ymin': [10.83, 20.8, 24.0, 5.24, 15.26, 24.35],
            |         'ymax': [15.63, 24.6, 28.11, 10.72, 18.28, 27.93],
            |         'group': 'supp'},
            |        'position': {'name': 'dodge', 'width': 0.1},
            |        'color': 'black',
            |        'width': 0.1
            |       },
            |       {'geom': 'line',
            |        'position': {'name': 'dodge', 'width': 0.1}
            |       },
            |       {'geom': 'point',
            |        'position': {'name': 'dodge', 'width': 0.1},
            |        'size': 5
            |       }
            |   ]
            |}
            """.trimMargin()
        return parsePlotSpec(spec)
    }

    private fun errorbar(): MutableMap<String, Any> {
        val spec = """
            |{'kind': 'plot',
            | 'ggtitle'  : {'text' : 'errorbar'},
            | 'data': {'supp': ['OJ', 'OJ', 'OJ', 'VC', 'VC', 'VC'],
            |           'dose': [0.5, 1.0, 2.0, 0.5, 1.0, 2.0],
            |           'len': [13.23, 22.7, 26.06, 7.98, 16.77, 26.14],
            |           'se': [2.4, 1.9, 2.05, 2.74, 1.51, 1.79]},
            | 'mapping': {'x': 'dose', 'y': 'len', 'color': 'supp'},
            | 'scales': [],
            | 'layers': [
            |       {'geom': 'errorbar',
            |        'mapping': {
            |         'ymin': [10.83, 20.8, 24.0, 5.24, 15.26, 24.35],
            |         'ymax': [15.63, 24.6, 28.11, 10.72, 18.28, 27.93]},
            |        'position': {'name': 'dodge', 'width': 0.1},
            |        'width': 0.1
            |       },
            |       {'geom': 'line',
            |        'position': {'name': 'dodge', 'width': 0.1}
            |       },
            |       {'geom': 'point',
            |        'position': {'name': 'dodge', 'width': 0.1},
            |        'size': 2
            |       }
            |   ]
            |}
            """.trimMargin()
        return parsePlotSpec(spec)
    }

    private fun pointrange(): MutableMap<String, Any> {
        val spec = """
            |{'kind': 'plot',
            | 'ggtitle'  : {'text' : 'pointrange'},
            | 'data': {'supp': ['OJ', 'OJ', 'OJ', 'VC', 'VC', 'VC'],
            |           'dose': [0.5, 1.0, 2.0, 0.5, 1.0, 2.0],
            |           'len': [13.23, 22.7, 26.06, 7.98, 16.77, 26.14],
            |           'se': [2.4, 1.9, 2.05, 2.74, 1.51, 1.79]},
            | 'mapping': {'x': 'dose', 'y': 'len', 'color': 'supp'},
            | 'scales': [],
            | 'layers': [
            |       {'geom': 'pointrange',
            |        'mapping': {
            |         'ymin': [10.83, 20.8, 24.0, 5.24, 15.26, 24.35],
            |         'ymax': [15.63, 24.6, 28.11, 10.72, 18.28, 27.93],
            |         'group': 'supp'},
            |         'position': {'name': 'dodge', 'width': 0.1},
            |         'color': 'black',
            |         'size': 1.0
            |       },
            |       {'geom': 'line',
            |        'position': {'name': 'dodge', 'width': 0.1}
            |       }
            |   ]
            |}
            """.trimMargin()
        return parsePlotSpec(spec)
    }

    private fun linerange(): MutableMap<String, Any> {
        val spec = """
            |{'kind': 'plot',
            | 'ggtitle'  : {'text' : 'linerange'},
            | 'data': {'supp': ['OJ', 'OJ', 'OJ', 'VC', 'VC', 'VC'],
            |           'dose': [0.5, 1.0, 2.0, 0.5, 1.0, 2.0],
            |           'len': [13.23, 22.7, 26.06, 7.98, 16.77, 26.14],
            |           'se': [2.4, 1.9, 2.05, 2.74, 1.51, 1.79]},
            | 'mapping': {'x': 'dose', 'y': 'len', 'color': 'supp'},
            | 'scales': [],
            | 'layers': [
            |       {'geom': 'linerange',
            |        'mapping': {
            |         'ymin': [10.83, 20.8, 24.0, 5.24, 15.26, 24.35],
            |         'ymax': [15.63, 24.6, 28.11, 10.72, 18.28, 27.93]},
            |        'position': {'name': 'dodge', 'width': 0.1},
            |        'size': 5
            |       },
            |       {'geom': 'line',
            |        'position': {'name': 'dodge', 'width': 0.1}
            |       },
            |       {'geom': 'point',
            |        'position': {'name': 'dodge', 'width': 0.1},
            |        'size': 2
            |       }
            |   ]
            |}
            """.trimMargin()
        return parsePlotSpec(spec)
    }

    private fun withBars(): MutableMap<String, Any> {
        val spec = """
            |{'kind': 'plot',
            | 'data': {'supp': ['OJ', 'OJ', 'OJ', 'VC', 'VC', 'VC'],
            |           'dose': [0.5, 1.0, 2.0, 0.5, 1.0, 2.0],
            |           'len': [13.23, 22.7, 26.06, 7.98, 16.77, 26.14],
            |           'se': [2.4, 1.9, 2.05, 2.74, 1.51, 1.79]},
            | 'mapping': {'x': 'dose', 'y': 'len', 'color': 'supp'},
            | 'scales': [],
            | 'layers': [
            |       {'geom': 'bar',
            |        'mapping': {'fill': 'supp'},
            |        'position': {'name': 'dodge'},
            |        'color': 'black',
            |        'stat': 'identity'
            |       },
            |       {'geom': 'errorbar',
            |        'mapping': {
            |         'ymin': [10.83, 20.8, 24.0, 5.24, 15.26, 24.35],
            |         'ymax': [15.63, 24.6, 28.11, 10.72, 18.28, 27.93],
            |         'group': 'supp'},
            |        'position': {'name': 'dodge', 'width': 0.9},
            |        'color': 'black',
            |        'width': 0.1
            |       }
            |   ]
            |}
            """.trimMargin()
        return parsePlotSpec(spec)
    }

    private fun horizontalWithBars(): MutableMap<String, Any> {
        val spec = """
            |{'kind': 'plot',
            | 'data': {'supp': ['OJ', 'OJ', 'OJ', 'VC', 'VC', 'VC'],
            |           'dose': [0.5, 1.0, 2.0, 0.5, 1.0, 2.0],
            |           'len': [13.23, 22.7, 26.06, 7.98, 16.77, 26.14],
            |           'se': [2.4, 1.9, 2.05, 2.74, 1.51, 1.79]},
            | 'mapping': {'y': 'dose', 'x': 'len', 'color': 'supp'},
            | 'layers': [
            |       {'geom': 'bar',
            |        'mapping': {'fill': 'supp'},
            |        'position': {'name': 'dodge'},
            |        'color': 'black',
            |        'stat': 'identity',
            |        'orientation': 'y'
            |       },
            |       {'geom': 'errorbar',
            |        'mapping': {
            |         'xmin': [10.83, 20.8, 24.0, 5.24, 15.26, 24.35],
            |         'xmax': [15.63, 24.6, 28.11, 10.72, 18.28, 27.93],
            |         'group': 'supp'},
            |        'position': {'name': 'dodgev', 'height': 0.9},
            |        'color': 'black',
            |        'height': 0.1
            |       }
            |   ]
            |}
            """.trimMargin()
        return parsePlotSpec(spec)
    }

    private fun horizontalErrorBar(): MutableMap<String, Any> {
        val spec = """
            |{'kind': 'plot',
            | 'ggtitle'  : {'text' : 'Horizontal errorbar'},
            | 'data': {'supp': ['OJ', 'OJ', 'OJ', 'VC', 'VC', 'VC'],
            |          'dose': [0.5, 1.0, 2.0, 0.5, 1.0, 2.0]},
            | 'mapping': {
            |     'y': 'dose', 'color': 'supp',
            |     'xmin': [10.83, 20.8, 24.0, 5.24, 15.26, 24.35],
            |     'xmax': [15.63, 24.6, 28.11, 10.72, 18.28, 27.93]
            |  },
            | 'layers': [
            |       {'geom': 'errorbar',
            |        'position': { 'name': 'dodgev', 'height': 0.3 },
            |        'height': 0.2
            |       }
            |   ]
            |}
            """.trimMargin()
        return parsePlotSpec(spec)
    }

    private fun horizontalErrorBarFlipped(): MutableMap<String, Any> {
        val spec = """
            |{'kind': 'plot',
            | 'ggtitle'  : {'text' : 'Horizontal errorbar + coord_flip()'},
            | 'data': {'supp': ['OJ', 'OJ', 'OJ', 'VC', 'VC', 'VC'],
            |          'dose': [0.5, 1.0, 2.0, 0.5, 1.0, 2.0]},
            | 'mapping': {
            |     'y': 'dose', 'color': 'supp',
            |     'xmin': [10.83, 20.8, 24.0, 5.24, 15.26, 24.35],
            |     'xmax': [15.63, 24.6, 28.11, 10.72, 18.28, 27.93]
            |  },
            | 'coord': {'name': 'flip', 'flip': true},
            | 'layers': [
            |       {'geom': 'errorbar',
            |        'position': { 'name': 'dodgev', 'height': 0.3 },
            |        'height': 0.2
            |       }
            |   ]
            |}
            """.trimMargin()
        return parsePlotSpec(spec)
    }

    private fun customWidth(width: Double, widthUnit: String): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'data': {
                'x': [-2, 0, 2],
                'ymin': [-2, -1, 0],
                'ymax': [0, 1, 2]
              },
              'mapping': {
                'x': 'x',
                'ymin': 'ymin',
                'ymax': 'ymax'
              },
              'ggtitle': {
                'text': 'Error bar with width=$width, width_unit=\"$widthUnit\"'
              },
              'layers': [
                {
                  'geom': 'errorbar',
                  'width': $width,
                  'width_unit': '$widthUnit'
                }
              ],
              'coord': {
                'name': 'cartesian',
                'flip': false,
                'xlim': [-3, 3]
              }
            }
        """.trimIndent()

        return HashMap(parsePlotSpec(spec))
    }
}