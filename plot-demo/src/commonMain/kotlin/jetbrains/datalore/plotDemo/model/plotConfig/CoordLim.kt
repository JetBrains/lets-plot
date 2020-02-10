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
            //fixed(),
            fixedLim()
            //noLims(),
            //xLims()
        )
    }

    private fun createSpec(coordSpec: String?): MutableMap<String, Any> {
        val spec = """
            |{
            |   'data': null, 
            |   'mapping': null,
            |    ${if (coordSpec == null) "" else (coordSpec + ",")}
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

    private fun noLims(): Map<String, Any> {
        return createSpec(null).apply { this["ggtitle"] = mapOf("text" to "coord_cartesian(x_lim=[2, 22])") }
    }

    private fun xLims(): Map<String, Any> {
        val spec = createSpec(
            """
            |   'coord': {
            |       'name': 'cartesian', 
            |       'xlim': [2, 22], 
            |       'ylim': null
            |   }
        """.trimMargin()
        )
        spec["ggtitle"] = mapOf("text" to "coord_cartesian(x_lim=[2, 22])")
        return spec
    }

    private fun fixedLim(): Map<String, Any> {
        val spec = createSpec(
            """
            |   'coord': {
            |       'name': 'fixed', 
            |       'xlim': [2, 22], 
            |       'ylim': null
            |   }
        """.trimMargin()
        )
        spec["ggtitle"] = mapOf("text" to "coord_fixed(x_lim=[2, 22])")
        return spec
    }

    private fun fixed(): Map<String, Any> {
        val spec = createSpec(
            """
            |   'coord': {
            |       'name': 'fixed', 
            |       'xlim': null, 
            |       'ylim': null
            |   }
        """.trimMargin()
        )
        spec["ggtitle"] = mapOf("text" to "coord_fixed()")
        return spec
    }
}