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
            fixed(),
            fixedXLim(7, 17),
            fixedYLim(7, 17),
            fixedLims(xMin = -1, xMax = 7, yMin = -1, yMax = 15)
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

    private fun fixedXLim(min: Number, max: Number): Map<String, Any> {
        val spec = createSpec(
            """
            |   'coord': {
            |       'name': 'fixed', 
            |       'xlim': [$min, $max], 
            |       'ylim': null
            |   }
        """.trimMargin()
        )
        spec["ggtitle"] = mapOf("text" to "coord_fixed(x_lim=[$min, $max])")
        return spec
    }

    private fun fixedYLim(min: Number, max: Number): Map<String, Any> {
        val spec = createSpec(
            """
            |   'coord': {
            |       'name': 'fixed',
            |       'xlim': null,
            |       'ylim': [$min, $max]
            |   }
        """.trimMargin()
        )
        spec["ggtitle"] = mapOf("text" to "coord_fixed(y_lim=[$min, $max])")
        return spec
    }

    private fun fixedLims(xMin: Number, xMax: Number, yMin: Number, yMax: Number): Map<String, Any> {
        val spec = createSpec(
            """
            |   'coord': {
            |       'name': 'fixed',
            |       'xlim': [$xMin, $xMax],
            |       'ylim': [$yMin, $yMax]
            |   }
        """.trimMargin()
        )
        spec["ggtitle"] = mapOf("text" to "coord_fixed(x_lim=[$xMin, $xMax], y_lim=[$yMin, $yMax])")
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