/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec

class YOrientation {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic(),
            sortedXAlphabeticallyRevesed(),
            sortedXByCount(),
            groupedByFill(),
            groupedByFillSortedX(),
            fillByNumeric(),
            fillByNumericGrouped(),
            fillByNumericGroupedSortedX()
        )
    }

    private fun basic(): MutableMap<String, Any> {
        return createPlotSpec("{'x': 'varX'}")
    }

    private fun sortedXAlphabeticallyRevesed(): MutableMap<String, Any> {
        val dataMeta = SORT_ALPHABETICALLY_REVERSED_DATA_META
        return createPlotSpec("{'x': 'varX'}", dataMeta)
    }

    private fun sortedXByCount(): MutableMap<String, Any> {
        val dataMeta = SORT_BY_COUNT_DATA_META
        return createPlotSpec("{'x': 'varX'}", dataMeta)
    }

    private fun groupedByFill(): MutableMap<String, Any> {
        return createPlotSpec("{'x': 'varX', 'fill': 'varGroup'}")
    }

    private fun groupedByFillSortedX(): MutableMap<String, Any> {
        return createPlotSpec("{'x': 'varX', 'fill': 'varGroup'}", SORT_BY_COUNT_DATA_META)
    }

    private fun fillByNumeric(): MutableMap<String, Any> {
        return createPlotSpec("{'x': 'varX', 'fill': 'varNum'}")
    }

    private fun fillByNumericGrouped(): MutableMap<String, Any> {
        return createPlotSpec("{'x': 'varX', 'fill': 'varNum', 'group': 'varGroup'}")
    }

    private fun fillByNumericGroupedSortedX(): MutableMap<String, Any> {
        return createPlotSpec("{'x': 'varX', 'fill': 'varNum', 'group': 'varGroup'}", SORT_BY_COUNT_DATA_META)
    }


    companion object {
        private val DATA = mapOf(
            "varX" to List(30) { "a" } +
                    List(40) { "b" } +
                    List(20) { "c" },
            "varGroup" to listOf("g0") + List(29) { "g1" } +
                    List(39) { "g1" } + listOf("g0") +
                    List(19) { "g1" } + listOf("g0"),
            "varNum" to listOf(0) + List(29) { 1 } +
                    List(39) { 1 } + listOf(0) +
                    List(19) { 1 } + listOf(0),
        )

        private val SORT_BY_COUNT_DATA_META = """
            {
                'mapping_annotations': [ 
                    {
                        'aes': 'x',
                        'annotation': 'as_discrete',
                        'parameters': { 
                                'label': 'varX',
                                'order_by': '..count..'
                        }
                    }
                ]
            }
        """.trimIndent()

        private val SORT_ALPHABETICALLY_REVERSED_DATA_META = """
            {
                'mapping_annotations': [ 
                    {
                        'aes': 'x',
                        'annotation': 'as_discrete',
                        'parameters': { 
                                'label': 'varX',
                                'order': -1
                        }
                    }
                ]
            }
        """.trimIndent()

        private fun createPlotSpec(layerMapping: String, dataMeta: String = "{}"): MutableMap<String, Any> {
            val spec = """
                {
                    'kind': 'plot',
                    'layers': [
                        {
                            'geom': 'bar', 
                            'mapping': $layerMapping, 
                            'data_meta': $dataMeta
                        }
                    ]
                }
            """.trimIndent()

            val plotSpec = HashMap(parsePlotSpec(spec))
            plotSpec["data"] = DATA
            return plotSpec
        }
    }
}