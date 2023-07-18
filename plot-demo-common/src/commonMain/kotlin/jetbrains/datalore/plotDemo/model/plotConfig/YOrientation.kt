/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import demoAndTestShared.parsePlotSpec

class YOrientation {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            basic(),
            (basic() + COORD_FLIP).toMutableMap(),
            basic(yOrientation = true),
            //---
            sortedXAlphabeticallyRevesed(),
            (sortedXAlphabeticallyRevesed() + COORD_FLIP).toMutableMap(),
            sortedXAlphabeticallyRevesed(yOrientation = true),
            //---
            sortedXByCount(),
            (sortedXByCount() + COORD_FLIP).toMutableMap(),
            sortedXByCount(yOrientation = true),
            //---
            groupedByFill(),
            (groupedByFill() + COORD_FLIP).toMutableMap(),
            groupedByFill(yOrientation = true),
            //---
            groupedByFillSortedByCount(),
            (groupedByFillSortedByCount() + COORD_FLIP).toMutableMap(),
            groupedByFillSortedByCount(yOrientation = true),
            //---
            fillByNumeric(),
            (fillByNumeric() + COORD_FLIP).toMutableMap(),
            fillByNumeric(yOrientation = true),
            //---
            fillByNumericGrouped(),
            (fillByNumericGrouped() + COORD_FLIP).toMutableMap(),
            fillByNumericGrouped(yOrientation = true),
            //---
            fillByNumericGroupedSortedByCount(),
            (fillByNumericGroupedSortedByCount() + COORD_FLIP).toMutableMap(),
            fillByNumericGroupedSortedByCount(yOrientation = true),
        )
    }

    private fun basic(yOrientation: Boolean = false): MutableMap<String, Any> {
        val categoryAes = categoryAes(yOrientation)
        val mapping = "{'$categoryAes': '$CATEGORY_VAR'}"
        return createPlotSpec(mapping, yOrientation = yOrientation)
    }

    private fun sortedXAlphabeticallyRevesed(yOrientation: Boolean = false): MutableMap<String, Any> {
        val categoryAes = categoryAes(yOrientation)
        val mapping = "{'$categoryAes': '$CATEGORY_VAR'}"
        val dataMeta = dataMeta_SortAlphabeticallyReversed(yOrientation)
        return createPlotSpec(mapping, dataMeta, yOrientation = yOrientation)
    }

    private fun sortedXByCount(yOrientation: Boolean = false): MutableMap<String, Any> {
        val categoryAes = categoryAes(yOrientation)
        val mapping = "{'$categoryAes': '$CATEGORY_VAR'}"
        val dataMeta = dataMeta_SortByCount(yOrientation)
        return createPlotSpec(mapping, dataMeta, yOrientation = yOrientation)
    }

    private fun groupedByFill(yOrientation: Boolean = false): MutableMap<String, Any> {
        val categoryAes = categoryAes(yOrientation)
        val mapping = "{'$categoryAes': '$CATEGORY_VAR', 'fill': '$GROUP_VAR'}"
        return createPlotSpec(mapping, yOrientation = yOrientation)
    }

    private fun groupedByFillSortedByCount(yOrientation: Boolean = false): MutableMap<String, Any> {
        val categoryAes = categoryAes(yOrientation)
        val mapping = "{'$categoryAes': '$CATEGORY_VAR', 'fill': '$GROUP_VAR'}"
        val dataMeta = dataMeta_SortByCount(yOrientation)
        return createPlotSpec(mapping, dataMeta, yOrientation = yOrientation)
    }

    private fun fillByNumeric(yOrientation: Boolean = false): MutableMap<String, Any> {
        val categoryAes = categoryAes(yOrientation)
        val mapping = "{'$categoryAes': '$CATEGORY_VAR', 'fill': '$NUMERIC_VAR'}"
        return createPlotSpec(mapping, yOrientation = yOrientation)
    }

    private fun fillByNumericGrouped(yOrientation: Boolean = false): MutableMap<String, Any> {
        val categoryAes = categoryAes(yOrientation)
        val mapping = "{'$categoryAes': '$CATEGORY_VAR', 'fill': '$NUMERIC_VAR', 'group': '$GROUP_VAR'}"
        return createPlotSpec(mapping, yOrientation = yOrientation)
    }

    private fun fillByNumericGroupedSortedByCount(yOrientation: Boolean = false): MutableMap<String, Any> {
        val categoryAes = categoryAes(yOrientation)
        val mapping = "{'$categoryAes': '$CATEGORY_VAR', 'fill': '$NUMERIC_VAR', 'group': '$GROUP_VAR'}"
        val dataMeta = dataMeta_SortByCount(yOrientation)
        return createPlotSpec(mapping, dataMeta, yOrientation)
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

        private val COORD_FLIP = mapOf(
            "coord" to mapOf(
                "name" to "flip",
                "flip" to true
            )
        )

        @Suppress("FunctionName")
        private fun dataMeta_SortByCount(yOrientation: Boolean): String {
            return """
            {
                'mapping_annotations': [ 
                    {
                        'aes': '${categoryAes(yOrientation)}',
                        'annotation': 'as_discrete',
                        'parameters': { 
                                'label': '$CATEGORY_VAR',
                                'order_by': '..count..'
                        }
                    }
                ]
            }
        """.trimIndent()
        }

        @Suppress("FunctionName")
        private fun dataMeta_SortAlphabeticallyReversed(yOrientation: Boolean): String {
            return """
            {
                'mapping_annotations': [ 
                    {
                        'aes': '${categoryAes(yOrientation)}',
                        'annotation': 'as_discrete',
                        'parameters': { 
                                'label': '$CATEGORY_VAR',
                                'order': -1
                        }
                    }
                ]
            }
        """.trimIndent()
        }

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