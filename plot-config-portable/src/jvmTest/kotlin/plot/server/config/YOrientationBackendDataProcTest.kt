/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config

import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.builder.data.OrderOptionUtil
import jetbrains.datalore.plot.config.DataMetaUtil
import jetbrains.datalore.plot.config.Option.Meta.DATA_META
import jetbrains.datalore.plot.parsePlotSpec
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * See related: YOrientationBatik.kt demo.
 */
class YOrientationBackendDataProcTest {

    @Test
    fun test() {
        check(plotSpecs_basic(), expected_basic())
        check(plotSpecs_sortedAlphabeticallyRevesed(), expected_sortedAlphabeticallyRevesed())
        check(plotSpecs_sortedByCount(), expected_sortedByCount())
        check(plotSpecs_groupedByFill(), expected_groupedByFill())
        check(plotSpecs_groupedByFillSortedByCount(), expected_groupedByFillSortedByCount())
        check(plotSpecs_fillByNumeric(), expected_fillByNumeric())
        check(plotSpecs_fillByNumericGrouped(), expected_fillByNumericGrouped())
        check(plotSpecs_fillByNumericGroupedSortedByCount(), expected_fillByNumericGroupedSortedByCount())


        check(plotSpecs_basic(yOrientation = true), expected_basic())
        check(
            plotSpecs_sortedAlphabeticallyRevesed(yOrientation = true),
            expected_sortedAlphabeticallyRevesed(yOrientation = true)
        )
        check(plotSpecs_sortedByCount(yOrientation = true), expected_sortedByCount(yOrientation = true))
        check(plotSpecs_groupedByFill(yOrientation = true), expected_groupedByFill())
        check(
            plotSpecs_groupedByFillSortedByCount(yOrientation = true),
            expected_groupedByFillSortedByCount(yOrientation = true)
        )
        check(plotSpecs_fillByNumeric(yOrientation = true), expected_fillByNumeric())
        check(plotSpecs_fillByNumericGrouped(yOrientation = true), expected_fillByNumericGrouped())
        check(
            plotSpecs_fillByNumericGroupedSortedByCount(yOrientation = true),
            expected_fillByNumericGroupedSortedByCount(yOrientation = true)
        )
    }

    private fun check(plotSpec: MutableMap<String, Any>, expected: Expected) {
        // See related: TestUtil.createGeomLayers()
        val layerConfig = ServerSideTestUtil.createLayerConfigs(plotSpec)[0]

        val dataAfterStat = DataFrameUtil.toMap(layerConfig.ownData)
        assertEquals(expected.data, dataAfterStat)

//        val layerOptions = layerConfig.mergedOptions
//        val dataMeta = layerOptions.getValue(DATA_META) as Map<*, *>
        val dataMeta = layerConfig.getMap(DATA_META)
        if (expected.asDiscreteAesSet.isNotEmpty()) {
            val asDiscreteAesSet = DataMetaUtil.getAsDiscreteAesSet(dataMeta)
            assertEquals(expected.asDiscreteAesSet, asDiscreteAesSet)
        } else {
            assertTrue(dataMeta.isEmpty())
        }


        val orderOptions = layerConfig.orderOptions
        expected.orderOption?.let {
            assertEquals(1, orderOptions.size)
            assertEquals(it, orderOptions[0])
        } ?: run {
            assertTrue(orderOptions.isEmpty())
        }
    }

    class Expected(
        val data: Map<String, Any>,
        val asDiscreteAesSet: Set<String> = emptySet(),
        val orderOption: OrderOptionUtil.OrderOption? = null
    )

