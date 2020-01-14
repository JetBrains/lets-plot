/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase

class CoordLim : PlotConfigDemoBase() {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            simple()
        )
    }

    private fun simple(): Map<String, Any> {
        val spec = """
            |{
            |   'data': null, 
            |   'mapping': null, 
            |   'coord': {
            |       'name': 'cartesian', 
            |       'xlim': [2, 22], 
            |       'ylim': null
            |   }, 
            |   'kind': 'plot', 
            |   'scales': [], 
            |   'layers': [{
            |       'geom': 'line', 
            |       'stat': null, 
            |       'data': {
            |           'x': [0, 5, 10, 15, 20, 25], 
            |           'y': [0, 5, 10, 15, 20, 25], 
            |           'g': ['a', 'a', 'b', 'b', 'c', 'c']
            |       },
            |       'mapping': {
            |           'x': 'x', 
            |           'y': 'y', 
            |           'group': 'g', 
            |           'color': 'g'
            |       },
            |       'position': null,
            |       'show_legend': null, 
            |       'sampling': null
            |   }]
            |}""".trimMargin()
        return parsePlotSpec(spec)
    }
}