/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import demoAndTestShared.parsePlotSpec


class CoordLimSimple {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            fixed(),
            fixedYLim(5, 15),
            fixedYLim(15, 25),
        )
    }

    private fun createSpec(coordSpec: String?): MutableMap<String, Any> {
        val spec = """
            |{'data': {'x': [-100, 100, -100, 100],
            |  'y': [10, 10, -10, -10],
            |  'c': ['a', 'b', 'c', 'd']},
            | 'theme': {'name': 'grey'},
            | 'kind': 'plot',
            |    ${if (coordSpec == null) "" else (coordSpec + ",")}
            | 'layers': [{'geom': 'point',
            |   'mapping': {'x': 'x', 'y': 'y', 'color': 'c'},
            |   'size': 5}]
            |}""".trimMargin()

        return parsePlotSpec(spec)
    }

    private fun fixed(): MutableMap<String, Any> {
        val spec = createSpec(
            """
            |   'coord': {
            |       'name': 'fixed',
            |       'ratio': 10, 
            |       'xlim': null, 
            |       'ylim': null
            |   }
        """.trimMargin()
        )
        spec["ggtitle"] = mapOf("text" to "coord_fixed(ratio=10)")
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

}