    companion object {
        private const val CATEGORY_VAR = "varCategory"
        private const val GROUP_VAR = "varGroup"
        private const val NUMERIC_VAR = "varNum"

        private val DATA = mapOf(
            CATEGORY_VAR to List(30) { "a" } +
                    List(40) { "b" } +
                    List(20) { "c" },
            GROUP_VAR to listOf("g0") + List(29) { "g1" } +
                    List(39) { "g1" } + listOf("g0") +
                    List(19) { "g1" } + listOf("g0"),
            NUMERIC_VAR to listOf(0) + List(29) { 1 } +
                    List(39) { 1 } + listOf(0) +
                    List(19) { 1 } + listOf(0),
        )

        private fun categoryAes(yOrientation: Boolean): String {
            return if (yOrientation) "y" else "x"
        }

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

        private fun createPlotSpec(
            layerMapping: String,
            dataMeta: String = "{}",
            yOrientation: Boolean
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

        // Basic

        private fun plotSpecs_basic(yOrientation: Boolean = false): MutableMap<String, Any> {
            return createPlotSpec("{'${categoryAes(yOrientation)}': '$CATEGORY_VAR'}", yOrientation = yOrientation)
        }

        private fun expected_basic(): Expected {
            return Expected(
                data = mapOf<String, List<Any>>(
                    CATEGORY_VAR to listOf("a", "b", "c"),
                    "..count.." to listOf(30.0, 40.0, 20.0)
                )
            )
        }

        // Categories are sorted alphabetically.

        private fun plotSpecs_sortedAlphabeticallyRevesed(yOrientation: Boolean = false): MutableMap<String, Any> {
            val categoryAes = categoryAes(yOrientation)
            val dataMeta = dataMeta_SortAlphabeticallyReversed(yOrientation)
            return createPlotSpec("{'$categoryAes': '$CATEGORY_VAR'}", dataMeta, yOrientation = yOrientation)
        }

        private fun expected_sortedAlphabeticallyRevesed(yOrientation: Boolean = false): Expected {
            return Expected(
                data = mapOf<String, List<Any>>(
                    CATEGORY_VAR to listOf("a", "b", "c"),
                    "..count.." to listOf(30.0, 40.0, 20.0)
                ),
                asDiscreteAesSet = setOf(categoryAes(yOrientation)),
                orderOption = OrderOptionUtil.OrderOption.create(
                    variableName = CATEGORY_VAR,
                    orderBy = null,
                    order = -1
                )
            )
        }

        // Categories are sorted by count.

        private fun plotSpecs_sortedByCount(yOrientation: Boolean = false): MutableMap<String, Any> {
            val categoryAes = categoryAes(yOrientation)
            val dataMeta = dataMeta_SortByCount(yOrientation)
            return createPlotSpec("{'$categoryAes': '$CATEGORY_VAR'}", dataMeta, yOrientation = yOrientation)
        }

        private fun expected_sortedByCount(yOrientation: Boolean = false): Expected {
            return Expected(
                data = mapOf<String, List<Any>>(
                    CATEGORY_VAR to listOf("a", "b", "c"),
                    "..count.." to listOf(30.0, 40.0, 20.0)
                ),
                asDiscreteAesSet = setOf(categoryAes(yOrientation)),
                orderOption = OrderOptionUtil.OrderOption.create(
                    variableName = CATEGORY_VAR,
                    orderBy = "..count..",
                    order = null
                )
            )
        }

        // Grouping by 'fill'.

        private fun plotSpecs_groupedByFill(yOrientation: Boolean = false): MutableMap<String, Any> {
            val categoryAes = categoryAes(yOrientation)
            val mapping = "{'$categoryAes': '$CATEGORY_VAR', 'fill': '$GROUP_VAR'}"
            return createPlotSpec(mapping, "{}", yOrientation = yOrientation)
        }

        private fun expected_groupedByFill(): Expected {
            return Expected(
                data = mapOf<String, List<Any>>(
                    CATEGORY_VAR to listOf("a", "b", "c", "a", "b", "c"),
                    "..count.." to listOf(1.0, 1.0, 1.0, 29.0, 39.0, 19.0),
                    GROUP_VAR to listOf("g0", "g0", "g0", "g1", "g1", "g1"),
                )
            )
        }

        // Grouping by 'fill' and sorted by count.

        private fun plotSpecs_groupedByFillSortedByCount(yOrientation: Boolean = false): MutableMap<String, Any> {
            val categoryAes = categoryAes(yOrientation)
            val mapping = "{'$categoryAes': '$CATEGORY_VAR', 'fill': '$GROUP_VAR'}"
            val dataMeta = dataMeta_SortByCount(yOrientation)
            return createPlotSpec(mapping, dataMeta, yOrientation = yOrientation)
        }

        private fun expected_groupedByFillSortedByCount(yOrientation: Boolean = false): Expected {
            return Expected(
                data = mapOf<String, List<Any>>(
                    CATEGORY_VAR to listOf("a", "b", "c", "a", "b", "c"),
                    "..count.." to listOf(1.0, 1.0, 1.0, 29.0, 39.0, 19.0),
                    GROUP_VAR to listOf("g0", "g0", "g0", "g1", "g1", "g1"),
                ),
                asDiscreteAesSet = setOf(categoryAes(yOrientation)),
                orderOption = OrderOptionUtil.OrderOption.create(
                    CATEGORY_VAR,
                    orderBy = "..count..",
                    order = null
                )
            )
        }

        // Fill by numeric.

        private fun plotSpecs_fillByNumeric(yOrientation: Boolean = false): MutableMap<String, Any> {
            val categoryAes = categoryAes(yOrientation)
            val mapping = "{'$categoryAes': '$CATEGORY_VAR', 'fill': '$NUMERIC_VAR'}"
            val dataMeta = "{}"
            return createPlotSpec(mapping, dataMeta, yOrientation = yOrientation)
        }

        private fun expected_fillByNumeric(): Expected {
            return Expected(
                data = mapOf<String, List<Any>>(
                    CATEGORY_VAR to listOf("a", "b", "c"),
                    "..count.." to listOf(30.0, 40.0, 20.0),
                    NUMERIC_VAR to listOf(0.9666666666666669, 0.9666666666666669, 0.9666666666666669),
                ),
            )
        }

        // Fill by numeric, grouped.

        private fun plotSpecs_fillByNumericGrouped(yOrientation: Boolean = false): MutableMap<String, Any> {
            val categoryAes = categoryAes(yOrientation)
            val mapping = "{'$categoryAes': '$CATEGORY_VAR', 'fill': '$NUMERIC_VAR', 'group': '$GROUP_VAR'}"
            val dataMeta = "{}"
            return createPlotSpec(mapping, dataMeta, yOrientation = yOrientation)
        }

        private fun expected_fillByNumericGrouped(): Expected {
            return Expected(
                data = mapOf<String, List<Any>>(
                    CATEGORY_VAR to listOf("a", "b", "c", "a", "b", "c"),
                    "..count.." to listOf(1.0, 1.0, 1.0, 29.0, 39.0, 19.0),
                    GROUP_VAR to listOf("g0", "g0", "g0", "g1", "g1", "g1"),
                    NUMERIC_VAR to listOf(0.0, 0.0, 0.0, 1.0, 1.0, 1.0),
                ),
            )
        }

        // Fill by numeric, grouped, sorted by count.

        private fun plotSpecs_fillByNumericGroupedSortedByCount(yOrientation: Boolean = false): MutableMap<String, Any> {
            val categoryAes = categoryAes(yOrientation)
            val mapping = "{'$categoryAes': '$CATEGORY_VAR', 'fill': '$NUMERIC_VAR', 'group': '$GROUP_VAR'}"
            val dataMeta = dataMeta_SortByCount(yOrientation)
            return createPlotSpec(mapping, dataMeta, yOrientation = yOrientation)
        }

        private fun expected_fillByNumericGroupedSortedByCount(yOrientation: Boolean = false): Expected {
            return Expected(
                data = mapOf<String, List<Any>>(
                    CATEGORY_VAR to listOf("a", "b", "c", "a", "b", "c"),
                    "..count.." to listOf(1.0, 1.0, 1.0, 29.0, 39.0, 19.0),
                    GROUP_VAR to listOf("g0", "g0", "g0", "g1", "g1", "g1"),
                    NUMERIC_VAR to listOf(0.0, 0.0, 0.0, 1.0, 1.0, 1.0),
                ),
                asDiscreteAesSet = setOf(categoryAes(yOrientation)),
                orderOption = OrderOptionUtil.OrderOption.create(
                    variableName = CATEGORY_VAR,
                    orderBy = "..count..",
                    order = null
                )
            )
        }
    }
}
