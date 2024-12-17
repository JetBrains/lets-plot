/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class LogScaleCoordLim {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            noLimits(),
            withLimits(1, 10000),
            withLimits(0.5, 1.5),
            withLimits(0.0, 10),
            withLimits(-1.0, 10000),
        )
    }

    private fun createSpec(coordSpec: String?): MutableMap<String, Any> {
        val spec = """
            |{
            |   'data': null, 
            |   'mapping': null,
            |    ${if (coordSpec == null) "" else (coordSpec + ",")}
            |   'kind': 'plot', 
            |   'scales': [
            |   {'aesthetic': 'y',
            |    'trans': 'log10'
            |   }
            |   ], 
            |   'layers': [{
            |       'geom': 'point', 
            |       'data': {
            |           'x': [0, 1, 2, 3, 4, 5], 
            |           'y': [0.01, 0.1, 1, 10, 100, 1000] 
            |       },
            |       'mapping': {
            |           'x': 'x', 
            |           'y': 'y' 
            |       },
            |       'size': 5
            |   }]
            |}""".trimMargin()

        return parsePlotSpec(spec)
    }

    private fun noLimits(): MutableMap<String, Any> {
        val spec = createSpec(null)
        spec["ggtitle"] = mapOf("text" to "no limits")
        return spec
    }

    private fun withLimits(min: Number, max: Number): MutableMap<String, Any> {
        val spec = createSpec(
            """
            |   'coord': {
            |       'name': 'cartesian', 
            |       'ylim': [$min, $max] 
            |   }
        """.trimMargin()
        )
        spec["ggtitle"] = mapOf("text" to "ylim=[$min, $max]")
        return spec
    }
}