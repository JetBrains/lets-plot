/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase

// see: www.cookbook-r.com/Graphs/Plotting_means_and_error_bars_(ggplot2)
@Suppress("DuplicatedCode")
class ErrorBar : PlotConfigDemoBase() {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            withLinesAndPoints(),
            withBars()
        )
    }

    private fun withLinesAndPoints(): Map<String, Any> {
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
    private fun withBars(): Map<String, Any> {
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
}