/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

/**
 * https://github.com/JetBrains/lets-plot-kotlin/issues/105
 */
@Suppress("ClassName")
class Issue_OOM_105 {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            case0()
        )
    }

    private fun case0(): MutableMap<String, Any> {
        val spec = """
            {
                'kind': 'plot',
                'mapping':  {
                                'x': 'Task interval coefficient of variation',
                                'y': 'Average processing time'
                            },
                'layers':   [
                                {
                                    'geom': 'smooth'
                                },
                                {
                                    'geom': 'point'
                                }
                            ]
            }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))

        val data = mapOf(
            "Task interval coefficient of variation" to listOf(
                1.17546,
                1.19216,
                1.20941,
                1.22708,
                1.24521,
                1.26378,
                1.28274,
                1.30207,
                1.32178,
                1.34187,
                1.3623,
                1.383,
                1.40404,
                1.42535,
                1.44695,
                1.46884,
                1.49097,
                1.51339,
                1.53597,
                1.55886
            ),
            "Average processing time" to listOf(
                41.1, //41.1325,
                41.1, //41.1325,
                41.1, //41.1325,
                41.1, //41.1325,
                41.1, //41.1325,
                41.1, //41.1325,
                41.1, //41.1325,
                41.1, //41.1325,
                41.1, //41.1325,
                41.1, //41.1325,
                41.1, //41.1325,
                41.1, //41.1325,
                41.1, //41.1325,
                41.1, //41.1325,
                41.1, //41.1325,
                41.1, //41.1325,
                41.1, //41.1325,
                41.1, //41.1325,
                41.1, //41.1325,
                41.1, //41.1325,
            )
        )
        plotSpec["data"] = data
        return plotSpec
    }
}