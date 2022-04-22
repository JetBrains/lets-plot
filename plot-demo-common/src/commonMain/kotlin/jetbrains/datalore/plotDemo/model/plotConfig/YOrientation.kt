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
            basic(yOrientation = true),
            sortedXAlphabeticallyRevesed(),
            sortedXByCount(),
            groupedByFill(),
            groupedByFillSortedByCount(),
            fillByNumeric(),
            fillByNumericGrouped(),
            fillByNumericGroupedSortedByCount()
        )
    }

    private fun basic(yOrientation: Boolean = false): MutableMap<String, Any> {
        val categoryAes = categoryAes(yOrientation)
        val mapping = "{'$categoryAes': '$CATEGORY_VAR'}"
        return createPlotSpec(mapping, yOrientation = yOrientation)
    }

    private fun sortedXAlphabeticallyRevesed(): MutableMap<String, Any> {
        val dataMeta = SORT_ALPHABETICALLY_REVERSED_DATA_META
        return createPlotSpec("{'x': '$CATEGORY_VAR'}", dataMeta)
    }

    private fun sortedXByCount(): MutableMap<String, Any> {
        val dataMeta = SORT_BY_COUNT_DATA_META
        return createPlotSpec("{'x': '$CATEGORY_VAR'}", dataMeta)
    }

    private fun groupedByFill(): MutableMap<String, Any> {
        return createPlotSpec("{'x': '$CATEGORY_VAR', 'fill': '$GROUP_VAR'}")
    }

    private fun groupedByFillSortedByCount(): MutableMap<String, Any> {
        return createPlotSpec("{'x': '$CATEGORY_VAR', 'fill': '$GROUP_VAR'}", SORT_BY_COUNT_DATA_META)
    }

    private fun fillByNumeric(): MutableMap<String, Any> {
        return createPlotSpec("{'x': '$CATEGORY_VAR', 'fill': '$NUMERIC_VAR'}")
    }

    private fun fillByNumericGrouped(): MutableMap<String, Any> {
        return createPlotSpec("{'x': '$CATEGORY_VAR', 'fill': '$NUMERIC_VAR', 'group': '$GROUP_VAR'}")
    }

    private fun fillByNumericGroupedSortedByCount(): MutableMap<String, Any> {
        return createPlotSpec(
            "{'x': '$CATEGORY_VAR', 'fill': '$NUMERIC_VAR', 'group': '$GROUP_VAR'}",
            SORT_BY_COUNT_DATA_META
        )
    }


    companion object {
        private const val CATEGORY_VAR = "varCategory"
        private const val GROUP_VAR = "varGroup"
        private const val NUMERIC_VAR = "varNum"

        private val DATA = mapOf(
            "$CATEGORY_VAR" to List(30) { "a" } +
                    List(40) { "b" } +
                    List(20) { "c" },
            "$GROUP_VAR" to listOf("g0") + List(29) { "g1" } +
                    List(39) { "g1" } + listOf("g0") +
                    List(19) { "g1" } + listOf("g0"),
            "$NUMERIC_VAR" to listOf(0) + List(29) { 1 } +
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
                                'label': '$CATEGORY_VAR',
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
                                'label': '$CATEGORY_VAR',
                                'order': -1
                        }
                    }
                ]
            }
        """.trimIndent()

        private fun categoryAes(yOrientation: Boolean): String {
            return if (yOrientation) "y" else "x"
        }

        private fun createPlotSpec(
            layerMapping: String,
            dataMeta: String = "{}",
            yOrientation: Boolean = false
        ): MutableMap<String, Any> {
            val spec = """
                {
                    'kind': 'plot',
                    'layers': [
                        {
                            ${if (yOrientation) "'orientation': 'y'," else ""}
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