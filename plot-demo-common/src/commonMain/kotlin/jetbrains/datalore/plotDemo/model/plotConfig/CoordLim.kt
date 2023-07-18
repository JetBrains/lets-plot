/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import demoAndTestShared.parsePlotSpec

class CoordLim {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            fixed(),
            fixedXLim(107, 117),
            fixedYLim(470, 570),
            fixedLims(xMin = 65, xMax = 165, yMin = 370, yMax = 570),
            cartesian(),
            cartesianXLim(107, 117)
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
            |           'x': [100, 105, 110, 115, 120, 125], 
            |           'y': [400, 450, 500, 550, 600, 650], 
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

    private fun fixedXLim(min: Number, max: Number): MutableMap<String, Any> {
        val spec = createSpec(
            """
            |   'coord': {
            |       'name': 'fixed', 
            |       'xlim': [$min, $max], 
            |       'ylim': null
            |   }
        """.trimMargin()
        )
        spec["ggtitle"] = mapOf("text" to "coord_fixed(xlim=[$min, $max])")
        return spec
    }

    private fun fixedYLim(min: Number, max: Number): MutableMap<String, Any> {
        val spec = createSpec(
            """
            |   'coord': {
            |       'name': 'fixed',
            |       'xlim': null,
            |       'ylim': [$min, $max]
            |   }
        """.trimMargin()
        )
        spec["ggtitle"] = mapOf("text" to "coord_fixed(ylim=[$min, $max])")
        return spec
    }

    private fun fixedLims(xMin: Number, xMax: Number, yMin: Number, yMax: Number): MutableMap<String, Any> {
        val spec = createSpec(
            """
            |   'coord': {
            |       'name': 'fixed',
            |       'xlim': [$xMin, $xMax],
            |       'ylim': [$yMin, $yMax]
            |   }
        """.trimMargin()
        )
        spec["ggtitle"] = mapOf("text" to "coord_fixed(xlim=[$xMin, $xMax], ylim=[$yMin, $yMax])")
        return spec
    }

    private fun fixed(): MutableMap<String, Any> {
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

    private fun cartesian(): MutableMap<String, Any> {
        val spec = createSpec(
            """
            |   'coord': {
            |       'name': 'cartesian', 
            |       'xlim': null, 
            |       'ylim': null
            |   }
        """.trimMargin()
        )
        spec["ggtitle"] = mapOf("text" to "coord_cartesian()")
        return spec
    }

    private fun cartesianXLim(min: Number, max: Number): MutableMap<String, Any> {
        val spec = createSpec(
            """
            |   'coord': {
            |       'name': 'cartesian', 
            |       'xlim': [$min, $max], 
            |       'ylim': null
            |   }
        """.trimMargin()
        )
        spec["ggtitle"] = mapOf("text" to "coord_cartesian(xlim=[$min, $max])")
        return spec
    }

}